package org.l2j.gameserver.model.actor.instance;

import org.l2j.commons.Config;
import org.l2j.gameserver.GameTimeController;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.clientpackets.Say2;
import org.l2j.gameserver.model.L2Character;
import org.l2j.gameserver.model.L2ItemInstance;
import org.l2j.gameserver.model.actor.knownlist.BoatKnownList;
import org.l2j.gameserver.model.entity.database.CharTemplate;
import org.l2j.gameserver.serverpackets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @author Maktakien
 */
public class L2BoatInstance extends L2Character<CharTemplate> {
    protected static final Logger _logBoat = LoggerFactory.getLogger(L2BoatInstance.class);

    private class L2BoatTrajet {
        private Map<Integer, L2BoatPoint> _path;

        int idWaypoint1;
        int idWTicket1;
        int ntx1;
        int nty1;
        int ntz1;
        public int max;
        String boatName;
        String npc1;
        String sysmess10_1;
        String sysmess5_1;
        String sysmess1_1;
        String sysmessb_1;
        String sysmess0_1;

        protected class L2BoatPoint {
            int speed1;
            int speed2;
            public int x;
            public int y;
            public int z;
            public int time;
        }

        L2BoatTrajet(int pIdWaypoint1, int pIdWTicket1, int pNtx1, int pNty1, int pNtz1, String pNpc1, String pSysmess10_1, String pSysmess5_1, String pSysmess1_1, String pSysmess0_1, String pSysmessb_1, String pBoatname) {
            idWaypoint1 = pIdWaypoint1;
            idWTicket1 = pIdWTicket1;
            ntx1 = pNtx1;
            nty1 = pNty1;
            ntz1 = pNtz1;
            npc1 = pNpc1;
            sysmess10_1 = pSysmess10_1;
            sysmess5_1 = pSysmess5_1;
            sysmess1_1 = pSysmess1_1;
            sysmessb_1 = pSysmessb_1;
            sysmess0_1 = pSysmess0_1;
            boatName = pBoatname;
            loadBoatPath();
        }

        void parseLine(String line) {
            _path = new LinkedHashMap<>();
            StringTokenizer st = new StringTokenizer(line, ";");
            st.nextToken();
            max = Integer.parseInt(st.nextToken());
            for (int i = 0; i < max; i++) {
                L2BoatPoint bp = new L2BoatPoint();
                bp.speed1 = Integer.parseInt(st.nextToken());
                bp.speed2 = Integer.parseInt(st.nextToken());
                bp.x = Integer.parseInt(st.nextToken());
                bp.y = Integer.parseInt(st.nextToken());
                bp.z = Integer.parseInt(st.nextToken());
                bp.time = Integer.parseInt(st.nextToken());
                _path.put(i, bp);
            }
        }

        private void loadBoatPath() {
            LineNumberReader lnr = null;
            try {
                File doorData = new File(Config.DATAPACK_ROOT, "data/boatpath.csv");
                lnr = new LineNumberReader(new BufferedReader(new FileReader(doorData)));

                String line = null;
                while ((line = lnr.readLine()) != null) {
                    if ((line.trim().length() == 0) || !line.startsWith(idWaypoint1 + ";")) {
                        continue;
                    }
                    parseLine(line);
                    return;
                }
                _logBoat.warn("No path for boat {}!!!", boatName);
            } catch (FileNotFoundException e) {
                _logBoat.warn("boatpath.csv is missing in data folder");
            } catch (Exception e) {
                _logBoat.warn("error while creating boat table {}", e);
            } finally {
                try {
                    lnr.close();
                } catch (Exception e1) { /* ignore problems */
                }
            }
        }

