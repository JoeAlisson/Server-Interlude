//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.3.1-b171012.0423 
// Consulte <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2018.10.24 às 08:00:04 AM BRT 
//


package org.l2j.gameserver.templates.xml.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de PhysicDefense complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="PhysicDefense"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="chest" type="{http://www.w3.org/2001/XMLSchema}int" default="1" /&gt;
 *       &lt;attribute name="legs" type="{http://www.w3.org/2001/XMLSchema}int" default="1" /&gt;
 *       &lt;attribute name="helmet" type="{http://www.w3.org/2001/XMLSchema}int" default="1" /&gt;
 *       &lt;attribute name="gloves" type="{http://www.w3.org/2001/XMLSchema}int" default="1" /&gt;
 *       &lt;attribute name="boots" type="{http://www.w3.org/2001/XMLSchema}int" default="1" /&gt;
 *       &lt;attribute name="pendant" type="{http://www.w3.org/2001/XMLSchema}int" default="1" /&gt;
 *       &lt;attribute name="cloak" type="{http://www.w3.org/2001/XMLSchema}int" default="1" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PhysicDefense")
public class PhysicDefense {

    @XmlAttribute(name = "chest")
    protected Integer chest;
    @XmlAttribute(name = "legs")
    protected Integer legs;
    @XmlAttribute(name = "helmet")
    protected Integer helmet;
    @XmlAttribute(name = "gloves")
    protected Integer gloves;
    @XmlAttribute(name = "boots")
    protected Integer boots;
    @XmlAttribute(name = "pendant")
    protected Integer pendant;
    @XmlAttribute(name = "cloak")
    protected Integer cloak;

    /**
     * Obtém o valor da propriedade chest.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getChest() {
        if (chest == null) {
            return  1;
        } else {
            return chest;
        }
    }

    /**
     * Define o valor da propriedade chest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setChest(Integer value) {
        this.chest = value;
    }

    /**
     * Obtém o valor da propriedade legs.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getLegs() {
        if (legs == null) {
            return  1;
        } else {
            return legs;
        }
    }

    /**
     * Define o valor da propriedade legs.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setLegs(Integer value) {
        this.legs = value;
    }

    /**
     * Obtém o valor da propriedade helmet.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getHelmet() {
        if (helmet == null) {
            return  1;
        } else {
            return helmet;
        }
    }

    /**
     * Define o valor da propriedade helmet.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setHelmet(Integer value) {
        this.helmet = value;
    }

    /**
     * Obtém o valor da propriedade gloves.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getGloves() {
        if (gloves == null) {
            return  1;
        } else {
            return gloves;
        }
    }

    /**
     * Define o valor da propriedade gloves.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setGloves(Integer value) {
        this.gloves = value;
    }

    /**
     * Obtém o valor da propriedade boots.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getBoots() {
        if (boots == null) {
            return  1;
        } else {
            return boots;
        }
    }

    /**
     * Define o valor da propriedade boots.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setBoots(Integer value) {
        this.boots = value;
    }

    /**
     * Obtém o valor da propriedade pendant.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getPendant() {
        if (pendant == null) {
            return  1;
        } else {
            return pendant;
        }
    }

    /**
     * Define o valor da propriedade pendant.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPendant(Integer value) {
        this.pendant = value;
    }

    /**
     * Obtém o valor da propriedade cloak.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getCloak() {
        if (cloak == null) {
            return  1;
        } else {
            return cloak;
        }
    }

    /**
     * Define o valor da propriedade cloak.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCloak(Integer value) {
        this.cloak = value;
    }

}
