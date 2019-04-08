import request from '@/utils/request'

const BASE_URL_V2 = '/admin/v2'

export function racksInfo() {
  return request({
    url: BASE_URL_V2 + `/bookies/racks-info`,
    method: 'get'
  })
}

export function racksInfoByBookie(bookie) {
  return request({
    url: BASE_URL_V2 + `/bookies/racks-info/${bookie}`,
    method: 'get'
  })
}

export function updateRacksByBookie(bookie, group, data) {
  return request({
    url: BASE_URL_V2 + `/bookies/racks-info/${bookie}?group=${group}`,
    method: 'post',
    data
  })
}

export function deleteRacksByBookie(bookie) {
  return request({
    url: BASE_URL_V2 + `/bookies/racks-info/${bookie}`,
    method: 'delete'
  })
}
