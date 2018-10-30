//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.3.1-b171012.0423 
// Consulte <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2018.10.29 às 07:49:11 PM BRT 
//


package org.l2j.xml.old.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Classe Java de setType complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="setType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="name" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *             &lt;enumeration value="displayId"/&gt;
 *             &lt;enumeration value="armor_type"/&gt;
 *             &lt;enumeration value="attack_range"/&gt;
 *             &lt;enumeration value="auto_destroy_time"/&gt;
 *             &lt;enumeration value="blessed"/&gt;
 *             &lt;enumeration value="bodypart"/&gt;
 *             &lt;enumeration value="capsuled_items"/&gt;
 *             &lt;enumeration value="change_weaponId"/&gt;
 *             &lt;enumeration value="crystal_count"/&gt;
 *             &lt;enumeration value="crystal_type"/&gt;
 *             &lt;enumeration value="damage_range"/&gt;
 *             &lt;enumeration value="default_action"/&gt;
 *             &lt;enumeration value="duration"/&gt;
 *             &lt;enumeration value="element_enabled"/&gt;
 *             &lt;enumeration value="enchant_enabled"/&gt;
 *             &lt;enumeration value="enchant4_skill"/&gt;
 *             &lt;enumeration value="enchanted"/&gt;
 *             &lt;enumeration value="equip_condition"/&gt;
 *             &lt;enumeration value="equip_reuse_delay"/&gt;
 *             &lt;enumeration value="ex_immediate_effect"/&gt;
 *             &lt;enumeration value="extractableCountMin"/&gt;
 *             &lt;enumeration value="extractableCountMax"/&gt;
 *             &lt;enumeration value="etcitem_type"/&gt;
 *             &lt;enumeration value="for_npc"/&gt;
 *             &lt;enumeration value="handler"/&gt;
 *             &lt;enumeration value="icon"/&gt;
 *             &lt;enumeration value="immediate_effect"/&gt;
 *             &lt;enumeration value="is_depositable"/&gt;
 *             &lt;enumeration value="is_destroyable"/&gt;
 *             &lt;enumeration value="is_dropable"/&gt;
 *             &lt;enumeration value="is_freightable"/&gt;
 *             &lt;enumeration value="is_magic_weapon"/&gt;
 *             &lt;enumeration value="is_oly_restricted"/&gt;
 *             &lt;enumeration value="is_coc_restricted"/&gt;
 *             &lt;enumeration value="isAppearanceable"/&gt;
 *             &lt;enumeration value="is_premium"/&gt;
 *             &lt;enumeration value="is_questitem"/&gt;
 *             &lt;enumeration value="is_sellable"/&gt;
 *             &lt;enumeration value="is_stackable"/&gt;
 *             &lt;enumeration value="is_tradable"/&gt;
 *             &lt;enumeration value="is_infinite"/&gt;
 *             &lt;enumeration value="is_commissionable"/&gt;
 *             &lt;enumeration value="is_mailable"/&gt;
 *             &lt;enumeration value="is_clan_depositable"/&gt;
 *             &lt;enumeration value="is_private_storeable"/&gt;
 *             &lt;enumeration value="isAttackWeapon"/&gt;
 *             &lt;enumeration value="isForceEquip"/&gt;
 *             &lt;enumeration value="item_skill"/&gt;
 *             &lt;enumeration value="allow_self_resurrection"/&gt;
 *             &lt;enumeration value="material"/&gt;
 *             &lt;enumeration value="mp_consume"/&gt;
 *             &lt;enumeration value="oncrit_chance"/&gt;
 *             &lt;enumeration value="oncrit_skill"/&gt;
 *             &lt;enumeration value="onmagic_chance"/&gt;
 *             &lt;enumeration value="onmagic_skill"/&gt;
 *             &lt;enumeration value="price"/&gt;
 *             &lt;enumeration value="random_damage"/&gt;
 *             &lt;enumeration value="recipe_id"/&gt;
 *             &lt;enumeration value="reduced_mp_consume"/&gt;
 *             &lt;enumeration value="reduced_soulshot"/&gt;
 *             &lt;enumeration value="reuse_delay"/&gt;
 *             &lt;enumeration value="shared_reuse_group"/&gt;
 *             &lt;enumeration value="soulshots"/&gt;
 *             &lt;enumeration value="spiritshots"/&gt;
 *             &lt;enumeration value="time"/&gt;
 *             &lt;enumeration value="unequip_skill"/&gt;
 *             &lt;enumeration value="use_condition"/&gt;
 *             &lt;enumeration value="useSkillDisTime"/&gt;
 *             &lt;enumeration value="useWeaponSkillsOnly"/&gt;
 *             &lt;enumeration value="weapon_type"/&gt;
 *             &lt;enumeration value="weight"/&gt;
 *             &lt;enumeration value="commissionItemType"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="val" use="required" type="{http://www.w3.org/2001/XMLSchema}token" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "setType")
public class SetType {

    @XmlAttribute(name = "name", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String name;
    @XmlAttribute(name = "val", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String val;

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
     * Obtém o valor da propriedade val.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVal() {
        return val;
    }

    /**
     * Define o valor da propriedade val.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVal(String value) {
        this.val = value;
    }

}
