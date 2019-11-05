<!--

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

-->
<template>
  <el-breadcrumb class="app-breadcrumb" separator="/">
    <transition-group name="breadcrumb">
      <el-breadcrumb-item v-for="(item,index) in levelList" :key="item.path">
        <span v-if="item.redirect==='noredirect'||index==levelList.length-1" class="no-redirect">{{ generateTitle(item.meta.title) }}</span>
        <router-link v-else :to="item.path">{{ generateTitle(item.meta.title) }}</router-link>
      </el-breadcrumb-item>
    </transition-group>
  </el-breadcrumb>
</template>

<script>
import { generateTitle } from '@/utils/i18n'
import pathToRegexp from 'path-to-regexp'

export default {
  data() {
    return {
      levelList: null
    }
  },
  watch: {
    $route() {
      this.getBreadcrumb()
    }
  },
  created() {
    this.getBreadcrumb()
  },
  methods: {
    generateTitle,
    getBreadcrumb() {
      const { params } = this.$route
      const matched = this.$route.matched.filter(item => {
        if (item.name) {
          // To solve this problem https://github.com/PanJiaChen/vue-element-admin/issues/561
          var toPath = pathToRegexp.compile(item.path)
          item.path = toPath(params)
          return true
        }
      })
      var route = []
      for (var i = 0; i < matched.length; i++) {
        var path = matched[i].path
        if (path === '/management') {
          route.push({
            'path': matched[i].path,
            'redirect': matched[i].redirect,
            'meta': {
              'title': this.$i18n.t('breadcrumb.management')
            }
          })
        }
        var pathList = path.split('/')
        if (pathList.indexOf('brokers') === 2) {
          route.push({
            'path': '/management/clusters',
            'meta': {
              'title': this.$i18n.t('breadcrumb.clusters')
            },
            'redirect': matched[i].redirect
          })
          route.push({
            'path': '/management/clusters/' + pathList[3] + '/cluster?tab=brokers',
            'meta': {
              'title': this.$i18n.t('breadcrumb.brokers')
            },
            'redirect': matched[i].redirect
          })
          route.push({
            'path': matched[i].path,
            'meta': {
              'title': this.$i18n.t('breadcrumb.brokerInfo')
            },
            'redirect': matched[i].redirect
          })
        } else if (pathList.indexOf('namespaceIsolationPolicy') === 5) {
          route.push({
            'path': '/management/clusters',
            'meta': {
              'title': this.$i18n.t('breadcrumb.clusters')
            },
            'redirect': matched[i].redirect
          })
          route.push({
            'path': '/management/clusters/' + pathList[3] + '/cluster?tab=isolationPolicies',
            'meta': {
              'title': this.$i18n.t('breadcrumb.isolationPolicies')
            },
            'redirect': matched[i].redirect
          })
          route.push({
            'path': matched[i].path,
            'meta': {
              'title': this.$i18n.t('breadcrumb.isolationPolicyInfo')
            },
            'redirect': matched[i].redirect
          })
        } else if (pathList.indexOf('failureDomainName') === 5) {
          route.push({
            'path': '/management/clusters',
            'meta': {
              'title': this.$i18n.t('breadcrumb.clusters')
            },
            'redirect': matched[i].redirect
          })
          route.push({
            'path': '/management/clusters/' + pathList[3] + '/cluster?tab=failureDomains',
            'meta': {
              'title': this.$i18n.t('breadcrumb.failureDomains')
            },
            'redirect': matched[i].redirect
          })
          route.push({
            'path': matched[i].path,
            'meta': {
              'title': this.$i18n.t('breadcrumb.failureDomainInfo')
            },
            'redirect': matched[i].redirect
          })
        } else if (pathList.indexOf('cluster') === 4) {
          route.push({
            'path': '/management/clusters',
            'meta': {
              'title': this.$i18n.t('breadcrumb.clusters')
            },
            'redirect': matched[i].redirect
          })
          route.push({
            'path': matched[i].path,
            'meta': {
              'title': this.$i18n.t('breadcrumb.clusterInfo')
            },
            'redirect': matched[i].redirect
          })
        } else if (pathList.indexOf('clusters') === 2) {
          route.push({
            'path': '/management/clusters',
            'meta': {
              'title': this.$i18n.t('breadcrumb.clusters')
            },
            'redirect': matched[i].redirect
          })
        } else if (pathList.indexOf('subscriptions') === 2) {
          route.push({
            'path': '/management/tenants',
            'meta': {
              'title': this.$i18n.t('breadcrumb.tenants')
            },
            'redirect': matched[i].redirect
          })
          route.push({
            'path': '/management/tenants/tenantInfo/' + pathList[4] + '?tab=namespaces',
            'redirect': matched[i].redirect,
            'meta': {
              'title': this.$i18n.t('breadcrumb.namespaces')
            }
          })
          route.push({
            'path': '/management/namespaces/' + pathList[4] + '/' + pathList[5] + '/namespace' + '?tab=topics',
            'redirect': matched[i].redirect,
            'meta': {
              'title': this.$i18n.t('breadcrumb.topics')
            }
          })
          route.push({
            'path': '/management/topics/' + pathList[3] + '/' + pathList[4] + '/' + pathList[5] + '/' + pathList[6] + '/topic',
            'redirect': matched[i].redirect,
            'meta': {
              'title': this.$i18n.t('breadcrumb.subscriptions')
            }
          })
          route.push({
            'path': matched[i].path,
            'redirect': matched[i].redirect,
            'meta': {
              'title': this.$i18n.t('breadcrumb.subscriptionInfo')
            }
          })
        } else if (pathList.indexOf('topics') === 2) {
          route.push({
            'path': '/management/tenants',
            'meta': {
              'title': this.$i18n.t('breadcrumb.tenants')
            },
            'redirect': matched[i].redirect
          })
          route.push({
            'path': '/management/tenants/tenantInfo/' + pathList[4] + '?tab=namespaces',
            'redirect': matched[i].redirect,
            'meta': {
              'title': this.$i18n.t('breadcrumb.namespaces')
            }
          })
          route.push({
            'path': '/management/namespaces/' + pathList[4] + '/' + pathList[5] + '/namespace' + '?tab=topics',
            'redirect': matched[i].redirect,
            'meta': {
              'title': this.$i18n.t('breadcrumb.topics')
            }
          })
          route.push({
            'path': matched[i].path,
            'redirect': matched[i].redirect,
            'meta': {
              'title': this.$i18n.t('breadcrumb.topicInfo')
            }
          })
        } else if (pathList.indexOf('namespaces') === 2) {
          route.push({
            'path': '/management/tenants',
            'redirect': matched[i].redirect,
            'meta': {
              'title': this.$i18n.t('breadcrumb.tenants')
            }
          })
          route.push({
            'path': '/management/tenants/tenantInfo/' + pathList[3] + '?tab=namespaces',
            'redirect': matched[i].redirect,
            'meta': {
              'title': this.$i18n.t('breadcrumb.namespaces')
            }
          })
          route.push({
            'path': matched[i].path,
            'redirect': matched[i].redirect,
            'meta': {
              'title': this.$i18n.t('breadcrumb.namespaceInfo')
            }
          })
        } else if (pathList.indexOf('tenantInfo') === 3) {
          route.push({
            'path': '/management/tenants',
            'redirect': matched[i].redirect,
            'meta': {
              'title': this.$i18n.t('breadcrumb.tenants')
            }
          })
          route.push({
            'path': matched[i].path,
            'redirect': matched[i].redirect,
            'meta': {
              'title': this.$i18n.t('breadcrumb.tenantInfo')
            }
          })
        }
      }
      // if (first && first.name.trim().toLocaleLowerCase() !== 'Management'.toLocaleLowerCase()) {
      //   matched = [{ path: '/management', meta: { title: 'management' }}].concat(matched)
      // }
      this.levelList = route
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  .app-breadcrumb.el-breadcrumb {
    display: inline-block;
    font-size: 14px;
    line-height: 50px;
    margin-left: 10px;
    .no-redirect {
      color: #97a8be;
      cursor: text;
    }
  }
</style>
