/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package adubbz.lockdown.mixin.client;

import adubbz.lockdown.Config;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MainMenuScreen.class)
public abstract class MixinTitleScreen extends Screen
{
    protected MixinTitleScreen(ITextComponent component)
    {
        super(component);
    }

    @Inject(method="init", at=@At(value="RETURN"))
    private void onInit(CallbackInfo ci)
    {
        Button singleplayerButton = (Button)this.buttons.get(0);
        Button multiplayerButton = (Button)this.buttons.get(1);
        Button realmsButton = (Button)this.buttons.get(2);
        Button modsButton = (Button)this.buttons.get(3);
        Button languageButton = (Button)this.buttons.get(4);
        Button optionsButton = (Button)this.buttons.get(5);
        Button quitButton = (Button)this.buttons.get(6);
        Button accessibilityButton = (Button)this.buttons.get(7);

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
