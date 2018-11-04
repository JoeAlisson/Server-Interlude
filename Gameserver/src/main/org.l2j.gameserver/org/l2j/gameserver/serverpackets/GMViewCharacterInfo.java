package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.model.Inventory;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;


public class GMViewCharacterInfo extends L2GameServerPacket
{
	private final L2PcInstance _activeChar;

	public GMViewCharacterInfo(L2PcInstance character)
	{
		_activeChar = character;
	}
	
	@Override
	protected final void writeImpl()
	{
		float moveMultiplier = _activeChar.getMovementSpeedMultiplier();
		int _runSpd = (int) (_activeChar.getRunSpeed() / moveMultiplier);
		int _walkSpd = (int) (_activeChar.getWalkSpeed() / moveMultiplier);
		
		writeByte(0x8f);
		
		writeInt(_activeChar.getX());
		writeInt(_activeChar.getY());
		writeInt(_activeChar.getZ());
		writeInt(_activeChar.getHeading());
		writeInt(_activeChar.getObjectId());
		writeString(_activeChar.getName());
		writeInt(_activeChar.getRace().ordinal());
		writeInt(_activeChar.getSex());
		writeInt(_activeChar.getPlayerClass().getId());
		writeInt(_activeChar.getLevel());
		writeLong(_activeChar.getExp());
		writeInt(_activeChar.getSTR());
		writeInt(_activeChar.getDEX());
		writeInt(_activeChar.getCON());
		writeInt(_activeChar.getINT());
		writeInt(_activeChar.getWIT());
		writeInt(_activeChar.getMEN());
		writeInt(_activeChar.getMaxHp());
		writeInt((int) _activeChar.getCurrentHp());
		writeInt(_activeChar.getMaxMp());
		writeInt((int) _activeChar.getCurrentMp());
		writeInt((int)_activeChar.getSp());
		writeInt(_activeChar.getCurrentLoad());
		writeInt(_activeChar.getMaxLoad());
		writeInt(0x28); // unknown
		
		writeInt(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_DHAIR));
		writeInt(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_REAR));
		writeInt(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LEAR));
		writeInt(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_NECK));
		writeInt(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_RFINGER));
		writeInt(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LFINGER));
		writeInt(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_HEAD));
		writeInt(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_RHAND));
		writeInt(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LHAND));
		writeInt(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_GLOVES));
		writeInt(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_CHEST));
		writeInt(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LEGS));
		writeInt(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_FEET));
		writeInt(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_BACK));
		writeInt(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LRHAND));
		writeInt(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_HAIR));
		writeInt(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_DECO1));
		
		writeInt(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_DHAIR));
		writeInt(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_REAR));
		writeInt(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_LEAR));
		writeInt(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_NECK));
		writeInt(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_RFINGER));
		writeInt(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_LFINGER));
		writeInt(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_HEAD));
		writeInt(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_RHAND));
		writeInt(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_LHAND));
		writeInt(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_GLOVES));
		writeInt(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_CHEST));
		writeInt(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_LEGS));
		writeInt(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_FEET));
		writeInt(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_BACK));
		writeInt(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_LRHAND));
		writeInt(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_HAIR));
		writeInt(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_DECO1));
		
		// c6 new h's
		writeShort(0x00);
		writeShort(0x00);
		writeShort(0x00);
		writeShort(0x00);
		writeShort(0x00);
		writeShort(0x00);
		writeShort(0x00);
		writeShort(0x00);
		writeShort(0x00);
		writeShort(0x00);
		writeShort(0x00);
		writeShort(0x00);
		writeShort(0x00);
		writeShort(0x00);
		writeShort(0x00);
		writeShort(0x00);
		writeShort(0x00);
		
		writeShort(0x00);
		writeShort(0x00);
		writeShort(0x00);
		writeShort(0x00);
		writeShort(0x00);
		writeShort(0x00);
		writeShort(0x00);
		writeShort(0x00);
		writeShort(0x00);
		writeShort(0x00);
		writeShort(0x00);
		writeShort(0x00);
		writeShort(0x00);
		writeShort(0x00);
		writeShort(0x00);
		writeShort(0x00);
		writeShort(0x00);
		// end of c6 new h's
		
		writeInt(_activeChar.getPAtk(null));
		writeInt(_activeChar.getPAtkSpd());
		writeInt(_activeChar.getPDef(null));
		writeInt(_activeChar.getEvasionRate(null));
		writeInt(_activeChar.getAccuracy());
		writeInt(_activeChar.getCriticalHit(null, null));
		writeInt(_activeChar.getMAtk(null, null));
		
		writeInt(_activeChar.getMAtkSpd());
		writeInt(_activeChar.getPAtkSpd());
		
		writeInt(_activeChar.getMDef(null, null));
		
		writeInt(_activeChar.getPvpFlag()); // 0-non-pvp 1-pvp = violett name
		writeInt(_activeChar.getKarma());
		
		writeInt(_runSpd);
		writeInt(_walkSpd);
		writeInt(_runSpd); // swimspeed
		writeInt(_walkSpd); // swimspeed
		writeInt(_runSpd);
		writeInt(_walkSpd);
		writeInt(_runSpd);
		writeInt(_walkSpd);
		writeDouble(moveMultiplier);
		writeDouble(_activeChar.getAttackSpeedMultiplier()); // 2.9);//
		writeDouble(_activeChar.getTemplate().getCollisionRadius()); // scale
		writeDouble(_activeChar.getTemplate().getCollisionHeight()); // y offset ??!? fem DWARF 4033
		writeInt(_activeChar.getHairStyle());
		writeInt(_activeChar.getHairColor());
		writeInt(_activeChar.getFace());
		writeInt(_activeChar.isGM() ? 0x01 : 0x00); // builder level
		
		writeString(_activeChar.getTitle());
		writeInt(_activeChar.getClanId()); // pledge id
		writeInt(_activeChar.getClanCrestId()); // pledge crest id
		writeInt(_activeChar.getAllianceId()); // ally id
		writeByte(_activeChar.getMountType()); // mount type
		writeByte(_activeChar.getPrivateStoreType());
		writeByte(_activeChar.hasDwarvenCraft() ? 1 : 0);
		writeInt(_activeChar.getPkKills());
		writeInt(_activeChar.getPvpKills());
		
		writeShort(_activeChar.getRecomLeft());
		writeShort(_activeChar.getRecomHave()); // Blue value for name (0 = white, 255 = pure blue)
		writeInt(_activeChar.getPlayerClass().getId());
		writeInt(0x00); // special effects? circles around reader...
		writeInt(_activeChar.getMaxCp());
		writeInt((int) _activeChar.getCurrentCp());
		
		writeByte(_activeChar.isRunning() ? 0x01 : 0x00); // changes the Speed display on Status Window
		
		writeByte(321);
		
		writeInt(_activeChar.getPledgeClass()); // changes the text above CP on Status Window
		
		writeByte(_activeChar.isNoble() ? 0x01 : 0x00);
		writeByte(_activeChar.isHero() ? 0x01 : 0x00);
		
		writeInt(_activeChar.getNameColor());
		writeInt(_activeChar.getTitleColor());
	}
}
