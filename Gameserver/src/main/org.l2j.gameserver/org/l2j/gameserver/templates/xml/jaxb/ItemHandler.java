//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.3.1-b171012.0423 
// Consulte <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2018.11.03 às 12:02:18 PM BRT 
//


package org.l2j.gameserver.templates.xml.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de ItemHandler.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * <p>
 * <pre>
 * &lt;simpleType name="ItemHandler"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="NONE"/&gt;
 *     &lt;enumeration value="ITEM_SKILL"/&gt;
 *     &lt;enumeration value="MERCENARY_TICKET"/&gt;
 *     &lt;enumeration value="RECIPE"/&gt;
 *     &lt;enumeration value="BLESSED_SOUL_SHOT"/&gt;
 *     &lt;enumeration value="EXTRACTABLE_ITEM"/&gt;
 *     &lt;enumeration value="BOOK"/&gt;
 *     &lt;enumeration value="BEAST_SOUL_SHOT"/&gt;
 *     &lt;enumeration value="BEAST_SPIRIT_SHOT"/&gt;
 *     &lt;enumeration value="SOUL_SHOT"/&gt;
 *     &lt;enumeration value="BLESSED_SPIRIT_SHOT"/&gt;
 *     &lt;enumeration value="BYPASS"/&gt;
 *     &lt;enumeration value="MAP"/&gt;
 *     &lt;enumeration value="SPIRIT_SHOT"/&gt;
 *     &lt;enumeration value="ENCHANT_SCROLL"/&gt;
 *     &lt;enumeration value="FISH_SHOT"/&gt;
 *     &lt;enumeration value="NICKNAME_COLOR"/&gt;
 *     &lt;enumeration value="DICE"/&gt;
 *     &lt;enumeration value="CALCULATOR"/&gt;
 *     &lt;enumeration value="RESURRECTION"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ItemHandler")
@XmlEnum
public enum ItemHandler {

    NONE,
    ITEM_SKILL,
    MERCENARY_TICKET,
    RECIPE,
    BLESSED_SOUL_SHOT,
    EXTRACTABLE_ITEM,
    BOOK,
    BEAST_SOUL_SHOT,
    BEAST_SPIRIT_SHOT,
    SOUL_SHOT,
    BLESSED_SPIRIT_SHOT,
    BYPASS,
    MAP,
    SPIRIT_SHOT,
    ENCHANT_SCROLL,
    FISH_SHOT,
    NICKNAME_COLOR,
    DICE,
    CALCULATOR,
    RESURRECTION;

    public String value() {
        return name();
    }

    public static ItemHandler fromValue(String v) {
        return valueOf(v);
    }

}
