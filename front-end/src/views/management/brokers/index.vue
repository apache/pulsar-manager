<template>
  <div class="app-container">
    <div class="createPost-container">
      <el-form ref="postForm" class="form-container">
        <div class="createPost-main-container">
          <el-row>
            <el-col :span="12">
              <div class="postInfo-container">
                <el-row>
                  <el-col :span="8">
                    <el-form-item class="postInfo-container-item">
                      <el-select v-model="cluster" placeholder="select clusters">
                        <el-option v-for="(item,index) in clusterListOptions" :key="item+index" :label="item" :value="item"/>
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
      <el-button v-waves class="filter-item" type="primary" @click="getBrokerConfig">{{ $t('table.config') }}</el-button>
      <el-button v-waves class="filter-item" type="primary" @click="getBrokerInternalConfig">{{ $t('table.internalConfig') }}</el-button>
      <el-button v-waves class="filter-item" type="primary" @click="getBrokerRuntimeConfig">{{ $t('table.runtimeConfig') }}</el-button>
      <el-button v-waves class="filter-item" type="primary" @click="getBrokersDynamicConfigValues">{{ $t('table.dynamicConfig') }}</el-button>
      <el-button v-waves class="filter-item" type="primary" @click="getBrokersHealth">{{ $t('table.healthCheck') }}</el-button>
    </div>
    <div>
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
            <el-table-column :label="$t('table.brokers')" min-width="50px" align="center">
              <template slot-scope="scope">
                <span>{{ scope.row.brokers }}</span>
              </template>
            </el-table-column>
            <el-table-column :label="$t('table.namespace')" min-width="50px" align="center">
              <template slot-scope="scope">
                <span class="link-type" @click="getBrokersNamespaceConfig(scope.row)">{{ scope.row.namespace }}</span>
              </template>
            </el-table-column>
            <el-table-column :label="$t('table.actions')" align="center" width="240" class-name="small-padding fixed-width">
              <template slot-scope="scope">
                <el-button type="primary" size="mini" @click="handleUpdate(scope.row)">{{ $t('table.edit') }}</el-button>
              </template>
            </el-table-column>
          </el-table>
          <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getBrokers" />
        </el-col>
        <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 10}" :xl="{span: 10}" style="margin-bottom:30px;">
          <jsonEditor :value="jsonValue"/>
        </el-col>
      </el-row>
      <el-dialog :visible.sync="dialogFormVisible">
        <el-form ref="temp" :rules="rules" :model="temp" label-position="left" label-width="100px" style="width: 400px; margin-left:50px;">
          <div v-if="dialogStatus==='update'">
            <el-form-item :label="$t('table.configName')" prop="configName">
              <el-input v-model="temp.configName"/>
            </el-form-item>
            <el-form-item :label="$t('table.configValue')" prop="configValue">
              <el-input v-model="temp.configValue"/>
            </el-form-item>
          </div>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="dialogFormVisible = false">{{ $t('table.cancel') }}</el-button>
          <el-button type="primary" @click="confirmUpdate()">{{ $t('table.confirm') }}</el-button>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script>

import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import jsonEditor from '@/components/JsonEditor'
import { fetchClusters } from '@/api/clusters'
import {
  fetchBrokers,
  fetchBrokersConfiguration,
  fetchBrokersInternalConfiguration,
  fetchBrokersRuntimeConfiguration,
  fetchBrokersOwnedNamespaces,
  fetchBrokersDynamicConfiguration,
  fetchBrokersHealth,
  updateBrokersDynamicConfiguration
} from '@/api/brokers'
import { validateEmpty } from '@/utils/validate'
import waves from '@/directive/waves' // Waves directive

