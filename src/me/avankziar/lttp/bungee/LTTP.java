package me.avankziar.lttp.bungee;

import java.util.logging.Logger;

import me.avankziar.lttp.bungee.database.SQLiteHandler;
import me.avankziar.lttp.bungee.database.SQLiteSetup;
import me.avankziar.lttp.bungee.handler.LuckPermsHandler;
import me.avankziar.lttp.bungee.listener.JoinQuitListener;
import me.avankziar.lttp.bungee.metrics.Metrics;
import me.avankziar.lttp.general.database.YamlHandler;
import me.avankziar.lttp.general.database.YamlManager;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class LTTP extends Plugin
{
	public static LTTP plugin;
	public static Logger logger;
	public static String pluginname = "LoginTimedTemporaryPermission";
	private static YamlHandler yamlHandler;
	private static YamlManager yamlManager;
	private SQLiteSetup sqlLiteSetup;
	private SQLiteHandler sqlLiteHandler;
	
	private LuckPermsHandler luckpermsHandler;
	
	public void onEnable() 
	{
		plugin = this;
		logger = Logger.getLogger("LTTP");
		
		//https://patorjk.com/software/taag/#p=display&f=ANSI%20Shadow&t=LTTP
		logger.info(" ██╗  ████████╗████████╗██████╗  | Version: "+plugin.getDescription().getVersion());
		logger.info(" ██║  ╚══██╔══╝╚══██╔══╝██╔══██╗ | Author: "+plugin.getDescription().getAuthor());
		logger.info(" ██║     ██║      ██║   ██████╔╝ | Plugin Website: https://www.spigotmc.org/resources/rootadministration.104833/");
		logger.info(" ██║     ██║      ██║   ██╔═══╝  | Depend Plugins: "+plugin.getDescription().getDepends().toString());
		logger.info(" ███████╗██║      ██║   ██║      | SoftDepend Plugins: "+plugin.getDescription().getSoftDepends().toString());
		logger.info(" ╚══════╝╚═╝      ╚═╝   ╚═╝      | Have Fun^^");
		
		yamlHandler = new YamlHandler(YamlManager.Type.BUNGEE, pluginname, logger, plugin.getDataFolder().toPath(), null);
        setYamlManager(yamlHandler.getYamlManager());
        
        sqlLiteSetup = new SQLiteSetup(plugin);
		sqlLiteHandler = new SQLiteHandler(plugin);
		
		ListenerSetup();
		setupBstats();
		setupPermissionApis();
	}
	
	public void onDisable()
	{
		getProxy().getScheduler().cancel(plugin);	
		logger = null;
		yamlHandler = null;
		yamlManager = null;
		sqlLiteHandler = null;
    	sqlLiteSetup = null;
		getProxy().getPluginManager().unregisterListeners(plugin);
		getProxy().getPluginManager().unregisterCommands(plugin);
		logger.info(pluginname + " is disabled!");
	}
	
	public static LTTP getPlugin()
	{
		return plugin;
	}
	
	public static void shutdown()
	{
		LTTP.getPlugin().onDisable();
	}
	
	public YamlHandler getYamlHandler() 
	{
		return yamlHandler;
	}
	
	public YamlManager getYamlManager()
	{
		return yamlManager;
	}
	
	public void setYamlManager(YamlManager yamlManager)
	{
		LTTP.yamlManager = yamlManager;
	}
	
	public SQLiteSetup getSQLLiteSetup() 
	{
		return sqlLiteSetup;
	}
	
	public SQLiteHandler getSQLLiteHandler()
	{
		return sqlLiteHandler;
	}
	
	public void ListenerSetup()
	{
		PluginManager pm = getProxy().getPluginManager();
		pm.registerListener(plugin, new JoinQuitListener(plugin));
	}
	
	 private void setupPermissionApis()
	 {
	 	Plugin lp = getProxy().getPluginManager().getPlugin("LuckPerms");
        if(lp != null) 
        {
        	luckpermsHandler = new LuckPermsHandler(plugin, net.luckperms.api.LuckPermsProvider.get());
        }
        if(luckpermsHandler == null)
        {
        	shutdown();
        }
    }
    
    public LuckPermsHandler getLuckPermsHandler()
    {
    	return luckpermsHandler;
    }
	
	public void setupBstats()
	{
		int pluginId = 23283;
        new Metrics(this, pluginId);
	}
}