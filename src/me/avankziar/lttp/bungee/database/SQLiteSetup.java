package me.avankziar.lttp.bungee.database;

import me.avankziar.lttp.general.database.SQLiteBaseSetup;
import me.avankziar.lttp.general.database.ServerType;
import me.avankziar.lttp.bungee.LTTP;

public class SQLiteSetup extends SQLiteBaseSetup
{
	public SQLiteSetup(LTTP plugin)
	{
		super(plugin.getLogger(), plugin.getDataFolder());
		loadMysqlSetup(ServerType.SPIGOT);
	}
}