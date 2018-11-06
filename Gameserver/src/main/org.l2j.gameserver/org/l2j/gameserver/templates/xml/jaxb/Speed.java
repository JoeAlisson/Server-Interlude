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
 * <p>Classe Java de Speed complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="Speed"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="walk" type="{http://www.w3.org/2001/XMLSchema}short" default="1" /&gt;
 *       &lt;attribute name="run" type="{http://www.w3.org/2001/XMLSchema}short" default="1" /&gt;
 *       &lt;attribute name="swim" type="{http://www.w3.org/2001/XMLSchema}int" default="1" /&gt;
 *       &lt;attribute name="fastSwim" type="{http://www.w3.org/2001/XMLSchema}int" default="1" /&gt;
 *       &lt;attribute name="fly" type="{http://www.w3.org/2001/XMLSchema}int" default="0" /&gt;
 *       &lt;attribute name="fastFly" type="{http://www.w3.org/2001/XMLSchema}int" default="0" /&gt;
 *       &lt;attribute name="ride" type="{http://www.w3.org/2001/XMLSchema}int" default="0" /&gt;
 *       &lt;attribute name="fastRide" type="{http://www.w3.org/2001/XMLSchema}int" default="0" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Speed")
public class Speed {

    @XmlAttribute(name = "walk")
    protected Short walk;
    @XmlAttribute(name = "run")
    protected Short run;
    @XmlAttribute(name = "swim")
    protected Integer swim;
    @XmlAttribute(name = "fastSwim")
    protected Integer fastSwim;
    @XmlAttribute(name = "fly")
    protected Integer fly;
    @XmlAttribute(name = "fastFly")
    protected Integer fastFly;
    @XmlAttribute(name = "ride")
    protected Integer ride;
    @XmlAttribute(name = "fastRide")
    protected Integer fastRide;

    /**
     * Obtém o valor da propriedade walk.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public short getWalk() {
        if (walk == null) {
            return ((short) 1);
        } else {
            return walk;
        }
    }

    /**
     * Define o valor da propriedade walk.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setWalk(Short value) {
        this.walk = value;
    }

    /**
     * Obtém o valor da propriedade run.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public short getRun() {
        if (run == null) {
            return ((short) 1);
        } else {
            return run;
        }
    }

    /**
     * Define o valor da propriedade run.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setRun(Short value) {
        this.run = value;
    }

    /**
     * Obtém o valor da propriedade swim.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getSwim() {
        if (swim == null) {
            return  1;
        } else {
            return swim;
        }
    }

    /**
     * Define o valor da propriedade swim.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSwim(Integer value) {
        this.swim = value;
    }

    /**
     * Obtém o valor da propriedade fastSwim.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getFastSwim() {
        if (fastSwim == null) {
            return  1;
        } else {
            return fastSwim;
        }
    }

    /**
     * Define o valor da propriedade fastSwim.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setFastSwim(Integer value) {
        this.fastSwim = value;
    }

    /**
     * Obtém o valor da propriedade fly.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getFly() {
        if (fly == null) {
            return  0;
        } else {
            return fly;
        }
    }

    /**
     * Define o valor da propriedade fly.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setFly(Integer value) {
        this.fly = value;
    }

    /**
     * Obtém o valor da propriedade fastFly.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getFastFly() {
        if (fastFly == null) {
            return  0;
        } else {
            return fastFly;
        }
    }

    /**
     * Define o valor da propriedade fastFly.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setFastFly(Integer value) {
        this.fastFly = value;
    }

    /**
     * Obtém o valor da propriedade ride.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getRide() {
        if (ride == null) {
            return  0;
        } else {
            return ride;
        }
    }

    /**
     * Define o valor da propriedade ride.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRide(Integer value) {
        this.ride = value;
    }

    /**
     * Obtém o valor da propriedade fastRide.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getFastRide() {
        if (fastRide == null) {
            return  0;
        } else {
            return fastRide;
        }
    }

    /**
     * Define o valor da propriedade fastRide.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setFastRide(Integer value) {
        this.fastRide = value;
    }

}
