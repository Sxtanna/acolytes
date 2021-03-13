package com.sxtanna.mc.acolytes.backend;

import org.jetbrains.annotations.Contract;

public final class PetConfig
{

	private final boolean headLook;


	private final boolean bobbing;

	private final double bobbingSpeed;
	private final double bobbingRangeShift;
	private final double bobbingRangeLimit;


	private final double followSpeed;

	private final double followRangeMin;
	private final double followRangeMax;


	@Contract(pure = true)
	public PetConfig(final boolean headLook, final boolean bobbing, final double bobbingSpeed, final double bobbingRangeShift, final double bobbingRangeLimit, final double followSpeed, final double followRangeMin, final double followRangeMax)
	{
		this.headLook = headLook;

		this.bobbing           = bobbing;
		this.bobbingSpeed      = bobbingSpeed;
		this.bobbingRangeShift = bobbingRangeShift;
		this.bobbingRangeLimit = bobbingRangeLimit;

		this.followSpeed    = followSpeed;
		this.followRangeMin = followRangeMin;
		this.followRangeMax = followRangeMax;
	}


	@Contract(pure = true)
	public boolean isHeadLook()
	{
		return headLook;
	}


	@Contract(pure = true)
	public boolean isBobbing()
	{
		return bobbing;
	}


	@Contract(pure = true)
	public double getBobbingSpeed()
	{
		return bobbingSpeed;
	}

	@Contract(pure = true)
	public double getBobbingRangeShift()
	{
		return bobbingRangeShift;
	}

	@Contract(pure = true)
	public double getBobbingRangeLimit()
	{
		return bobbingRangeLimit;
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