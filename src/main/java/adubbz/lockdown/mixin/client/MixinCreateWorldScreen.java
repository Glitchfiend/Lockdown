/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package adubbz.lockdown.mixin.client;

import adubbz.lockdown.Config;
import adubbz.lockdown.Lockdown;
import com.mojang.serialization.Lifecycle;
import net.minecraft.client.gui.screen.CreateWorldScreen;
import net.minecraft.client.gui.screen.DirtMessageScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.WorldOptionsScreen;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.codec.DatapackCodec;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraft.world.storage.IServerConfiguration;
import net.minecraft.world.storage.SaveFormat;
import net.minecraft.world.storage.ServerWorldInfo;
import org.apache.commons.io.FileUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.IOException;

@Mixin(CreateWorldScreen.class)
public abstract class MixinCreateWorldScreen extends Screen
{
    @Shadow
    private TextFieldWidget nameEdit;

    @Shadow
    private CreateWorldScreen.GameMode gameMode;

    @Shadow
    private Difficulty effectiveDifficulty;

    @Shadow
    private boolean commands;

    @Shadow
    private String initName;

    @Shadow
    private GameRules gameRules;

    @Shadow
    public boolean hardCore;

    @Shadow
    protected DatapackCodec dataPacks;

    @Shadow
    private Button modeButton;
    @Shadow
    private Button difficultyButton;
    @Shadow
    private Button commandsButton;

    @Shadow
    private Button moreOptionsButton;
    @Shadow
    private Button gameRulesButton;
    @Shadow
    private Button dataPacksButton;

    @Shadow
    private ITextComponent gameModeHelp1;
    @Shadow
    private ITextComponent gameModeHelp2;

    @Shadow
    @Final
    public WorldOptionsScreen worldGenSettingsComponent;

    protected MixinCreateWorldScreen(ITextComponent component)
    {
        super(component);
    }

    @Inject(method="init", at=@At(value="RETURN"))
    private void onInit(CallbackInfo ci)
    {
        if (Config.disableGameMode.get())
        {
            this.modeButton.visible = false;
            this.gameModeHelp1 = new TranslationTextComponent("");
            this.gameModeHelp2 = new TranslationTextComponent("");
        }

        if (Config.disableCheats.get())
        {
            this.commandsButton.visible = false;
        }

        if (Config.disableGameRules.get())
        {
            this.gameRulesButton.visible = false;
        }

        if (Config.disableDifficulty.get())
        {
            this.difficultyButton.visible = false;
        }

        if (Config.disableDataPacks.get())
        {
            this.dataPacksButton.visible = false;
        }

        if (Config.disableMoreWorldOptions.get())
        {
            this.moreOptionsButton.visible = false;
        }
    }

    @Inject(method="onCreate", at=@At(value="HEAD"), cancellable = true)
    private void onCreate(CallbackInfo ci)
    {
        if (Config.useTemplate.get())
        {
            File gameDirectory = this.minecraft.gameDirectory.getAbsoluteFile();
            File templateDirectory = new File(gameDirectory + File.separator + Config.templateDirectory.get());

            // Queue the load screen whilst we copy the template
            this.queueLoadScreen();

            // Copy the world
            try
            {
                FileUtils.copyDirectory(templateDirectory, new File(gameDirectory + File.separator + "saves" + File.separator + this.initName));
            }
            catch (IOException e)
            {
                Lockdown.LOGGER.error("The template world does not exist at " + templateDirectory, e);
                return;
            }

            try
            {
                SaveFormat.LevelSave storageAccess = this.minecraft.getLevelSource().createAccess(this.initName);

                // Rename the level for our new name
                storageAccess.renameLevel(this.initName);

                // Replace the world data if needed
                if (!Config.useTemplateWorldSettings.get())
                {
                    Lockdown.LOGGER.info("Replacing world data...");

                    // Create the new world data
                    DimensionGeneratorSettings worldGenSettings = this.worldGenSettingsComponent.makeSettings(this.hardCore);
                    WorldSettings levelSettings = this.createLevelSettings(worldGenSettings.isDebug());
                    IServerConfiguration newWorldData = new ServerWorldInfo(levelSettings, worldGenSettings, Lifecycle.stable());

                    // Save the new world data
                    storageAccess.saveDataTag(this.worldGenSettingsComponent.registryHolder(), newWorldData);
                }

                // Close the storage access
                storageAccess.close();
            }
            catch (IOException e)
            {
                SystemToast.onWorldAccessFailure(this.minecraft, this.initName);
                Lockdown.LOGGER.error("Failed to rename level {}", this.initName, e);
            }

            // Load the level
            this.minecraft.loadLevel(this.initName);

            // Cancel to prevent normal world creation
            ci.cancel();
        }
    }

    private WorldSettings createLevelSettings(boolean isDebug)
    {
        String s = this.nameEdit.getValue().trim();
        if (isDebug) {
            GameRules gamerules = new GameRules();
            gamerules.getRule(GameRules.RULE_DAYLIGHT).set(false, (MinecraftServer)null);
            return new WorldSettings(s, GameType.SPECTATOR, false, Difficulty.PEACEFUL, true, gamerules, DatapackCodec.DEFAULT);
        } else {
            return new WorldSettings(s, this.gameMode.gameType, this.hardCore, this.effectiveDifficulty, this.commands && !this.hardCore, this.gameRules, this.dataPacks);
        }
    }

    private void queueLoadScreen()
    {
        this.minecraft.forceSetScreen(new DirtMessageScreen(new TranslationTextComponent("selectWorld.data_read")));
    }
}
