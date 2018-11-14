package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.datatables.ClanTable;
import org.l2j.gameserver.instancemanager.CastleManager;

import static java.util.Objects.nonNull;

public class ExShowCastleInfo extends L2GameServerPacket {

	@Override
	protected final void writeImpl() {
		var castles = CastleManager.getInstance().getCastles();

		writeByte(0xFE);
		writeShort(0x14);
		writeInt(castles.size());
		for(var castle : castles)
		{
			writeInt(castle.getCastleId());

			var clan = ClanTable.getInstance().getClan(castle.getOwnerId());
			writeString(nonNull(clan) ? clan.getName() : "");

			writeInt(castle.getTaxPercent());
			writeInt((int) (castle.getSiegeDate().getTimeInMillis() / 1000));
			writeByte(castle.getSiege().getIsInProgress());
			writeByte(0x00); // TODO implement sides
		}
	}
}