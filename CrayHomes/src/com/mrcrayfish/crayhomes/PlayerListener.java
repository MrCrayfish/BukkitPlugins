package com.mrcrayfish.crayhomes;

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

public class PlayerListener implements Listener
{
	private CrayHomes crayHomes;
	private ArrayList<UUID> namePendingList = new ArrayList<UUID>();
	private Map<UUID, Integer> UUIDtoTask = new HashMap<UUID, Integer>();
	private Random rand = new Random();

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
			homeCount = CrayHomes.owners.get(player.getUniqueId()).size();
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
					player.openInventory(HomeInventory.createDeleteHomeInventory(crayHomes, player));
				}
				else if (slotNum >= 0 && slotNum <= homeCount)
				{
					if (inventory.getItem(slotNum) != null)
					{
						String homeName = inventory.getItem(slotNum).getItemMeta().getDisplayName();
						Home home = CrayHomes.owners.get(player.getUniqueId()).getHome(homeName);
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

							boolean loaded = world.getChunkAt(location).load();
							if (loaded)
							{
								if (player.getLevel() >= 2)
								{
									player.sendMessage(ChatColor.YELLOW + "Teleport will commence in " + crayHomes.timeBeforeTeleport + " second(s).");
									playerWorld.playSound(player.getLocation(), Sound.ENDERDRAGON_DEATH, 1.0F, 1.0F);
									UUIDtoTask.put(player.getUniqueId(), player.getServer().getScheduler().scheduleSyncRepeatingTask(crayHomes, new Runnable()
									{

										private int currentTime = 1;

										@Override
										public void run()
										{
											double percent = ((double) currentTime / (20 * (double) crayHomes.timeBeforeTeleport));
											double amount = 20 * percent;
											try
											{
												ParticleEffect.CRIT_MAGIC.display(0, rand.nextFloat(), 0, 1, (int) amount, player.getLocation(), 30);
											}
											catch (Exception e)
											{
												e.printStackTrace();
											}
											currentTime++;
										}
									}, 0, 1));
									player.getServer().getScheduler().scheduleSyncDelayedTask(crayHomes, new Runnable()
									{

										@Override
										public void run()
										{
											try
											{
												ParticleEffect.SMOKE_LARGE.display(0, rand.nextFloat(), 0, 0.1F, 50, player.getLocation(), 30);
												playerWorld.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, 1.0F);
											}
											catch (Exception e)
											{
												e.printStackTrace();
											}
										}

									}, (20 * crayHomes.timeBeforeTeleport) - 2);
								}
								else
								{
									player.sendMessage(ChatColor.RED + "You need at least 2 experience levels to teleport.");
								}
								if (entity != null)
								{
									player.getServer().getScheduler().scheduleSyncDelayedTask(crayHomes, new Runnable()
									{
										@Override
										public void run()
										{
											boolean teleported = false;
											player.sendMessage("Sending Packet");
											if (!entity.getType().name().equals("BOAT") || !entity.getType().name().equals("MINECART"))
											{
												teleported = teleport(location, player, entity);
											}
											else
											{
												teleported = teleport(location, player);
											}
											if (teleported)
											{
												try
												{
													ParticleEffect.SMOKE_LARGE.display( 0, 0, 0, 0.1F, 50, player.getLocation(), 30);
													playerWorld.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, 1.0F);
												}
												catch (Exception e)
												{
													e.printStackTrace();
												}
												player.sendMessage(ChatColor.GREEN + "Wooooosh!");
											}
											player.getServer().getScheduler().cancelTask(UUIDtoTask.get(player.getUniqueId()));
											UUIDtoTask.remove(player.getUniqueId());
										}
									}, 20 * crayHomes.timeBeforeTeleport);
								}
								else
								{
									player.getServer().getScheduler().scheduleSyncDelayedTask(crayHomes, new Runnable()
									{
										@Override
										public void run()
										{
											if (teleport(location, player))
											{
												try
												{
													ParticleEffect.SMOKE_LARGE.display( 0, 0, 0, 0.1F, 50, player.getLocation(), 30);
													playerWorld.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, 1.0F);
												}
												catch (Exception e)
												{
													e.printStackTrace();
												}
												player.sendMessage(ChatColor.GREEN + "Wooooosh!");
											}
											player.getServer().getScheduler().cancelTask(UUIDtoTask.get(player.getUniqueId()));
											UUIDtoTask.remove(player.getUniqueId());
										}
									}, 20 * crayHomes.timeBeforeTeleport);
								}
							}
							else
							{
								player.sendMessage(ChatColor.RED + "Teleport could not be completed as chunk did not load correctly. Please try again.");
							}
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
				player.openInventory(HomeInventory.createHomeInventory(crayHomes, player));
			}
			else if (slotNum >= 0 && slotNum <= homeCount)
			{
				if (inventory.getItem(slotNum) != null)
				{
					String homeName = inventory.getItem(slotNum).getItemMeta().getDisplayName();
					Homes homeList = CrayHomes.owners.get(player.getUniqueId());
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

				player.openInventory(HomeInventory.createDeleteHomeInventory(crayHomes, player));
			}
		}
		else if (inventory.getName().equals("Select Icon"))
		{
			event.setCancelled(true);
			if (slotNum >= 0 && slotNum < 36)
			{
				String homeName = inventory.getItem(slotNum).getItemMeta().getDisplayName();

				if (player.getLevel() >= crayHomes.creationCost)
				{
					if (CrayHomes.owners.containsKey(player.getUniqueId()))
					{
						Homes homelist = CrayHomes.owners.get(player.getUniqueId());

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
							homelist.add(new Home(player.getWorld().getName(), inventory.getItem(slotNum).getType().name(), homeName, player.getUniqueId(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ(), player.getLocation().getYaw(), player.getLocation().getPitch()));
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
						CrayHomes.owners.put(player.getUniqueId(), new Homes());
						CrayHomes.owners.get(player.getUniqueId()).add(new Home(player.getWorld().getName(), inventory.getItem(slotNum).getType().name(), homeName, player.getUniqueId(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ(), player.getLocation().getYaw(), player.getLocation().getPitch()));
						player.sendMessage(ChatColor.GREEN + "Home created!");
					}
					player.setLevel(player.getLevel() - crayHomes.creationCost);
				}
				else
				{
					player.sendMessage(ChatColor.RED + "You need at least " + crayHomes.creationCost + " experience level(s) to set a teleport location.");
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
					player.openInventory(HomeInventory.createSetIconInventory(crayHomes, homeName));
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
					if (crayHomes.getPerm().has(player, "crayhomes.use") | player.isOp())
					{
						player.openInventory(HomeInventory.createHomeInventory(crayHomes, player));
					}
					else
					{
						player.sendMessage(ChatColor.RED + "You do not have permission to use CrayHomes.");
					}
				}
			}
		}
	}

	public boolean teleport(Location location, Player player)
	{
		if (player.getLevel() < crayHomes.teleportCost)
			return false;
		player.setLevel(player.getLevel() - crayHomes.teleportCost);
		return player.teleport(location);
	}

	public boolean teleport(Location location, Player player, Entity entity)
	{
		if (player.getLevel() < crayHomes.teleportCost)
			return false;
		player.setLevel(player.getLevel() - crayHomes.teleportCost);
		if (player.teleport(location))
		{
			entity.teleport(location);
			entity.setPassenger(player);
			return true;
		}
		return false;
	}
}
