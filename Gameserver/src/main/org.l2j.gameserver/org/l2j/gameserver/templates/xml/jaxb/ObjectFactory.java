//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.3.1-b171012.0423 
// Consulte <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2018.11.03 às 12:02:18 PM BRT 
//


package org.l2j.gameserver.templates.xml.jaxb;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.l2j.gameserver.templates.xml.jaxb package. 
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
    private final static QName _Owner_QNAME = new QName("http://la2j.org", "owner");
    private final static QName _State_QNAME = new QName("http://la2j.org", "state");
    private final static QName _Level_QNAME = new QName("http://la2j.org", "level");
    private final static QName _Using_QNAME = new QName("http://la2j.org", "using");
    private final static QName _Player_QNAME = new QName("http://la2j.org", "player");
    private final static QName _Game_QNAME = new QName("http://la2j.org", "game");
    private final static QName _ItemTemplate_QNAME = new QName("http://la2j.org", "itemTemplate");
    private final static QName _Weapon_QNAME = new QName("http://la2j.org", "weapon");
    private final static QName _Armor_QNAME = new QName("http://la2j.org", "armor");
    private final static QName _Item_QNAME = new QName("http://la2j.org", "item");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.l2j.gameserver.templates.xml.jaxb
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ClassInfo }
     * 
     */
    public ClassInfo createClassInfo() {
        return new ClassInfo();
    }

    /**
     * Create an instance of {@link LevelInfo }
     * 
     */
    public LevelInfo createLevelInfo() {
        return new LevelInfo();
    }

    /**
     * Create an instance of {@link AND }
     * 
     */
    public AND createAND() {
        return new AND();
    }

    /**
     * Create an instance of {@link OR }
     * 
     */
    public OR createOR() {
        return new OR();
    }

    /**
     * Create an instance of {@link NOT }
     * 
     */
    public NOT createNOT() {
        return new NOT();
    }

    /**
     * Create an instance of {@link OwnerCondition }
     * 
     */
    public OwnerCondition createOwnerCondition() {
        return new OwnerCondition();
    }

    /**
     * Create an instance of {@link StateCondition }
     * 
     */
    public StateCondition createStateCondition() {
        return new StateCondition();
    }

    /**
     * Create an instance of {@link LevelCondition }
     * 
     */
    public LevelCondition createLevelCondition() {
        return new LevelCondition();
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
     * Create an instance of {@link PlayerTemplate }
     * 
     */
    public PlayerTemplate createPlayerTemplate() {
        return new PlayerTemplate();
    }

    /**
     * Create an instance of {@link BaseStat }
     * 
     */
    public BaseStat createBaseStat() {
        return new BaseStat();
    }

    /**
     * Create an instance of {@link PhysicDefense }
     * 
     */
    public PhysicDefense createPhysicDefense() {
        return new PhysicDefense();
    }

    /**
     * Create an instance of {@link PhysicAttack }
     * 
     */
    public PhysicAttack createPhysicAttack() {
        return new PhysicAttack();
    }

    /**
     * Create an instance of {@link MagicDefense }
     * 
     */
    public MagicDefense createMagicDefense() {
        return new MagicDefense();
    }

    /**
     * Create an instance of {@link MagicAttack }
     * 
     */
    public MagicAttack createMagicAttack() {
        return new MagicAttack();
    }

    /**
     * Create an instance of {@link Damage }
     * 
     */
    public Damage createDamage() {
        return new Damage();
    }

    /**
     * Create an instance of {@link Collision }
     * 
     */
    public Collision createCollision() {
        return new Collision();
    }

    /**
     * Create an instance of {@link Speed }
     * 
     */
    public Speed createSpeed() {
        return new Speed();
    }

    /**
     * Create an instance of {@link AbnormalResist }
     * 
     */
    public AbnormalResist createAbnormalResist() {
        return new AbnormalResist();
    }

    /**
     * Create an instance of {@link Attributes }
     * 
     */
    public Attributes createAttributes() {
        return new Attributes();
    }

    /**
     * Create an instance of {@link StartingItem }
     * 
     */
    public StartingItem createStartingItem() {
        return new StartingItem();
    }

    /**
     * Create an instance of {@link LocationList }
     * 
     */
    public LocationList createLocationList() {
        return new LocationList();
    }

    /**
     * Create an instance of {@link UseCondition }
     * 
     */
    public UseCondition createUseCondition() {
        return new UseCondition();
    }

    /**
     * Create an instance of {@link Location }
     * 
     */
    public Location createLocation() {
        return new Location();
    }

    /**
     * Create an instance of {@link Stat }
     * 
     */
    public Stat createStat() {
        return new Stat();
    }

    /**
     * Create an instance of {@link XmlStatCondition }
     * 
     */
    public XmlStatCondition createXmlStatCondition() {
        return new XmlStatCondition();
    }

    /**
     * Create an instance of {@link CrystalInfo }
     * 
     */
    public CrystalInfo createCrystalInfo() {
        return new CrystalInfo();
    }

    /**
     * Create an instance of {@link ItemConsume }
     * 
     */
    public ItemConsume createItemConsume() {
        return new ItemConsume();
    }

    /**
     * Create an instance of {@link ItemRestriction }
     * 
     */
    public ItemRestriction createItemRestriction() {
        return new ItemRestriction();
    }

    /**
     * Create an instance of {@link ItemSkill }
     * 
     */
    public ItemSkill createItemSkill() {
        return new ItemSkill();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Operator }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Operator }{@code >}
     */
    @XmlElementDecl(namespace = "http://la2j.org", name = "operator")
    public JAXBElement<Operator> createOperator(Operator value) {
        return new JAXBElement<Operator>(_Operator_QNAME, Operator.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AND }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link AND }{@code >}
     */
    @XmlElementDecl(namespace = "http://la2j.org", name = "and", substitutionHeadNamespace = "http://la2j.org", substitutionHeadName = "operator")
    public JAXBElement<AND> createAnd(AND value) {
        return new JAXBElement<AND>(_And_QNAME, AND.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OR }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link OR }{@code >}
     */
    @XmlElementDecl(namespace = "http://la2j.org", name = "or", substitutionHeadNamespace = "http://la2j.org", substitutionHeadName = "operator")
    public JAXBElement<OR> createOr(OR value) {
        return new JAXBElement<OR>(_Or_QNAME, OR.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NOT }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link NOT }{@code >}
     */
    @XmlElementDecl(namespace = "http://la2j.org", name = "not", substitutionHeadNamespace = "http://la2j.org", substitutionHeadName = "operator")
    public JAXBElement<NOT> createNot(NOT value) {
        return new JAXBElement<NOT>(_Not_QNAME, NOT.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Condition }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Condition }{@code >}
     */
    @XmlElementDecl(namespace = "http://la2j.org", name = "condition")
    public JAXBElement<Condition> createCondition(Condition value) {
        return new JAXBElement<Condition>(_Condition_QNAME, Condition.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OwnerCondition }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link OwnerCondition }{@code >}
     */
    @XmlElementDecl(namespace = "http://la2j.org", name = "owner", substitutionHeadNamespace = "http://la2j.org", substitutionHeadName = "condition")
    public JAXBElement<OwnerCondition> createOwner(OwnerCondition value) {
        return new JAXBElement<OwnerCondition>(_Owner_QNAME, OwnerCondition.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StateCondition }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link StateCondition }{@code >}
     */
    @XmlElementDecl(namespace = "http://la2j.org", name = "state", substitutionHeadNamespace = "http://la2j.org", substitutionHeadName = "condition")
    public JAXBElement<StateCondition> createState(StateCondition value) {
        return new JAXBElement<StateCondition>(_State_QNAME, StateCondition.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LevelCondition }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link LevelCondition }{@code >}
     */
    @XmlElementDecl(namespace = "http://la2j.org", name = "level", substitutionHeadNamespace = "http://la2j.org", substitutionHeadName = "condition")
    public JAXBElement<LevelCondition> createLevel(LevelCondition value) {
        return new JAXBElement<LevelCondition>(_Level_QNAME, LevelCondition.class, null, value);
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
