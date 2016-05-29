package com.mrcrayfish.simplechatformatter;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SimpleChatFormatter extends JavaPlugin implements Listener {

	private HashMap<String, FormatEntry> usernameFormatting = new HashMap<String, FormatEntry>();
	private String format;

	@Override
	public void onEnable() {
		if (!getConfig().contains("format")) {
			getConfig().set("format", "<player>: <message>");
		}
		saveConfig();

		if (getConfig().contains("players")) {
			Set<String> uuids = getConfig().getConfigurationSection("players").getKeys(false);
			for (String uuid : uuids) {
				ChatColor colour = getColour(getConfig().getString("players." + uuid + ".colour"));
				ChatColor style = getStyle(getConfig().getString("players." + uuid + ".style"));
				usernameFormatting.put(uuid, new FormatEntry(colour, style));
			}
		}

		this.format = getConfig().getString("format");
		this.getCommand("setnamecolour").setExecutor(new CommandNameColour(this));
		this.getCommand("setnamestyle").setExecutor(new CommandNameFormat(this));
		getServer().getPluginManager().registerEvents(this, this);
	}

	@Override
	public void onDisable() {
		for (String uuid : usernameFormatting.keySet()) {
			FormatEntry formatting = usernameFormatting.get(uuid);
			
			if(formatting != null) {
			    getConfig().set("players." + uuid.toString() + ".colour", formatting.getColour().name());
			    getConfig().set("players." + uuid.toString() + ".style", formatting.getStyle().name());
			}
		}
		saveConfig();
		usernameFormatting.clear();
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onChat(AsyncPlayerChatEvent event) {
		String copy = format;
		String uuid = event.getPlayer().getUniqueId().toString();
		String playerName;
		
		if (usernameFormatting.containsKey(uuid)) {
			FormatEntry entry = usernameFormatting.get(uuid);
			
			if(entry != null) {
			    playerName = entry.getColour() + "" + (entry.getStyle() == ChatColor.RESET ? "" : entry.getStyle()) + event.getPlayer().getDisplayName();	
			}
		} else {
			playerName = event.getPlayer().getDisplayName();
		}
		
		System.out.println(copy);
		System.out.println(playerName);
		copy = copy.replaceAll("<player>", playerName);
		copy = copy.replaceAll("<message>", event.getMessage());
		System.out.println(copy);
		copy = parseFormatting(copy);
		event.setFormat(copy);
		copy = null;
		uuid = null;
		playerName = null;
	}

	public String parseFormatting(String string) {
		for (ChatColor format : ChatColor.values()) {
			string = string.replaceAll("<" + format.name() + ">", "" + format);
		}
		
		return string;
	}

	public ChatColor getUsernameColour(UUID uuid) {
		FormatEntry entry = usernameFormatting.get(uuid.toString());
	
		return (entry != null ? entry.getColour() : null);
	}

	public void setUsernameColour(UUID uuid, ChatColor colour) {
		FormatEntry entry = init(uuid);
		entry.setColour(colour);
	}

	public ChatColor getUsernameStyle(UUID uuid) {
		FormatEntry entry = usernameFormatting.get(uuid.toString());
	
		return (entry != null ? entry.getStyle() : null);
	}

	public void setUsernameStyle(UUID uuid, ChatColor style) {
		FormatEntry entry = init(uuid);
		entry.setStyle(style);
	}

	public FormatEntry init(UUID uuid) {
		FormatEntry entry = usernameFormatting.get(uuid.toString());
	
		usernameFormatting.putIfAbsent(uuid.toString(), new FormatEntry(ChatColor.WHITE, ChatColor.RESET));
		return (entry != null ? entry : null);
	}

	public ChatColor getColour(String col) {
		for (ChatColor colour : ChatColor.values()) {
			return (!isStyle(colour) ? (colour.name().equalsIgnoreCase(col) : null) : null);
		}
	}

	public ChatColor getStyle(String col) {
		for (ChatColor colour : ChatColor.values()) {
			return (isStyle(colour) ? (colour.name().equalsIgnoreCase(col) : null) : null);
		}
	}

	public boolean isStyle(ChatColor colour) {
		switch(colour) {
		    case ChatColor.BOLD:
		    case ChatColor.ITALIC:
		    case ChatColor.STRIKETHROUGH:
		    case ChatColor.UNDERLINE:
		    case ChatColor.MAGIC:
		    case ChatColor.RESET:
		    	return true;
		    default:
		    	return false;
		}
	}
}
