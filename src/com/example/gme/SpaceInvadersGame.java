package com.example.gme;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

public class SpaceInvadersGame extends Game {

	private HeroFlight hero;
	
	private int fieldHeight, fieldWidth;
	private int invadersCount = 32;
	private int invaderGap = 10; //10px gap between ships
	private int invaderH, invaderW;
	private int invaderRows = 4; //rows of invader 
	private static final int WIN = 0;
	private static final int LOOSE = 1;
	private static final int NOT_YET = -1;
	
	private int gameOver = WIN;
	

	private EnemyFlight[] invaders;
	private Bullet[] bullets;
	private int maxBullets = 2;
	private int maxEnemyBullets = 2;

	private int activeInvaders = invadersCount;
	private int score = 0;
	private ScoreTable scoreTable;
	private Paint paint = new Paint();
	private Bitmap background = Bitmaps.getBitmap(Bitmaps.BACKGROUND);
	private Rect screenRect = new Rect();
	private LevelBoss currentBoss = null;
	
	private Bullet[] enemyBullets;
	private long elapsedShootTime = 0;
	private long shootInterval = 1000;
	private char heroLives = 2;

	public SpaceInvadersGame(Context context)
	{
		super(context);

		DisplayMetrics metrics = new DisplayMetrics();
		
		((Activity )context).getWindowManager().getDefaultDisplay().getMetrics(metrics);

		fieldHeight = metrics.heightPixels;
		fieldWidth = metrics.widthPixels;

		screenRect.left = 0;
		screenRect.top = 0;
		screenRect.right = fieldWidth;
		screenRect.bottom = fieldHeight;
		

		invaders = new EnemyFlight[invadersCount];
		bullets = new Bullet[maxBullets];

		maxEnemyBullets = 2;
		heroLives = 2;

		enemyBullets = new Bullet[maxEnemyBullets];
		paint.setColor(Color.YELLOW);
		paint.setTextSize(96);
		paint.setFakeBoldText(true);
		// amount in a row

		reset();
	}
	
	private void makeNewHero() {
		//Let's calc the size and pos of the hero ship.
		int hero_h = invaderH*2;
		int hero_w = Math.round((float)hero_h*(float)0.5);

		int hero_pos_left = (fieldWidth - hero_w)/2; //so we centered the hero ship
		int hero_pos_top = fieldHeight - hero_h - invaderGap*6;

		hero = new HeroFlight(hero_pos_left,
				hero_pos_top,
				hero_pos_left + hero_w,
				hero_pos_top + hero_h,
				10, Color.BLUE);

		hero.setVisible(true);		
	}

	public void reset() {

		int numInRow = invadersCount / invaderRows;

		invaderW = ((fieldWidth - 40) - invaderGap)/(8) - invaderGap;
		invaderH = invaderW;

		makeNewHero();

		heroLives = 2;
		maxEnemyBullets = 2;

		for (int i = 0; i < invaderRows; ++i) {
			for (int j = 0; j < numInRow; ++j) {
				invaders[i*numInRow + j]
						= new EnemyFlight(20 + invaderGap + j*(invaderW + invaderGap),
								invaderGap + (i*invaderH + invaderGap),
								20 + invaderGap + j*(invaderW + invaderGap) + invaderW,
								invaderGap + (i*invaderH + invaderGap) + invaderH,
								8,
								Color.RED);
				invaders[i*numInRow + j].setDownShift(10);
				invaders[i*numInRow + j].setVisible(true);
			}
		}
		
		float speed = -((float)fieldHeight/ (float)1500)*10;

		for (int i = 0; i < maxBullets; ++i) {
			bullets[i] = new Bullet();
			bullets[i].setVisible(false);
			bullets[i].setSpeed(Math.round(speed));
			bullets[i].setSpaceArea(0, 0, fieldWidth, fieldHeight);
		}

		speed = (float)(fieldHeight/ (float)3000 )*10;

		
		for (int i = 0; i < maxEnemyBullets; ++i) {
			enemyBullets[i] = new EnemyBullet();
			enemyBullets[i].setVisible(false);
			enemyBullets[i].setSpeed(Math.round(speed));
			enemyBullets[i].setSpaceArea(invaderGap, invaderGap, fieldWidth - invaderGap, fieldHeight - invaderGap);
		}

		activeInvaders = invadersCount;
		scoreTable = new ScoreTable();

		scoreTable.setBoundRect(invaderGap, fieldHeight - invaderGap*4, fieldWidth - invaderGap, fieldHeight - invaderGap);
		scoreTable.setLives(heroLives);
		currentBoss = null;
		gameOver = NOT_YET;
	}

