package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author Luca Baldi
 */
public class RelationChanged extends L2GameServerPacket
{
	public static final int RELATION_PVP_FLAG = 0x00002; // pvp ???
	public static final int RELATION_HAS_KARMA = 0x00004; // karma ???
	public static final int RELATION_LEADER = 0x00080; // leader
	public static final int RELATION_INSIEGE = 0x00200; // true if in siege
	public static final int RELATION_ATTACKER = 0x00400; // true when attacker
	public static final int RELATION_ALLY = 0x00800; // blue siege icon, cannot have if red
	public static final int RELATION_ENEMY = 0x01000; // true when red icon, doesn't matter with blue
	public static final int RELATION_MUTUAL_WAR = 0x08000; // double fist
	public static final int RELATION_1SIDED_WAR = 0x10000; // single fist
	
	private static final String _S__CE_RELATIONCHANGED = "[S] CE RelationChanged";
	
	private final int _objId, _relation, _autoAttackable, _karma, _pvpFlag;
	
	public RelationChanged(L2PcInstance cha, int relation, boolean autoattackable)
	{
		_objId = cha.getObjectId();
		_relation = relation;
		_autoAttackable = autoattackable ? 1 : 0;
		_karma = cha.getKarma();
		_pvpFlag = cha.getPvpFlag();
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0xce);
		writeInt(_objId);
		writeInt(_relation);
		writeInt(_autoAttackable);
		writeInt(_karma);
		writeInt(_pvpFlag);
	}
}
