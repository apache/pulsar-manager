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
export default {
  items: [
    {
      name: 'Dashboard',
      url: '/dashboard',
      icon: 'icon-speedometer',
      badge: {
        variant: 'info',
        text: 'NEW',
      },
    },
    {
      title: true,
      name: 'Management',
      wrapper: {            // optional wrapper object
        element: '',        // required valid HTML5 element tag
        attributes: {}        // optional valid JS object with JS API naming ex: { className: "my-class", style: { fontFamily: "Verdana" }, id: "my-id"}
      },
      class: ''             // optional class names space delimited list for title item ex: "text-center"
    },
    {
      name: 'Connectors',
      url: '/management/connectors',
      icon: 'icon-drop',
    },
    {
      name: 'Topics',
      url: '/management/topics',
      icon: 'icon-pencil',
    },
    {
      name: 'Functions',
      url: '/management/functions',
      icon: 'icon-pencil',
    },
    {
      name: 'SQL',
      url: '/management/sql',
      icon: 'icon-pencil',
    },
    {
      title: true,
      name: 'Monitoring',
      wrapper: {
        element: '',
        attributes: {},
      },
    },
    {
      name: 'System Health',
      url: '/monitoring/health',
      icon: 'icon-puzzle',
      children: [
      ],
    },
    {
      name: 'Namespaces',
      url: '/monitoring/namespaces',
      icon: 'icon-cursor',
      children: [
      ],
    },
    {
      title: true,
      name: 'Alerts',
      wrapper: {
        element: '',
        attributes: {},
      },
    },
    {
      name: 'Overview',
      url: '/alerts/overview',
      icon: 'icon-star',
      children: [
      ],
    },
  ],
};
