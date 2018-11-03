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
 * <p>Classe Java de Collision complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="Collision"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="maleRadius" use="required" type="{http://www.w3.org/2001/XMLSchema}float" /&gt;
 *       &lt;attribute name="maleHeight" use="required" type="{http://www.w3.org/2001/XMLSchema}float" /&gt;
 *       &lt;attribute name="femaleRadius" use="required" type="{http://www.w3.org/2001/XMLSchema}float" /&gt;
 *       &lt;attribute name="femaleHeight" use="required" type="{http://www.w3.org/2001/XMLSchema}float" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Collision")
public class Collision {

    @XmlAttribute(name = "maleRadius", required = true)
    protected float maleRadius;
    @XmlAttribute(name = "maleHeight", required = true)
    protected float maleHeight;
    @XmlAttribute(name = "femaleRadius", required = true)
    protected float femaleRadius;
    @XmlAttribute(name = "femaleHeight", required = true)
    protected float femaleHeight;

    /**
     * Obtém o valor da propriedade maleRadius.
     * 
     */
    public float getMaleRadius() {
        return maleRadius;
    }

    /**
     * Define o valor da propriedade maleRadius.
     * 
     */
    public void setMaleRadius(float value) {
        this.maleRadius = value;
    }

    /**
     * Obtém o valor da propriedade maleHeight.
     * 
     */
    public float getMaleHeight() {
        return maleHeight;
    }

    /**
     * Define o valor da propriedade maleHeight.
     * 
     */
    public void setMaleHeight(float value) {
        this.maleHeight = value;
    }

    /**
     * Obtém o valor da propriedade femaleRadius.
     * 
     */
    public float getFemaleRadius() {
        return femaleRadius;
    }

    /**
     * Define o valor da propriedade femaleRadius.
     * 
     */
    public void setFemaleRadius(float value) {
        this.femaleRadius = value;
    }

    /**
     * Obtém o valor da propriedade femaleHeight.
     * 
     */
    public float getFemaleHeight() {
        return femaleHeight;
    }

    /**
     * Define o valor da propriedade femaleHeight.
     * 
     */
    public void setFemaleHeight(float value) {
        this.femaleHeight = value;
    }

}
