package org.l2j.gameserver.network.clientpackets;

import org.l2j.commons.Config;
import org.l2j.gameserver.model.L2Character;
import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Action extends L2GameClientPacket
{
	private static Logger _log = LoggerFactory.getLogger(Action.class.getName());
	
	// cddddc
	private int _objectId;
	@SuppressWarnings("unused")
	private int _originX;
	@SuppressWarnings("unused")
	private int _originY;
	@SuppressWarnings("unused")
	private int _originZ;
	private int _actionId;
	
	@Override
	protected void readImpl()
	{
		_objectId = readInt(); // Target object Identifier
		_originX = readInt();
		_originY = readInt();
		_originZ = readInt();
		_actionId = readUnsignedByte(); // Action identifier : 0-Simple click, 1-Shift click
	}
	
	@Override
	protected void runImpl()
	{
		if (Config.DEBUG)
		{
			_log.debug("Action:" + _actionId);
		}
		if (Config.DEBUG)
		{
			_log.debug("oid:" + _objectId);
		}
		
		// Get the current L2PcInstance of the reader
		L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
		{
			return;
		}
		
		L2Object obj;
		
		if (activeChar.getTargetId() == _objectId)
		{
			obj = activeChar.getTarget();
		}
		else
		{
			obj = L2World.getInstance().findObject(_objectId);
		}
		
		// If object requested does not exist, add warn msg into logs
		if (obj == null)
		{
			// pressing e.g. pickup many times quickly would get you here
			// logger.warn("Character: " + activeChar.getName() + " request action with non existent ObjectID:" + _objectId);
			getClient().sendPacket(new ActionFailed());
			return;
		}
		
		// Check if the target is valid, if the reader haven't a shop or isn't the requester of a transaction (ex : FriendInvite, JoinAlly, JoinParty...)
		if ((activeChar.getPrivateStoreType() == 0) && (activeChar.getActiveRequester() == null))
		{
			switch (_actionId)
			{
				case 0:
					obj.onAction(activeChar);
					break;
				case 1:
					if ((obj instanceof L2Character) && ((L2Character) obj).isAlikeDead())
					{
						obj.onAction(activeChar);
					}
					else
					{
						obj.onActionShift(getClient());
					}
					break;
				default:
					// Ivalid action detected (probably client cheating), log this
					_log.warn("Character: " + activeChar.getName() + " requested invalid action: " + _actionId);
					getClient().sendPacket(new ActionFailed());
					break;
			}
		}
		else
		{
			// Actions prohibited when in trade
			getClient().sendPacket(new ActionFailed());
		}
	}
}
