package com.sxtanna.mc.acolytes.backend;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.util.Vector;

import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.EntityTrackerEntry;
import net.minecraft.server.v1_8_R3.Vector3f;
import net.minecraft.server.v1_8_R3.WorldServer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public final class PetEntity1_8_8 extends EntityArmorStand implements PetEntity
{

	@Nullable
	private Entity    target;
	@NotNull
	private PetConfig config;

	@NotNull
	private final List<PetEntityGoal> goals = new ArrayList<>();


	PetEntity1_8_8(@NotNull final World world, @NotNull final PetConfig config)
	{
		super(((CraftWorld) world).getHandle());

		this.config = config;

		if (config.isHeadLook())
		{
			this.goals.add(new PetEntityGoalHeadLook());
		}

		this.goals.add(new PetEntityGoalPetOwner());

		if (config.isBobbing())
		{
			this.goals.add(new PetEntityGoalFloating());
		}
	}


	@Override
	public void setTargetEntity(@NotNull final org.bukkit.entity.Entity entity)
	{
		this.target = ((CraftEntity) entity).getHandle();
	}


	@Override
	public boolean damageEntity(final DamageSource damagesource, final float f)
	{
		return false;
	}

	@Override
	protected void a(final double d0, final boolean flag, final Block block, final BlockPosition blockposition)
	{

	}

	@Override
	public void move(final double x, final double y, final double z)
	{
		super.move(x, 0, z);
	}

	@Override
	public void K()
	{
		for (final PetEntityGoal goal : this.goals)
		{
			if (!goal.hasBeganGoal())
			{
				if (goal.attemptStart())
				{
					goal.update();
				}

				continue;
			}

			if (goal.shouldUpdate())
			{
				goal.update();
			}
			else
			{
				goal.cancel();
			}
		}

		super.K();
	}


	private final class PetEntityGoalHeadLook implements PetEntityGoal
	{

		@NotNull
		private final AtomicBoolean started = new AtomicBoolean();


		@Override
		public boolean attemptStart()
		{
			return target != null && !started.getAndSet(true);
		}

		@Override
		public boolean shouldUpdate()
		{
			return target != null;
		}

		@Override
		public boolean hasBeganGoal()
		{
			return started.get();
		}


		@Override
		public void update()
		{
			double vecX = target.locX;
			double vecY = target.locY;
			double vecZ = target.locZ;

			vecX -= locX;
			vecY -= locY;
			vecZ -= locZ;

			if (Math.abs(vecX) < 1.0 && Math.abs(vecY) < 1.0 && Math.abs(vecZ) < 1.0)
			{
				return; // too close
			}

			final EntityTrackerEntry entry = ((WorldServer) world).tracker.trackedEntities.get(getId());
			entry.m = entry.c;
			entry.xRot = -4;
			entry.i = -4;

			setPositionRotation(locX, locY, locZ, ((float) Math.toDegrees(Math.atan2(-vecX, vecZ))), 0.0f);

			if (config.isHeadLookAndPitch())
			{
				final float pitch = ((vecX == 0 && vecZ == 0) ? (vecY > 0 ? -90f : +90f) : (float) Math.toDegrees(Math.atan(-vecY / Math.sqrt((vecX * vecX) + (vecZ * vecZ))))) % 360.0f;
				setHeadPose(new Vector3f(pitch, 0.0f, 0.0f));
			}
		}

		@Override
		public void cancel()
		{
			started.set(false);
		}

	}

	private final class PetEntityGoalFloating implements PetEntityGoal
	{

		@NotNull
		private final AtomicBoolean started = new AtomicBoolean();


		final AtomicDouble  shifted = new AtomicDouble();
		final AtomicBoolean upwards = new AtomicBoolean();


		@Override
		public boolean attemptStart()
		{
			return !started.getAndSet(true);
		}

		@Override
		public boolean shouldUpdate()
		{
			return true;
		}

		@Override
		public boolean hasBeganGoal()
		{
			return true;
		}


		@Override
		public void update()
		{
			shifted.addAndGet(upwards.get() ? +config.getBobbingRangeShift() : -config.getBobbingRangeShift());

			if (shifted.get() >= +config.getBobbingRangeLimit())
			{
				upwards.set(false);
			}

			if (shifted.get() <= -config.getBobbingRangeLimit())
			{
				upwards.set(true);
			}

			setPosition(locX, locY += upwards.get() ? +config.getBobbingSpeed() : -config.getBobbingSpeed(), locZ);
		}

		@Override
		public void cancel()
		{
			started.set(false);
			shifted.set(0.0);
			upwards.set(false);
		}

	}

	private final class PetEntityGoalPetOwner implements PetEntityGoal
	{

		@NotNull
		private final AtomicBoolean started = new AtomicBoolean();


		@Override
		public boolean attemptStart()
		{
			return target != null && h(target) >= (config.getFollowRangeMin() * config.getFollowRangeMin()) && !started.getAndSet(true);
		}

		@Override
		public boolean shouldUpdate()
		{
			return target != null && (Math.abs(target.locY - locY) > 1) || h(target) >= (config.getFollowRangeMax() * config.getFollowRangeMax());
		}

		@Override
		public boolean hasBeganGoal()
		{
			return started.get();
		}


		@Override
		public void update()
		{
			double vecX = target.locX;
			double vecY = target.locY;
			double vecZ = target.locZ;

			vecX -= locX;
			vecY -= locY;
			vecZ -= locZ;

			final double len = Math.sqrt((vecX * vecX) + (vecY * vecY) + (vecZ * vecZ));
			vecX /= len;
			vecY /= len;
			vecZ /= len;

			final double mul = config.getFollowSpeed() * 0.25;
			vecX *= mul;
			vecY *= mul;
			vecZ *= mul;

			setPosition(locX += vecX, locY += vecY, locZ += vecZ);
		}

		@Override
		public void cancel()
		{
			started.set(false);

			move(0.0, 0.0, 0.0);
		}

	}

}