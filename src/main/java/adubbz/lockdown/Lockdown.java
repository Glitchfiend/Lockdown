package adubbz.lockdown;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import adubbz.lockdown.eventhandler.WorldCreationEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "lockdown", name = "Lockdown", clientSideOnly = true)
public class Lockdown 
{
    @Instance("lockdown")
    public static Lockdown instance;
    
    public static String templateDirectory;
    
    public static Logger logger = LogManager.getLogger("lockdown");
    
    public static boolean disableWorldCreation;
    public static boolean disableGameMode;
    public static boolean disableMoreWorldOptions;
    public static boolean disableMultiplayer;
    public static boolean disableSingleplayer;
    public static boolean enableOverridingTerrainGen;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	Configuration config = new Configuration(event.getSuggestedConfigurationFile());
    	
    	try
    	{
    		config.load();
    		
    		templateDirectory = config.get("World Creation", "World Template Directory", "template").getString();
    		disableWorldCreation = config.get("World Creation", "Disable Regular World Creation", true).getBoolean(true);
			disableGameMode = config.get("World Creation", "Disable Game Mode Button", true).getBoolean(true);
    		disableMoreWorldOptions = config.get("World Creation", "Disable More World Options Button", true).getBoolean(true);
            enableOverridingTerrainGen = config.get("World Creation", "Enable Overriding Template World Settings", false).getBoolean(false);

    		disableMultiplayer = config.get("Main Menu", "Disable Multiplayer Button", true).getBoolean(true);
    		disableSingleplayer = config.get("Main Menu", "Disable Singleplayer Button", false).getBoolean(false);
    	}
    	catch (Exception e)
    	{
    		FMLLog.log(Level.ERROR, "Lockdown has had a problem loading its configuration", e);
    	}
    	finally
    	{
    		if (config.hasChanged()) config.save();
    	}
    	
    	MinecraftForge.EVENT_BUS.register(new WorldCreationEventHandler());
    }
}
