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
 * <p>Classe Java de ItemConsume complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="ItemConsume"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="mp" type="{http://www.w3.org/2001/XMLSchema}int" default="0" /&gt;
 *       &lt;attribute name="shot" type="{http://www.w3.org/2001/XMLSchema}int" default="0" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ItemConsume")
public class ItemConsume {

    @XmlAttribute(name = "mp")
    protected Integer mp;
    @XmlAttribute(name = "shot")
    protected Integer shot;

    /**
     * Obtém o valor da propriedade mp.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getMp() {
        if (mp == null) {
            return  0;
        } else {
            return mp;
        }
    }

    /**
     * Define o valor da propriedade mp.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMp(Integer value) {
        this.mp = value;
    }

    /**
     * Obtém o valor da propriedade shot.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getShot() {
        if (shot == null) {
            return  0;
        } else {
            return shot;
        }
    }

    /**
     * Define o valor da propriedade shot.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setShot(Integer value) {
        this.shot = value;
    }

}
