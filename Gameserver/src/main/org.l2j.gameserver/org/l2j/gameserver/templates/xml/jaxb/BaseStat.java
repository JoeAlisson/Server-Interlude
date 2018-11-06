//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.3.1-b171012.0423 
// Consulte <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2018.11.06 às 03:44:38 PM BRT 
//


package org.l2j.gameserver.templates.xml.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de BaseStat complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="BaseStat"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="strength" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="constitution" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="dexterity" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="mentality" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="wisdom" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="intelligence" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BaseStat")
public class BaseStat {

    @XmlAttribute(name = "strength", required = true)
    protected int strength;
    @XmlAttribute(name = "constitution", required = true)
    protected int constitution;
    @XmlAttribute(name = "dexterity", required = true)
    protected int dexterity;
    @XmlAttribute(name = "mentality", required = true)
    protected int mentality;
    @XmlAttribute(name = "wisdom", required = true)
    protected int wisdom;
    @XmlAttribute(name = "intelligence", required = true)
    protected int intelligence;

    /**
     * Obtém o valor da propriedade strength.
     * 
     */
    public int getStrength() {
        return strength;
    }

    /**
     * Define o valor da propriedade strength.
     * 
     */
    public void setStrength(int value) {
        this.strength = value;
    }

    /**
     * Obtém o valor da propriedade constitution.
     * 
     */
    public int getConstitution() {
        return constitution;
    }

    /**
     * Define o valor da propriedade constitution.
     * 
     */
    public void setConstitution(int value) {
        this.constitution = value;
    }

    /**
     * Obtém o valor da propriedade dexterity.
     * 
     */
    public int getDexterity() {
        return dexterity;
    }

    /**
     * Define o valor da propriedade dexterity.
     * 
     */
    public void setDexterity(int value) {
        this.dexterity = value;
    }

    /**
     * Obtém o valor da propriedade mentality.
     * 
     */
    public int getMentality() {
        return mentality;
    }

    /**
     * Define o valor da propriedade mentality.
     * 
     */
    public void setMentality(int value) {
        this.mentality = value;
    }

    /**
     * Obtém o valor da propriedade wisdom.
     * 
     */
    public int getWisdom() {
        return wisdom;
    }

    /**
     * Define o valor da propriedade wisdom.
     * 
     */
    public void setWisdom(int value) {
        this.wisdom = value;
    }

    /**
     * Obtém o valor da propriedade intelligence.
     * 
     */
    public int getIntelligence() {
        return intelligence;
    }

    /**
     * Define o valor da propriedade intelligence.
     * 
     */
    public void setIntelligence(int value) {
        this.intelligence = value;
    }

}