        public int state(int state, L2BoatInstance _boat) {
            if (state < max) {
                L2BoatPoint bp = _path.get(state);
                double dx = (bp.x - _boat.getX());
                double dy = (bp.y - _boat.getX());
                double distance = Math.sqrt((dx * dx) + (dy * dy));
                double cos;
                double sin;
                sin = dy / distance;
                cos = dx / distance;

                int heading = (int) (Math.atan2(-sin, -cos) * 10430.378350470452724949566316381);
                heading += 32768;
                _boat.getPosition().setHeading(heading);

                _boat._vd = new VehicleDeparture(_boat, bp.speed1, bp.speed2, bp.x, bp.y, bp.z);
                // _boat.getPlayerTemplate().baseRunSpd = bp.speed1;
                _boat.moveToLocation(bp.x, bp.y, bp.z, (float) bp.speed1);
                Collection<L2PcInstance> knownPlayers = _boat.getKnownList().getKnownPlayers().values();
                if (knownPlayers.isEmpty()) {
                    return bp.time;
                }
                for (L2PcInstance player : knownPlayers) {
                    player.sendPacket(_boat._vd);
                }
                if (bp.time == 0) {
                    bp.time = 1;
                }
                return bp.time;
            }
            return 0;
        }

    }

    private final String _name;
    private L2BoatTrajet _t1;
    private L2BoatTrajet _t2;
    private int _cycle = 0;
    private VehicleDeparture _vd = null;
    private Map<Integer, L2PcInstance> _inboat;

    public L2BoatInstance(int objectId, CharTemplate template, String name) {
        super(objectId, template);
        super.setKnownList(new BoatKnownList(this));
        _name = name;
    }

    private void moveToLocation(int x, int y, int z, float speed) {
        final int curX = getX();
        final int curY = getY();
        final int curZ = getZ();

        final int dx = (x - curX);
        final int dy = (y - curY);
        double distance = Math.sqrt((dx * dx) + (dy * dy));

        _logBoat.debug("distance to target:" + distance);


        // Define movement angles needed
        // ^
        // | X (x,y)
        // | /
        // | /distance
        // | /
        // |/ angle
        // X ---------->
        // (curx,cury)

        double cos;
        double sin;
        sin = dy / distance;
        cos = dx / distance;
        // Create and Init a MoveData object
        MoveData m = new MoveData();

        // Caclulate the Nb of ticks between the current position and the destination
        m.ticksToMove = (int) ((GameTimeController.TICKS_PER_SECOND * distance) / speed);

        // Calculate the xspeed and yspeed in unit/ticks in function of the movement speed
        m.xSpeedTicks = (float) ((cos * speed) / GameTimeController.TICKS_PER_SECOND);
        m._ySpeedTicks = (float) ((sin * speed) / GameTimeController.TICKS_PER_SECOND);

        // Calculate and set the heading of the L2Character
        int heading = (int) (Math.atan2(-sin, -cos) * 10430.378350470452724949566316381);
        heading += 32768;
        getPosition().setHeading(heading);

        _logBoat.debug("dist:" + distance + "speed:" + speed + " ttt:" + m.ticksToMove + " dx:" + (int) m.xSpeedTicks + " dy:" + (int) m._ySpeedTicks + " heading:" + heading);


        m.xDestination = x;
        m.yDestination = y;
        m.zDestination = z; // this is what was requested from client
        m.heading = 0;

        m.moveStartTime = GameTimeController.getGameTicks();
        m.xMoveFrom = curX;
        m.yMoveFrom = curY;
        m.zMoveFrom = curZ;

        // If necessary set Nb ticks needed to a min value to ensure small distancies movements
        if (m.ticksToMove < 1) {
            m.ticksToMove = 1;
        }

        _logBoat.debug("time to target:" + m.ticksToMove);

        move = m;

        GameTimeController.getInstance().registerMovingObject(this);
    }

    class BoatCaptain implements Runnable {
        private final int _state;
        private final L2BoatInstance _boat;

        public BoatCaptain(int i, L2BoatInstance instance) {
            _state = i;
            _boat = instance;
        }

        @Override
        public void run() {
            BoatCaptain bc;
            switch (_state) {
                case 1:
                    _boat.say(5);
                    bc = new BoatCaptain(2, _boat);
                    ThreadPoolManager.getInstance().scheduleGeneral(bc, 240000);
                    break;
                case 2:
                    _boat.say(1);
                    bc = new BoatCaptain(3, _boat);
                    ThreadPoolManager.getInstance().scheduleGeneral(bc, 40000);
                    break;
                case 3:
                    _boat.say(0);
                    bc = new BoatCaptain(4, _boat);
                    ThreadPoolManager.getInstance().scheduleGeneral(bc, 20000);
                    break;
                case 4:
                    _boat.say(-1);
                    _boat.begin();
                    break;
            }
        }
    }

