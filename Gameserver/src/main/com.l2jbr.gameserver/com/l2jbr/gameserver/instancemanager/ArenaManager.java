package com.l2jbr.gameserver.instancemanager;

import com.l2jbr.gameserver.model.L2Character;
import com.l2jbr.gameserver.model.zone.type.L2ArenaZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

public class ArenaManager {

    private static Logger logger = LoggerFactory.getLogger(ArenaManager.class);
    private static ArenaManager _instance;
    private List<L2ArenaZone> _arenas;


    public static final ArenaManager getInstance() {
        if (_instance == null) {
            logger.info("Initializing ArenaManager");
            _instance = new ArenaManager();
        }
        return _instance;
    }

    private ArenaManager() { }

    public void addArena(L2ArenaZone arena) {
        if (_arenas == null) {
            _arenas = new LinkedList<>();
        }

        _arenas.add(arena);
    }

    public final L2ArenaZone getArena(L2Character character) {
        for (L2ArenaZone temp : _arenas) {
            if (temp.isCharacterInZone(character)) {
                return temp;
            }
        }

        return null;
    }
}