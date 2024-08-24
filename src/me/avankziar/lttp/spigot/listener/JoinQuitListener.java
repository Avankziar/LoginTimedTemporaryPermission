package me.avankziar.lttp.spigot.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.avankziar.lttp.spigot.LTTP;

public class JoinQuitListener implements Listener
{
	private LTTP plugin;
	
	public JoinQuitListener(LTTP plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		if(plugin.getLuckPermsHandler() != null)
		{
			plugin.getLuckPermsHandler().join(event.getPlayer(), event.getPlayer().getUniqueId());
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		if(plugin.getLuckPermsHandler() != null)
		{
			plugin.getLuckPermsHandler().quit(event.getPlayer().getUniqueId());
		}
	}
}