package org.l2j.gameserver.network;

import org.l2j.gameserver.AuthServerClient;
import org.l2j.gameserver.AuthServerClient.SessionKey;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.communitybbs.Manager.RegionBBSManager;
import org.l2j.gameserver.datatables.SkillTable;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.entity.L2Event;
import org.l2j.gameserver.model.entity.database.Character;
import org.l2j.gameserver.security.SecondFactorAuth;
import org.l2j.gameserver.serverpackets.L2GameServerPacket;
import org.l2j.gameserver.serverpackets.ServerClose;
import org.l2j.gameserver.serverpackets.UserInfo;
import org.l2j.gameserver.util.EventData;
import org.l2j.mmocore.Client;
import org.l2j.mmocore.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.util.Util.isNullOrEmpty;

public final class L2GameClient extends Client<Connection<L2GameClient>> {

    private static final Logger _log = LoggerFactory.getLogger(L2GameClient.class);
    private List<Character> characters;
    private SecondFactorAuth secondFactorAuth;

    public L2GameClient(Connection<L2GameClient> con) {
        super(con);
        state = GameClientState.CONNECTED;
        _connectionStartTime = System.currentTimeMillis();
        crypt = new GameCrypt();
        _autoSaveInDB = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new AutoSaveTask(), 300000L, 900000L);
    }

    public Character getCharacterForSlot(int charslot) {
        if ((charslot < 0) || isNull(characters) || (charslot >= characters.size())) {
            _log.warn("{} tried to get Character in slot {} but no characters exits at that slot.", this, charslot);
            return null;
        }
        return characters.get(charslot);
    }

    public void setAccount(String account) {
        this.account = account;
        secondFactorAuth = new SecondFactorAuth(account);
    }

    public boolean isSecondFactorAuthed() {
        return nonNull(secondFactorAuth) && secondFactorAuth.isAuthed();
    }

    public boolean hasSecondPassword() {
        return nonNull(secondFactorAuth) && secondFactorAuth.hasPassword();
    }

    public boolean saveSecondFactorPassword(String password) {
        if(hasSecondPassword()) {
            _log.warn("{} forced save Second Factor Password", account);
            closeNow();
            return false;
        }
        return secondFactorAuth.save(account, password);
    }

    public boolean changeSecondFactorPassword(String password, String newPassword) {
        if(!hasSecondPassword()) {
            _log.warn("{} forced change Second Factor Password", account);
            closeNow();
            return false;
        }
        return secondFactorAuth.changePassword(password, newPassword);
    }

    public void setCharacters(List<Character> characters) {
        this.characters = characters;
    }

    public List<Character> getCharacters() {
        return characters;
    }

    public void removeCharacter(Character character) {
        characters.remove(character);
    }

    public void addCharacter(Character character) {
        characters.add(character);
    }




    // ###########################################


    /**
     * CONNECTED - client has just connected
     * AUTHED - client has authed but doesnt has character attached to it yet
     * IN_GAME - client has selected a char and is in game
     *
     * @author KenM
     */
    public enum GameClientState {
        CONNECTED,
        AUTHED,
        IN_GAME
    }

    public GameClientState state;

    // Info
    public String account;
    public SessionKey sessionId;
    public L2PcInstance activeChar;
    private final ReentrantLock _activeCharLock = new ReentrantLock();

    private final long _connectionStartTime;

    // Task
    protected/* final */ ScheduledFuture<?> _autoSaveInDB;
    protected ScheduledFuture<?> _cleanupTask = null;

    // Crypt
    public GameCrypt crypt;

    // Flood protection
    public byte packetsSentInSec = 0;
    public int packetsSentStartTick = 0;

    public byte[] enableCrypt() {
        byte[] key = BlowFishKeygen.getRandomKey();
        crypt.setKey(key);
        return key;
    }

    public GameClientState getState() {
        return state;
    }

    public void setState(GameClientState pState) {
        state = pState;
    }

    public long getConnectionStartTime() {
        return _connectionStartTime;
    }


    @Override
    public int encrypt(byte[] data, int offset, int size) {
        crypt.encrypt(data, offset, size);
        return size;
    }

    @Override
    public boolean decrypt(byte[] data, int offset, int size) {
        crypt.decrypt(data, offset, size);
        return true;
    }

    public L2PcInstance getActiveChar() {
        return activeChar;
    }

    public void setActiveChar(L2PcInstance pActiveChar) {
        activeChar = pActiveChar;
        if (activeChar != null) {
            L2World.getInstance().storeObject(getActiveChar());
        }
    }

    public ReentrantLock getActiveCharLock() {
        return _activeCharLock;
    }

    public void setGameGuardOk(boolean val) {
    }

    public String getAccount() {
        return account;
    }

    public void setSessionId(SessionKey sk) {
        sessionId = sk;
    }

    public SessionKey getSessionId() {
        return sessionId;
    }

    public void sendPacket(L2GameServerPacket gsp) {
        writePacket(gsp);
        gsp.runImpl();
    }

    /**
     * Save the L2PcInstance to the database.
     *
     * @param cha
     */
    public static void saveCharToDisk(L2PcInstance cha) {
        try {
            cha.store();
        } catch (Exception e) {
            _log.error("Error saving reader character: " + e);
        }
    }

    public L2PcInstance loadCharFromDisk(int charslot) {
        L2PcInstance character = L2PcInstance.load(getCharacterForSlot(charslot).getObjectId());

        if (character != null) {
            // restoreInventory(character);
            // restoreSkills(character);
            // character.restoreSkills();
            // restoreShortCuts(character);
            // restoreWarehouse(character);

            // preinit some values for each login
            character.setRunning(); // running is default
            character.standUp(); // standing is default

            character.refreshOverloaded();
            character.refreshExpertisePenalty();
            character.sendPacket(new UserInfo(character));
            character.broadcastKarma();
            character.setOnlineStatus(true);
        } else {
            _log.error("could not restore in slot: " + charslot);
        }

        // setCharacter(character);
        return character;
    }

    @Override
    protected void onDisconnection() {
        _log.info("Cliente Disconnected {}", this);
        // no long running tasks here, do it async
        try {
            ThreadPoolManager.getInstance().executeTask(new DisconnectTask());
        } catch (RejectedExecutionException e) {
            // server is closing
        }
    }

    @Override
    public void onConnected() {

    }

    /**
     * Close client connection with {@link ServerClose} packet
     */
    public void closeNow() {
        close(new ServerClose());
        synchronized (this) {
            if (_cleanupTask != null) {
                cancelCleanup();
            }
            _cleanupTask = ThreadPoolManager.getInstance().scheduleGeneral(new CleanupTask(), 0); // instant
        }
    }

    private boolean cancelCleanup() {
        final Future<?> task = _cleanupTask;
        if (task != null) {
            _cleanupTask = null;
            return task.cancel(true);
        }
        return false;
    }

    protected class CleanupTask implements Runnable {
        @Override
        public void run() {
            try {
                // we are going to manually save the char bellow thus we can force the cancel
                if (_autoSaveInDB != null) {
                    _autoSaveInDB.cancel(true);
                    // ThreadPoolManager.getInstance().removeGeneral((Runnable) _autoSaveInDB);
                }

                if (getActiveChar() != null) // this should only happen on connection loss
                {
                    // prevent closing again
                    getActiveChar().setClient(null);

                    if (getActiveChar().isOnline()) {
                        getActiveChar().deleteMe();
                    }
                }
                setActiveChar(null);
            } catch (Exception e1) {
                _log.warn("Error while cleanup client.", e1);
            } finally {
                AuthServerClient.getInstance().sendLogout(getAccount());
            }
        }
    }

    /**
     * Produces the best possible string representation of this client.
     */
    @Override
    public String toString() {
        try {
            String address = getHostAddress();
            switch (getState()) {
                case CONNECTED:
                    return "[IP: " + (isNullOrEmpty(address) ? "disconnect" : address) + "]";
                case AUTHED:
                    return "[Account: " + getAccount() + " - IP: " + (isNullOrEmpty(address) ? "disconnect" : address) + "]";
                case IN_GAME:
                    return "[Character: " + (getActiveChar() == null ? "disconnect" : getActiveChar().getName()) + " - Account: " + getAccount() + " - IP: " + (isNullOrEmpty(address) ? "disconnect" : address) + "]";
                default:
                    throw new IllegalStateException("Missing state on switch");
            }
        } catch (NullPointerException e) {
            return "[Character read failed due to disconnect]";
        }
    }

    class DisconnectTask implements Runnable {

        /**
         * @see Runnable#run()
         */
        @Override
        public void run() {
            try {
                // Update BBS
                try {
                    RegionBBSManager.getInstance().changeCommunityBoard();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // we are going to mannually save the char bellow thus we can force the cancel
                _autoSaveInDB.cancel(true);

                L2PcInstance player = getActiveChar();
                if (player != null) // this should only happen on connection loss
                {

                    // we store allTemplates data from players who are disconnect while in an event in order to restore it in the next login
                    if (player.atEvent) {
                        EventData data = new EventData(player.eventX, player.eventY, player.eventZ, player.eventkarma, player.eventpvpkills, player.eventpkkills, player.eventTitle, player.kills, player.eventSitForced);
                        L2Event.connectionLossData.put(player.getName(), data);
                    }
                    if (player.isFlying()) {
                        player.removeSkill(SkillTable.getInstance().getInfo(4289, 1));
                    }
                    // notify the world about our disconnect
                    player.deleteMe();

                    try {
                        saveCharToDisk(player);
                    } catch (Exception e2) { /* ignore any problems here */
                    }
                }
                setActiveChar(null);
            } catch (Exception e1) {
                _log.warn("error while disconnecting client", e1);
            } finally {
                AuthServerClient.getInstance().sendLogout(getAccount());
            }
        }
    }

    class AutoSaveTask implements Runnable {
        @Override
        public void run() {
            try {
                L2PcInstance player = getActiveChar();
                if (player != null) {
                    saveCharToDisk(player);
                }
            } catch (Throwable e) {
                _log.error(e.toString());
            }
        }
    }
}
