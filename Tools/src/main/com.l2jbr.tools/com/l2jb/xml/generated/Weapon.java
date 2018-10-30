//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.3.1-b171012.0423 
// Consulte <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2018.10.29 às 07:57:01 PM BRT 
//


package com.l2jb.xml.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de Weapon complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="Weapon"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://la2j.org}ItemTemplate"&gt;
 *       &lt;choice&gt;
 *         &lt;element name="type" type="{http://la2j.org}ItemType"/&gt;
 *         &lt;element name="bodyPart" type="{http://la2j.org}BodyPart"/&gt;
 *         &lt;element name="damage" type="{http://la2j.org}Damage"/&gt;
 *       &lt;/choice&gt;
 *       &lt;attribute name="shots" type="{http://www.w3.org/2001/XMLSchema}int" default="1" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Weapon", propOrder = {
    "type",
    "bodyPart",
    "damage"
})
public class Weapon
    extends ItemTemplate
{

    @XmlSchemaType(name = "string")
    protected ItemType type;
    @XmlSchemaType(name = "string")
    protected BodyPart bodyPart;
    protected Damage damage;
    @XmlAttribute(name = "shots")
    protected Integer shots;

    /**
     * Obtém o valor da propriedade type.
     * 
     * @return
     *     possible object is
     *     {@link ItemType }
     *     
     */
    public ItemType getType() {
        return type;
    }

    /**
     * Define o valor da propriedade type.
     * 
     * @param value
     *     allowed object is
     *     {@link ItemType }
     *     
     */
    public void setType(ItemType value) {
        this.type = value;
    }

    /**
     * Obtém o valor da propriedade bodyPart.
     * 
     * @return
     *     possible object is
     *     {@link BodyPart }
     *     
     */
    public BodyPart getBodyPart() {
        return bodyPart;
    }

    /**
     * Define o valor da propriedade bodyPart.
     * 
     * @param value
     *     allowed object is
     *     {@link BodyPart }
     *     
     */
    public void setBodyPart(BodyPart value) {
        this.bodyPart = value;
    }

    /**
     * Obtém o valor da propriedade damage.
     * 
     * @return
     *     possible object is
     *     {@link Damage }
     *     
     */
    public Damage getDamage() {
        return damage;
    }

    /**
     * Define o valor da propriedade damage.
     * 
     * @param value
     *     allowed object is
     *     {@link Damage }
     *     
     */
    public void setDamage(Damage value) {
        this.damage = value;
    }

    /**
     * Obtém o valor da propriedade shots.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getShots() {
        if (shots == null) {
            return  1;
        } else {
            return shots;
        }
    }

    /**
     * Define o valor da propriedade shots.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setShots(Integer value) {
        this.shots = value;
    }

}
