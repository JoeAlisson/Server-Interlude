-- ---------------------------
-- Table structure for characters
-- ---------------------------
CREATE TABLE IF NOT EXISTS characters (
  account varchar(45) NOT NULL,
  object_id decimal(11,0) NOT NULL default '0',
  name varchar(35) NOT NULL,
  `level` decimal(11,0) default 1,
  hp decimal(18,0) default NULL,
  cp decimal(18,0) default NULL,
  mp decimal(18,0) default NULL,
  face decimal(1, 0) default NULL,
  hair_style decimal(1,0) default NULL,
  hair_color decimal(1,0) default NULL,
  sex decimal(11,0) default NULL,
  heading decimal(11,0) default NULL,
  x decimal(11,0) default NULL,
  y decimal(11,0) default NULL,
  z decimal(11,0) default NULL,
  exp decimal(20,0) default NULL,
  exp_before_death decimal(20,0) default 0,
  sp decimal(20,0) default NULL,
  karma decimal(11,0) default NULL,
  pvp decimal(11,0) default NULL,
  pk decimal(11,0) default NULL,
  clan decimal(11,0) default NULL,
  race decimal(11,0) default NULL,
  class_id decimal(11,0) default NULL,
  base_class int(2) NOT NULL default '0',
  delete_time decimal(20,0) default NULL,
  title varchar(16) default NULL,
  rec_have int(3) NOT NULL default '0',
  rec_left int(3) NOT NULL default '0',
  access_level decimal(4,0) default NULL,
  online BOOL default NULL,
  online_time decimal(20,0) default NULL,
  slot decimal(1) default NULL,
  newbie bool default false,
  last_access decimal(20,0) default NULL,
  clan_privs INT DEFAULT 0,
  wants_peace BOOL DEFAULT false,
  in_seven_signs BOOL NOT NULL default 0,
  in_jail BOOL DEFAULT false,
  jail_timer decimal(20,0) DEFAULT 0,
  power_grade decimal(11,0) DEFAULT NULL,
  nobless BOOL NOT NULL DEFAULT FALSE,
  subpledge DECIMAL(1,0) NOT NULL DEFAULT 0,
  last_recom_date decimal(20,0) NOT NULL DEFAULT 0,
  lvl_joined_academy decimal(3, 0) NOT NULL DEFAULT 0,
  apprentice int NOT NULL DEFAULT 0,
  sponsor int NOT NULL DEFAULT 0,
  varka_ketra_ally int(1) NOT NULL DEFAULT 0,
  clan_join_expiry_time DECIMAL(20,0) NOT NULL DEFAULT 0,
  clan_create_expiry_time DECIMAL(20,0) NOT NULL DEFAULT 0,
  death_penalty_level int(2) NOT NULL DEFAULT 0,
  PRIMARY KEY  (object_id),
  INDEX (clan, online),
  INDEX (name),
  INDEX (account, object_id),
  INDEX (access_level)

) ;
