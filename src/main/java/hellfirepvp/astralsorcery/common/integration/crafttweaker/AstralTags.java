package hellfirepvp.astralsorcery.common.integration.crafttweaker;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.tag.CraftTweakerTagRegistry;
import com.blamejared.crafttweaker.api.tag.type.KnownTag;
import com.blamejared.crafttweaker.api.util.StringUtil;
import com.blamejared.crafttweaker_annotations.annotations.NativeTypeRegistration;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.crystal.CrystalProperty;
import hellfirepvp.astralsorcery.common.ingredient.CrystalPropertyTagIngredient;
import hellfirepvp.astralsorcery.common.ingredient.LumenTagIngredient;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Ingredient;
import org.openzen.zencode.java.ZenCodeType;

import java.util.Arrays;

@ZenRegister
@ZenCodeType.Name("mods.astralsorcery.AstralTags")
public final class AstralTags {

    private AstralTags() {}

    @ZenCodeType.Method
    public static Lumen lumen(String id) {
        return AstralRecipeManagers.registered(RegistriesAS.REGISTRY_LUMEN.get(), id);
    }

    @ZenCodeType.Method
    public static BaseConstellation constellation(String id) {
        return AstralRecipeManagers.registered(RegistriesAS.REGISTRY_CONSTELLATIONS.get(), id);
    }

    @ZenCodeType.Method
    public static CrystalProperty crystalProperty(String id) {
        return AstralRecipeManagers.registered(RegistriesAS.REGISTRY_CRYSTAL_PROPERTIES.get(), id);
    }

    @ZenCodeType.Method
    public static KnownTag<Lumen> lumenTag(String id) {
        return tag(RegistriesAS.KEY_LUMEN, id);
    }

    @ZenCodeType.Method
    public static KnownTag<BaseConstellation> constellationTag(String id) {
        return tag(RegistriesAS.KEY_CONSTELLATIONS, id);
    }

    @ZenCodeType.Method
    public static KnownTag<CrystalProperty> crystalPropertyTag(String id) {
        return tag(RegistriesAS.KEY_CRYSTAL_PROPERTIES, id);
    }

    @ZenCodeType.Method
    public static IIngredient lumenCrystal(KnownTag<Lumen> tag) {
        var ingredient = new LumenTagIngredient(TagKey.create(RegistriesAS.KEY_LUMEN, tag.id()));
        String command = "mods.astralsorcery.AstralTags.lumenCrystal(mods.astralsorcery.AstralTags.lumenTag("
                + StringUtil.quoteAndEscape(tag.id()) + "))";
        return new ScriptIngredient(ingredient.toVanilla(), command);
    }

    @ZenCodeType.Method
    public static IIngredient crystalPropertyIngredient(IIngredient input, KnownTag<CrystalProperty> tag,
                                                        @ZenCodeType.OptionalInt(1) int minimumTier) {
        var ingredient = new CrystalPropertyTagIngredient(input.asVanillaIngredient(),
                TagKey.create(RegistriesAS.KEY_CRYSTAL_PROPERTIES, tag.id()), minimumTier);
        String command = "mods.astralsorcery.AstralTags.crystalPropertyIngredient(" + input.getCommandString()
                + ", mods.astralsorcery.AstralTags.crystalPropertyTag(" + StringUtil.quoteAndEscape(tag.id())
                + "), " + minimumTier + ")";
        return new ScriptIngredient(ingredient.toVanilla(), command);
    }

    @ZenCodeType.Method
    public static void replaceLumens(KnownTag<Lumen> tag, Lumen... elements) {
        replace(tag, elements);
    }

    @ZenCodeType.Method
    public static void replaceConstellations(KnownTag<BaseConstellation> tag, BaseConstellation... elements) {
        replace(tag, elements);
    }

    @ZenCodeType.Method
    public static void replaceCrystalProperties(KnownTag<CrystalProperty> tag, CrystalProperty... elements) {
        replace(tag, elements);
    }

    @SafeVarargs
    private static <T> void replace(KnownTag<T> tag, T... elements) {
        if (tag.manager().exists(tag)) {
            tag.manager().clear(tag);
        }
        tag.add(elements);
    }

    private static <T> KnownTag<T> tag(ResourceKey<Registry<T>> key, String name) {
        return CraftTweakerTagRegistry.INSTANCE.knownTagManager(key).tag(AstralRecipeManager.exactId(name));
    }

    private record ScriptIngredient(Ingredient ingredient, String command) implements IIngredient {
        @Override
        public boolean matches(IItemStack stack, boolean ignoreDamage) {
            return this.ingredient.test(stack.getInternal());
        }

        @Override
        public Ingredient asVanillaIngredient() {
            return this.ingredient;
        }

        @Override
        public String getCommandString() {
            return this.command;
        }

        @Override
        public IItemStack[] getItems() {
            return Arrays.stream(this.ingredient.getItems()).map(IItemStack::of).toArray(IItemStack[]::new);
        }
    }

    @ZenRegister
    @NativeTypeRegistration(value = Lumen.class, zenCodeName = "mods.astralsorcery.Lumen")
    public static final class LumenType {
        @ZenCodeType.Getter("id")
        public static ResourceLocation id(Lumen lumen) {
            return RegistriesAS.REGISTRY_LUMEN.get().getKey(lumen);
        }
    }

    @ZenRegister
    @NativeTypeRegistration(value = BaseConstellation.class, zenCodeName = "mods.astralsorcery.Constellation")
    public static final class ConstellationType {
        @ZenCodeType.Getter("id")
        public static ResourceLocation id(BaseConstellation constellation) {
            return RegistriesAS.REGISTRY_CONSTELLATIONS.get().getKey(constellation);
        }
    }

    @ZenRegister
    @NativeTypeRegistration(value = CrystalProperty.class, zenCodeName = "mods.astralsorcery.CrystalProperty")
    public static final class CrystalPropertyType {
        @ZenCodeType.Getter("id")
        public static ResourceLocation id(CrystalProperty property) {
            return RegistriesAS.REGISTRY_CRYSTAL_PROPERTIES.get().getKey(property);
        }
    }
}
