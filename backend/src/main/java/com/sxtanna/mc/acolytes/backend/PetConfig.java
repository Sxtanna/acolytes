package com.sxtanna.mc.acolytes.backend;

import org.jetbrains.annotations.Contract;

public final class PetConfig
{

	private final boolean headLook;

	private final double followSpeed;

	private final double followRangeMin;
	private final double followRangeMax;


	@Contract(pure = true)
	public PetConfig(final boolean headLook, final double followSpeed, final double followRangeMin, final double followRangeMax)
	{
		this.headLook    = headLook;
		this.followSpeed = followSpeed;

		this.followRangeMin = followRangeMin;
		this.followRangeMax = followRangeMax;
	}


	@Contract(pure = true)
	public boolean isHeadLook()
	{
		return headLook;
	}

	@Contract(pure = true)
	public double getFollowSpeed()
	{
		return this.followSpeed;
	}


	@Contract(pure = true)
	public double getFollowRangeMin()
	{
		return this.followRangeMin;
	}

	@Contract(pure = true)
	public double getFollowRangeMax()
	{
		return this.followRangeMax;
	}

}