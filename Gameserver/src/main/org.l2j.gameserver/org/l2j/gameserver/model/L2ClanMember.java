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
package org.l2j.gameserver.model;

import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.instancemanager.SiegeManager;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.entity.database.repository.CharacterRepository;


/**
 * This class ...
 * @version $Revision: 1.5.4.2 $ $Date: 2005/03/27 15:29:33 $
 */
public class L2ClanMember
{
	private final L2Clan _clan;
	private int _objectId;
	private String _name;
	private String _title;
	private int _powerGrade;
	private int _level;
	private int _classId;
	private L2PcInstance _player;
	private int _pledgeType;
	private int _apprentice;
	private int _sponsor;
	
	public L2ClanMember(L2Clan clan, String name, int level, int classId, int objectId, int pledgeType, int powerGrade, String title)
	{
		if (clan == null)
		{
			throw new IllegalArgumentException("Can not create a ClanMember with a null clan.");
		}
		_clan = clan;
		_name = name;
		_level = level;
		_classId = classId;
		_objectId = objectId;
		_powerGrade = powerGrade;
		_title = title;
		_pledgeType = pledgeType;
		_apprentice = 0;
		_sponsor = 0;
		
	}
	
	public L2ClanMember(L2PcInstance player)
	{
		if (player.getClan() == null)
		{
			throw new IllegalArgumentException("Can not create a ClanMember if reader has a null clan.");
		}
		_clan = player.getClan();
		_player = player;
		_name = _player.getName();
		_level = _player.getLevel();
		_classId = _player.getPlayerClass().getId();
		_objectId = _player.getObjectId();
		_powerGrade = _player.getPowerGrade();
		_pledgeType = _player.getPledgeType();
		_title = _player.getTitle();
		_apprentice = 0;
		_sponsor = 0;
	}
	
	public void setPlayerInstance(L2PcInstance player)
	{
		if ((player == null) && (_player != null))
		{
			// this is here to keep the data when the reader logs off
			_name = _player.getName();
			_level = _player.getLevel();
			_classId = _player.getPlayerClass().getId();
			_objectId = _player.getObjectId();
			_powerGrade = _player.getPowerGrade();
			_pledgeType = _player.getPledgeType();
			_title = _player.getTitle();
			_apprentice = _player.getApprentice();
			_sponsor = _player.getSponsor();
		}
		
		if (player != null)
		{
			if ((_clan.getLevel() > 3) && player.isClanLeader())
			{
				SiegeManager.getInstance().addSiegeSkills(player);
			}
			
			if (_clan.getReputationScore() >= 0)
			{
				L2Skill[] skills = _clan.getAllSkills();
				for (L2Skill sk : skills)
				{
					if (sk.getMinPledgeClass() <= player.getPledgeClass())
					{
						player.addSkill(sk, false);
					}
				}
			}
		}
		
		_player = player;
	}
	
	public L2PcInstance getPlayerInstance()
	{
		return _player;
	}
	
	public boolean isOnline()
	{
		return _player != null;
	}
	
	/**
	 * @return Returns the classId.
	 */
	public int getClassId()
	{
		if (_player != null)
		{
			return _player.getPlayerClass().getId();
		}
		return _classId;
	}
	
	/**
	 * @return Returns the level.
	 */
	public int getLevel()
	{
		if (_player != null)
		{
			return _player.getLevel();
		}
		return _level;
	}
	
	/**
	 * @return Returns the name.
	 */
	public String getName()
	{
		if (_player != null)
		{
			return _player.getName();
		}
		return _name;
	}
	
	/**
	 * @return Returns the objectId.
	 */
	public int getObjectId()
	{
		if (_player != null)
		{
			return _player.getObjectId();
		}
		return _objectId;
	}
	
	public String getTitle()
	{
		if (_player != null)
		{
			return _player.getTitle();
		}
		return _title;
	}
	
	public int getPledgeType()
	{
		if (_player != null)
		{
			return _player.getPledgeType();
		}
		return _pledgeType;
	}
	
	public void setPledgeType(int pledgeType)
	{
		_pledgeType = pledgeType;
		if (_player != null)
		{
			_player.setPledgeType(pledgeType);
		}
		else
		{
			// db save if char not logged in
			updatePledgeType();
		}
	}
	
	public void updatePledgeType()  {
        CharacterRepository repository = DatabaseAccess.getRepository(CharacterRepository.class);
        repository.updateSubpledge(getObjectId(), _pledgeType);
	}
	
	public int getPowerGrade()
	{
		if (_player != null)
		{
			return _player.getPowerGrade();
		}
		return _powerGrade;
	}
	
