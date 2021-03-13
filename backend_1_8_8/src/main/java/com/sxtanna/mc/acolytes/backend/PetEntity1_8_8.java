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

		this.goals.add(new PetEntityGoalPetOwner());

		if (config.isBobbing())
		{
			this.goals.add(new PetEntityGoalFloating());
		}
		if (config.isHeadLook())
		{
			this.goals.add(new PetEntityGoalHeadLook());
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
	public void K()
	{
		super.K();

		for (final PetEntityGoal goal : this.goals)
		{
			if (goal.hasBeganGoal())
			{
				if (goal.shouldUpdate())
				{
					goal.update();
				}
				else
				{
					goal.cancel();
				}

				continue;
			}


			if (!goal.attemptStart())
			{
				continue;
			}

			goal.update();
		}
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
			final Location location = getBukkitEntity().getLocation();
			location.setDirection(target.getBukkitEntity().getLocation().toVector().subtract(location.toVector()));

			setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
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

			PetEntity1_8_8.this.motY = upwards.get() ? +config.getBobbingSpeed() : -config.getBobbingSpeed();
		}

		@Override
		public void cancel()
		{
			started.set(false);
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
			final Vector vector = target.getBukkitEntity().getLocation().toVector().subtract(getBukkitEntity().getLocation().toVector()).normalize();
			vector.multiply(config.getFollowSpeed() * 0.25);

			move(vector.getX(), vector.getY(), vector.getZ());
		}

		@Override
		public void cancel()
		{
			started.set(false);

			move(0.0, 0.0, 0.0);
		}

	}

}