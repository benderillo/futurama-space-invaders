package com.example.gme;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.Surface;
import android.view.Surface.OutOfResourcesException;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

	protected Game game;
	
	private RenderLoop renderer;
	
	private SurfaceHolder mHolder = null;
	protected Canvas mCanvas;

	public static GameView createView(Game game, Context context) {
		
		GameView view = new GameView(context);
		// Making a thread based renderer. 
		// It will ask the world to draw itself to the canvas
		// with average 30fps
		view.renderer = view.new RenderLoop(game, view);

		return view;
	}


	private GameView(Context context)
	{
		super(context);
		//Using Android's SurfaceView+ SurfaceHolder concept
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);

	}

	public void doStart() {
		// TODO Auto-generated method stub

		if (renderer.getRunningState() == RenderLoop.PAUSED) {
			renderer.doResume();
		} else {
			renderer.doStart();
		}
		
	}

	public void doPause() {
		// TODO Auto-generated method stub

		renderer.doPause();
		
	}

	public void doStop() {
		// TODO Auto-generated method stub

		renderer.doStop();
		
	}

	public Bitmap getFrameBitmap() {
		// TODO Auto-generated method stub
		return null;
	}

	public Canvas getCanvas() {
		
		if (mHolder != null) {
			Surface surface = mHolder.getSurface();
			
			if (surface != null) {
				try {
					mCanvas = surface.lockCanvas(null);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (OutOfResourcesException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return mCanvas;
	}

	//this will swap foreground and background buffers
	public void swapBuffers() {
		if (mCanvas != null) {
			mHolder.getSurface().unlockCanvasAndPost(mCanvas);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
		mHolder = arg0;	
	}


	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		mHolder = arg0;
	}


	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		
	}
	
	// Private renderer class
	// This class is not dependent on View implementation
	// Exact view implementation is abstracted away via some wrapper methods 
	private class RenderLoop extends Thread {
		private Game mGame;
		private GameView mView;
		private boolean running = false;
		private int state = STOPPED;
		private static final int STOPPED = 0;
		private static final int PAUSED = 1;
		private static final int RUNNING = 2;
		private long lastTimeStamp;
		private long currentTimeStamp;


		public RenderLoop(Game game, GameView view) {
			mGame = game;
			mView = view;
			state = STOPPED;
		}

		public int getRunningState() {
			return state;
		}
		
		public void doStart() {
			running = true;
			state = RUNNING;
			lastTimeStamp = System.currentTimeMillis();
			start();
		}
		
		public void doPause() {
			state = PAUSED;
		}
		
		public void doResume() {
			state = RUNNING;
		}
		
		public void doStop() {
			running = false;
			state = STOPPED;
		}

		public void run() {
			while (running) {

				if (state == RUNNING) {
					
					//we use getCanvas() to make renderer independent from the view implementation
					if (mView.getCanvas() != null) {
						currentTimeStamp = System.currentTimeMillis();

						mGame.update(currentTimeStamp - lastTimeStamp);

						lastTimeStamp = currentTimeStamp;

						//Let the game fill up our canvas 
						mGame.drawFrame(mView.mCanvas);

						// Yeah, swapBuffers() like an old double-buffering approach :)
						// again, we don't care how the View will handle that. 
						// All we need to know is that the view will not try to render the canvas
						// before this point in time.
						mView.swapBuffers();
					}
				}
/*
				try {
					sleep(33);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
*/			}
		}
	}
}
