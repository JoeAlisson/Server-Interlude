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
package org.l2j.gameserver.communitybbs.Manager;

import org.l2j.gameserver.communitybbs.BB.Forum;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.entity.database.repository.ForumRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.l2j.commons.database.DatabaseAccess.getRepository;
import static java.util.Objects.isNull;

public class ForumsBBSManager extends BaseBBSManager {
    private static Logger _log = LoggerFactory.getLogger(ForumsBBSManager.class.getName());
    private final Map<Integer, Forum> _root;
    private final List<Forum> _table;
    private static ForumsBBSManager _instance;
    private int _lastid = 1;

    /**
     * @return
     */
    public static ForumsBBSManager getInstance() {
        if (isNull(_instance)) {
            _instance = new ForumsBBSManager();
            _instance.load();
        }
        return _instance;
    }

    private ForumsBBSManager() {
        _root = new HashMap<>();
        _table = new LinkedList<>();
    }

    public void addForum(Forum ff) {
        _table.add(ff);

        if (ff.getID() > _lastid) {
            _lastid = ff.getID();
        }
    }

    private void load() {
        getRepository(ForumRepository.class).findAllIdsByType(0).forEach(forumId -> {
            Forum f = new Forum(forumId, null);
            _root.put(forumId, f);
        });
    }

    /*
     * (non-Javadoc)
     * @see org.l2j.gameserver.communitybbs.Manager.BaseBBSManager#parsecmd(java.lang.String, org.l2j.gameserver.model.actor.instance.L2PcInstance)
     */
    @Override
    public void parsecmd(String command, L2PcInstance activeChar) {
        // TODO Auto-generated method stub
    }

    /**
     * @param Name
     * @return
     */
    public Forum getForumByName(String Name) {
        for (Forum f : _table) {
            if (f.getName().equals(Name)) {
                return f;
            }
        }

        return null;
    }

    /**
     * @param name
     * @param parent
     * @param type
     * @param perm
     * @param oid
     * @return
     */
    public Forum createNewForum(String name, Forum parent, int type, int perm, int oid) {
        Forum forum;
        forum = new Forum(name, parent, type, perm, oid);
        forum.insertInDb();
        return forum;
    }

    /**
     * @return
     */
    public int getANewID() {
        _lastid++;
        return _lastid;
    }

    /**
     * @param idf
     * @return
     */
    public Forum getForumByID(int idf) {
        for (Forum f : _table) {
            if (f.getID() == idf) {
                return f;
            }
        }
        return null;
    }

    @Override
    public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2PcInstance activeChar) {
        // TODO Auto-generated method stub
    }
}