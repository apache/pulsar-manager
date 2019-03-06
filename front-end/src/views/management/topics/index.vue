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
                      <el-select v-model="postForm.tenant" placeholder="选择租户" @change="getNamespacesList(postForm.tenant)">
                        <el-option v-for="(item,index) in tenantsListOptions" :key="item+index" :label="item" :value="item"/>
                      </el-select>
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item class="postInfo-container-item">
                      <el-select v-model="postForm.namespace" placeholder="选择项目组" @change="getTopicsList(postForm.tenant, postForm.namespace)">
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
    </div>
    <el-row :gutter="8">
      <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 12}" :xl="{span: 12}" style="padding-right:8px;margin-bottom:30px;">
        <el-table
          v-loading="listLoading"
          :key="tableKey"
          :data="list"
          border
          fit
          highlight-current-row
          style="width: 100%;">
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
      <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 12}" :xl="{span: 12}" style="margin-bottom:30px;">
        <jsonEditor :value="jsonValue"/>
      </el-col>
    </el-row>

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form ref="dataForm" :rules="rules" :model="temp" label-position="left" label-width="70px" style="width: 400px; margin-left:50px;">
        <el-form-item v-if="dialogStatus==='create'" :label="$t('table.topic')" prop="topic">
          <el-input v-model="temp.topic"/>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='create'" :label="$t('table.partition')" prop="topic">
          <el-input v-model="temp.partitions"/>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='update'" :label="$t('table.topic')">
          <span>{{ temp.topic }}</span>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='update'" :label="$t('table.partition')" prop="topic">
          <el-input v-model="temp.partitions"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">{{ $t('table.cancel') }}</el-button>
        <el-button type="primary" @click="dialogStatus==='create'?createData():updateData()">{{ $t('table.confirm') }}</el-button>
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
  updateTopic,
  getPartitionMetadata,
  fetchPartitionTopicStats,
  fetchPersistentPartitonsTopics,
  fetchNonPersistentPartitonsTopics,
  deletePartitionTopic
} from '@/api/topics'
import { parsePulsarSchema } from '@/utils'
import waves from '@/directive/waves' // Waves directive
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import jsonEditor from '@/components/JsonEditor'
import { validateEmpty } from '@/utils/validate'
const defaultForm = {
  tenant: '',
  namespace: ''
}

export default {
  name: 'Topics',
  components: {
    Pagination,
    jsonEditor
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
      tableKey: 0,
      list: null,
      localList: [],
      searchList: [],
      total: 0,
      listLoading: true,
      jsonValue: {},
      tenant: '',
      namespace: '',
      listQuery: {
        topic: '',
        page: 1,
        limit: 10
      },
      temp: {
        topic: '',
        partitions: 0
      },
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: 'Edit',
        create: 'Create'
      },
      rules: {
        tenant: [{ required: true, message: 'tenant is required', trigger: 'blur' }]
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
  methods: {
    getTopics() {
      if (this.localList.length > 0) {
        setTimeout(() => {
          this.localPaging()
        }, 0)
      } else {
        this.listLoading = true
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
            this.localList.push({ 'topic': response.data[i], 'isPartition': 'no', 'partitions': '0' })
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
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    createData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          if (this.tenant.length <= 0 || this.namespace <= 0) {
            this.$notify({
              title: 'error',
              message: 'please select tenant and namespace',
              type: 'success',
              duration: 2000
            })
          }
          putTopic(this.tenant, this.namespace, this.temp.topic, parseInt(this.temp.partitions)).then(() => {
            this.localList = []
            this.getTopics()
            this.dialogFormVisible = false
            this.$notify({
              title: 'success',
              message: 'create success',
              type: 'success',
              duration: 2000
            })
          })
        }
      })
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
      this.$refs['dataForm'].validate((valid) => {
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
          message: '非分区partition暂时不支持删除',
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
        const index = this.list.indexOf(row)
        this.list.splice(index, 1)
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
