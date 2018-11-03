/* This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */

package org.l2j.gameserver.model;

import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.datatables.AugmentationData;
import org.l2j.gameserver.datatables.SkillTable;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.entity.database.Augmentation;
import org.l2j.gameserver.model.entity.database.repository.AugmentationsRepository;
import org.l2j.gameserver.skills.Stats;
import org.l2j.gameserver.skills.funcs.FuncAdd;
import org.l2j.gameserver.skills.funcs.LambdaConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * Used to store an augmentation and its boni
 *
 * @author durgus
 */
public final class L2Augmentation {
    private static final Logger _log = LoggerFactory.getLogger(L2Augmentation.class.getName());

    private final L2ItemInstance _item;
    private int _effectsId = 0;
    private augmentationStatBoni _boni = null;
    private L2Skill _skill = null;

    public L2Augmentation(L2ItemInstance item, int effects, L2Skill skill, boolean save) {
        _item = item;
        _effectsId = effects;
        _boni = new augmentationStatBoni(_effectsId);
        _skill = skill;

        // write to DB if save is true
        if (save) {
            saveAugmentationData();
        }
    }

    public L2Augmentation(L2ItemInstance item, int effects, int skill, int skillLevel, boolean save) {
        this(item, effects, SkillTable.getInstance().getInfo(skill, skillLevel), save);
    }

    // =========================================================
    // Nested Class

    public class augmentationStatBoni {
        private final Stats _stats[];
        private final float _values[];
        private boolean _active;

        public augmentationStatBoni(int augmentationId) {
            _active = false;
            List<AugmentationData.AugStat> as = AugmentationData.getInstance().getAugStatsById(augmentationId);

            _stats = new Stats[as.size()];
            _values = new float[as.size()];

            int i = 0;
            for (AugmentationData.AugStat aStat : as) {
                _stats[i] = aStat.getStat();
                _values[i] = aStat.getValue();
                i++;
            }
        }

        public void applyBoni(L2PcInstance player) {
            // make sure the boni are not applyed twice..
            if (_active) {
                return;
            }

            for (int i = 0; i < _stats.length; i++) {
                ((L2Character) player).addStatFunc(new FuncAdd(_stats[i], 0x40, this, new LambdaConst(_values[i])));
            }

            _active = true;
        }

        public void removeBoni(L2PcInstance player) {
            // make sure the boni is not removed twice
            if (!_active) {
                return;
            }

            ((L2Character) player).removeStatsOwner(this);

            _active = false;
        }
    }

    private void saveAugmentationData() {
        Augmentation augmentation = new Augmentation(_item.getObjectId(), _effectsId, _skill);
        AugmentationsRepository repository = DatabaseAccess.getRepository(AugmentationsRepository.class);
        repository.save(augmentation);
    }

    public void deleteAugmentationData() {
        if (!_item.isAugmented()) {
            return;
        }

        AugmentationsRepository repository = DatabaseAccess.getRepository(AugmentationsRepository.class);
        repository.deleteById(_item.getObjectId());
    }

    /**
     * Get the augmentation "id" used in serverpackets.
     *
     * @return augmentationId
     */
    public int getAugmentationId() {
        return _effectsId;
    }

    public L2Skill getSkill() {
        return _skill;
    }

    /**
     * Applys the boni to the reader.
     *
     * @param player
     */
    public void applyBoni(L2PcInstance player) {
        _boni.applyBoni(player);

        // add the skill if any
        if (_skill != null) {
            player.addSkill(_skill);
            player.sendSkillList();
        }
    }

    /**
     * Removes the augmentation boni from the reader.
     *
     * @param player
     */
    public void removeBoni(L2PcInstance player) {
        _boni.removeBoni(player);

        // remove the skill if any
        if (_skill != null) {
            player.removeSkill(_skill);
            player.sendSkillList();
        }
    }
}
