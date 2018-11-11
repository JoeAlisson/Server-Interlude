//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.3.1-b171012.0423 
// Consulte <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2018.11.11 às 07:52:07 AM BRT 
//


package org.l2j.gameserver.templates.xml.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de ItemSkill complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="ItemSkill"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="level" type="{http://www.w3.org/2001/XMLSchema}int" default="1" /&gt;
 *       &lt;attribute name="chance" type="{http://www.w3.org/2001/XMLSchema}int" default="100" /&gt;
 *       &lt;attribute name="triggerType" type="{http://la2j.org}SkillTrigger" default="ON_USE" /&gt;
 *       &lt;attribute name="triggerValue" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ItemSkill")
public class ItemSkill {

    @XmlAttribute(name = "id", required = true)
    protected int id;
    @XmlAttribute(name = "level")
    protected Integer level;
    @XmlAttribute(name = "chance")
    protected Integer chance;
    @XmlAttribute(name = "triggerType")
    protected SkillTrigger triggerType;
    @XmlAttribute(name = "triggerValue")
    protected Integer triggerValue;

    /**
     * Obtém o valor da propriedade id.
     * 
     */
    public int getId() {
        return id;
    }

    /**
     * Define o valor da propriedade id.
     * 
     */
    public void setId(int value) {
        this.id = value;
    }

    /**
     * Obtém o valor da propriedade level.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getLevel() {
        if (level == null) {
            return  1;
        } else {
            return level;
        }
    }

    /**
     * Define o valor da propriedade level.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setLevel(Integer value) {
        this.level = value;
    }

    /**
     * Obtém o valor da propriedade chance.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getChance() {
        if (chance == null) {
            return  100;
        } else {
            return chance;
        }
    }

    /**
     * Define o valor da propriedade chance.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setChance(Integer value) {
        this.chance = value;
    }

    /**
     * Obtém o valor da propriedade triggerType.
     * 
     * @return
     *     possible object is
     *     {@link SkillTrigger }
     *     
     */
    public SkillTrigger getTriggerType() {
        if (triggerType == null) {
            return SkillTrigger.ON_USE;
        } else {
            return triggerType;
        }
    }

    /**
     * Define o valor da propriedade triggerType.
     * 
     * @param value
     *     allowed object is
     *     {@link SkillTrigger }
     *     
     */
    public void setTriggerType(SkillTrigger value) {
        this.triggerType = value;
    }

    /**
     * Obtém o valor da propriedade triggerValue.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTriggerValue() {
        return triggerValue;
    }

    /**
     * Define o valor da propriedade triggerValue.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTriggerValue(Integer value) {
        this.triggerValue = value;
    }

}
