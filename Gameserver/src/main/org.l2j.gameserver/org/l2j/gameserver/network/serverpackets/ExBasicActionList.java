package org.l2j.gameserver.network.serverpackets;

public class ExBasicActionList extends L2GameServerPacket {
	private static final int[] BASIC_ACTIONS = {
		0, // (/sit, /stand)
		1, // (/walk, /run)
		2, // (/attack, /attackforce)
		3, // (/trade)
		4, // (/targetnext)
		5, // (/pickup)
		6, // (/assist)
		7, // (/invite)
		8, // (/leave)
		9, // (/dismiss)
		10, // (/vendor)
		11, // (/partymatching)
		12, // (/socialhello)
		13, // (/socialvictory)
		14, // (/socialcharge)
		15, // Pet follow
		16, // Attack target
		17, // Abort current action.
		18, // Pick up
		19, // Clear pet invetory
		20, // Use Special Skill
		21, // Minion Follow 2 ??
		22, // Attack target 2 ??
		23, // Abort current acton 2 ??
		24, // (/socialyes)
		25, // (/socialno)
		26, // (/socialbow)
		27, // Use Special Skill 2 ??
		28, // (/buy)
		29, // (/socialunaware)
		30, // (/socialwaiting)
		31, // (/sociallaugh)
		32, // Switching attacks/moviment
		33, // (/socialapplause)
		34, // (/socialdance)
		35, // (/socialsad)
		36, // Poisonous gas attack
		37, // (/dwarvenmanufacture)
		38, // (/mount, /dismount, /mountdismount)
		39, // Attack by exploding corpses.
		40, // (/evaluate)
		41, // Attack the gates of the castle, walls or headquarters with a gun shot.
		42, // Returns damage to the enemy.
		43, // Attack the enemy, creating a swirling maelstrom.
		44, // Attack the enemy with a powerful explosion.
		45, // Restores MP Summoner.
		46, // Attack the enemy, calling a devastating storm.
		47, // At the same time damages the enemy and heals the servant.
		48, // Attack the enemy with a gun shot.
		49, // Attack in a fit of rage.
		50, // (/changepartyleader)
		51, // (/generalmanufacture)
		52, // Removes the bond from the minion and frees him.
		53, // Move to the goal.
		54, // Move to the goal 2 ??.
		55, // (/start_videorecording, /end_videorecording, /startend_videorecording)
		56, // (/channelinvite)
		57, // (/findprivatestore)
		58, // (/duel)
		59, // (/withdraw)
		60, // (/partyduel)
		61, // (/packagesale)
		62, // (/charm)
		63, // (/minigame)
		64, // (/teleportbookmark)
		65, // Report Bot
		66, // (команда: /shyness)
		67, // Ship Control
		68, // Termination of ship control
		69, // Ship departure
		70, // Descent from the ship
		71, // Social Bow
		72, // Social High five
		73, // Social Dance
		74, // On / Off status data
		76, // Inviting a friend
		77, // On off. Record
		78, // Use of Sign 1
		79, // Use of Sign 2
		80, // Use of Sign 3
		81, // Use of Sign 4
		82, // ??
		83, //
		84, //
		85, //
		86, // Start / interrupt automatic group search
		87, // Propose
		88, // Provoke
		89, // Boasting
		90, // Underground
		1000, // Siege Hammer
		1001, // Terminal Accelerator
		1002, // Hostility
		1003, // Wild Stun
		1004, // Wild Protection
		1005, // Bright flash
		1006, // Healing Light
		1007, // Queen's Blessing
		1008, // Queen's Gift
		1009, // Healing Queen
		1010, // Blessing of Seraphim
		1011, // Gift Seraphim
		1012, // Healing of Seraphim
		1013, // Curse of Shadow
		1014, // Mass Curse of Shadow
		1015, // Shadow Victim
		1016, // Cursed Impulse
		1017, // Cursed Strike
		1018, // Curse of Energy Absorption
		1019, // Cat Skill 2
		1020, // Meow skill 2
		1021, // Kai's skill 2
		1022, // Jupiter skill 2
		1023, // Mirage Skill 2
		1024, // Skill of Bekara 2
		1025, // Shadow Skill 1
		1026, // Shadow Skill 2
		1027, // Hecate Skill
		1028, // Resurrection Skill 1
		1029, // Resurrection Skill 2
		1030, // Vicious Skill 2
		1031, // Dissection
		1032, // Cutting Whirl
		1033, // Cat Snack
		1034, // Whip
		1035, // Tidal wave
		1036, // Corpse Explosion
		1037, // Accidental Death
		1038, // The Power of Curse
		1039, // Cannon Meat
		1040, // Big Boom
		1041, // Bite
		1042, // Sledgehammer
		1043, // Wolf Ryk
		1044, // Awakening
		1045, // Wolf Howl
		1046, // Roar of the Driving Dragon
		1047, // Bite of the Divine Beast
		1048, // Deafening Attack of the Divine Beast
		1049, // Fiery Breath of the Divine Beast
		1050, // The Roar of the Divine Beast
		1051, // Blessing of the Body
		1052, // Blessing of the Spirit
		1053, // Acceleration
		1054, // Insight
		1055, // Clean
		1056, // Encouragement
		1057, // Wild Magic
		1058, // Whisper of Death
		1059, // Focus
		1060, // Guidance
		1061, // Death Strike
		1062, // Double Attack
		1063, // Swirl Attack
		1064, // Meteor shower
		1065, // Awakening
		1066, // Thunderbolt
		1067, // Lightning
		1068, // Light Wave
		1069, // Flash
		1070, // Effect Control
		1071, // powerful blow
		1072, // Penetrating Attack
		1073, // Furious Wind
		1074, // Beat the Spear
		1075, // Battle Cry
		1076, // Powerful Devastation
		1077, // Ball Lightning
		1078, // Shock Wave
		1079, // Howl
		1080, // Tide of the Phoenix
		1081, // Cleansing the Phoenix
		1082, // Flaming Phoenix Feather
		1083, // Phoenix Flaming Beak
		1084, // Change Mode
		1086, // Panther onslaught
		1087, // Dark Claw of the Panther
		1088, // Panther's Deadly Claw
		1089, // Tail
		1090, // Ride Dragon Bite
		1091, // Ride of the Dragon
		1092, // Rush of the Racing Dragon
		1093, // The Magwen Beat
		1094, // Magwen's Wind Walk
		1095, // Magwen's Powerful Punch
		1096, // Elk Maguven Wind Walk
		1097, // The Return of Magwen
		1098, // Magwen Group Return
		1099, // Attack
		1100, // Move
		1101, // Termination
		1102, // Cancel Call
		1103, // Passivity
		1104, // Protection
		1106, // Bear Claw
		1107, // Bumble Bear
		1108, // Cougar Bite
		1109, // Cougar Jump
		1110, // Touch of the Ripper
		1111, // The Power Of The Ripper
		1113, // Lion Roar
		1114, // Lion Claw
		1115, // Lion Throw
		1116, // The Lion Flame
		1117, // Flight of the Thunder Serpent
		1118, // Cleansing the Thunder Serpent
		1120, // Shooting Thunder Snake Feathers
		1121, // Sharp Claws of the Thunder Serpent
		1122, // Blessing of Life
		1123, // Siege Strike
		1124, // Aggression of the Cat
		1125, // Cat's Stunning
		1126, // Cat Bite
		1127, // Attacking Cat Jump
		1128, // The Touch of the Cat
		1129, // Cat Power
		1130, // Aggression of the Unicorn
		1131, // Stunning the Unicorn
		1132, // Bite of the Unicorn
		1133, // Unicorn Attack Jump
		1134, // Touch of the Unicorn
		1135, // The Power of the Unicorn
		1136, // Aggression of the Phantom
		1137, // Phantom Stun
		1138, // Bite of the Phantom
		1139, // Attack Phantom Jump
		1140, // Touch of the Phantom
		1141, // The Power of the Phantom
		1142, // The Roar of the Panther
		1143, // Swift Panther Throw
		5000, // Pat
		5001, // The Temptation of the Light of the Rose
		5002, // The Extreme Temptation
		5003, // Thunderbolt
		5004, // Lightning
		5005, // Light Wave
		5006, // Effect Control
		5007, // Penetrating Attack
		5008, // Vortex Attack
		5009, // Smashing
		5010, // Battle Cry
		5011, // Powerful Devastation
		5012, // Ball Lightning
		5013, // Shock Wave
		5014, // Ignite
		5015, // Change Mode
		5016, // Reinforcement of the Cat Ranger
	};

	@Override
	protected void writeImpl() {
		// TODO implement Transformation Action
        writeByte(0xFE);
        writeShort(0x60);
        writeInt(BASIC_ACTIONS.length);
        for (int action : BASIC_ACTIONS) {
            writeInt(action);
        }
	}
}