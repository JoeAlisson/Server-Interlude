//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.3.1-b171012.0423 
// Consulte <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2018.10.29 às 07:49:11 PM BRT 
//


package com.l2jb.xml.old.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de statType.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * <p>
 * <pre>
 * &lt;simpleType name="statType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *     &lt;enumeration value="mAtk"/&gt;
 *     &lt;enumeration value="mDef"/&gt;
 *     &lt;enumeration value="pAtk"/&gt;
 *     &lt;enumeration value="pDef"/&gt;
 *     &lt;enumeration value="sDef"/&gt;
 *     &lt;enumeration value="mAtk"/&gt;
 *     &lt;enumeration value="pAtkAngle"/&gt;
 *     &lt;enumeration value="pAtkRange"/&gt;
 *     &lt;enumeration value="pAtkSpd"/&gt;
 *     &lt;enumeration value="rCrit"/&gt;
 *     &lt;enumeration value="mCritRate"/&gt;
 *     &lt;enumeration value="rShld"/&gt;
 *     &lt;enumeration value="rEvas"/&gt;
 *     &lt;enumeration value="mEvas"/&gt;
 *     &lt;enumeration value="accCombat"/&gt;
 *     &lt;enumeration value="accMagic"/&gt;
 *     &lt;enumeration value="darkRes"/&gt;
 *     &lt;enumeration value="earthRes"/&gt;
 *     &lt;enumeration value="fireRes"/&gt;
 *     &lt;enumeration value="holyPower"/&gt;
 *     &lt;enumeration value="holyRes"/&gt;
 *     &lt;enumeration value="maxMp"/&gt;
 *     &lt;enumeration value="waterRes"/&gt;
 *     &lt;enumeration value="windRes"/&gt;
 *     &lt;enumeration value="magicSuccRes"/&gt;
 *     &lt;enumeration value="moveSpeed"/&gt;
 *     &lt;enumeration value="broochJewels"/&gt;
 *     &lt;enumeration value="inventoryLimit"/&gt;
 *     &lt;enumeration value="randomDamage"/&gt;
 *     &lt;enumeration value="firePower"/&gt;
 *     &lt;enumeration value="waterPower"/&gt;
 *     &lt;enumeration value="windPower"/&gt;
 *     &lt;enumeration value="earthPower"/&gt;
 *     &lt;enumeration value="holyPower"/&gt;
 *     &lt;enumeration value="darkPower"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "statType")
@XmlEnum
public enum StatType {

    @XmlEnumValue("mAtk")
    M_ATK("mAtk"),
    @XmlEnumValue("mDef")
    M_DEF("mDef"),
    @XmlEnumValue("pAtk")
    P_ATK("pAtk"),
    @XmlEnumValue("pDef")
    P_DEF("pDef"),
    @XmlEnumValue("sDef")
    S_DEF("sDef"),
    @XmlEnumValue("pAtkAngle")
    P_ATK_ANGLE("pAtkAngle"),
    @XmlEnumValue("pAtkRange")
    P_ATK_RANGE("pAtkRange"),
    @XmlEnumValue("pAtkSpd")
    P_ATK_SPD("pAtkSpd"),
    @XmlEnumValue("rCrit")
    R_CRIT("rCrit"),
    @XmlEnumValue("mCritRate")
    M_CRIT_RATE("mCritRate"),
    @XmlEnumValue("rShld")
    R_SHLD("rShld"),
    @XmlEnumValue("rEvas")
    R_EVAS("rEvas"),
    @XmlEnumValue("mEvas")
    M_EVAS("mEvas"),
    @XmlEnumValue("accCombat")
    ACC_COMBAT("accCombat"),
    @XmlEnumValue("accMagic")
    ACC_MAGIC("accMagic"),
    @XmlEnumValue("darkRes")
    DARK_RES("darkRes"),
    @XmlEnumValue("earthRes")
    EARTH_RES("earthRes"),
    @XmlEnumValue("fireRes")
    FIRE_RES("fireRes"),
    @XmlEnumValue("holyPower")
    HOLY_POWER("holyPower"),
    @XmlEnumValue("holyRes")
    HOLY_RES("holyRes"),
    @XmlEnumValue("maxMp")
    MAX_MP("maxMp"),
    @XmlEnumValue("waterRes")
    WATER_RES("waterRes"),
    @XmlEnumValue("windRes")
    WIND_RES("windRes"),
    @XmlEnumValue("magicSuccRes")
    MAGIC_SUCC_RES("magicSuccRes"),
    @XmlEnumValue("moveSpeed")
    MOVE_SPEED("moveSpeed"),
    @XmlEnumValue("broochJewels")
    BROOCH_JEWELS("broochJewels"),
    @XmlEnumValue("inventoryLimit")
    INVENTORY_LIMIT("inventoryLimit"),
    @XmlEnumValue("randomDamage")
    RANDOM_DAMAGE("randomDamage"),
    @XmlEnumValue("firePower")
    FIRE_POWER("firePower"),
    @XmlEnumValue("waterPower")
    WATER_POWER("waterPower"),
    @XmlEnumValue("windPower")
    WIND_POWER("windPower"),
    @XmlEnumValue("earthPower")
    EARTH_POWER("earthPower"),
    @XmlEnumValue("darkPower")
    DARK_POWER("darkPower");
    private final String value;

    StatType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static StatType fromValue(String v) {
        for (StatType c: StatType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
