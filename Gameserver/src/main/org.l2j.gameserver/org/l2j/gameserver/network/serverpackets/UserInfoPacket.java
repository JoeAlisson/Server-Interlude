package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.datatables.NpcTable;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.base.Experience;
import org.l2j.gameserver.model.entity.database.NpcTemplate;
import org.l2j.gameserver.templates.base.SkillConstants;

import static java.util.Objects.*;

public class UserInfoPacket extends AbstractMaskPacket<UserInfoType> {

    private final L2PcInstance player;

    private byte[] _masks = new byte[] { 0x00, 0x00, 0x00 };
    private int _initSize = 5;

    // Params
	private boolean can_writeImpl;
	private int  _relation;
	private String title;


	public UserInfoPacket(L2PcInstance player) {
		this(player, true);
	}

	public UserInfoPacket(L2PcInstance player, boolean addAll) {
	    this.player = player;

		title = requireNonNullElse(player.getTitle(), "");

		if(player.isInvisible() && player.isGM())
			title += "[I]";
		if(player.isMorphed()) {
            NpcTemplate polyObj = NpcTable.getInstance().getTemplate(player.getPolyMorph());
            if(nonNull(polyObj)) {
                title += " - " + polyObj.getName();
            } else {
                title += " - Polymorphed";
            }
		}

		if(player.getClan() != null) {
			_relation |= RelationChangedPacket.USER_RELATION_CLAN_MEMBER;
			if(player.isClanLeader())
				_relation |= RelationChangedPacket.USER_RELATION_CLAN_LEADER;
		}

		/*for(Event e : player.getEvents())  // TODO implement Events Relation
			_relation = e.getUserRelation(reader, _relation);*/

		can_writeImpl = true;

		if(addAll) {
            addComponentType(UserInfoType.values());
        }
	}

	@Override
	protected byte[] getMasks() {
		return _masks;
	}

	@Override
	protected void onNewMaskAdded(UserInfoType component) {
		calcBlockSize(component);
	}

	private void calcBlockSize(UserInfoType type) {
		switch(type) {
			case BASIC_INFO:
				_initSize += type.getBlockLength() + (player.getName().length() * 2);
				break;
			case CLAN:
				_initSize += type.getBlockLength() + (title.length() * 2);
				break;
			default:
				_initSize += type.getBlockLength();
				break;
		}
	}

