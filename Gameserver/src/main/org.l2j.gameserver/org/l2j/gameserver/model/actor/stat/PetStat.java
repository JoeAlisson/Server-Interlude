package org.l2j.gameserver.model.actor.stat;

import org.l2j.gameserver.model.L2Character;
import org.l2j.gameserver.model.L2PetDataTable;
import org.l2j.gameserver.model.L2Skill;
import org.l2j.gameserver.model.actor.instance.L2PetInstance;
import org.l2j.gameserver.model.base.Experience;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.serverpackets.PetInfo;
import org.l2j.gameserver.serverpackets.StatusUpdate;
import org.l2j.gameserver.serverpackets.SystemMessage;
import org.l2j.gameserver.skills.Stats;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class PetStat extends SummonStat  {

	public PetStat(L2PetInstance activeChar)
	{
		super(activeChar);
	}

	public boolean addExp(int value) {
		if (!super.addExp(value)) {
			return false;
		}
		
		getActiveChar().broadcastPacket(new PetInfo(getActiveChar()));
		// The PetInfo packet wipes the PartySpelled (list of active spells' icons). Re-add them
		getActiveChar().updateEffectIcons(true);
		return true;
	}
	
	@Override
	public boolean addExpAndSp(long addToExp, int addToSp) {
		if (!super.addExpAndSp(addToExp, addToSp)) {
			return false;
		}
		
		SystemMessage sm = new SystemMessage(SystemMessageId.PET_EARNED_S1_EXP);
		sm.addNumber((int) addToExp);
		getActiveChar().getOwner().sendPacket(sm);
		return true;
	}
	
	@Override
	public final boolean addLevel(byte value) {
		if ((getLevel() + value) > (Experience.MAX_LEVEL - 1)) {
			return false;
		}
		
		boolean levelIncreased = super.addLevel(value);
		
		// Sync up exp with current level
		if ((getExp() > getExpForLevel(getLevel() + 1)) || (getExp() < getExpForLevel(getLevel()))) {
			setExp(Experience.LEVEL[getLevel()]);
		}
		
		if (levelIncreased) {
			getActiveChar().getOwner().sendMessage("Your pet has increased it's level.");
		}
		
		StatusUpdate su = new StatusUpdate(getActiveChar().getObjectId());
		su.addAttribute(StatusUpdate.LEVEL, getLevel());
		su.addAttribute(StatusUpdate.MAX_HP, getMaxHp());
		su.addAttribute(StatusUpdate.MAX_MP, getMaxMp());
		getActiveChar().broadcastPacket(su);
		
		// Send a Server->Client packet PetInfo to the L2PcInstance
		getActiveChar().getOwner().sendPacket(new PetInfo(getActiveChar()));
		// The PetInfo packet wipes the PartySpelled (list of active spells' icons). Re-add them
		getActiveChar().updateEffectIcons(true);
		
		if (getActiveChar().getControlItem() != null) {
			getActiveChar().getControlItem().setEnchantLevel(getLevel());
		}
		
		return levelIncreased;
	}
	
	@Override
	public final long getExpForLevel(int level) {
		return L2PetDataTable.getInstance().getPetData(getActiveChar().getNpcId(), level).getExpMax();
	}


	@Override
	public L2PetInstance getActiveChar()
	{
		return (L2PetInstance) super.getActiveChar();
	}
	
	public final int getFeedBattle()
	{
		return getActiveChar().getPetData().getFeedbattle();
	}
	
	public final int getFeedNormal()
	{
		return getActiveChar().getPetData().getFeednormal();
	}
	
	@Override
	public void setLevel(int value) {
		getActiveChar().stopFeed();
		super.setLevel(value);
		
		getActiveChar().setPetData(L2PetDataTable.getInstance().getPetData(getActiveChar().getNpcId(), getLevel()));
		getActiveChar().startFeed(false);
		
		if (nonNull(getActiveChar().getControlItem())) {
			getActiveChar().getControlItem().setEnchantLevel(getLevel());
		}
	}
	
	public final int getMaxFeed()
	{
		return getActiveChar().getPetData().getFeedMax();
	}
	
	@Override
	public int getMaxHp()
	{
		return (int) calcStat(Stats.MAX_HP, getActiveChar().getPetData().getHpMax(), null, null);
	}
	
	@Override
	public int getMaxMp()
	{
		return (int) calcStat(Stats.MAX_MP, getActiveChar().getPetData().getMpMax(), null, null);
	}
	
	@Override
	public int getMAtk(L2Character target, L2Skill skill) {
		double attack = getActiveChar().getPetData().getMatk();
		Stats stat = isNull(skill) ? null : skill.getStat();
		if (nonNull(stat )) {
			switch (stat) {
				case AGGRESSION:
					attack += getActiveChar().getAggression();
					break;
				case BLEED:
					attack += getActiveChar().getBleed();
					break;
				case POISON:
					attack += getActiveChar().getPoison();
					break;
				case STUN:
					attack += getActiveChar().getStun();
					break;
				case ROOT:
					attack += getActiveChar().getRoot();
					break;
				case MOVEMENT:
					attack += getActiveChar().getMovement();
					break;
				case CONFUSION:
					attack += getActiveChar().getConfusion();
					break;
				case SLEEP:
					attack += getActiveChar().getSleep();
					break;
				case FIRE:
					attack += getActiveChar().getFire();
					break;
				case WIND:
					attack += getActiveChar().getWind();
					break;
				case WATER:
					attack += getActiveChar().getWater();
					break;
				case EARTH:
					attack += getActiveChar().getEarth();
					break;
				case HOLY:
					attack += getActiveChar().getHoly();
					break;
				case DARK:
					attack += getActiveChar().getDark();
					break;
			}
		}
		if (nonNull(skill)) {
			attack += skill.getPower();
		}
		return (int) calcStat(Stats.MAGIC_ATTACK, attack, target, skill);
	}
	
	@Override
	public int getMDef(L2Character target, L2Skill skill) {
		return (int) calcStat(Stats.MAGIC_DEFENCE, getActiveChar().getPetData().getMdef(), target, skill);
	}
	
	@Override
	public int getPAtk(L2Character target) {
		return (int) calcStat(Stats.PHYSIC_ATTACK, getActiveChar().getPetData().getPatk(), target, null);
	}
	
	@Override
	public int getPDef(L2Character target) {
		return (int) calcStat(Stats.PHYSIC_DEFENCE, getActiveChar().getPetData().getPdef(), target, null);
	}
	
	@Override
	public int getAccuracy()
	{
		return (int) calcStat(Stats.ACCURACY, getActiveChar().getPetData().getAcc(), null, null);
	}
	
	@Override
	public int getCriticalHit(L2Character target, L2Skill skill) {
		return (int) calcStat(Stats.CRITICAL_RATE, getActiveChar().getPetData().getCrit(), target, null);
	}
	
	@Override
	public int getEvasionRate(L2Character target) {
		return (int) calcStat(Stats.EVASION_RATE, getActiveChar().getPetData().getEvasion(), target, null);
	}
	
	@Override
	public int getRunSpeed() {
		return (int) calcStat(Stats.RUN_SPEED, getActiveChar().getPetData().getSpeed(), null, null);
	}
	
	@Override
	public int getPAtkSpd() {
		return (int) calcStat(Stats.PHYSIC_ATTACK_SPEED, getActiveChar().getPetData().getAtkSpeed(), null, null);
	}
	
	@Override
	public int getMAtkSpd() {
		return (int) calcStat(Stats.MAGIC_ATTACK_SPEED, getActiveChar().getPetData().getCastSpeed(), null, null);
	}
}
