CREATE DATABASE `sinaweibo_net` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE sinaweibo_net;

CREATE TABLE `attention_relation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `fan_uid` char(20) DEFAULT NULL,
  `followeder_uid` char(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13374 DEFAULT CHARSET=utf8;

CREATE TABLE `user` (
  `uid` char(20) NOT NULL,
  `username` char(40) DEFAULT NULL,
  `url` char(50) DEFAULT NULL,
  `sex` varchar(10) DEFAULT NULL,
  `location` varchar(40) DEFAULT NULL,
  `birthday` varchar(30) DEFAULT NULL,
  `isexplored` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
