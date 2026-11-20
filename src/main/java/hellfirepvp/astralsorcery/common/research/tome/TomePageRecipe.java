/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.research.tome;

import hellfirepvp.astralsorcery.common.util.codec.ByteBufCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.client.screen.tome.page.RenderPage;
import hellfirepvp.astralsorcery.client.screen.tome.page.RenderPageRecipe;
import hellfirepvp.astralsorcery.common.lib.types.TomePageTypesAS;
import hellfirepvp.astralsorcery.common.research.ResearchNode;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import hellfirepvp.astralsorcery.common.util.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TomePageRecipe
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record TomePageRecipe(ResourceKey<RecipeType<?>> type, ResourceLocation recipeId) implements TomePage {

    //TODO modify to allow for dynamic resolving by output or id
    public static final MapCodec<TomePageRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ResourceKey.codec(Registries.RECIPE_TYPE).fieldOf("recipe_type").forGetter(TomePageRecipe::type),
            ResourceLocation.CODEC.fieldOf("recipeId").forGetter(TomePageRecipe::recipeId)
    ).apply(inst, TomePageRecipe::new));
    public static final StreamCodec<FriendlyByteBuf, TomePageRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.resourceKey(Registries.RECIPE_TYPE),
            TomePageRecipe::type,
            ByteBufCodecs.RESOURCE_LOCATION,
            TomePageRecipe::recipeId,
            TomePageRecipe::new);

    private static final TomePageText NOT_FOUND_PAGE = new TomePageText("tome.research.info.recipe_error");

    public static TomePageText getNotFoundPage() {
        return NOT_FOUND_PAGE;
    }

    public static TomePageRecipe of(RegistryObject<? extends RecipeType<?>> type, ResourceLocation recipeId) {
        return of(ResourceKey.create(Registries.RECIPE_TYPE, type.getId()), recipeId);
    }

    public static TomePageRecipe of(ResourceKey<RecipeType<?>> type, ResourceLocation recipeId) {
        return new TomePageRecipe(type, recipeId);
    }

    @Override
    public TomePageType<?> getType() {
        return TomePageTypesAS.RECIPE_PAGE.get();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public RenderPage createPage(@Nullable ResearchNode node, int page) {
        return RenderPageRecipe.getFactoryForType(this.type())
                .<RenderPage>map(factory -> factory.create(node, page, this.recipeId()))
                .orElseGet(() -> getNotFoundPage().createPage(node, page));
    }
}
