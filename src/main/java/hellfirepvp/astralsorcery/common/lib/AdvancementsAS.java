/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.common.advancement.ConstellationDiscoveryTrigger;
import hellfirepvp.astralsorcery.common.advancement.FocalPointDiscoveryTrigger;
import hellfirepvp.astralsorcery.common.advancement.GemSocketTrigger;
import hellfirepvp.astralsorcery.common.advancement.ItemAttunementTrigger;
import hellfirepvp.astralsorcery.common.advancement.LumenDiscoveryTrigger;
import hellfirepvp.astralsorcery.common.advancement.PerkLevelTrigger;
import hellfirepvp.astralsorcery.common.advancement.PlayerAttunementTrigger;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AdvancementsAS
 * Created by HellFirePvP
 * Date: 07.10.2026 / 23:38
 */
public class AdvancementsAS {

    public static final PerkLevelTrigger PERK_LEVEL = new PerkLevelTrigger();
    public static final PlayerAttunementTrigger PLAYER_ATTUNEMENT = new PlayerAttunementTrigger();
    public static final ItemAttunementTrigger ITEM_ATTUNEMENT = new ItemAttunementTrigger();
    public static final ConstellationDiscoveryTrigger CONSTELLATION_DISCOVERY = new ConstellationDiscoveryTrigger();
    public static final LumenDiscoveryTrigger LUMEN_DISCOVERY = new LumenDiscoveryTrigger();
    public static final FocalPointDiscoveryTrigger FOCAL_POINT_DISCOVERY = new FocalPointDiscoveryTrigger();
    public static final GemSocketTrigger GEM_SOCKET = new GemSocketTrigger();

    public static void register(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            CriteriaTriggers.register(PERK_LEVEL);
            CriteriaTriggers.register(PLAYER_ATTUNEMENT);
            CriteriaTriggers.register(ITEM_ATTUNEMENT);
            CriteriaTriggers.register(CONSTELLATION_DISCOVERY);
            CriteriaTriggers.register(LUMEN_DISCOVERY);
            CriteriaTriggers.register(FOCAL_POINT_DISCOVERY);
            CriteriaTriggers.register(GEM_SOCKET);
        });
    }
}
