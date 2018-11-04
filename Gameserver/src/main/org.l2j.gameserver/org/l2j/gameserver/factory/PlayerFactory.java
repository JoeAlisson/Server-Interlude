package org.l2j.gameserver.factory;

import org.l2j.commons.Config;
import org.l2j.gameserver.datatables.ClanTable;
import org.l2j.gameserver.datatables.PlayerTemplateTable;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.entity.database.Character;
import org.l2j.gameserver.model.entity.database.Items;
import org.l2j.gameserver.model.entity.database.repository.*;
import org.l2j.gameserver.templates.ClassTemplate;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.System.currentTimeMillis;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNullElse;
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
        character.setClassId(requireNonNullElse(template.getId(), 0));
        character.setBaseClass(template.getId());

        if (Config.ALT_GAME_NEW_CHAR_ALWAYS_IS_NEWBIE) {
            character.setNewbie(true);
        }
        character.setLastAccess(currentTimeMillis());
        getRepository(CharacterRepository.class).save(character);
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
        var item = ItemHelper.create(id);
        if (count > 1) {
            item.setCount(count);
        }

        if (equipped) {
            item.setLocation(PAPERDOLL, item.getPaperDoll());
        } else {
            item.setLocation(INVENTORY);
        }
        var modelItem = new Items(item.getObjectId(), objectId, item.getItemId(), item.getCount(), item.getLocation().name(), item.getEquipSlot(), item.getEnchantLevel(), item.getPriceToSell(), item.getPriceToBuy(), item.getCustomType1(), item.getCustomType2(), item.getMana());
        getRepository(ItemRepository.class).save(modelItem);
    }

    public static L2PcInstance load(Character character) {
        if(isNull(character)) {
            return null;
        }
        var template = PlayerTemplateTable.getInstance().getClassTemplate(character.getClassId());
        return new L2PcInstance(template, character);
    }

    public static List<Character> loadCharacters(String account) {
        return getRepository(CharacterRepository.class).findAllByAccountName(account).stream().filter(PlayerFactory::restore).collect(Collectors.toList());
    }

    private static boolean restore(Character character) {
        var deleteTime = character.getDeleteTime();

        if (deleteTime > 0 && System.currentTimeMillis() > deleteTime) {
            if (character.getClanId() > 0) {
                var clan = ClanTable.getInstance().getClan(character.getClanId());
                if (nonNull(clan)) {
                    clan.removeClanMember(character.getName(), 0);
                }
            }
            PlayerFactory.delete(character.getObjectId());
            return false;
        }
        return true;
    }

    public static void delete(int objectId) {
        if (objectId < 0) {
            return;
        }
        // TODO  release all objects Id
        IdFactory.getInstance().releaseId(objectId);

        getRepository(CharacterFriendRepository.class).deleteFriends(objectId);
        getRepository(CharacterHennasRepository.class).deleteById(objectId);
        getRepository(CharacterMacrosesRepository.class).deleteById(objectId);
        getRepository(CharacterQuestsRepository.class).deleteById(objectId);
        getRepository(CharacterRecipebookRepository.class).deleteAllByCharacter(objectId);
        getRepository(CharacterShortcutsRepository.class).deleteById(objectId);
        getRepository(CharacterSkillsRepository.class).deleteById(objectId);
        getRepository(CharacterSkillsSaveRepository.class).deleteById(objectId);
        getRepository(CharacterSubclassesRepository.class).deleteById(objectId);
        getRepository(HeroesRepository.class).deleteById(objectId);
        getRepository(OlympiadNoblesRepository.class).deleteById(objectId);
        getRepository(SevenSignsRepository.class).deleteById(objectId);
        getRepository(PetsRepository.class).deleteByOwner(objectId);
        getRepository(AugmentationsRepository.class).deleteByItemOwner(objectId);
        getRepository(ItemRepository.class).deleteByOwner(objectId);
        getRepository(MerchantLeaseRepository.class).deleteByPlayer(objectId);
        getRepository(CharacterRepository.class).deleteById(objectId);
    }

    public static void markToDelete(Character character) {
        long deleteTime = System.currentTimeMillis() + (Config.DELETE_DAYS * 86400000L);
        getRepository(CharacterRepository.class).updateDeleteTime(character.getObjectId(), deleteTime);
        character.setDeleteTime(deleteTime);
    }

    public static void removeMarkDelete(Character character) {
        if(nonNull(character)) {
            getRepository(CharacterRepository.class).updateDeleteTime(character.getObjectId(), 0);
            character.setDeleteTime(0);
        }
    }
}
