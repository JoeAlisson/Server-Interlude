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
package com.l2jbr.gameserver.clientpackets;

import com.l2jbr.gameserver.serverpackets.ExSendManorList;

import java.util.ArrayList;
import java.util.List;


/**
 * Format: ch c (id) 0xD0 h (subid) 0x08
 *
 * @author l3x
 */
public class RequestManorList extends L2GameClientPacket {
    private static final String _C__FE_08_REQUESTMANORLIST = "[S] 0xd0:0x01 RequestManorList";


    @Override
    protected void readImpl() { }

    @Override
    protected void runImpl() {
        sendPacket(new ExSendManorList());
    }

    @Override
    public String getType() {
        return _C__FE_08_REQUESTMANORLIST;
    }
}