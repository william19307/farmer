// ===== 农经一张图 · Mock 数据 =====

/** 顶部总览指标 */
export const overviewStats = {
  totalAssets:    { value: '428.6',  unit: '亿元', label: '村集体总资产' },
  totalResources: { value: '1204.3', unit: '万亩', label: '集体资源总量' },
  totalContracts: { value: '93,412', unit: '份',   label: '在管合同总数' },
  totalMembers:   { value: '312.8',  unit: '万人', label: '集体成员总数' },
  totalEquity:    { value: '87.2',   unit: '亿元', label: '股权总价值' },
}

/** 收入分布 · 各市县数据（用于地图标注） */
export const incomeMapData = [
  { name: '海口市',   value: 8420,  lng: 110.35, lat: 20.02, level: 'city' },
  { name: '三亚市',   value: 6830,  lng: 109.51, lat: 18.25, level: 'city' },
  { name: '儋州市',   value: 4210,  lng: 109.58, lat: 19.52, level: 'city' },
  { name: '文昌市',   value: 3640,  lng: 110.80, lat: 19.55, level: 'city' },
  { name: '琼海市',   value: 3180,  lng: 110.47, lat: 19.25, level: 'city' },
  { name: '万宁市',   value: 2950,  lng: 110.40, lat: 18.80, level: 'city' },
  { name: '东方市',   value: 2630,  lng: 108.65, lat: 19.10, level: 'city' },
  { name: '澄迈县',   value: 2290,  lng: 110.00, lat: 19.74, level: 'county' },
  { name: '定安县',   value: 1820,  lng: 110.36, lat: 19.68, level: 'county' },
  { name: '屯昌县',   value: 1560,  lng: 110.10, lat: 19.35, level: 'county' },
  { name: '临高县',   value: 2140,  lng: 109.69, lat: 19.91, level: 'county' },
  { name: '保亭县',   value: 1340,  lng: 109.70, lat: 18.64, level: 'county' },
  { name: '琼中县',   value: 1120,  lng: 109.84, lat: 19.03, level: 'county' },
  { name: '白沙县',   value: 1280,  lng: 109.45, lat: 19.23, level: 'county' },
  { name: '昌江县',   value: 1430,  lng: 109.05, lat: 19.30, level: 'county' },
  { name: '陵水县',   value: 1890,  lng: 110.04, lat: 18.51, level: 'county' },
  { name: '乐东县',   value: 1750,  lng: 109.17, lat: 18.75, level: 'county' },
]

/** 收入阈值分布 */
export const incomeThreshold = [
  { range: '5万以下',   count: 4823, pct: 28.3, color: '#4a90d9' },
  { range: '5-10万',   count: 3912, pct: 22.9, color: '#00c8ff' },
  { range: '10-20万',  count: 5134, pct: 30.1, color: '#00ff99' },
  { range: '20万以上',  count: 3201, pct: 18.7, color: '#ffb400' },
]

/** 财务健康度雷达 */
export const financialRadar = {
  indicators: [
    { name: '资产质量', max: 100 },
    { name: '负债管控', max: 100 },
    { name: '收益能力', max: 100 },
    { name: '流动性',   max: 100 },
    { name: '成员分配', max: 100 },
    { name: '合同履约', max: 100 },
  ],
  series: [
    { name: '全省均值', value: [72, 68, 65, 78, 60, 82], color: '#00c8ff' },
    { name: '优质村',   value: [91, 85, 88, 90, 83, 95], color: '#00ff99' },
  ]
}

/** 预警汇总 */
export const warningList = [
  { id: 1, level: 'high',   type: '资产异常',   region: '海口市·秀英区·长流村', desc: '固定资产账实不符，差额 82 万元', time: '2小时前' },
  { id: 2, level: 'high',   type: '债务风险',   region: '儋州市·那大镇·兰洋村', desc: '负债率超警戒线（78%）',           time: '5小时前' },
  { id: 3, level: 'mid',    type: '合同逾期',   region: '文昌市·文城镇·宝芳村', desc: '3份合同已逾期未续签',             time: '1天前'   },
  { id: 4, level: 'mid',    type: '收益下滑',   region: '三亚市·天涯区·白马村', desc: '同比下降 34%，连续2季度下滑',    time: '1天前'   },
  { id: 5, level: 'low',    type: '数据缺报',   region: '琼中县·红毛镇·什保村', desc: '季度财务报表未上报',             time: '3天前'   },
]

/** 合同到期提醒 */
export const contractExpiry = [
  { id: 1, name: '长流村土地承包合同',  region: '海口市',  expire: '2025-07-15', daysLeft: 22,  amount: '120万', status: 'urgent' },
  { id: 2, name: '那大集体鱼塘租赁',    region: '儋州市',  expire: '2025-07-28', daysLeft: 35,  amount: '45万',  status: 'urgent' },
  { id: 3, name: '宝芳村厂房租赁合同',  region: '文昌市',  expire: '2025-08-10', daysLeft: 48,  amount: '88万',  status: 'normal' },
  { id: 4, name: '兴隆旅游合作协议',    region: '万宁市',  expire: '2025-08-25', daysLeft: 63,  amount: '260万', status: 'normal' },
  { id: 5, name: '定城村林地租赁',      region: '定安县',  expire: '2025-09-01', daysLeft: 70,  amount: '32万',  status: 'normal' },
]
