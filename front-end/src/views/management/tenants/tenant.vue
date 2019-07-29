<template>
  <div class="mixin-components-container">
    <el-row :gutter="24">
      <el-col :span="24">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <h1>Tenant Info</h1>
          </div>
          <h4>Tenant Name</h4>
          <hr class="split-line">
          <div style="height:20px;">
            <el-form>
              <el-form-item prop="title">
                {{ tenant }}
              </el-form-item>
            </el-form>
          </div>
          <h4>Clusters</h4>
          <hr class="split-line">
          <div class="component-item">
            <div class="section-title">
              <span>Allowed Clusters</span>
              <el-tooltip :content="allowedClustersContent" class="item" effect="dark" placement="top">
                <i class="el-icon-info"/>
              </el-tooltip>
            </div>
            <el-select
              v-model="clusterValue"
              style="width:500px;margin-top:20px"
              multiple
              placeholder="Please Select Cluster"
              @change="handleSelectClusters()">
              <el-option v-for="item in clusterOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </div>
          <h4>Permissions</h4>
          <hr class="split-line">
          <div class="component-item">
            <div class="section-title">
              <span>Admin Roles</span>
              <el-tooltip :content="adminRolesContent" class="item" effect="dark" placement="top">
                <i class="el-icon-info"/>
              </el-tooltip>
            </div>
            <div class="section-content">
              <el-tag
                v-for="tag in dynamicRoles"
                :key="tag"
                :disable-transitions="false"
                closable
                @close="handleClose(tag)">
                {{ tag }}
              </el-tag>
              <el-input
                v-if="inputVisible"
                ref="saveTagInput"
                v-model="inputValue"
                size="small"
                class="input-new-tag"
                @keyup.enter.native="handleInputConfirm"/>
              <el-button v-else class="button-new-tag" size="small" @click="showInput">+ New Role</el-button>
            </div>
          </div>
          <hr class="split-line">
          <el-button type="danger" class="button" @click="deleteTenant">Delete</el-button>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { fetchClusters } from '@/api/clusters'
import { fetchTenantsInfo, updateTenant, deleteTenant } from '@/api/tenants'
export default {
  name: 'TenantInfo',
  data() {
    return {
      dynamicRoles: [],
      inputVisible: false,
      inputValue: '',
      tenant: '',
      adminRoles: '',
      allowedClustersContent: 'This is Allowed Clusters',
      adminRolesContent: 'This is Admin Roles',
      clusterValue: [],
      clusterOptions: []
    }
  },
  created() {
    this.tenant = this.$route.params && this.$route.params.tenant
    this.getClustersList()
    this.getTenantsInfo()
  },
  methods: {
    handleClose(tag) {
      this.dynamicRoles.splice(this.dynamicRoles.indexOf(tag), 1)
      const data = {}
      data.adminRoles = this.dynamicRoles
      updateTenant(this.tenant, data).then(() => {
        this.$notify({
          title: 'success',
          message: 'Update role success',
          type: 'success',
          duration: 2000
        })
      })
    },
    showInput() {
      this.inputVisible = true
      this.$nextTick(_ => {
        this.$refs.saveTagInput.$refs.input.focus()
      })
    },
    handleInputConfirm() {
      const inputValue = this.inputValue
      if (inputValue) {
        if (this.dynamicRoles.indexOf(this.inputValue) >= 0) {
          this.$notify({
            title: 'error',
            message: 'Role is exists',
            type: 'error',
            duration: 2000
          })
          return
        }
        this.dynamicRoles.push(inputValue)
      }
      this.inputVisible = false
      this.inputValue = ''
      const data = {}
      data.adminRoles = this.dynamicRoles
      updateTenant(this.tenant, data).then(() => {
        this.$notify({
          title: 'success',
          message: 'Update role success',
          type: 'success',
          duration: 2000
        })
      })
    },
    getClustersList() {
      fetchClusters().then(response => {
        for (var i in response.data.data) {
          this.clusterOptions.push({
            value: response.data.data[i].cluster,
            label: response.data.data[i].cluster
          })
        }
      })
    },
    getTenantsInfo() {
      fetchTenantsInfo(this.tenant).then(response => {
        this.clusterValue = response.data.allowedClusters
        this.dynamicRoles = response.data.adminRoles
      })
    },
    handleSelectClusters() {
      const data = {}
      data.allowedClusters = this.clusterValue
      updateTenant(this.tenant, data).then(() => {
        this.$notify({
          title: 'success',
          message: 'Update cluster success',
          type: 'success',
          duration: 2000
        })
      })
    },
    deleteTenant() {
      deleteTenant(this.tenant).then((response) => {
        this.$notify({
          title: 'success',
          message: 'delete success',
          type: 'success',
          duration: 2000
        })
        this.$router.push({ path: '/management/tenants' })
      })
    }
  }
}
</script>

<style scoped>
.el-tag + .el-tag {
    margin-left: 10px;
  }
.button-new-tag {
  margin-left: 10px;
  height: 32px;
  line-height: 30px;
  padding-top: 0;
  padding-bottom: 0;
}
.input-new-tag {
  width: 90px;
  margin-left: 10px;
  vertical-align: bottom;
}
.section-content {
  margin-top: 15px;
}
.confirm-button {
  margin-top: 10px;
}
.section-title {
  color: rgba(0,0,0,.45);
  font-size: 16px;
}
.mixin-components-container {
  background-color: #f0f2f5;
  padding: 30px;
  min-height: calc(100vh - 84px);
}
.component-item{
  min-height: 60px;
}
.button {
  right:0px;
  margin-bottom:15px;
  float: right;
}
.split-line {
  background: #e6e9f3;
  border: none;
  height: 1px;
}
</style>
