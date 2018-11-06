//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.3.1-b171012.0423 
// Consulte <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2018.11.06 às 03:44:38 PM BRT 
//


package org.l2j.gameserver.templates.xml.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de StateType.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * <p>
 * <pre>
 * &lt;simpleType name="StateType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="IN_OLYMPIAD"/&gt;
 *     &lt;enumeration value="FLYING"/&gt;
 *     &lt;enumeration value="MOUNTING"/&gt;
 *     &lt;enumeration value="IS_CHAOTIC"/&gt;
 *     &lt;enumeration value="IS_HERO"/&gt;
 *     &lt;enumeration value="IS_MALE"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "StateType")
@XmlEnum
public enum StateType {

    IN_OLYMPIAD,
    FLYING,
    MOUNTING,
    IS_CHAOTIC,
    IS_HERO,
    IS_MALE;

    public String value() {
        return name();
    }

    public static StateType fromValue(String v) {
        return valueOf(v);
    }

}
