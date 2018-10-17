package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.commons.Config;
import com.l2jbr.gameserver.datatables.NpcTable;
import com.l2jbr.gameserver.model.actor.instance.L2CubicInstance;
import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;
import com.l2jbr.gameserver.model.base.Experience;
import com.l2jbr.gameserver.model.entity.database.NpcTemplate;
import com.l2jbr.gameserver.skills.SkillConstants;

import static java.util.Objects.nonNull;

/**
 * @reworked by Bonux
 */
public class UIPacket extends AbstractMaskPacket<UserInfoType> {
    private final int x;
    private final int y;
    private final int z;
    // Params
	private boolean can_writeImpl = false, partyRoom;
	private int _runSpd, _walkSpd, _swimRunSpd, _swimWalkSpd, _flRunSpd, _flWalkSpd, _flyRunSpd, _flyWalkSpd, _relation;
	private double move_speed, attack_speed, col_radius, col_height;
	private int obj_id, vehicle_obj_id, _race, sex, base_class, level, curCp, maxCp, _weaponEnchant, _armorSetEnchant, _weaponFlag;
	private long _exp, _sp;
	private int curHp, maxHp, curMp, maxMp, curLoad, maxLoad, rec_left, rec_have;
	private int _str, _con, _dex, _int, _wit, _men, ClanPrivs, InventoryLimit;
	private int _patk, _patkspd, _pdef, _matk, _matkspd;
	private int _pEvasion, _pAccuracy, _pCrit, _mEvasion, _mAccuracy, _mCrit;
	private int _mdef, pvp_flag, karma, hair_style, hair_color, face, gm_commands, fame;
	private int clan_id, _isClanLeader, clan_crest_id, ally_id, ally_crest_id, large_clan_crest_id;
	private int private_store, can_crystalize, pk_kills, pvp_kills, class_id, agathion, _partySubstitute;
	private int hero, mount_id;
	private int name_color, running, pledge_class, pledge_type, title_color, transformation;
	private int defenceFire, defenceWater, defenceWind, defenceEarth, defenceHoly, defenceUnholy;
	private int mount_type;
	private String _name, _title;
	private L2CubicInstance[] cubics;
	private int attackElementValue;
	private int _moveType;
	private int talismans;
	private int _jewelsLimit;
	private double _expPercent;
	private int _team;
	private final boolean _hideHeadAccessories;

	private final byte[] _masks = new byte[]
	{
		(byte) 0x00,
		(byte) 0x00,
		(byte) 0x00
	};

	private int _initSize = 5;

	public UIPacket(L2PcInstance player)
	{
		this(player, true);
	}