	/**
	 * @param powerGrade
	 */
	public void setPowerGrade(int powerGrade)
	{
		_powerGrade = powerGrade;
		if (_player != null)
		{
			_player.setPowerGrade(powerGrade);
		}
		else
		{
			// db save if char not logged in
			updatePowerGrade();
		}
	}
	
	/**
	 * Update the characters table of the database with power grade.<BR>
	 * <BR>
	 */
	public void updatePowerGrade() {
	    CharacterRepository repository = DatabaseAccess.getRepository(CharacterRepository.class);
	    repository.updatePowerGrade(getObjectId(), _powerGrade);
	}
	
	public void initApprenticeAndSponsor(int apprenticeID, int sponsorID)
	{
		_apprentice = apprenticeID;
		_sponsor = sponsorID;
	}
	
	public int getSponsor()
	{
		if (_player != null)
		{
			return _player.getSponsor();
		}
		return _sponsor;
	}
	
	public int getApprentice()
	{
		if (_player != null)
		{
			return _player.getApprentice();
		}
		return _apprentice;
	}
	
	public String getApprenticeOrSponsorName()
	{
		if (_player != null)
		{
			_apprentice = _player.getApprentice();
			_sponsor = _player.getSponsor();
		}
		
		if (_apprentice != 0)
		{
			L2ClanMember apprentice = _clan.getClanMember(_apprentice);
			if (apprentice != null)
			{
				return apprentice.getName();
			}
			return "Error";
		}
		if (_sponsor != 0)
		{
			L2ClanMember sponsor = _clan.getClanMember(_sponsor);
			if (sponsor != null)
			{
				return sponsor.getName();
			}
			return "Error";
		}
		return "";
	}
	
	public L2Clan getClan()
	{
		return _clan;
	}
	
	public int calculatePledgeClass(L2PcInstance player)
	{
		int pledgeClass = 0;
		L2Clan clan = player.getClan();
		if (clan != null)
		{
			switch (player.getClan().getLevel())
			{
				case 4:
					if (player.isClanLeader())
					{
						pledgeClass = 3;
					}
					break;
				case 5:
					if (player.isClanLeader())
					{
						pledgeClass = 4;
					}
					else
					{
						pledgeClass = 2;
					}
					break;
				case 6:
					switch (player.getPledgeType())
					{
						case -1:
							pledgeClass = 1;
							break;
						case 100:
						case 200:
							pledgeClass = 2;
							break;
						case 0:
							if (player.isClanLeader())
							{
								pledgeClass = 5;
							}
							else
							{
								switch (clan.getLeaderSubPledge(player.getName()))
								{
									case 100:
									case 200:
										pledgeClass = 4;
										break;
									case -1:
									default:
										pledgeClass = 3;
										break;
								}
							}
							break;
					}
					break;
				case 7:
					switch (player.getPledgeType())
					{
						case -1:
							pledgeClass = 1;
							break;
						case 100:
						case 200:
							pledgeClass = 3;
							break;
						case 1001:
						case 1002:
						case 2001:
						case 2002:
							pledgeClass = 2;
							break;
						case 0:
							if (player.isClanLeader())
							{
								pledgeClass = 7;
							}
							else
							{
								switch (clan.getLeaderSubPledge(player.getName()))
								{
									case 100:
									case 200:
										pledgeClass = 6;
										break;
									case 1001:
									case 1002:
									case 2001:
									case 2002:
										pledgeClass = 5;
										break;
									case -1:
									default:
										pledgeClass = 4;
										break;
								}
							}
							break;
					}
					break;
				case 8:
					switch (player.getPledgeType())
					{
						case -1:
							pledgeClass = 1;
							break;
						case 100:
						case 200:
							pledgeClass = 4;
							break;
						case 1001:
						case 1002:
						case 2001:
						case 2002:
							pledgeClass = 3;
							break;
						case 0:
							if (player.isClanLeader())
							{
								pledgeClass = 8;
							}
							else
							{
								switch (clan.getLeaderSubPledge(player.getName()))
								{
									case 100:
									case 200:
										pledgeClass = 7;
										break;
									case 1001:
									case 1002:
									case 2001:
									case 2002:
										pledgeClass = 6;
										break;
									case -1:
									default:
										pledgeClass = 5;
										break;
								}
							}
							break;
					}
					break;
				default:
					pledgeClass = 1;
					break;
			}
		}
		return pledgeClass;
	}
	
	public void saveApprenticeAndSponsor(int apprentice, int sponsor) {
		CharacterRepository repository = DatabaseAccess.getRepository(CharacterRepository.class);
		repository.updateApprenticeAndSponsor(getObjectId(), apprentice, sponsor);
	}
}
