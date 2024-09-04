package me.avankziar.lttp.general.database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class SQLiteBaseSetup
{
	@Nullable
	protected static Logger logger;
	private String dbTemporaryPermission = "temporaryPermission";
	private File directory = null;
	
	public SQLiteBaseSetup(Logger logger, @Nonnull File directory)
	{
		SQLiteBaseSetup.logger = logger;
		this.directory = directory;
	}
	
	public boolean loadMysqlSetup(ServerType serverType)
	{
		if(!connectToDatabase())
		{
			return false;
		}
		for(SQLiteType mt : SQLiteType.values())
		{
			//Decide, on which server, the mysql Table should be used.
			switch(mt.getUsedOnServer())
			{
			case ALL: break;
			case PROXY:
				if(serverType == ServerType.BUNGEE || serverType == ServerType.VELOCITY)
				{
					break;
				} else
				{
					continue;
				}
			case BUNGEE:
			case SPIGOT:
			case VELOCITY:
				if(mt.getUsedOnServer() == serverType)
				{
					break;
				} else
				{
					continue;
				}
			}
			if(!baseSetup(mt.getSetupQuery()))
			{
				return false;
			}
		}
		return true;
	}
	
	public boolean connectToDatabase() 
	{
		logger.info("Connecting to the database...");
		try
		{
			getConnection();
			logger.info("Database connection successful!");
			return true;
		} catch(Exception e) 
		{
			logger.log(Level.WARNING, "Could not connect to Database!", e);
			return false;
		}		
	}
	
	public Connection getConnection() throws SQLException
	{
		return reConnect();
	}
	
	private Connection reConnect() throws SQLException
	{
		File directory = new File(this.directory.getPath()+"/SQLite/");
		if(!directory.exists())
		{
			directory.mkdir();
		}
		File db = new File(directory.getPath(), dbTemporaryPermission+".db");
		if(!db.exists())
		{
			try
			{
				db.createNewFile();
			} catch (IOException e)
			{
				logger.log(Level.WARNING, "Could not build db file!", e);
				e.printStackTrace();
			}
		}
		try
		{
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*boolean bool = false;
	    try
	    {
	    	// Load new Drivers for papermc
	    	Class.forName("com.mysql.cj.jdbc.Driver");
	    	bool = true;
	    } catch (Exception e)
	    {
	    	bool = false;
	    } 
	    if (bool == false)
    	{
    		// Load old Drivers for spigot
    		try
    		{
    			Class.forName("com.mysql.jdbc.Driver");
    		}  catch (Exception e) {}
    	}*/
        //Connect to database
        return DriverManager.getConnection("jdbc:sqlite:" + db);
	}
	
	public boolean baseSetup(String data) 
	{
		try (Connection conn = getConnection(); PreparedStatement query = conn.prepareStatement(data))
		{
			query.execute();
		} catch (SQLException e) 
		{
			logger.log(Level.WARNING, "Could not build data source. Or connection is null", e);
		}
		return true;
	}
}