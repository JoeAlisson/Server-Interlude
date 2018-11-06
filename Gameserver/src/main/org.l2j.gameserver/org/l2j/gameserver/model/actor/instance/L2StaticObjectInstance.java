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

import static java.util.Objects.nonNull;

/**
 * GODSON ROX!
 */
public class L2StaticObjectInstance extends L2Object {

    private static final Logger logger = LoggerFactory.getLogger(L2StaticObjectInstance.class);
	public static final int INTERACTION_DISTANCE = 150;
	
	private int staticObjectId;
	private int type = -1; // 0 - map signs, 1 - throne , 2 - arena signs
	private int mapX;
	private int mapY;
	private String texture;

	public L2StaticObjectInstance(int objectId) {
		super(objectId);
		setKnownList(new NullKnownList(this));
	}

	@Override
	public void onAction(L2PcInstance player) {
		if (type < 0) {
			logger.warn("L2StaticObjectInstance: StaticObject with invalid type! StaticObjectId: {}", getStaticObjectId());
		}

		if (this != player.getTarget()) {
			player.setTarget(this);
			player.sendPacket(new MyTargetSelected(getObjectId(), 0));
		} else {
			if (!player.isInsideRadius(this, INTERACTION_DISTANCE, false, false)) {
				player.getAI().setIntention(Intention.AI_INTENTION_INTERACT, this);
				player.sendPacket(new ActionFailed());
			} else {
				if (type == 2) {
					String filename = "data/html/signboard.htm";
					String content = HtmCache.getInstance().getHtm(filename);
					NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
					
					if (nonNull(content)) {
						html.setHtml("<html><body>Signboard is missing:<br>" + filename + "</body></html>");
					} else {
						html.setHtml(content);
					}
					player.sendPacket(html);
				} else if (type == 0) {
					player.sendPacket(new ShowTownMap(texture, getMapX(), getMapY()));
				}
			}
		}
	}

    public void setMap(String texture, int x, int y) {
        this.texture = "town_map." + texture;
        mapX = x;
        mapY = y;
    }

    @Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		return false;
	}

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    private int getMapX()
    {
        return mapX;
    }

    private int getMapY()
    {
        return mapY;
    }

    public int getStaticObjectId()
    {
        return staticObjectId;
    }

    public void setStaticObjectId(int StaticObjectId)
    {
        staticObjectId = StaticObjectId;
    }

}
