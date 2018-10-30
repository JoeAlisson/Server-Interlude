//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.3.1-b171012.0423 
// Consulte <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2018.10.30 às 03:19:39 PM BRT 
//


package org.l2j.xml.generated;

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
 *         &lt;element name="bodyPart" type="{http://la2j.org}BodyPart"/&gt;
 *         &lt;element name="damage" type="{http://la2j.org}Damage"/&gt;
 *         &lt;element name="consume" type="{http://la2j.org}ItemConsume" minOccurs="0"/&gt;
 *       &lt;/choice&gt;
 *       &lt;attribute name="shots" type="{http://www.w3.org/2001/XMLSchema}int" default="1" /&gt;
 *       &lt;attribute name="magic" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Weapon", propOrder = {
    "bodyPart",
    "damage",
    "consume"
})
public class Weapon
    extends ItemTemplate
{

    @XmlSchemaType(name = "string")
    protected BodyPart bodyPart;
    protected Damage damage;
    protected ItemConsume consume;
    @XmlAttribute(name = "shots")
    protected Integer shots;
    @XmlAttribute(name = "magic")
    protected Boolean magic;

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
     * Obtém o valor da propriedade consume.
     * 
     * @return
     *     possible object is
     *     {@link ItemConsume }
     *     
     */
    public ItemConsume getConsume() {
        return consume;
    }

    /**
     * Define o valor da propriedade consume.
     * 
     * @param value
     *     allowed object is
     *     {@link ItemConsume }
     *     
     */
    public void setConsume(ItemConsume value) {
        this.consume = value;
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

    /**
     * Obtém o valor da propriedade magic.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isMagic() {
        if (magic == null) {
            return false;
        } else {
            return magic;
        }
    }

    /**
     * Define o valor da propriedade magic.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMagic(Boolean value) {
        this.magic = value;
    }

}
