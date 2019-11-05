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
    <div class="filter-container">
      <el-input v-model="listQuery.bookie" placeholder="Bookie" style="width: 200px;" class="filter-item" @keyup.enter.native="handleFilter"/>
      <el-button class="filter-item" style="margin-left: 10px;" type="primary" icon="el-icon-edit" @click="handleCreate">{{ $t('table.add') }}</el-button>
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
          <el-table-column label="Bookies" min-width="100px" align="center">
            <template slot-scope="scope">
              <span>{{ scope.row.bookie }}</span>
            </template>
          </el-table-column>
          <el-table-column label="Group" align="center" min-width="50px">
            <template slot-scope="scope">
              <span>{{ scope.row.group }}</span>
            </template>
          </el-table-column>
          <el-table-column label="Rack" align="center" min-width="50px">
            <template slot-scope="scope">
              <span>{{ scope.row.rack }}</span>
            </template>
          </el-table-column>
          <el-table-column label="HostName" align="center" min-width="50px">
            <template slot-scope="scope">
              <span>{{ scope.row.hostname }}</span>
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
        <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getBookies" />
      </el-col>
    </el-row>
    <el-dialog :visible.sync="dialogFormVisible">
      <el-form ref="dataForm" :rules="rules" :model="temp" label-position="left" label-width="90px" style="width: 400px; margin-left:50px;">
        <div v-if="dialogStatus==='create'||dialogStatus==='update'">
          <el-form-item label="bookie" prop="bookie">
            <el-input v-model="temp.bookie"/>
          </el-form-item>
          <el-form-item label="group" prop="group">
            <el-input v-model="temp.group"/>
          </el-form-item>
          <el-form-item label="hostname" prop="hostname">
            <el-input v-model="temp.hostname"/>
          </el-form-item>
          <el-form-item label="rack" prop="rack">
            <el-input v-model="temp.rack"/>
          </el-form-item>
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
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import {
  racksInfo,
  updateRacksByBookie,
  deleteRacksByBookie
} from '@/api/bookies'
import { validateEmpty } from '@/utils/validate'

export default {
  name: 'Bookies',
  components: {
    Pagination
  },
  data() {
    return {
      loading: false,
      tableKey: 0,
      list: null,
      listLoading: false,
      localList: [],
      searchList: [],
      total: 0,
      dialogFormVisible: false,
      currentBookie: '',
      dialogStatus: '',
      listQuery: {
        bookie: '',
        page: 1,
        limit: 10
      },
      temp: {
        bookie: '',
        group: '',
        hostname: '',
        rack: ''
      },
      rules: {
        bookie: [{ required: true, message: 'bookie is required', trigger: 'blur' }],
        group: [{ required: true, message: 'group is required', trigger: 'blur' }],
        hostname: [{ required: true, message: 'hostname is required', trigger: 'blur' }],
        rack: [{ required: true, message: 'rack is required', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getBookies()
  },
  methods: {
    getBookies() {
      if (this.localList.length > 0) {
        setTimeout(() => {
          this.localPaging()
        }, 0)
      } else {
        this.listLoading = true
        racksInfo().then(response => {
          for (var group in response.data) {
            for (var bookie in response.data[group]) {
              this.localList.push({
                'bookie': bookie,
                'group': group,
                'rack': response.data[group][bookie]['rack'],
                'hostname': response.data[group][bookie]['hostname']
              })
            }
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
      if (!validateEmpty(this.listQuery.bookie)) {
        this.searchList = []
        for (var i = 0; i < this.localList.length; i++) {
          if (this.localList[i]['bookie'].indexOf(this.listQuery.bookie) !== -1) {
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
    getCurrentRow(item) {
      this.currentBookie = item.bookie
    },
    handleUpdate(row) {
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
    },
    handleDelete(row) {
      this.confirmDelete(row)
    },
    handleFilter() {
      this.getBookies()
    },
    handleCreate() {
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.resetTemp()
    },
    handleOptions() {
      switch (this.dialogStatus) {
        case 'create':
          this.confirmCreateAndUpdate()
          break
        case 'update':
          this.confirmCreateAndUpdate()
          break
      }
    },
    confirmCreateAndUpdate() {
      const data = {
        'rack': this.temp.rack,
        'hostname': this.temp.hostname
      }
      updateRacksByBookie(this.temp.bookie, this.temp.group, data).then(response => {
        this.$notify({
          title: 'success',
          message: 'Create success for bookies',
          type: 'success',
          duration: 3000
        })
        this.localList = []
        this.getBookies()
        this.dialogFormVisible = false
      })
    },
    confirmDelete(row) {
      deleteRacksByBookie(row.bookie).then(response => {
        this.$notify({
          title: 'success',
          message: 'Delete success for bookies',
          type: 'success',
          duration: 3000
        })
        this.localList = []
        this.getBookies()
      })
    },
    resetTemp() {
      this.temp = {
        bookie: '',
        group: '',
        hostname: '',
        rack: ''
      }
    }
  }
}
</script>

