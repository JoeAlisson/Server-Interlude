package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.entity.ClanHall;
import org.l2j.gameserver.model.entity.ClanHall.ClanHallFunction;

/**
 * @author Steuf
 */
public class ClanHallDecoration extends L2GameServerPacket {
	private final ClanHall _clanHall;
	private ClanHallFunction _function;
	
	public ClanHallDecoration(ClanHall ClanHall)
	{
		_clanHall = ClanHall;
	}

	@Override
	protected final void writeImpl() {
		writeByte(0xf7);
		writeInt(_clanHall.getId()); // clanhall id
		// FUNC_RESTORE_HP
		_function = _clanHall.getFunction(ClanHall.FUNC_RESTORE_HP);
		if ((_function == null) || (_function.getLvl() == 0)) {
			writeByte(0);
		} else if (((_clanHall.getGrade() == 0) && (_function.getLvl() < 220)) || ((_clanHall.getGrade() == 1) && (_function.getLvl() < 160)) || ((_clanHall.getGrade() == 2) && (_function.getLvl() < 260)) || ((_clanHall.getGrade() == 3) && (_function.getLvl() < 300))) {
			writeByte(1);
		} else {
			writeByte(2);
		}
		// FUNC_RESTORE_MP
		_function = _clanHall.getFunction(ClanHall.FUNC_RESTORE_MP);
		if ((_function == null) || (_function.getLvl() == 0)) {
			writeByte(0);
			writeByte(0);
		} else if ((((_clanHall.getGrade() == 0) || (_clanHall.getGrade() == 1)) && (_function.getLvl() < 25)) || ((_clanHall.getGrade() == 2) && (_function.getLvl() < 30)) || ((_clanHall.getGrade() == 3) && (_function.getLvl() < 40))) {
			writeByte(1);
			writeByte(1);
		} else {
			writeByte(2);
			writeByte(2);
		}
		// FUNC_RESTORE_EXP
		_function = _clanHall.getFunction(ClanHall.FUNC_RESTORE_EXP);
		if ((_function == null) || (_function.getLvl() == 0)) {
			writeByte(0);
		} else if (((_clanHall.getGrade() == 0) && (_function.getLvl() < 25)) || ((_clanHall.getGrade() == 1) && (_function.getLvl() < 30)) || ((_clanHall.getGrade() == 2) && (_function.getLvl() < 40)) || ((_clanHall.getGrade() == 3) && (_function.getLvl() < 50))) {
			writeByte(1);
		} else {
			writeByte(2);
		}
		// FUNC_TELEPORT
		_function = _clanHall.getFunction(ClanHall.FUNC_TELEPORT);
		if ((_function == null) || (_function.getLvl() == 0)) {
			writeByte(0);
		} else if (_function.getLvl() < 2) {
			writeByte(1);
		} else {
			writeByte(2);
		}
		writeByte(0);
		// CURTAINS
		_function = _clanHall.getFunction(ClanHall.FUNC_DECO_CURTAINS);
		if ((_function == null) || (_function.getLvl() == 0)) {
			writeByte(0);
		} else if (_function.getLvl() <= 1) {
			writeByte(1);
		} else {
			writeByte(2);
		}
		// FUNC_ITEM_CREATE
		_function = _clanHall.getFunction(ClanHall.FUNC_ITEM_CREATE);
		if ((_function == null) || (_function.getLvl() == 0)) {
			writeByte(0);
		} else if (((_clanHall.getGrade() == 0) && (_function.getLvl() < 2)) || (_function.getLvl() < 3)) {
			writeByte(1);
		} else {
			writeByte(2);
		}
		// FUNC_SUPPORT
		_function = _clanHall.getFunction(ClanHall.FUNC_SUPPORT);
		if ((_function == null) || (_function.getLvl() == 0)) {
			writeByte(0);
			writeByte(0);
		} else if (((_clanHall.getGrade() == 0) && (_function.getLvl() < 2)) || ((_clanHall.getGrade() == 1) && (_function.getLvl() < 4)) || ((_clanHall.getGrade() == 2) && (_function.getLvl() < 5)) || ((_clanHall.getGrade() == 3) && (_function.getLvl() < 8))) {
			writeByte(1);
			writeByte(1);
		} else {
			writeByte(2);
			writeByte(2);
		}
		// Front Plateform
		_function = _clanHall.getFunction(ClanHall.FUNC_DECO_FRONTPLATEFORM);
		if ((_function == null) || (_function.getLvl() == 0)) {
			writeByte(0);
		} else if (_function.getLvl() <= 1) {
			writeByte(1);
		} else {
			writeByte(2);
		}
		// FUNC_ITEM_CREATE
		_function = _clanHall.getFunction(ClanHall.FUNC_ITEM_CREATE);
		if ((_function == null) || (_function.getLvl() == 0)) {
			writeByte(0);
		} else if (((_clanHall.getGrade() == 0) && (_function.getLvl() < 2)) || (_function.getLvl() < 3)) {
			writeByte(1);
		} else {
			writeByte(2);
		}
		writeInt(0);
		writeInt(0);
	}
}
