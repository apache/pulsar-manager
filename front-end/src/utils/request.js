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

// create an axios instance
const service = axios.create({
  baseURL: process.env.BASE_API, // api 的 base_url
  timeout: 5000 // request timeout
})

// request interceptor
service.interceptors.request.use(
  config => {
    // Do something before request is sent
    if (store.getters.token) {
      config.headers['token'] = getToken()
    }
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
  // response => response,
  /**
   * 下面的注释为通过在response里，自定义code来标示请求状态
   * 当code返回如下情况则说明权限有问题，登出并返回到登录页
   * 如想通过 xmlhttprequest 来状态码标识 逻辑可写在下面error中
   * 以下代码均为样例，请结合自生需求加以修改，若不需要，则可删除
   */
  response => {
    // const res = response.data
    if (response.status < 500 && response.status >= 200) {
      return response
    } else {
      return Promise.reject('error')
    }
  },
  error => {
    console.log('err' + error) // for debug
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
    } else {
      message = error.response.data.reason
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
