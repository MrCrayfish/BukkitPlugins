package com.mrcrayfish.crayhomes.listeners;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.mrcrayfish.crayhomes.CrayHomes;
import com.mrcrayfish.crayhomes.main.Home;
import com.mrcrayfish.crayhomes.main.HomeGUI;
import com.mrcrayfish.crayhomes.main.Homes;

public class MessageListener implements PluginMessageListener
{
	private CrayHomes plugin;

	public MessageListener(CrayHomes plugin)
	{
		this.plugin = plugin;
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message)
	{
		if (!channel.equals(CrayHomes.INCOMING_PLUGIN_CHANNEL))
		{
			return;
		}

		this.plugin.getLogger().info("Got Plugin Message on " + channel + " from " + player.getName() + " messge was: " + message.toString());

		try
		{
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
			String subChannel = in.readUTF();
			String uuid = in.readUTF();
			this.plugin.getLogger().info(subChannel);

			if (subChannel.equals("TeleportHomeGUI"))
			{
				Homes homes = new Homes();
				int length = in.readInt();
				System.out.println(length);
				if (length > 0)
				{
					for (int i = 0; i < length; i++)
					{
						homes.add(new Home(in.readUTF(), in.readUTF()));
					}
				}
				Player newPlayer = Bukkit.getPlayer(UUID.fromString(uuid));
				newPlayer.openInventory(HomeGUI.createHomeInventory(plugin, newPlayer, homes));
			}

			if (subChannel.equals("DeleteHomeGUI"))
			{
				int length = in.readInt();
				Homes homes = new Homes();
				if (length > 0)
				{
					for (int i = 0; i < length; i++)
					{
						homes.add(new Home(in.readUTF(), in.readUTF()));
					}
				}
				Player newPlayer = Bukkit.getPlayer(UUID.fromString(uuid));
				newPlayer.openInventory(HomeGUI.createDeleteHomeInventory(plugin, newPlayer, homes));
			}

			if (subChannel.equalsIgnoreCase("PreTeleportToHome"))
			{
				String world = in.readUTF();
				int x = in.readInt();
				int y = in.readInt();
				int z = in.readInt();
				float yaw = in.readFloat();
				float pitch = in.readFloat();
				Home home = new Home(world, null, null, uuid, x, y, z, yaw, pitch);
				Player newPlayer = Bukkit.getPlayer(UUID.fromString(uuid));
				PlayerListener.handleTeleport(newPlayer, home);
			}
			
			if (subChannel.equalsIgnoreCase("TeleportToHome"))
			{
				
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
