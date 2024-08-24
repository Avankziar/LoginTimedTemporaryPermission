package me.avankziar.lttp.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;

public class JoinQuitListener 
{
	
	@Subscribe
	public void onPlayerJoin(PlayerChooseInitialServerEvent event)
	{
		
	}
	
	@Subscribe
	public void onPlayerQuit(DisconnectEvent event)
	{
		
	}
}