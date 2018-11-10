package org.l2j.gameserver.model;

import org.l2j.commons.Config;
import org.l2j.gameserver.ai.AI;
import org.l2j.gameserver.ai.Intention;
import org.l2j.gameserver.ai.L2SummonAI;
import org.l2j.gameserver.datatables.SkillTable;
import org.l2j.gameserver.model.L2Skill.SkillTargetType;
import org.l2j.gameserver.model.actor.instance.L2DoorInstance;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.actor.instance.L2PlayableInstance;
import org.l2j.gameserver.model.actor.knownlist.SummonKnownList;
import org.l2j.gameserver.model.actor.stat.SummonStat;
import org.l2j.gameserver.model.actor.status.SummonStatus;
import org.l2j.gameserver.model.base.Experience;
import org.l2j.gameserver.model.entity.database.NpcTemplate;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.taskmanager.DecayTaskManager;

import java.util.Map;

public abstract class L2Summon extends L2PlayableInstance<NpcTemplate> {

	protected int _pkKills;
	private byte _pvpFlag;
	private L2PcInstance _owner;
	private int _karma = 0;
	private int _attackRange = 36; // Melee range
	private boolean _follow = true;
	private boolean _previousFollowStatus = true;
	private int _maxLoad;
	
	private int _chargedSoulShot;
	private int _chargedSpiritShot;
	
	// TODO: currently, allTemplates servitors use 1 shot. However, this value
	// should vary depending on the servitor template (id and level)!
	private final int _soulShotsPerHit = 1;
	private final int _spiritShotsPerHit = 1;
	protected boolean _showSummonAnimation;

	public Map<Integer, L2Skill> getSkills() {
		return template.getSkills();
	}

	public int getNpcTemplateId() {
		return template.getTemplateId();
	}


	public boolean isServerSideName() {
		return template.isServerSideName();
	}

	public class AIAccessor extends L2Character.AIAccessor {
		protected AIAccessor()
		{
		}

		@Override
		public L2Summon getActor() {
			return L2Summon.this;
		}
		
		public boolean isAutoFollow()
		{
			return getFollowStatus();
		}
		
		public void doPickupItem(L2Object object)
		{
			L2Summon.this.doPickupItem(object);
		}
	}
	
	public L2Summon(int objectId, NpcTemplate template, L2PcInstance owner)
	{
		super(objectId, template);
		getKnownList(); // init knownlist
		getStat(); // init stats
		getStatus(); // init status
		
		_showSummonAnimation = true;
		_owner = owner;
		ai = new L2SummonAI(new AIAccessor());
		
		setPositionInvisible(owner.getX() + 50, owner.getY() + 100, owner.getZ() + 100);
	}
	
	@Override
	public final SummonKnownList getKnownList()
	{
		if ((super.getKnownList() == null) || !(super.getKnownList() instanceof SummonKnownList))
		{
			setKnownList(new SummonKnownList(this));
		}
		return (SummonKnownList) super.getKnownList();
	}
	
	@Override
	public SummonStat getStat()
	{
		if ((super.getStat() == null) || !(super.getStat() instanceof SummonStat))
		{
			setStat(new SummonStat(this));
		}
		return (SummonStat) super.getStat();
	}
	
	@Override
	public SummonStatus getStatus()
	{
		if ((super.getStatus() == null) || !(super.getStatus() instanceof SummonStatus))
		{
			setStatus(new SummonStatus(this));
		}
		return (SummonStatus) super.getStatus();
	}
	
	@Override
	public AI getAI()
	{
		if (ai == null)
		{
			synchronized (this)
			{
				if (ai == null)
				{
					ai = new L2SummonAI(new AIAccessor());
				}
			}
		}
		
		return ai;
	}
	
	// this defines the action buttons, 1 for Summon, 2 for Pets
	public abstract int getSummonType();
	
	@Override
	public void updateAbnormalEffect()
	{
		for (L2PcInstance player : getKnownList().getKnownPlayers().values())
		{
			player.sendPacket(new NpcInfo(this, player));
		}
	}
	
	/**
	 * @return Returns the mountable.
	 */
	public boolean isMountable()
	{
		return false;
	}
	
	@Override
	public void onAction(L2PcInstance player)
	{
		if ((player == _owner) && (player.getTarget() == this))
		{
			player.sendPacket(new PetStatusShow(this));
			player.sendPacket(new ActionFailed());
		}
		else
		{
			player.setTarget(this);
			MyTargetSelected my = new MyTargetSelected(getObjectId(), player.getLevel() - getLevel());
			player.sendPacket(my);
		}
	}
	
	public long getExpForThisLevel()
	{
		if (getLevel() >= Experience.LEVEL.length)
		{
			return 0;
		}
		return Experience.LEVEL[getLevel()];
	}
	
