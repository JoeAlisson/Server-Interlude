package org.l2j.gameserver.clientpackets;

import org.l2j.commons.Base64;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.*;
import org.l2j.gameserver.instancemanager.*;
import org.l2j.gameserver.model.*;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.entity.database.Wedding;
import org.l2j.gameserver.model.entity.database.repository.CharacterFriendRepository;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.serverpackets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.Objects;

import static java.util.Objects.isNull;

public class EnterWorld extends L2GameClientPacket {

	private static Logger _log = LoggerFactory.getLogger(EnterWorld.class);
	
	public TaskPriority getPriority()
	{
		return TaskPriority.PR_URGENT;
	}
	
	@Override
	protected void readImpl() {
		// this is just a trigger packet. it has no content
	}
	
	@Override
	protected void runImpl() {
		var activeChar = client.getActiveChar();
		
		if (isNull(activeChar)) {
			_log.warn("EnterWorld failed! activeChar is null...");
			client.closeNow();
			return;
		}

        //sendPacket(new ExLightingCandleEvent(ExLightingCandleEvent.ENABLED));
        sendPacket(new ExEnterWorldPacket());
        /*sendPacket(new ExConnectedTimeAndGettableReward());
        sendPacket(new ExOneDayReceiveRewardList(activeChar));
        sendPacket(new ExConnectedTimeAndGettableReward());
        sendPacket(new ExPeriodicHenna(activeChar));
        sendPacket(new HennaInfoPacket(activeChar));
        sendPacket(new EtcStatusUpdate(activeChar));*/
        sendPacket(new UserInfoPacket(activeChar));
		activeChar.spawnMe(activeChar.getX(), activeChar.getY(), activeChar.getZ());
        sendPacket(new SystemMessage(SystemMessageId.WELCOME_TO_THE_WORLD_OF_LINEAGE_II));
/*

		// Register in flood protector
		FloodProtector.getInstance().registerNewPlayer(activeChar.getObjectId());
		
		if (L2World.getInstance().findObject(activeChar.getObjectId()) != null)
		{
			if (Config.DEBUG)
			{
				logger.warn("User already exist in OID map! User " + activeChar.getName() + " is character clone");
				// activeChar.closeNetConnection();
			}
		}
		
		if (activeChar.isGM())
		{
			if (Config.GM_STARTUP_INVULNERABLE && ((!Config.ALT_PRIVILEGES_ADMIN && (activeChar.getAccessLevel() >= Config.GM_GODMODE)) || (Config.ALT_PRIVILEGES_ADMIN && AdminCommandHandler.getInstance().checkPrivileges(activeChar, "admin_invul"))))
			{
				activeChar.setIsInvul(true);
			}
			
			if (Config.GM_STARTUP_INVISIBLE && ((!Config.ALT_PRIVILEGES_ADMIN && (activeChar.getAccessLevel() >= Config.GM_GODMODE)) || (Config.ALT_PRIVILEGES_ADMIN && AdminCommandHandler.getInstance().checkPrivileges(activeChar, "admin_invisible"))))
			{
				activeChar.setInvisible(true);
			}
			
			if (Config.GM_STARTUP_SILENCE && ((!Config.ALT_PRIVILEGES_ADMIN && (activeChar.getAccessLevel() >= Config.GM_MENU)) || (Config.ALT_PRIVILEGES_ADMIN && AdminCommandHandler.getInstance().checkPrivileges(activeChar, "admin_silence"))))
			{
				activeChar.setMessageRefusal(true);
			}
			
			if (Config.GM_STARTUP_AUTO_LIST && ((!Config.ALT_PRIVILEGES_ADMIN && (activeChar.getAccessLevel() >= Config.GM_MENU)) || (Config.ALT_PRIVILEGES_ADMIN && AdminCommandHandler.getInstance().checkPrivileges(activeChar, "admin_gmliston"))))
			{
				GmListTable.getInstance().addGm(activeChar, false);
			}
			else
			{
				GmListTable.getInstance().addGm(activeChar, true);
			}
			
			if (Config.GM_NAME_COLOR_ENABLED)  {
				if (activeChar.getAccessLevel() >= 100) {
					activeChar.setNameColor(Config.ADMIN_NAME_COLOR);
				} else if (activeChar.getAccessLevel() >= 75)  {
					activeChar.setNameColor(Config.GM_NAME_COLOR);
				}
			}
		}
		
		if (Config.PLAYER_SPAWN_PROTECTION > 0) {
			activeChar.setProtection(true);
		}
		
		activeChar.spawnMe(activeChar.getX(), activeChar.getY(), activeChar.getZ());
		
		if (L2Event.active && L2Event.connectionLossData.containsKey(activeChar.getName()) && L2Event.isOnEvent(activeChar))
		{
			L2Event.restoreChar(activeChar);
		}
		else if (L2Event.connectionLossData.containsKey(activeChar.getName()))
		{
			L2Event.restoreAndTeleChar(activeChar);
		}
		
		if (SevenSigns.getInstance().isSealValidationPeriod())
		{
			sendPacket(new SignsSky());
		}
		
		// buff and status icons
		if (Config.STORE_SKILL_COOLTIME)
		{
			activeChar.restoreEffects();
		}
		
		activeChar.sendPacket(new EtcStatusUpdate(activeChar));
		
		// engage and notify Partner
		if (Config.L2JMOD_ALLOW_WEDDING)
		{
			engage(activeChar);
			notifyPartner(activeChar, activeChar.getPartnerId());
		}
		
		if (activeChar.getAllEffects() != null)
		{
			for (L2Effect e : activeChar.getAllEffects())
			{
				if (e.getEffectType() == L2Effect.EffectType.HEAL_OVER_TIME)
				{
					activeChar.stopEffects(L2Effect.EffectType.HEAL_OVER_TIME);
					activeChar.removeEffect(e);
				}
				
				if (e.getEffectType() == L2Effect.EffectType.COMBAT_POINT_HEAL_OVER_TIME)
				{
					activeChar.stopEffects(L2Effect.EffectType.COMBAT_POINT_HEAL_OVER_TIME);
					activeChar.removeEffect(e);
				}
			}
		}
		
		// apply augmentation boni for equipped items
		for (L2ItemInstance temp : activeChar.getInventory().getAugmentedItems())
		{
			if ((temp != null) && temp.isEquipped())
			{
				temp.getAugmentation().applyBoni(activeChar);
			}
		}
		
		// Expand Skill
		ExStorageMaxCount esmc = new ExStorageMaxCount(activeChar);
		activeChar.sendPacket(esmc);
		
		activeChar.getMacroses().sendUpdate();
		
		sendPacket(new UserInfo(activeChar));
		
		sendPacket(new HennaInfo(activeChar));
		
		sendPacket(new FriendList(activeChar));
		
		sendPacket(new ItemList(activeChar, false));
		
		sendPacket(new ShortCutInit(activeChar));
		
		SystemMessage sm = new SystemMessage(SystemMessageId.WELCOME_TO_LINEAGE);
		sendPacket(sm);
		
		sm = new SystemMessage(SystemMessageId.S1_S2);
		sm.addString(getText("V2VsY29tZSB0byBhIEwySiBTZXJ2ZXIsIGZvdW5kZWQgYnkgTDJDaGVmLg=="));
		
		sendPacket(sm);
		sm = new SystemMessage(SystemMessageId.S1_S2);
		sm.addString(getText("RGV2ZWxvcGVkIGJ5IHRoZSBMMkogRGV2IFRlYW0gYXQgbDJqc2VydmVyLmNvbS4="));
		
		sendPacket(sm);
		
		sm = new SystemMessage(SystemMessageId.S1_S2);
		sm.addString(getText("Q29weXJpZ2h0IDIwMDQtMjAwNw=="));
		sendPacket(sm);
		sm = new SystemMessage(SystemMessageId.S1_S2);
		sm.addString(getText("V2VsY29tZSB0byA="));
		sm.addString(AuthServerClient.getInstance().getServerName());
		sendPacket(sm);
		
		SevenSigns.getInstance().sendCurrentPeriodMsg(activeChar);
		Announcements.getInstance().showAnnouncements(activeChar);
		
		Quest.playerEnter(activeChar);
		activeChar.sendPacket(new QuestList());
		
		if (Config.SERVER_NEWS)
		{
			String serverNews = HtmCache.getInstance().getHtm("data/html/servnews.htm");
			if (serverNews != null)
			{
				sendPacket(new NpcHtmlMessage(1, serverNews));
			}
		}
		
		PetitionManager.getInstance().checkPetitionMessages(activeChar);
		
		// send user info again .. just like the real client
		// sendPacket(ui);
		
		if ((activeChar.getClanId() != 0) && (activeChar.getClan() != null))
		{
			sendPacket(new PledgeShowMemberListAll(activeChar.getClan(), activeChar));
			sendPacket(new PledgeStatusChanged(activeChar.getClan()));
		}
		
		if (activeChar.isAlikeDead())
		{
			// no broadcast needed since the reader will already spawn dead to others
			sendPacket(new Die(activeChar));
		}
		
		if (Config.ALLOW_WATER)
		{
			activeChar.checkWaterState();
		}
		
		if ((Heroes.getInstance().getHeroes() != null) && Heroes.getInstance().getHeroes().containsKey(activeChar.getObjectId()))
		{
			activeChar.setHero(true);
		}
		
		setPledgeClass(activeChar);
		
		// add char to online characters
		activeChar.setOnlineStatus(true);
		
		notifyFriends(activeChar);
		notifyClanMembers(activeChar);
		notifySponsorOrApprentice(activeChar);
		
		activeChar.onPlayerEnter();
		
		if (Olympiad.getInstance().playerInStadia(activeChar))
		{
			activeChar.teleToLocation(MapRegionTable.TeleportWhereType.Town);
			activeChar.sendMessage("You have been teleported to the nearest town due to you being in an Olympiad Stadium");
		}
		
		if (DimensionalRiftManager.getInstance().checkIfInRiftZone(activeChar.getX(), activeChar.getY(), activeChar.getZ(), false))
		{
			DimensionalRiftManager.getInstance().teleportToWaitingRoom(activeChar);
		}
		
		if (activeChar.getClanJoinExpiryTime() > System.currentTimeMillis())
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.CLAN_MEMBERSHIP_TERMINATED));
		}
		
		if (activeChar.getClan() != null)
		{
			activeChar.sendPacket(new PledgeSkillList(activeChar.getClan()));
			
			for (Siege siege : SiegeManager.getInstance().getSieges())
			{
				if (!siege.getIsInProgress())
				{
					continue;
				}
				if (siege.checkIsAttacker(activeChar.getClan()))
				{
					activeChar.setSiegeState((byte) 1);
				}
				else if (siege.checkIsDefender(activeChar.getClan()))
				{
					activeChar.setSiegeState((byte) 2);
				}
			}
			// Add message at connexion if clanHall not paid.
			// Possibly this is custom...
			ClanHall clanHall = ClanHallManager.getInstance().getClanHallByOwner(activeChar.getClan());
			if (clanHall != null)
			{
				if (!clanHall.isPaid())
				{
					activeChar.sendPacket(new SystemMessage(SystemMessageId.PAYMENT_FOR_YOUR_CLAN_HALL_HAS_NOT_BEEN_MADE_PLEASE_MAKE_PAYMENT_TO_YOUR_CLAN_WAREHOUSE_BY_S1_TOMORROW));
				}
			}
		}
		
		if (!activeChar.isGM() && (activeChar.getSiegeState() < 2) && activeChar.isInsideZone(Zone.SIEGE))
		{
			// Attacker or spectator logging in to a siege zone. Actually should be checked for inside castle only?
			activeChar.teleToLocation(MapRegionTable.TeleportWhereType.Town);
			activeChar.sendMessage("You have been teleported to the nearest town due to you being in siege zone");
		}
		
		RegionBBSManager.getInstance().changeCommunityBoard();
		
		*//*
		 * if(Config.GAMEGUARD_ENFORCE) - disabled by KenM will be reenabled later activeChar.sendPacket(new GameGuardQuery());
		 *//*
		
		TvTEvent.onLogin(activeChar);*/

	}
	
