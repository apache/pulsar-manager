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
                      <el-select v-model="postForm.namespace" placeholder="select namespace" @change="getFunctionsList(postForm.tenant, postForm.namespace)">
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
        <el-input :placeholder="$t('table.functions')" v-model="listQuery.topic" style="width: 200px;" class="filter-item" @keyup.enter.native="handleFilter"/>
        <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">{{ $t('table.search') }}</el-button>
        <el-button class="filter-item" style="margin-left: 10px;" type="primary" icon="el-icon-edit" @click="handleCreate">{{ $t('table.add') }}</el-button>
        <el-button class="filter-item" style="margin-left: 10px;" type="primary" icon="el-icon-edit" @click="handleUpdate">{{ $t('table.edit') }}</el-button>
        <el-button class="filter-item" style="margin-left: 10px;" type="primary" icon="el-icon-edit" @click="handleDelete">{{ $t('table.delete') }}</el-button>
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
            <el-table-column :label="$t('table.functions')" min-width="50px" align="center">
              <template slot-scope="scope">
                <span>{{ scope.row.function }}</span>
              </template>
            </el-table-column>
            <el-table-column :label="$t('table.stats')" min-width="40px" align="center">
              <template slot-scope="scope">
                <span class="link-type" @click="handleGetStats(scope.row)">{{ scope.row.stats }}</span>
              </template>
            </el-table-column>
            <el-table-column :label="$t('table.status')" min-width="40px" align="center">
              <template slot-scope="scope">
                <span class="link-type" @click="handleGetStatus(scope.row)">{{ scope.row.status }}</span>
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
          <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getFunctions" />
        </el-col>
        <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 10}" :xl="{span: 10}" style="margin-bottom:30px;">
          <jsonEditor :value="jsonValue"/>
        </el-col>
      </el-row>

      <el-dialog :visible.sync="dialogFormVisible">
        <el-form ref="temp" :rules="rules" :model="temp" label-position="left" label-width="100px" style="width: 400px; margin-left:50px;">
          <div v-if="dialogStatus==='create'||dialogStatus==='update'">
            <div v-if="dialogStatus==='create'">
              <el-form-item label="name" prop="function">
                <el-input v-model="temp.function"/>
              </el-form-item>
            </div>
            <div v-if="dialogStatus==='update'">
              <el-form-item label="name">
                <span>{{ currentFunction }}</span>
              </el-form-item>
            </div>
            <el-form-item label="file" prop="file">
              <input type="file" @change="loadTextFromFile">
            </el-form-item>
            <el-form-item label="class" prop="className">
              <el-input v-model="temp.className"/>
            </el-form-item>
            <el-form-item label="inputs" prop="inputTopic">
              <el-select v-model="temp.inputTopic" placeholder="select topic" @focus="getTopicsList()">
                <el-option v-for="(item,index) in inputTopicsListOptions" :key="item+index" :label="item" :value="item"/>
              </el-select>
            </el-form-item>
            <el-form-item label="output" prop="outputTopic">
              <el-select v-model="temp.outputTopic" placeholder="select topic" @focus="getTopicsList()">
                <el-option v-for="(item,index) in outputTopicsListOptions" :key="item+index" :label="item" :value="item"/>
              </el-select>
            </el-form-item>
            <el-form-item label="language" prop="language">
              <el-switch
                v-model="temp.language"
                active-color="#13ce66"
                inactive-color="#ff4949"
                active-text="jar"
                inactive-text="py"/>
            </el-form-item>
            <el-form-item label="autoAck" prop="autoAck">
              <el-switch
                v-model="temp.autoAck"
                active-color="#13ce66"
                inactive-color="#ff4949"/>
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
            <el-form-item label="inSerde" prop="serdeInput">
              <el-input v-model="temp.serdeInput"/>
            </el-form-item>
            <el-form-item label="outSerde" prop="serdeOutput">
              <el-input v-model="temp.serdeOutput"/>
            </el-form-item>
            <el-form-item label="letter" prop="letterTopic">
              <el-input v-model="temp.letterTopic"/>
            </el-form-item>
            <el-form-item label="fqfn" prop="fqfn">
              <el-input v-model="temp.fqfn"/>
            </el-form-item>
            <el-form-item label="logTopic" prop="logTopic">
              <el-input v-model="temp.logTopic"/>
            </el-form-item>
            <el-form-item label="maxRetries" prop="maxMessageRetries">
              <el-input v-model="temp.maxMessageRetries"/>
            </el-form-item>
            <el-form-item label="parallelism" prop="parallelism">
              <el-input v-model="temp.parallelism"/>
            </el-form-item>
            <el-form-item label="retainOrder" prop="retainOrdering">
              <el-switch
                v-model="temp.retainOrdering"
                active-color="#13ce66"
                inactive-color="#ff4949"/>
            </el-form-item>
            <el-form-item label="schemaType" prop="schemaType">
              <el-input v-model="temp.schemaType"/>
            </el-form-item>
            <el-form-item label="interCount" prop="slidingIntervalCount">
              <el-input v-model="temp.slidingIntervalCount"/>
            </el-form-item>
            <el-form-item label="interMs" prop="slidingIntervalDurationMs">
              <el-input v-model="temp.slidingIntervalDurationMs"/>
            </el-form-item>
            <el-form-item label="subName" prop="subName">
              <el-input v-model="temp.subName"/>
            </el-form-item>
            <el-form-item label="timeout" prop="timeoutMs">
              <el-input v-model="temp.timeoutMs"/>
            </el-form-item>
            <el-form-item label="guarantees" prop="guarantees">
              <el-select v-model="temp.guarantees" placeholder="select guarantees">
                <el-option v-for="(item,index) in guanrateesListOptions" :key="item+index" :label="item" :value="index"/>
              </el-select>
            </el-form-item>
            <el-form-item label="userConfig" prop="userConfig">
              <el-input v-model="temp.userConfig"/>
            </el-form-item>
            <el-form-item label="lengthCount" prop="windowLengthCount">
              <el-input v-model="temp.windowLengthCount"/>
            </el-form-item>
            <el-form-item label="lengthMs" prop="windowLengthDurationMs">
              <el-input v-model="temp.windowLengthDurationMs"/>
            </el-form-item>
            <el-form-item label="topicsPattern" prop="topicsPattern">
              <el-input v-model="temp.topicsPattern"/>
            </el-form-item>
          </div>
          <div v-if="dialogStatus==='start'||dialogStatus==='stop'||dialogStatus==='restart'">
            <el-form-item label="instanceId" prop="instanceId">
              <el-select v-model="temp.instanceId" placeholder="select instanceId">
                <el-option v-for="(item,index) in instancesListOptions" :key="item+index" :label="item" :value="item"/>
              </el-select>
            </el-form-item>
          </div>
          <div v-if="dialogStatus==='trigger'">
            <el-form-item label="name" prop="function">
              <span>{{ currentFunction }}</span>
            </el-form-item>
            <el-form-item label="triggerFile" prop="triggerFile">
              <input type="file" @change="loadTriggerFile">
            </el-form-item>
            <el-form-item label="triggerValue" prop="triggerValue">
              <el-input v-model="temp.triggerValue"/>
            </el-form-item>
            <el-form-item label="triggerInput" prop="triggerInput">
              <el-select v-model="temp.triggerInput" placeholder="select topic" @focus="getTopicsList()">
                <el-option v-for="(item,index) in triggerInputTopicsListOptions" :key="item+index" :label="item" :value="item"/>
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
import { fetchTenants } from '@/api/tenants'
import { fetchNamespaces } from '@/api/namespaces'
import jsonEditor from '@/components/JsonEditor'
import { validateEmpty } from '@/utils/validate'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import {
  fetchFunctions,
  fetchFunctionStats,
  fetchFunctionStatus,
  createFunction,
  deleteFunction,
  startFunctionInstance,
  stopFunctionInstance,
  restartFunctionInstance,
  updateFunction,
  triggerFunction
} from '@/api/functions'
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
  name: 'Functions',
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
      inputTopicsListOptions: [],
      outputTopicsListOptions: [],
      guanrateesListOptions: [],
      instancesListOptions: [],
      triggerInputTopicsListOptions: [],
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
      currentFunction: '',
      dialogStatus: '',
      listQuery: {
        topic: '',
        page: 1,
        limit: 10
      },
      temp: {
        autoAck: false,
        function: '',
        instanceId: 0,
        className: '',
        inputTopic: '',
        outputTopic: '',
        language: false,
        cpu: '',
        disk: '',
        ram: '',
        schemaInput: '',
        serdeInput: '',
        serdeOutput: '',
        letterTopic: '',
        guarantees: '',
        fqfn: '',
        logTopic: '',
        parallelism: '',
        retainOrdering: false,
        schemaType: '',
        slidingIntervalCount: '',
        slidingIntervalDurationMs: '',
        maxMessageRetries: '',
        subName: '',
        timeoutMs: '',
        windowLengthCount: '',
        windowLengthDurationMs: '',
        topicsPattern: '',
        currentFile: '',
        currentFileName: '',
        currentJsonFile: '',
        userConfig: '',
        triggerValue: '',
        triggerInput: ''
      },
      rules: {
        function: [{ required: true, message: 'functions name is required', trigger: 'blur' }],
        className: [{ required: true, message: 'className is required', trigger: 'blur' }],
        inputTopic: [{ required: true, message: 'inputTopic is required', trigger: 'blur' }],
        outputTopic: [{ required: true, message: 'outputTopic is required', trigger: 'blur' }]
      }
    }
  },
  mounted() {
    this.guanrateesListOptions = ['ATLEAST_ONCE', 'ATMOST_ONCE', 'EFFECTIVELY_ONCE']
  },
  created() {
    this.getFunctions()
    this.getRemoteTenantsList()
  },
  methods: {
    getFunctions() {
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
        fetchFunctions(this.tenant, this.namespace).then(response => {
          for (var i = 0; i < response.data.length; i++) {
            this.localList.push({ 'function': response.data[i], 'stats': 'stats', 'status': 'status' })
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
          if (this.localList[i]['function'].indexOf(this.listQuery.topic) !== -1) {
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
    handleFilter() {
      this.getFunctions()
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
      this.currentFunction = item.function
    },
    handleDelete() {
      if (this.currentFunction.length <= 0) {
        this.$notify({
          title: 'error',
          message: 'Please select any one function in table',
          type: 'error',
          duration: 3000
        })
        return
      }
      deleteFunction(this.tenant, this.namespace, this.currentFunction).then(response => {
        this.$notify({
          title: 'success',
          message: 'delete functions success',
          type: 'success',
          duration: 2000
        })
        this.localList = []
        this.getFunctions()
      })
    },
    createFunction() {
      this.$refs['temp'].validate((valid) => {
        if (valid) {
          const formData = this.prepareFunctionParams(this.temp.function)
          createFunction(this.tenant, this.namespace, this.temp.function, formData).then(response => {
            this.$notify({
              title: 'success',
              message: 'create functions success',
              type: 'success',
              duration: 2000
            })
            this.dialogFormVisible = false
            this.localList = []
            this.getFunctions()
          })
        }
      })
    },
    handleUpdate() {
      this.resetTemp()
      if (this.currentFunction.length <= 0) {
        this.$notify({
          title: 'error',
          message: 'Please select any one function in table',
          type: 'error',
          duration: 3000
        })
        return
      }
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
    },
    handleTrigger() {
      if (this.currentFunction.length <= 0) {
        this.$notify({
          title: 'error',
          message: 'Please select any one function in table',
          type: 'error',
          duration: 3000
        })
        return
      }
      this.dialogStatus = 'trigger'
      this.dialogFormVisible = true
    },
    handleAction(action, row) {
      this.dialogStatus = action
      this.dialogFormVisible = true
      this.instancesListOptions = []
      this.currentFunction = row.function
      fetchFunctionStatus(this.tenant, this.namespace, row.function).then(response => {
        for (var i = 0; i < response.data.instances.length; i++) {
          this.instancesListOptions.push(response.data.instances[i]['instanceId'])
        }
      })
    },
    handleOptions() {
      switch (this.dialogStatus) {
        case 'create':
          this.createFunction()
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
        case 'trigger':
          this.confirmTrigger()
          break
      }
    },
    loadTextFromFile(ev) {
      const file = ev.target.files[0]
      this.temp.currentFileName = file.name
      var reader = new FileReader()
      reader.onload = e => {
        this.temp.currentFile = e.target.result
      }
      reader.readAsArrayBuffer(file)
    },
    loadTriggerFile(ev) {
      const file = ev.target.files[0]
      this.temp.currentTriggerFileName = file.name
      var reader = new FileReader()
      reader.onload = e => {
        this.temp.triggerFile = e.target.result
      }
      reader.readAsText(file)
    },
    handleGetStats(row) {
      fetchFunctionStats(this.tenant, this.namespace, row.function).then(response => {
        this.jsonValue = response.data
      })
    },
    handleGetStatus(row) {
      fetchFunctionStatus(this.tenant, this.namespace, row.function).then(response => {
        this.jsonValue = response.data
      })
    },
    confirmStart() {
      startFunctionInstance(this.tenant, this.namespace, this.currentFunction, this.temp.instanceId).then(response => {
        this.$notify({
          title: 'success',
          message: 'start function success',
          type: 'success',
          duration: 2000
        })
        this.dialogFormVisible = false
      })
    },
    confirmStop() {
      stopFunctionInstance(this.tenant, this.namespace, this.currentFunction, this.temp.instanceId).then(response => {
        this.$notify({
          title: 'success',
          message: 'stop function success',
          type: 'success',
          duration: 2000
        })
        this.dialogFormVisible = false
      })
    },
    confirmRestart() {
      restartFunctionInstance(this.tenant, this.namespace, this.currentFunction, this.temp.instanceId).then(response => {
        this.$notify({
          title: 'success',
          message: 'restart function success',
          type: 'success',
          duration: 2000
        })
        this.dialogFormVisible = false
      })
    },
    prepareFunctionParams(functionName) {
      const data = {
        'inputs': [this.temp.inputTopic],
        'output': this.temp.outputTopic,
        'className': this.temp.className
      }
      if (functionName.length > 0) {
        data['name'] = functionName
      }
      const formData = new FormData()
      const blob = new Blob([this.temp.currentFile], { type: 'application/octet-stream' })
      formData.append('data', blob, this.temp.currentFileName)
      if (this.temp.autoAck) {
        formData.append('autoAck', this.temp.autoAck)
      }
      if (this.temp.language) {
        data['jar'] = this.temp.currentFileName
      } else {
        data['py'] = this.temp.currentFileName
      }
      if (this.temp.schemaType.length > 0) {
        data['outputSchemaType'] = this.temp.outputSchemaType
      }
      if (this.temp.cpu.length > 0) {
        data['resources'] = { 'cpu': parseInt(this.temp.cpu) }
      }
      if (this.temp.disk.length > 0) {
        data['resources'] = { 'disk': this.temp.disk }
      }
      if (this.temp.ram.length > 0) {
        data['resources'] = { 'ram': this.temp.ram }
      }
      if (this.temp.schemaInput.length > 0) {
        data['customSerdeInputs'] = this.temp.schemaInput
      }
      if (this.temp.serdeInput.length > 0) {
        data['customSchemaInputs'] = this.temp.serdeInput
      }
      if (this.temp.serdeOutput.length > 0) {
        data['customSchemaInputs'] = this.temp.serdeOutput
      }
      if (this.temp.letterTopic.length > 0) {
        data['deadLetterTopic'] = this.temp.deadLetterTopic
      }
      if (this.temp.fqfn.length > 0) {
        data['fqfn'] = this.temp.fqfn
      }
      if (this.temp.logTopic.length > 0) {
        data['logTopic'] = this.temp.logTopic
      }
      if (this.temp.maxMessageRetries.length > 0) {
        data['maxMessageRetries'] = parseInt(this.temp.maxMessageRetries)
      }
      if (this.temp.parallelism.length > 0) {
        data['parallelism'] = parseInt(this.temp.parallelism)
      }
      if (this.temp.retainOrdering.length > 0) {
        data['retainOrdering'] = this.temp.retainOrdering
      }
      if (this.temp.schemaType.length > 0) {
        data['schemaType'] = this.temp.outputSchemaType
      }
      if (this.temp.slidingIntervalCount.length > 0) {
        data['windowConfig'] = { 'slidingIntervalCount': this.temp.slidingIntervalCount }
      }
      if (this.temp.slidingIntervalDurationMs.length > 0) {
        data['windowConfig'] = { 'slidingIntervalDurationMs': this.temp.slidingIntervalDurationMs }
      }
      if (this.temp.windowLengthCount.length > 0) {
        data['windowConfig'] = { 'windowLengthCount': this.temp.windowLengthCount }
      }
      if (this.temp.windowLengthDurationMs > 0) {
        data['windowConfig'] = { 'windowLengthDurationMs': parseInt(this.temp.windowLengthDurationMs) }
      }
      if (this.temp.topicsPattern.length > 0) {
        data['topicsPattern'] = this.temp.topicsPattern
      }
      if (this.temp.guarantees.length > 0) {
        data['processingGuarantees'] = this.temp.guarantees
      }
      if (this.temp.userConfig.length > 0) {
        data['userConfig'] = this.temp.userConfig
      }
      if (this.temp.timeoutMs.length > 0) {
        data['timeoutMs'] = parseInt(this.temp.timeoutMs)
      }
      if (this.temp.subName.length > 0) {
        data['subName'] = this.temp.subName
      }
      formData.append('functionConfig', JSON.stringify(data))
      return formData
    },
    confirmUpdate() {
      const formData = this.prepareFunctionParams('')
      updateFunction(this.tenant, this.namespace, this.currentFunction, formData).then(response => {
        this.$notify({
          title: 'success',
          message: 'update function success',
          type: 'success',
          duration: 2000
        })
        this.dialogFormVisible = false
      })
    },
    confirmTrigger() {
      const formData = new FormData()
      const blob = new Blob([this.temp.triggerFile], { type: 'application/octet-stream' })
      formData.append('dataStream', blob, this.temp.currentTriggerFileName)
      formData.append('topic', this.temp.triggerInput)
      formData.append('data', this.temp.triggerValue)
      triggerFunction(this.tenant, this.namespace, this.currentFunction, formData).then(response => {
        this.$notify({
          title: 'success',
          message: 'trigger set success for function',
          type: 'success',
          duration: 2000
        })
        this.dialogFormVisible = false
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
    getFunctionsList(tenant, namespace) {
      fetchFunctions(tenant, namespace).then(response => {

      })
    },
    getTopicsList() {
      this.inputTopicsListOptions = []
      this.outputTopicsListOptions = []
      this.triggerInputTopicsListOptions = []
      fetchPersistentPartitonsTopics(this.tenant, this.namespace).then(response => {
        for (var i = 0; i < response.data.length; i++) {
          this.inputTopicsListOptions.push(response.data[i])
          this.outputTopicsListOptions.push(response.data[i])
          this.triggerInputTopicsListOptions.push(response.data[i])
        }
      })
      fetchNonPersistentPartitonsTopics(this.tenant, this.namespace).then(response => {
        for (var i = 0; i < response.data.length; i++) {
          this.inputTopicsListOptions.push(response.data[i])
          this.outputTopicsListOptions.push(response.data[i])
          this.triggerInputTopicsListOptions.push(response.data[i])
        }
      })
      fetchTopics(this.tenant, this.namespace, this.listQuery).then(response => {
        for (var i = 0; i < response.data.length; i++) {
          this.inputTopicsListOptions.push(response.data[i])
          this.outputTopicsListOptions.push(response.data[i])
          this.triggerInputTopicsListOptions.push(response.data[i])
        }
      })
    },
    resetTemp() {
      this.temp = {
        autoAck: false,
        function: '',
        instanceId: 0,
        className: '',
        inputTopic: '',
        outputTopic: '',
        language: false,
        cpu: '',
        disk: '',
        ram: '',
        schemaInput: '',
        serdeInput: '',
        serdeOutput: '',
        letterTopic: '',
        guarantees: '',
        fqfn: '',
        logTopic: '',
        parallelism: '',
        retainOrdering: false,
        schemaType: '',
        slidingIntervalCount: '',
        slidingIntervalDurationMs: '',
        maxMessageRetries: '',
        subName: '',
        timeoutMs: '',
        windowLengthCount: '',
        windowLengthDurationMs: '',
        topicsPattern: '',
        currentFile: '',
        currentFileName: '',
        currentJsonFile: '',
        userConfig: ''
      }
    }
  }
}
</script>
