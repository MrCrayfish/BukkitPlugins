package com.mrcrayfish.crayhomes;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.mrcrayfish.crayhomes.tasks.teleport.CancelTaskTask;
import com.mrcrayfish.crayhomes.tasks.teleport.EffectBuildUpTask;
import com.mrcrayfish.crayhomes.tasks.teleport.EffectWooshTask;
import com.mrcrayfish.crayhomes.tasks.teleport.TeleportTask;
import com.mrcrayfish.crayhomes.tasks.teleport.TeleportWithEntityTask;

public class TeleportHandler
{
	private static Random rand = new Random();
	public static HashMap<String, Location> pendingTeleport = new HashMap<String, Location>();

	public static void displayEffects(final Player player)
	{
		player.getWorld().playSound(player.getLocation(), Sound.ENDERDRAGON_DEATH, 1.0F, 1.0F);
		int taskId = player.getServer().getScheduler().scheduleSyncRepeatingTask(CrayHomes.instance, new EffectBuildUpTask(player), 0, 1);
		player.getServer().getScheduler().scheduleSyncDelayedTask(CrayHomes.instance, new CancelTaskTask(player.getServer(), taskId), (20 * CrayHomes.instance.config.timeBeforeTeleport) - 2);
		player.getServer().getScheduler().scheduleSyncDelayedTask(CrayHomes.instance, new EffectWooshTask(player, rand), (20 * CrayHomes.instance.config.timeBeforeTeleport));
	}

	public static void commenceTeleport(final Player player)
	{
		commenceTeleport(player, true, CrayHomes.instance.config.timeBeforeTeleport);
	}

	public static void commenceTeleport(final Player player, boolean displayEffects, int delayTime)
	{
		player.closeInventory();

		if (displayEffects)
			displayEffects(player);

		final Entity entity = player.getVehicle();
		if (entity != null)
		{
			entity.eject();
		}

		if (entity != null)
		{
			player.getServer().getScheduler().scheduleSyncDelayedTask(CrayHomes.instance, new TeleportWithEntityTask(player, entity), 20 * delayTime);
		}
		else
		{
			player.getServer().getScheduler().scheduleSyncDelayedTask(CrayHomes.instance, new TeleportTask(player), 20 * delayTime);
		}
	}

	public static boolean hasEnoughXP(Player player)
	{
		if (player.getLevel() >= CrayHomes.instance.config.teleportCost)
		{
			return true;
		}
		return false;
	}
}
