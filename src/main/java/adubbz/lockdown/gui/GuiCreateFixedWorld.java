package adubbz.lockdown.gui;

import java.io.File;
import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ISaveFormat;

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
            	FileUtils.copyDirectory(new File(mcDataDir.getAbsoluteFile() + File.separator + Lockdown.templateDirectory), new File(mcDataDir.getAbsoluteFile() + File.separator + "saves" + File.separator + folderName));
            }
            catch (IOException e)
            {
            	LDLogger.log(Level.ERROR, "The template world does not exist at " + Lockdown.templateDirectory, e);
            	return;
            }
            
            ISaveFormat isaveformat = this.mc.getSaveLoader();
            isaveformat.renameWorld(folderName, worldName);
            
            if (this.mc.getSaveLoader().canLoadWorld(folderName))
            {
                this.mc.launchIntegratedServer(folderName, worldName, (WorldSettings)null);
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
