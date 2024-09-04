package me.avankziar.lttp.velocity.database;

import me.avankziar.lttp.general.database.SQLiteBaseSetup;
import me.avankziar.lttp.general.database.ServerType;
import me.avankziar.lttp.velocity.LTTP;

public class SQLiteSetup extends SQLiteBaseSetup
{
	public SQLiteSetup(LTTP plugin)
	{
		super(plugin.getLogger(), plugin.getDataDirectory().toFile());
		loadMysqlSetup(ServerType.VELOCITY);
	}
}