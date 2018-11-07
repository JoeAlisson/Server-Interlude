package org.l2j.gameserver.handler.skillhandlers;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.handler.ISkillHandler;
import org.l2j.gameserver.model.*;
import org.l2j.gameserver.model.L2Skill.SkillType;
import org.l2j.gameserver.model.actor.instance.L2DoorInstance;
import org.l2j.gameserver.model.actor.instance.L2NpcInstance;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.serverpackets.SystemMessage;
import org.l2j.gameserver.skills.Env;
import org.l2j.gameserver.skills.Formulas;
import org.l2j.gameserver.skills.funcs.Func;
import org.l2j.gameserver.templates.xml.jaxb.ItemType;

/**
 * @author Steuf
 */
public class Blow implements ISkillHandler
{
	private static final SkillType[] SKILL_IDS =
	{
		SkillType.BLOW
	};
	
	private int _successChance;
	public final static int FRONT = 50;
	public final static int SIDE = 60;
	public final static int BEHIND = 70;
	
	@Override
	public void useSkill(L2Character activeChar, L2Skill skill, L2Object[] targets)
	{
		if (activeChar.isAlikeDead())
		{
			return;
		}
		for (L2Object target2 : targets)
		{
			L2Character target = (L2Character) target2;
			if (target.isAlikeDead())
			{
				continue;
			}
			if (activeChar.isBehindTarget())
			{
				_successChance = BEHIND;
			}
			else if (activeChar.isFrontTarget())
			{
				_successChance = FRONT;
			}
			else
			{
				_successChance = SIDE;
			}
			// If skill requires Crit or skill requires behind,
			// calculate chance based on DEX, Position and on self BUFF
			if ((((skill.getCondition() & L2Skill.COND_BEHIND) != 0) && (_successChance == BEHIND)) || (((skill.getCondition() & L2Skill.COND_CRIT) != 0) && Formulas.getInstance().calcBlow(activeChar, target, _successChance)))
			{
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
				}
				L2ItemInstance weapon = activeChar.getActiveWeaponInstance();
				boolean soul = ((weapon != null) && (weapon.getChargedSoulshot() == L2ItemInstance.CHARGED_SOULSHOT) && (weapon.getType() == ItemType.DAGGER));
				boolean shld = Formulas.getInstance().calcShldUse(activeChar, target);
				
				// Crit rate base crit rate for skill, modified with STR bonus
				boolean crit = false;
				if (Formulas.getInstance().calcCrit(skill.getBaseCritRate() * 10 * Formulas.getInstance().getSTRBonus(activeChar)))
				{
					crit = true;
				}
				double damage = (int) Formulas.getInstance().calcBlowDamage(activeChar, target, skill, shld, soul);
				if (crit)
				{
					damage *= 2;
					// Vicious Stance is special after C5, and only for BLOW skills
					// Adds directly to damage
					L2Effect vicious = activeChar.getFirstEffect(312);
					if ((vicious != null) && (damage > 1))
					{
						for (Func func : vicious.getStatFuncs())
						{
							Env env = new Env();
							env.player = activeChar;
							env.target = target;
							env.skill = skill;
							env.value = damage;
							func.calc(env);
							damage = (int) env.value;
						}
					}
				}
				
				if (soul && (weapon != null))
				{
					weapon.setChargedSoulshot(L2ItemInstance.CHARGED_NONE);
				}
				if (skill.getDmgDirectlyToHP() && (target instanceof L2PcInstance))
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
					smsg.addNumber((int) damage);
					player.sendPacket(smsg);
				}
				else
				{
					target.reduceCurrentHp(damage, activeChar);
				}
				if (activeChar instanceof L2PcInstance)
				{
					activeChar.sendPacket(new SystemMessage(SystemMessageId.CRITICAL_HIT));
				}
				SystemMessage sm = new SystemMessage(SystemMessageId.YOU_DID_S1_DMG);
				sm.addNumber((int) damage);
				activeChar.sendPacket(sm);
			}
			// Possibility of a lethal strike
			if (!target.isRaid() && !(target instanceof L2DoorInstance) && !((target instanceof L2NpcInstance) && (((L2NpcInstance) target).getNpcId() == 35062)))
			{
				int chance = Rnd.get(100);
				// 2nd lethal effect activate (cp,hp to 1 or if target is npc then hp to 1)
				if ((skill.getLethalChance2() > 0) && (chance < Formulas.getInstance().calcLethal(activeChar, target, skill.getLethalChance2())))
				{
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
					activeChar.sendPacket(new SystemMessage(SystemMessageId.LETHAL_STRIKE));
				}
				else if ((skill.getLethalChance1() > 0) && (chance < Formulas.getInstance().calcLethal(activeChar, target, skill.getLethalChance1())))
				{
					if (target instanceof L2PcInstance)
					{
						L2PcInstance player = (L2PcInstance) target;
						if (!player.isInvul())
						{
							player.setCurrentCp(1); // Set CP to 1
						}
					}
					else if (target instanceof L2NpcInstance)
					{
						target.reduceCurrentHp(target.getCurrentHp() / 2, activeChar);
					}
					activeChar.sendPacket(new SystemMessage(SystemMessageId.LETHAL_STRIKE));
				}
			}
			L2Effect effect = activeChar.getFirstEffect(skill.getId());
			// Self Effect
			if ((effect != null) && effect.isSelfEffect())
			{
				effect.exit();
			}
			skill.getEffectsSelf(activeChar);
		}
	}
	
	@Override
	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}
