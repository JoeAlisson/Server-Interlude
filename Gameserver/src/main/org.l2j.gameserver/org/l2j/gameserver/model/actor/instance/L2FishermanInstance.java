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
package org.l2j.gameserver.model.actor.instance;

import org.l2j.commons.Config;
import org.l2j.gameserver.TradeController;
import org.l2j.gameserver.datatables.SkillTable;
import org.l2j.gameserver.datatables.SkillTreeTable;
import org.l2j.gameserver.model.L2Skill;
import org.l2j.gameserver.model.entity.database.MerchantShop;
import org.l2j.gameserver.model.entity.database.NpcTemplate;
import org.l2j.gameserver.model.entity.database.SkillInfo;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.StringTokenizer;


public class L2FishermanInstance extends L2FolkInstance {
    private static final Logger logger = LoggerFactory.getLogger(L2FishermanInstance.class);

    /**
     * @param objectId
     * @param template
     */
    public L2FishermanInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public String getHtmlPath(int npcId, int val) {
        String pom = "";

        if (val == 0) {
            pom = "" + npcId;
        } else {
            pom = npcId + "-" + val;
        }

        return "data/html/fisherman/" + pom + ".htm";
    }

    private void showBuyWindow(L2PcInstance player, int val) {
        double taxRate = 0;
        if (getIsInTown()) {
            taxRate = getCastle().getTaxRate();
        }
        player.tempInvetoryDisable();
        if (Config.DEBUG) {
            logger.debug("Showing buylist");
        }
        MerchantShop list = TradeController.getInstance().getBuyList(val);

        if ((list != null) && list.getNpcId().equals(String.valueOf(getNpcId()))) {
            BuyList bl = new BuyList(list, player.getAdena(), taxRate);
            player.sendPacket(bl);
        } else {
            logger.warn("possible client hacker: " + player.getName() + " attempting to buy from GM shop! < Ban him!");
            logger.warn("buylist id:" + val);
        }

        player.sendPacket(new ActionFailed());
    }

    private void showSellWindow(L2PcInstance player) {
        if (Config.DEBUG) {
            logger.debug("Showing selllist");
        }

        player.sendPacket(new SellList(player));

        if (Config.DEBUG) {
            logger.debug("Showing sell window");
        }

        player.sendPacket(new ActionFailed());
    }

    @Override
    public void onBypassFeedback(L2PcInstance player, String command) {
        if (command.startsWith("FishSkillList")) {
            player.setSkillLearningClassId(player.getPlayerClass());
            showSkillList(player);
        }

        StringTokenizer st = new StringTokenizer(command, " ");
        String command2 = st.nextToken();

        if (command2.equalsIgnoreCase("Buy")) {
            if (st.countTokens() < 1) {
                return;
            }
            int val = Integer.parseInt(st.nextToken());
            showBuyWindow(player, val);
        } else if (command2.equalsIgnoreCase("Sell")) {
            showSellWindow(player);
        } else {
            super.onBypassFeedback(player, command);
        }
    }

    public void showSkillList(L2PcInstance player) {
        List<SkillInfo> skills = SkillTreeTable.getInstance().getAvailableSkills(player);
        AquireSkillListPacket asl = new AquireSkillListPacket(AquireSkillListPacket.skillType.Fishing);

        int counts = 0;

        for (SkillInfo s : skills) {
            L2Skill sk = SkillTable.getInstance().getInfo(s.getId(), s.getLevel());

            if (sk == null) {
                continue;
            }

            counts++;
            asl.addSkill(s.getId(), s.getLevel(), s.getLevel(), s.getSpCost(), 1);
        }

        if (counts == 0) {
            NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
            int minlevel = SkillTreeTable.getInstance().getMinLevelForNewSkill(player);

            if (minlevel > 0) {
                // No more skills to learn, come back when you level.
                SystemMessage sm = new SystemMessage(SystemMessageId.DO_NOT_HAVE_FURTHER_SKILLS_TO_LEARN);
                sm.addNumber(minlevel);
                player.sendPacket(sm);
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("<html><head><body>");
                sb.append("You've learned allTemplates skills.<br>");
                sb.append("</body></html>");
                html.setHtml(sb.toString());
                player.sendPacket(html);
            }
        } else {
            player.sendPacket(asl);
        }

        player.sendPacket(new ActionFailed());
    }
}
