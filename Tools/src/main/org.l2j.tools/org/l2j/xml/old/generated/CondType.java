//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.3.1-b171012.0423 
// Consulte <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2018.10.29 às 07:49:11 PM BRT 
//


package org.l2j.xml.old.generated;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Classe Java de condType complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="condType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice maxOccurs="unbounded" minOccurs="0"&gt;
 *         &lt;element name="using" type="{}usingType"/&gt;
 *         &lt;element name="and" type="{}andType"/&gt;
 *         &lt;element name="not" type="{}notType"/&gt;
 *         &lt;element name="player" type="{}playerType"/&gt;
 *         &lt;element name="target" type="{}targetType"/&gt;
 *       &lt;/choice&gt;
 *       &lt;attribute name="msgId" type="{http://www.w3.org/2001/XMLSchema}short" /&gt;
 *       &lt;attribute name="addName" type="{http://www.w3.org/2001/XMLSchema}byte" /&gt;
 *       &lt;attribute name="msg" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "condType", propOrder = {
    "usingOrAndOrNot"
})
public class CondType {

    @XmlElements({
        @XmlElement(name = "using", type = UsingType.class),
        @XmlElement(name = "and", type = AndType.class),
        @XmlElement(name = "not", type = NotType.class),
        @XmlElement(name = "player", type = PlayerType.class),
        @XmlElement(name = "target", type = TargetType.class)
    })
    protected List<Object> usingOrAndOrNot;
    @XmlAttribute(name = "msgId")
    protected Short msgId;
    @XmlAttribute(name = "addName")
    protected Byte addName;
    @XmlAttribute(name = "msg")
    protected String msg;

    /**
     * Gets the value of the usingOrAndOrNot property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the usingOrAndOrNot property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUsingOrAndOrNot().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UsingType }
     * {@link AndType }
     * {@link NotType }
     * {@link PlayerType }
     * {@link TargetType }
     * 
     * 
     */
    public List<Object> getUsingOrAndOrNot() {
        if (usingOrAndOrNot == null) {
            usingOrAndOrNot = new ArrayList<Object>();
        }
        return this.usingOrAndOrNot;
    }

    /**
     * Obtém o valor da propriedade msgId.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getMsgId() {
        return msgId;
    }

    /**
     * Define o valor da propriedade msgId.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setMsgId(Short value) {
        this.msgId = value;
    }

    /**
     * Obtém o valor da propriedade addName.
     * 
     * @return
     *     possible object is
     *     {@link Byte }
     *     
     */
    public Byte getAddName() {
        return addName;
    }

    /**
     * Define o valor da propriedade addName.
     * 
     * @param value
     *     allowed object is
     *     {@link Byte }
     *     
     */
    public void setAddName(Byte value) {
        this.addName = value;
    }

    /**
     * Obtém o valor da propriedade msg.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMsg() {
        return msg;
    }

    /**
     * Define o valor da propriedade msg.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMsg(String value) {
        this.msg = value;
    }

}
