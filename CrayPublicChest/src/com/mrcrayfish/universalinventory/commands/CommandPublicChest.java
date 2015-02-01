package com.mrcrayfish.universalinventory.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mrcrayfish.universalinventory.UniversalInventory;

public class CommandPublicChest implements CommandExecutor {

	private UniversalInventory ui;

	public CommandPublicChest(UniversalInventory ui) {
		this.ui = ui;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("You need to be a player to execute this command.");
			return true;
		}

		Player player = (Player) sender;
		player.openInventory(ui.publicInventory);
		return true;
	}
}
