package org.l2j.gameserver.model;

import org.l2j.commons.Config;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.GameTimeController;
import org.l2j.gameserver.GeoData;
import org.l2j.gameserver.Olympiad;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.ai.*;
import org.l2j.gameserver.datatables.DoorTable;
import org.l2j.gameserver.datatables.MapRegionTable;
import org.l2j.gameserver.datatables.MapRegionTable.TeleportWhereType;
import org.l2j.gameserver.datatables.SkillTable;
import org.l2j.gameserver.handler.ISkillHandler;
import org.l2j.gameserver.handler.SkillHandler;
import org.l2j.gameserver.instancemanager.DimensionalRiftManager;
import org.l2j.gameserver.instancemanager.TownManager;
import org.l2j.gameserver.model.L2Skill.SkillTargetType;
import org.l2j.gameserver.model.L2Skill.SkillType;
import org.l2j.gameserver.model.actor.instance.*;
import org.l2j.gameserver.model.actor.instance.L2PcInstance.SkillDat;
import org.l2j.gameserver.model.actor.knownlist.CharKnownList;
import org.l2j.gameserver.model.actor.knownlist.KnownList;
import org.l2j.gameserver.model.actor.knownlist.KnownList.KnownListAsynchronousUpdateTask;
import org.l2j.gameserver.model.actor.stat.CharStat;
import org.l2j.gameserver.model.actor.status.CharStatus;
import org.l2j.gameserver.model.entity.Duel;
import org.l2j.gameserver.model.entity.database.CharTemplate;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.zone.Zone;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.pathfinding.AbstractNodeLoc;
import org.l2j.gameserver.pathfinding.geonodes.GeoPathFinding;
import org.l2j.gameserver.serverpackets.*;
import org.l2j.gameserver.skills.Calculator;
import org.l2j.gameserver.skills.Formulas;
import org.l2j.gameserver.skills.Stats;
import org.l2j.gameserver.skills.effects.EffectCharge;
import org.l2j.gameserver.skills.funcs.Func;
import org.l2j.gameserver.templates.xml.jaxb.CrystalType;
import org.l2j.gameserver.templates.xml.jaxb.ItemType;
import org.l2j.gameserver.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.util.Util.isNullOrEmpty;
import static org.l2j.gameserver.ai.Intention.AI_INTENTION_ATTACK;
import static org.l2j.gameserver.ai.Intention.AI_INTENTION_FOLLOW;

/**
 * Mother class of allTemplates character objects of the world (PC, NPC...)<BR>
 * <BR>
 * L2Character :<BR>
 * <li>L2CastleGuardInstance</li> <li>L2DoorInstance</li> <li>L2NpcInstance</li> <li>L2PlayableInstance</li><BR>
 * <B><U> Concept of L2CharTemplate</U> :</B><BR>
 * Each L2Character owns generic and static properties (ex : allTemplates Keltir have the same number of HP...). All of those properties are stored in a different template for each type of L2Character. Each template is loaded once in the server cache memory (reduce memory use). When a new instance of
 * L2Character is spawned, server just create a link between the instance and the template. This link is stored in <B>template</B><BR>
 *
 */
public abstract class L2Character<T extends CharTemplate> extends L2Object {

    private static final Logger logger = LoggerFactory.getLogger(L2Character.class);
    private static final L2Effect[] EMPTY_EFFECTS = new L2Effect[0];
    public static final int ABNORMAL_EFFECT_BLEEDING = 0x000001;
    public static final int ABNORMAL_EFFECT_POISON = 0x000002;
    public static final int ABNORMAL_EFFECT_UNKNOWN_3 = 0x000004;
    public static final int ABNORMAL_EFFECT_UNKNOWN_4 = 0x000008;
    public static final int ABNORMAL_EFFECT_UNKNOWN_5 = 0x000010;
    public static final int ABNORMAL_EFFECT_UNKNOWN_6 = 0x000020;
    private static final int ABNORMAL_EFFECT_STUN = 0x000040;
    private static final int ABNORMAL_EFFECT_SLEEP = 0x000080;
    private static final int ABNORMAL_EFFECT_MUTED = 0x000100;
    private static final int ABNORMAL_EFFECT_ROOT = 0x000200;
    public static final int ABNORMAL_EFFECT_HOLD_1 = 0x000400;
    public static final int ABNORMAL_EFFECT_HOLD_2 = 0x000800;
    public static final int ABNORMAL_EFFECT_UNKNOWN_13 = 0x001000;
    public static final int ABNORMAL_EFFECT_BIG_HEAD = 0x002000;
    public static final int ABNORMAL_EFFECT_FLAME = 0x004000;
    public static final int ABNORMAL_EFFECT_UNKNOWN_16 = 0x008000;
    public static final int ABNORMAL_EFFECT_GROW = 0x010000;
    public static final int ABNORMAL_EFFECT_FLOATING_ROOT = 0x020000;
    public static final int ABNORMAL_EFFECT_DANCE_STUNNED = 0x040000;
    public static final int ABNORMAL_EFFECT_FIREROOT_STUN = 0x080000;
    public static final int ABNORMAL_EFFECT_STEALTH = 0x100000;
    public static final int ABNORMAL_EFFECT_IMPRISIONING_1 = 0x200000;
    public static final int ABNORMAL_EFFECT_IMPRISIONING_2 = 0x400000;
    public static final int ABNORMAL_EFFECT_MAGIC_CIRCLE = 0x800000;

    // TODO TEMP HACKS (get the proper mask for these effects)
    public static final int ABNORMAL_EFFECT_CONFUSED = 0x0020;
    public static final int ABNORMAL_EFFECT_AFRAID = 0x0010;

    protected T template;
    private List<L2Character> attackByList;
    private CharStat stat;
    private CharStatus status;
    private String title;
    protected Calculator[] calculators;
    protected  Map<Integer, L2Skill> skills;

    private boolean champion; // TODO move to Monster
    private String aiClass = "default";

    private L2Skill lastSkillCast;
    private L2Character lastBuffer;
    private int lastHealAmount;

    private boolean isFakeDeath; // Fake death
    private boolean isKilledAlready;
    private boolean isFlying; // Is flying Wyvern?
    protected boolean isTeleporting;
    private boolean isOverloaded; // the char is carrying too much
    private boolean isRiding; // Is Riding strider?
    private boolean isPendingRevive;
    protected boolean isInvul;

    private boolean isAfraid; // Flee in a random direction
    private boolean isConfused; // Attack anyone randomly
    private boolean isMuted; // Cannot use magic
    private boolean isPhysicallyMuted; // Cannot use physical skills
    private boolean isImmobilized;
    private boolean isRooted; // Cannot move until root timed out
    private boolean isRunning;
    private boolean isSleeping; // Cannot move/attack until sleep timed out or monster is attacked
    private boolean isStunned ; // Cannot move/attack until stun timed out
    private boolean isParalyzed;
    private boolean isBetrayed; // Betrayed by own summon

    private double hpUpdateIncCheck;
    private double hpUpdateDecCheck;
    private double hpUpdateInterval;

    private long zones = 0;
    private int AbnormalEffects;
    private List<L2Effect> effects;
    private Map<String, List<L2Effect>> stackedEffects;

    protected List<Integer> disabledSkills;
    private boolean allSkillsDisabled;
    protected MoveData move;
    private int heading;
    private L2Object target;
    private int castEndTime;
    private int castInterruptTime;
    private int attackEndTime;
    private int _attacking;
    private int _disableBowAttackEndTime;
    protected static final Calculator[] NPC_STD_CALCULATOR;

    static {
        NPC_STD_CALCULATOR = Formulas.getInstance().getStdNPCCalculators();
    }

    protected AI ai;
    protected Future<?> skillCast;
    private int clientX;
    private int clientY;
    private int clientZ;
    private int clientHeading;
    private List<QuestState> NotifyQuestOfDeathList = new LinkedList<>();

    public L2Character(int objectId, T template) {
        super(objectId);
        this.template = template;

        getKnownList();
        initSkillsStat(template);
    }

    @Override
    public KnownList getKnownList() {
        if(isNull(knownList)) {
            knownList = new CharKnownList(this);
        }
        return knownList;
    }

    public int getSkillLevel(int skillId) {
        if (isNull(skills)) {
            return -1;
        }

        var skill = skills.get(skillId);

        if (isNull(skill)) {
            return -1;
        }
        return skill.getLevel();
    }

    public boolean isInsideZone(Zone zone) {
        return ((zones & zone.getId()) != 0);
    }

    public void setInsideZone(Zone zone, boolean state) {
        if (state) {
            zones |= zone.getId();
        } else if (isInsideZone(zone)) {
            zones ^= zone.getId();
        }
    }

    protected void initSkillsStat(CharTemplate template) {
        skills = new ConcurrentHashMap<>();

        // TODO move to Playable If L2Character is a L2PcInstance or a L2Summon, create the basic calculator set
        calculators = new Calculator[Stats.NUM_STATS];
        Formulas.getInstance().addFuncsToNewCharacter(this);
    }

    protected void initCharStatusUpdateValues() {
        hpUpdateInterval = getMaxHp() / 352.0;
        hpUpdateIncCheck = getMaxHp();
        hpUpdateDecCheck = getMaxHp() - hpUpdateInterval;
    }

    public void onDecay() {
        L2WorldRegion reg = getWorldRegion();
        if (nonNull(reg)) {
            reg.removeFromZones(this);
        }
        decayMe();
    }

    @Override
    public void onSpawn() {
        super.onSpawn();
        revalidateZone();
    }

    public void onTeleported() {
        if (!isTeleporting()) {
            return;
        }

        spawnMe(getPosition().getX(), getPosition().getY(), getPosition().getZ());
        setIsTeleporting(false);

        if (isPendingRevive) {
            doRevive();
        }

        if (nonNull(getPet())) {
            getPet().setFollowStatus(false);
            getPet().teleToLocation(getPosition().getX() + Rnd.get(-100, 100), getPosition().getY() + Rnd.get(-100, 100), getPosition().getZ(), false);
            getPet().setFollowStatus(true);
        }

    }

    /**
     * Add L2Character instance that is attacking to the attacker list.<BR>
     * <BR>
     *
     * @param player The L2Character that attcks this one
     * TODO move to Monster
     */
    public void addAttackerToAttackByList(L2Character player) {
        if ((isNull(player)) || (player == this) || (isNull(getAttackByList())) || getAttackByList().contains(player)) {
            return;
        }
        getAttackByList().add(player);
    }

    public final void broadcastPacket(L2GameServerPacket mov) {
        broadcastPacket(mov, 0);
    }

    public final void broadcastPacket(L2GameServerPacket mov, int radiusInKnownlist) {
        if (!(mov instanceof CharInfo)) {
            sendPacket(mov);
        }

        for (L2PcInstance player : getKnownList().getKnownPlayers().values()) {
            try {
                if (radiusInKnownlist != 0 && !isInsideRadius(player, radiusInKnownlist, false, false)) {
                    continue;
                }
                player.sendPacket(mov);
                if ((mov instanceof CharInfo) && (this instanceof L2PcInstance)) {
                    int relation = ((L2PcInstance) this).getRelation(player);
                    if ((getKnownList().getKnownRelations().get(player.getObjectId()) != null) && (getKnownList().getKnownRelations().get(player.getObjectId()) != relation)) {
                        player.sendPacket(new RelationChanged((L2PcInstance) this, relation, player.isAutoAttackable(this)));
                    }
                }
            } catch (NullPointerException e) {
                logger.error(e.getLocalizedMessage(), e);
            }
        }
    }

    protected boolean needHpUpdate(int barPixels) {
        double currentHp = getCurrentHp();

        if ((currentHp <= 1.0) || (getMaxHp() < barPixels)) {
            return true;
        }

        if ((currentHp <= hpUpdateDecCheck) || (currentHp >= hpUpdateIncCheck)) {
            if (currentHp == getMaxHp()) {
                hpUpdateIncCheck = currentHp + 1;
                hpUpdateDecCheck = currentHp - hpUpdateInterval;
            } else {
                double doubleMulti = currentHp / hpUpdateInterval;
                int intMulti = (int) doubleMulti;

                hpUpdateDecCheck = hpUpdateInterval * (doubleMulti < intMulti ? --intMulti : intMulti);
                hpUpdateIncCheck = hpUpdateDecCheck + hpUpdateInterval;
            }
            return true;
        }
        return false;
    }

    public void broadcastStatusUpdate() {
        if (getStatus().getStatusListener().isEmpty()) {
            return;
        }

        if (!needHpUpdate(352)) {
            return;
        }

        logger.debug("Broadcast Status Update for {} ({}). HP: {}", getObjectId(), getName(),  getCurrentHp());

        StatusUpdate su = new StatusUpdate(getObjectId());
        su.addAttribute(StatusUpdate.CUR_HP, (int) getCurrentHp());
        su.addAttribute(StatusUpdate.CUR_MP, (int) getCurrentMp());

        synchronized (getStatus().getStatusListener()) {
            for (L2Character temp : getStatus().getStatusListener()) {
                try {
                    temp.sendPacket(su);
                } catch (NullPointerException e) {
                    logger.error(e.getLocalizedMessage(),  e);
                }
            }
        }
    }

    public void sendPacket(L2GameServerPacket mov) {
        // default implementation
    }

    public void teleToLocation(int x, int y, int z, boolean allowRandomOffset) {
        stopMove(null, false);
        abortAttack();
        abortCast();

        setIsTeleporting(true);
        setTarget(null);

        getWorldRegion().removeFromZones(this);

        getAI().setIntention(Intention.AI_INTENTION_ACTIVE);

        if (Config.RESPAWN_RANDOM_ENABLED && allowRandomOffset) {
            x += Rnd.get(-Config.RESPAWN_RANDOM_MAX_OFFSET, Config.RESPAWN_RANDOM_MAX_OFFSET);
            y += Rnd.get(-Config.RESPAWN_RANDOM_MAX_OFFSET, Config.RESPAWN_RANDOM_MAX_OFFSET);
        }
        z += 5;
        logger.debug("Teleporting to: {}, {}, {}", x, y, z);

        broadcastPacket(new TeleportToLocation(this, x, y, z));
        getPosition().setXYZ(x, y, z);
        decayMe();

        if (!(this instanceof L2PcInstance)) {
            onTeleported(); // TODO why this ?
        }
    }

    public void teleToLocation(int x, int y, int z) {
        teleToLocation(x, y, z, false);
    }

    public void teleToLocation(Location loc, boolean allowRandomOffset) {
        int x = loc.getX();
        int y = loc.getY();
        int z = loc.getZ();

        // TODO move to L2PcInstance
        if ((this instanceof L2PcInstance) && DimensionalRiftManager.getInstance().checkIfInRiftZone(getX(), getY(), getZ(), true)) // true -> ignore waiting room :)
        {
            L2PcInstance player = (L2PcInstance) this;
            player.sendMessage("You have been sent to the waiting room.");
            if (player.isInParty() && player.getParty().isInDimensionalRift()) {
                player.getParty().getDimensionalRift().usedTeleport(player);
            }
            int[] newCoords = DimensionalRiftManager.getInstance().getRoom((byte) 0, (byte) 0).getTeleportCoords();
            x = newCoords[0];
            y = newCoords[1];
            z = newCoords[2];
        }
        teleToLocation(x, y, z, allowRandomOffset);
    }

    public void teleToLocation(TeleportWhereType teleportWhere) {
        teleToLocation(MapRegionTable.getInstance().getTeleToLocation(this, teleportWhere), true);
    }

