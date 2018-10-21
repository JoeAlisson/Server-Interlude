package org.l2j.gameserver.clientpackets;

import org.l2j.commons.Config;
import org.l2j.gameserver.datatables.*;
import org.l2j.gameserver.idfactory.IdFactory;
import org.l2j.gameserver.model.L2ItemInstance;
import org.l2j.gameserver.model.L2ShortCut;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.entity.database.ItemTemplate;
import org.l2j.gameserver.model.entity.database.SkillInfo;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.serverpackets.CharCreateFail;
import org.l2j.gameserver.serverpackets.CharCreateOk;
import org.l2j.gameserver.templates.ClassTemplate;
import org.l2j.gameserver.templates.ItemTypeGroup;
import org.l2j.gameserver.templates.xml.jaxb.PlayerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.util.Util.isValidPlayerName;

public final class CharacterCreate extends L2GameClientPacket  {

	private static Logger logger = LoggerFactory.getLogger(CharacterCreate.class);

	private String _name;
	private byte _sex;
	private int _classId;
	private byte _hairStyle;
	private byte _hairColor;
	private byte _face;
	
	@Override
	protected void readImpl()
	{
		_name = readString();
		int _race = readInt();
		_sex = (byte) readInt();
		_classId = readInt();
		int _int = readInt();
		int _str = readInt();
		int _con = readInt();
		int _men = readInt();
		int _dex = readInt();
		int _wit = readInt();
		_hairStyle = (byte) readInt();
		_hairColor = (byte) readInt();
		_face = (byte) readInt();
	}
	
	@Override
	protected void runImpl() {
        if (!isValidPlayerName(_name)) {
            logger.debug("player name {} is invalid. creation failed.",  _name);
            sendPacket(new CharCreateFail(CharCreateFail.REASON_16_ENG_CHARS));
            return;
        }

		if ((CharNameTable.accountCharNumber(client.getAccountName()) >= Config.MAX_CHARACTERS_NUMBER_PER_ACCOUNT) && (Config.MAX_CHARACTERS_NUMBER_PER_ACCOUNT != 0)) {
			logger.debug("Max number of characters ({}) reached. Creation failed.", Config.MAX_CHARACTERS_NUMBER_PER_ACCOUNT);
			sendPacket(new CharCreateFail(CharCreateFail.REASON_TOO_MANY_CHARACTERS));
			return;
		}

		if (CharNameTable.doesCharNameExist(_name)) {
            logger.debug("player name  {} already exists. creation failed.",  _name);
			sendPacket(new CharCreateFail(CharCreateFail.REASON_NAME_ALREADY_EXISTS));
			return;
		}

		logger.debug("player name: {}  classId: {}", _name , _classId);
		
		ClassTemplate template = PlayerTemplateTable.getInstance().getClassTemplate(_classId);
		if ((isNull(template))) {
			sendPacket(new CharCreateFail(CharCreateFail.REASON_CREATION_FAILED));
			return;
		}
		
		int objectId = IdFactory.getInstance().getNextId();
		L2PcInstance newChar = L2PcInstance.create(objectId, template, getClient().getAccountName(), _name, _hairStyle, _hairColor, _face, _sex != 0);
		newChar.setCurrentHp(template.getHp());
		newChar.setCurrentCp(template.getCp());
		newChar.setCurrentMp(template.getMp());
		// newChar.setMaxLoad(template.baseLoad);
		
		// send acknowledgement
		initNewChar(getClient(), newChar);
		CharCreateOk cco = new CharCreateOk();
		sendPacket(cco);
	}
	
	private boolean isValidName(String text) {
		boolean result = true;
		String test = text;
		Pattern pattern;
		try
		{
			pattern = Pattern.compile(Config.CNAME_TEMPLATE);
		}
		catch (PatternSyntaxException e) // case of illegal pattern
		{
			logger.warn("ERROR : Character name pattern of config is wrong!");
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
		logger.debug("Character init start");
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
				logger.debug("adding starter skill:" + startSkill.getId() + " / " + startSkill.getLevel());
			}
		}
		
		L2GameClient.saveCharToDisk(newChar);
		newChar.deleteMe(); // release the world of this character and it's inventory
	}

}
