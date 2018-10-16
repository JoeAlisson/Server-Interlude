package com.l2jbr.gameserver.clientpackets;

import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;
import com.l2jbr.gameserver.network.L2GameClient.GameClientState;
import com.l2jbr.gameserver.serverpackets.ActionFailed;
import com.l2jbr.gameserver.serverpackets.CharacterSelectedPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.isNull;

public class CharacterSelected extends L2GameClientPacket  {
	private static final String _C__0D_CHARACTERSELECTED = "[C] 0D CharacterSelected";
	private static Logger _log = LoggerFactory.getLogger(CharacterSelected.class.getName());

	private int _charSlot;

	@Override
	protected void readImpl()  {
		_charSlot = readInt();
	}
	
	@Override
	protected void runImpl() {
		// if there is a playback.dat file in the current directory, it will
		// be sent to the client instead of any regular packets
		// to make this work, the first packet in the playback.dat has to
		// be a [S]0x21 packet
		// after playback is done, the client will not work correct and need to exit
		// playLogFile(getConnection()); // try to play log file
		
		// we should always be abble to acquire the lock
		// but if we cant lock then nothing should be done (ie repeated packet)
		if (client.getActiveCharLock().tryLock()) {
			try {
				// should always be null
				// but if not then this is repeated packet and nothing should be done here
				if (isNull(client.getActiveChar())) {
					// The L2PcInstance must be created here, so that it can be attached to the L2GameClient
					_log.debug("selected slot: {}", _charSlot);
					
					// load up character from disk
					L2PcInstance cha = client.loadCharFromDisk(_charSlot);
					if (isNull(cha)) {
						_log.error("Character could not be loaded (slot: {})", _charSlot);
						sendPacket(new ActionFailed());
						return;
					}
					if (cha.getAccessLevel() < 0)  {
						cha.closeNetConnection();
						return;
					}
					
					cha.setClient(client);
					client.setActiveChar(cha);
					
					client.setState(GameClientState.IN_GAME);
					sendPacket(new CharacterSelectedPacket(cha));
				}
			}
			finally {
				client.getActiveCharLock().unlock();
			}
		}
	}
	
	/*
	 * private void playLogFile(Connection connection) { long diff = 0; long first = -1; try { LineNumberReader lnr = new LineNumberReader(new FileReader("playback.dat")); String line = null; while ((line = lnr.readLine()) != null) { if (line.length() > 0 && line.substring(0, 1).equals("1")) {
	 * String timestamp = line.substring(0, 13); long time = Long.parseLong(timestamp); if (first == -1) { long start = System.currentTimeMillis(); first = time; diff = start - first; } String cs = line.substring(14, 15); // read packet definition ByteArrayOutputStream bais = new
	 * ByteArrayOutputStream(); while (true) { String temp = lnr.readLine(); if (temp.length() < 53) { break; } String bytes = temp.substring(6, 53); StringTokenizer st = new StringTokenizer(bytes); while (st.hasMoreTokens()) { String b = st.nextToken(); int number = Integer.parseInt(b, 16);
	 * bais.write(number); } } if (cs.equals("S")) { //wait for timestamp and send packet int wait = (int) (time + diff - System.currentTimeMillis()); if (wait > 0) { if (Config.DEBUG) logger.debug("waiting"+ wait); Thread.sleep(wait); } if (Config.DEBUG) logger.debug("sending:"+ time); byte[] data =
	 * bais.toByteArray(); if (data.length != 0) { //connection.sendPacket(data); } else { if (Config.DEBUG) logger.debug("skipping broken data"); } } else { // skip packet } } } } catch (FileNotFoundException f) { // should not happen } catch (Exception e) { logger.error( "Error:", e); } }
	 */
	
	/*
	 * (non-Javadoc)
	 * @see com.l2jbr.gameserver.clientpackets.ClientBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _C__0D_CHARACTERSELECTED;
	}
}
