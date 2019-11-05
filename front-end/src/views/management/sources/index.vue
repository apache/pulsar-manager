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
  <div class="app-container">
    <div class="createPost-container">
      <el-form ref="postForm" :model="postForm" class="form-container">
        <div class="createPost-main-container">
          <el-row>
            <el-col :span="12">
              <div class="postInfo-container">
                <el-row>
                  <el-col :span="8">
                    <el-form-item class="postInfo-container-item">
                      <el-select v-model="postForm.tenant" placeholder="select tenant" @change="getNamespacesList(postForm.tenant)">
                        <el-option v-for="(item,index) in tenantsListOptions" :key="item+index" :label="item" :value="item"/>
                      </el-select>
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item class="postInfo-container-item">
                      <el-select v-model="postForm.namespace" placeholder="select namespace" @change="getSourcesList(postForm.tenant, postForm.namespace)">
                        <el-option v-for="(item,index) in namespacesListOptions" :key="item+index" :label="item" :value="item"/>
                      </el-select>
                    </el-form-item>
                  </el-col>
                </el-row>
              </div>
            </el-col>
          </el-row>
        </div>
      </el-form>
    </div>
    <!-- <codeEditor :value="codeValue" :mode="codeMode"/> -->
    <div>
      <div class="filter-container">
        <el-input :placeholder="$t('table.sources')" v-model="listQuery.topic" style="width: 200px;" class="filter-item" @keyup.enter.native="handleFilter"/>
        <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">{{ $t('table.search') }}</el-button>
        <el-button class="filter-item" style="margin-left: 10px;" type="primary" icon="el-icon-edit" @click="handleCreate">{{ $t('table.add') }}</el-button>
        <el-button class="filter-item" style="margin-left: 10px;" type="primary" icon="el-icon-edit" @click="handleUpdate">{{ $t('table.edit') }}</el-button>
        <el-button class="filter-item" style="margin-left: 10px;" type="primary" icon="el-icon-edit" @click="handleDelete">{{ $t('table.delete') }}</el-button>
        <el-button class="filter-item" style="margin-left: 10px;" type="primary" icon="el-icon-edit" @click="builtinSources">{{ $t('table.builtin') }}</el-button>
        <!-- <el-button class="filter-item" style="margin-left: 10px;" type="primary" icon="el-icon-edit" @click="handleTrigger">{{ $t('table.trigger') }}</el-button> -->
      </div>
      <el-row :gutter="8">
        <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 14}" :xl="{span: 14}" style="padding-right:8px;margin-bottom:30px;">
          <el-table
            v-loading="listLoading"
            :key="tableKey"
            :data="list"
            border
            fit
            highlight-current-row
            style="width: 100%;"
            @row-click="getCurrentRow">
            <el-table-column :label="$t('table.sources')" min-width="50px" align="center">
              <template slot-scope="scope">
                <span>{{ scope.row.source }}</span>
              </template>
            </el-table-column>
            <el-table-column :label="$t('table.stats')" min-width="40px" align="center">
              <template slot-scope="scope">
                <span class="link-type" @click="handleGet(scope.row)">{{ scope.row.stats }}</span>
              </template>
            </el-table-column>
            <el-table-column :label="$t('table.status')" min-width="40px" align="center">
              <template slot-scope="scope">
                <span class="link-type" @click="handleAction('status', scope.row)">{{ scope.row.status }}</span>
              </template>
            </el-table-column>
            <el-table-column :label="$t('table.actions')" align="center" width="250" class-name="small-padding fixed-width">
              <template slot-scope="scope">
                <el-button type="primary" size="mini" @click="handleAction('start', scope.row)">{{ $t('table.start') }}</el-button>
                <el-button type="primary" size="mini" @click="handleAction('stop', scope.row)">{{ $t('table.stop') }}</el-button>
                <el-button type="primary" size="mini" @click="handleAction('restart', scope.row)">{{ $t('table.restart') }}</el-button>
              </template>
            </el-table-column>
          </el-table>
          <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getSources" />
        </el-col>
        <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 10}" :xl="{span: 10}" style="margin-bottom:30px;">
          <jsonEditor :value="jsonValue"/>
        </el-col>
      </el-row>

      <el-dialog :visible.sync="dialogFormVisible">
        <el-form ref="temp" :rules="rules" :model="temp" label-position="left" label-width="100px" style="width: 400px; margin-left:50px;">
          <div v-if="dialogStatus==='create'||dialogStatus==='update'">
            <div v-if="dialogStatus==='create'">
              <el-form-item label="name" prop="source">
                <el-input v-model="temp.source"/>
              </el-form-item>
            </div>
            <div v-if="dialogStatus==='update'">
              <el-form-item label="name">
                <span>{{ currentSource }}</span>
              </el-form-item>
            </div>
            <el-form-item label="archive" prop="file">
              <input type="file" @change="loadTextFromFile">
            </el-form-item>
            <el-form-item label="class" prop="className">
              <el-input v-model="temp.className"/>
            </el-form-item>
            <el-form-item label="configFile" prop="sourceConfigFile">
              <input type="file" @change="loadSourceConfigFile">
            </el-form-item>
            <el-form-item label="output" prop="outputTopic">
              <el-select v-model="temp.outputTopic" placeholder="select topic" @focus="getTopicsList()">
                <el-option v-for="(item,index) in outputTopicsListOptions" :key="item+index" :label="item" :value="item"/>
              </el-select>
            </el-form-item>
            <el-form-item label="cpu" prop="cpu">
              <el-input v-model="temp.cpu"/>
            </el-form-item>
            <el-form-item label="disk" prop="disk">
              <el-input v-model="temp.disk"/>
            </el-form-item>
            <el-form-item label="ram" prop="ram">
              <el-input v-model="temp.ram"/>
            </el-form-item>
            <el-form-item label="schema" prop="schemaInput">
              <el-input v-model="temp.schemaInput"/>
            </el-form-item>
            <el-form-item label="deClassName" prop="deClassName">
              <el-input v-model="temp.deClassName"/>
            </el-form-item>
            <el-form-item label="parallelism" prop="parallelism">
              <el-input v-model="temp.parallelism"/>
            </el-form-item>
            <el-form-item label="schemaType" prop="schemaType">
              <el-input v-model="temp.schemaType"/>
            </el-form-item>
            <el-form-item label="guarantees" prop="guarantees">
              <el-select v-model="temp.guarantees" placeholder="select guarantees">
                <el-option v-for="(item,index) in guanrateesListOptions" :key="item+index" :label="item" :value="index"/>
              </el-select>
            </el-form-item>
            <el-form-item label="sourceConfig" prop="sourceConfig">
              <el-input v-model="temp.sourceConfig"/>
            </el-form-item>
          </div>
          <div v-if="dialogStatus==='start'||dialogStatus==='stop'||dialogStatus==='restart'||dialogStatus==='status'">
            <el-form-item label="instanceId" prop="instanceId">
              <el-select v-model="temp.instanceId" placeholder="select instanceId">
                <el-option v-for="(item,index) in instancesListOptions" :key="item+index" :label="item" :value="item"/>
              </el-select>
            </el-form-item>
          </div>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="dialogFormVisible = false">{{ $t('table.cancel') }}</el-button>
          <el-button type="primary" @click="handleOptions()">{{ $t('table.confirm') }}</el-button>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script>
