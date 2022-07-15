/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package adubbz.lockdown;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config
{
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static ForgeConfigSpec.ConfigValue<String> templateDirectory;
    public static ForgeConfigSpec.BooleanValue useTemplate;
    public static ForgeConfigSpec.BooleanValue useTemplateWorldSettings;

    public static ForgeConfigSpec.BooleanValue disableGameMode;
    public static ForgeConfigSpec.BooleanValue disableCheats;
    public static ForgeConfigSpec.BooleanValue disableGameRules;
    public static ForgeConfigSpec.BooleanValue disableDifficulty;
    public static ForgeConfigSpec.BooleanValue disableDataPacks;
    public static ForgeConfigSpec.BooleanValue disableMoreWorldOptions;
    public static ForgeConfigSpec.BooleanValue disableMultiplayer;
    public static ForgeConfigSpec.BooleanValue disableSingleplayer;

    static
    {
        BUILDER.push("world_creation_settings");
        templateDirectory = BUILDER.comment("The directory of the world template").define("template_directory", "template");
        useTemplate = BUILDER.comment("Whether a template should be used instead of creating regular worlds").define("use_template", true);
        useTemplateWorldSettings = BUILDER.comment("Whether to use the world settings from the template and ignore player changes").define("use_template_world_settings", true);
        BUILDER.pop();

        BUILDER.push("world_creation_menu_settings");
        disableGameMode = BUILDER.comment("Toggle the game mode button").define("disable_game_mode", true);
        disableCheats = BUILDER.comment("Toggle the cheats button").define("disable_cheats", true);
        disableGameRules = BUILDER.comment("Toggle the game rules button").define("disable_game_rules", true);
        disableDifficulty = BUILDER.comment("Toggle the difficulty button").define("disable_difficulty", true);
        disableDataPacks = BUILDER.comment("Toggle the data packs button").define("disable_data_packs", true);
        disableMoreWorldOptions = BUILDER.comment("Toggle the more world options button").define("disable_more_world_options", true);
        BUILDER.pop();

        BUILDER.push("main_menu_settings");
        disableMultiplayer = BUILDER.comment("Toggle the multiplayer button").define("disable_multiplayer", true);
        disableSingleplayer = BUILDER.comment("Toggle the singleplayer button").define("disable_singleplayer", false);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
