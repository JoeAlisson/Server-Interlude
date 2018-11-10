/*
 * This program is free software; you can redistribute it and/or modify
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
package org.l2j.gameserver.datatables;

import org.l2j.gameserver.model.L2Skill;
import org.l2j.gameserver.skills.SkillsEngine;
import org.l2j.gameserver.templates.base.ItemType;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.isNull;

public class SkillTable {

    private static SkillTable INSTANCE;

    private final Map<Integer, L2Skill> _skills;

    public static SkillTable getInstance() {
        if (isNull(INSTANCE)) {
            INSTANCE = new SkillTable();
        }
        return INSTANCE;
    }

    private SkillTable() {
        _skills = new HashMap<>();
        SkillsEngine.getInstance().loadAllSkills(_skills);
    }

    public void reload() {
        INSTANCE = new SkillTable();
    }



    /**
     * Provides the skill hash
     *
     * @param skill The L2Skill to be hashed
     * @return SkillTable.getSkillHashCode(skill.getId (), skill.getLevel())
     */
    public static int getSkillHashCode(L2Skill skill) {
        return SkillTable.getSkillHashCode(skill.getId(), skill.getLevel());
    }

    /**
     * Centralized method for easier change of the hashing sys
     *
     * @param skillId    The Skill Id
     * @param skillLevel The Skill Level
     * @return The Skill hash number
     */
    public static int getSkillHashCode(int skillId, int skillLevel) {
        return (skillId * 256) + skillLevel;
    }

    public L2Skill getInfo(int skillId, int level) {
        return _skills.get(SkillTable.getSkillHashCode(skillId, level));
    }

    public int getMaxLevel(int magicId, int level) {
        L2Skill temp;

        while (level < 100) {
            level++;
            temp = _skills.get(SkillTable.getSkillHashCode(magicId, level));

            if (temp == null) {
                return level - 1;
            }
        }

        return level;
    }

    private static final ItemType[] weaponDbMasks =
            {
                    ItemType.ETC,
                    ItemType.BOW,
                    ItemType.POLE,
                    ItemType.DUAL_FIST,
                    ItemType.DUAL,
                    ItemType.BLUNT,
                    ItemType.SWORD,
                    ItemType.DAGGER,
                    ItemType.BIG_SWORD,
                    ItemType.ROD,
                    ItemType.BIG_BLUNT
            };

    public int calcWeaponsAllowed(int mask) {
        if (mask == 0) {
            return 0;
        }

        int weaponsAllowed = 0;

        for (int i = 0; i < weaponDbMasks.length; i++) {
            if ((mask & (1 << i)) != 0) {
                weaponsAllowed |= weaponDbMasks[i].mask();
            }
        }

        return weaponsAllowed;
    }
}
