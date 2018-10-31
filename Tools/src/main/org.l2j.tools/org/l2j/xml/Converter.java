package org.l2j.xml;

import org.l2j.xml.generated.*;
import org.l2j.xml.generated.ObjectFactory;
import org.l2j.xml.old.generated.*;
import org.l2j.xml.old.generated.ItemType;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class Converter {

    static Map<String, ItemList>  converted = new HashMap<>();
    static ObjectFactory factory = new ObjectFactory();

    public static void main(String[] args) throws JAXBException, IOException {
        OldXmlReader reader = new OldXmlReader();
        var dir = Paths.get("xml");
        Files.list(dir).forEach(path -> {
            processPath(reader, path);
        });
    }

    private static void processPath(OldXmlReader reader, Path path) {
        System.out.println("Processing file " + path.getFileName());
        var file = path.toFile();
        reader.read(file);
        var itemList  = new ItemList();
        converted.put(file.getName(), itemList);
        reader.getItems().values().forEach(itemType -> {
            processItem(itemList,  itemType);
        });

    }

    private static void processItem(ItemList list, ItemType itemType) {
        switch (itemType.getType().toLowerCase()) {
            case "weapon":
                processWeapon(list, itemType);
                break;
            case "armor":
                processArmor(list, itemType);
                break;
            case "etcitem":
                processEtcItem(list, itemType);
                break;

        }

    }

    private static void processEtcItem(ItemList list, ItemType itemType) {

    }

    private static void processArmor(ItemList list, ItemType itemType) {
    }

    private static void processWeapon(ItemList list, ItemType itemType) {
        try {
            var weapon = factory.createWeapon();
            weapon.setId(itemType.getId().intValue());
            weapon.setName(itemType.getName());
            weapon.setAdditionalName(itemType.getAdditionalName());
            parseSet(itemType, weapon);
            parseCond(itemType, weapon);
            itemType.getCapsuledItems();
            itemType.getSkills();
            itemType.getStats();

            list.getItemTemplate().add(factory.createWeapon(weapon));
        } catch(Exception e) {
            System.out.println("Erro processing item " + itemType.getId().intValue());
            e.printStackTrace();
        }
    }

    private static void parseCond(ItemType itemType, Weapon weapon) {
        itemType.getCond().forEach(condType -> {
            var usarCondition = false;
            if(isNull(weapon.getCondition())) {
                weapon.setCondition(factory.createUseCondition());
                usarCondition = true;
            } else {
                var useCondition = weapon.getCondition();
                if(nonNull(useCondition.getCondition())) {
                    var and = factory.createAND();
                    and.getCondition().add(useCondition.getCondition());
                    useCondition.setCondition(null);
                    useCondition.setOperator(factory.createAnd(and));
                } else if(! (useCondition.getOperator().getValue() instanceof  AND)) {
                    var operator = useCondition.getOperator();
                    var and = factory.createAND();
                    and.getOperator().add(operator);
                    useCondition.setOperator(factory.createAnd(and));
                }
            }
            for (Object usingAndOrNot : condType.getUsingOrAndOrNot()) {
                usarCondition = parseCondition(weapon, usarCondition, usingAndOrNot);
            }
        });
    }

    private static boolean parseCondition(Weapon weapon, boolean usarCondition, Object usingAndOrNot) {
        if(usingAndOrNot instanceof AndType) {
            var andType = (AndType) usingAndOrNot;
            for (Object o : andType.getUsingOrPlayerOrTarget()) {
                if (usarCondition) {
                    weapon.getCondition().setOperator(factory.createAnd(factory.createAND()));
                    usarCondition = false;
                    parseCondition(weapon, usarCondition, o);
                } else {
                    usarCondition = false;
                    parseCondition(weapon, false, o);
                }
            }
        } else if(usingAndOrNot instanceof PlayerType) {

        } else if(usingAndOrNot instanceof TargetType) {

        } return usarCondition;
    }

    private static void parseSet(ItemType itemType, Weapon weapon) {
        itemType.getSet().forEach(setType -> {
            switch (setType.getName()) {
                case "bodypart":
                    weapon.setBodyPart(getBodyPart(setType.getVal()));
                    break;
                case "crystal_count":
                    if (isNull(weapon.getCrystalInfo())) {
                        weapon.setCrystalInfo(factory.createCrystalInfo());
                    }
                    weapon.getCrystalInfo().setCount(Integer.parseInt(setType.getVal()));
                    break;
                case "crystal_type":
                    if (isNull(weapon.getCrystalInfo())) {
                        weapon.setCrystalInfo(factory.createCrystalInfo());
                    }
                    weapon.getCrystalInfo().setType(CrystalType.fromValue(setType.getVal()));
                    break;
                case "damage_range":
                    var damage = factory.createDamage();
                    var values = setType.getVal().split(";");
                    damage.setVertical(Integer.parseInt(values[0]));
                    damage.setHorizontal(Integer.parseInt(values[1]));
                    damage.setDistance(Integer.parseInt(values[2]));
                    damage.setWidth(Integer.parseInt(values[3]));
                    weapon.setDamage(damage);
                    break;
                case "equip_condition":
                    var condition = getCondition(setType.getVal());
                    weapon.setCondition(condition);
                    break;
                case "handler":
                    var handler = getHandler(setType.getVal());
                    weapon.setHandler(handler);
                    break;
                case "icon":
                    weapon.setIcon(setType.getVal());
                    break;
                case "is_destroyable":
                    if(isNull(weapon.getRestriction())) {
                        weapon.setRestriction(factory.createItemRestriction());
                    }
                    weapon.getRestriction().setDestroyable(Boolean.parseBoolean(setType.getVal()));
                    break;
                case "is_dropable":
                    if(isNull(weapon.getRestriction())) {
                        weapon.setRestriction(factory.createItemRestriction());
                    }
                    weapon.getRestriction().setDropable(Boolean.parseBoolean(setType.getVal()));
                    break;
                case "is_freightable":
                    if(isNull(weapon.getRestriction())) {
                        weapon.setRestriction(factory.createItemRestriction());
                    }
                    weapon.getRestriction().setFreightable(Boolean.parseBoolean(setType.getVal()));
                    break;
                case "is_sellable":
                    if(isNull(weapon.getRestriction())) {
                        weapon.setRestriction(factory.createItemRestriction());
                    }
                    weapon.getRestriction().setSellable(Boolean.parseBoolean(setType.getVal()));
                    break;
                case "is_tradable":
                    if(isNull(weapon.getRestriction())) {
                        weapon.setRestriction(factory.createItemRestriction());
                    }
                    weapon.getRestriction().setTradeable(Boolean.parseBoolean(setType.getVal()));
                    break;
                case "is_magic_weapon":
                    weapon.setMagic(Boolean.parseBoolean(setType.getVal()));
                    break;
                case "is_questitem":
                    weapon.setQuestItem(Boolean.parseBoolean(setType.getVal()));
                    break;
                case "mp_consume":
                    if(isNull(weapon.getConsume())) {
                        weapon.setConsume(factory.createItemConsume());
                    }
                    weapon.getConsume().setMp(Integer.parseInt(setType.getVal()));
                    break;
                case "price":
                    weapon.setPrice(Long.parseLong(setType.getVal()));
                    break;
                case "random_damage":
                    if(isNull(weapon.getDamage())) {
                        weapon.setDamage(factory.createDamage());
                    }
                    weapon.getDamage().setRandom(Integer.parseInt(setType.getVal()));
                    break;
                case "reuse_delay":
                    weapon.setReuseDelay(Long.parseLong(setType.getVal()));
                    break;
                case "soulshots":
                case "spiritshots":
                    if(isNull(weapon.getConsume())) {
                        weapon.setConsume(factory.createItemConsume());
                    }
                    weapon.getConsume().setShot(Integer.parseInt(setType.getVal()));
                    break;
                case "time":
                    weapon.setTime(Long.parseLong(setType.getVal()));
                    break;
                case "weapon_type":
                    weapon.setType(org.l2j.xml.generated.ItemType.fromValue(setType.getVal()));
                    break;
                case "weight":
                    weapon.setWeight(Integer.parseInt(setType.getVal()));
                    break;
                case "commissionItemType":
                    weapon.setCommissionType(CommissionType.fromValue(setType.getVal()));
                    break;
            }
        });
    }

    private static ItemHandler getHandler(String val) {
        ItemHandler handler = null;
        switch (val) {
            case "ItemSkills":
                handler = ItemHandler.ITEM_SKILL;
                break;
            case "MercTicket":
                handler = ItemHandler.MERCENARY_TICKET;
                break;
            case "Recipes":
                handler = ItemHandler.RECIPE;
                break;
            case "BlessedSoulShots":
                handler = ItemHandler.BLESSED_SOUL_SHOT;
                break;
            case "ExtractableItems":
                handler = ItemHandler.EXTRACTABLE_ITEM;
                break;
            case "Book":
                handler = ItemHandler.BOOK;
                break;
            case "BeastSoulShot":
                handler = ItemHandler.BEAST_SOUL_SHOT;
                break;
            case "BeastSpiritShot":
                handler = ItemHandler.BEAST_SPIRIT_SHOT;
                break;
            case "SoulShots":
                handler = ItemHandler.SOUL_SHOT;
                break;
            case "BlessedSpiritShot":
                handler = ItemHandler.BLESSED_SPIRIT_SHOT;
                break;
            case "Bypass":
                handler = ItemHandler.BYPASS;
                break;
            case "Maps":
                handler = ItemHandler.MAP;
                break;
            case "SpiritShot":
                handler = ItemHandler.SPIRIT_SHOT;
                break;
            case "EnchantScrolls":
                handler = ItemHandler.ENCHANT_SCROLL;
                break;
            case "FishShots":
                handler = ItemHandler.FISH_SHOT;
                break;
            case "NicknameColor":
                handler = ItemHandler.NICKNAME_COLOR;
                break;
            case "RollingDice":
                handler = ItemHandler.DICE;
                break;
            case "Calculator":
                handler = ItemHandler.CALCULATOR;
                break;
        }
        return handler;
    }

    private static UseCondition getCondition(String val) {
        if(val.toLowerCase().contains("{ec_castle_num")) {
            var pattern = Pattern.compile(";\\{(\\d+)}");
            var matcher = pattern.matcher(val);
            if(matcher.find()) {
                var castleId = Integer.parseInt(matcher.group(1));
                var condition = factory.createOwnerCondition();
                condition.setType(OwnerConditionType.OWNER_CASTLE);
                condition.setOwnedId(castleId);
                var useCondition = factory.createUseCondition();
                useCondition.setCondition(factory.createCondition(condition));
                return useCondition;
            }
        } else if(val.toLowerCase().contains("{ec_clan_leader")) {
            var condition = factory.createOwnerCondition();
            condition.setType(OwnerConditionType.OWNER_CASTLE_LEADER);
            var useCondition = factory.createUseCondition();
            useCondition.setCondition(factory.createCondition(condition));
            return useCondition;
        }
        return null;
    }

    private static BodyPart getBodyPart(String name) {
        BodyPart part = null;
        switch (name) {
            case "lbracelet":
                part =  BodyPart.LEFT_BRACELET;
                break;
            case "hairall":
                part = BodyPart.FULL_HAIR;
                break;
            case "rear;lear":
                part = BodyPart.EAR;
                break;
            case "underwear":
                part = BodyPart.UNDERWEAR;
                break;
            case "alldress":
                part = BodyPart.FULL_BODY;
                break;
            case "hair2":
                part = BodyPart.HAIR_DOWN;
                break;
            case "lrhand":
                part = BodyPart.TWO_HANDS;
                break;
            case "hair":
                part = BodyPart.HAIR;
                break;
            case "rhand":
                part = BodyPart.RIGHT_HAND;
                break;
            case "chest":
                part = BodyPart.CHEST;
                break;
            case "legs":
                part = BodyPart.LEGS;
                break;
            case "gloves":
                part = BodyPart.GLOVES;
                break;
            case "feet":
                part = BodyPart.FEET;
                break;
            case "lhand":
                part = BodyPart.LEFT_HAND;
                break;
            case "brooch_jewel":
                part = BodyPart.BROOCH_JEWEL;
                break;
            case "rfinger;lfinger":
                part = BodyPart.FINGER;
                break;
            case "neck":
                part = BodyPart.NECK;
                break;
            case "talisman":
                part = BodyPart.TALISMAN;
                break;
            case "deco1":
                part = BodyPart.DECO;
                break;
            case "head":
                part = BodyPart.HEAD;
                break;
            case "brooch":
                part = BodyPart.BROOCH;
                break;
            case "waist":
                part = BodyPart.WAIST;
                break;
            case "onepiece":
                part = BodyPart.ONE_PIECE;
                break;
            case "back":
                part = BodyPart.BACK;
                break;
            case "rbracelet":
                part = BodyPart.RIGHT_BRACELET;
                break;
        }
        return part;
    }
}
