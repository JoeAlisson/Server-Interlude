package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.model.L2Character;
import org.l2j.gameserver.model.L2Object;

/**
 * sample 0000: 8e d8 a8 10 48 10 04 00 00 01 00 00 00 01 00 00 ....H........... 0010: 00 d8 a8 10 48 ....H format ddddd d
 * @version $Revision: 1.4.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class MagicSkillLaunched extends L2GameServerPacket
{
	private final int _charObjId;
	private final int _skillId;
	private final int _skillLevel;
	private final int _numberOfTargets;
	private L2Object[] _targets;
	private final int _singleTargetId;
	
	public MagicSkillLaunched(L2Character cha, int skillId, int skillLevel, L2Object[] targets)
	{
		_charObjId = cha.getObjectId();
		_skillId = skillId;
		_skillLevel = skillLevel;
		_numberOfTargets = targets.length;
		_targets = targets;
		_singleTargetId = 0;
	}
	
	public MagicSkillLaunched(L2Character cha, int skillId, int skillLevel)
	{
		_charObjId = cha.getObjectId();
		_skillId = skillId;
		_skillLevel = skillLevel;
		_numberOfTargets = 1;
		_singleTargetId = cha.getTargetId();
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x76);
		writeInt(_charObjId);
		writeInt(_skillId);
		writeInt(_skillLevel);
		writeInt(_numberOfTargets); // also failed or not?
		if ((_singleTargetId != 0) || (_numberOfTargets == 0))
		{
			writeInt(_singleTargetId);
		}
		else
		{
			for (L2Object target : _targets)
			{
				try
				{
					writeInt(target.getObjectId());
				}
				catch (NullPointerException e)
				{
					writeInt(0); // untested
				}
			}
		}
	}
}
