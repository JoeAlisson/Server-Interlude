-- 
-- Table structure for table `char_templates`
--
DROP TABLE IF EXISTS `char_templates`;
CREATE TABLE IF NOT EXISTS `char_templates` (
  `ClassId` INT(11) NOT NULL DEFAULT '0',
  `ClassName` VARCHAR(20) NOT NULL DEFAULT '',
  `classLevel` INT(3) NOT NULL DEFAULT '0',
  `RaceId` INT(1) NOT NULL DEFAULT '0',
  `parent_id` INT(11) NOT NULL DEFAULT '0',
  `STR` INT(2) NOT NULL DEFAULT '0',
  `CON` INT(2) NOT NULL DEFAULT '0',
  `DEX` INT(2) NOT NULL DEFAULT '0',
  `_INT` INT(2) NOT NULL DEFAULT '0',
  `WIT` INT(2) NOT NULL DEFAULT '0',
  `MEN` INT(2) NOT NULL DEFAULT '0',
  `P_ATK` INT(3) NOT NULL DEFAULT '0',
  `P_DEF` INT(3) NOT NULL DEFAULT '0',
  `M_ATK` INT(3) NOT NULL DEFAULT '0',
  `M_DEF` INT(2) NOT NULL DEFAULT '0',
  `P_SPD` INT(3) NOT NULL DEFAULT '0',
  `M_SPD` INT(3) NOT NULL DEFAULT '0',
  `ACC` INT(3) NOT NULL DEFAULT '0',
  `CRITICAL` INT(3) NOT NULL DEFAULT '0',
  `EVASION` INT(3) NOT NULL DEFAULT '0',
  `MOVE_SPD` INT(3) NOT NULL DEFAULT '0',
  `_LOAD` INT(11) NOT NULL DEFAULT '0',
  `defaulthpbase` DECIMAL(5,1) NOT NULL DEFAULT '0.0',
  `defaulthpadd` DECIMAL(4,2) NOT NULL DEFAULT '0.00',
  `defaulthpmod` DECIMAL(4,2) NOT NULL DEFAULT '0.00',
  `defaultcpbase` DECIMAL(5,1) NOT NULL DEFAULT '0.0',
  `defaultcpadd` DECIMAL(4,2) NOT NULL DEFAULT '0.00',
  `defaultcpmod` DECIMAL(4,2) NOT NULL DEFAULT '0.00',
  `defaultmpbase` DECIMAL(5,1) NOT NULL DEFAULT '0.0',
  `defaultmpadd` DECIMAL(4,2) NOT NULL DEFAULT '0.00',
  `defaultmpmod` DECIMAL(4,2) NOT NULL DEFAULT '0.00',
  `x` INT(9) NOT NULL DEFAULT '0',
  `y` INT(9) NOT NULL DEFAULT '0',
  `z` INT(9) NOT NULL DEFAULT '0',
  `canCraft` INT(1) NOT NULL DEFAULT '0',
  `M_UNK1` DECIMAL(4,2) NOT NULL DEFAULT '0.00',
  `M_UNK2` DECIMAL(8,6) NOT NULL DEFAULT '0.000000',
  `M_COL_R` DECIMAL(3,1) NOT NULL DEFAULT '0.0',
  `M_COL_H` DECIMAL(4,1) NOT NULL DEFAULT '0.0',
  `F_UNK1` DECIMAL(4,2) NOT NULL DEFAULT '0.00',
  `F_UNK2` DECIMAL(8,6) NOT NULL DEFAULT '0.000000',
  `F_COL_R` DECIMAL(3,1) NOT NULL DEFAULT '0.0',
  `F_COL_H` DECIMAL(4,1) NOT NULL DEFAULT '0.0',
  `items1` INT(4) NOT NULL DEFAULT '0',
  `items2` INT(4) NOT NULL DEFAULT '0',
  `items3` INT(4) NOT NULL DEFAULT '0',
  `items4` INT(4) NOT NULL DEFAULT '0',
  `items5` INT(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ClassId`))
ENGINE = InnoDB;

-- 
-- Dumping data for table `char_templates`
-- 
INSERT INTO `char_templates` VALUES
 (0, 'Human Fighter',1,0,-1,40,43,30,21,11,25,4,80,6,41,300,333,33,44,33,115,81900, 80.0, 11.83, 0.37, 32.0, 4.73, 0.22, 30.0, 5.46, 0.14,-71338,258271,-3104,0, 1.10, 1.188000, 9.0, 23.0, 1.10, 1.188000, 8.0, 23.5,1147,1146,10,2369, 5588),
 (1, 'Warrior',20,0,0,40,43,30,21,11,25,4,80,6,41,300,333,33,44,33,115,81900,327.0, 33.00, 0.37, 261.6, 26.40, 0.22, 144.0, 9.90, 0.14,-71338,258271,-3104,0, 1.10, 1.188000, 9.0, 23.0, 1.10, 1.188000, 8.0, 23.5,1147,1146,10,2369, 5588),
 (2, 'Gladiator',40,0,1,40,43,30,21,11,25,4,80,6,41,300,333,33,44,33,115,81900,1044.0, 49.40, 0.37, 939.6, 44.46, 0.22, 359.1, 19.50, 0.14,-71338,258271,-3104,0, 1.10, 1.188000, 9.0, 23.0, 1.10, 1.188000, 8.0, 23.5,1147,1146,10,2369, 5588),
 (3, 'Warlord',40,0,1,40,43,30,21,11,25,4,80,6,41,300,333,33,44,33,115,81900,1044.0, 54.60, 0.37, 835.2, 43.68, 0.22, 359.1, 19.50, 0.14,-71338,258271,-3104,0, 1.10, 1.188000, 9.0, 23.0, 1.10, 1.188000, 8.0, 23.5,1147,1146,10,2369, 5588),
 (4, 'Human Knight',20,0,0,40,43,30,21,11,25,4,80,6,41,300,333,33,44,33,115,81900,327.0, 29.70, 0.37, 196.2, 17.82, 0.22, 144.0, 9.90, 0.14,-71338,258271,-3104,0, 1.10, 1.188000, 9.0, 23.0, 1.10, 1.188000, 8.0, 23.5,1147,1146,10,2369, 5588),
 (5, 'Paladin',40,0,4,40,43,30,21,11,25,4,80,6,41,300,333,33,44,33,115,81900,972.3, 46.80, 0.37, 583.3, 28.08, 0.22, 359.1, 19.50, 0.14,-71338,258271,-3104,0, 1.10, 1.188000, 9.0, 23.0, 1.10, 1.188000, 8.0, 23.5,1147,1146,10,2369, 5588),
 (6, 'Dark Avenger',40,0,4,40,43,30,21,11,25,4,80,6,41,300,333,33,44,33,115,81900,972.3, 46.80, 0.37, 583.3, 28.08, 0.22, 359.1, 19.50, 0.14,-71338,258271,-3104,0, 1.10, 1.188000, 9.0, 23.0, 1.10, 1.188000, 8.0, 23.5,1147,1146,10,2369, 5588),
 (7, 'Rogue',20,0,0,40,43,30,21,11,25,4,80,6,41,300,333,33,44,33,115,81900,327.0, 27.50, 0.37, 130.8, 11.00, 0.22, 144.0, 9.90, 0.14,-71338,258271,-3104,0, 1.10, 1.188000, 9.0, 23.0, 1.10, 1.188000, 8.0, 23.5,1147,1146,10,2369, 5588),
 (8, 'Treasure Hunter',40,0,7,40,43,30,21,11,25,4,80,6,41,300,333,33,44,33,115,81900,924.5, 41.60, 0.37, 369.8, 16.64, 0.22, 359.1, 19.50, 0.14,-71338,258271,-3104,0, 1.10, 1.188000, 9.0, 23.0, 1.10, 1.188000, 8.0, 23.5,1147,1146,10,2369, 5588),
 (9, 'Hawkeye',40,0,7,40,43,30,21,11,25,4,80,6,41,300,333,33,44,33,115,81900,924.5, 44.20, 0.37, 647.1, 30.94, 0.22, 359.1, 19.50, 0.14,-71338,258271,-3104,0, 1.10, 1.188000, 9.0, 23.0, 1.10, 1.188000, 8.0, 23.5,1147,1146,10,2369, 5588),
 (10, 'Human Mage',1,0,-1,22,27,21,41,20,39,3,54,6,41,300,333,28,40,28,120,62500,101.0, 15.57, 0.37, 50.5, 7.84, 0.22, 40.0, 7.38, 0.14,-90890,248027,-3570,0, 1.01, 0.872640, 7.5, 22.8, 1.01, 0.872640, 6.5, 22.5,425,461,6,5588, 0),
 (11, 'Human Wizard',20,0,10,22,27,21,41,20,39,3,54,6,41,300,333,28,40,28,120,62500,424.0, 27.60, 0.37, 212.0, 13.85, 0.22, 192.0, 13.30, 0.14,-90890,248027,-3570,0, 1.01, 0.872640, 7.5, 22.8, 1.01, 0.872640, 6.5, 22.5,425,461,6,5588, 0),
 (12, 'Sorcerer',40,0,11,22,27,21,41,20,39,3,54,6,41,300,333,28,40,28,120,62500,1021.5, 45.60, 0.37, 510.7, 22.85, 0.22, 478.8, 26.10, 0.14,-90890,248027,-3570,0, 1.01, 0.872640, 7.5, 22.8, 1.01, 0.872640, 6.5, 22.5,425,461,6,5588, 0),
 (13, 'Necromancer',40,0,11,22,27,21,41,20,39,3,54,6,41,300,333,28,40,28,120,62500,1021.5, 45.60, 0.37, 510.7, 22.85, 0.22, 478.8, 26.10, 0.14,-90890,248027,-3570,0, 1.01, 0.872640, 7.5, 22.8, 1.01, 0.872640, 6.5, 22.5,425,461,6,5588, 0),
 (14, 'Warlock',40,0,11,22,27,21,41,20,39,3,54,6,41,300,333,28,40,28,120,62500,1021.5, 49.50, 0.37, 612.9, 29.74, 0.22, 478.8, 26.10, 0.14,-90890,248027,-3570,0, 1.01, 0.872640, 7.5, 22.8, 1.01, 0.872640, 6.5, 22.5,425,461,6,5588, 0),
 (15, 'Cleric',20,0,10,22,27,21,41,20,39,3,54,6,41,300,333,28,40,28,120,62500,424.0, 34.20, 0.37, 212.0, 17.15, 0.22, 192.0, 13.30, 0.14,-90890,248027,-3570,0, 1.01, 0.872640, 7.5, 22.8, 1.01, 0.872640, 6.5, 22.5,425,461,6,5588, 0),
 (16, 'Bishop',40,0,15,22,27,21,41,20,39,3,54,6,41,300,333,28,40,28,120,62500,1164.9, 49.50, 0.37, 815.4, 34.68, 0.22, 478.8, 26.10, 0.14,-90890,248027,-3570,0, 1.01, 0.872640, 7.5, 22.8, 1.01, 0.872640, 6.5, 22.5,425,461,6,5588, 0),
 (17, 'Human Prophet',40,0,15,22,27,21,41,20,39,3,54,6,41,300,333,28,40,28,120,62500,1164.9, 53.40, 0.37, 582.4, 26.75, 0.22, 478.8, 26.10, 0.14,-90890,248027,-3570,0, 1.01, 0.872640, 7.5, 22.8, 1.01, 0.872640, 6.5, 22.5,425,461,6,5588, 0),
 (18, 'Elf Fighter',1,1,-1,36,36,35,23,14,26,4,80,6,41,300,333,36,46,36,125,73000,89.0, 12.74, 0.37, 35.6, 5.00, 0.22, 30.0, 5.46, 0.14,45978,41196,-3440,0, 1.15, 1.242000, 7.5, 24.0, 1.15, 1.242000, 7.5, 23.0,1147,1146,10,2369, 5588),
 (19, 'Elf Knight',20,1,18,36,36,35,23,14,26,4,80,6,41,300,333,36,46,36,125,73000,355.0, 33.00, 0.37, 177.5, 16.50, 0.22, 144.0, 9.90, 0.14,45978,41196,-3440,0, 1.15, 1.242000, 7.5, 24.0, 1.15, 1.242000, 7.5, 23.0,1147,1146,10,2369, 5588),
 (20, 'Temple Knight',40,1,19,36,36,35,23,14,26,4,80,6,41,300,333,36,46,36,125,73000,1072.0, 52.00, 0.37, 643.2, 31.20, 0.22, 359.1, 19.50, 0.14,45978,41196,-3440,0, 1.15, 1.242000, 7.5, 24.0, 1.15, 1.242000, 7.5, 23.0,1147,1146,10,2369, 5588),
 (21, 'Swordsinger',40,1,19,36,36,35,23,14,26,4,80,6,41,300,333,36,46,36,125,73000,1072.0, 54.60, 0.37, 536.0, 27.30, 0.22, 359.1, 19.50, 0.14,45978,41196,-3440,0, 1.15, 1.242000, 7.5, 24.0, 1.15, 1.242000, 7.5, 23.0,1147,1146,10,2369, 5588),
 (22, 'Scout',20,1,18,36,36,35,23,14,26,4,80,6,41,300,333,36,46,36,125,73000,355.0, 30.80, 0.37, 142.0, 12.32, 0.22, 144.0, 9.90, 0.14,45978,41196,-3440,0, 1.15, 1.242000, 7.5, 24.0, 1.15, 1.242000, 7.5, 23.0,1147,1146,10,2369, 5588),
 (23, 'Plains Walker',40,1,22,36,36,35,23,14,26,4,80,6,41,300,333,36,46,36,125,73000,1024.2, 46.80, 0.37, 409.6, 18.72, 0.22, 359.1, 19.50, 0.14,45978,41196,-3440,0, 1.15, 1.242000, 7.5, 24.0, 1.15, 1.242000, 7.5, 23.0,1147,1146,10,2369, 5588),
 (24, 'Silver Ranger',40,1,22,36,36,35,23,14,26,4,80,6,41,300,333,36,46,36,125,73000,1024.2, 49.40, 0.37, 512.1, 24.70, 0.22, 359.1, 19.50, 0.14,45978,41196,-3440,0, 1.15, 1.242000, 7.5, 24.0, 1.15, 1.242000, 7.5, 23.0,1147,1146,10,2369, 5588),
 (25, 'Elf Mage',1,1,-1,21,25,24,37,23,40,3,54,6,41,300,333,30,41,30,122,62400,104.0, 15.57, 0.37, 52.0, 7.84, 0.22, 40.0, 7.38, 0.14,46182,41198,-3440,0, 1.04, 0.898560, 7.5, 24.0, 1.04, 0.898560, 7.5, 23.0,425,461,6,5588, 0),
 (26, 'Elf Wizard',20,1,25,21,25,24,37,23,40,3,54,6,41,300,333,30,41,30,122,62400,427.0, 28.70, 0.37, 213.5, 14.40, 0.22, 192.0, 13.30, 0.14,46182,41198,-3440,0, 1.04, 0.898560, 7.5, 24.0, 1.04, 0.898560, 7.5, 23.0,425,461,6,5588, 0),
 (27, 'Spellsinger',40,1,26,21,25,24,37,23,40,3,54,6,41,300,333,30,41,30,122,62400,1048.4, 48.20, 0.37, 524.2, 24.15, 0.22, 478.8, 26.10, 0.14,46182,41198,-3440,0, 1.04, 0.898560, 7.5, 24.0, 1.04, 0.898560, 7.5, 23.0,425,461,6,5588, 0),
 (28, 'Elemental Summoner',40,1,26,21,25,24,37,23,40,3,54,6,41,300,333,30,41,30,122,62400,1048.4, 50.80, 0.37, 629.0, 30.52, 0.22, 478.8, 26.10, 0.14,46182,41198,-3440,0, 1.04, 0.898560, 7.5, 24.0, 1.04, 0.898560, 7.5, 23.0,425,461,6,5588, 0),
 (29, 'Oracle',20,1,25,21,25,24,37,23,40,3,54,6,41,300,333,30,41,30,122,62400,427.0, 35.30, 0.37, 213.5, 17.70, 0.22, 192.0, 13.30, 0.14,46182,41198,-3440,0, 1.04, 0.898560, 7.5, 24.0, 1.04, 0.898560, 7.5, 23.0,425,461,6,5588, 0),
 (30, 'Elder',40,1,29,21,25,24,37,23,40,3,54,6,41,300,333,30,41,30,122,62400,1191.8, 54.70, 0.37, 595.9, 27.40, 0.22, 478.8, 26.10, 0.14,46182,41198,-3440,0, 1.04, 0.898560, 7.5, 24.0, 1.04, 0.898560, 7.5, 23.0,425,461,6,5588, 0),
 (31, 'DE Fighter',1,2,-1,41,32,34,25,12,26,4,80,6,41,300,333,35,45,35,122,69000,94.0, 13.65, 0.37, 37.6, 5.46, 0.22, 30.0, 5.46, 0.14,28377,10916,-4224,0, 1.14, 1.231200, 7.5, 24.0, 1.14, 1.231200, 7.0, 23.5,1147,1146,10,2369, 5588),
 (32, 'Palus Knight',20,2,31,41,32,34,25,12,26,4,80,6,41,300,333,35,45,35,122,69000,379.0, 35.20, 0.37, 189.5, 17.60, 0.22, 144.0, 9.90, 0.14,28377,10916,-4224,0, 1.14, 1.231200, 7.5, 24.0, 1.14, 1.231200, 7.0, 23.5,1147,1146,10,2369, 5588),
 (33, 'Shillien Knight',40,2,32,41,32,34,25,12,26,4,80,6,41,300,333,35,45,35,122,69000,1143.8, 54.60, 0.37, 686.2, 32.76, 0.22, 359.1, 19.50, 0.14,28377,10916,-4224,0, 1.14, 1.231200, 7.5, 24.0, 1.14, 1.231200, 7.0, 23.5,1147,1146,10,2369, 5588),
 (34, 'Bladedancer',40,2,32,41,32,34,25,12,26,4,80,6,41,300,333,35,45,35,122,69000,1143.8, 58.50, 0.37, 571.9, 29.25, 0.22, 359.1, 19.50, 0.14,28377,10916,-4224,0, 1.14, 1.231200, 7.5, 24.0, 1.14, 1.231200, 7.0, 23.5,1147,1146,10,2369, 5588),
 (35, 'Assassin',20,2,31,41,32,34,25,12,26,4,80,6,41,300,333,35,45,35,122,69000,379.0, 33.00, 0.37, 151.6, 13.20, 0.22, 144.0, 9.90, 0.14,28377,10916,-4224,0, 1.14, 1.231200, 7.5, 24.0, 1.14, 1.231200, 7.0, 23.5,1147,1146,10,2369, 5588),
 (36, 'Abyss Walker',40,2,35,41,32,34,25,12,26,4,80,6,41,300,333,35,45,35,122,69000,1096.0, 49.40, 0.37, 438.4, 19.76, 0.22, 359.1, 19.50, 0.14,28377,10916,-4224,0, 1.14, 1.231200, 7.5, 24.0, 1.14, 1.231200, 7.0, 23.5,1147,1146,10,2369, 5588),
 (37, 'Phantom Ranger',40,2,35,41,32,34,25,12,26,4,80,6,41,300,333,35,45,35,122,69000,1096.0, 52.00, 0.37, 548.0, 26.00, 0.22, 359.1, 19.50, 0.14,28377,10916,-4224,0, 1.14, 1.231200, 7.5, 24.0, 1.14, 1.231200, 7.0, 23.5,1147,1146,10,2369, 5588),
 (38, 'DE Mage',1,2,-1,23,24,23,44,19,37,3,54,6,41,300,333,29,41,29,122,61000,106.0, 15.57, 0.37, 53.0, 7.84, 0.22, 40.0, 7.38, 0.14,28295,11063,-4224,0, 1.14, 1.231200, 7.5, 24.0, 1.03, 0.889920, 7.0, 23.5,425,461,6,5588, 0),
 (39, 'DE Wizard',20,2,38,23,24,23,44,19,37,3,54,6,41,300,333,29,41,29,122,61000,429.0, 29.80, 0.37, 214.5, 14.95, 0.22, 192.0, 13.30, 0.14,28295,11063,-4224,0, 1.14, 1.231200, 7.5, 24.0, 1.03, 0.889920, 7.0, 23.5,425,461,6,5588, 0),
 (40, 'Spell Howler',40,2,39,23,24,23,44,19,37,3,54,6,41,300,333,29,41,29,122,61000,1074.3, 48.20, 0.37, 537.1, 24.15, 0.22, 478.8, 26.10, 0.14,28295,11063,-4224,0, 1.14, 1.231200, 7.5, 24.0, 1.03, 0.889920, 7.0, 23.5,425,461,6,5588, 0),
 (41, 'Phantom Summoner',40,2,39,23,24,23,44,19,37,3,54,6,41,300,333,29,41,29,122,61000,1074.3, 52.10, 0.37, 644.5, 31.30, 0.22, 478.8, 26.10, 0.14,28295,11063,-4224,0, 1.14, 1.231200, 7.5, 24.0, 1.03, 0.889920, 7.0, 23.5,425,461,6,5588, 0),
 (42, 'Shillien Oracle',20,2,38,23,24,23,44,19,37,3,54,6,41,300,333,29,41,29,122,61000,429.0, 36.40, 0.37, 214.5, 18.25, 0.22, 192.0, 13.30, 0.14,28295,11063,-4224,0, 1.14, 1.231200, 7.5, 24.0, 1.03, 0.889920, 7.0, 23.5,425,461,6,5588, 0),
 (43, 'Shillien Elder',40,2,42,23,24,23,44,19,37,3,54,6,41,300,333,29,41,29,122,61000,1217.7, 54.70, 0.37, 608.8, 27.40, 0.22, 478.8, 26.10, 0.14,28295,11063,-4224,0, 1.14, 1.231200, 7.5, 24.0, 1.03, 0.889920, 7.0, 23.5,425,461,6,5588, 0),
 (44, 'Orc Fighter',1,3,-1,40,47,26,18,12,27,4,80,6,41,300,333,31,42,31,117,87000,80.0, 12.64, 0.37, 40.0, 6.27, 0.22, 30.0, 5.36, 0.14,-56693,-113610,-690,0, 1.06, 1.144800, 11.0, 28.0, 1.06, 1.144800, 7.0, 27.0,1147,1146,2368,2369, 5588),
 (45, 'Raider',20,3,44,40,47,26,18,12,27,4,80,6,41,300,333,31,42,31,117,87000,346.0, 35.10, 0.37, 242.2, 24.54, 0.22, 144.0, 9.80, 0.14,-56693,-113610,-690,0, 1.06, 1.144800, 11.0, 28.0, 1.06, 1.144800, 7.0, 27.0,1147,1146,2368,2369, 5588),
 (46, 'Destroyer',40,3,45,40,47,26,18,12,27,4,80,6,41,300,333,31,42,31,117,87000,1110.8, 57.10, 0.37, 777.5, 39.94, 0.22, 359.1, 19.40, 0.14,-56693,-113610,-690,0, 1.06, 1.144800, 11.0, 28.0, 1.06, 1.144800, 7.0, 27.0,1147,1146,2368,2369, 5588),
 (47, 'Monk',20,3,44,40,47,26,18,12,27,4,80,6,41,300,333,31,42,31,117,87000,346.0, 32.90, 0.37, 173.0, 16.40, 0.22, 144.0, 9.80, 0.14,-56682,-113610,-690,0, 1.06, 1.144800, 11.0, 28.0, 1.06, 1.144800, 7.0, 27.0,1147,1146,2368,2369, 5588),
 (48, 'Tyrant',40,3,47,40,47,26,18,12,27,4,80,6,41,300,333,31,42,31,117,87000,1063.0, 54.50, 0.37, 531.5, 27.20, 0.22, 359.1, 19.40, 0.14,-56693,-113610,-690,0, 1.06, 1.144800, 11.0, 28.0, 1.06, 1.144800, 7.0, 27.0,1147,1146,2368,2369, 5588),
 (49, 'Orc Mage',1,3,-1,27,31,24,31,15,42,3,54,6,41,300,333,30,41,30,121,68000,95.0, 15.47, 0.37, 47.5, 7.74, 0.22, 40.0, 7.28, 0.14,-56682,-113730,-690,0, 1.04, 0.898560, 7.0, 27.5, 1.04, 0.898560, 8.0, 25.5,425,461,2368,5588, 0),
 (50, 'Shaman',20,3,49,27,31,24,31,15,42,3,54,6,41,300,333,30,41,30,121,68000,418.0, 35.20, 0.37, 209.0, 17.60, 0.22, 192.0, 13.20, 0.14,-56682,-113730,-690,0, 1.04, 0.898560, 7.0, 27.5, 1.04, 0.898560, 8.0, 25.5,425,461,2368,5588, 0),
 (51, 'Overlord',40,3,50,27,31,24,31,15,42,3,54,6,41,300,333,30,41,30,121,68000,1182.8, 53.30, 0.37, 946.2, 42.64, 0.22, 478.8, 26.00, 0.14,-56682,-113730,-690,0, 1.04, 0.898560, 7.0, 27.5, 1.04, 0.898560, 8.0, 25.5,425,461,2368,5588, 0),
 (52, 'Warcryer',40,3,50,27,31,24,31,15,42,3,54,6,41,300,333,30,41,30,121,68000,1182.8, 53.30, 0.37, 591.4, 26.65, 0.22, 478.8, 26.00, 0.14,-56682,-113730,-690,0, 1.04, 0.898560, 7.0, 27.5, 1.04, 0.898560, 8.0, 25.5,425,461,2368,5588, 0),
 (53, 'Dwarf Fighter',1,4,-1,39,45,29,20,10,27,4,80,6,41,300,333,33,43,33,115,83000,80.0, 12.64, 0.37, 56.0, 8.82, 0.22, 30.0, 5.36, 0.14,108512,-174026,-400,1, 1.09, 1.487196, 9.0, 18.0, 1.09, 1.487196, 5.0, 19.0,1147,1146,10,2370, 5588),
 (54, 'Scavenger',20,4,53,39,45,29,20,10,27,4,80,6,41,300,333,33,43,33,115,83000,346.0, 35.10, 0.37, 242.2, 24.54, 0.22, 144.0, 9.80, 0.14,108512,-174026,-400,1, 1.09, 1.487196, 9.0, 18.0, 1.09, 1.487196, 5.0, 19.0,1147,1146,10,2370, 5588),
 (55, 'Bounty Hunter',40,4,54,39,45,29,20,10,27,4,80,6,41,300,333,33,43,33,115,83000,1110.8, 57.10, 0.37, 777.5, 39.94, 0.22, 359.1, 19.40, 0.14,108512,-174026,-400,1, 1.09, 1.487196, 9.0, 18.0, 1.09, 1.487196, 5.0, 19.0,1147,1146,10,2370, 5588),
 (56, 'Artisan',20,4,53,39,45,29,20,10,27,4,80,6,41,300,333,33,43,33,115,83000,346.0, 32.90, 0.37, 276.8, 26.30, 0.22, 144.0, 9.80, 0.14,108512,-174026,-400,1, 1.09, 1.487196, 9.0, 18.0, 1.09, 1.487196, 5.0, 19.0,1147,1146,10,2370, 5588),
 (57, 'Warsmith',40,4,56,39,45,29,20,10,27,4,80,6,41,300,333,33,43,33,115,83000,1063.0, 54.50, 0.37, 850.4, 43.58, 0.22, 359.1, 19.40, 0.14,108512,-174026,-400,1, 1.09, 1.487196, 9.0, 18.0, 1.09, 1.487196, 5.0, 19.0,1147,1146,10,2370, 5588),
 (88, 'Duelist',76,0,2,40,43,30,21,11,25,4,80,6,41,300,333,33,44,33,115,81900,3061.8, 63.08, 0.37, 2755.6, 56.77, 0.22, 1155.6, 24.90, 0.14 ,-71338,258271,-3104,0, 1.10, 1.188000, 9.0, 23.0, 1.10, 1.188000, 8.0, 23.5,1147,1146,10,2369, 5588),
 (89, 'DreadNought',76,0,3,40,43,30,21,11,25,4,80,6,41,300,333,33,44,33,115,81900,3274.2, 69.72, 0.37, 2619.3, 55.78, 0.22, 1155.6, 24.90, 0.14 ,-71338,258271,-3104,0, 1.10, 1.188000, 9.0, 23.0, 1.10, 1.188000, 8.0, 23.5,1147,1146,10,2369, 5588),
 (90, 'Phoenix Knight',76,0,5,40,43,30,21,11,25,4,80,6,41,300,333,33,44,33,115,81900,2883.9, 59.76, 0.37, 1730.3, 35.86, 0.22, 1155.6, 24.90, 0.14 ,-71338,258271,-3104,0, 1.10, 1.188000, 9.0, 23.0, 1.10, 1.188000, 8.0, 23.5,1147,1146,10,2369, 5588),
 (91, 'Hell Knight',76,0,6,40,43,30,21,11,25,4,80,6,41,300,333,33,44,33,115,81900,2883.9, 59.76, 0.37, 1730.3, 35.86, 0.22, 1155.6, 24.90, 0.14 ,-71338,258271,-3104,0, 1.10, 1.188000, 9.0, 23.0, 1.10, 1.188000, 8.0, 23.5,1147,1146,10,2369, 5588),
 (92, 'Sagittarius',76,0,9,40,43,30,21,11,25,4,80,6,41,300,333,33,44,33,115,81900,2729.9, 56.44, 0.37, 1910.9, 39.51, 0.22, 1155.6, 24.90, 0.14 ,-71338,258271,-3104,0, 1.10, 1.188000, 9.0, 23.0, 1.10, 1.188000, 8.0, 23.5,1147,1146,10,2369, 5588),
 (93, 'Adventurer',76,0,8,40,43,30,21,11,25,4,80,6,41,300,333,33,44,33,115,81900,2623.7, 53.12, 0.37, 1049.4, 21.25, 0.22, 1155.6, 24.90, 0.14 ,-71338,258271,-3104,0, 1.10, 1.188000, 9.0, 23.0, 1.10, 1.188000, 8.0, 23.5,1147,1146,10,2369, 5588),
 (94, 'Archmage',76,0,12,22,27,21,41,20,39,3,54,6,41,300,333,28,40,28,120,62500,2880.0, 58.10, 0.37, 1440.0, 29.05, 0.22, 1540.8, 33.20, 0.14 ,-90890,248027,-3570,0, 1.01, 0.872640, 7.5, 22.8, 1.01, 0.872640, 6.5, 22.5,425,461,6,5588, 0),
 (95, 'Soultaker',76,0,13,22,27,21,41,20,39,3,54,6,41,300,333,28,40,28,120,62500,2880.0, 58.10, 0.37, 1440.0, 29.05, 0.22, 1540.8, 33.20, 0.14 ,-90890,248027,-3570,0, 1.01, 0.872640, 7.5, 22.8, 1.01, 0.872640, 6.5, 22.5,425,461,6,5588, 0),
 (96, 'Arcana Lord',76,0,14,22,27,21,41,20,39,3,54,6,41,300,333,28,40,28,120,62500,3039.3, 63.08, 0.37, 1823.5, 37.85, 0.22, 1540.8, 33.20, 0.14 ,-90890,248027,-3570,0, 1.01, 0.872640, 7.5, 22.8, 1.01, 0.872640, 6.5, 22.5,425,461,6,5588, 0),
 (97, 'Cardinal',76,0,16,22,27,21,41,20,39,3,54,6,41,300,333,28,40,28,120,62500,3182.7, 63.08, 0.37, 2227.8, 44.16, 0.22, 1540.8, 33.20, 0.14 ,-90890,248027,-3570,0, 1.01, 0.872640, 7.5, 22.8, 1.01, 0.872640, 6.5, 22.5,425,461,6,5588, 0),
 (98, 'Hierophant',76,0,17,22,27,21,41,20,39,3,54,6,41,300,333,28,40,28,120,62500,3342.0, 68.06, 0.37, 1671.0, 34.03, 0.22, 1540.8, 33.20, 0.14 ,-90890,248027,-3570,0, 1.01, 0.872640, 7.5, 22.8, 1.01, 0.872640, 6.5, 22.5,425,461,6,5588, 0),
 (99, 'Eva Templar',76,1,20,36,36,35,23,14,26,4,80,6,41,300,333,36,46,36,125,73000,3196.0, 66.40, 0.37, 1917.6, 39.84, 0.22, 1155.6, 24.90, 0.14 ,45978,41196,-3440,0, 1.15, 1.242000, 7.5, 24.0, 1.15, 1.242000, 7.5, 23.0,1147,1146,10,2369, 5588),
 (100, 'Sword Muse',76,1,21,36,36,35,23,14,26,4,80,6,41,300,333,36,46,36,125,73000,3302.2, 69.72, 0.37, 1651.1, 34.86, 0.22, 1155.6, 24.90, 0.14 ,45978,41196,-3440,0, 1.15, 1.242000, 7.5, 24.0, 1.15, 1.242000, 7.5, 23.0,1147,1146,10,2369, 5588),
 (101, 'Wind Rider',76,1,23,36,36,35,23,14,26,4,80,6,41,300,333,36,46,36,125,73000,2935.8, 59.76, 0.37, 1174.3, 23.90, 0.22, 1155.6, 24.90, 0.14 ,45978,41196,-3440,0, 1.15, 1.242000, 7.5, 24.0, 1.15, 1.242000, 7.5, 23.0,1147,1146,10,2369, 5588),
 (102, 'Moonlight Sentinel',76,1,24,36,36,35,23,14,26,4,80,6,41,300,333,36,46,36,125,73000,3042.0, 63.08, 0.37, 1521.0, 31.54, 0.22, 1155.6, 24.90, 0.14 ,45978,41196,-3440,0, 1.15, 1.242000, 7.5, 24.0, 1.15, 1.242000, 7.5, 23.0,1147,1146,10,2369, 5588),
 (103, 'Mystic Muse',76,1,27,21,25,24,37,23,40,3,54,6,41,300,333,30,41,30,122,62400,3013.1, 61.42, 0.37, 1506.5, 30.71, 0.22, 1540.8, 33.20, 0.14 ,46182,41198,-3440,0, 1.04, 0.898560, 7.5, 24.0, 1.04, 0.898560, 7.5, 23.0,425,461,6,5588, 0),
 (104, 'Elemental Master',76,1,28,21,25,24,37,23,40,3,54,6,41,300,333,30,41,30,122,62400,3119.3, 64.74, 0.37, 1871.5, 38.84, 0.22, 1540.8, 33.20, 0.14 ,46182,41198,-3440,0, 1.04, 0.898560, 7.5, 24.0, 1.04, 0.898560, 7.5, 23.0,425,461,6,5588, 0),
 (105, 'Eva Saint',76,1,30,21,25,24,37,23,40,3,54,6,41,300,333,30,41,30,122,62400,3422.0, 69.72, 0.37, 1711.0, 34.86, 0.22, 1540.8, 33.20, 0.14 ,46182,41198,-3440,0, 1.04, 0.898560, 7.5, 24.0, 1.04, 0.898560, 7.5, 23.0,425,461,6,5588, 0),
 (106, 'Shillien Templar',76,2,33,41,32,34,25,12,26,4,80,6,41,300,333,35,45,35,122,69000,3374.0, 69.72, 0.37, 2024.4, 41.83, 0.22, 1155.6, 24.90, 0.14 ,28377,10916,-4224,0, 1.14, 1.231200, 7.5, 24.0, 1.14, 1.231200, 7.0, 23.5,1147,1146,10,2369, 5588),
 (107, 'Spectral Dancer',76,2,34,41,32,34,25,12,26,4,80,6,41,300,333,35,45,35,122,69000,3533.3, 74.70, 0.37, 1766.6, 37.35, 0.22, 1155.6, 24.90, 0.14 ,28377,10916,-4224,0, 1.14, 1.231200, 7.5, 24.0, 1.14, 1.231200, 7.0, 23.5,1147,1146,10,2369, 5588),
 (108, 'Ghost Hunter',76,2,36,41,32,34,25,12,26,4,80,6,41,300,333,35,45,35,122,69000,3113.8, 63.08, 0.37, 1245.5, 25.23, 0.22, 1155.6, 24.90, 0.14 ,28377,10916,-4224,0, 1.14, 1.231200, 7.5, 24.0, 1.14, 1.231200, 7.0, 23.5,1147,1146,10,2369, 5588),
 (109, 'Ghost Sentinel',76,2,37,41,32,34,25,12,26,4,80,6,41,300,333,35,45,35,122,69000,3220.0, 66.40, 0.37, 1610.0, 33.20, 0.22, 1155.6, 24.90, 0.14 ,28377,10916,-4224,0, 1.14, 1.231200, 7.5, 24.0, 1.14, 1.231200, 7.0, 23.5,1147,1146,10,2369, 5588),
 (110, 'Storm Screamer',76,2,40,23,24,23,44,19,37,3,54,6,41,300,333,29,41,29,122,61000,3039.0, 61.42, 0.37, 1519.5, 30.71, 0.22, 1540.8, 33.20, 0.14 ,28295,11063,-4224,0, 1.14, 1.231200, 7.5, 24.0, 1.03, 0.889920, 7.0, 23.5,425,461,6,5588, 0),
 (111, 'Spectral Master',76,2,41,23,24,23,44,19,37,3,54,6,41,300,333,29,41,29,122,61000,3198.3, 66.40, 0.37, 1918.9, 39.84, 0.22, 1540.8, 33.20, 0.14 ,28295,11063,-4224,0, 1.14, 1.231200, 7.5, 24.0, 1.03, 0.889920, 7.0, 23.5,425,461,6,5588, 0),
 (112, 'Shillen Saint',76,2,43,23,24,23,44,19,37,3,54,6,41,300,333,29,41,29,122,61000,3447.9, 69.72, 0.37, 1723.9, 34.86, 0.22, 1540.8, 33.20, 0.14 ,28295,11063,-4224,0, 1.14, 1.231200, 7.5, 24.0, 1.03, 0.889920, 7.0, 23.5,425,461,6,5588, 0),
 (113, 'Titan',76,3,46,40,47,26,18,12,27,4,80,6,41,300,333,31,42,31,117,87000,3447.2, 72.94, 0.37, 2413.0, 51.03, 0.22, 1155.6, 24.80, 0.14 ,-56693,-113610,-690,0, 1.06, 1.144800, 11.0, 28.0, 1.06, 1.144800, 7.0, 27.0,1147,1146,2368,2369, 5588),
 (114, 'Grand Khauatari',76,3,48,40,47,26,18,12,27,4,80,6,41,300,333,31,42,31,117,87000,3293.2, 69.62, 0.37, 1646.6, 34.76, 0.22, 1155.6, 24.80, 0.14 ,-56693,-113610,-690,0, 1.06, 1.144800, 11.0, 28.0, 1.06, 1.144800, 7.0, 27.0,1147,1146,2368,2369, 5588),
 (115, 'Dominator',76,3,51,27,31,24,31,15,42,3,54,6,41,300,333,30,41,30,121,68000,3359.9, 67.96, 0.37, 2687.9, 54.35, 0.22, 1540.8, 33.10, 0.14 ,-56682,-113730,-690,0, 1.04, 0.898560, 7.0, 27.5, 1.04, 0.898560, 8.0, 25.5,425,461,2368,5588, 0),
 (116, 'Doomcryer',76,3,52,27,31,24,31,15,42,3,54,6,41,300,333,30,41,30,121,68000,3359.9, 67.96, 0.37, 1679.9, 33.93, 0.22, 1540.8, 33.10, 0.14 ,-56682,-113730,-690,0, 1.04, 0.898560, 7.0, 27.5, 1.04, 0.898560, 8.0, 25.5,425,461,2368,5588, 0),
 (117, 'Fortune Seeker',76,4,55,39,45,29,20,10,27,4,80,6,41,300,333,33,43,33,115,83000,3447.2, 72.94, 0.37, 2413.0, 51.03, 0.22, 1155.6, 24.80, 0.14 ,108512,-174026,-400,1, 1.09, 1.487196, 9.0, 18.0, 1.09, 1.487196, 5.0, 19.0,1147,1146,10,2370, 5588),
 (118, 'Maestro',76,4,57,39,45,29,20,10,27,4,80,6,41,300,333,33,43,33,115,83000,3293.2, 69.62, 0.37, 2634.5, 55.68, 0.22, 1155.6, 24.80, 0.14,108512,-174026,-400,1, 1.09, 1.487196, 9.0, 18.0, 1.09, 1.487196, 5.0, 19.0,1147,1146,10,2370, 5588);