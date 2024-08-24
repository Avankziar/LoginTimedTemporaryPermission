package me.avankziar.lttp.bungee.database;

import me.avankziar.lttp.general.database.SQLiteBaseHandler;
import me.avankziar.lttp.bungee.LTTP;

public class SQLiteHandler extends SQLiteBaseHandler
{
	public SQLiteHandler(LTTP plugin)
	{
		super(plugin.getLogger(), plugin.getSQLLiteSetup());
	}
}