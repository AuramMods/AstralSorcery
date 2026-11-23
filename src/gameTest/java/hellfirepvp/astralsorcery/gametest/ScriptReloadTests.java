package hellfirepvp.astralsorcery.gametest;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.item.LumenCrystalItem;
import hellfirepvp.astralsorcery.common.lib.LumenAS;
import hellfirepvp.astralsorcery.common.recipe.RecipeHolder;
import hellfirepvp.astralsorcery.common.recipe.StageRules;
import hellfirepvp.astralsorcery.common.recipe.infusion.InfusionRecipe;
import hellfirepvp.astralsorcery.common.recipe.lightwell.LightwellRecipe;
import hellfirepvp.astralsorcery.common.util.data.LazyRecipeHolder;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletionException;

@GameTestHolder(AstralSorcery.MODID)
@PrefixGameTestTemplate(false)
public final class ScriptReloadTests {

    @GameTest(template = "gametest/empty", batch = "astral_reload", timeoutTicks = 800)
    public static void scriptReloadChangesThenRestoresTagsRulesAndRecipes(GameTestHelper test) throws IOException {
        if (!ModList.get().isLoaded("crafttweaker")) {
            test.succeed();
            return;
        }
        var level = test.getLevel();
        var server = level.getServer();
        var originalRecipes = level.getRecipeManager();
        var infusionId = ResourceLocation.tryParse("astraltest:infusion");
        var ruleId = ResourceLocation.tryParse("astraltest:lightwell_recipe");
        var infusion = (InfusionRecipe) originalRecipes.byKey(infusionId).orElseThrow();
        var activeReference = LazyRecipeHolder.of(RecipeHolder.of(infusion));
        boolean hasStages = ModList.get().isLoaded("gamestages");
        var tempScript = Files.createTempFile(Path.of("scripts"), "zz_as_reload_", ".zs");
        Files.writeString(tempScript, """
                mods.astralsorcery.AstralTags.replaceLumens(
                    mods.astralsorcery.AstralTags.lumenTag("astraltest:light"),
                    mods.astralsorcery.AstralTags.lumen("astralsorcery:caldor"));
                <recipetype:astralsorcery:infusion>.removeByName("astraltest:infusion");
                """ + (hasStages ? "mods.astralsorcery.AstralStages.remove(\"astraltest:lightwell_recipe\");\n" : ""));
        var packs = server.getPackRepository().getSelectedIds();
        var roundTrip = server.reloadResources(packs).handleAsync((ignored, error) -> {
            try {
                Files.deleteIfExists(tempScript);
            } catch (IOException cleanupError) {
                throw new CompletionException(cleanupError);
            }
            if (error != null) throw new CompletionException(error);
            var current = level.getRecipeManager();
            return current != originalRecipes && current.byKey(infusionId).isEmpty()
                    && activeReference.resolveRecipe(level) == null
                    && taggedRecipeMatchesCaldor(test)
                    && !StageRules.configured(current).containsKey(ruleId);
        }, server).thenComposeAsync(changed -> server.reloadResources(packs).thenApply(ignored -> changed), server);

        test.succeedWhen(() -> {
            test.assertTrue(roundTrip.isDone(), "Waiting for two real server resource reloads");
            test.assertTrue(roundTrip.join(), "The first reload must change the actual tag, remove the recipe and remove the stage rule");
            test.assertTrue(activeReference.resolveRecipe(level) != null && !taggedRecipeMatchesCaldor(test),
                    "Reloading without the temporary script must restore the original recipe and tag");
            test.assertTrue(StageRules.configured(level.getRecipeManager()).containsKey(ruleId) == hasStages,
                    "Reloading must restore the ZenCode stage rule when GameStages is installed");
        });
    }

    private static boolean taggedRecipeMatchesCaldor(GameTestHelper test) {
        var recipe = (LightwellRecipe) test.getLevel().getRecipeManager()
                .byKey(ResourceLocation.tryParse("crafttweaker:lumen_tag")).orElseThrow();
        var crystal = LumenCrystalItem.getCrystal(Holder.direct(LumenAS.CALDOR.get()));
        return recipe.getInput().test(crystal);
    }
}
