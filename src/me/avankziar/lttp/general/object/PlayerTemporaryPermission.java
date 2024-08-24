package me.avankziar.lttp.general.object;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

import me.avankziar.lttp.general.database.QueryType;
import me.avankziar.lttp.general.database.SQLiteBaseHandler;
import me.avankziar.lttp.general.database.SQLiteHandable;

public class PlayerTemporaryPermission implements SQLiteHandable
{
	private int id;
	private UUID uuid;
	private String permission;
	private long duration;
	private boolean value;
	
	public PlayerTemporaryPermission()
	{
		//empty
	}
	
	public PlayerTemporaryPermission(int id, UUID uuid, String permission, long duration, boolean value)
	{
		setId(id);
		setUuid(uuid);
		setPermission(permission);
		setDuration(duration);
		setValue(value);
	}
	
	public int getId() 
	{
		return id;
	}

	public void setId(int id) {
		this.id = id;
		
	}

	public UUID getUuid() 
	{
		return uuid;
	}

	public void setUuid(UUID uuid) 
	{
		this.uuid = uuid;
	}

	public String getPermission() 
	{
		return permission;
	}

	public void setPermission(String permission) 
	{
		this.permission = permission;
	}

	public long getDuration() 
	{
		return duration;
	}

	public void setDuration(long duration) 
	{
		this.duration = duration;
	}

	public boolean isValue() 
	{
		return value;
	}

	public void setValue(boolean value) 
	{
		this.value = value;
	}

	@Override
	public boolean create(Connection conn, String tablename)
	{
		try
		{
			String sql = "INSERT INTO `" + tablename
					+ "`(`player_uuid`, `permission`, `duration`, `permission_value`) " 
					+ "VALUES(?, ?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, getUuid().toString());
	        ps.setString(2, getPermission());
	        ps.setLong(3, getDuration());
	        ps.setBoolean(4, isValue());
	        
	        int i = ps.executeUpdate();
	        SQLiteBaseHandler.addRows(QueryType.INSERT, i);
	        return true;
		} catch (SQLException e)
		{
			this.log(Level.WARNING, "SQLException! Could not create a "+this.getClass().getSimpleName()+" Object!", e);
		}
		return false;
	}

	@Override
	public boolean update(Connection conn, String tablename, String whereColumn, Object... whereObject)
	{
		try
		{
			String sql = "UPDATE `" + tablename
					+ "` SET `player_uuid` = ?, `permission` = ?, `duration` = ?, `permission_value` = ?"
					+ " WHERE "+whereColumn;
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, getUuid().toString());
	        ps.setString(2, getPermission());
	        ps.setLong(3, getDuration());
	        ps.setBoolean(4, isValue());
	        
	        int i = 5;
			for(Object o : whereObject)
			{
				ps.setObject(i, o);
				i++;
			}			
			int u = ps.executeUpdate();
			SQLiteBaseHandler.addRows(QueryType.UPDATE, u);
			return true;
		} catch (SQLException e)
		{
			this.log(Level.WARNING, "SQLException! Could not update a "+this.getClass().getSimpleName()+" Object!", e);
		}
		return false;
	}

	@Override
	public ArrayList<Object> get(Connection conn, String tablename, String orderby, String limit, String whereColumn, Object... whereObject)
	{
		try
		{
			String sql = "SELECT * FROM `" + tablename
				+ "` WHERE "+whereColumn+" ORDER BY "+orderby+limit;
			PreparedStatement ps = conn.prepareStatement(sql);
			int i = 1;
			for(Object o : whereObject)
			{
				ps.setObject(i, o);
				i++;
			}
			
			ResultSet rs = ps.executeQuery();
			SQLiteBaseHandler.addRows(QueryType.READ, rs.getMetaData().getColumnCount());
			ArrayList<Object> al = new ArrayList<>();
			while (rs.next()) 
			{
				al.add(new PlayerTemporaryPermission(
						rs.getInt("id"),
	        			UUID.fromString(rs.getString("player_uuid")),
	        			rs.getString("permission"),
	        			rs.getLong("duration"),
	        			rs.getBoolean("permission_value")));
			}
			return al;
		} catch (SQLException e)
		{
			this.log(Level.WARNING, "SQLException! Could not get a "+this.getClass().getSimpleName()+" Object!", e);
		}
		return new ArrayList<>();
	}
	
	public static ArrayList<PlayerTemporaryPermission> convert(ArrayList<Object> arrayList)
	{
		ArrayList<PlayerTemporaryPermission> l = new ArrayList<>();
		for(Object o : arrayList)
		{
			if(o instanceof PlayerTemporaryPermission)
			{
				l.add((PlayerTemporaryPermission) o);
			}
		}
		return l;
	}
}