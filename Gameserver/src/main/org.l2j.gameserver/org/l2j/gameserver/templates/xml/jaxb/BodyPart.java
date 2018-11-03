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
 * <p>Classe Java de BodyPart.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * <p>
 * <pre>
 * &lt;simpleType name="BodyPart"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="NONE"/&gt;
 *     &lt;enumeration value="HEAD"/&gt;
 *     &lt;enumeration value="RIGHT_HAND"/&gt;
 *     &lt;enumeration value="LEFT_BRACELET"/&gt;
 *     &lt;enumeration value="FULL_HAIR"/&gt;
 *     &lt;enumeration value="LEFT_EAR"/&gt;
 *     &lt;enumeration value="RIGHT_EAR"/&gt;
 *     &lt;enumeration value="EAR"/&gt;
 *     &lt;enumeration value="UNDERWEAR"/&gt;
 *     &lt;enumeration value="FULL_BODY"/&gt;
 *     &lt;enumeration value="HAIR_DOWN"/&gt;
 *     &lt;enumeration value="TWO_HANDS"/&gt;
 *     &lt;enumeration value="HAIR"/&gt;
 *     &lt;enumeration value="RIGHT_HAND"/&gt;
 *     &lt;enumeration value="CHEST"/&gt;
 *     &lt;enumeration value="LEGS"/&gt;
 *     &lt;enumeration value="GLOVES"/&gt;
 *     &lt;enumeration value="FEET"/&gt;
 *     &lt;enumeration value="LEFT_HAND"/&gt;
 *     &lt;enumeration value="BROOCH_JEWEL"/&gt;
 *     &lt;enumeration value="LEFT_FINGER"/&gt;
 *     &lt;enumeration value="RIGHT_FINGER"/&gt;
 *     &lt;enumeration value="FINGER"/&gt;
 *     &lt;enumeration value="NECK"/&gt;
 *     &lt;enumeration value="TALISMAN"/&gt;
 *     &lt;enumeration value="DECO"/&gt;
 *     &lt;enumeration value="BROOCH"/&gt;
 *     &lt;enumeration value="WAIST"/&gt;
 *     &lt;enumeration value="ONE_PIECE"/&gt;
 *     &lt;enumeration value="BACK"/&gt;
 *     &lt;enumeration value="RIGHT_BRACELET"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "BodyPart")
@XmlEnum
public enum BodyPart {

    NONE,
    HEAD,
    RIGHT_HAND,
    LEFT_BRACELET,
    FULL_HAIR,
    LEFT_EAR,
    RIGHT_EAR,
    EAR,
    UNDERWEAR,
    FULL_BODY,
    HAIR_DOWN,
    TWO_HANDS,
    HAIR,
    CHEST,
    LEGS,
    GLOVES,
    FEET,
    LEFT_HAND,
    BROOCH_JEWEL,
    LEFT_FINGER,
    RIGHT_FINGER,
    FINGER,
    NECK,
    TALISMAN,
    DECO,
    BROOCH,
    WAIST,
    ONE_PIECE,
    BACK,
    RIGHT_BRACELET;

    public String value() {
        return name();
    }

    public static BodyPart fromValue(String v) {
        return valueOf(v);
    }

}
