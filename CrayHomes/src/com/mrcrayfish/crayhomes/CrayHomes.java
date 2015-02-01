package com.mrcrayfish.crayhomes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.mrcrayfish.crayhomes.commands.CommandHome;
import com.mrcrayfish.crayhomes.listeners.PlayerListener;
import com.mrcrayfish.crayhomes.main.Home;
import com.mrcrayfish.crayhomes.main.Homes;

public final class CrayHomes extends JavaPlugin
{
	public File homeDataFile;
	public YamlConfiguration homeConfig;
	public boolean useCompass = true;
	public int timeBeforeTeleport;
	public int teleportCost;
	public int creationCost;
	public static HashMap<UUID, Homes> owners = new HashMap<UUID, Homes>();
	public static Permission perms = null;

	public Permission getPerm()
	{
		return perms;
	}

	@Override
	public void onEnable()
	{
		if (!getConfig().contains("useCompass"))
		{
			getConfig().set("useCompass", true);
		}
		if (!getConfig().contains("timeBeforeTeleport"))
		{
			getConfig().set("timeBeforeTeleport", 1);

		}
		if (!getConfig().contains("teleportCost"))
		{
			getConfig().set("teleportCost", 2);
		}
		if (!getConfig().contains("creationCost"))
		{
			getConfig().set("creationCost", 10);
		}
		saveConfig();

		this.useCompass = getConfig().getBoolean("useCompass");
		this.timeBeforeTeleport = getConfig().getInt("timeBeforeTeleport");
		this.teleportCost = getConfig().getInt("teleportCost");
		this.creationCost = getConfig().getInt("creationCost");
		this.getCommand("homes").setExecutor(new CommandHome(this));
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

		setupConfig();
		setupPermissions();

		loadHomeData();
	}

	@Override
	public void onDisable()
	{
		try
		{
			saveHomeData();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		owners.clear();
	}

	private void setupConfig()
	{
		homeDataFile = new File(getDataFolder(), "home_data.yml");
		if (!homeDataFile.exists())
		{
			homeDataFile.getParentFile().mkdirs();
			copy(getResource("homeData.yml"), homeDataFile);
		}

		homeConfig = new YamlConfiguration();
		try
		{
			homeConfig.load(homeDataFile);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (InvalidConfigurationException e)
		{
			e.printStackTrace();
		}
	}

	private void copy(InputStream in, File file)
	{
		try
		{
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0)
			{
				out.write(buf, 0, len);
			}
			out.close();
			in.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private boolean setupPermissions()
	{
		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
		perms = rsp.getProvider();
		return perms != null;
	}

	public void saveHomeData() throws IOException
	{
		homeConfig.set("homes", null);
		for (Homes homes : owners.values())
		{
			for (Home home : homes)
			{
				homeConfig.set("homes." + home.owner.toString() + "." + home.name + ".world", home.world);
				homeConfig.set("homes." + home.owner.toString() + "." + home.name + ".icon", home.icon);
				homeConfig.set("homes." + home.owner.toString() + "." + home.name + ".x", home.x);
				homeConfig.set("homes." + home.owner.toString() + "." + home.name + ".y", home.y);
				homeConfig.set("homes." + home.owner.toString() + "." + home.name + ".z", home.z);
				homeConfig.set("homes." + home.owner.toString() + "." + home.name + ".yaw", home.yaw);
				homeConfig.set("homes." + home.owner.toString() + "." + home.name + ".pitch", home.pitch);
				homeConfig.save(homeDataFile);
			}
		}
	}

	public void loadHomeData()
	{
		Set<String> uuids = homeConfig.getConfigurationSection("homes").getKeys(false);
		for (String uuid : uuids)
		{
			owners.put(UUID.fromString(uuid), new Homes());
			Set<String> homes = homeConfig.getConfigurationSection("homes." + uuid).getKeys(false);
			for (String home : homes)
			{
				String world = homeConfig.getString("homes." + uuid + "." + home + ".world");
				String icon = homeConfig.getString("homes." + uuid + "." + home + ".icon");
				int x = homeConfig.getInt("homes." + uuid + "." + home + ".x");
				int y = homeConfig.getInt("homes." + uuid + "." + home + ".y");
				int z = homeConfig.getInt("homes." + uuid + "." + home + ".z");
				int yaw = homeConfig.getInt("homes." + uuid + "." + home + ".yaw");
				int pitch = homeConfig.getInt("homes." + uuid + "." + home + ".pitch");
				owners.get(UUID.fromString(uuid)).add(new Home(world, icon, home, UUID.fromString(uuid), x, y, z, yaw, pitch));
			}
		}
	}
}
