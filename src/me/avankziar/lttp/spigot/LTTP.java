package me.avankziar.lttp.spigot;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.avankziar.lttp.general.database.YamlHandler;
import me.avankziar.lttp.general.database.YamlManager;
import me.avankziar.lttp.spigot.database.SQLiteHandler;
import me.avankziar.lttp.spigot.database.SQLiteSetup;
import me.avankziar.lttp.spigot.metrics.Metrics;
import me.avankziar.lttp.spigot.handler.LuckPermsHandler;
import me.avankziar.lttp.spigot.listener.JoinQuitListener;

public class LTTP extends JavaPlugin
{
	public static Logger logger;
	private static LTTP plugin;
	public static String pluginname = "LoginTimedTemporaryPermission";
	
	private YamlHandler yamlHandler;
	private YamlManager yamlManager;
	private SQLiteSetup sqlLiteSetup;
	private SQLiteHandler sqlLiteHandler;
	
	private LuckPermsHandler luckpermsHandler;
	
	public void onEnable()
	{
		plugin = this;
		logger = getLogger();
		
		//https://patorjk.com/software/taag/#p=display&f=ANSI%20Shadow&t=LTTP
		logger.info(" ██╗  ████████╗████████╗██████╗  | API-Version: "+plugin.getDescription().getAPIVersion());
		logger.info(" ██║  ╚══██╔══╝╚══██╔══╝██╔══██╗ | Author: "+plugin.getDescription().getAuthors().toString());
		logger.info(" ██║     ██║      ██║   ██████╔╝ | Plugin Website: "+plugin.getDescription().getWebsite());
		logger.info(" ██║     ██║      ██║   ██╔═══╝  | Depend Plugins: "+plugin.getDescription().getDepend().toString());
		logger.info(" ███████╗██║      ██║   ██║      | SoftDepend Plugins: "+plugin.getDescription().getSoftDepend().toString());
		logger.info(" ╚══════╝╚═╝      ╚═╝   ╚═╝      | LoadBefore: "+plugin.getDescription().getLoadBefore().toString());
		
		yamlHandler = new YamlHandler(YamlManager.Type.SPIGOT, pluginname, logger, plugin.getDataFolder().toPath(), null);
        setYamlManager(yamlHandler.getYamlManager());
        
        sqlLiteSetup = new SQLiteSetup(plugin);
		sqlLiteHandler = new SQLiteHandler(plugin);
		
		setupListeners();
		setupBstats();
		setupPermissionApis();
	}
	
	public void onDisable()
	{
		Bukkit.getScheduler().cancelTasks(this);
		HandlerList.unregisterAll(this);
		logger = null;
		yamlHandler = null;
		yamlManager = null;
		sqlLiteSetup = null;
		sqlLiteHandler = null;
		if(getServer().getPluginManager().isPluginEnabled("InterfaceHub")) 
	    {
	    	getServer().getServicesManager().unregisterAll(plugin);
	    }
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
		this.yamlManager = yamlManager;
	}
	
	public SQLiteSetup getSQLLiteSetup() 
	{
		return sqlLiteSetup;
	}
	
	public SQLiteHandler getSQLLiteHandler()
	{
		return sqlLiteHandler;
	}
	
	public void setupListeners()
	{
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new JoinQuitListener(plugin), plugin);
	}
	
	private void setupPermissionApis()
    {
		Plugin lp = getServer().getPluginManager().getPlugin("LuckPerms");
        if(lp != null && lp.isEnabled()) 
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
		int pluginId = 0;
        new Metrics(this, pluginId);
	}
}