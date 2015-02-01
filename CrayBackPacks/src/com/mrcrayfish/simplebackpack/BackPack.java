package com.mrcrayfish.simplebackpack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.mrcrayfish.simplebackpack.commands.CommandBackPack;

public class BackPack extends JavaPlugin
{
	private File backPacks;
	private YamlConfiguration backPacksYAML;

	private HashMap<String, Inventory> backpacks;
	private int rows = 1;

	@Override
	public void onEnable()
	{
		backpacks = new HashMap<String, Inventory>();

		if (!getConfig().contains("rows"))
		{
			getConfig().set("rows", 2);
		}
		saveConfig();
		rows = getConfig().getInt("rows");
		if (rows > 6)
			rows = 6;
		if (rows < 1)
			rows = 1;

		backPacks = new File(getDataFolder(), "backpacks.yml");

		if (!backPacks.exists())
		{
			backPacks.getParentFile().mkdirs();
			copy(getResource("backpacks.yml"), backPacks);
		}

		backPacksYAML = new YamlConfiguration();

		try
		{
			backPacksYAML.load(backPacks);
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

		loadBackPacks();
		
		this.getCommand("backpack").setExecutor(new CommandBackPack(this));
	}

	@Override
	public void onDisable()
	{
		saveBackPacks();
		backpacks.clear();
	}

	public void saveBackPacks()
	{
		for (String uuid : backpacks.keySet())
		{
			ItemStack[] contents = backpacks.get(uuid).getContents();
			for (int i = 0; i < backpacks.get(uuid).getSize(); i++)
			{
				backPacksYAML.set("backpacks." + uuid + ".slot." + i, contents[i]);
			}
			try
			{
				backPacksYAML.save(backPacks);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void loadBackPacks()
	{
		if (new File("plugins/SimpleBackPacks/", "backpacks.yml").exists())
		{
			if (backPacksYAML.contains("backpacks"))
			{
				Set<String> uuids = backPacksYAML.getConfigurationSection("backpacks").getKeys(false);
				for (String uuid : uuids)
				{
					Inventory inventory = Bukkit.createInventory(null, rows * 9, ChatColor.GREEN + "Backpack");
					Set<String> slots = backPacksYAML.getConfigurationSection("backpacks." + uuid + ".slot").getKeys(false);
					for(String slot : slots)
					{
						if(Integer.parseInt(slot) < (rows * 9))
						{
							inventory.setItem(Integer.parseInt(slot), backPacksYAML.getItemStack("backpacks." + uuid + ".slot." + slot));
						}	
					}
					backpacks.put(uuid, inventory);
				}
			}
		}
	}

	public Inventory getInventory(UUID uuid)
	{
		if (!backpacks.containsKey(uuid.toString()))
		{
			backpacks.put(uuid.toString(), Bukkit.createInventory(null, rows * 9, ChatColor.GREEN + "Backpack"));
		}
		return backpacks.get(uuid.toString());
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
}