    class Boatrun implements Runnable {
        private int _state;
        private final L2BoatInstance _boat;


        Boatrun(int i, L2BoatInstance instance) {
            _state = i;
            _boat = instance;
        }

        @Override
        public void run() {
            _boat._vd = null;
            _boat.needOnVehicleCheckLocation = false;
            if (_boat._cycle == 1) {
                int time = _boat._t1.state(_state, _boat);
                if (time > 0) {
                    _state++;
                    Boatrun bc = new Boatrun(_state, _boat);
                    ThreadPoolManager.getInstance().scheduleGeneral(bc, time);

                } else if (time == 0) {
                    _boat._cycle = 2;
                    _boat.say(10);
                    BoatCaptain bc = new BoatCaptain(1, _boat);
                    ThreadPoolManager.getInstance().scheduleGeneral(bc, 300000);
                } else {
                    _boat.needOnVehicleCheckLocation = true;
                    _state++;
                    _boat._runstate = _state;
                }
            } else if (_boat._cycle == 2) {
                int time = _boat._t2.state(_state, _boat);
                if (time > 0) {
                    _state++;
                    Boatrun bc = new Boatrun(_state, _boat);
                    ThreadPoolManager.getInstance().scheduleGeneral(bc, time);
                } else if (time == 0) {
                    _boat._cycle = 1;
                    _boat.say(10);
                    BoatCaptain bc = new BoatCaptain(1, _boat);
                    ThreadPoolManager.getInstance().scheduleGeneral(bc, 300000);
                } else {
                    _boat.needOnVehicleCheckLocation = true;
                    _state++;
                    _boat._runstate = _state;
                }
            }
        }
    }

    private int _runstate = 0;

    public void evtArrived() {

        if (_runstate != 0) {
            Boatrun bc = new Boatrun(_runstate, this);
            ThreadPoolManager.getInstance().scheduleGeneral(bc, 10);
            _runstate = 0;
        }
    }

    public void sendVehicleDeparture(L2PcInstance activeChar) {
        if (_vd != null) {
            activeChar.sendPacket(_vd);
        }
    }

    public VehicleDeparture getVehicleDeparture() {
        return _vd;
    }

    private void beginCycle() {
        say(10);
        BoatCaptain bc = new BoatCaptain(1, this);
        ThreadPoolManager.getInstance().scheduleGeneral(bc, 300000);
    }

    private int lastx = -1;
    private int lasty = -1;
    private boolean needOnVehicleCheckLocation = false;

    public void updatePeopleInTheBoat(int x, int y, int z) {

        if (_inboat != null) {
            boolean check = false;
            if ((lastx == -1) || (lasty == -1)) {
                check = true;
                lastx = x;
                lasty = y;
            } else if ((((x - lastx) * (x - lastx)) + ((y - lasty) * (y - lasty))) > 2250000) // 1500 * 1500 = 2250000
            {
                check = true;
                lastx = x;
                lasty = y;
            }
            for (int i = 0; i < _inboat.size(); i++) {
                L2PcInstance player = _inboat.get(i);
                if ((player != null) && player.isInBoat()) {
                    if (player.getBoat() == this) {
                        // reader.getKnownList().addKnownObject(this);
                        player.getPosition().setXYZ(x, y, z);
                        player.revalidateZone(false);
                    }
                }
                if (check == true) {
                    if (needOnVehicleCheckLocation == true) {
                        OnVehicleCheckLocation vcl = new OnVehicleCheckLocation(this, x, y, z);
                        player.sendPacket(vcl);
                    }
                }
            }
        }

    }

