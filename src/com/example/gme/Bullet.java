package com.example.gme;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Bullet extends GameObject {

	private static Bitmap bmp;
	protected int damage = 100;
	protected int speedX = 0;

	public Bullet() {
		bmp = Bitmaps.getBitmap(Bitmaps.BULLET);
	}

	@Override
	public void draw(Canvas area) {
		area.drawBitmap(bmp, null, getBoundRect(), null);
	}

	@Override
	public void update(long timeDelta) {

		int dy = (int) ( (getSpeed() * timeDelta) / 10);

		int dx = (int) (getXSpeed()*timeDelta);
		
		int top = getBoundRect().top += dy;
		int bottom = getBoundRect().bottom += dy;
		
		int left = getBoundRect().left += dx;
		int right = getBoundRect().right += dx;
		
		if ( (top <= 0) || (bottom >= spaceRect.bottom)) {
			setVisible(false);
		} else {
			setBoundRect(left, top, right, bottom);
		}

	}
	
	private int getXSpeed() {
		return speedX;
	}

	public void setXSpeed(int value) { speedX = value; }
	public int getPower() { return damage; }
}
