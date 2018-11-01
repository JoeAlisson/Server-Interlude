//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.3.1-b171012.0423 
// Consulte <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2018.11.01 às 01:28:10 PM BRT 
//


package org.l2j.xml.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de Race.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * <p>
 * <pre>
 * &lt;simpleType name="Race"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="HUMAN"/&gt;
 *     &lt;enumeration value="ELF"/&gt;
 *     &lt;enumeration value="DARKELF"/&gt;
 *     &lt;enumeration value="ORC"/&gt;
 *     &lt;enumeration value="DWARF"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "Race")
@XmlEnum
public enum Race {

    HUMAN,
    ELF,
    DARKELF,
    ORC,
    DWARF;

    public String value() {
        return name();
    }

    public static Race fromValue(String v) {
        return valueOf(v);
    }

}
