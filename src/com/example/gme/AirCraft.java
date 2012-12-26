package com.example.gme;

public abstract class AirCraft extends GameObject {

	protected boolean killed = false;	
	protected int lifePower = 100;

	public AirCraft() {}

	public AirCraft(int left, int top, int right, int bottom, int speed)
	{
		setBoundRect(left, top, right, bottom);
		setSpeed(speed);
	}
	
	public void setKilled(boolean value) {
		killed = value;
	}

	public boolean isKilled() {
		return killed;
	}

	public void addDamage(int damage) {
		lifePower -= damage;
		
		if (lifePower <= 0) {
			killed = true;
		}
	}
}
