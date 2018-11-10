package org.l2j.gameserver.model.actor.stat;

import org.l2j.commons.Config;
import org.l2j.commons.database.AccountRepository;
import org.l2j.gameserver.model.L2Character;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.actor.instance.L2PetInstance;
import org.l2j.gameserver.model.base.Experience;
import org.l2j.gameserver.model.zone.Zone;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.skills.Stats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.isNull;
import static org.l2j.commons.database.DatabaseAccess.getRepository;

public class PcStat extends PlayableStat {

	private static Logger _log = LoggerFactory.getLogger(L2PcInstance.class);

	private int _oldMaxHp; // stats watch
	private int _oldMaxMp; // stats watch

	public PcStat(L2PcInstance activeChar)
	{
		super(activeChar);
	}

	@Override
	public boolean addExp(long value) {
		L2PcInstance activeChar = getActiveChar();

		if (!activeChar.isCursedWeaponEquiped() && (activeChar.getKarma() > 0) && (activeChar.isGM() || !activeChar.isInsideZone(Zone.PVP))) {
			int karmaLost = activeChar.calculateKarmaLost(value);
			if (karmaLost > 0) {
				activeChar.setKarma(activeChar.getKarma() - karmaLost);
			}
		}
		// Player is Gm and access level is below or equal to GM_DONT_TAKE_EXPSP and is in party, don't give Xp
		if (getActiveChar().isGM() && (getActiveChar().getAccessLevel() <= Config.GM_DONT_TAKE_EXPSP) && getActiveChar().isInParty()) {
			return false;
		}
		
		if (!super.addExp(value)) {
			return false;
		}

		activeChar.sendPacket(new UserInfo(activeChar));
		return true;
	}
	
	/**
	 * Add Experience and SP rewards to the L2PcInstance, remove its Karma (if necessary) and Launch increase level task.<BR>
	 * <BR>
	 * <B><U> Actions </U> :</B><BR>
	 * <BR>
	 * <li>Remove Karma when the reader kills L2MonsterInstance</li> <li>Send a Server->Client packet StatusUpdate to the L2PcInstance</li> <li>Send a Server->Client System Message to the L2PcInstance</li> <li>If the L2PcInstance increases it's level, send a Server->Client packet SocialAction
	 * (broadcast)</li> <li>If the L2PcInstance increases it's level, manage the increase level task (Max MP, Max MP, Recommandation, Expertise and beginner skills...)</li> <li>If the L2PcInstance increases it's level, send a Server->Client packet UserInfo to the L2PcInstance</li><BR>
	 * <BR>
	 * @param addToExp The Experience value to add
	 * @param addToSp The SP value to add
	 */
	@Override
	public boolean addExpAndSp(long addToExp, int addToSp) {
		float ratioTakenByPet;
		// Player is Gm and acces level is below or equal to GM_DONT_TAKE_EXPSP and is in party, don't give Xp/Sp
		L2PcInstance activeChar = getActiveChar();
		if (activeChar.isGM() && (activeChar.getAccessLevel() <= Config.GM_DONT_TAKE_EXPSP) && activeChar.isInParty()) {
			return false;
		}
		
		// if this player has a pet that takes from the owner's Exp, give the pet Exp now
		if (activeChar.getPet() instanceof L2PetInstance) {
			L2PetInstance pet = (L2PetInstance) activeChar.getPet();
			ratioTakenByPet = pet.getPetData().getOwnerExpTaken();
			
			// only give exp/sp to the pet by taking from the owner if the pet has a non-zero, positive ratio
			// allow possible customizations that would have the pet earning more than 100% of the owner's exp/sp
			if ((ratioTakenByPet > 0) && !pet.isDead()) {
				pet.addExpAndSp((long) (addToExp * ratioTakenByPet), (int) (addToSp * ratioTakenByPet));
			}
			// now adjust the max ratio to avoid the owner earning negative exp/sp
			if (ratioTakenByPet > 1) {
				ratioTakenByPet = 1;
			}
			addToExp = (long) (addToExp * (1 - ratioTakenByPet));
			addToSp = (int) (addToSp * (1 - ratioTakenByPet));
		}
		
		if (!super.addExpAndSp(addToExp, addToSp)) {
			return false;
		}
		
		// Send a Server->Client System Message to the L2PcInstance
		SystemMessage sm = new SystemMessage(SystemMessageId.YOU_EARNED_S1_EXP_AND_S2_SP);
		sm.addNumber((int) addToExp);
		sm.addNumber(addToSp);
		getActiveChar().sendPacket(sm);
		return true;
	}
	
