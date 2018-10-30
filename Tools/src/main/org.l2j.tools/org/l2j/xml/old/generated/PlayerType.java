//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.3.1-b171012.0423 
// Consulte <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2018.10.29 às 07:49:11 PM BRT 
//


package org.l2j.xml.old.generated;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Classe Java de playerType complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="playerType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="castle"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}integer"&gt;
 *             &lt;minInclusive value="-1"/&gt;
 *             &lt;maxInclusive value="9"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="isOnSide"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *             &lt;enumeration value="NEUTRAL"/&gt;
 *             &lt;enumeration value="LIGHT"/&gt;
 *             &lt;enumeration value="DARK"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="isClanLeader" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="clanHall" type="{http://www.w3.org/2001/XMLSchema}normalizedString" /&gt;
 *       &lt;attribute name="class_id_restriction" type="{http://www.w3.org/2001/XMLSchema}normalizedString" /&gt;
 *       &lt;attribute name="cloakStatus" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="isHero" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="isPvpFlagged" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="insideZoneId" type="{http://www.w3.org/2001/XMLSchema}normalizedString" /&gt;
 *       &lt;attribute name="level"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}positiveInteger"&gt;
 *             &lt;minInclusive value="1"/&gt;
 *             &lt;maxInclusive value="107"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="pledgeClass"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}integer"&gt;
 *             &lt;minInclusive value="-1"/&gt;
 *             &lt;maxInclusive value="11"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="levelRange" type="{http://www.w3.org/2001/XMLSchema}token" /&gt;
 *       &lt;attribute name="races" type="{http://www.w3.org/2001/XMLSchema}token" /&gt;
 *       &lt;attribute name="sex" type="{http://www.w3.org/2001/XMLSchema}byte" /&gt;
 *       &lt;attribute name="fort" type="{http://www.w3.org/2001/XMLSchema}integer" /&gt;
 *       &lt;attribute name="chaotic" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="subclass" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="SiegeZone" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" /&gt;
 *       &lt;attribute name="flyMounted" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="instanceId" type="{http://www.w3.org/2001/XMLSchema}normalizedString" /&gt;
 *       &lt;attribute name="categoryType" type="{http://www.w3.org/2001/XMLSchema}normalizedString" /&gt;
 *       &lt;attribute name="pkCount" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="vehicleMounted" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "playerType")
public class PlayerType {

    @XmlAttribute(name = "castle")
    protected Integer castle;
    @XmlAttribute(name = "isOnSide")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String isOnSide;
    @XmlAttribute(name = "isClanLeader")
    protected Boolean isClanLeader;
    @XmlAttribute(name = "clanHall")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String clanHall;
    @XmlAttribute(name = "class_id_restriction")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String classIdRestriction;
    @XmlAttribute(name = "cloakStatus")
    protected Boolean cloakStatus;
    @XmlAttribute(name = "isHero")
    protected Boolean isHero;
    @XmlAttribute(name = "isPvpFlagged")
    protected Boolean isPvpFlagged;
    @XmlAttribute(name = "insideZoneId")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String insideZoneId;
    @XmlAttribute(name = "level")
    protected Integer level;
    @XmlAttribute(name = "pledgeClass")
    protected Integer pledgeClass;
    @XmlAttribute(name = "levelRange")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String levelRange;
    @XmlAttribute(name = "races")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String races;
    @XmlAttribute(name = "sex")
    protected Byte sex;
    @XmlAttribute(name = "fort")
    protected BigInteger fort;
    @XmlAttribute(name = "chaotic")
    protected Boolean chaotic;
    @XmlAttribute(name = "subclass")
    protected Boolean subclass;
    @XmlAttribute(name = "SiegeZone")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger siegeZone;
    @XmlAttribute(name = "flyMounted")
    protected Boolean flyMounted;
    @XmlAttribute(name = "instanceId")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String instanceId;
    @XmlAttribute(name = "categoryType")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String categoryType;
    @XmlAttribute(name = "pkCount")
    protected Integer pkCount;
    @XmlAttribute(name = "vehicleMounted")
    protected Boolean vehicleMounted;

