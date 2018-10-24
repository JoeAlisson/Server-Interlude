package org.l2j.gameserver.ai.accessor;

import org.l2j.gameserver.model.L2Position;

public interface MovableAIAccessor extends AIAccessor {

    void moveTo(int x, int y, int z, int offset);

    void moveTo(int x, int y, int z);

    void stopMove(L2Position pos);

}
