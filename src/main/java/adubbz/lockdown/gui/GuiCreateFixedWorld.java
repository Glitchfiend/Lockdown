package adubbz.lockdown.gui;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import adubbz.lockdown.Lockdown;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

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

    		this.gameModeDesc1 = "";
    		this.gameModeDesc2 = "";
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
            
            try
            {
            	FileUtils.copyDirectory(new File(mcDataDir.getAbsoluteFile() + File.separator + Lockdown.templateDirectory), new File(mcDataDir.getAbsoluteFile() + File.separator + "saves" + File.separator + this.saveDirName));
            }
            catch (IOException e)
            {
            	Lockdown.logger.log(Level.ERROR, "The template world does not exist at " + Lockdown.templateDirectory, e);
            	return;
            }
            
            ISaveFormat isaveformat = this.mc.getSaveLoader();
            isaveformat.renameWorld(this.saveDirName, worldName);

            WorldSettings worldsettings = null;     // Default will just use the template's world settings.

            if(Lockdown.enableOverridingTerrainGen)
            {
                long defaultSeed = (new Random()).nextLong();
                String seedText = this.worldSeedField.getText();

                if (!StringUtils.isEmpty(seedText))
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

                GameType gametype = GameType.parseGameTypeWithDefault(this.gameMode, GameType.NOT_SET);
                worldsettings = new WorldSettings(defaultSeed, gametype, this.generateStructuresEnabled, this.bonusChestEnabled, WorldType.WORLD_TYPES[this.selectedIndex]);
                worldsettings.setGeneratorOptions(this.chunkProviderSettingsJson);

                if(this.inMoreWorldOptionsDisplay)
                {
                    worldsettings.enableCommands();
                }

                // This normally happens once in the instance is started, but this code is skipped if a save already
                // exists. We're going to flatten the copied save settings with the ones the user just defined.
                // However, we have to make sure it's JUST the world settings. We need to keep the player's coordinates,
                // inventory, game mode, and whatever other miscellaneous junk.
                ISaveFormat saveLoader = new AnvilSaveConverter(new File(this.mc.mcDataDir, "saves"), this.mc.getDataFixer());
                ISaveHandler isavehandler = saveLoader.getSaveLoader(this.saveDirName, false);
                WorldInfo worldinfo = isavehandler.loadWorldInfo();
                worldinfo.populateFromWorldSettings(worldsettings);

                isavehandler.saveWorldInfo(worldinfo);
            }

            if (this.mc.getSaveLoader().canLoadWorld(this.saveDirName))
            {
                this.mc.launchIntegratedServer(this.saveDirName, worldName, worldsettings);
            }
    	}
    	else
    	{
            try {
    		super.actionPerformed(guiButton);
            } catch (IOException e) {
                Lockdown.logger.log(Level.ERROR, "Action couldn't be performed ",e);
            }
    	}
    }
}
