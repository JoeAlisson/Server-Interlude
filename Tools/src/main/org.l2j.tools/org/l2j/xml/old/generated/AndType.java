//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.3.1-b171012.0423 
// Consulte <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2018.10.29 às 07:49:11 PM BRT 
//


package org.l2j.xml.old.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de andType complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="andType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice maxOccurs="unbounded" minOccurs="0"&gt;
 *         &lt;element name="using" type="{}usingType"/&gt;
 *         &lt;element name="reader" type="{}playerType"/&gt;
 *         &lt;element name="target" type="{}targetType"/&gt;
 *         &lt;element name="not" type="{}notType"/&gt;
 *         &lt;element name="game" type="{}gameType"/&gt;
 *         &lt;element name="or" type="{}andType" minOccurs="0"/&gt;
 *       &lt;/choice&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "andType", propOrder = {
    "usingOrPlayerOrTarget"
})
public class AndType {

    @XmlElements({
        @XmlElement(name = "using", type = UsingType.class),
        @XmlElement(name = "reader", type = PlayerType.class),
        @XmlElement(name = "target", type = TargetType.class),
        @XmlElement(name = "not", type = NotType.class),
        @XmlElement(name = "game", type = GameType.class),
        @XmlElement(name = "or", type = AndType.class)
    })
    protected List<Object> usingOrPlayerOrTarget;

    /**
     * Gets the value of the usingOrPlayerOrTarget property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the usingOrPlayerOrTarget property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUsingOrPlayerOrTarget().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UsingType }
     * {@link PlayerType }
     * {@link TargetType }
     * {@link NotType }
     * {@link GameType }
     * {@link AndType }
     * 
     * 
     */
    public List<Object> getUsingOrPlayerOrTarget() {
        if (usingOrPlayerOrTarget == null) {
            usingOrPlayerOrTarget = new ArrayList<Object>();
        }
        return this.usingOrPlayerOrTarget;
    }

}
