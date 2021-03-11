package com.sxtanna.mc.acolytes.pets.repository;

import com.sxtanna.mc.acolytes.base.State;

public interface PetRepository extends State
{

	@Override
	void load();

	@Override
	void kill();

}