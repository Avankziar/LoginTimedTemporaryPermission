package me.avankziar.lttp.bungee.listener;


import me.avankziar.lttp.bungee.LTTP;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class JoinQuitListener implements Listener
{
	private LTTP plugin;
	
	public JoinQuitListener(LTTP plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PostLoginEvent event)
	{
		if(plugin.getLuckPermsHandler() != null)
		{
			plugin.getLuckPermsHandler().join(event.getPlayer(), event.getPlayer().getUniqueId());
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerDisconnectEvent event)
	{
		if(plugin.getLuckPermsHandler() != null)
		{
			plugin.getLuckPermsHandler().quit(event.getPlayer().getUniqueId());
		}
	}
}