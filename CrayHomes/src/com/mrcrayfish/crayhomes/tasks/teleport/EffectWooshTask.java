package com.mrcrayfish.crayhomes.tasks.teleport;

import java.util.Random;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.mrcrayfish.crayhomes.util.ParticleEffect;

public class EffectWooshTask implements Runnable
{
	private Player player;
	private Random rand;
	
	public EffectWooshTask(Player player, Random rand)
	{
		this.player = player;
		this.rand = rand;
	}
	
	@Override
	public void run()
	{
		try
		{
			ParticleEffect.SMOKE_LARGE.display(0, rand.nextFloat(), 0, 0.1F, 50, player.getLocation(), 30);
			player.getWorld().playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, 1.0F);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
