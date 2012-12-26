package com.example.gme;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class EnemyBullet extends Bullet {

	private static Bitmap bmp;

	EnemyBullet() {
		bmp = Bitmaps.getBitmap(Bitmaps.ENEMY_BULLET);
	}

	@Override
	public void draw(Canvas area) {
		area.drawBitmap(bmp, null, getBoundRect(), null);
	}
}
