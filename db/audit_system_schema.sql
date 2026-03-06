-- ============================================================
-- 海南数字三农平台 · 村集体智能审计系统
-- 数据库建表脚本
-- MySQL 8.0 | 字符集 utf8mb4 | 引擎 InnoDB
-- 公共字段约定：
--   org_id       多租户隔离
--   created_by / updated_by  操作人ID
--   created_time / updated_time  时间戳（自动维护）
--   deleted      逻辑删除（0正常 1删除，MyBatis Plus @TableLogic）
--   version      乐观锁（MyBatis Plus @Version）
-- ============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ------------------------------------------------------------
-- 1. t_audit_plan  审计计划
--    生命周期：草稿 → 已发布 → 进行中 → 已完成 → 已归档
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `t_audit_plan`;
CREATE TABLE `t_audit_plan` (
    `id`            BIGINT          NOT NULL                        COMMENT '主键ID（雪花算法）',
    `org_id`        BIGINT          NOT NULL                        COMMENT '租户/组织ID',
    `plan_code`     VARCHAR(32)     NOT NULL                        COMMENT '计划编号（业务唯一，格式 AP-YYYYMM-NNNN）',
    `plan_name`     VARCHAR(128)    NOT NULL                        COMMENT '计划名称',
    `plan_type`     TINYINT         NOT NULL DEFAULT 1              COMMENT '计划类型：1年度计划 2专项计划 3临时抽查',
    `audit_year`    SMALLINT        NOT NULL                        COMMENT '审计年度',
    `start_date`    DATE            NOT NULL                        COMMENT '计划开始日期',
    `end_date`      DATE            NOT NULL                        COMMENT '计划结束日期',
    `scope`         TEXT                                            COMMENT '审计范围描述',
    `objectives`    TEXT                                            COMMENT '审计目标',
    `status`        TINYINT         NOT NULL DEFAULT 0              COMMENT '状态：0草稿 1已发布 2进行中 3已完成 4已归档',
    `approved_by`   BIGINT                                          COMMENT '审批人ID',
    `approved_time` DATETIME                                        COMMENT '审批时间',
    `remark`        VARCHAR(512)                                    COMMENT '备注',
    `version`       INT             NOT NULL DEFAULT 0              COMMENT '乐观锁版本号',
    `deleted`       TINYINT         NOT NULL DEFAULT 0              COMMENT '逻辑删除：0正常 1删除',
    `created_by`    BIGINT          NOT NULL                        COMMENT '创建人ID',
    `created_time`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by`    BIGINT          NOT NULL                        COMMENT '最后修改人ID',
    `updated_time`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_org_plan_code` (`org_id`, `plan_code`),
    KEY `idx_org_status`    (`org_id`, `status`),
    KEY `idx_org_audit_year`(`org_id`, `audit_year`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='审计计划';


-- ------------------------------------------------------------
-- 2. t_audit_task  审计任务
--    一个计划可拆分为多个任务，每个任务对应一个被审计村集体
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `t_audit_task`;
CREATE TABLE `t_audit_task` (
    `id`              BIGINT        NOT NULL                        COMMENT '主键ID',
    `org_id`          BIGINT        NOT NULL                        COMMENT '租户/组织ID',
    `plan_id`         BIGINT        NOT NULL                        COMMENT '所属审计计划ID',
    `task_code`       VARCHAR(32)   NOT NULL                        COMMENT '任务编号（AT-YYYYMM-NNNN）',
    `task_name`       VARCHAR(128)  NOT NULL                        COMMENT '任务名称',
    `village_org_id`  BIGINT        NOT NULL                        COMMENT '被审计村集体组织ID',
    `village_name`    VARCHAR(64)   NOT NULL                        COMMENT '被审计村集体名称（冗余，避免跨库关联）',
    `audit_period_start` DATE       NOT NULL                        COMMENT '审计期间开始',
    `audit_period_end`   DATE       NOT NULL                        COMMENT '审计期间结束',
    `leader_id`       BIGINT        NOT NULL                        COMMENT '主审人ID',
    `member_ids`      JSON                                          COMMENT '参审人员ID列表（JSON数组）',
    `status`          TINYINT       NOT NULL DEFAULT 0              COMMENT '状态：0待开始 1进行中 2待审核 3已完成',
    `actual_start`    DATE                                          COMMENT '实际开始日期',
    `actual_end`      DATE                                          COMMENT '实际结束日期',
    `remark`          VARCHAR(512)                                  COMMENT '备注',
    `version`         INT           NOT NULL DEFAULT 0              COMMENT '乐观锁版本号',
    `deleted`         TINYINT       NOT NULL DEFAULT 0              COMMENT '逻辑删除：0正常 1删除',
    `created_by`      BIGINT        NOT NULL                        COMMENT '创建人ID',
    `created_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by`      BIGINT        NOT NULL                        COMMENT '最后修改人ID',
    `updated_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_org_task_code` (`org_id`, `task_code`),
    KEY `idx_plan_id`        (`plan_id`),
    KEY `idx_org_status`     (`org_id`, `status`),
    KEY `idx_org_village`    (`org_id`, `village_org_id`),
    KEY `idx_leader`         (`leader_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='审计任务';


-- ------------------------------------------------------------
-- 3. t_audit_evidence  审计底稿/证据
--    记录现场采集的每条证据，支持文件附件（存对象存储路径）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `t_audit_evidence`;
CREATE TABLE `t_audit_evidence` (
    `id`              BIGINT        NOT NULL                        COMMENT '主键ID',
    `org_id`          BIGINT        NOT NULL                        COMMENT '租户/组织ID',
    `task_id`         BIGINT        NOT NULL                        COMMENT '所属审计任务ID',
    `evidence_code`   VARCHAR(32)   NOT NULL                        COMMENT '证据编号（AE-YYYYMM-NNNN）',
    `evidence_type`   TINYINT       NOT NULL DEFAULT 1              COMMENT '证据类型：1财务凭证 2合同协议 3会议记录 4实物照片 5访谈记录 6其他',
    `title`           VARCHAR(256)  NOT NULL                        COMMENT '证据标题',
    `description`     TEXT                                          COMMENT '证据描述/摘要',
    `source`          VARCHAR(128)                                  COMMENT '证据来源（如：村委会财务室）',
    `evidence_date`   DATE                                          COMMENT '证据对应日期',
    `file_count`      INT           NOT NULL DEFAULT 0              COMMENT '附件数量',
    `attachments`     JSON                                          COMMENT '附件信息（JSON数组：[{name,url,size,mimeType}]）',
    `ref_finding_id`  BIGINT                                        COMMENT '关联审计发现ID（采集时已识别问题）',
    `collected_by`    BIGINT        NOT NULL                        COMMENT '采集人ID',
    `collected_time`  DATETIME      NOT NULL                        COMMENT '采集时间',
    `status`          TINYINT       NOT NULL DEFAULT 0              COMMENT '状态：0待审核 1已采纳 2已驳回',
    `review_by`       BIGINT                                        COMMENT '复核人ID',
    `review_time`     DATETIME                                      COMMENT '复核时间',
    `review_comment`  VARCHAR(512)                                  COMMENT '复核意见',
    `version`         INT           NOT NULL DEFAULT 0              COMMENT '乐观锁版本号',
    `deleted`         TINYINT       NOT NULL DEFAULT 0              COMMENT '逻辑删除：0正常 1删除',
    `created_by`      BIGINT        NOT NULL                        COMMENT '创建人ID',
    `created_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by`      BIGINT        NOT NULL                        COMMENT '最后修改人ID',
    `updated_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_org_evidence_code` (`org_id`, `evidence_code`),
    KEY `idx_task_id`          (`task_id`),
    KEY `idx_org_type`         (`org_id`, `evidence_type`),
    KEY `idx_ref_finding`      (`ref_finding_id`),
    KEY `idx_collected_time`   (`org_id`, `collected_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='审计底稿/证据';


-- ------------------------------------------------------------
-- 4. t_audit_finding  审计发现/问题
--    支持规则引擎自动触发（source=AUTO）和人工录入（source=MANUAL）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `t_audit_finding`;
CREATE TABLE `t_audit_finding` (
    `id`              BIGINT        NOT NULL                        COMMENT '主键ID',
    `org_id`          BIGINT        NOT NULL                        COMMENT '租户/组织ID',
    `task_id`         BIGINT        NOT NULL                        COMMENT '所属审计任务ID',
    `finding_code`    VARCHAR(32)   NOT NULL                        COMMENT '发现编号（AF-YYYYMM-NNNN）',
    `finding_type`    TINYINT       NOT NULL                        COMMENT '问题类型：1违规支出 2资产流失 3账目不符 4程序违规 5异常交易 6其他',
    `severity`        TINYINT       NOT NULL DEFAULT 2              COMMENT '严重程度：1低 2中 3高 4重大',
    `title`           VARCHAR(256)  NOT NULL                        COMMENT '问题标题',
    `description`     TEXT          NOT NULL                        COMMENT '问题详细描述',
    `amount_involved` DECIMAL(18,2)                                 COMMENT '涉及金额（元）',
    `source`          VARCHAR(16)   NOT NULL DEFAULT 'MANUAL'       COMMENT '来源：MANUAL人工录入 AUTO规则引擎',
    `rule_code`       VARCHAR(64)                                   COMMENT '触发的规则编码（AUTO时填写）',
    `rule_detail`     JSON                                          COMMENT '规则引擎命中明细（异常交易原始数据快照）',
    `ref_finance_id`  BIGINT                                        COMMENT '关联财务记录ID（只读视图数据源ID）',
    `status`          TINYINT       NOT NULL DEFAULT 0              COMMENT '状态：0待确认 1已确认 2整改中 3已整改 4已销号',
    `confirmed_by`    BIGINT                                        COMMENT '确认人ID',
    `confirmed_time`  DATETIME                                      COMMENT '确认时间',
    `deadline`        DATE                                          COMMENT '整改截止日期',
    `remark`          VARCHAR(512)                                  COMMENT '备注',
    `version`         INT           NOT NULL DEFAULT 0              COMMENT '乐观锁版本号',
    `deleted`         TINYINT       NOT NULL DEFAULT 0              COMMENT '逻辑删除：0正常 1删除',
    `created_by`      BIGINT        NOT NULL                        COMMENT '创建人ID',
    `created_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by`      BIGINT        NOT NULL                        COMMENT '最后修改人ID',
    `updated_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_org_finding_code` (`org_id`, `finding_code`),
    KEY `idx_task_id`          (`task_id`),
    KEY `idx_org_type_severity`(`org_id`, `finding_type`, `severity`),
    KEY `idx_org_status`       (`org_id`, `status`),
    KEY `idx_rule_code`        (`rule_code`),
    KEY `idx_deadline`         (`org_id`, `deadline`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='审计发现/问题';


-- ------------------------------------------------------------
-- 5. t_audit_report  审计报告
--    支持模板自动生成与人工修订，存储最终 PDF/Word 路径
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `t_audit_report`;
CREATE TABLE `t_audit_report` (
    `id`              BIGINT        NOT NULL                        COMMENT '主键ID',
    `org_id`          BIGINT        NOT NULL                        COMMENT '租户/组织ID',
    `task_id`         BIGINT        NOT NULL                        COMMENT '所属审计任务ID',
    `report_code`     VARCHAR(32)   NOT NULL                        COMMENT '报告编号（AR-YYYYMM-NNNN）',
    `report_title`    VARCHAR(256)  NOT NULL                        COMMENT '报告标题',
    `report_type`     TINYINT       NOT NULL DEFAULT 1              COMMENT '报告类型：1征求意见稿 2正式报告 3专项报告',
    `summary`         TEXT                                          COMMENT '报告摘要',
    `content`         LONGTEXT                                      COMMENT '报告正文（富文本/Markdown）',
    `finding_count`   INT           NOT NULL DEFAULT 0              COMMENT '问题数量（冗余统计）',
    `finding_amount`  DECIMAL(18,2) NOT NULL DEFAULT 0              COMMENT '问题涉及金额合计（冗余统计）',
    `file_url`        VARCHAR(512)                                  COMMENT '导出文件路径（对象存储URL）',
    `file_type`       VARCHAR(16)                                   COMMENT '文件类型：PDF / DOCX',
    `gen_mode`        VARCHAR(16)   NOT NULL DEFAULT 'AUTO'         COMMENT '生成方式：AUTO自动 MANUAL人工',
    `status`          TINYINT       NOT NULL DEFAULT 0              COMMENT '状态：0草稿 1待审核 2已发布 3已归档',
    `published_by`    BIGINT                                        COMMENT '发布人ID',
    `published_time`  DATETIME                                      COMMENT '发布时间',
    `version`         INT           NOT NULL DEFAULT 0              COMMENT '乐观锁版本号',
    `deleted`         TINYINT       NOT NULL DEFAULT 0              COMMENT '逻辑删除：0正常 1删除',
    `created_by`      BIGINT        NOT NULL                        COMMENT '创建人ID',
    `created_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by`      BIGINT        NOT NULL                        COMMENT '最后修改人ID',
    `updated_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_org_report_code` (`org_id`, `report_code`),
    UNIQUE KEY `uk_task_report_type` (`task_id`, `report_type`),
    KEY `idx_org_status`       (`org_id`, `status`),
    KEY `idx_published_time`   (`org_id`, `published_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='审计报告';


-- ------------------------------------------------------------
-- 6. t_audit_rectify  整改记录
--    每条 finding 可有多轮整改，直至问题销号
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `t_audit_rectify`;
CREATE TABLE `t_audit_rectify` (
    `id`              BIGINT        NOT NULL                        COMMENT '主键ID',
    `org_id`          BIGINT        NOT NULL                        COMMENT '租户/组织ID',
    `finding_id`      BIGINT        NOT NULL                        COMMENT '关联审计发现ID',
    `task_id`         BIGINT        NOT NULL                        COMMENT '关联审计任务ID（冗余，方便查询）',
    `round`           TINYINT       NOT NULL DEFAULT 1              COMMENT '整改轮次（第几次整改）',
    `rectify_type`    TINYINT       NOT NULL DEFAULT 1              COMMENT '整改方式：1退款补账 2制度完善 3人员处理 4其他',
    `plan_desc`       TEXT          NOT NULL                        COMMENT '整改方案描述',
    `result_desc`     TEXT                                          COMMENT '整改结果描述',
    `amount_rectified` DECIMAL(18,2)                               COMMENT '已整改金额（元）',
    `attachments`     JSON                                          COMMENT '整改佐证附件（JSON数组）',
    `responsible_id`  BIGINT        NOT NULL                        COMMENT '整改责任人ID',
    `responsible_name` VARCHAR(64)  NOT NULL                        COMMENT '整改责任人姓名（冗余）',
    `submit_time`     DATETIME                                      COMMENT '整改提交时间',
    `status`          TINYINT       NOT NULL DEFAULT 0              COMMENT '状态：0待提交 1已提交 2审核通过 3审核驳回',
    `review_by`       BIGINT                                        COMMENT '核查人ID',
    `review_time`     DATETIME                                      COMMENT '核查时间',
    `review_comment`  VARCHAR(512)                                  COMMENT '核查意见',
    `deadline`        DATE          NOT NULL                        COMMENT '整改截止日期',
    `overdue`         TINYINT       NOT NULL DEFAULT 0              COMMENT '是否逾期：0否 1是',
    `version`         INT           NOT NULL DEFAULT 0              COMMENT '乐观锁版本号',
    `deleted`         TINYINT       NOT NULL DEFAULT 0              COMMENT '逻辑删除：0正常 1删除',
    `created_by`      BIGINT        NOT NULL                        COMMENT '创建人ID',
    `created_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by`      BIGINT        NOT NULL                        COMMENT '最后修改人ID',
    `updated_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    PRIMARY KEY (`id`),
    KEY `idx_finding_id`       (`finding_id`),
    KEY `idx_task_id`          (`task_id`),
    KEY `idx_org_status`       (`org_id`, `status`),
    KEY `idx_responsible`      (`responsible_id`),
    KEY `idx_deadline_overdue` (`org_id`, `deadline`, `overdue`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='整改记录';

SET FOREIGN_KEY_CHECKS = 1;
