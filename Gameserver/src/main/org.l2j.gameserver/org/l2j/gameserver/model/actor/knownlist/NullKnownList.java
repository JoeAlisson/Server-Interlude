package org.l2j.gameserver.model.actor.knownlist;

import org.l2j.gameserver.model.L2Character;
import org.l2j.gameserver.model.L2Object;

public class NullKnownList extends KnownList {

	public NullKnownList(L2Object activeObject) {
		super(activeObject);
	}

	@Override
	public boolean addKnownObject(L2Object object, L2Character dropper)
	{
		return false;
	}

	@Override
	public boolean addKnownObject(L2Object object)
	{
		return false;
	}

	@Override
	public L2Object getActiveObject()
	{
		return super.getActiveObject();
	}

	@Override
	public int getDistanceToForgetObject(L2Object object)
	{
		return 0;
	}

	@Override
	public int getDistanceToWatchObject(L2Object object)
	{
		return 0;
	}

	@Override
	public void removeAllKnownObjects() { }

	@Override
	public boolean removeKnownObject(L2Object object)
	{
		return false;
	}
}