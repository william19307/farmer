import request from './request'

// 账套
export const getAccountSets = (params) => request.get('/finance/account-set', { params })
export const createAccountSet = (data) => request.post('/finance/account-set', data)
export const updateAccountSet = (data) => request.put('/finance/account-set', data)
export const getAccounts = (accountSetId) => request.get(`/finance/account-set/${accountSetId}/accounts`)

// 凭证
export const getVouchers = (params) => request.get('/finance/voucher', { params })
export const getVoucherDetail = (id) => request.get(`/finance/voucher/${id}`)
export const createVoucher = (data) => request.post('/finance/voucher', data)
export const updateVoucher = (data) => request.put('/finance/voucher', data)
export const deleteVoucher = (id) => request.delete(`/finance/voucher/${id}`)
export const auditVoucher = (id) => request.post(`/finance/voucher/${id}/audit`)
export const unauditVoucher = (id) => request.post(`/finance/voucher/${id}/unaudit`)
export const batchAuditVouchers = (ids) => request.post('/finance/voucher/batch-audit', ids)

// 日记账
export const getJournals = (params) => request.get('/finance/journal', { params })
export const createJournal = (data) => request.post('/finance/journal', data)
export const updateJournal = (data) => request.put('/finance/journal', data)
export const deleteJournal = (id) => request.delete(`/finance/journal/${id}`)
export const generateVoucherFromJournal = (id) => request.post(`/finance/journal/${id}/generate-voucher`)

// 账簿
export const getSubjectBalance = (params) => request.get('/finance/ledger/subject-balance', { params })
export const getSequentialLedger = (params) => request.get('/finance/ledger/sequential', { params })
export const getDetailLedger = (params) => request.get('/finance/ledger/detail', { params })

// 固定资产
export const getAssets = (params) => request.get('/finance/asset', { params })
export const createAsset = (data) => request.post('/finance/asset', data)
export const updateAsset = (data) => request.put('/finance/asset', data)
export const deleteAsset = (id) => request.delete(`/finance/asset/${id}`)
export const depreciate = (params) => request.post('/finance/asset/depreciate', null, { params })