	@Override
	protected final void writeImpl() {

		if(!can_writeImpl)
			return;
		
		writeByte(0x32);
		writeInt(player.getObjectId());
		writeInt(_initSize);
		writeShort(24);
		writeBytes(_masks);

		if(containsMask(UserInfoType.RELATION))
			writeInt(_relation);

		if(containsMask(UserInfoType.BASIC_INFO)) {
		    var name = player.getName();
			writeShort(UserInfoType.BASIC_INFO.getBlockLength() + (name.length() * 2));
			writeSizedString(name);
			writeByte(player.isGM());
			writeByte(player.getRace().ordinal());
			writeByte(player.getSex());
			writeInt(player.getBaseClass());
			writeInt(player.getClassId());
			writeByte(player.getLevel());
		}

		if(containsMask(UserInfoType.BASE_STATS)) {
			writeShort(UserInfoType.BASE_STATS.getBlockLength());
			writeShort(player.getStrength());
			writeShort(player.getDexterity());
			writeShort(player.getConstitution());
			writeShort(player.getIntelligence());
			writeShort(player.getWisdom());
			writeShort(player.getMentality());
			writeShort(0x00); // Lucky TODO implement
			writeShort(0x00); // Charm TODO implement
		}

		if(containsMask(UserInfoType.MAX_HPCPMP)) {
			writeShort(UserInfoType.MAX_HPCPMP.getBlockLength());
			writeInt(player.getMaxHp());
			writeInt(player.getMaxMp());
			writeInt(player.getMaxCp());
		}

		if(containsMask(UserInfoType.CURRENT_HPMPCP_EXP_SP)) {
			writeShort(UserInfoType.CURRENT_HPMPCP_EXP_SP.getBlockLength());
			writeInt((int) player.getCurrentHp());
			writeInt((int) player.getCurrentMp());
			writeInt((int) player.getCurrentCp());
			writeLong(player.getSkillPoints());
			writeLong(player.getExperience());

			writeDouble(Experience.getLevelProgress(player));
		}

		if(containsMask(UserInfoType.ENCHANTLEVEL)) {
			writeShort(UserInfoType.ENCHANTLEVEL.getBlockLength());
			writeByte(player.isMounted() ? 0x00 : player.getEnchantEffect());
			writeByte(0); // TODO implement armor enchantment effect
		}

		if(containsMask(UserInfoType.APPAREANCE)) {
			writeShort(UserInfoType.APPAREANCE.getBlockLength());
			writeInt(player.getHairStyle());
			writeInt(player.getHairColor());
			writeInt(player.getFace());
			writeByte(1);  //show Head Accessory ?
		}

		if(containsMask(UserInfoType.STATUS)) {
			writeShort(UserInfoType.STATUS.getBlockLength());
			writeByte(player.getMountType());
			writeByte(player.getPrivateStoreType());
			writeByte(player.getSkillLevel(SkillConstants.CRYSTALLIZE) > 0);
			writeByte(0x00); // Ability points
		}

		if(containsMask(UserInfoType.STATS)) {
			writeShort(UserInfoType.STATS.getBlockLength());
			writeShort(isNull(player.getActiveWeaponInstance()) ? 0x14 : 0x28);
			writeInt(player.getPAtk(null));
			writeInt(player.getPAtkSpd());
			writeInt(player.getPDef(null));
			writeInt(player.getEvasionRate(null));
			writeInt(player.getAccuracy());
			writeInt(player.getCriticalHit(null, null));
			writeInt(player.getMAtk(null, null));
			writeInt(player.getMAtkSpd());
			writeInt(player.getPAtkSpd()); // Again ??
			writeInt(player.getEvasionRate(null)); // TODO implement magic evasion
			writeInt(player.getMDef(null, null));
			writeInt(player.getAccuracy()); // TODO implement magic accuracy
			writeInt(player.getMCriticalHit(null, null));
		}

		if(containsMask(UserInfoType.ELEMENTALS)) {
			writeShort(UserInfoType.ELEMENTALS.getBlockLength());
			writeShort((int) player.getFireDefense()); // TODO implement Elements Defense
			writeShort((int) player.getWaterDefense());
			writeShort((int) player.getWindDefense());
			writeShort((int) player.getEarthDefense());
			writeShort((int) player.getHolyDefense());
			writeShort((int) player.getUnholyDefense());
		}

		if(containsMask(UserInfoType.POSITION)) {
			writeShort(UserInfoType.POSITION.getBlockLength());
			writeInt(player.getX());
			writeInt(player.getY());
			writeInt(player.getZ());
			writeInt(player.isInBoat() ? player.getBoat().getObjectId() : 0x00);
		}

		if(containsMask(UserInfoType.SPEED)) {
			writeShort(UserInfoType.SPEED.getBlockLength());
			writeShort(player.getRunSpeed());
			writeShort(player.getWalkSpeed());
			writeShort(player.getRunSpeed()); // TODO implement swim speed
			writeShort(player.getWalkSpeed()); // TODO implement swim
			writeShort(player.getRunSpeed()); // TODO implement mount speed
			writeShort(player.getWalkSpeed()); // TODO implement mount
			writeShort(player.getRunSpeed()); // TODO implement Fly speed
			writeShort(player.getWalkSpeed()); // TODO implement Fly
		}

		if(containsMask(UserInfoType.MULTIPLIER)) {
			writeShort(UserInfoType.MULTIPLIER.getBlockLength());
			writeDouble(player.getMovementSpeedMultiplier());
			writeDouble(player.getAttackSpeedMultiplier());
		}

		if(containsMask(UserInfoType.COL_RADIUS_HEIGHT)) {
			writeShort(UserInfoType.COL_RADIUS_HEIGHT.getBlockLength());
			writeDouble(player.getCollisionRadius());
			writeDouble(player.getCollisionHeight());
		}

		if(containsMask(UserInfoType.ATK_ELEMENTAL)) {
			writeShort(UserInfoType.ATK_ELEMENTAL.getBlockLength());
			writeByte(0); // TODO attack Element Id
			writeShort(0); // power
		}

		if(containsMask(UserInfoType.CLAN)) {
			writeShort(UserInfoType.CLAN.getBlockLength() + (nonNull(title) ? title.length() * 2 : 0));
			writeSizedString(title);
			writeShort(player.getPledgeType());
			writeInt(player.getClanId());

            var clan = player.getClan();
			writeInt(nonNull(clan) ? clan.getCrestLargeId(): 0x00);
			writeInt(nonNull(clan) ? clan.getCrestId() : 0x00);

			writeInt(player.getClanPrivileges());
			writeByte(player.isClanLeader());
			writeInt(player.getAllianceId());
			writeInt(player.getAllyCrestId());
			writeByte(false); // TODO implement looking for party room
		}

		if(containsMask(UserInfoType.SOCIAL)) {
			writeShort(UserInfoType.SOCIAL.getBlockLength());
			writeByte(player.getPvpFlag());
			writeInt(player.getKarma()); // TODO reputation
			writeByte(player.isNoble());
			writeByte(player.isHero());
			writeByte(player.getPledgeClass());
			writeInt(player.getPvpKills());
			writeInt(player.getPkKills());
			writeShort(player.getRecomLeft());
			writeShort(player.getRecomHave());
		}

		if(containsMask(UserInfoType.VITA_FAME)) {
			writeShort(UserInfoType.VITA_FAME.getBlockLength());
			writeInt(0x00);  // TODO implement Vitality level
			writeByte(0x00); // Vita Bonus
			writeInt(0x00);  // Fame
			writeInt(0x00); // raid points
		}

		if(containsMask(UserInfoType.SLOTS)) {
			writeShort(UserInfoType.SLOTS.getBlockLength());
			writeByte(0x00); // TODO Implement Talismans
			writeByte(0x00); // Jewel
			writeByte(player.getTeam()); // Duel Team
            writeInt(0x00); // Team mask ??
			writeByte(0x00); // charm slots ??
			writeByte(0x00); // ??
		}

		if(containsMask(UserInfoType.MOVEMENTS)) {
			writeShort(UserInfoType.MOVEMENTS.getBlockLength());
			writeByte(player.isInWater());
			writeByte(player.isRunning());
		}

		if(containsMask(UserInfoType.COLOR)) {
			writeShort(UserInfoType.COLOR.getBlockLength());
			writeInt(player.getNameColor());
			writeInt(player.getTitleColor());
		}

		if(containsMask(UserInfoType.INVENTORY_LIMIT)) {
			writeShort(UserInfoType.INVENTORY_LIMIT.getBlockLength());
			writeInt(0x00); // Mount ?
			writeShort(player.getInventoryLimit());
			writeByte(false); // hide title
		}

		if(containsMask(UserInfoType.UNK_3)) {
			writeShort(UserInfoType.UNK_3.getBlockLength());
			writeByte(0x00);
			writeInt(0x00);
			writeByte(0x00);
			writeByte(0x00);
		}
	}

    @Override
    protected int packetSize() {
        return _initSize + 11;
    }
}

