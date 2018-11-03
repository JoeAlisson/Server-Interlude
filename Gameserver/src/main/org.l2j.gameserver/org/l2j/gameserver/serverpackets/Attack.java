package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.model.L2Character;
import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.templates.xml.jaxb.CrystalType;

public class Attack extends L2GameServerPacket  {

	private final int _attackerObjId;
	public final boolean soulshot;
    private final int tx;
    private final int ty;
    private final int tz;
    private CrystalType _grade;
	private final int _x;
	private final int _y;
	private final int _z;
	private Hit[] _hits;
	

	public Attack(L2Character attacker, boolean useShots, CrystalType grade) {
		_attackerObjId = attacker.getObjectId();
		soulshot = useShots;
		_grade = grade;
		_x = attacker.getX();
		_y = attacker.getY();
		_z = attacker.getZ();
		_hits = new Hit[0];
        var target = attacker.getTarget();
        tx = target.getX();
        ty = target.getY();
        tz = target.getZ();
	}

	public void addHit(L2Object target, int damage, boolean miss, boolean crit, boolean shld) {
		// Get the last position in the hits table
		int pos = _hits.length;
		Hit[] tmp = new Hit[pos + 1];

		for (int i = 0; i < _hits.length; i++) {
			tmp[i] = _hits[i];
		}
		tmp[pos] = new Hit(target, damage, miss, crit, shld);
		_hits = tmp;
	}
	
	/**
	 * @return {@code true} if the Server-Client packet Attack contains at least 1 hit, {@code false} otherwise
	 */
	public boolean hasHits()
	{
		return _hits.length > 0;
	}
	
	@Override
	protected final void writeImpl()  {
		writeByte(0x33);
		
		writeInt(_attackerObjId);
		writeInt(_hits[0]._targetId);
		writeInt(0); // Additional Shots effect
		writeInt(_hits[0]._damage);
		writeInt(_hits[0]._flags);
		writeInt(soulshot ? _grade.ordinal() : 0x00);
		writeInt(_x);
		writeInt(_y);
		writeInt(_z);
		writeShort(_hits.length - 1);
		for (int i = 1; i < _hits.length; i++)
		{
			writeInt(_hits[i]._targetId);
			writeInt(_hits[i]._damage);
			writeInt(_hits[i]._flags);
			writeInt(soulshot ? _grade.ordinal() : 0x00);
		}

		writeInt(tx);
		writeInt(ty);
		writeInt(tz);
	}

    @Override
    protected int packetSize() {
        return _hits.length * 20 + 63 ;
    }

    private class Hit {
        int _targetId;
        int _damage;
        int _flags;

        Hit(L2Object target, int damage, boolean miss, boolean crit, boolean shld)  {
            _targetId = target.getObjectId();
            _damage = damage;
            if (soulshot) {
                _flags |= 0x10 | _grade.ordinal();
            }
            if (crit)  {
                _flags |= 0x20;
            }
            if (shld) {
                _flags |= 0x40;
            }
            if (miss) {
                _flags |= 0x80;
            }

        }
    }
}
