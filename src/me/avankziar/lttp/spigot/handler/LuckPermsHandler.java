package me.avankziar.lttp.spigot.handler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.avankziar.lttp.general.assistance.ChatApi;
import me.avankziar.lttp.general.database.SQLiteType;
import me.avankziar.lttp.general.object.PlayerTemporaryPermission;
import me.avankziar.lttp.spigot.LTTP;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.PermissionNode;

public class LuckPermsHandler 
{
	private LTTP plugin;
	private LuckPerms lp;
	private ArrayList<String> excludedPermissionDefault = new ArrayList<>();
	private LinkedHashMap<String, ArrayList<String>> excludedPermissionPerGroup = new LinkedHashMap<>();
	private ArrayList<UUID> inSync = new ArrayList<>();
	
	public LuckPermsHandler(LTTP plugin, LuckPerms lp)
	{
		this.plugin = plugin;
		this.lp = lp;
		for(String s : plugin.getYamlHandler().getConfig().getStringList("ExcludedPermissionPerGroup"))
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
	
	public void join(final Player player, final UUID uuid)
	{
		new BukkitRunnable() 
		{
			@Override
			public void run() 
			{
				if(isInSync(uuid))
				{
					return;
				}
				addInSync(uuid);
				final ArrayList<PlayerTemporaryPermission> ptps = PlayerTemporaryPermission.convert(
						LTTP.getPlugin().getSQLLiteHandler().getFullList(
								SQLiteType.PLAYER_TEMP_PERM, "`id` ASC", "`player_uuid` = ?", uuid.toString()));
				if(ptps.isEmpty())
				{
					cancel();
					return;
				}
				User user = lp.getUserManager().getUser(uuid);
				for(PlayerTemporaryPermission ptp : ptps)
				{
					PermissionNode pn = PermissionNode
							.builder(ptp.getPermission())
							.expiry(ptp.getDuration(), TimeUnit.MILLISECONDS)
							.value(ptp.isValue()).build();
					user.data().add(pn);
				}
				CompletableFuture.runAsync(() -> lp.getUserManager().saveUser(user));
				LTTP.getPlugin().getSQLLiteHandler().deleteData(SQLiteType.PLAYER_TEMP_PERM, "`player_uuid` = ?", uuid.toString());
				cancel();
				removeInSync(uuid);
				if(player != null
						&& LTTP.getPlugin().getYamlHandler().getConfig().getBoolean("Sync.Message.SendByJoin"))
				{
					ChatApi.sendMessage(player, LTTP.getPlugin().getYamlHandler().getLang().getString("SyncMessage")
							.replace("%amount%", String.valueOf(ptps.size())));
				}
			}
		}.runTaskTimerAsynchronously(plugin, 0L, 10L);
	}
	
	public void quit(final UUID uuid)
	{
		new BukkitRunnable() 
		{
			@Override
			public void run() 
			{
				if(isInSync(uuid))
				{
					return;
				}
				addInSync(uuid);
				User user = lp.getUserManager().getUser(uuid);
				String primary = user.getPrimaryGroup();
				final List<PermissionNode> list = user.getNodes(NodeType.PERMISSION).stream()
						.filter(x -> x.hasExpiry())
					    .collect(Collectors.toList());
				for(PermissionNode pn : list)
				{
					String perm = pn.getPermission();
					if(isExcluded(primary, perm))
					{
						continue;
					}
					long dur = pn.getExpiryDuration().toMillis();
					boolean value = pn.getValue();
					PlayerTemporaryPermission ptp = new PlayerTemporaryPermission(0, uuid, perm, dur, value);
					LTTP.getPlugin().getSQLLiteHandler().create(SQLiteType.PLAYER_TEMP_PERM, ptp);
					user.data().remove(pn);
				}
				CompletableFuture.runAsync(() -> lp.getUserManager().saveUser(user));
				cancel();
				removeInSync(uuid);
			}
		}.runTaskTimerAsynchronously(plugin, 0, 10L);
	}
}