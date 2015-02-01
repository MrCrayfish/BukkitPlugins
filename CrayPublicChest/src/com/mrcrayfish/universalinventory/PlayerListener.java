package com.mrcrayfish.universalinventory;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {

	private UniversalInventory ui;

	public PlayerListener(UniversalInventory ui) {
		this.ui = ui;
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if ((event.getEntity() instanceof Player) && event.getCause().equals(DamageCause.SUFFOCATION)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		final Inventory inventory = event.getInventory();
		Player player = (Player) event.getWhoClicked();

		if (inventory.getName().equals("Public Chest")) {
			player.getServer().getScheduler().scheduleSyncDelayedTask(ui, new Runnable() {

				@Override
				public void run() {
					try {
						save(inventory);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}, 2);

			Player[] players = event.getViewers().toArray(new Player[1]);
			for (Player playa : players) {
				if (!playa.getName().equals(player.getName())) {
					playa.openInventory(ui.publicInventory);
				}
			}
		}
	}

	public void save(Inventory inventory) throws IOException {
		ItemStack[] contents = inventory.getContents();
		for (int i = 0; i < 27; i++) {
			ui.publicChestYAML.set("inventory.contents.slot" + i, contents[i]);
		}
		ui.publicChestYAML.save(ui.publicChest);
	}
}