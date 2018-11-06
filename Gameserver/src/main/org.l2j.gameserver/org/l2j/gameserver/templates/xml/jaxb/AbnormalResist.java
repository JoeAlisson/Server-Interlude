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
 * <p>Classe Java de AbnormalResist complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="AbnormalResist"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="magic" type="{http://www.w3.org/2001/XMLSchema}int" default="1" /&gt;
 *       &lt;attribute name="physic" type="{http://www.w3.org/2001/XMLSchema}float" default="1" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbnormalResist")
public class AbnormalResist {

    @XmlAttribute(name = "magic")
    protected Integer magic;
    @XmlAttribute(name = "physic")
    protected Float physic;

    /**
     * Obtém o valor da propriedade magic.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getMagic() {
        if (magic == null) {
            return  1;
        } else {
            return magic;
        }
    }

    /**
     * Define o valor da propriedade magic.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMagic(Integer value) {
        this.magic = value;
    }

    /**
     * Obtém o valor da propriedade physic.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public float getPhysic() {
        if (physic == null) {
            return  1.0F;
        } else {
            return physic;
        }
    }

    /**
     * Define o valor da propriedade physic.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setPhysic(Float value) {
        this.physic = value;
    }

}
