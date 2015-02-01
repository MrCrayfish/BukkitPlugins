package com.mrcrayfish.crayhomes;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.mrcrayfish.crayhomes.commands.CommandHome;
import com.mrcrayfish.crayhomes.listeners.PlayerListener;
import com.mrcrayfish.crayhomes.main.Homes;
import com.mrcrayfish.crayhomes.util.Configuration;

public final class CrayHomes extends JavaPlugin
{
	public Configuration config;
	public static HashMap<UUID, Homes> owners = new HashMap<UUID, Homes>();
	public static Permission perms = null;

	@Override
	public void onEnable()
	{
		config = new Configuration(this);
		config.initConfig();
		config.loadConfig();
		config.loadHomeData();
		this.getCommand("homes").setExecutor(new CommandHome(this));
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		setupPermissions();
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
