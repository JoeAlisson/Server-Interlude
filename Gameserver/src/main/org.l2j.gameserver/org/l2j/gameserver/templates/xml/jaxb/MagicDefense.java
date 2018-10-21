//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.3.1-b171012.0423 
// Consulte <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2018.10.20 às 10:28:55 PM BRT 
//


package org.l2j.gameserver.templates.xml.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de MagicDefense complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="MagicDefense"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="necklace" type="{http://www.w3.org/2001/XMLSchema}int" default="1" /&gt;
 *       &lt;attribute name="rightEarring" type="{http://www.w3.org/2001/XMLSchema}int" default="1" /&gt;
 *       &lt;attribute name="leftEarring" type="{http://www.w3.org/2001/XMLSchema}int" default="1" /&gt;
 *       &lt;attribute name="rightRing" type="{http://www.w3.org/2001/XMLSchema}int" default="1" /&gt;
 *       &lt;attribute name="leftRing" type="{http://www.w3.org/2001/XMLSchema}int" default="1" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MagicDefense")
public class MagicDefense {

    @XmlAttribute(name = "necklace")
    protected Integer necklace;
    @XmlAttribute(name = "rightEarring")
    protected Integer rightEarring;
    @XmlAttribute(name = "leftEarring")
    protected Integer leftEarring;
    @XmlAttribute(name = "rightRing")
    protected Integer rightRing;
    @XmlAttribute(name = "leftRing")
    protected Integer leftRing;

    /**
     * Obtém o valor da propriedade necklace.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getNecklace() {
        if (necklace == null) {
            return  1;
        } else {
            return necklace;
        }
    }

    /**
     * Define o valor da propriedade necklace.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNecklace(Integer value) {
        this.necklace = value;
    }

    /**
     * Obtém o valor da propriedade rightEarring.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getRightEarring() {
        if (rightEarring == null) {
            return  1;
        } else {
            return rightEarring;
        }
    }

    /**
     * Define o valor da propriedade rightEarring.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRightEarring(Integer value) {
        this.rightEarring = value;
    }

    /**
     * Obtém o valor da propriedade leftEarring.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getLeftEarring() {
        if (leftEarring == null) {
            return  1;
        } else {
            return leftEarring;
        }
    }

    /**
     * Define o valor da propriedade leftEarring.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setLeftEarring(Integer value) {
        this.leftEarring = value;
    }

    /**
     * Obtém o valor da propriedade rightRing.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getRightRing() {
        if (rightRing == null) {
            return  1;
        } else {
            return rightRing;
        }
    }

    /**
     * Define o valor da propriedade rightRing.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRightRing(Integer value) {
        this.rightRing = value;
    }

    /**
     * Obtém o valor da propriedade leftRing.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getLeftRing() {
        if (leftRing == null) {
            return  1;
        } else {
            return leftRing;
        }
    }

    /**
     * Define o valor da propriedade leftRing.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setLeftRing(Integer value) {
        this.leftRing = value;
    }

}
