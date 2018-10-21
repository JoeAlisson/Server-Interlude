package org.l2j.gameserver.ai.accessor;

import org.l2j.gameserver.model.L2Character;
import org.l2j.gameserver.model.L2Skill;

public interface OffensiveAIAccessor extends AIAccessor {

    void doAttack(L2Character target);

    void doCast(L2Skill skill);
}
