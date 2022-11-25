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
import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/* Layout */
import Layout from '@/views/layout/Layout'

/** note: Submenu only appear when children.length>=1
 *  detail see  https://panjiachen.github.io/vue-element-admin-site/guide/essentials/router-and-nav.html
 **/

/**
* hidden: true                   if `hidden:true` will not show in the sidebar(default is false)
* alwaysShow: true               if set true, will always show the root menu, whatever its child routes length
*                                if not set alwaysShow, only more than one route under the children
*                                it will becomes nested mode, otherwise not show the root menu
* redirect: noredirect           if `redirect:noredirect` will no redirect in the breadcrumb
* name:'router-name'             the name is used by <keep-alive> (must set!!!)
* meta : {
    roles: ['admin','editor']     will control the page roles (you can set multiple roles)
    title: 'title'               the name show in submenu and breadcrumb (recommend set)
    icon: 'svg-name'             the icon show in the sidebar,
    noCache: true                if true ,the page will no be cached(default is false)
  }
**/
export const constantRouterMap = [
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:path*',
        component: () => import('@/views/redirect/index')
      }
    ]
  },
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true
  },
  {
    path: '/callback',
    component: () => import('@/views/callback/index'),
    hidden: true
  },
  {
    path: '/auth-redirect',
    component: () => import('@/views/login/authredirect'),
    hidden: true
  },
  {
    path: '/404',
    component: () => import('@/views/errorPage/404'),
    hidden: true
  },
  {
    path: '/401',
    component: () => import('@/views/errorPage/401'),
    hidden: true
  }
]

export default new Router({
  // mode: 'history', // require service support
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRouterMap
})

