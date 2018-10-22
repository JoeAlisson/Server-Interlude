package org.l2j.gameserver.skills.conditions;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.skills.Env;
import org.l2j.gameserver.templates.xml.jaxb.Race;


/**
 * @author mkizub TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class ConditionPlayerRace extends Condition {
	private final Race _race;
	
	public ConditionPlayerRace(Race race)
	{
		_race = race;
	}
	
	@Override
	public boolean testImpl(Env env)
	{
		if (!(env.player instanceof L2PcInstance))
		{
			return false;
		}
		return ((L2PcInstance) env.player).getRace() == _race;
	}
}
