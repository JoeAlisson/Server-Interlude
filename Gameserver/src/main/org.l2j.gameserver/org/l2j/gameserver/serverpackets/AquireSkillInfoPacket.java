package org.l2j.gameserver.serverpackets;

import java.util.ArrayList;
import java.util.List;

public class AquireSkillInfoPacket extends L2GameServerPacket  {

	private final List<Req> _reqs;
	private final int _id, _level, _spCost, _mode;
	
	private class Req {
		public int itemId;
		public int count;
		public int type;
		int unk;
		
		Req(int pType, int pItemId, int pCount, int pUnk) {
			itemId = pItemId;
			type = pType;
			count = pCount;
			unk = pUnk;
		}
	}
	
	public AquireSkillInfoPacket(int id, int level, int spCost, int mode) {
		_reqs = new ArrayList<>();
		_id = id;
		_level = level;
		_spCost = spCost;
		_mode = mode;
	}
	
	public void addRequirement(int type, int id, int count, int unk)
	{
		_reqs.add(new Req(type, id, count, unk));
	}
	
	@Override
	protected final void writeImpl() {
		writeByte(0x91);
		writeInt(_id);
		writeInt(_level);
		writeLong(_spCost);
		writeInt(_mode);
		
		writeInt(_reqs.size());
		
		for (Req temp : _reqs) {
			writeInt(temp.type);
			writeInt(temp.itemId);
			writeInt(temp.count);
			writeInt(temp.unk);
		}
	}

	@Override
	protected int packetSize() {
		return _reqs.size() * 16 + 27;
	}
}