    /**
     * Obtém o valor da propriedade castle.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCastle() {
        return castle;
    }

    /**
     * Define o valor da propriedade castle.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCastle(Integer value) {
        this.castle = value;
    }

    /**
     * Obtém o valor da propriedade isOnSide.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsOnSide() {
        return isOnSide;
    }

    /**
     * Define o valor da propriedade isOnSide.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsOnSide(String value) {
        this.isOnSide = value;
    }

    /**
     * Obtém o valor da propriedade isClanLeader.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsClanLeader() {
        return isClanLeader;
    }

    /**
     * Define o valor da propriedade isClanLeader.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsClanLeader(Boolean value) {
        this.isClanLeader = value;
    }

    /**
     * Obtém o valor da propriedade clanHall.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClanHall() {
        return clanHall;
    }

    /**
     * Define o valor da propriedade clanHall.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClanHall(String value) {
        this.clanHall = value;
    }

    /**
     * Obtém o valor da propriedade classIdRestriction.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClassIdRestriction() {
        return classIdRestriction;
    }

    /**
     * Define o valor da propriedade classIdRestriction.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClassIdRestriction(String value) {
        this.classIdRestriction = value;
    }

    /**
     * Obtém o valor da propriedade cloakStatus.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCloakStatus() {
        return cloakStatus;
    }

    /**
     * Define o valor da propriedade cloakStatus.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCloakStatus(Boolean value) {
        this.cloakStatus = value;
    }

    /**
     * Obtém o valor da propriedade isHero.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsHero() {
        return isHero;
    }

    /**
     * Define o valor da propriedade isHero.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsHero(Boolean value) {
        this.isHero = value;
    }

    /**
     * Obtém o valor da propriedade isPvpFlagged.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsPvpFlagged() {
        return isPvpFlagged;
    }

    /**
     * Define o valor da propriedade isPvpFlagged.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsPvpFlagged(Boolean value) {
        this.isPvpFlagged = value;
    }

    /**
     * Obtém o valor da propriedade insideZoneId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInsideZoneId() {
        return insideZoneId;
    }

    /**
     * Define o valor da propriedade insideZoneId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInsideZoneId(String value) {
        this.insideZoneId = value;
    }

    /**
     * Obtém o valor da propriedade level.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getLevel() {
        return level;
    }

    /**
     * Define o valor da propriedade level.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setLevel(Integer value) {
        this.level = value;
    }

    /**
     * Obtém o valor da propriedade pledgeClass.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getPledgeClass() {
        return pledgeClass;
    }

    /**
     * Define o valor da propriedade pledgeClass.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPledgeClass(Integer value) {
        this.pledgeClass = value;
    }

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
     * Obtém o valor da propriedade races.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRaces() {
        return races;
    }

    /**
     * Define o valor da propriedade races.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRaces(String value) {
        this.races = value;
    }

    /**
     * Obtém o valor da propriedade sex.
     * 
     * @return
     *     possible object is
     *     {@link Byte }
     *     
     */
    public Byte getSex() {
        return sex;
    }

    /**
     * Define o valor da propriedade sex.
     * 
     * @param value
     *     allowed object is
     *     {@link Byte }
     *     
     */
    public void setSex(Byte value) {
        this.sex = value;
    }

    /**
     * Obtém o valor da propriedade fort.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getFort() {
        return fort;
    }

    /**
     * Define o valor da propriedade fort.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setFort(BigInteger value) {
        this.fort = value;
    }

    /**
     * Obtém o valor da propriedade chaotic.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isChaotic() {
        return chaotic;
    }

    /**
     * Define o valor da propriedade chaotic.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setChaotic(Boolean value) {
        this.chaotic = value;
    }

    /**
     * Obtém o valor da propriedade subclass.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSubclass() {
        return subclass;
    }

    /**
     * Define o valor da propriedade subclass.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSubclass(Boolean value) {
        this.subclass = value;
    }

    /**
     * Obtém o valor da propriedade siegeZone.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSiegeZone() {
        return siegeZone;
    }

    /**
     * Define o valor da propriedade siegeZone.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSiegeZone(BigInteger value) {
        this.siegeZone = value;
    }

    /**
     * Obtém o valor da propriedade flyMounted.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isFlyMounted() {
        return flyMounted;
    }

    /**
     * Define o valor da propriedade flyMounted.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setFlyMounted(Boolean value) {
        this.flyMounted = value;
    }

    /**
     * Obtém o valor da propriedade instanceId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInstanceId() {
        return instanceId;
    }

    /**
     * Define o valor da propriedade instanceId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInstanceId(String value) {
        this.instanceId = value;
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

    /**
     * Obtém o valor da propriedade pkCount.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getPkCount() {
        return pkCount;
    }

    /**
     * Define o valor da propriedade pkCount.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPkCount(Integer value) {
        this.pkCount = value;
    }

    /**
     * Obtém o valor da propriedade vehicleMounted.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isVehicleMounted() {
        return vehicleMounted;
    }

    /**
     * Define o valor da propriedade vehicleMounted.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setVehicleMounted(Boolean value) {
        this.vehicleMounted = value;
    }

}
