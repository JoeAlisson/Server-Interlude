/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.gameserver.instancemanager.CastleManager;
import com.l2jbr.gameserver.model.L2ItemInstance;
import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;
import com.l2jbr.gameserver.model.entity.database.CropProcure;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SellListProcure extends L2GameServerPacket {
    private static final String _S__E9_SELLLISTPROCURE = "[S] E9 SellListProcure";
    // private static Logger _log = LoggerFactory.getLogger(SellListProcure.class.getName());

    private final L2PcInstance _activeChar;
    private final int _money;
    private final Map<L2ItemInstance, Integer> _sellList = new LinkedHashMap<>();
    private List<CropProcure> _procureList;
    private final int _castle;

    public SellListProcure(L2PcInstance player, int castleId) {
        _money = player.getAdena();
        _activeChar = player;
        _castle = castleId;
        _procureList = CastleManager.getInstance().getCastleById(_castle).getCropProcure(0);
        for (CropProcure c : _procureList) {
            L2ItemInstance item = _activeChar.getInventory().getItemByItemId(c.getCropId());
            if ((item != null) && (c.getAmount() > 0)) {
                _sellList.put(item, c.getAmount());
            }
        }
    }

    @Override
    protected final void writeImpl() {
        writeByte(0xE9);
        writeInt(_money); // money
        writeInt(0x00); // lease ?
        writeShort(_sellList.size()); // list size

        for (L2ItemInstance item : _sellList.keySet()) {
            writeShort(item.getItem().getType1().getId());
            writeInt(item.getObjectId());
            writeInt(item.getItemId());
            writeInt(_sellList.get(item)); // count
            writeShort(item.getItem().getType2().getId());
            writeShort(0); // unknown
            writeInt(0); // price, u shouldnt get any adena for crops, only raw materials
        }
    }

    @Override
    public String getType() {
        return _S__E9_SELLLISTPROCURE;
    }
}
