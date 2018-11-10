package org.l2j.gameserver.handler.skillhandlers;

import org.l2j.gameserver.handler.ISkillHandler;
import org.l2j.gameserver.model.*;
import org.l2j.gameserver.model.L2Skill.SkillType;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

public class FishingSkill implements ISkillHandler
{
	// private static Logger logger = LoggerFactory.getLogger(SiegeFlag.class.getName());
	private static final SkillType[] SKILL_IDS =
	{
		SkillType.PUMPING,
		SkillType.REELING
	};
	
	@Override
	public void useSkill(L2Character activeChar, L2Skill skill, L2Object[] targets)
	{
		if ((activeChar == null) || !(activeChar instanceof L2PcInstance))
		{
			return;
		}
		
		L2PcInstance player = (L2PcInstance) activeChar;
		
		L2Fishing fish = player.GetFishCombat();
		if (fish == null)
		{
			if (skill.getSkillType() == SkillType.PUMPING)
			{
				// Pumping skill is available only while fishing
				player.sendPacket(new SystemMessage(SystemMessageId.CAN_USE_PUMPING_ONLY_WHILE_FISHING));
			}
			else if (skill.getSkillType() == SkillType.REELING)
			{
				// Reeling skill is available only while fishing
				player.sendPacket(new SystemMessage(SystemMessageId.CAN_USE_REELING_ONLY_WHILE_FISHING));
			}
			player.sendPacket(new ActionFailed());
			return;
		}
		var weaponItem = player.getActiveWeaponInstance();
		L2ItemInstance weaponInst = activeChar.getActiveWeaponInstance();
		if ((weaponInst == null) || (weaponItem == null))
		{
			return;
		}
		int SS = 1;
		int pen = 0;
		if (weaponInst.getChargedFishshot())
		{
			SS = 2;
		}
		double gradebonus = 1 + (weaponItem.getCrystal().ordinal() * 0.1);
		int dmg = (int) (skill.getPower() * gradebonus * SS);
		if (player.getSkillLevel(1315) <= (skill.getLevel() - 2)) // 1315 - Fish Expertise
		{// Penalty
			player.sendPacket(new SystemMessage(SystemMessageId.REELING_PUMPING_3_LEVELS_HIGHER_THAN_FISHING_PENALTY));
			pen = 50;
			int penatlydmg = dmg - pen;
			if (player.isGM())
			{
				player.sendMessage("Dmg w/o penalty = " + dmg);
			}
			dmg = penatlydmg;
		}
		if (SS > 1)
		{
			weaponInst.setChargedFishshot(false);
		}
		if (skill.getSkillType() == SkillType.REELING)// Realing
		{
			fish.useRealing(dmg, pen);
		}
		else
		// Pumping
		{
			fish.usePomping(dmg, pen);
		}
	}
	
	@Override
	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}
