package com.example.gme;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class ScoreTable extends GameObject {

	private int score;
	private Paint mPaint;
	private Paint mTextPaint;
	private Bitmap bmp = null;
	private int lives = 0;
	private Rect rect = new Rect();
	
	public void setLives( int value) { 
		lives = value;
	}

	public ScoreTable() {
		score = 0;
		
		mPaint = new Paint();
		mPaint.setColor(Color.WHITE);
		mPaint.setAntiAlias(true);

		mTextPaint = new Paint();
		mTextPaint.setColor(Color.WHITE);
		mTextPaint.setTextSize(20);
		mTextPaint.setAntiAlias(true);

		bmp = Bitmaps.getBitmap(Bitmaps.HERO);
		
	}
	public void setScore(int value) {score = value; }

	@Override
	public void draw(Canvas area) {
		
		mPaint.setColor(Color.WHITE);
		area.drawRect(getBoundRect(), mPaint);

		mPaint.setColor(Color.BLACK);

		area.drawRect(getBoundRect().left + 2, getBoundRect().top + 2, getBoundRect().right - 2, getBoundRect().bottom - 2, mPaint);

		area.drawText(String.valueOf(score), getBoundRect().left + 6, getBoundRect().bottom - 6, mTextPaint);
		
		rect.left = getBoundRect().right - (30 * 3 + 10);
		rect.top = getBoundRect().top + 3;
		rect.right = rect.left + 30;
		rect.bottom = getBoundRect().bottom - 3;
		
		for (int i = 0; i < lives; ++i) {
			rect.left += i*30 + 2;
			rect.right += i*30 + 2;

			area.drawBitmap(bmp, null, rect, null);
		}
	}

	@Override
	public void update(long timeDelta) {
		// TODO Auto-generated method stub
		
	}

}
