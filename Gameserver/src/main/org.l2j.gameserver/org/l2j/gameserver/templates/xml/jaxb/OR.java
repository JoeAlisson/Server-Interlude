//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.3.1-b171012.0423 
// Consulte <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2018.11.06 às 03:44:38 PM BRT 
//


package org.l2j.gameserver.templates.xml.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de OR complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="OR"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://la2j.org}Operator"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://la2j.org}operator" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{http://la2j.org}condition" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OR", propOrder = {
    "operator",
    "condition"
})
public class OR
    extends Operator
{

    @XmlElementRef(name = "operator", namespace = "http://la2j.org", type = JAXBElement.class, required = false)
    protected List<JAXBElement<? extends Operator>> operator;
    @XmlElementRef(name = "condition", namespace = "http://la2j.org", type = JAXBElement.class, required = false)
    protected List<JAXBElement<? extends Condition>> condition;

    /**
     * Gets the value of the operator property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the operator property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOperator().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link NOT }{@code >}
     * {@link JAXBElement }{@code <}{@link AND }{@code >}
     * {@link JAXBElement }{@code <}{@link OR }{@code >}
     * {@link JAXBElement }{@code <}{@link Operator }{@code >}
     * 
     * 
     */
    public List<JAXBElement<? extends Operator>> getOperator() {
        if (operator == null) {
            operator = new ArrayList<JAXBElement<? extends Operator>>();
        }
        return this.operator;
    }

    /**
     * Gets the value of the condition property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the condition property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCondition().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link OwnerCondition }{@code >}
     * {@link JAXBElement }{@code <}{@link XmlStatGameCondition }{@code >}
     * {@link JAXBElement }{@code <}{@link LevelCondition }{@code >}
     * {@link JAXBElement }{@code <}{@link XmlStatUsingCondition }{@code >}
     * {@link JAXBElement }{@code <}{@link XmlStatPlayerCondition }{@code >}
     * {@link JAXBElement }{@code <}{@link StateCondition }{@code >}
     * {@link JAXBElement }{@code <}{@link Condition }{@code >}
     * 
     * 
     */
    public List<JAXBElement<? extends Condition>> getCondition() {
        if (condition == null) {
            condition = new ArrayList<JAXBElement<? extends Condition>>();
        }
        return this.condition;
    }

}
