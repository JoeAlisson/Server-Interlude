package org.l2j.gameserver.model;

public class ItemInfo
{
	/** Identifier of the L2ItemInstance */
	private int _objectId;
	
	/** The ItemTemplate template of the L2ItemInstance */
	private L2ItemInstance _item;
	
	/** The level of enchant on the L2ItemInstance */
	private int _enchant;
	
	/** The augmentation of the item */
	private int _augmentation;
	
	/** The quantity of L2ItemInstance */
	private long _count;
	
	/** The price of the L2ItemInstance */
	private int _price;
	
	/** The custom L2ItemInstance types (used loto, race tickets) */
	private int _type1;
	private int _type2;
	
	/** If True the L2ItemInstance is equipped */
	private int _equipped;
	
	/** The action to do client side (1=ADD, 2=MODIFY, 3=REMOVE) */
	private int _change;
	
	/** The mana of this item */
	private int _mana;
	
	/**
	 * Get allTemplates information from L2ItemInstance to generate ItemInfo.
	 * @param item
	 */
	public ItemInfo(L2ItemInstance item)
	{
		if (item == null)
		{
			return;
		}
		
		// Get the Identifier of the L2ItemInstance
		_objectId = item.getObjectId();
		
		// Get the ItemTemplate of the L2ItemInstance
		_item = item;
		
		// Get the enchant level of the L2ItemInstance
		_enchant = item.getEnchantLevel();
		
		// Get the augmentation boni
		if (item.isAugmented())
		{
			_augmentation = item.getAugmentation().getAugmentationId();
		}
		else
		{
			_augmentation = 0;
		}
		
		// Get the quantity of the L2ItemInstance
		_count = item.getCount();
		
		// Get custom item types (used loto, race tickets)
		_type1 = item.getCustomType1();
		_type2 = item.getSubType();
		
		// Verify if the L2ItemInstance is equipped
		_equipped = item.isEquipped() ? 1 : 0;
		
		// Get the action to do clientside
		switch (item.getLastChange())
		{
			case (L2ItemInstance.ADDED):
			{
				_change = 1;
				break;
			}
			case (L2ItemInstance.MODIFIED):
			{
				_change = 2;
				break;
			}
			case (L2ItemInstance.REMOVED):
			{
				_change = 3;
				break;
			}
		}
		
		// Get shadow item mana
		_mana = item.getTime();
	}
	
	public ItemInfo(L2ItemInstance item, int change)
	{
		if (item == null)
		{
			return;
		}
		
		// Get the Identifier of the L2ItemInstance
		_objectId = item.getObjectId();
		
		// Get the ItemTemplate of the L2ItemInstance
		_item = item;
		
		// Get the enchant level of the L2ItemInstance
		_enchant = item.getEnchantLevel();
		
		// Get the augmentation boni
		if (item.isAugmented())
		{
			_augmentation = item.getAugmentation().getAugmentationId();
		}
		else
		{
			_augmentation = 0;
		}
		
		// Get the quantity of the L2ItemInstance
		_count = item.getCount();
		
		// Get custom item types (used loto, race tickets)
		_type1 = item.getCustomType1();
		_type2 = item.getSubType();
		
		// Verify if the L2ItemInstance is equipped
		_equipped = item.isEquipped() ? 1 : 0;
		
		// Get the action to do clientside
		_change = change;
		
		// Get shadow item mana
		_mana = item.getTime();
	}
	
	public int getObjectId()
	{
		return _objectId;
	}
	
	public L2ItemInstance getItem()
	{
		return _item;
	}
	
	public int getEnchant()
	{
		return _enchant;
	}
	
	public int getAugemtationBoni()
	{
		return _augmentation;
	}
	
	public long getCount()
	{
		return _count;
	}
	
	public int getPrice()
	{
		return _price;
	}
	
	public int getCustomType1()
	{
		return _type1;
	}
	
	public int getCustomType2()
	{
		return _type2;
	}
	
	public int getEquipped()
	{
		return _equipped;
	}
	
	public int getChange()
	{
		return _change;
	}
	
	public int getMana()
	{
		return _mana;
	}
}
