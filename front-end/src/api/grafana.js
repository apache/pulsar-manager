import request from '@/utils/request'

const BASE_URL = '/api'

export function grafanaSearch(query, tag) {
  return request({
    headers: { 'Authorization': 'Bearer ' + process.env.GRAFANA_TOKEN },
    url: BASE_URL + `/search/?query=${query}&type=dash-db&tag=${tag}`,
    method: 'get'
  })
}
