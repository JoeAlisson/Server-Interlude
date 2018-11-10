package org.l2j.gameserver.model.actor.instance;

import org.l2j.commons.Config;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.ai.AI;
import org.l2j.gameserver.ai.Intention;
import org.l2j.gameserver.ai.L2DoorAI;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.*;
import org.l2j.gameserver.model.actor.knownlist.DoorKnownList;
import org.l2j.gameserver.model.actor.stat.DoorStat;
import org.l2j.gameserver.model.actor.status.DoorStatus;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.entity.ClanHall;
import org.l2j.gameserver.model.entity.database.CharTemplate;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.serverpackets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class L2DoorInstance extends L2Character<CharTemplate> {
    protected static final Logger log = LoggerFactory.getLogger(L2DoorInstance.class);

    private int castleIndex = -2;
    private int mapRegion = -1;

    // when door is closed, the dimensions are
    private int rangeXMin = 0;
    private int rangeYMin = 0;
    private int rangeZMin = 0;
    private int rangeXMax = 0;
    private int rangeYMax = 0;
    private int rangeZMax = 0;

    private final int doorId;
    protected final String name;
    private int open;
    private final boolean unlockable;

    private ClanHall clanHall;

    private int autoActionDelay = -1;

    public class AIAccessor extends L2Character.AIAccessor {
        protected AIAccessor() {
        }

        @Override
        public L2DoorInstance getActor() {
            return L2DoorInstance.this;
        }

        @Override
        public void moveTo(int x, int y, int z, int offset) {
        }

        @Override
        public void moveTo(int x, int y, int z) {
        }

        @Override
        public void stopMove(L2Position pos) {
        }

        @Override
        public void doAttack(L2Character target) {
        }

        @Override
        public void doCast(L2Skill skill) {
        }
    }

    @Override
    public synchronized AI getAI() {
        if (isNull(ai)) {
            ai = new L2DoorAI(new AIAccessor());
        }
        return ai;
    }

    @Override
    public boolean hasAI() {
        return nonNull(ai);
    }

    class CloseTask implements Runnable {
        @Override
        public void run() {
            try {
                onClose();
            } catch (Throwable e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
    }

    class AutoOpenClose implements Runnable {
        @Override
        public void run() {
            try {
                String doorAction;

                if (getOpen() == 1) {
                    doorAction = "opened";
                    openMe();
                } else {
                    doorAction = "closed";
                    closeMe();
                }

                log.debug("Auto {} door ID {} ({}) for {} minute(s).", doorAction, doorId, name, (autoActionDelay / 60000));

            } catch (Exception e) {
                log.warn("Could not auto open/close door ID {} ({}): {}", doorId, name, e);
            }
        }
    }

    public L2DoorInstance(int objectId, CharTemplate template, int doorId, String name, boolean unlockable) {
        super(objectId, template);
        getKnownList();
        getStat();
        getStatus();
        this.doorId = doorId;
        this.name = name;
        this.unlockable = unlockable;
    }

    @Override
    public final DoorKnownList getKnownList() {
        if ((super.getKnownList() == null) || !(super.getKnownList() instanceof DoorKnownList)) {
            setKnownList(new DoorKnownList(this));
        }
        return (DoorKnownList) super.getKnownList();
    }

    @Override
    public final DoorStat getStat() {
        if ((super.getStat() == null) || !(super.getStat() instanceof DoorStat)) {
            setStat(new DoorStat(this));
        }
        return (DoorStat) super.getStat();
    }

    @Override
    public final DoorStatus getStatus() {
        if ((super.getStatus() == null) || !(super.getStatus() instanceof DoorStatus)) {
            setStatus(new DoorStatus(this));
        }
        return (DoorStatus) super.getStatus();
    }

    public final boolean isUnlockable() {
        return unlockable;
    }

    @Override
    public final int getLevel() {
        return 1;
    }

    public int getDoorId() {
        return doorId;
    }

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }

    public void setAutoActionDelay(int actionDelay) {
        if (autoActionDelay == actionDelay) {
            return;
        }

        if (actionDelay > -1) {
            AutoOpenClose ao = new AutoOpenClose();
            ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(ao, actionDelay, actionDelay);
        }
        autoActionDelay = actionDelay;
    }

    public int getDamage() {
        int dmg = 6 - (int) Math.ceil((getCurrentHp() / getMaxHp()) * 6);
        if (dmg > 6) {
            return 6;
        }
        if (dmg < 0) {
            return 0;
        }
        return dmg;
    }

    public final Castle getCastle() {
        if (castleIndex < 0) {
            castleIndex = CastleManager.getInstance().getCastleIndex(this);
        }
        if (castleIndex < 0) {
            return null;
        }
        return CastleManager.getInstance().getCastles().get(castleIndex);
    }

    public void setClanHall(ClanHall clanhall) {
        clanHall = clanhall;
    }

    private ClanHall getClanHall() {
        return clanHall;
    }

    public boolean isEnemyOf(L2Character cha) {
        return true;
    }

    @Override
    public boolean isAutoAttackable(L2Character attacker) {
        if (isUnlockable()) {
            return true;
        }

        return (attacker instanceof L2PcInstance) && (nonNull(getCastle())) && (getCastle().getCastleId() > 0) && getCastle().getSiege().getIsInProgress() && getCastle().getSiege().checkIsAttacker(((L2PcInstance) attacker).getClan());
    }

    public boolean isAttackable(L2Character attacker) {
        return isAutoAttackable(attacker);
    }

    @Override
    public void updateAbnormalEffect() {
    }

    public int getDistanceToWatchObject(L2Object object) {
        if (!(object instanceof L2PcInstance)) {
            return 0;
        }
        return 2000;
    }

    public int getDistanceToForgetObject(L2Object object) {
        if (!(object instanceof L2PcInstance)) {
            return 0;
        }

        return 4000;
    }

    @Override
    public L2ItemInstance getActiveWeaponInstance() {
        return null;
    }

    @Override
    public L2ItemInstance getSecondaryWeaponInstance() {
        return null;
    }

    @Override
    public void onAction(L2PcInstance player) {
        if (isNull(player)) {
            return;
        }

        if (this != player.getTarget()) {
            player.setTarget(this);

            MyTargetSelected my = new MyTargetSelected(getObjectId(), 0);
            player.sendPacket(my);
            DoorStatusUpdate su = new DoorStatusUpdate(this);
            player.sendPacket(su);

            player.sendPacket(new ValidateLocation(this));
        } else {
            if (isAutoAttackable(player)) {
                if (Math.abs(player.getZ() - getZ()) < 400) // this max heigth difference might need some tweaking
                {
                    player.getAI().setIntention(Intention.AI_INTENTION_ATTACK, this);
                }
            } else if ((player.getClan() != null) && (getClanHall() != null) && (player.getClanId() == getClanHall().getOwnerId())) {
                if (!isInsideRadius(player, L2NpcInstance.INTERACTION_DISTANCE, false, false)) {
                    player.getAI().setIntention(Intention.AI_INTENTION_INTERACT, this);
                } else {
                    // TODO need find serverpacket which ask open/close gate. now auto
                    // if (getOpen() == 1) player.sendPacket(new SystemMessage(1140));
                    // else reader.sendPacket(new SystemMessage(1141));
                    if (getOpen() == 1) {
                        openMe();
                    } else {
                        closeMe();
                    }
                }
            }
        }

        player.sendPacket(new ActionFailed());
    }

    @Override
    public void onActionShift(L2GameClient client) {
        L2PcInstance player = client.getActiveChar();
        if (isNull(player)) {
            return;
        }

        if (player.getAccessLevel() >= Config.GM_ACCESSLEVEL) {
            player.setTarget(this);
            MyTargetSelected my = new MyTargetSelected(getObjectId(), player.getLevel());
            player.sendPacket(my);

            if (isAutoAttackable(player)) {
                DoorStatusUpdate su = new DoorStatusUpdate(this);
                player.sendPacket(su);
            }

            NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
            StringBuilder html1 = new StringBuilder("<html><body><table border=0>");
            html1.append("<tr><td>S.Y.L. Says:</td></tr>");
            html1.append("<tr><td>Current HP  " + getCurrentHp() + "</td></tr>");
            html1.append("<tr><td>Max HP      " + getMaxHp() + "</td></tr>");

            html1.append("<tr><td>Object ID: " + getObjectId() + "</td></tr>");
            html1.append("<tr><td>Door ID:<br>" + getDoorId() + "</td></tr>");
            html1.append("<tr><td><br></td></tr>");

            html1.append("<tr><td>Class: " + getClass().getName() + "</td></tr>");
            html1.append("<tr><td><br></td></tr>");
            html1.append("</table>");

            html1.append("<table><tr>");
            html1.append("<td><button value=\"Open\" action=\"bypass -h admin_open " + getDoorId() + "\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
            html1.append("<td><button value=\"Close\" action=\"bypass -h admin_close " + getDoorId() + "\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
            html1.append("<td><button value=\"Kill\" action=\"bypass -h admin_kill\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
            html1.append("<td><button value=\"Delete\" action=\"bypass -h admin_delete\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
            html1.append("</tr></table></body></html>");

            html.setHtml(html1.toString());
            player.sendPacket(html);
        } else {
            // ATTACK the mob without moving?
        }

        player.sendPacket(new ActionFailed());
    }

    @Override
    public void broadcastStatusUpdate() {
        Collection<L2PcInstance> knownPlayers = getKnownList().getKnownPlayers().values();
        if (knownPlayers.isEmpty()) {
            return;
        }

        DoorStatusUpdate su = new DoorStatusUpdate(this);
        for (L2PcInstance player : knownPlayers) {
            player.sendPacket(su);
        }
    }

    public void onOpen() {
        ThreadPoolManager.getInstance().scheduleGeneral(new CloseTask(), 60000);
    }

    public void onClose() {
        closeMe();
    }


    public final void closeMe() {
        setOpen(1);
        broadcastStatusUpdate();
    }

    public final void openMe() {
        setOpen(0);
        broadcastStatusUpdate();
    }

    @Override
    public String toString() {
        return "door " + doorId;
    }

    public String getDoorName() {
        return name;
    }

    public int getXMin() {
        return rangeXMin;
    }

    public int getYMin() {
        return rangeYMin;
    }

    public int getZMin() {
        return rangeZMin;
    }

    public int getXMax() {
        return rangeXMax;
    }

    public int getYMax() {
        return rangeYMax;
    }

    public int getZMax() {
        return rangeZMax;
    }

    public void setRange(int xMin, int yMin, int zMin, int xMax, int yMax, int zMax) {
        rangeXMin = xMin;
        rangeYMin = yMin;
        rangeZMin = zMin;

        rangeXMax = xMax;
        rangeYMax = yMax;
        rangeZMax = zMax;
    }

    public int getMapRegion() {
        return mapRegion;
    }

    public void setMapRegion(int region) {
        mapRegion = region;
    }

    public Collection<L2SiegeGuardInstance> getKnownSiegeGuards() {
        List<L2SiegeGuardInstance> result = new LinkedList<>();

        for (L2Object obj : getKnownList().getKnownObjects().values()) {
            if (obj instanceof L2SiegeGuardInstance) {
                result.add((L2SiegeGuardInstance) obj);
            }
        }

        return result;
    }
}
