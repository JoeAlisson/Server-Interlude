//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.3.1-b171012.0423 
// Consulte <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2018.10.29 às 07:57:01 PM BRT 
//


package com.l2jb.xml.generated;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.l2jb.xml.generated package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Operator_QNAME = new QName("http://la2j.org", "operator");
    private final static QName _And_QNAME = new QName("http://la2j.org", "and");
    private final static QName _Or_QNAME = new QName("http://la2j.org", "or");
    private final static QName _Not_QNAME = new QName("http://la2j.org", "not");
    private final static QName _Condition_QNAME = new QName("http://la2j.org", "condition");
    private final static QName _Using_QNAME = new QName("http://la2j.org", "using");
    private final static QName _Player_QNAME = new QName("http://la2j.org", "player");
    private final static QName _Game_QNAME = new QName("http://la2j.org", "game");
    private final static QName _ItemTemplate_QNAME = new QName("http://la2j.org", "itemTemplate");
    private final static QName _Weapon_QNAME = new QName("http://la2j.org", "weapon");
    private final static QName _Armor_QNAME = new QName("http://la2j.org", "armor");
    private final static QName _Item_QNAME = new QName("http://la2j.org", "item");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.l2jb.xml.generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link XmlStatConditionAND }
     * 
     */
    public XmlStatConditionAND createXmlStatConditionAND() {
        return new XmlStatConditionAND();
    }

    /**
     * Create an instance of {@link XmlStatConditionOR }
     * 
     */
    public XmlStatConditionOR createXmlStatConditionOR() {
        return new XmlStatConditionOR();
    }

    /**
     * Create an instance of {@link XmlStatConditionNOT }
     * 
     */
    public XmlStatConditionNOT createXmlStatConditionNOT() {
        return new XmlStatConditionNOT();
    }

    /**
     * Create an instance of {@link XmlStatUsingCondition }
     * 
     */
    public XmlStatUsingCondition createXmlStatUsingCondition() {
        return new XmlStatUsingCondition();
    }

    /**
     * Create an instance of {@link XmlStatPlayerCondition }
     * 
     */
    public XmlStatPlayerCondition createXmlStatPlayerCondition() {
        return new XmlStatPlayerCondition();
    }

    /**
     * Create an instance of {@link XmlStatGameCondition }
     * 
     */
    public XmlStatGameCondition createXmlStatGameCondition() {
        return new XmlStatGameCondition();
    }

    /**
     * Create an instance of {@link ItemList }
     * 
     */
    public ItemList createItemList() {
        return new ItemList();
    }

    /**
     * Create an instance of {@link Weapon }
     * 
     */
    public Weapon createWeapon() {
        return new Weapon();
    }

    /**
     * Create an instance of {@link Armor }
     * 
     */
    public Armor createArmor() {
        return new Armor();
    }

    /**
     * Create an instance of {@link Item }
     * 
     */
    public Item createItem() {
        return new Item();
    }

    /**
     * Create an instance of {@link LocationList }
     * 
     */
    public LocationList createLocationList() {
        return new LocationList();
    }

    /**
     * Create an instance of {@link Location }
     * 
     */
    public Location createLocation() {
        return new Location();
    }

    /**
     * Create an instance of {@link Damage }
     * 
     */
    public Damage createDamage() {
        return new Damage();
    }

    /**
     * Create an instance of {@link XmlTypeStat }
     * 
     */
    public XmlTypeStat createXmlTypeStat() {
        return new XmlTypeStat();
    }

    /**
     * Create an instance of {@link XmlStatCondition }
     * 
     */
    public XmlStatCondition createXmlStatCondition() {
        return new XmlStatCondition();
    }

    /**
     * Create an instance of {@link ItemRestriction }
     * 
     */
    public ItemRestriction createItemRestriction() {
        return new ItemRestriction();
    }

    /**
     * Create an instance of {@link XmlItemSkill }
     * 
     */
    public XmlItemSkill createXmlItemSkill() {
        return new XmlItemSkill();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XmlStatConditionOperator }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XmlStatConditionOperator }{@code >}
     */
    @XmlElementDecl(namespace = "http://la2j.org", name = "operator")
    public JAXBElement<XmlStatConditionOperator> createOperator(XmlStatConditionOperator value) {
        return new JAXBElement<XmlStatConditionOperator>(_Operator_QNAME, XmlStatConditionOperator.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XmlStatConditionAND }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XmlStatConditionAND }{@code >}
     */
    @XmlElementDecl(namespace = "http://la2j.org", name = "and", substitutionHeadNamespace = "http://la2j.org", substitutionHeadName = "operator")
    public JAXBElement<XmlStatConditionAND> createAnd(XmlStatConditionAND value) {
        return new JAXBElement<XmlStatConditionAND>(_And_QNAME, XmlStatConditionAND.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XmlStatConditionOR }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XmlStatConditionOR }{@code >}
     */
    @XmlElementDecl(namespace = "http://la2j.org", name = "or", substitutionHeadNamespace = "http://la2j.org", substitutionHeadName = "operator")
    public JAXBElement<XmlStatConditionOR> createOr(XmlStatConditionOR value) {
        return new JAXBElement<XmlStatConditionOR>(_Or_QNAME, XmlStatConditionOR.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XmlStatConditionNOT }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XmlStatConditionNOT }{@code >}
     */
    @XmlElementDecl(namespace = "http://la2j.org", name = "not", substitutionHeadNamespace = "http://la2j.org", substitutionHeadName = "operator")
    public JAXBElement<XmlStatConditionNOT> createNot(XmlStatConditionNOT value) {
        return new JAXBElement<XmlStatConditionNOT>(_Not_QNAME, XmlStatConditionNOT.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XmlStatConditionType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XmlStatConditionType }{@code >}
     */
    @XmlElementDecl(namespace = "http://la2j.org", name = "condition")
    public JAXBElement<XmlStatConditionType> createCondition(XmlStatConditionType value) {
        return new JAXBElement<XmlStatConditionType>(_Condition_QNAME, XmlStatConditionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XmlStatUsingCondition }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XmlStatUsingCondition }{@code >}
     */
    @XmlElementDecl(namespace = "http://la2j.org", name = "using", substitutionHeadNamespace = "http://la2j.org", substitutionHeadName = "condition")
    public JAXBElement<XmlStatUsingCondition> createUsing(XmlStatUsingCondition value) {
        return new JAXBElement<XmlStatUsingCondition>(_Using_QNAME, XmlStatUsingCondition.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XmlStatPlayerCondition }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XmlStatPlayerCondition }{@code >}
     */
    @XmlElementDecl(namespace = "http://la2j.org", name = "player", substitutionHeadNamespace = "http://la2j.org", substitutionHeadName = "condition")
    public JAXBElement<XmlStatPlayerCondition> createPlayer(XmlStatPlayerCondition value) {
        return new JAXBElement<XmlStatPlayerCondition>(_Player_QNAME, XmlStatPlayerCondition.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XmlStatGameCondition }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XmlStatGameCondition }{@code >}
     */
    @XmlElementDecl(namespace = "http://la2j.org", name = "game", substitutionHeadNamespace = "http://la2j.org", substitutionHeadName = "condition")
    public JAXBElement<XmlStatGameCondition> createGame(XmlStatGameCondition value) {
        return new JAXBElement<XmlStatGameCondition>(_Game_QNAME, XmlStatGameCondition.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ItemTemplate }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ItemTemplate }{@code >}
     */
    @XmlElementDecl(namespace = "http://la2j.org", name = "itemTemplate")
    public JAXBElement<ItemTemplate> createItemTemplate(ItemTemplate value) {
        return new JAXBElement<ItemTemplate>(_ItemTemplate_QNAME, ItemTemplate.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Weapon }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Weapon }{@code >}
     */
    @XmlElementDecl(namespace = "http://la2j.org", name = "weapon", substitutionHeadNamespace = "http://la2j.org", substitutionHeadName = "itemTemplate")
    public JAXBElement<Weapon> createWeapon(Weapon value) {
        return new JAXBElement<Weapon>(_Weapon_QNAME, Weapon.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Armor }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Armor }{@code >}
     */
    @XmlElementDecl(namespace = "http://la2j.org", name = "armor", substitutionHeadNamespace = "http://la2j.org", substitutionHeadName = "itemTemplate")
    public JAXBElement<Armor> createArmor(Armor value) {
        return new JAXBElement<Armor>(_Armor_QNAME, Armor.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Item }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Item }{@code >}
     */
    @XmlElementDecl(namespace = "http://la2j.org", name = "item", substitutionHeadNamespace = "http://la2j.org", substitutionHeadName = "itemTemplate")
    public JAXBElement<Item> createItem(Item value) {
        return new JAXBElement<Item>(_Item_QNAME, Item.class, null, value);
    }

}
