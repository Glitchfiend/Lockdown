/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package adubbz.lockdown.mixin.client;

import adubbz.lockdown.Config;
import adubbz.lockdown.Lockdown;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class MixinTitleScreen extends Screen
{
    protected MixinTitleScreen(Component component)
    {
        super(component);
    }

    @Inject(method="init", at=@At(value="RETURN"))
    private void onInit(CallbackInfo ci)
    {
        Button singleplayerButton = (Button)this.renderables.get(0);
        Button multiplayerButton = (Button)this.renderables.get(1);
        Button realmsButton = (Button)this.renderables.get(2);
        Button modsButton = (Button)this.renderables.get(3);
        Button languageButton = (Button)this.renderables.get(4);
        Button optionsButton = (Button)this.renderables.get(5);
        Button quitButton = (Button)this.renderables.get(6);
        Button accessibilityButton = (Button)this.renderables.get(7);

        if (Config.disableSingleplayer.get())
        {
            singleplayerButton.visible = false;
            multiplayerButton.y -= 24;
            realmsButton.y -= 24;
            modsButton.y -= 24;
            languageButton.y -= 24;
            optionsButton.y -= 24;
            quitButton.y -= 24;
            accessibilityButton.y -= 24;
        }

        if (Config.disableMultiplayer.get())
        {
            multiplayerButton.visible = false;
            realmsButton.y -= 24;
            modsButton.y -= 24;
            languageButton.y -= 24;
            optionsButton.y -= 24;
            quitButton.y -= 24;
            accessibilityButton.y -= 24;
        }
    }
}
