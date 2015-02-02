package com.mrcrayfish.crayhomes;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.mrcrayfish.crayhomes.tasks.teleport.EffectBuildUpTask;
import com.mrcrayfish.crayhomes.tasks.teleport.EffectWooshTask;

public class TeleportHandler
{
	private static Random rand = new Random();
	public static HashMap<String, Location> pendingTeleport = new HashMap<String, Location>();

	public static void startTeleport(final Player player, Location loc)
	{
		if (player.getLevel() >= 2)
		{
			player.sendMessage(ChatColor.YELLOW + "Teleport will commence in " + CrayHomes.instance.config.timeBeforeTeleport + " second(s).");
			player.getWorld().playSound(player.getLocation(), Sound.ENDERDRAGON_DEATH, 1.0F, 1.0F);
			player.getServer().getScheduler().scheduleSyncRepeatingTask(CrayHomes.instance, new EffectBuildUpTask(player), 0, 1);
			player.getServer().getScheduler().scheduleSyncDelayedTask(CrayHomes.instance, new EffectWooshTask(player, rand), (20 * CrayHomes.instance.config.timeBeforeTeleport) - 2);
			pendingTeleport.put(player.getUniqueId().toString(), loc);
		}
		else
		{
			player.sendMessage(ChatColor.RED + "You need at least 2 experience levels to teleport.");
		}
	}
}