	@Override
	public void update(long timeDelta) {

		if (gameOver != NOT_YET) {
			return;
		}

		synchronized (this) {

			elapsedShootTime += timeDelta;
			
			if (!hero.isVisible()) {
				if ( (heroLives) > 0) {
					--heroLives;
					scoreTable.setLives(heroLives);
					makeNewHero();
				} else {
					gameOver = LOOSE;
				}
			}

			// handle the bullets sent by a hero
			for (int i = 0; i < maxBullets; ++i) {
				if (bullets[i].isVisible()) {
					bullets[i].update(timeDelta);
				}
			}

			if (activeInvaders > 0) {
				updateInvaders(timeDelta);
				handleCollisions();
			}
			
			if ( (currentBoss != null) && currentBoss.isVisible()) {
				currentBoss.update(timeDelta);
				handleBossBulletCollisions();
			}

			if (activeInvaders == 0) {
				if ((currentBoss != null) && currentBoss.isDefeated()) {
					gameOver = WIN;
				} else if (currentBoss == null) {
					currentBoss = createLevelBoss();
					maxEnemyBullets = 1;
					currentBoss.setVisible(true);
				}
			}

			if (elapsedShootTime >= shootInterval) {
				elapsedShootTime = 0;
				
				if (activeInvaders > 0) {
					
					int index = invadersCount;

					//silly logic to choose a ship that will fire a bullet
					do {
						--index;
						if (!invaders[index].isKilled() && ( Math.random() > 0.6f)) {
							break;
						}
					} while(index > 0);
					
					//if we still have our invaders, one of them will fire a bullet
					fireEnemyBullet(invaders[index].getBoundRect(), hero.getBoundRect());
				} else {
					//there are no ships, that means we have a boss fighting
					fireEnemyBullet(currentBoss.getBoundRect(), hero.getBoundRect());
				}
			}
			
			// handle the bullets sent by a boss
			for (int i = 0; i < maxEnemyBullets; ++i) {
				if (enemyBullets[i].isVisible()) {
					//currently enemy bullet is using parent's update()
					//so the bullets flight straight
					//TODO: override the method to shoot the bullets to the hero's location
					enemyBullets[i].update(timeDelta);
				}
			}
			
			handleHeroBulletsCollisions();
			
		}
	}

	private void handleHeroBulletsCollisions() {
		for (int i = 0; i < maxEnemyBullets; ++i) {
			if (enemyBullets[i].isVisible()) {
				if (Rect.intersects(enemyBullets[i].getBoundRect(), hero.getBoundRect())) {

					enemyBullets[i].setVisible(false);
					hero.addDamage(enemyBullets[i].getPower());
				}
			}
		}				
	}

	private void handleBossBulletCollisions() {
		for (int i = 0; i < maxBullets; ++i) {
			if (bullets[i].isVisible()) {
				if (Rect.intersects(bullets[i].getBoundRect(), currentBoss.getBoundRect())) {

					bullets[i].setVisible(false);
					currentBoss.addDamage(bullets[i].getPower());
					
					if (currentBoss.isKilled()) {
						score += currentBoss.getScore();
						scoreTable.setScore(score);
					}
				}
			}
		}
	}

	private LevelBoss createLevelBoss() {
		LevelBoss boss = new BigBrain((fieldWidth - 140)/2, invaderGap, (fieldWidth - 140)/2 + 140, invaderGap + 140, 10, 0);
		
		boss.setSpaceArea(invaderGap, invaderGap, fieldWidth - invaderGap, fieldHeight - invaderH*2);
		
		
		return boss;
	}

	private void updateInvaders(long timeDelta) {
		boolean isWallMet = false;
		Rect tmpRect;

		// Run through the all invaders and update their positions
		for (int i = 0; i < invadersCount; ++i) {
			if (invaders[i].isVisible()) {
				invaders[i].update(timeDelta);

				tmpRect = invaders[i].getBoundRect();

				// if we met a wall, remember that
				if ((tmpRect.left < invaderGap)
						|| (tmpRect.right > (fieldWidth - invaderGap))) {
					isWallMet = true;
				}
			 }
		}

		// if we met a wall, then we need to count the bounce and reverse
		// the direction (maybe)
		if (isWallMet) {
			for (int i = 0; i < invadersCount; ++i) {
				if (invaders[i].isVisible()) {
					invaders[i].handleWall();
					invaders[i].stepBack();
				}
			}

		}
	}

	private void handleCollisions() {
		// Now calculate collisions
		for (int i = 0; i < maxBullets; ++i) {
			if (bullets[i].isVisible()) {

				for (int j = 0; j < invadersCount; ++j) {
					if (invaders[j].isVisible()) {
						// Remove the bullet and the battleship
						if (Rect.intersects(invaders[j].getBoundRect(),
							bullets[i].getBoundRect())) {
							bullets[i].setVisible(false);
							
							//invaders[j].setKilled(true);
							invaders[j].addDamage(bullets[i].getPower());

							if (invaders[j].isKilled()) {
								handleAMurder(j);
							}
						}
					}
				}
			}
		}
	}
	