// import codeEditor from '@/components/CodeEditor'
import yaml from 'js-yaml'
import { fetchTenants } from '@/api/tenants'
import { fetchNamespaces } from '@/api/namespaces'
import jsonEditor from '@/components/JsonEditor'
import { validateEmpty } from '@/utils/validate'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import {
  fetchSources,
  createSource,
  updateSource,
  getSourceStatus,
  startSourceInstance,
  stopSourceInstance,
  restartSourceInstance,
  deleteSource,
  fetchSourcesStats,
  getSourceStatusInstance,
  getBuiltinSources
} from '@/api/sources'
import {
  fetchTopics,
  fetchPersistentPartitonsTopics,
  fetchNonPersistentPartitonsTopics
} from '@/api/topics'

const defaultForm = {
  tenant: '',
  namespace: ''
}
export default {
  name: 'Sources',
  components: {
    // codeEditor
    jsonEditor,
    Pagination
  },
  data() {
    return {
      codeValue: null,
      postForm: Object.assign({}, defaultForm),
      tenantsListOptions: [],
      namespacesListOptions: [],
      outputTopicsListOptions: [],
      guanrateesListOptions: [],
      instancesListOptions: [],
      // codeMode: 'text/x-cython',
      localList: [],
      searchList: [],
      tenant: '',
      namespace: '',
      jsonValue: {},
      tableKey: 0,
      total: 0,
      listLoading: false,
      dialogFormVisible: false,
      list: null,
      currentFile: '',
      triggerFile: '',
      currentTriggerFileName: '',
      currentSource: '',
      dialogStatus: '',
      listQuery: {
        topic: '',
        page: 1,
        limit: 10
      },
      temp: {
        source: '',
        instanceId: 0,
        className: '',
        cpu: '',
        disk: '',
        ram: '',
        schemaInput: '',
        parallelism: '',
        schemaType: '',
        currentFile: '',
        currentFileName: '',
        currentConfigFile: '',
        currentConfigFileName: '',
        sourceConfig: '',
        triggerValue: '',
        triggerInput: '',
        deClassName: '',
        guarantees: ''
      },
      rules: {
        source: [{ required: true, message: 'sources name is required', trigger: 'blur' }],
        className: [{ required: true, message: 'className is required', trigger: 'blur' }],
        outputTopic: [{ required: true, message: 'outputTopic is required', trigger: 'blur' }]
      }
    }
  },
  mounted() {
    this.guanrateesListOptions = ['ATLEAST_ONCE', 'ATMOST_ONCE', 'EFFECTIVELY_ONCE']
  },
  created() {
    this.getSources()
    this.getRemoteTenantsList()
  },
  methods: {
    getSources() {
      if (this.localList.length > 0) {
        setTimeout(() => {
          this.localPaging()
        }, 0)
      } else {
        this.listLoading = true
        if (this.postForm.tenant.length > 0) {
          this.tenant = this.postForm.tenant
        }
        if (this.postForm.namespace.length > 0) {
          this.namespace = this.postForm.namespace
        }
        if (this.tenant.length <= 0 || this.namespace.length <= 0) {
          this.tenant = 'public'
          this.namespace = 'default'
        }
        fetchSources(this.tenant, this.namespace).then(response => {
          for (var i = 0; i < response.data.length; i++) {
            this.localList.push({ 'source': response.data[i], 'stats': 'stats', 'status': 'status' })
          }
          this.total = this.localList.length
          this.list = this.localList.slice((this.listQuery.page - 1) * this.listQuery.limit, this.listQuery.limit * this.listQuery.page)
          setTimeout(() => {
            this.listLoading = false
          }, 1.5 * 1000)
        })
      }
    },
    localPaging() {
      this.listLoading = true
      if (!validateEmpty(this.listQuery.topic)) {
        this.searchList = []
        for (var i = 0; i < this.localList.length; i++) {
          if (this.localList[i]['source'].indexOf(this.listQuery.topic) !== -1) {
            this.searchList.push(this.localList[i])
          }
        }
        this.total = this.searchList.length
        this.list = this.searchList.slice((this.listQuery.page - 1) * this.listQuery.limit, this.listQuery.limit * this.listQuery.page)
      } else {
        this.total = this.localList.length
        this.list = this.localList.slice((this.listQuery.page - 1) * this.listQuery.limit, this.listQuery.limit * this.listQuery.page)
      }
      this.listLoading = false
    },
    handleFilter(tenant, namespace) {
      this.tenant = tenant
      this.namespace = namespace
      this.getSources()
    },
    handleCreate() {
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.resetTemp()
      this.$nextTick(() => {
        this.$refs['temp'].clearValidate()
      })
    },
    getCurrentRow(item) {
      this.currentSource = item.source
    },
    handleDelete() {
      if (this.currentSource.length <= 0) {
        this.$notify({
          title: 'error',
          message: 'Please select any one source in table',
          type: 'error',
          duration: 3000
        })
        return
      }
      deleteSource(this.tenant, this.namespace, this.currentSource).then(response => {
        this.$notify({
          title: 'success',
          message: 'delete source success',
          type: 'success',
          duration: 2000
        })
        this.localList = []
        this.getSources()
      })
    },
    confirmCreateSource() {
      this.$refs['temp'].validate((valid) => {
        if (valid) {
          const formData = this.prepareSourceParams(this.temp.source)
          createSource(this.tenant, this.namespace, this.temp.source, formData).then(response => {
            this.$notify({
              title: 'success',
              message: 'create source success',
              type: 'success',
              duration: 2000
            })
            this.dialogFormVisible = false
            this.localList = []
            this.getSources()
          })
        }
      })
    },
    handleUpdate() {
      this.resetTemp()
      if (this.currentSource.length <= 0) {
        this.$notify({
          title: 'error',
          message: 'Please select any one source in table',
          type: 'error',
          duration: 3000
        })
        return
      }
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
    },
    handleAction(action, row) {
      this.dialogStatus = action
      this.dialogFormVisible = true
      this.instancesListOptions = []
      this.currentSource = row.source
      getSourceStatus(this.tenant, this.namespace, row.source).then(response => {
        for (var i = 0; i < response.data.instances.length; i++) {
          this.instancesListOptions.push(response.data.instances[i]['instanceId'])
        }
      })
    },
    handleOptions() {
      switch (this.dialogStatus) {
        case 'create':
          this.confirmCreateSource()
          break
        case 'start':
          this.confirmStart()
          break
        case 'stop':
          this.confirmStop()
          break
        case 'restart':
          this.confirmRestart()
          break
        case 'update':
          this.confirmUpdate()
          break
        case 'status':
          this.confirmStatus()
          break
      }
    },
    loadTextFromFile(ev) {
      const file = ev.target.files[0]
      console.log(ev.target)
      console.log(ev.target.files)
      this.temp.currentFileName = file.name
      var reader = new FileReader()
      reader.onload = e => {
        this.temp.currentFile = e.target.result
      }
      reader.readAsArrayBuffer(file)
    },
    loadSourceConfigFile(ev) {
      const file = ev.target.files[0]
      this.temp.currentConfigFileName = file.name
      var reader = new FileReader()
      reader.onload = e => {
        this.temp.currentConfigFile = e.target.result
      }
      reader.readAsText(file)
    },
    handleGet(row) {
      fetchSourcesStats(this.tenant, this.namespace, row.source).then(response => {
        this.jsonValue = response.data
      })
    },
    // handleGetStatus(row) {
    //   getSourceStatus(this.tenant, this.namespace, row.source).then(response => {
    //     this.jsonValue = response.data
    //   })
    // },
    confirmStart() {
      startSourceInstance(this.tenant, this.namespace, this.currentSource, this.temp.instanceId).then(response => {
        this.$notify({
          title: 'success',
          message: 'start source success',
          type: 'success',
          duration: 2000
        })
        this.dialogFormVisible = false
      })
    },
    confirmStop() {
      stopSourceInstance(this.tenant, this.namespace, this.currentSource, this.temp.instanceId).then(response => {
        this.$notify({
          title: 'success',
          message: 'stop source success',
          type: 'success',
          duration: 2000
        })
        this.dialogFormVisible = false
      })
    },
    confirmRestart() {
      restartSourceInstance(this.tenant, this.namespace, this.currentSource, this.temp.instanceId).then(response => {
        this.$notify({
          title: 'success',
          message: 'restart source success',
          type: 'success',
          duration: 2000
        })
        this.dialogFormVisible = false
      })
    },
    confirmStatus() {
      getSourceStatusInstance(this.tenant, this.namespace, this.currentSource, this.temp.instanceId).then(response => {
        this.jsonValue = response.data
        this.$notify({
          title: 'success',
          message: 'get status source success',
          type: 'success',
          duration: 2000
        })
        this.dialogFormVisible = false
      })
    },
    prepareSourceParams(sourceName) {
      const formData = new FormData()
      const sourceConfig = {
        'topicName': 'my-topic',
        'className': this.temp.className,
        'tenant': this.tenant,
        'namespace': this.namespace
      }
      if (sourceName.length > 0) {
        sourceConfig['name'] = sourceName
      }
      const blob = new Blob([this.temp.currentFile], { type: 'application/octet-stream' })
      sourceConfig['archive'] = this.temp.currentFileName
      formData.append('data', blob, this.temp.currentFileName)
      if (this.temp.schemaType.length > 0) {
        sourceConfig['schemaType'] = this.temp.schemaType
      }
      if (this.temp.cpu.length > 0) {
        sourceConfig['resources'] = { 'cpu': parseInt(this.temp.cpu) }
      }
      if (this.temp.disk.length > 0) {
        sourceConfig['resources'] = { 'disk': this.temp.disk }
      }
      if (this.temp.ram.length > 0) {
        sourceConfig['resources'] = { 'ram': this.temp.ram }
      }
      if (this.temp.parallelism.length > 0) {
        sourceConfig['parallelism'] = parseInt(this.temp.parallelism)
      }
      if (this.temp.guarantees.length > 0) {
        sourceConfig['processingGuarantees'] = this.temp.guarantees
      }
      if (this.temp.sourceConfig.length > 0) {
        sourceConfig['configs'] = JSON.parse(this.temp.sourceConfig)
      }
      if (this.temp.currentConfigFile.length > 0) {
        const yamlFile = yaml.load(this.temp.currentConfigFile)
        const yamlJSON = JSON.parse(JSON.stringify(yamlFile))
        sourceConfig['configs'] = yamlJSON['configs']
      }
      formData.append('sourceConfig', JSON.stringify(sourceConfig))
      return formData
    },
    confirmUpdate() {
      this.$refs['temp'].validate((valid) => {
        if (valid) {
          const formData = this.prepareSourceParams('')
          updateSource(this.tenant, this.namespace, this.currentSource, formData).then(response => {
            this.$notify({
              title: 'success',
              message: 'update source success',
              type: 'success',
              duration: 2000
            })
            this.dialogFormVisible = false
          })
        }
      })
    },
    getSourcesList() {
      this.getSources()
    },
    builtinSources() {
      getBuiltinSources().then(response => {
        this.jsonValue = response.data
      })
    },
    getRemoteTenantsList() {
      fetchTenants().then(response => {
        if (!response.data) return
        this.tenantsListOptions = response.data
      })
    },
    getNamespacesList(tenant) {
      this.tenant = tenant
      fetchNamespaces(tenant, this.query).then(response => {
        let namespace = []
        for (var i = 0; i < response.data.length; i++) {
          namespace = response.data[i].split('/')
          // namespace.splice(1, namespace.length).join('/')
          this.namespacesListOptions.push(namespace.splice(1, namespace.length).join('/'))
        }
      })
    },
    getTopicsList() {
      this.outputTopicsListOptions = []
      fetchPersistentPartitonsTopics(this.tenant, this.namespace).then(response => {
        for (var i = 0; i < response.data.length; i++) {
          this.outputTopicsListOptions.push(response.data[i])
        }
      })
      fetchNonPersistentPartitonsTopics(this.tenant, this.namespace).then(response => {
        for (var i = 0; i < response.data.length; i++) {
          this.outputTopicsListOptions.push(response.data[i])
        }
      })
      fetchTopics(this.tenant, this.namespace, this.listQuery).then(response => {
        for (var i = 0; i < response.data.length; i++) {
          this.outputTopicsListOptions.push(response.data[i])
        }
      })
    },
    resetTemp() {
      this.temp = {
        source: '',
        instanceId: 0,
        className: '',
        cpu: '',
        disk: '',
        ram: '',
        schemaInput: '',
        parallelism: '',
        schemaType: '',
        currentFile: '',
        currentFileName: '',
        currentConfigFile: '',
        currentConfigFileName: '',
        sourceConfig: '',
        triggerValue: '',
        triggerInput: '',
        deClassName: '',
        guarantees: ''
      }
    }
  }
}
</script>
