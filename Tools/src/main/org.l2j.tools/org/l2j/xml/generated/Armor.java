//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.3.1-b171012.0423 
// Consulte <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2018.11.01 às 01:28:10 PM BRT 
//


package org.l2j.xml.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de Armor complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="Armor"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://la2j.org}ItemTemplate"&gt;
 *       &lt;choice&gt;
 *         &lt;element name="bodyPart" type="{http://la2j.org}BodyPart"/&gt;
 *       &lt;/choice&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Armor", propOrder = {
    "bodyPart"
})
public class Armor
    extends ItemTemplate
{

    @XmlSchemaType(name = "string")
    protected BodyPart bodyPart;

    /**
     * Obtém o valor da propriedade bodyPart.
     * 
     * @return
     *     possible object is
     *     {@link BodyPart }
     *     
     */
    public BodyPart getBodyPart() {
        return bodyPart;
    }

    /**
     * Define o valor da propriedade bodyPart.
     * 
     * @param value
     *     allowed object is
     *     {@link BodyPart }
     *     
     */
    public void setBodyPart(BodyPart value) {
        this.bodyPart = value;
    }

}
