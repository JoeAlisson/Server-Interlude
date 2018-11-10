package org.l2j.gameserver.model.actor.instance;

import org.l2j.commons.Config;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Olympiad;
import org.l2j.gameserver.SevenSigns;
import org.l2j.gameserver.SevenSignsFestival;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.ai.Intention;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.datatables.*;
import org.l2j.gameserver.factory.IdFactory;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.instancemanager.DimensionalRiftManager;
import org.l2j.gameserver.instancemanager.QuestManager;
import org.l2j.gameserver.instancemanager.TownManager;
import org.l2j.gameserver.instancemanager.games.Lottery;
import org.l2j.gameserver.model.*;
import org.l2j.gameserver.model.L2Skill.SkillType;
import org.l2j.gameserver.model.actor.knownlist.NpcKnownList;
import org.l2j.gameserver.model.actor.stat.NpcStat;
import org.l2j.gameserver.model.actor.status.NpcStatus;
import org.l2j.gameserver.model.base.CreatureRace;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.entity.L2Event;
import org.l2j.gameserver.model.entity.database.CharTemplate;
import org.l2j.gameserver.model.entity.database.NpcTemplate;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.zone.type.L2TownZone;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.skills.Stats;
import org.l2j.gameserver.taskmanager.DecayTaskManager;
import org.l2j.gameserver.templates.base.NpcType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.gameserver.ai.Intention.AI_INTENTION_ACTIVE;
import static org.l2j.gameserver.templates.base.NpcType.L2Auctioneer;

/**
 * This class represents a Non-Player-Character in the world. It can be a monster or a friendly character. It also uses a template to fetch some static values. The templates are hardcoded in the client, so we can rely on them.<BR>
 * <BR>
 * L2Character :<BR>
 * <BR>
 * <li>L2Attackable</li> <li>L2FolkInstance</li>
 *
 * @version $Revision: 1.32.2.7.2.24 $ $Date: 2005/04/11 10:06:09 $
 */
public class L2NpcInstance extends L2Character<NpcTemplate> {

    private static final Logger logger = LoggerFactory.getLogger(L2NpcInstance.class);
    public static final int INTERACTION_DISTANCE = 150;

    private L2Spawn spawn;

    private boolean isBusy = false;

    private String busyMessage = "";

    volatile boolean isDecayed = false;

    private boolean isSpoil = false;

    private int castleIndex = -2;

    public boolean isEventMob = false;
    private boolean isInTown = false;

    private int isSpoiledBy = 0;

    private RandomAnimationTask _rAniTask = null;
    private int currentLHandId; // normally this shouldn't change from the template, but there exist exceptions
    private int currentRHandId; // normally this shouldn't change from the template, but there exist exceptions
    private float currentCollisionHeight; // used for npc grow effect skills
    private float currentCollisionRadius; // used for npc grow effect skills

    public Quest[] getEventQuests(Quest.QuestEventType eventType) {
        return template.getEventQuests(eventType);
    }

    public int getNpcTemplateId() {
        return template.getTemplateId();
    }

    public boolean isServerSideName() {
        return template.isServerSideName();
    }

    public boolean isServerSideTitle() {
        return template.isServerSideTitle();
    }

    public double getVulnerability(Stats stat) {
        return template.getVulnerability(stat);
    }

    protected class RandomAnimationTask implements Runnable {
        @Override
        public void run() {
            try {
                if (this != _rAniTask) {
                    return; // Shouldn't happen, but who knows... just to make sure every active npc has only one timer.
                }
                if (isMob()) {
                    if (getAI().getIntention() != AI_INTENTION_ACTIVE) {
                        return;
                    }
                } else {
                    if (!isInActiveRegion()) {
                        return;
                    }
                    getKnownList().updateKnownObjects();
                }

                if (!(isDead() || isStunned() || isSleeping() || isParalyzed())) {
                    onRandomAnimation();
                }

                startRandomAnimationTimer();
            } catch (Throwable t) {
                logger.error(t.getLocalizedMessage(), t);
            }
        }
    }


    private void onRandomAnimation() {
        SocialAction sa = new SocialAction(getObjectId(), Rnd.get(2, 3));
        broadcastPacket(sa);
    }


    public void startRandomAnimationTimer() {
        if (!hasRandomAnimation()) {
            return;
        }

        int minWait = isMob() ? Config.MIN_MONSTER_ANIMATION : Config.MIN_NPC_ANIMATION;
        int maxWait = isMob() ? Config.MAX_MONSTER_ANIMATION : Config.MAX_NPC_ANIMATION;

        int interval = Rnd.get(minWait, maxWait) * 1000;

        _rAniTask = new RandomAnimationTask();
        ThreadPoolManager.getInstance().scheduleGeneral(_rAniTask, interval);
    }

    public boolean hasRandomAnimation() {
        return (Config.MAX_NPC_ANIMATION > 0);
    }

    public L2NpcInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
        if (isNull(template)) {
            logger.error("No template for NpcTemplate. Please check your datapack is setup correctly.");
            return;
        }
        getKnownList(); // init knownlist
        getStat(); // init stats
        getStatus(); // init status
        initCharStatusUpdateValues();

        currentLHandId = template.getLhand();
        currentRHandId = template.getRhand();
        currentCollisionHeight = template.getCollisionHeight();
        currentCollisionRadius = template.getCollisionRadius();

