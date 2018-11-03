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
package org.l2j.tools.gsregistering;

import org.l2j.commons.Config;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.commons.database.GameserverRepository;
import org.l2j.commons.Server;


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.math.BigInteger;
import java.util.Map;


public class GameServerRegister
{
	private static String _choice;
	private static boolean _choiceOk;
	
	public static void main(String[] args) throws IOException
	{
		Server.serverMode = Server.MODE_LOGINSERVER;
		
		Config.load();
		
		LineNumberReader _in = new LineNumberReader(new InputStreamReader(System.in));
		try
		{
			//GameServerManager.load();
		}
		catch (Exception e)
		{
			System.out.println("FATAL: Failed loading GameServerManager. Reason: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	//	GameServerManager gameServerManager = GameServerManager.getInstance();
		System.out.println("Welcome to L2J GameServer Regitering");
		System.out.println("Enter The id of the server you want to registerClient");
		System.out.println("Type 'help' to get a list of ids.");
		System.out.println("Type 'clean' to unregister allTemplates currently registered gameservers on this LoginServer.");
		while (!_choiceOk)
		{
			System.out.println("Your choice:");
			_choice = _in.readLine();
			if (_choice.equalsIgnoreCase("help"))
			{
			/*	for (Map.Entry<Integer, String> entry : gameServerManager.getServerNames().entrySet())
				{
					System.out.println("Server: ID: " + entry.getKey() + "\t- " + entry.getValue() + " - In Use: " + (gameServerManager.hasRegisteredGameServerOnId(entry.getKey()) ? "YES" : "NO"));
				}*/
				System.out.println("You can also see servername.xml");
			}
			else if (_choice.equalsIgnoreCase("clean"))
			{
				System.out.print("This is going to UNREGISTER ALL servers from this LoginServer. Are you sure? (y/n) ");
				_choice = _in.readLine();
				if (_choice.equals("y"))
				{
					GameServerRegister.cleanRegisteredGameServersFromDB();
				//	gameServerManager.getRegisteredGameServers().clear();
				}
				else
				{
					System.out.println("ABORTED");
				}
			}
			else
			{
				try
				{
					int id = Integer.parseInt(_choice);
					//int size = gameServerManager.getServerNames().size();
					/*
					if (size == 0)
					{
						System.out.println("No server names avalible, please make sure that servername.xml is in the LoginServer directory.");
						System.exit(1);
					}*/
					
					/*String name = gameServerManager.getServerNameById(id);
					if (name == null)
					{
						System.out.println("No name for id: " + id);
						continue;
					}
					
					if (gameServerManager.hasRegisteredGameServerOnId(id))
					{
						System.out.println("This id is not free");
					}
					else
					{
						byte[] hexId = LoginServerThread.generateHex(16);
						gameServerManager.registerServerOnDB(hexId, id, "");
						Config.saveHexid(id, new BigInteger(hexId).toString(16), "hexid(server " + id + ").txt");
						System.out.println("Server Registered hexid saved to 'hexid(server " + id + ").txt'");
						System.out.println("Put this file in the /config folder of your gameserver and rename it to 'hexid.txt'");
						return;
					}*/
				}
				catch (NumberFormatException nfe)
				{
					System.out.println("Please, type a number or 'help'");
				}
			}
		}
	}
	
	public static void cleanRegisteredGameServersFromDB() {
        GameserverRepository repository = DatabaseAccess.getRepository(GameserverRepository.class);
        repository.deleteAll();
	}
}