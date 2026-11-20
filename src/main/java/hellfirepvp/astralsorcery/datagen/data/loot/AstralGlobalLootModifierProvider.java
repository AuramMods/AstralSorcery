/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.EnchantmentsAS;
import hellfirepvp.astralsorcery.common.loot.global.SmeltLootFunction;
import hellfirepvp.astralsorcery.common.loot.global.TeleportDropsFunction;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraftforge.common.loot.IGlobalLootModifier;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AstralGlobalLootModifierProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AstralGlobalLootModifierProvider implements DataProvider {

    private final PackOutput output;

    public AstralGlobalLootModifierProvider(PackOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        Path data = this.output.getOutputFolder(PackOutput.Target.DATA_PACK);
        Path modifiers = data.resolve(AstralSorcery.MODID).resolve("loot_modifiers");
        var smelt = SmeltLootFunction.of(MatchTool.toolMatches(ItemPredicate.Builder.item()
                .hasEnchantment(new EnchantmentPredicate(EnchantmentsAS.SCORCHING_HEAT.get(),
                        MinMaxBounds.Ints.atLeast(1)))).build());

        // Keep the baseline order. Teleporting a drop removes it before smelting can process it.
        JsonArray entries = new JsonArray();
        entries.add(AstralSorcery.key("teleport_drops").toString());
        entries.add(AstralSorcery.key("smelt_loot").toString());
        JsonObject index = new JsonObject();
        index.add("entries", entries);
        index.addProperty("replace", false);

        return CompletableFuture.allOf(
                saveModifier(cache, modifiers.resolve("teleport_drops.json"), TeleportDropsFunction.of()),
                saveModifier(cache, modifiers.resolve("smelt_loot.json"), smelt),
                DataProvider.saveStable(cache, index, data.resolve("forge/loot_modifiers/global_loot_modifiers.json")));
    }

    private static CompletableFuture<?> saveModifier(CachedOutput cache, Path file, IGlobalLootModifier modifier) {
        var json = IGlobalLootModifier.DIRECT_CODEC.encodeStart(JsonOps.INSTANCE, modifier)
                .getOrThrow(false, message -> {});
        return DataProvider.saveStable(cache, json, file);
    }

    @Override
    public String getName() {
        return "Astral Sorcery global loot modifiers";
    }
}
