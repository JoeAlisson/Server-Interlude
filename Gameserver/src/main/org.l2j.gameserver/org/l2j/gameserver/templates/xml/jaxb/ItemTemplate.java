//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.3.1-b171012.0423 
// Consulte <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2018.11.03 às 12:02:18 PM BRT 
//


package org.l2j.gameserver.templates.xml.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de ItemTemplate complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="ItemTemplate"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="bodyPart" type="{http://la2j.org}BodyPart" minOccurs="0"/&gt;
 *         &lt;element name="price" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="restriction" type="{http://la2j.org}ItemRestriction" minOccurs="0"/&gt;
 *         &lt;element name="crystalInfo" type="{http://la2j.org}CrystalInfo" minOccurs="0"/&gt;
 *         &lt;element name="handler" type="{http://la2j.org}ItemHandler" minOccurs="0"/&gt;
 *         &lt;element name="questItem" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="reuseDelay" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="time" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="weight" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="condition" type="{http://la2j.org}UseCondition" minOccurs="0"/&gt;
 *         &lt;element name="stat" type="{http://la2j.org}Stat" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="skill" type="{http://la2j.org}ItemSkill" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="additionalName" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="type" type="{http://la2j.org}ItemType" default="NONE" /&gt;
 *       &lt;attribute name="commissionType" type="{http://la2j.org}CommissionType" default="OTHER_ITEM" /&gt;
 *       &lt;attribute name="icon" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ItemTemplate", propOrder = {
    "bodyPart",
    "price",
    "restriction",
    "crystalInfo",
    "handler",
    "questItem",
    "reuseDelay",
    "time",
    "weight",
    "condition",
    "stat",
    "skill"
})
@XmlSeeAlso({
    Weapon.class,
    Armor.class,
    Item.class
})
public abstract class ItemTemplate {

