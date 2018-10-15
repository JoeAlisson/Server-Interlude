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

import com.l2jbr.commons.Config;
import com.l2jbr.gameserver.datatables.*;
import com.l2jbr.gameserver.idfactory.IdFactory;
import com.l2jbr.gameserver.model.L2ItemInstance;
import com.l2jbr.gameserver.model.L2ShortCut;
import com.l2jbr.gameserver.model.L2World;
import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;
import com.l2jbr.gameserver.model.entity.database.ItemTemplate;
import com.l2jbr.gameserver.model.entity.database.PlayerTemplate;
import com.l2jbr.gameserver.model.entity.database.SkillInfo;
import com.l2jbr.gameserver.network.L2GameClient;
import com.l2jbr.gameserver.serverpackets.CharCreateFail;
import com.l2jbr.gameserver.serverpackets.CharCreateOk;
import com.l2jbr.gameserver.templates.ItemTypeGroup;
import com.l2jbr.gameserver.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static java.util.Objects.isNull;

/**
 * This class ...
 * @version $Revision: 1.9.2.3.2.8 $ $Date: 2005/03/27 15:29:30 $
 */
@SuppressWarnings("unused")
public final class CharacterCreate extends L2GameClientPacket
{
	private static final String _C__0B_CHARACTERCREATE = "[C] 0B CharacterCreate";
	private static Logger _log = LoggerFactory.getLogger(CharacterCreate.class.getName());
	
	// cSdddddddddddd
	private String _name;
	private int _race;
	private byte _sex;
	private int _classId;
	private int _int;
	private int _str;
	private int _con;
	private int _men;
	private int _dex;
	private int _wit;
	private byte _hairStyle;
	private byte _hairColor;
	private byte _face;
	
	@Override
	protected void readImpl()
	{
		_name = readString();
		_race = readInt();
		_sex = (byte) readInt();
		_classId = readInt();
		_int = readInt();
		_str = readInt();
		_con = readInt();
		_men = readInt();
		_dex = readInt();
		_wit = readInt();
		_hairStyle = (byte) readInt();
		_hairColor = (byte) readInt();
		_face = (byte) readInt();
	}
	
	@Override
	protected void runImpl() {
		if ((CharNameTable.accountCharNumber(getClient().getAccountName()) >= Config.MAX_CHARACTERS_NUMBER_PER_ACCOUNT) && (Config.MAX_CHARACTERS_NUMBER_PER_ACCOUNT != 0)) {

			_log.debug("Max number of characters reached. Creation failed.");

			CharCreateFail ccf = new CharCreateFail(CharCreateFail.REASON_TOO_MANY_CHARACTERS);
			sendPacket(ccf);
			return;
		}

		if (CharNameTable.doesCharNameExist(_name)) {

            _log.debug("charname: {} already exists. creation failed.",  _name);

			CharCreateFail ccf = new CharCreateFail(CharCreateFail.REASON_NAME_ALREADY_EXISTS);
			sendPacket(ccf);
			return;
		}

		if ((_name.length() < 3) || (_name.length() > 16) || !Util.isAlphaNumeric(_name) || !isValidName(_name)) {
		    _log.debug("charname: {} is invalid. creation failed.",  _name );
			CharCreateFail ccf = new CharCreateFail(CharCreateFail.REASON_16_ENG_CHARS);
			sendPacket(ccf);
			return;
		}
		

		_log.debug("charname: {}  classId: {}", _name , _classId);

		
		PlayerTemplate template = CharTemplateTable.getInstance().getTemplate(_classId);
		if ((isNull(template)) || (template.getClassLevel() > 0)) {
			CharCreateFail ccf = new CharCreateFail(CharCreateFail.REASON_CREATION_FAILED);
			sendPacket(ccf);
			return;
		}
		
		int objectId = IdFactory.getInstance().getNextId();
		L2PcInstance newChar = L2PcInstance.create(objectId, template, getClient().getAccountName(), _name, _hairStyle, _hairColor, _face, _sex != 0);
		newChar.setCurrentHp(template.getHp());
		newChar.setCurrentCp(template.getCp());
		newChar.setCurrentMp(template.getMp());
		// newChar.setMaxLoad(template.baseLoad);
		
		// send acknowledgement
		CharCreateOk cco = new CharCreateOk();
		sendPacket(cco);
		
		initNewChar(getClient(), newChar);
	}
	
