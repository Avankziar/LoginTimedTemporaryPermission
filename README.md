# LoginTimedTemporaryPermission
LTTP is a plugin which store individual temporary permission/groups of players in a local database (sqlite) if the player is offline. When the player joins, the permission/groups are called from the database and set in the permissionsystem.<br>
This means that these temporary permissions/groups are tied to the player's actual playing time and not, as usual, the normal real life time.

# Suppored Servers
Supported are:
- Spigot (Forks should no problems)
- Bungeecord
- Velocity

# How to Install
To install LoginTimedTemporaryPermission (LTTP), proceed as follows:
- Download the Jar file.
- Copy the jar file into the plugins folder your server.
- Install the plugin **[InterfaceHub](https://www.spigotmc.org/resources/interfacehub.101648/)** for a dependency!
- Restart the server.
- Finish

<b>Attention!</b><br>
Have you ONLY one Server like Spigot/Paper, installed it there BUT if you have mutiple backend server like Spigot/Paper, you have likely a proxy server like Bungeecord/Velocity. If you have a proxy server only install the plugin there and nowhere else!

# Supported Permissionsystems
At the moment only [LuckPerms](https://luckperms.net) are supported. If you have a permissionplugin, which also has a normal to good api, you can send me a heads up^^

# Loading and Storing Behavior
The plugin store all not excluded permission and groups, which are temporary with all its context. If any are present.
But if a permission or group was set in the time the player was offline, the plugin determinate as follow the logic:
- If the permission/group which was set while the player was offline is now a permanent one. The plugin will not update or change this permission/group.
- If the permission/group which was set while the player was offline has now a context set, it will be always override the stored one. Present or not. But if the store one has a context and the new one did not. Than the stored context will be used.

And always will be the of the new one set duration added to the stored one.

# Good to know
LTTP has no commands, and permission BUT it has a config.<br>
There you can put excluded Permission, which you dont want the plugin to store.<br>
(The idea there is, that you as admin, can seperate between permission which are bound to the players actual playtime or (the excluded ones) on the normale time, offline or online)

For example: "vip;here.your.permission"<br>
The term "vip" is the primary group. Meaning if the player has this group as primary group, it will it ignore it. If you which, that a permission is excluded for all players, use the term "default".<br>
The term "here.your.permission" is the permission which will be ignored.

Also can you excluded groups, which will be ignored to coldstore. Simple put the to exclude groups in the list.