package org.l2j.gameserver.model.entity.database;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.l2j.commons.database.model.Entity;
import org.springframework.data.annotation.Id;

@Table("seven_signs")
public class SevenSignsPlayer extends Entity<Integer> {

    @Id
    @Column("char_obj_id")
    private int charObjId;
    private String cabal;
    private int seal;
    @Column("red_stones")
    private long redStones;
    @Column("green_stones")
    private long greenStones;
    @Column("blue_stones")
    private long blueStones;
    @Column("ancient_adena_amount")
    private long ancientAdenaAmount;
    @Column("contribution_score")
    private long contributionScore;

    public SevenSignsPlayer(int charObjId, String cabal, int seal) {
        this.charObjId = charObjId;
        this.cabal = cabal;
        this.seal = seal;
    }

    public long getStoneContrib() {
        return  redStones + blueStones + greenStones;
    }

    @Override
    public Integer getId() {
        return charObjId;
    }

    public String getCabal() {
        return cabal;
    }

    public void setCabal(String cabal) {
        this.cabal = cabal;
    }

    public int getSeal() {
        return seal;
    }

    public void setSeal(int seal) {
        this.seal = seal;
    }

    public long getRedStones() {
        return redStones;
    }

    public void setRedStones(long stones) {
        this.redStones = stones;
    }

    public long getGreenStones() {
        return greenStones;
    }

    public void setGreenStones(long stones) {
        this.greenStones = stones;
    }

    public long getBlueStones() {
        return blueStones;
    }

    public void setBlueStones(long stones) {
        this.blueStones = stones;
    }

    public long getAncientAdenaAmount() {
        return ancientAdenaAmount;
    }

    public void setAncientAdenaAmount(long totalAncientAdena) {
        this.ancientAdenaAmount = totalAncientAdena;
    }

    public long getContributionScore() {
        return contributionScore;
    }

    public void setContribuitionScore(long contribuition) {
        this.contributionScore = contribuition;
    }
}
