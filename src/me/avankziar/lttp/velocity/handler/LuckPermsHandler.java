package me.avankziar.lttp.velocity.handler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.velocitypowered.api.proxy.Player;

import me.avankziar.lttp.general.assistance.ChatApi;
import me.avankziar.lttp.general.database.SQLiteType;
import me.avankziar.lttp.general.handler.LuckPermsBaseHandler;
import me.avankziar.lttp.general.object.PlayerTemporaryPermission;
import me.avankziar.lttp.velocity.LTTP;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.MutableContextSet;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.query.QueryOptions;

public class LuckPermsHandler extends LuckPermsBaseHandler
{
	public LuckPermsHandler(LTTP plugin, LuckPerms lp)
	{
		super(plugin.getYamlHandler().getConfig(), lp);
	}
	
	public void join(final Player player, final UUID uuid)
	{
		LTTP.getPlugin().getServer().getScheduler().buildTask(LTTP.getPlugin(), (task) ->
		{
			if(isInSync(uuid))
			{
				return;
			}
			addInSync(uuid);
			final ArrayList<PlayerTemporaryPermission> ptps = PlayerTemporaryPermission.convert(
					LTTP.getPlugin().getSQLLiteHandler().getFullList(
							SQLiteType.PLAYER_TEMP_PERM, "`id` ASC", "`player_uuid` = ?", uuid.toString()));
			final ArrayList<PlayerTemporaryPermission> ptgs = PlayerTemporaryPermission.convert(
					LTTP.getPlugin().getSQLLiteHandler().getFullList(
							SQLiteType.PLAYER_TEMP_GROUP, "`id` ASC", "`player_uuid` = ?", uuid.toString()));
			if(ptps.isEmpty() && ptgs.isEmpty())
			{
				removeInSync(uuid);
				task.cancel();
				return;
			}
			User user = lp.getUserManager().getUser(uuid);
			for(PlayerTemporaryPermission ptp : ptps)
			{
				Optional<PermissionNode> opn = user.getNodes(NodeType.PERMISSION).stream()
						.filter(x -> x.getPermission().equals(ptp.getPermission())).findAny();
				if(opn.isPresent())
				{
					//A new Perm was added
					PermissionNode perm = opn.get();
					if(perm.hasExpiry() && !perm.hasExpired())
					{
						long dur = perm.getExpiryDuration().toMillis();
						PermissionNode.Builder b = perm.toBuilder();
						b.expiry(dur+ptp.getDuration(), TimeUnit.MILLISECONDS);
						if(perm.getContexts().isEmpty())
						{	
							LinkedHashMap<String, String> map = ptp.getContextMap();
							if(map != null)
							{
								map.keySet().stream().forEach(x -> b.context(MutableContextSet.of(x, map.get(x))));
							}
						}
						user.data().remove(perm);
						user.data().add(b.build());
					} else
					{
						//Perm is permanent, dont touch
						continue;
					}
				} else
				{
					//No new perm of the same was edit. Load normal
					PermissionNode.Builder b = PermissionNode
							.builder(ptp.getPermission())
							.expiry(ptp.getDuration(), TimeUnit.MILLISECONDS)
							.value(ptp.isValue());
					user.data().add(b.build());
				}
			}
			for(PlayerTemporaryPermission ptp : ptgs)
			{
				Optional<InheritanceNode> opn = user.getNodes(NodeType.INHERITANCE).stream()
						.filter(x -> x.getGroupName().equals(ptp.getPermission())).findAny();
				if(opn.isPresent())
				{
					InheritanceNode perm = opn.get();
					if(perm.hasExpiry() && !perm.hasExpired())
					{
						long dur = perm.getExpiryDuration().toMillis();
						InheritanceNode.Builder b = perm.toBuilder();
						b.expiry(dur+ptp.getDuration(), TimeUnit.MILLISECONDS);
						if(perm.getContexts().isEmpty())
						{	
							LinkedHashMap<String, String> map = ptp.getContextMap();
							if(map != null)
							{
								map.keySet().stream().forEach(x -> b.context(MutableContextSet.of(x, map.get(x))));
							}
						}
						user.data().remove(perm);
						user.data().add(b.build());
					} else
					{
						//Perm is permanent, dont touch
						continue;
					}
				} else
				{
					InheritanceNode.Builder b = InheritanceNode
							.builder(ptp.getPermission())
							.expiry(ptp.getDuration(), TimeUnit.MILLISECONDS)
							.value(ptp.isValue());
					LinkedHashMap<String, String> map = ptp.getContextMap();
					if(map != null)
					{
						map.keySet().stream().forEach(x -> b.context(MutableContextSet.of(x, map.get(x))));
					}					
					user.data().add(b.build());
				}
			}
			CompletableFuture.runAsync(() -> lp.getUserManager().saveUser(user));
			LTTP.getPlugin().getSQLLiteHandler().deleteData(SQLiteType.PLAYER_TEMP_PERM, "`player_uuid` = ?", uuid.toString());
			LTTP.getPlugin().getSQLLiteHandler().deleteData(SQLiteType.PLAYER_TEMP_GROUP, "`player_uuid` = ?", uuid.toString());
			task.cancel();
			removeInSync(uuid);
			if(player != null && player.isActive() 
					&& LTTP.getPlugin().getYamlHandler().getConfig().getBoolean("Sync.Message.SendByJoin"))
			{
				ChatApi.sendMessage(player, LTTP.getPlugin().getYamlHandler().getLang().getString("SyncMessage")
						.replace("%perm%", String.valueOf(ptps.size()))
						.replace("%group%", String.valueOf(ptgs.size())));
			}
		}).repeat(500L, TimeUnit.MILLISECONDS).schedule();
	}
	
	public void quit(final UUID uuid)
	{
		LTTP.getPlugin().getServer().getScheduler().buildTask(LTTP.getPlugin(), (task) ->
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
				ArrayList<String> context = new ArrayList<>();
				pn.getContexts().forEach(x -> context.add(x.getKey()+"="+x.getValue()));
				PlayerTemporaryPermission ptp = new PlayerTemporaryPermission(0, uuid, perm, dur, value, 
						context.toArray(new String[context.size()]));
				LTTP.getPlugin().getSQLLiteHandler().create(SQLiteType.PLAYER_TEMP_PERM, ptp);
				user.data().remove(pn);
			}
			for(InheritanceNode in : user.resolveInheritedNodes(QueryOptions.nonContextual()).stream()
					.filter(NodeType.INHERITANCE::matches)
					.filter(x -> x.hasExpiry())
					.filter(x -> !x.hasExpired())
					.map(NodeType.INHERITANCE::cast)
					.collect(Collectors.toList()))
			{
				if(isExcluded(in.getGroupName()))
				{
					continue;
				}
				long dur = in.getExpiryDuration().toMillis();
				boolean value = in.getValue();
				ArrayList<String> context = new ArrayList<>();
				in.getContexts().forEach(x -> context.add(x.getKey()+"="+x.getValue()));
				PlayerTemporaryPermission ptp = new PlayerTemporaryPermission(0, uuid, in.getGroupName(), dur, value, 
						context.toArray(new String[context.size()]));
				LTTP.getPlugin().getSQLLiteHandler().create(SQLiteType.PLAYER_TEMP_GROUP, ptp);
				user.data().remove(in);
			}
			CompletableFuture.runAsync(() -> lp.getUserManager().saveUser(user));
			task.cancel();
			removeInSync(uuid);
		}).repeat(500L, TimeUnit.MILLISECONDS).schedule();
	}
}