	public UIPacket(L2PcInstance player, boolean addAll)
	{
		_name = player.getName();
		name_color = player.getAppearance().getNameColor();
		_title = player.getTitle();
		title_color = player.getAppearance().getTitleColor();

			var clan = player.getClan();
			clan_id = clan == null ? 0 : clan.getClanId();
			_isClanLeader = player.isClanLeader() ? 1 : 0;
			clan_crest_id = clan == null ? 0 : clan.getCrestId();
			large_clan_crest_id = clan == null ? 0 : clan.getCrestLargeId();
			//
			ally_id = player.getAllianceId();
			ally_crest_id = player.getAllyCrestId();

		if(player.getAppearance().getInvisible() && player.isGM())
			_title += "[I]";
		if(player.getPoly().isMorphed())
		{
            NpcTemplate polyObj = NpcTable.getInstance().getTemplate(player.getPoly().getPolyId());
            if(nonNull(polyObj))
				_title += " - " + polyObj.getName();
			else
				_title += " - Polymorphed";
		}

		if(player.isMounted())
		{
			_weaponEnchant = 0;
			mount_id = player.getPet().getNpcId() + 1000000;
			mount_type = player.getMountType();
		}
		else
		{
			_weaponEnchant = player.getEnchantEffect();
			mount_id = 0;
			mount_type = 0;
		}

		_weaponFlag = player.getActiveWeaponInstance() == null ? 0x14 : 0x28;

		move_speed = player.getMovementSpeedMultiplier();
		_runSpd = (int) (player.getRunSpeed() / move_speed);
		_walkSpd = (int) (player.getWalkSpeed() / move_speed);

		_flRunSpd = 0; // TODO
		_flWalkSpd = 0; // TODO

		if(player.isFlying())
		{
			_flyRunSpd = _runSpd;
			_flyWalkSpd = _walkSpd;
		}
		else
		{
			_flyRunSpd = 0;
			_flyWalkSpd = 0;
		}

		_swimRunSpd = (int) (_runSpd  / move_speed); //  TODO implement swim speed
		_swimWalkSpd = (int) (_walkSpd / move_speed);

		if(player.getClan() != null)
		{
			_relation |= RelationChangedPacket.USER_RELATION_CLAN_MEMBER;
			if(player.isClanLeader())
				_relation |= RelationChangedPacket.USER_RELATION_CLAN_LEADER;
		}

		/*for(Event e : player.getEvents())  // TODO implement Events Relation
			_relation = e.getUserRelation(player, _relation);*/


		obj_id = player.getObjectId();
		vehicle_obj_id = player.isInBoat() ? player.getBoat().getObjectId() : 0x00;
		_race = player.getRace().ordinal();
		sex = player.getAppearance().getSex() ? 1 : 0;
		base_class = player.getBaseClass();
		level = player.getLevel();
		_exp = player.getExp();

        long xpToNextLevel = Experience.LEVEL[player.getLevel() +1] - Experience.LEVEL[player.getLevel()];
        long xpInCurrentLevel = player.getExp() - Experience.LEVEL[player.getLevel()];
		_expPercent = (double) xpInCurrentLevel / xpToNextLevel;

		_str = player.getSTR();
		_dex = player.getDEX();
		_con = player.getCON();
		_int = player.getINT();
		_wit = player.getWIT();
		_men = player.getMEN();
		curHp = (int) player.getCurrentHp();
		maxHp = player.getMaxHp();
		curMp = (int) player.getCurrentMp();
		maxMp = player.getMaxMp();
		curLoad = player.getCurrentLoad();
		maxLoad = player.getMaxLoad();
		_sp = player.getSp();
		_patk = player.getPAtk(null);
		_patkspd = player.getPAtkSpd();
		_pdef = player.getPDef(null);
		_pEvasion = player.getEvasionRate(null);
		_pAccuracy = player.getAccuracy();
		_pCrit = player.getCriticalHit(null, null);
		_mEvasion = player.getEvasionRate(null); // TODO implement Magic atk Evasion
		_mAccuracy = player.getAccuracy(); // TODO implement Magic Accuracy
		_mCrit = player.getMCriticalHit(null, null);
		_matk = player.getMAtk(null, null);
		_matkspd = player.getMAtkSpd();
		_mdef = player.getMDef(null, null);
		pvp_flag = player.getPvpFlag(); // 0=white, 1=purple, 2=purpleblink
		karma = player.getKarma();
		attack_speed = player.getAttackSpeedMultiplier();
		col_radius = player.getBaseTemplate().getCollisionRadius();
		col_height = player.getBaseTemplate().getCollisionHeight();
		hair_style = player.getAppearance().getHairStyle();
		hair_color = player.getAppearance().getHairColor();
		face = player.getAppearance().getFace();
		gm_commands = player.isGM() || player.getAccessLevel() >= Config.GM_ALTG_MIN_LEVEL ? 1 : 0;
		// builder level активирует в клиенте админские команды
		clan_id = player.getClanId();
		ally_id = player.getAllianceId();
		private_store =  player.getPrivateStoreType();
		can_crystalize = player.getSkillLevel(SkillConstants.CRYSTALLIZE) > 0 ? 1 : 0;
		pk_kills = player.getPkKills();
		pvp_kills = player.getPvpKills();
		cubics = player.getCubics().values().toArray(new L2CubicInstance[player.getCubics().size()]);
		ClanPrivs = player.getClanPrivileges();
		rec_left = player.getRecomLeft(); //c2 recommendations remaining
		rec_have = player.getRecomHave(); //c2 recommendations received
		InventoryLimit = player.GetInventoryLimit();
		class_id = player.getPlayerClass().getId();
		maxCp = player.getMaxCp();
		curCp = (int) player.getCurrentCp();
		_team = player.getTeam();
		hero = player.isHero() || player.isGM() && Config.GM_HERO_AURA ? 1 : 0; //0x01: Hero Aura and symbol
		running = player.isRunning() ? 0x01 : 0x00; //changes the Speed display on Status Window
		pledge_class = player.getPledgeClass();
		pledge_type = player.getPledgeType();
		transformation = 0; // transoformation ID TODO implement Transformations
		//attackElement = player.getAttackElement();  //TODO implement Elements
		attackElementValue = 0; // player.getAttack(attackElement);
		defenceFire = (int) player.getTemplate().getFireVuln();
		defenceWater = (int) player.getTemplate().getWaterVuln();
		defenceWind = (int) player.getTemplate().getWindVuln();
		defenceEarth = (int) player.getTemplate().getEarthVuln();
		defenceHoly = (int) player.getTemplate().getHolyVuln();
		defenceUnholy = (int) player.getTemplate().getDarkVuln();
		agathion = 0; // player.getAgathionId(); // TODO implement Agathion
		fame = 0; // player.getFame(); // TODO implement Fame
		partyRoom =  false; //TODO implement party Room 
		_moveType = player.isInWater() ? 0x01 : 0x00; // FlyingTransform  0x02
		talismans = 0; // player.getTalismanCount(); TODO implements talisman
		_jewelsLimit = 0; // player.getJewelsLimit(); TODO implements Jewels
		//_allowMap = player.isActionBlocked(Zone.BLOCKED_ACTION_MINIMAP);
		_partySubstitute = 0; // player.isPartySubstituteStarted()  ? 1 : 0; TODO implements party substitute 
		_hideHeadAccessories = false; // TODO player.hideHeadAccessories();
		_armorSetEnchant = 0; // player.getArmorSetEnchant(); TODO implement ArmorSet Enchantment
        x = player.getX();
        y = player.getY();
        z = player.getZ();

		can_writeImpl = true;



		if(addAll)
			addComponentType(UserInfoType.values());
	}

