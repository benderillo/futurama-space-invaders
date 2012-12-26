package com.example.gme;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

	private static Game mGame;
	private static GameView myView;
	private static GameLooper looper;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		//Create a helper static class responsible for mapping of bitmap resources to IDs
		Bitmaps.createInstance(this);

		// Make an object of current game.
		// If you make your own game, just make sure it is all encapsulated inside a separate class
		// that inherits from abstract Game class
		mGame = new SpaceInvadersGame(this);

		//reset all states
		mGame.reset();

		// Making a looper. The thread that is going to fire update(deltaTime) events to the game
		// Actually, for a simple 2D game it is not really necessary
		// as it can be combined with the renderer and the world can be recalculated on every frame
		looper = new GameLooper(mGame);

		//Creating a custom view: this is basically a canvas + thread-renderer
		myView = GameView.createView(mGame, getApplicationContext());

		setContentView(myView);

		//Make it touchable
		myView.setEnabled(true);
		//Redirect touches to the game object, let the game to take care of it
		myView.setOnTouchListener(mGame);
	}

	public void onResume()
	{
		super.onResume();
		myView.doStart();
		looper.startLoop();
		
	}
	
	public void onPause()
	{
		super.onPause();
		myView.doPause();
		looper.pauseLoop();
	}
	
	public void onDestroy()
	{
		super.onDestroy();
		myView.doStop();
		looper.stopLoop();
		
		mGame.release();
	}
}
