/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery;

import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.common.CommonProxy;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.loading.DatagenModLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AstralSorcery
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
@Mod(AstralSorcery.MODID)
public class AstralSorcery {

    public static final String MODID = "astralsorcery";
    public static final String NAME = "Astral Sorcery";

    public static final Logger LOG = LogManager.getLogger(NAME);

    private static AstralSorcery instance;
    private final FMLJavaModLoadingContext loadingContext;
    private final CommonProxy proxy;

    public AstralSorcery(FMLJavaModLoadingContext loadingContext) {
        instance = this;
        this.loadingContext = loadingContext;

        this.proxy = DistExecutor.unsafeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
        this.proxy.init();
        this.proxy.initLifecycle(loadingContext.getModEventBus());
        this.proxy.initListeners(MinecraftForge.EVENT_BUS);
    }

    public static AstralSorcery getInstance() {
        return instance;
    }

    public static ModContainer getModContainer() {
        return instance.loadingContext.getContainer();
    }

    public FMLJavaModLoadingContext getLoadingContext() {
        return this.loadingContext;
    }

    public CommonProxy getProxy() {
        return this.proxy;
    }

    public static ResourceLocation key(String path) {
        return new ResourceLocation(AstralSorcery.MODID, path);
    }

    public static boolean isDoingDataGeneration() {
        return DatagenModLoader.isRunningDataGen();
    }

    public static void assertDataGeneration() {
        if (!isDoingDataGeneration()) {
            throw new IllegalStateException("This method may only be called during data generation!");
        }
    }
}
