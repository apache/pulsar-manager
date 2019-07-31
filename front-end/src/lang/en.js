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
  route: {
    dashboard: 'Dashboard',
    introduction: 'Introduction',
    documentation: 'Documentation',
    guide: 'Guide',
    permission: 'Permission',
    pagePermission: 'Page Permission',
    directivePermission: 'Directive Permission',
    icons: 'Icons',
    components: 'Components',
    componentIndex: 'Introduction',
    tinymce: 'Tinymce',
    markdown: 'Markdown',
    jsonEditor: 'JSON Editor',
    dndList: 'Dnd List',
    splitPane: 'SplitPane',
    avatarUpload: 'Avatar Upload',
    dropzone: 'Dropzone',
    sticky: 'Sticky',
    countTo: 'CountTo',
    componentMixin: 'Mixin',
    backToTop: 'BackToTop',
    dragDialog: 'Drag Dialog',
    dragSelect: 'Drag Select',
    dragKanban: 'Drag Kanban',
    charts: 'Charts',
    keyboardChart: 'Keyboard Chart',
    lineChart: 'Line Chart',
    mixChart: 'Mix Chart',
    example: 'Example',
    nested: 'Nested Routes',
    menu1: 'Menu 1',
    'menu1-1': 'Menu 1-1',
    'menu1-2': 'Menu 1-2',
    'menu1-2-1': 'Menu 1-2-1',
    'menu1-2-2': 'Menu 1-2-2',
    'menu1-3': 'Menu 1-3',
    menu2: 'Menu 2',
    Table: 'Table',
    dynamicTable: 'Dynamic Table',
    dragTable: 'Drag Table',
    inlineEditTable: 'Inline Edit',
    complexTable: 'Complex Table',
    treeTable: 'Tree Table',
    customTreeTable: 'Custom TreeTable',
    tab: 'Tab',
    form: 'Form',
    createArticle: 'Create Article',
    editArticle: 'Edit Article',
    articleList: 'Article List',
    errorPages: 'Error Pages',
    page401: '401',
    page404: '404',
    errorLog: 'Error Log',
    excel: 'Excel',
    exportExcel: 'Export Excel',
    selectExcel: 'Export Selected',
    uploadExcel: 'Upload Excel',
    zip: 'Zip',
    exportZip: 'Export Zip',
    theme: 'Theme',
    clipboardDemo: 'Clipboard',
    i18n: 'I18n',
    externalLink: 'External Link'
  },
  navbar: {
    logOut: 'Log Out',
    dashboard: 'Dashboard',
    github: 'Github',
    screenfull: 'Screenfull',
    theme: 'Theme',
    size: 'Global Size'
  },
  login: {
    title: 'Login Form',
    logIn: 'Log in',
    username: 'Username',
    password: 'Password',
    any: 'any',
    thirdparty: 'Or connect with',
    thirdpartyTips: 'Can not be simulated on local, so please combine you own business simulation! ! !'
  },
  documentation: {
    documentation: 'Documentation',
    github: 'Github Repository'
  },
  permission: {
    roles: 'Your roles',
    switchRoles: 'Switch roles'
  },
  guide: {
    description: 'The guide page is useful for some people who entered the project for the first time. You can briefly introduce the features of the project. Demo is based on ',
    button: 'Show Guide'
  },
  components: {
    documentation: 'Documentation',
    tinymceTips: 'Rich text editor is a core part of management system, but at the same time is a place with lots of problems. In the process of selecting rich texts, I also walked a lot of detours. The common rich text editors in the market are basically used, and the finally chose Tinymce. See documentation for more detailed rich text editor comparisons and introductions.',
    dropzoneTips: 'Because my business has special needs, and has to upload images to qiniu, so instead of a third party, I chose encapsulate it by myself. It is very simple, you can see the detail code in @/components/Dropzone.',
    stickyTips: 'when the page is scrolled to the preset position will be sticky on the top.',
    backToTopTips1: 'When the page is scrolled to the specified position, the Back to Top button appears in the lower right corner',
    backToTopTips2: 'You can customize the style of the button, show / hide, height of appearance, height of the return. If you need a text prompt, you can use element-ui el-tooltip elements externally',
    imageUploadTips: 'Since I was using only the vue@1 version, and it is not compatible with mockjs at the moment, I modified it myself, and if you are going to use it, it is better to use official version.'
  },
  table: {
    dynamicTips1: 'Fixed header, sorted by header order',
    dynamicTips2: 'Not fixed header, sorted by click order',
    dragTips1: 'The default order',
    dragTips2: 'The after dragging order',
    title: 'Title',
    importance: 'Imp',
    type: 'Type',
    remark: 'Remark',
    search: 'Search',
    add: 'Add',
    export: 'Export',
    reviewer: 'reviewer',
    id: 'ID',
    date: 'Date',
    author: 'Author',
    readings: 'Readings',
    status: 'Status',
    actions: 'Actions',
    edit: 'Edit',
    publish: 'Publish',
    draft: 'Draft',
    delete: 'Delete',
    cancel: 'Cancel',
    confirm: 'Confirm',
    name: 'Name',
    serviceUrl: 'ServiceUrl',
    brokerServiceUrl: 'BrokerServiceUrl',
    tenant: 'Tenants',
    namespace: 'Namespaces',
    role: 'Role',
    clusters: 'Clusters',
    addRole: 'AddRole',
    addCluster: 'AddCluster',
    config: 'Config',
    policies: 'Policies',
    description: 'Description',
    update: 'Update',
    topic: 'Topic',
    stats: 'Stats',
    partition: 'Partition',
    cluster: 'Cluster',
    subscription: 'Subscription',
    unsubscription: 'Unsubscription',
    grant: 'Grant',
    limit: 'Limit',
    schemas: 'Schemas',
    brokers: 'Brokers',
    internalConfig: 'InternalConfig',
    runtimeConfig: 'RuntimeConfig',
    dynamicConfig: 'DynamicConfig',
    healthCheck: 'HealthCheck',
    functions: 'Functions',
    start: 'Start',
    stop: 'Stop',
    restart: 'Restart',
    trigger: 'Trigger',
    configName: 'ConfigName',
    configValue: 'ConfigValue',
    sources: 'Sources',
    builtin: 'Builtin',
    sinks: 'Sink',
    quotas: 'Quotas',
    monitor: 'Monitor',
    allowedClusters: 'Allowed Clusters',
    adminRoles: 'Admin Roles'
  },
  errorLog: {
    tips: 'Please click the bug icon in the upper right corner',
    description: 'Now the management system are basically the form of the spa, it enhances the user experience, but it also increases the possibility of page problems, a small negligence may lead to the entire page deadlock. Fortunately Vue provides a way to catch handling exceptions, where you can handle errors or report exceptions.',
    documentation: 'Document introduction'
  },
  excel: {
    export: 'Export',
    selectedExport: 'Export Selected Items',
    placeholder: 'Please enter the file name(default excel-list)'
  },
  zip: {
    export: 'Export',
    placeholder: 'Please enter the file name(default file)'
  },
  theme: {
    change: 'Change Theme',
    documentation: 'Theme documentation',
    tips: 'Tips: It is different from the theme-pick on the navbar is two different skinning methods, each with different application scenarios. Refer to the documentation for details.'
  },
  tagsView: {
    refresh: 'Refresh',
    close: 'Close',
    closeOthers: 'Close Others',
    closeAll: 'Close All'
  },
  tabs: {
    config: 'CONFIG',
    failuredomains: 'FAILURE DOMAINS',
    isolationpolicies: 'ISOLATION POLICIES'
  },
  common: {
    dangerZone: 'Danger Zone',
    actions: 'Actions',
    clusterLabel: 'Cluster',
    namespacesLabel: 'Namespaces',
    regex: 'Regex'
  },
  tenant: {
    searchTenant: 'Search Tenants',
    newTenant: 'New Tenant',
    nameLabel: 'Tenant Name',
    adminRolesLabel: 'Admin Roles',
    allowedClustersLabel: 'Allowed Clusters'
  },
  namespace: {
    newTopic: 'New Topic',
    searchTopics: 'Search Topics'
  },
  cluster: {
    label: 'Cluster',
    name: 'Cluster Name',
    addCluster: 'Add Cluster',
    updateCluster: 'Update Cluster',
    deleteCluster: 'Delete Cluster',
    searchClusters: 'Search Clusters',
    selectCluster: 'Select Cluster',
    webServiceUrlPrefix: 'Http Service Url',
    webServiceUrlTlsPrefix: 'Https Service Url',
    brokerServiceUrlPrefix: 'Broker Service Url',
    brokerServiceUrlTlsPrefix: 'Broker Service Url (TLS)'
  },
  // failure domain
  fd: {
    label: 'Failure Domain',
    createFdTitle: 'Add a New Failure Domain',
    name: 'Domain Name',
    brokerList: 'Broker List',
    selectBrokers: 'Please select brokers',
    searchFds: 'Search Failure Domains',
    newFd: 'New FailureDomain',
    updateFd: 'Update Failure Domain',
    deleteFd: 'Delete Failure Domain'
  },
  // isolation policies
  ip: {
    label: 'Policy',
    heading: 'Namespace Isolation Policy',
    name: 'Policy Name',
    searchIps: 'Search Isolation Policies',
    selectIp: 'Select Isolation Policy',
    newIp: 'New Isolation Policy',
    nameLabel: 'Isolation Policy',
    numPBLabel: 'Number of Primary Brokers',
    numSBLabel: 'Number of Secondary Brokers',
    newIpName: 'Please input policy name',
    selectNsLabel: 'Select Namespaces',
    selectPbLabel: 'Select Brokers',
    selectSbLabel: 'Select Brokers',
    selectAfpPh: 'Please select auto failover policy',
    pbHeading: 'Primary Brokers',
    sbHeading: 'Secondary Brokers',
    afpHeading: 'Auto Failover Policy',
    ptHeading: 'Policy Type',
    brokerUsageThresholdLabel: 'Broker Usage Threshold',
    brokerUsageThresholdPh: 'Please input broker usage threshold',
    minimalAvailableBrokerLabel: 'Minimal Available Brokers',
    minimalAvailableBrokerPh: 'Please input minimalAvailableBroker'
  }
}
