package me.avankziar.lttp.spigot.database;

import me.avankziar.lttp.general.database.SQLiteBaseHandler;
import me.avankziar.lttp.spigot.LTTP;

public class SQLiteHandler extends SQLiteBaseHandler
{
	public SQLiteHandler(LTTP plugin)
	{
		super(plugin.getLogger(), plugin.getSQLLiteSetup());
	}
}