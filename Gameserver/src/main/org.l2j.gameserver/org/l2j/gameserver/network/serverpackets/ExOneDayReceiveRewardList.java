package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ExOneDayReceiveRewardList extends L2GameServerPacket {

	private final int _dayRemainTime;
	private final int _weekRemainTime;
	private final int _monthRemainTime;
	private final int _classId;
	private final int _dayOfWeek;
	private final List<Object> _missions = new ArrayList<Object>();

	public ExOneDayReceiveRewardList(L2PcInstance player)
	{
		_dayRemainTime = 5;
		_weekRemainTime = 2;
		_monthRemainTime = 3;
		_classId = player.getBaseClass();
		_dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

		// TODO add missions
	}

	@Override
	protected void writeImpl() {
		writeByte(0xFE);
		writeShort(0x1A8);
		writeInt(_dayRemainTime);
		writeInt(_weekRemainTime);
		writeInt(_monthRemainTime);
		writeByte(0x14);
		writeInt(_classId);
		writeInt(_dayOfWeek);
		writeInt(_missions.size());
		for(Object mission : _missions)
		{
			writeShort(0); // mission id
			writeByte(0); ;// mission status
			writeByte(0x01);
			writeInt(0); // mission progress
			writeInt(0); // mission required progress
		}
	}
}