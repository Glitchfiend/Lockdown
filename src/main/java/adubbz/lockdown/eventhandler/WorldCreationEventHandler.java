package adubbz.lockdown.eventhandler;

import adubbz.lockdown.Lockdown;
import adubbz.lockdown.gui.GuiCreateFixedWorld;
import adubbz.lockdown.gui.GuiNonMultiplayerMainMenu;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WorldCreationEventHandler 
{
	@SubscribeEvent
	public void modifyWorldCreation(GuiOpenEvent event)
	{
		if (event.getGui() instanceof GuiMainMenu && Lockdown.disableMultiplayer && !(event.getGui() instanceof GuiNonMultiplayerMainMenu))
		{
			event.setGui(new GuiNonMultiplayerMainMenu());
		}
		
		if (event.getGui() instanceof GuiCreateWorld && !(event.getGui() instanceof GuiCreateFixedWorld))
		{
			GuiCreateWorld createWorld = (GuiCreateWorld)event.getGui();
			event.setGui(new GuiCreateFixedWorld(createWorld.parentScreen));
		}
	}
}
