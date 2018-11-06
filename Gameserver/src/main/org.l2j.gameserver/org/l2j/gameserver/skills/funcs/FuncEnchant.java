package org.l2j.gameserver.skills.funcs;

import org.l2j.gameserver.model.L2ItemInstance;
import org.l2j.gameserver.skills.Env;
import org.l2j.gameserver.skills.Stats;
import org.l2j.gameserver.templates.xml.jaxb.BodyPart;
import org.l2j.gameserver.templates.xml.jaxb.CrystalType;
import org.l2j.gameserver.templates.xml.jaxb.ItemType;

public class FuncEnchant extends Func
{
	
	public FuncEnchant(Stats pStat, int pOrder, Object owner, Lambda lambda)
	{
		super(pStat, pOrder, owner);
	}
	
	@Override
	public void calc(Env env)
	{
		if ((cond != null) && !cond.test(env))
		{
			return;
		}
		L2ItemInstance item = (L2ItemInstance) funcOwner;
		CrystalType cristall = item.getCrystal();
		Enum<?> itemType = item.getType();
		
		if (cristall == CrystalType.NONE)
		{
			return;
		}
		int enchant = item.getEnchantLevel();
		
		int overenchant = 0;
		if (enchant > 3)
		{
			overenchant = enchant - 3;
			enchant = 3;
		}
		
		if ((stat == Stats.MAGIC_DEFENCE) || (stat == Stats.PHYSIC_DEFENCE))
		{
			env.value += enchant + (3 * overenchant);
			return;
		}
		
		if (stat == Stats.MAGIC_ATTACK)
		{
			switch (item.getCrystal())
			{
				case S:
					env.value += (4 * enchant) + (8 * overenchant);
					break;
				case A:
					env.value += (3 * enchant) + (6 * overenchant);
					break;
				case B:
					env.value += (3 * enchant) + (6 * overenchant);
					break;
				case C:
					env.value += (3 * enchant) + (6 * overenchant);
					break;
				case D:
					env.value += (2 * enchant) + (4 * overenchant);
					break;
			}
			return;
		}
		
		switch (item.getCrystal())
		{
			case A:
				if (itemType == ItemType.BOW)
				{
					env.value += (8 * enchant) + (16 * overenchant);
				}
				else if ((itemType == ItemType.DUALFIST) || (itemType == ItemType.DUAL) || ((itemType == ItemType.SWORD) && isTwoHand(item)))
				{
					env.value += (5 * enchant) + (10 * overenchant);
				}
				else
				{
					env.value += (4 * enchant) + (8 * overenchant);
				}
				break;
			case B:
				if (itemType == ItemType.BOW)
				{
					env.value += (6 * enchant) + (12 * overenchant);
				}
				else if ((itemType == ItemType.DUALFIST) || (itemType == ItemType.DUAL) || ((itemType == ItemType.SWORD) && (isTwoHand(item))))
				{
					env.value += (4 * enchant) + (8 * overenchant);
				}
				else
				{
					env.value += (3 * enchant) + (6 * overenchant);
				}
				break;
			case C:
				if (itemType == ItemType.BOW)
				{
					env.value += (6 * enchant) + (12 * overenchant);
				}
				else if ((itemType == ItemType.DUALFIST) || (itemType == ItemType.DUAL) || ((itemType == ItemType.SWORD) && (isTwoHand(item))))
				{
					env.value += (4 * enchant) + (8 * overenchant);
				}
				else
				{
					env.value += (3 * enchant) + (6 * overenchant);
				}
				
				break;
			case D:
				if (itemType == ItemType.BOW)
				{
					env.value += (4 * enchant) + (8 * overenchant);
				}
				else
				{
					env.value += (2 * enchant) + (4 * overenchant);
				}
				break;
			case S:
				if (itemType == ItemType.BOW)
				{
					env.value += (10 * enchant) + (20 * overenchant);
				}
				else if ((itemType == ItemType.DUALFIST) || (itemType == ItemType.DUAL) || ((itemType == ItemType.SWORD) && (isTwoHand(item))))
				{
					env.value += (4 * enchant) + (12 * overenchant);
				}
				else
				{
					env.value += (4 * enchant) + (10 * overenchant);
				}
				break;
		}
		return;
	}

	private boolean isTwoHand(L2ItemInstance item) {
		return item.getBodyPart() == BodyPart.TWO_HANDS;
	}
}
