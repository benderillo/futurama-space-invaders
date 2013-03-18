package com.example.gme;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;

public class BigBrain extends LevelBoss {

	private Bitmap bmp1, bmp2, bmp3, bmp4, bmp5, bmp6;

	private int hitPhase = 0;
	
	private long elapsedTime = 0;
	private long shiftTime = 3000;
	private long elapsedXTime = 0;
	private long shiftXTime = 1000;
	private float[] bezier = new float[10];
	
	public BigBrain() {
		this(100, 0, 200, 100, 8, Color.RED);
	}

	public BigBrain(int left, int top, int right, int bottom, int speed, int color) {
		super(left, top, right, bottom, speed, color);

		bmp1 = Bitmaps.getBitmap(Bitmaps.BIG_BRAIN1);
		bmp2 = Bitmaps.getBitmap(Bitmaps.BIG_BRAIN2);
		bmp3 = Bitmaps.getBitmap(Bitmaps.BIG_BRAIN3);
		bmp4 = Bitmaps.getBitmap(Bitmaps.BIG_BRAIN4);
		bmp5 = Bitmaps.getBitmap(Bitmaps.BIG_BRAIN5);
		bmp6 = Bitmaps.getBitmap(Bitmaps.BIG_BRAIN6);
		
		lifePower = 2000;
		SCORE = 20000;
		
		
	}

	private Point getPathPos(long timeDelta) {
		
		
		return null;
	}

	@Override
	public void draw(Canvas area) {
		if (killed) {
			//area.drawBitmap(bitmap, matrix, paint)
			setDefeated(true);
			setVisible(false);
		} else if (hitPhase > 0){
			area.drawBitmap(getHitBmp(), null, getBoundRect(), null);
			--hitPhase;
		}
		else {
			area.drawBitmap(bmp1, null, getBoundRect(), null);
		}
	}
	
	public void addDamage(int damage) {
		super.addDamage(damage);
		hitPhase = 6;
	}

	private Bitmap getHitBmp() {
		Bitmap bmp = bmp1;

		switch (hitPhase) {
		case 1:
			bmp = bmp1;
			break;
		case 2:
			bmp = bmp2;
			break;
		case 3:
			bmp = bmp3;
			break;
		case 4:
			bmp = bmp4;
			break;
		case 5:
			bmp = bmp5;
			break;
		case 6:
			bmp = bmp6;
			break;
		}
		
		return bmp;
	}

	@Override
	public void update(long timeDelta) {
		elapsedTime += timeDelta;
		elapsedXTime += timeDelta;
		
		if (elapsedXTime >= shiftXTime) {
			//if rand is greater than 0.5 then let's shift right.
			if (Math.random() > 0.5) {
				double random = Math.random()*(spaceRect.right - getBoundRect().right);
				int offset = (int)Math.round(random);
				
				setBoundRect(getBoundRect().left + offset , getBoundRect().top, getBoundRect().right + offset, getBoundRect().bottom);
			} else {
				double random = Math.random()*(getBoundRect().left - spaceRect.left);
				int offset = (int)Math.round(random);
				
				setBoundRect(getBoundRect().left - offset , getBoundRect().top, getBoundRect().right - offset, getBoundRect().bottom);				
			}
			elapsedXTime = 0;
		}

		if (elapsedTime >= shiftTime) {
			setBoundRect(getBoundRect().left , getBoundRect().top + 40, getBoundRect().right, getBoundRect().bottom + 40);
			elapsedTime = 0;
		}
	}
}
