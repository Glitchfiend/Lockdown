package adubbz.lockdown.eventhandler;

import adubbz.lockdown.Lockdown;
import adubbz.lockdown.gui.GuiCreateFixedWorld;
import adubbz.lockdown.gui.GuiMainMenuTweaked;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WorldCreationEventHandler 
{
	@SubscribeEvent
	public void modifyWorldCreation(GuiOpenEvent event)
	{
	    boolean overrideMainMenu = (Lockdown.disableMultiplayer || Lockdown.disableSingleplayer);
	    
		if (event.getGui() instanceof GuiMainMenu && overrideMainMenu && !(event.getGui() instanceof GuiMainMenuTweaked))
		{
			event.setGui(new GuiMainMenuTweaked());
		}
		
		if (event.getGui() instanceof GuiCreateWorld && !(event.getGui() instanceof GuiCreateFixedWorld))
		{
			GuiCreateWorld createWorld = (GuiCreateWorld)event.getGui();
			event.setGui(new GuiCreateFixedWorld(createWorld.parentScreen));
		}
	}
}
