package com.mrcrayfish.universalinventory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.mrcrayfish.universalinventory.commands.CommandPublicChest;

public class UniversalInventory extends JavaPlugin {

	public File publicChest;
	public YamlConfiguration publicChestYAML;
	public Inventory publicInventory;

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		
		publicChest = new File(getDataFolder(), "publicChest.yml");
		
		if (!publicChest.exists()) {
			publicChest.getParentFile().mkdirs();
			copy(getResource("publicChest.yml"), publicChest);
		}

		publicChestYAML = new YamlConfiguration();
		try {
			publicChestYAML.load(publicChest);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}

		try {
			publicInventory = restore();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.getCommand("pchest").setExecutor(new CommandPublicChest(this));
	}

	@Override
	public void onDisable() {
		try {
			save(publicInventory);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Inventory restore() throws IOException {
		if (new File("plugins/PublicChest/", "publicchest.yml").exists()) {
			YamlConfiguration c = publicChestYAML;
			if (c.contains("inventory.content")) {
				Inventory inventory = Bukkit.createInventory(null, 27, "Public Chest");
				for (int i = 0; i < 27; i++) {
					inventory.setItem(i, publicChestYAML.getItemStack("inventory.content.slot" + i));
				}
				return inventory;
			} else {
				return Bukkit.createInventory(null, 27, "Public Chest");
			}
		} else {
			return Bukkit.createInventory(null, 27, "Public Chest");
		}
	}

	public void save(Inventory inventory) throws IOException {
		ItemStack[] contents = inventory.getContents();
		for (int i = 0; i < 27; i++) {
			publicChestYAML.set("inventory.contents.slot" + i, contents[i]);
		}
		publicChestYAML.save(publicChest);
	}

	private void copy(InputStream in, File file) {
		try {
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
