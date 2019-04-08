/*
Navicat MySQL Data Transfer

Source Server         : ke
Source Server Version : 50559
Source Host           : localhost:3306
Source Database       : newenergy

Target Server Type    : MYSQL
Target Server Version : 50559
File Encoding         : 65001

Date: 2019-04-08 16:28:06
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `newenergy_admin`
-- ----------------------------
DROP TABLE IF EXISTS `newenergy_admin`;
CREATE TABLE `newenergy_admin` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `add_time` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `last_login_ip` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `last_login_time` datetime DEFAULT NULL,
  `password` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `role_ids` tinyblob,
  `update_time` datetime DEFAULT NULL,
  `username` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of newenergy_admin
-- ----------------------------
INSERT INTO `newenergy_admin` VALUES ('1', '2019-03-17 11:56:25', '', null, '2019-03-17 11:56:17', '$2a$10$.rEfyBb/GURD9P2p0fRg/OAJGloGNDkJS4lY0cQHeqDgsbdTylBpu', 0xACED0005757200145B4C6A6176612E6C616E672E496E74656765723BFE97ADA00183E21B020000787000000001737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B020000787000000001, '2019-03-17 11:55:59', 'admin123');
INSERT INTO `newenergy_admin` VALUES ('2', '2019-04-01 10:23:45', '', null, null, '$2a$10$rxjtvxm/0czbX/E5hLYbpu3NmoQAB3jwokS0C9j..aQgtYDaaeBYu', 0xACED0005757200145B4C6A6176612E6C616E672E496E74656765723BFE97ADA00183E21B020000787000000001737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B020000787000000006, '2019-04-01 10:23:45', 'audit123');
INSERT INTO `newenergy_admin` VALUES ('3', '2019-04-01 11:01:37', '', null, null, '$2a$10$VslqcvIASDEhyHVtRjEEV.pcByynK7uDvqRJ1DFA/7ImLBe7F1VB.', 0xACED0005757200145B4C6A6176612E6C616E672E496E74656765723BFE97ADA00183E21B020000787000000001737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B020000787000000002, '2019-04-01 11:01:37', 'info123');
INSERT INTO `newenergy_admin` VALUES ('4', '2019-04-02 16:15:43', '', null, null, '$2a$10$hxN3qApcao6L6Gr/eixR8eCYnl5hmrM3IseQedj1RxAD3fFGbSGra', 0xACED0005757200145B4C6A6176612E6C616E672E496E74656765723BFE97ADA00183E21B020000787000000001737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B020000787000000005, '2019-04-02 16:15:43', 'mgr123');
INSERT INTO `newenergy_admin` VALUES ('5', '2019-04-02 16:22:45', '', null, null, '$2a$10$qghI1yLbMNyyAZX7cSuE.udxZR74G3kY0/Ws4s9ikcn113QdxDUEq', 0xACED0005757200145B4C6A6176612E6C616E672E496E74656765723BFE97ADA00183E21B020000787000000001737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B020000787000000008, '2019-04-02 16:22:45', 'fault123');
INSERT INTO `newenergy_admin` VALUES ('6', '2019-04-02 16:23:40', '', null, null, '$2a$10$Ow9JRK1W7yn9aT7zpzJzEOVoA4wnpcdWnCLhsPiUIhtDjkC8xB4z.', 0xACED0005757200145B4C6A6176612E6C616E672E496E74656765723BFE97ADA00183E21B020000787000000001737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B020000787000000009, '2019-04-02 16:23:40', 'faultLeader123');

-- ----------------------------
-- Table structure for `newenergy_permission`
-- ----------------------------
DROP TABLE IF EXISTS `newenergy_permission`;
CREATE TABLE `newenergy_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `permission` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `add_time` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `role_id` int(11) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of newenergy_permission
-- ----------------------------
INSERT INTO `newenergy_permission` VALUES ('1', '*', '2019-03-17 12:03:06', '', '1', '2019-03-17 12:03:53');

-- ----------------------------
-- Table structure for `newenergy_role`
-- ----------------------------
DROP TABLE IF EXISTS `newenergy_role`;
CREATE TABLE `newenergy_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `add_time` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `description` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `enable` bit(1) DEFAULT NULL,
  `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of newenergy_role
-- ----------------------------
INSERT INTO `newenergy_role` VALUES ('1', '2019-03-17 12:04:33', '', '所有模块的权限', '', '超级管理员', '2019-03-17 12:05:07');
INSERT INTO `newenergy_role` VALUES ('2', '2019-03-31 15:44:12', '', '负责录入用户基本信息、小区信息、设备类型信息、泵房信息', '', '数据录入人员', '2019-03-31 15:44:12');
INSERT INTO `newenergy_role` VALUES ('3', '2019-03-31 15:47:56', '', '批量充值功能(非移动充值)、修改充值系数、发起退款请求', '', '财务充值人员', '2019-03-31 15:47:56');
INSERT INTO `newenergy_role` VALUES ('4', '2019-03-31 16:09:53', '', '对财务充值人员的批量充值、退款请求进行审核，对运维人员修改的充值系数进行审核', '', '财务审核人员', '2019-03-31 16:09:53');
INSERT INTO `newenergy_role` VALUES ('5', '2019-03-31 16:16:36', '', '管理以上人员的身份和密码；输出各种报表', '', '公司管理人员', '2019-03-31 16:16:36');
INSERT INTO `newenergy_role` VALUES ('6', '2019-03-31 16:18:41', '', '对公司的所有报表可以查看', '', '审计人员', '2019-03-31 16:18:41');
INSERT INTO `newenergy_role` VALUES ('7', '2019-03-31 16:22:55', '', '每天对系统的数据做好备份工作', '', '备份管理人员', '2019-03-31 16:22:55');
INSERT INTO `newenergy_role` VALUES ('8', '2019-04-02 16:17:27', '', '查看各小区水量消耗数据；修改充值系数；接收机器故障信息；可手动添加故障记录；超时无响应向故障处理领导上诉', '', '运维人员', '2019-04-02 16:17:27');
INSERT INTO `newenergy_role` VALUES ('9', '2019-04-02 16:18:22', '', '响应运营维护管理人员调度（收到信息后按一个确认键表示已响应）；对机器故障进行维护和处理,处理完成之后，还需在微信中反馈处理结果（已处理，未处理已上报）', '', '故障领导', '2019-04-02 16:18:22');
