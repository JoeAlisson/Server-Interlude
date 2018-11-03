//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.3.1-b171012.0423 
// Consulte <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2018.11.02 às 11:52:58 AM BRT 
//


package org.l2j.xml.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de ItemRestriction complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="ItemRestriction"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="dropable" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" /&gt;
 *       &lt;attribute name="tradeable" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" /&gt;
 *       &lt;attribute name="freightable" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" /&gt;
 *       &lt;attribute name="sellable" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" /&gt;
 *       &lt;attribute name="destroyable" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ItemRestriction")
public class ItemRestriction {

    @XmlAttribute(name = "dropable")
    protected Boolean dropable;
    @XmlAttribute(name = "tradeable")
    protected Boolean tradeable;
    @XmlAttribute(name = "freightable")
    protected Boolean freightable;
    @XmlAttribute(name = "sellable")
    protected Boolean sellable;
    @XmlAttribute(name = "destroyable")
    protected Boolean destroyable;

    /**
     * Obtém o valor da propriedade dropable.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isDropable() {
        if (dropable == null) {
            return true;
        } else {
            return dropable;
        }
    }

    /**
     * Define o valor da propriedade dropable.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDropable(Boolean value) {
        this.dropable = value;
    }

    /**
     * Obtém o valor da propriedade tradeable.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isTradeable() {
        if (tradeable == null) {
            return true;
        } else {
            return tradeable;
        }
    }

    /**
     * Define o valor da propriedade tradeable.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTradeable(Boolean value) {
        this.tradeable = value;
    }

    /**
     * Obtém o valor da propriedade freightable.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isFreightable() {
        if (freightable == null) {
            return true;
        } else {
            return freightable;
        }
    }

    /**
     * Define o valor da propriedade freightable.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setFreightable(Boolean value) {
        this.freightable = value;
    }

    /**
     * Obtém o valor da propriedade sellable.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isSellable() {
        if (sellable == null) {
            return true;
        } else {
            return sellable;
        }
    }

    /**
     * Define o valor da propriedade sellable.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSellable(Boolean value) {
        this.sellable = value;
    }

    /**
     * Obtém o valor da propriedade destroyable.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isDestroyable() {
        if (destroyable == null) {
            return true;
        } else {
            return destroyable;
        }
    }

    /**
     * Define o valor da propriedade destroyable.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDestroyable(Boolean value) {
        this.destroyable = value;
    }

}
