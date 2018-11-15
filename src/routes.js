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
import React from 'react'
import Loadable from 'react-loadable'

import DefaultLayout from './containers/DefaultLayout'

function Loading() {
  return <div>Loading...</div>;
}

// Management Modules

const Clusters = Loadable({
  loader: () => import('./views/Management/Clusters'),
  loading: Loading
});

const Settings = Loadable({
  loader: () => import('./views/Management/Settings'),
  loading: Loading
});

const Users = Loadable({
  loader: () => import('./views/Management/Users'),
  loading: Loading
});

const Tenants = Loadable({
  loader: () => import('./views/Management/Tenants'),
  loading: Loading
});

const Namespaces = Loadable({
  loader: () => import('./views/Management/Namespaces'),
  loading: Loading
});

const Connectors = Loadable({
  loader: () => import('./views/Management/Connectors'),
  loading: Loading
});

const Topics = Loadable({
  loader: () => import('./views/Management/Topics'),
  loading: Loading
});

const Functions = Loadable({
  loader: () => import('./views/Management/Functions'),
  loading: Loading
});

const SQL = Loadable({
  loader: () => import('./views/Management/SQL'),
  loading: Loading
});

// Monitoring Modules

const Health = Loadable({
  loader: () => import('./views/Monitoring/Health'),
  loading: Loading
});

const routes = [
  { path: '/', exact: true, name: 'Home', component: DefaultLayout },
  // management modules
  { path: '/management/clusters', name: 'Clusters', component: Clusters },
  { path: '/management/settings', name: 'Settings', component: Settings },
  { path: '/management/users', name: 'Users', component: Users },
  { path: '/management/connectors', name: 'Connectors', component: Connectors },
  { path: '/management/tenants', name: 'Tenants', component: Tenants },
  { path: '/management/namespaces', name: 'Namespaces', component: Namespaces },
  { path: '/management/topics', name: 'Topics', component: Topics },
  { path: '/management/functions', name: 'Functions', component: Functions },
  { path: '/management/sql', name: 'SQL', component: SQL },
  // monitoring modules
  { path: '/monitoring/health', name: 'System Health', component: Health },
]

export default routes;
