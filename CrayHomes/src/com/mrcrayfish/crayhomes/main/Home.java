package com.mrcrayfish.crayhomes.main;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Home
{
	public String world;
	public String name;
	public String icon;
	public String owner;
	public int x, y, z;
	public float yaw, pitch;

	public Home(String name, String icon)
	{
		this.name = name;
		this.icon = icon;
	}
	
	public Home(String world, String icon, String name, String owner, int x, int y, int z, float yaw, float pitch)
	{
		this.world = world;
		this.icon = icon;
		this.name = name;
		this.owner = owner;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}
	
	public Location getLocation()
	{
		return new Location(Bukkit.getWorld(world), x + 0.5, y, z + 0.5, yaw, pitch);
	}
}
