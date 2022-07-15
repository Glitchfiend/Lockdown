/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package adubbz.lockdown.mixin.client;

import adubbz.lockdown.Config;
import adubbz.lockdown.Lockdown;
import com.mojang.serialization.Lifecycle;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.client.gui.screens.worldselection.WorldGenSettingsComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.WorldData;
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
    private String initName;
    @Shadow
    public boolean hardCore;

    @Shadow
    private CycleButton modeButton;
    @Shadow
    private CycleButton<Difficulty> difficultyButton;
    @Shadow
    private CycleButton<Boolean> commandsButton;

    @Shadow
    private Button moreOptionsButton;
    @Shadow
    private Button gameRulesButton;
    @Shadow
    private Button dataPacksButton;

    @Shadow
    private Component gameModeHelp1;
    @Shadow
    private Component gameModeHelp2;

    @Shadow
    @Final
    public WorldGenSettingsComponent worldGenSettingsComponent;

    protected MixinCreateWorldScreen(Component component)
    {
        super(component);
    }

    @Inject(method="init", at=@At(value="RETURN"))
    private void onInit(CallbackInfo ci)
    {
        if (Config.disableGameMode.get())
        {
            this.modeButton.visible = false;
            this.gameModeHelp1 = Component.translatable("");
            this.gameModeHelp2 = Component.translatable("");
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
                LevelStorageSource.LevelStorageAccess storageAccess = this.minecraft.getLevelSource().createAccess(this.initName);

                // Rename the level for our new name
                storageAccess.renameLevel(this.initName);

                // Replace the world data if needed
                if (!Config.useTemplateWorldSettings.get())
                {
                    Lockdown.LOGGER.info("Replacing world data...");

                    // Create the new world data
                    WorldCreationContext worldCreationContext = this.worldGenSettingsComponent.createFinalSettings(this.hardCore);
                    LevelSettings levelSettings = this.createLevelSettings(worldCreationContext.worldGenSettings().isDebug());
                    WorldData newWorldData = new PrimaryLevelData(levelSettings, worldCreationContext.worldGenSettings(), worldCreationContext.worldSettingsStability());

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
            this.minecraft.createWorldOpenFlows().loadLevel(this, this.initName);

            // Cancel to prevent normal world creation
            ci.cancel();
        }
    }

    @Shadow
    abstract LevelSettings createLevelSettings(boolean isDebug);

    private void queueLoadScreen()
    {
        this.minecraft.forceSetScreen(new GenericDirtMessageScreen(Component.translatable("selectWorld.data_read")));
    }
}
