//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.3.1-b171012.0423 
// Consulte <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2018.10.21 às 04:09:27 PM BRT 
//


package org.l2j.gameserver.templates.xml.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de anonymous complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="baseStats" type="{http://la2j.org}BaseStat"/&gt;
 *         &lt;element name="physicDefense" type="{http://la2j.org}PhysicDefense"/&gt;
 *         &lt;element name="physicAttack" type="{http://la2j.org}PhysicAttack"/&gt;
 *         &lt;element name="magicDefense" type="{http://la2j.org}MagicDefense"/&gt;
 *         &lt;element name="magicAttack" type="{http://la2j.org}MagicAttack"/&gt;
 *         &lt;element name="damage" type="{http://la2j.org}Damage"/&gt;
 *         &lt;element name="collision" type="{http://la2j.org}Collision"/&gt;
 *         &lt;element name="baseSpeed" type="{http://la2j.org}Speed"/&gt;
 *         &lt;element name="abnormalResist" type="{http://la2j.org}AbnormalResist"/&gt;
 *         &lt;element name="attributes" type="{http://la2j.org}Attributes"/&gt;
 *         &lt;element name="startingItem" type="{http://la2j.org}StartingItem" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="startingLocation" type="{http://la2j.org}LocationList"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="classId" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="race" use="required" type="{http://la2j.org}Race" /&gt;
 *       &lt;attribute name="type" type="{http://la2j.org}ClassType" default="FIGHTER" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "baseStats",
    "physicDefense",
    "physicAttack",
    "magicDefense",
    "magicAttack",
    "damage",
    "collision",
    "baseSpeed",
    "abnormalResist",
    "attributes",
    "startingItem",
    "startingLocation"
})
@XmlRootElement(name = "PlayerTemplate")
public class PlayerTemplate {

