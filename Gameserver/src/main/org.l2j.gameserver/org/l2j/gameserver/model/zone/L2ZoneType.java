package org.l2j.gameserver.model.zone;

import org.l2j.gameserver.model.L2Character;
import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.isNull;

/**
 * Abstract base class for any zone type Handles basic operations
 *
 * @author durgus
 */
public abstract class L2ZoneType {
    private L2ZoneForm zoneForm;
    protected Map<Integer, L2Character> characterList;

    private boolean checkAffected;

    private int minLvl;
    private int maxLvl;
    private int[] race;
    private int[] classesId;
    private char classType;

    protected L2ZoneType() {
        characterList = new ConcurrentHashMap<>();
        maxLvl = 0xFF;
    }

    public void setParameter(String name, String value) {
        checkAffected = true;

        switch (name) {
            case "affectedLvlMin":
                minLvl = Integer.parseInt(value);
                break;
            case "affectedLvlMax":
                maxLvl = Integer.parseInt(value);
                break;
            case "affectedRace":
                if (isNull(race)) {
                    race = new int[1];
                    race[0] = Integer.parseInt(value);
                } else {
                    int[] temp = new int[race.length + 1];

                    int i = 0;
                    for (; i < race.length; i++) {
                        temp[i] = race[i];
                    }
                    temp[i] = Integer.parseInt(value);
                    race = temp;
                }
                break;
            case "affectedClassId":
                if (isNull(classesId)) {
                    classesId = new int[1];
                    classesId[0] = Integer.parseInt(value);
                } else {
                    int[] temp = new int[classesId.length + 1];

                    int i = 0;
                    for (; i < classesId.length; i++) {
                        temp[i] = classesId[i];
                    }

                    temp[i] = Integer.parseInt(value);

                    classesId = temp;
                }
                break;
            case "affectedClassType":
                classType = (char) (value.equals("Fighter") ? 1 : 2);
                break;
        }
    }

    private boolean isAffected(L2Character character) {
        // Check lvl
        if ((character.getLevel() < minLvl) || (character.getLevel() > maxLvl)) {
            return false;
        }

        if (character instanceof L2PcInstance) {
            // Check class type
            if (classType != 0) {
                if (((L2PcInstance) character).isMageClass()) {
                    if (classType == 1) {
                        return false;
                    }
                } else if (classType == 2) {
                    return false;
                }
            }

            // Check race
            if (race != null) {
                boolean ok = false;

                for (int element : race) {
                    if (((L2PcInstance) character).getRace().ordinal() == element) {
                        ok = true;
                        break;
                    }
                }

                if (!ok) {
                    return false;
                }
            }

            // Check class
            if (classesId != null) {
                boolean ok = false;

                for (int _clas : classesId) {
                    if (((L2PcInstance) character).getPlayerClass().ordinal() == _clas) {
                        ok = true;
                        break;
                    }
                }

                if (!ok) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Set the zone for this L2ZoneType Instance
     *
     * @param zoneForm
     */
    public void setZoneForm(L2ZoneForm zoneForm) {
        this.zoneForm = zoneForm;
    }

    /**
     * Returns this zones zone form
     *
     * @return
     */
    public L2ZoneForm getZoneForm() {
        return zoneForm;
    }

    /**
     * Checks if the given coordinates are within the zone
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public boolean isInsideZone(int x, int y, int z) {
        return zoneForm.isInsideZone(x, y, z);
    }

    /**
     * Checks if the given object is inside the zone.
     *
     * @param object
     * @return
     */
    public boolean isInsideZone(L2Object object) {
        return zoneForm.isInsideZone(object.getX(), object.getY(), object.getZ());
    }

    public double getDistanceToZone(int x, int y) {
        return zoneForm.getDistanceToZone(x, y);
    }

    public double getDistanceToZone(L2Object object) {
        return zoneForm.getDistanceToZone(object.getX(), object.getY());
    }

    public void revalidateInZone(L2Character character) {
        // If the character can't be affected by this zone return
        if (checkAffected) {
            if (!isAffected(character)) {
                return;
            }
        }

        // If the object is inside the zone...
        if (zoneForm.isInsideZone(character.getX(), character.getY(), character.getZ())) {
            // Was the character not yet inside this zone?
            if (!characterList.containsKey(character.getObjectId())) {
                characterList.put(character.getObjectId(), character);
                onEnter(character);
            }
        } else {
            // Was the character inside this zone?
            if (characterList.containsKey(character.getObjectId())) {
                characterList.remove(character.getObjectId());
                onExit(character);
            }
        }
    }

    /**
     * Force fully removes a character from the zone Should use during teleport / logoff
     *
     * @param character
     */
    public void removeCharacter(L2Character character) {
        if (characterList.containsKey(character.getObjectId())) {
            characterList.remove(character.getObjectId());
            onExit(character);
        }
    }

    /**
     * Will scan the zones char list for the character
     *
     * @param character
     * @return
     */
    public boolean isCharacterInZone(L2Character character) {
        return characterList.containsKey(character.getObjectId());
    }

    protected abstract void onEnter(L2Character character);

    protected abstract void onExit(L2Character character);

    protected abstract void onDieInside(L2Character character);

    protected abstract void onReviveInside(L2Character character);

}
