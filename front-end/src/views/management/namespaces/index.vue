<template>
  <div class="app-container">
    <div class="createPost-container">
      <el-form :inline="true" :model="postForm" class="form-container">
        <el-form-item class="postInfo-container-item" label="Tenant">
          <el-select v-model="postForm.tenant" placeholder="select tenant" @change="getNamespacesList(postForm.tenant)">
            <el-option v-for="(item,index) in tenantsListOptions" :key="item+index" :label="item" :value="item"/>
          </el-select>
        </el-form-item>
      </el-form>
    </div>
    <div class="filter-container">
      <el-input :placeholder="$t('table.namespace')" v-model="listQuery.namespace" style="width: 200px;" class="filter-item" @keyup.enter.native="handleFilter"/>
      <el-button v-waves class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">{{ $t('table.search') }}</el-button>
      <el-button class="filter-item" style="margin-left: 10px;" type="primary" icon="el-icon-edit" @click="handleCreate">{{ $t('table.add') }}</el-button>
    </div>
    <div>
      <el-row :gutter="24">
        <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 24}" :xl="{span: 24}" style="padding-right:8px;margin-bottom:30px;">
          <el-table
            v-loading="listLoading"
            :key="tableKey"
            :data="list"
            border
            fit
            highlight-current-row
            style="width: 100%;">
            <el-table-column :label="$t('table.namespace')" min-width="50px" align="center">
              <template slot-scope="scope">
                <span>{{ scope.row.namespace }}</span>
              </template>
            </el-table-column>
            <el-table-column label="topics" min-width="30px" align="center">
              <template slot-scope="scope">
                <span>{{ scope.row.topics }}</span>
              </template>
            </el-table-column>
            <div v-if="monitorEnable">
              <el-table-column :label="$t('table.monitor')" min-width="30px" align="center">
                <template slot-scope="scope">
                  <a :href="scope.row.monitor" class="link-type">monitor</a>
                </template>
              </el-table-column>
            </div>
            <el-table-column :label="$t('table.actions')" align="center" width="150" class-name="small-padding fixed-width">
              <template slot-scope="scope">
                <router-link :to="'/management/namespaces/' + scope.row.tenant +'/' + scope.row.namespace + '/namespace'">
                  <el-button type="primary" size="mini">{{ $t('table.edit') }}</el-button>
                </router-link>
                <el-button v-if="scope.row.status!='deleted'" size="mini" type="danger" @click="handleDelete(scope.row)">{{ $t('table.delete') }}</el-button>
              </template>
            </el-table-column>
          </el-table>
          <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getNamespaces" />
        </el-col>
      </el-row>
    </div>

    <el-dialog :visible.sync="dialogFormVisible">
      <el-form ref="temp" :rules="rules" :model="temp" label-position="left" label-width="110px" style="width: 400px; margin-left:50px;">
        <div v-if="dialogStatus==='create'">
          <el-form-item :label="$t('table.namespace')" prop="namespace">
            <el-input v-model="temp.namespace" placeholder="Please input namespace"/>
          </el-form-item>
        </div>
        <div v-if="dialogStatus==='delete'">
          <h4>Are you sure you want to delete this namespace {{ temp.tenant }}/{{ temp.namespace }}?</h4>
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
import {
  fetchNamespaces,
  putNamespace,
  deleteNamespace
} from '@/api/namespaces'
import { fetchTenants } from '@/api/tenants'
import { fetchClusters } from '@/api/clusters'
import waves from '@/directive/waves' // Waves directive
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import { validateEmpty } from '@/utils/validate'

