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
 * <p>Classe Java de Weapon complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="Weapon"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://la2j.org}ItemTemplate"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="damage" type="{http://la2j.org}Damage" minOccurs="0"/&gt;
 *         &lt;element name="consume" type="{http://la2j.org}ItemConsume" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
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
    "damage",
    "consume"
})
public class Weapon
    extends ItemTemplate
{

    protected Damage damage;
    protected ItemConsume consume;
    @XmlAttribute(name = "shots")
    protected Integer shots;
    @XmlAttribute(name = "magic")
    protected Boolean magic;

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
