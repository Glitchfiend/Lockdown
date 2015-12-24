package adubbz.lockdown.gui;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Random;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.storage.ISaveFormat;

import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;

import adubbz.lockdown.Lockdown;
import adubbz.lockdown.util.LDLogger;
import adubbz.lockdown.util.LDObfuscationHelper;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class GuiCreateFixedWorld extends GuiCreateWorld
{
    private boolean createClicked;
	
	public GuiCreateFixedWorld(GuiScreen guiScreen) 
	{
		super(guiScreen);
	}
	
	@Override
    public void initGui()
    {
    	super.initGui();
    	
    	if (Lockdown.disableMoreWorldOptions) this.buttonList.remove(3); //More World Options 
    	
    	if (Lockdown.disableGameMode)
    	{
    		this.buttonList.remove(2); //Game Mode

    		ObfuscationReflectionHelper.setPrivateValue(GuiCreateWorld.class, this, "", LDObfuscationHelper.gameModeDescriptionLine1);
    		ObfuscationReflectionHelper.setPrivateValue(GuiCreateWorld.class, this, "", LDObfuscationHelper.gameModeDescriptionLine2);
    	}
    }

	@Override
    protected void actionPerformed(GuiButton guiButton)
    {
    	if (Lockdown.disableWorldCreation && guiButton.id == 0)
    	{
            this.mc.displayGuiScreen((GuiScreen)null);

            if (this.createClicked)
            {
                return;
            }

            this.createClicked = true;
            
            File mcDataDir = this.mc.mcDataDir;
            
            String folderName = ObfuscationReflectionHelper.getPrivateValue(GuiCreateWorld.class, this, LDObfuscationHelper.folderName);
            String worldName = ((GuiTextField)ObfuscationReflectionHelper.getPrivateValue(GuiCreateWorld.class, this, LDObfuscationHelper.textboxWorldName)).getText().trim();
            
            try
            {
                LDLogger.log(Level.INFO, "Lockdown using template at " + Lockdown.templateDirectory);
            	FileUtils.copyDirectory(new File(mcDataDir.getAbsoluteFile() + File.separator + Lockdown.templateDirectory), new File(mcDataDir.getAbsoluteFile() + File.separator + "saves" + File.separator + folderName));
            }
            catch (IOException e)
            {
            	LDLogger.log(Level.ERROR, "The template world does not exist at " + Lockdown.templateDirectory, e);
            	return;
            }
            
            ISaveFormat isaveformat = this.mc.getSaveLoader();
            isaveformat.renameWorld(folderName, worldName);

            WorldSettings worldsettings = null;     // Default will just use the template's world settings.

            if(Lockdown.enableOverridingTerrainGen)
            {

                // This mostly follows what a Vanilla instance would already do, excepting that we have to rip out all
                // the private fields. We are going to populate worldsettings ourselves. One exception is we go out of
                // our way to check that commands need to be enabled.
                GuiTextField seedTextField = ObfuscationReflectionHelper.getPrivateValue(GuiCreateWorld.class, this, "field_146335_h");
                String gameModeName = ObfuscationReflectionHelper.getPrivateValue(GuiCreateWorld.class, this, "field_146342_r");
                boolean generateStructures = ObfuscationReflectionHelper.getPrivateValue(GuiCreateWorld.class, this, "field_146341_s");
                boolean bonusChest = ObfuscationReflectionHelper.getPrivateValue(GuiCreateWorld.class, this, "field_146337_w");
                boolean commandsAllowed = ObfuscationReflectionHelper.getPrivateValue(GuiCreateWorld.class, this, "field_146344_y");
                int terrainType = ObfuscationReflectionHelper.getPrivateValue(GuiCreateWorld.class, this, "field_146331_K");

                long defaultSeed = (new Random()).nextLong();
                String seedText = seedTextField.getText();

                if (!MathHelper.stringNullOrLengthZero(seedText))
                {
                    try
                    {
                        long j = Long.parseLong(seedText);

                        if (j != 0L)
                        {
                            defaultSeed = j;
                        }
                    }
                    catch (NumberFormatException numberformatexception)
                    {
                        defaultSeed = (long)seedText.hashCode();
                    }
                }

                WorldSettings.GameType gametype = WorldSettings.GameType.getByName(gameModeName);
                worldsettings = new WorldSettings(defaultSeed, gametype, generateStructures, bonusChest, WorldType.worldTypes[terrainType]);
                worldsettings.func_82750_a(this.field_146334_a);

                if(commandsAllowed)
                {
                    worldsettings.enableCommands();
                }

                // This normally happens once in the instance is started, but this code is skipped if a save already
                // exists. We're going to flatten the copied save settings with the ones the user just defined.
                WorldInfo worldinfo = new WorldInfo(worldsettings, folderName);
                ISaveFormat saveLoader = new AnvilSaveConverter(new File(this.mc.mcDataDir, "saves"));
                ISaveHandler isavehandler = saveLoader.getSaveLoader(folderName, false);
                isavehandler.saveWorldInfo(worldinfo);
            }

            if (this.mc.getSaveLoader().canLoadWorld(folderName))
            {
                this.mc.launchIntegratedServer(folderName, worldName, worldsettings);
            }
    	}
    	else
    	{
            try {
    		super.actionPerformed(guiButton);
            } catch (IOException e) {
                LDLogger.log(Level.ERROR, "Action couldn't be performed ",e);
            }
    	}
    }
}
