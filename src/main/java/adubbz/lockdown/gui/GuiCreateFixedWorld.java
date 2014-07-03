package adubbz.lockdown.gui;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import adubbz.lockdown.Lockdown;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.stats.StatList;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ISaveFormat;
import cpw.mods.fml.common.ObfuscationReflectionHelper;

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

    		ObfuscationReflectionHelper.setPrivateValue(GuiCreateWorld.class, this, "", "gameModeDescriptionLine1", "field_73920_A");
    		ObfuscationReflectionHelper.setPrivateValue(GuiCreateWorld.class, this, "", "gameModeDescriptionLine2", "field_73922_B");
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
            
            String folderName = ObfuscationReflectionHelper.getPrivateValue(GuiCreateWorld.class, this, "folderName", "field_73918_d");
            String worldName = ((GuiTextField)ObfuscationReflectionHelper.getPrivateValue(GuiCreateWorld.class, this, "textboxWorldName", "field_73919_b")).getText().trim();
            
            try
            {
            	FileUtils.copyDirectory(new File(mcDataDir.getAbsoluteFile() + File.separator + Lockdown.templateDirectory), new File(mcDataDir.getAbsoluteFile() + File.separator + "saves" + File.separator + folderName));
            }
            catch (IOException e)
            {
            	System.out.println("The template world does not exist at " + Lockdown.templateDirectory);
            	return;
            }
            
            ISaveFormat isaveformat = this.mc.getSaveLoader();
            isaveformat.renameWorld(folderName, worldName);
            
            if (this.mc.getSaveLoader().canLoadWorld(folderName))
            {
                this.mc.launchIntegratedServer(folderName, worldName, (WorldSettings)null);
                this.mc.statFileWriter.readStat(StatList.loadWorldStat, 1);
            }
    	}
    	else
    	{
    		super.actionPerformed(guiButton);
    	}
    }
}
