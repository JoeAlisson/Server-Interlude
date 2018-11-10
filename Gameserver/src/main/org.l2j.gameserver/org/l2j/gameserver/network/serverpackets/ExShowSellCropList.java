package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2ItemInstance;
import org.l2j.gameserver.model.L2Manor;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.entity.database.CropProcure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * format(packet 0xFE) ch dd [ddddcdcdddc] c - id h - sub id d - manor id d - size [ d - Object id d - crop id d - seed level c d - reward 1 id c d - reward 2 id d - manor d - buy residual d - buy price d - reward ]
 * @author l3x
 */
public class ExShowSellCropList extends L2GameServerPacket
{
	private int _manorId;
	private final Map<Integer, L2ItemInstance> _cropsItems;
	private final Map<Integer, CropProcure> _castleCrops;
	
	public ExShowSellCropList(L2PcInstance player, int manorId, List<CropProcure> crops)
	{
		_manorId = manorId;
		_castleCrops = new HashMap<>();
		_cropsItems = new HashMap<>();
		
		List<Integer> allCrops = L2Manor.getInstance().getAllCrops();
		for (int cropId : allCrops)
		{
			L2ItemInstance item = player.getInventory().getItemByItemId(cropId);
			if (item != null)
			{
				_cropsItems.put(cropId, item);
			}
		}
		
		for (CropProcure crop : crops)
		{
			if (_cropsItems.containsKey(crop.getCropId()) && (crop.getAmount() > 0))
			{
				_castleCrops.put(crop.getCropId(), crop);
			}
		}
		
	}
	
	@Override
	public void runImpl()
	{
		// no long running
	}
	
	@Override
	public void writeImpl()
	{
		writeByte(0xFE);
		writeShort(0x21);
		
		writeInt(_manorId); // manor id
		writeInt(_cropsItems.size()); // size
		
		for (L2ItemInstance item : _cropsItems.values())
		{
			writeInt(item.getObjectId()); // Object id
			writeInt(item.getId()); // crop id
			writeInt(L2Manor.getInstance().getSeedLevelByCrop(item.getId())); // seed level
			writeByte(1);
			writeInt(L2Manor.getInstance().getRewardItem(item.getId(), 1)); // reward 1 id
			writeByte(1);
			writeInt(L2Manor.getInstance().getRewardItem(item.getId(), 2)); // reward 2 id
			
			if (_castleCrops.containsKey(item.getId()))
			{
				CropProcure crop = _castleCrops.get(item.getId());
				writeInt(_manorId); // manor
				writeInt(crop.getAmount()); // buy residual
				writeInt(crop.getPrice()); // buy price
				writeByte(crop.getReward()); // reward
			}
			else
			{
				writeInt(0xFFFFFFFF); // manor
				writeInt(0); // buy residual
				writeInt(0); // buy price
				writeByte(0); // reward
			}
			writeInt((int) item.getCount()); // my crops
		}
	}
}
