package org.l2j.gameserver.model;

import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.entity.database.repository.CharacterShortcutsRepository;
import org.l2j.gameserver.serverpackets.ExAutoSoulShot;
import org.l2j.gameserver.serverpackets.ShortCutInit;
import org.l2j.gameserver.templates.xml.jaxb.CommissionType;

import java.util.Map;
import java.util.TreeMap;

public class ShortCuts
{
	private final L2PcInstance _owner;
	private final Map<Integer, L2ShortCut> _shortCuts = new TreeMap<>();
	
	public ShortCuts(L2PcInstance owner)
	{
		_owner = owner;
	}
	
	public L2ShortCut[] getAllShortCuts()
	{
		return _shortCuts.values().toArray(new L2ShortCut[_shortCuts.values().size()]);
	}
	
	public L2ShortCut getShortCut(int slot, int page)
	{
		L2ShortCut sc = _shortCuts.get(slot + (page * 12));
		
		// verify shortcut
		if ((sc != null) && (sc.getType() == L2ShortCut.TYPE_ITEM))
		{
			if (_owner.getInventory().getItemByObjectId(sc.getId()) == null)
			{
				deleteShortCut(sc.getSlot(), sc.getPage());
				sc = null;
			}
		}
		
		return sc;
	}
	
	public synchronized void registerShortCut(L2ShortCut shortcut)
	{
		L2ShortCut oldShortCut = _shortCuts.put(shortcut.getSlot() + (12 * shortcut.getPage()), shortcut);
		registerShortCutInDb(shortcut, oldShortCut);
	}
	
	private void registerShortCutInDb(L2ShortCut shortcut, L2ShortCut oldShortCut) {
		if (oldShortCut != null) {
			deleteShortCutFromDb(oldShortCut);
		}

		CharacterShortcutsRepository repository = DatabaseAccess.getRepository(CharacterShortcutsRepository.class);
        repository.saveOrUpdate(_owner.getObjectId(), shortcut.getId(), shortcut.getSlot(), shortcut.getPage(), shortcut.getType(),
                shortcut.getLevel(), _owner.getClassIndex());
	}
	
	/**
	 * @param slot
	 * @param page
	 */
	public synchronized void deleteShortCut(int slot, int page)
	{
		L2ShortCut old = _shortCuts.remove(slot + (page * 12));
		
		if ((old == null) || (_owner == null))
		{
			return;
		}
		deleteShortCutFromDb(old);
		if (old.getType() == L2ShortCut.TYPE_ITEM)
		{
			L2ItemInstance item = _owner.getInventory().getItemByObjectId(old.getId());
			
			if ((item != null) && (item.getCommissionType() == CommissionType.SOULSHOT  || item.getCommissionType() == CommissionType.SPIRITSHOT))
			{
				_owner.removeAutoSoulShot(item.getId());
				_owner.sendPacket(new ExAutoSoulShot(item.getId(), 0));
			}
		}
		
		_owner.sendPacket(new ShortCutInit(_owner));
		
		for (int shotId : _owner.getAutoSoulShot().values())
		{
			_owner.sendPacket(new ExAutoSoulShot(shotId, 1));
		}
	}
	
	public synchronized void deleteShortCutByObjectId(int objectId)
	{
		L2ShortCut toRemove = null;
		
		for (L2ShortCut shortcut : _shortCuts.values())
		{
			if ((shortcut.getType() == L2ShortCut.TYPE_ITEM) && (shortcut.getId() == objectId))
			{
				toRemove = shortcut;
				break;
			}
		}
		
		if (toRemove != null)
		{
			deleteShortCut(toRemove.getSlot(), toRemove.getPage());
		}
	}

	private void deleteShortCutFromDb(L2ShortCut shortcut) {
	    CharacterShortcutsRepository repository = DatabaseAccess.getRepository(CharacterShortcutsRepository.class);
	    repository.delete(_owner.getObjectId(), shortcut.getSlot(), shortcut.getPage(), _owner.getClassIndex());
	}
	
	public void restore()
	{
		_shortCuts.clear();
        CharacterShortcutsRepository repository = DatabaseAccess.getRepository(CharacterShortcutsRepository.class);
        repository.finAllByClassIndex(_owner.getObjectId(), _owner.getClassIndex()).forEach(shortcut -> {
            int slot = shortcut.getSlot();
            int page = shortcut.getPage();
            int type = shortcut.getType();
            int id = shortcut.getShortcutId();
            int level = shortcut.getLevel();

            L2ShortCut sc = new L2ShortCut(slot, page, type, id, level, 1);
            _shortCuts.put(slot + (page * 12), sc);
        });

		// verify shortcuts
		for (L2ShortCut sc : getAllShortCuts())
		{
			if (sc.getType() == L2ShortCut.TYPE_ITEM)
			{
				if (_owner.getInventory().getItemByObjectId(sc.getId()) == null)
				{
					deleteShortCut(sc.getSlot(), sc.getPage());
				}
			}
		}
	}
}
