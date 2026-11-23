package hellfirepvp.astralsorcery.common.integration.crafttweaker;

import com.blamejared.crafttweaker.api.plugin.CraftTweakerPlugin;
import com.blamejared.crafttweaker.api.plugin.ICraftTweakerPlugin;
import com.blamejared.crafttweaker.api.plugin.ITaggableElementRegistrationHandler;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.crystal.CrystalProperty;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;

@CraftTweakerPlugin("astralsorcery:integration")
public final class AstralCraftTweakerPlugin implements ICraftTweakerPlugin {

    @Override
    public void registerTaggableElements(ITaggableElementRegistrationHandler handler) {
        handler.registerTaggableElement(RegistriesAS.KEY_LUMEN, Lumen.class);
        handler.registerTaggableElement(RegistriesAS.KEY_CONSTELLATIONS, BaseConstellation.class);
        handler.registerTaggableElement(RegistriesAS.KEY_CRYSTAL_PROPERTIES, CrystalProperty.class);
    }
}
