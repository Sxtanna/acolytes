package com.sxtanna.mc.acolytes.cmds;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import org.bukkit.command.CommandSender;

import com.sxtanna.mc.acolytes.AcolytesPlugin;
import com.sxtanna.mc.acolytes.conf.AcolytesConfig;
import com.sxtanna.mc.acolytes.conf.Resolved;
import com.sxtanna.mc.acolytes.data.Pet;
import com.sxtanna.mc.acolytes.data.attr.PetAttributes;
import com.sxtanna.mc.acolytes.util.Colors;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;

@CommandAlias("petadmin|petsadmin")
public final class CommandAcolytesAdmin extends BaseCommand
{

	@NotNull
	private final AcolytesPlugin plugin;


	@NotNull
	private final Resolved<Integer> customNameLengthMin = Resolved.from(AcolytesConfig.Basic.PET_CUSTOM_NAME_LENGTH_MIN);
	@NotNull
	private final Resolved<Integer> customNameLengthMax = Resolved.from(AcolytesConfig.Basic.PET_CUSTOM_NAME_LENGTH_MAX);


	@Contract(pure = true)
	public CommandAcolytesAdmin(@NotNull final AcolytesPlugin plugin)
	{
		this.plugin = plugin;
	}


	@Subcommand("enable")
	@CommandCompletion("@players @pets")
	@CommandPermission("pets.admin.enable")
	public void enable(@NotNull final CommandSender sender, @NotNull final OnlinePlayer target, @NotNull @Flags("target") final Pet pet)
	{
		plugin.getModule().getController().getActive(target.getPlayer()).ifPresent(active -> {
			plugin.getModule().getController().kill(target.getPlayer(), active);

			// todo: send message "active pet disabled"

		});

		plugin.getModule().getController().load(target.getPlayer(), pet);

		// todo: send message "target pet enabled"
	}


	@Subcommand("disable")
	@CommandCompletion("@players")
	@CommandPermission("pets.admin.disable")
	public void disable(@NotNull final CommandSender sender, @NotNull final OnlinePlayer target, @NotNull @Flags("active") final Pet pet)
	{
		plugin.getModule().getController().kill(target.getPlayer(), pet);

		// todo: send message "active pet disabled"
	}


	@Subcommand("give")
	@CommandCompletion("@players @pets")
	@CommandPermission("pets.admin.give")
	public void give(@NotNull final CommandSender sender, @NotNull final OnlinePlayer target, @NotNull @Flags("target") final Pet pet)
	{
		// todo: give them access to the target pet
	}


	@Subcommand("name")
	@CommandCompletion("@players")
	@CommandPermission("pets.admin.name")
	public void name(@NotNull final CommandSender sender, @NotNull final OnlinePlayer target, @NotNull @Flags("active") final Pet pet, @NotNull @Single final String name)
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