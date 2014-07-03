package adubbz.lockdown;

import org.apache.logging.log4j.Level;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import adubbz.lockdown.eventhandler.WorldCreationEventHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "Lockdown", name = "Lockdown")
public class Lockdown 
{
    @Instance("Lockdown")
    public static Lockdown instance;
    
    public static String templateDirectory;
    
    public static boolean disableWorldCreation;
    
    public static boolean disableGameMode;
    public static boolean disableMoreWorldOptions;
    
    public static boolean disableMultiplayer;
    //public static boolean disableQuit;
    
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
    		
    		disableMultiplayer = config.get("Main Menu", "Disable Multiplayer Button", true).getBoolean(true);
    		//disableQuit = config.get("Main Menu", "Disable Quit Button", true).getBoolean(true);
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
