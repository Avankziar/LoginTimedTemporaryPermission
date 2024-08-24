package me.avankziar.lttp.velocity.database;

import me.avankziar.lttp.general.database.SQLiteBaseHandler;
import me.avankziar.lttp.velocity.LTTP;

public class SQLiteHandler extends SQLiteBaseHandler
{
	public SQLiteHandler(LTTP plugin)
	{
		super(plugin.getLogger(), plugin.getSQLLiteSetup());
	}
}