    @XmlElement(defaultValue = "NONE")
    @XmlSchemaType(name = "string")
    protected BodyPart bodyPart;
    @XmlElement(defaultValue = "0")
    protected long price;
    protected ItemRestriction restriction;
    protected CrystalInfo crystalInfo;
    @XmlSchemaType(name = "string")
    protected ItemHandler handler;
    @XmlElement(defaultValue = "false")
    protected boolean questItem;
    @XmlElement(defaultValue = "1000")
    protected long reuseDelay;
    @XmlElement(defaultValue = "-1")
    protected long time;
    @XmlElement(defaultValue = "1")
    protected int weight;
    protected UseCondition condition;
    protected List<Stat> stat;
    protected List<ItemSkill> skill;
    @XmlAttribute(name = "id", required = true)
    protected int id;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "additionalName")
    protected String additionalName;
    @XmlAttribute(name = "type")
    protected ItemType type;
    @XmlAttribute(name = "commissionType")
    protected CommissionType commissionType;
    @XmlAttribute(name = "icon")
    protected String icon;

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

    /**
     * Obtém o valor da propriedade price.
     * 
     */
    public long getPrice() {
        return price;
    }

    /**
     * Define o valor da propriedade price.
     * 
     */
    public void setPrice(long value) {
        this.price = value;
    }

    /**
     * Obtém o valor da propriedade restriction.
     * 
     * @return
     *     possible object is
     *     {@link ItemRestriction }
     *     
     */
    public ItemRestriction getRestriction() {
        return restriction;
    }

    /**
     * Define o valor da propriedade restriction.
     * 
     * @param value
     *     allowed object is
     *     {@link ItemRestriction }
     *     
     */
    public void setRestriction(ItemRestriction value) {
        this.restriction = value;
    }

    /**
     * Obtém o valor da propriedade crystalInfo.
     * 
     * @return
     *     possible object is
     *     {@link CrystalInfo }
     *     
     */
    public CrystalInfo getCrystalInfo() {
        return crystalInfo;
    }

    /**
     * Define o valor da propriedade crystalInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link CrystalInfo }
     *     
     */
    public void setCrystalInfo(CrystalInfo value) {
        this.crystalInfo = value;
    }

    /**
     * Obtém o valor da propriedade handler.
     * 
     * @return
     *     possible object is
     *     {@link ItemHandler }
     *     
     */
    public ItemHandler getHandler() {
        return handler;
    }

    /**
     * Define o valor da propriedade handler.
     * 
     * @param value
     *     allowed object is
     *     {@link ItemHandler }
     *     
     */
    public void setHandler(ItemHandler value) {
        this.handler = value;
    }

    /**
     * Obtém o valor da propriedade questItem.
     * 
     */
    public boolean isQuestItem() {
        return questItem;
    }

    /**
     * Define o valor da propriedade questItem.
     * 
     */
    public void setQuestItem(boolean value) {
        this.questItem = value;
    }

    /**
     * Obtém o valor da propriedade reuseDelay.
     * 
     */
    public long getReuseDelay() {
        return reuseDelay;
    }

    /**
     * Define o valor da propriedade reuseDelay.
     * 
     */
    public void setReuseDelay(long value) {
        this.reuseDelay = value;
    }

    /**
     * Obtém o valor da propriedade time.
     * 
     */
    public long getTime() {
        return time;
    }

    /**
     * Define o valor da propriedade time.
     * 
     */
    public void setTime(long value) {
        this.time = value;
    }

    /**
     * Obtém o valor da propriedade weight.
     * 
     */
    public int getWeight() {
        return weight;
    }

    /**
     * Define o valor da propriedade weight.
     * 
     */
    public void setWeight(int value) {
        this.weight = value;
    }

    /**
     * Obtém o valor da propriedade condition.
     * 
     * @return
     *     possible object is
     *     {@link UseCondition }
     *     
     */
    public UseCondition getCondition() {
        return condition;
    }

    /**
     * Define o valor da propriedade condition.
     * 
     * @param value
     *     allowed object is
     *     {@link UseCondition }
     *     
     */
    public void setCondition(UseCondition value) {
        this.condition = value;
    }

    /**
     * Gets the value of the stat property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the stat property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStat().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Stat }
     * 
     * 
     */
    public List<Stat> getStat() {
        if (stat == null) {
            stat = new ArrayList<Stat>();
        }
        return this.stat;
    }

    /**
     * Gets the value of the skill property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the skill property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSkill().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ItemSkill }
     * 
     * 
     */
    public List<ItemSkill> getSkill() {
        if (skill == null) {
            skill = new ArrayList<ItemSkill>();
        }
        return this.skill;
    }

    /**
     * Obtém o valor da propriedade id.
     * 
     */
    public int getId() {
        return id;
    }

    /**
     * Define o valor da propriedade id.
     * 
     */
    public void setId(int value) {
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
     *     {@link ItemType }
     *     
     */
    public ItemType getType() {
        if (type == null) {
            return ItemType.NONE;
        } else {
            return type;
        }
    }

    /**
     * Define o valor da propriedade type.
     * 
     * @param value
     *     allowed object is
     *     {@link ItemType }
     *     
     */
    public void setType(ItemType value) {
        this.type = value;
    }

    /**
     * Obtém o valor da propriedade commissionType.
     * 
     * @return
     *     possible object is
     *     {@link CommissionType }
     *     
     */
    public CommissionType getCommissionType() {
        if (commissionType == null) {
            return CommissionType.OTHER_ITEM;
        } else {
            return commissionType;
        }
    }

    /**
     * Define o valor da propriedade commissionType.
     * 
     * @param value
     *     allowed object is
     *     {@link CommissionType }
     *     
     */
    public void setCommissionType(CommissionType value) {
        this.commissionType = value;
    }

    /**
     * Obtém o valor da propriedade icon.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIcon() {
        return icon;
    }

    /**
     * Define o valor da propriedade icon.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIcon(String value) {
        this.icon = value;
    }

}
