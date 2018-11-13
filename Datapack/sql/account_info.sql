CREATE TABLE IF NOT EXISTS `account_info` (
  `account` VARCHAR(32) NOT NULL,
  `2nd_password` VARCHAR(44) NOT NULL,
  attemps INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`account`))
ENGINE = InnoDB