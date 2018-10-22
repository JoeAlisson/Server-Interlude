//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.3.1-b171012.0423 
// Consulte <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2018.10.21 às 04:09:27 PM BRT 
//


package org.l2j.gameserver.templates.xml.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de MagicAttack complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="MagicAttack"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="attack" type="{http://www.w3.org/2001/XMLSchema}int" default="1" /&gt;
 *       &lt;attribute name="criticalRate" type="{http://www.w3.org/2001/XMLSchema}float" default="1" /&gt;
 *       &lt;attribute name="speed" type="{http://www.w3.org/2001/XMLSchema}int" default="1" /&gt;
 *       &lt;attribute name="range" type="{http://www.w3.org/2001/XMLSchema}int" default="1" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MagicAttack")
public class MagicAttack {

    @XmlAttribute(name = "attack")
    protected Integer attack;
    @XmlAttribute(name = "criticalRate")
    protected Float criticalRate;
    @XmlAttribute(name = "speed")
    protected Integer speed;
    @XmlAttribute(name = "range")
    protected Integer range;

    /**
     * Obtém o valor da propriedade attack.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getAttack() {
        if (attack == null) {
            return  1;
        } else {
            return attack;
        }
    }

    /**
     * Define o valor da propriedade attack.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setAttack(Integer value) {
        this.attack = value;
    }

    /**
     * Obtém o valor da propriedade criticalRate.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public float getCriticalRate() {
        if (criticalRate == null) {
            return  1.0F;
        } else {
            return criticalRate;
        }
    }

    /**
     * Define o valor da propriedade criticalRate.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setCriticalRate(Float value) {
        this.criticalRate = value;
    }

    /**
     * Obtém o valor da propriedade speed.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getSpeed() {
        if (speed == null) {
            return  1;
        } else {
            return speed;
        }
    }

    /**
     * Define o valor da propriedade speed.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSpeed(Integer value) {
        this.speed = value;
    }

    /**
     * Obtém o valor da propriedade range.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getRange() {
        if (range == null) {
            return  1;
        } else {
            return range;
        }
    }

    /**
     * Define o valor da propriedade range.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRange(Integer value) {
        this.range = value;
    }

}
