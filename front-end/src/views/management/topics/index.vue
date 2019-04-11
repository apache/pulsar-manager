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
                      <el-select v-model="postForm.namespace" placeholder="select namespace" @change="getTopicsList(postForm.tenant, postForm.namespace)">
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
    <div class="filter-container">
      <el-input :placeholder="$t('table.topic')" v-model="listQuery.topic" style="width: 200px;" class="filter-item" @keyup.enter.native="handleFilter"/>
      <el-button v-waves class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">{{ $t('table.search') }}</el-button>
      <el-button class="filter-item" style="margin-left: 10px;" type="primary" icon="el-icon-edit" @click="handleCreate">{{ $t('table.add') }}</el-button>
      <el-dropdown class="filter-item" style="margin-left: 10px;" @command="handleCommand">
        <el-button type="primary">
          Schemas<i class="el-icon-arrow-down el-icon--right"/>
        </el-button>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item command="schemas-get">get</el-dropdown-item>
          <el-dropdown-item command="schemas-delete">delete</el-dropdown-item>
          <el-dropdown-item command="schemas-upload">upload</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
      <el-autocomplete
        v-model="postForm.otherOptions"
        :fetch-suggestions="querySearch"
        class="filter-item inline-input"
        style="margin-left: 10px; width:400px"
        placeholder="select options"
        clearable
        @select="moreListOptionsChange"
      />
    </div>
    <el-row :gutter="8">
      <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 16}" :xl="{span: 16}" style="padding-right:8px;margin-bottom:30px;">
        <el-table
          v-loading="listLoading"
          :key="tableKey"
          :data="list"
          border
          fit
          highlight-current-row
          style="width: 100%;"
          @row-click="getCurrentRow">
          <el-table-column :label="$t('table.topic')" min-width="100px" align="center">
            <template slot-scope="scope">
              <span>{{ scope.row.topic }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('table.stats')" align="center" min-width="50px">
            <template slot-scope="scope">
              <span class="link-type" @click="handleGetStats(scope.row)">stats</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('table.partition')" align="center" min-width="50px">
            <template slot-scope="scope">
              <span>{{ scope.row.isPartition }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('table.actions')" align="center" width="240" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button type="primary" size="mini" @click="handleUpdate(scope.row)">{{ $t('table.edit') }}</el-button>
              <el-button v-if="scope.row.status!='deleted'" size="mini" type="danger" @click="handleDelete(scope.row)">{{ $t('table.delete') }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getTopics" />
      </el-col>
      <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 8}" :xl="{span: 8}" style="margin-bottom:30px;">
        <jsonEditor :value="jsonValue"/>
      </el-col>
    </el-row>

    <el-dialog :visible.sync="dialogFormVisible">
      <el-form ref="temp" :rules="rules" :model="temp" label-position="left" label-width="70px" style="width: 400px; margin-left:50px;">
        <div v-if="dialogStatus==='create'">
          <el-form-item :label="$t('table.topic')" prop="topic">
            <el-input v-model="temp.topic"/>
          </el-form-item>
          <el-form-item :label="$t('table.partition')" prop="partition">
            <el-input v-model="temp.partitions"/>
          </el-form-item>
        </div>
        <div v-if="dialogStatus==='update'">
          <el-form-item :label="$t('table.topic')">
            <span>{{ temp.topic }}</span>
          </el-form-item>
          <el-form-item :label="$t('table.partition')" prop="partition">
            <el-input v-model="temp.partitions"/>
          </el-form-item>
        </div>
        <div v-else-if="dialogStatus==='grant-permission'">
          <el-form-item :label="$t('table.topic')">
            <span>{{ currentTopic }}</span>
          </el-form-item>
          <el-form-item :label="$t('table.grant')" prop="grant">
            <el-drag-select v-model="temp.actions" style="width:300px;" multiple placeholder="Please select">
              <el-option v-for="item in actionsListOptions" :label="item.label" :value="item.value" :key="item.value" />
            </el-drag-select>
          </el-form-item>
          <el-form-item :label="$t('table.role')" prop="role">
            <el-input v-model="temp.role" style="width:300px;" />
          </el-form-item>
        </div>
        <div v-else-if="dialogStatus==='revoke-permission'">
          <el-form-item :label="$t('table.namespace')" prop="namespace">
            <span>{{ currentTopic }}</span>
          </el-form-item>
          <el-form-item :label="$t('table.role')" prop="role">
            <el-input v-model="temp.role"/>
          </el-form-item>
        </div>
        <div v-else-if="dialogStatus==='unload'">
          <el-form-item :label="$t('table.topic')">
            <span>{{ currentTopic }}</span>
          </el-form-item>
        </div>
        <div v-else-if="dialogStatus==='terminate'">
          <el-form-item :label="$t('table.topic')">
            <span>{{ currentTopic }}</span>
          </el-form-item>
        </div>
        <div v-else-if="dialogStatus==='compact'">
          <el-form-item :label="$t('table.topic')">
            <span>{{ currentTopic }}</span>
          </el-form-item>
        </div>
        <div v-else-if="dialogStatus==='offload'">
          <el-form-item :label="$t('table.topic')">
            <span>{{ currentTopic }}</span>
          </el-form-item>
          <el-form-item label="Size" prop="thresholdSize">
            <el-input v-model="temp.thresholdSize"/>
          </el-form-item>
        </div>
        <div v-else-if="dialogStatus==='schemas-delete'">
          <el-form-item :label="$t('table.topic')">
            <span>{{ currentTopic }}</span>
          </el-form-item>
        </div>
        <div v-else-if="dialogStatus==='schemas-upload'">
          <el-form-item :label="$t('table.topic')">
            <span>{{ currentTopic }}</span>
          </el-form-item>
          <label class="text-reader">
            <input type="file" @change="loadTextFromFile">
          </label>
        </div>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">{{ $t('table.cancel') }}</el-button>
        <el-button type="primary" @click="handleOptions()">{{ $t('table.confirm') }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { fetchTenants } from '@/api/tenants'
import { fetchNamespaces } from '@/api/namespaces'
import {
  fetchTopics,
  fetchTopicStats,
  putTopic,
  putTopicByPartition,
  updateTopic,
  getPartitionMetadata,
  fetchPartitionTopicStats,
  fetchPersistentPartitonsTopics,
  fetchNonPersistentPartitonsTopics,
  deletePartitionTopic,
  grantPermissions,
  revokePermissions,
  unload,
  terminate,
  compact,
  compactionStatus,
  offload
} from '@/api/topics'
import { schemasGet, schemasDelete, schemasUpload } from '@/api/schemas'
import { parsePulsarSchema } from '@/utils'
import waves from '@/directive/waves' // Waves directive
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import jsonEditor from '@/components/JsonEditor'
import { validateEmpty } from '@/utils/validate'
import ElDragSelect from '@/components/DragSelect' // base on element-ui
const defaultForm = {
  tenant: '',
  namespace: '',
  otherOptions: ''
}

export default {
  name: 'Topics',
  components: {
    Pagination,
    jsonEditor,
    ElDragSelect
  },
  directives: { waves },
  filters: {
    statusFilter(status) {
      const statusMap = {
        published: 'success',
        draft: 'info',
        deleted: 'danger'
      }
      return statusMap[status]
    }
  },
  props: {
    isEdit: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      postForm: Object.assign({}, defaultForm),
      loading: false,
      tenantsListOptions: [],
      namespacesListOptions: [],
      actionsListOptions: [],
      moreListOptions: [],
      fileList: [],
      tableKey: 0,
      list: null,
      localList: [],
      searchList: [],
      total: 0,
      listLoading: true,
      jsonValue: {},
      tenant: '',
      namespace: '',
      currentTopic: '',
      currentCommand: '',
      isPartitioned: '',
      listQuery: {
        topic: '',
        page: 1,
        limit: 10
      },
      temp: {
        topic: '',
        partitions: 0,
        role: '',
        actions: [],
        thresholdSize: '',
        schemasFilenamePath: '',
        currentJsonFile: ''
      },
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: 'Edit',
        create: 'Create'
      },
      rules: {
        topic: [{ required: true, message: 'topic is required', trigger: 'blur' }],
        thresholdSize: [{ required: true, message: 'thresholdSize is required', trigger: 'blur' }]
      }
      // tempRoute: {}
    }
  },
  computed: {
  },
  created() {
    this.getTopics()
    if (this.isEdit) {
      // const id = this.$route.params && this.$route.params.id
      // this.fetchData(id)
    } else {
      this.postForm = Object.assign({}, defaultForm)
    }
    this.getRemoteTenantsList()
  },
  mounted() {
    this.moreListOptions = this.loadAllOptions()
    this.actionsListOptions = [{ value: 'produce', label: 'produce' }, { value: 'consume', label: 'consume' }]
  },
  methods: {
    getTopics() {
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
        fetchPersistentPartitonsTopics(this.tenant, this.namespace).then(response => {
          for (var i = 0; i < response.data.length; i++) {
            this.localList.push({ 'topic': response.data[i], 'isPartition': 'yes', 'partitions': 0 })
          }
        })
        fetchNonPersistentPartitonsTopics(this.tenant, this.namespace).then(response => {
          for (var i = 0; i < response.data.length; i++) {
            this.localList.push({ 'topic': response.data[i], 'isPartition': 'yes', 'partitions': 0 })
          }
        })
        fetchTopics(this.tenant, this.namespace, this.listQuery).then(response => {
          for (var i = 0; i < response.data.length; i++) {
            this.localList.push({ 'topic': response.data[i], 'isPartition': 'no', 'partitions': 0 })
          }
          this.total = this.localList.length
          this.list = this.localList.slice((this.listQuery.page - 1) * this.listQuery.limit, this.listQuery.limit * this.listQuery.page)
          // this.localPaging()
          // Just to simulate the time of the request
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
          if (this.localList[i]['topic'].indexOf(this.listQuery.topic) !== -1) {
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
      this.getTopics()
    },
    resetTemp() {
      this.temp = {
        topic: ''
      }
    },
    handleCreate() {
      this.resetTemp()
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
    },
    createTopic() {
      if (this.tenant.length <= 0 || this.namespace <= 0) {
        this.$notify({
          title: 'error',
          message: 'please select tenant and namespace',
          type: 'success',
          duration: 2000
        })
        return
      }
      if (parseInt(this.temp.partitions) > 0) {
        putTopicByPartition(this.tenant, this.namespace, this.temp.topic, parseInt(this.temp.partitions)).then(() => {
          this.localList = []
          this.getTopics()
          this.dialogFormVisible = false
          this.$notify({
            title: 'success',
            message: 'create parition topic success',
            type: 'success',
            duration: 2000
          })
        })
      } else {
        putTopic(this.tenant, this.namespace, this.temp.topic, parseInt(this.temp.partitions)).then(() => {
          this.localList = []
          this.getTopics()
          this.dialogFormVisible = false
          this.$notify({
            title: 'success',
            message: 'create topic success',
            type: 'success',
            duration: 2000
          })
        })
      }
    },
    handleUpdate(row) {
      this.temp = Object.assign({}, row) // copy obj
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      const tenantNamespaceTopic = parsePulsarSchema(this.temp.topic)
      getPartitionMetadata(tenantNamespaceTopic[1]).then(response => {
        this.temp.partitions = response.data.partitions
      })
    },
    updateData() {
      this.$refs['temp'].validate((valid) => {
        if (valid) {
          const tempData = Object.assign({}, this.temp)
          if (tempData.isPartition !== 'yes') {
            this.$notify({
              title: 'error',
              message: 'this partition no support update',
              type: 'success',
              duration: 2000
            })
            return
          }
          const tenantNamespaceTopic = parsePulsarSchema(tempData.topic)
          updateTopic(tenantNamespaceTopic[1], parseInt(tempData.partitions)).then(() => {
            this.dialogFormVisible = false
            this.$notify({
              title: 'success',
              message: 'update success',
              type: 'success',
              duration: 2000
            })
          })
        }
      })
    },
    handleDelete(row) {
      this.temp = Object.assign({}, row) // copy obj
      const tenantNamespaceTopic = parsePulsarSchema(this.temp.topic)
      if (this.temp.isPartition !== 'yes') {
        this.$notify({
          title: 'error',
          message: 'non partition no support delete event',
          type: 'success',
          duration: 2000
        })
        return
      }
      deletePartitionTopic(tenantNamespaceTopic[1]).then(response => {
        this.$notify({
          title: 'success',
          message: 'delete success',
          type: 'success',
          duration: 2000
        })
        this.localList = []
        this.getTopics()
      })
    },
    handleGetStats(row) {
      this.temp = Object.assign({}, row) // copy obj
      const tenantNamespaceTopic = parsePulsarSchema(this.temp.topic)
      if (this.temp.isPartition !== 'yes') {
        fetchTopicStats(tenantNamespaceTopic[1]).then(response => {
          this.jsonValue = response.data
        })
      } else {
        fetchPartitionTopicStats(tenantNamespaceTopic[1]).then(response => {
          this.jsonValue = response.data
        })
      }
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
    getTopicsList(tenant, namespace) {
      this.tenant = tenant
      this.namespace = namespace
      this.localList = []
      this.getTopics()
    },
    moreListOptionsChange(item) {
      if (this.currentTopic.length <= 0) {
        this.$notify({
          title: 'error',
          message: 'Please select any one namespace in table',
          type: 'error',
          duration: 3000
        })
        this.postForm.otherOptions = ''
        return
      }
      this.dialogStatus = item.value
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['temp'].clearValidate()
      })
    },
    querySearch(queryString, cb) {
      var moreListOptions = this.moreListOptions
      var results = moreListOptions.filter(this.createFilterOptions(queryString))
      cb(results)
    },
    createFilterOptions(queryString) {
      return (moreListOptions) => {
        return (moreListOptions.value.toLowerCase().indexOf(queryString.toLowerCase()) === 0)
      }
    },
    loadAllOptions() {
      const options = [
        { 'value': 'unload' },
        // No find document for this interface
        // { 'value': 'clear-backlog' },
        { 'value': 'terminate' },
        { 'value': 'compact' },
        { 'value': 'offload' }
      ]
      if (process.env.USE_TLS) {
        options.push({ 'value': 'grant-permission' })
        options.push({ 'value': 'revoke-permission' })
      }
      return options
    },
    getCurrentRow(item) {
      this.currentTopic = parsePulsarSchema(item.topic)[1]
      this.isPartitioned = item.isPartition
    },
    handleOptions() {
      this.$refs['temp'].validate((valid) => {
        if (valid) {
          switch (this.dialogStatus) {
            case 'create':
              this.createTopic()
              break
            case 'update':
              this.updateData()
              break
            case 'grant-permission':
              this.confirmGrantPermission()
              break
            case 'revoke-permission':
              this.confirmRevokePermissions()
              break
            case 'unload':
              this.confirmUnload()
              break
            case 'terminate':
              this.confirmTerminate()
              break
            case 'compact':
              this.confirmCompact()
              break
            case 'offload':
              this.confirmOffload()
              break
            case 'schemas-upload':
              this.confirmSchemasUpload()
              break
          }
        }
      })
    },
    confirmGrantPermission() {
      grantPermissions(this.currentTopic, this.temp.role, this.temp.actions).then(response => {
        this.dialogFormVisible = false
        this.$notify({
          title: 'success',
          message: 'Grant Permissions success',
          type: 'success',
          duration: 3000
        })
      })
    },
    confirmRevokePermissions() {
      revokePermissions(this.currentTopic, this.temp.role).then(response => {
        this.dialogFormVisible = false
        this.$notify({
          title: 'success',
          message: 'Revoke Permissions success',
          type: 'success',
          duration: 3000
        })
      })
    },
    confirmUnload() {
      unload(this.currentTopic).then(response => {
        this.dialogFormVisible = false
        this.$notify({
          title: 'success',
          message: 'Unload success for this topic',
          type: 'success',
          duration: 3000
        })
      })
    },
    confirmTerminate() {
      if (this.isPartitioned === 'yes') {
        this.$notify({
          title: 'warning',
          message: 'Termination of a partitioned topic is not allowed',
          type: 'warning',
          duration: 3000
        })
        return
      }
      terminate(this.currentTopic).then(response => {
        this.dialogFormVisible = false
        this.$notify({
          title: 'success',
          message: 'Terminate for this topic',
          type: 'success',
          duration: 3000
        })
      })
    },
    confirmCompact() {
      if (this.isPartitioned === 'yes') {
        this.$notify({
          title: 'warning',
          message: 'no support in partitioned topic',
          type: 'warning',
          duration: 3000
        })
        return
      }
      compact(this.currentTopic).then(response => {
        this.dialogFormVisible = false
        this.$notify({
          title: 'success',
          message: 'Compact for this topic',
          type: 'success',
          duration: 3000
        })
      })
    },
    confirmOffload() {
      if (this.isPartitioned === 'yes') {
        this.$notify({
          title: 'warning',
          message: 'no support in partitioned topic',
          type: 'warning',
          duration: 3000
        })
        return
      }
      offload(this.currentTopic, this.temp.thresholdSize).then(response => {
        this.dialogFormVisible = false
        this.$notify({
          title: 'success',
          message: 'Compact for this topic',
          type: 'success',
          duration: 3000
        })
      })
    },
    confirmCompactionStatus() {
      const data = this.temp.data
      compactionStatus(this.currentTopic, data).then(response => {
        this.dialogFormVisible = false
        this.$notify({
          title: 'success',
          message: 'compactionStatus for this topic',
          type: 'success',
          duration: 3000
        })
      })
    },
    confirmSchemasGet() {
      schemasGet(this.currentTopic, 0).then(response => {
        this.$notify({
          title: 'success',
          message: 'Schemas get success for this topic',
          type: 'success',
          duration: 3000
        })
        this.jsonValue = response.data
      })
    },
    confirmSchemasDelete() {
      schemasDelete(this.currentTopic).then(response => {
        this.$notify({
          title: 'success',
          message: 'Schemas delete success for this topic',
          type: 'success',
          duration: 3000
        })
      })
    },
    loadTextFromFile(ev) {
      const file = ev.target.files[0]
      var reader = new FileReader()
      reader.onload = e => {
        this.temp.currentJsonFile = ''
        this.temp.currentJsonFile = e.target.result
      }
      reader.readAsText(file)
    },
    confirmSchemasUpload() {
      // don't read json file
      this.dialogFormVisible = true
      schemasUpload(this.currentTopic, this.temp.currentJsonFile).then(response => {
        this.dialogFormVisible = false
        this.$notify({
          title: 'success',
          message: 'Schemas upload success for this topic',
          type: 'success',
          duration: 3000
        })
      })
    },
    handleCommand(command) {
      if (this.currentTopic.length <= 0) {
        this.$notify({
          title: 'error',
          message: 'Please select any one topic in table',
          type: 'error',
          duration: 3000
        })
        return
      }
      this.currentCommand = command
      switch (this.currentCommand) {
        case 'schemas-get':
          this.confirmSchemasGet()
          break
        case 'schemas-upload':
          this.dialogFormVisible = true
          this.dialogStatus = 'schemas-upload'
          break
        case 'schemas-delete':
          this.confirmSchemasDelete()
          break
      }
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
@import "src/styles/mixin.scss";
.createPost-container {
  position: relative;
  .createPost-main-container {
    .postInfo-container {
      position: relative;
      @include clearfix;
      margin-bottom: 10px;
      .postInfo-container-item {
        float: left;
      }
    }
  }
}
</style>
