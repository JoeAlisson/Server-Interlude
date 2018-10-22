package org.l2j.gameserver.serverpackets;

import org.l2j.commons.Config;
import org.l2j.gameserver.model.CharSelectInfoPackage;
import org.l2j.gameserver.model.Inventory;
import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.base.Experience;
import org.l2j.gameserver.model.entity.Heroes;
import org.l2j.gameserver.model.entity.database.Character;
import org.l2j.gameserver.model.entity.database.repository.AugmentationsRepository;
import org.l2j.gameserver.model.entity.database.repository.CharacterRepository;
import org.l2j.gameserver.model.entity.database.repository.CharacterSubclassesRepository;
import org.l2j.gameserver.network.L2GameClient;

import java.util.ArrayList;
import java.util.List;

import static org.l2j.commons.database.DatabaseAccess.getRepository;
import static java.util.Objects.nonNull;

public class CharSelectInfo extends L2GameServerPacket {

    private final String account;

    private final int sessionId;

    private int _activeId;

    private final CharSelectInfoPackage[] _characterPackages;

    public CharSelectInfo(String account, int sessionId) {
        this(account, sessionId, -1);
    }

    public CharSelectInfo(String account, int sessionId, int activeId) {
        this.sessionId = sessionId;
        this.account = account;
        _characterPackages = loadCharacterSelectInfo();
        _activeId = activeId;
    }

    public CharSelectInfoPackage[] getCharInfo() {
        return _characterPackages;
    }

