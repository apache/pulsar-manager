/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import axios from 'axios'
// import { Message, MessageBox } from 'element-ui'
import { Message } from 'element-ui'
import store from '@/store'
import { getToken } from '@/utils/auth'
import { getName } from '@/utils/username'
import { getEnvironment } from '@/utils/environment'
import { getTenant } from '@/utils/tenant'
import router from '../router'
import { getCsrfToken } from '@/utils/csrfToken'

// create an axios instance
const service = axios.create({
  baseURL: process.env.BASE_API, // api 的 base_url
  timeout: 60000 // request timeout
})

// request interceptor
service.interceptors.request.use(
  config => {
    // Do something before request is sent
    if (store.getters.token) {
      config.headers['token'] = getToken()
    }
    config.headers['username'] = getName()
    config.headers['tenant'] = getTenant()
    config.headers['environment'] = getEnvironment()
    config.headers['X-XSRF-TOKEN'] = getCsrfToken()
    return config
  },
  error => {
    // Do something with request error
    console.log(error) // for debug
    Promise.reject(error)
  }
)

// response interceptor
service.interceptors.response.use(
  response => {
    // const res = response.data
    if (response.status < 500 && response.status >= 200) {
      return response
    } else {
      return Promise.reject('error')
    }
  },
  error => {
    let message = ''
    if (error.response.status === 404) {
      if (error.response.data.length <= 0) {
        message = 'not found'
      } else if (error.response.data.data.reason.length > 0 && error.response.data.data.reason.indexOf('NamespaceIsolationPolicies for cluster')) {
        return
      }
    } else if (error.response.status === 401) {
      if (error.response.data.hasOwnProperty('message') && error.response.data.message.indexOf('login') > 0) {
        store.dispatch('FedLogOut').then(() => {
          location.reload()
        })
      }
    } else if (error.response.status === 400) {
      if (error.response.data.hasOwnProperty('message') && error.response.data.message.indexOf('no active environment') > 0) {
        router.replace({
          path: '/environments'
        })
        return
      }
    } else if (error.response.data.hasOwnProperty('reason')) {
      message = error.response.data.reason
    } else {
      message = error.response.data
      if (message.indexOf('Trying to subscribe with incompatible') >= 0) {
        message = 'Incompatible schema detected while heartbeating'
      }
    }
    Message({
      message: message,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

export default service
