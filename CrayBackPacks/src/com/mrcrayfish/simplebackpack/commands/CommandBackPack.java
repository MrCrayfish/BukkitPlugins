package com.mrcrayfish.simplebackpack.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mrcrayfish.simplebackpack.BackPack;

public class CommandBackPack implements CommandExecutor
{
	private BackPack backpack;

	public CommandBackPack(BackPack backpack)
	{
		this.backpack = backpack;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (!(sender instanceof Player))
		{
			sender.sendMessage("You need to be a player to execute this command.");
			return true;
		}

		Player player = (Player) sender;
		player.openInventory(backpack.getInventory(((Player) sender).getUniqueId()));
		return true;
	}
}
