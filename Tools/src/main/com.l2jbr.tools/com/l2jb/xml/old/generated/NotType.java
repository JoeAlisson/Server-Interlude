//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.3.1-b171012.0423 
// Consulte <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2018.10.29 às 07:49:11 PM BRT 
//


package com.l2jb.xml.old.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de notType complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="notType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="using" type="{}usingType" minOccurs="0"/&gt;
 *         &lt;element name="player" type="{}playerType" minOccurs="0"/&gt;
 *         &lt;element name="target" type="{}targetType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "notType", propOrder = {
    "using",
    "player",
    "target"
})
public class NotType {

    protected UsingType using;
    protected PlayerType player;
    protected TargetType target;

    /**
     * Obtém o valor da propriedade using.
     * 
     * @return
     *     possible object is
     *     {@link UsingType }
     *     
     */
    public UsingType getUsing() {
        return using;
    }

    /**
     * Define o valor da propriedade using.
     * 
     * @param value
     *     allowed object is
     *     {@link UsingType }
     *     
     */
    public void setUsing(UsingType value) {
        this.using = value;
    }

    /**
     * Obtém o valor da propriedade player.
     * 
     * @return
     *     possible object is
     *     {@link PlayerType }
     *     
     */
    public PlayerType getPlayer() {
        return player;
    }

    /**
     * Define o valor da propriedade player.
     * 
     * @param value
     *     allowed object is
     *     {@link PlayerType }
     *     
     */
    public void setPlayer(PlayerType value) {
        this.player = value;
    }

    /**
     * Obtém o valor da propriedade target.
     * 
     * @return
     *     possible object is
     *     {@link TargetType }
     *     
     */
    public TargetType getTarget() {
        return target;
    }

    /**
     * Define o valor da propriedade target.
     * 
     * @param value
     *     allowed object is
     *     {@link TargetType }
     *     
     */
    public void setTarget(TargetType value) {
        this.target = value;
    }

}