	@Override
	protected byte[] getMasks()
	{
		return _masks;
	}

	@Override
	protected void onNewMaskAdded(UserInfoType component)
	{
		calcBlockSize(component);
	}

	private void calcBlockSize(UserInfoType type)
	{
		switch(type)
		{
			case BASIC_INFO:
			{
				_initSize += type.getBlockLength() + (_name.length() * 2);
				break;
			}
			case CLAN:
			{
				_initSize += type.getBlockLength() + (_title.length() * 2);
				break;
			}
			default:
			{
				_initSize += type.getBlockLength();
				break;
			}
		}
	}

	@Override
	protected final void writeImpl() {
		if(!can_writeImpl)
			return;
		
		writeByte(0x32);
		writeInt(obj_id);
		writeInt(_initSize);
		writeShort(24);
		writeBytes(_masks);

		if(containsMask(UserInfoType.RELATION))
			writeInt(_relation);

		if(containsMask(UserInfoType.BASIC_INFO))
		{
			writeShort(UserInfoType.BASIC_INFO.getBlockLength() + (_name.length() * 2));
			writeSizedString(_name);
			writeByte(gm_commands);
			writeByte(_race);
			writeByte(sex);
			writeInt(base_class);
			writeInt(class_id);
			writeByte(level);
		}

		if(containsMask(UserInfoType.BASE_STATS))
		{
			writeShort(UserInfoType.BASE_STATS.getBlockLength());
			writeShort(_str);
			writeShort(_dex);
			writeShort(_con);
			writeShort(_int);
			writeShort(_wit);
			writeShort(_men);
			writeShort(0x00);
			writeShort(0x00);
		}

		if(containsMask(UserInfoType.MAX_HPCPMP))
		{
			writeShort(UserInfoType.MAX_HPCPMP.getBlockLength());
			writeInt(maxHp);
			writeInt(maxMp);
			writeInt(maxCp);
		}

		if(containsMask(UserInfoType.CURRENT_HPMPCP_EXP_SP))
		{
			writeShort(UserInfoType.CURRENT_HPMPCP_EXP_SP.getBlockLength());
			writeInt(curHp);
			writeInt(curMp);
			writeInt(curCp);
			writeLong(_sp);
			writeLong(_exp);
			writeDouble(_expPercent);
		}

		if(containsMask(UserInfoType.ENCHANTLEVEL))
		{
			writeShort(UserInfoType.ENCHANTLEVEL.getBlockLength());
			writeByte(_weaponEnchant);
			writeByte(_armorSetEnchant);
		}

		if(containsMask(UserInfoType.APPAREANCE))
		{
			writeShort(UserInfoType.APPAREANCE.getBlockLength());
			writeInt(hair_style);
			writeInt(hair_color);
			writeInt(face);
			writeByte(_hideHeadAccessories ?  0 : 1);  //переключения прически/головного убора
		}

		if(containsMask(UserInfoType.STATUS))
		{
			writeShort(UserInfoType.STATUS.getBlockLength());
			writeByte(mount_type);
			writeByte(private_store);
			writeByte(can_crystalize);
			writeByte(0x00);
		}

		if(containsMask(UserInfoType.STATS))
		{
			writeShort(UserInfoType.STATS.getBlockLength());
			writeShort(_weaponFlag);
			writeInt(_patk);
			writeInt(_patkspd);
			writeInt(_pdef);
			writeInt(_pEvasion);
			writeInt(_pAccuracy);
			writeInt(_pCrit);
			writeInt(_matk);
			writeInt(_matkspd);
			writeInt(_patkspd);
			writeInt(_mEvasion);
			writeInt(_mdef);
			writeInt(_mAccuracy);
			writeInt(_mCrit);
		}

		if(containsMask(UserInfoType.ELEMENTALS))
		{
			writeShort(UserInfoType.ELEMENTALS.getBlockLength());
			writeShort(defenceFire);
			writeShort(defenceWater);
			writeShort(defenceWind);
			writeShort(defenceEarth);
			writeShort(defenceHoly);
			writeShort(defenceUnholy);
		}

		if(containsMask(UserInfoType.POSITION))
		{
			writeShort(UserInfoType.POSITION.getBlockLength());
			writeInt(x);
			writeInt(y);
			writeInt(z + 16);
			writeInt(vehicle_obj_id);
		}

		if(containsMask(UserInfoType.SPEED))
		{
			writeShort(UserInfoType.SPEED.getBlockLength());
			writeShort(_runSpd);
			writeShort(_walkSpd);
			writeShort(_swimRunSpd);
			writeShort(_swimWalkSpd);
			writeShort(_flRunSpd);
			writeShort(_flWalkSpd);
			writeShort(_flyRunSpd);
			writeShort(_flyWalkSpd);
		}

		if(containsMask(UserInfoType.MULTIPLIER))
		{
			writeShort(UserInfoType.MULTIPLIER.getBlockLength());
			writeDouble(move_speed);
			writeDouble(attack_speed);
		}

		if(containsMask(UserInfoType.COL_RADIUS_HEIGHT))
		{
			writeShort(UserInfoType.COL_RADIUS_HEIGHT.getBlockLength());
			writeDouble(col_radius);
			writeDouble(col_height);
		}

		if(containsMask(UserInfoType.ATK_ELEMENTAL))
		{
			writeShort(UserInfoType.ATK_ELEMENTAL.getBlockLength());
			writeByte(0); // TODO attackElementId
			writeShort(attackElementValue);
		}

		if(containsMask(UserInfoType.CLAN))
		{
			writeShort(UserInfoType.CLAN.getBlockLength() + (_title.length() * 2));
			writeSizedString(_title);
			writeShort(pledge_type);
			writeInt(clan_id);
			writeInt(large_clan_crest_id);
			writeInt(clan_crest_id);
			writeInt(ClanPrivs);
			writeByte(_isClanLeader);
			writeInt(ally_id);
			writeInt(ally_crest_id);
			writeByte(partyRoom ? 0x01 : 0x00);
		}

		if(containsMask(UserInfoType.SOCIAL))
		{
			writeShort(UserInfoType.SOCIAL.getBlockLength());
			writeByte(pvp_flag);
			writeInt(karma);
			writeByte(0x00);
			writeByte(hero);
			writeByte(pledge_class);
			writeInt(pk_kills);
			writeInt(pvp_kills);
			writeShort(rec_left);
			writeShort(rec_have);
		}

		if(containsMask(UserInfoType.VITA_FAME))
		{
			writeShort(UserInfoType.VITA_FAME.getBlockLength());
			writeInt(0x00);
			writeByte(0x00); // Vita Bonus
			writeInt(fame);
			writeInt(0x00); // raid points
		}

		if(containsMask(UserInfoType.SLOTS))
		{
			writeShort(UserInfoType.SLOTS.getBlockLength());
			writeByte(talismans);
			writeByte(_jewelsLimit);
			writeByte(_team);
			writeByte(0x00);
			writeByte(0x00);
			writeByte(0x00);
			writeByte(0x00);
		}

		if(containsMask(UserInfoType.MOVEMENTS))
		{
			writeShort(UserInfoType.MOVEMENTS.getBlockLength());
			writeByte(_moveType);
			writeByte(running);
		}

		if(containsMask(UserInfoType.COLOR))
		{
			writeShort(UserInfoType.COLOR.getBlockLength());
			writeInt(name_color);
			writeInt(title_color);
		}

		if(containsMask(UserInfoType.INVENTORY_LIMIT))
		{
			writeShort(UserInfoType.INVENTORY_LIMIT.getBlockLength());
			writeShort(0x00);
			writeShort(0x00);
			writeShort(InventoryLimit);
			writeByte(0); // hide title - 1, 0 - no
		}

		if(containsMask(UserInfoType.UNK_3))
		{
			writeShort(UserInfoType.UNK_3.getBlockLength());
			writeInt(0x00);
			writeShort(0x00);
			writeByte(0x00);
		}
	}

	@Override
	public String getType() {
		return "[S] UIPacket";
	}
}