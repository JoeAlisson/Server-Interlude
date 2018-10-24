package org.l2j.gameserver;

import org.l2j.commons.Config;
import org.l2j.commons.Server;
import org.l2j.commons.status.Status;
import org.l2j.gameserver.datatables.PlayerTemplateTable;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.L2GamePacketHandler;
import org.l2j.gameserver.status.GameStatus;
import org.l2j.gameserver.util.IPv4Filter;
import org.l2j.mmocore.ConnectionBuilder;
import org.l2j.mmocore.ConnectionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.Locale;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameserverMessages.getMessage;

public class GameServer {

    private static final String INFO_LOADED_HANDLERS = "info.loaded.handlers";
    private static final String LOG4J_CONFIGURATION_FILE = "log4j.configurationFile";

    private ConnectionHandler<L2GameClient> connectionHandler;

    private static Logger _log;
    private static GameServer gameServer;

    public static final Instant instantServerStarted = Instant.now();

    public static void shutdown() {
        if (nonNull(gameServer)) {
            gameServer.connectionHandler.shutdown();
            gameServer.connectionHandler.setDaemon(true);
        }
    }

    public GameServer() throws Exception {
        gameServer = this;
        makeDataDirectories();

        PlayerTemplateTable.getInstance();


/*
        ThreadPoolManager.getInstance();
        IdFactory.getInstance();
        // start game time control early
        GameTimeController.getInstance();

        SkillTable.getInstance();
        SkillTreeTable.getInstance();
        NobleSkillTable.getInstance();
        HeroSkillTable.getInstance();

        ItemTable.getInstance();
        SkillSpellbookTable.getInstance();
        ArmorSetsTable.getInstance();
        RecipeController.getInstance();
        ExtractableItemsData.getInstance();
        SummonItemsData.getInstance();
        HennaTable.getInstance();
        HennaTreeTable.getInstance();
        L2Manor.getInstance();

        NpcTable.getInstance();
        NpcWalkerRoutesTable.getInstance();
        HelperBuffTable.getInstance();
        TradeController.getInstance();
        FishTable.getInstance();

        HtmCache.getInstance();
        CrestCache.getInstance();

        ClanTable.getInstance();

        ClanHallManager.getInstance(); // Load clan hall data before zone data
        ZoneData.getInstance();

        CastleManager.getInstance();
        SiegeManager.getInstance();

        TeleportLocationTable.getInstance();

        L2World.getInstance();

        DayNightSpawnManager.getInstance().notifyChangeMode();
        SpawnTable.getInstance();
        RaidBossSpawnManager.getInstance();
        DimensionalRiftManager.getInstance();

        Announcements.getInstance();

        MapRegionTable.getInstance();

        GeoData.getInstance();
        if (Config.GEODATA == 2) {
            GeoPathFinding.getInstance();
        }

        AuctionManager.getInstance();
        BoatManager.getInstance();
        CastleManorManager.getInstance();
        MercTicketManager.getInstance();
        PetitionManager.getInstance();
        ScriptingManager.getInstance();
        QuestManager.getInstance();
        AugmentationData.getInstance();
        if (Config.SAVE_DROPPED_ITEM) {
            ItemsOnGroundManager.getInstance();
        }


        if ((Config.AUTODESTROY_ITEM_AFTER > 0) || (Config.HERB_AUTO_DESTROY_TIME > 0)) {
            ItemsAutoDestroy.getInstance();
        }

        MonsterRace.getInstance();

        DoorTable.getInstance();
        StaticObjects.getInstance();

        SevenSigns _sevenSignsEngine = SevenSigns.getInstance();
        SevenSignsFestival.getInstance();
        AutoSpawnHandler.getInstance();
        AutoChatHandler.getInstance();

        // Spawn the Orators/Preachers if in the Seal Validation period.
        _sevenSignsEngine.spawnSevenSignsNPC();

        Olympiad.getInstance();
        Heroes.getInstance();
        ScriptEventManager.getInstance();
        // Init of a cursed weapon manager
        CursedWeaponsManager.getInstance();

        ItemHandler _itemHandler = ItemHandler.getInstance();
        _log.info(getMessage(INFO_LOADED_HANDLERS, _itemHandler.size(), "ItemHandler"));

        SkillHandler _skillHandler = SkillHandler.getInstance();
        _log.info(getMessage(INFO_LOADED_HANDLERS, _skillHandler.size(), "SkillHandler"));

        AdminCommandHandler _adminCommandHandler = AdminCommandHandler.getInstance();
        _log.info(getMessage(INFO_LOADED_HANDLERS, _adminCommandHandler.size(), "AdminCommandHandler"));

        UserCommandHandler _userCommandHandler = UserCommandHandler.getInstance();
        _log.info(getMessage(INFO_LOADED_HANDLERS, _userCommandHandler.size(), "UserCommandHandler"));

        VoicedCommandHandler _voicedCommandHandler = VoicedCommandHandler.getInstance();
        _log.info(getMessage(INFO_LOADED_HANDLERS, _voicedCommandHandler.size(), "VoicedCommandHandler"));

        if (Config.L2JMOD_ALLOW_WEDDING) {
            CoupleManager.getInstance();
        }

        TaskManager.getInstance();

        GmListTable.getInstance();

        // read pet stats from db
        L2PetDataTable.getInstance().loadPetsData();

        Universe.getInstance();

        if (Config.ACCEPT_GEOEDITOR_CONN) {
            GeoEditorListener.getInstance();
        }

        Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());

        ForumsBBSManager.getInstance();

        _log.info(getMessage("info.object.id.free", IdFactory.getInstance().size()));

        // initialize the dynamic extension loader
        try {
            DynamicExtension.getInstance();
        } catch (Exception ex) {
            _log.warn(getMessage("error.dynamic.extension.not.loaded"), ex);
        }

        FloodProtector.getInstance();
        TvTManager.getInstance();
*/

        // maxMemory is the upper limit the jvm can use, totalMemory the size of the current allocation pool, freeMemory the unused memory in the allocation pool
        long freeMem = ((Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory()) + Runtime.getRuntime().freeMemory()) / 1048576; // 1024 * 1024 = 1048576;
        long totalMem = Runtime.getRuntime().maxMemory() / 1048576;

        _log.info(getMessage("info.free.memory", freeMem, totalMem));

        LoginServerThread _loginThread = LoginServerThread.getInstance();
        _loginThread.start();

        var bindAddress =  getInetSocketAddress();
        L2GamePacketHandler gph = new L2GamePacketHandler();
        connectionHandler = ConnectionBuilder.create(bindAddress, gph, gph, gph).filter(new IPv4Filter()).build();
        connectionHandler.start();
        _log.info(getMessage("info.max.connected.players", Config.MAXIMUM_ONLINE_USERS));
    }

