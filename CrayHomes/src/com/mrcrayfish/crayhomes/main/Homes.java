package com.mrcrayfish.crayhomes.main;

import java.util.ArrayList;

public class Homes extends ArrayList<Home>
{
	private static final long serialVersionUID = 1L;

	public Home getHome(String name)
	{
		for (Home home : this)
		{
			if (home.name.equalsIgnoreCase(name))
			{
				return home;
			}
		}
		return null;
	}

	public void removeHome(String name)
	{
		for (Home home : this)
		{
			if (home.name.equalsIgnoreCase(name))
			{
				remove(home);
			}
		}
	}
}
