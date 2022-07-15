/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package adubbz.lockdown;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(value = Lockdown.MOD_ID)
public class Lockdown
{
    public static final String MOD_ID = "lockdown";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public Lockdown()
    {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.SPEC, "lockdown.toml");
    }
}
