package com.mrcrayfish.simplechatformatter;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandNameColour implements CommandExecutor {

	private SimpleChatFormatter plugin = null;

	public CommandNameColour(SimpleChatFormatter plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (args.length == 1) {
				ChatColor colour = plugin.getColour(args[0]);
				if (colour != null) {
					plugin.setUsernameColour(player.getUniqueId(), colour);
					player.sendMessage(ChatColor.GREEN + "Successfully set your name colour to '" + colour + colour.name() + ChatColor.GREEN + "'");
				} else {
					player.sendMessage(ChatColor.RED + "Unknown colour '" + args[0] + "'");
				}
			} else {
				player.sendMessage(ChatColor.BOLD + "Avaliable Colours:");
				player.sendMessage(printColours());
			}
		}
		return true;
	}

	public String printColours() {
		String colours = "";
		for (ChatColor colour : ChatColor.values()) {
			if (!plugin.isStyle(colour)) {
				colours += colour + colour.name() + ChatColor.RESET;
				colours += ", ";
			}
		}
		return colours.substring(0, colours.length() - 2);
	}
}
