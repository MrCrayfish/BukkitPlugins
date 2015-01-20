package com.mrcrayfish.simplechatformatter;

import org.bukkit.ChatColor;

public class FormatEntry {
	public ChatColor colour;
	public ChatColor style;
	
	public FormatEntry(ChatColor colour, ChatColor style) {
		this.colour = colour;
		this.style = style;
	}

	public ChatColor getColour() {
		return colour;
	}

	public void setColour(ChatColor colour) {
		this.colour = colour;
	}

	public ChatColor getStyle() {
		return style;
	}

	public void setStyle(ChatColor style) {
		this.style = style;
	}
}