    protected void doAttack(L2Character target) {
        logger.debug("{} doAttack: target={}", getName(), target);

        if (isAlikeDead() || (isNull(target)) || ((this instanceof L2NpcInstance) && target.isAlikeDead()) || ((this instanceof L2PcInstance) && target.isDead() && !target.isFakeDeath()) || !getKnownList().knowsObject(target) || ((this instanceof L2PcInstance) && isDead()) || ((target instanceof L2PcInstance) && (((L2PcInstance) target).getDuelState() == Duel.DUELSTATE_DEAD))) {
            // If L2PcInstance is dead or the target is dead, the action is stopped
            getAI().setIntention(Intention.AI_INTENTION_ACTIVE);

            sendPacket(new ActionFailed());
            return;
        }

        if (isAttackingDisabled()) {
            return;
        }

        // TODO move to L2PcInstance
        if (this instanceof L2PcInstance) {
            if (((L2PcInstance) this).inObserverMode()) {
                sendPacket(new SystemMessage(SystemMessageId.OBSERVERS_CANNOT_PARTICIPATE));
                sendPacket(new ActionFailed());
                return;
            }

            if (target instanceof L2PcInstance) {
                if (((L2PcInstance) target).isCursedWeaponEquiped() && (this.getLevel() <= 20)) {
                    ((L2PcInstance) this).sendMessage("Can't attack a cursed reader when under level 21.");
                    sendPacket(new ActionFailed());
                    return;
                }

                if (((L2PcInstance) this).isCursedWeaponEquiped() && ( target.getLevel() <= 20)) {
                    ((L2PcInstance) this).sendMessage("Can't attack a newbie reader using a cursed weapon.");
                    sendPacket(new ActionFailed());
                    return;
                }
            }
        }

        L2ItemInstance weapon = getActiveWeaponInstance();

        if ((nonNull(weapon)) && (weapon.getType() == ItemType.FISHINGROD)) {
            this.sendPacket(new SystemMessage(SystemMessageId.CANNOT_ATTACK_WITH_FISHING_POLE));
            getAI().setIntention(Intention.AI_INTENTION_IDLE);

            sendPacket(new ActionFailed());
            return;
        }

        if (!GeoData.getInstance().canSeeTarget(this, target)) {
            sendPacket(new SystemMessage(SystemMessageId.CANT_SEE_TARGET));
            getAI().setIntention(Intention.AI_INTENTION_ACTIVE);
            sendPacket(new ActionFailed());
            return;
        }

        if ((nonNull(weapon) && (weapon.getType() == ItemType.BOW))) {
            // Check for arrows and MP
            // TODO move To L2PcInstance
            if (this instanceof L2PcInstance) {
                // Checking if target has moved to peace zone - only for reader-bow attacks at the moment
                // Other melee is checked in movement code and for offensive spells a check is done every time
                if (target.isInsidePeaceZone((L2PcInstance) this)) {
                    getAI().setIntention(Intention.AI_INTENTION_ACTIVE);
                    sendPacket(new ActionFailed());
                    return;
                }

                // TODO this should be use with any Character
                // Verify if the bow can be use
                if (_disableBowAttackEndTime <= GameTimeController.getGameTicks()) {
                    // Verify if L2PcInstance owns enough MP
                    int saMpConsume = (int) getStat().calcStat(Stats.MP_CONSUME, 0, null, null);
                    int mpConsume = saMpConsume == 0 ? weapon.getMpConsume() : saMpConsume;

                    if (getCurrentMp() < mpConsume) {
                        ThreadPoolManager.getInstance().scheduleAi(new NotifyAITask(Event.EVT_READY_TO_ACT), 1000);

                        sendPacket(new SystemMessage(SystemMessageId.NOT_ENOUGH_MP));
                        sendPacket(new ActionFailed());
                        return;
                    }

                    getStatus().reduceMp(mpConsume);

                    // Set the period of bow non re-use
                    _disableBowAttackEndTime = (5 * GameTimeController.TICKS_PER_SECOND) + GameTimeController.getGameTicks();
                } else {
                    ThreadPoolManager.getInstance().scheduleAi(new NotifyAITask(Event.EVT_READY_TO_ACT), 1000);

                    sendPacket(new ActionFailed());
                    return;
                }

                // Equip arrows needed in left hand and send a Server->Client packet ItemList to the L2PcInstance then return True
                if (!checkAndEquipArrows()) {
                    getAI().setIntention(Intention.AI_INTENTION_IDLE);

                    sendPacket(new ActionFailed());
                    sendPacket(new SystemMessage(SystemMessageId.NOT_ENOUGH_ARROWS));
                    return;
                }
            } else if (this instanceof L2NpcInstance) {
                if (_disableBowAttackEndTime > GameTimeController.getGameTicks()) {
                    return;
                }
            }
        }

        // Add the L2PcInstance to _knownObjects and _knownPlayer of the target
        target.getKnownList().addKnownObject(this);

        // Reduce the current CP if TIREDNESS configuration is activated
        if (Config.ALT_GAME_TIREDNESS) {
            setCurrentCp(getCurrentCp() - 10);
        }

        // TODO move to L2PcInstance
        // Recharge any active auto soulshot tasks for reader (or reader's summon if one exists).
        if (this instanceof L2PcInstance) {
            ((L2PcInstance) this).rechargeAutoSoulShot(true, false, false);
        } else if (this instanceof L2Summon) {
            ((L2Summon) this).getOwner().rechargeAutoSoulShot(true, false, true);
        }

        // Verify if soulshots are charged.
        boolean wasSSCharged;

        // TODO move to L2Summon
        if ((this instanceof L2Summon) && !(this instanceof L2PetInstance)) {
            wasSSCharged = (((L2Summon) this).getChargedSoulShot() != L2ItemInstance.CHARGED_NONE);
        } else {
            wasSSCharged = ((weapon != null) && (weapon.getChargedSoulshot() != L2ItemInstance.CHARGED_NONE));
        }

        // Get the Attack Speed of the L2Character (delay (in milliseconds) before next attack)
        int timeAtk = calculateTimeBetweenAttacks(target, weapon);
        // the hit is calculated to happen halfway to the animation - might need further tuning e.g. in bow case
        int timeToHit = timeAtk / 2;
        attackEndTime = GameTimeController.getGameTicks();
        attackEndTime += (timeAtk / GameTimeController.MILLIS_IN_TICK);
        attackEndTime -= 1;

        CrystalType ssGrade = CrystalType.NONE;

        if (nonNull(weapon)) {
            ssGrade = weapon.getCrystal();
        }

        // Create a Server->Client packet Attack
        Attack attack = new Attack(this, wasSSCharged, ssGrade);

        boolean hitted;

        // Set the Attacking Body part to CHEST
        setAttackingBodypart();

        // Get the Attack Reuse Delay of the Weapon
        int reuse = calculateReuseTime(target, weapon);

        // Select the type of attack to start
        if (isNull(weapon)) {
            hitted = doAttackHitSimple(attack, target, timeToHit);
        } else if (weapon.getType() == ItemType.BOW) {
            hitted = doAttackHitByBow(attack, target, timeAtk, reuse);
        } else if (weapon.getType() == ItemType.POLE) {
            hitted = doAttackHitByPole(attack, timeToHit);
        } else if (isUsingDualWeapon()) {
            hitted = doAttackHitByDual(attack, target, timeToHit);
        } else {
            hitted = doAttackHitSimple(attack, target, timeToHit);
        }

        // TODO move to L2PCInstance
        // Flag the attacker if it's a L2PcInstance outside a PvP area
        L2PcInstance player = null;

        if (this instanceof L2PcInstance) {
            player = (L2PcInstance) this;
        } else if (this instanceof L2Summon) {
            player = ((L2Summon) this).getOwner();
        }

        if (player != null) {
            player.updatePvPStatus(target);
        }

        if (!hitted) {
            abortAttack();
        } else {
            // If we didn't miss the hit, discharge the shoulshots, if any
            if ((this instanceof L2Summon) && !(this instanceof L2PetInstance)) {
                ((L2Summon) this).setChargedSoulShot(L2ItemInstance.CHARGED_NONE);
            } else if (weapon != null) {
                weapon.setChargedSoulshot(L2ItemInstance.CHARGED_NONE);
            }

            if (player != null) {
                if (player.isCursedWeaponEquiped()) {
                    if (!target.isInvul()) {
                        target.setCurrentCp(0);
                    }
                } else if (player.isHero()) {
                    if ((target instanceof L2PcInstance) && ((L2PcInstance) target).isCursedWeaponEquiped()) {
                        target.setCurrentCp(0);
                    }
                }
            }
        }

        if (attack.hasHits()) {
            broadcastPacket(attack);
        }
        ThreadPoolManager.getInstance().scheduleAi(new NotifyAITask(Event.EVT_READY_TO_ACT), timeAtk + reuse);
    }

    private boolean doAttackHitByBow(Attack attack, L2Character target, int timeToHit, int reuse) {
        reduceArrowCount();

        move = null;

        boolean hitted = doHit(attack, target, timeToHit, 1);

        // TODO move to L2PcInstance Check if the L2Character is a L2PcInstance
        if (this instanceof L2PcInstance) {
            sendPacket(new SystemMessage(SystemMessageId.GETTING_READY_TO_SHOOT_AN_ARROW));
            SetupGauge sg = new SetupGauge(SetupGauge.RED, timeToHit + reuse);
            sendPacket(sg);
        }
        _disableBowAttackEndTime = ((timeToHit + reuse) / GameTimeController.MILLIS_IN_TICK) + GameTimeController.getGameTicks();

        return hitted;
    }

    private boolean doAttackHitByDual(Attack attack, L2Character target, int timeToHit) {
        boolean hitted = false;
        for (int i = 0; i < 2; i++) {
            hitted = doHit(attack, target, timeToHit / 2, 0.5f);
        }
        return hitted;
    }

    private boolean doHit(Attack attack, L2Character target, int timeToHit, float damageFactor) {
        int damage = 0;
        boolean shield = false;
        boolean critic = false;
        boolean miss = Formulas.getInstance().calcHitMiss(this, target);
        if (!miss) {
            shield = Formulas.getInstance().calcShldUse(this, target);
            critic = Formulas.getInstance().calcCrit(getStat().getCriticalHit(target, null));
            damage = (int) Formulas.getInstance().calcPhysDam(this, target, null, shield, critic, true, attack.soulshot);
            damage *= damageFactor;
        }

        ThreadPoolManager.getInstance().scheduleAi(new HitTask(target, damage, critic, miss, attack.soulshot, shield), timeToHit);
        attack.addHit(target, damage, miss, critic, shield);
        return !miss;
    }

    private boolean doAttackHitByPole(Attack attack, int sAtk) {

        double angleChar, angleTarget;
        int maxRadius = (int) getStat().calcStat(Stats.PHYSIC_ATTACK_RANGE, 66, null, null);
        int maxAngleDiff = (int) getStat().calcStat(Stats.PHYSIC_ATTACK_ANGLE, 120, null, null);

        if (isNull(getTarget())) {
            return false;
        }

        logger.info("doAttackHitByPole: Max radius = {}, angle = {}", maxRadius, maxAngleDiff);

        // TODO review
        angleTarget = Util.calculateAngleFrom(this, getTarget());
        setHeading((int) ((angleTarget / 9.0) * 1610.0)); // angleTarget / 360.0 * 64400.0;

        // Update char's heading degree
        angleChar = Util.convertHeadingToDegree(getHeading());
        float attackpercent = 85;
        int attackcountmax = (int) getStat().calcStat(Stats.ATTACK_COUNT_MAX, 3, null, null);
        int attackcount = 0;

        if (angleChar <= 0) {
            angleChar += 360;
        }

        L2Character target;
        boolean hitted = false;
        for (L2Object obj : getKnownList().getKnownObjects().values()) {
            // Check if the L2Object is a L2Character
            if (obj instanceof L2Character) {
                if ((obj instanceof L2PetInstance) && (this instanceof L2PcInstance) && (((L2PetInstance) obj).getOwner() ==  this)) {
                    continue;
                }

                if (!Util.checkIfInRange(maxRadius, this, obj, false)) {
                    continue;
                }

                // otherwise hit too high/low. 650 because mob z coord sometimes wrong on hills
                if (Math.abs(obj.getZ() - getZ()) > 650) {
                    continue;
                }
                angleTarget = Util.calculateAngleFrom(this, obj);
                // TODO is those angles always the same value ?
                if ((Math.abs(angleChar - angleTarget) > maxAngleDiff) && (Math.abs((angleChar + 360) - angleTarget) > maxAngleDiff) && // Example: char is at 1 degree and target is at 359 degree
                        (Math.abs(angleChar - (angleTarget + 360)) > maxAngleDiff // Example: target is at 1 degree and char is at 359 degree
                        )) {
                    continue;
                }

                target = (L2Character) obj;

                // Launch a simple attack against the L2Character targeted
                if (!target.isAlikeDead()) {
                    attackcount += 1;
                    if (attackcount <= attackcountmax) {
                        if ((target == getAI().getAttackTarget()) || target.isAutoAttackable(this)) {

                            hitted |= doAttackHitSimple(attack, target, attackpercent, sAtk);
                            attackpercent /= 1.15;
                        }
                    }
                }
            }
        }
        return hitted;
    }

    private boolean doAttackHitSimple(Attack attack, L2Character target, int sAtk) {
        return doAttackHitSimple(attack, target, 100, sAtk);
    }

    private boolean doAttackHitSimple(Attack attack, L2Character target, float attackPercent, int sAtk) {
        return doHit(attack, target, sAtk, attackPercent / 100.0f);
    }

    public void doCast(L2Skill skill) {
        if (isNull(skill)) {
            getAI().notifyEvent(Event.EVT_CANCEL);
            return;
        }

        if (isSkillDisabled(skill.getId())) {
            if (this instanceof L2PcInstance) {
                SystemMessage sm = new SystemMessage(SystemMessageId.S1_PREPARED_FOR_REUSE);
                sm.addSkillName(skill.getId(), skill.getLevel());
                sendPacket(sm);
            }
            return;
        }

        if (skill.isMagic() && isMuted() && !skill.isPotion()) {
            getAI().notifyEvent(Event.EVT_CANCEL);
            return;
        }

        if (!skill.isMagic() && isPsychicalMuted() && !skill.isPotion()) {
            getAI().notifyEvent(Event.EVT_CANCEL);
            return;
        }

        // TODO move to L2PcInstance
        if ((this instanceof L2PcInstance) && ((L2PcInstance) this).isInOlympiadMode() && (skill.isHeroSkill() || (skill.getSkillType() == SkillType.RESURRECT))) {
            SystemMessage sm = new SystemMessage(SystemMessageId.THIS_SKILL_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT);
            sendPacket(sm);
            return;
        }

        // Recharge AutoSoulShot
        // TODO move to L2Playable
        if (skill.useSoulShot()) {
            if (this instanceof L2PcInstance) {
                ((L2PcInstance) this).rechargeAutoSoulShot(true, false, false);
            } else if (this instanceof L2Summon) {
                ((L2Summon) this).getOwner().rechargeAutoSoulShot(true, false, true);
            }
        } else if (skill.useSpiritShot()) {
            if (this instanceof L2PcInstance) {
                ((L2PcInstance) this).rechargeAutoSoulShot(false, true, false);
            } else if (this instanceof L2Summon) {
                ((L2Summon) this).getOwner().rechargeAutoSoulShot(false, true, true);
            }
        }

        L2Object[] targets = skill.getTargetList(this);

        if ((isNull(targets)) || (targets.length == 0)) {
            getAI().notifyEvent(Event.EVT_CANCEL);
            return;
        }

        L2Character target;

        if ((skill.getSkillType() == SkillType.BUFF) || (skill.getSkillType() == SkillType.HEAL) || (skill.getSkillType() == SkillType.COMBATPOINTHEAL) ||
                (skill.getSkillType() == SkillType.MANAHEAL) || (skill.getSkillType() == SkillType.REFLECT) || (skill.getSkillType() == SkillType.SEED) ||
                (skill.getTargetType() == L2Skill.SkillTargetType.TARGET_SELF) || (skill.getTargetType() == L2Skill.SkillTargetType.TARGET_PET) ||
                (skill.getTargetType() == L2Skill.SkillTargetType.TARGET_PARTY) || (skill.getTargetType() == L2Skill.SkillTargetType.TARGET_CLAN) ||
                (skill.getTargetType() == L2Skill.SkillTargetType.TARGET_ALLY)) {
            target = (L2Character) targets[0];

            // TODO move to L2PcInstance
            if ((this instanceof L2PcInstance) && (target instanceof L2PcInstance) && (target.getAI().getIntention() == Intention.AI_INTENTION_ATTACK)) {
                if ((skill.getSkillType() == SkillType.BUFF) || (skill.getSkillType() == SkillType.HOT) || (skill.getSkillType() == SkillType.HEAL) ||
                        (skill.getSkillType() == SkillType.HEAL_PERCENT) || (skill.getSkillType() == SkillType.MANAHEAL) || (skill.getSkillType() == SkillType.MANAHEAL_PERCENT)
                        || (skill.getSkillType() == SkillType.BALANCE_LIFE)) {
                    target.setLastBuffer(this);
                }

                if ((this.isInParty() && (skill.getTargetType() == L2Skill.SkillTargetType.TARGET_PARTY))) {
                    for (L2PcInstance member :  this.getParty().getPartyMembers()) {
                        member.setLastBuffer(this);
                    }
                }
            }
        } else {
            target = (L2Character) getTarget();
        }

        // AURA skills should always be using caster as target
        if (skill.getTargetType() == SkillTargetType.TARGET_AURA) {
            target = this;
        }

        if (isNull(target)) {
            getAI().notifyEvent(Event.EVT_CANCEL);
            return;
        }

        setLastSkillCast(skill);

        int magicId = skill.getId();
        int displayId = skill.getDisplayId();
        int level = skill.getLevel();

        if (level < 1) {
            level = 1;
        }

        int hitTime = skill.getHitTime();
        int coolTime = skill.getCoolTime();

        boolean forceBuff = (skill.getSkillType() == SkillType.FORCE_BUFF) && (target instanceof L2PcInstance);

        // Calculate the casting time of the skill (base + modifier of MAtkSpd)
        // Don't modify the skill time for FORCE_BUFF skills. The skill time for those skills represent the buff time.
        if (!forceBuff) {
            hitTime = Formulas.getInstance().calcMAtkSpd(this, skill, hitTime);
            if (coolTime > 0) {
                coolTime = Formulas.getInstance().calcMAtkSpd(this, skill, coolTime);
            }
        }

        // Calculate altered Cast Speed due to BSpS/SpS
        L2ItemInstance weaponInst = getActiveWeaponInstance();

        if ((nonNull(weaponInst)) && skill.isMagic() && !forceBuff && (skill.getTargetType() != SkillTargetType.TARGET_SELF)) {
            if ((weaponInst.getChargedSpiritshot() == L2ItemInstance.CHARGED_BLESSED_SPIRITSHOT) || (weaponInst.getChargedSpiritshot() == L2ItemInstance.CHARGED_SPIRITSHOT)) {
                // Only takes 70% of the time to cast a BSpS/SpS cast
                hitTime = (int) (0.70 * hitTime);
                coolTime = (int) (0.70 * coolTime);

                // Because the following are magic skills that do not actively 'eat' BSpS/SpS,
                // I must 'eat' them here so players don't take advantage of infinite speed increase
                if ((skill.getSkillType() == SkillType.BUFF) || (skill.getSkillType() == SkillType.MANAHEAL) || (skill.getSkillType() == SkillType.RESURRECT) || (skill.getSkillType() == SkillType.RECALL) ||
                        // (skill.getSkillType() == SkillType.POISON)||
                        // (skill.getSkillType() == SkillType.CANCEL)||
                        // (skill.getSkillType() == SkillType.DEBUFF)||
                        // (skill.getSkillType() == SkillType.PARALYZE)||
                        // (skill.getSkillType() == SkillType.ROOT)||
                        // (skill.getSkillType() == SkillType.SLEEP)||
                        (skill.getSkillType() == SkillType.DOT)) {
                    weaponInst.setChargedSpiritshot(L2ItemInstance.CHARGED_NONE);
                }
            }
        }

        // Set the castEndTime and _castInterruptTim. +10 ticks for lag situations, will be reseted in onMagicFinalizer
        castEndTime = 10 + GameTimeController.getGameTicks() + ((coolTime + hitTime) / GameTimeController.MILLIS_IN_TICK);
        castInterruptTime = -2 + GameTimeController.getGameTicks() + (hitTime / GameTimeController.MILLIS_IN_TICK);

        int reuseDelay = (int) (skill.getReuseDelay() * getStat().getMReuseRate(skill));
        reuseDelay *= 333.0 / (skill.isMagic() ? getMAtkSpd() : getPAtkSpd());

        // Send a Server->Client packet MagicSkillUser with target, displayId, level, skillTime, reuseDelay
        // to the L2Character AND to allTemplates L2PcInstance in the _KnownPlayers of the L2Character
        broadcastPacket(new MagicSkillUser(this, target, displayId, level, hitTime, reuseDelay));

        // Send a system message USE_S1 to the L2Character
        // TODO move to L2PcInstance
        if ((this instanceof L2PcInstance) && (magicId != 1312)) {
            SystemMessage sm = new SystemMessage(SystemMessageId.USE_S1);
            sm.addSkillName(magicId, skill.getLevel());
            sendPacket(sm);
        }

        if (reuseDelay > 30000) {
            addTimeStamp(skill.getId(), reuseDelay);
        }

        int initmpcons = getStat().getMpInitialConsume(skill);
        if (initmpcons > 0) {
            StatusUpdate su = new StatusUpdate(getObjectId());
            getStatus().reduceMp(calcStat(Stats.MP_CONSUME_RATE, initmpcons, null, null));
            su.addAttribute(StatusUpdate.CUR_MP, (int) getCurrentMp());
            sendPacket(su);
        }

        if (reuseDelay > 10) {
            disableSkill(skill.getId(), reuseDelay);
        }

        if (forceBuff) {
            startForceBuff(target, skill);
        }

        if (hitTime > 210) {
            // TODO move to L2PcInstance
            if ((this instanceof L2PcInstance) && !forceBuff) {
                SetupGauge sg = new SetupGauge(SetupGauge.BLUE, hitTime);
                sendPacket(sg);
            }

            // Disable allTemplates skills during the casting
            disableAllSkills();

            if (nonNull(skillCast)) {
                skillCast.cancel(true);
                skillCast = null;
            }

            // Create a task MagicUseTask to launch the MagicSkill at the end of the casting time (hitTime)
            // For client animation reasons (party buffs especially) 200 ms before!
            if (nonNull(getForceBuff())) {
                skillCast = ThreadPoolManager.getInstance().scheduleEffect(new MagicUseTask(targets, skill, coolTime, 2), hitTime);
            } else {
                skillCast = ThreadPoolManager.getInstance().scheduleEffect(new MagicUseTask(targets, skill, coolTime, 1), hitTime - 200);
            }
        } else {
            onMagicLaunchedTimer(targets, skill, coolTime, true);
        }
    }

