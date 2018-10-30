//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.3.1-b171012.0423 
// Consulte <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2018.10.30 às 03:19:39 PM BRT 
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
 *     &lt;enumeration value="NONE"/&gt;
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
    NONE;

    public String value() {
        return name();
    }

    public static ItemType fromValue(String v) {
        return valueOf(v);
    }

}
