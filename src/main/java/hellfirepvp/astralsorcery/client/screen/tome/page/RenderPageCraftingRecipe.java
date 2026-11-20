/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.tome.page;

import hellfirepvp.astralsorcery.common.recipe.RecipeHolder;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.research.ResearchNode;
import hellfirepvp.astralsorcery.common.research.tome.TomePage;
import hellfirepvp.astralsorcery.common.util.IngredientUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RenderPageCraftingRecipe
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RenderPageCraftingRecipe extends RenderPageRecipe<CraftingRecipe> {

    private static final AbstractContainerMenu PREVIEW_MENU = new AbstractContainerMenu(null, 0) {
        @Override
        public ItemStack quickMoveStack(Player player, int slot) {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean stillValid(Player player) {
            return false;
        }
    };

    public RenderPageCraftingRecipe(@Nullable ResearchNode node, int nodePage, ResourceLocation recipeId) {
        super(node, nodePage, () -> RecipeType.CRAFTING, recipeId);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int x, int y, float pTicks, float mouseX, float mouseY) {
        this.clearFrame();

        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection == null) return;
        RegistryAccess registries = connection.registryAccess();

        this.resolveRecipeOrWriteError(guiGraphics, x, y).map(RecipeHolder::value).ifPresent(recipe -> {
            if (recipe instanceof ShapedRecipe shapedRecipe) {
                this.renderCraftingRecipe(guiGraphics, x, y, registries, shapedRecipe, shapedRecipe.getWidth(), shapedRecipe.getHeight());
            } else if (recipe instanceof ShapelessRecipe shapelessRecipe) {
                this.renderCraftingRecipe(guiGraphics, x, y, registries, shapelessRecipe, 3, 3);
            } else {
                ResourceLocation id = BuiltInRegistries.RECIPE_TYPE.getKey(recipe.getType());
                id = id == null ? new ResourceLocation("minecraft", "unregistered_sadface") : id;
                Component errorDisplay = Component.translatable("tome.research.info.recipe_cannot_display", id.toString());

                Font font = Minecraft.getInstance().font;
                List<FormattedCharSequence> lines = new ArrayList<>(font.split(errorDisplay, TomePage.DEFAULT_WIDTH));

                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(x, y, 0);
                for (FormattedCharSequence text : lines) {
                    guiGraphics.drawString(font, text, 0, 0, ColorsAS.TOME_TEXT_COLOR.getColor(), true);
                    guiGraphics.pose().translate(0, 10, 0);
                }
                guiGraphics.pose().popPose();
            }

            this.renderEffects(guiGraphics, pTicks);
            this.renderHoverTooltips(guiGraphics, mouseX, mouseY);
        });
    }

    private void renderCraftingRecipe(GuiGraphics graphics, int x, int y, RegistryAccess registries, CraftingRecipe recipe,
                                      int width, int height) {
        this.renderPageOverlay(graphics, x, y, TexturesAS.SCREEN_TOME_PAGE_GRID_CRAFTING);

        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        CraftingContainer input = createPreviewInput(ingredients, width, height);
        ItemStack result = recipe.assemble(input, registries);

        int midX = x + TomePage.DEFAULT_WIDTH / 2;
        int resultY = y + 18 + 60 / 2;
        this.renderOutput(graphics, midX - 8, resultY - 8, 16, 16, result);
        this.renderInputGrid(graphics, x, y, width, height, ingredients);
    }

    private static CraftingContainer createPreviewInput(NonNullList<Ingredient> ingredients, int width, int height) {
        NonNullList<ItemStack> stacks = NonNullList.withSize(width * height, ItemStack.EMPTY);
        for (int index = 0; index < ingredients.size(); index++) {
            stacks.set(index, IngredientUtil.getRandomDisplayStack(ingredients.get(index), 0).copy());
        }
        return new TransientCraftingContainer(PREVIEW_MENU, width, height, stacks);
    }

    private void renderInputGrid(GuiGraphics guiGraphics, int x, int y, int width, int height, NonNullList<Ingredient> inputPattern) {
        int centerOffsetX = x + 61;
        int centerOffsetY = y + 118;

        for (int xx = 0; xx < width; xx++) {
            for (int yy = 0; yy < height; yy++) {
                int index = xx + yy * width;
                if (inputPattern.size() <= index) continue;

                Ingredient input = inputPattern.get(index);
                if (!input.isEmpty()) {
                    int slotX = centerOffsetX + xx * 18;
                    int slotY = centerOffsetY + yy * 18;
                    this.renderInput(guiGraphics, slotX, slotY, input);
                }
            }
        }
    }
}
