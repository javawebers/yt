create database if not exists `yt-mybatis`;
use `yt-mybatis`;
CREATE TABLE  if not exists `mysql_example` (
  `example_id` varchar(36) NOT NULL COMMENT 'id',
  `test_varchar` varchar(36) DEFAULT NULL COMMENT 'String类型',
  `test_int` int(11) DEFAULT NULL COMMENT 'int类型',
  `test_boolean` tinyint(1) DEFAULT NULL COMMENT 'boolean类型',
  `test_Enum` enum('MALE','FEMALE','OTHER') DEFAULT NULL COMMENT 'enum 类型，MALE:男 ，FEMALE:女，OTHER:其他',
  `founder_id` varchar(255) DEFAULT NULL,
  `founder_name` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `modifier_id` varchar(255) DEFAULT NULL,
  `modifier_name` varchar(255) DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `delete_flag` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`example_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='示例';

