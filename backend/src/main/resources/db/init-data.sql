USE farmer_db;

-- ============================================================
-- 初始化角色
-- ============================================================
INSERT IGNORE INTO sys_role (id, role_name, role_code, description, status) VALUES
(1, '超级管理员', 'ROLE_SUPER_ADMIN', '系统超级管理员', 1),
(2, '县级管理员',  'ROLE_COUNTY_ADMIN', '县级财务管理员', 1),
(3, '乡镇管理员',  'ROLE_TOWNSHIP_ADMIN', '乡镇财务管理员', 1),
(4, '村级财务员',  'ROLE_VILLAGE_FINANCE', '村委财务员', 1);

-- ============================================================
-- 初始化用户 (密码: admin123, BCrypt加密)
-- ============================================================
INSERT IGNORE INTO sys_user (id, username, password, real_name, phone, role_id, status) VALUES
(1, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAum', '超级管理员', '13800000000', 1, 1);

-- ============================================================
-- 初始化菜单权限
-- ============================================================
INSERT IGNORE INTO sys_permission (id, parent_id, perm_name, perm_code, path, component, icon, type, sort) VALUES
-- 一级目录
(1,  0, '财务一张图',   NULL,                    '/finance-map',     NULL,                           'Map',        0, 1),
(2,  0, '村委财务管理', NULL,                    '/village-finance',  NULL,                           'Money',      0, 2),
-- 财务一张图子菜单
(10, 1, '财务概览',     'finance:map:overview',  '/finance-map/overview', 'finance-map/FinanceMap', 'DataBoard',  1, 1),
-- 村委财务管理子菜单
(20, 2, '账套管理',     'finance:account-set',   '/village-finance/account-set',  'village-finance/settings/AccountSet',   'Setting',   1, 1),
(21, 2, '出纳管理',     NULL,                    '/village-finance/cashier',       NULL,                                     'Wallet',    0, 2),
(22, 2, '凭证管理',     'finance:voucher',       '/village-finance/voucher',       'village-finance/voucher/VoucherList',    'Document',  1, 3),
(23, 2, '账簿查询',     NULL,                    '/village-finance/ledger',        NULL,                                     'Notebook',  0, 4),
(24, 2, '报表中心',     NULL,                    '/village-finance/report',        NULL,                                     'PieChart',  0, 5),
(25, 2, '资产管理',     'finance:asset',         '/village-finance/asset',         'village-finance/asset/AssetList',        'Box',       1, 6),
-- 出纳子菜单
(30, 21, '银行日记账',  'finance:journal:bank',  '/village-finance/cashier/bank',  'village-finance/cashier/BankJournal',   'Bank',      1, 1),
(31, 21, '现金日记账',  'finance:journal:cash',  '/village-finance/cashier/cash',  'village-finance/cashier/CashJournal',   'Cash',      1, 2),
-- 账簿子菜单
(40, 23, '序时账',      'finance:ledger:seq',    '/village-finance/ledger/sequential',     'village-finance/ledger/SequentialLedger', 'List',     1, 1),
(41, 23, '科目余额表',  'finance:ledger:balance','village-finance/ledger/subject-balance', 'village-finance/ledger/SubjectBalance',   'Grid',     1, 2),
(42, 23, '明细账',      'finance:ledger:detail', '/village-finance/ledger/detail',         'village-finance/ledger/DetailLedger',     'Files',    1, 3),
-- 报表子菜单
(50, 24, '资产负债表',  'finance:report:balance','village-finance/report/balance-sheet',  'village-finance/report/BalanceSheet',     'DataLine', 1, 1),
(51, 24, '收益分配表',  'finance:report:income', '/village-finance/report/income',        'village-finance/report/IncomeStatement',  'TrendCharts', 1, 2);

-- ============================================================
-- 超级管理员拥有所有权限
-- ============================================================
INSERT IGNORE INTO sys_role_permission (role_id, perm_id)
SELECT 1, id FROM sys_permission;

-- ============================================================
-- 初始化行政区划（示例数据）
-- ============================================================
INSERT IGNORE INTO sys_region (id, parent_id, region_name, region_code, level, sort) VALUES
(1,  0,  '示例省',        '510000', 1, 1),
(2,  1,  '示例市',        '510100', 2, 1),
(3,  2,  '示例县',        '510101', 3, 1),
(4,  3,  '示例镇',        '510101001', 4, 1),
(5,  3,  '第二镇',        '510101002', 4, 2),
(10, 4,  '一村',          '510101001001', 5, 1),
(11, 4,  '二村',          '510101001002', 5, 2),
(12, 4,  '三村',          '510101001003', 5, 3),
(13, 5,  '四村',          '510101002001', 5, 1),
(14, 5,  '五村',          '510101002002', 5, 2);

-- ============================================================
-- 初始化财务一张图示例数据
-- ============================================================
INSERT IGNORE INTO fin_map_overview (region_id, stat_month, county_count, township_count, village_total, unclosed_count, no_account_count) VALUES
(3, '202601', 1, 2, 5, 1, 0),
(3, '202502', 1, 2, 5, 2, 1);

INSERT IGNORE INTO fin_village_income (village_id, region_id, stat_month, total_income, business_income, subsidy_income, investment_income, other_income) VALUES
(10, 3, '202601', 280000, 150000, 80000, 30000, 20000),
(11, 3, '202601', 95000,  60000,  25000, 5000,  5000),
(12, 3, '202601', 42000,  25000,  15000, 0,     2000),
(13, 3, '202601', 180000, 100000, 60000, 15000, 5000),
(14, 3, '202601', 35000,  20000,  12000, 0,     3000);

INSERT IGNORE INTO fin_indicators (village_id, region_id, stat_month, monetary_funds, receivables, inventory, fixed_assets, payables, business_income, business_expense, management_fee, current_year_profit, undistributed_profit) VALUES
(10, 3, '202601', 520000, 30000, 15000, 380000, 20000, 150000, 80000, 25000, 45000, 120000),
(11, 3, '202601', 180000, 10000, 5000,  95000,  8000,  60000,  35000, 12000, 13000, 30000),
(12, 3, '202601', 65000,  5000,  2000,  40000,  3000,  25000,  18000, 5000,  2000,  8000);

-- ============================================================
-- 初始化账套及会计科目（一村示例）
-- ============================================================
INSERT IGNORE INTO fin_account_set (id, village_id, region_id, set_name, account_year, currency, status) VALUES
(1, 10, 3, '一村2026年度账套', 2026, 'CNY', 1);

-- 会计科目（村集体经济组织会计制度科目）
INSERT IGNORE INTO fin_account (account_set_id, parent_id, account_code, account_name, account_type, balance_direction, level, is_leaf, init_balance) VALUES
-- 资产类
(1, 0, '1001', '货币资金',     1, 'debit', 1, 0, 0),
(1, 0, '1002', '银行存款',     1, 'debit', 2, 1, 520000),
(1, 0, '1003', '现金',         1, 'debit', 2, 1, 5000),
(1, 0, '1101', '应收账款',     1, 'debit', 1, 1, 30000),
(1, 0, '1201', '存货',         1, 'debit', 1, 1, 15000),
(1, 0, '1501', '固定资产',     1, 'debit', 1, 1, 380000),
(1, 0, '1502', '累计折旧',     1, 'credit',1, 1, 0),
-- 负债类
(1, 0, '2001', '应付账款',     2, 'credit',1, 1, 20000),
(1, 0, '2101', '应付款项',     2, 'credit',1, 1, 0),
-- 所有者权益
(1, 0, '3001', '资本',         3, 'credit',1, 1, 0),
(1, 0, '3101', '公积公益金',   3, 'credit',1, 1, 0),
(1, 0, '3201', '未分配收益',   3, 'credit',1, 1, 120000),
-- 收入类
(1, 0, '4001', '经营收入',     4, 'credit',1, 1, 0),
(1, 0, '4002', '发包及上交收入',4,'credit',1, 1, 0),
(1, 0, '4003', '投资收益',     4, 'credit',1, 1, 0),
(1, 0, '4004', '补助收入',     4, 'credit',1, 1, 0),
(1, 0, '4005', '其他收入',     4, 'credit',1, 1, 0),
-- 支出类
(1, 0, '5001', '经营支出',     5, 'debit', 1, 1, 0),
(1, 0, '5002', '管理费用',     5, 'debit', 1, 1, 0),
(1, 0, '5003', '其他支出',     5, 'debit', 1, 1, 0);

-- 初始化资金账户
INSERT IGNORE INTO fin_account_book (account_set_id, book_name, book_type, bank_account, bank_name, init_balance, current_balance, status) VALUES
(1, '农业银行基本户', 1, '62284801000001', '中国农业银行示例支行', 520000, 520000, 1),
(1, '现金备用金',     2, NULL,             NULL,                   5000,   5000,   1);