    public void addTimeStamp(int s, int r) {
        /***/
    }

    public void removeTimeStamp(int s) {
    }

    public void startForceBuff(L2Character caster, L2Skill skill) {
        /***/
    }

    public boolean doDie(L2Character killer) {
        synchronized (this) {
            if (isKilledAlready()) {
                return false;
            }
            setIsKilledAlready(true);
        }
        setTarget(null);
        stopMove(null);
        getStatus().stopHpMpRegeneration();

        // TODO  Move to Playable
        if ((this instanceof L2PlayableInstance) && ((L2PlayableInstance) this).isNoblesseBlessed()) {
            ((L2PlayableInstance) this).stopNoblesseBlessing(null);
            if (((L2PlayableInstance) this).getCharmOfLuck()) {
                ((L2PlayableInstance) this).stopCharmOfLuck(null);
            }
        } else {
            stopAllEffects();
        }

        calculateRewards(killer);

        broadcastStatusUpdate();

        getAI().notifyEvent(Event.EVT_DEAD, null);

        if (nonNull(getWorldRegion())) {
            getWorldRegion().onDeath(this);
        }

        for (QuestState qs : getNotifyQuestOfDeath()) {
            qs.getQuest().notifyDeath((isNull(killer) ? this : killer), this, qs);
        }
        getNotifyQuestOfDeath().clear();

        getAttackByList().clear();
        return true;
    }

    protected void calculateRewards(L2Character killer) {
    }

    public void doRevive() {
        if (!isTeleporting()) {
            setIsPendingRevive(false);

            status.setCurrentCp(getMaxCp() * Config.RESPAWN_RESTORE_CP);
            status.setCurrentHp(getMaxHp() * Config.RESPAWN_RESTORE_HP);

            broadcastPacket(new Revive(this));
            if (nonNull(getWorldRegion())) {
                getWorldRegion().onRevive(this);
            }
        } else {
            setIsPendingRevive(true);
        }
    }

    public void doRevive(double revivePower) {
        doRevive();
    }

    protected void useMagic(L2Skill skill) {
        if ((isNull(skill)) || isDead()) {
            return;
        }

        if (isAllSkillsDisabled()) {
            // TODO must be checked by caller
            return;
        }

        if (skill.isPassive()) {
            return;
        }

        L2Object target;

        switch (skill.getTargetType()) {
            case TARGET_AURA: // AURA, SELF should be cast even if no target has been found
            case TARGET_SELF:
                target = this;
                break;
            default:
                target = skill.getFirstOfTargetList(this);
                break;
        }
        getAI().setIntention(Intention.AI_INTENTION_CAST, skill, target);
    }

    public synchronized AI getAI() {
        if (isNull(ai)) {
            ai = new L2CharacterAI<>(new AIAccessor());
        }
        return ai;
    }

    public void setAI(AI newAI) {
        AI oldAI = getAI();
        // TODO move to L2AttacableAI
        if ((oldAI instanceof L2AttackableAI) && (oldAI != newAI)) {
            ((L2AttackableAI) oldAI).stopAITask(false);
        }
        ai = newAI;
    }

    public boolean hasAI() {
        return nonNull(ai);
    }

    public boolean isRaid() {
        return false;
    }

    public final List<L2Character> getAttackByList() {
        if (isNull(attackByList)) {
            attackByList = new LinkedList<>();
        }
        return attackByList;
    }

    private L2Skill getLastSkillCast() {
        return lastSkillCast;
    }

    private void setLastSkillCast(L2Skill skill) {
        lastSkillCast = skill;
    }

    public final boolean isAfraid() {
        return isAfraid;
    }

    private void setIsAfraid(boolean value) {
        isAfraid = value;
    }

    public final boolean isAlikeDead() {
        return isFakeDeath() || !(getCurrentHp() > 0.5);
    }

    public final boolean isAllSkillsDisabled() {
        return allSkillsDisabled || isStunned() || isSleeping() || isParalyzed();
    }

    public boolean isAttackingDisabled() {
        return isStunned() || isSleeping() || (attackEndTime > GameTimeController.getGameTicks()) || isFakeDeath() || isParalyzed();
    }

    public final Calculator[] getCalculators() {
        return calculators;
    }

    public final boolean isConfused() {
        return isConfused;
    }

    private void setIsConfused(boolean value) {
        isConfused = value;
    }

    public final boolean isDead() {
        return !(isFakeDeath()) && !(getCurrentHp() > 0.5);
    }

    public final boolean isFakeDeath() {
        return isFakeDeath;
    }

    public final void setIsFakeDeath(boolean value) {
        isFakeDeath = value;
    }

    public final boolean isFlying() {
        return isFlying;
    }

    public final void setIsFlying(boolean mode) {
        isFlying = mode;
    }

    public boolean isImobilised() {
        return isImmobilized;
    }

    public void setIsImobilised(boolean value) {
        isImmobilized = value;
    }

    private  boolean isKilledAlready() {
        return isKilledAlready;
    }

    public final void setIsKilledAlready(boolean value) {
        isKilledAlready = value;
    }

    public final boolean isMuted() {
        return isMuted;
    }

    private  void setIsMuted(boolean value) {
        isMuted = value;
    }

    private  boolean isPsychicalMuted() {
        return isPhysicallyMuted;
    }

    private void setIsPsychicalMuted(boolean value) {
        isPhysicallyMuted = value;
    }

    public boolean isMovementDisabled() {
        return isStunned() || isRooted() || isSleeping() || isOverloaded() || isParalyzed() || isImobilised() || isFakeDeath();
    }

    public final boolean isOutOfControl() {
        return isConfused() || isAfraid();
    }

    private boolean isOverloaded() {
        return isOverloaded;
    }

    public final void setIsOverloaded(boolean value) {
        isOverloaded = value;
    }

    public final boolean isParalyzed() {
        return isParalyzed;
    }

    public final void setIsParalyzed(boolean value) {
        isParalyzed = value;
    }

    public final boolean isPendingRevive() {
        return isDead() && isPendingRevive;
    }

    public final void setIsPendingRevive(boolean value) {
        isPendingRevive = value;
    }

    public L2Summon getPet() {
        return null;
    }

    public final boolean isRiding() {
        return isRiding;
    }

    public final void setIsRiding(boolean mode) {
        isRiding = mode;
    }

    public final boolean isRooted() {
        return isRooted;
    }

    private void setIsRooted(boolean value) {
        isRooted = value;
    }

    public final void setIsRunning(boolean value) {
        isRunning = value;
        broadcastPacket(new ChangeMoveType(this));
    }

    public final void setRunning() {
        if (!isRunning()) {
            setIsRunning(true);
        }
    }

    public final boolean isSleeping() {
        return isSleeping;
    }

    private void setIsSleeping(boolean value) {
        isSleeping = value;
    }

    public final boolean isStunned() {
        return isStunned;
    }

    private  void setIsStunned(boolean value) {
        isStunned = value;
    }

    public final boolean isBetrayed() {
        return isBetrayed;
    }

    public final void setIsBetrayed(boolean value) {
        isBetrayed = value;
    }

    public final boolean isTeleporting() {
        return isTeleporting;
    }

    public final void setIsTeleporting(boolean value) {
        isTeleporting = value;
    }

    public void setIsInvul(boolean b) {
        isInvul = b;
    }

    public boolean isInvul() {
        return isInvul || isTeleporting;
    }

    public boolean isUndead() {
        return false;
    }

    public CharStat getStat() {
        if (isNull(stat)) {
            stat = new CharStat(this);
        }
        return stat;
    }

    public final void setStat(CharStat value) {
        stat = value;
    }

    public CharStatus getStatus() {
        if (isNull(status)) {
            status = new CharStatus(this);
        }
        return status;
    }

    public final void setStatus(CharStatus value) {
        status = value;
    }

    public final void setTitle(String value) {
        title = value;
    }

    public final void setWalking() {
        if (isRunning()) {
            setIsRunning(false);
        }
    }

    public float getCollisionRadius() {
        return template.getCollisionRadius();
    }

    public int getTemplateId() {
        return template.getId();
    }

    public float getCollisionHeight() {
        return template.getCollisionHeight();
    }

    public double getBasePAtkSpd() {
        return template.getPAtkSpd();
    }

    public double getBaseConstitution() {
        return template.getConstitution();
    }

    public double getBaseCritRate() {
        return template.getCritRate();
    }

    public double getBaseDexterity() {
        return template.getDexterity();
    }

    public double getBaseIntelligence() {
        return template.getIntelligence();
    }

    public int getBaseAtkRange() {
        return template.getAtkRange();
    }

    public double getBaseCp() {
        return template.getCp();
    }

    public double getBaseHp() {
        return template.getHp();
    }

    public double getBaseMp() {
        return template.getMp();
    }

    public double getBaseCpRegen() {
        return template.getHpRegen();
    }

    public double getBaseMAtk() {
        return template.getMAtk();
    }

    public double getAggression() {
        return template.getAggression();
    }

    public double getBleed() {
        return template.getBleed();
    }

    public double getPoison() {
        return template.getPoison();
    }

    public double getStun() {
        return template.getStun();
    }

    public double getRoot() {
        return template.getRoot();
    }

    public double getMovement() {
        return template.getMovement();
    }

    public double getConfusion() {
        return template.getConfusion();
    }

    public double getSleep() {
        return template.getSleep();
    }

    public double getFire() {
        return template.getFire();
    }

    public double getWind() {
        return template.getWind();
    }

    public double getWater() {
        return template.getWater();
    }

    public double getEarth() {
        return template.getEarth();
    }

    public double getHoly() {
        return template.getHoly();
    }

    public double getDark() {
        return template.getDark();
    }

    public double getBaseMAtkSpd() {
        return template.getMAtkSpd();
    }

    public double getBaseMDef() {
        return template.getMDef();
    }

    public double getBaseMentality() {
        return template.getMentality();
    }

    public float getBaseRunSpd() {
        return template.getRunSpd();
    }

    public double getBaseMReuseRate() {
        return template.getMReuseRate();
    }

    public double getBasepAtk() {
        return template.getpAtk();
    }

    public int getBasePDef() {
        return template.getpDef();
    }

    public double getBaseStrength() {
        return template.getStrength();
    }

    public double getBaseWalkSpd() {
        return template.getWalkSpd();
    }

    public double getBaseWitness() {
        return template.getWitness();
    }

    public int getClassLevel() {
        return 0;
    }

    public double getBaseHpRegen() {
        return template.getHpRegen();
    }

    public double getBaseMpRegen() {
        return template.getMpRegen();
    }

    public double getAggressionVuln() {
        return template.getAggressionVuln();
    }

    public double getBleedVuln() {
        return template.getBleedVuln();
    }

    public double getPoisonVuln() {
        return template.getPoisonVuln();
    }

    public double getStunVuln() {
        return template.getStunVuln();
    }

    public double getRootVuln() {
        return template.getRootVuln();
    }

    public double getMovementVuln() {
        return template.getMovementVuln();
    }

    public double getConfusionVuln() {
        return template.getConfusionVuln();
    }

    public double getSleepVuln() {
        return template.getSleepVuln();
    }

    class EnableSkill implements Runnable {

        int _skillId;

        EnableSkill(int skillId) {
            _skillId = skillId;
        }

        @Override
        public void run() {
            try {
                enableSkill(_skillId);
            } catch (Throwable e) {
                logger.error( "", e);
            }
        }
    }


    class HitTask implements Runnable {

        L2Character _hitTarget;
        int _damage;
        boolean _crit;
        boolean _miss;
        boolean _shld;
        boolean _soulshot;

        HitTask(L2Character target, int damage, boolean crit, boolean miss, boolean soulshot, boolean shld) {
            _hitTarget = target;
            _damage = damage;
            _crit = crit;
            _shld = shld;
            _miss = miss;
            _soulshot = soulshot;
        }

        @Override
        public void run() {
            try {
                onHitTimer(_hitTarget, _damage, _crit, _miss, _soulshot, _shld);
            } catch (Throwable e) {
                logger.error(e.getLocalizedMessage(), e);
            }
        }
    }

    class MagicUseTask implements Runnable {

        L2Object[] _targets;
        L2Skill _skill;
        int _coolTime;
        int _phase;

        MagicUseTask(L2Object[] targets, L2Skill skill, int coolTime, int phase) {
            _targets = targets;
            _skill = skill;
            _coolTime = coolTime;
            _phase = phase;
        }

        @Override
        public void run() {
            try {
                switch (_phase) {
                    case 1:
                        onMagicLaunchedTimer(_targets, _skill, _coolTime, false);
                        break;
                    case 2:
                        onMagicHitTimer(_targets, _skill, _coolTime, false);
                        break;
                    case 3:
                        onMagicFinalizer(_targets, _skill);
                        break;
                    default:
                        break;
                }
            } catch (Throwable e) {
                logger.error(e.getLocalizedMessage(), e);
                enableAllSkills();
            }
        }
    }

