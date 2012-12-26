package com.example.gme;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public abstract class Game implements OnTouchListener {
	public Game(Context context){ };
	//Re-calculate the world's state for given time
	public abstract void update(long timeDelta);
	//We get a bitmap so we need to draw everything here
	public abstract void drawFrame(Canvas canvas) ;
	public void reset() {}
	public void release() {}
	public boolean onTouch(View arg0, MotionEvent arg1) { return false; }
}
