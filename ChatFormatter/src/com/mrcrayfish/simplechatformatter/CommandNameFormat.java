package com.mrcrayfish.simplechatformatter;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandNameFormat implements CommandExecutor {

	private SimpleChatFormatter plugin = null;

	public CommandNameFormat(SimpleChatFormatter plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (args.length == 1) {
				ChatColor colour = plugin.getStyle(args[0]);
				if (colour != null) {
					plugin.setUsernameStyle(player.getUniqueId(), colour);
					player.sendMessage(ChatColor.GREEN + "Successfully set your name style to '" + colour + colour.name() + ChatColor.RESET + ChatColor.GREEN + "'");
				} else {
					player.sendMessage(ChatColor.RED + "Unknown style '" + args[0] + "'");
					player.sendMessage(ChatColor.BOLD + "Avaliable Styles:");
					player.sendMessage(printStyles());
				}
			} else {
				player.sendMessage(ChatColor.BOLD + "Avaliable Styles:");
				player.sendMessage(printStyles());
			}
		}
		return true;
	}

	public String printStyles() {
		String colours = "";
		for (ChatColor colour : ChatColor.values()) {
			if (isStyle(colour)) {
				colours += colour + colour.name() + ChatColor.RESET;
				if(colour == ChatColor.MAGIC)
				{
					colours += "(MAGIC)";
				}
				colours += ", ";
			}
		}
		return colours.substring(0, colours.length() - 2);
	}

	public boolean isStyle(ChatColor colour) {
		if (colour == ChatColor.BOLD)
			return true;
		if (colour == ChatColor.ITALIC)
			return true;
		if (colour == ChatColor.STRIKETHROUGH)
			return true;
		if (colour == ChatColor.UNDERLINE)
			return true;
		if (colour == ChatColor.MAGIC)
			return true;
		if (colour == ChatColor.RESET)
			return true;
		return false;

	}
}