    class QueuedMagicUseTask implements Runnable {

        L2PcInstance _currPlayer;
        L2Skill _queuedSkill;
        boolean _isCtrlPressed;
        boolean _isShiftPressed;

        QueuedMagicUseTask(L2PcInstance currPlayer, L2Skill queuedSkill, boolean isCtrlPressed, boolean isShiftPressed) {
            _currPlayer = currPlayer;
            _queuedSkill = queuedSkill;
            _isCtrlPressed = isCtrlPressed;
            _isShiftPressed = isShiftPressed;
        }

        @Override
        public void run() {
            try {
                _currPlayer.useMagic(_queuedSkill, _isCtrlPressed, _isShiftPressed);
            } catch (Throwable e) {
                logger.error(e.getLocalizedMessage(), e);
            }
        }
    }

    public class NotifyAITask implements Runnable {

        private final Event _evt;

        NotifyAITask(Event evt) {
            _evt = evt;
        }

        @Override
        public void run() {
            try {
                getAI().notifyEvent(_evt, null);
            } catch (Throwable t) {
                logger.warn(t.getLocalizedMessage(), t);
            }
        }
    }

    class PvPFlag implements Runnable {

        @Override
        public void run() {
            try {
                if (System.currentTimeMillis() > getPvpFlagLasts()) {
                    stopPvPFlag();
                } else if (System.currentTimeMillis() > (getPvpFlagLasts() - 5000)) {
                    updatePvPFlag(2);
                } else {
                    updatePvPFlag(1);
                }
            } catch (Exception e) {
                logger.warn("error in pvp flag task:", e);
            }
        }
    }


    final void addEffect(L2Effect newEffect) {
        if (isNull(newEffect)) {
            return;
        }

        synchronized (this) {
            if (isNull(effects)) {
                effects = new LinkedList<>();
            }

            if (isNull(stackedEffects)) {
                stackedEffects = new HashMap<>();
            }
        }
        synchronized (effects) {
            L2Effect tempEffect = null;

            for (int i = 0; i < effects.size(); i++) {
                if ((effects.get(i).getSkill().getId() == newEffect.getSkill().getId()) && (effects.get(i).getEffectType() == newEffect.getEffectType())) {
                    newEffect.stopEffectTask();
                    return;
                }
            }

            // Remove first Buff if number of buffs > 19
            L2Skill tempskill = newEffect.getSkill();
            if ((getBuffCount() > Config.BUFFS_MAX_AMOUNT) && !doesStack(tempskill) && (((tempskill.getSkillType() == L2Skill.SkillType.BUFF) ||
                    (tempskill.getSkillType() == L2Skill.SkillType.DEBUFF) || (tempskill.getSkillType() == L2Skill.SkillType.REFLECT) ||
                    (tempskill.getSkillType() == L2Skill.SkillType.HEAL_PERCENT) || (tempskill.getSkillType() == L2Skill.SkillType.MANAHEAL_PERCENT))
                    && !((tempskill.getId() > 4360) && (tempskill.getId() < 4367)))) {
                removeFirstBuff(tempskill.getId());
            }

            // Add the L2Effect to allTemplates effect in progress on the L2Character
            if (!newEffect.getSkill().isToggle()) {
                int pos = 0;
                for (int i = 0; i < effects.size(); i++) {
                    if (effects.get(i) != null) {
                        int skillid = effects.get(i).getSkill().getId();
                        if (!effects.get(i).getSkill().isToggle() && (!((skillid > 4360) && (skillid < 4367)))) {
                            pos++;
                        }
                    } else {
                        break;
                    }
                }
                effects.add(pos, newEffect);
            } else {
                effects.add(newEffect);
            }

            if (newEffect.getStackType().equals("none")) {
                newEffect.setInUse(true);
                addStatFuncs(newEffect.getStatFuncs());
                updateEffectIcons();
                return;
            }

            List<L2Effect> stackQueue = stackedEffects.get(newEffect.getStackType());

            if (isNull(stackQueue)) {
                stackQueue = new LinkedList<>();
            }

            if (stackQueue.size() > 0) {
                for (int i = 0; i < effects.size(); i++) {
                    if (effects.get(i) == stackQueue.get(0)) {
                        tempEffect = effects.get(i);
                        break;
                    }
                }

                if (nonNull(tempEffect)) {
                    removeStatsOwner(tempEffect);
                    tempEffect.setInUse(false);
                }
            }

            stackQueue = effectQueueInsert(newEffect, stackQueue);

            if (isNull(stackQueue)) {
                return;
            }

            stackedEffects.put(newEffect.getStackType(), stackQueue);

            tempEffect = null;
            for (int i = 0; i < effects.size(); i++) {
                if (effects.get(i) == stackQueue.get(0)) {
                    tempEffect = effects.get(i);
                    break;
                }
            }
            tempEffect.setInUse(true);

            // Add allTemplates Func objects corresponding to this stacked effect to the Calculator set of the L2Character
            addStatFuncs(tempEffect.getStatFuncs());
        }
        // Update active skills in progress (In Use and Not In Use because stacked) icones on client
        updateEffectIcons();
    }


    private List<L2Effect> effectQueueInsert(L2Effect newStackedEffect, List<L2Effect> stackQueue) {
        if (isNull(effects)) {
            return null;
        }

        Iterator<L2Effect> queueIterator = stackQueue.iterator();

        int i = 0;
        while (queueIterator.hasNext()) {
            L2Effect cur = queueIterator.next();
            if (newStackedEffect.getStackOrder() < cur.getStackOrder()) {
                i++;
            } else {
                break;
            }
        }

        stackQueue.add(i, newStackedEffect);

        if (Config.EFFECT_CANCELING && !newStackedEffect.isHerbEffect() && (stackQueue.size() > 1)) {
            // only keep the current effect, cancel other effects
            for (int n = 0; n < effects.size(); n++) {
                if (effects.get(n) == stackQueue.get(1)) {
                    effects.remove(n);
                    break;
                }
            }
            stackQueue.remove(1);
        }
        return stackQueue;
    }

    public final void removeEffect(L2Effect effect) {
        if (isNull(effect ) || isNull(effects)) {
            return;
        }

        synchronized (effects) {

            if (effect.getStackType() == "none") {
                removeStatsOwner(effect);
            } else {
                if (stackedEffects == null) {
                    return;
                }

                List<L2Effect> stackQueue = stackedEffects.get(effect.getStackType());

                if (isNullOrEmpty(stackQueue)) {
                    return;
                }

                L2Effect frontEffect = stackQueue.get(0);
                boolean removed = stackQueue.remove(effect);

                if (removed) {
                    if (frontEffect == effect) {
                        removeStatsOwner(effect);
                        if (!stackQueue.isEmpty()) {
                            for (int i = 0; i < effects.size(); i++) {
                                if (effects.get(i) == stackQueue.get(0)) {
                                    // Add its list of Funcs to the Calculator set of the L2Character
                                    addStatFuncs(effects.get(i).getStatFuncs());
                                    effects.get(i).setInUse(true);
                                    break;
                                }
                            }
                        }
                    }
                    if (stackQueue.isEmpty()) {
                        stackedEffects.remove(effect.getStackType());
                    } else {
                        stackedEffects.put(effect.getStackType(), stackQueue);
                    }
                }
            }
            for (int i = 0; i < effects.size(); i++) {
                if (effects.get(i) == effect) {
                    effects.remove(i);
                    break;
                }
            }

        }
        updateEffectIcons();
    }

    public final void startAbnormalEffect(int mask) {
        AbnormalEffects |= mask;
        updateAbnormalEffect();
    }

    public final void startConfused() {
        setIsConfused(true);
        getAI().notifyEvent(Event.EVT_CONFUSED);
        updateAbnormalEffect();
    }


    public final void startFakeDeath() {
        setIsFakeDeath(true);
        abortAttack();
        abortCast();
        getAI().notifyEvent(Event.EVT_FAKE_DEATH, null);
        broadcastPacket(new ChangeWaitType(this, ChangeWaitType.WT_START_FAKEDEATH));
    }

    public final void startFear() {
        setIsAfraid(true);
        getAI().notifyEvent(Event.EVT_AFFRAID);
        updateAbnormalEffect();
    }

    public final void startMuted() {
        setIsMuted(true);
        abortCast();
        getAI().notifyEvent(Event.EVT_MUTED);
        updateAbnormalEffect();
    }

    public final void startPsychicalMuted() {
        setIsPsychicalMuted(true);
        getAI().notifyEvent(Event.EVT_MUTED);
        updateAbnormalEffect();
    }

    public final void startRooted() {
        setIsRooted(true);
        getAI().notifyEvent(Event.EVT_ROOTED, null);
        updateAbnormalEffect();
    }

    public final void startSleeping() {
        setIsSleeping(true);
        abortAttack();
        abortCast();
        getAI().notifyEvent(Event.EVT_SLEEPING, null);
        updateAbnormalEffect();
    }

    public final void startStunning() {
        setIsStunned(true);
        abortAttack();
        abortCast();
        getAI().notifyEvent(Event.EVT_STUNNED, null);
        updateAbnormalEffect();
    }

    public final void startBetray() {
        setIsBetrayed(true);
        getAI().notifyEvent(Event.EVT_BETRAYED, null);
        updateAbnormalEffect();
    }

    public final void stopBetray() {
        stopEffects(L2Effect.EffectType.BETRAY);
        setIsBetrayed(false);
        updateAbnormalEffect();
    }

    public final void stopAbnormalEffect(int mask) {
        AbnormalEffects &= ~mask;
        updateAbnormalEffect();
    }

    public final void stopAllEffects() {
        L2Effect[] effects = getAllEffects();
        if (isNull(effects)) {
            return;
        }

        for (L2Effect e : effects) {
            if (nonNull(e)) {
                e.exit(true);
            }
        }

        // TODO move to L2PcInstance
        if (this instanceof L2PcInstance) {
            ((L2PcInstance) this).updateAndBroadcastStatus(2);
        }
    }

    public final void stopConfused(L2Effect effect) {
        if (isNull(effect )) {
            stopEffects(L2Effect.EffectType.CONFUSION);
        } else {
            removeEffect(effect);
        }

        setIsConfused(false);
        getAI().notifyEvent(Event.EVT_THINK, null);
        updateAbnormalEffect();
    }


    public final void stopSkillEffects(int skillId) {
        L2Effect[] effects = getAllEffects();
        if (isNull(effects)) {
            return;
        }

        for (L2Effect e : effects) {
            if (e.getSkill().getId() == skillId) {
                e.exit();
            }
        }
    }

    public final void stopEffects(L2Effect.EffectType type) {
        L2Effect[] effects = getAllEffects();

        if (isNull(effects)) {
            return;
        }

        for (L2Effect e : effects) {
            if (e.getEffectType() == type) {
                e.exit();
            }
        }
    }

    public final void stopFakeDeath(L2Effect effect) {
        if (isNull(effect)) {
            stopEffects(L2Effect.EffectType.FAKE_DEATH);
        } else {
            removeEffect(effect);
        }

        setIsFakeDeath(false);
        // TODO move to L2PcInstance
        if (this instanceof L2PcInstance) {
            ((L2PcInstance) this).setRecentFakeDeath(true);
        }

        ChangeWaitType revive = new ChangeWaitType(this, ChangeWaitType.WT_STOP_FAKEDEATH);
        broadcastPacket(revive);
        getAI().notifyEvent(Event.EVT_THINK, null);
    }

    public final void stopFear(L2Effect effect) {
        if (isNull(effect)) {
            stopEffects(L2Effect.EffectType.FEAR);
        } else {
            removeEffect(effect);
        }

        setIsAfraid(false);
        updateAbnormalEffect();
    }

    public final void stopMuted(L2Effect effect) {
        if (isNull(effect)) {
            stopEffects(L2Effect.EffectType.MUTE);
        } else {
            removeEffect(effect);
        }

        setIsMuted(false);
        updateAbnormalEffect();
    }

    public final void stopPsychicalMuted(L2Effect effect) {
        if (isNull(effect)) {
            stopEffects(L2Effect.EffectType.PSYCHICAL_MUTE);
        } else {
            removeEffect(effect);
        }

        setIsPsychicalMuted(false);
        updateAbnormalEffect();
    }

    public final void stopRooting(L2Effect effect) {
        if (isNull(effect)) {
            stopEffects(L2Effect.EffectType.ROOT);
        } else {
            removeEffect(effect);
        }

        setIsRooted(false);
        getAI().notifyEvent(Event.EVT_THINK, null);
        updateAbnormalEffect();
    }

    public final void stopSleeping(L2Effect effect) {
        if (isNull(effect)) {
            stopEffects(L2Effect.EffectType.SLEEP);
        } else {
            removeEffect(effect);
        }

        setIsSleeping(false);
        getAI().notifyEvent(Event.EVT_THINK, null);
        updateAbnormalEffect();
    }

    public final void stopStunning(L2Effect effect) {
        if (isNull(effect)) {
            stopEffects(L2Effect.EffectType.STUN);
        } else {
            removeEffect(effect);
        }

        setIsStunned(false);
        getAI().notifyEvent(Event.EVT_THINK, null);
        updateAbnormalEffect();
    }

    public abstract void updateAbnormalEffect();

    public final void updateEffectIcons() {
        updateEffectIcons(false);
    }


    public final void updateEffectIcons(boolean partyOnly) {
        // TODO move to L2PcInstance
        L2PcInstance player = null;
        if (this instanceof L2PcInstance) {
            player = (L2PcInstance) this;
        }

        // TODO move to L2Summon
        L2Summon summon = null;
        if (this instanceof L2Summon) {
            summon = (L2Summon) this;
            player = summon.getOwner();
        }

        MagicEffectIcons mi = null;
        if (!partyOnly) {
            mi = new MagicEffectIcons();
        }

        PartySpelled ps = null;
        if (nonNull(summon)) {
            ps = new PartySpelled(summon);
        } else if (nonNull(player) && player.isInParty()) {
            ps = new PartySpelled(player);
        }

        ExOlympiadSpelledInfo os = null;
        if (nonNull(player) && player.isInOlympiadMode()) {
            os = new ExOlympiadSpelledInfo(player);
        }

        if ((isNull(mi)) && (isNull(ps)) && (isNull(os))) {
            return;

        }

        L2Effect[] effects = getAllEffects();
        if (nonNull(effects) && (effects.length > 0)) {
            for (L2Effect effect : effects) {
                if (isNull(effect)) {
                    continue;
                }

                if ((effect.getEffectType() == L2Effect.EffectType.CHARGE) && (nonNull(player))) {
                    // handled by EtcStatusUpdate
                    continue;
                }

                if (effect.getInUse()) {
                    if (nonNull(mi )) {
                        effect.addIcon(mi);
                    }
                    if (nonNull(ps)) {
                        effect.addPartySpelledIcon(ps);
                    }
                    if (nonNull(os)) {
                        effect.addOlympiadSpelledIcon(os);
                    }
                }
            }
        }

        // Send the packets if needed
        if (nonNull(mi)) {
            sendPacket(mi);
        }
        if ((nonNull(ps)) && (nonNull(player))) {
            // summon info only needs to go to the owner, not to the whole party
            // party info: if in party, send to allTemplates party members except one's self.
            // if not in party, send to self.
            if (player.isInParty() && (isNull(summon))) {
                player.getParty().broadcastToPartyMembers(player, ps);
            } else {
                player.sendPacket(ps);
            }
        }
        if (nonNull(os)) {
            if (nonNull(Olympiad.getInstance().getSpectators(player.getOlympiadGameId()))) {
                for (L2PcInstance spectator : Olympiad.getInstance().getSpectators(player.getOlympiadGameId())) {
                    if (isNull(spectator)) {
                        continue;
                    }
                    spectator.sendPacket(os);
                }
            }
        }
    }

    public int getAbnormalEffect() {
        int ae = AbnormalEffects;
        if (isStunned()) {
            ae |= ABNORMAL_EFFECT_STUN;
        }
        if (isRooted()) {
            ae |= ABNORMAL_EFFECT_ROOT;
        }
        if (isSleeping()) {
            ae |= ABNORMAL_EFFECT_SLEEP;
        }
        if (isConfused()) {
            ae |= ABNORMAL_EFFECT_CONFUSED;
        }
        if (isMuted()) {
            ae |= ABNORMAL_EFFECT_MUTED;
        }
        if (isAfraid()) {
            ae |= ABNORMAL_EFFECT_AFRAID;
        }
        if (isPsychicalMuted()) {
            ae |= ABNORMAL_EFFECT_MUTED;
        }
        return ae;
    }

    public final L2Effect[] getAllEffects() {
        List<L2Effect> effects = this.effects;

        if (isNullOrEmpty(effects)) {
            return EMPTY_EFFECTS;
        }

        int ArraySize = effects.size();
        L2Effect[] effectArray = new L2Effect[ArraySize];
        for (int i = 0; i < ArraySize; i++) {
            if (isNull(effects.get(i))) {
                break;
            }
            effectArray[i] = effects.get(i);
        }
        return effectArray;
    }

