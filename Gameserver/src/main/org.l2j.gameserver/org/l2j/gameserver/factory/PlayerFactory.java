package org.l2j.gameserver.factory;

import org.l2j.commons.Config;
import org.l2j.gameserver.model.entity.database.Character;
import org.l2j.gameserver.model.entity.database.Items;
import org.l2j.gameserver.model.entity.database.repository.CharacterRepository;
import org.l2j.gameserver.model.entity.database.repository.ItemRepository;
import org.l2j.gameserver.templates.ClassTemplate;

import static java.lang.System.currentTimeMillis;
import static org.l2j.commons.database.DatabaseAccess.getRepository;
import static org.l2j.gameserver.model.L2ItemInstance.ItemLocation.INVENTORY;
import static org.l2j.gameserver.model.L2ItemInstance.ItemLocation.PAPERDOLL;
import static org.l2j.gameserver.templates.ItemConstants.ADENA;

public class PlayerFactory {

    public static Character create(ClassTemplate template, String account, String name, byte hairStyle, byte hairColor, byte face, byte sex) {
        var objectId = IdFactory.getInstance().getNextId();

        Character character = createCharacter(template, account, name, hairStyle, hairColor, face, sex, objectId);
        createPlayerItens(objectId, template);
        return character;
    }

    private static Character createCharacter(ClassTemplate template, String account, String name, byte hairStyle, byte hairColor, byte face, byte sex, int objectId) {
        var character = new Character();
        character.setObjectId(objectId);
        character.setAccount(account);
        character.setName(name);
        int level = 1;
        character.setLevel(level);
        character.setMaxHp(template.getHp(level));
        character.setHp(template.getHp(level));
        character.setMaxCp(template.getCp(level));
        character.setCp(template.getCp(level));
        character.setMaxMp(template.getMp(level));
        character.setMp(template.getMp(level));

        character.setFace(face);
        character.setHairStyle(hairStyle);
        character.setHairColor(hairColor);
        character.setSex(sex);

        var loc = template.getRandomStartingLocation();

        character.setX(loc.getX());
        character.setY(loc.getY());
        character.setZ(loc.getY());
        character.setRace(template.getRace());
        character.setClassId(template.getId());
        character.setBaseClass(template.getId());

        if (Config.ALT_GAME_NEW_CHAR_ALWAYS_IS_NEWBIE) {
            character.setNewbie(true);
        }
        character.setLastAccess(currentTimeMillis());

        var repository = getRepository(CharacterRepository.class);

        var slot = repository.getLastCharSlotFromAccount(account);
        character.setSlot((byte) (slot + 1));

        repository.save(character);
        return character;
    }

    private static void createPlayerItens(int objectId, ClassTemplate template) {
        var items = template.getStartingItems();
        for (var startingItem : items) {
            createItem(objectId, startingItem.getId(), startingItem.getCount(), startingItem.isEquipped());
        }
        if (Config.STARTING_ADENA > 0) {
            createItem(objectId, ADENA, Config.STARTING_ADENA, false);
        }
    }

    private static void createItem(int objectId, int id, int count, boolean equipped) {
        var item = ItemFactory.create(id);
        if (count > 1) {
            item.setCount(count);
        }

        if (equipped) {
            item.setLocation(PAPERDOLL);
        } else {
            item.setLocation(INVENTORY);
        }
        var modelItem = new Items(item.getObjectId(), objectId, item.getItemId(), item.getCount(), item.getLocation().name(), 0, item.getEnchantLevel(), item.getPriceToSell(), item.getPriceToBuy(), item.getCustomType1(), item.getCustomType2(), item.getMana());
        getRepository(ItemRepository.class).save(modelItem);
    }
}