	private boolean isValidName(String text)
	{
		boolean result = true;
		String test = text;
		Pattern pattern;
		try
		{
			pattern = Pattern.compile(Config.CNAME_TEMPLATE);
		}
		catch (PatternSyntaxException e) // case of illegal pattern
		{
			_log.warn("ERROR : Character name pattern of config is wrong!");
			pattern = Pattern.compile(".*");
		}
		Matcher regexp = pattern.matcher(test);
		if (!regexp.matches())
		{
			result = false;
		}
		return result;
	}
	
	private void initNewChar(L2GameClient client, L2PcInstance newChar) {
		_log.debug("Character init start");
		L2World.getInstance().storeObject(newChar);
		
		PlayerTemplate template = newChar.getTemplate();
		
		newChar.addAdena("Init", Config.STARTING_ADENA, null, false);
		
		newChar.setPositionInvisible(template.getX(), template.getY(), template.getZ());
		newChar.setTitle("");
		
		L2ShortCut shortcut;
		// add attack shortcut
		shortcut = new L2ShortCut(0, 0, 3, 2, -1, 1);
		newChar.registerShortCut(shortcut);
		// add take shortcut
		shortcut = new L2ShortCut(3, 0, 3, 5, -1, 1);
		newChar.registerShortCut(shortcut);
		// add sit shortcut
		shortcut = new L2ShortCut(10, 0, 3, 0, -1, 1);
		newChar.registerShortCut(shortcut);
		
		ItemTable.getInstance();
		List<ItemTemplate> items = template.getItems();
		for (ItemTemplate item2 : items)
		{
			L2ItemInstance item = newChar.getInventory().addItem("Init", item2.getId(), 1, newChar, null);
			if (item.getItemId() == 5588)
			{
				// add tutbook shortcut
				shortcut = new L2ShortCut(11, 0, 1, item.getObjectId(), -1, 1);
				newChar.registerShortCut(shortcut);
			}
			if (item.isEquipable())
			{
				if ((newChar.getActiveWeaponItem() == null) || item.getItem().getType2() == ItemTypeGroup.TYPE2_WEAPON)
				{
					newChar.getInventory().equipItemAndRecord(item);
				}
			}
		}
		
		List<SkillInfo> startSkills = SkillTreeTable.getInstance().getAvailableSkills(newChar, newChar.getPlayerClass());
		for (SkillInfo startSkill : startSkills)
		{
			newChar.addSkill(SkillTable.getInstance().getInfo(startSkill.getId(), startSkill.getLevel()), true);
			if ((startSkill.getId() == 1001) || (startSkill.getId() == 1177))
			{
				shortcut = new L2ShortCut(1, 0, 2, startSkill.getId(), 1, 1);
				newChar.registerShortCut(shortcut);
			}
			if (startSkill.getId() == 1216)
			{
				shortcut = new L2ShortCut(10, 0, 2, startSkill.getId(), 1, 1);
				newChar.registerShortCut(shortcut);
			}
			if (Config.DEBUG)
			{
				_log.debug("adding starter skill:" + startSkill.getId() + " / " + startSkill.getLevel());
			}
		}
		
		L2GameClient.saveCharToDisk(newChar);
		newChar.deleteMe(); // release the world of this character and it's inventory
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.l2jbr.gameserver.clientpackets.ClientBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _C__0B_CHARACTERCREATE;
	}
}
