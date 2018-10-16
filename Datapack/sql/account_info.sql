CREATE TABLE IF NOT EXISTS `accounts_info` (
  `login` VARCHAR(32) NOT NULL,
  `2nd_password` VARCHAR(44) NOT NULL,
  PRIMARY KEY (`login`))
ENGINE = InnoDB