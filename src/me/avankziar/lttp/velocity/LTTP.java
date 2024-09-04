package me.avankziar.lttp.velocity;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.PluginDescription;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

import me.avankziar.ifh.velocity.IFH;
import me.avankziar.lttp.general.database.YamlHandler;
import me.avankziar.lttp.general.database.YamlManager;
import me.avankziar.lttp.velocity.database.SQLiteHandler;
import me.avankziar.lttp.velocity.database.SQLiteSetup;
import me.avankziar.lttp.velocity.handler.LuckPermsHandler;
import me.avankziar.lttp.velocity.listener.JoinQuitListener;
import me.avankziar.lttp.velocity.metric.Metrics;

@Plugin(
	id = "logintimedtemporarypermission",
	name = "LoginTimedTemporaryPermission",
	version = "1-0-0",
	url = "https://example.org",
	dependencies = {
			@Dependency(id = "interfacehub"),
			@Dependency(id = "luckperms", optional = true)
	},
	description = "base for template",
	authors = {"Avankziar"}
)
public class LTTP
{
	private static LTTP plugin;
    private final ProxyServer server;
    private Logger logger = null;
    private Path dataDirectory;
    public String pluginname = "LoginTimedTemporaryPermission";
    private final Metrics.Factory metricsFactory;
    
    private YamlHandler yamlHandler;
    private YamlManager yamlManager;
    private SQLiteSetup sqlLiteSetup;
	private SQLiteHandler sqlLiteHandler;
	
	private LuckPermsHandler luckpermsHandler;
    
    @Inject
    public LTTP(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory, Metrics.Factory metricsFactory) 
    {
    	LTTP.plugin = this;
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.metricsFactory = metricsFactory;
    }
    
    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) 
    {
    	logger = Logger.getLogger("LTTP");
    	PluginDescription pd = server.getPluginManager().getPlugin(pluginname.toLowerCase()).get().getDescription();
        List<String> dependencies = new ArrayList<>();
        pd.getDependencies().stream().allMatch(x -> dependencies.add(x.getId()));
        //https://patorjk.com/software/taag/#p=display&f=ANSI%20Shadow&t=LTTP
		logger.info(" ██╗  ████████╗████████╗██████╗  | Id: "+pd.getId());
		logger.info(" ██║  ╚══██╔══╝╚══██╔══╝██╔══██╗ | Version: "+pd.getVersion().get());
		logger.info(" ██║     ██║      ██║   ██████╔╝ | Author: ["+String.join(", ", pd.getAuthors())+"]");
		logger.info(" ██║     ██║      ██║   ██╔═══╝  | Description: "+(pd.getDescription().isPresent() ? pd.getDescription().get() : "/"));
		logger.info(" ███████╗██║      ██║   ██║      | Plugin Website:"+pd.getUrl().toString());
		logger.info(" ╚══════╝╚═╝      ╚═╝   ╚═╝      | Dependencies Plugins: ["+String.join(", ", dependencies)+"]");
        
		yamlHandler = new YamlHandler(YamlManager.Type.VELO, pluginname, logger, dataDirectory, null);
        setYamlManager(yamlHandler.getYamlManager());
        
		sqlLiteSetup = new SQLiteSetup(plugin);
		sqlLiteHandler = new SQLiteHandler(plugin);
		
        setListeners();
        setupBstats();
        setupPermissionApis();
    }
    
    public void onDisable(ProxyShutdownEvent event)
	{
    	getServer().getScheduler().tasksByPlugin(plugin).forEach(x -> x.cancel());
    	logger = null;
    	yamlHandler = null;
    	yamlManager = null;
    	sqlLiteHandler = null;
    	sqlLiteSetup = null;
    	getServer().getEventManager().unregisterListeners(plugin);
    	Optional<PluginContainer> ifhp = plugin.getServer().getPluginManager().getPlugin("interfacehub");
        if(!ifhp.isEmpty()) 
        {
        	Optional<PluginContainer> plugins = plugin.getServer().getPluginManager().getPlugin(pluginname.toLowerCase());
        	me.avankziar.ifh.velocity.IFH ifh = IFH.getPlugin();
        	plugins.ifPresent(x -> ifh.getServicesManager().unregister(x));
        }
	}
    
    public static LTTP getPlugin()
    {
    	return LTTP.plugin;
    }
    
    public static void shutdown()
    {
    	LTTP.getPlugin().onDisable(null);
    }
    
    public ProxyServer getServer()
    {
    	return server;
    }
    
    public Logger getLogger()
    {
    	return logger;
    }
    
    public Path getDataDirectory()
    {
    	return dataDirectory;
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
    
    private void setListeners()
    {
    	EventManager em = server.getEventManager();
    	em.register(this, new JoinQuitListener(plugin));
    }
    
    private void setupPermissionApis()
    {
    	Optional<PluginContainer> lp = plugin.getServer().getPluginManager().getPlugin("luckperms");
        if(lp.isPresent()) 
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
    	int pluginId = 23284;
        metricsFactory.make(this, pluginId);
	}
}