const defaultForm = {
  tenant: '',
  otherOptions: ''
}
export default {
  name: 'Namespaces',
  components: {
    Pagination
    // ElDragSelect
  },
  directives: { waves },
  data() {
    return {
      postForm: Object.assign({}, defaultForm),
      loading: false,
      clusters: [],
      clusterListOptions: [],
      tenantsListOptions: [],
      authModeListOptions: [],
      moreListOptions: [],
      startBundleListOptions: [],
      stopBundleListOptions: [],
      tableKey: 0,
      tenant: '',
      otherOptions: '',
      list: null,
      localList: [],
      searchList: [],
      total: 0,
      listLoading: true,
      policiesListLoading: false,
      currentNamespace: '',
      grafanaUrl: '',
      monitorEnable: false,
      listQuery: {
        namespace: '',
        page: 1,
        limit: 20
      },
      policiesQuery: {
        page: 1,
        limit: 20
      },
      temp: {
        namespace: '',
        limit: '',
        actions: [],
        clusters: []
      },
      dialogFormVisible: false,
      dialogStatus: '',
      rules: {
        namespace: [{ required: true, message: 'namespace is required', trigger: 'blur' }],
        grant: [{ required: true, message: 'grant is required', trigger: 'blur' }],
        clusters: [{ required: true, message: 'clusters is required', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.tenant = this.$route.params && this.$route.params.tenant
    if (process.env.GRAFANA_ENABLE) {
      // this.getGrafanaSearch()
    } else {
      this.getNamespaces()
    }
    this.postForm.tenant = this.tenant
    this.getRemoteTenantsList()
  },
  mounted() {
    this.clusterListOptions = []
    fetchClusters(this.listQuery).then(response => {
      for (var i = 0; i < response.data.length; i++) {
        this.clusterListOptions.push({ 'value': response.data[i], 'label': response.data[i] })
      }
    })
  },
  methods: {
    getNamespaces() {
      if (this.localList.length > 0) {
        setTimeout(() => {
          this.localPaging()
        }, 0.5 * 1000)
      } else {
        this.listLoading = true
        if (this.tenant == null || this.tenant.length <= 0) {
          this.tenant = 'public'
        }
        fetchNamespaces(this.tenant, this.listQuery).then(response => {
          for (var i = 0; i < response.data.data.length; i++) {
            if (this.monitorEnable) {
              const monitorUrl = process.env.GRAFANA_ADDRESS + this.grafanaUrl + '?refresh=1m&' + 'var-namespace=' + response.data.data[i].namespace
              this.localList.push({
                'tenant': this.tenant,
                'namespace': response.data.data[i].namespace,
                'topics': response.data.data[i].topics,
                'monitor': monitorUrl
              })
            } else {
              this.localList.push({
                'tenant': this.tenant,
                'namespace': response.data.data[i].namespace,
                'topics': response.data.data[i].topics
              })
            }
          }
          this.total = this.localList.length
          this.list = this.localList.slice((this.listQuery.page - 1) * this.listQuery.limit, this.listQuery.limit * this.listQuery.page)
          // Just to simulate the time of the request
          setTimeout(() => {
            this.listLoading = false
          }, 1.5 * 1000)
        })
      }
    },
    localPaging() {
      this.listLoading = true
      if (!validateEmpty(this.listQuery.namespace)) {
        this.searchList = []
        for (var i = 0; i < this.localList.length; i++) {
          if (this.localList[i]['namespace'].indexOf(this.listQuery.namespace) !== -1) {
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
      this.getNamespaces()
    },
    resetTemp() {
      this.temp = {
        namespace: '',
        limit: '',
        actions: [],
        clusters: []
      }
    },
    handleCreate() {
      this.resetTemp()
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
    },
    createNamespace() {
      putNamespace(this.tenant, this.temp.namespace, this.temp).then(() => {
        this.localList = []
        this.getNamespaces()
        this.dialogFormVisible = false
        this.$notify({
          title: 'success',
          message: 'create success',
          type: 'success',
          duration: 2000
        })
      })
    },
    handleDelete(row) {
      this.dialogStatus = 'delete'
      this.dialogFormVisible = true
      this.temp.tenant = row.tenant
      this.temp.namespace = row.namespace
    },
    deleteData() {
      var tenantNamespace = this.temp.tenant + '/' + this.temp.namespace
      deleteNamespace(tenantNamespace).then((response) => {
        this.$notify({
          title: 'success',
          message: 'delete success',
          type: 'success',
          duration: 2000
        })
        this.dialogFormVisible = false
        this.localList = []
        this.getNamespaces()
      })
    },
    getRemoteTenantsList() {
      fetchTenants().then(response => {
        if (!response.data) return
        for (var i = 0; i < response.data.total; i++) {
          this.tenantsListOptions.push(response.data.data[i].tenant)
        }
      })
    },
    getNamespacesList(tenant) {
      this.tenant = tenant
      this.localList = []
      this.getNamespaces()
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
    handleOptions() {
      this.$refs['temp'].validate((valid) => {
        if (valid) {
          switch (this.dialogStatus) {
            case 'create':
              this.createNamespace()
              break
            case 'delete':
              this.deleteData()
              break
          }
        }
      })
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