	@Override
	public boolean removeExpAndSp(long addToExp, int addToSp) {
		if (!super.removeExpAndSp(addToExp, addToSp)) {
			return false;
		}

		SystemMessage sm = new SystemMessage(SystemMessageId.EXP_DECREASED_BY_S1);
		sm.addNumber((int) addToExp);
		getActiveChar().sendPacket(sm);
		sm = new SystemMessage(SystemMessageId.SP_DECREASED_S1);
		sm.addNumber(addToSp);
		getActiveChar().sendPacket(sm);
		return true;
	}
	
	@Override
	public final boolean addLevel(byte value) {
		if ((getLevel() + value) > (Experience.MAX_LEVEL - 1)) {
			return false;
		}
		
		boolean levelIncreased = super.addLevel(value);
		
		if (levelIncreased) {
			/**
			 * If there are no characters on the server, the bonuses will be applied to the first character that becomes level 6
			 * and end if this character reaches level 25 or above;
			 *
			 * If the first character that becomes level 6 is deleted, the rest of the characters may not receive the new character bonus;
			 *
			 * If the first character to become level 6 loses a level, and the player makes another character level 6,
			 * the bonus will be applied to only the first character to achieve level 6;
			 *
			 * If the character loses a level after reaching level 25, the character may not receive the bonus;
             *
			 */
			if (!Config.ALT_GAME_NEW_CHAR_ALWAYS_IS_NEWBIE) {
                L2PcInstance activeChar = getActiveChar();
				if ((activeChar.getLevel() >= Experience.MIN_NEWBIE_LEVEL) && (activeChar.getLevel() < Experience.MAX_NEWBIE_LEVEL)
                        && !activeChar.isNewbie()) {

                    AccountRepository repository = getRepository(AccountRepository.class);
                    repository.findById(activeChar.getAccountName()).ifPresent( account -> {
                        if(account.getNewbieCharacterId() == 0) {
                            account.setNewbieCharacterId(activeChar.getObjectId());
                            repository.save(account);
                        }
                    });
				}

				if ((activeChar.getLevel() >= 25) && activeChar.isNewbie()) {
					activeChar.setNewbie(false);
					if (Config.DEBUG) {
						_log.info("Newbie character ended: {}", getActiveChar().getObjectId());
					}
				}
			}
			
			getActiveChar().setCurrentCp(getMaxCp());
			getActiveChar().broadcastPacket(new SocialAction(getActiveChar().getObjectId(), 15));
			getActiveChar().sendPacket(new SystemMessage(SystemMessageId.YOU_INCREASED_YOUR_LEVEL));
		}
		
		getActiveChar().rewardSkills(); // Give Expertise skill of this level
		if (getActiveChar().getClan() != null) {
			getActiveChar().getClan().updateClanMember(getActiveChar());
			getActiveChar().getClan().broadcastToOnlineMembers(new PledgeShowMemberListUpdate(getActiveChar()));
		}

		if (getActiveChar().isInParty()) {
			getActiveChar().getParty().recalculatePartyLevel(); // Recalculate the party level
		}
		
		StatusUpdate su = new StatusUpdate(getActiveChar().getObjectId());
		su.addAttribute(StatusUpdate.LEVEL, getLevel());
		su.addAttribute(StatusUpdate.MAX_CP, getMaxCp());
		su.addAttribute(StatusUpdate.MAX_HP, getMaxHp());
		su.addAttribute(StatusUpdate.MAX_MP, getMaxMp());
		getActiveChar().sendPacket(su);

		// Update the overloaded status of the L2PcInstance
		getActiveChar().refreshOverloaded();
		// Update the expertise status of the L2PcInstance
		getActiveChar().refreshExpertisePenalty();
		// Send a Server->Client packet UserInfo to the L2PcInstance
		getActiveChar().sendPacket(new UserInfo(getActiveChar()));
		
		return levelIncreased;
	}
	
