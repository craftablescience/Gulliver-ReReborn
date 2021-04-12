/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package com.camellias.gulliverreborn.asreachpatch;

import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.IFMLCallHook;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AstralCore
 * Created by HellFirePvP
 * Date: 07.05.2016 / 02:55
 */
@IFMLLoadingPlugin.Name(value = "GulliverRebornCore")
@IFMLLoadingPlugin.TransformerExclusions({"com.camellias.gulliverreborn.asreachpatch"})
@IFMLLoadingPlugin.SortingIndex(1005)
public class AstralCore implements IFMLLoadingPlugin, IFMLCallHook {

    public static final Logger log = LogManager.getLogger("GulliverRebornCore");

    public static Side side;

    public AstralCore() {
        log.info("[GulliverRebornCore] Initialized.");
        side = FMLLaunchHandler.side();
    }

    @Override
    public Void call() throws Exception {
        return null;
    }

    @Override
    public String[] getASMTransformerClass() {
        return null;
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
    }

    @Override
    public String getAccessTransformerClass() {
        return "com.camellias.gulliverreborn.asreachpatch.AstralTransformer";
    }

}
