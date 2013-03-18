package com.example.gme;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class EnemyFlight extends AirCraft {

	private static Bitmap bmp1;
	private static Bitmap bmp2;
	private static Bitmap bmp3;
	protected int SCORE = 100;
	private boolean plug = false;
	
	private int invadersXShiftTime = 600;
	private int invadersBounceCounter = 0;
	private int downShift = 10;
	private long elapsedTime = 0;
	private char numBounces = 0;

	public EnemyFlight(int left, int top, int right, int bottom, int speed, int color) {
		super(left, top, right, bottom, speed);
		setColor(color);
		bmp1 = Bitmaps.getBitmap(Bitmaps.ALIEN1);
		bmp2 = Bitmaps.getBitmap(Bitmaps.ALIEN2);
		bmp3 = Bitmaps.getBitmap(Bitmaps.BOOM);
	}

	public void setColor(int color) {

	}
	
	public int getScore() {return SCORE; }

	public void step() { plug = !plug; }

	public void draw(Canvas area) {
		if (killed) {
			area.drawBitmap(bmp3, null, getBoundRect(), null);
			setVisible(false);
		} else if (plug) {
			area.drawBitmap(bmp1, null, getBoundRect(), null);
		} else {
			area.drawBitmap(bmp2, null, getBoundRect(), null);
		}
	}
	
	public void stepBack() {
		int left = getBoundRect().left + (int)getSpeed();
		int right = getBoundRect().right + (int)getSpeed();

		setBoundRect(left, getBoundRect().top, right,
				getBoundRect().bottom);

		step();
	}

	@Override
	public void update(long timeDelta) {

		if (isVisible()) {			
			if (invadersBounceCounter > numBounces) {
				// Shift down one gap
				int top = getBoundRect().top + downShift * 2;
				int bottom = getBoundRect().bottom + downShift * 2;

				setBoundRect(getBoundRect().left, top, getBoundRect().right,
						bottom);
	
				// frequency of steps for invaders is increased
				if (invadersXShiftTime > 400) {
					invadersXShiftTime -= 50;
				}
	
				invadersBounceCounter = 0;
			}
	
			// accumulate how much time passed since last invaders shift
			elapsedTime += timeDelta;
	
			// if it is time for shift, let's do the shift
			if ((elapsedTime / invadersXShiftTime) > 0) {
				int left = getBoundRect().left + (int)getSpeed();
				int right = getBoundRect().right + (int)getSpeed();

				setBoundRect(left, getBoundRect().top, right,
						getBoundRect().bottom);

				step();

				elapsedTime = 0;
			}
		}
	}
	
	public void handleWall() {
		++invadersBounceCounter;
		setSpeed(0 - (int)getSpeed());
		elapsedTime = 0;
	}

	public int getJumpInterval() {
		return invadersXShiftTime;
	}

	public void setJumpInterval(int value) {
		invadersXShiftTime = value;
	}

	public int getDownShift() {
		return downShift;
	}

	public void setDownShift(int value) {
		downShift = value;
	}
}

