-- ============================================================
-- 三农经济平台 数据库建表脚本
-- ============================================================

CREATE DATABASE IF NOT EXISTS farmer_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE farmer_db;

-- ============================================================
-- 系统模块
-- ============================================================

CREATE TABLE IF NOT EXISTS sys_user (
    id           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    username     VARCHAR(50)  NOT NULL COMMENT '用户名',
    password     VARCHAR(200) NOT NULL COMMENT '密码（BCrypt）',
    real_name    VARCHAR(50)  COMMENT '真实姓名',
    phone        VARCHAR(20)  COMMENT '手机号',
    email        VARCHAR(100) COMMENT '邮箱',
    region_id    BIGINT       COMMENT '所属区划id',
    role_id      BIGINT       COMMENT '角色id',
    status       TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    avatar       VARCHAR(500) COMMENT '头像URL',
    remark       VARCHAR(500) COMMENT '备注',
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by    BIGINT       COMMENT '创建人',
    update_by    BIGINT       COMMENT '更新人',
    deleted      TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户';

CREATE TABLE IF NOT EXISTS sys_role (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    role_name    VARCHAR(50)  NOT NULL COMMENT '角色名称',
    role_code    VARCHAR(50)  NOT NULL COMMENT '角色编码',
    description  VARCHAR(200) COMMENT '描述',
    status       TINYINT      NOT NULL DEFAULT 1,
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by    BIGINT,
    update_by    BIGINT,
    deleted      TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色';

CREATE TABLE IF NOT EXISTS sys_permission (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    parent_id    BIGINT       NOT NULL DEFAULT 0 COMMENT '父节点id',
    perm_name    VARCHAR(100) NOT NULL COMMENT '权限名称',
    perm_code    VARCHAR(100) COMMENT '权限编码',
    path         VARCHAR(200) COMMENT '路由路径',
    component    VARCHAR(200) COMMENT '组件路径',
    icon         VARCHAR(100) COMMENT '图标',
    type         TINYINT      NOT NULL DEFAULT 1 COMMENT '类型 0-目录 1-菜单 2-按钮',
    sort         INT          NOT NULL DEFAULT 0,
    status       TINYINT      NOT NULL DEFAULT 1,
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by    BIGINT,
    update_by    BIGINT,
    deleted      TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限菜单';

CREATE TABLE IF NOT EXISTS sys_role_permission (
    role_id      BIGINT NOT NULL,
    perm_id      BIGINT NOT NULL,
    PRIMARY KEY (role_id, perm_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联';

CREATE TABLE IF NOT EXISTS sys_region (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    parent_id    BIGINT       NOT NULL DEFAULT 0,
    region_name  VARCHAR(100) NOT NULL COMMENT '区划名称',
    region_code  VARCHAR(20)  COMMENT '区划编码',
    level        TINYINT      NOT NULL COMMENT '层级 1-省 2-市 3-县 4-乡镇 5-村',
    region_type  VARCHAR(20)  COMMENT '区划类型',
    sort         INT          NOT NULL DEFAULT 0,
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by    BIGINT,
    update_by    BIGINT,
    deleted      TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_parent_id (parent_id),
    KEY idx_level (level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='行政区划';

-- ============================================================
-- 财务一张图
-- ============================================================

CREATE TABLE IF NOT EXISTS fin_map_overview (
    id               BIGINT   NOT NULL AUTO_INCREMENT,
    region_id        BIGINT   NOT NULL COMMENT '区划id',
    stat_month       CHAR(6)  NOT NULL COMMENT '统计年月 yyyyMM',
    county_count     INT      DEFAULT 0 COMMENT '区县数',
    township_count   INT      DEFAULT 0 COMMENT '乡镇数',
    village_total    INT      DEFAULT 0 COMMENT '村集体总数',
    unclosed_count   INT      DEFAULT 0 COMMENT '未结账村数',
    no_account_count INT      DEFAULT 0 COMMENT '未创建账套村数',
    create_time      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by        BIGINT,
    update_by        BIGINT,
    deleted          TINYINT  NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_region_month (region_id, stat_month)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='财务一张图-总览统计';

CREATE TABLE IF NOT EXISTS fin_village_income (
    id                BIGINT         NOT NULL AUTO_INCREMENT,
    village_id        BIGINT         NOT NULL COMMENT '村id',
    region_id         BIGINT         NOT NULL COMMENT '区划id',
    stat_month        CHAR(6)        NOT NULL COMMENT '统计年月',
    total_income      DECIMAL(15,2)  DEFAULT 0 COMMENT '总收入',
    business_income   DECIMAL(15,2)  DEFAULT 0 COMMENT '经营收入',
    subsidy_income    DECIMAL(15,2)  DEFAULT 0 COMMENT '补助收入',
    investment_income DECIMAL(15,2)  DEFAULT 0 COMMENT '投资收益',
    other_income      DECIMAL(15,2)  DEFAULT 0 COMMENT '其他收入',
    create_time       DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time       DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by         BIGINT,
    update_by         BIGINT,
    deleted           TINYINT        NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_region_month (region_id, stat_month)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='村集体收入明细';

CREATE TABLE IF NOT EXISTS fin_indicators (
    id                    BIGINT        NOT NULL AUTO_INCREMENT,
    village_id            BIGINT        NOT NULL,
    region_id             BIGINT        NOT NULL,
    stat_month            CHAR(6)       NOT NULL,
    monetary_funds        DECIMAL(15,2) DEFAULT 0 COMMENT '货币资金',
    receivables           DECIMAL(15,2) DEFAULT 0 COMMENT '应收账款',
    inventory             DECIMAL(15,2) DEFAULT 0 COMMENT '存货',
    fixed_assets          DECIMAL(15,2) DEFAULT 0 COMMENT '固定资产',
    payables              DECIMAL(15,2) DEFAULT 0 COMMENT '应付账款',
    business_income       DECIMAL(15,2) DEFAULT 0 COMMENT '经营收入',
    business_expense      DECIMAL(15,2) DEFAULT 0 COMMENT '经营支出',
    management_fee        DECIMAL(15,2) DEFAULT 0 COMMENT '管理费用',
    current_year_profit   DECIMAL(15,2) DEFAULT 0 COMMENT '本年收益',
    undistributed_profit  DECIMAL(15,2) DEFAULT 0 COMMENT '未分配收益',
    create_time           DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time           DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by             BIGINT,
    update_by             BIGINT,
    deleted               TINYINT       NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_region_month (region_id, stat_month)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='财务指标';

-- ============================================================
-- 村委财务管理
-- ============================================================

CREATE TABLE IF NOT EXISTS fin_account_set (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    village_id   BIGINT       NOT NULL COMMENT '村id',
    region_id    BIGINT       NOT NULL COMMENT '区划id',
    set_name     VARCHAR(100) NOT NULL COMMENT '账套名称',
    account_year INT          NOT NULL COMMENT '会计年度',
    currency     VARCHAR(10)  NOT NULL DEFAULT 'CNY' COMMENT '本位币',
    status       TINYINT      NOT NULL DEFAULT 1 COMMENT '0-停用 1-启用',
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by    BIGINT,
    update_by    BIGINT,
    deleted      TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_village_year (village_id, account_year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账套';

CREATE TABLE IF NOT EXISTS fin_account (
    id               BIGINT        NOT NULL AUTO_INCREMENT,
    account_set_id   BIGINT        NOT NULL COMMENT '账套id',
    parent_id        BIGINT        NOT NULL DEFAULT 0,
    account_code     VARCHAR(20)   NOT NULL COMMENT '科目编码',
    account_name     VARCHAR(100)  NOT NULL COMMENT '科目名称',
    account_type     TINYINT       NOT NULL COMMENT '1-资产 2-负债 3-所有者权益 4-收入 5-支出',
    balance_direction VARCHAR(10)  NOT NULL COMMENT 'debit/credit',
    level            TINYINT       NOT NULL COMMENT '级次',
    is_leaf          TINYINT       NOT NULL DEFAULT 1 COMMENT '是否末级',
    init_balance     DECIMAL(15,2) DEFAULT 0 COMMENT '期初余额',
    status           TINYINT       NOT NULL DEFAULT 1,
    create_time      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by        BIGINT,
    update_by        BIGINT,
    deleted          TINYINT       NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_account_set (account_set_id),
    KEY idx_account_code (account_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会计科目';

CREATE TABLE IF NOT EXISTS fin_voucher (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    account_set_id BIGINT     NOT NULL,
    voucher_word VARCHAR(10)  NOT NULL DEFAULT '记' COMMENT '凭证字',
    voucher_no   INT          NOT NULL COMMENT '凭证号',
    make_date    DATE         NOT NULL COMMENT '制单日期',
    summary      VARCHAR(500) COMMENT '摘要',
    status       TINYINT      NOT NULL DEFAULT 0 COMMENT '0-草稿 1-已审核',
    attach_count INT          DEFAULT 0 COMMENT '附件数',
    audit_by     BIGINT       COMMENT '审核人',
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by    BIGINT,
    update_by    BIGINT,
    deleted      TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_account_set_date (account_set_id, make_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='记账凭证';

CREATE TABLE IF NOT EXISTS fin_voucher_detail (
    id             BIGINT        NOT NULL AUTO_INCREMENT,
    voucher_id     BIGINT        NOT NULL,
    account_id     BIGINT        NOT NULL COMMENT '科目id',
    account_code   VARCHAR(20)   COMMENT '科目编码',
    account_name   VARCHAR(100)  COMMENT '科目名称',
    summary        VARCHAR(500)  COMMENT '摘要',
    debit_amount   DECIMAL(15,2) DEFAULT 0 COMMENT '借方金额',
    credit_amount  DECIMAL(15,2) DEFAULT 0 COMMENT '贷方金额',
    sort_order     INT           DEFAULT 0,
    create_time    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by      BIGINT,
    update_by      BIGINT,
    deleted        TINYINT       NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_voucher_id (voucher_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='凭证明细';

CREATE TABLE IF NOT EXISTS fin_account_book (
    id              BIGINT        NOT NULL AUTO_INCREMENT,
    account_set_id  BIGINT        NOT NULL,
    book_name       VARCHAR(100)  NOT NULL COMMENT '账户名称',
    book_type       TINYINT       NOT NULL COMMENT '1-银行 2-现金',
    bank_account    VARCHAR(50)   COMMENT '账号',
    bank_name       VARCHAR(100)  COMMENT '开户行',
    init_balance    DECIMAL(15,2) DEFAULT 0 COMMENT '期初余额',
    current_balance DECIMAL(15,2) DEFAULT 0 COMMENT '当前余额',
    status          TINYINT       NOT NULL DEFAULT 1,
    create_time     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by       BIGINT,
    update_by       BIGINT,
    deleted         TINYINT       NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_account_set (account_set_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资金账户';

CREATE TABLE IF NOT EXISTS fin_journal (
    id              BIGINT        NOT NULL AUTO_INCREMENT,
    account_set_id  BIGINT        NOT NULL,
    account_book_id BIGINT        NOT NULL COMMENT '资金账户id',
    journal_type    TINYINT       NOT NULL COMMENT '1-银行 2-现金',
    journal_date    DATE          NOT NULL,
    summary         VARCHAR(500)  COMMENT '摘要',
    income_amount   DECIMAL(15,2) DEFAULT 0 COMMENT '收入金额',
    expense_amount  DECIMAL(15,2) DEFAULT 0 COMMENT '支出金额',
    balance         DECIMAL(15,2) DEFAULT 0 COMMENT '余额',
    voucher_id      BIGINT        COMMENT '关联凭证id',
    voucher_no      VARCHAR(50)   COMMENT '凭证号',
    create_time     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by       BIGINT,
    update_by       BIGINT,
    deleted         TINYINT       NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_account_book_date (account_book_id, journal_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='日记账';

CREATE TABLE IF NOT EXISTS fin_contract (
    id            BIGINT        NOT NULL AUTO_INCREMENT,
    account_set_id BIGINT       NOT NULL,
    contract_no   VARCHAR(50)   NOT NULL COMMENT '合同编号',
    contract_name VARCHAR(200)  NOT NULL COMMENT '合同名称',
    contract_type TINYINT       COMMENT '1-承包 2-租赁 3-工程 4-其他',
    counterparty  VARCHAR(100)  COMMENT '对方当事人',
    sign_date     DATE          COMMENT '签订日期',
    amount        DECIMAL(15,2) COMMENT '合同金额',
    start_date    DATE          COMMENT '起始日期',
    end_date      DATE          COMMENT '终止日期',
    status        TINYINT       NOT NULL DEFAULT 1 COMMENT '0-未生效 1-履行中 2-已完成 3-已终止',
    remark        VARCHAR(500),
    create_time   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by     BIGINT,
    update_by     BIGINT,
    deleted       TINYINT       NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_account_set (account_set_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合同管理';

CREATE TABLE IF NOT EXISTS fin_asset (
    id                      BIGINT        NOT NULL AUTO_INCREMENT,
    account_set_id          BIGINT        NOT NULL,
    asset_no                VARCHAR(50)   NOT NULL COMMENT '资产编号',
    asset_name              VARCHAR(200)  NOT NULL COMMENT '资产名称',
    asset_type              TINYINT       COMMENT '1-房屋建筑 2-机器设备 3-运输设备 4-其他',
    purchase_date           DATE          COMMENT '入账日期',
    original_value          DECIMAL(15,2) COMMENT '原值',
    residual_rate           DECIMAL(5,4)  DEFAULT 0.05 COMMENT '残值率',
    useful_life_months      INT           DEFAULT 60 COMMENT '预计使用月份',
    deprec_method           TINYINT       DEFAULT 1 COMMENT '折旧方法 1-平均年限',
    accumulated_depreciation DECIMAL(15,2) DEFAULT 0 COMMENT '累计折旧',
    net_value               DECIMAL(15,2) DEFAULT 0 COMMENT '净值',
    status                  TINYINT       NOT NULL DEFAULT 1 COMMENT '0-停用 1-在用 2-已处置',
    last_deprec_date        DATE          COMMENT '最后折旧日期',
    remark                  VARCHAR(500),
    create_time             DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time             DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by               BIGINT,
    update_by               BIGINT,
    deleted                 TINYINT       NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_account_set (account_set_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='固定资产';

CREATE TABLE IF NOT EXISTS fin_close_period (
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    account_set_id BIGINT       NOT NULL,
    account_period CHAR(6)      NOT NULL COMMENT '会计期间 yyyyMM',
    status         TINYINT      NOT NULL DEFAULT 0 COMMENT '0-未结 1-已结',
    close_time     DATETIME     COMMENT '结账时间',
    close_by       BIGINT       COMMENT '结账人',
    remark         VARCHAR(500),
    create_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by      BIGINT,
    update_by      BIGINT,
    deleted        TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_account_set_period (account_set_id, account_period)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='月结记录';
