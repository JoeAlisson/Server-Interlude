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
package org.l2j.gameserver.datatables;

import org.l2j.commons.Config;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.factory.IdFactory;
import org.l2j.gameserver.instancemanager.SiegeManager;
import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.L2ClanMember;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.entity.Siege;
import org.l2j.gameserver.model.entity.database.repository.*;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.isNull;
import static org.l2j.commons.database.DatabaseAccess.getRepository;
import static org.l2j.gameserver.util.GameserverMessages.getMessage;


/**
 * This class ...
 *
 * @version $Revision: 1.11.2.5.2.5 $ $Date: 2005/03/27 15:29:18 $
 */
public class ClanTable {
    private static Logger _log = LoggerFactory.getLogger(ClanTable.class.getName());

    private static ClanTable _instance;

    private final Map<Integer, L2Clan> _clans;

    public static ClanTable getInstance() {
        if (isNull(_instance)) {
            _instance = new ClanTable();
        }
        return _instance;
    }

    public L2Clan[] getClans() {
        return _clans.values().toArray(new L2Clan[_clans.size()]);
    }

    private ClanTable() {
        _clans = new HashMap<>();
        getRepository(ClanRepository.class).findAll().forEach(clan -> {
            _clans.put(clan.getId(), new L2Clan(clan));
            if(clan.getDissolvingExpiryTime() !=0) {
                if(clan.getDissolvingExpiryTime() <= System.currentTimeMillis()) {
                    destroyClan(clan.getId());
                } else {
                    scheduleRemoveClan(clan.getId());
                }
            }
        });
        _log.info(getMessage("info.restored.clans", _clans.size()));
        restoreWars();
    }

    /**
     * @param clanId
     * @return
     */
    public L2Clan getClan(int clanId) {
        L2Clan clan = _clans.get(clanId);

        return clan;
    }

    public L2Clan getClanByName(String clanName) {
        for (L2Clan clan : getClans()) {
            if (clan.getName().equalsIgnoreCase(clanName)) {
                return clan;
            }

        }

        return null;
    }

    /**
     * Creates a new clan and store clan info to database
     *
     * @param player
     * @param clanName
     * @return NULL if clan with same name already exists
     */
    public L2Clan createClan(L2PcInstance player, String clanName) {
        if (null == player) {
            return null;
        }

        if (Config.DEBUG) {
            _log.debug(player.getObjectId() + "(" + player.getName() + ") requested a clan creation.");
        }

        if (10 > player.getLevel()) {
            player.sendPacket(new SystemMessage(SystemMessageId.YOU_DO_NOT_MEET_CRITERIA_IN_ORDER_TO_CREATE_A_CLAN));
            return null;
        }
        if (0 != player.getClanId()) {
            player.sendPacket(new SystemMessage(SystemMessageId.FAILED_TO_CREATE_CLAN));
            return null;
        }
        if (System.currentTimeMillis() < player.getClanCreateExpiryTime()) {
            player.sendPacket(new SystemMessage(SystemMessageId.YOU_MUST_WAIT_XX_DAYS_BEFORE_CREATING_A_NEW_CLAN));
            return null;
        }
        if (!Util.isAlphaNumeric(clanName) || (2 > clanName.length())) {
            player.sendPacket(new SystemMessage(SystemMessageId.CLAN_NAME_INCORRECT));
            return null;
        }
        if (16 < clanName.length()) {
            player.sendPacket(new SystemMessage(SystemMessageId.CLAN_NAME_TOO_LONG));
            return null;
        }

        if (null != getClanByName(clanName)) {
            // clan name is already taken
            SystemMessage sm = new SystemMessage(SystemMessageId.S1_ALREADY_EXISTS);
            sm.addString(clanName);
            player.sendPacket(sm);
            sm = null;
            return null;
        }

        L2Clan clan = new L2Clan(IdFactory.getInstance().getNextId(), clanName);
        L2ClanMember leader = new L2ClanMember(clan, player.getName(), player.getLevel(), player.getPlayerClass().getId(), player.getObjectId(), player.getPledgeType(), player.getPowerGrade(), player.getTitle());
        clan.setLeader(leader);
        leader.setPlayerInstance(player);
        clan.store();
        player.setClan(clan);
        player.setPledgeClass(leader.calculatePledgeClass(player));
        player.setClanPrivileges(L2Clan.CP_ALL);

        if (Config.DEBUG) {
            _log.debug("New clan created: " + clan.getClanId() + " " + clan.getName());
        }

        _clans.put(clan.getClanId(), clan);

        // should be update packet only
        player.sendPacket(new PledgeShowInfoUpdate(clan));
        player.sendPacket(new PledgeShowMemberListAll(clan, player));
        player.sendPacket(new UserInfo(player));
        player.sendPacket(new PledgeShowMemberListUpdate(player));
        player.sendPacket(new SystemMessage(SystemMessageId.CLAN_CREATED));
        return clan;
    }