    @XmlElement(required = true)
    protected BaseStat baseStats;
    @XmlElement(required = true)
    protected PhysicDefense physicDefense;
    @XmlElement(required = true)
    protected PhysicAttack physicAttack;
    @XmlElement(required = true)
    protected MagicDefense magicDefense;
    @XmlElement(required = true)
    protected MagicAttack magicAttack;
    @XmlElement(required = true)
    protected Damage damage;
    @XmlElement(required = true)
    protected Collision collision;
    @XmlElement(required = true)
    protected Speed baseSpeed;
    @XmlElement(required = true)
    protected AbnormalResist abnormalResist;
    @XmlElement(required = true)
    protected Attributes attributes;
    protected List<StartingItem> startingItem;
    @XmlElement(required = true)
    protected LocationList startingLocation;
    @XmlAttribute(name = "classId", required = true)
    protected int classId;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "race", required = true)
    protected Race race;
    @XmlAttribute(name = "type")
    protected ClassType type;

    /**
     * Obtém o valor da propriedade baseStats.
     * 
     * @return
     *     possible object is
     *     {@link BaseStat }
     *     
     */
    public BaseStat getBaseStats() {
        return baseStats;
    }

    /**
     * Define o valor da propriedade baseStats.
     * 
     * @param value
     *     allowed object is
     *     {@link BaseStat }
     *     
     */
    public void setBaseStats(BaseStat value) {
        this.baseStats = value;
    }

    /**
     * Obtém o valor da propriedade physicDefense.
     * 
     * @return
     *     possible object is
     *     {@link PhysicDefense }
     *     
     */
    public PhysicDefense getPhysicDefense() {
        return physicDefense;
    }

    /**
     * Define o valor da propriedade physicDefense.
     * 
     * @param value
     *     allowed object is
     *     {@link PhysicDefense }
     *     
     */
    public void setPhysicDefense(PhysicDefense value) {
        this.physicDefense = value;
    }

    /**
     * Obtém o valor da propriedade physicAttack.
     * 
     * @return
     *     possible object is
     *     {@link PhysicAttack }
     *     
     */
    public PhysicAttack getPhysicAttack() {
        return physicAttack;
    }

    /**
     * Define o valor da propriedade physicAttack.
     * 
     * @param value
     *     allowed object is
     *     {@link PhysicAttack }
     *     
     */
    public void setPhysicAttack(PhysicAttack value) {
        this.physicAttack = value;
    }

    /**
     * Obtém o valor da propriedade magicDefense.
     * 
     * @return
     *     possible object is
     *     {@link MagicDefense }
     *     
     */
    public MagicDefense getMagicDefense() {
        return magicDefense;
    }

    /**
     * Define o valor da propriedade magicDefense.
     * 
     * @param value
     *     allowed object is
     *     {@link MagicDefense }
     *     
     */
    public void setMagicDefense(MagicDefense value) {
        this.magicDefense = value;
    }

    /**
     * Obtém o valor da propriedade magicAttack.
     * 
     * @return
     *     possible object is
     *     {@link MagicAttack }
     *     
     */
    public MagicAttack getMagicAttack() {
        return magicAttack;
    }

    /**
     * Define o valor da propriedade magicAttack.
     * 
     * @param value
     *     allowed object is
     *     {@link MagicAttack }
     *     
     */
    public void setMagicAttack(MagicAttack value) {
        this.magicAttack = value;
    }

    /**
     * Obtém o valor da propriedade damage.
     * 
     * @return
     *     possible object is
     *     {@link Damage }
     *     
     */
    public Damage getDamage() {
        return damage;
    }

    /**
     * Define o valor da propriedade damage.
     * 
     * @param value
     *     allowed object is
     *     {@link Damage }
     *     
     */
    public void setDamage(Damage value) {
        this.damage = value;
    }

    /**
     * Obtém o valor da propriedade collision.
     * 
     * @return
     *     possible object is
     *     {@link Collision }
     *     
     */
    public Collision getCollision() {
        return collision;
    }

    /**
     * Define o valor da propriedade collision.
     * 
     * @param value
     *     allowed object is
     *     {@link Collision }
     *     
     */
    public void setCollision(Collision value) {
        this.collision = value;
    }

    /**
     * Obtém o valor da propriedade baseSpeed.
     * 
     * @return
     *     possible object is
     *     {@link Speed }
     *     
     */
    public Speed getBaseSpeed() {
        return baseSpeed;
    }

    /**
     * Define o valor da propriedade baseSpeed.
     * 
     * @param value
     *     allowed object is
     *     {@link Speed }
     *     
     */
    public void setBaseSpeed(Speed value) {
        this.baseSpeed = value;
    }

    /**
     * Obtém o valor da propriedade abnormalResist.
     * 
     * @return
     *     possible object is
     *     {@link AbnormalResist }
     *     
     */
    public AbnormalResist getAbnormalResist() {
        return abnormalResist;
    }

    /**
     * Define o valor da propriedade abnormalResist.
     * 
     * @param value
     *     allowed object is
     *     {@link AbnormalResist }
     *     
     */
    public void setAbnormalResist(AbnormalResist value) {
        this.abnormalResist = value;
    }

    /**
     * Obtém o valor da propriedade attributes.
     * 
     * @return
     *     possible object is
     *     {@link Attributes }
     *     
     */
    public Attributes getAttributes() {
        return attributes;
    }

    /**
     * Define o valor da propriedade attributes.
     * 
     * @param value
     *     allowed object is
     *     {@link Attributes }
     *     
     */
    public void setAttributes(Attributes value) {
        this.attributes = value;
    }

    /**
     * Gets the value of the startingItem property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the startingItem property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStartingItem().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StartingItem }
     * 
     * 
     */
    public List<StartingItem> getStartingItem() {
        if (startingItem == null) {
            startingItem = new ArrayList<StartingItem>();
        }
        return this.startingItem;
    }

    /**
     * Obtém o valor da propriedade startingLocation.
     * 
     * @return
     *     possible object is
     *     {@link LocationList }
     *     
     */
    public LocationList getStartingLocation() {
        return startingLocation;
    }

    /**
     * Define o valor da propriedade startingLocation.
     * 
     * @param value
     *     allowed object is
     *     {@link LocationList }
     *     
     */
    public void setStartingLocation(LocationList value) {
        this.startingLocation = value;
    }

    /**
     * Obtém o valor da propriedade classId.
     * 
     */
    public int getClassId() {
        return classId;
    }

    /**
     * Define o valor da propriedade classId.
     * 
     */
    public void setClassId(int value) {
        this.classId = value;
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
     * Obtém o valor da propriedade race.
     * 
     * @return
     *     possible object is
     *     {@link Race }
     *     
     */
    public Race getRace() {
        return race;
    }

    /**
     * Define o valor da propriedade race.
     * 
     * @param value
     *     allowed object is
     *     {@link Race }
     *     
     */
    public void setRace(Race value) {
        this.race = value;
    }

    /**
     * Obtém o valor da propriedade type.
     * 
     * @return
     *     possible object is
     *     {@link ClassType }
     *     
     */
    public ClassType getType() {
        if (type == null) {
            return ClassType.FIGHTER;
        } else {
            return type;
        }
    }

    /**
     * Define o valor da propriedade type.
     * 
     * @param value
     *     allowed object is
     *     {@link ClassType }
     *     
     */
    public void setType(ClassType value) {
        this.type = value;
    }

}
