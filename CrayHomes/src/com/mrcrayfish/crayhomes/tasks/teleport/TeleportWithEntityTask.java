package com.mrcrayfish.crayhomes.tasks.teleport;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.mrcrayfish.crayhomes.CrayHomes;
import com.mrcrayfish.crayhomes.TeleportHandler;
import com.mrcrayfish.crayhomes.util.ParticleEffect;

public class TeleportWithEntityTask implements Runnable
{
	private Player player;
	private Entity entity;

	public TeleportWithEntityTask(Player player, Entity entity)
	{
		this.player = player;
		this.entity = entity;
	}

	@Override
	public void run()
	{
		Location homeLoc = TeleportHandler.pendingTeleport.get(player.getUniqueId().toString());
		boolean loaded = homeLoc.getWorld().getChunkAt(homeLoc).load();
		if (loaded)
		{
			boolean teleported = false;
			if (!entity.getType().name().equals("BOAT") || !entity.getType().name().equals("MINECART") && !CrayHomes.instance.config.isBungeeCord)
			{
				teleported = teleport(homeLoc, player, entity);
			}
			else
			{
				teleported = teleport(homeLoc, player);
			}
			if (teleported)
			{
				try
				{
					ParticleEffect.SMOKE_LARGE.display(0, 0, 0, 0.1F, 50, player.getLocation(), 30);
					player.getWorld().playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, 1.0F);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				player.sendMessage(ChatColor.GREEN + "Wooooosh!");
			}
			
		}
		else
		{
			player.sendMessage(ChatColor.RED + "Teleport could not be completed as chunk did not load correctly. Please try again.");
		}
		TeleportHandler.pendingTeleport.remove(player.getUniqueId().toString());
	}

	private static boolean teleport(Location location, Player player)
	{
		if (player.getLevel() < CrayHomes.instance.config.teleportCost)
			return false;
		player.setLevel(player.getLevel() - CrayHomes.instance.config.teleportCost);
		return player.teleport(location);
	}

	private static boolean teleport(Location location, Player player, Entity entity)
	{
		if (player.getLevel() < CrayHomes.instance.config.teleportCost)
			return false;
		player.setLevel(player.getLevel() - CrayHomes.instance.config.teleportCost);
		if (player.teleport(location))
		{
			entity.teleport(location);
			entity.setPassenger(player);
			return true;
		}
		return false;
	}
}
