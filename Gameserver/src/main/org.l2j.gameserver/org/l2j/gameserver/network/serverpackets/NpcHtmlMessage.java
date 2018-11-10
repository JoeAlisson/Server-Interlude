package org.l2j.gameserver.network.serverpackets;

import org.l2j.commons.Config;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.clientpackets.RequestBypassToServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * the HTML parser in the client knowns these standard and non-standard tags and attributes VOLUMN UNKNOWN UL U TT TR TITLE TEXTCODE TEXTAREA TD TABLE SUP SUB STRIKE SPIN SELECT RIGHT PRE P OPTION OL MULTIEDIT LI LEFT INPUT IMG I HTML H7 H6 H5 H4 H3 H2 H1 FONT EXTEND EDIT COMMENT COMBOBOX CENTER
 * BUTTON BR BODY BAR ADDRESS A SEL LIST VAR FORE READONL ROWS VALIGN FIXWIDTH BORDERCOLORLI BORDERCOLORDA BORDERCOLOR BORDER BGCOLOR BACKGROUND ALIGN VALU READONLY MULTIPLE SELECTED TYP TYPE MAXLENGTH CHECKED SRC Y X QUERYDELAY NOSCROLLBAR IMGSRC B FG SIZE DECO COLOR DEFFON DEFFIXEDFONT WIDTH VALUE
 * TOOLTIP NAME MIN MAX HEIGHT DISABLED ALIGN MSG LINK HREF ACTION
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class NpcHtmlMessage extends L2GameServerPacket
{
	private static Logger _log = LoggerFactory.getLogger(RequestBypassToServer.class.getName());
	private final int _npcObjId;
	private String _html;

	public NpcHtmlMessage(int npcObjId, String text)
	{
		_npcObjId = npcObjId;
		setHtml(text);
	}
	
	public NpcHtmlMessage(int npcObjId)
	{
		_npcObjId = npcObjId;
	}
	
	@Override
	public void runImpl()
	{
		if (Config.BYPASS_VALIDATION)
		{
			buildBypassCache(getClient().getActiveChar());
		}
	}
	
	public void setHtml(String text)
	{
		if (text.length() > 8192)
		{
			_log.warn("Html is too long! this will crash the client!");
			_html = "<html><body>Html was too long</body></html>";
			return;
		}
		_html = text; // html code must not exceed 8192 bytes
	}
	
	public boolean setFile(String path)
	{
		String content = HtmCache.getInstance().getHtm(path);
		
		if (content == null)
		{
			setHtml("<html><body>My Text is missing:<br>" + path + "</body></html>");
			_log.warn("missing html page " + path);
			return false;
		}
		
		setHtml(content);
		return true;
	}
	
	public void replace(String pattern, String value)
	{
		_html = _html.replaceAll(pattern, value);
	}
	
	private final void buildBypassCache(L2PcInstance activeChar)
	{
		if (activeChar == null)
		{
			return;
		}
		
		activeChar.clearBypass();
		int len = _html.length();
		for (int i = 0; i < len; i++)
		{
			int start = _html.indexOf("bypass -h", i);
			int finish = _html.indexOf("\"", start);
			
			if ((start < 0) || (finish < 0))
			{
				break;
			}
			
			start += 10;
			i = start;
			int finish2 = _html.indexOf("$", start);
			if ((finish2 < finish) && (finish2 > 0))
			{
				activeChar.addBypass2(_html.substring(start, finish2));
			}
			else
			{
				activeChar.addBypass(_html.substring(start, finish));
			}
		}
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x0f);
		
		writeInt(_npcObjId);
		writeString(_html);
		writeInt(0x00);
	}
}
