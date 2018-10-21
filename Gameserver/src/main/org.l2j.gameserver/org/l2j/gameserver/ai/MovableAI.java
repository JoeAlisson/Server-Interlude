package org.l2j.gameserver.ai;

import org.l2j.gameserver.model.L2Character;

public class MovableAI<T extends L2Character.AIAccessor> extends L2CharacterAI<T> {

    public MovableAI(T accessor) {
        super(accessor);
    }
}