	public long getExpForNextLevel()
	{
		if (getLevel() >= (Experience.LEVEL.length - 1))
		{
			return 0;
		}
		return Experience.LEVEL[getLevel() + 1];
	}
	
	public final int getKarma()
	{
		return _karma;
	}
	
	public void setKarma(int karma)
	{
		_karma = karma;
	}
	
	public final L2PcInstance getOwner()
	{
		return _owner;
	}
	
	public final int getNpcId() {
		return template.getId();
	}
	
	public void setPvpFlag(byte pvpFlag)
	{
		_pvpFlag = pvpFlag;
	}
	
	public byte getPvpFlag()
	{
		return _pvpFlag;
	}
	
	public void setPkKills(int pkKills)
	{
		_pkKills = pkKills;
	}
	
	public final int getPkKills()
	{
		return _pkKills;
	}
	
	public final int getMaxLoad()
	{
		return _maxLoad;
	}
	
	public final int getSoulShotsPerHit()
	{
		return _soulShotsPerHit;
	}
	
	public final int getSpiritShotsPerHit()
	{
		return _spiritShotsPerHit;
	}
	
	public void setMaxLoad(int maxLoad)
	{
		_maxLoad = maxLoad;
	}
	
	public void setChargedSoulShot(int shotType)
	{
		_chargedSoulShot = shotType;
	}
	
	public void setChargedSpiritShot(int shotType)
	{
		_chargedSpiritShot = shotType;
	}
	
	public void followOwner()
	{
		setFollowStatus(true);
	}
	
	@Override
	public boolean doDie(L2Character killer)
	{
		if (!super.doDie(killer))
		{
			return false;
		}
		DecayTaskManager.getInstance().addDecayTask(this);
		return true;
	}
	
	public boolean doDie(L2Character killer, boolean decayed)
	{
		if (!super.doDie(killer))
		{
			return false;
		}
		if (!decayed)
		{
			DecayTaskManager.getInstance().addDecayTask(this);
		}
		return true;
	}
	
	@Override
	public void onDecay()
	{
		deleteMe(_owner);
	}
	
	@Override
	public void broadcastStatusUpdate()
	{
		super.broadcastStatusUpdate();
		
		if ((getOwner() != null) && isVisible())
		{
			getOwner().sendPacket(new PetStatusUpdate(this));
		}
	}
	
	public void deleteMe(L2PcInstance owner)
	{
		getAI().stopFollow();
		owner.sendPacket(new PetDelete(getObjectId(), 2));
		
		// FIXME: I think it should really drop items to ground and only owner can take for a while
		giveAllToOwner();
		decayMe();
		getKnownList().removeAllKnownObjects();
		owner.setPet(null);
	}
	
	public synchronized void unSummon(L2PcInstance owner)
	{
		if (isVisible() && !isDead())
		{
			getAI().stopFollow();
			owner.sendPacket(new PetDelete(getObjectId(), 2));
			if (getWorldRegion() != null)
			{
				getWorldRegion().removeFromZones(this);
			}
			store();
			
			giveAllToOwner();
			decayMe();
			getKnownList().removeAllKnownObjects();
			owner.setPet(null);
			setTarget(null);
		}
	}
	
	public void setFollowStatus(boolean state)
	{
		_follow = state;
		if (_follow)
		{
			getAI().setIntention(Intention.AI_INTENTION_FOLLOW, getOwner());
		}
		else
		{
			getAI().setIntention(Intention.AI_INTENTION_IDLE, null);
		}
	}
	
