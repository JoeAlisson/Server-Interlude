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
 * <p>Classe Java de Damage complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="Damage"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="vertical" type="{http://www.w3.org/2001/XMLSchema}int" default="0" /&gt;
 *       &lt;attribute name="horizontal" type="{http://www.w3.org/2001/XMLSchema}int" default="0" /&gt;
 *       &lt;attribute name="distance" type="{http://www.w3.org/2001/XMLSchema}int" default="0" /&gt;
 *       &lt;attribute name="width" type="{http://www.w3.org/2001/XMLSchema}int" default="0" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Damage")
public class Damage {

    @XmlAttribute(name = "vertical")
    protected Integer vertical;
    @XmlAttribute(name = "horizontal")
    protected Integer horizontal;
    @XmlAttribute(name = "distance")
    protected Integer distance;
    @XmlAttribute(name = "width")
    protected Integer width;

    /**
     * Obtém o valor da propriedade vertical.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getVertical() {
        if (vertical == null) {
            return  0;
        } else {
            return vertical;
        }
    }

    /**
     * Define o valor da propriedade vertical.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setVertical(Integer value) {
        this.vertical = value;
    }

    /**
     * Obtém o valor da propriedade horizontal.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getHorizontal() {
        if (horizontal == null) {
            return  0;
        } else {
            return horizontal;
        }
    }

    /**
     * Define o valor da propriedade horizontal.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setHorizontal(Integer value) {
        this.horizontal = value;
    }

    /**
     * Obtém o valor da propriedade distance.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getDistance() {
        if (distance == null) {
            return  0;
        } else {
            return distance;
        }
    }

    /**
     * Define o valor da propriedade distance.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDistance(Integer value) {
        this.distance = value;
    }

    /**
     * Obtém o valor da propriedade width.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getWidth() {
        if (width == null) {
            return  0;
        } else {
            return width;
        }
    }

    /**
     * Define o valor da propriedade width.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setWidth(Integer value) {
        this.width = value;
    }

}
