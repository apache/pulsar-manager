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
  },
  {
    path: '',
    component: Layout,
    redirect: 'dashboard',
    children: [
      {
        path: 'dashboard',
        component: () => import('@/views/dashboard/index'),
        name: 'Dashboard',
        meta: { title: 'dashboard', icon: 'dashboard', noCache: true }
      }
    ]
  },
  {
    path: '/management',
    component: Layout,
    redirect: 'management',
    meta: {
      title: 'Management',
      icon: 'component'
    },
    children: [
      {
        path: 'clusters',
        component: () => import('@/views/management/clusters/index'),
        name: 'Clusters',
        meta: { title: 'Clusters', noCache: true }
      },
      {
        path: 'tenants',
        component: () => import('@/views/management/tenants/index'),
        name: 'Tenants',
        meta: { title: 'Tenants', noCache: true }
      },
      {
        path: 'namespace',
        component: () => import('@/views/management/namespaces/index'),
        name: 'Namespaces',
        meta: { title: 'Namespaces', noCache: true }
      },
      {
        path: 'topics',
        component: () => import('@/views/management/topics/index'),
        name: 'Topics',
        meta: { title: 'Topics', noCache: true }
      },
      {
        path: 'subscriptions',
        component: () => import('@/views/management/subscriptions/index'),
        name: 'Subscriptions',
        meta: { title: 'Subscriptions', noCache: true }
      },
      {
        path: 'namespaces/:tenant',
        component: () => import('@/views/management/namespaces/index'),
        name: 'NamespacesTenant',
        meta: { title: 'Namespaces', noCache: true },
        hidden: true
      },
      {
        path: 'namespaces/:tenant/:namespace/policies',
        component: () => import('@/views/management/namespaces/policies'),
        name: 'NamespacesPolicies',
        meta: { title: 'NamespacesPolicies', noCache: true },
        hidden: true
      },
      {
        path: 'brokers',
        component: () => import('@/views/management/brokers'),
        name: 'Brokers',
        meta: { title: 'Brokers', noCache: true }
      },
      {
        path: 'functions',
        component: () => import('@/views/management/functions'),
        name: 'Functions',
        meta: { title: 'Functions', noCache: true }
      },
      {
        path: 'sources',
        component: () => import('@/views/management/sources'),
        name: 'Sources',
        meta: { title: 'Sources', noCache: true }
      },
      {
        path: 'sinks',
        component: () => import('@/views/management/sinks'),
        name: 'Sinks',
        meta: { title: 'Sinks', noCache: true }
      },
      {
        path: 'bookies',
        component: () => import('@/views/management/bookies'),
        name: 'Bookies',
        meta: { title: 'Bookies', noCache: true }
      }
    ]
  }
]

export default new Router({
  // mode: 'history', // require service support
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRouterMap
})

export const asyncRouterMap = [

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
