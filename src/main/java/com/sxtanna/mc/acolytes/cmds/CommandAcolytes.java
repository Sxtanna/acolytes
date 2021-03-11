package com.sxtanna.mc.acolytes.cmds;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;

import com.sxtanna.mc.acolytes.AcolytesPlugin;
import com.sxtanna.mc.acolytes.conf.AcolytesConfig;
import com.sxtanna.mc.acolytes.conf.Resolved;
import com.sxtanna.mc.acolytes.data.Pet;
import com.sxtanna.mc.acolytes.data.attr.PetAttributes;
import com.sxtanna.mc.acolytes.menu.Menu;
import com.sxtanna.mc.acolytes.menu.impl.MenuPets;
import com.sxtanna.mc.acolytes.util.Colors;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;

@CommandAlias("pet|pets")
public final class CommandAcolytes extends BaseCommand
{

	@NotNull
	private final AcolytesPlugin plugin;


	@NotNull
	private final Resolved<Integer> customNameLengthMin = Resolved.from(AcolytesConfig.Basic.PET_CUSTOM_NAME_LENGTH_MIN);
	@NotNull
	private final Resolved<Integer> customNameLengthMax = Resolved.from(AcolytesConfig.Basic.PET_CUSTOM_NAME_LENGTH_MAX);


	@Contract(pure = true)
	public CommandAcolytes(@NotNull final AcolytesPlugin plugin)
	{
		this.plugin = plugin;
	}


	@Default
	@CommandPermission("pets.user.menu")
	public void def(@NotNull final Player player)
	{
		final Menu menu = new MenuPets(plugin);
		menu.open(player);
	}


	@Subcommand("name")
	@CommandPermission("pets.user.name")
	public void name(@NotNull final Player player, @NotNull @Flags("active") final Pet pet, @NotNull @Single final String name)
	{
		final String oldName = pet.select(PetAttributes.NAME);
		final String newName = Colors.colorize(name);

		if (newName.length() < customNameLengthMin.get() || newName.length() > customNameLengthMax.get())
		{
			throw new InvalidCommandArgument(String.format("Pet name length must be between %d and %d",
			                                               customNameLengthMin.get(),
			                                               customNameLengthMax.get()),
			                                 false);
		}

		pet.update(PetAttributes.NAME, newName);

		// todo: send message "pet name has been updated from `oldName` to `newName`"

	}

}