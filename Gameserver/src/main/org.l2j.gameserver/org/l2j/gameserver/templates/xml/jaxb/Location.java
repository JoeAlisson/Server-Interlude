//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.3.1-b171012.0423 
// Consulte <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2018.10.20 às 10:28:55 PM BRT 
//


package org.l2j.gameserver.templates.xml.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de Location complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="Location"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="x" type="{http://www.w3.org/2001/XMLSchema}int" default="0" /&gt;
 *       &lt;attribute name="y" type="{http://www.w3.org/2001/XMLSchema}int" default="0" /&gt;
 *       &lt;attribute name="z" type="{http://www.w3.org/2001/XMLSchema}int" default="0" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Location")
public class Location {

    @XmlAttribute(name = "x")
    protected Integer x;
    @XmlAttribute(name = "y")
    protected Integer y;
    @XmlAttribute(name = "z")
    protected Integer z;

    /**
     * Obtém o valor da propriedade x.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getX() {
        if (x == null) {
            return  0;
        } else {
            return x;
        }
    }

    /**
     * Define o valor da propriedade x.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setX(Integer value) {
        this.x = value;
    }

    /**
     * Obtém o valor da propriedade y.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getY() {
        if (y == null) {
            return  0;
        } else {
            return y;
        }
    }

    /**
     * Define o valor da propriedade y.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setY(Integer value) {
        this.y = value;
    }

    /**
     * Obtém o valor da propriedade z.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getZ() {
        if (z == null) {
            return  0;
        } else {
            return z;
        }
    }

    /**
     * Define o valor da propriedade z.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setZ(Integer value) {
        this.z = value;
    }

}