	@Override
	public boolean addSp(long value) {
		if (!super.addSp(value)) {
			return false;
		}
		
		StatusUpdate su = new StatusUpdate(getActiveChar().getObjectId());
		su.addAttribute(StatusUpdate.SP, (int) getSp());
		getActiveChar().sendPacket(su);
		
		return true;
	}
	
	@Override
	public final long getExpForLevel(int level)
	{
		return Experience.LEVEL[level];
	}

	@Override
	public int getAccuracy() {
		if (isNull(getActiveChar())) {
			return 0;
		}

		//TODO use Base Stats to calc
		return (int) (calcStat(Stats.ACCURACY, 0, null, null) / getActiveChar().getWeaponExpertisePenalty());
	}

	@Override
	public int getEvasionRate(L2Character target) {
        if (isNull(getActiveChar())) {
            return 1;
        }

        //TODO use Base Stats to calc
        return (int) (calcStat(Stats.EVASION_RATE, 0, target, null) / getActiveChar().getArmourExpertisePenalty());
	}

	@Override
	public final L2PcInstance getActiveChar()
	{
		return (L2PcInstance) super.getActiveChar();
	}
	
	@Override
	public final long getExp() {
		if (getActiveChar().isSubClassActive()) {
			return getActiveChar().getSubClasses().get(getActiveChar().getClassIndex()).getExp();
		}
		return super.getExp();
	}
	
	@Override
	public final void setExp(long value) {
		if (getActiveChar().isSubClassActive()) {
			getActiveChar().getSubClasses().get(getActiveChar().getClassIndex()).setExp(value);
		} else {
			super.setExp(value);
		}
	}
	
	@Override
	public final int getLevel() {
		if (getActiveChar().isSubClassActive()) {
			return getActiveChar().getSubClasses().get(getActiveChar().getClassIndex()).getLevel();
		}
		return super.getLevel();
	}
	
	@Override
	public final void setLevel(int value) {
		if (value > (Experience.MAX_LEVEL - 1)) {
			value = Experience.MAX_LEVEL - 1;
		}
		
		if (getActiveChar().isSubClassActive()) {
			getActiveChar().getSubClasses().get(getActiveChar().getClassIndex()).setLevel(value);
		} else {
			super.setLevel(value);
		}
	}
	
	@Override
	public final int getMaxHp() {
		int val = super.getMaxHp();
		if (val != _oldMaxHp) {
			_oldMaxHp = val;
			if (getActiveChar().getStatus().getCurrentHp() != val) {
				getActiveChar().getStatus().setCurrentHp(getActiveChar().getStatus().getCurrentHp()); // trigger start of regeneration
			}
		}
		return val;
	}
	
	@Override
	public final int getMaxMp() {
		int val = super.getMaxMp();
		
		if (val != _oldMaxMp) {
			_oldMaxMp = val;

			if (getActiveChar().getStatus().getCurrentMp() != val) {
				getActiveChar().getStatus().setCurrentMp(getActiveChar().getStatus().getCurrentMp()); // trigger start of regeneration
			}
		}
		return val;
	}
	
	@Override
	public final long getSp() {
		if (getActiveChar().isSubClassActive()) {
			return getActiveChar().getSubClasses().get(getActiveChar().getClassIndex()).getSp();
		}
		return super.getSp();
	}
	
	@Override
	public final void setSp(long value) {
		if (getActiveChar().isSubClassActive()) {
			getActiveChar().getSubClasses().get(getActiveChar().getClassIndex()).setSp(value);
		} else {
			super.setSp(value);
		}
	}
}