	/**
	 * @param cha
	 */
	private void engage(L2PcInstance cha)
	{
		int _chaid = cha.getObjectId();
		
		for (Wedding cl : CoupleManager.getInstance().getCouples())
		{
			if ((cl.getPlayer1Id() == _chaid) || (cl.getPlayer2Id() == _chaid))
			{
				if (cl.isMarried())
				{
					cha.setMarried(true);
				}
				
				cha.setCoupleId(Objects.requireNonNullElse(cl.getId(), 0));
				
				if (cl.getPlayer1Id() == _chaid)
				{
					cha.setPartnerId(cl.getPlayer2Id());
				}
				else
				{
					cha.setPartnerId(cl.getPlayer1Id());
				}
			}
		}
	}
	
	/**
	 * @param cha
	 * @param partnerId
	 */
	private void notifyPartner(L2PcInstance cha, int partnerId)
	{
		if (cha.getPartnerId() != 0)
		{
			L2PcInstance partner;
			partner = (L2PcInstance) L2World.getInstance().findObject(cha.getPartnerId());
			
			if (partner != null)
			{
				partner.sendMessage("Your Partner has logged in");
			}
			
			partner = null;
		}
	}

	private void notifyFriends(L2PcInstance cha) {
        CharacterFriendRepository repository = DatabaseAccess.getRepository(CharacterFriendRepository.class);
        SystemMessage sm = new SystemMessage(SystemMessageId.FRIEND_S1_HAS_LOGGED_IN);
        sm.addString(cha.getName());
        repository.findAllByCharacterId(cha.getObjectId()).forEach(characterFriends -> {
            L2PcInstance friend = L2World.getInstance().getPlayer(characterFriends.getFriendName());

            if (friend != null) // friend logged in.
            {
                friend.sendPacket(new FriendList(friend));
                friend.sendPacket(sm);
            }
        });
	}
	
