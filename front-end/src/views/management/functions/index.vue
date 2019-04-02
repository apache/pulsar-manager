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
            style="width: 100%;">
            <el-table-column :label="$t('table.functions')" min-width="50px" align="center">
              <template slot-scope="scope">
                <span>{{ scope.row.function }}</span>
              </template>
            </el-table-column>
            <el-table-column :label="$t('table.stats')" min-width="50px" align="center">
              <template slot-scope="scope">
                <span class="link-type" @click="handleGetStats(scope.row)">{{ scope.row.stats }}</span>
              </template>
            </el-table-column>
            <el-table-column :label="$t('table.actions')" align="center" width="150" class-name="small-padding fixed-width">
              <template slot-scope="scope">
                <el-button type="primary" size="mini" @click="handleUpdate(scope.row)">{{ $t('table.edit') }}</el-button>
                <el-button v-if="scope.row.status!='deleted'" size="mini" type="danger" @click="handleDelete(scope.row)">{{ $t('table.delete') }}
                </el-button>
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
        <el-form ref="dataForm" :rules="rules" :model="temp" label-position="left" label-width="100px" style="width: 400px; margin-left:50px;">
          <div v-if="dialogStatus==='create'">
            <el-form-item label="autoAck" prop="autoAck">
              <el-switch
                v-model="temp.autoAck"
                active-color="#13ce66"
                inactive-color="#ff4949"/>
            </el-form-item>
            <el-form-item label="name" prop="functions">
              <el-input v-model="temp.functions"/>
            </el-form-item>
            <el-form-item label="file" prop="file">
              <input type="file" @change="loadTextFromFile">
            </el-form-item>
            <el-form-item label="language" prop="language">
              <el-switch
                v-model="temp.language"
                active-color="#13ce66"
                inactive-color="#ff4949"
                active-text="jar"
                inactive-text="py"/>
            </el-form-item>
            <el-form-item label="class" prop="className">
              <el-input v-model="temp.className"/>
            </el-form-item>
            <!-- <el-form-item label="cpu" prop="cpu">
              <el-input v-model="temp.cpu"/>
            </el-form-item>
            <el-form-item label="disk" prop="disk">
              <el-input v-model="temp.disk"/>
            </el-form-item>
            <el-form-item label="ram" prop="ram">
              <el-input v-model="temp.ram"/>
            </el-form-item> -->
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
              <el-input v-model="temp.schemaType"/>
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
              <el-select v-model="temp.guarantees" placeholder="select topic" @focus="getTopicsList()">
                <el-option v-for="(item,index) in guanrateesListOptions" :key="item+index" :label="item" :value="item"/>
              </el-select>
            </el-form-item>
            <el-form-item label="useConfig" prop="useConfig">
              <input type="file" @change="loadUserConfig">
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
            <el-form-item label="inputs" prop="inputTopics">
              <el-select v-model="temp.inputTopic" placeholder="select topic" @focus="getTopicsList()">
                <el-option v-for="(item,index) in inputTopicsListOptions" :key="item+index" :label="item" :value="item"/>
              </el-select>
            </el-form-item>
            <el-form-item label="output" prop="outputTopics">
              <el-select v-model="temp.outputTopic" placeholder="select topic" @focus="getTopicsList()">
                <el-option v-for="(item,index) in outputTopicsListOptions" :key="item+index" :label="item" :value="item"/>
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
  createFunction,
  deleteFunction
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
      dialogStatus: '',
      listQuery: {
        topic: '',
        page: 1,
        limit: 10
      },
      temp: {
        autoAck: false,
        className: '',
        inputTopic: '',
        outputTopic: '',
        language: false,
        cpu: '',
        disk: '',
        schemaInput: '',
        serdeInput: '',
        serdeOutput: '',
        letterTopic: '',
        fqfn: '',
        logTopic: '',
        parallelism: '',
        ram: '',
        retainOrdering: false,
        schemaType: '',
        slidingIntervalCount: '',
        slidingIntervalDurationMs: '',
        subName: '',
        timeoutMs: '',
        windowLengthCount: '',
        windowLengthDurationMs: '',
        topicsPattern: '',
        currentFile: '',
        currentFileName: '',
        currentJsonFile: ''
      },
      rules: {
        functions: [{ required: true, message: 'functions name is required', trigger: 'blur' }]
      }
    }
  },
  mounted() {
    this.actionsListOptions = [
      { value: 'ATLEAST_ONCE', label: 'ATLEAST_ONCE' },
      { value: 'ATMOST_ONCE', label: 'ATMOST_ONCE' },
      { value: 'EFFECTIVELY_ONCE', label: 'EFFECTIVELY_ONCE' }
    ]
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
            this.localList.push({ 'function': response.data[i], 'stats': 'stats' })
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
    },
    handleDelete(row) {
      deleteFunction(this.tenant, this.namespace, row.function).then(response => {
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
      const data = {
        'name': this.temp.functions,
        'inputs': [this.temp.inputTopic],
        'output': this.temp.outputTopic,
        'className': this.temp.className
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
      formData.append('functionConfig', JSON.stringify(data))
      createFunction(this.tenant, this.namespace, this.temp.functions, formData).then(response => {
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
    },
    handleUpdate() {

    },
    handleOptions() {
      switch (this.dialogStatus) {
        case 'create':
          this.createFunction()
          break
      }
    },
    loadTextFromFile(ev) {
      const file = ev.target.files[0]
      this.temp.currentFileName = file.name
      var reader = new FileReader()
      reader.onload = e => {
        this.temp.currentFile = ''
        this.temp.currentFile = e.target.result
      }
      reader.readAsText(file)
    },
    loadUserConfig(ev) {
      const file = ev.target.files[0]
      var reader = new FileReader()
      reader.onload = e => {
        this.temp.currentJsonFile = ''
        this.temp.currentJsonFile = e.target.result
      }
      reader.readAsText(file)
    },
    handleGetStats(row) {
      this.temp = Object.assign({}, row) // copy obj
      fetchFunctionStats(this.tenant, this.namespace, this.temp.function).then(response => {
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
    getFunctionsList(tenant, namespace) {
      fetchFunctions(tenant, namespace).then(response => {

      })
    },
    getTopicsList() {
      this.inputTopicsListOptions = []
      this.outputTopicsListOptions = []
      fetchPersistentPartitonsTopics(this.tenant, this.namespace).then(response => {
        for (var i = 0; i < response.data.length; i++) {
          this.inputTopicsListOptions.push(response.data[i])
          this.outputTopicsListOptions.push(response.data[i])
        }
      })
      fetchNonPersistentPartitonsTopics(this.tenant, this.namespace).then(response => {
        for (var i = 0; i < response.data.length; i++) {
          this.inputTopicsListOptions.push(response.data[i])
          this.outputTopicsListOptions.push(response.data[i])
        }
      })
      fetchTopics(this.tenant, this.namespace, this.listQuery).then(response => {
        for (var i = 0; i < response.data.length; i++) {
          this.inputTopicsListOptions.push(response.data[i])
          this.outputTopicsListOptions.push(response.data[i])
        }
      })
    }
  }
}
</script>
