package com.mrcrayfish.crayhomes;

import java.io.IOException;
import java.util.HashMap;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import com.mrcrayfish.crayhomes.commands.CommandHome;
import com.mrcrayfish.crayhomes.listeners.MessageListener;
import com.mrcrayfish.crayhomes.listeners.PlayerListener;
import com.mrcrayfish.crayhomes.main.Homes;
import com.mrcrayfish.crayhomes.util.Configuration;

public final class CrayHomes extends JavaPlugin
{
	public static CrayHomes instance;

	public Configuration config;
	public static HashMap<String, Homes> owners = new HashMap<String, Homes>();
	public static Permission perms = null;

	public static String OUTGOING_PLUGIN_CHANNEL = "CrayHomesOut";
	public static String INCOMING_PLUGIN_CHANNEL = "CrayHomesIn";

	@Override
	public void onEnable()
	{
		instance = this;

		config = new Configuration(this);
		config.initConfig();
		config.loadConfig();
		config.loadHomeData();

		if (config.isBungeeCord)
		{
			getServer().getMessenger().registerOutgoingPluginChannel(this, OUTGOING_PLUGIN_CHANNEL);
			getServer().getMessenger().registerIncomingPluginChannel(this, INCOMING_PLUGIN_CHANNEL, new MessageListener(this));
		}

		getCommand("homes").setExecutor(new CommandHome(this));
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

		setupPermissions();

		try
		{
			Metrics metrics = new Metrics(this);
			metrics.start();
		}
		catch (IOException e)
		{
		}
	}

	@Override
	public void onDisable()
	{
		try
		{
			config.saveHomeData();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		owners.clear();
	}

	private boolean setupPermissions()
	{
		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
		perms = rsp.getProvider();
		return perms != null;
	}

	public Permission getPerm()
	{
		return perms;
	}
}
