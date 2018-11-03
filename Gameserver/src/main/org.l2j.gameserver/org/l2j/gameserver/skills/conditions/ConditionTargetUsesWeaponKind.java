package org.l2j.gameserver.skills.conditions;

import org.l2j.gameserver.skills.Env;
import org.l2j.gameserver.templates.xml.jaxb.Weapon;

/**
 * @author mkizub
 */
public class ConditionTargetUsesWeaponKind extends Condition
{
	
	private final int _weaponMask;
	
	public ConditionTargetUsesWeaponKind(int weaponMask)
	{
		_weaponMask = weaponMask;
	}
	
	@Override
	public boolean testImpl(Env env)
	{
		
		if (env.target == null)
		{
			return false;
		}
		
		Weapon item = env.target.getActiveWeaponItem();
		
		if (item == null)
		{
			return false;
		}
		
		return (1 << item.getType().ordinal() & _weaponMask) != 0;
	}
}
