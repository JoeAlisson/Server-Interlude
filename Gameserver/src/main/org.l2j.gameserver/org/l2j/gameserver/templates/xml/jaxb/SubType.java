//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.3.1-b171012.0423 
// Consulte <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2018.11.11 às 07:52:07 AM BRT 
//


package org.l2j.gameserver.templates.xml.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de SubType.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * <p>
 * <pre>
 * &lt;simpleType name="SubType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="TWO_HAND_SWORD"/&gt;
 *     &lt;enumeration value="SPEAR"/&gt;
 *     &lt;enumeration value="DUALSWORD"/&gt;
 *     &lt;enumeration value="ONE_HAND_MAGIC_BLUNT_WEAPON"/&gt;
 *     &lt;enumeration value="TWO_HAND_MAGIC_BLUNT_WEAPON"/&gt;
 *     &lt;enumeration value="ONE_HAND_MAGIC_SWORD"/&gt;
 *     &lt;enumeration value="TWO_HAND_BLUNT_WEAPON"/&gt;
 *     &lt;enumeration value="ONE_HAND_SWORD"/&gt;
 *     &lt;enumeration value="BLUNT_WEAPON"/&gt;
 *     &lt;enumeration value="FIST_WEAPON"/&gt;
 *     &lt;enumeration value="DAGGER"/&gt;
 *     &lt;enumeration value="BOW"/&gt;
 *     &lt;enumeration value="OTHER_WEAPON"/&gt;
 *     &lt;enumeration value="ARMOR_TOP"/&gt;
 *     &lt;enumeration value="FULL_BODY"/&gt;
 *     &lt;enumeration value="ARMOR_PANTS"/&gt;
 *     &lt;enumeration value="HELMET"/&gt;
 *     &lt;enumeration value="FEET"/&gt;
 *     &lt;enumeration value="SHIELD"/&gt;
 *     &lt;enumeration value="GLOVES"/&gt;
 *     &lt;enumeration value="HAIR_ACCESSORY"/&gt;
 *     &lt;enumeration value="RING"/&gt;
 *     &lt;enumeration value="EARRING"/&gt;
 *     &lt;enumeration value="NECKLACE"/&gt;
 *     &lt;enumeration value="BRACELET"/&gt;
 *     &lt;enumeration value="BELT"/&gt;
 *     &lt;enumeration value="POTION"/&gt;
 *     &lt;enumeration value="SOULSHOT"/&gt;
 *     &lt;enumeration value="SPIRITSHOT"/&gt;
 *     &lt;enumeration value="SCROLL_OTHER"/&gt;
 *     &lt;enumeration value="SCROLL_ENCHANT_WEAPON"/&gt;
 *     &lt;enumeration value="SCROLL_ENCHANT_ARMOR"/&gt;
 *     &lt;enumeration value="PET_EQUIPMENT"/&gt;
 *     &lt;enumeration value="PET_SUPPLIES"/&gt;
 *     &lt;enumeration value="OTHER_ITEM"/&gt;
 *     &lt;enumeration value="RECIPE"/&gt;
 *     &lt;enumeration value="MAJOR_CRAFTING_INGREDIENTS"/&gt;
 *     &lt;enumeration value="SPELLBOOK"/&gt;
 *     &lt;enumeration value="GEMSTONE"/&gt;
 *     &lt;enumeration value="DYES"/&gt;
 *     &lt;enumeration value="SOUL_CRYSTAL"/&gt;
 *     &lt;enumeration value="CRYSTAL"/&gt;
 *     &lt;enumeration value="HERB"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "SubType")
@XmlEnum
public enum SubType {

    TWO_HAND_SWORD,
    SPEAR,
    DUALSWORD,
    ONE_HAND_MAGIC_BLUNT_WEAPON,
    TWO_HAND_MAGIC_BLUNT_WEAPON,
    ONE_HAND_MAGIC_SWORD,
    TWO_HAND_BLUNT_WEAPON,
    ONE_HAND_SWORD,
    BLUNT_WEAPON,
    FIST_WEAPON,
    DAGGER,
    BOW,
    OTHER_WEAPON,

    ARMOR_TOP,
    FULL_BODY,
    ARMOR_PANTS,
    HELMET,
    FEET,
    SHIELD,
    GLOVES,
    HAIR_ACCESSORY,

    RING,
    EARRING,
    NECKLACE,
    BRACELET,
    BELT,

    POTION,
    SOULSHOT,
    SPIRITSHOT,
    SCROLL_OTHER,
    SCROLL_ENCHANT_WEAPON,
    SCROLL_ENCHANT_ARMOR,

    PET_EQUIPMENT,
    PET_SUPPLIES,

    OTHER_ITEM,
    RECIPE,
    MAJOR_CRAFTING_INGREDIENTS,
    SPELLBOOK,
    GEMSTONE,
    DYES,
    SOUL_CRYSTAL,
    CRYSTAL,
    HERB;

    public String value() {
        return name();
    }

    public static SubType fromValue(String v) {
        return valueOf(v);
    }

}
