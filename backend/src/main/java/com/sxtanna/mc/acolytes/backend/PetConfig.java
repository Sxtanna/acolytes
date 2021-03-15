package com.sxtanna.mc.acolytes.backend;

import org.jetbrains.annotations.Contract;

public final class PetConfig
{

	private final boolean smallHead;

	private final boolean headLook;
	private final boolean headLookAndPitch;


	private final boolean bobbing;

	private final double bobbingSpeed;
	private final double bobbingRangeShift;
	private final double bobbingRangeLimit;


	private final double followSpeed;

	private final double followRangeMin;
	private final double followRangeMax;

	private final double teleportDistance;

	private final double teleportOffsetX;
	private final double teleportOffsetY;
	private final double teleportOffsetZ;


	@Contract(pure = true)
	public PetConfig(final boolean smallHead,
	                 final boolean headLook,
	                 final boolean headLookAndPitch,

	                 final boolean bobbing,
	                 final double bobbingSpeed,
	                 final double bobbingRangeShift,
	                 final double bobbingRangeLimit,

	                 final double followSpeed,
	                 final double followRangeMin,
	                 final double followRangeMax,

	                 final double teleportDistance,

	                 final double teleportOffsetX,
	                 final double teleportOffsetY,
	                 final double teleportOffsetZ)
	{
		this.smallHead        = smallHead;
		this.headLook         = headLook;
		this.headLookAndPitch = headLookAndPitch;

		this.bobbing           = bobbing;
		this.bobbingSpeed      = bobbingSpeed;
		this.bobbingRangeShift = bobbingRangeShift;
		this.bobbingRangeLimit = bobbingRangeLimit;

		this.followSpeed      = followSpeed;
		this.followRangeMin   = followRangeMin;
		this.followRangeMax   = followRangeMax;
		this.teleportDistance = teleportDistance;
		this.teleportOffsetX  = teleportOffsetX;
		this.teleportOffsetY  = teleportOffsetY;
		this.teleportOffsetZ  = teleportOffsetZ;
	}


	@Contract(pure = true)
	public boolean isSmallHead()
	{
		return smallHead;
	}

	@Contract(pure = true)
	public boolean isHeadLook()
	{
		return headLook;
	}

	@Contract(pure = true)
	public boolean isHeadLookAndPitch()
	{
		return headLookAndPitch;
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


	@Contract(pure = true)
	public double getTeleportDistance()
	{
		return teleportDistance;
	}


	@Contract(pure = true)
	public double getTeleportOffsetX()
	{
		return teleportOffsetX;
	}

	@Contract(pure = true)
	public double getTeleportOffsetY()
	{
		return teleportOffsetY;
	}

	@Contract(pure = true)
	public double getTeleportOffsetZ()
	{
		return teleportOffsetZ;
	}

}