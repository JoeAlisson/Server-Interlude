package org.l2j.gameserver.clientpackets;

import org.l2j.commons.Config;
import org.l2j.gameserver.datatables.ItemTable;
import org.l2j.gameserver.instancemanager.CastleManorManager;
import org.l2j.gameserver.model.L2ItemInstance;
import org.l2j.gameserver.model.L2Manor;
import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.actor.instance.L2ManorManagerInstance;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.entity.database.CropProcure;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.serverpackets.ActionFailed;
import org.l2j.gameserver.serverpackets.InventoryUpdate;
import org.l2j.gameserver.serverpackets.StatusUpdate;
import org.l2j.gameserver.serverpackets.SystemMessage;
import org.l2j.gameserver.templates.xml.jaxb.Item;
import org.l2j.gameserver.templates.xml.jaxb.ItemTemplate;
import org.l2j.gameserver.util.Util;

import java.util.ArrayList;
import java.util.List;

public class RequestBuyProcure extends L2GameClientPacket {
    private static final String _C__C3_REQUESTBUYPROCURE = "[C] C3 RequestBuyProcure";
    private int _listId;
    private int _count;
    private int[] _items;
    private List<CropProcure> _procureList = new ArrayList<>();

    @Override
    protected void readImpl() {
        _listId = readInt();
        _count = readInt();
        if (_count > 500) // protect server
        {
            _count = 0;
            return;
        }

        _items = new int[_count * 2];
        for (int i = 0; i < _count; i++) {
            readInt();
            int itemId = readInt();
            _items[(i * 2) + 0] = itemId;
            long cnt = readInt();
            if ((cnt > Integer.MAX_VALUE) || (cnt < 1)) {
                _count = 0;
                _items = null;
                return;
            }
            _items[(i * 2) + 1] = (int) cnt;
        }
    }

    @Override
    protected void runImpl() {
        L2PcInstance player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        // Alt game - Karma punishment
        if (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && (player.getKarma() > 0)) {
            return;
        }

        L2Object target = player.getTarget();

        if (_count < 1) {
            sendPacket(new ActionFailed());
            return;
        }

        // Check for buylist validity and calculates summary values
        int slots = 0;
        int weight = 0;
        L2ManorManagerInstance manor = ((target != null) && (target instanceof L2ManorManagerInstance)) ? (L2ManorManagerInstance) target : null;

        for (int i = 0; i < _count; i++) {
            int itemId = _items[(i * 2) + 0];
            int count = _items[(i * 2) + 1];
            if (count > Integer.MAX_VALUE) {
                Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to purchase over " + Integer.MAX_VALUE + " items at the same time.", Config.DEFAULT_PUNISH);
                SystemMessage sm = new SystemMessage(SystemMessageId.YOU_HAVE_EXCEEDED_QUANTITY_THAT_CAN_BE_INPUTTED);
                sendPacket(sm);
                return;
            }

            ItemTemplate template = ItemTable.getInstance().getTemplate(L2Manor.getInstance().getRewardItem(itemId, manor.getCastle().getCrop(itemId, CastleManorManager.PERIOD_CURRENT).getReward()));
            weight += count * template.getWeight();

            if (!(template instanceof Item) || !((Item)template).isStackable()) {
                slots += count;
            } else if (player.getInventory().getItemByItemId(itemId) == null) {
                slots++;
            }
        }

        if (!player.getInventory().validateWeight(weight)) {
            sendPacket(new SystemMessage(SystemMessageId.WEIGHT_LIMIT_EXCEEDED));
            return;
        }

        if (!player.getInventory().validateCapacity(slots)) {
            sendPacket(new SystemMessage(SystemMessageId.SLOTS_FULL));
            return;
        }

        // Proceed the purchase
        InventoryUpdate playerIU = new InventoryUpdate();
        _procureList = manor.getCastle().getCropProcure(CastleManorManager.PERIOD_CURRENT);

        for (int i = 0; i < _count; i++) {
            int itemId = _items[(i * 2) + 0];
            int count = _items[(i * 2) + 1];
            if (count < 0) {
                count = 0;
            }

            int rewradItemId = L2Manor.getInstance().getRewardItem(itemId, manor.getCastle().getCrop(itemId, CastleManorManager.PERIOD_CURRENT).getReward());

            int rewradItemCount = 1; // L2Manor.getInstance().getRewardAmount(itemId, manor.getCastle().getCropReward(itemId));

            rewradItemCount = count / rewradItemCount;

            // Add item to Inventory and adjust update packet
            L2ItemInstance item = player.getInventory().addItem("Manor", rewradItemId, rewradItemCount, player, manor);
            L2ItemInstance iteme = player.getInventory().destroyItemByItemId("Manor", itemId, count, player, manor);

            if ((item == null) || (iteme == null)) {
                continue;
            }

            playerIU.addRemovedItem(iteme);
            if (item.getCount() > rewradItemCount) {
                playerIU.addModifiedItem(item);
            } else {
                playerIU.addNewItem(item);
            }

            // Send Char Buy Messages
            SystemMessage sm = new SystemMessage(SystemMessageId.EARNED_S2_S1_S);
            sm.addItemName(rewradItemId);
            sm.addNumber(rewradItemCount);
            player.sendPacket(sm);
            sm = null;

            // manor.getCastle().setCropAmount(itemId, manor.getCastle().getCrop(itemId, CastleManorManager.PERIOD_CURRENT).getCount() - count);
        }

        // Send update packets
        player.sendPacket(playerIU);

        StatusUpdate su = new StatusUpdate(player.getObjectId());
        su.addAttribute(StatusUpdate.CUR_LOAD, player.getCurrentLoad());
        player.sendPacket(su);
    }

    @Override
    public String getType() {
        return _C__C3_REQUESTBUYPROCURE;
    }
}
