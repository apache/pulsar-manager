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
/**
 * Created by jiachenpan on 16/11/18.
 */

export function isvalidUsername(str) {
  const valid_map = ['admin', 'editor']
  return valid_map.indexOf(str.trim()) >= 0
}

/* 合法uri*/
export function validateURL(textval) {
  const urlregex = /^(https?|ftp):\/\/([a-zA-Z0-9.-]+(:[a-zA-Z0-9.&%$-]+)*@)*((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]?)(\.(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])){3}|([a-zA-Z0-9-]+\.)*[a-zA-Z0-9-]+\.(com|edu|gov|int|mil|net|org|biz|arpa|info|name|pro|aero|coop|museum|[a-zA-Z]{2}))(:[0-9]+)*(\/($|[a-zA-Z0-9.,?'\\+&%$#=~_-]+))*$/
  return urlregex.test(textval)
}

/* 小写字母*/
export function validateLowerCase(str) {
  const reg = /^[a-z]+$/
  return reg.test(str)
}

/* 大写字母*/
export function validateUpperCase(str) {
  const reg = /^[A-Z]+$/
  return reg.test(str)
}

/* 大小写字母*/
export function validateAlphabets(str) {
  const reg = /^[A-Za-z]+$/
  return reg.test(str)
}

/**
 * validate email
 * @param email
 * @returns {boolean}
 */
export function validateEmail(email) {
  const re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
  return re.test(email)
}

/**
 * validate obj is empty
 * @param object
 * @returns {boolean}
 */
export function validateEmpty(obj) {
  if (typeof obj === 'undefined' || obj === null || obj === '') {
    return true
  }
  return false
}

export function validateServiceUrl(expectedProtocol, allowEmpty) {
  return (rule, value, callback) => {
    if (!value || value.length === 0) {
      if (allowEmpty) {
        callback()
        return
      }
    }
    try {
      const parsedUrl = new URL(value)
      if (parsedUrl.protocol !== expectedProtocol) {
        callback(new Error('Please input an `' + expectedProtocol + '` service URL'))
        return
      }
      callback()
    } catch (e) {
      callback(new Error('Please input a valid service URL'))
    }
  }
}

export function validateSizeString(str) {
  var last = str.charAt(str.length - 1)
  var subStr = str.substring(0, str.length - 1)
  switch (last) {
    case 'k':
    case 'K':
      return Number(subStr) * 1024

    case 'm':
    case 'M':
      return Number(subStr) * 1024 * 1024

    case 'g':
    case 'G':
      return Number(subStr) * 1024 * 1024 * 1024

    case 't':
    case 'T':
      return Number(subStr) * 1024 * 1024 * 1024 * 1024

    default:
      return Number(str)
  }
}
