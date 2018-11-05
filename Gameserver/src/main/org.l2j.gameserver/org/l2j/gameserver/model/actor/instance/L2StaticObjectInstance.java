package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.ai.Intention;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.model.L2Character;
import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.actor.knownlist.NullKnownList;
import org.l2j.gameserver.serverpackets.ActionFailed;
import org.l2j.gameserver.serverpackets.MyTargetSelected;
import org.l2j.gameserver.serverpackets.NpcHtmlMessage;
import org.l2j.gameserver.serverpackets.ShowTownMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GODSON ROX!
 */
public class L2StaticObjectInstance extends L2Object {
	private static Logger _log = LoggerFactory.getLogger(L2StaticObjectInstance.class);
	
	/** The interaction distance of the L2StaticObjectInstance */
	public static final int INTERACTION_DISTANCE = 150;
	
	private int _staticObjectId;
	private int _type = -1; // 0 - map signs, 1 - throne , 2 - arena signs
	private int _x;
	private int _y;
	private String _texture;
	
	/**
	 * @return Returns the StaticObjectId.
	 */
	public int getStaticObjectId()
	{
		return _staticObjectId;
	}
	
	/**
	 * @param StaticObjectId
	 */
	public void setStaticObjectId(int StaticObjectId)
	{
		_staticObjectId = StaticObjectId;
	}
	
	/**
	 * @param objectId
	 */
	public L2StaticObjectInstance(int objectId)
	{
		super(objectId);
		setKnownList(new NullKnownList(this));
	}
	
	public int getType()
	{
		return _type;
	}
	
	public void setType(int type)
	{
		_type = type;
	}
	
	public void setMap(String texture, int x, int y)
	{
		_texture = "town_map." + texture;
		_x = x;
		_y = y;
	}
	
	private int getMapX()
	{
		return _x;
	}
	
	private int getMapY()
	{
		return _y;
	}
	
	/**
	 * this is called when a reader interacts with this NPC
	 * @param player
	 */
	@Override
	public void onAction(L2PcInstance player)
	{
		if (_type < 0)
		{
			_log.info("L2StaticObjectInstance: StaticObject with invalid type! StaticObjectId: " + getStaticObjectId());
		}
		// Check if the L2PcInstance already target the L2NpcInstance
		if (this != player.getTarget())
		{
			// Set the target of the L2PcInstance reader
			player.setTarget(this);
			player.sendPacket(new MyTargetSelected(getObjectId(), 0));
			
		}
		else
		{
			
			MyTargetSelected my = new MyTargetSelected(getObjectId(), 0);
			player.sendPacket(my);
			
			// Calculate the distance between the L2PcInstance and the L2NpcInstance
			if (!player.isInsideRadius(this, INTERACTION_DISTANCE, false, false))
			{
				// Notify the L2PcInstance AI with AI_INTENTION_INTERACT
				player.getAI().setIntention(Intention.AI_INTENTION_INTERACT, this);
				
				// Send a Server->Client packet ActionFailed (target is out of interaction range) to the L2PcInstance reader
				player.sendPacket(new ActionFailed());
			}
			else
			{
				if (_type == 2)
				{
					String filename = "data/html/signboard.htm";
					String content = HtmCache.getInstance().getHtm(filename);
					NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
					
					if (content == null)
					{
						html.setHtml("<html><body>Signboard is missing:<br>" + filename + "</body></html>");
					}
					else
					{
						html.setHtml(content);
					}
					
					player.sendPacket(html);
					player.sendPacket(new ActionFailed());
				}
				else if (_type == 0)
				{
					player.sendPacket(new ShowTownMap(_texture, getMapX(), getMapY()));
				}
				// Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
				player.sendPacket(new ActionFailed());
			}
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.l2j.gameserver.model.L2Object#isAttackable()
	 */
	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		return false;
	}
}