    public void begin() {
        if (_cycle == 1) {
            Collection<L2PcInstance> knownPlayers = getKnownList().getKnownPlayers().values();
            if ((knownPlayers != null) && !knownPlayers.isEmpty()) {
                _inboat = new LinkedHashMap<>();
                int i = 0;
                for (L2PcInstance player : knownPlayers) {
                    if (player.isInBoat()) {
                        L2ItemInstance it;
                        it = player.getInventory().getItemByItemId(_t1.idWTicket1);
                        if ((it != null) && (it.getCount() >= 1)) {
                            player.getInventory().destroyItem("Boat", it.getObjectId(), 1, player, this);
                            InventoryUpdate iu = new InventoryUpdate();
                            iu.addModifiedItem(it);
                            player.sendPacket(iu);
                            _inboat.put(i, player);
                            i++;
                        } else if ((it == null) && (_t1.idWTicket1 == 0)) {
                            _inboat.put(i, player);
                            i++;
                        } else {
                            player.teleToLocation(_t1.ntx1, _t1.nty1, _t1.ntz1, false);
                        }
                    }
                }
            }
            Boatrun bc = new Boatrun(0, this);
            ThreadPoolManager.getInstance().scheduleGeneral(bc, 0);
        } else if (_cycle == 2) {
            Collection<L2PcInstance> knownPlayers = getKnownList().getKnownPlayers().values();
            if ((knownPlayers != null) && !knownPlayers.isEmpty()) {
                _inboat = new LinkedHashMap<>();
                int i = 0;
                for (L2PcInstance player : knownPlayers) {
                    if (player.isInBoat()) {
                        L2ItemInstance it;
                        it = player.getInventory().getItemByItemId(_t2.idWTicket1);
                        if ((it != null) && (it.getCount() >= 1)) {

                            player.getInventory().destroyItem("Boat", it.getObjectId(), 1, player, this);
                            InventoryUpdate iu = new InventoryUpdate();
                            iu.addModifiedItem(it);
                            player.sendPacket(iu);
                            _inboat.put(i, player);
                            i++;
                        } else if ((it == null) && (_t2.idWTicket1 == 0)) {
                            _inboat.put(i, player);
                            i++;
                        } else {
                            player.teleToLocation(_t2.ntx1, _t2.nty1, _t2.ntz1, false);
                        }
                    }
                }

            }
            Boatrun bc = new Boatrun(0, this);
            ThreadPoolManager.getInstance().scheduleGeneral(bc, 0);
        }
    }

    /**
     * @param i
     */
    public void say(int i) {

        Collection<L2PcInstance> knownPlayers = getKnownList().getKnownPlayers().values();
        CreatureSay sm;
        PlaySound ps;
        switch (i) {
            case 10:
                if (_cycle == 1) {
                    sm = new CreatureSay(0, Say2.SHOUT, _t1.npc1, _t1.sysmess10_1);
                } else {
                    sm = new CreatureSay(0, Say2.SHOUT, _t2.npc1, _t2.sysmess10_1);
                }
                ps = new PlaySound(0, "itemsound.ship_arrival_departure", 1, getObjectId(), getX(), getY(), getZ());
                if ((knownPlayers == null) || knownPlayers.isEmpty()) {
                    return;
                }
                for (L2PcInstance player : knownPlayers) {
                    player.sendPacket(sm);
                    player.sendPacket(ps);
                }
                break;
            case 5:
                if (_cycle == 1) {
                    sm = new CreatureSay(0, Say2.SHOUT, _t1.npc1, _t1.sysmess5_1);
                } else {
                    sm = new CreatureSay(0, Say2.SHOUT, _t2.npc1, _t2.sysmess5_1);
                }
                ps = new PlaySound(0, "itemsound.ship_5min", 1, getObjectId(), getX(), getY(), getZ());
                if ((knownPlayers == null) || knownPlayers.isEmpty()) {
                    return;
                }
                for (L2PcInstance player : knownPlayers) {
                    player.sendPacket(sm);
                    player.sendPacket(ps);
                }
                break;
            case 1:

                if (_cycle == 1) {
                    sm = new CreatureSay(0, Say2.SHOUT, _t1.npc1, _t1.sysmess1_1);
                } else {
                    sm = new CreatureSay(0, Say2.SHOUT, _t2.npc1, _t2.sysmess1_1);
                }
                ps = new PlaySound(0, "itemsound.ship_1min", 1, getObjectId(), getX(), getY(), getZ());
                if ((knownPlayers == null) || knownPlayers.isEmpty()) {
                    return;
                }
                for (L2PcInstance player : knownPlayers) {
                    player.sendPacket(sm);
                    player.sendPacket(ps);
                }
                break;
            case 0:

                if (_cycle == 1) {
                    sm = new CreatureSay(0, Say2.SHOUT, _t1.npc1, _t1.sysmess0_1);
                } else {
                    sm = new CreatureSay(0, Say2.SHOUT, _t2.npc1, _t2.sysmess0_1);
                }
                if ((knownPlayers == null) || knownPlayers.isEmpty()) {
                    return;
                }
                for (L2PcInstance player : knownPlayers) {
                    player.sendPacket(sm);
                    // reader.sendPacket(ps);
                }
                break;
            case -1:
                if (_cycle == 1) {
                    sm = new CreatureSay(0, Say2.SHOUT, _t1.npc1, _t1.sysmessb_1);
                } else {
                    sm = new CreatureSay(0, Say2.SHOUT, _t2.npc1, _t2.sysmessb_1);
                }
                ps = new PlaySound(0, "itemsound.ship_arrival_departure", 1, getObjectId(), getX(), getY(), getZ());
                for (L2PcInstance player : knownPlayers) {
                    player.sendPacket(sm);
                    player.sendPacket(ps);
                }
                break;
        }
    }

