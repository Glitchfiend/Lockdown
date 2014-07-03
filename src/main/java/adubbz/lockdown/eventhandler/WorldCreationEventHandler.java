package adubbz.lockdown.eventhandler;

import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.ForgeSubscribe;
import adubbz.lockdown.gui.GuiCreateFixedWorld;
import adubbz.lockdown.gui.GuiNonMultiplayerMainMenu;
import cpw.mods.fml.common.ObfuscationReflectionHelper;

public class WorldCreationEventHandler 
{
	@ForgeSubscribe
	public void modifyWorldCreation(GuiOpenEvent event)
	{
		if (event.gui instanceof GuiMainMenu && !(event.gui instanceof GuiNonMultiplayerMainMenu))
		{
			GuiMainMenu guiMainMenu = (GuiMainMenu)event.gui;
			
			event.gui = new GuiNonMultiplayerMainMenu();
		}
		
		if (event.gui instanceof GuiCreateWorld && !(event.gui instanceof GuiCreateFixedWorld))
		{
			GuiCreateWorld createWorld = (GuiCreateWorld)event.gui;
			GuiScreen parentScreen = ObfuscationReflectionHelper.getPrivateValue(GuiCreateWorld.class, createWorld, "parentGuiScreen", "field_73924_a");
			
			event.gui = new GuiCreateFixedWorld(parentScreen);
		}
	}
}
