package com.mrcrayfish.crayhomes.tasks;

import java.io.ByteArrayOutputStream;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.mrcrayfish.crayhomes.CrayHomes;

public class PluginMessageTask extends BukkitRunnable
{
	private final Player player;
	private final ByteArrayOutputStream bytes;

	public PluginMessageTask(Player player, ByteArrayOutputStream bytes)
	{
		this.player = player;
		this.bytes = bytes;
	}

	public void run()
	{
		System.out.println("Sending Message");
		player.sendPluginMessage(CrayHomes.instance, CrayHomes.OUTGOING_PLUGIN_CHANNEL, this.bytes.toByteArray());
	}
}