export default {
  name: 'Brokers',
  components: {
    Pagination,
    jsonEditor
  },
  directives: { waves },
  data() {
    return {
      list: null,
      loading: false,
      listLoading: true,
      tableKey: 0,
      localList: [],
      searchList: [],
      cluster: '',
      clusterListOptions: [],
      listQuery: {
        broker: '',
        page: 1,
        limit: 20
      },
      total: 0,
      jsonValue: {},
      dialogFormVisible: false,
      dialogStatus: '',
      temp: {
        configName: '',
        configValue: ''
      },
      rules: {
        configName: [{ required: true, message: 'config name is required', trigger: 'blur' }],
        configValue: [{ required: true, message: 'config value is required', trigger: 'blur' }]
      }
    }
  },
  mounted() {
    this.getRemoteClusters()
  },
  created() {
    this.getBrokers()
  },
  methods: {
    getRemoteClusters() {
      fetchClusters(this.listQuery).then(response => {
        for (var i = 0; i < response.data.length; i++) {
          this.clusterListOptions.push(response.data[i])
        }
      })
    },
    getBrokers() {
      if (this.localList.length > 0) {
        setTimeout(() => {
          this.localPaging()
        }, 0.5 * 1000)
      } else {
        this.listLoading = true
        if (this.cluster == null || this.cluster.length <= 0) {
          this.cluster = 'standalone'
        }
        fetchBrokers(this.cluster).then(response => {
          for (var i = 0; i < response.data.length; i++) {
            this.localList.push({
              'brokers': response.data[i],
              'namespace': 'namespaceConfig'
            })
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
      if (!validateEmpty(this.listQuery.broker)) {
        this.searchList = []
        for (var i = 0; i < this.localList.length; i++) {
          if (this.localList[i]['brokers'].indexOf(this.listQuery.broker) !== -1) {
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
      this.getBrokers()
    },
    handleUpdate() {
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['temp'].clearValidate()
      })
    },
    confirmUpdate() {
      this.$refs['temp'].validate((valid) => {
        if (valid) {
          updateBrokersDynamicConfiguration(this.temp.configName, this.temp.configValue).then(response => {
            this.$notify({
              title: 'success',
              message: 'Set config success',
              type: 'success',
              duration: 3000
            })
            this.dialogFormVisible = false
          })
        }
      })
    },
    getBrokerConfig() {
      fetchBrokersConfiguration().then(response => {
        this.$notify({
          title: 'success',
          message: 'Get config success',
          type: 'success',
          duration: 3000
        })
        this.jsonValue = response.data
      })
    },
    getBrokerInternalConfig() {
      fetchBrokersInternalConfiguration().then(response => {
        this.$notify({
          title: 'success',
          message: 'Get config success',
          type: 'success',
          duration: 3000
        })
        this.jsonValue = response.data
      })
    },
    getBrokerRuntimeConfig() {
      fetchBrokersRuntimeConfiguration().then(response => {
        this.$notify({
          title: 'success',
          message: 'Get config success',
          type: 'success',
          duration: 3000
        })
        this.jsonValue = response.data
      })
    },
    getBrokersNamespaceConfig(row) {
      fetchBrokersOwnedNamespaces(this.cluster, row.brokers).then(response => {
        this.$notify({
          title: 'success',
          message: 'Get namespace config success',
          type: 'success',
          duration: 3000
        })
        this.jsonValue = response.data
      })
    },
    getBrokersDynamicConfigValues() {
      fetchBrokersDynamicConfiguration().then(response => {
        this.$notify({
          title: 'success',
          message: 'Get dynamic config success',
          type: 'success',
          duration: 3000
        })
        this.jsonValue = response.data
      })
    },
    getBrokersHealth() {
      fetchBrokersHealth().then(response => {
        if (response.data === 'ok') {
          this.$notify({
            title: 'success',
            message: 'Health Check success',
            type: 'success',
            duration: 3000
          })
        } else {
          this.$notify({
            title: 'error',
            message: 'Health Check failed',
            type: 'error',
            duration: 3000
          })
        }
      })
    }
  }
}
</script>
