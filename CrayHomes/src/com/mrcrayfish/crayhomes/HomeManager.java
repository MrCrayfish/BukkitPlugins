package com.mrcrayfish.crayhomes;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.mrcrayfish.crayhomes.tasks.PluginMessageTask;

public class HomeManager
{
	public static void setHome(Player player, String home_name, String icon_name)
	{
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		Location loc = player.getLocation();
		try
		{
			out.writeUTF("CreateHome");
			out.writeUTF(player.getUniqueId().toString());
			out.writeUTF(loc.getWorld().getName());
			out.writeUTF(icon_name);
			out.writeUTF(home_name);
			out.writeInt(loc.getBlockX());
			out.writeInt(loc.getBlockY());
			out.writeInt(loc.getBlockZ());
			out.writeFloat(loc.getYaw());
			out.writeFloat(loc.getPitch());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		new PluginMessageTask(player, b).runTaskAsynchronously(CrayHomes.instance);
	}
	
	public static void deleteHome(Player player, String home_name)
	{
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		try
		{
			out.writeUTF("DeleteHome");
			out.writeUTF(player.getUniqueId().toString());
			out.writeUTF(home_name);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		new PluginMessageTask(player, b).runTaskAsynchronously(CrayHomes.instance);
	}
	
	public static void delayTeleportToHome(Player player, String home_name)
	{
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		try
		{
			out.writeUTF("TeleportHome");
			out.writeUTF(player.getUniqueId().toString());
			out.writeUTF(home_name);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		new PluginMessageTask(player, b).runTaskLaterAsynchronously(CrayHomes.instance, 20 * CrayHomes.instance.config.timeBeforeTeleport);
	}
	
	public static void openHomeGui(Player player)
	{
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		try
		{
			out.writeUTF("OpenHomeGUI");
			out.writeUTF(player.getUniqueId().toString());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		new PluginMessageTask(player, b).runTaskAsynchronously(CrayHomes.instance);
	}
	
	public static void openDeleteGui(Player player)
	{
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		try
		{
			out.writeUTF("OpenDeleteGUI");
			out.writeUTF(player.getUniqueId().toString());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		new PluginMessageTask(player, b).runTaskAsynchronously(CrayHomes.instance);
	}
}
