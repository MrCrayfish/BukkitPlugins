package com.mrcrayfish.crayhomes.main;

public class Home
{
	public String server;
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
	
	public void setServer(String string)
	{
		this.server = string;
	}
}
