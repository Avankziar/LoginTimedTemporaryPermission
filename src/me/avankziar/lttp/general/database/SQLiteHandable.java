package me.avankziar.lttp.general.database;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.logging.Level;

import me.avankziar.lttp.velocity.LTTP;

public interface SQLiteHandable
{
	public boolean create(Connection conn, String tablename);
	
	public boolean update(Connection conn, String tablename, String whereColumn, Object... whereObject);
	
	public ArrayList<Object> get(Connection conn, String tablename, String orderby, String limit, String whereColumn, Object... whereObject);
	
	default void log(Level level, String log, Exception e)
	{
		LTTP.getPlugin().getLogger().log(level, log, e);
	}
}
