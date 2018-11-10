package org.l2j.gameserver.network.clientpackets;

import org.l2j.commons.Config;
import org.l2j.gameserver.Olympiad;
import org.l2j.gameserver.SevenSignsFestival;
import org.l2j.gameserver.communitybbs.Manager.RegionBBSManager;
import org.l2j.gameserver.datatables.SkillTable;
import org.l2j.gameserver.model.L2Party;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.entity.TvTEvent;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.L2GameClient.GameClientState;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.CharSelectInfo;
import org.l2j.gameserver.network.serverpackets.RestartResponse;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.taskmanager.AttackStanceTaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RequestRestart extends L2GameClientPacket  {

	private static Logger _log = LoggerFactory.getLogger(RequestRestart.class);
	
	@Override
	protected void readImpl() {
		// trigger
	}
	
	@Override
	protected void runImpl() {
		L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			_log.warn("[RequestRestart] activeChar null!?");
			return;
		}
		
		if (player.isInOlympiadMode() || Olympiad.getInstance().isRegistered(player))
		{
			player.sendMessage("You cant logout in olympiad mode");
			return;
		}
		
		player.getInventory().updateDatabase();
		
		if (player.getPrivateStoreType() != 0)
		{
			player.sendMessage("Cannot restart while trading");
			return;
		}
		
		if (player.getActiveRequester() != null)
		{
			player.getActiveRequester().onTradeCancel(player);
			player.onTradeCancel(player.getActiveRequester());
		}
		
		if (AttackStanceTaskManager.getInstance().getAttackStanceTask(player))
		{
			if (Config.DEBUG)
			{
				_log.debug("Player " + player.getName() + " tried to logout while fighting.");
			}
			
			player.sendPacket(new SystemMessage(SystemMessageId.CANT_RESTART_WHILE_FIGHTING));
			player.sendPacket(new ActionFailed());
			return;
		}
		
		// Prevent reader from restarting if they are a festival participant
		// and it is in progress, otherwise notify party members that the reader
		// is not longer a participant.
		if (player.isFestivalParticipant())
		{
			if (SevenSignsFestival.getInstance().isFestivalInitialized())
			{
				player.sendPacket(SystemMessage.sendString("You cannot restart while you are a participant in a festival."));
				player.sendPacket(new ActionFailed());
				return;
			}
			L2Party playerParty = player.getParty();
			
			if (playerParty != null)
			{
				player.getParty().broadcastToPartyMembers(SystemMessage.sendString(player.getName() + " has been removed from the upcoming festival."));
			}
		}
		if (player.isFlying())
		{
			player.removeSkill(SkillTable.getInstance().getInfo(4289, 1));
		}

		// detach the client from the char so that the connection isnt closed in the deleteMe
		player.setClient(null);
		
		TvTEvent.onLogout(player);
		RegionBBSManager.getInstance().changeCommunityBoard();
		
		// removing reader from the world
		player.deleteMe();
		L2GameClient.saveCharToDisk(client.getActiveChar());
		
		client.setActiveChar(null);
		client.setState(GameClientState.AUTHED);

		sendPacket(new RestartResponse());
		sendPacket(new CharSelectInfo());
	}
}