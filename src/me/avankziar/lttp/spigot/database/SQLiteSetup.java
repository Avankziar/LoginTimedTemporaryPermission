package me.avankziar.lttp.spigot.database;

import me.avankziar.lttp.general.database.SQLiteBaseSetup;
import me.avankziar.lttp.general.database.ServerType;
import me.avankziar.lttp.spigot.LTTP;

public class SQLiteSetup extends SQLiteBaseSetup
{
	public SQLiteSetup(LTTP plugin)
	{
		super(plugin.getLogger());
		loadMysqlSetup(ServerType.SPIGOT);
	}
}