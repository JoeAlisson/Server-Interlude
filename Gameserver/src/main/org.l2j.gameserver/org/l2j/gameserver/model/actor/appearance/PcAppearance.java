package org.l2j.gameserver.model.actor.appearance;

public class PcAppearance {
	private byte face;
	private byte hairColor;
	private byte hairStyle;
	private byte sex; // Female 1
	/** true if the player is invisible */
	private boolean _invisible = false;
	/** The hexadecimal Color of players name (white is 0xFFFFFF) */
	private int _nameColor = 0xFFFFFF;
	/** The hexadecimal Color of players name (white is 0xFFFFFF) */
	private int _titleColor = 0xFFFF77;

	public PcAppearance(byte Face, byte HColor, byte HStyle, byte Sex) {
		face = Face;
		hairColor = HColor;
		hairStyle = HStyle;
		sex = Sex;
	}

	public final byte getFace()
	{
		return face;
	}

	public final void setFace(int value)
	{
		face = (byte) value;
	}
	
	public final byte getHairColor()
	{
		return hairColor;
	}

	public final void setHairColor(int value)
	{
		hairColor = (byte) value;
	}
	
	public final byte getHairStyle()
	{
		return hairStyle;
	}

	public final void setHairStyle(int value)
	{
		hairStyle = (byte) value;
	}
	
	public final byte getSex()
	{
		return sex;
	}

	public final void setSex(byte sex)
	{
		this.sex = sex;
	}
	
	public void setInvisible()
	{
		_invisible = true;
	}
	
	public void setVisible()
	{
		_invisible = false;
	}
	
	public boolean getInvisible()
	{
		return _invisible;
	}
	
	public int getNameColor()
	{
		return _nameColor;
	}
	
	public void setNameColor(int nameColor)
	{
		_nameColor = nameColor;
	}
	
	public void setNameColor(int red, int green, int blue)
	{
		_nameColor = (red & 0xFF) + ((green & 0xFF) << 8) + ((blue & 0xFF) << 16);
	}
	
	public int getTitleColor()
	{
		return _titleColor;
	}
	
	public void setTitleColor(int titleColor)
	{
		_titleColor = titleColor;
	}
	
	public void setTitleColor(int red, int green, int blue)
	{
		_titleColor = (red & 0xFF) + ((green & 0xFF) << 8) + ((blue & 0xFF) << 16);
	}
}
