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
 * <p>Classe Java de LevelCondition complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="LevelCondition"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://la2j.org}Condition"&gt;
 *       &lt;attribute name="min" type="{http://www.w3.org/2001/XMLSchema}int" default="0" /&gt;
 *       &lt;attribute name="max" type="{http://www.w3.org/2001/XMLSchema}int" default="100" /&gt;
 *       &lt;attribute name="target" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LevelCondition")
public class LevelCondition
    extends Condition
{

    @XmlAttribute(name = "min")
    protected Integer min;
    @XmlAttribute(name = "max")
    protected Integer max;
    @XmlAttribute(name = "target")
    protected Boolean target;

    /**
     * Obtém o valor da propriedade min.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getMin() {
        if (min == null) {
            return  0;
        } else {
            return min;
        }
    }

    /**
     * Define o valor da propriedade min.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMin(Integer value) {
        this.min = value;
    }

    /**
     * Obtém o valor da propriedade max.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getMax() {
        if (max == null) {
            return  100;
        } else {
            return max;
        }
    }

    /**
     * Define o valor da propriedade max.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMax(Integer value) {
        this.max = value;
    }

    /**
     * Obtém o valor da propriedade target.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isTarget() {
        if (target == null) {
            return false;
        } else {
            return target;
        }
    }

    /**
     * Define o valor da propriedade target.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTarget(Boolean value) {
        this.target = value;
    }

}
