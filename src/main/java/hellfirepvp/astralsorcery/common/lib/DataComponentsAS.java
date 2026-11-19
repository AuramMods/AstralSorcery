/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.EnumExtensions;
import hellfirepvp.astralsorcery.common.component.*;
import hellfirepvp.astralsorcery.common.util.codec.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;
import io.netty.handler.codec.DecoderException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Rarity;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class DataComponentsAS {

    private static final Map<ResourceLocation, StackDataType<?>> TYPES = new LinkedHashMap<>();

    public static final Codec<StackDataType<?>> TYPE_CODEC = ResourceLocation.CODEC.comapFlatMap(id -> {
        StackDataType<?> type = TYPES.get(id);
        return type == null ? DataResult.error(() -> "Unknown Astral item data " + id) : DataResult.success(type);
    }, StackDataType::id);
    public static final StreamCodec<FriendlyByteBuf, StackDataType<?>> TYPE_STREAM_CODEC =
            StreamCodec.of((buffer, type) -> buffer.writeResourceLocation(type.id()), buffer -> {
                ResourceLocation id = buffer.readResourceLocation();
                StackDataType<?> type = TYPES.get(id);
                if (type == null) {
                    throw new DecoderException("Unknown Astral item data " + id);
                }
                return type;
            });

    public static final StackDataType<CrystalAttributesComponent> CRYSTAL_ATTRIBUTES = register("crystal_attributes", () -> CrystalAttributesComponent.CODEC);
    public static final StackDataType<AstrolabeAngleComponent> ASTROLABE_ANGLE = register("astrolabe_angle", () -> AstrolabeAngleComponent.CODEC);
    public static final StackDataType<ArtifactComponent> ARTIFACT = register("artifact", () -> ArtifactComponent.CODEC);
    public static final StackDataType<ArtifactTypeComponent> ARTIFACT_TYPE = register("artifact_type", () -> ArtifactTypeComponent.CODEC);
    public static final StackDataType<ConstellationPaperComponent> CONSTELLATION_PAPER = register("constellation_paper", () -> ConstellationPaperComponent.CODEC);
    public static final StackDataType<AttunedConstellationComponent> ATTUNED_CONSTELLATION = register("attuned_constellation", () -> AttunedConstellationComponent.CODEC);
    public static final StackDataType<LumenComponent> LUMEN = register("lumen", () -> LumenComponent.CODEC);
    public static final StackDataType<StoredLumenComponent> STORED_LUMEN = register("stored_lumen", () -> StoredLumenComponent.CODEC);
    public static final StackDataType<IdentifierComponent> IDENTIFIER = register("identifier", () -> IdentifierComponent.CODEC);
    public static final StackDataType<ColorComponent> COLOR = register("color", () -> ColorComponent.CODEC);
    public static final StackDataType<DynamicModifiersComponent> DYNAMIC_MODIFIERS = register("dynamic_modifiers", () -> DynamicModifiersComponent.CODEC);
    public static final StackDataType<EnchantmentModifierComponent> ENCHANTMENT_MODIFIERS = register("enchantment_modifiers", () -> EnchantmentModifierComponent.CODEC);
    public static final StackDataType<BlockStateStorageComponent> BLOCK_STATE_STORAGE = register("block_state_storage", () -> BlockStateStorageComponent.CODEC);
    public static final StackDataType<StoredItemsComponent> STORED_ITEMS = register("stored_items", () -> StoredItemsComponent.CODEC);
    public static final StackDataType<IntegerModeComponent> MODE = register("mode", () -> IntegerModeComponent.CODEC);
    public static final StackDataType<FlagsComponent> FLAGS = register("flags", () -> FlagsComponent.CODEC);
    public static final StackDataType<StoredPlayerProgressComponent> STORED_PLAYER_PROGRESS = register("stored_player_progress", () -> StoredPlayerProgressComponent.CODEC);
    public static final StackDataType<VeiledItemStackComponent> VEILED = register("veiled_item_stack", () -> VeiledItemStackComponent.CODEC);


    public static final StackDataType<Rarity> RARITY =
            register(new ResourceLocation("minecraft", "rarity"), () -> EnumExtensions.CODEC);

    private DataComponentsAS() {}

    public static Collection<StackDataType<?>> values() {
        return Collections.unmodifiableCollection(TYPES.values());
    }

    private static <T> StackDataType<T> register(String name, Supplier<Codec<T>> codec) {
        return register(AstralSorcery.key(name), codec);
    }

    private static <T> StackDataType<T> register(ResourceLocation id, Supplier<Codec<T>> codec) {
        StackDataType<T> type = new StackDataType<>(id, ExtraCodecs.lazyInitializedCodec(codec));
        TYPES.put(id, type);
        return type;
    }
}
