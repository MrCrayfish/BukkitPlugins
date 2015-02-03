package com.mrcrayfish.crayhomes.tasks.teleport;

import org.bukkit.Server;

public class CancelTaskTask implements Runnable
{
	private Server server;
	private int taskId;

	public CancelTaskTask(Server server, int taskId)
	{
		this.server = server;
		this.taskId = taskId;
	}

	@Override
	public void run()
	{
		server.getScheduler().cancelTask(taskId);
	}

}
