--
-- Table structure for table `henna`
--
DROP TABLE IF EXISTS henna;
CREATE TABLE henna (
  symbol_id int(11) NOT NULL default '0',
  symbol_name varchar(45) default NULL,
  dye_id int(11) default NULL,
  dye_amount int(11) default NULL,
  price int(11) default NULL,
  stat_INT decimal(11,0) default NULL,
  stat_STR decimal(11,0) default NULL,
  stat_CON decimal(11,0) default NULL,
  stat_MEM decimal(11,0) default NULL,
  stat_DEX decimal(11,0) default NULL,
  stat_WIT decimal(11,0) default NULL,
  PRIMARY KEY  (symbol_id)
);

--
-- Dumping data for table `henna`
--
INSERT INTO henna VALUES
(1, 'STR+1, CON-3', 4445, 10, 5100, '0', '1', '-3', '0', '0', '0'), 
(2, 'STR+1, DEX-3', 4446, 10, 5100, '0', '1', '0', '0', '-3', '0'), 
(3, 'CON+1, STR-3', 4447, 10, 5100, '0', '-3', '1', '0', '0', '0'), 
(4, 'CON+1, DEX-3', 4448, 10, 5100, '0', '0', '1', '0', '-3', '0'), 
(5, 'DEX+1, STR-3', 4449, 10, 5100, '0', '-3', '0', '0', '1', '0'), 
(6, 'DEX+1, CON-3', 4450, 10, 5100, '0', '0', '-3', '0', '1', '0'), 
(7, 'INT+1, MEN-3', 4451, 10, 5100, '1', '0', '0', '-3', '0', '0'), 
(8, 'INT+1, WIT-3', 4452, 10, 5100, '1', '0', '0', '0', '0', '-3'), 
(9, 'MEN+1, INT-3', 4453, 10, 5100, '-3', '0', '0', '1', '0', '0'), 
(10, 'MEN+1, WIT-3', 4454, 10, 5100, '0', '0', '0', '1', '0', '-3'), 
(11, 'WIT+1, INT-3', 4455, 10, 5100, '-3', '0', '0', '0', '0', '1'), 
(12, 'WIT+1, MEN-3', 4456, 10, 5100, '0', '0', '0', '-3', '0', '1'), 
(13, 'STR+1, CON-2', 4457, 10, 12000, '0', '1', '-2', '0', '0', '0'), 
(14, 'STR+1, DEX-2', 4458, 10, 12000, '0', '1', '0', '0', '-2', '0'), 
(15, 'CON+1, STR-2', 4459, 10, 12000, '0', '-2', '1', '0', '0', '0'), 
(16, 'CON+1, DEX-2', 4460, 10, 12000, '0', '0', '1', '0', '-2', '0'), 
(17, 'DEX+1, STR-2', 4461, 10, 12000, '0', '-2', '0', '0', '1', '0'), 
(18, 'DEX+1, CON-2', 4462, 10, 12000, '0', '0', '-2', '0', '1', '0'), 
(19, 'INT+1, MEN-2', 4463, 10, 12000, '1', '0', '0', '-2', '0', '0'), 
(20, 'INT+1, WIT-2', 4464, 10, 12000, '1', '0', '0', '0', '0', '-2'), 
(21, 'MEN+1, INT-2', 4465, 10, 12000, '-2', '0', '0', '1', '0', '0'), 
(22, 'MEN+1, WIT-2', 4466, 10, 12000, '0', '0', '0', '1', '0', '-2'), 
(23, 'WIT+1, INT-2', 4467, 10, 12000, '-2', '0', '0', '0', '0', '1'), 
(24, 'WIT+1, MEN-2', 4468, 10, 12000, '0', '0', '0', '-2', '0', '1'), 
(25, 'STR+1, CON-1', 4469, 10, 35000, '0', '1', '-1', '0', '0', '0'), 
(26, 'STR+1, DEX-1', 4470, 10, 35000, '0', '1', '0', '0', '-1', '0'), 
(27, 'CON+1, STR-1', 4471, 10, 35000, '0', '-1', '1', '0', '0', '0'), 
(28, 'CON+1, DEX-1', 4472, 10, 35000, '0', '0', '1', '0', '-1', '0'), 
(29, 'DEX+1, STR-1', 4473, 10, 35000, '0', '-1', '0', '0', '1', '0'), 
(30, 'DEX+1, CON-1', 4474, 10, 35000, '0', '0', '-1', '0', '1', '0'), 
(31, 'INT+1, MEN-1', 4475, 10, 35000, '1', '0', '0', '-1', '0', '0'), 
(32, 'INT+1, WIT-1', 4476, 10, 35000, '1', '0', '0', '0', '0', '-1'), 
(33, 'MEN+1, INT-1', 4477, 10, 35000, '-1', '0', '0', '1', '0', '0'), 
(34, 'MEN+1, WIT-1', 4478, 10, 35000, '0', '0', '0', '1', '0', '-1'), 
(35, 'WIT+1, INT-1', 4479, 10, 35000, '-1', '0', '0', '0', '0', '1'), 
(36, 'WIT+1, MEN-1', 4480, 10, 35000, '0', '0', '0', '-1', '0', '1'), 
(37, 'STR+1, CON-3', 4481, 10, 12000, '0', '1', '-3', '0', '0', '0'), 
(38, 'STR+1, DEX-3', 4482, 10, 24600, '0', '1', '0', '0', '-3', '0'), 
(39, 'CON+1, STR-3', 4483, 10, 24600, '0', '-3', '1', '0', '0', '0'), 
(40, 'CON+1, DEX-3', 4484, 10, 24600, '0', '0', '1', '0', '-3', '0'), 
(41, 'DEX+1, STR-3', 4485, 10, 30000, '0', '-3', '0', '0', '1', '0'), 
(42, 'DEX+1, CON-3', 4486, 10, 30000, '0', '0', '-3', '0', '1', '0'), 
(43, 'INT+1, MEN-3', 4487, 10, 30000, '1', '0', '0', '-3', '0', '0'), 
(44, 'INT+1, WIT-3', 4488, 10, 30000, '1', '0', '0', '0', '0', '-3'), 
(45, 'MEN+1, INT-3', 4489, 10, 30000, '-3', '0', '0', '1', '0', '0'), 
(46, 'MEN+1, WIT-3', 4490, 10, 12000, '0', '0', '0', '1', '0', '-3'), 
(47, 'WIT+1, INT-3', 4491, 10, 30000, '-3', '0', '0', '0', '0', '1'), 
(48, 'WIT+1, MEN-3', 4492, 10, 12000, '0', '0', '0', '-3', '0', '1'), 
(49, 'STR+1, CON-2', 4493, 10, 24600, '0', '1', '-2', '0', '0', '0'), 
(50, 'STR+1, DEX-2', 4494, 10, 30000, '0', '1', '0', '0', '-2', '0'), 
(51, 'CON+1, STR-2', 4495, 10, 35000, '0', '-2', '1', '0', '0', '0'), 
(52, 'CON+1, DEX-2', 4496, 10, 35000, '0', '0', '1', '0', '-2', '0'), 
(53, 'DEX+1, STR-2', 4497, 10, 36000, '0', '-2', '0', '0', '1', '0'), 
(54, 'DEX+1, CON-2', 4498, 10, 36000, '0', '0', '-2', '0', '1', '0'), 
(55, 'INT+1, MEN-2', 4499, 10, 50000, '1', '0', '0', '-2', '0', '0'), 
(56, 'INT+1, WIT-2', 4500, 10, 36000, '1', '0', '0', '0', '0', '-2'), 
(57, 'MEN+1, INT-2', 4501, 10, 36000, '-2', '0', '0', '1', '0', '0'), 
(58, 'MEN+1, WIT-2', 4502, 10, 21000, '0', '0', '0', '1', '0', '-2'), 
(59, 'WIT+1, INT-2', 4503, 10, 30000, '-2', '0', '0', '0', '0', '1'), 
(60, 'WIT+1, MEN-2', 4504, 10, 36000, '0', '0', '0', '-2', '0', '1'), 
(61, 'STR+2, CON-4', 4505, 10, 24600, '0', '2', '-4', '0', '0', '0'), 
(62, 'STR+2, DEX-4', 4506, 10, 24600, '0', '2', '0', '0', '-4', '0'), 
(63, 'CON+2, STR-4', 4507, 10, 24600, '0', '-4', '2', '0', '0', '0'), 
(64, 'CON+2, DEX-4', 4508, 10, 24600, '0', '0', '2', '0', '-4', '0'), 
(65, 'DEX+2, STR-4', 4509, 10, 24600, '0', '-4', '0', '0', '2', '0'), 
(66, 'DEX+2, CON-4', 4510, 10, 24600, '0', '0', '-4', '0', '2', '0'), 
(67, 'INT+2, MEN-4', 4511, 10, 24600, '2', '0', '0', '-4', '0', '0'), 
(68, 'INT+2, WIT-4', 4512, 10, 24600, '2', '0', '0', '0', '0', '-4'), 
(69, 'MEN+2, INT-4', 4513, 10, 24600, '-4', '0', '0', '2', '0', '0'), 
(70, 'MEN+2, WIT-4', 4514, 10, 30000, '0', '0', '0', '2', '0', '-4'), 
(71, 'WIT+2, INT-4', 4515, 10, 30000, '-4', '0', '0', '0', '0', '2'), 
(72, 'WIT+2, MEN-4', 4516, 10, 30000, '0', '0', '0', '-4', '0', '2'), 
(73, 'STR+2, CON-3', 4517, 10, 30000, '0', '2', '-3', '0', '0', '0'), 
(74, 'STR+2, DEX-3', 4518, 10, 35000, '0', '2', '0', '0', '-3', '0'), 
(75, 'CON+2, STR-3', 4519, 10, 35000, '0', '-3', '2', '0', '0', '0'), 
(76, 'CON+2, DEX-3', 4520, 10, 35000, '0', '0', '2', '0', '-3', '0'), 
(77, 'DEX+2, STR-3', 4521, 10, 27000, '0', '-3', '0', '0', '2', '0'), 
(78, 'DEX+2, CON-3', 4522, 10, 27000, '0', '0', '-3', '0', '2', '0'), 
(79, 'INT+2, MEN-3', 4523, 10, 27000, '2', '0', '0', '-3', '0', '0'), 
(80, 'INT+2, WIT-3', 4524, 10, 30000, '2', '0', '0', '0', '0', '-3'), 
(81, 'MEN+2, INT-3', 4525, 10, 30000, '-3', '0', '0', '2', '0', '0'), 
(82, 'MEN+2, WIT-3', 4526, 10, 30000, '0', '0', '0', '2', '0', '-3'), 
(83, 'WIT+2, INT-3', 4527, 10, 30000, '-3', '0', '0', '0', '0', '2'), 
(84, 'WIT+2, MEN-3', 4528, 10, 30000, '0', '0', '0', '-3', '0', '2'), 
(85, 'STR+3, CON-5', 4529, 10, 30000, '0', '3', '-5', '0', '0', '0'), 
(86, 'STR+3, DEX-5', 4530, 10, 30000, '0', '3', '0', '0', '-5', '0'), 
(87, 'CON+3, STR-5', 4531, 10, 30000, '0', '-5', '3', '0', '0', '0'), 
(88, 'CON+3, DEX-5', 4532, 10, 30000, '0', '0', '3', '0', '-5', '0'), 
(89, 'DEX+3, STR-5', 4533, 10, 30000, '0', '-5', '0', '0', '3', '0'), 
(90, 'DEX+3, CON-5', 4534, 10, 30000, '0', '0', '-5', '0', '3', '0'), 
(91, 'INT+3, MEN-5', 4535, 10, 30000, '3', '0', '0', '-5', '0', '0'), 
(92, 'INT+3, WIT-5', 4536, 10, 30000, '3', '0', '0', '0', '0', '-5'), 
(93, 'MEN+3, INT-5', 4537, 10, 30000, '-5', '0', '0', '3', '0', '0'), 
(94, 'MEN+3, WIT-5', 4538, 10, 30000, '0', '0', '0', '3', '0', '-5'), 
(95, 'WIT+3, INT-5', 4539, 10, 30000, '-5', '0', '0', '0', '0', '3'), 
(96, 'WIT+3, MEN-5', 4540, 10, 30000, '0', '0', '0', '-5', '0', '3'), 
(97, 'STR+3, CON-4', 4541, 10, 30000, '0', '3', '-4', '0', '0', '0'), 
(98, 'STR+3, DEX-4', 4542, 10, 30000, '0', '3', '0', '0', '-4', '0'), 
(99, 'CON+3, STR-4', 4543, 10, 50000, '0', '-4', '3', '0', '0', '0'), 
(100, 'CON+3, DEX-4', 4544, 10, 50000, '0', '0', '3', '0', '-4', '0'), 
(101, 'DEX+3, STR-4', 4545, 10, 50000, '0', '-4', '0', '0', '3', '0'), 
(102, 'DEX+3, CON-4', 4546, 10, 50000, '0', '0', '-4', '0', '3', '0'), 
(103, 'INT+3, MEN-4', 4547, 10, 50000, '3', '0', '0', '-4', '0', '0'), 
(104, 'INT+3, WIT-4', 4548, 10, 50000, '3', '0', '0', '0', '0', '-4'), 
(105, 'MEN+3, INT-4', 4549, 10, 50000, '-4', '0', '0', '3', '0', '0'), 
(106, 'MEN+3, WIT-4', 4550, 10, 50000, '0', '0', '0', '3', '0', '-4'), 
(107, 'WIT+3, INT-4', 4551, 10, 50000, '-4', '0', '0', '0', '0', '3'), 
(108, 'WIT+3, MEN-4', 4552, 10, 50000, '0', '0', '0', '-4', '0', '3'), 
(109, 'STR+4, CON-6', 4565, 10, 36000, '0', '4', '-6', '0', '0', '0'), 
(110, 'STR+4, DEX-6', 4566, 10, 36000, '0', '4', '0', '0', '-6', '0'), 
(111, 'CON+4, STR-6', 4567, 10, 50000, '0', '-6', '4', '0', '0', '0'), 
(112, 'CON+4, DEX-6', 4568, 10, 50000, '0', '0', '4', '0', '-6', '0'), 
(113, 'DEX+4, STR-6', 4569, 10, 30000, '0', '-6', '0', '0', '4', '0'), 
(114, 'DEX+4, CON-6', 4570, 10, 36000, '0', '0', '-6', '0', '4', '0'), 
(115, 'INT+4, MEN-6', 4571, 10, 36000, '4', '0', '0', '-6', '0', '0'), 
(116, 'INT+4, WIT-6', 4572, 10, 30000, '4', '0', '0', '0', '0', '-6'), 
(117, 'MEN+4, INT-6', 4573, 10, 36000, '-6', '0', '0', '4', '0', '0'), 
(118, 'MEN+4, WIT-6', 4574, 10, 36000, '0', '0', '0', '4', '0', '-6'), 
(119, 'WIT+4, INT-6', 4575, 10, 36000, '-6', '0', '0', '0', '0', '4'), 
(120, 'WIT+4, MEN-6', 4576, 10, 30000, '0', '0', '0', '-6', '0', '4'), 
(121, 'STR+4, CON-5', 4577, 10, 36000, '0', '4', '-5', '0', '0', '0'), 
(122, 'STR+4, DEX-5', 4578, 10, 90000, '0', '4', '0', '0', '-5', '0'), 
(123, 'CON+4, STR-5', 4579, 10, 90000, '0', '-5', '4', '0', '0', '0'), 
(124, 'CON+4, DEX-5', 4580, 10, 90000, '0', '0', '4', '0', '-5', '0'), 
(125, 'DEX+4, STR-5', 4581, 10, 36000, '0', '-5', '0', '0', '4', '0'), 
(126, 'DEX+4, CON-5', 4582, 10, 36000, '0', '0', '-5', '0', '4', '0'), 
(127, 'INT+4, MEN-5', 4583, 10, 90000, '4', '0', '0', '-5', '0', '0'), 
(128, 'INT+4, WIT-5', 4584, 10, 36000, '4', '0', '0', '0', '0', '-5'), 
(129, 'MEN+4, INT-5', 4585, 10, 90000, '-5', '0', '0', '4', '0', '0'), 
(130, 'MEN+4, WIT-5', 4586, 10, 90000, '0', '0', '0', '4', '0', '-5'), 
(131, 'WIT+4, INT-5', 4587, 10, 36000, '-5', '0', '0', '0', '0', '4'), 
(132, 'WIT+4, MEN-5', 4588, 10, 36000, '0', '0', '0', '-5', '0', '4'), 
(133, 'STR+1, CON-1', 4553, 10, 50000, '0', '1', '-1', '0', '0', '0'), 
(134, 'STR+1, DEX-1', 4554, 10, 50000, '0', '1', '0', '0', '-1', '0'), 
(135, 'CON+1, STR-1', 4555, 10, 50000, '0', '-1', '1', '0', '0', '0'), 
(136, 'CON+1, DEX-1', 4556, 10, 50000, '0', '0', '1', '0', '-1', '0'), 
(137, 'DEX+1, STR-1', 4557, 10, 50000, '0', '-1', '0', '0', '1', '0'), 
(138, 'DEX+1, CON-1', 4558, 10, 50000, '0', '0', '-1', '0', '1', '0'), 
(139, 'INT+1, MEN-1', 4559, 10, 90000, '1', '0', '0', '-1', '0', '0'), 
(140, 'INT+1, WIT-1', 4560, 10, 50000, '1', '0', '0', '0', '0', '-1'), 
(141, 'MEN+1, INT-1', 4561, 10, 50000, '-1', '0', '0', '1', '0', '0'), 
(142, 'MEN+1, WIT-1', 4562, 10, 50000, '0', '0', '0', '1', '0', '-1'), 
(143, 'WIT+1, INT-1', 4563, 10, 50000, '-1', '0', '0', '0', '0', '1'), 
(144, 'WIT+1, MEN-1', 4564, 10, 50000, '0', '0', '0', '-1', '0', '1'), 
(145, 'STR+2, CON-2', 4589, 10, 60000, '0', '2', '-2', '0', '0', '0'), 
(146, 'STR+2, DEX-2', 4590, 10, 60000, '0', '2', '0', '0', '-2', '0'), 
(147, 'CON+2, STR-2', 4591, 10, 60000, '0', '-2', '2', '0', '0', '0'), 
(148, 'CON+2, DEX-2', 4592, 10, 60000, '0', '0', '2', '0', '-2', '0'), 
(149, 'DEX+2, STR-2', 4593, 10, 60000, '0', '-2', '0', '0', '2', '0'), 
(150, 'DEX+2, CON-2', 4594, 10, 60000, '0', '0', '-2', '0', '2', '0'), 
(151, 'INT+2, MEN-2', 4595, 10, 60000, '2', '0', '0', '-2', '0', '0'), 
(152, 'INT+2, WIT-2', 4596, 10, 90000, '2', '0', '0', '0', '0', '-2'), 
(153, 'MEN+2, INT-2', 4597, 10, 60000, '-2', '0', '0', '2', '0', '0'), 
(154, 'MEN+2, WIT-2', 4598, 10, 60000, '0', '0', '0', '2', '0', '-2'), 
(155, 'WIT+2, INT-2', 4599, 10, 60000, '-2', '0', '0', '0', '0', '2'), 
(156, 'WIT+2, MEN-2', 4600, 10, 60000, '0', '0', '0', '-2', '0', '2'), 
(157, 'STR+3, CON-3', 4601, 10, 90000, '0', '3', '-3', '0', '0', '0'), 
(158, 'STR+3, DEX-3', 4602, 10, 90000, '0', '3', '0', '0', '-3', '0'), 
(159, 'CON+3, STR-3', 4603, 10, 90000, '0', '-3', '3', '0', '0', '0'), 
(160, 'CON+3, DEX-3', 4604, 10, 90000, '0', '0', '3', '0', '-3', '0'), 
(161, 'DEX+3, STR-3', 4605, 10, 90000, '0', '-3', '0', '0', '3', '0'), 
(162, 'DEX+3, CON-3', 4606, 10, 90000, '0', '0', '-3', '0', '3', '0'), 
(163, 'INT+3, MEN-3', 4607, 10, 90000, '3', '0', '0', '-3', '0', '0'), 
(164, 'INT+3, WIT-3', 4608, 10, 90000, '3', '0', '0', '0', '0', '-3'), 
(165, 'MEN+3, INT-3', 4609, 10, 90000, '-3', '0', '0', '3', '0', '0'), 
(166, 'MEN+3, WIT-3', 4610, 10, 90000, '0', '0', '0', '3', '0', '-3'), 
(167, 'WIT+3, INT-3', 4611, 10, 90000, '-3', '0', '0', '0', '0', '3'), 
(168, 'WIT+3, MEN-3', 4612, 10, 90000, '0', '0', '0', '-3', '0', '3'), 
(169, 'STR+4, CON-4', 4613, 10, 145000, '0', '4', '-4', '0', '0', '0'), 
(170, 'STR+4, DEX-4', 4614, 10, 145000, '0', '4', '0', '0', '-4', '0'), 
(171, 'CON+4, STR-4', 4615, 10, 145000, '0', '-4', '4', '0', '0', '0'), 
(172, 'CON+4, DEX-4', 4616, 10, 145000, '0', '0', '4', '0', '-4', '0'), 
(173, 'DEX+4, STR-4', 4617, 10, 145000, '0', '-4', '0', '0', '4', '0'), 
(174, 'DEX+4, CON-4', 4618, 10, 145000, '0', '0', '-4', '0', '4', '0'), 
(175, 'INT+4, MEN-4', 4619, 10, 145000, '4', '0', '0', '-4', '0', '0'), 
(176, 'INT+4, WIT-4', 4620, 10, 145000, '4', '0', '0', '0', '0', '-4'), 
(177, 'MEN+4, INT-4', 4621, 10, 145000, '-4', '0', '0', '4', '0', '0'), 
(178, 'MEN+4, WIT-4', 4622, 10, 145000, '0', '0', '0', '4', '0', '-4'), 
(179, 'WIT+4, INT-4', 4623, 10, 145000, '-4', '0', '0', '0', '0', '4'), 
(180, 'WIT+4, MEN-4', 4624, 10, 145000, '0', '0', '0', '-4', '0', '4');