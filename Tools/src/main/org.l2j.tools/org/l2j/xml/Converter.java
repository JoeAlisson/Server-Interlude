package org.l2j.xml;

import org.l2j.xml.generated.*;
import org.l2j.xml.generated.ObjectFactory;
import org.l2j.xml.old.generated.*;
import org.l2j.xml.old.generated.ItemType;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class Converter {

    static Map<String, ItemList> converted = new HashMap<>();
    static ObjectFactory factory = new ObjectFactory();
    private static JAXBContext context;
    private static Marshaller marsh;
    private static List<String> onClient;

    public static void main(String[] args) throws JAXBException, IOException {
        try {
            onClient = Files.readAllLines(Paths.get("itemname_classic-eu.csv"));
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            var schema = factory.newSchema(Paths.get("schema/item.xsd").toFile());

            context = JAXBContext.newInstance(ItemList.class);
            marsh = context.createMarshaller();
            marsh.setSchema(schema);
            marsh.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marsh.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://la2j.org ../schema/item.xsd");
            marsh.setEventHandler(new ValidationEventHandler() {
                @Override
                public boolean handleEvent(ValidationEvent event) {
                    System.out.println(event);
                    System.out.println(event.getMessage());
                    System.out.println(event.getLinkedException());
                    return true;
                }
            });
            OldXmlReader reader = new OldXmlReader();
            var dir = Paths.get("xml");
            Files.createDirectory(Paths.get("result"));
            Files.list(dir).forEach(path -> {
                processPath(reader, path);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        Verifier.verify();
    }

    private static void processPath(OldXmlReader reader, Path path) {
        System.out.println("Processing file " + path.getFileName());
        var file = path.toFile();
        reader.read(file);
        var itemList = new ItemList();
        converted.put(file.getName(), itemList);
        reader.getItems().values().forEach(itemType -> {
            processItem(itemList, itemType);
        });

        try {
            marsh.marshal(itemList, Paths.get("result/" + path.getFileName()).toFile());
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    private static void processItem(ItemList list, ItemType itemType) {
        if(!onClient.contains(String.valueOf(itemType.getId().intValue()))) {
            System.out.println("Not in client " + itemType.getId().intValue());
            return;
        }
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
        try {
            var item = factory.createItem();
            item.setId(itemType.getId().intValue());
            item.setName(itemType.getName());
            item.setAdditionalName(itemType.getAdditionalName());
            parseSet(itemType, item);
            parseCond(itemType, item);
            parseStats(itemType, item);
            parseSkills(itemType, item);

            list.getItemTemplate().add(factory.createItem(item));
        } catch (Exception e) {
            System.out.println("Error processing item " + itemType.getId().intValue());
            e.printStackTrace();
        }
    }

    private static void parseSet(ItemType itemType, Item item) {
        itemType.getSet().forEach(setType -> {
            switch (setType.getName()) {
                case "etcitem_type":
                    item.setType(org.l2j.xml.generated.ItemType.fromValue(setType.getVal()));
                    break;
                case "is_stackable":
                    item.setStackable(Boolean.parseBoolean(setType.getVal()));
                    break;
                case "recipe_id":
                    item.setRecipeId(Integer.parseInt(setType.getVal()));
                    break;
                case "crystal_count":
                    if (isNull(item.getCrystalInfo())) {
                        item.setCrystalInfo(factory.createCrystalInfo());
                    }
                    item.getCrystalInfo().setCount(Integer.parseInt(setType.getVal()));
                    break;
                case "crystal_type":
                    if (isNull(item.getCrystalInfo())) {
                        item.setCrystalInfo(factory.createCrystalInfo());
                    }
                    item.getCrystalInfo().setType(CrystalType.fromValue(setType.getVal()));
                    break;
                case "equip_condition":
                    var condition = getCondition(setType.getVal());
                    item.setCondition(condition);
                    break;
                case "handler":
                    var handler = getHandler(setType.getVal());
                    item.setHandler(handler);
                    break;
                case "icon":
                    item.setIcon(setType.getVal());
                    break;
                case "is_destroyable":
                    if (isNull(item.getRestriction())) {
                        item.setRestriction(factory.createItemRestriction());
                    }
                    item.getRestriction().setDestroyable(Boolean.parseBoolean(setType.getVal()));
                    break;
                case "is_dropable":
                    if (isNull(item.getRestriction())) {
                        item.setRestriction(factory.createItemRestriction());
                    }
                    item.getRestriction().setDropable(Boolean.parseBoolean(setType.getVal()));
                    break;
                case "is_freightable":
                    if (isNull(item.getRestriction())) {
                        item.setRestriction(factory.createItemRestriction());
                    }
                    item.getRestriction().setFreightable(Boolean.parseBoolean(setType.getVal()));
                    break;
                case "is_sellable":
                    if (isNull(item.getRestriction())) {
                        item.setRestriction(factory.createItemRestriction());
                    }
                    item.getRestriction().setSellable(Boolean.parseBoolean(setType.getVal()));
                    break;
                case "is_tradable":
                    if (isNull(item.getRestriction())) {
                        item.setRestriction(factory.createItemRestriction());
                    }
                    item.getRestriction().setTradeable(Boolean.parseBoolean(setType.getVal()));
                    break;
                case "is_questitem":
                    item.setQuestItem(Boolean.parseBoolean(setType.getVal()));
                    break;
                case "price":
                    item.setPrice(Long.parseLong(setType.getVal()));
                    break;
                case "reuse_delay":
                    item.setReuseDelay(Long.parseLong(setType.getVal()));
                    break;
                case "time":
                    item.setTime(Long.parseLong(setType.getVal()));
                    break;
                case "weight":
                    item.setWeight(Integer.parseInt(setType.getVal()));
                    break;
                case "commissionItemType":
                    item.setCommissionType(CommissionType.fromValue(setType.getVal()));
                    break;
            }
        });
    }

    private static void processArmor(ItemList list, ItemType itemType) {
        try {
            var armor = factory.createArmor();
            armor.setId(itemType.getId().intValue());
            armor.setName(itemType.getName());
            armor.setAdditionalName(itemType.getAdditionalName());
            parseSet(itemType, armor);
            parseCond(itemType, armor);
            parseStats(itemType, armor);
            parseSkills(itemType, armor);
            list.getItemTemplate().add(factory.createArmor(armor));
        } catch (Exception e) {
            System.out.println("Error processing Armor " + itemType.getId().intValue());
            e.printStackTrace();
        }
    }

    private static void parseSet(ItemType itemType, Armor armor) {
        itemType.getSet().forEach(setType -> {
            switch (setType.getName()) {
                case "armor_type":
                    armor.setType(org.l2j.xml.generated.ItemType.fromValue(setType.getVal()));
                    break;
                case "bodypart":
                    ;
                    armor.setBodyPart(getBodyPart(setType.getVal()));
                    break;
                case "crystal_count":
                    if (isNull(armor.getCrystalInfo())) {
                        armor.setCrystalInfo(factory.createCrystalInfo());
                    }
                    armor.getCrystalInfo().setCount(Integer.parseInt(setType.getVal()));
                    break;
                case "crystal_type":
                    if (isNull(armor.getCrystalInfo())) {
                        armor.setCrystalInfo(factory.createCrystalInfo());
                    }
                    armor.getCrystalInfo().setType(CrystalType.fromValue(setType.getVal()));
                    break;
                case "equip_condition":
                    var condition = getCondition(setType.getVal());
                    armor.setCondition(condition);
                    break;
                case "handler":
                    var handler = getHandler(setType.getVal());
                    armor.setHandler(handler);
                    break;
                case "icon":
                    armor.setIcon(setType.getVal());
                    break;
                case "is_destroyable":
                    if (isNull(armor.getRestriction())) {
                        armor.setRestriction(factory.createItemRestriction());
                    }
                    armor.getRestriction().setDestroyable(Boolean.parseBoolean(setType.getVal()));
                    break;
                case "is_dropable":
                    if (isNull(armor.getRestriction())) {
                        armor.setRestriction(factory.createItemRestriction());
                    }
                    armor.getRestriction().setDropable(Boolean.parseBoolean(setType.getVal()));
                    break;
                case "is_freightable":
                    if (isNull(armor.getRestriction())) {
                        armor.setRestriction(factory.createItemRestriction());
                    }
                    armor.getRestriction().setFreightable(Boolean.parseBoolean(setType.getVal()));
                    break;
                case "is_sellable":
                    if (isNull(armor.getRestriction())) {
                        armor.setRestriction(factory.createItemRestriction());
                    }
                    armor.getRestriction().setSellable(Boolean.parseBoolean(setType.getVal()));
                    break;
                case "is_tradable":
                    if (isNull(armor.getRestriction())) {
                        armor.setRestriction(factory.createItemRestriction());
                    }
                    armor.getRestriction().setTradeable(Boolean.parseBoolean(setType.getVal()));
                    break;
                case "is_questitem":
                    armor.setQuestItem(Boolean.parseBoolean(setType.getVal()));
                    break;
                case "price":
                    armor.setPrice(Long.parseLong(setType.getVal()));
                    break;
                case "reuse_delay":
                    armor.setReuseDelay(Long.parseLong(setType.getVal()));
                    break;
                case "time":
                    armor.setTime(Long.parseLong(setType.getVal()));
                    break;
                case "weight":
                    armor.setWeight(Integer.parseInt(setType.getVal()));
                    break;
                case "commissionItemType":
                    if (!setType.getVal().equalsIgnoreCase("false")) {
                        armor.setCommissionType(CommissionType.fromValue(setType.getVal()));
                    }
                    break;
            }
        });
    }

    private static void processWeapon(ItemList list, ItemType itemType) {
        try {
            var weapon = factory.createWeapon();
            weapon.setId(itemType.getId().intValue());
            weapon.setName(itemType.getName());
            weapon.setAdditionalName(itemType.getAdditionalName());
            parseSet(itemType, weapon);
            parseCond(itemType, weapon);
            parseStats(itemType, weapon);
            parseSkills(itemType, weapon);
            list.getItemTemplate().add(factory.createWeapon(weapon));
        } catch (Exception e) {
            System.out.println("Erro processing Weapon " + itemType.getId().intValue());
            e.printStackTrace();
        }
    }

    private static void parseSkills(ItemType itemType, ItemTemplate weapon) {
        if (isNull(itemType.getSkills())) {
            return;
        }
        for (SkillType skillType : itemType.getSkills().getSkill()) {
            var skill = factory.createItemSkill();
            skill.setId(skillType.getId().intValue());
            skill.setLevel(skillType.getLevel().intValue());
            if (nonNull(skillType.getTypeChance())) {
                skill.setChance(skillType.getTypeChance().intValue());
            }
            if (nonNull(skillType.getTypeValue())) {
                skill.setTriggerValue(skillType.getTypeValue().intValue());
            }
            if (nonNull(skillType.getType())) {
                switch (skillType.getType()) {
                    case "NORMAL":
                    case "ON_EQUIP":
                    case "ON_UNEQUIP":
                        skill.setTriggerType(SkillTrigger.ON_USE);
                        break;
                    case "ON_ENCHANT":
                        skill.setTriggerType(SkillTrigger.ENCHANTED);
                        break;
                    case "ON_CRITICAL_SKILL":
                        skill.setTriggerType(SkillTrigger.ON_CRITICAL);
                        break;
                    case "ON_MAGIC_SKILL":
                        skill.setTriggerType(SkillTrigger.ON_MAGIC_SKILL);
                        break;
                }
            }
            weapon.getSkill().add(skill);
        }
    }

    private static void parseStats(ItemType itemType, ItemTemplate weapon) {
        if (isNull(itemType.getStats())) {
            return;
        }
        for (Serializable content : itemType.getStats().getContent()) {
            if (content instanceof ForType.Stat) {
                var statType = (ForType.Stat) content;
                var stat = factory.createStat();
                stat.setFunction(Function.ADD);
                stat.setOrder(16);
                stat.setValue(Float.parseFloat(statType.getValue()));
                switch (statType.getType()) {
                    case P_ATK:
                        stat.setType(Stats.PHYSIC_ATTACK);
                        weapon.getStat().add(stat);
                        break;
                    case M_ATK:
                        stat.setType(Stats.MAGIC_ATTACK);
                        weapon.getStat().add(stat);
                        break;
                    case M_DEF:
                        stat.setType(Stats.MAGIC_DEFENCE);
                        weapon.getStat().add(stat);
                        break;
                    case P_DEF:
                        stat.setType(Stats.PHYSIC_DEFENCE);
                        weapon.getStat().add(stat);
                        break;
                    case S_DEF:
                        stat.setType(Stats.SHIELD_DEFENCE);
                        weapon.getStat().add(stat);
                        break;
                    case M_EVAS:
                        stat.setType(Stats.MAGIC_EVASION_RATE);
                        weapon.getStat().add(stat);
                        break;
                    case MAX_MP:
                        stat.setType(Stats.MAX_MP);
                        weapon.getStat().add(stat);
                        break;
                    case R_CRIT:
                        stat.setType(Stats.CRITICAL_RATE);
                        weapon.getStat().add(stat);
                        break;
                    case R_EVAS:
                        stat.setType(Stats.EVASION_RATE);
                        weapon.getStat().add(stat);
                        break;
                    case R_SHLD:
                        stat.setType(Stats.SHIELD_RATE);
                        weapon.getStat().add(stat);
                        break;
                    case DARK_RES:
                        stat.setType(Stats.DARK_RESISTENCE);
                        weapon.getStat().add(stat);
                        break;
                    case FIRE_RES:
                        stat.setType(Stats.FIRE_RESISTENCE);
                        weapon.getStat().add(stat);
                        break;
                    case HOLY_RES:
                        stat.setType(Stats.HOLY_RESISTENCE);
                        weapon.getStat().add(stat);
                        break;
                    case WIND_RES:
                        stat.setType(Stats.WIND_RESISTENCE);
                        weapon.getStat().add(stat);
                        break;
                    case ACC_MAGIC:
                        stat.setType(Stats.MAGIC_ACCURACY);
                        weapon.getStat().add(stat);
                        break;
                    case EARTH_RES:
                        stat.setType(Stats.EARTH_RESISTENCE);
                        weapon.getStat().add(stat);
                        break;
                    case P_ATK_SPD:
                        stat.setType(Stats.PHYSIC_ATTACK_SPEED);
                        weapon.getStat().add(stat);
                        break;
                    case WATER_RES:
                        stat.setType(Stats.WATER_RESISTENCE);
                        weapon.getStat().add(stat);
                        break;
                    case ACC_COMBAT:
                        stat.setType(Stats.ACCURACY);
                        weapon.getStat().add(stat);
                        break;
                    case DARK_POWER:
                        stat.setType(Stats.DARK);
                        weapon.getStat().add(stat);
                        break;
                    case FIRE_POWER:
                        stat.setType(Stats.FIRE);
                        weapon.getStat().add(stat);
                        break;
                    case HOLY_POWER:
                        stat.setType(Stats.HOLY);
                        weapon.getStat().add(stat);
                        break;
                    case MOVE_SPEED:
                        stat.setType(Stats.MOVE_SPEED);
                        weapon.getStat().add(stat);
                        break;
                    case WIND_POWER:
                        stat.setType(Stats.WIND);
                        weapon.getStat().add(stat);
                        break;
                    case EARTH_POWER:
                        stat.setType(Stats.EARTH);
                        weapon.getStat().add(stat);
                        break;
                    case M_CRIT_RATE:
                        stat.setType(Stats.MAGIC_CRITICAL_RATE);
                        weapon.getStat().add(stat);
                        break;
                    case P_ATK_ANGLE:
                        stat.setType(Stats.PHYSIC_ATTACK_ANGLE);
                        weapon.getStat().add(stat);
                        break;
                    case P_ATK_RANGE:
                        stat.setType(Stats.PHYSIC_ATTACK_RANGE);
                        weapon.getStat().add(stat);
                        break;
                    case WATER_POWER:
                        stat.setType(Stats.WATER);
                        weapon.getStat().add(stat);
                        break;
                    case BROOCH_JEWELS:
                        stat.setType(Stats.MAGIC_ACCURACY);
                        weapon.getStat().add(stat);
                        break;
                    case RANDOM_DAMAGE:
                        stat.setType(Stats.BROOCH_JEWEL);
                        weapon.getStat().add(stat);
                        break;
                    case MAGIC_SUCC_RES:
                        stat.setType(Stats.MAGIC_RESISTENCE);
                        weapon.getStat().add(stat);
                        break;
                    case INVENTORY_LIMIT:
                        stat.setType(Stats.INVENTORY_LIMIT);
                        weapon.getStat().add(stat);
                        break;
                }
            }
        }
    }

    private static void parseCond(ItemType itemType, ItemTemplate weapon) {
        itemType.getCond().forEach(condType -> {
            var usarCondition = false;
            if (isNull(weapon.getCondition())) {
                var useCondition = factory.createUseCondition();
                useCondition.setMessage(condType.getMsg());
                useCondition.setMessageId((int) condType.getMsgId());
                useCondition.setIncludeName(nonNull(condType.getAddName()) && condType.getAddName() == 1);
                weapon.setCondition(useCondition);
                usarCondition = true;
            } else {
                var useCondition = weapon.getCondition();
                if (nonNull(useCondition.getCondition())) {
                    var and = factory.createAND();
                    and.getCondition().add(useCondition.getCondition());
                    useCondition.setCondition(null);
                    useCondition.setOperator(factory.createAnd(and));
                } else if (!(useCondition.getOperator().getValue() instanceof AND)) {
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

    private static boolean parseCondition(ItemTemplate weapon, boolean usarCondition, Object usingAndOrNot) {
        if (usingAndOrNot instanceof AndType) {
            usarCondition = parseAndType(weapon, usarCondition, (AndType) usingAndOrNot);
        } else if (usingAndOrNot instanceof PlayerType) {
            parsePlayerType(weapon, usarCondition, (PlayerType) usingAndOrNot);
        } else if (usingAndOrNot instanceof TargetType) {
            parseTargetType(weapon, usarCondition, (TargetType) usingAndOrNot);
        }
        return usarCondition;
    }

    private static void parseTargetType(ItemTemplate weapon, boolean usarCondition, TargetType usingAndOrNot) {
        var targetType = usingAndOrNot;
        if (nonNull(targetType.getLevelRange())) {
            var levels = targetType.getLevelRange().split(";");
            var level = factory.createLevelCondition();
            level.setMin(Integer.parseInt(levels[0]));
            level.setMin(Integer.parseInt(levels[1]));
            level.setTarget(true);
            parseLevel(weapon, usarCondition, level);
        }
    }

    private static void parsePlayerType(ItemTemplate weapon, boolean usarCondition, PlayerType usingAndOrNot) {
        var playerType = usingAndOrNot;
        if (nonNull(playerType.getSex())) {
            var state = factory.createStateCondition();
            state.setState(StateType.IS_MALE);
            var stateCondition = factory.createState(state);
            if (playerType.getSex() == 0) {
                if (usarCondition) {
                    weapon.getCondition().setCondition(stateCondition);
                } else {
                    var operator = weapon.getCondition().getOperator().getValue();
                    if (operator instanceof AND) {
                        ((AND) operator).getCondition().add(stateCondition);
                    }
                }
            } else {
                var not = factory.createNOT();
                not.setCondition(stateCondition);
                if (usarCondition) {
                    weapon.getCondition().setOperator(factory.createNot(not));
                } else {
                    addNotInOperator(weapon, not);
                }
            }
        } else if (nonNull(playerType.isFlyMounted()) && !playerType.isFlyMounted()) {
            var state = factory.createStateCondition();
            state.setState(StateType.FLYING);
            var not = factory.createNOT();
            not.setCondition(factory.createState(state));
            parseNot(weapon, usarCondition, not);
        } else if (nonNull(playerType.isChaotic()) && !playerType.isChaotic()) {
            var state = factory.createStateCondition();
            state.setState(StateType.IS_CHAOTIC);
            var not = factory.createNOT();
            not.setCondition(factory.createState(state));
            parseNot(weapon, usarCondition, not);
        } else if (nonNull(playerType.getLevel())) {
            var levelCondition = factory.createLevelCondition();
            levelCondition.setMin(playerType.getLevel());
            parseLevel(weapon, usarCondition, levelCondition);
        } else if (nonNull(playerType.isIsHero()) && playerType.isIsHero()) {
            var state = factory.createStateCondition();
            state.setState(StateType.IS_HERO);
            if (usarCondition) {
                weapon.getCondition().setCondition(factory.createState(state));
            } else {
                var operator = weapon.getCondition().getOperator().getValue();
                if (operator instanceof AND) {
                    ((AND) operator).getCondition().add(factory.createState(state));
                }

            }
        }
    }

    private static boolean parseAndType(ItemTemplate weapon, boolean usarCondition, AndType andType) {
        if (usarCondition) {
            weapon.getCondition().setOperator(factory.createAnd(factory.createAND()));
            usarCondition = false;
        }
        for (Object o : andType.getUsingOrPlayerOrTarget()) {
            parseCondition(weapon, false, o);
        }
        return usarCondition;
    }

    private static void parseNot(ItemTemplate weapon, boolean usarCondition, NOT not) {
        if (usarCondition) {
            weapon.getCondition().setOperator(factory.createNot(not));
        } else {
            addNotInOperator(weapon, not);
        }
    }

    private static void parseLevel(ItemTemplate weapon, boolean usarCondition, LevelCondition level) {
        if (usarCondition) {
            weapon.getCondition().setCondition(factory.createLevel(level));
        } else {
            var operator = weapon.getCondition().getOperator().getValue();
            if (operator instanceof AND) {
                ((AND) operator).getCondition().add(factory.createLevel(level));
            }
        }
    }

    private static void addNotInOperator(ItemTemplate weapon, NOT not) {
        var operator = weapon.getCondition().getOperator().getValue();
        if (operator instanceof AND) {
            ((AND) operator).getOperator().add(factory.createNot(not));
        }
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
                    if (isNull(weapon.getRestriction())) {
                        weapon.setRestriction(factory.createItemRestriction());
                    }
                    weapon.getRestriction().setDestroyable(Boolean.parseBoolean(setType.getVal()));
                    break;
                case "is_dropable":
                    if (isNull(weapon.getRestriction())) {
                        weapon.setRestriction(factory.createItemRestriction());
                    }
                    weapon.getRestriction().setDropable(Boolean.parseBoolean(setType.getVal()));
                    break;
                case "is_freightable":
                    if (isNull(weapon.getRestriction())) {
                        weapon.setRestriction(factory.createItemRestriction());
                    }
                    weapon.getRestriction().setFreightable(Boolean.parseBoolean(setType.getVal()));
                    break;
                case "is_sellable":
                    if (isNull(weapon.getRestriction())) {
                        weapon.setRestriction(factory.createItemRestriction());
                    }
                    weapon.getRestriction().setSellable(Boolean.parseBoolean(setType.getVal()));
                    break;
                case "is_tradable":
                    if (isNull(weapon.getRestriction())) {
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
                    if (isNull(weapon.getConsume())) {
                        weapon.setConsume(factory.createItemConsume());
                    }
                    weapon.getConsume().setMp(Integer.parseInt(setType.getVal()));
                    break;
                case "price":
                    weapon.setPrice(Long.parseLong(setType.getVal()));
                    break;
                case "random_damage":
                    if (isNull(weapon.getDamage())) {
                        weapon.setDamage(factory.createDamage());
                    }
                    weapon.getDamage().setRandom(Integer.parseInt(setType.getVal()));
                    break;
                case "reuse_delay":
                    weapon.setReuseDelay(Long.parseLong(setType.getVal()));
                    break;
                case "soulshots":
                case "spiritshots":
                    if (isNull(weapon.getConsume())) {
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
        if (val.toLowerCase().contains("{ec_castle_num")) {
            var pattern = Pattern.compile(";\\{(\\d+)}");
            var matcher = pattern.matcher(val);
            if (matcher.find()) {
                var castleId = Integer.parseInt(matcher.group(1));
                var condition = factory.createOwnerCondition();
                condition.setType(OwnerConditionType.OWNER_CASTLE);
                condition.setOwnedId(castleId);
                var useCondition = factory.createUseCondition();
                useCondition.setCondition(factory.createCondition(condition));
                return useCondition;
            }
        } else if (val.toLowerCase().contains("{ec_clan_leader")) {
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
                part = BodyPart.LEFT_BRACELET;
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
