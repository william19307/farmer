import request from './request'

export const getOverview = (params) => request.get('/map/finance/overview', { params })
export const getIncomeStats = (params) => request.get('/map/finance/income/stats', { params })
export const getIncomeDetailAbove = (params) => request.get('/map/finance/income/detail/above', { params })
export const getIncomeDetailBelow = (params) => request.get('/map/finance/income/detail/below', { params })
export const getIndicators = (params) => request.get('/map/finance/indicators', { params })