    //

    /**
     *
     */
    public void spawn() {
        Collection<L2PcInstance> knownPlayers = getKnownList().getKnownPlayers().values();
        _cycle = 1;
        beginCycle();
        if ((knownPlayers == null) || knownPlayers.isEmpty()) {
            return;
        }
        VehicleInfo vi = new VehicleInfo(this);
        for (L2PcInstance player : knownPlayers) {
            player.sendPacket(vi);
        }
    }

    /**
     * @param idWaypoint1
     * @param idWTicket1
     * @param ntx1
     * @param nty1
     * @param ntz1
     * @param idnpc1
     * @param sysmess10_1
     * @param sysmess5_1
     * @param sysmess1_1
     * @param sysmess0_1
     * @param sysmessb_1
     */
    public void setTrajet1(int idWaypoint1, int idWTicket1, int ntx1, int nty1, int ntz1, String idnpc1, String sysmess10_1, String sysmess5_1, String sysmess1_1, String sysmess0_1, String sysmessb_1) {
        _t1 = new L2BoatTrajet(idWaypoint1, idWTicket1, ntx1, nty1, ntz1, idnpc1, sysmess10_1, sysmess5_1, sysmess1_1, sysmess0_1, sysmessb_1, _name);
    }

    public void setTrajet2(int idWaypoint1, int idWTicket1, int ntx1, int nty1, int ntz1, String idnpc1, String sysmess10_1, String sysmess5_1, String sysmess1_1, String sysmess0_1, String sysmessb_1) {
        _t2 = new L2BoatTrajet(idWaypoint1, idWTicket1, ntx1, nty1, ntz1, idnpc1, sysmess10_1, sysmess5_1, sysmess1_1, sysmess0_1, sysmessb_1, _name);
    }

    /*
     * (non-Javadoc)
     * @see org.l2j.gameserver.model.L2Character#updateAbnormalEffect()
     */
    @Override
    public void updateAbnormalEffect() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * @see org.l2j.gameserver.model.L2Character#getActiveWeaponInstance()
     */
    @Override
    public L2ItemInstance getActiveWeaponInstance() {
        // TODO Auto-generated method stub
        return null;
    }


    /*
     * (non-Javadoc)
     * @see org.l2j.gameserver.model.L2Character#getSecondaryWeaponInstance()
     */
    @Override
    public L2ItemInstance getSecondaryWeaponInstance() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.l2j.gameserver.model.L2Character#getLevel()
     */
    @Override
    public int getLevel() {
        // TODO Auto-generated method stub
        return 0;
    }

    /*
     * (non-Javadoc)
     * @see org.l2j.gameserver.model.L2Object#isAutoAttackable(org.l2j.gameserver.model.L2Character)
     */
    @Override
    public boolean isAutoAttackable(L2Character attacker) {
        // TODO Auto-generated method stub
        return false;
    }
}
