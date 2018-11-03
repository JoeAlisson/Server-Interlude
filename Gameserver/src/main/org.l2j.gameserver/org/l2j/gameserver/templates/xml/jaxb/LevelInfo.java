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
 * <p>Classe Java de LevelInfo complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="LevelInfo"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="hp" use="required" type="{http://www.w3.org/2001/XMLSchema}float" /&gt;
 *       &lt;attribute name="mp" use="required" type="{http://www.w3.org/2001/XMLSchema}float" /&gt;
 *       &lt;attribute name="cp" use="required" type="{http://www.w3.org/2001/XMLSchema}float" /&gt;
 *       &lt;attribute name="hpRegen" use="required" type="{http://www.w3.org/2001/XMLSchema}float" /&gt;
 *       &lt;attribute name="mpRegen" use="required" type="{http://www.w3.org/2001/XMLSchema}float" /&gt;
 *       &lt;attribute name="cpRegen" use="required" type="{http://www.w3.org/2001/XMLSchema}float" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LevelInfo")
public class LevelInfo {

    @XmlAttribute(name = "value", required = true)
    protected int value;
    @XmlAttribute(name = "hp", required = true)
    protected float hp;
    @XmlAttribute(name = "mp", required = true)
    protected float mp;
    @XmlAttribute(name = "cp", required = true)
    protected float cp;
    @XmlAttribute(name = "hpRegen", required = true)
    protected float hpRegen;
    @XmlAttribute(name = "mpRegen", required = true)
    protected float mpRegen;
    @XmlAttribute(name = "cpRegen", required = true)
    protected float cpRegen;

    /**
     * Obtém o valor da propriedade value.
     * 
     */
    public int getValue() {
        return value;
    }

    /**
     * Define o valor da propriedade value.
     * 
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * Obtém o valor da propriedade hp.
     * 
     */
    public float getHp() {
        return hp;
    }

    /**
     * Define o valor da propriedade hp.
     * 
     */
    public void setHp(float value) {
        this.hp = value;
    }

    /**
     * Obtém o valor da propriedade mp.
     * 
     */
    public float getMp() {
        return mp;
    }

    /**
     * Define o valor da propriedade mp.
     * 
     */
    public void setMp(float value) {
        this.mp = value;
    }

    /**
     * Obtém o valor da propriedade cp.
     * 
     */
    public float getCp() {
        return cp;
    }

    /**
     * Define o valor da propriedade cp.
     * 
     */
    public void setCp(float value) {
        this.cp = value;
    }

    /**
     * Obtém o valor da propriedade hpRegen.
     * 
     */
    public float getHpRegen() {
        return hpRegen;
    }

    /**
     * Define o valor da propriedade hpRegen.
     * 
     */
    public void setHpRegen(float value) {
        this.hpRegen = value;
    }

    /**
     * Obtém o valor da propriedade mpRegen.
     * 
     */
    public float getMpRegen() {
        return mpRegen;
    }

    /**
     * Define o valor da propriedade mpRegen.
     * 
     */
    public void setMpRegen(float value) {
        this.mpRegen = value;
    }

    /**
     * Obtém o valor da propriedade cpRegen.
     * 
     */
    public float getCpRegen() {
        return cpRegen;
    }

    /**
     * Define o valor da propriedade cpRegen.
     * 
     */
    public void setCpRegen(float value) {
        this.cpRegen = value;
    }

}
