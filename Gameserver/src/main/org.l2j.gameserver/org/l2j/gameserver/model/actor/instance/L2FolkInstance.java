package org.l2j.gameserver.model.actor.instance;

import org.l2j.commons.Config;
import org.l2j.gameserver.datatables.PlayerTemplateTable;
import org.l2j.gameserver.datatables.SkillTable;
import org.l2j.gameserver.datatables.SkillTreeTable;
import org.l2j.gameserver.model.L2Skill;
import org.l2j.gameserver.model.base.PlayerClass;
import org.l2j.gameserver.model.entity.database.EnchantSkillInfo;
import org.l2j.gameserver.model.entity.database.NpcTemplate;
import org.l2j.gameserver.model.entity.database.SkillInfo;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;


public class L2FolkInstance extends L2NpcInstance {
    private final Set<PlayerClass> _classesToTeach;
    private static final Logger logger = LoggerFactory.getLogger(L2FolkInstance.class);

    public L2FolkInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
        _classesToTeach = template.getTeachInfo();
    }

    @Override
    public void onAction(L2PcInstance player) {
        player.setLastFolkNPC(this);
        super.onAction(player);
    }

    /**
     * this displays SkillList to the reader.
     *
     * @param player
     * @param playerClass
     */
    public void showSkillList(L2PcInstance player, PlayerClass playerClass) {
        if (Config.DEBUG) {
            logger.debug("SkillList activated on: " + getObjectId());
        }

        int npcId = getNpcId();

        if (_classesToTeach == null) {
            NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
            StringBuilder sb = new StringBuilder();
            sb.append("<html><body>");
            sb.append("I cannot teach you. My class list is empty.<br> Ask admin to fix it. Need add my npcid and classes to skill_learn.sql.<br>NpcId:" + npcId + ", Your playerClass:" + player.getPlayerClass().getId() + "<br>");
            sb.append("</body></html>");
            html.setHtml(sb.toString());
            player.sendPacket(html);

            return;
        }

        if (!canTeach(playerClass)) {
            NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
            StringBuilder sb = new StringBuilder();
            sb.append("<html><body>");
            sb.append("I cannot teach you any skills.<br> You must find your current class teachers.");
            sb.append("</body></html>");
            html.setHtml(sb.toString());
            player.sendPacket(html);

            return;
        }

        List<SkillInfo> skills = SkillTreeTable.getInstance().getAvailableSkills(player, PlayerTemplateTable.getInstance().getClassTemplate(playerClass.getId()));
        AquireSkillListPacket asl = new AquireSkillListPacket(AquireSkillListPacket.skillType.Usual);
        int counts = 0;

        for (SkillInfo s : skills) {
            L2Skill sk = SkillTable.getInstance().getInfo(s.getId(), s.getLevel());

            if ((sk == null) || !sk.getCanLearn(player.getPlayerClass()) || !sk.canTeachBy(npcId)) {
                continue;
            }

            int cost = SkillTreeTable.getInstance().getSkillCost(player, sk);
            counts++;

            asl.addSkill(s.getId(), s.getLevel(), s.getLevel(), cost, 0);
        }

        if (counts == 0) {
            int minlevel = SkillTreeTable.getInstance().getMinLevelForNewSkill(player, playerClass);

            if (minlevel > 0) {
                SystemMessage sm = new SystemMessage(SystemMessageId.DO_NOT_HAVE_FURTHER_SKILLS_TO_LEARN);
                sm.addNumber(minlevel);
                player.sendPacket(sm);
            } else {
                SystemMessage sm = new SystemMessage(SystemMessageId.NO_MORE_SKILLS_TO_LEARN);
                player.sendPacket(sm);
            }
        } else {
            player.sendPacket(asl);
        }

        player.sendPacket(new ActionFailed());
    }

    /**
     * this displays EnchantSkillList to the reader.
     *
     * @param player
     * @param playerClass
     */
    public void showEnchantSkillList(L2PcInstance player, PlayerClass playerClass) {
        if (Config.DEBUG) {
            logger.debug("EnchantSkillList activated on: " + getObjectId());
        }
        int npcId = getNpcId();

        if (_classesToTeach == null) {
            NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
            StringBuilder sb = new StringBuilder();
            sb.append("<html><body>");
            sb.append("I cannot teach you. My class list is empty.<br> Ask admin to fix it. Need add my npcid and classes to skill_learn.sql.<br>NpcId:" + npcId + ", Your playerClass:" + player.getPlayerClass().getId() + "<br>");
            sb.append("</body></html>");
            html.setHtml(sb.toString());
            player.sendPacket(html);

            return;
        }

        if (!canTeach(playerClass)) {
            NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
            StringBuilder sb = new StringBuilder();
            sb.append("<html><body>");
            sb.append("I cannot teach you any skills.<br> You must find your current class teachers.");
            sb.append("</body></html>");
            html.setHtml(sb.toString());
            player.sendPacket(html);

            return;
        }
        if (player.getPlayerClass().getId() < 88) {
            NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
            StringBuilder sb = new StringBuilder();
            sb.append("<html><body>");
            sb.append("You must have 3rd class change quest completed.");
            sb.append("</body></html>");
            html.setHtml(sb.toString());
            player.sendPacket(html);

            return;
        }

        List<EnchantSkillInfo> skills = SkillTreeTable.getInstance().getAvailableEnchantSkills(player);
        ExEnchantSkillList esl = new ExEnchantSkillList();
        int counts = 0;

        for (EnchantSkillInfo s : skills) {
            L2Skill sk = SkillTable.getInstance().getInfo(s.getId(), s.getLevel());
            if (sk == null) {
                continue;
            }
            counts++;
            esl.addSkill(s.getId(), s.getLevel(), s.getSp(), s.getExp());
        }
        if (counts == 0) {
            player.sendPacket(new SystemMessage(SystemMessageId.THERE_IS_NO_SKILL_THAT_ENABLES_ENCHANT));
            NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
            int level = player.getLevel();

            if (level < 74) {
                SystemMessage sm = new SystemMessage(SystemMessageId.DO_NOT_HAVE_FURTHER_SKILLS_TO_LEARN);
                sm.addNumber(level);
                player.sendPacket(sm);
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("<html><body>");
                sb.append("You've learned allTemplates skills for your class.<br>");
                sb.append("</body></html>");
                html.setHtml(sb.toString());
                player.sendPacket(html);
            }
        } else {
            player.sendPacket(esl);
        }

        player.sendPacket(new ActionFailed());
    }

    @Override
    public void onBypassFeedback(L2PcInstance player, String command) {
        if (command.startsWith("SkillList")) {
            if (Config.ALT_GAME_SKILL_LEARN) {
                String id = command.substring(9).trim();

                if (id.length() != 0) {
                    player.setSkillLearningClassId(PlayerClass.values()[Integer.parseInt(id)]);
                    showSkillList(player, PlayerClass.values()[Integer.parseInt(id)]);
                } else {
                    boolean own_class = false;

                    if (_classesToTeach != null) {
                        for (PlayerClass cid : _classesToTeach) {
                            if (cid.equalsOrChildOf(player.getPlayerClass())) {
                                own_class = true;
                                break;
                            }
                        }
                    }

                    String text = "<html><body><center>Skill learning:</center><br>";

                    if (!own_class) {
                        String mages = player.getPlayerClass().isMage() ? "fighters" : "mages";
                        text += "Skills of your class are the easiest to learn.<br>" + "Skills of another class are harder.<br>" + "Skills for another race are even more hard to learn.<br>" + "You can also learn skills of " + mages + ", and they are" + " the hardest to learn!<br>" + "<br>";
                    }

                    // make a list of classes
                    if (_classesToTeach != null) {
                        int count = 0;
                        PlayerClass classCheck = player.getPlayerClass();

                        while ((count == 0) && (classCheck != null)) {
                            for (PlayerClass cid : _classesToTeach) {
                                if (cid.level() != classCheck.level()) {
                                    continue;
                                }

                                if (SkillTreeTable.getInstance().getAvailableSkills(player, PlayerTemplateTable.getInstance().getClassTemplate(cid.getId())).isEmpty()) {
                                    continue;
                                }

                                text += "<a action=\"bypass -h npc_%objectId%_SkillList " + cid.getId() + "\">Learn " + cid + "'s class Skills</a><br>\n";
                                count++;
                            }
                            classCheck = classCheck.getParent();
                        }
                        classCheck = null;
                    } else {
                        text += "No Skills.<br>";
                    }

                    text += "</body></html>";

                    insertObjectIdAndShowChatWindow(player, text);
                    player.sendPacket(new ActionFailed());
                }
            } else {
                player.setSkillLearningClassId(player.getPlayerClass());
                showSkillList(player, player.getPlayerClass());
            }
        } else if (command.startsWith("EnchantSkillList")) {
            showEnchantSkillList(player, player.getPlayerClass());
        } else {
            // this class dont know any other commands, let forward
            // the command to the parent class

            super.onBypassFeedback(player, command);
        }
    }

    public boolean canTeach(PlayerClass classId) {
        return ((NpcTemplate)template).canTeach(classId);
    }
}