export const asyncRouterMap = [

  {
    path: '/environments',
    component: () => import('@/views/management/environments/index'),
    hidden: true,
    meta: {
      roles: ['super', 'admin']
    }
  },
  {
    path: '',
    component: Layout,
    redirect: 'management/tenants',
    meta: {
      title: 'Dashboard',
      icon: 'dashboard',
      roles: ['super']
    },
    hidden: true,
    children: [
      {
        path: 'prometheus',
        component: () => import('@/views/dashboard/prometheus/index'),
        name: 'Prometheus',
        meta: { title: 'prometheus', noCache: true }
      },
      {
        path: 'grafana',
        component: () => import('@/views/dashboard/grafana/index'),
        name: 'Grafana',
        meta: { title: 'grafana', noCache: true }
      }
    ]
  },
  {
    path: '/management',
    component: Layout,
    name: 'Management',
    redirect: 'management/roles',
    meta: {
      title: 'Management',
      icon: 'component',
      roles: ['super', 'admin']
    },
    children: [
      {
        path: 'clusters',
        component: () => import('@/views/management/clusters/index'),
        name: 'Clusters',
        meta: {
          title: 'Clusters',
          noCache: true,
          roles: ['super']
        },
        hidden: true
      },
      {
        path: 'clusters/:cluster/cluster',
        component: () => import('@/views/management/clusters/cluster'),
        name: 'ClusterInfo',
        meta: {
          title: 'ClusterInfo',
          noCache: true,
          roles: ['super']
        },
        hidden: true
      },
      {
        path: 'clusters/:cluster/:failureDomainName/failureDomainName',
        component: () => import('@/views/management/clusters/failureDomain'),
        name: 'FailureDomainInfo',
        meta: {
          title: 'FailureDomainInfo',
          noCache: true,
          roles: ['super']
        },
        hidden: true
      },
      {
        path: 'clusters/:cluster/:namespaceIsolation/namespaceIsolationPolicy',
        component: () => import('@/views/management/namespaceIsolations/namespaceIsolationPolicy'),
        name: 'NamespaceIsolationPolicy',
        meta: {
          title: 'NamespaceIsolationPolicy',
          noCache: true,
          roles: ['super']
        },
        hidden: true
      },
      {
        path: 'brokers/:cluster/:broker/broker',
        component: () => import('@/views/management/brokers/broker'),
        name: 'BrokerInfo',
        meta: {
          title: 'BrokerInfo',
          noCache: true,
          roles: ['super']
        },
        hidden: true
      },
      {
        path: 'tenants',
        component: () => import('@/views/management/tenants/index'),
        name: 'Tenants',
        meta: {
          title: 'Tenants',
          noCache: true,
          roles: ['super']
        }
      },
      {
        path: 'tenants/tenantInfo/:tenant',
        component: () => import('@/views/management/tenants/tenant'),
        name: 'Tenant',
        meta: {
          title: 'TenantInfo',
          noCache: true,
          roles: ['super']
        },
        hidden: true
      },
      {
        path: 'admin/tenants/tenantInfo',
        component: () => import('@/views/management/admin/tenants/tenant'),
        name: 'Tenant Admin',
        meta: {
          title: 'TenantInfo',
          noCache: true,
          roles: ['super', 'admin']
        },
        hidden: true
      },
      {
        path: 'tenants/tenantInfo/public?tab=namespaces',
        name: 'Namespaces',
        meta: {
          title: 'Namespaces',
          noCache: true,
          roles: ['super']
        }
      },
      {
        path: 'namespaces/public/default/namespace?tab=topics',
        name: 'Topics',
        meta: {
          title: 'Topics',
          noCache: true,
          roles: ['super']
        }
      },
      {
        path: 'topics/:persistent/:tenant/:namespace/:topic/topic',
        component: () => import('@/views/management/topics/topic'),
        name: 'TopicInfo',
        meta: {
          title: 'TopicInfo',
          noCache: true,
          roles: ['super', 'admin']
        },
        hidden: true
      },
      {
        path: 'topics/:persistent/:tenant/:namespace/:topic/partitionedTopic',
        component: () => import('@/views/management/topics/partitionedTopic'),
        name: 'ParititionTopicInfo',
        meta: {
          title: 'ParititionTopicInfo',
          noCache: true,
          roles: ['super', 'admin']
        },
        hidden: true
      },
      {
        path: 'subscriptions/:persistent/:tenant/:namespace/:topic/:subscription/subscription',
        component: () => import('@/views/management/subscriptions/subscription'),
        name: 'SubscriptionInfo',
        meta: {
          title: 'SubscriptionInfo',
          noCache: true,
          roles: ['super', 'admin']
        },
        hidden: true
      },
      {
        path: 'namespaces/:tenant',
        name: 'NamespacesTenant',
        meta: {
          title: 'Namespaces',
          noCache: true,
          roles: ['super']
        },
        hidden: true
      },
      {
        path: 'namespaces/:tenant/:namespace/namespace',
        component: () => import('@/views/management/namespaces/namespace'),
        name: 'NamespacesInfo',
        meta: {
          title: 'NamespacesInfo',
          noCache: true,
          roles: ['super', 'admin']
        },
        hidden: true
      },
      {
        path: 'functions',
        component: () => import('@/views/management/functions'),
        name: 'Functions',
        meta: {
          title: 'Functions',
          noCache: true,
          roles: ['super']
        },
        hidden: true
      },
      {
        path: 'sources',
        component: () => import('@/views/management/sources'),
        name: 'Sources',
        meta: {
          title: 'Sources',
          noCache: true,
          roles: ['super']
        },
        hidden: true
      },
      {
        path: 'sinks',
        component: () => import('@/views/management/sinks'),
        name: 'Sinks',
        meta: {
          title: 'Sinks',
          noCache: true,
          roles: ['super']
        },
        hidden: true
      },
      {
        path: 'bookies',
        component: () => import('@/views/management/bookies'),
        name: 'Bookies',
        meta: {
          title: 'Bookies',
          noCache: true,
          roles: ['super']
        },
        hidden: true
      },
      {
        path: '/users',
        component: () => import('@/views/management/users/index'),
        name: 'Users',
        meta: {
          title: 'Users',
          noCache: true,
          roles: ['super']
        },
        hidden: true
      },
      {
        path: '/tokens',
        component: () => import('@/views/management/tokens/index'),
        name: 'Tokens',
        meta: {
          title: 'Tokens',
          noCache: true,
          roles: ['super', 'admin']
        },
        hidden: false
      }
    ]
  },
  {
    path: 'external-link',
    component: Layout,
    children: [
      {
        path: 'https://pulsar.apache.org',
        meta: { title: 'Apache Pulsar', icon: 'link' }
      }
    ]
  },
  { path: '*', redirect: '/404', hidden: true }
]
