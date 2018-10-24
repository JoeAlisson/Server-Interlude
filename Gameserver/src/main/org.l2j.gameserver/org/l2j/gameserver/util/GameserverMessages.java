package org.l2j.gameserver.util;

import org.l2j.commons.util.Messages;


public class GameserverMessages {

    public static String getMessage(String key, Object... arguments) {
        return Messages.getMessage("gameserver-messages", key, arguments);

    }
}
