//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.3.1-b171012.0423 
// Consulte <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2018.10.29 às 07:49:11 PM BRT 
//


package com.l2jb.xml.old.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Classe Java de targetType complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="targetType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="levelRange" type="{http://www.w3.org/2001/XMLSchema}token" /&gt;
 *       &lt;attribute name="categoryType" type="{http://www.w3.org/2001/XMLSchema}normalizedString" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "targetType")
public class TargetType {

    @XmlAttribute(name = "levelRange")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String levelRange;
    @XmlAttribute(name = "categoryType")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String categoryType;

    /**
     * Obtém o valor da propriedade levelRange.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLevelRange() {
        return levelRange;
    }

    /**
     * Define o valor da propriedade levelRange.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLevelRange(String value) {
        this.levelRange = value;
    }

    /**
     * Obtém o valor da propriedade categoryType.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCategoryType() {
        return categoryType;
    }

    /**
     * Define o valor da propriedade categoryType.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCategoryType(String value) {
        this.categoryType = value;
    }

}
