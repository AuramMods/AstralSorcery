/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.loot;

import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AstralLootTableProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AstralLootTableProvider extends LootTableProvider {

    public AstralLootTableProvider(PackOutput output) {
        super(output, Set.of(), List.of(
                new SubProviderEntry(AstralBlockLootTableProvider::new, LootContextParamSets.BLOCK),
                new SubProviderEntry(() -> AstralGameplayLootTableProvider::generateShootingStar, LootContextParamSets.SELECTOR),
                new SubProviderEntry(() -> AstralGameplayLootTableProvider::generateShrineChests, LootContextParamSets.CHEST),
                new SubProviderEntry(() -> AstralGameplayLootTableProvider::generateDigSite, LootContextParamSets.ARCHAEOLOGY)
        ));
    }
}
