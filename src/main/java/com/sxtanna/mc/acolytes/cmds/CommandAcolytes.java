package com.sxtanna.mc.acolytes.cmds;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;

import com.sxtanna.mc.acolytes.AcolytesPlugin;
import com.sxtanna.mc.acolytes.conf.AcolytesConfig;
import com.sxtanna.mc.acolytes.conf.Resolved;
import com.sxtanna.mc.acolytes.data.Pet;
import com.sxtanna.mc.acolytes.data.attr.PetAttributes;
import com.sxtanna.mc.acolytes.lang.Lang;
import com.sxtanna.mc.acolytes.menu.Menu;
import com.sxtanna.mc.acolytes.menu.impl.MenuPets;
import com.sxtanna.mc.acolytes.util.bukkit.Colors;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Flags;
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
		Menu.decode(plugin.getConfiguration().get(AcolytesConfig.Menus.PETS_MENU_LAYOUT)).ifPresent(decode -> {
			new MenuPets(plugin, player, decode).open(player);
		});
	}


	@Subcommand("name")
	@CommandPermission("pets.user.name")
	public void name(@NotNull final Player player, @NotNull @Flags("active") final Pet pet, @NotNull final String name)
	{
		final String newName = Colors.colorize(name);

		if (newName.length() < customNameLengthMin.get() || newName.length() > customNameLengthMax.get())
		{
			throw new InvalidCommandArgument(String.format("Pet name length must be between %d and %d",
			                                               customNameLengthMin.get(),
			                                               customNameLengthMax.get()),
			                                 false);
		}

		final String oldName = pet.update(PetAttributes.NAME, newName).orElse(null);
		pet.pushAttrs(plugin);

		plugin.send(player,
		            Lang.CMDS__USERS__NAME_UPDATE,

		            "{new_name}",
		            newName,

		            "{old_name}",
		            oldName != null ? oldName : "");

	}

}