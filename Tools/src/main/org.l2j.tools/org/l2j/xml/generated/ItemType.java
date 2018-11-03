//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.3.1-b171012.0423 
// Consulte <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2018.11.02 às 11:52:58 AM BRT 
//


package org.l2j.xml.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de ItemType.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * <p>
 * <pre>
 * &lt;simpleType name="ItemType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="SWORD"/&gt;
 *     &lt;enumeration value="DUAL"/&gt;
 *     &lt;enumeration value="BLUNT"/&gt;
 *     &lt;enumeration value="BOW"/&gt;
 *     &lt;enumeration value="DAGGER"/&gt;
 *     &lt;enumeration value="POLE"/&gt;
 *     &lt;enumeration value="RAPIER"/&gt;
 *     &lt;enumeration value="FISHINGROD"/&gt;
 *     &lt;enumeration value="CROSSBOW"/&gt;
 *     &lt;enumeration value="TWOHANDCROSSBOW"/&gt;
 *     &lt;enumeration value="DUALFIST"/&gt;
 *     &lt;enumeration value="DUALDAGGER"/&gt;
 *     &lt;enumeration value="DUALBLUNT"/&gt;
 *     &lt;enumeration value="ETC"/&gt;
 *     &lt;enumeration value="LIGHT"/&gt;
 *     &lt;enumeration value="HEAVY"/&gt;
 *     &lt;enumeration value="MAGIC"/&gt;
 *     &lt;enumeration value="NONE"/&gt;
 *     &lt;enumeration value="ARROW"/&gt;
 *     &lt;enumeration value="SCROLL"/&gt;
 *     &lt;enumeration value="RECIPE"/&gt;
 *     &lt;enumeration value="ENCHT_WP"/&gt;
 *     &lt;enumeration value="ENCHT_ATTR_RUNE"/&gt;
 *     &lt;enumeration value="ENCHT_AM"/&gt;
 *     &lt;enumeration value="DYE"/&gt;
 *     &lt;enumeration value="MATERIAL"/&gt;
 *     &lt;enumeration value="TICKET_OF_LORD"/&gt;
 *     &lt;enumeration value="SOULSHOT"/&gt;
 *     &lt;enumeration value="CASTLE_GUARD"/&gt;
 *     &lt;enumeration value="POTION"/&gt;
 *     &lt;enumeration value="LURE"/&gt;
 *     &lt;enumeration value="BLESS_ENCHT_WP"/&gt;
 *     &lt;enumeration value="BLESS_ENCHT_AM"/&gt;
 *     &lt;enumeration value="BOLT"/&gt;
 *     &lt;enumeration value="SIGIL"/&gt;
 *     &lt;enumeration value="TELEPORTBOOKMARK"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ItemType")
@XmlEnum
public enum ItemType {

    SWORD,
    DUAL,
    BLUNT,
    BOW,
    DAGGER,
    POLE,
    RAPIER,
    FISHINGROD,
    CROSSBOW,
    TWOHANDCROSSBOW,
    DUALFIST,
    DUALDAGGER,
    DUALBLUNT,
    ETC,
    LIGHT,
    HEAVY,
    MAGIC,
    NONE,
    ARROW,
    SCROLL,
    RECIPE,
    ENCHT_WP,
    ENCHT_ATTR_RUNE,
    ENCHT_AM,
    DYE,
    MATERIAL,
    TICKET_OF_LORD,
    SOULSHOT,
    CASTLE_GUARD,
    POTION,
    LURE,
    BLESS_ENCHT_WP,
    BLESS_ENCHT_AM,
    BOLT,
    SIGIL,
    TELEPORTBOOKMARK;

    public String value() {
        return name();
    }

    public static ItemType fromValue(String v) {
        return valueOf(v);
    }

}
