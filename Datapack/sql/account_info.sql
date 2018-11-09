CREATE TABLE IF NOT EXISTS `account_info` (
  `account` VARCHAR(32) NOT NULL,
  `2nd_password` VARCHAR(44) NOT NULL,
  PRIMARY KEY (`account`))
ENGINE = InnoDB