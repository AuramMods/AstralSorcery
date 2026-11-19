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
import hellfirepvp.astralsorcery.common.init.InitConstellations;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ConstellationsAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ConstellationsAS {

    public static final DeferredRegister<BaseConstellation> CONSTELLATION_REGISTER =
            DeferredRegister.create(RegistriesAS.KEY_CONSTELLATIONS, AstralSorcery.MODID);

    public static final RegistryObject<BaseConstellation> AEVITAS = InitConstellations.aevitas(CONSTELLATION_REGISTER);
    public static final RegistryObject<BaseConstellation> ARMARA = InitConstellations.armara(CONSTELLATION_REGISTER);
    public static final RegistryObject<BaseConstellation> DISCIDIA = InitConstellations.discidia(CONSTELLATION_REGISTER);
    public static final RegistryObject<BaseConstellation> EVORSIO = InitConstellations.evorsio(CONSTELLATION_REGISTER);
    public static final RegistryObject<BaseConstellation> VICIO = InitConstellations.vicio(CONSTELLATION_REGISTER);

    public static final RegistryObject<BaseConstellation> LUCERNA = InitConstellations.lucerna(CONSTELLATION_REGISTER);
    public static final RegistryObject<BaseConstellation> MINERALIS = InitConstellations.mineralis(CONSTELLATION_REGISTER);
    public static final RegistryObject<BaseConstellation> HOROLOGIUM = InitConstellations.horologium(CONSTELLATION_REGISTER);
    public static final RegistryObject<BaseConstellation> OCTANS = InitConstellations.octans(CONSTELLATION_REGISTER);
    public static final RegistryObject<BaseConstellation> BOOTES = InitConstellations.bootes(CONSTELLATION_REGISTER);
    public static final RegistryObject<BaseConstellation> FORNAX = InitConstellations.fornax(CONSTELLATION_REGISTER);
    public static final RegistryObject<BaseConstellation> PELOTRIO = InitConstellations.pelotrio(CONSTELLATION_REGISTER);

}
