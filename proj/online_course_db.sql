CREATE DATABASE online_course_db;
USE online_course_db;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- t_chapter: table
CREATE TABLE `t_chapter`
(
    `id`           bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '序号',
    `chapter_id`   char(19)     NOT NULL COMMENT '章节ID',
    `chapter_name` varchar(50)  NOT NULL COMMENT '章节名称',
    `course_id`    char(19)     NOT NULL COMMENT '课程ID',
    `grade`        tinyint(1)   NOT NULL COMMENT '章节分数',
    `video_url`    varchar(100) NOT NULL COMMENT '章节视频',
    `homework_url` varchar(100) NOT NULL COMMENT '章节作业',
    `problem`      varchar(100) NOT NULL COMMENT '章节问题',
    `gmt_create`   varchar(255) NOT NULL COMMENT '创建时间',
    `gmt_modified` varchar(255) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_course_id` (`course_id`),
    UNIQUE KEY (`chapter_id`, `chapter_name`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '章节'
  ROW_FORMAT = DYNAMIC;

-- No native definition for element: idx_course_id (index)

-- t_chapter_student: table
CREATE TABLE `t_chapter_student`
(
    `id`           int unsigned                            NOT NULL AUTO_INCREMENT COMMENT '序号',
    `chapter_id`   char(19) COLLATE utf8mb4_unicode_ci     NOT NULL COMMENT '章节ID',
    `student_id`   char(19) COLLATE utf8mb4_unicode_ci     NOT NULL COMMENT '学生ID',
    `hw_url`       varchar(255) COLLATE utf8mb4_unicode_ci          DEFAULT NULL COMMENT '学生提交作业',
    `time`         tinyint unsigned                        NOT NULL DEFAULT '0' COMMENT '观看时长（min）',
    `hw_grade`     tinyint(1)                              NOT NULL DEFAULT '-1' COMMENT '作业分数',
    `pro_grade`    tinyint(1)                              NOT NULL DEFAULT '-1' COMMENT '问题分数',
    `gmt_create`   varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建时间',
    `gmt_modified` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_chapter_id` (`chapter_id`),
    KEY `idx_student_id` (`student_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 3
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='章节-学生';

-- No native definition for element: idx_chapter_id (index)

-- No native definition for element: idx_student_id (index)

-- t_course: table
CREATE TABLE `t_course`
(
    `id`           char(19) COLLATE utf8mb4_unicode_ci                           NOT NULL COMMENT '课程ID',
    `title`        varchar(50) COLLATE utf8mb4_unicode_ci                        NOT NULL COMMENT '课程名称',
    `teacher_id`   char(19) COLLATE utf8mb4_unicode_ci                           NOT NULL COMMENT '课程老师ID',
    `price`        decimal(10, 2) unsigned                                       NOT NULL DEFAULT '0.00' COMMENT '课程销售价格，设置为0则可免费观看',
    `lesson_num`   int unsigned                                                  NOT NULL DEFAULT '0' COMMENT '总课时',
    `cover`        varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '课程封面图片路径',
    `description`  longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '课程内容详情',
    `status`       tinyint unsigned                                              NOT NULL DEFAULT '0' COMMENT '课程状态 0申请中 1已发布 2已拒绝',
    `gmt_create`   varchar(255) COLLATE utf8mb4_unicode_ci                       NOT NULL COMMENT '创建时间',
    `gmt_modified` varchar(255) COLLATE utf8mb4_unicode_ci                       NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_teacher_id` (`teacher_id`),
    KEY `idx_title` (`title`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  ROW_FORMAT = DYNAMIC COMMENT ='课程';

-- No native definition for element: idx_title (index)

-- No native definition for element: idx_teacher_id (index)

-- t_course_student: table
CREATE TABLE `t_course_student`
(
    `id`           int unsigned                            NOT NULL AUTO_INCREMENT COMMENT '序号',
    `course_id`    char(19) COLLATE utf8mb4_unicode_ci     NOT NULL COMMENT '课程ID',
    `student_id`   char(19) COLLATE utf8mb4_unicode_ci     NOT NULL COMMENT '学生ID',
    `course_grade` tinyint unsigned                        NOT NULL DEFAULT '0' COMMENT '当前课程已获分数',
    `gmt_create`   varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建时间',
    `gmt_modified` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_course_id` (`course_id`),
    KEY `idx_student_id` (`student_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='课程-学生';

-- No native definition for element: idx_course_id (index)

-- No native definition for element: idx_student_id (index)

-- t_message: table
CREATE TABLE `t_message`
(
    `id`         bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `content`    text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '内容',
    `username`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '评论人',
    `parent_id`  bigint                                                        DEFAULT NULL COMMENT '父ID',
    `foreign_id` bigint                                                        DEFAULT '0' COMMENT '关联id',
    `gmt_create` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '评论时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 20
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  ROW_FORMAT = DYNAMIC COMMENT ='留言表';

-- t_notice: table
CREATE TABLE `t_notice`
(
    `id`               bigint     NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `title`            varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标题',
    `content`          text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '内容',
    `to_all`           tinyint(1) NOT NULL                                           DEFAULT '1' COMMENT '该notice是否面向全体user，0（仅面向部分user），1（面向全部，默认）',
    `teacher_username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '老师用户名（发出notice）',
    `student_username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '学生用户名（接收notice）',
    `gmt_create`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发布时间',
    `gmt_modified`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 7
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  ROW_FORMAT = DYNAMIC COMMENT ='公告';

-- t_user: table
CREATE TABLE `t_user`
(
    `id`        bigint                                                        NOT NULL AUTO_INCREMENT,
    `username`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
    `password`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci          DEFAULT NULL COMMENT '密码',
    `nick_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci          DEFAULT NULL COMMENT '昵称',
    `email`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci          DEFAULT NULL COMMENT '邮箱',
    `phone`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci          DEFAULT NULL COMMENT '手机号',
    `avatar`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci          DEFAULT NULL COMMENT '头像',
    `role`      tinyint(1)                                                    NOT NULL DEFAULT '3' COMMENT '角色',
    `address`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci          DEFAULT NULL COMMENT '地址',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `username` (`username`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 4
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  ROW_FORMAT = DYNAMIC COMMENT ='用户表';

SET FOREIGN_KEY_CHECKS = 1;
