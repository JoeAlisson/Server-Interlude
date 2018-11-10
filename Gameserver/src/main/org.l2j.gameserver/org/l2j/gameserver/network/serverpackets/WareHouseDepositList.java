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
package org.l2j.gameserver.network.serverpackets;

import org.l2j.commons.Config;
import org.l2j.gameserver.model.L2ItemInstance;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * 0x53 WareHouseDepositList dh (h dddhh dhhh d)
 *
 * @version $Revision: 1.4.2.1.2.4 $ $Date: 2005/03/27 15:29:39 $
 */
public class WareHouseDepositList extends L2GameServerPacket {
    public static final int PRIVATE = 1;
    public static final int CLAN = 2;
    public static final int CASTLE = 3; // not sure
    public static final int FREIGHT = 4; // not sure
    private static Logger _log = LoggerFactory.getLogger(WareHouseDepositList.class.getName());
    private final L2PcInstance _activeChar;
    private final long _playerAdena;
    private final List<L2ItemInstance> _items;
    private final int _whType;

    public WareHouseDepositList(L2PcInstance player, int type) {
        _activeChar = player;
        _whType = type;
        _playerAdena = _activeChar.getAdena();
        _items = new LinkedList<>();

        for (L2ItemInstance temp : _activeChar.getInventory().getAvailableItems(true)) {
            _items.add(temp);
        }

        // augmented and shadow items can be stored in private wh
        if (_whType == PRIVATE) {
            for (L2ItemInstance temp : player.getInventory().getItems()) {
                if ((temp != null) && !temp.isEquipped() && (temp.isShadowItem() || temp.isAugmented())) {
                    _items.add(temp);
                }
            }
        }
    }

    @Override
    protected final void writeImpl() {
        writeByte(0x41);
        /*
         * 0x01-Private Warehouse 0x02-Clan Warehouse 0x03-Castle Warehouse 0x04-Warehouse
         */
        writeShort(_whType);
        writeLong(_playerAdena);
        int count = _items.size();
        if (Config.DEBUG) {
            _log.debug("count:" + count);
        }
        writeShort(count);

        for (L2ItemInstance item : _items) {
            writeShort(item.getType().ordinal()); // item type1 //unconfirmed, works
            writeInt(item.getObjectId()); // unconfirmed, works
            writeInt(item.getId()); // unconfirmed, works
            writeLong(item.getCount()); // unconfirmed, works
            writeShort(item.getCommissionType().ordinal()); // item type2 //unconfirmed, works
            writeShort(0x00); // ? 100
            writeInt(0); // TODO item.getItem().getBodyPart().getId()); // ?
            writeShort(item.getEnchantLevel()); // enchant level -confirmed
            writeShort(0x00); // ? 300
            writeShort(0x00); // ? 200
            writeInt(item.getObjectId()); // item id - confimed
            if (item.isAugmented()) {
                writeInt(0x0000FFFF & item.getAugmentation().getAugmentationId());
                writeInt(item.getAugmentation().getAugmentationId() >> 16);
            } else {
                writeLong(0x00);
            }
        }
    }
}
