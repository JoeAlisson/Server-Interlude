package org.l2j.gameserver.serverpackets;

import org.l2j.commons.database.DatabaseAccess;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.entity.database.CharacterFriends;
import org.l2j.gameserver.model.entity.database.repository.CharacterFriendRepository;

import java.util.List;

/**
 * Support for "Chat with Friends" dialog.
 *
 * Format: ch (hdSdh) h: Total Friend Count h: Unknown d: Player Object ID S: Friend Name d: Online/Offline h: Unknown
 * @author Tempy
 */
public class FriendList extends L2GameServerPacket {

	private final L2PcInstance _activeChar;
	
	public FriendList(L2PcInstance character)  {
		_activeChar = character;
	}
	
	@Override
	protected final void writeImpl() {
		if (_activeChar == null) {
			return;
		}
		
        CharacterFriendRepository repository = DatabaseAccess.getRepository(CharacterFriendRepository.class);
        List<CharacterFriends> friendList = repository.findAllByCharacterId(_activeChar.getObjectId());

        if(!Util.isNullOrEmpty(friendList)) {
            writeByte(0xfa);
            writeShort(friendList.size());
            friendList.forEach(characterFriends -> {
                L2PcInstance friend = L2World.getInstance().getPlayer(characterFriends.getFriendName());

                writeShort(0); // ??
                writeInt(characterFriends.getFriendId());
                writeString(characterFriends.getFriendName());
                writeInt(friend == null ? 0 : 1);  // offline : online
                writeShort(0); // ??

            });
        }
	}

}
