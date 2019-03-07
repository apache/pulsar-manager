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
                </el-row>
              </div>
            </el-col>
          </el-row>
        </div>
      </el-form>
    </div>
    <div class="filter-container">
      <el-input :placeholder="$t('table.namespace')" v-model="listQuery.namespace" style="width: 200px;" class="filter-item" @keyup.enter.native="handleFilter"/>
      <el-button v-waves class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">{{ $t('table.search') }}</el-button>
      <el-button class="filter-item" style="margin-left: 10px;" type="primary" icon="el-icon-edit" @click="handleCreate">{{ $t('table.add') }}</el-button>
    </div>

    <div>
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
            <el-table-column :label="$t('table.namespace')" min-width="50px" align="center">
              <template slot-scope="scope">
                <span class="link-type" @click="getNamespacePolicies(scope.row.namespace)">{{ scope.row.namespace }}</span>
              </template>
            </el-table-column>
            <el-table-column :label="$t('table.policies')" min-width="50px" align="center">
              <template slot-scope="scope">
                <router-link :to="'/management/namespaces/' + scope.row.namespace + '/policies'" class="link-type">
                  <span>policies</span>
                </router-link>
              </template>
            </el-table-column>
            <el-table-column :label="$t('table.actions')" align="center" width="150" class-name="small-padding fixed-width">
              <template slot-scope="scope">
                <el-button v-if="scope.row.status!='deleted'" size="mini" type="danger" @click="handleDelete(scope.row)">{{ $t('table.delete') }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getNamespaces" />
        </el-col>
        <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 12}" :xl="{span: 12}" style="margin-bottom:30px;">
          <jsonEditor :value="jsonValue"/>
        </el-col>
      </el-row>

    </div>

    <el-dialog :visible.sync="dialogFormVisible">
      <el-form ref="dataForm" :rules="rules" :model="temp" label-position="left" label-width="100px" style="width: 400px; margin-left:50px;">
        <el-form-item :label="$t('table.namespace')" prop="namespace">
          <el-input v-model="temp.namespace"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">{{ $t('table.cancel') }}</el-button>
        <el-button type="primary" @click="createData()">{{ $t('table.confirm') }}</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import { fetchNamespaces, fetchNamespacePolicies, putNamespace, deleteNamespace } from '@/api/namespaces'
import { fetchTenants } from '@/api/tenants'
import waves from '@/directive/waves' // Waves directive
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import jsonEditor from '@/components/JsonEditor'
import { validateEmpty } from '@/utils/validate'

const defaultForm = {
  tenant: ''
}

export default {
  name: 'Namespaces',
  components: {
    Pagination,
    jsonEditor
  },
  directives: { waves },
  data() {
    return {
      postForm: Object.assign({}, defaultForm),
      loading: false,
      tenantsListOptions: [],
      tableKey: 0,
      tenant: '',
      list: null,
      localList: [],
      searchList: [],
      total: 0,
      jsonValue: {},
      listLoading: true,
      policiesListLoading: false,
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
        namespace: ''
      },
      dialogFormVisible: false,
      rules: {
        namespace: [{ required: true, message: 'namespace is required', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.tenant = this.$route.params && this.$route.params.tenant
    console.log(this.tenant)
    this.getNamespaces()
    this.getRemoteTenantsList()
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
          for (var i = 0; i < response.data.length; i++) {
            this.localList.push({ 'namespace': response.data[i] })
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
    getNamespacePolicies(namespace) {
      // this.policiesListLoading = true
      fetchNamespacePolicies(namespace).then(response => {
        this.jsonValue = response.data
        // Just to simulate the time of the request
        setTimeout(() => {
          this.policiesListLoading = false
        }, 1.5 * 1000)
      })
    },
    handleFilter() {
      this.getNamespaces()
    },
    resetTemp() {
      this.temp = {
        namespace: ''
      }
    },
    handleCreate() {
      this.resetTemp()
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    createData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
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
        }
      })
    },
    handleDelete(row) {
      deleteNamespace(row.namespace).then((response) => {
        this.$notify({
          title: 'success',
          message: 'delete success',
          type: 'success',
          duration: 2000
        })
        this.localList = []
        this.getNamespaces()
      })
    },
    getRemoteTenantsList() {
      fetchTenants().then(response => {
        if (!response.data) return
        this.tenantsListOptions = response.data
        console.log(this.tenantsListOptions)
      })
    },
    getNamespacesList(tenant) {
      this.tenant = tenant
      this.localList = []
      this.getNamespaces()
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
