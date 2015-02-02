package com.mrcrayfish.crayhomes.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import com.mrcrayfish.crayhomes.CrayHomes;
import com.mrcrayfish.crayhomes.main.Home;
import com.mrcrayfish.crayhomes.main.Homes;

public class Configuration
{
	/** Config and Data files */
	public File configFile;
	public YamlConfiguration configYaml;
	public File homeDataFile;
	public YamlConfiguration homeYaml;

	/** CrayHomes instance */
	private CrayHomes plugin;

	/** Settings */
	public boolean useCompass = true;
	public boolean isBungeeCord = false;
	public int timeBeforeTeleport;
	public int teleportCost;
	public int creationCost;

	public Configuration(CrayHomes plugin)
	{
		this.plugin = plugin;
		setupConfig();
	}

	private void setupConfig()
	{
		configFile = new File(plugin.getDataFolder(), "config.yml");
		if (!configFile.exists())
		{
			configFile.getParentFile().mkdirs();
			copy(plugin.getResource("config.yml"), configFile);
		}

		configYaml = new YamlConfiguration();
		try
		{
			configYaml.load(configFile);
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

		homeDataFile = new File(plugin.getDataFolder(), "home_data.yml");
		if (!homeDataFile.exists())
		{
			homeDataFile.getParentFile().mkdirs();
			copy(plugin.getResource("home_data.yml"), homeDataFile);
		}

		homeYaml = new YamlConfiguration();
		try
		{
			homeYaml.load(homeDataFile);
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

	public void initConfig()
	{
		if (!configYaml.contains("useCompass"))
		{
			configYaml.set("useCompass", true);
		}
		if (!configYaml.contains("timeBeforeTeleport"))
		{
			configYaml.set("timeBeforeTeleport", 5);
		}
		if (!configYaml.contains("teleportCost"))
		{
			configYaml.set("teleportCost", 2);
		}
		if (!configYaml.contains("creationCost"))
		{
			configYaml.set("creationCost", 10);
		}
		if (!configYaml.contains("isBungeeCord"))
		{
			configYaml.set("isBungeeCord", false);
		}
		try
		{
			configYaml.save(configFile);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void loadConfig()
	{
		this.useCompass = configYaml.getBoolean("useCompass");
		this.timeBeforeTeleport = configYaml.getInt("timeBeforeTeleport");
		this.teleportCost = configYaml.getInt("teleportCost");
		this.creationCost = configYaml.getInt("creationCost");
		this.isBungeeCord = configYaml.getBoolean("isBungeeCord");
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

	public void loadHomeData()
	{
		if (homeYaml.contains("homes"))
		{
			Set<String> uuids = homeYaml.getConfigurationSection("homes").getKeys(false);
			for (String uuid : uuids)
			{
				CrayHomes.owners.put(uuid, new Homes());
				Set<String> homes = homeYaml.getConfigurationSection("homes." + uuid).getKeys(false);
				for (String home : homes)
				{
					String world = homeYaml.getString("homes." + uuid + "." + home + ".world");
					String icon = homeYaml.getString("homes." + uuid + "." + home + ".icon");
					int x = homeYaml.getInt("homes." + uuid + "." + home + ".x");
					int y = homeYaml.getInt("homes." + uuid + "." + home + ".y");
					int z = homeYaml.getInt("homes." + uuid + "." + home + ".z");
					int yaw = homeYaml.getInt("homes." + uuid + "." + home + ".yaw");
					int pitch = homeYaml.getInt("homes." + uuid + "." + home + ".pitch");
					CrayHomes.owners.get(uuid).add(new Home(world, icon, home, uuid, x, y, z, yaw, pitch));
				}
			}
		}
	}

	public void saveHomeData() throws IOException
	{
		homeYaml.set("homes", null);
		for (Homes homes : CrayHomes.owners.values())
		{
			for (Home home : homes)
			{
				homeYaml.set("homes." + home.owner.toString() + "." + home.name + ".world", home.world);
				homeYaml.set("homes." + home.owner.toString() + "." + home.name + ".icon", home.icon);
				homeYaml.set("homes." + home.owner.toString() + "." + home.name + ".x", home.x);
				homeYaml.set("homes." + home.owner.toString() + "." + home.name + ".y", home.y);
				homeYaml.set("homes." + home.owner.toString() + "." + home.name + ".z", home.z);
				homeYaml.set("homes." + home.owner.toString() + "." + home.name + ".yaw", home.yaw);
				homeYaml.set("homes." + home.owner.toString() + "." + home.name + ".pitch", home.pitch);
				homeYaml.save(homeDataFile);
			}
		}
	}
}
