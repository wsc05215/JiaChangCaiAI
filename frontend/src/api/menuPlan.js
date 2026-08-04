import axios from 'axios'

const BASE = '/meal-plan'

export function addMealPlan(data) {
  return axios.post(`${BASE}/add`, data)
}

export function removeMealPlan(id) {
  return axios.delete(`${BASE}/remove`, { params: { id } })
}

export function getMealPlanList(userId, date) {
  return axios.get(`${BASE}/list`, { params: { userId, date } })
}

export function getMealPlanDates(userId, year, month) {
  return axios.get(`${BASE}/dates`, { params: { userId, year, month } })
}
