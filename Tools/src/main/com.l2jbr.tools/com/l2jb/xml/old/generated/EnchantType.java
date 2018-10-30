//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.3.1-b171012.0423 
// Consulte <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2018.10.29 às 07:49:11 PM BRT 
//


package com.l2jb.xml.old.generated;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de enchantType complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="enchantType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="order" type="{http://www.w3.org/2001/XMLSchema}byte" /&gt;
 *       &lt;attribute name="stat" use="required" type="{}statType" /&gt;
 *       &lt;attribute name="val" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" fixed="0" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "enchantType")
public class EnchantType {

    @XmlAttribute(name = "order")
    protected Byte order;
    @XmlAttribute(name = "stat", required = true)
    protected StatType stat;
    @XmlAttribute(name = "val", required = true)
    protected BigInteger val;

    /**
     * Obtém o valor da propriedade order.
     * 
     * @return
     *     possible object is
     *     {@link Byte }
     *     
     */
    public Byte getOrder() {
        return order;
    }

    /**
     * Define o valor da propriedade order.
     * 
     * @param value
     *     allowed object is
     *     {@link Byte }
     *     
     */
    public void setOrder(Byte value) {
        this.order = value;
    }

    /**
     * Obtém o valor da propriedade stat.
     * 
     * @return
     *     possible object is
     *     {@link StatType }
     *     
     */
    public StatType getStat() {
        return stat;
    }

    /**
     * Define o valor da propriedade stat.
     * 
     * @param value
     *     allowed object is
     *     {@link StatType }
     *     
     */
    public void setStat(StatType value) {
        this.stat = value;
    }

    /**
     * Obtém o valor da propriedade val.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getVal() {
        if (val == null) {
            return new BigInteger("0");
        } else {
            return val;
        }
    }

    /**
     * Define o valor da propriedade val.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setVal(BigInteger value) {
        this.val = value;
    }

}
