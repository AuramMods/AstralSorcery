/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.research.tome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.client.screen.tome.page.RenderPage;
import hellfirepvp.astralsorcery.client.screen.tome.page.RenderPageStructure;
import hellfirepvp.astralsorcery.common.lib.ObserversAS;
import hellfirepvp.astralsorcery.common.lib.types.TomePageTypesAS;
import hellfirepvp.astralsorcery.common.research.ResearchNode;
import hellfirepvp.astralsorcery.common.util.data.ObserverRegistryObject;
import hellfirepvp.observerlib.api.ObserverProvider;
import hellfirepvp.observerlib.api.util.StructureBlockArray;
import hellfirepvp.observerlib.common.registry.RegistryProviders;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import hellfirepvp.astralsorcery.common.util.codec.ByteBufCodecs;
import hellfirepvp.astralsorcery.common.util.codec.StreamCodec;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TomePageStructure
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record TomePageStructure(ObserverProvider structureObserver, int structureIndex) implements TomePage {

    public static final MapCodec<TomePageStructure> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ObserversAS.PROVIDER_CODEC.fieldOf("structureObserver").forGetter(TomePageStructure::structureObserver),
            Codec.INT.fieldOf("structureIndex").forGetter(TomePageStructure::structureIndex)
    ).apply(inst, TomePageStructure::new));
    public static final StreamCodec<FriendlyByteBuf, TomePageStructure> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.RESOURCE_LOCATION.map(TomePageStructure::readProvider, ObserverProvider::getRegistryName),
            TomePageStructure::structureObserver,
            ByteBufCodecs.INT,
            TomePageStructure::structureIndex,
            TomePageStructure::new);

    private static ObserverProvider readProvider(ResourceLocation id) {
        ObserverProvider provider = RegistryProviders.getProvider(id);
        if (provider == null) {
            throw new DecoderException("Unknown observer provider " + id);
        }
        return provider;
    }

    public static TomePageStructure of(ObserverRegistryObject structureObject) {
        return of(structureObject, 0);
    }

    public static TomePageStructure of(ObserverRegistryObject structureObject, int structureIndex) {
        return new TomePageStructure(structureObject.observer().get(), structureIndex);
    }

    @Override
    public TomePageType<?> getType() {
        return TomePageTypesAS.STRUCTURE_PAGE.get();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public RenderPage createPage(@Nullable ResearchNode node, int page) {
        List<StructureBlockArray> structures = ObserverRegistryObject.extractRequiredStructures(this.structureObserver());
        if (structures.isEmpty()) return TomePageEmpty.getInstance().createPage(node, page);
        if (this.structureIndex() < 0 || this.structureIndex() >= structures.size()) return TomePageEmpty.getInstance().createPage(node, page);
        return Optional.ofNullable(structures.get(this.structureIndex()))
                .map(structure -> (RenderPage) new RenderPageStructure(node, page, structure))
                .orElse(TomePageEmpty.getInstance().createPage(node, page));
    }
}
