package com.sxtanna.mc.acolytes.backend;

public interface PetEntityGoal
{

	boolean attemptStart();

	boolean shouldUpdate();

	boolean hasBeganGoal();


	void update();

	void cancel();

}