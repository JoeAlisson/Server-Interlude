package org.l2j.gameserver.handler.skillhandlers;

import org.l2j.commons.Config;
import org.l2j.commons.lib.Log;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.datatables.SkillTable;
import org.l2j.gameserver.handler.ISkillHandler;
import org.l2j.gameserver.model.*;
import org.l2j.gameserver.model.L2Skill.SkillType;
import org.l2j.gameserver.model.actor.instance.*;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.EtcStatusUpdate;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.skills.Formulas;
import org.l2j.gameserver.skills.effects.EffectCharge;
import org.l2j.gameserver.templates.xml.jaxb.ItemType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Pdam implements ISkillHandler
{
	// allTemplates the items ids that this handler knowns
	private static Logger _log = LoggerFactory.getLogger(Pdam.class.getName());
	
	/*
	 * (non-Javadoc)
	 * @see org.l2j.gameserver.handler.IItemHandler#useItem(org.l2j.gameserver.model.L2PcInstance, org.l2j.gameserver.model.L2ItemInstance)
	 */
	private static final SkillType[] SKILL_IDS =
	{
		SkillType.PDAM,
	/* SkillType.CHARGEDAM */
	};
	
	/*
	 * (non-Javadoc)
	 * @see org.l2j.gameserver.handler.IItemHandler#useItem(org.l2j.gameserver.model.L2PcInstance, org.l2j.gameserver.model.L2ItemInstance)
	 */
	@Override
	public void useSkill(L2Character activeChar, L2Skill skill, L2Object[] targets)
	{
		if (activeChar.isAlikeDead())
		{
			return;
		}
		
		int damage = 0;
		
		if (Config.DEBUG)
		{
			if (Config.DEBUG)
			{
				_log.debug("Begin Skill processing in Pdam.java " + skill.getSkillType());
			}
		}
		
		for (L2Object target2 : targets)
		{
			L2Character target = (L2Character) target2;
			Formulas f = Formulas.getInstance();
			L2ItemInstance weapon = activeChar.getActiveWeaponInstance();
			if ((activeChar instanceof L2PcInstance) && (target instanceof L2PcInstance) && target.isAlikeDead() && target.isFakeDeath())
			{
				target.stopFakeDeath(null);
			}
			else if (target.isAlikeDead())
			{
				continue;
			}
			
			boolean dual = activeChar.isUsingDualWeapon();
			boolean shld = f.calcShldUse(activeChar, target);
			// PDAM critical chance not affected by buffs, only by STR. Only some skills are meant to crit.
			boolean crit = false;
			if (skill.getBaseCritRate() > 0)
			{
				crit = f.calcCrit(skill.getBaseCritRate() * 10 * f.getSTRBonus(activeChar));
			}
			
			boolean soul = ((weapon != null) && (weapon.getChargedSoulshot() == L2ItemInstance.CHARGED_SOULSHOT) && (weapon.getType() != ItemType.DAGGER));
			
			if (!crit && ((skill.getCondition() & L2Skill.COND_CRIT) != 0))
			{
				damage = 0;
			}
			else
			{
				damage = (int) f.calcPhysDam(activeChar, target, skill, shld, false, dual, soul);
			}
			if (crit)
			{
				damage *= 2; // PDAM Critical damage always 2x and not affected by buffs
			}
			
			if ((damage > 5000) && (activeChar instanceof L2PcInstance))
			{
				String name = "";
				if (target instanceof L2RaidBossInstance)
				{
					name = "RaidBoss ";
				}
				if (target instanceof L2NpcInstance)
				{
					name += target.getName() + "(" + ((L2NpcInstance) target).getTemplateId() + ")";
				}
				if (target instanceof L2PcInstance)
				{
					name = target.getName() + "(" + target.getObjectId() + ") ";
				}
				name += target.getLevel() + " lvl";
				Log.add(activeChar.getName() + "(" + activeChar.getObjectId() + ") " + activeChar.getLevel() + " lvl did damage " + damage + " with skill " + skill.getName() + "(" + skill.getId() + ") to " + name, "damage_pdam");
			}
			
			if (soul && (weapon != null))
			{
				weapon.setChargedSoulshot(L2ItemInstance.CHARGED_NONE);
			}
			
			if (damage > 0)
			{
				activeChar.sendDamageMessage(target, damage, false, crit, false);
				
				if (skill.hasEffects())
				{
					if (target.reflectSkill(skill))
					{
						activeChar.stopSkillEffects(skill.getId());
						skill.getEffects(null, activeChar);
						SystemMessage sm = new SystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT);
						sm.addSkillName(skill.getId());
						activeChar.sendPacket(sm);
					}
					else
					{
						// activate attacked effects, if any
						target.stopSkillEffects(skill.getId());
						if (f.calcSkillSuccess(activeChar, target, skill, false, false, false))
						{
							skill.getEffects(activeChar, target);
							
							SystemMessage sm = new SystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT);
							sm.addSkillName(skill.getId());
							target.sendPacket(sm);
						}
						else
						{
							SystemMessage sm = new SystemMessage(SystemMessageId.S1_WAS_UNAFFECTED_BY_S2);
							sm.addString(target.getName());
							sm.addSkillName(skill.getDisplayId());
							activeChar.sendPacket(sm);
						}
					}
				}
				
				// Success of lethal effect
				int chance = Rnd.get(100);
				if (!target.isRaid() && (chance < skill.getLethalChance1()) && !(target instanceof L2DoorInstance) && !((target instanceof L2NpcInstance) && (((L2NpcInstance) target).getNpcId() == 35062)))
				{
					// 1st lethal effect activate (cp to 1 or if target is npc then hp to 50%)
					if ((skill.getLethalChance2() > 0) && (chance >= skill.getLethalChance2()))
					{
						if (target instanceof L2PcInstance)
						{
							L2PcInstance player = (L2PcInstance) target;
							if (!player.isInvul())
							{
								player.setCurrentCp(1); // Set CP to 1
								player.reduceCurrentHp(damage, activeChar);
							}
						}
						else if (target instanceof L2MonsterInstance) // If is a monster remove first damage and after 50% of current hp
						{
							target.reduceCurrentHp(damage, activeChar);
							target.reduceCurrentHp(target.getCurrentHp() / 2, activeChar);
						}
					}
					else
					// 2nd lethal effect activate (cp,hp to 1 or if target is npc then hp to 1)
					{
						// If is a monster damage is (CurrentHp - 1) so HP = 1
						if (target instanceof L2NpcInstance)
						{
							target.reduceCurrentHp(target.getCurrentHp() - 1, activeChar);
						}
						else if (target instanceof L2PcInstance) // If is a active reader set his HP and CP to 1
						{
							L2PcInstance player = (L2PcInstance) target;
							if (!player.isInvul())
							{
								player.setCurrentHp(1);
								player.setCurrentCp(1);
							}
						}
					}
					// Lethal Strike was succefful!
					activeChar.sendPacket(new SystemMessage(SystemMessageId.LETHAL_STRIKE_SUCCESSFUL));
				}
				else
				{
					// Make damage directly to HP
					if (skill.getDmgDirectlyToHP())
					{
						if (target instanceof L2PcInstance)
						{
							L2PcInstance player = (L2PcInstance) target;
							if (!player.isInvul())
							{
								if (damage >= player.getCurrentHp())
								{
									if (player.isInDuel())
									{
										player.setCurrentHp(1);
									}
									else
									{
										player.setCurrentHp(0);
										if (player.isInOlympiadMode())
										{
											player.abortAttack();
											player.abortCast();
											player.getStatus().stopHpMpRegeneration();
										}
										else
										{
											player.doDie(activeChar);
										}
									}
								}
								else
								{
									player.setCurrentHp(player.getCurrentHp() - damage);
								}
							}
							
							SystemMessage smsg = new SystemMessage(SystemMessageId.S1_GAVE_YOU_S2_DMG);
							smsg.addString(activeChar.getName());
							smsg.addNumber(damage);
							player.sendPacket(smsg);
							
						}
						else
						{
							target.reduceCurrentHp(damage, activeChar);
						}
					}
					else
					{
						target.reduceCurrentHp(damage, activeChar);
					}
				}
			}
			else
			// No - damage
			{
				activeChar.sendPacket(new SystemMessage(SystemMessageId.ATTACK_FAILED));
			}
			
			if ((skill.getId() == 345) || (skill.getId() == 346)) // Sonic Rage or Raging Force
			{
				EffectCharge effect = (EffectCharge) activeChar.getFirstEffect(L2Effect.EffectType.CHARGE);
				if (effect != null)
				{
					int effectcharge = effect.getLevel();
					if (effectcharge < 7)
					{
						effectcharge++;
						effect.addNumCharges(1);
						if (activeChar instanceof L2PcInstance)
						{
							activeChar.sendPacket(new EtcStatusUpdate((L2PcInstance) activeChar));
							SystemMessage sm = new SystemMessage(SystemMessageId.FORCE_INCREASED_TO_S1);
							sm.addNumber(effectcharge);
							activeChar.sendPacket(sm);
						}
					}
					else
					{
						SystemMessage sm = new SystemMessage(SystemMessageId.FORCE_MAXLEVEL_REACHED);
						activeChar.sendPacket(sm);
					}
				}
				else
				{
					if (skill.getId() == 345) // Sonic Rage
					{
						L2Skill dummy = SkillTable.getInstance().getInfo(8, 7); // Lv7 Sonic Focus
						dummy.getEffects(activeChar, activeChar);
					}
					else if (skill.getId() == 346) // Raging Force
					{
						L2Skill dummy = SkillTable.getInstance().getInfo(50, 7); // Lv7 Focused Force
						dummy.getEffects(activeChar, activeChar);
					}
				}
			}
			// self Effect :]
			L2Effect effect = activeChar.getFirstEffect(skill.getId());
			if ((effect != null) && effect.isSelfEffect())
			{
				// Replace old effect with new one.
				effect.exit();
			}
			skill.getEffectsSelf(activeChar);
		}
		
		if (skill.isSuicideAttack())
		{
			activeChar.doDie(null);
			activeChar.setCurrentHp(0);
		}
	}
	
	@Override
	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}