    public final L2Effect getFirstEffect(int index) {
        List<L2Effect> effects = this.effects;
        if (isNull(effects)) {
            return null;
        }

        L2Effect e;
        L2Effect eventNotInUse = null;
        for (int i = 0; i < effects.size(); i++) {
            e = effects.get(i);
            if (e.getSkill().getId() == index) {
                if (e.getInUse()) {
                    return e;
                }
                eventNotInUse = e;
            }
        }
        return eventNotInUse;
    }

    public final L2Effect getFirstEffect(L2Skill skill) {
        List<L2Effect> effects = this.effects;
        if (isNull(effects)) {
            return null;
        }

        L2Effect e;
        L2Effect eventNotInUse = null;
        for (int i = 0; i < effects.size(); i++) {
            e = effects.get(i);
            if (e.getSkill() == skill) {
                if (e.getInUse()) {
                    return e;
                }
                eventNotInUse = e;
            }
        }
        return eventNotInUse;
    }

    public final L2Effect getFirstEffect(L2Effect.EffectType tp) {
        List<L2Effect> effects = this.effects;
        if (isNull(effects)) {
            return null;
        }

        L2Effect e;
        L2Effect eventNotInUse = null;
        for (int i = 0; i < effects.size(); i++) {
            e = effects.get(i);
            if (e.getEffectType() == tp) {
                if (e.getInUse()) {
                    return e;
                }
                eventNotInUse = e;
            }
        }
        return eventNotInUse;
    }


    public EffectCharge getChargeEffect() {
        L2Effect[] effects = getAllEffects();
        for (L2Effect e : effects) {
            if (e.getSkill().getSkillType() == L2Skill.SkillType.CHARGE) {
                return (EffectCharge) e;
            }
        }
        return null;
    }

    public class AIAccessor implements org.l2j.gameserver.ai.accessor.AIAccessor {

        @Override
        public L2Character getActor() {
            return  L2Character.this;

        }

        public void moveTo(int x, int y, int z, int offset) {
            moveToLocation(x, y, z, offset);
        }

        public void moveTo(int x, int y, int z) {
            moveToLocation(x, y, z, 0);
        }

        public void stopMove(L2Position pos) {
            L2Character.this.stopMove(pos);
        }

        public void doAttack(L2Character target) {
            L2Character.this.doAttack(target);
        }

        public void doCast(L2Skill skill) {
            L2Character.this.doCast(skill);
        }

        public NotifyAITask newNotifyTask(Event evt) {
            return new NotifyAITask(evt);
        }

        public void detachAI() {
            ai = null;
        } // TODO why this ?
    }

    public static class MoveData {
        // when we retrieve x/y/z we use GameTimeControl.getGameTicks()
        // if we are moving, but move timestamp==gameticks, we don't need
        // to recalculate position

        public int moveTimestamp;
        public int xDestination;
        public int yDestination;
        public int zDestination;
        public int xMoveFrom;
        public int yMoveFrom;
        public int zMoveFrom;

        public int heading;
        public int moveStartTime;
        public int ticksToMove;
        public float xSpeedTicks;
        public float _ySpeedTicks;
        public int onGeodataPathIndex;
        public List<AbstractNodeLoc> geoPath;
        public int geoPathAccurateTx;
        public int geoPathAccurateTy;
        public int geoPathGtx;
        public int geoPathGty;
    }




    public void addNotifyQuestOfDeath(QuestState qs) {
        if ((qs == null) || NotifyQuestOfDeathList.contains(qs)) {
            return;
        }

        NotifyQuestOfDeathList.add(qs);
    }

    public final List<QuestState> getNotifyQuestOfDeath() {
        if (NotifyQuestOfDeathList == null) {
            NotifyQuestOfDeathList = new LinkedList<>();
        }

        return NotifyQuestOfDeathList;
    }

    public final synchronized void addStatFunc(Func f) {
        if (isNull(f)) {
            return;
        }

        if (calculators == NPC_STD_CALCULATOR) {
            calculators = new Calculator[Stats.NUM_STATS];

            for (int i = 0; i < Stats.NUM_STATS; i++) {
                if (NPC_STD_CALCULATOR[i] != null) {
                    calculators[i] = new Calculator(NPC_STD_CALCULATOR[i]);
                }
            }
        }

        int stat = f.stat.ordinal();

        if (calculators[stat] == null) {
            calculators[stat] = new Calculator();
        }

        calculators[stat].addFunc(f);
    }

    public final synchronized void addStatFuncs(List<Func> funcs) {

        List<Stats> modifiedStats = new LinkedList<>();

        for (Func f : funcs) {
            modifiedStats.add(f.stat);
            addStatFunc(f);
        }
        broadcastModifiedStats(modifiedStats);
    }

    public final synchronized void removeStatsOwner(Object owner) {

        List<Stats> modifiedStats = null;
        for (int i = 0; i < calculators.length; i++) {
            if (nonNull(calculators[i])) {
                if (nonNull(modifiedStats)) {
                    modifiedStats.addAll(calculators[i].removeOwner(owner));
                } else {
                    modifiedStats = calculators[i].removeOwner(owner);
                }

                if (calculators[i].size() == 0) {
                    calculators[i] = null;
                }
            }
        }

        // TODO move to L2PcInstance If possible, free the memory and just create a link on NPC_STD_CALCULATOR
        if (this instanceof L2NpcInstance) {
            int i = 0;
            for (; i < Stats.NUM_STATS; i++) {
                if (!Calculator.equalsCals(calculators[i], NPC_STD_CALCULATOR[i])) {
                    break;
                }
            }

            if (i >= Stats.NUM_STATS) {
                calculators = NPC_STD_CALCULATOR;
            }
        }

        if ((owner instanceof L2Effect) && !((L2Effect) owner).preventExitUpdate) {
            broadcastModifiedStats(modifiedStats);
        }

    }

    private void broadcastModifiedStats(List<Stats> stats) {
        if (isNullOrEmpty(stats)) {
            return;
        }

        boolean broadcastFull = false;
        boolean otherStats = false;
        StatusUpdate su = null;

        for (Stats stat : stats) {
            if (stat == Stats.PHYSIC_ATTACK_SPEED) {
                if (isNull(su)) {
                    su = new StatusUpdate(getObjectId());
                }
                su.addAttribute(StatusUpdate.ATK_SPD, getPAtkSpd());
            } else if (stat == Stats.MAGIC_ATTACK_SPEED) {
                if (isNull(su)) {
                    su = new StatusUpdate(getObjectId());
                }
                su.addAttribute(StatusUpdate.CAST_SPD, getMAtkSpd());
            }
            else if (stat == Stats.MAX_CP) {
                if (this instanceof L2PcInstance) {
                    if (su == null) {
                        su = new StatusUpdate(getObjectId());
                    }
                    su.addAttribute(StatusUpdate.MAX_CP, getMaxCp());
                }
            } else if (stat == Stats.RUN_SPEED) {
                broadcastFull = true;
            } else {
                otherStats = true;
            }
        }

        // TODO move to L2PcInstance
        if (this instanceof L2PcInstance) {
            if (broadcastFull) {
                ((L2PcInstance) this).updateAndBroadcastStatus(2);
            } else {
                if (otherStats) {
                    ((L2PcInstance) this).updateAndBroadcastStatus(1);
                    if (su != null) {
                        for (L2PcInstance player : getKnownList().getKnownPlayers().values()) {
                            try {
                                player.sendPacket(su);
                            } catch (NullPointerException e) {
                            }
                        }
                    }
                } else if (nonNull(su)) {
                    broadcastPacket(su);
                }
            }
        } else if (this instanceof L2NpcInstance) { // TODO move to L2NpcInstance
            if (broadcastFull) {
                for (L2PcInstance player : getKnownList().getKnownPlayers().values()) {
                    if (player != null) {
                        player.sendPacket(new NpcInfo((L2NpcInstance) this, player));
                    }
                }
            } else if (nonNull(su)) {
                broadcastPacket(su);
            }
        } else if (this instanceof L2Summon) { // TODO move to L2Summon
            if (broadcastFull) {
                for (L2PcInstance player : getKnownList().getKnownPlayers().values()) {
                    if (player != null) {
                        player.sendPacket(new NpcInfo((L2Summon) this, player));
                    }
                }
            } else if (nonNull(su)) {
                broadcastPacket(su);
            }
        } else if (nonNull(su)) {
            broadcastPacket(su);
        }
    }

    public final int getHeading() {
        return heading;
    }

    public final void setHeading(int heading) {
        this.heading = heading;
    }

    public final int getClientX() {
        return clientX;
    }

    public final int getClientY() {
        return clientY;
    }

    public final int getClientZ() {
        return clientZ;
    }

    public final int getClientHeading() {
        return clientHeading;
    }

    public final void setClientX(int val) {
        clientX = val;
    }

    public final void setClientY(int val) {
        clientY = val;
    }

    public final void setClientZ(int val) {
        clientZ = val;
    }

    public final void setClientHeading(int val) {
        clientHeading = val;
    }

    public final int getXdestination() {
        return nonNull(move) ? move.xDestination : getX();
    }

    public final int getYdestination() {
        return nonNull(move) ? move.yDestination : getY();
    }

    public final int getZdestination() {
        return nonNull(move) ? move.zDestination : getZ();
    }

    public final boolean isInCombat() {
        return (getAI().getAttackTarget() != null);
    }

    public final boolean isMoving() {
        return move != null;
    }

