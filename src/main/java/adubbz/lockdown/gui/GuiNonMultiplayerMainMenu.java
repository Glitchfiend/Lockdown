package adubbz.lockdown.gui;

import adubbz.lockdown.Lockdown;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;

public class GuiNonMultiplayerMainMenu extends GuiMainMenu
{
	@Override
    public void initGui()
    {
    	super.initGui();
    	
    	GuiButton singleplayerButton = (GuiButton)this.buttonList.get(0);
    	GuiButton multiplayerButton = (GuiButton)this.buttonList.get(1);
    	GuiButton realmsButton = (GuiButton)this.buttonList.get(2);
    	GuiButton modsButton = (GuiButton)this.buttonList.get(3); 
    	GuiButton optionsButton = (GuiButton)this.buttonList.get(4);
    	GuiButton quitButton = (GuiButton)this.buttonList.get(5);
    	GuiButton languageButton = (GuiButton)this.buttonList.get(6);
    	
    	/*if (Lockdown.disableQuit)
    	{
        	this.buttonList.remove(5); //Quit Button
    		
    		ObfuscationReflectionHelper.setPrivateValue(GuiButton.class, optionsButton, 200, "width", "field_73747_a");
    	}*/
    	
    	if (Lockdown.disableMultiplayer)
    	{
        	this.buttonList.remove(1); //Multiplayer Button
    		
        	realmsButton.yPosition -= 24;
    		modsButton.yPosition -= 24;
    		
    		languageButton.yPosition -= 24;
    		optionsButton.yPosition -=24;
    		quitButton.yPosition -= 24;
    	}
    }
}
