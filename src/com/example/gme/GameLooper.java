package com.example.gme;

public class GameLooper extends Thread {
	private Game mGame;
	private boolean running = false;
	
	private static final int STOPPED = 0;
	private static final int PAUSED = 1;
	private static final int RUNNING = 2;
	private int state = STOPPED;
	private long lastTimeStamp;
	private long currentTimeStamp;

	
	public GameLooper(Game game) {
		mGame = game;
		state = STOPPED;
	}

	public void startLoop() {
		if (state == STOPPED) {
			running = true;
			start();
		}

		lastTimeStamp = System.currentTimeMillis();

		state = RUNNING;
	}

	public void pauseLoop() {
		state = PAUSED;
	}

	public void stopLoop(){
		running = false;
		
		try {
			this.join(300);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while(running) {
			if (state == RUNNING) {
				//Simply update the game's state in time
				currentTimeStamp = System.currentTimeMillis();

				//mGame.update(currentTimeStamp - lastTimeStamp);

				lastTimeStamp = currentTimeStamp;
			}

			try {
				// Don't ask ;)
				sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
