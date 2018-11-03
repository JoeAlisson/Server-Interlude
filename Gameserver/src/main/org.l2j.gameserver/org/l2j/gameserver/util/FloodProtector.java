/* This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package org.l2j.gameserver.util;

import org.l2j.commons.Config;
import org.l2j.gameserver.GameTimeController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.isNull;

/**
 * Flood protector
 *
 * @author durgus
 */
public class FloodProtector {
    private static final Logger _log = LoggerFactory.getLogger(FloodProtector.class.getName());
    private static FloodProtector _instance;

    public static final FloodProtector getInstance() {
        if (isNull(_instance)) {
            _instance = new FloodProtector();
        }
        return _instance;
    }


    private final Map<Integer, Integer[]> _floodClient;

    // reuse delays for protected actions (in game ticks 1 tick = 100ms)
    private static final int[] REUSEDELAY = new int[]
                                                {
                                                    4,
                                                    42,
                                                    42,
                                                    16,
                                                    100
                                                };


    public static final int PROTECTED_USEITEM = 0;
    public static final int PROTECTED_ROLLDICE = 1;
    public static final int PROTECTED_FIREWORK = 2;
    public static final int PROTECTED_ITEMPETSUMMON = 3;
    public static final int PROTECTED_HEROVOICE = 4;

    private FloodProtector() {
        _log.info("Initializing FloodProtector");
        _floodClient = new ConcurrentHashMap<>(Config.FLOODPROTECTOR_INITIALSIZE);
    }

    /**
     * Add a new reader to the flood protector (should be done for allTemplates players when they enter the world)
     *
     * @param playerObjId
     */
    public void registerNewPlayer(int playerObjId) {
        // create a new array
        Integer[] array = new Integer[REUSEDELAY.length];
        for (int i = 0; i < array.length; i++) {
            array[i] = 0;
        }

        // register the reader with an empty array
        _floodClient.put(playerObjId, array);
    }

    /**
     * Remove a reader from the flood protector (should be done if reader loggs off)
     *
     * @param playerObjId
     */
    public void removePlayer(int playerObjId) {
        _floodClient.remove(playerObjId);
    }

    /**
     * Return the size of the flood protector
     *
     * @return size
     */
    public int getSize() {
        return _floodClient.size();
    }

    /**
     * Try to perform the requested action
     *
     * @param playerObjId
     * @param action
     * @return true if the action may be performed
     */
    public boolean tryPerformAction(int playerObjId, int action) {


        Integer[] value =  _floodClient.get(playerObjId);

        if (value[action] < GameTimeController.getGameTicks()) {
            value[action] = GameTimeController.getGameTicks() + REUSEDELAY[action];
            _floodClient.put(playerObjId, value);
            return true;
        }
        return false;
    }
}