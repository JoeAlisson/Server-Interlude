package org.l2j.gameserver.model.actor.stat;

import org.l2j.commons.Config;
import org.l2j.gameserver.model.L2Character;
import org.l2j.gameserver.model.L2Skill;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.skills.Calculator;
import org.l2j.gameserver.skills.Env;
import org.l2j.gameserver.skills.Stats;

import static java.lang.Math.min;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class CharStat {

	private final L2Character activeChar;
	private long _exp = 0;
	private long _sp = 0;
	private int _level = 1;
	

	public CharStat(L2Character activeChar) {
		this.activeChar = activeChar;
	}

	/**
	 * Calculate the new value of the state with modifiers that will be applied on the targeted L2Character.<BR>
	 * <BR>
	 * <B><U> Concept</U> :</B><BR>
	 * <BR>
	 * A L2Character owns a table of Calculators called <B>_calculators</B>. Each Calculator (a calculator per state)
     *  own a table of Func object. A Func object is a mathematic function that permit to calculate the modifier of a state
     *  (ex : REGENERATE_HP_RATE...) : <BR>
	 * <BR>
	 *      FuncAtkAccuracy -> Math.sqrt(_player.getDexterity())*6+_player.getLevel()<BR>
	 * <BR>
	 * When the calc method of a calculator is launched, each mathematic function is called according to its priority
     *  <B>_order</B>. Indeed, Func with lowest priority order is executed first and Funcs with the same order
     *  are executed in unspecified order. The result of the calculation is stored in
	 *  the value property of an Env class instance.<BR>
	 * <BR>
	 * @param stat The stat to calculate the new value with modifiers
	 * @param init The initial value of the stat before applying modifiers
	 * @param target The L2Charcater whose properties will be used in the calculation (ex : CON, INT...)
	 * @param skill The L2Skill whose properties will be used in the calculation (ex : Level...)
	 * @return stat value
	 */
	public final double calcStat(Stats stat, double init, L2Character target, L2Skill skill) {
		if (isNull(activeChar)) {
			return init;
		}
		
		int id = stat.ordinal();
		
		Calculator c = activeChar.getCalculators()[id];
		
		// If no Func object found, no modifier is applied
		if (isNull(c) || c.size() == 0) {
			return init;
		}
		
		// Create and init an Env object to pass parameters to the Calculator
		Env env = new Env();
		env.player = activeChar;
		env.target = target;
		env.skill = skill;
		env.value = init;
		
		// Launch the calculation
		c.calc(env);
		// avoid some troubles with negative stats (some stats should never be negative)
		if ((env.value <= 0) && ((stat == Stats.MAX_HP) || (stat == Stats.MAX_MP) ||
				(stat == Stats.MAX_CP) || (stat == Stats.MAGIC_DEFENCE) || (stat == Stats.PHYSIC_DEFENCE) ||
				(stat == Stats.PHYSIC_ATTACK) || (stat == Stats.MAGIC_ATTACK) || (stat == Stats.PHYSIC_ATTACK_SPEED) ||
				(stat == Stats.MAGIC_ATTACK_SPEED) || (stat == Stats.SHIELD_DEFENCE) || (stat == Stats.STAT_CON) ||
				(stat == Stats.STAT_DEX) || (stat == Stats.STAT_INT) || (stat == Stats.STAT_MEN) || (stat == Stats.STAT_STR) ||
				(stat == Stats.STAT_WIT))) {
			env.value = 1;
		}
		
		return env.value;
	}

	public int getAccuracy() {
	    return isNull(activeChar) ? 0 : (int) (calcStat(Stats.ACCURACY, 0, null, null) / activeChar.getWeaponExpertisePenalty());
	}

	public final float getAttackSpeedMultiplier() {
	    return isNull(activeChar) ? 1 : (float) (((1.1) * getPAtkSpd()) / activeChar.getTemplate().getPAtkSpd());
	}

	public final int getCON() {
	    return isNull(activeChar) ? 1 : (int) calcStat(Stats.STAT_CON, activeChar.getTemplate().getConstitution(), null, null);
	}

	public final double getCriticalDmg(L2Character target, double init) {
		return calcStat(Stats.CRITICAL_DAMAGE, init, target, null);
	}

	public int getCriticalHit(L2Character target, L2Skill skill) {
		int criticalHit = isNull(activeChar) ? 1 : (int) calcStat(Stats.CRITICAL_RATE, activeChar.getTemplate().getCritRate(), target, skill);
		// Set a cap of Critical Hit at 500
		return min(500, criticalHit);
	}

	public final int getDEX() {
		return isNull(activeChar) ? 1 : (int) calcStat(Stats.STAT_DEX, activeChar.getTemplate().getDexterity(), null, null);
	}

	public int getEvasionRate(L2Character target) {
		return isNull(activeChar) ? 1 : (int) (calcStat(Stats.EVASION_RATE, 0, target, null) / activeChar.getArmourExpertisePenalty());
	}

	public int getINT() {
		return isNull(activeChar) ? 1 : (int) calcStat(Stats.STAT_INT, activeChar.getTemplate().getIntellienge(), null, null);
	}

	public final int getMagicalAttackRange(L2Skill skill) {
		if (nonNull(skill)) {
			return (int) calcStat(Stats.MAGIC_ATTACK_RANGE, skill.getCastRange(), null, skill);
		}
		
		if (isNull(activeChar)) {
			return 1;
		}
		
		return activeChar.getTemplate().getAtkRange();
	}
	
	public final int getMaxCp() {
	    return isNull(activeChar) ? 1 : (int) calcStat(Stats.MAX_CP, activeChar.getTemplate().getCp(), null, null);
	}
	
	public int getMaxHp() {
	    return isNull(activeChar) ? 1 : (int) calcStat(Stats.MAX_HP, activeChar.getTemplate().getHp(), null, null);
	}
	
	public int getMaxMp() {
	    return isNull(activeChar) ? 1 : (int) calcStat(Stats.MAX_MP, activeChar.getTemplate().getMp(), null, null);
	}

	public int getMAtk(L2Character target, L2Skill skill) {
	    if (isNull(activeChar)) {
			return 1;
		}

		// TODO move to the Monsters Stats
		float bonusAtk = 1;
		if (Config.L2JMOD_CHAMPION_ENABLE && activeChar.isChampion()) {
			bonusAtk = Config.L2JMOD_CHAMPION_ATK;
		}

		double attack = activeChar.getTemplate().getMAtk() * bonusAtk;
		// Get the skill type to calculate its effect in function of base stats of the L2Character target
		Stats stat = isNull(skill) ? null : skill.getStat();
		
		if (nonNull(stat)) {
			switch (stat) {
				case AGGRESSION:
					attack += activeChar.getTemplate().getAggression();
					break;
				case BLEED:
					attack += activeChar.getTemplate().getBleed();
					break;
				case POISON:
					attack += activeChar.getTemplate().getPoison();
					break;
				case STUN:
					attack += activeChar.getTemplate().getStun();
					break;
				case ROOT:
					attack += activeChar.getTemplate().getRoot();
					break;
				case MOVEMENT:
					attack += activeChar.getTemplate().getMovement();
					break;
				case CONFUSION:
					attack += activeChar.getTemplate().getConfusion();
					break;
				case SLEEP:
					attack += activeChar.getTemplate().getSleep();
					break;
				case FIRE:
					attack += activeChar.getTemplate().getFire();
					break;
				case WIND:
					attack += activeChar.getTemplate().getWind();
					break;
				case WATER:
					attack += activeChar.getTemplate().getWater();
					break;
				case EARTH:
					attack += activeChar.getTemplate().getEarth();
					break;
				case HOLY:
					attack += activeChar.getTemplate().getHoly();
					break;
				case DARK:
					attack += activeChar.getTemplate().getDark();
					break;
			}
		}
		
		// Add the power of the skill to the attack effect
		if (nonNull(skill)) {
			attack += skill.getPower();
		}
		
		// Calculate modifiers Magic Attack
		return (int) calcStat(Stats.MAGIC_ATTACK, attack, target, skill);
	}

	public int getMAtkSpd() {
		if (isNull(activeChar)) {
			return 1;
		}

        // TODO move to the Monsters Stats
		float bonusSpdAtk = 1;
		if (Config.L2JMOD_CHAMPION_ENABLE && activeChar.isChampion()) {
			bonusSpdAtk = Config.L2JMOD_CHAMPION_SPD_ATK;
		}
		return (int) (calcStat(Stats.MAGIC_ATTACK_SPEED, activeChar.getTemplate().getMAtkSpd() * bonusSpdAtk, null, null) / activeChar.getArmourExpertisePenalty());
	}

	public final int getMCriticalHit(L2Character target, L2Skill skill) {
		return (int) calcStat(Stats.MCRITICAL_RATE, 5, target, skill);
	}

	public int getMDef(L2Character target, L2Skill skill) {
		if (isNull(activeChar)){
			return 1;
		}
		
		// Get the base MAtk of the L2Character
		double defence = activeChar.getTemplate().getMDef();

		// TODO move to Monster Stats
		// Calculate modifier for Raid Bosses
		if (activeChar.isRaid()) {
			defence *= Config.RAID_DEFENCE_MULTIPLIER;
		}
		
		// Calculate modifiers Magic Attack
		return (int) calcStat(Stats.MAGIC_DEFENCE, defence, target, skill);
	}

	public final int getMEN() {
	    return isNull(activeChar) ? 1 : (int) calcStat(Stats.STAT_MEN, activeChar.getTemplate().getMentality(), null, null);
	}
	
	public final float getMovementSpeedMultiplier() {
	    return isNull(activeChar) ? 1 : (float) getRunSpeed() / activeChar.getTemplate().getRunSpd();
	}

	public final float getMoveSpeed() {
		if (isNull(activeChar)) {
			return 1;
		}
		return  activeChar.isRunning() ?  getRunSpeed() :  getWalkSpeed();
	}

	public final double getMReuseRate(L2Skill skill) {
	    return isNull(activeChar) ? 1 : calcStat(Stats.MAGIC_REUSE_RATE, activeChar.getTemplate().getMReuseRate(), null, skill);
	}

	public int getPAtk(L2Character target) {
		if (isNull(activeChar)) {
			return 1;
		}
		// TODO move to monster stats
		float bonusAtk = 1;
		if (Config.L2JMOD_CHAMPION_ENABLE && activeChar.isChampion()) {
			bonusAtk = Config.L2JMOD_CHAMPION_ATK;
		}
		return (int) calcStat(Stats.PHYSIC_ATTACK, activeChar.getTemplate().getpAtk() * bonusAtk, target, null);
	}

	public final double getPAtkAnimals(L2Character target)
	{
		return calcStat(Stats.PATK_ANIMALS, 1, target, null);
	}

	public final double getPAtkDragons(L2Character target)
	{
		return calcStat(Stats.PATK_DRAGONS, 1, target, null);
	}

	public final double getPAtkInsects(L2Character target)
	{
		return calcStat(Stats.PATK_INSECTS, 1, target, null);
	}

	public final double getPAtkMonsters(L2Character target)
	{
		return calcStat(Stats.PATK_MONSTERS, 1, target, null);
	}

	public final double getPAtkPlants(L2Character target)
	{
		return calcStat(Stats.PATK_PLANTS, 1, target, null);
	}

	public int getPAtkSpd() {
	    if(isNull(activeChar)) {
	        return 1;
        }

        // TODO Move to Monsters stats
		float bonusAtk = 1;
		if (Config.L2JMOD_CHAMPION_ENABLE && activeChar.isChampion()) {
			bonusAtk = Config.L2JMOD_CHAMPION_SPD_ATK;
		}
		return (int) (calcStat(Stats.PHYSIC_ATTACK_SPEED, activeChar.getTemplate().getPAtkSpd() * bonusAtk, null, null) / activeChar.getArmourExpertisePenalty());
	}

	public final double getPAtkUndead(L2Character target)
	{
		return calcStat(Stats.PATK_UNDEAD, 1, target, null);
	}
	
	public final double getPDefUndead(L2Character target)
	{
		return calcStat(Stats.PDEF_UNDEAD, 1, target, null);
	}

	public int getPDef(L2Character target) {
		if (isNull(activeChar)) {
			return 1;
		}
        int defense = activeChar.getTemplate().getpDef();
		// TODO move to Monsters stats
        if (activeChar.isRaid()) {
            defense *= Config.RAID_DEFENCE_MULTIPLIER;
        }

		return (int) calcStat(Stats.PHYSIC_DEFENCE, defense, target, null);
	}

	public final int getPhysicalAttackRange() {
	    return isNull(activeChar) ? 1 : (int) calcStat(Stats.PHYSIC_ATTACK_RANGE, activeChar.getTemplate().getAtkRange(), null, null);
	}

	public final double getReuseModifier(L2Character target)
	{
		return calcStat(Stats.ATTACK_REUSE, 1, target, null);
	}

	public int getRunSpeed() {
		if (isNull(activeChar)) {
			return 1;
		}

		int val = (int) (calcStat(Stats.RUN_SPEED, activeChar.getTemplate().getRunSpd(), null, null) / activeChar.getArmourExpertisePenalty());
		// TODO split into others stats
		if (activeChar.isFlying()) {
			val += Config.WYVERN_SPEED;
		} else if(activeChar.isRiding()) {
			val += Config.STRIDER_SPEED;
		}
		return val;
	}

	public final int getShldDef()
	{
		return (int) calcStat(Stats.SHIELD_DEFENCE, 0, null, null);
	}

	public final int getSTR() {
	    return isNull(activeChar) ? 1 : (int) calcStat(Stats.STAT_STR, activeChar.getTemplate().getStrength(), null, null);
	}

	public final int getWalkSpeed() {
		if (isNull(activeChar)) {
			return 1;
		}

		// TODO move to PCStatus;
		if (activeChar instanceof L2PcInstance) {
			return (getRunSpeed() * 70) / 100;
		}
		return (int) calcStat(Stats.WALK_SPEED, activeChar.getTemplate().getWalkSpd(), null, null);
	}

	public final int getWIT() {
	    return isNull(activeChar) ? 1 : (int) calcStat(Stats.STAT_WIT, activeChar.getTemplate().getWitness(), null, null);
	}

	public final int getMpConsume(L2Skill skill) {
		if (isNull(skill)) {
			return 1;
		}

		int mpConsume = skill.getMpConsume();
		if (skill.isDance() && (nonNull(activeChar)) && (activeChar.getDanceCount() > 0)) {
			mpConsume += activeChar.getDanceCount() * skill.getNextDanceMpCost();
		}
		return (int) calcStat(Stats.MP_CONSUME, mpConsume, null, skill);
	}

	public final int getMpInitialConsume(L2Skill skill) {
	    return isNull(skill) ? 1 : (int) calcStat(Stats.MP_CONSUME, skill.getMpInitialConsume(), null, skill);
	}

    protected L2Character getActiveChar()
    {
        return activeChar;
    }

    // TODO move to Characters
    public long getExp()
    {
        return _exp;
    }
    public void setExp(long value)
    {
        _exp = value;
    }
    public int getLevel()
    {
        return _level;
    }
    public void setLevel(int value) {
        _level = value;
    }
    public long getSp()
    {
        return _sp;
    }

    public void setSp(long value)
    {
        _sp = value;
    }
}
