package org.l2j.gameserver.clientpackets;

import org.l2j.gameserver.TaskPriority;
import org.l2j.gameserver.ai.Intention;
import org.l2j.gameserver.instancemanager.BoatManager;
import org.l2j.gameserver.model.L2Position;
import org.l2j.gameserver.model.actor.instance.L2BoatInstance;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.serverpackets.ActionFailed;
import org.l2j.gameserver.templates.xml.jaxb.ItemType;
import org.l2j.gameserver.util.Point3D;

public final class RequestMoveToLocationInVehicle extends L2GameClientPacket
{
	private static final String _C__5C_REQUESTPACKAGESEND = "[C] 5C RequestMoveToLocationInVehicle";
	private final Point3D _pos = new Point3D(0, 0, 0);
	private final Point3D _origin_pos = new Point3D(0, 0, 0);
	private int _boatId;
	
	public TaskPriority getPriority()
	{
		return TaskPriority.PR_HIGH;
	}
	
	@Override
	protected void readImpl()
	{
		int _x, _y, _z;
		_boatId = readInt(); // objectId of boat
		_x = readInt();
		_y = readInt();
		_z = readInt();
		_pos.setXYZ(_x, _y, _z);
		_x = readInt();
		_y = readInt();
		_z = readInt();
		_origin_pos.setXYZ(_x, _y, _z);
	}
	
	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		else if (activeChar.isAttackingNow() && (activeChar.getActiveWeaponItem() != null) && (activeChar.getActiveWeaponItem().getType() == ItemType.BOW))
		{
			activeChar.sendPacket(new ActionFailed());
		}
		else
		{
			if (!activeChar.isInBoat())
			{
				activeChar.setInBoat(true);
			}
			L2BoatInstance boat = BoatManager.getInstance().GetBoat(_boatId);
			activeChar.setBoat(boat);
			activeChar.setInBoatPosition(_pos);
			activeChar.getAI().setIntention(Intention.AI_INTENTION_MOVE_TO_IN_A_BOAT, new L2Position(_pos.getX(), _pos.getY(), _pos.getZ(), 0), new L2Position(_origin_pos.getX(), _origin_pos.getY(), _origin_pos.getZ(), 0));
		}
	}
	
	@Override
	public String getType()
	{
		return _C__5C_REQUESTPACKAGESEND;
	}
}