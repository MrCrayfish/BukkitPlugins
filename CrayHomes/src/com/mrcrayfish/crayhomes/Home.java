package com.mrcrayfish.crayhomes;

import java.util.UUID;

public class Home
{
	public String world;
	public String name;
	public String icon;
	public UUID owner;
	public int x, y, z;
	public float yaw, pitch;

	public Home(String world, String icon, String name, UUID owner, int x, int y, int z, float yaw, float pitch)
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

}
