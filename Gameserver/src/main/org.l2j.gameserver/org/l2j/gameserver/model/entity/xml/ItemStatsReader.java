package org.l2j.gameserver.model.entity.xml;

import org.l2j.commons.xml.XMLReader;
import org.l2j.gameserver.datatables.SkillTable;
import org.l2j.gameserver.model.L2Skill;
import org.l2j.gameserver.model.entity.database.ItemTemplate;
import org.l2j.gameserver.skills.Stats;
import org.l2j.gameserver.skills.conditions.*;
import org.l2j.gameserver.skills.conditions.ConditionPlayerState.CheckPlayerState;
import org.l2j.gameserver.skills.funcs.FuncTemplate;
import org.l2j.gameserver.skills.funcs.Lambda;
import org.l2j.gameserver.skills.funcs.LambdaConst;
import org.l2j.gameserver.templates.ItemType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import static java.util.Objects.isNull;

public class ItemStatsReader extends XMLReader<ItemList> {

    private final Map<Integer, ItemStat> itemStats;

    public ItemStatsReader() throws JAXBException {
        itemStats  = new HashMap<>();
    }

    private ItemStat getItemStat(int itemId) {
        return itemStats.get(itemId);
    }

    @Override
    protected void processEntity(ItemList entity) {
        entity.getItem().forEach(itemStat -> itemStats.put(itemStat.getId(), itemStat));
    }

    public void attach(ItemTemplate item) {
        ItemStat itemStat = getItemStat(item.getId());
        if(isNull(itemStat)) {
            return;
        }
        for(XmlTypeStat statType : itemStat.getStat()) {
            Condition condition = parseConditions(statType.getConditions());
            Lambda lambda = new LambdaConst(statType.getValue());
            Stats stat = Stats.valueOf(statType.getName().name());
            FuncTemplate funcTemplate = new FuncTemplate(condition, null, statType.getFunction(), stat, statType.getOrder(), lambda);
            item.addFunction(funcTemplate);
        }

        for(XmlItemSkill itemSkill : itemStat.getSkill()) {
            L2Skill skill = SkillTable.getInstance().getInfo(itemSkill.getId(), itemSkill.getLevel());
            if(isNull(skill)) {
                logger.warn("Skill not found {} attached on XML item {} ({}).", itemSkill.getId(), itemStat.getName(), itemStat.getId());
                continue;
            }
            skill.attach(new ConditionGameChance(itemSkill.getChance()), true);
            item.attach(skill, itemSkill.getTriggerType());
        }
    }

    private Condition parseConditions(XmlStatCondition conditions) {
        if(isNull(conditions)) {
            return null;
        }
        if(!isNull(conditions.getOperator())) {
            return constructConditionWithOperator(conditions.getOperator().getValue());
        }
        return constructCondition(conditions.getCondition().getValue());
    }


    private Condition constructConditionWithOperator(XmlStatConditionOperator operator) {
        if(operator instanceof XmlStatConditionAND) {
            return parseConditionAnd((XmlStatConditionAND) operator);
        } else if(operator instanceof XmlStatConditionOR) {
            return parseConditionOr((XmlStatConditionOR) operator);
        } else if(operator instanceof XmlStatConditionNOT) {
            return parseConditionNot((XmlStatConditionNOT) operator);
        }
        return null;
    }

    private Condition parseConditionNot(XmlStatConditionNOT operator) {
        if(!isNull(operator.getOperator())) {
            return new ConditionLogicNot(constructConditionWithOperator(operator.getOperator().getValue()));
        }
        return new ConditionLogicNot(constructCondition(operator.getCondition().getValue()));
    }

    private Condition parseConditionOr(XmlStatConditionOR operator) {
        ConditionLogicOr or = new ConditionLogicOr();
        operator.getOperator().forEach(op -> or.add(constructConditionWithOperator(op.getValue())));
        operator.getCondition().forEach(condition -> or.add(constructCondition(condition.getValue())));
        return or;
    }

    private Condition parseConditionAnd(XmlStatConditionAND operator) {
        ConditionLogicAnd and = new ConditionLogicAnd();
        operator.getOperator().forEach(op -> and.add(constructConditionWithOperator(op.getValue())));
        operator.getCondition().forEach(condition -> and.add(constructCondition(condition.getValue())));
        return and;
    }

    private Condition constructCondition(XmlStatConditionType condition) {
        if(condition instanceof XmlStatUsingCondition) {
            return parseUsingCondition((XmlStatUsingCondition)condition);
        } else if(condition instanceof XmlStatPlayerCondition) {
            return parsePlayerCondition((XmlStatPlayerCondition) condition);
        } else if(condition instanceof XmlStatGameCondition) {
            return parseGameCondition((XmlStatGameCondition) condition);
        }
        return null;
    }

    private Condition parseGameCondition(XmlStatGameCondition condition) {
        switch (condition.getKind().toLowerCase()) {
            case "chance": {
                int chance = Integer.parseInt(condition.getValue());
                return new ConditionGameChance(chance);
            }
            case "skill": {
                boolean value = Boolean.parseBoolean(condition.getValue());
                return new ConditionWithSkill(value);
            }
        }
        return null;
    }

    private Condition parsePlayerCondition(XmlStatPlayerCondition condition) {
        switch (condition.getKind().toLowerCase()) {
            case "behind": {
                boolean value = Boolean.parseBoolean(condition.getValue());
                return new ConditionPlayerState(CheckPlayerState.BEHIND, value);
            }
            case "hp": {
                int hp = Integer.parseInt(condition.getValue());
                return new ConditionPlayerHp(hp);
            }
        }
        return null;
    }

    private Condition parseUsingCondition(XmlStatUsingCondition condition) {
        switch (condition.getKind().toLowerCase()) {
            case "slotitem":
                return new ConditionSlotItemId(condition.getSlot(), condition.getItem(), condition.getEnchantment());
            case "itemtype":
                return new ConditionUsingItemType(createItemTypesMask(condition.getTypes()));
            case "skill":
                return new ConditionUsingSkill(condition.getSkill());
        }
        return null;
    }

    private int createItemTypesMask(String types) {
        int mask = 0;
        StringTokenizer tokens = new StringTokenizer(types, ",");
        while (tokens.hasMoreElements()) {
            try {
                String type = tokens.nextToken().toUpperCase().trim();
                mask |= ItemType.valueOf(type).mask();
            } catch (IllegalArgumentException | NullPointerException e) {
                logger.error("Error parsing Condition");
                logger.error(e.getLocalizedMessage(), e);
            }
        }
        return mask;
    }

    @Override
    protected JAXBContext getJAXBContext() throws JAXBException {
        return JAXBContext.newInstance(ItemList.class);
    }

    @Override
    public String getSchemaFilePath() {
        return "data/stats/item.xsd";
    }

    @Override
    public String[] getXmlFileDirectories() {
        return new String[] { "data/stats/armor", "data/stats/weapon" };
    }
}
