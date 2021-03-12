package com.sxtanna.mc.acolytes.backend;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;

import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityZombie;
import net.minecraft.server.v1_8_R3.Navigation;
import net.minecraft.server.v1_8_R3.PathfinderGoal;
import net.minecraft.server.v1_8_R3.PathfinderGoalFloat;
import net.minecraft.server.v1_8_R3.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector;

import java.lang.reflect.Field;
import java.util.List;

public final class PetEntity1_8_8 extends EntityZombie implements PetEntity
{

	@Nullable
	private Entity    target;
	@NotNull
	private PetConfig config;


	public PetEntity1_8_8(final World world, @NotNull final PetConfig config)
	{
		super(((CraftWorld) world).getHandle());

		this.config = config;

		clearGoals();

		((Navigation) this.getNavigation()).b(true);

		this.goalSelector.a(0, new PathfinderGoalFloat(this));
		this.goalSelector.a(5, new PathfinderGoalFollowPetEntityOwner());
		this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
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


	@SuppressWarnings("rawtypes")
	private void clearGoals()
	{
		try
		{
			final Field b = PathfinderGoalSelector.class.getDeclaredField("b");
			b.setAccessible(true);

			final Field c = PathfinderGoalSelector.class.getDeclaredField("c");
			c.setAccessible(true);


			((List) b.get(this.goalSelector)).clear();
			((List) c.get(this.goalSelector)).clear();

			((List) b.get(this.targetSelector)).clear();
			((List) c.get(this.targetSelector)).clear();
		}
		catch (final NoSuchFieldException | IllegalAccessException ex)
		{
			ex.printStackTrace();
		}
	}


	private final class PathfinderGoalFollowPetEntityOwner extends PathfinderGoal
	{

		// shouldExecute
		@Override
		public boolean a()
		{
			return PetEntity1_8_8.this.target != null && PetEntity1_8_8.this.h(PetEntity1_8_8.this.target) >= (config.getFollowRangeMin() * config.getFollowRangeMin());
		}

		// continueExecuting
		@Override
		public boolean b()
		{
			return !getNavigation().m() && PetEntity1_8_8.this.h(PetEntity1_8_8.this.target) > (config.getFollowRangeMax() * config.getFollowRangeMax());
		}


		// isInterruptible
		@Override
		public boolean i()
		{
			return super.i();
		}


		// startExecuting
		@Override
		public void c()
		{

		}

		// resetTask
		@Override
		public void d()
		{
			getNavigation().n();
		}

		// updateTask
		@Override
		public void e()
		{
			if (getNavigation().a(target, config.getFollowSpeed()))
			{
				return;
			}

			// check if distance is too high, then teleport
		}

	}

}