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
              'title': 'Management'
            }
          })
        }
        var pathList = path.split('/')
        if (pathList.indexOf('brokers') === 2) {
          route.push({
            'path': '/management/clusters',
            'meta': {
              'title': 'Clusters'
            },
            'redirect': matched[i].redirect
          })
          route.push({
            'path': '/management/clusters/' + pathList[3] + '/cluster?tab=brokers',
            'meta': {
              'title': 'Brokers'
            },
            'redirect': matched[i].redirect
          })
          route.push({
            'path': matched[i].path,
            'meta': {
              'title': 'BrokerInfo'
            },
            'redirect': matched[i].redirect
          })
        } else if (pathList.indexOf('namespaceIsolationPolicy') === 5) {
          route.push({
            'path': '/management/clusters',
            'meta': {
              'title': 'Clusters'
            },
            'redirect': matched[i].redirect
          })
          route.push({
            'path': '/management/clusters/' + pathList[3] + '/cluster?tab=isolationPolicies',
            'meta': {
              'title': 'IsolationPolicies'
            },
            'redirect': matched[i].redirect
          })
          route.push({
            'path': matched[i].path,
            'meta': {
              'title': 'IsolationPolicieInfo'
            },
            'redirect': matched[i].redirect
          })
        } else if (pathList.indexOf('failureDomainName') === 5) {
          route.push({
            'path': '/management/clusters',
            'meta': {
              'title': 'Clusters'
            },
            'redirect': matched[i].redirect
          })
          route.push({
            'path': '/management/clusters/' + pathList[3] + '/cluster?tab=failureDomains',
            'meta': {
              'title': 'FailureDomains'
            },
            'redirect': matched[i].redirect
          })
          route.push({
            'path': matched[i].path,
            'meta': {
              'title': 'FailureDomainInfo'
            },
            'redirect': matched[i].redirect
          })
        } else if (pathList.indexOf('cluster') === 4) {
          route.push({
            'path': '/management/clusters',
            'meta': {
              'title': 'Clusters'
            },
            'redirect': matched[i].redirect
          })
          route.push({
            'path': matched[i].path,
            'meta': {
              'title': 'ClusterInfo'
            },
            'redirect': matched[i].redirect
          })
        } else if (pathList.indexOf('clusters') === 2) {
          route.push({
            'path': '/management/clusters',
            'meta': {
              'title': 'Clusters'
            },
            'redirect': matched[i].redirect
          })
        } else if (pathList.indexOf('subscriptions') === 2) {
          route.push({
            'path': '/management/tenants',
            'meta': {
              'title': 'Tenants'
            },
            'redirect': matched[i].redirect
          })
          route.push({
            'path': '/management/tenants/tenantInfo/' + pathList[3] + '?tab=namespaces',
            'redirect': matched[i].redirect,
            'meta': {
              'title': 'Namespaces'
            }
          })
          route.push({
            'path': '/management/namespaces/' + pathList[4] + '/' + pathList[5] + '/namespace' + '?tab=topics',
            'redirect': matched[i].redirect,
            'meta': {
              'title': 'Topics'
            }
          })
          route.push({
            'path': '/management/topics/' + pathList[3] + '/' + pathList[4] + '/' + pathList[5] + '/' + pathList[6] + '/topic',
            'redirect': matched[i].redirect,
            'meta': {
              'title': 'Subscriptions'
            }
          })
          route.push({
            'path': matched[i].path,
            'redirect': matched[i].redirect,
            'meta': {
              'title': 'SubscriptionInfo'
            }
          })
        } else if (pathList.indexOf('topics') === 2) {
          route.push({
            'path': '/management/tenants',
            'meta': {
              'title': 'Tenants'
            },
            'redirect': matched[i].redirect
          })
          route.push({
            'path': '/management/tenants/tenantInfo/' + pathList[3] + '?tab=namespaces',
            'redirect': matched[i].redirect,
            'meta': {
              'title': 'Namespaces'
            }
          })
          route.push({
            'path': '/management/namespaces/' + pathList[4] + '/' + pathList[5] + '/namespace' + '?tab=topics',
            'redirect': matched[i].redirect,
            'meta': {
              'title': 'Topics'
            }
          })
          route.push({
            'path': matched[i].path,
            'redirect': matched[i].redirect,
            'meta': {
              'title': 'TopicInfo'
            }
          })
        } else if (pathList.indexOf('namespaces') === 2) {
          route.push({
            'path': '/management/tenants',
            'redirect': matched[i].redirect,
            'meta': {
              'title': 'Tenants'
            }
          })
          route.push({
            'path': '/management/tenants/tenantInfo/' + pathList[3] + '?tab=namespaces',
            'redirect': matched[i].redirect,
            'meta': {
              'title': 'Namespaces'
            }
          })
          route.push({
            'path': matched[i].path,
            'redirect': matched[i].redirect,
            'meta': {
              'title': 'NamespaceInfo'
            }
          })
        } else if (pathList.indexOf('tenantInfo') === 3) {
          route.push({
            'path': '/management/tenants',
            'redirect': matched[i].redirect,
            'meta': {
              'title': 'Tenants'
            }
          })
          route.push({
            'path': matched[i].path,
            'redirect': matched[i].redirect,
            'meta': {
              'title': 'TenantInfo'
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
