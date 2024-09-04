package me.avankziar.lttp.general.handler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;

import dev.dejvokep.boostedyaml.YamlDocument;
import net.luckperms.api.LuckPerms;

public class LuckPermsBaseHandler
{
	public LuckPerms lp;
	private ArrayList<String> excludedPermissionDefault = new ArrayList<>();
	private LinkedHashMap<String, ArrayList<String>> excludedPermissionPerGroup = new LinkedHashMap<>();
	private ArrayList<String> excludedInheritance = new ArrayList<>();
	private ArrayList<UUID> inSync = new ArrayList<>();
	
	public LuckPermsBaseHandler(YamlDocument config, LuckPerms lp)
	{
		this.lp = lp;
		for(String s : config.getStringList("Excluded.PermissionPerGroup"))
		{
			String[] split = s.split(";");
			if(split.length != 2)
			{
				continue;
			}
			String group = split[0];
			String perm = split[1];
			ArrayList<String> list = new ArrayList<>();
			if(group.equals("default"))
			{
				if(!excludedPermissionDefault.contains(perm))
				{
					excludedPermissionDefault.add(perm);
				}
			} else
			{
				if(excludedPermissionPerGroup.containsKey(group))
				{
					list = excludedPermissionPerGroup.get(group);
				}
				if(!list.contains(perm))
				{
					list.add(perm);
				}
				excludedPermissionPerGroup.put(group, list);
			}
		}
		for(String s : config.getStringList("Excluded.GroupInheritance"))
		{
			if(excludedInheritance.contains(s))
			{
				continue;
			}
			excludedInheritance.add(s);
		}
	}
	
	public void addInSync(UUID uuid)
	{
		inSync.add(uuid);
	}
	
	public boolean isInSync(UUID uuid)
	{
		return inSync.contains(uuid);
	}
	
	public void removeInSync(UUID uuid)
	{
		inSync.remove(uuid);
	}
	
	public boolean isExcluded(String primary, String perm)
	{
		if(excludedPermissionPerGroup.containsKey(primary))
		{
			return excludedPermissionDefault.contains(perm) || excludedPermissionPerGroup.get(primary).contains(perm);
		}
		return excludedPermissionDefault.contains(perm);
	}
	
	public boolean isExcluded(String inhertiance)
	{
		return excludedInheritance.contains(inhertiance);
	}
}