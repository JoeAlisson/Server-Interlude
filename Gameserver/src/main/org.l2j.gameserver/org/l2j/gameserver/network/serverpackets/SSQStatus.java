package org.l2j.gameserver.network.serverpackets;

import org.l2j.commons.Config;
import org.l2j.gameserver.SevenSigns;
import org.l2j.gameserver.SevenSignsFestival;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.entity.database.SevenSignsFestivalData;
import org.l2j.gameserver.network.SystemMessageId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Seven Signs Record Update packet type id 0xf5 format: c cc (Page Num = 1 -> 4, period) 1: [ddd cc dd ddd c ddd c] 2: [hc [cd (dc (S))] 3: [ccc (cccc)] 4: [(cchh)]
 * @author Tempy
 */
public class SSQStatus extends L2GameServerPacket
{
	private static Logger _log = LoggerFactory.getLogger(SSQStatus.class.getName());

	private final L2PcInstance _activevChar;
	private final int _page;
	
	public SSQStatus(L2PcInstance player, int recordPage)
	{
		_activevChar = player;
		_page = recordPage;
	}
	
	@Override
	protected final void writeImpl()
	{
		int winningCabal = SevenSigns.getInstance().getCabalHighestScore();
		int totalDawnMembers = SevenSigns.getInstance().getTotalMembers(SevenSigns.CABAL_DAWN);
		int totalDuskMembers = SevenSigns.getInstance().getTotalMembers(SevenSigns.CABAL_DUSK);
		
		writeByte(0xf5);
		
		writeByte(_page);
		writeByte(SevenSigns.getInstance().getCurrentPeriod()); // current period?
		
		int dawnPercent = 0;
		int duskPercent = 0;
		
		switch (_page)
		{
			case 1:
				// [ddd cc dd ddd c ddd c]
				writeInt(SevenSigns.getInstance().getCurrentCycle());
				
				int currentPeriod = SevenSigns.getInstance().getCurrentPeriod();
				
				switch (currentPeriod)
				{
					case SevenSigns.PERIOD_COMP_RECRUITING:
						writeInt(SystemMessageId.INITIAL_PERIOD.getId());
						break;
					case SevenSigns.PERIOD_COMPETITION:
						writeInt(SystemMessageId.QUEST_EVENT_PERIOD.getId());
						break;
					case SevenSigns.PERIOD_COMP_RESULTS:
						writeInt(SystemMessageId.RESULTS_PERIOD.getId());
						break;
					case SevenSigns.PERIOD_SEAL_VALIDATION:
						writeInt(SystemMessageId.VALIDATION_PERIOD.getId());
						break;
				}
				
				switch (currentPeriod)
				{
					case SevenSigns.PERIOD_COMP_RECRUITING:
					case SevenSigns.PERIOD_COMP_RESULTS:
						writeInt(SystemMessageId.UNTIL_TODAY_6PM.getId());
						break;
					case SevenSigns.PERIOD_COMPETITION:
					case SevenSigns.PERIOD_SEAL_VALIDATION:
						writeInt(SystemMessageId.UNTIL_MONDAY_6PM.getId());
						break;
				}
				
				writeByte(SevenSigns.getInstance().getPlayerCabal(_activevChar));
				writeByte(SevenSigns.getInstance().getPlayerSeal(_activevChar));
				
				writeLong(SevenSigns.getInstance().getPlayerStoneContrib(_activevChar)); // Seal Stones Turned-In
				writeLong(SevenSigns.getInstance().getPlayerAdenaCollect(_activevChar)); // Ancient Adena to Collect
				
				double dawnStoneScore = SevenSigns.getInstance().getCurrentStoneScore(SevenSigns.CABAL_DAWN);
				int dawnFestivalScore = SevenSigns.getInstance().getCurrentFestivalScore(SevenSigns.CABAL_DAWN);
				
				double duskStoneScore = SevenSigns.getInstance().getCurrentStoneScore(SevenSigns.CABAL_DUSK);
				int duskFestivalScore = SevenSigns.getInstance().getCurrentFestivalScore(SevenSigns.CABAL_DUSK);
				
				double totalStoneScore = duskStoneScore + dawnStoneScore;
				
				/*
				 * Scoring seems to be proportionate to a set base value, so base this on the maximum obtainable score from festivals, which is 500.
				 */
				int duskStoneScoreProp = 0;
				int dawnStoneScoreProp = 0;
				
				if (totalStoneScore != 0)
				{
					duskStoneScoreProp = Math.round(((float) duskStoneScore / (float) totalStoneScore) * 500);
					dawnStoneScoreProp = Math.round(((float) dawnStoneScore / (float) totalStoneScore) * 500);
				}
				
				int duskTotalScore = SevenSigns.getInstance().getCurrentScore(SevenSigns.CABAL_DUSK);
				int dawnTotalScore = SevenSigns.getInstance().getCurrentScore(SevenSigns.CABAL_DAWN);
				
				int totalOverallScore = duskTotalScore + dawnTotalScore;
				
				if (totalOverallScore != 0)
				{
					dawnPercent = Math.round(((float) dawnTotalScore / (float) totalOverallScore) * 100);
					duskPercent = Math.round(((float) duskTotalScore / (float) totalOverallScore) * 100);
				}
				
				if (Config.DEBUG)
				{
					_log.info("Dusk Stone Score: " + duskStoneScore + " - Dawn Stone Score: " + dawnStoneScore);
					_log.info("Dusk Festival Score: " + duskFestivalScore + " - Dawn Festival Score: " + dawnFestivalScore);
					_log.info("Dusk Score: " + duskTotalScore + " - Dawn Score: " + dawnTotalScore);
					_log.info("Overall Score: " + totalOverallScore);
					_log.info("");
					if (totalStoneScore == 0)
					{
						_log.info("Dusk Prop: 0 - Dawn Prop: 0");
					}
					else
					{
						_log.info("Dusk Prop: " + ((duskStoneScore / totalStoneScore) * 500) + " - Dawn Prop: " + ((dawnStoneScore / totalStoneScore) * 500));
					}
					_log.info("Dusk %: " + duskPercent + " - Dawn %: " + dawnPercent);
				}
				
				/* DUSK */
				writeInt(duskStoneScoreProp); // Seal Stone Score
				writeInt(duskFestivalScore); // Festival Score
				writeInt(duskTotalScore); // Total Score
				
				writeByte(duskPercent); // Dusk %
				
				/* DAWN */
				writeInt(dawnStoneScoreProp); // Seal Stone Score
				writeInt(dawnFestivalScore); // Festival Score
				writeInt(dawnTotalScore); // Total Score
				
				writeByte(dawnPercent); // Dawn %
				break;
			case 2:
				// c cc hc [cd (dc (S))]
				writeShort(1);
				
				writeByte(5); // Total number of festivals
				
				for (int i = 0; i < 5; i++)
				{
					writeByte(i + 1); // Current client-side festival ID
					writeInt(SevenSignsFestival.FESTIVAL_LEVEL_SCORES[i]);
					
					long duskScore = SevenSignsFestival.getInstance().getHighestScore(SevenSigns.CABAL_DUSK, i);
					long dawnScore = SevenSignsFestival.getInstance().getHighestScore(SevenSigns.CABAL_DAWN, i);
					
					// Dusk Score \\
					writeLong(duskScore);
					
					SevenSignsFestivalData highScoreData = SevenSignsFestival.getInstance().getHighestScoreData(SevenSigns.CABAL_DUSK, i);
					String[] partyMembers = highScoreData.getMembers().split(",");
					
					if (partyMembers != null)
					{
						writeByte(partyMembers.length);
						
						for (String partyMember : partyMembers)
						{
							writeString(partyMember);
						}
					}
					else
					{
						writeByte(0);
					}
					
					// Dawn Score \\
					writeLong(dawnScore);
					
					highScoreData = SevenSignsFestival.getInstance().getHighestScoreData(SevenSigns.CABAL_DAWN, i);
					partyMembers = highScoreData.getMembers().split(",");
					
					if (partyMembers != null)
					{
						writeByte(partyMembers.length);
						
						for (String partyMember : partyMembers)
						{
							writeString(partyMember);
						}
					}
					else
					{
						writeByte(0);
					}
				}
				break;
			case 3:
				// c cc [ccc (cccc)]
				writeByte(10); // Minimum limit for winning cabal to retain their seal
				writeByte(35); // Minimum limit for winning cabal to claim a seal
				writeByte(3); // Total number of seals
				
				for (int i = 1; i < 4; i++)
				{
					int dawnProportion = SevenSigns.getInstance().getSealProportion(i, SevenSigns.CABAL_DAWN);
					int duskProportion = SevenSigns.getInstance().getSealProportion(i, SevenSigns.CABAL_DUSK);
					
					if (Config.DEBUG)
					{
						_log.info(SevenSigns.getSealName(i, true) + " = Dawn Prop: " + dawnProportion + "(" + ((dawnProportion / totalDawnMembers) * 100) + "%)" + ", Dusk Prop: " + duskProportion + "(" + ((duskProportion / totalDuskMembers) * 100) + "%)");
					}
					
					writeByte(i);
					writeByte(SevenSigns.getInstance().getSealOwner(i));
					
					if (totalDuskMembers == 0)
					{
						if (totalDawnMembers == 0)
						{
							writeByte(0);
							writeByte(0);
						}
						else
						{
							writeByte(0);
							writeByte(Math.round(((float) dawnProportion / (float) totalDawnMembers) * 100));
						}
					}
					else
					{
						if (totalDawnMembers == 0)
						{
							writeByte(Math.round(((float) duskProportion / (float) totalDuskMembers) * 100));
							writeByte(0);
						}
						else
						{
							writeByte(Math.round(((float) duskProportion / (float) totalDuskMembers) * 100));
							writeByte(Math.round(((float) dawnProportion / (float) totalDawnMembers) * 100));
						}
					}
				}
				break;
			case 4:
				// c cc [cc (cchh)]
				writeByte(winningCabal); // Overall predicted winner
				writeByte(3); // Total number of seals
				
				for (int i = 1; i < 4; i++)
				{
					int dawnProportion = SevenSigns.getInstance().getSealProportion(i, SevenSigns.CABAL_DAWN);
					int duskProportion = SevenSigns.getInstance().getSealProportion(i, SevenSigns.CABAL_DUSK);
					dawnPercent = Math.round((dawnProportion / (totalDawnMembers == 0 ? 1 : (float) totalDawnMembers)) * 100);
					duskPercent = Math.round((duskProportion / (totalDuskMembers == 0 ? 1 : (float) totalDuskMembers)) * 100);
					int sealOwner = SevenSigns.getInstance().getSealOwner(i);
					
					writeByte(i);
					
					switch (sealOwner)
					{
						case SevenSigns.CABAL_NULL:
							switch (winningCabal)
							{
								case SevenSigns.CABAL_NULL:
									writeByte(SevenSigns.CABAL_NULL);
									writeShort(SystemMessageId.COMPETITION_TIE_SEAL_NOT_AWARDED.getId());
									break;
								case SevenSigns.CABAL_DAWN:
									if (dawnPercent >= 35)
									{
										writeByte(SevenSigns.CABAL_DAWN);
										writeShort(SystemMessageId.SEAL_NOT_OWNED_35_MORE_VOTED.getId());
									}
									else
									{
										writeByte(SevenSigns.CABAL_NULL);
										writeShort(SystemMessageId.SEAL_NOT_OWNED_35_LESS_VOTED.getId());
									}
									break;
								case SevenSigns.CABAL_DUSK:
									if (duskPercent >= 35)
									{
										writeByte(SevenSigns.CABAL_DUSK);
										writeShort(SystemMessageId.SEAL_NOT_OWNED_35_MORE_VOTED.getId());
									}
									else
									{
										writeByte(SevenSigns.CABAL_NULL);
										writeShort(SystemMessageId.SEAL_NOT_OWNED_35_LESS_VOTED.getId());
									}
									break;
							}
							break;
						case SevenSigns.CABAL_DAWN:
							switch (winningCabal)
							{
								case SevenSigns.CABAL_NULL:
									if (dawnPercent >= 10)
									{
										writeByte(SevenSigns.CABAL_DAWN);
										writeShort(SystemMessageId.SEAL_OWNED_10_MORE_VOTED.getId());
									}
									else
									{
										writeByte(SevenSigns.CABAL_NULL);
										writeShort(SystemMessageId.COMPETITION_TIE_SEAL_NOT_AWARDED.getId());
									}
									break;
								case SevenSigns.CABAL_DAWN:
									if (dawnPercent >= 10)
									{
										writeByte(sealOwner);
										writeShort(SystemMessageId.SEAL_OWNED_10_MORE_VOTED.getId());
									}
									else
									{
										writeByte(SevenSigns.CABAL_NULL);
										writeShort(SystemMessageId.SEAL_OWNED_10_LESS_VOTED.getId());
									}
									break;
								case SevenSigns.CABAL_DUSK:
									if (duskPercent >= 35)
									{
										writeByte(SevenSigns.CABAL_DUSK);
										writeShort(SystemMessageId.SEAL_NOT_OWNED_35_MORE_VOTED.getId());
									}
									else if (dawnPercent >= 10)
									{
										writeByte(SevenSigns.CABAL_DAWN);
										writeShort(SystemMessageId.SEAL_OWNED_10_MORE_VOTED.getId());
									}
									else
									{
										writeByte(SevenSigns.CABAL_NULL);
										writeShort(SystemMessageId.SEAL_OWNED_10_LESS_VOTED.getId());
									}
									break;
							}
							break;
						case SevenSigns.CABAL_DUSK:
							switch (winningCabal)
							{
								case SevenSigns.CABAL_NULL:
									if (duskPercent >= 10)
									{
										writeByte(SevenSigns.CABAL_DUSK);
										writeShort(SystemMessageId.SEAL_OWNED_10_MORE_VOTED.getId());
									}
									else
									{
										writeByte(SevenSigns.CABAL_NULL);
										writeShort(SystemMessageId.COMPETITION_TIE_SEAL_NOT_AWARDED.getId());
									}
									break;
								case SevenSigns.CABAL_DAWN:
									if (dawnPercent >= 35)
									{
										writeByte(SevenSigns.CABAL_DAWN);
										writeShort(SystemMessageId.SEAL_NOT_OWNED_35_MORE_VOTED.getId());
									}
									else if (duskPercent >= 10)
									{
										writeByte(sealOwner);
										writeShort(SystemMessageId.SEAL_OWNED_10_MORE_VOTED.getId());
									}
									else
									{
										writeByte(SevenSigns.CABAL_NULL);
										writeShort(SystemMessageId.SEAL_OWNED_10_LESS_VOTED.getId());
									}
									break;
								case SevenSigns.CABAL_DUSK:
									if (duskPercent >= 10)
									{
										writeByte(sealOwner);
										writeShort(SystemMessageId.SEAL_OWNED_10_MORE_VOTED.getId());
									}
									else
									{
										writeByte(SevenSigns.CABAL_NULL);
										writeShort(SystemMessageId.SEAL_OWNED_10_LESS_VOTED.getId());
									}
									break;
							}
							break;
					}
					writeShort(0);
				}
				break;
		}
	}
}