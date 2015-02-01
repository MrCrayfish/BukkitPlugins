package com.mrcrayfish.crayhomes.main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.mrcrayfish.crayhomes.CrayHomes;

public class HomeGUI
{
	private static Material[] blocks = { Material.GRASS, Material.DIRT, Material.STONE, Material.COBBLESTONE, Material.LOG, Material.WOOD, Material.GLASS, Material.BRICK, Material.BOOKSHELF, Material.OBSIDIAN, Material.NETHER_BRICK, Material.NETHERRACK, Material.COAL_BLOCK, Material.IRON_BLOCK, Material.GOLD_BLOCK, Material.DIAMOND_BLOCK, Material.EMERALD_BLOCK, Material.HAY_BLOCK, Material.COAL_ORE, Material.IRON_ORE, Material.GOLD_ORE, Material.LAPIS_ORE, Material.DIAMOND_ORE,
			Material.LEAVES, Material.WORKBENCH, Material.FURNACE, Material.CHEST, Material.REDSTONE_LAMP_OFF, Material.TNT, Material.PISTON_BASE, Material.JUKEBOX, Material.DISPENSER, Material.REDSTONE_BLOCK, Material.PACKED_ICE, Material.PUMPKIN, Material.MELON_BLOCK };

	public static Inventory createHomeInventory(CrayHomes crayHomes, Player player)
	{
		String newPlayerName = player.getName();
		if (newPlayerName.length() >= 11)
		{
			newPlayerName = newPlayerName.substring(0, 11);
		}
		Inventory inventory = Bukkit.getServer().createInventory(null, 9, newPlayerName + "'s Homes " + ChatColor.GREEN + "[Teleport]");
		int count = 0;

		if (CrayHomes.owners.containsKey(player.getUniqueId()))
		{
			Homes homelist = CrayHomes.owners.get(player.getUniqueId());
			int rows = 1;
			if (homelist.size() > 7)
			{
				for (int i = 0; i < homelist.size(); i++)
				{
					if ((i + 2) % 9 == 0)
					{
						rows++;
					}
				}
			}

			int size = rows * 9;
			if (size > 54)
			{
				size = 54;
			}
			inventory = Bukkit.getServer().createInventory(null, size, newPlayerName + "'s Homes " + ChatColor.GREEN + "[Teleport]");

			for (Home home : homelist)
			{
				ItemStack homeItem = new ItemStack(Material.getMaterial(home.icon));
				ItemMeta homeItemMeta = homeItem.getItemMeta();
				homeItemMeta.setDisplayName(home.name);
				homeItem.setItemMeta(homeItemMeta);
				inventory.setItem(count, homeItem);
				count++;
			}

			ItemStack setHomeItem = new ItemStack(Material.WOOL, 1, (byte) 5);
			ItemMeta setHomeItemMeta = setHomeItem.getItemMeta();
			setHomeItemMeta.setDisplayName("Set Home");
			setHomeItem.setItemMeta(setHomeItemMeta);
			inventory.setItem(size - 2, setHomeItem);

			ItemStack delHomeItem = new ItemStack(Material.WOOL, 1, (byte) 14);
			ItemMeta delHomeItemMeta = delHomeItem.getItemMeta();
			delHomeItemMeta.setDisplayName("Delete Home");
			delHomeItem.setItemMeta(delHomeItemMeta);
			inventory.setItem(size - 1, delHomeItem);
		}
		else
		{
			ItemStack setHomeItem = new ItemStack(Material.WOOL, 1, (byte) 5);
			ItemMeta setHomeItemMeta = setHomeItem.getItemMeta();
			setHomeItemMeta.setDisplayName("Set Home");
			setHomeItem.setItemMeta(setHomeItemMeta);
			inventory.setItem(7, setHomeItem);

			ItemStack delHomeItem = new ItemStack(Material.WOOL, 1, (byte) 14);
			ItemMeta delHomeItemMeta = delHomeItem.getItemMeta();
			delHomeItemMeta.setDisplayName("Delete Home");
			delHomeItem.setItemMeta(delHomeItemMeta);
			inventory.setItem(8, delHomeItem);
		}
		return inventory;
	}

	public static Inventory createDeleteHomeInventory(CrayHomes crayHomes, Player player)
	{
		String newPlayerName = player.getName();
		if (newPlayerName.length() >= 11)
		{
			newPlayerName = newPlayerName.substring(0, 11);
		}
		Inventory inventory = Bukkit.getServer().createInventory(null, 9, newPlayerName + "'s Homes " + ChatColor.RED + "[Delete]");
		int count = 0;

		if (CrayHomes.owners.containsKey(player.getUniqueId()))
		{
			Homes homelist = CrayHomes.owners.get(player.getUniqueId());
			int rows = 1;
			if (homelist.size() > 7)
			{
				for (int i = 0; i < homelist.size(); i++)
				{
					if ((i + 1) % 9 == 0)
					{
						rows++;
					}
				}
			}

			int size = rows * 9;
			if (size > 54)
			{
				size = 54;
			}
			inventory = Bukkit.getServer().createInventory(null, size, newPlayerName + "'s Homes " + ChatColor.RED + "[Delete]");

			for (Home home : homelist)
			{
				ItemStack homeItem = new ItemStack(Material.getMaterial(home.icon));
				ItemMeta homeItemMeta = homeItem.getItemMeta();
				homeItemMeta.setDisplayName(home.name);
				homeItem.setItemMeta(homeItemMeta);
				inventory.setItem(count, homeItem);
				count++;
			}

			ItemStack delHomeItem = new ItemStack(Material.WOOL, 1, (byte) 14);
			ItemMeta delHomeItemMeta = delHomeItem.getItemMeta();
			delHomeItemMeta.setDisplayName("Back");
			delHomeItem.setItemMeta(delHomeItemMeta);
			inventory.setItem(size - 1, delHomeItem);
		}
		else
		{
			ItemStack delHomeItem = new ItemStack(Material.WOOL, 1, (byte) 14);
			ItemMeta delHomeItemMeta = delHomeItem.getItemMeta();
			delHomeItemMeta.setDisplayName("Back");
			delHomeItem.setItemMeta(delHomeItemMeta);
			inventory.setItem(8, delHomeItem);
		}
		return inventory;
	}

	public static Inventory createSetIconInventory(CrayHomes crayHomes, String homeName)
	{
		Inventory inventory = Bukkit.getServer().createInventory(null, 36, "Select Icon");

		for (int i = 0; i < blocks.length; i++)
		{
			ItemStack iconItem = new ItemStack(blocks[i]);
			ItemMeta iconItemMeta = iconItem.getItemMeta();
			iconItemMeta.setDisplayName(homeName);
			iconItem.setItemMeta(iconItemMeta);
			inventory.setItem(i, iconItem);
		}

		return inventory;
	}
}