	public boolean getFollowStatus()
	{
		return _follow;
	}
	
	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		return _owner.isAutoAttackable(attacker);
	}
	
	public int getChargedSoulShot()
	{
		return _chargedSoulShot;
	}
	
	public int getChargedSpiritShot()
	{
		return _chargedSpiritShot;
	}
	
	public int getControlItemId()
	{
		return 0;
	}
	
	public PetInventory getInventory()
	{
		return null;
	}
	
	protected void doPickupItem(L2Object object)
	{
		return;
	}
	
	public void giveAllToOwner()
	{
		return;
	}
	
	public void store()
	{
		return;
	}
	
	@Override
	public L2ItemInstance getActiveWeaponInstance()
	{
		return null;
	}
	
	@Override
	public L2ItemInstance getSecondaryWeaponInstance()
	{
		return null;
	}


	@Override
	public L2Party getParty()
	{
		if (_owner == null)
		{
			return null;
		}
		return _owner.getParty();
	}

	@Override
	public boolean isInParty()
	{
		if (_owner == null)
		{
			return false;
		}
		return _owner.getParty() != null;
	}

	public void useMagic(L2Skill skill, boolean forceUse, boolean dontMove) {
		if ((skill == null) || isDead()) {
			return;
		}

		if (skill.isPassive()) {
			// just ignore the passive skill request. why does the client send it anyway ??
			return;
		}

		if (isCastingNow()){
			return;
		}

		L2Object target = null;
		
		switch (skill.getTargetType()) {
			case TARGET_OWNER_PET:
				target = getOwner();
				break;
			// PARTY, AURA, SELF should be cast even if no target has been found
			case TARGET_PARTY:
			case TARGET_AURA:
			case TARGET_SELF:
				target = this;
				break;
			default:
				target = skill.getFirstOfTargetList(this);
				break;
		}
		
		// Check the validity of the target
		if (target == null)
		{
			if (getOwner() != null)
			{
				getOwner().sendPacket(new SystemMessage(SystemMessageId.TARGET_CANT_FOUND));
			}
			return;
		}

		if (isSkillDisabled(skill.getId()) && (getOwner() != null) && (getOwner().getAccessLevel() < Config.GM_PEACEATTACK))
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.SKILL_NOT_AVAILABLE);
			sm.addString(skill.getName());
			getOwner().sendPacket(sm);
			return;
		}

		if (isAllSkillsDisabled() && (getOwner() != null) && (getOwner().getAccessLevel() < Config.GM_PEACEATTACK))
		{
			return;
		}
		

		if (getCurrentMp() < (getStat().getMpConsume(skill) + getStat().getMpInitialConsume(skill)))
		{
			if (getOwner() != null)
			{
				getOwner().sendPacket(new SystemMessage(SystemMessageId.NOT_ENOUGH_MP));
			}
			return;
		}

		if (getCurrentHp() <= skill.getHpConsume())
		{
			if (getOwner() != null)
			{
				getOwner().sendPacket(new SystemMessage(SystemMessageId.NOT_ENOUGH_HP));
			}
			return;
		}

		if (skill.isOffensive()) {
			if (isInsidePeaceZone(this, target) && (getOwner() != null) && (getOwner().getAccessLevel() < Config.GM_PEACEATTACK))
			{
				sendPacket(new SystemMessage(SystemMessageId.TARGET_IN_PEACEZONE));
				return;
			}
			
			if ((getOwner() != null) && getOwner().isInOlympiadMode() && !getOwner().isOlympiadStart())
			{
				sendPacket(new ActionFailed());
				return;
			}

			if (target instanceof L2DoorInstance)
			{
				if (!((L2DoorInstance) target).isAttackable(getOwner()))
				{
					return;
				}
			}
			else
			{
				if (!target.isAttackable() && (getOwner() != null) && (getOwner().getAccessLevel() < Config.GM_PEACEATTACK))
				{
					return;
				}

				if (!target.isAutoAttackable(this) && !forceUse && (skill.getTargetType() != SkillTargetType.TARGET_AURA) && (skill.getTargetType() != SkillTargetType.TARGET_CLAN) && (skill.getTargetType() != SkillTargetType.TARGET_ALLY) && (skill.getTargetType() != SkillTargetType.TARGET_PARTY) && (skill.getTargetType() != SkillTargetType.TARGET_SELF))
				{
					return;
				}
			}
		}
		getAI().setIntention(Intention.AI_INTENTION_CAST, skill, target);
	}
	
	@Override
	public void setIsImobilised(boolean value) {
		super.setIsImobilised(value);
		
		if (value)
		{
			_previousFollowStatus = getFollowStatus();
			if (_previousFollowStatus) {
				setFollowStatus(false);
			}
		}
		else
		{
			setFollowStatus(_previousFollowStatus);
		}
	}
	
	public void setOwner(L2PcInstance newOwner)
	{
		_owner = newOwner;
	}

	public boolean isShowSummonAnimation()
	{
		return _showSummonAnimation;
	}

	public void setShowSummonAnimation(boolean showSummonAnimation)
	{
		_showSummonAnimation = showSummonAnimation;
	}
	
	/**
	 * Servitors' skills automatically change their level based on the servitor's level.
	 * Until level 70, the servitor gets 1 lv of skill per 10 levels. After that, it is 1 skill level per 5 servitor levels. If the resulting skill level doesn't exist use the max that does exist!
	 * @see org.l2j.gameserver.model.L2Character#doCast(org.l2j.gameserver.model.L2Skill)
	 */
	@Override
	public void doCast(L2Skill skill)
	{
		int petLevel = getLevel();
		int skillLevel = petLevel / 10;
		if (petLevel >= 70){
			skillLevel += (petLevel - 65) / 10;
		}
		
		// adjust the level for servitors less than lv 10
		if (skillLevel < 1) {
			skillLevel = 1;
		}
		
		L2Skill skillToCast = SkillTable.getInstance().getInfo(skill.getId(), skillLevel);
		
		if (skillToCast != null) {
			super.doCast(skillToCast);
		}
		else
		{
			super.doCast(skill);
		}
	}
}