    public synchronized void destroyClan(Integer clanId) {
        L2Clan clan = getClan(clanId);
        if (clan == null) {
            return;
        }

        clan.broadcastToOnlineMembers(new SystemMessage(SystemMessageId.CLAN_HAS_DISPERSED));
        int castleId = clan.getCastle();
        if (castleId == 0) {
            for (Siege siege : SiegeManager.getInstance().getSieges()) {
                siege.removeSiegeClan(clanId);
            }
        }

        L2ClanMember leaderMember = clan.getLeader();
        if (leaderMember == null) {
            clan.getWarehouse().destroyAllItems("ClanRemove", null, null);
        } else {
            clan.getWarehouse().destroyAllItems("ClanRemove", clan.getLeader().getPlayerInstance(), null);
        }

        for (L2ClanMember member : clan.getMembers()) {
            clan.removeClanMember(member.getName(), 0);
        }

        _clans.remove(clanId);
        IdFactory.getInstance().releaseId(clanId);

        ClanRepository clanRepository = getRepository(ClanRepository.class);
        clanRepository.deleteById(clanId);

        ClanPrivsRepository clanPrivsRepository = getRepository(ClanPrivsRepository.class);
        clanPrivsRepository.deleteById(clanId);

        ClanSkillRepository skillRepository = getRepository(ClanSkillRepository.class);
        skillRepository.deleteById(clanId);

        ClanSubpledgesRepository subpledgesRepository = getRepository(ClanSubpledgesRepository.class);
        subpledgesRepository.deleteById(clanId);

        ClanWarsRepository warsRepository = getRepository(ClanWarsRepository.class);
        warsRepository.deleteByClan(clanId);


        if (castleId != 0) {
            CastleRepository castleRepository = getRepository(CastleRepository.class);
            castleRepository.updateTaxById(castleId, 0);
        }

        _log.debug(getMessage("debug.clan.removed"), clanId);
    }

    public void scheduleRemoveClan(final Integer clanId) {
        ThreadPoolManager.getInstance().scheduleGeneral(() ->
        {
            if (getClan(clanId) == null) {
                return;
            }
            if (getClan(clanId).getDissolvingExpiryTime() != 0) {
                destroyClan(clanId);
            }
        }, getClan(clanId).getDissolvingExpiryTime() - System.currentTimeMillis());
    }

    public boolean isAllyExists(String allyName) {
        for (L2Clan clan : getClans()) {
            if ((clan.getAllyName() != null) && clan.getAllyName().equalsIgnoreCase(allyName)) {
                return true;
            }
        }
        return false;
    }

    public void storeclanswars(int clanId1, int clanId2) {
        L2Clan clan1 = ClanTable.getInstance().getClan(clanId1);
        L2Clan clan2 = ClanTable.getInstance().getClan(clanId2);
        clan1.setEnemyClan(clan2);
        clan2.setAttackerClan(clan1);
        clan1.broadcastClanStatus();
        clan2.broadcastClanStatus();

        ClanWarsRepository repository = getRepository(ClanWarsRepository.class);
        repository.saveOrUpdate(clanId1, clanId2, 0, 0);

        SystemMessage msg = new SystemMessage(SystemMessageId.CLAN_WAR_DECLARED_AGAINST_S1_IF_KILLED_LOSE_LOW_EXP);
        msg.addString(clan2.getName());
        clan1.broadcastToOnlineMembers(msg);
        // msg = new SystemMessage(SystemMessageId.WAR_WITH_THE_S1_CLAN_HAS_BEGUN);
        // msg.addString(clan1.getName());
        // clan2.broadcastToOnlineMembers(msg);
        // clan1 declared clan war.
        msg = new SystemMessage(SystemMessageId.CLAN_S1_DECLARED_WAR);
        msg.addString(clan1.getName());
        clan2.broadcastToOnlineMembers(msg);
    }

    public void deleteclanswars(int clanId1, int clanId2) {
        L2Clan clan1 = ClanTable.getInstance().getClan(clanId1);
        L2Clan clan2 = ClanTable.getInstance().getClan(clanId2);
        clan1.deleteEnemyClan(clan2);
        clan2.deleteAttackerClan(clan1);
        clan1.broadcastClanStatus();
        clan2.broadcastClanStatus();

        ClanWarsRepository repository = getRepository(ClanWarsRepository.class);
        repository.deleteWar(clanId1, clanId2);

        // SystemMessage msg = new SystemMessage(SystemMessageId.WAR_WITH_THE_S1_CLAN_HAS_ENDED);
        SystemMessage msg = new SystemMessage(SystemMessageId.WAR_AGAINST_S1_HAS_STOPPED);
        msg.addString(clan2.getName());
        clan1.broadcastToOnlineMembers(msg);
        msg = new SystemMessage(SystemMessageId.CLAN_S1_HAS_DECIDED_TO_STOP);
        msg.addString(clan1.getName());
        clan2.broadcastToOnlineMembers(msg);
        // msg = new SystemMessage(SystemMessageId.WAR_WITH_THE_S1_CLAN_HAS_ENDED);
        // msg.addString(clan1.getName());
        // clan2.broadcastToOnlineMembers(msg);
    }

    public void checkSurrender(L2Clan clan1, L2Clan clan2) {
        int count = 0;
        for (L2ClanMember player : clan1.getMembers()) {
            if ((player != null) && (player.getPlayerInstance().getWantsPeace())) {
                count++;
            }
        }
        if (count == (clan1.getMembers().length - 1)) {
            clan1.deleteEnemyClan(clan2);
            clan2.deleteEnemyClan(clan1);
            deleteclanswars(clan1.getClanId(), clan2.getClanId());
        }
    }

    private void restoreWars() {
        ClanWarsRepository repository = getRepository(ClanWarsRepository.class);
        repository.findAll().forEach(clanWars -> {
            getClan(clanWars.getClan1()).setEnemyClan(clanWars.getClan2());
            getClan(clanWars.getClan2()).setAttackerClan(clanWars.getClan1());
        });
    }
}
