/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import hellfirepvp.astralsorcery.common.crystal.CrystalPropertyCalculator;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.state.BlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CrystalAxeItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CrystalAxeItem extends AxeItem implements CrystalToolItem {

    public CrystalAxeItem() {
        this(ItemsAS.CRYSTAL_TOOL_TIER, new Properties().setNoRepair());
    }

    protected CrystalAxeItem(Tier tier, Properties properties) {
        super(tier, 5, -3.0F, properties);
    }

    @Override
    public int getCrystalCount() {
        return 3;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return CrystalPropertyCalculator.getToolDurability(super.getMaxDamage(stack), stack, 1);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        float speed = super.getDestroySpeed(stack, state);
        return state.is(BlockTags.MINEABLE_WITH_AXE) ? CrystalPropertyCalculator.getToolSpeed(speed, stack, 1) : speed;
    }
}
