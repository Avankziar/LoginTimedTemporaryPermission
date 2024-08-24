# LoginTimedTemporaryPermission
LTTP is a plugin which store individual temporary permission of players in a local database (sqlite) if the player is offline. When the player joins, the permission are called from the database and set in the permissionsystem.<br>
This means that these temporary permissions are tied to the player's actual playing time and not, as usual, the normal time online and offline.

# Suppored Servers
Supported are:
- Spigot
- Bungeecord
- Velocity

# How to Install
To install LoginTimedTemporaryPermission (LTTP), proceed as follows:
- Download the Jar file.
- Copy the jar file into the plugins folder your server.
- Restart the server.
- Finish

<b>Attention!</b><br>
Have you ONLY one Server like Spigot/Paper, installed it there BUT if you have mutiple backend server like Spigot/Paper, you have likely a proxy server like Bungeecord/Velocity. If you have a proxy server only install the plugin there and nowhere else!

# Supported Permissionsystems
At the moment only [LuckPerms](https://luckperms.net) are supported. If you have a permissionplugin, which also has a normal to good api, you can send me a heads up^^

# Good to know
LTTP has no commands, and permission BUT it has a config.<br>
There you can put excluded Permission, which you dont want the plugin to store.<br>
(The idea there is, that you as admin, can seperate between permission which are bound to the players actual playtime or (the excluded ones) on the normale time, offline or online)

For example: "vip;here.your.permission"<br>
The term "vip" is the primary group. Meaning if the player has this group as primary group, it will it ignore it. If you which, that a permission is excluded for all players, use the term "default".<br>
The term "here.your.permission" is the permission which will be ignored.