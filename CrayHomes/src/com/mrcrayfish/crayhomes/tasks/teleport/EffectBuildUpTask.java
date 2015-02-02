package com.mrcrayfish.crayhomes.tasks.teleport;

import java.util.Random;

import org.bukkit.entity.Player;

import com.mrcrayfish.crayhomes.CrayHomes;
import com.mrcrayfish.crayhomes.util.ParticleEffect;

public class EffectBuildUpTask implements Runnable
{
	private Random rand = new Random();
	private Player player;
	private int currentTime = 1;
	
	public EffectBuildUpTask(Player player)
	{
		this.player = player;
	}
	
	@Override
	public void run()
	{
		double percent = ((double) currentTime / (20 * (double) CrayHomes.instance.config.timeBeforeTeleport));
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
}
