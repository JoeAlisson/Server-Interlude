//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.3.1-b171012.0423 
// Consulte <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2018.10.29 às 07:49:11 PM BRT 
//


package org.l2j.xml.old.generated;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de capsuledItemType complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="capsuledItemType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" /&gt;
 *       &lt;attribute name="min" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" /&gt;
 *       &lt;attribute name="max" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" /&gt;
 *       &lt;attribute name="chance" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="minEnchant" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" /&gt;
 *       &lt;attribute name="maxEnchant" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "capsuledItemType")
public class CapsuledItemType {

    @XmlAttribute(name = "id")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger id;
    @XmlAttribute(name = "min")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger min;
    @XmlAttribute(name = "max")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger max;
    @XmlAttribute(name = "chance")
    protected Double chance;
    @XmlAttribute(name = "minEnchant")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger minEnchant;
    @XmlAttribute(name = "maxEnchant")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger maxEnchant;

    /**
     * Obtém o valor da propriedade id.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getId() {
        return id;
    }

    /**
     * Define o valor da propriedade id.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setId(BigInteger value) {
        this.id = value;
    }

    /**
     * Obtém o valor da propriedade min.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMin() {
        return min;
    }

    /**
     * Define o valor da propriedade min.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMin(BigInteger value) {
        this.min = value;
    }

    /**
     * Obtém o valor da propriedade max.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMax() {
        return max;
    }

    /**
     * Define o valor da propriedade max.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMax(BigInteger value) {
        this.max = value;
    }

    /**
     * Obtém o valor da propriedade chance.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getChance() {
        return chance;
    }

    /**
     * Define o valor da propriedade chance.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setChance(Double value) {
        this.chance = value;
    }

    /**
     * Obtém o valor da propriedade minEnchant.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMinEnchant() {
        return minEnchant;
    }

    /**
     * Define o valor da propriedade minEnchant.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMinEnchant(BigInteger value) {
        this.minEnchant = value;
    }

    /**
     * Obtém o valor da propriedade maxEnchant.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMaxEnchant() {
        return maxEnchant;
    }

    /**
     * Define o valor da propriedade maxEnchant.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMaxEnchant(BigInteger value) {
        this.maxEnchant = value;
    }

}
