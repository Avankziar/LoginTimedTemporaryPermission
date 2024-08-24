package me.avankziar.lttp.general.database;

import me.avankziar.lttp.general.object.PlayerTemporaryPermission;

public enum SQLiteType
{
	PLAYER_TEMP_PERM("lttpPlayerTemporaryPermission", new PlayerTemporaryPermission(), ServerType.ALL,
			"CREATE TABLE IF NOT EXISTS `%%tablename%%"
			+ "` (id int AUTO_INCREMENT PRIMARY KEY,"
			+ " player_uuid char(36) NOT NULL,"
			+ " permission text,"
			+ " duration bigint,"
			+ " permission_value boolean);");
	
	private SQLiteType(String tableName, Object object, ServerType usedOnServer, String setupQuery)
	{
		this.tableName = tableName;
		this.object = object;
		this.usedOnServer = usedOnServer;
		this.setupQuery = setupQuery.replace("%%tablename%%", tableName);
	}
	
	private final String tableName;
	private final Object object;
	private final ServerType usedOnServer;
	private final String setupQuery;

	public String getValue()
	{
		return tableName;
	}
	
	public Object getObject()
	{
		return object;
	}
	
	public ServerType getUsedOnServer()
	{
		return usedOnServer;
	}
	
	public String getSetupQuery()
	{
		return setupQuery;
	}
}