        setName(template.getName());

    }

    @Override
    protected void initSkillsStat(CharTemplate template) {
        calculators = NPC_STD_CALCULATOR;

        skills = ((NpcTemplate) template).getSkills();
        if (nonNull(skills)) {
            for (Map.Entry<Integer, L2Skill> skill : skills.entrySet()) {
                addStatFuncs(skill.getValue().getStatFuncs(null, this));
            }
        }
    }

    @Override
    public NpcKnownList getKnownList() {
        if ((super.getKnownList() == null) || !(super.getKnownList() instanceof NpcKnownList)) {
            setKnownList(new NpcKnownList(this));
        }
        return (NpcKnownList) super.getKnownList();
    }

    @Override
    public NpcStat getStat() {
        if ((super.getStat() == null) || !(super.getStat() instanceof NpcStat)) {
            setStat(new NpcStat(this));
        }
        return (NpcStat) super.getStat();
    }

    @Override
    public NpcStatus getStatus() {
        if ((super.getStatus() == null) || !(super.getStatus() instanceof NpcStatus)) {
            setStatus(new NpcStatus(this));
        }
        return (NpcStatus) super.getStatus();
    }

    public int getNpcId() {
        return template.getId();
    }

    @Override
    public boolean isAttackable() {
        return false;
    }


    public final String getFactionId() {
        return template.getFactionId();
    }

    @Override
    public final int getLevel() {
        return template.getLevel();
    }

    public boolean isAggressive() {
        return false;
    }

    public int getAggroRange() {
        return template.getAggro();
    }

    public int getFactionRange() {
        return template.getFactionRange();
    }

    public boolean isUndead() {
        return template.isUndead();
    }

    @Override
    public void updateAbnormalEffect() {
        for (L2PcInstance player : getKnownList().getKnownPlayers().values()) {
            if (player != null) {
                player.sendPacket(new NpcInfo(this, player));
            }
        }
    }


    public int getDistanceToWatchObject(L2Object object) {
        if (object instanceof L2FestivalGuideInstance) {
            return 10000;
        }

        if ((object instanceof L2FolkInstance) || !(object instanceof L2Character)) {
            return 0;
        }

        if (object instanceof L2PlayableInstance) {
            return 1500;
        }

        return 500;
    }

    public int getDistanceToForgetObject(L2Object object) {
        return 2 * getDistanceToWatchObject(object);
    }

    @Override
    public boolean isAutoAttackable(L2Character attacker) {
        return false;
    }

    public int getLeftHandItem() {
        return currentLHandId;
    }

    public int getRightHandItem() {
        return currentRHandId;
    }

    public boolean isSpoil() {
        return isSpoil;
    }

    public void setSpoil(boolean isSpoil) {
        this.isSpoil = isSpoil;
    }

    public final int getIsSpoiledBy() {
        return isSpoiledBy;
    }

    public final void setIsSpoiledBy(int value) {
        isSpoiledBy = value;
    }

    public final boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean isBusy) {
        this.isBusy = isBusy;
    }

    private final String getBusyMessage() {
        return busyMessage;
    }


    public void setBusyMessage(String message) {
        busyMessage = message;
    }

    protected boolean canTarget(L2PcInstance player) {
        if (player.isOutOfControl()) {
            player.sendPacket(new ActionFailed());
            return false;
        }

        return true;
    }

    protected boolean canInteract(L2PcInstance player) {
        // TODO: NPC busy check etc...

        if (!isInsideRadius(player, INTERACTION_DISTANCE, false, false)) {
            return false;
        }

        return true;
    }

    @Override
    public void onAction(L2PcInstance player) {
        if (!canTarget(player)) {
            return;
        }

        if (this != player.getTarget()) {
            logger.debug("new target selected: {}", getObjectId());

            player.setTarget(this);

            if (isAutoAttackable(player)) {
                MyTargetSelected my = new MyTargetSelected(getObjectId(), player.getLevel() - getLevel());
                player.sendPacket(my);

                StatusUpdate su = new StatusUpdate(getObjectId());
                su.addAttribute(StatusUpdate.CUR_HP, (int) getCurrentHp());
                su.addAttribute(StatusUpdate.MAX_HP, getMaxHp());
                player.sendPacket(su);
            } else {
                MyTargetSelected my = new MyTargetSelected(getObjectId(), 0);
                player.sendPacket(my);
            }

            player.sendPacket(new ValidateLocation(this));
        } else {
            player.sendPacket(new ValidateLocation(this));
            if (isAutoAttackable(player) && !isAlikeDead()) {

                if (Math.abs(player.getZ() - getZ()) < 400) // this max heigth difference might need some tweaking
                {
                    player.getAI().setIntention(Intention.AI_INTENTION_ATTACK, this);
                } else {
                    player.sendPacket(new ActionFailed());
                }
            } else if (!isAutoAttackable(player)) {
                if (!canInteract(player)) {
                    player.getAI().setIntention(Intention.AI_INTENTION_INTERACT, this);
                } else {
                    SocialAction sa = new SocialAction(getObjectId(), Rnd.get(8));
                    broadcastPacket(sa);

                    if (isEventMob) {
                        L2Event.showEventHtml(player, String.valueOf(getObjectId()));
                    } else {
                        Quest[] qlst = getEventQuests(Quest.QuestEventType.NPC_FIRST_TALK);
                        if ((qlst != null) && (qlst.length == 1)) {
                            qlst[0].notifyFirstTalk(this, player);
                        } else {
                            showChatWindow(player, 0);
                        }
                    }
                }
            } else {
                player.sendPacket(new ActionFailed());
            }
        }
    }

    @Override
    public void onActionShift(L2GameClient client) {
        L2PcInstance player = client.getActiveChar();
        if (isNull(player)) {
            return;
        }

        if (player.getAccessLevel() >= Config.GM_ACCESSLEVEL) {
            player.setTarget(this);


            MyTargetSelected my = new MyTargetSelected(getObjectId(), player.getLevel() - getLevel());
            player.sendPacket(my);

            if (isAutoAttackable(player)) {
                StatusUpdate su = new StatusUpdate(getObjectId());
                su.addAttribute(StatusUpdate.CUR_HP, (int) getCurrentHp());
                su.addAttribute(StatusUpdate.MAX_HP, getMaxHp());
                player.sendPacket(su);
            }


            NpcHtmlMessage html = new NpcHtmlMessage(0);
            StringBuilder html1 = new StringBuilder("<html><body><center><font color=\"LEVEL\">NPC Information</font></center>");
            String className = getClass().getName().substring(43);
            html1.append("<br>");

            html1.append("Instance Type: " + className + "<br1>Faction: " + getFactionId() + "<br1>Location ID: " + (getSpawn() != null ? getSpawn().getLocation() : 0) + "<br1>");

            if (this instanceof L2ControllableMobInstance) {
                html1.append("Mob Group: " + MobGroupTable.getInstance().getGroupForMob((L2ControllableMobInstance) this).getGroupId() + "<br>");
            } else {
                html1.append("Respawn Time: " + (getSpawn() != null ? (getSpawn().getRespawnDelay() / 1000) + "  Seconds<br>" : "?  Seconds<br>"));
            }

            html1.append("<table border=\"0\" width=\"100%\">");
            html1.append("<tr><td>Object ID</td><td>" + getObjectId() + "</td><td>NPC ID</td><td>" +  getNpcId() + "</td></tr>");
            html1.append("<tr><td>Castle</td><td>" + getCastle().getCastleId() + "</td><td>Coords</td><td>" + getX() + "," + getY() + "," + getZ() + "</td></tr>");
            html1.append("<tr><td>Level</td><td>" + getLevel() + "</td><td>Aggro</td><td>" + ((this instanceof L2Attackable) ? ((L2Attackable) this).getAggroRange() : 0) + "</td></tr>");
            html1.append("</table><br>");

            html1.append("<font color=\"LEVEL\">Combat</font>");
            html1.append("<table border=\"0\" width=\"100%\">");
            html1.append("<tr><td>Current HP</td><td>" + getCurrentHp() + "</td><td>Current MP</td><td>" + getCurrentMp() + "</td></tr>");
            html1.append("<tr><td>Max.HP</td><td>" + (int) (getMaxHp() / getStat().calcStat(Stats.MAX_HP, 1, this, null)) + "*" + getStat().calcStat(Stats.MAX_HP, 1, this, null) + "</td><td>Max.MP</td><td>" + getMaxMp() + "</td></tr>");
            html1.append("<tr><td>P.Atk.</td><td>" + getPAtk(null) + "</td><td>M.Atk.</td><td>" + getMAtk(null, null) + "</td></tr>");
            html1.append("<tr><td>P.Def.</td><td>" + getPDef(null) + "</td><td>M.Def.</td><td>" + getMDef(null, null) + "</td></tr>");
            html1.append("<tr><td>Accuracy</td><td>" + getAccuracy() + "</td><td>Evasion</td><td>" + getEvasionRate(null) + "</td></tr>");
            html1.append("<tr><td>Critical</td><td>" + getCriticalHit(null, null) + "</td><td>Speed</td><td>" + getRunSpeed() + "</td></tr>");
            html1.append("<tr><td>Atk.Speed</td><td>" + getPAtkSpd() + "</td><td>Cast.Speed</td><td>" + getMAtkSpd() + "</td></tr>");
            html1.append("</table><br>");

            html1.append("<font color=\"LEVEL\">Basic Stats</font>");
            html1.append("<table border=\"0\" width=\"100%\">");
            html1.append("<tr><td>STR</td><td>" + getStrength() + "</td><td>DEX</td><td>" + getDexterity() + "</td><td>CON</td><td>" + getConstitution() + "</td></tr>");
            html1.append("<tr><td>INT</td><td>" + getIntelligence() + "</td><td>WIT</td><td>" + getWisdom() + "</td><td>MEN</td><td>" + getMentality() + "</td></tr>");
            html1.append("</table>");

            html1.append("<br><center><table><tr><td><button value=\"Edit NPC\" action=\"bypass -h admin_edit_npc " + getNpcId() + "\" width=100 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"><br1></td>");
            html1.append("<td><button value=\"Kill\" action=\"bypass -h admin_kill\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td><br1></tr>");
            html1.append("<tr><td><button value=\"Show DropList\" action=\"bypass -h admin_show_droplist " + getNpcId() + "\" width=100 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
            html1.append("<td><button value=\"Delete\" action=\"bypass -h admin_delete\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
            html1.append("</table></center><br>");
            html1.append("</body></html>");

            html.setHtml(html1.toString());
            player.sendPacket(html);
        } else if (Config.ALT_GAME_VIEWNPC) {
            // Set the target of the L2PcInstance reader
            player.setTarget(this);

            // Send a Server->Client packet MyTargetSelected to the L2PcInstance reader
            // The reader.getLevel() - getLevel() permit to display the correct color in the select window
            MyTargetSelected my = new MyTargetSelected(getObjectId(), player.getLevel() - getLevel());
            player.sendPacket(my);

            // Check if the reader is attackable (without a forced attack)
            if (isAutoAttackable(player)) {
                // Send a Server->Client packet StatusUpdate of the L2NpcInstance to the L2PcInstance to update its HP bar
                StatusUpdate su = new StatusUpdate(getObjectId());
                su.addAttribute(StatusUpdate.CUR_HP, (int) getCurrentHp());
                su.addAttribute(StatusUpdate.MAX_HP, getMaxHp());
                player.sendPacket(su);
            }

            NpcHtmlMessage html = new NpcHtmlMessage(0);
            StringBuilder html1 = new StringBuilder("<html><body>");

            html1.append("<br><center><font color=\"LEVEL\">[Combat Stats]</font></center>");
            html1.append("<table border=0 width=\"100%\">");
            html1.append("<tr><td>Max.HP</td><td>" + (int) (getMaxHp() / getStat().calcStat(Stats.MAX_HP, 1, this, null)) + "*" + (int) getStat().calcStat(Stats.MAX_HP, 1, this, null) + "</td><td>Max.MP</td><td>" + getMaxMp() + "</td></tr>");
            html1.append("<tr><td>P.Atk.</td><td>" + getPAtk(null) + "</td><td>M.Atk.</td><td>" + getMAtk(null, null) + "</td></tr>");
            html1.append("<tr><td>P.Def.</td><td>" + getPDef(null) + "</td><td>M.Def.</td><td>" + getMDef(null, null) + "</td></tr>");
            html1.append("<tr><td>Accuracy</td><td>" + getAccuracy() + "</td><td>Evasion</td><td>" + getEvasionRate(null) + "</td></tr>");
            html1.append("<tr><td>Critical</td><td>" + getCriticalHit(null, null) + "</td><td>Speed</td><td>" + getRunSpeed() + "</td></tr>");
            html1.append("<tr><td>Atk.Speed</td><td>" + getPAtkSpd() + "</td><td>Cast.Speed</td><td>" + getMAtkSpd() + "</td></tr>");
            html1.append("<tr><td>CreatureRace</td><td>" + getRace() + "</td><td></td><td></td></tr>");
            html1.append("</table>");

            html1.append("<br><center><font color=\"LEVEL\">[Basic Stats]</font></center>");
            html1.append("<table border=0 width=\"100%\">");
            html1.append("<tr><td>STR</td><td>" + getStrength() + "</td><td>DEX</td><td>" + getDexterity() + "</td><td>CON</td><td>" + getConstitution() + "</td></tr>");
            html1.append("<tr><td>INT</td><td>" + getIntelligence() + "</td><td>WIT</td><td>" + getWisdom() + "</td><td>MEN</td><td>" + getMentality() + "</td></tr>");
            html1.append("</table>");

            html1.append("<br><center><font color=\"LEVEL\">[Drop Info]</font></center>");
            html1.append("Rates legend: <font color=\"ff0000\">50%+</font> <font color=\"00ff00\">30%+</font> <font color=\"0000ff\">less than 30%</font>");
            html1.append("<table border=0 width=\"100%\">");

            for (L2DropCategory cat : getDropCategories().values()) {
                for (L2DropData drop : cat.getAllDrops()) {
                    String name = ItemTable.getInstance().getTemplate(drop.getItemId()).getName();

                    if (drop.getChance() >= 600000) {
                        html1.append("<tr><td><font color=\"ff0000\">" + name + "</font></td><td>" + (drop.isQuestDrop() ? "Quest" : (cat.isSweep() ? "Sweep" : "Drop")) + "</td></tr>");
                    } else if (drop.getChance() >= 300000) {
                        html1.append("<tr><td><font color=\"00ff00\">" + name + "</font></td><td>" + (drop.isQuestDrop() ? "Quest" : (cat.isSweep() ? "Sweep" : "Drop")) + "</td></tr>");
                    } else {
                        html1.append("<tr><td><font color=\"0000ff\">" + name + "</font></td><td>" + (drop.isQuestDrop() ? "Quest" : (cat.isSweep() ? "Sweep" : "Drop")) + "</td></tr>");
                    }
                }
            }

            html1.append("</table>");
            html1.append("</body></html>");

            html.setHtml(html1.toString());
            player.sendPacket(html);
        }

        // Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
        player.sendPacket(new ActionFailed());
    }

    private Map<Integer, L2DropCategory> getDropCategories() {
        return template.getDropCategories();
    }

    public CreatureRace getRace() {
        return template.getRace();
    }

    /**
     * Return the L2Castle this L2NpcInstance belongs to.
     *
     * @return
     */
    public final Castle getCastle() {
        // Get castle this NPC belongs to (excluding L2Attackable)
        if (castleIndex < 0) {
            L2TownZone town = TownManager.getInstance().getTown(getX(), getY(), getZ());

            if (town != null) {
                castleIndex = CastleManager.getInstance().getCastleIndex(town.getTaxById());
            }

            if (castleIndex < 0) {
                castleIndex = CastleManager.getInstance().findNearestCastleIndex(this);
            } else {
                isInTown = true; // NpcTemplate was spawned in town
            }
        }

        if (castleIndex < 0) {
            return null;
        }

        return CastleManager.getInstance().getCastles().get(castleIndex);
    }

    public final boolean getIsInTown() {
        if (castleIndex < 0) {
            getCastle();
        }
        return isInTown;
    }


    public void onBypassFeedback(L2PcInstance player, String command) {
        // if (canInteract(reader))
        {
            if (isBusy() && (getBusyMessage().length() > 0)) {
                player.sendPacket(new ActionFailed());

                NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
                html.setFile("data/html/npcbusy.htm");
                html.replace("%busymessage%", getBusyMessage());
                html.replace("%npcname%", getName());
                html.replace("%playername%", player.getName());
                player.sendPacket(html);
            } else if (command.equalsIgnoreCase("TerritoryStatus")) {
                NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
                {
                    if (getCastle().getOwnerId() > 0) {
                        html.setFile("data/html/territorystatus.htm");
                        L2Clan clan = ClanTable.getInstance().getClan(getCastle().getOwnerId());
                        html.replace("%clanname%", clan.getName());
                        html.replace("%clanleadername%", clan.getLeaderName());
                    } else {
                        html.setFile("data/html/territorynoclan.htm");
                    }
                }
                html.replace("%castlename%", getCastle().getName());
                html.replace("%taxpercent%", "" + getCastle().getTaxPercent());
                html.replace("%objectId%", String.valueOf(getObjectId()));
                {
                    if (getCastle().getCastleId() > 6) {
                        html.replace("%territory%", "The Kingdom of Elmore");
                    } else {
                        html.replace("%territory%", "The Kingdom of Aden");
                    }
                }
                player.sendPacket(html);
            } else if (command.startsWith("Quest")) {
                String quest = "";
                try {
                    quest = command.substring(5).trim();
                } catch (IndexOutOfBoundsException ioobe) {
                }
                if (quest.length() == 0) {
                    showQuestWindow(player);
                } else {
                    showQuestWindow(player, quest);
                }
            } else if (command.startsWith("Chat")) {
                int val = 0;
                try {
                    val = Integer.parseInt(command.substring(5));
                } catch (IndexOutOfBoundsException ioobe) {
                } catch (NumberFormatException nfe) {
                }
                showChatWindow(player, val);
            } else if (command.startsWith("Link")) {
                String path = command.substring(5).trim();
                if (path.indexOf("..") != -1) {
                    return;
                }
                String filename = "data/html/" + path;
                NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
                html.setFile(filename);
                html.replace("%objectId%", String.valueOf(getObjectId()));
                player.sendPacket(html);
            } else if (command.startsWith("NobleTeleport")) {
                if (!player.isNoble()) {
                    String filename = "data/html/teleporter/nobleteleporter-no.htm";
                    NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
                    html.setFile(filename);
                    html.replace("%objectId%", String.valueOf(getObjectId()));
                    html.replace("%npcname%", getName());
                    player.sendPacket(html);
                    return;
                }
                int val = 0;
                try {
                    val = Integer.parseInt(command.substring(5));
                } catch (IndexOutOfBoundsException ioobe) {
                } catch (NumberFormatException nfe) {
                }
                showChatWindow(player, val);
            } else if (command.startsWith("Loto")) {
                int val = 0;
                try {
                    val = Integer.parseInt(command.substring(5));
                } catch (IndexOutOfBoundsException ioobe) {
                } catch (NumberFormatException nfe) {
                }
                if (val == 0) {
                    // new loto ticket
                    for (int i = 0; i < 5; i++) {
                        player.setLoto(i, 0);
                    }
                }
                showLotoWindow(player, val);
            } else if (command.startsWith("CPRecovery")) {
                makeCPRecovery(player);
            } else if (command.startsWith("SupportMagic")) {
                makeSupportMagic(player);
            } else if (command.startsWith("multisell")) {
                L2Multisell.getInstance().SeparateAndSend(Integer.parseInt(command.substring(9).trim()), player, false, getCastle().getTaxRate());
            } else if (command.startsWith("exc_multisell")) {
                L2Multisell.getInstance().SeparateAndSend(Integer.parseInt(command.substring(13).trim()), player, true, getCastle().getTaxRate());
            } else if (command.startsWith("Augment")) {
                int cmdChoice = Integer.parseInt(command.substring(8, 9).trim());
                switch (cmdChoice) {
                    case 1:
                        player.sendPacket(new SystemMessage(SystemMessageId.SELECT_THE_ITEM_TO_BE_AUGMENTED));
                        player.sendPacket(new ExShowVariationMakeWindow());
                        break;
                    case 2:
                        player.sendPacket(new SystemMessage(SystemMessageId.SELECT_THE_ITEM_FROM_WHICH_YOU_WISH_TO_REMOVE_AUGMENTATION));
                        player.sendPacket(new ExShowVariationCancelWindow());
                        break;
                }
            } else if (command.startsWith("npcfind_byid")) {
                try {
                    L2Spawn spawn = SpawnTable.getInstance().getTemplate(Integer.parseInt(command.substring(12).trim()));

                    if (spawn != null) {
                        player.sendPacket(new RadarControl(0, 1, spawn.getLocx(), spawn.getLocy(), spawn.getLocz()));
                    }
                } catch (NumberFormatException nfe) {
                    player.sendMessage("Wrong command parameters");
                }
            } else if (command.startsWith("EnterRift")) {
                try {
                    Byte b1 = Byte.parseByte(command.substring(10)); // Selected Area: Recruit, Soldier etc
                    DimensionalRiftManager.getInstance().start(player, b1, this);
                } catch (Exception e) {
                }
            } else if (command.startsWith("ChangeRiftRoom")) {
                if (player.isInParty() && player.getParty().isInDimensionalRift()) {
                    player.getParty().getDimensionalRift().manualTeleport(player, this);
                } else {
                    DimensionalRiftManager.getInstance().handleCheat(player, this);
                }
            } else if (command.startsWith("ExitRift")) {
                if (player.isInParty() && player.getParty().isInDimensionalRift()) {
                    player.getParty().getDimensionalRift().manualExitRift(player, this);
                } else {
                    DimensionalRiftManager.getInstance().handleCheat(player, this);
                }
            }
        }
    }


    @Override
    public L2ItemInstance getActiveWeaponInstance() {
        // regular NPCs dont have weapons instancies
        return null;
    }

    @Override
    public L2ItemInstance getSecondaryWeaponInstance() {
        // regular NPCs dont have weapons instancies
        return null;
    }

    public void insertObjectIdAndShowChatWindow(L2PcInstance player, String content) {
        content = content.replaceAll("%objectId%", String.valueOf(getObjectId()));
        NpcHtmlMessage npcReply = new NpcHtmlMessage(getObjectId());
        npcReply.setHtml(content);
        player.sendPacket(npcReply);
    }

    public String getHtmlPath(int npcId, int val) {
        String pom = "";

        if (val == 0) {
            pom = "" + npcId;
        } else {
            pom = npcId + "-" + val;
        }

        String temp = "data/html/default/" + pom + ".htm";

        if (!Config.LAZY_CACHE) {
            // If not running lazy cache the file must be in the cache or it doesnt exist
            if (HtmCache.getInstance().contains(temp)) {
                return temp;
            }
        } else {
            if (HtmCache.getInstance().isLoadable(temp)) {
                return temp;
            }
        }

        return "data/html/npcdefault.htm";
    }

    public void showQuestChooseWindow(L2PcInstance player, Quest[] quests) {
        StringBuilder sb = new StringBuilder();

        sb.append("<html><body><title>Talk about:</title><br>");

        for (Quest q : quests) {
            sb.append("<a action=\"bypass -h npc_").append(getObjectId()).append("_Quest ").append(q.getName()).append("\">").append(q.getDescr()).append("</a><br>");
        }

        sb.append("</body></html>");

        // Send a Server->Client packet NpcHtmlMessage to the L2PcInstance in order to display the message of the L2NpcInstance
        insertObjectIdAndShowChatWindow(player, sb.toString());
    }


    public void showQuestWindow(L2PcInstance player, String questId) {
        String content;

        Quest q = QuestManager.getInstance().getQuest(questId);

        if ((player.getWeightPenalty() >= 3) && (q.getQuestIntId() >= 1) && (q.getQuestIntId() < 1000)) {
            player.sendPacket(new SystemMessage(SystemMessageId.INVENTORY_LESS_THAN_80_PERCENT));
            return;
        }

        // FileInputStream fis = null;

        // Get the state of the selected quest
        QuestState qs = player.getQuestState(questId);

        if (qs != null) {
            // If the quest is alreday started, no need to show a window
            if (!qs.getQuest().notifyTalk(this, qs)) {
                return;
            }
        } else {
            if (q != null) {
                // check for start point
                Quest[] qlst = getEventQuests(Quest.QuestEventType.QUEST_START);

                if ((qlst != null) && (qlst.length > 0)) {
                    for (Quest element : qlst) {
                        if (element == q) {
                            qs = q.newQuestState(player);
                            // disabled by mr. becouse quest dialog only show on second click.
                            // if(qs.getState().getName().equalsIgnoreCase("completed"))
                            // {
                            if (!qs.getQuest().notifyTalk(this, qs)) {
                                return; // no need to show a window
                            }
                            // }
                            break;
                        }
                    }
                }
            }
        }

        if (qs == null) {
            // no quests found
            content = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>";
        } else {
            questId = qs.getQuest().getName();
            String stateId = qs.getStateId();
            String path = "data/jscript/quests/" + questId + "/" + stateId + ".htm";
            content = HtmCache.getInstance().getHtm(path); // TODO path for quests html

            if (Config.DEBUG) {
                if (content != null) {
                    logger.debug("Showing quest window for quest " + questId + " html path: " + path);
                } else {
                    logger.debug("File not exists for quest " + questId + " html path: " + path);
                }
            }
        }

        // Send a Server->Client packet NpcHtmlMessage to the L2PcInstance in order to display the message of the L2NpcInstance
        if (content != null) {
            insertObjectIdAndShowChatWindow(player, content);
        }

        // Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
        player.sendPacket(new ActionFailed());
    }

    /**
     * Collect awaiting quests/start points and display a QuestChooseWindow (if several available) or QuestWindow.<BR>
     * <BR>
     *
     * @param player The L2PcInstance that talk with the L2NpcInstance
     */
    public void showQuestWindow(L2PcInstance player) {
        // collect awaiting quests and start points
        List<Quest> options = new LinkedList<>();

        QuestState[] awaits = player.getQuestsForTalk(getNpcId());
        Quest[] starts = getEventQuests(Quest.QuestEventType.QUEST_START);

        // Quests are limited between 1 and 999 because those are the quests that are supported by the client.
        // By limitting them there, we are allowed to create custom quests at higher IDs without interfering
        if (awaits != null) {
            for (QuestState x : awaits) {
                if (!options.contains(x)) {
                    if ((x.getQuest().getQuestIntId() > 0) && (x.getQuest().getQuestIntId() < 1000)) {
                        options.add(x.getQuest());
                    }
                }
            }
        }

        if (starts != null) {
            for (Quest x : starts) {
                if (!options.contains(x)) {
                    if ((x.getQuestIntId() > 0) && (x.getQuestIntId() < 1000)) {
                        options.add(x);
                    }
                }
            }
        }

        // Display a QuestChooseWindow (if several quests are available) or QuestWindow
        if (options.size() > 1) {
            showQuestChooseWindow(player, options.toArray(new Quest[options.size()]));
        } else if (options.size() == 1) {
            showQuestWindow(player, options.get(0).getName());
        } else {
            showQuestWindow(player, "");
        }
    }

    /**
     * Open a Loto window on client with the text of the L2NpcInstance.<BR>
     * <BR>
     * <B><U> Actions</U> :</B><BR>
     * <BR>
     * <li>Get the text of the selected HTML file in function of the npcId and of the page number</li> <li>Send a Server->Client NpcHtmlMessage containing the text of the L2NpcInstance to the L2PcInstance</li> <li>Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the
     * client wait another packet</li><BR>
     *
     * @param player The L2PcInstance that talk with the L2NpcInstance
     * @param val    The number of the page of the L2NpcInstance to display
     */
    // 0 - first buy lottery ticket window
    // 1-20 - buttons
    // 21 - second buy lottery ticket window
    // 22 - selected ticket with 5 numbers
    // 23 - current lottery jackpot
    // 24 - Previous winning numbers/Prize claim
    // >24 - check lottery ticket by item object id
    public void showLotoWindow(L2PcInstance player, int val) {
        int npcId = getNpcId();
        String filename;
        SystemMessage sm;
        NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());

        if (val == 0) // 0 - first buy lottery ticket window
        {
            filename = (getHtmlPath(npcId, 1));
            html.setFile(filename);
        } else if ((val >= 1) && (val <= 21)) // 1-20 - buttons, 21 - second buy lottery ticket window
        {
            if (!Lottery.getInstance().isStarted()) {
                // tickets can't be sold
                player.sendPacket(new SystemMessage(SystemMessageId.NO_LOTTERY_TICKETS_CURRENT_SOLD));
                return;
            }
            if (!Lottery.getInstance().isSellableTickets()) {
                // tickets can't be sold
                player.sendPacket(new SystemMessage(SystemMessageId.NO_LOTTERY_TICKETS_AVAILABLE));
                return;
            }

            filename = (getHtmlPath(npcId, 5));
            html.setFile(filename);

            int count = 0;
            int found = 0;
            // counting buttons and unsetting button if found
            for (int i = 0; i < 5; i++) {
                if (player.getLoto(i) == val) {
                    // unsetting button
                    player.setLoto(i, 0);
                    found = 1;
                } else if (player.getLoto(i) > 0) {
                    count++;
                }
            }

            // if not rearched limit 5 and not unseted value
            if ((count < 5) && (found == 0) && (val <= 20)) {
                for (int i = 0; i < 5; i++) {
                    if (player.getLoto(i) == 0) {
                        player.setLoto(i, val);
                        break;
                    }
                }
            }

            // setting pusshed buttons
            count = 0;
            for (int i = 0; i < 5; i++) {
                if (player.getLoto(i) > 0) {
                    count++;
                    String button = String.valueOf(player.getLoto(i));
                    if (player.getLoto(i) < 10) {
                        button = "0" + button;
                    }
                    String search = "fore=\"L2UI.lottoNum" + button + "\" back=\"L2UI.lottoNum" + button + "a_check\"";
                    String replace = "fore=\"L2UI.lottoNum" + button + "a_check\" back=\"L2UI.lottoNum" + button + "\"";
                    html.replace(search, replace);
                }
            }

            if (count == 5) {
                String search = "0\">Return";
                String replace = "22\">The winner selected the numbers above.";
                html.replace(search, replace);
            }
        } else if (val == 22) // 22 - selected ticket with 5 numbers
        {
            if (!Lottery.getInstance().isStarted()) {
                // tickets can't be sold
                player.sendPacket(new SystemMessage(SystemMessageId.NO_LOTTERY_TICKETS_CURRENT_SOLD));
                return;
            }
            if (!Lottery.getInstance().isSellableTickets()) {
                // tickets can't be sold
                player.sendPacket(new SystemMessage(SystemMessageId.NO_LOTTERY_TICKETS_AVAILABLE));
                return;
            }

            int price = Config.ALT_LOTTERY_TICKET_PRICE;
            int lotonumber = Lottery.getInstance().getId();
            int enchant = 0;
            int type2 = 0;

            for (int i = 0; i < 5; i++) {
                if (player.getLoto(i) == 0) {
                    return;
                }

                if (player.getLoto(i) < 17) {
                    enchant += Math.pow(2, player.getLoto(i) - 1);
                } else {
                    type2 += Math.pow(2, player.getLoto(i) - 17);
                }
            }
            if (player.getAdena() < price) {
                sm = new SystemMessage(SystemMessageId.YOU_NOT_ENOUGH_ADENA);
                player.sendPacket(sm);
                return;
            }
            if (!player.reduceAdena("Loto", price, this, true)) {
                return;
            }
            Lottery.getInstance().increasePrize(price);

            sm = new SystemMessage(SystemMessageId.ACQUIRED);
            sm.addNumber(lotonumber);
            sm.addItemName(4442);
            player.sendPacket(sm);

            L2ItemInstance item = new L2ItemInstance(IdFactory.getInstance().getNextId(), 4442);
            item.setCount(1);
            item.setCustomType1(lotonumber);
            item.setEnchantLevel(enchant);
            item.setCustomType2(type2);
            player.getInventory().addItem("Loto", item, player, this);

            InventoryUpdate iu = new InventoryUpdate();
            iu.addItem(item);
            L2ItemInstance adenaupdate = player.getInventory().getItemByItemId(57);
            iu.addModifiedItem(adenaupdate);
            player.sendPacket(iu);

            filename = (getHtmlPath(npcId, 3));
            html.setFile(filename);
        } else if (val == 23) // 23 - current lottery jackpot
        {
            filename = (getHtmlPath(npcId, 3));
            html.setFile(filename);
        } else if (val == 24) // 24 - Previous winning numbers/Prize claim
        {
            filename = (getHtmlPath(npcId, 4));
            html.setFile(filename);

            int lotonumber = Lottery.getInstance().getId();
            String message = "";
            for (L2ItemInstance item : player.getInventory().getItems()) {
                if (item == null) {
                    continue;
                }
                if ((item.getId() == 4442) && (item.getCustomType1() < lotonumber)) {
                    message = message + "<a action=\"bypass -h npc_%objectId%_Loto " + item.getObjectId() + "\">" + item.getCustomType1() + " Event Number ";
                    int[] numbers = Lottery.getInstance().decodeNumbers(item.getEnchantLevel(), item.getCustomType2());
                    for (int i = 0; i < 5; i++) {
                        message += numbers[i] + " ";
                    }
                    int[] check = Lottery.getInstance().checkTicket(item);
                    if (check[0] > 0) {
                        switch (check[0]) {
                            case 1:
                                message += "- 1st Prize";
                                break;
                            case 2:
                                message += "- 2nd Prize";
                                break;
                            case 3:
                                message += "- 3th Prize";
                                break;
                            case 4:
                                message += "- 4th Prize";
                                break;
                        }
                        message += " " + check[1] + "a.";
                    }
                    message += "</a><br>";
                }
            }
            if (message == "") {
                message += "There is no winning lottery ticket...<br>";
            }
            html.replace("%result%", message);
        } else if (val > 24) // >24 - check lottery ticket by item object id
        {
            int lotonumber = Lottery.getInstance().getId();
            L2ItemInstance item = player.getInventory().getItemByObjectId(val);
            if ((item == null) || (item.getId() != 4442) || (item.getCustomType1() >= lotonumber)) {
                return;
            }
            int[] check = Lottery.getInstance().checkTicket(item);

            sm = new SystemMessage(SystemMessageId.DISSAPEARED_ITEM);
            sm.addItemName(4442);
            player.sendPacket(sm);

            int adena = check[1];
            if (adena > 0) {
                player.addAdena("Loto", adena, this, true);
            }
            player.destroyItem("Loto", item, this, false);
            return;
        }
        html.replace("%objectId%", String.valueOf(getObjectId()));
        html.replace("%race%", "" + Lottery.getInstance().getId());
        html.replace("%adena%", "" + Lottery.getInstance().getPrize());
        html.replace("%ticket_price%", "" + Config.ALT_LOTTERY_TICKET_PRICE);
        html.replace("%prize5%", "" + (Config.ALT_LOTTERY_5_NUMBER_RATE * 100));
        html.replace("%prize4%", "" + (Config.ALT_LOTTERY_4_NUMBER_RATE * 100));
        html.replace("%prize3%", "" + (Config.ALT_LOTTERY_3_NUMBER_RATE * 100));
        html.replace("%prize2%", "" + Config.ALT_LOTTERY_2_AND_1_NUMBER_PRIZE);
        html.replace("%enddate%", "" + DateFormat.getDateInstance().format(Lottery.getInstance().getEndDate()));
        player.sendPacket(html);

        // Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
        player.sendPacket(new ActionFailed());
    }

    public void makeCPRecovery(L2PcInstance player) {
        if ((getNpcId() != 31225) && (getNpcId() != 31226)) {
            return;
        }
        if (player.isCursedWeaponEquiped()) {
            player.sendMessage("Go away, you're not welcome here.");
            return;
        }

        int neededmoney = 100;
        SystemMessage sm;
        if (!player.reduceAdena("RestoreCP", neededmoney, player.getLastFolkNPC(), true)) {
            return;
        }
        player.setCurrentCp(getCurrentCp() + 5000);
        // cp restored
        sm = new SystemMessage(SystemMessageId.S1_CP_WILL_BE_RESTORED);
        sm.addString(player.getName());
        player.sendPacket(sm);
    }

    /**
     * Add Newbie helper buffs to L2Player according to its level.<BR>
     * <BR>
     * <B><U> Actions</U> :</B><BR>
     * <BR>
     * <li>Get the range level in wich reader must be to obtain buff</li> <li>If reader level is out of range, display a message and return</li> <li>According to reader level cast buff</li><BR>
     * <BR>
     * <FONT COLOR=#FF0000><B> Newbie Helper Buff list is define in sql table helper_buff_list</B></FONT><BR>
     * <BR>
     *
     * @param player The L2PcInstance that talk with the L2NpcInstance
     */
    public void makeSupportMagic(L2PcInstance player) {
        if (player == null) {
            return;
        }

        // Prevent a cursed weapon weilder of being buffed
        if (player.isCursedWeaponEquiped()) {
            return;
        }

        int player_level = player.getLevel();
        int lowestLevel = 0;
        int higestLevel = 0;

        // Select the reader
        setTarget(player);

        // Calculate the min and max level between wich the reader must be to obtain buff
        if (player.isMageClass()) {
            lowestLevel = HelperBuffTable.getInstance().getMagicClassLowestLevel();
            higestLevel = HelperBuffTable.getInstance().getMagicClassHighestLevel();
        } else {
            lowestLevel = HelperBuffTable.getInstance().getPhysicClassLowestLevel();
            higestLevel = HelperBuffTable.getInstance().getPhysicClassHighestLevel();
        }

        // If the reader is too high level, display a message and return
        if ((player_level > higestLevel) || !player.isNewbie()) {
            String content = "<html><body>Newbie Guide:<br>Only a <font color=\"LEVEL\">novice character of level " + higestLevel + " or less</font> can receive my support magic.<br>Your novice character is the first one that you created and raised in this world.</body></html>";
            insertObjectIdAndShowChatWindow(player, content);
            return;
        }

        // If the reader is too low level, display a message and return
        if (player_level < lowestLevel) {
            String content = "<html><body>Come back here when you have reached level " + lowestLevel + ". I will give you support magic then.</body></html>";
            insertObjectIdAndShowChatWindow(player, content);
            return;
        }

        L2Skill skill = null;
        // Go through the Helper Buff list define in sql table helper_buff_list and cast skill
        for (var helperBuffItem : HelperBuffTable.getInstance().getHelperBuffTable()) {
            if (helperBuffItem.isMagicClass() == player.isMageClass()) {
                if ((player_level >= helperBuffItem.getLowerLevel()) && (player_level <= helperBuffItem.getUpperLevel())) {
                    skill = SkillTable.getInstance().getInfo(helperBuffItem.getSkillId(), helperBuffItem.getSkillLevel());
                    if (skill.getSkillType() == SkillType.SUMMON) {
                        player.doCast(skill);
                    } else {
                        doCast(skill);
                    }
                }
            }
        }

    }

    public void showChatWindow(L2PcInstance player) {
        showChatWindow(player, 0);
    }

    /**
     * Returns true if html exists
     *
     * @param player
     * @param type
     * @return boolean
     */
    private boolean showPkDenyChatWindow(L2PcInstance player, String type) {
        String html = HtmCache.getInstance().getHtm("data/html/" + type + "/" + getNpcId() + "-pk.htm");

        if (html != null) {
            NpcHtmlMessage pkDenyMsg = new NpcHtmlMessage(getObjectId());
            pkDenyMsg.setHtml(html);
            player.sendPacket(pkDenyMsg);
            player.sendPacket(new ActionFailed());
            return true;
        }

        return false;
    }

    /**
     * Open a chat window on client with the text of the L2NpcInstance.<BR>
     * <BR>
     * <B><U> Actions</U> :</B><BR>
     * <BR>
     * <li>Get the text of the selected HTML file in function of the npcId and of the page number</li> <li>Send a Server->Client NpcHtmlMessage containing the text of the L2NpcInstance to the L2PcInstance</li> <li>Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the
     * client wait another packet</li><BR>
     *
     * @param player The L2PcInstance that talk with the L2NpcInstance
     * @param val    The number of the page of the L2NpcInstance to display
     */
    public void showChatWindow(L2PcInstance player, int val) {
        if (player.getKarma() > 0) {
            if (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && (this instanceof L2MerchantInstance)) {
                if (showPkDenyChatWindow(player, "merchant")) {
                    return;
                }
            } else if (!Config.ALT_GAME_KARMA_PLAYER_CAN_USE_GK && (this instanceof L2TeleporterInstance)) {
                if (showPkDenyChatWindow(player, "teleporter")) {
                    return;
                }
            } else if (!Config.ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE && (this instanceof L2WarehouseInstance)) {
                if (showPkDenyChatWindow(player, "warehouse")) {
                    return;
                }
            } else if (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && (this instanceof L2FishermanInstance)) {
                if (showPkDenyChatWindow(player, "fisherman")) {
                    return;
                }
            }
        }

        if ((L2Auctioneer.equals(getType())) && (val == 0)) {
            return;
        }

        int npcId = getNpcId();

        /* For use with Seven Signs implementation */
        String filename = SevenSigns.SEVEN_SIGNS_HTML_PATH;
        int sealAvariceOwner = SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_AVARICE);
        int sealGnosisOwner = SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_GNOSIS);
        int playerCabal = SevenSigns.getInstance().getPlayerCabal(player);
        boolean isSealValidationPeriod = SevenSigns.getInstance().isSealValidationPeriod();
        int compWinner = SevenSigns.getInstance().getCabalHighestScore();

        switch (npcId) {
            case 31078:
            case 31079:
            case 31080:
            case 31081:
            case 31082: // Dawn Priests
            case 31083:
            case 31084:
            case 31168:
            case 31692:
            case 31694:
            case 31997:
                switch (playerCabal) {
                    case SevenSigns.CABAL_DAWN:
                        if (isSealValidationPeriod) {
                            if (compWinner == SevenSigns.CABAL_DAWN) {
                                if (compWinner != sealGnosisOwner) {
                                    filename += "dawn_priest_2c.htm";
                                } else {
                                    filename += "dawn_priest_2a.htm";
                                }
                            } else {
                                filename += "dawn_priest_2b.htm";
                            }
                        } else {
                            filename += "dawn_priest_1b.htm";
                        }
                        break;
                    case SevenSigns.CABAL_DUSK:
                        if (isSealValidationPeriod) {
                            filename += "dawn_priest_3b.htm";
                        } else {
                            filename += "dawn_priest_3a.htm";
                        }
                        break;
                    default:
                        if (isSealValidationPeriod) {
                            if (compWinner == SevenSigns.CABAL_DAWN) {
                                filename += "dawn_priest_4.htm";
                            } else {
                                filename += "dawn_priest_2b.htm";
                            }
                        } else {
                            filename += "dawn_priest_1a.htm";
                        }
                        break;
                }
                break;
            case 31085:
            case 31086:
            case 31087:
            case 31088: // Dusk Priest
            case 31089:
            case 31090:
            case 31091:
            case 31169:
            case 31693:
            case 31695:
            case 31998:
                switch (playerCabal) {
                    case SevenSigns.CABAL_DUSK:
                        if (isSealValidationPeriod) {
                            if (compWinner == SevenSigns.CABAL_DUSK) {
                                if (compWinner != sealGnosisOwner) {
                                    filename += "dusk_priest_2c.htm";
                                } else {
                                    filename += "dusk_priest_2a.htm";
                                }
                            } else {
                                filename += "dusk_priest_2b.htm";
                            }
                        } else {
                            filename += "dusk_priest_1b.htm";
                        }
                        break;
                    case SevenSigns.CABAL_DAWN:
                        if (isSealValidationPeriod) {
                            filename += "dusk_priest_3b.htm";
                        } else {
                            filename += "dusk_priest_3a.htm";
                        }
                        break;
                    default:
                        if (isSealValidationPeriod) {
                            if (compWinner == SevenSigns.CABAL_DUSK) {
                                filename += "dusk_priest_4.htm";
                            } else {
                                filename += "dusk_priest_2b.htm";
                            }
                        } else {
                            filename += "dusk_priest_1a.htm";
                        }
                        break;
                }
                break;
            case 31095: //
            case 31096: //
            case 31097: //
            case 31098: // Enter Necropolises
            case 31099: //
            case 31100: //
            case 31101: //
            case 31102: //
                if (isSealValidationPeriod) {
                    if ((playerCabal != compWinner) || (sealAvariceOwner != compWinner)) {
                        switch (compWinner) {
                            case SevenSigns.CABAL_DAWN:
                                player.sendPacket(new SystemMessage(SystemMessageId.CAN_BE_USED_BY_DAWN));
                                filename += "necro_no.htm";
                                break;
                            case SevenSigns.CABAL_DUSK:
                                player.sendPacket(new SystemMessage(SystemMessageId.CAN_BE_USED_BY_DUSK));
                                filename += "necro_no.htm";
                                break;
                            case SevenSigns.CABAL_NULL:
                                filename = (getHtmlPath(npcId, val)); // do the default!
                                break;
                        }
                    } else {
                        filename = (getHtmlPath(npcId, val)); // do the default!
                    }
                } else {
                    if (playerCabal == SevenSigns.CABAL_NULL) {
                        filename += "necro_no.htm";
                    } else {
                        filename = (getHtmlPath(npcId, val)); // do the default!
                    }
                }
                break;
            case 31114: //
            case 31115: //
            case 31116: // Enter Catacombs
            case 31117: //
            case 31118: //
            case 31119: //
                if (isSealValidationPeriod) {
                    if ((playerCabal != compWinner) || (sealGnosisOwner != compWinner)) {
                        switch (compWinner) {
                            case SevenSigns.CABAL_DAWN:
                                player.sendPacket(new SystemMessage(SystemMessageId.CAN_BE_USED_BY_DAWN));
                                filename += "cata_no.htm";
                                break;
                            case SevenSigns.CABAL_DUSK:
                                player.sendPacket(new SystemMessage(SystemMessageId.CAN_BE_USED_BY_DUSK));
                                filename += "cata_no.htm";
                                break;
                            case SevenSigns.CABAL_NULL:
                                filename = (getHtmlPath(npcId, val)); // do the default!
                                break;
                        }
                    } else {
                        filename = (getHtmlPath(npcId, val)); // do the default!
                    }
                } else {
                    if (playerCabal == SevenSigns.CABAL_NULL) {
                        filename += "cata_no.htm";
                    } else {
                        filename = (getHtmlPath(npcId, val)); // do the default!
                    }
                }
                break;
            case 31111: // Gatekeeper Spirit (Disciples)
                if ((playerCabal == sealAvariceOwner) && (playerCabal == compWinner)) {
                    switch (sealAvariceOwner) {
                        case SevenSigns.CABAL_DAWN:
                            filename += "spirit_dawn.htm";
                            break;
                        case SevenSigns.CABAL_DUSK:
                            filename += "spirit_dusk.htm";
                            break;
                        case SevenSigns.CABAL_NULL:
                            filename += "spirit_null.htm";
                            break;
                    }
                } else {
                    filename += "spirit_null.htm";
                }
                break;
            case 31112: // Gatekeeper Spirit (Disciples)
                filename += "spirit_exit.htm";
                break;
            case 31127: //
            case 31128: //
            case 31129: // Dawn Festival Guides
            case 31130: //
            case 31131: //
                filename += "festival/dawn_guide.htm";
                break;
            case 31137: //
            case 31138: //
            case 31139: // Dusk Festival Guides
            case 31140: //
            case 31141: //
                filename += "festival/dusk_guide.htm";
                break;
            case 31092: // Black Marketeer of Mammon
                filename += "blkmrkt_1.htm";
                break;
            case 31113: // Merchant of Mammon
                switch (compWinner) {
                    case SevenSigns.CABAL_DAWN:
                        if ((playerCabal != compWinner) || (playerCabal != sealAvariceOwner)) {
                            player.sendPacket(new SystemMessage(SystemMessageId.CAN_BE_USED_BY_DAWN));
                            return;
                        }
                        break;
                    case SevenSigns.CABAL_DUSK:
                        if ((playerCabal != compWinner) || (playerCabal != sealAvariceOwner)) {
                            player.sendPacket(new SystemMessage(SystemMessageId.CAN_BE_USED_BY_DUSK));
                            return;
                        }
                        break;
                }
                filename += "mammmerch_1.htm";
                break;
            case 31126: // Blacksmith of Mammon
                switch (compWinner) {
                    case SevenSigns.CABAL_DAWN:
                        if ((playerCabal != compWinner) || (playerCabal != sealGnosisOwner)) {
                            player.sendPacket(new SystemMessage(SystemMessageId.CAN_BE_USED_BY_DAWN));
                            return;
                        }
                        break;
                    case SevenSigns.CABAL_DUSK:
                        if ((playerCabal != compWinner) || (playerCabal != sealGnosisOwner)) {
                            player.sendPacket(new SystemMessage(SystemMessageId.CAN_BE_USED_BY_DUSK));
                            return;
                        }
                        break;
                }
                filename += "mammblack_1.htm";
                break;
            case 31132:
            case 31133:
            case 31134:
            case 31135:
            case 31136: // Festival Witches
            case 31142:
            case 31143:
            case 31144:
            case 31145:
            case 31146:
                filename += "festival/festival_witch.htm";
                break;
            case 31688:
                if (player.isNoble()) {
                    filename = Olympiad.OLYMPIAD_HTML_FILE + "noble_main.htm";
                } else {
                    filename = (getHtmlPath(npcId, val));
                }
                break;
            case 31690:
            case 31769:
            case 31770:
            case 31771:
            case 31772:
                if (player.isHero()) {
                    filename = Olympiad.OLYMPIAD_HTML_FILE + "hero_main.htm";
                } else {
                    filename = (getHtmlPath(npcId, val));
                }
                break;
            default:
                if ((npcId >= 31865) && (npcId <= 31918)) {
                    filename += "rift/GuardianOfBorder.htm";
                    break;
                }
                if (((npcId >= 31093) && (npcId <= 31094)) || ((npcId >= 31172) && (npcId <= 31201)) || ((npcId >= 31239) && (npcId <= 31254))) {
                    return;
                }
                // Get the text of the selected HTML file in function of the npcId and of the page number
                filename = (getHtmlPath(npcId, val));
                break;
        }

        // Send a Server->Client NpcHtmlMessage containing the text of the L2NpcInstance to the L2PcInstance
        NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
        html.setFile(filename);

        // String word = "npc-"+npcId+(val>0 ? "-"+val : "" )+"-dialog-append";

        if (this instanceof L2MerchantInstance) {
            if (Config.LIST_PET_RENT_NPC.contains(npcId)) {
                html.replace("_Quest", "_RentPet\">Rent Pet</a><br><a action=\"bypass -h npc_%objectId%_Quest");
            }
        }

        html.replace("%objectId%", String.valueOf(getObjectId()));
        html.replace("%festivalMins%", SevenSignsFestival.getInstance().getTimeToNextFestivalStr());
        player.sendPacket(html);

        // Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
        player.sendPacket(new ActionFailed());
    }

    private NpcType getType() {
        return template.getType();
    }

    /**
     * Open a chat window on client with the text specified by the given file name and path,<BR>
     * relative to the datapack root. <BR>
     * <BR>
     * Added by Tempy
     *
     * @param player   The L2PcInstance that talk with the L2NpcInstance
     * @param filename The filename that contains the text to send
     */
    public void showChatWindow(L2PcInstance player, String filename) {
        // Send a Server->Client NpcHtmlMessage containing the text of the L2NpcInstance to the L2PcInstance
        NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
        html.setFile(filename);
        html.replace("%objectId%", String.valueOf(getObjectId()));
        player.sendPacket(html);

        // Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
        player.sendPacket(new ActionFailed());
    }

    /**
     * Return the Exp Reward of this L2NpcInstance contained in the L2NpcTemplate (modified by RATE_XP).<BR>
     * <BR>
     *
     * @return
     */
    public int getExpReward() {
        double rateXp = getStat().calcStat(Stats.MAX_HP, 1, this, null);
        return (int) (getExp() * rateXp * Config.RATE_XP);
    }

    private int getExp() {
        return template.getExp();
    }

    /**
     * Return the SP Reward of this L2NpcInstance contained in the L2NpcTemplate (modified by RATE_SP).<BR>
     * <BR>
     *
     * @return
     */
    public int getSpReward() {
        double rateSp = getStat().calcStat(Stats.MAX_HP, 1, this, null);
        return (int) (getSp() * rateSp * Config.RATE_SP);
    }

    private int getSp() {
        return template.getSp();
    }

    /**
     * Kill the L2NpcInstance (the corpse disappeared after 7 seconds).<BR>
     * <BR>
     * <B><U> Actions</U> :</B><BR>
     * <BR>
     * <li>Create a DecayTask to remove the corpse of the L2NpcInstance after 7 seconds</li> <li>Set target to null and cancel Attack or Cast</li> <li>Stop movement</li> <li>Stop HP/MP/CP Regeneration task</li> <li>Stop allTemplates active skills effects in progress on the L2Character</li> <li>Send the
     * Server->Client packet StatusUpdate with current HP and MP to allTemplates other L2PcInstance to inform</li> <li>Notify L2Character AI</li><BR>
     * <BR>
     * <B><U> Overriden in </U> :</B><BR>
     * <BR>
     * <li>L2Attackable</li><BR>
     * <BR>
     *
     * @param killer The L2Character who killed it
     */
    @Override
    public boolean doDie(L2Character killer) {
        if (!super.doDie(killer)) {
            return false;
        }

        // normally this wouldn't really be needed, but for those few exceptions,
        // we do need to reset the weapons back to the initial templated weapon.
        currentLHandId = template.getLhand();
        currentRHandId = template.getRhand();
        currentCollisionHeight = getCollisionHeight();
        currentCollisionRadius = getCollisionRadius();
        DecayTaskManager.getInstance().addDecayTask(this);
        return true;
    }

    /**
     * Set the spawn of the L2NpcInstance.<BR>
     * <BR>
     *
     * @param spawn The L2Spawn that manage the L2NpcInstance
     */
    public void setSpawn(L2Spawn spawn) {
        this.spawn = spawn;
    }

    /**
     * Remove the L2NpcInstance from the world and update its spawn object (for a complete removal use the deleteMe method).<BR>
     * <BR>
     * <B><U> Actions</U> :</B><BR>
     * <BR>
     * <li>Remove the L2NpcInstance from the world when the decay task is launched</li> <li>Decrease its spawn counter</li> <li>Manage Siege task (killFlag, killCT)</li><BR>
     * <BR>
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T REMOVE the object from _allObjects of L2World </B></FONT><BR>
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T SEND Server->Client packets to players</B></FONT><BR>
     * <BR>
     */
    @Override
    public void onDecay() {
        if (isDecayed()) {
            return;
        }
        setDecayed(true);

        // Manage Life Control Tower
        if (this instanceof L2ControlTowerInstance) {
            ((L2ControlTowerInstance) this).onDeath();
        }

        // Remove the L2NpcInstance from the world when the decay task is launched
        super.onDecay();

        // Decrease its spawn counter
        if (spawn != null) {
            spawn.decreaseCount(this);
        }
    }

    /**
     * Remove PROPERLY the L2NpcInstance from the world.<BR>
     * <BR>
     * <B><U> Actions</U> :</B><BR>
     * <BR>
     * <li>Remove the L2NpcInstance from the world and update its spawn object</li> <li>Remove allTemplates L2Object from _knownObjects and _knownPlayer of the L2NpcInstance then cancel Attak or Cast and notify AI</li> <li>Remove L2Object object from _allObjects of L2World</li><BR>
     * <BR>
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T SEND Server->Client packets to players</B></FONT><BR>
     * <BR>
     */
    public void deleteMe() {
        if (getWorldRegion() != null) {
            getWorldRegion().removeFromZones(this);
            // FIXME this is just a temp hack, we should find a better solution
        }

        try {
            decayMe();
        } catch (Throwable t) {
            logger.error("deletedMe(): " + t);
        }

        // Remove allTemplates L2Object from _knownObjects and _knownPlayer of the L2Character then cancel Attak or Cast and notify AI
        try {
            getKnownList().removeAllKnownObjects();
        } catch (Throwable t) {
            logger.error("deletedMe(): " + t);
        }

        // Remove L2Object object from _allObjects of L2World
        L2World.getInstance().removeObject(this);
    }

    /**
     * Return the L2Spawn object that manage this L2NpcInstance.<BR>
     * <BR>
     *
     * @return
     */
    public L2Spawn getSpawn() {
        return spawn;
    }

    @Override
    public String toString() {
        return getName();
    }

    public boolean isDecayed() {
        return isDecayed;
    }

    public void setDecayed(boolean decayed) {
        isDecayed = decayed;
    }

    public void endDecayTask() {
        if (!isDecayed()) {
            DecayTaskManager.getInstance().cancelDecayTask(this);
            onDecay();
        }
    }

    public boolean isMob() // rather delete this check
    {
        return false; // This means we use MAX_NPC_ANIMATION instead of MAX_MONSTER_ANIMATION
    }

    public void setCollisionHeight(float height) {
        currentCollisionHeight = height;
    }

    public void setCollisionRadius(float radius) {
        currentCollisionRadius = radius;
    }

    public float getCollisionHeight() {
        return currentCollisionHeight;
    }

    public float getCollisionRadius() {
        return currentCollisionRadius;
    }
}
