/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.reload.RegistryJsonDataLoader;
import hellfirepvp.astralsorcery.common.util.data.MapStream;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PerkTreeLoader
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PerkTreeLoader extends RegistryJsonDataLoader {

    private static final PerkTreeLoader INSTANCE = new PerkTreeLoader();

    private PerkTreeLoader() {
        super("perks");
    }

    public static PerkTreeLoader getInstance() {
        return INSTANCE;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> dataMap, RegistryAccess registries) {
        List<JsonObject> perkObjects = MapStream.of(dataMap)
                .filterValue(JsonElement::isJsonObject)
                .mapValue(JsonElement::getAsJsonObject)
                .valueStream()
                .toList();

        AstralSorcery.LOG.info("Loading perk tree with {} perks.", perkObjects.size());
        PerkTreeData data = PerkTreeData.load(perkObjects, registries);
        PerkTree.getInstance().updateOriginPerkTree(data);
    }
}
