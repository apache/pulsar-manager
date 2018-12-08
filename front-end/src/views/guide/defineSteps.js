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
const steps = [
  {
    element: '.hamburger-container',
    popover: {
      title: 'Hamburger',
      description: 'Open && Close sidebar',
      position: 'bottom'
    }
  },
  {
    element: '.breadcrumb-container',
    popover: {
      title: 'Breadcrumb',
      description: 'Indicate the current page location',
      position: 'bottom'
    }
  },
  {
    element: '.screenfull',
    popover: {
      title: 'Screenfull',
      description: 'Bring the page into fullscreen',
      position: 'left'
    }
  },
  {
    element: '.international-icon',
    popover: {
      title: 'Switch language',
      description: 'Switch the system language',
      position: 'left'
    }
  },
  {
    element: '.theme-switch',
    popover: {
      title: 'Theme Switch',
      description: 'Custom switch system theme',
      position: 'left'
    }
  },
  {
    element: '.tags-view-container',
    popover: {
      title: 'Tags view',
      description: 'The history of the page you visited',
      position: 'bottom'
    }
  }
]

export default steps