	private void handleAMurder(int index) {

		--activeInvaders;
		score += invaders[index].getScore();
		scoreTable.setScore(score);
		
		if (activeInvaders < 10) {
			for (int k = 0; k < invadersCount; ++k) {

				if (invaders[k].isVisible()) {
					if (invaders[k].getSpeed() > 0) {
						invaders[k].setSpeed(invaders[k].getSpeed() + 6);
					} else {
						invaders[k].setSpeed(invaders[k].getSpeed() - 6);
					}

					// frequency of steps for invaders is increased
					if (invaders[k].getJumpInterval() > 80) {
						invaders[k].setJumpInterval(invaders[k].getJumpInterval() - 30);
					}

					invaders[k].setDownShift(invaders[k].getDownShift() + 1);
				}
			}
		}
	}

	@Override
	public void drawFrame(Canvas canvas) {
		//Draw Background
		synchronized(this) {
			
			drawBackground(canvas);

			//draw a hero
			if (hero.isVisible()) {
				hero.draw(canvas);
			}

			drawEnemies(canvas);
			
			drawBullets(canvas);
			
			drawEnemyBullets(canvas);
	
			scoreTable.draw(canvas);

			drawMisc(canvas);
		}
	}

	private void drawEnemies(Canvas canvas) {

		if (activeInvaders > 0) {
			for(int i = 0; i < invadersCount; ++i) {
				if (invaders[i].isVisible()) {
					invaders[i].draw(canvas);
				}
			}
		}
		
		if (currentBoss != null && currentBoss.isVisible()) {
			currentBoss.draw(canvas);
		}
	}

	private void drawBackground(Canvas canvas) {
		//canvas.drawARGB(255, 1, 1, 1);
		canvas.drawBitmap(background, null, screenRect, null);
	}
	
	private void drawBullets(Canvas canvas) {
		for (int i = 0; i < maxBullets; ++i) {
			if (bullets[i].isVisible()) {
				bullets[i].draw(canvas);
			}
		}
	}

	private void drawMisc(Canvas canvas) {
		if (gameOver != NOT_YET) {
			canvas.drawARGB(160, 1, 1, 1);
			if (gameOver == WIN) {
				paint.setColor(Color.YELLOW);
				canvas.drawText("You WIN", fieldWidth/2 - 200, fieldHeight/2 - 50, paint);
			} else {
				paint.setColor(Color.RED);
				canvas.drawText("Game Over", fieldWidth/2 - 250, fieldHeight/2 - 50, paint);
			}
		}		
	}
	
	private void drawEnemyBullets(Canvas canvas) {
		for (int i = 0; i < maxEnemyBullets; ++i) {
			if (enemyBullets[i].isVisible()) {
				enemyBullets[i].draw(canvas);
			}
		}		
	}
	
	private void fireEnemyBullet(Rect src, Rect dest) {
		synchronized(this) { 
			for (int i = 0; i < maxEnemyBullets; ++i) {
				if (!(enemyBullets[i].isVisible())) {
					int left = src.centerX() - 5;
					int right = src.centerX() + 5;
					int top = src.bottom;
					int bottom = src.bottom + 20;

					enemyBullets[i].setBoundRect(left, top, right, bottom);
					enemyBullets[i].setVisible(true);
					break;
				}
			}
		}
	}

	private void fireBullet() {
		synchronized(this) { 
			for (int i = 0; i < maxBullets; ++i) {
				if (!(bullets[i].isVisible())) {
					int left = hero.getBoundRect().centerX() - 5;
					int right = hero.getBoundRect().centerX() + 5;
					int top = hero.getBoundRect().top - 20;
					int bottom = hero.getBoundRect().top;

					bullets[i].setBoundRect(left, top, right, bottom);
					bullets[i].setVisible(true);
					break;
				}
			}
		}
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		
		switch (arg1.getAction()) {
			case MotionEvent.ACTION_DOWN: {
					if (gameOver != NOT_YET) {
						reset();
					} else if (!hero.isKilled()){
	
						int x = Math.round(arg1.getX());
						int y = Math.round(arg1.getY());

						boolean intersects = false;

						Rect longRect = new Rect(hero.getBoundRect());

						int width = longRect.width();
						
						if (x > longRect.right) {
							longRect.right = x + width/2;
						} else if (x < longRect.left){
							longRect.left = x - width/2;
						}
						
						//Let's calculate a collision
						for (int i = 0; i < maxEnemyBullets; ++i) {
							if (enemyBullets[i].isVisible()) {
								if (Rect.intersects(longRect, enemyBullets[i].getBoundRect()) ) {
									enemyBullets[i].setVisible(false);
									hero.setPos(enemyBullets[i].getBoundRect().left, enemyBullets[i].getBoundRect().top);
									hero.addDamage(enemyBullets[i].getPower());

									intersects = true;
									break;
								}
							}
						}

						if (!intersects) {
							hero.setPos(x,y);
							fireBullet();
						}
					}
				}break;

			case MotionEvent.ACTION_MOVE: {
				if (!hero.isKilled()){
					hero.setPos(Math.round(arg1.getX()), Math.round(arg1.getY()));
				}
				}break;

			case MotionEvent.ACTION_UP: {
				}
			case MotionEvent.ACTION_CANCEL: {
				}break;
				
			default:
				return false;
		}

		return true;
	}
}