    private InetSocketAddress getInetSocketAddress() {
        InetSocketAddress bindAddress;
        if (!Config.GAMESERVER_HOSTNAME.equals("*")) {
            bindAddress = new InetSocketAddress(Config.GAMESERVER_HOSTNAME, Config.PORT_GAME);
        } else {
            bindAddress = new InetSocketAddress(Config.PORT_GAME);
        }
        return bindAddress;
    }

    private void makeDataDirectories() {
        new File(Config.DATAPACK_ROOT, "data/clans").mkdirs();
        new File(Config.DATAPACK_ROOT, "data/crests").mkdirs();
        new File("pathnode").mkdirs();
    }

    public static void main(String[] args) throws Exception {
        Server.serverMode = Server.MODE_GAMESERVER;
        configureLogger();
        Config.load();
        Locale.setDefault(Locale.forLanguageTag(Config.LANGUAGE));

        gameServer = new GameServer();

        if (Config.IS_TELNET_ENABLED) {
            Status _statusServer = new GameStatus();
            _statusServer.start();
        } else {
            System.out.println(getMessage("info.telnet.disabled"));
        }
    }

    private static void configureLogger() {
        String logConfigurationFile = System.getProperty(LOG4J_CONFIGURATION_FILE);
        if (logConfigurationFile == null || logConfigurationFile.isEmpty()) {
            System.setProperty(LOG4J_CONFIGURATION_FILE, "log4j.xml");
        }
        _log = LoggerFactory.getLogger(GameServer.class);
    }
}
