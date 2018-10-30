//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.3.1-b171012.0423 
// Consulte <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2018.10.29 às 07:49:11 PM BRT 
//


package org.l2j.xml.old.generated;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.l2jb.xml.old.generated package. 
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

    private final static QName _ForTypeStat_QNAME = new QName("", "stat");
    private final static QName _ForTypeEnchant_QNAME = new QName("", "enchant");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.l2jb.xml.old.generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ForType }
     * 
     */
    public ForType createForType() {
        return new ForType();
    }

    /**
     * Create an instance of {@link List }
     * 
     */
    public List createList() {
        return new List();
    }

    /**
     * Create an instance of {@link ItemType }
     * 
     */
    public ItemType createItemType() {
        return new ItemType();
    }

    /**
     * Create an instance of {@link SkillsType }
     * 
     */
    public SkillsType createSkillsType() {
        return new SkillsType();
    }

    /**
     * Create an instance of {@link UnequipSkillsType }
     * 
     */
    public UnequipSkillsType createUnequipSkillsType() {
        return new UnequipSkillsType();
    }

    /**
     * Create an instance of {@link CapsuledItemsType }
     * 
     */
    public CapsuledItemsType createCapsuledItemsType() {
        return new CapsuledItemsType();
    }

    /**
     * Create an instance of {@link CreateItemsType }
     * 
     */
    public CreateItemsType createCreateItemsType() {
        return new CreateItemsType();
    }

    /**
     * Create an instance of {@link SkillType }
     * 
     */
    public SkillType createSkillType() {
        return new SkillType();
    }

    /**
     * Create an instance of {@link CapsuledItemType }
     * 
     */
    public CapsuledItemType createCapsuledItemType() {
        return new CapsuledItemType();
    }

    /**
     * Create an instance of {@link CreateItemType }
     * 
     */
    public CreateItemType createCreateItemType() {
        return new CreateItemType();
    }

    /**
     * Create an instance of {@link SetType }
     * 
     */
    public SetType createSetType() {
        return new SetType();
    }

    /**
     * Create an instance of {@link PlayerType }
     * 
     */
    public PlayerType createPlayerType() {
        return new PlayerType();
    }

    /**
     * Create an instance of {@link AndType }
     * 
     */
    public AndType createAndType() {
        return new AndType();
    }

    /**
     * Create an instance of {@link GameType }
     * 
     */
    public GameType createGameType() {
        return new GameType();
    }

    /**
     * Create an instance of {@link NotType }
     * 
     */
    public NotType createNotType() {
        return new NotType();
    }

    /**
     * Create an instance of {@link UsingType }
     * 
     */
    public UsingType createUsingType() {
        return new UsingType();
    }

    /**
     * Create an instance of {@link TargetType }
     * 
     */
    public TargetType createTargetType() {
        return new TargetType();
    }

    /**
     * Create an instance of {@link EnchantType }
     * 
     */
    public EnchantType createEnchantType() {
        return new EnchantType();
    }

    /**
     * Create an instance of {@link CondType }
     * 
     */
    public CondType createCondType() {
        return new CondType();
    }

    /**
     * Create an instance of {@link ForType.Stat }
     * 
     */
    public ForType.Stat createForTypeStat() {
        return new ForType.Stat();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ForType.Stat }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ForType.Stat }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "stat", scope = ForType.class)
    public JAXBElement<ForType.Stat> createForTypeStat(ForType.Stat value) {
        return new JAXBElement<ForType.Stat>(_ForTypeStat_QNAME, ForType.Stat.class, ForType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EnchantType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link EnchantType }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "enchant", scope = ForType.class)
    public JAXBElement<EnchantType> createForTypeEnchant(EnchantType value) {
        return new JAXBElement<EnchantType>(_ForTypeEnchant_QNAME, EnchantType.class, ForType.class, value);
    }

}
