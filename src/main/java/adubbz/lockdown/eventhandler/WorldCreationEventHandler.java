package adubbz.lockdown.eventhandler;

import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiOpenEvent;
import adubbz.lockdown.gui.GuiCreateFixedWorld;
import adubbz.lockdown.gui.GuiNonMultiplayerMainMenu;
import adubbz.lockdown.util.LDObfuscationHelper;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WorldCreationEventHandler 
{
	@SubscribeEvent
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
			GuiScreen parentScreen = ObfuscationReflectionHelper.getPrivateValue(GuiCreateWorld.class, createWorld, LDObfuscationHelper.parentGuiScreen);
			
			event.gui = new GuiCreateFixedWorld(parentScreen);
		}
	}
}
