package com.sxtanna.mc.acolytes.cmds;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import org.bukkit.command.CommandSender;

import com.sxtanna.mc.acolytes.AcolytesPlugin;
import com.sxtanna.mc.acolytes.conf.AcolytesConfig;
import com.sxtanna.mc.acolytes.conf.Resolved;
import com.sxtanna.mc.acolytes.data.Pet;
import com.sxtanna.mc.acolytes.data.attr.PetAttributes;
import com.sxtanna.mc.acolytes.lang.Lang;
import com.sxtanna.mc.acolytes.util.bukkit.Colors;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Flags;
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
	@CommandCompletion("@players @target_pets")
	@CommandPermission("pets.admin.enable")
	public void enable(@NotNull final CommandSender sender, @NotNull final OnlinePlayer target, @NotNull @Flags("target") final Pet pet)
	{
		plugin.getModule().getController().getActive(target.getPlayer()).ifPresent(active -> {
			plugin.getModule().getController().kill(target.getPlayer(), active);
		});

		plugin.getModule().getController().load(target.getPlayer(), pet);

		plugin.send(sender,
		            Lang.CMDS__ADMIN__ENABLE__SENDER,

		            "{target}",
		            target.getPlayer().getName(),

		            "{pet_name}",
		            pet.select(PetAttributes.NAME).orElse(pet.select(PetAttributes.UUID).orElse("Pet")));

		if (sender.equals(target.getPlayer()))
		{
			return;
		}

		plugin.send(target.getPlayer(),
		            Lang.CMDS__ADMIN__ENABLE__TARGET,

		            "{sender}",
		            sender.getName(),

		            "{pet_name}",
		            pet.select(PetAttributes.NAME).orElse(pet.select(PetAttributes.UUID).orElse("Pet")));
	}


	@Subcommand("disable")
	@CommandCompletion("@players")
	@CommandPermission("pets.admin.disable")
	public void disable(@NotNull final CommandSender sender, @NotNull final OnlinePlayer target, @NotNull @Flags("active") final Pet pet)
	{
		plugin.getModule().getController().kill(target.getPlayer(), pet);

		plugin.send(sender,
		            Lang.CMDS__ADMIN__DISABLE__SENDER,

		            "{target}",
		            target.getPlayer().getName(),

		            "{pet_name}",
		            pet.select(PetAttributes.NAME).orElse(pet.select(PetAttributes.UUID).orElse("Pet")));

		if (sender.equals(target.getPlayer()))
		{
			return;
		}

		plugin.send(target.getPlayer(),
		            Lang.CMDS__ADMIN__DISABLE__TARGET,

		            "{sender}",
		            sender.getName(),

		            "{pet_name}",
		            pet.select(PetAttributes.NAME).orElse(pet.select(PetAttributes.UUID).orElse("Pet")));
	}


	@Subcommand("give")
	@CommandCompletion("@players @pets")
	@CommandPermission("pets.admin.give")
	public void give(@NotNull final CommandSender sender, @NotNull final OnlinePlayer target, @NotNull @Flags("by_uuid") final Pet pet)
	{
		if (!plugin.getModule().getController().give(target.getPlayer(), pet))
		{

			plugin.send(sender,
			            Lang.CMDS__ADMIN__GIVE__FAIL,

			            "{target}",
			            target.getPlayer().getName(),

			            "{reason}",
			            "pet already given",

			            "{pet_name}",
			            pet.select(PetAttributes.NAME).orElse(pet.select(PetAttributes.UUID).orElse("Pet")));

			return;
		}

		plugin.send(sender,
		            Lang.CMDS__ADMIN__GIVE__SENDER,

		            "{target}",
		            target.getPlayer().getName(),

		            "{pet_name}",
		            pet.select(PetAttributes.NAME).orElse(pet.select(PetAttributes.UUID).orElse("Pet")));

		if (sender.equals(target.getPlayer()))
		{
			return;
		}

		plugin.send(target.getPlayer(),
		            Lang.CMDS__ADMIN__GIVE__TARGET,

		            "{sender}",
		            sender.getName(),

		            "{pet_name}",
		            pet.select(PetAttributes.NAME).orElse(pet.select(PetAttributes.UUID).orElse("Pet")));
	}

	@Subcommand("take")
	@CommandCompletion("@players @pets")
	@CommandPermission("pets.admin.take")
	public void take(@NotNull final CommandSender sender, @NotNull final OnlinePlayer target, @NotNull @Flags("by_uuid") final Pet pet)
	{
		if (!plugin.getModule().getController().take(target.getPlayer(), pet))
		{

			plugin.send(sender,
			            Lang.CMDS__ADMIN__TAKE__FAIL,

			            "{target}",
			            target.getPlayer().getName(),

			            "{reason}",
			            "pet not given",

			            "{pet_name}",
			            pet.select(PetAttributes.NAME).orElse(pet.select(PetAttributes.UUID).orElse("Pet")));

			return;
		}

		plugin.getModule().getController().kill(target.getPlayer(), pet);

		plugin.send(sender,
		            Lang.CMDS__ADMIN__TAKE__SENDER,

		            "{target}",
		            target.getPlayer().getName(),

		            "{pet_name}",
		            pet.select(PetAttributes.NAME).orElse(pet.select(PetAttributes.UUID).orElse("Pet")));

		if (sender.equals(target.getPlayer()))
		{
			return;
		}

		plugin.send(target.getPlayer(),
		            Lang.CMDS__ADMIN__TAKE__TARGET,

		            "{sender}",
		            sender.getName(),

		            "{pet_name}",
		            pet.select(PetAttributes.NAME).orElse(pet.select(PetAttributes.UUID).orElse("Pet")));
	}


	@Subcommand("name")
	@CommandCompletion("@players")
	@CommandPermission("pets.admin.name")
	public void name(@NotNull final CommandSender sender, @NotNull final OnlinePlayer target, @NotNull @Flags("active") final Pet pet, @NotNull final String name)
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

		plugin.send(sender,
		            Lang.CMDS__ADMIN__NAME_UPDATE__SENDER,

		            "{new_name}",
		            newName,

		            "{old_name}",
		            oldName != null ? oldName : "");

		if (sender.equals(target.getPlayer()))
		{
			return;
		}

		plugin.send(target.getPlayer(),
		            Lang.CMDS__ADMIN__NAME_UPDATE__TARGET,

		            "{new_name}",
		            newName,

		            "{old_name}",
		            oldName != null ? oldName : "");
	}


	@Subcommand("reload")
	@CommandPermission("pets.admin.reload")
	public void reload(@NotNull final CommandSender sender)
	{
		plugin.getConfiguration().reload();
		plugin.send(sender, Lang.CMDS__ADMIN__RELOAD);
	}

}