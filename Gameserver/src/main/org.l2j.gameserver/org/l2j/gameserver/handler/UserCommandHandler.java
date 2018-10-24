/*
 * This program is free software; you can redistribute it and/or modify
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
package org.l2j.gameserver.handler;

import org.l2j.commons.Config;
import org.l2j.gameserver.handler.usercommandhandlers.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.isNull;


/**
 * This class ...
 *
 * @version $Revision: 1.1.2.1.2.5 $ $Date: 2005/03/27 15:30:09 $
 */
public class UserCommandHandler {
    private static Logger _log = LoggerFactory.getLogger(UserCommandHandler.class.getName());

    private static UserCommandHandler _instance;

    private final Map<Integer, IUserCommandHandler> _datatable;

    public static UserCommandHandler getInstance() {
        if (isNull(_instance)) {
            _instance = new UserCommandHandler();
        }
        return _instance;
    }

    private UserCommandHandler() {
        _datatable = new HashMap<>();
        load();
    }

    private void load() {
        registerUserCommandHandler(new ClanPenalty());
        registerUserCommandHandler(new ClanWarsList());
        registerUserCommandHandler(new DisMount());
        registerUserCommandHandler(new Escape());
        registerUserCommandHandler(new Loc());
        registerUserCommandHandler(new Mount());
        registerUserCommandHandler(new PartyInfo());
        registerUserCommandHandler(new Time());
        registerUserCommandHandler(new OlympiadStat());
        registerUserCommandHandler(new ChannelLeave());
        registerUserCommandHandler(new ChannelDelete());
        registerUserCommandHandler(new ChannelListUpdate());
    }

    public void registerUserCommandHandler(IUserCommandHandler handler) {
        int[] ids = handler.getUserCommandList();
        for (int id : ids) {
            if (Config.DEBUG) {
                _log.debug("Adding handler for user command " + id);
            }
            _datatable.put(id, handler);
        }
    }

    public IUserCommandHandler getUserCommandHandler(int userCommand) {
        if (Config.DEBUG) {
            _log.debug("getting handler for user command: " + userCommand);
        }
        return _datatable.get(userCommand);
    }

    /**
     * @return
     */
    public int size() {
        return _datatable.size();
    }
}