    public final boolean isOnGeodataPath() {
        if (isNull(move)) {
            return false;
        }
        try {
            if (move.onGeodataPathIndex == -1) {
                return false;
            }
            if (move.onGeodataPathIndex == (move.geoPath.size() - 1)) {
                return false;
            }
        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }

    public final boolean isCastingNow() {
        return castEndTime > GameTimeController.getGameTicks();
    }

    public final boolean canAbortCast() {
        return castInterruptTime > GameTimeController.getGameTicks();
    }

    public final boolean isAttackingNow() {
        return attackEndTime > GameTimeController.getGameTicks();
    }

    public final boolean isAttackAborted() {
        return _attacking <= 0;
    }

    public final void abortAttack() {
        if (isAttackingNow()) {
            _attacking = 0;
            sendPacket(new ActionFailed());
        }
    }

    public final int getAttackingBodyPart() {
        return _attacking;
    }

    public final void abortCast() {
        if (isCastingNow()) {
            castEndTime = 0;
            castInterruptTime = 0;
            if (nonNull(skillCast)) {
                skillCast.cancel(true);
                skillCast = null;
            }

            if (nonNull(getForceBuff())) {
                getForceBuff().delete();
            }

            enableAllSkills();
            if (this instanceof L2PcInstance) { // TODO move to L2PcInstance
                getAI().notifyEvent(Event.EVT_FINISH_CASTING);
            }
            broadcastPacket(new MagicSkillCanceld(getObjectId()));
            sendPacket(new ActionFailed());
        }
    }

    public boolean updatePosition(int gameTicks) {

        if (isNull(move)) {
            return true;
        }

        if (!isVisible()) {
            move = null;
            return true;
        }

        if (move.moveTimestamp == gameTicks) {
            return false;
        }

        int elapsed = gameTicks - move.moveStartTime;

        if (elapsed >= move.ticksToMove) {
            move.moveTimestamp = gameTicks;

            if (this instanceof L2BoatInstance) { // TODO move to L2Boat
                super.getPosition().setXYZ(move.xDestination, move.yDestination, move.zDestination);
                ((L2BoatInstance) this).updatePeopleInTheBoat(move.xDestination, move.yDestination, move.zDestination);
            } else {
                super.getPosition().setXYZ(move.xDestination, move.yDestination, move.zDestination);
            }

            return true;
        }

        if (this instanceof L2BoatInstance) { // TODO move to L2Boat
            super.getPosition().setXYZ(move.xMoveFrom + (int) (elapsed * move.xSpeedTicks), move.yMoveFrom + (int) (elapsed * move._ySpeedTicks), super.getZ());
            ((L2BoatInstance) this).updatePeopleInTheBoat(move.xMoveFrom + (int) (elapsed * move.xSpeedTicks), move.yMoveFrom + (int) (elapsed * move._ySpeedTicks), super.getZ());
        } else {
            super.getPosition().setXYZ(move.xMoveFrom + (int) (elapsed * move.xSpeedTicks), move.yMoveFrom + (int) (elapsed * move._ySpeedTicks), super.getZ());
            if (this instanceof L2PcInstance) { // TODO  Move to L2Pc
                ((L2PcInstance) this).revalidateZone(false);
            } else {
                revalidateZone();
            }
        }
        move.moveTimestamp = gameTicks;
        return false;
    }

    public void revalidateZone() {
        if (nonNull(getWorldRegion())) {
            getWorldRegion().revalidateZones(this);
        }
    }

    public void stopMove(L2Position pos) {
        stopMove(pos, true);
    }

    public void stopMove(L2Position pos, boolean updateKnownObjects) {
        move = null;
        if (nonNull(pos)) {
            getPosition().setXYZ(pos.x, pos.y, pos.z);
            setHeading(pos.heading);
            if (this instanceof L2PcInstance) { // TODO move to L2Pc
                ((L2PcInstance) this).revalidateZone(true);
            }
        }
        sendPacket(new StopMove(this));
        if (updateKnownObjects) {
            ThreadPoolManager.getInstance().executeTask(new KnownListAsynchronousUpdateTask(this));
        }
    }

    public void setTarget(L2Object object) {
        if ((nonNull(object)) && !object.isVisible()) {
            object = null;
        }

        if ((nonNull(object)) && (object != target)) {
            getKnownList().addKnownObject(object);
            object.getKnownList().addKnownObject(this);
        }

        if (isNull(object) && nonNull(target)) {
            broadcastPacket(new TargetUnselected(this));
        }

        target = object;
    }

    public final int getTargetId() {
        if (nonNull(target)) {
            return target.getObjectId();
        }
        return -1;
    }

    public final L2Object getTarget() {
        return target;
    }

    protected void moveToLocation(int x, int y, int z, int offset) {
        float speed = getStat().getMoveSpeed();
        if ((speed <= 0) || isMovementDisabled()) {
            return;
        }

        final int curX = super.getX();
        final int curY = super.getY();
        final int curZ = super.getZ();

        // TODO: improve Z axis move/follow support when dx,dy are small compared to dz
        double dx = (x - curX);
        double dy = (y - curY);
        double dz = (z - curZ);
        double distance = Math.sqrt((dx * dx) + (dy * dy));

        logger.debug("distance to target: {}", distance);

        // Define movement angles needed
        // ^
        // |    X (x,y)
        // |   /
        // |  /distance
        // | /
        // |/ angle
        // X ---------->
        // (curx,cury)

        double cos;
        double sin;

        if ((offset > 0) || (distance < 1)) {
            // approximation for moving closer when z coordinates are different
            // TODO: handle Z axis movement better
            offset -= Math.abs(dz);
            if (offset < 5) {
                offset = 5;
            }

            // If no distance to go through, the movement is canceled
            if (((distance - offset) <= 0)) {
                sin = 0;
                cos = 1;
                distance = 0;
                x = curX;
                y = curY;

                logger.debug("already in range, no movement needed.");

                getAI().notifyEvent(Event.EVT_ARRIVED, null);

                return;
            }
            sin = dy / distance;
            cos = dx / distance;

            distance -= (offset - 5); // due to rounding error, we have to move a bit closer to be in range

            x = curX + (int) (distance * cos);
            y = curY + (int) (distance * sin);

        } else {
            sin = dy / distance;
            cos = dx / distance;
        }

        MoveData m = new MoveData();

        m.onGeodataPathIndex = -1;
        if ((Config.GEODATA > 0) && !isFlying()) // currently flying characters not checked
        {
            double originalDistance = distance;
            int originalX = x;
            int originalY = y;
            int originalZ = z;
            int gtx = (originalX - L2World.MAP_MIN_X) >> 4;
            int gty = (originalY - L2World.MAP_MIN_Y) >> 4;

            // Movement checks:
            // when geodata == 2, for allTemplates characters except mobs returning home (could be changed later to teleport if pathfinding fails)
            // when geodata == 1, for l2playableinstance and l2riftinstance only
            if (((Config.GEODATA == 2) && !((this instanceof L2Attackable) && ((L2Attackable) this).isReturningToSpawnPoint())) || (this instanceof L2PcInstance) || ((this instanceof L2Summon) && !(getAI().getIntention() == AI_INTENTION_FOLLOW)) // assuming intention_follow only when following owner
                    || (this instanceof L2RiftInvaderInstance)) {
                if (isOnGeodataPath()) {
                    if ((gtx == move.geoPathGtx) && (gty == move.geoPathGty)) {
                        return;
                    }
                    move.onGeodataPathIndex = -1; // Set not on geodata path
                }

                if ((curX < L2World.MAP_MIN_X) || (curX > L2World.MAP_MAX_X) || (curY < L2World.MAP_MIN_Y) || (curY > L2World.MAP_MAX_Y)) {
                    // Temporary fix for character outside world region errors
                    logger.warn("Character " + getName() + " outside world area, in coordinates x:" + curX + " y:" + curY);
                    getAI().setIntention(Intention.AI_INTENTION_IDLE);
                    if (this instanceof L2PcInstance) {
                        ((L2PcInstance) this).deleteMe();
                    } else {
                        onDecay();
                    }
                    return;
                }
                Location destiny = GeoData.getInstance().moveCheck(curX, curY, curZ, x, y, z);
                // location different if destination wasn't reached (or just z coord is different)
                x = destiny.getX();
                y = destiny.getY();
                z = destiny.getZ();
                distance = Math.sqrt(((x - curX) * (x - curX)) + ((y - curY) * (y - curY)));

            }
            // Pathfinding checks. Only when geodata setting is 2, the LoS check gives shorter result
            // than the original movement was and the LoS gives a shorter distance than 2000
            // This way of detecting need for pathfinding could be changed.
            if ((Config.GEODATA == 2) && ((originalDistance - distance) > 100) && (distance < 2000)) {
                // Path calculation
                // Overrides previous movement check
                if ((this instanceof L2PlayableInstance) || isInCombat()) {
                    int gx = (curX - L2World.MAP_MIN_X) >> 4;
                    int gy = (curY - L2World.MAP_MIN_Y) >> 4;

                    m.geoPath = GeoPathFinding.getInstance().findPath(gx, gy, (short) curZ, gtx, gty, (short) originalZ);
                    if ((m.geoPath == null) || (m.geoPath.size() < 2)) // No path found
                    {
                        // Even though there's no path found (remember geonodes aren't perfect),
                        // the mob is attacking and right now we set it so that the mob will go
                        // after target anyway, is dz is small enough. Summons will follow their masters no matter what.
                        if ((this instanceof L2PcInstance) || (!(this instanceof L2PlayableInstance) && (Math.abs(z - curZ) > 140)) || ((this instanceof L2Summon) && !((L2Summon) this).getFollowStatus())) {
                            getAI().setIntention(Intention.AI_INTENTION_IDLE);
                            return;
                        }
                        x = originalX;
                        y = originalY;
                        z = originalZ;
                        distance = originalDistance;
                    } else {
                        m.onGeodataPathIndex = 0; // on first segment
                        m.geoPathGtx = gtx;
                        m.geoPathGty = gty;
                        m.geoPathAccurateTx = originalX;
                        m.geoPathAccurateTy = originalY;

                        x = m.geoPath.get(m.onGeodataPathIndex).getX();
                        y = m.geoPath.get(m.onGeodataPathIndex).getY();
                        z = m.geoPath.get(m.onGeodataPathIndex).getZ();

                        // check for doors in the route
                        if (DoorTable.getInstance().checkIfDoorsBetween(curX, curY, curZ, x, y, z)) {
                            m.geoPath = null;
                            getAI().setIntention(Intention.AI_INTENTION_IDLE);
                            return;
                        }
                        for (int i = 0; i < (m.geoPath.size() - 1); i++) {
                            if (DoorTable.getInstance().checkIfDoorsBetween(m.geoPath.get(i), m.geoPath.get(i + 1))) {
                                m.geoPath = null;
                                getAI().setIntention(Intention.AI_INTENTION_IDLE);
                                return;
                            }
                        }

                        dx = (x - curX);
                        dy = (y - curY);
                        distance = Math.sqrt((dx * dx) + (dy * dy));
                        sin = dy / distance;
                        cos = dx / distance;
                    }
                }
            }
            // If no distance to go through, the movement is canceled
            if ((distance < 1) && ((Config.GEODATA == 2) || (this instanceof L2PlayableInstance) || (this instanceof L2RiftInvaderInstance))) {
                sin = 0;
                cos = 1;
                distance = 0;
                x = curX;
                y = curY;

                if (this instanceof L2Summon) {
                    ((L2Summon) this).setFollowStatus(false);
                }
                getAI().notifyEvent(Event.EVT_ARRIVED, null);
                getAI().setIntention(Intention.AI_INTENTION_IDLE); // needed?
                return;
            }
        }

        // Caclulate the Nb of ticks between the current position and the destination
        // One tick added for rounding reasons
        m.ticksToMove = 1 + (int) ((GameTimeController.TICKS_PER_SECOND * distance) / speed);

        // Calculate the xspeed and yspeed in unit/ticks in function of the movement speed
        m.xSpeedTicks = (float) ((cos * speed) / GameTimeController.TICKS_PER_SECOND);
        m._ySpeedTicks = (float) ((sin * speed) / GameTimeController.TICKS_PER_SECOND);

        // Calculate and set the heading of the L2Character
        int heading = (int) (Math.atan2(-sin, -cos) * 10430.378350470452724949566316381);
        heading += 32768;
        setHeading(heading);

        if (Config.DEBUG) {
            logger.debug("dist:" + distance + "speed:" + speed + " ttt:" + m.ticksToMove + " dx:" + (int) m.xSpeedTicks + " dy:" + (int) m._ySpeedTicks + " heading:" + heading);
        }

        m.xDestination = x;
        m.yDestination = y;
        m.zDestination = z; // this is what was requested from client
        m.heading = 0;

        m.moveStartTime = GameTimeController.getGameTicks();
        m.xMoveFrom = curX;
        m.yMoveFrom = curY;
        m.zMoveFrom = curZ;

        logger.debug("time to target: {} ", m.ticksToMove);

        // Set the L2Character move object to MoveData object
        move = m;

        // Add the L2Character to movingObjects of the GameTimeController
        // The GameTimeController manage objects movement
        GameTimeController.getInstance().registerMovingObject(this);

        int tm = m.ticksToMove * GameTimeController.MILLIS_IN_TICK;

        // Create a task to notify the AI that L2Character arrives at a check point of the movement
        if (tm > 3000) {
            ThreadPoolManager.getInstance().scheduleAi(new NotifyAITask(Event.EVT_ARRIVED_REVALIDATE), 2000);
        }
    }

    public boolean moveToNextRoutePoint() {
        if (!isOnGeodataPath()) {
            move = null;
            return false;
        }

        float speed = getStat().getMoveSpeed();
        if ((speed <= 0) || isMovementDisabled()) {
            move = null;
            return false;
        }

        MoveData m = new MoveData();

        // Update MoveData object
        m.onGeodataPathIndex = move.onGeodataPathIndex + 1; // next segment
        m.geoPath = move.geoPath;
        m.geoPathGtx = move.geoPathGtx;
        m.geoPathGty = move.geoPathGty;
        m.geoPathAccurateTx = move.geoPathAccurateTx;
        m.geoPathAccurateTy = move.geoPathAccurateTy;

        // Get current position of the L2Character
        m.xMoveFrom = super.getX();
        m.yMoveFrom = super.getY();
        m.zMoveFrom = super.getZ();

        if (move.onGeodataPathIndex == (move.geoPath.size() - 2)) {
            m.xDestination = move.geoPathAccurateTx;
            m.yDestination = move.geoPathAccurateTy;
            m.zDestination = move.geoPath.get(m.onGeodataPathIndex).getZ();
        } else {
            m.xDestination = move.geoPath.get(m.onGeodataPathIndex).getX();
            m.yDestination = move.geoPath.get(m.onGeodataPathIndex).getY();
            m.zDestination = move.geoPath.get(m.onGeodataPathIndex).getZ();
        }
        double dx = (m.xDestination - m.xMoveFrom);
        double dy = (m.yDestination - m.yMoveFrom);
        double distance = Math.sqrt((dx * dx) + (dy * dy));
        double sin = dy / distance;
        double cos = dx / distance;

        // Caclulate the Nb of ticks between the current position and the destination
        // One tick added for rounding reasons
        m.ticksToMove = 1 + (int) ((GameTimeController.TICKS_PER_SECOND * distance) / speed);

        // Calculate the xspeed and yspeed in unit/ticks in function of the movement speed
        m.xSpeedTicks = (float) ((cos * speed) / GameTimeController.TICKS_PER_SECOND);
        m._ySpeedTicks = (float) ((sin * speed) / GameTimeController.TICKS_PER_SECOND);

        // Calculate and set the heading of the L2Character
        int heading = (int) (Math.atan2(-sin, -cos) * 10430.378350470452724949566316381);
        heading += 32768;
        setHeading(heading);
        m.heading = 0; // ?

        m.moveStartTime = GameTimeController.getGameTicks();

        if (Config.DEBUG) {
            logger.debug("time to target:" + m.ticksToMove);
        }

        // Set the L2Character move object to MoveData object
        move = m;

        GameTimeController.getInstance().registerMovingObject(this);

        int tm = m.ticksToMove * GameTimeController.MILLIS_IN_TICK;


        if (tm > 3000) {
            ThreadPoolManager.getInstance().scheduleAi(new NotifyAITask(Event.EVT_ARRIVED_REVALIDATE), 2000);
        }

        CharMoveToLocation msg = new CharMoveToLocation(this);
        broadcastPacket(msg);

        return true;
    }

    public boolean validateMovementHeading(int heading) {
        MoveData md = move;

        if (isNull(md)) {
            return true;
        }

        boolean result = true;
        if (md.heading != heading) {
            result = (md.heading == 0);
            md.heading = heading;
        }

        return result;
    }

    /**
     * @deprecated use getPlanDistanceSq(int x, int y, int z)
     */
    @Deprecated
    public final double getDistance(int x, int y) {
        double dx = x - getX();
        double dy = y - getY();

        return Math.sqrt((dx * dx) + (dy * dy));
    }

    /**
     * @deprecated use getPlanDistanceSq(int x, int y, int z)
     */
    @Deprecated
    public final double getDistance(int x, int y, int z) {
        double dx = x - getX();
        double dy = y - getY();
        double dz = z - getZ();

        return Math.sqrt((dx * dx) + (dy * dy) + (dz * dz));
    }

    public final double getDistanceSq(L2Object object) {
        return getDistanceSq(object.getX(), object.getY(), object.getZ());
    }

    public final double getDistanceSq(int x, int y, int z) {
        double dx = x - getX();
        double dy = y - getY();
        double dz = z - getZ();

        return ((dx * dx) + (dy * dy) + (dz * dz));
    }

    public final double getPlanDistanceSq(L2Object object) {
        return getPlanDistanceSq(object.getX(), object.getY());
    }

    public final double getPlanDistanceSq(int x, int y) {
        double dx = x - getX();
        double dy = y - getY();

        return ((dx * dx) + (dy * dy));
    }

    public final boolean isInsideRadius(L2Object object, int radius, boolean checkZ, boolean strictCheck) {
        return isInsideRadius(object.getX(), object.getY(), object.getZ(), radius, checkZ, strictCheck);
    }

    public final boolean isInsideRadius(int x, int y, int radius, boolean strictCheck) {
        return isInsideRadius(x, y, 0, radius, false, strictCheck);
    }

    public final boolean isInsideRadius(int x, int y, int z, int radius, boolean checkZ, boolean strictCheck) {
        double dx = x - getX();
        double dy = y - getY();
        double dz = z - getZ();

        if (strictCheck) {
            if (checkZ) {
                return ((dx * dx) + (dy * dy) + (dz * dz)) < (radius * radius);
            }
            return ((dx * dx) + (dy * dy)) < (radius * radius);
        }
        if (checkZ) {
            return ((dx * dx) + (dy * dy) + (dz * dz)) <= (radius * radius);
        }
        return ((dx * dx) + (dy * dy)) <= (radius * radius);
    }

    public float getWeaponExpertisePenalty() {
        return 1.f;
    }

    public float getArmourExpertisePenalty() {
        return 1.f;
    }
    public void setAttackingBodypart() {
        _attacking = Inventory.PAPERDOLL_CHEST;
    }

    protected boolean checkAndEquipArrows() {
        return true;
    }

    // TODO move to Playable
    public void addExpAndSp(long addToExp, int addToSp) {
        // Dummy method (overridden by players and pets)
    }

    public abstract L2ItemInstance getActiveWeaponInstance();

    public abstract L2ItemInstance getSecondaryWeaponInstance();

    protected void onHitTimer(L2Character target, int damage, boolean crit, boolean miss, boolean soulshot, boolean shld) {
        // TODO move to L2Npc
        if ((isNull(target)) || isAlikeDead() || ((this instanceof L2NpcInstance) && ((L2NpcInstance) this).isEventMob)) {
            getAI().notifyEvent(Event.EVT_CANCEL);
            return;
        }

        if (((this instanceof L2NpcInstance) && target.isAlikeDead()) || target.isDead() || (!getKnownList().knowsObject(target) && !(this instanceof L2DoorInstance))) {
            getAI().notifyEvent(Event.EVT_CANCEL);
            sendPacket(new ActionFailed());
            return;
        }

        if (miss) {
            if (target instanceof L2PcInstance) {
                SystemMessage sm = new SystemMessage(SystemMessageId.AVOIDED_S1S_ATTACK);
                // TODO move to L2Summon
                if (this instanceof L2Summon) {
                    int mobId = ((L2Summon) this).getTemplateId();
                    sm.addNpcName(mobId);
                } else {
                    sm.addString(getName());
                }

                 target.sendPacket(sm);
            }
        }

        if (!isAttackAborted()) {
            if (target.isRaid()) {
                int level = 0;
                // TODO Move to L2Pc
                if (this instanceof L2PcInstance) {
                    level = getLevel();
                } else if (this instanceof L2Summon) {
                    level = ((L2Summon) this).getOwner().getLevel();
                }

                if (level > (target.getLevel() + 8)) {
                    L2Skill skill = SkillTable.getInstance().getInfo(4515, 99);

                    if (skill != null) {
                        skill.getEffects(target, this);
                    } else {
                        logger.warn("Skill 4515 at level 99 is missing in DP.");
                    }

                    damage = 0;
                }
            }

            sendDamageMessage(target, damage, false, crit, miss);

            // TODO move to L2PcInstance
            if (target instanceof L2PcInstance) {
                L2PcInstance enemy = (L2PcInstance) target;

                if (shld) {
                    enemy.sendPacket(new SystemMessage(SystemMessageId.SHIELD_DEFENCE_SUCCESSFULL));
                }
            } else if (target instanceof L2Summon) { // TODO move to L2Summon
                L2Summon activeSummon = (L2Summon) target;

                SystemMessage sm = new SystemMessage(SystemMessageId.PET_RECEIVED_S2_DAMAGE_BY_S1);
                sm.addString(getName());
                sm.addNumber(damage);
                activeSummon.getOwner().sendPacket(sm);
            }

            if (!miss && (damage > 0)) {
                var weapon = getActiveWeaponInstance();
                boolean isBow = ((weapon != null) && weapon.getType().toString().equalsIgnoreCase("Bow"));

                if (!isBow) // Do not reflect or absorb if weapon is of type bow
                {
                    double reflectPercent = target.getStat().calcStat(Stats.REFLECT_DAMAGE_PERCENT, 0, null, null);

                    if (reflectPercent > 0) {
                        int reflectedDamage = (int) ((reflectPercent / 100.) * damage);
                        damage -= reflectedDamage;

                        if (reflectedDamage > target.getMaxHp()) {
                            reflectedDamage = target.getMaxHp();
                        }

                        getStatus().reduceHp(reflectedDamage, target, true);
                    }

                    double absorbPercent = getStat().calcStat(Stats.ABSORB_DAMAGE_PERCENT, 0, null, null);

                    if (absorbPercent > 0) {
                        int maxCanAbsorb = (int) (getMaxHp() - getCurrentHp());
                        int absorbDamage = (int) ((absorbPercent / 100.) * damage);

                        if (absorbDamage > maxCanAbsorb) {
                            absorbDamage = maxCanAbsorb;
                        }

                        if (absorbDamage > 0) {
                            setCurrentHp(getCurrentHp() + absorbDamage);
                        }
                    }
                }

                target.reduceCurrentHp(damage, this);

                target.getAI().notifyEvent(Event.EVT_ATTACKED, this);

                // TODO hotfix to refactoring hierarchy should be removed asap
                getAI().clientStartAutoAttack();

                if (!target.isRaid() && Formulas.getInstance().calcAtkBreak(target, damage)) {
                    target.breakAttack();
                    target.breakCast();
                }
            }

            var activeWeapon = getActiveWeaponInstance();

            if (nonNull(activeWeapon)) {
                // activeWeapon.getSkillEffects(this, target, crit); TODO implements
            }
            return;
        }

        getAI().notifyEvent(Event.EVT_CANCEL);
    }


    public void breakAttack() {
        if (isAttackingNow()) {
            abortAttack();

            // TODO move to L2PcInstance
            if (this instanceof L2PcInstance) {
                // Send a system message
                sendPacket(new SystemMessage(SystemMessageId.ATTACK_FAILED));
            }
        }
    }

    public void breakCast() {
        if (isCastingNow() && canAbortCast() && (getLastSkillCast() != null) && getLastSkillCast().isMagic()) {
            abortCast();

            // TODO move to L2PcInstance
            if (this instanceof L2PcInstance) {
                sendPacket(new SystemMessage(SystemMessageId.CASTING_INTERRUPTED));
            }
        }
    }

    protected void reduceArrowCount() {
        // default is to do nothin
    }

    @Override
    public void onForcedAttack(L2PcInstance player) {
        if (isInsidePeaceZone(player)) {
            player.sendPacket(new SystemMessage(SystemMessageId.TARGET_IN_PEACEZONE));
            player.sendPacket(new ActionFailed());
        } else if (player.isInOlympiadMode() && (player.getTarget() != null)) {
            L2PcInstance target;
            if (player.getTarget() instanceof L2Summon) {
                target = ((L2Summon) player.getTarget()).getOwner();
            } else {
                target = (L2PcInstance) player.getTarget();
            }

            if (target.isInOlympiadMode() && !player.isOlympiadStart() && (player.getOlympiadGameId() != target.getOlympiadGameId())) {
                player.sendPacket(new ActionFailed());
            }
        } else if ((player.getTarget() != null) && !player.getTarget().isAttackable() && (player.getAccessLevel() < Config.GM_PEACEATTACK)) {
            player.sendPacket(new ActionFailed());
        } else if (player.isConfused()) {
            player.sendPacket(new ActionFailed());
        } else if (this instanceof L2ArtefactInstance) {
            player.sendPacket(new ActionFailed());
        } else {
            if (!GeoData.getInstance().canSeeTarget(player, this)) {
                player.sendPacket(new SystemMessage(SystemMessageId.CANT_SEE_TARGET));
                player.sendPacket(new ActionFailed());
                return;
            }
            player.getAI().setIntention(Intention.AI_INTENTION_ATTACK, this);
        }
    }

    public boolean isInsidePeaceZone(L2PcInstance attacker) {
        return isInsidePeaceZone(attacker, this);
    }

    public boolean isInsidePeaceZone(L2PcInstance attacker, L2Object target) {
        return ((attacker.getAccessLevel() < Config.GM_PEACEATTACK) && isInsidePeaceZone((L2Object) attacker, target));
    }

    public boolean isInsidePeaceZone(L2Object attacker, L2Object target) {
        if (isNull(target)) {
            return false;
        }
        if (target instanceof L2MonsterInstance) {
            return false;
        }
        if (attacker instanceof L2MonsterInstance) {
            return false;
        }
        if (Config.ALT_GAME_KARMA_PLAYER_CAN_BE_KILLED_IN_PEACEZONE) {
            if ((target instanceof L2PcInstance) && (((L2PcInstance) target).getKarma() > 0)) {
                return false;
            }
            if ((target instanceof L2Summon) && (((L2Summon) target).getOwner().getKarma() > 0)) {
                return false;
            }
            if ((attacker instanceof L2PcInstance) && (((L2PcInstance) attacker).getKarma() > 0)) {
                if ((target instanceof L2PcInstance) && (((L2PcInstance) target).getPvpFlag() > 0)) {
                    return false;
                }
                if ((target instanceof L2Summon) && (((L2Summon) target).getOwner().getPvpFlag() > 0)) {
                    return false;
                }
            }
            if ((attacker instanceof L2Summon) && (((L2Summon) attacker).getOwner().getKarma() > 0)) {
                if ((target instanceof L2PcInstance) && (((L2PcInstance) target).getPvpFlag() > 0)) {
                    return false;
                }
                if ((target instanceof L2Summon) && (((L2Summon) target).getOwner().getPvpFlag() > 0)) {
                    return false;
                }
            }
        }
        // TODO: ZONETODO: Are there things < L2Characters in peace zones that can be attacked? If not this could be cleaned up

        if ((attacker instanceof L2Character) && (target instanceof L2Character)) {
            return (((L2Character) target).isInsideZone(Zone.PEACE) || ((L2Character) attacker).isInsideZone(Zone.PEACE));
        }
        if (attacker instanceof L2Character) {
            return ((TownManager.getInstance().getTown(target.getX(), target.getY(), target.getZ()) != null) || ((L2Character) attacker).isInsideZone(Zone.PEACE));
        }

        return ((TownManager.getInstance().getTown(target.getX(), target.getY(), target.getZ()) != null) || (TownManager.getInstance().getTown(attacker.getX(), attacker.getY(), attacker.getZ()) != null));
    }

    public Boolean isInActiveRegion() {
        try {
            L2WorldRegion region = L2World.getInstance().getRegion(getX(), getY());
            return nonNull(region) && region.isActive();
        } catch (Exception e) {
            if (this instanceof L2PcInstance) {
                logger.warn("Player {} at bad coords: (x: {}, y: {}, z: {})", getName(), getX(), getY(), getZ());
                ((L2PcInstance) this).sendMessage("Error with your coordinates! Please reboot your game fully!");
                ((L2PcInstance) this).teleToLocation(80753, 145481, -3532, false); // Near Giran luxury shop
            } else {
                logger.warn("Object {} at bad coords: (x: {}, y: {}, z: {})", getName(), getX(), getY(), getZ());
                decayMe();
            }
            return false;
        }
    }

    public boolean isInParty() {
        return false;
    }

    /**
     * TODO move to L2Playable
     */
    public L2Party getParty() {
        return null;
    }

    public int calculateTimeBetweenAttacks(L2Character target, L2ItemInstance weapon) {
        double atkSpd = 0;
        if (nonNull(weapon)) {
            switch (weapon.getType()) {
                case BOW:
                    atkSpd = getStat().getPAtkSpd();
                    return (int) ((1500 * 345) / atkSpd);
                case DAGGER:
                    atkSpd = getStat().getPAtkSpd();
                    break;
                default:
                    atkSpd = getStat().getPAtkSpd();
            }
        } else {
            atkSpd = getPAtkSpd();
        }

        return Formulas.getInstance().calcPAtkSpd(this, target, atkSpd);
    }

    public int calculateReuseTime(L2Character target, L2ItemInstance weapon) {
        if (isNull(weapon)) {
            return 0;
        }

        long reuse = weapon.getReuseDelay();
        if (reuse == 0) {
            return 0;
        }

        reuse *= getStat().getReuseModifier(target);
        double atkSpd = getStat().getPAtkSpd();
        switch (weapon.getType()) {
            case BOW:
                return (int) ((reuse * 345) / atkSpd);
            default:
                return (int) ((reuse * 312) / atkSpd);
        }
    }

    public boolean isUsingDualWeapon() {
        return false;
    }

    public L2Skill addSkill(L2Skill newSkill) {
        L2Skill oldSkill = null;

        if (nonNull(newSkill)) {
            oldSkill = skills.put(newSkill.getId(), newSkill);

            if (oldSkill != null) {
                removeStatsOwner(oldSkill);
            }

            addStatFuncs(newSkill.getStatFuncs(null, this));
        }

        return oldSkill;
    }

    public L2Skill removeSkill(L2Skill skill) {
        if (isNull(skill)) {
            return null;
        }

        L2Skill oldSkill = skills.remove(skill.getId());

        if (nonNull(oldSkill)) {
            removeStatsOwner(oldSkill);
        }

        return oldSkill;
    }

    public final L2Skill[] getAllSkills() {
        if (skills == null) {
            return new L2Skill[0];
        }

        return skills.values().toArray(new L2Skill[skills.values().size()]);
    }

    public final L2Skill getKnownSkill(int skillId) {
        if (isNull(skills)) {
            return null;
        }

        return skills.get(skillId);
    }

    public int getBuffCount() {
        L2Effect[] effects = getAllEffects();
        int numBuffs = 0;
        if (nonNull(effects)) {
            for (L2Effect e : effects) {
                if (e != null) {
                    if (((e.getSkill().getSkillType() == L2Skill.SkillType.BUFF) || (e.getSkill().getSkillType() == L2Skill.SkillType.DEBUFF) || (e.getSkill().getSkillType() == L2Skill.SkillType.REFLECT) || (e.getSkill().getSkillType() == L2Skill.SkillType.HEAL_PERCENT) || (e.getSkill().getSkillType() == L2Skill.SkillType.MANAHEAL_PERCENT)) && !((e.getSkill().getId() > 4360) && (e.getSkill().getId() < 4367))) { // 7s buffs
                        numBuffs++;
                    }
                }
            }
        }
        return numBuffs;
    }

    public void removeFirstBuff(int preferSkill) {
        L2Effect[] effects = getAllEffects();
        L2Effect removeMe = null;
        if (effects != null) {
            for (L2Effect e : effects) {
                if (e != null) {
                    if (((e.getSkill().getSkillType() == L2Skill.SkillType.BUFF) || (e.getSkill().getSkillType() == L2Skill.SkillType.DEBUFF) || (e.getSkill().getSkillType() == L2Skill.SkillType.REFLECT) || (e.getSkill().getSkillType() == L2Skill.SkillType.HEAL_PERCENT) || (e.getSkill().getSkillType() == L2Skill.SkillType.MANAHEAL_PERCENT)) && !((e.getSkill().getId() > 4360) && (e.getSkill().getId() < 4367))) {
                        if (preferSkill == 0) {
                            removeMe = e;
                            break;
                        } else if (e.getSkill().getId() == preferSkill) {
                            removeMe = e;
                            break;
                        } else if (removeMe == null) {
                            removeMe = e;
                        }
                    }
                }
            }
        }
        if (removeMe != null) {
            removeMe.exit();
        }
    }

    public int getDanceCount() {
        int danceCount = 0;
        L2Effect[] effects = getAllEffects();
        for (L2Effect effect : effects) {
            if (effect == null) {
                continue;
            }
            if (effect.getSkill().isDance() && effect.getInUse()) {
                danceCount++;
            }
        }
        return danceCount;
    }

    public boolean doesStack(L2Skill checkSkill) {
        if (isNullOrEmpty(effects) || isNull(checkSkill._effectTemplates) || (checkSkill._effectTemplates.length < 1) || (isNull(checkSkill._effectTemplates[0].stackType))) {
            return false;
        }
        String stackType = checkSkill._effectTemplates[0].stackType;
        if (stackType.equals("none")) {
            return false;
        }

        for (int i = 0; i < effects.size(); i++) {
            if ((effects.get(i).getStackType() != null) && effects.get(i).getStackType().equals(stackType)) {
                return true;
            }
        }
        return false;
    }

    public void onMagicLaunchedTimer(L2Object[] targets, L2Skill skill, int coolTime, boolean instant) {
        if (isNull(skill) || isNull(targets) || (targets.length <= 0)) {
            skillCast = null;
            enableAllSkills();
            getAI().notifyEvent(Event.EVT_CANCEL);
            return;
        }

        int escapeRange = 0;
        if (skill.getEffectRange() > escapeRange) {
            escapeRange = skill.getEffectRange();
        } else if ((skill.getCastRange() < 0) && (skill.getSkillRadius() > 80)) {
            escapeRange = skill.getSkillRadius();
        }

        if (escapeRange > 0) {
            List<L2Character> targetList = new LinkedList<>();
            for (int i = 0; i < targets.length; i++) {
                if (targets[i] instanceof L2Character) {
                    if (!Util.checkIfInRange(escapeRange, this, targets[i], true)) {
                        continue;
                    }
                    if (skill.isOffensive()) {
                        if (this instanceof L2PcInstance) {
                            if (((L2Character) targets[i]).isInsidePeaceZone((L2PcInstance) this)) {
                                continue;
                            }
                        } else {
                            if (((L2Character) targets[i]).isInsidePeaceZone(this, targets[i])) {
                                continue;
                            }
                        }
                    }
                    targetList.add((L2Character) targets[i]);
                }
            }
            if (targetList.isEmpty()) {
                abortCast();
                return;
            }
            targets = targetList.toArray(new L2Character[targetList.size()]);
        }

        if (!isCastingNow() || (isAlikeDead() && !skill.isPotion())) {
            skillCast = null;
            enableAllSkills();

            getAI().notifyEvent(Event.EVT_CANCEL);

            castEndTime = 0;
            castInterruptTime = 0;
            return;
        }

        int magicId = skill.getDisplayId();
        int level = getSkillLevel(skill.getId());

        if (level < 1) {
            level = 1;
        }

        if (!skill.isPotion()) {
            broadcastPacket(new MagicSkillLaunched(this, magicId, level, targets));
        }

        if (instant) {
            onMagicHitTimer(targets, skill, coolTime, true);
        } else {
            skillCast = ThreadPoolManager.getInstance().scheduleEffect(new MagicUseTask(targets, skill, coolTime, 2), 200);
        }

    }

    public void onMagicHitTimer(L2Object[] targets, L2Skill skill, int coolTime, boolean instant) {

        if (isNull(skill)|| isNull(targets) || (targets.length <= 0)) {
            skillCast = null;
            enableAllSkills();
            getAI().notifyEvent(Event.EVT_CANCEL);
            return;
        }
        if (getForceBuff() != null) {
            skillCast = null;
            enableAllSkills();

            getForceBuff().delete();
            return;
        }

        try {
            for (L2Object target2 : targets) {
                if (target2 instanceof L2PlayableInstance) {
                    L2Character target = (L2Character) target2;

                    if ((skill.getSkillType() == L2Skill.SkillType.BUFF) || (skill.getSkillType() == L2Skill.SkillType.SEED)) {
                        SystemMessage smsg = new SystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT);
                        smsg.addString(skill.getName());
                        target.sendPacket(smsg);
                    }
                    // TODO move to L2PcInstance
                    if ((this instanceof L2PcInstance) && (target instanceof L2Summon)) {
                        ((L2Summon) target).getOwner().sendPacket(new PetInfo((L2Summon) target));
                        sendPacket(new NpcInfo((L2Summon) target, this));
                        target.updateEffectIcons(true);
                    }
                }
            }

            StatusUpdate su = new StatusUpdate(getObjectId());
            boolean isSendStatus = false;

            double mpConsume = getStat().getMpConsume(skill);
            if (mpConsume > 0) {
                getStatus().reduceMp(calcStat(Stats.MP_CONSUME_RATE, mpConsume, null, null));
                su.addAttribute(StatusUpdate.CUR_MP, (int) getCurrentMp());
                isSendStatus = true;
            }

            // Consume HP if necessary and Send the Server->Client packet StatusUpdate with current HP and MP to allTemplates other L2PcInstance to inform
            if (skill.getHpConsume() > 0) {
                double consumeHp;

                consumeHp = calcStat(Stats.HP_CONSUME_RATE, skill.getHpConsume(), null, null);
                if ((consumeHp + 1) >= getCurrentHp()) {
                    consumeHp = getCurrentHp() - 1.0;
                }

                getStatus().reduceHp(consumeHp, this);

                su.addAttribute(StatusUpdate.CUR_HP, (int) getCurrentHp());
                isSendStatus = true;
            }

            // Send a Server->Client packet StatusUpdate with MP modification to the L2PcInstance
            if (isSendStatus) {
                sendPacket(su);
            }

            // Consume Items if necessary and Send the Server->Client packet InventoryUpdate with Item modification to allTemplates the L2Character
            if (skill.getItemConsume() > 0) {
                consumeItem(skill.getItemConsumeId(), skill.getItemConsume());
            }

            // Launch the magic skill in order to calculate its effects
            callSkill(skill, targets);
        } catch (NullPointerException e) {
        }

        if (instant || (coolTime == 0)) {
            onMagicFinalizer(targets, skill);
        } else {
            skillCast = ThreadPoolManager.getInstance().scheduleEffect(new MagicUseTask(targets, skill, coolTime, 3), coolTime);
        }
    }

