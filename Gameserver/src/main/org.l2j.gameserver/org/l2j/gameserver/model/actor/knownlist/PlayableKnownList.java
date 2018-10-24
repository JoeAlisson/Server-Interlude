package org.l2j.gameserver.model.actor.knownlist;

import org.l2j.gameserver.model.actor.instance.L2PlayableInstance;

public class PlayableKnownList extends CharKnownList {

	public PlayableKnownList(L2PlayableInstance activeChar) {
		super(activeChar);
	}

	@Override
	public L2PlayableInstance getActiveChar()
	{
		return (L2PlayableInstance) super.getActiveChar();
	}
}
