//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.3.1-b171012.0423 
// Consulte <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2018.10.29 às 07:49:11 PM BRT 
//


package com.l2jb.xml.old.generated;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Classe Java de itemType complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="itemType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="set" type="{}setType" maxOccurs="unbounded"/&gt;
 *         &lt;element name="unequip_skills" type="{}skillsType" minOccurs="0"/&gt;
 *         &lt;element name="capsuled_items" type="{}capsuledItemsType" minOccurs="0"/&gt;
 *         &lt;element name="createItems" type="{}createItemsType" minOccurs="0"/&gt;
 *         &lt;element name="cond" type="{}condType" maxOccurs="2" minOccurs="0"/&gt;
 *         &lt;element name="skills" type="{}skillsType" minOccurs="0"/&gt;
 *         &lt;element name="stats" type="{}forType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="id" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}positiveInteger"&gt;
 *             &lt;minInclusive value="1"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}normalizedString" /&gt;
 *       &lt;attribute name="additionalName" type="{http://www.w3.org/2001/XMLSchema}normalizedString" /&gt;
 *       &lt;attribute name="type" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *             &lt;enumeration value="Armor"/&gt;
 *             &lt;enumeration value="EtcItem"/&gt;
 *             &lt;enumeration value="Weapon"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "itemType", propOrder = {
    "set",
    "unequipSkills",
    "capsuledItems",
    "createItems",
    "cond",
    "skills",
    "stats"
})
public class ItemType {

    @XmlElement(required = true)
    protected List<SetType> set;
    @XmlElement(name = "unequip_skills")
    protected SkillsType unequipSkills;
    @XmlElement(name = "capsuled_items")
    protected CapsuledItemsType capsuledItems;
    protected CreateItemsType createItems;
    protected List<CondType> cond;
    protected SkillsType skills;
    protected ForType stats;
    @XmlAttribute(name = "id", required = true)
    protected BigInteger id;
    @XmlAttribute(name = "name", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String name;
    @XmlAttribute(name = "additionalName")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String additionalName;
    @XmlAttribute(name = "type", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String type;

    /**
     * Gets the value of the set property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the set property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSet().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SetType }
     * 
     * 
     */
    public List<SetType> getSet() {
        if (set == null) {
            set = new ArrayList<SetType>();
        }
        return this.set;
    }

    /**
     * Obtém o valor da propriedade unequipSkills.
     * 
     * @return
     *     possible object is
     *     {@link SkillsType }
     *     
     */
    public SkillsType getUnequipSkills() {
        return unequipSkills;
    }

    /**
     * Define o valor da propriedade unequipSkills.
     * 
     * @param value
     *     allowed object is
     *     {@link SkillsType }
     *     
     */
    public void setUnequipSkills(SkillsType value) {
        this.unequipSkills = value;
    }

    /**
     * Obtém o valor da propriedade capsuledItems.
     * 
     * @return
     *     possible object is
     *     {@link CapsuledItemsType }
     *     
     */
    public CapsuledItemsType getCapsuledItems() {
        return capsuledItems;
    }

    /**
     * Define o valor da propriedade capsuledItems.
     * 
     * @param value
     *     allowed object is
     *     {@link CapsuledItemsType }
     *     
     */
    public void setCapsuledItems(CapsuledItemsType value) {
        this.capsuledItems = value;
    }

    /**
     * Obtém o valor da propriedade createItems.
     * 
     * @return
     *     possible object is
     *     {@link CreateItemsType }
     *     
     */
    public CreateItemsType getCreateItems() {
        return createItems;
    }

    /**
     * Define o valor da propriedade createItems.
     * 
     * @param value
     *     allowed object is
     *     {@link CreateItemsType }
     *     
     */
    public void setCreateItems(CreateItemsType value) {
        this.createItems = value;
    }

    /**
     * Gets the value of the cond property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cond property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCond().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CondType }
     * 
     * 
     */
    public List<CondType> getCond() {
        if (cond == null) {
            cond = new ArrayList<CondType>();
        }
        return this.cond;
    }

    /**
     * Obtém o valor da propriedade skills.
     * 
     * @return
     *     possible object is
     *     {@link SkillsType }
     *     
     */
    public SkillsType getSkills() {
        return skills;
    }

    /**
     * Define o valor da propriedade skills.
     * 
     * @param value
     *     allowed object is
     *     {@link SkillsType }
     *     
     */
    public void setSkills(SkillsType value) {
        this.skills = value;
    }

    /**
     * Obtém o valor da propriedade stats.
     * 
     * @return
     *     possible object is
     *     {@link ForType }
     *     
     */
    public ForType getStats() {
        return stats;
    }

    /**
     * Define o valor da propriedade stats.
     * 
     * @param value
     *     allowed object is
     *     {@link ForType }
     *     
     */
    public void setStats(ForType value) {
        this.stats = value;
    }

    /**
     * Obtém o valor da propriedade id.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getId() {
        return id;
    }

    /**
     * Define o valor da propriedade id.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setId(BigInteger value) {
        this.id = value;
    }

    /**
     * Obtém o valor da propriedade name.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Define o valor da propriedade name.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Obtém o valor da propriedade additionalName.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditionalName() {
        return additionalName;
    }

    /**
     * Define o valor da propriedade additionalName.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditionalName(String value) {
        this.additionalName = value;
    }

    /**
     * Obtém o valor da propriedade type.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Define o valor da propriedade type.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

}