	/**
	 * @param activeChar
	 */
	private void notifyClanMembers(L2PcInstance activeChar)
	{
		L2Clan clan = activeChar.getClan();
		if (clan != null)
		{
			clan.getClanMember(activeChar.getName()).setPlayerInstance(activeChar);
			SystemMessage msg = new SystemMessage(SystemMessageId.CLAN_MEMBER_S1_LOGGED_IN);
			msg.addString(activeChar.getName());
			clan.broadcastToOtherOnlineMembers(msg, activeChar);
			msg = null;
			clan.broadcastToOtherOnlineMembers(new PledgeShowMemberListUpdate(activeChar), activeChar);
		}
	}
	
	/**
	 * @param activeChar
	 */
	private void notifySponsorOrApprentice(L2PcInstance activeChar)
	{
		if (activeChar.getSponsor() != 0)
		{
			L2PcInstance sponsor = (L2PcInstance) L2World.getInstance().findObject(activeChar.getSponsor());
			
			if (sponsor != null)
			{
				SystemMessage msg = new SystemMessage(SystemMessageId.YOUR_APPRENTICE_S1_HAS_LOGGED_IN);
				msg.addString(activeChar.getName());
				sponsor.sendPacket(msg);
			}
		}
		else if (activeChar.getApprentice() != 0)
		{
			L2PcInstance apprentice = (L2PcInstance) L2World.getInstance().findObject(activeChar.getApprentice());
			
			if (apprentice != null)
			{
				SystemMessage msg = new SystemMessage(SystemMessageId.YOUR_SPONSOR_S1_HAS_LOGGED_IN);
				msg.addString(activeChar.getName());
				apprentice.sendPacket(msg);
			}
		}
	}
	
	/**
	 * @param string
	 * @return
	 */
	private String getText(String string)
	{
		try
		{
			String result = new String(Base64.decode(string), "UTF-8");
			return result;
		}
		catch (UnsupportedEncodingException e)
		{
			// huh, UTF-8 is not supported? :)
			return null;
		}
	}
	
	private void setPledgeClass(L2PcInstance activeChar)
	{
		int pledgeClass = 0;
		if (activeChar.getClan() != null)
		{
			pledgeClass = activeChar.getClan().getClanMember(activeChar.getObjectId()).calculatePledgeClass(activeChar);
		}
		
		if (activeChar.isNoble() && (pledgeClass < 5))
		{
			pledgeClass = 5;
		}
		
		if (activeChar.isHero())
		{
			pledgeClass = 8;
		}
		
		activeChar.setPledgeClass(pledgeClass);
	}
}
