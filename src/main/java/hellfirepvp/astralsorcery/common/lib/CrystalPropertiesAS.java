/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.crystal.CrystalProperty;
import hellfirepvp.astralsorcery.common.crystal.CrystalPropertyConstellation;
import hellfirepvp.astralsorcery.common.crystal.CrystalPropertyLumen;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import net.minecraft.ChatFormatting;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CrystalPropertiesAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CrystalPropertiesAS {

    public static final DeferredRegister<CrystalProperty> CRYSTAL_PROPERTY_REGISTER =
            DeferredRegister.create(RegistriesAS.KEY_CRYSTAL_PROPERTIES, AstralSorcery.MODID);

    private static final List<RegistryObject<CrystalPropertyConstellation>> CONSTELLATION_PROPERTIES = new ArrayList<>();
    private static final List<RegistryObject<CrystalPropertyLumen>> LUMEN_PROPERTIES = new ArrayList<>();

    public static final RegistryObject<CrystalProperty> SIZE =
            CRYSTAL_PROPERTY_REGISTER.register("size", () -> new CrystalProperty(ChatFormatting.GRAY, 8));
    public static final RegistryObject<CrystalProperty> PURITY =
            CRYSTAL_PROPERTY_REGISTER.register("purity", () -> new CrystalProperty(ChatFormatting.GRAY, 4));
    public static final RegistryObject<CrystalProperty> CUT =
            CRYSTAL_PROPERTY_REGISTER.register("cut", () -> new CrystalProperty(ChatFormatting.GRAY, 4));

    public static final RegistryObject<CrystalProperty> TOOL_EFFICIENCY =
            CRYSTAL_PROPERTY_REGISTER.register("tool_efficiency", () -> new CrystalProperty(ChatFormatting.GRAY, 3));
    public static final RegistryObject<CrystalProperty> TOOL_DURABILITY =
            CRYSTAL_PROPERTY_REGISTER.register("tool_durability", () -> new CrystalProperty(ChatFormatting.GRAY, 3));
    public static final RegistryObject<CrystalProperty> STARLIGHT_FOCUS =
            CRYSTAL_PROPERTY_REGISTER.register("starlight_focus", () -> new CrystalProperty(ChatFormatting.GRAY, 4));
    public static final RegistryObject<CrystalProperty> LUMEN_CHANNELING =
            CRYSTAL_PROPERTY_REGISTER.register("lumen_channeling", () -> new CrystalProperty(ChatFormatting.GRAY, 4));

    public static Optional<RegistryObject<CrystalPropertyConstellation>> getProperty(BaseConstellation cst) {
        return CONSTELLATION_PROPERTIES.stream()
                .filter(RegistryObject::isPresent)
                .filter(holder -> holder.get().getConstellation().equals(cst))
                .findFirst();
    }

    public static Optional<RegistryObject<CrystalPropertyLumen>> getProperty(Lumen lumen) {
        return LUMEN_PROPERTIES.stream()
                .filter(RegistryObject::isPresent)
                .filter(holder -> holder.get().getLumen().equals(lumen))
                .findFirst();
    }

    static {
        ConstellationsAS.CONSTELLATION_REGISTER.getEntries().forEach(holder -> {
            CONSTELLATION_PROPERTIES.add(
                    CRYSTAL_PROPERTY_REGISTER.register("constellation_" + holder.getKey().location().getPath(),
                            () -> new CrystalPropertyConstellation(holder))
            );
        });

        LumenAS.LUMEN_REGISTER.getEntries().forEach(holder -> {
            if (LumenAS.NONE.getKey().equals(holder.getKey())) return;
            LUMEN_PROPERTIES.add(
                    CRYSTAL_PROPERTY_REGISTER.register("lumen_" + holder.getKey().location().getPath(),
                            () -> new CrystalPropertyLumen(holder))
            );
        });
    }
}
