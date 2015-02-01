package com.mrcrayfish.sounds;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class CraySounds extends JavaPlugin implements Listener {

	private Map<String, Boolean> enabledList = new HashMap<String, Boolean>();

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		getCommand("sounds").setExecutor(this);
		loadConfig();
	}

	@Override
	public void onDisable() {
		saveConfig();
		enabledList.clear();
	}

	public void loadConfig() {
		Set<String> uuids = getConfig().getConfigurationSection("players").getKeys(false);
		for (String uuid : uuids) {
			boolean flag = getConfig().getBoolean("players." + uuid);
			enabledList.put(uuid, flag);
		}
	}

	public void saveConfig() {
		for (String uuid : enabledList.keySet()) {
			boolean flag = enabledList.get(uuid);
			getConfig().set("players." + uuid, flag);
		}
		saveConfig();
	}
	
	@EventHandler
	public void shiftClick(InventoryClickEvent e) {
		if (e.isShiftClick()) {
			if (e.isLeftClick()) {
				Player player = (Player) e.getWhoClicked();
				String uuid = player.getUniqueId().toString();
				init(uuid);

				if (enabledList.get(uuid).booleanValue()) {
					World world = player.getWorld();
					ItemStack item = player.getInventory().getItem(e.getSlot());
					if (item != null) {
						world.playSound(player.getEyeLocation(), Sound.ITEM_PICKUP, 1, 1.5F);
					}
				}
			}
		}
	}

	@EventHandler
	public void switchedItem(PlayerItemHeldEvent e) {
		Player player = e.getPlayer();
		String uuid = player.getUniqueId().toString();
		init(uuid);

		if (enabledList.get(uuid).booleanValue()) {
			World world = player.getWorld();
			ItemStack item = player.getInventory().getItem(e.getNewSlot());
			if (item != null) {
				world.playSound(player.getEyeLocation(), Sound.IRONGOLEM_THROW, 1, 1.5F);
			}
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}

		if (args.length != 1) {
			sender.sendMessage(ChatColor.RED + "/sounds <true|false|on|off|toggle>");
			return true;
		}

		Player player = (Player) sender;
		String uuid = player.getUniqueId().toString();
		init(uuid);

		if (args[0].equalsIgnoreCase("true") | args[0].equalsIgnoreCase("on")) {
			enabledList.put(uuid, Boolean.valueOf(true));
		}
		if (args[0].equalsIgnoreCase("false") | args[0].equalsIgnoreCase("false")) {
			enabledList.put(uuid, Boolean.valueOf(false));
		}
		if (args[0].equalsIgnoreCase("toggle")) {
			boolean value = enabledList.get(uuid).booleanValue();
			enabledList.put(uuid, Boolean.valueOf(value ^= true));
		}
		player.sendMessage("Sounds are now " + (enabledList.get(uuid).booleanValue() ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"));
		return true;
	}

	public void init(String uuid) {
		if (!enabledList.containsKey(uuid)) {
			enabledList.put(uuid, Boolean.valueOf(true));
		}
	}
}
