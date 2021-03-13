package com.sxtanna.mc.acolytes.pets;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.sxtanna.mc.acolytes.AcolytesPlugin;
import com.sxtanna.mc.acolytes.backend.PetEntityProvider;
import com.sxtanna.mc.acolytes.backend.PetEntityProvider1_8_8;
import com.sxtanna.mc.acolytes.backend.PlayerSkinAdapter;
import com.sxtanna.mc.acolytes.backend.PlayerSkinAdapter1_8_8;
import com.sxtanna.mc.acolytes.base.State;
import com.sxtanna.mc.acolytes.pets.controller.PetController;
import com.sxtanna.mc.acolytes.pets.controller.PetControllerLocal;
import com.sxtanna.mc.acolytes.pets.repository.PetRepository;
import com.sxtanna.mc.acolytes.pets.repository.PetRepositoryLocal;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public final class AcolytesModule implements State
{

	@NotNull
	private final AcolytesPlugin plugin;


	@Nullable
	private PetController controller;
	@Nullable
	private PetRepository repository;

	@NotNull
	private final AtomicReference<PlayerSkinAdapter> adapter = new AtomicReference<>();
	@NotNull
	private final AtomicReference<PetEntityProvider> provider = new AtomicReference<>();


	public AcolytesModule(@NotNull final AcolytesPlugin plugin)
	{
		this.plugin = plugin;
	}


	@Override
	public void load()
	{
		switch (plugin.getServer().getClass().getPackage().getName().split("\\.")[3])
		{
			case "v1_8_R3":
				this.adapter.set(new PlayerSkinAdapter1_8_8());
				this.provider.set(new PetEntityProvider1_8_8());
				break;
			default:
				throw new IllegalStateException("could not find pet entity provider for server version");
		}


		getProvider().initialize();

		this.controller = new PetControllerLocal(this.plugin);
		this.repository = new PetRepositoryLocal(this.plugin);

		getRepository().load();
		getController().load();
	}

	@Override
	public void kill()
	{
		getController().kill();
		getRepository().kill();

		this.controller = null;
		this.repository = null;
	}


	@Contract(pure = true)
	public @NotNull PetController getController()
	{
		return Objects.requireNonNull(this.controller, "acolytes module not enabled");
	}

	@Contract(pure = true)
	public @NotNull PetRepository getRepository()
	{
		return Objects.requireNonNull(this.repository, "acolytes module not enabled");
	}


	public @NotNull PlayerSkinAdapter getAdapter()
	{
		return Objects.requireNonNull(this.adapter.get(), "adapter not resolved yet!");
	}

	public @NotNull PetEntityProvider getProvider()
	{
		return Objects.requireNonNull(this.provider.get(), "provider not resolved yet!");
	}

}