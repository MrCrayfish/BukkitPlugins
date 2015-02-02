package com.mrcrayfish.crayhomes.tasks.teleport;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.mrcrayfish.crayhomes.CrayHomes;
import com.mrcrayfish.crayhomes.TeleportHandler;
import com.mrcrayfish.crayhomes.util.ParticleEffect;

public class TeleportTask implements Runnable
{
	private Player player;

	public TeleportTask(Player player)
	{
		this.player = player;
	}

	@Override
	public void run()
	{
		Location homeLoc = TeleportHandler.pendingTeleport.get(player.getUniqueId().toString());
		boolean loaded = homeLoc.getWorld().getChunkAt(homeLoc).load();
		if (loaded)
		{
			if (teleport(homeLoc, player))
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
}