    @Override
    protected final void writeImpl() {
        int size = (_characterPackages.length);

        writeByte(0x09);
        writeInt(size);
        writeInt(Config.MAX_CHARACTERS_NUMBER_PER_ACCOUNT);
        writeByte(0x00);
        writeByte(0x02); // Play Mode [0=can't play] [1=can play free until level 85] [2=100% free play] -->
        writeInt(0x00);
        writeByte(0x01);

        long lastAccess = 0L;

        if (_activeId ==  -1) {
            for (int i = 0; i < size; i++) {
                if (lastAccess < _characterPackages[i].getLastAccess()) {
                    lastAccess = _characterPackages[i].getLastAccess();
                    _activeId = i;
                }
            }
        }

        for (int i = 0; i < size; i++) {
            CharSelectInfoPackage charInfoPackage = _characterPackages[i];

            writeString(charInfoPackage.getName());
            writeInt(charInfoPackage.getObjectId());
            writeString(account);
            writeInt(sessionId);
            writeInt(charInfoPackage.getClanId());
            writeInt(0x00); // Builder Level ??

            writeInt(charInfoPackage.getSex());
            writeInt(charInfoPackage.getRace());
            writeInt(charInfoPackage.getBaseClassId());

            writeInt(Config.SERVER_ID);

            writeInt(charInfoPackage.getX());
            writeInt(charInfoPackage.getY());
            writeInt(charInfoPackage.getZ());

            writeDouble(charInfoPackage.getCurrentHp());
            writeDouble(charInfoPackage.getCurrentMp());

            writeLong(charInfoPackage.getSp());
            writeLong(charInfoPackage.getExp());

            long xpToNextLevel = Experience.LEVEL[charInfoPackage.getLevel() +1] - Experience.LEVEL[charInfoPackage.getLevel()];
            long xpInCurrentLevel = charInfoPackage.getExp() - Experience.LEVEL[charInfoPackage.getLevel()];
            writeDouble( (double) xpInCurrentLevel / xpToNextLevel); // level progress
            writeInt(charInfoPackage.getLevel());

            writeInt(charInfoPackage.getKarma()); // Reputation
            writeInt(charInfoPackage.getPk());
            writeInt(charInfoPackage.getPvP());

            writeInt(0x00);
            writeInt(0x00);
            writeInt(0x00);
            writeInt(0x00);
            writeInt(0x00);
            writeInt(0x00);
            writeInt(0x00);

            writeInt(0x00); // Unk
            writeInt(0x00); // Unk

            writeInt(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_PENDANT));
            writeInt(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_LEAR));
            writeInt(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_REAR));
            writeInt(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_NECK));
            writeInt(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_LFINGER));
            writeInt(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_RFINGER));
            writeInt(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_HEAD));
            writeInt(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_RHAND));
            writeInt(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_LHAND));
            writeInt(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_GLOVES));
            writeInt(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_CHEST));
            writeInt(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_LEGS));
            writeInt(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_FEET));
            writeInt(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_BACK));
            writeInt(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_LRHAND));
            writeInt(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_HAIR));
            writeInt(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_DHAIR));
            writeInt(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_RBRACELET));
            writeInt(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_LBRACELET));
            writeInt(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_DECO1));
            writeInt(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_DECO2));
            writeInt(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_DECO3));
            writeInt(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_DECO4));
            writeInt(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_DECO5));
            writeInt(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_DECO6));
            writeInt(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_BELT));
            writeInt(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_BROOCH));
            writeInt(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_JEWEL1));
            writeInt(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_JEWEL2));
            writeInt(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_JEWEL3));
            writeInt(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_JEWEL4));
            writeInt(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_JEWEL5));
            writeInt(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_JEWEL6));

            writeInt(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_RHAND)); // Visible Itens
            writeInt(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_LHAND));
            writeInt(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_GLOVES));
            writeInt(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_CHEST));
            writeInt(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_LEGS));
            writeInt(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_FEET));
            writeInt(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_LRHAND));
            writeInt(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_HAIR));
            writeInt(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_DHAIR));

            writeShort(charInfoPackage.getPaperdollEnchantEffect(Inventory.PAPERDOLL_CHEST));
            writeShort(charInfoPackage.getPaperdollEnchantEffect(Inventory.PAPERDOLL_LEGS));
            writeShort(charInfoPackage.getPaperdollEnchantEffect(Inventory.PAPERDOLL_HEAD));
            writeShort(charInfoPackage.getPaperdollEnchantEffect(Inventory.PAPERDOLL_GLOVES));
            writeShort(charInfoPackage.getPaperdollEnchantEffect(Inventory.PAPERDOLL_FEET));

            writeInt(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_HAIR) > 0 ? charInfoPackage.getSex() : charInfoPackage.getHairStyle());
            writeInt(charInfoPackage.getHairColor());
            writeInt(charInfoPackage.getFace());

            writeDouble(charInfoPackage.getMaxHp()); // hp max
            writeDouble(charInfoPackage.getMaxMp()); // mp max

            long deleteTime = charInfoPackage.getDeleteTimer();
            int deletedays = 0;
            if (deleteTime > 0) {
                deletedays = (int) ((deleteTime - System.currentTimeMillis()) / 1000);
            }
            writeInt(deletedays); // days left before
            // delete .. if != 0
            // then char is inactive
            writeInt(charInfoPackage.getClassId());
            writeInt(i == _activeId ? 0x01 : 0x00);

            writeByte(charInfoPackage.getEnchantEffect() > 127 ? 127 : charInfoPackage.getEnchantEffect());
            writeInt(charInfoPackage.getAugmentationId()); // TODO Augmentantion Effect 1
            writeInt(charInfoPackage.getAugmentationId()); // TODO Augmentantion Effect 2

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

            writeInt(charInfoPackage.getAccessLevel() > - 100 ? 1 : 0 );
            writeByte(charInfoPackage.getNobles() ? 0x01 : 0x00);
            writeByte(Heroes.getInstance().getHeroes().containsKey(charInfoPackage.getObjectId()) ? 0x01 : 0x00);
            writeByte(0x01); // show hair Acessory

        }
    }

    private CharSelectInfoPackage[] loadCharacterSelectInfo() {
        List<CharSelectInfoPackage> characterList = new ArrayList<>();

        getRepository(CharacterRepository.class).findAllByAccountName(account).forEach(character -> {
            CharSelectInfoPackage charInfoPackage = restoreChar(character);
            if (nonNull(charInfoPackage)) {
                characterList.add(charInfoPackage);
            }
        });

        return characterList.toArray(new CharSelectInfoPackage[0]);

    }

    private void loadCharacterSubclassInfo(CharSelectInfoPackage charInfopackage, int ObjectId, int activeClassId) {
        CharacterSubclassesRepository repository = getRepository(CharacterSubclassesRepository.class);
        repository.findByClassId(ObjectId, activeClassId).ifPresent(characterSubclasse -> {
            charInfopackage.setExp(characterSubclasse.getExp());
            charInfopackage.setSp(characterSubclasse.getSp());
            charInfopackage.setLevel(characterSubclasse.getLevel());
        });
    }

    private CharSelectInfoPackage restoreChar(Character character) {
        int objectId = character.getObjectId();

        // See if the char must be deleted
        long deleteTime = character.getDeleteTime();
        if (deleteTime > 0 && System.currentTimeMillis() > deleteTime) {
            L2PcInstance cha = L2PcInstance.load(objectId);
            L2Clan clan = cha.getClan();
            if (clan != null) {
                clan.removeClanMember(cha.getName(), 0);
            }

            L2GameClient.deleteCharByObjId(objectId);
            return null;
        }

        String name = character.getCharName();

        CharSelectInfoPackage charInfopackage = new CharSelectInfoPackage(objectId, name);
        charInfopackage.setLevel(character.getLevel());
        charInfopackage.setMaxHp(character.getMaxHp());
        charInfopackage.setCurrentHp(character.getHp());
        charInfopackage.setMaxMp(character.getMaxMp());
        charInfopackage.setCurrentMp(character.getMp());
        charInfopackage.setKarma(character.getKarma());

        charInfopackage.setFace(character.getFace());
        charInfopackage.setHairStyle(character.getHairStyle());
        charInfopackage.setHairColor(character.getHairColor());
        charInfopackage.setSex(character.getSex());

        charInfopackage.setExp(character.getExperience());
        charInfopackage.setSp(character.getSp());
        charInfopackage.setClanId(character.getClanId());

        charInfopackage.setRace(character.getRace().ordinal());
        charInfopackage.setX(character.getX());
        charInfopackage.setY(character.getY());
        charInfopackage.setZ(character.getZ());
        charInfopackage.setPk(character.getPk());
        charInfopackage.setPvp(character.getPvp());
        charInfopackage.setAccessLevel(character.getAccesslevel());
        charInfopackage.setNobles(character.isNobless());

        final int baseClassId = character.getBaseClass();
        final int activeClassId = character.getClassId();

        // if is in subclass, load subclass exp, sp, lvl info
        if (baseClassId != activeClassId) {
            loadCharacterSubclassInfo(charInfopackage, objectId, activeClassId);
        }

        charInfopackage.setClassId(activeClassId);

        // Get the augmentation id for equipped weapon
        int weaponObjId = charInfopackage.getPaperdollObjectId(Inventory.PAPERDOLL_LRHAND);
        if (weaponObjId < 1) {
            weaponObjId = charInfopackage.getPaperdollObjectId(Inventory.PAPERDOLL_RHAND);
        }

        if (weaponObjId > 0) {
            AugmentationsRepository repository = getRepository(AugmentationsRepository.class);
            repository.findById(weaponObjId).ifPresent(augmentation -> charInfopackage.setAugmentationId(augmentation.getAttributes()));
        }

        /*
         * Check if the base class is set to zero and alse doesn't match with the current active class, otherwise send the base class ID. This prevents chars created before base class was introduced from being displayed incorrectly.
         */
        if ((baseClassId == 0) && (activeClassId > 0)) {
            charInfopackage.setBaseClassId(activeClassId);
        } else {
            charInfopackage.setBaseClassId(baseClassId);
        }

        charInfopackage.setDeleteTimer(deleteTime);
        charInfopackage.setLastAccess(character.getLastAccess());

        return charInfopackage;
    }
}
