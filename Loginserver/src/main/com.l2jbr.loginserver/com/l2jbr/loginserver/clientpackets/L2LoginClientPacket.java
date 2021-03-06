/* This program is free software; you can redistribute it and/or modify
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
package com.l2jbr.loginserver.clientpackets;

import com.l2jbr.loginserver.L2LoginClient;
import org.l2j.mmocore.ReadablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author KenM
 */
public abstract class L2LoginClientPacket extends ReadablePacket<L2LoginClient>
{
	private static Logger _log = LoggerFactory.getLogger(L2LoginClientPacket.class.getName());
	
	@Override
	protected final boolean read()
	{
		try
		{
			return readImpl();
		}
		catch (Exception e)
		{
			_log.error("ERROR READING: " + this.getClass().getSimpleName());
			e.printStackTrace();
			return false;
		}
	}
	
	protected abstract boolean readImpl();
}
