//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.3.1-b171012.0423 
// Consulte <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2018.11.03 às 12:02:18 PM BRT 
//


package org.l2j.gameserver.templates.xml.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de Attributes complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="Attributes"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="safeFallHeight" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="breath" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Attributes")
public class Attributes {

    @XmlAttribute(name = "safeFallHeight", required = true)
    protected int safeFallHeight;
    @XmlAttribute(name = "breath", required = true)
    protected int breath;

    /**
     * Obtém o valor da propriedade safeFallHeight.
     * 
     */
    public int getSafeFallHeight() {
        return safeFallHeight;
    }

    /**
     * Define o valor da propriedade safeFallHeight.
     * 
     */
    public void setSafeFallHeight(int value) {
        this.safeFallHeight = value;
    }

    /**
     * Obtém o valor da propriedade breath.
     * 
     */
    public int getBreath() {
        return breath;
    }

    /**
     * Define o valor da propriedade breath.
     * 
     */
    public void setBreath(int value) {
        this.breath = value;
    }

}
