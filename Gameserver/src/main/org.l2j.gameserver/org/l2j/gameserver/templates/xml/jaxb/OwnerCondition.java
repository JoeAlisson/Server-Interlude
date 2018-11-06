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
 * <p>Classe Java de OwnerCondition complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="OwnerCondition"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://la2j.org}Condition"&gt;
 *       &lt;attribute name="type" use="required" type="{http://la2j.org}OwnerConditionType" /&gt;
 *       &lt;attribute name="ownedId" type="{http://www.w3.org/2001/XMLSchema}int" default="-1" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OwnerCondition")
public class OwnerCondition
    extends Condition
{

    @XmlAttribute(name = "type", required = true)
    protected OwnerConditionType type;
    @XmlAttribute(name = "ownedId")
    protected Integer ownedId;

    /**
     * Obtém o valor da propriedade type.
     * 
     * @return
     *     possible object is
     *     {@link OwnerConditionType }
     *     
     */
    public OwnerConditionType getType() {
        return type;
    }

    /**
     * Define o valor da propriedade type.
     * 
     * @param value
     *     allowed object is
     *     {@link OwnerConditionType }
     *     
     */
    public void setType(OwnerConditionType value) {
        this.type = value;
    }

    /**
     * Obtém o valor da propriedade ownedId.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getOwnedId() {
        if (ownedId == null) {
            return -1;
        } else {
            return ownedId;
        }
    }

    /**
     * Define o valor da propriedade ownedId.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setOwnedId(Integer value) {
        this.ownedId = value;
    }

}
