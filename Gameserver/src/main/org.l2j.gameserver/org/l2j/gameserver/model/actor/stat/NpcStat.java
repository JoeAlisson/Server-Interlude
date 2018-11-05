package org.l2j.gameserver.model.actor.stat;

import org.l2j.gameserver.model.actor.instance.L2NpcInstance;
import org.l2j.gameserver.skills.Stats;

public class NpcStat extends CharStat {

	public NpcStat(L2NpcInstance activeChar) {
		super(activeChar);
		setLevel(getActiveChar().getTemplate().getLevel());
	}

	@Override
	public L2NpcInstance getActiveChar()
	{
		return (L2NpcInstance) super.getActiveChar();
	}
}
