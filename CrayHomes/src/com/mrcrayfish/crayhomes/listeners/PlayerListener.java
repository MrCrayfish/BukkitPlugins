package com.mrcrayfish.crayhomes.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import com.mrcrayfish.crayhomes.CrayHomes;
import com.mrcrayfish.crayhomes.HomeManager;
import com.mrcrayfish.crayhomes.TeleportHandler;
import com.mrcrayfish.crayhomes.main.Home;
import com.mrcrayfish.crayhomes.main.HomeGUI;
import com.mrcrayfish.crayhomes.main.Homes;
import com.mrcrayfish.crayhomes.tasks.teleport.TeleportTask;
import com.mrcrayfish.crayhomes.tasks.teleport.TeleportWithEntityTask;
import com.mrcrayfish.crayhomes.util.ParticleEffect;

public class PlayerListener implements Listener
{
	private CrayHomes crayHomes;
	private ArrayList<UUID> namePendingList = new ArrayList<UUID>();
	private static Map<UUID, Integer> UUIDtoTask = new HashMap<UUID, Integer>();
	private static Random rand = new Random();

	private synchronized ArrayList<UUID> getPending()
	{
		return namePendingList;
	}

	public PlayerListener(CrayHomes crayHomes)
	{
		this.crayHomes = crayHomes;
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event)
	{
		if ((event.getEntity() instanceof Player) && event.getCause().equals(DamageCause.SUFFOCATION))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onInventoryClick(InventoryClickEvent event)
	{
		final Player player = (Player) event.getWhoClicked();
		Inventory inventory = event.getInventory();
		int slotNum = event.getRawSlot();

		String newPlayerName = player.getName();
		if (newPlayerName.length() >= 11)
		{
			newPlayerName = newPlayerName.substring(0, 11);
		}

		int homeCount = 0;
		if (CrayHomes.owners.containsKey(player.getUniqueId()))
		{
			homeCount = CrayHomes.owners.get(player.getUniqueId().toString()).size();
		}

		if (inventory.getName().equals(newPlayerName + "'s Homes " + ChatColor.GREEN + "[Teleport]"))
		{
			event.setCancelled(true);
			if (slotNum < inventory.getSize())
			{
				if (slotNum == (inventory.getSize() - 2))
				{
					if (!getPending().contains(player.getUniqueId()))
					{
						getPending().add(player.getUniqueId());
					}
					player.sendMessage(ChatColor.GREEN + "Enter a name for your home.");
					player.closeInventory();
				}
				else if (slotNum == (inventory.getSize() - 1))
				{
					if (crayHomes.config.isBungeeCord)
					{
						HomeManager.openDeleteGui(player);
					}
					else
					{
						player.openInventory(HomeGUI.createDeleteHomeInventory(crayHomes, player));
					}
				}
				else if (slotNum >= 0 && slotNum <= homeCount)
				{
					if (inventory.getItem(slotNum) != null)
					{
						final String homeName = inventory.getItem(slotNum).getItemMeta().getDisplayName();
						if (crayHomes.config.isBungeeCord)
						{
							HomeManager.teleportPlayerToHome(player, homeName);
						}
						else
						{
							Home home = CrayHomes.owners.get(player.getUniqueId().toString()).getHome(homeName);
							handleTeleport(player, home);
						}
					}
				}
			}
		}
		else if (inventory.getName().equals(newPlayerName + "'s Homes " + ChatColor.RED + "[Delete]"))
		{
			event.setCancelled(true);
			if (slotNum == (inventory.getSize() - 1))
			{
				if (crayHomes.config.isBungeeCord)
				{
					HomeManager.openHomeGui(player);
				}
				else
				{
					player.openInventory(HomeGUI.createHomeInventory(crayHomes, player));
				}
			}
			else if (slotNum >= 0 && slotNum <= homeCount)
			{
				if (inventory.getItem(slotNum) != null)
				{
					String homeName = inventory.getItem(slotNum).getItemMeta().getDisplayName();
					if (crayHomes.config.isBungeeCord)
					{
						HomeManager.deleteHome(player, homeName);
					}
					else
					{
						Homes homeList = CrayHomes.owners.get(player.getUniqueId().toString());
						if (homeList != null)
						{
							Home home = homeList.getHome(homeName);
							if (home != null)
							{
								homeList.remove(home);
								player.sendMessage(ChatColor.GREEN + "Home deleted!");
							}
						}
					}
				}

				if (crayHomes.config.isBungeeCord)
				{
					HomeManager.openDeleteGui(player);
				}
				else
				{
					player.openInventory(HomeGUI.createDeleteHomeInventory(crayHomes, player));
				}
			}
		}
		else if (inventory.getName().equals("Select Icon"))
		{
			event.setCancelled(true);
			if (slotNum >= 0 && slotNum < 36)
			{
				String homeName = inventory.getItem(slotNum).getItemMeta().getDisplayName();

				if (player.getLevel() >= crayHomes.config.creationCost)
				{
					if (CrayHomes.owners.containsKey(player.getUniqueId()))
					{
						Homes homelist = CrayHomes.owners.get(player.getUniqueId().toString());

						int homeLimit = 1;

						for (int i = 1; i <= 52; i++)
						{
							if (crayHomes.getPerm().has(player, "crayhomes.limit." + i))
							{
								homeLimit = i;
							}
						}

						if (homelist.size() < homeLimit)
						{
							if (crayHomes.config.isBungeeCord)
							{
								HomeManager.setHome(player, homeName, inventory.getItem(slotNum).getType().name());
							}
							else
							{
								Home home = new Home(player.getWorld().getName(), inventory.getItem(slotNum).getType().name(), homeName, player.getUniqueId().toString(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
								homelist.add(home);
							}
							player.sendMessage(ChatColor.GREEN + "Home created!");
							player.closeInventory();
						}
						else
						{
							player.sendMessage(ChatColor.RED + "You have reached your allowed maximum of houses!");
						}
					}
					else
					{
						if (crayHomes.config.isBungeeCord)
						{
							HomeManager.setHome(player, homeName, inventory.getItem(slotNum).getType().name());
						}
						else
						{
							CrayHomes.owners.put(player.getUniqueId().toString(), new Homes());
							Home home = new Home(player.getWorld().getName(), inventory.getItem(slotNum).getType().name(), homeName, player.getUniqueId().toString(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
							CrayHomes.owners.get(player.getUniqueId().toString()).add(home);
						}
						player.sendMessage(ChatColor.GREEN + "Home created!");
						player.closeInventory();
					}
					player.setLevel(player.getLevel() - crayHomes.config.creationCost);
				}
				else
				{
					player.sendMessage(ChatColor.RED + "You need at least " + crayHomes.config.creationCost + " experience level(s) to set a teleport location.");
				}
				player.closeInventory();
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onChat(AsyncPlayerChatEvent event)
	{
		Player player = event.getPlayer();
		if (getPending().contains(player.getUniqueId()))
		{
			String homeName = event.getMessage().replaceAll("[.:]", "");
			event.setCancelled(true);
			for (int i = 0; i < getPending().size(); i++)
			{
				if (getPending().get(i).equals(player.getUniqueId()))
				{
					player.openInventory(HomeGUI.createSetIconInventory(crayHomes, homeName));
					player.sendMessage(ChatColor.GREEN + "Name set. Now select an icon.");
					getPending().remove(i);
					break;
				}
			}
		}
	}

	@EventHandler
	public void onPlayerExit(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();
		if (getPending().contains(player.getUniqueId()))
		{
			for (int i = 0; i < getPending().size(); i++)
			{
				if (getPending().get(i).equals(player.getName()))
				{
					getPending().remove(i);
					break;
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerUse(PlayerInteractEvent event)
	{
		if (crayHomes.getConfig().getBoolean("useCompass"))
		{
			Player player = event.getPlayer();
			if (player.getItemInHand().getType() == Material.COMPASS)
			{
				event.setCancelled(true);
				if (event.getAction() == Action.RIGHT_CLICK_AIR | event.getAction() == Action.RIGHT_CLICK_BLOCK)
				{
					if (crayHomes.config.isBungeeCord)
					{
						HomeManager.openHomeGui(player);
					}
					else
					{
						if (crayHomes.getPerm().has(player, "crayhomes.use") | player.isOp())
						{
							player.openInventory(HomeGUI.createHomeInventory(crayHomes, player));
						}
						else
						{
							player.sendMessage(ChatColor.RED + "You do not have permission to use CrayHomes.");
						}
					}
				}
			}
		}
	}

	public static void handleTeleport(final Player player, Home home)
	{
		if (home != null)
		{
			player.closeInventory();

			double x = home.x + 0.5D;
			double y = home.y;
			double z = home.z + 0.5D;

			float pitch = home.pitch;
			float yaw = home.yaw;

			String worldName = home.world;
			World world = Bukkit.getWorld(worldName);

			final Location location = new Location(world, x, y, z, yaw, pitch);
			final World playerWorld = Bukkit.getWorld(player.getWorld().getName());

			final Entity entity = player.getVehicle();
			if (entity != null)
			{
				entity.eject();
			}

			TeleportHandler.startTeleport(player, location);
			if (entity != null)
			{
				if (CrayHomes.instance.config.isBungeeCord)
				{
					HomeManager.teleportPlayerToHome(player, "");
				}
				else
				{
					player.getServer().getScheduler().scheduleSyncDelayedTask(CrayHomes.instance, new TeleportWithEntityTask(player, entity), 20 * CrayHomes.instance.config.timeBeforeTeleport);
				}
			}
			else
			{
				if (CrayHomes.instance.config.isBungeeCord)
				{

				}
				else
				{
					player.getServer().getScheduler().scheduleSyncDelayedTask(CrayHomes.instance, new TeleportTask(player), 20 * CrayHomes.instance.config.timeBeforeTeleport);
				}
			}
		}

	}

	private static boolean teleport(Location location, Player player, Entity entity)
	{
		if (player.getLevel() < CrayHomes.instance.config.teleportCost)
			return false;
		player.setLevel(player.getLevel() - CrayHomes.instance.config.teleportCost);
		if (player.teleport(location))
		{
			entity.teleport(location);
			entity.setPassenger(player);
			return true;
		}
		return false;
	}
}