    public void onMagicFinalizer(L2Object[] targets, L2Skill skill) {
        skillCast = null;
        castEndTime = 0;
        castInterruptTime = 0;
        enableAllSkills();

        if ((skill.getSkillType() == SkillType.PDAM) || (skill.getSkillType() == SkillType.BLOW) || (skill.getSkillType() == SkillType.DRAIN_SOUL) || (skill.getSkillType() == SkillType.SOW) || (skill.getSkillType() == SkillType.SPOIL)) {
            if ((getTarget() != null) && (getTarget() instanceof L2Character)) {
                getAI().setIntention(Intention.AI_INTENTION_ATTACK, getTarget());
            }
        }

        if (skill.isOffensive() && !(skill.getSkillType() == SkillType.UNLOCK) && !(skill.getSkillType() == SkillType.DELUXE_KEY_UNLOCK)) {
            getAI().clientStartAutoAttack();
        }

        getAI().notifyEvent(Event.EVT_FINISH_CASTING);

        // TODO move to L2PcInstance
        if (this instanceof L2PcInstance) {
            L2PcInstance currPlayer = (L2PcInstance) this;
            SkillDat queuedSkill = currPlayer.getQueuedSkill();

            currPlayer.setCurrentSkill(null, false, false);

            if (nonNull(queuedSkill)) {
                currPlayer.setQueuedSkill(null, false, false);

                ThreadPoolManager.getInstance().executeTask(new QueuedMagicUseTask(currPlayer, queuedSkill.getSkill(), queuedSkill.isCtrlPressed(), queuedSkill.isShiftPressed()));
            }
        }
    }

    public void consumeItem(int itemConsumeId, int itemCount) {
    }


    public void enableSkill(int skillId) {
        if (isNull(disabledSkills)) {
            return;
        }

        disabledSkills.remove(Integer.valueOf(skillId)); //this forced wrapper is necessary. otherwise it should try remove by index

        // TODO move to L2Pc
        if (this instanceof L2PcInstance) {
            removeTimeStamp(skillId);
        }
    }

