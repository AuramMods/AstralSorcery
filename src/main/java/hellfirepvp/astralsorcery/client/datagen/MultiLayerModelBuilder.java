/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.datagen;

import hellfirepvp.astralsorcery.common.util.data.RegisteredItemLike;
import hellfirepvp.astralsorcery.datagen.assets.AstralBlockStateProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: MultiLayerModelBuilder
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class MultiLayerModelBuilder {

    private final MultiPartBlockStateBuilder builder;
    private final RegisteredItemLike<? extends Block> block;

    protected MultiLayerModelBuilder(BlockStateProvider provider, RegisteredItemLike<? extends Block> block) {
        this.block = block;
        this.builder = provider.getMultipartBuilder(this.block.get());
    }

    public static MultiLayerModelBuilder makeState(BlockStateProvider provider, RegisteredItemLike<? extends Block> block) {
        return new MultiLayerModelBuilder(provider, block);
    }

    public static ResourceLocation getMultiLayerModelId(RegisteredItemLike<? extends Block> block) {
        ResourceLocation id = block.getId();
        return id.withPrefix("block/").withSuffix("/" + id.getPath());
    }

    public static ItemModelBuilder makeCombinedItemModel(ItemModelProvider provider, RegisteredItemLike<? extends Block> block) {
        return provider.withExistingParent(block.getId().toString(), getMultiLayerModelId(block).withSuffix("_combined"));
    }

    public MultiLayerModelBuilder addLayer(RenderType layer) {
        ResourceLocation id = this.block.getId();
        this.builder.part().modelFile(AstralBlockStateProvider.model(id.withSuffix("/" + id.getPath() + "_" + layer.name))).addModel();
        return this;
    }
}
