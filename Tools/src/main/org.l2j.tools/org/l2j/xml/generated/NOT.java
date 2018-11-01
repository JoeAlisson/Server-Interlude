//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.3.1-b171012.0423 
// Consulte <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2018.11.01 às 01:28:10 PM BRT 
//


package org.l2j.xml.generated;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de NOT complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="NOT"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://la2j.org}Operator"&gt;
 *       &lt;choice&gt;
 *         &lt;element ref="{http://la2j.org}operator"/&gt;
 *         &lt;element ref="{http://la2j.org}condition"/&gt;
 *       &lt;/choice&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NOT", propOrder = {
    "operator",
    "condition"
})
public class NOT
    extends Operator
{

    @XmlElementRef(name = "operator", namespace = "http://la2j.org", type = JAXBElement.class, required = false)
    protected JAXBElement<? extends Operator> operator;
    @XmlElementRef(name = "condition", namespace = "http://la2j.org", type = JAXBElement.class, required = false)
    protected JAXBElement<? extends Condition> condition;

    /**
     * Obtém o valor da propriedade operator.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link NOT }{@code >}
     *     {@link JAXBElement }{@code <}{@link OR }{@code >}
     *     {@link JAXBElement }{@code <}{@link AND }{@code >}
     *     {@link JAXBElement }{@code <}{@link Operator }{@code >}
     *     
     */
    public JAXBElement<? extends Operator> getOperator() {
        return operator;
    }

    /**
     * Define o valor da propriedade operator.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link NOT }{@code >}
     *     {@link JAXBElement }{@code <}{@link OR }{@code >}
     *     {@link JAXBElement }{@code <}{@link AND }{@code >}
     *     {@link JAXBElement }{@code <}{@link Operator }{@code >}
     *     
     */
    public void setOperator(JAXBElement<? extends Operator> value) {
        this.operator = value;
    }

    /**
     * Obtém o valor da propriedade condition.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XmlStatPlayerCondition }{@code >}
     *     {@link JAXBElement }{@code <}{@link LevelCondition }{@code >}
     *     {@link JAXBElement }{@code <}{@link XmlStatGameCondition }{@code >}
     *     {@link JAXBElement }{@code <}{@link OwnerCondition }{@code >}
     *     {@link JAXBElement }{@code <}{@link XmlStatUsingCondition }{@code >}
     *     {@link JAXBElement }{@code <}{@link StateCondition }{@code >}
     *     {@link JAXBElement }{@code <}{@link Condition }{@code >}
     *     
     */
    public JAXBElement<? extends Condition> getCondition() {
        return condition;
    }

    /**
     * Define o valor da propriedade condition.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XmlStatPlayerCondition }{@code >}
     *     {@link JAXBElement }{@code <}{@link LevelCondition }{@code >}
     *     {@link JAXBElement }{@code <}{@link XmlStatGameCondition }{@code >}
     *     {@link JAXBElement }{@code <}{@link OwnerCondition }{@code >}
     *     {@link JAXBElement }{@code <}{@link XmlStatUsingCondition }{@code >}
     *     {@link JAXBElement }{@code <}{@link StateCondition }{@code >}
     *     {@link JAXBElement }{@code <}{@link Condition }{@code >}
     *     
     */
    public void setCondition(JAXBElement<? extends Condition> value) {
        this.condition = value;
    }

}
