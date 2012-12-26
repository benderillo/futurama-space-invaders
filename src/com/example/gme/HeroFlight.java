package com.example.gme;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;


public class HeroFlight extends AirCraft {
	private static Bitmap bmp;
	private static Bitmap bmp1;
	private Paint mPaint;
	private char delay = 10;

	public HeroFlight(int left, int top, int right, int bottom, int speed, int color) {
		super(left, top, right, bottom, speed);
		setColor(color);
		bmp = Bitmaps.getBitmap(Bitmaps.HERO);
		bmp1 = Bitmaps.getBitmap(Bitmaps.BOOM);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		lifePower = 100;
	}

	public void setColor(int color) {
	}

	public void draw(Canvas area) {
		if (killed) {
			area.drawBitmap(bmp1, null, getBoundRect(), mPaint);

			if(--delay <= 0) {
				setVisible(false);
			}
		} else {
			area.drawBitmap(bmp, null, getBoundRect(), mPaint);
		}
	}

	@Override
	public void update(long timeDelta) {
		// Hero Flight is a special case,
		// We don't need to calculate anything here 
	}
}
