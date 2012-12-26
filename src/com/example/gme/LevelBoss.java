package com.example.gme;

public class LevelBoss extends EnemyFlight {

	public LevelBoss(int left, int top, int right, int bottom, int speed,
			int color) {
		super(left, top, right, bottom, speed, color);
		// TODO Auto-generated constructor stub
	}

	private boolean defeated = false;
	
	public boolean isDefeated() { return defeated; }
	public void setDefeated(boolean value) { defeated = value; }
}
