package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.model.L2Character;
import org.l2j.gameserver.model.L2Effect;
import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.L2Summon;
import org.l2j.gameserver.model.actor.knownlist.KnownList;
import org.l2j.gameserver.model.actor.knownlist.PlayableKnownList;
import org.l2j.gameserver.model.actor.stat.PlayableStat;
import org.l2j.gameserver.model.actor.status.PlayableStatus;
import org.l2j.gameserver.model.entity.database.CharTemplate;

import java.util.Objects;

import static java.util.Objects.isNull;

public abstract class L2PlayableInstance extends L2Character {
	
	private boolean _isNoblesseBlessed = false; // for Noblesse Blessing skill, restores buffs after death
	private boolean _getCharmOfLuck = false; // Charm of Luck - During a Raid/Boss war, decreased chance for death penalty

	/**
	 * Constructor of L2PlayableInstance (use L2Character constructor).<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Call the L2Character constructor to create an empty skills slot and link copy basic Calculator set to this L2PlayableInstance</li><BR>
	 * <BR>
	 * @param objectId Identifier of the object to initialized
	 * @param template The L2CharTemplate to apply to the L2PlayableInstance
	 */
	public L2PlayableInstance(int objectId, CharTemplate template) {
		super(objectId, template);
		getStat(); // init stats
		getStatus(); // init status
	}
	
	@Override
	public KnownList getKnownList() {
		if(isNull(_knownList)) {
		    _knownList = new PlayableKnownList(this);
        }
        return _knownList;
	}
	
	@Override
	public PlayableStat getStat()
	{
		if ((super.getStat() == null) || !(super.getStat() instanceof PlayableStat))
		{
			setStat(new PlayableStat(this));
		}
		return (PlayableStat) super.getStat();
	}
	
	@Override
	public PlayableStatus getStatus()
	{
		if ((super.getStatus() == null) || !(super.getStatus() instanceof PlayableStatus))
		{
			setStatus(new PlayableStatus(this));
		}
		return (PlayableStatus) super.getStatus();
	}
	
	@Override
	public boolean doDie(L2Character killer)
	{
		if (!super.doDie(killer))
		{
			return false;
		}
		
		if (killer != null)
		{
			L2PcInstance player = null;
			if (killer instanceof L2PcInstance)
			{
				player = (L2PcInstance) killer;
			}
			else if (killer instanceof L2Summon)
			{
				player = ((L2Summon) killer).getOwner();
			}
			
			if (player != null)
			{
				player.onKillUpdatePvPKarma(this);
			}
		}
		return true;
	}
	
	public boolean checkIfPvP(L2Character target)
	{
		if (target == null)
		{
			return false; // Target is null
		}
		if (target == this)
		{
			return false; // Target is self
		}
		if (!(target instanceof L2PlayableInstance))
		{
			return false; // Target is not a L2PlayableInstance
		}
		
		L2PcInstance player = null;
		if (this instanceof L2PcInstance)
		{
			player = (L2PcInstance) this;
		}
		else if (this instanceof L2Summon)
		{
			player = ((L2Summon) this).getOwner();
		}
		
		if (player == null)
		{
			return false; // Active reader is null
		}
		if (player.getKarma() != 0)
		{
			return false; // Active reader has karma
		}
		
		L2PcInstance targetPlayer = null;
		if (target instanceof L2PcInstance)
		{
			targetPlayer = (L2PcInstance) target;
		}
		else if (target instanceof L2Summon)
		{
			targetPlayer = ((L2Summon) target).getOwner();
		}
		
		if (targetPlayer == null)
		{
			return false; // Target reader is null
		}
		if (targetPlayer == this)
		{
			return false; // Target reader is self
		}
		if (targetPlayer.getKarma() != 0)
		{
			return false; // Target reader has karma
		}
		if (targetPlayer.getPvpFlag() == 0)
		{
			return false;
		}
		
		return true;
		/*
		 * Even at war, there should be PvP flag if( reader.getClan() == null || targetPlayer.getClan() == null || ( !targetPlayer.getClan().isAtWarWith(reader.getClanId()) && targetPlayer.getWantsPeace() == 0 && reader.getWantsPeace() == 0 ) ) { return true; } return false;
		 */
	}
	
	/**
	 * Return True.<BR>
	 * <BR>
	 */
	@Override
	public boolean isAttackable()
	{
		return true;
	}
	
	// Support for Noblesse Blessing skill, where buffs are retained
	// after resurrect
	public final boolean isNoblesseBlessed()
	{
		return _isNoblesseBlessed;
	}
	
	public final void setIsNoblesseBlessed(boolean value)
	{
		_isNoblesseBlessed = value;
	}
	
	public final void startNoblesseBlessing()
	{
		setIsNoblesseBlessed(true);
		updateAbnormalEffect();
	}
	
	public final void stopNoblesseBlessing(L2Effect effect)
	{
		if (effect == null)
		{
			stopEffects(L2Effect.EffectType.NOBLESSE_BLESSING);
		}
		else
		{
			removeEffect(effect);
		}
		
		setIsNoblesseBlessed(false);
		updateAbnormalEffect();
	}
	
	public abstract boolean destroyItemByItemId(String process, int itemId, long count, L2Object reference, boolean sendMessage);
	
	public abstract boolean destroyItem(String process, int objectId, long count, L2Object reference, boolean sendMessage);
	
	// Charm of Luck - During a Raid/Boss war, decreased chance for death penalty
	public final boolean getCharmOfLuck()
	{
		return _getCharmOfLuck;
	}
	
	public final void setCharmOfLuck(boolean value)
	{
		_getCharmOfLuck = value;
	}
	
	public final void startCharmOfLuck()
	{
		setCharmOfLuck(true);
		updateAbnormalEffect();
	}
	
	public final void stopCharmOfLuck(L2Effect effect)
	{
		if (effect == null)
		{
			stopEffects(L2Effect.EffectType.CHARM_OF_LUCK);
		}
		else
		{
			removeEffect(effect);
		}
		
		setCharmOfLuck(false);
		updateAbnormalEffect();
	}
}