    public void disableSkill(int skillId) {
        if (isNull(disabledSkills)) {
            disabledSkills = Collections.synchronizedList(new LinkedList<>());
        }

        disabledSkills.add(skillId);
    }


    public void disableSkill(int skillId, long delay) {
        disableSkill(skillId);
        if (delay > 10) {
            ThreadPoolManager.getInstance().scheduleAi(new EnableSkill(skillId), delay);
        }
    }

    public boolean isSkillDisabled(int skillId) {
        if (isAllSkillsDisabled()) {
            return true;
        }

        if (isNull(disabledSkills)) {
            return false;
        }

        return disabledSkills.contains(skillId);
    }

    public void disableAllSkills() {
        logger.debug("allTemplates skills disabled");
        allSkillsDisabled = true;
    }

    public void enableAllSkills() {
        logger.debug("allTemplates skills enabled");
        allSkillsDisabled = false;
    }

    public void callSkill(L2Skill skill, L2Object[] targets) {
        try {
            for (L2Object target : targets) {
                if (target instanceof L2Character) {
                    L2Character player = (L2Character) target;

                    var activeWeapon = getActiveWeaponInstance();
                    if ((nonNull(activeWeapon)) && !((L2Character) target).isDead()) {
                        // TODO implment
                        /*if ((! activeWeapon.getSkillEffects(this, player, skill).isEmpty()) && (this instanceof L2PcInstance)) {
                            sendPacket(SystemMessage.sendString("Target affected by weapon special ability!"));
                        }*/
                    }

                    if (player.isRaid() && (getLevel() > (player.getLevel() + 8))) {
                        L2Skill tempSkill = SkillTable.getInstance().getInfo(4515, 99);
                        if (nonNull(tempSkill)) {
                            tempSkill.getEffects(player, this);
                        } else {
                            logger.warn("Skill 4515 at level 99 is missing in DP.");
                        }
                    }

                    L2PcInstance activeChar = null;
                    // TODO move to L2Playable
                    if (this instanceof L2PcInstance) {
                        activeChar = (L2PcInstance) this;
                    } else if (this instanceof L2Summon) {
                        activeChar = ((L2Summon) this).getOwner();
                    }

                    if (activeChar != null) {
                        if (skill.isOffensive()) {
                            if ((player instanceof L2PcInstance) || (player instanceof L2Summon)) {
                                player.getAI().notifyEvent(Event.EVT_ATTACKED, activeChar);
                                activeChar.updatePvPStatus(player);
                            } else if (player instanceof L2Attackable) {
                                // notify the AI that she is attacked
                                player.getAI().notifyEvent(Event.EVT_ATTACKED, activeChar);
                            }
                        } else {
                            if (player instanceof L2PcInstance) {
                                // Casting non offensive skill on reader with pvp flag set or with karma
                                if (!player.equals(this) && ((((L2PcInstance) player).getPvpFlag() > 0) || (((L2PcInstance) player).getKarma() > 0))) {
                                    activeChar.updatePvPStatus();
                                }
                            } else if ((player instanceof L2Attackable) && !(skill.getSkillType() == L2Skill.SkillType.SUMMON) && !(skill.getSkillType() == L2Skill.SkillType.BEAST_FEED) && !(skill.getSkillType() == L2Skill.SkillType.UNLOCK) && !(skill.getSkillType() == L2Skill.SkillType.DELUXE_KEY_UNLOCK)) {
                                activeChar.updatePvPStatus();
                            }
                        }
                    }
                }
            }

            ISkillHandler handler = null;

            // TODO Remove this useless section
            if (skill.isToggle()) {
                // Check if the skill effects are already in progress on the L2Character
                if (getFirstEffect(skill.getId()) != null) {
                    handler = SkillHandler.getInstance().getSkillHandler(skill.getSkillType());

                    if (handler != null) {
                        handler.useSkill(this, skill, targets);
                    } else {
                        skill.useSkill(this, targets);
                    }

                    // TODO move to L2Playable
                    if ((this instanceof L2PcInstance) || (this instanceof L2Summon)) {
                        L2PcInstance caster = (this instanceof L2PcInstance) ? (L2PcInstance) this : ((L2Summon) this).getOwner();
                        for (L2Object target : targets) {
                            if (target instanceof L2NpcInstance) {
                                for (Quest quest : ((L2NpcInstance) target).getEventQuests(Quest.QuestEventType.MOB_TARGETED_BY_SKILL)) {
                                    quest.notifySkillUse((L2NpcInstance) target, caster, skill);
                                }
                            }
                        }
                    }

                    return;
                }
            }

            if (skill.isOverhit()) {
                for (L2Object target : targets) {
                    L2Character player = (L2Character) target;
                    if (player instanceof L2Attackable) {
                        ((L2Attackable) player).overhitEnabled(true);
                    }
                }
            }

            handler = SkillHandler.getInstance().getSkillHandler(skill.getSkillType());

            if (handler != null) {
                handler.useSkill(this, skill, targets);
            } else {
                skill.useSkill(this, targets);
            }

            // TODO move to L2Playable
            if ((this instanceof L2PcInstance) || (this instanceof L2Summon)) {
                L2PcInstance caster = (this instanceof L2PcInstance) ? (L2PcInstance) this : ((L2Summon) this).getOwner();
                for (L2Object target : targets) {
                    if (target instanceof L2NpcInstance) {
                        L2NpcInstance npc = (L2NpcInstance) target;
                        if (npc.getEventQuests(Quest.QuestEventType.MOB_TARGETED_BY_SKILL) != null) {
                            for (Quest quest : npc.getEventQuests(Quest.QuestEventType.MOB_TARGETED_BY_SKILL)) {
                                quest.notifySkillUse(npc, caster, skill);
                            }
                        }
                    }
                }
                if (skill.getAggroPoints() > 0) {
                    for (L2Object spMob : caster.getKnownList().getKnownObjects().values()) {
                        if (spMob instanceof L2NpcInstance) {
                            L2NpcInstance npcMob = (L2NpcInstance) spMob;
                            if (npcMob.isInsideRadius(caster, 1000, true, true) && npcMob.hasAI() && (npcMob.getAI().getIntention() == AI_INTENTION_ATTACK)) {
                                L2Object npcTarget = npcMob.getTarget();
                                for (L2Object target : targets) {
                                    if ((npcTarget == target) || (npcMob == target)) {
                                        npcMob.seeSpell(caster, target, skill);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.warn(e.getLocalizedMessage(), e);
        }
    }

    public void seeSpell(L2PcInstance caster, L2Object target, L2Skill skill) {
        if (this instanceof L2Attackable) {
            ((L2Attackable) this).addDamageHate(caster, 0, -skill.getAggroPoints());
        }
    }

    public boolean isBehind(L2Object target) {
        double angleChar, angleTarget, angleDiff, maxAngleDiff = 45;

        if (isNull(target)) {
            return false;
        }

        if (target instanceof L2Character) {
            L2Character target1 = (L2Character) target;
            angleChar = Util.calculateAngleFrom(target1, this);
            angleTarget = Util.convertHeadingToDegree(target1.getHeading());
            angleDiff = angleChar - angleTarget;
            if (angleDiff <= (-360 + maxAngleDiff)) {
                angleDiff += 360;
            }
            if (angleDiff >= (360 - maxAngleDiff)) {
                angleDiff -= 360;
            }
            if (Math.abs(angleDiff) <= maxAngleDiff) {
                logger.debug("Char {} is behind {}", getName(), target.getName());
                return true;
            }
        } else {
            logger.debug("isBehindTarget's target not an L2 Character.");
        }
        return false;
    }

    public boolean isBehindTarget() {
        return isBehind(getTarget());
    }

    public boolean isFront(L2Object target) {
        double angleChar, angleTarget, angleDiff, maxAngleDiff = 45;

        if (isNull(target)) {
            return false;
        }

        if (target instanceof L2Character) {
            L2Character target1 = (L2Character) target;
            angleChar = Util.calculateAngleFrom(target1, this);
            angleTarget = Util.convertHeadingToDegree(target1.getHeading());
            angleDiff = angleChar - angleTarget;
            if (angleDiff <= (-180 + maxAngleDiff)) {
                angleDiff += 180;
            }
            if (angleDiff >= (180 - maxAngleDiff)) {
                angleDiff -= 180;
            }
            if (Math.abs(angleDiff) <= maxAngleDiff) {
                logger.debug("Char {} is side {}", getName(), target.getName());
                return true;
            }
        } else {
            logger.debug("isSideTarget's target not an L2 Character.");
        }
        return false;
    }

    public boolean isFrontTarget() {
        return isFront(getTarget());
    }

    public double getLevelMod() {
        return 1;
    }

    public final void setSkillCast(Future<?> newSkillCast) {
        skillCast = newSkillCast;
    }


    public final void setSkillCastEndTime(int newSkillCastEndTime) {
        castEndTime = newSkillCastEndTime;
        // for interrupt -12 ticks; first removing the extra second and then -200 ms
        castInterruptTime = newSkillCastEndTime - 12;
    }

    private Future<?> _PvPRegTask;
    private long _pvpFlagLasts;

    public void setPvpFlagLasts(long time) {
        _pvpFlagLasts = time;
    }

    public long getPvpFlagLasts() {
        return _pvpFlagLasts;
    }

    public void startPvPFlag() {
        updatePvPFlag(1);

        _PvPRegTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new PvPFlag(), 1000, 1000);
    }

    public void stopPvpRegTask() {
        if (_PvPRegTask != null) {
            _PvPRegTask.cancel(true);
        }
    }

    public void stopPvPFlag() {
        stopPvpRegTask();

        updatePvPFlag(0);

        _PvPRegTask = null;
    }

    public void updatePvPFlag(int value) {
        if (!(this instanceof L2PcInstance)) {
            return;
        }
        L2PcInstance player = (L2PcInstance) this;
        if (player.getPvpFlag() == value) {
            return;
        }
        player.setPvpFlag(value);

        player.sendPacket(new UserInfo(player));
        for (L2PcInstance target : getKnownList().getKnownPlayers().values()) {
            target.sendPacket(new RelationChanged(player, player.getRelation(player), player.isAutoAttackable(target)));
        }
    }


    public final int getRandomDamage(L2Character target) {
        var weaponItem = getActiveWeaponInstance();

        if (isNull(weaponItem)) {
            return 5 + (int) Math.sqrt(getLevel());
        }

        return weaponItem.getRandomDamage();
    }

    @Override
    public String toString() {
        return "mob " + getObjectId();
    }

    public int getAttackEndTime() {
        return attackEndTime;
    }

    public abstract int getLevel();

    public final double calcStat(Stats stat, double init, L2Character target, L2Skill skill) {
        return getStat().calcStat(stat, init, target, skill);
    }

    public final float getAttackSpeedMultiplier() {
        return getStat().getAttackSpeedMultiplier();
    }

    public final double getCriticalDmg(L2Character target, double init) {
        return getStat().getCriticalDmg(target, init);
    }

    public final int getMagicalAttackRange(L2Skill skill) {
        return getStat().getMagicalAttackRange(skill);
    }

    public double getMReuseRate(L2Skill skill) {
        return getStat().getMReuseRate(skill);
    }

    public float getMovementSpeedMultiplier() {
        return getStat().getMovementSpeedMultiplier();
    }

    public double getPAtkAnimals(L2Character target) {
        return getStat().getPAtkAnimals(target);
    }

    public double getPAtkDragons(L2Character target) {
        return getStat().getPAtkDragons(target);
    }

    public double getPAtkInsects(L2Character target) {
        return getStat().getPAtkInsects(target);
    }

    public double getPAtkMonsters(L2Character target) {
        return getStat().getPAtkMonsters(target);
    }

    public double getPAtkPlants(L2Character target) {
        return getStat().getPAtkPlants(target);
    }

    public double getPAtkUndead(L2Character target) {
        return getStat().getPAtkUndead(target);
    }

    public double getPDefUndead(L2Character target) {
        return getStat().getPDefUndead(target);
    }

    public final int getPhysicalAttackRange() {
        return getStat().getPhysicalAttackRange();
    }

    public final int getShldDef() {
        return getStat().getShldDef();
    }

    public final int getWalkSpeed() {
        return getStat().getWalkSpeed();
    }

    public void addStatusListener(L2Character object) {
        getStatus().addStatusListener(object);
    }

    public void reduceCurrentHp(double i, L2Character attacker) {
        reduceCurrentHp(i, attacker, true);
    }

    public void reduceCurrentHp(double i, L2Character attacker, boolean awake) {
        if (Config.L2JMOD_CHAMPION_ENABLE && isChampion() && (Config.L2JMOD_CHAMPION_HP != 0)) {
            getStatus().reduceHp(i / Config.L2JMOD_CHAMPION_HP, attacker, awake);
        } else {
            getStatus().reduceHp(i, attacker, awake);
        }
    }

    public void reduceCurrentMp(double i) {
        getStatus().reduceMp(i);
    }

    public void removeStatusListener(L2Character object) {
        getStatus().removeStatusListener(object);
    }

    protected void stopHpMpRegeneration() {
        getStatus().stopHpMpRegeneration();
    }

    public final void setCurrentCp(Double newCp) {
        setCurrentCp((double) newCp);
    }

    public final void setCurrentCp(double newCp) {
        getStatus().setCurrentCp(newCp);
    }

    public final void setCurrentHp(double newHp) {
        getStatus().setCurrentHp(newHp);
    }

    public final void setCurrentHpMp(double newHp, double newMp) {
        getStatus().setCurrentHpMp(newHp, newMp);
    }

    public final void setCurrentMp(Double newMp) {
        setCurrentMp((double) newMp);
    }

    public final void setCurrentMp(double newMp) {
        getStatus().setCurrentMp(newMp);
    }

    public void setAiClass(String aiClass) {
        this.aiClass = aiClass;
    }

    public String getAiClass() {
        return aiClass;
    }

    public L2Character getLastBuffer() {
        return lastBuffer;
    }

    public void setChampion(boolean champ) {
        champion = champ;
    }

    public boolean isChampion() {
        return champion;
    }

    public int getLastHealAmount() {
        return lastHealAmount;
    }


    public void setLastBuffer(L2Character buffer) {
        lastBuffer = buffer;
    }

    public void setLastHealAmount(int hp) {
        lastHealAmount = hp;
    }

    public boolean reflectSkill(L2Skill skill) {
        double reflect = calcStat(skill.isMagic() ? Stats.REFLECT_SKILL_MAGIC : Stats.REFLECT_SKILL_PHYSIC, 0, null, null);
        if (Rnd.get(100) < reflect) {
            return true;
        }

        return false;
    }


    public void sendDamageMessage(L2Character target, int damage, boolean mcrit, boolean pcrit, boolean miss) {
    }


    public ForceBuff getForceBuff() {
        return null;
    }


    public void setForceBuff(ForceBuff fb) {
    }

    public final String getTitle() {
        return title;
    }

    public int getStrength() {
        return getStat().getSTR();
    }

    public int getDexterity() {
        return getStat().getDEX();
    }

    public int getConstitution() {
        return getStat().getCON();
    }

    public int getIntelligence() {
        return getStat().getINT();
    }

    public int getWisdom() {
        return getStat().getWIT();
    }

    public int getMentality() {
        return getStat().getMEN();
    }

    public int getMaxHp() {
        return getStat().getMaxHp();
    }

    public final double getCurrentHp() {
        return getStatus().getCurrentHp();
    }

    public int getMaxMp() {
        return getStat().getMaxMp();
    }

    public final double getCurrentMp() {
        return getStatus().getCurrentMp();
    }

    public final double getCurrentCp() {
        return getStatus().getCurrentCp();
    }

    public final int getMaxCp() {
        return getStat().getMaxCp();
    }

    public int getPAtk(L2Character target) {
        return getStat().getPAtk(target);
    }

    public int getPAtkSpd() {
        return getStat().getPAtkSpd();
    }

    public int getPDef(L2Character target) {
        return getStat().getPDef(target);
    }

    public int getEvasionRate(L2Character target) {
        return getStat().getEvasionRate(target);
    }

    public int getAccuracy() {
        return getStat().getAccuracy();
    }

    public int getCriticalHit(L2Character target, L2Skill skill) {
        return getStat().getCriticalHit(target, skill);
    }

    public int getMAtk(L2Character target, L2Skill skill) {
        return getStat().getMAtk(target, skill);
    }

    public int getMAtkSpd() {
        return getStat().getMAtkSpd();
    }

    public int getMDef(L2Character target, L2Skill skill) {
        return getStat().getMDef(target, skill);
    }

    public final int getMCriticalHit(L2Character target, L2Skill skill) {
        return getStat().getMCriticalHit(target, skill);
    }

    public float getFireDefense() {
        return template.getFireDefense();
    }

    public float getWaterDefense() {
        return template.getWaterDefense();
    }

    public float getWindDefense() {
        return template.getWindDefense();
    }

    public float getEarthDefense() {
        return template.getEarthDefense();
    }

    public float getHolyDefense() {
        return template.getHolyDefense();
    }

    public float getUnholyDefense() {
        return template.getUnholyDefense();
    }

    public int getRunSpeed() {
        return getStat().getRunSpeed();
    }

    public final boolean isRunning() {
        return isRunning;
    }
}