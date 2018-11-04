package org.l2j.gameserver.serverpackets;

import org.l2j.commons.Config;
import org.l2j.gameserver.model.Inventory;
import org.l2j.gameserver.model.PcInventory;
import org.l2j.gameserver.model.base.Experience;
import org.l2j.gameserver.model.entity.Heroes;
import org.l2j.gameserver.model.entity.database.repository.AugmentationsRepository;
import org.l2j.gameserver.model.entity.database.repository.CharacterSubclassesRepository;

import static org.l2j.commons.database.DatabaseAccess.getRepository;
import static org.l2j.gameserver.factory.ItemHelper.*;

public class CharSelectInfo extends L2GameServerPacket {

    private int activeId;

    public CharSelectInfo() {
        this(-1);
    }

    public CharSelectInfo(int activeId) {
        this.activeId = activeId;
    }

    @Override
    protected final void writeImpl() {
        var characters = client.getCharacters();

        writeByte(0x09);
        writeInt(characters.size());
        writeInt(Config.MAX_CHARACTERS_NUMBER_PER_ACCOUNT);
        writeByte(0x00);
        writeByte(0x02); // Play Mode [0=can't play] [1=can play free until level 85] [2=100% free play] -->
        writeInt(0x00);
        writeByte(0x01);

        long lastAccess = 0L;
        if (activeId == -1) {
            for (int i = 0; i < characters.size(); i++) {
                var characterLastAcess = characters.get(i).getLastAccess();
                if (characterLastAcess > lastAccess) {
                    lastAccess = characterLastAcess;
                }
                activeId = i;
            }
        }

        for (int i = 0; i < characters.size(); i++) {
            var character = characters.get(i);

            writeString(character.getName());
            writeInt(character.getObjectId());
            writeString(client.getAccountName());
            writeInt(client.getSessionId().sessionId);
            writeInt(character.getClanId());
            writeInt(0x00); // Builder Level ??

            writeInt(character.getSex());
            writeInt(character.getRace().ordinal());
            writeInt(character.getBaseClass());

            writeInt(Config.SERVER_ID);

            writeInt(character.getX());
            writeInt(character.getY());
            writeInt(character.getZ());

            writeDouble(character.getHp());
            writeDouble(character.getMp());

            var experience = character.getExperience();
            var sp = character.getSp();
            var level = character.getLevel();

            if (character.getBaseClass() != character.getClassId()) {
                var optionSub = getRepository(CharacterSubclassesRepository.class).findByClassId(character.getObjectId(), character.getClassId());
                if (optionSub.isPresent()) {
                    var sub = optionSub.get();
                    experience = sub.getExp();
                    sp = sub.getSp();
                    level = sub.getLevel();
                }
            }

            writeLong(sp);
            writeLong(experience);

            long xpToNextLevel = Experience.LEVEL[level + 1] - Experience.LEVEL[level];
            long xpInCurrentLevel = experience - Experience.LEVEL[level];
            writeDouble((double) xpInCurrentLevel / xpToNextLevel); // level progress
            writeInt(level);

            writeInt(character.getKarma()); // Reputation
            writeInt(character.getPk());
            writeInt(character.getPvp());

            writeInt(0x00);
            writeInt(0x00);
            writeInt(0x00);
            writeInt(0x00);
            writeInt(0x00);
            writeInt(0x00);
            writeInt(0x00);

            writeInt(0x00); // Unk
            writeInt(0x00); // Unk

            var paperdoll = PcInventory.restoreVisibleInventory(character.getObjectId());

            writeInt(paperdoll[PAPERDOLL_UNDERWEAR][1]);
            writeInt(paperdoll[PAPERDOLL_LEFT_EAR][1]);
            writeInt(paperdoll[PAPERDOLL_RIGHT_EAR][1]);
            writeInt(paperdoll[PAPERDOLL_NECK][1]);
            writeInt(paperdoll[PAPERDOLL_LEFT_FINGER][1]);
            writeInt(paperdoll[PAPERDOLL_RIGHT_FINGER][1]);
            writeInt(paperdoll[PAPERDOLL_HEAD][1]);
            writeInt(paperdoll[PAPERDOLL_RIGHT_HAND][1]);
            writeInt(paperdoll[PAPERDOLL_LEFT_HAND][1]);
            writeInt(paperdoll[PAPERDOLL_GLOVES][1]);
            writeInt(paperdoll[PAPERDOLL_CHEST][1]);
            writeInt(paperdoll[PAPERDOLL_LEGS][1]);
            writeInt(paperdoll[PAPERDOLL_FEET][1]);
            writeInt(paperdoll[PAPERDOLL_BACK][1]);
            writeInt(paperdoll[PAPERDOLL_TWO_HANDS][1]);
            writeInt(paperdoll[PAPERDOLL_HAIR][1]);
            writeInt(paperdoll[PAPERDOLL_HAIR_DOWN][1]);
            writeInt(paperdoll[PAPERDOLL_RIGHT_BRACELET][1]);
            writeInt(paperdoll[PAPERDOLL_LEFT_BRACELET][1]);
            writeInt(paperdoll[PAPERDOLL_DECO1][1]);
            writeInt(paperdoll[PAPERDOLL_DECO2][1]);
            writeInt(paperdoll[PAPERDOLL_DECO3][1]);
            writeInt(paperdoll[PAPERDOLL_DECO4][1]);
            writeInt(paperdoll[PAPERDOLL_DECO5][1]);
            writeInt(paperdoll[PAPERDOLL_DECO6][1]);
            writeInt(paperdoll[PAPERDOLL_WAIST][1]);
            writeInt(paperdoll[PAPERDOLL_BROOCH][1]);
            writeInt(paperdoll[PAPERDOLL_JEWEL1][1]);
            writeInt(paperdoll[PAPERDOLL_JEWEL2][1]);
            writeInt(paperdoll[PAPERDOLL_JEWEL3][1]);
            writeInt(paperdoll[PAPERDOLL_JEWEL4][1]);
            writeInt(paperdoll[PAPERDOLL_JEWEL5][1]);
            writeInt(paperdoll[PAPERDOLL_JEWEL6][1]);

            writeInt(paperdoll[PAPERDOLL_RIGHT_HAND][1]); // Visible Itens
            writeInt(paperdoll[PAPERDOLL_LEFT_HAND][1]);
            writeInt(paperdoll[PAPERDOLL_GLOVES][1]);
            writeInt(paperdoll[PAPERDOLL_CHEST][1]);
            writeInt(paperdoll[PAPERDOLL_LEGS][1]);
            writeInt(paperdoll[PAPERDOLL_FEET][1]);
            writeInt(paperdoll[PAPERDOLL_TWO_HANDS][1]);
            writeInt(paperdoll[PAPERDOLL_HAIR][1]);
            writeInt(paperdoll[PAPERDOLL_HAIR_DOWN][1]);

            writeShort(paperdoll[PAPERDOLL_CHEST][2]);
            writeShort(paperdoll[PAPERDOLL_LEGS][2]);
            writeShort(paperdoll[PAPERDOLL_HEAD][2]);
            writeShort(paperdoll[PAPERDOLL_GLOVES][2]);
            writeShort(paperdoll[PAPERDOLL_FEET][2]);

            writeInt(paperdoll[PAPERDOLL_HAIR][1] > 0 ? character.getSex() : character.getHairStyle());
            writeInt(character.getHairColor());
            writeInt(character.getFace());

            writeDouble(character.getMaxHp()); // hp max
            writeDouble(character.getMaxMp()); // mp max

            long deleteTime = character.getDeleteTime();
            int deletedays = 0;
            if (deleteTime > 0) {
                deletedays = (int) ((deleteTime - System.currentTimeMillis()) / 1000);
            }
            writeInt(deletedays); // days left before
            // delete .. if != 0
            // then char is inactive
            writeInt(character.getClassId());
            writeInt(i == activeId ? 0x01 : 0x00);

            var enchantEffect = paperdoll[PAPERDOLL_RIGHT_HAND][2];
            if (enchantEffect <= 0) {
                enchantEffect = paperdoll[PAPERDOLL_TWO_HANDS][2];
            }

            writeByte(enchantEffect > 127 ? 127 : enchantEffect);

            int weaponObjId = paperdoll[PAPERDOLL_TWO_HANDS][0];
            if (weaponObjId < 1) {
                weaponObjId = paperdoll[Inventory.PAPERDOLL_RHAND][0];
            }

            int augmentationId = 0;
            if (weaponObjId > 0) {
                var augmentation = getRepository(AugmentationsRepository.class).findById(weaponObjId);
                if (augmentation.isPresent()) {
                    augmentationId = augmentation.get().getAttributes();
                }
            }

            writeInt(augmentationId); // TODO Augmentantion Effect 1
            writeInt(augmentationId); // TODO Augmentantion Effect 2

            writeInt(0x00); // tranformation

            //TODO: Pet info?
            writeInt(0x00); // petId
            writeInt(0x00); // level
            writeInt(0x00); // food
            writeInt(0x00); // food level
            writeDouble(0x00); // current hp
            writeDouble(0x00); // current mp

            writeInt(0x00); // Vitality Level
            writeInt(0x00); // Vitality Percent
            writeInt(0x00); // remaining vitality item uses

            writeInt(character.getAccesslevel() > -100 ? 1 : 0);
            writeByte(character.isNobless() ? 0x01 : 0x00);
            writeByte(Heroes.getInstance().getHeroes().containsKey(character.getObjectId()) ? 0x01 : 0x00);
            writeByte(0x01); // show hair Acessory
        }
    }
}
