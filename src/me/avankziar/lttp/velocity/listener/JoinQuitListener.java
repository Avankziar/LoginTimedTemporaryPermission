package me.avankziar.lttp.velocity.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;

import me.avankziar.lttp.velocity.LTTP;

public class JoinQuitListener 
{
	private LTTP plugin;
	
	public JoinQuitListener(LTTP plugin)
	{
		this.plugin = plugin;
	}
	
	@Subscribe(order = PostOrder.EARLY)
	public void onPlayerJoin(PostLoginEvent event)
	{
		if(plugin.getLuckPermsHandler() != null)
		{
			plugin.getLuckPermsHandler().join(event.getPlayer(), event.getPlayer().getUniqueId());
		}
	}
	
	@Subscribe
	public void onPlayerQuit(DisconnectEvent event)
	{
		if(plugin.getLuckPermsHandler() != null)
		{
			plugin.getLuckPermsHandler().quit(event.getPlayer().getUniqueId());
		}
	}
}