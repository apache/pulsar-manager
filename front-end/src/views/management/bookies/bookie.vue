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
      <el-form :inline="true" :model="postForm" class="form-container">
        <el-form-item class="postInfo-container-item" label="Bookie">
          <el-select v-model="postForm.bookie" placeholder="select bookie" @change="getBookieInfo()">
            <el-option v-for="(item,index) in bookiesListOptions" :key="item+index" :label="item" :value="item"/>
          </el-select>
        </el-form-item>
      </el-form>
    </div>
    <el-row :gutter="24">
      <el-col :span="12">
        <el-card style="height: 300px">
          <h4>Basic info</h4>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card body-style>
          <h4>Heartbeat</h4>
          <el-button type="primary" circle class="circle"><span>{{ heartbeat }}</span></el-button>
          <el-button type="primary" style="display:block;margin-top:15px;margin-left:auto;margin-right:auto;" @click="handleHeartbeat()">Heartbeat</el-button>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card>
          <h4>GC</h4>
          <el-button type="primary" circle class="circle"><span>Gc</span></el-button>
          <el-button type="primary" style="display:block;margin-top:15px;margin-left:auto;margin-right:auto;" @click="handleGc()">Gc</el-button>
        </el-card>
      </el-col>
      <!-- <el-col :span="4">
        <el-card>
          <h4>Decommission</h4>
          <el-button type="primary" circle class="circle"><span class="circle-font">xx</span></el-button>
          <el-button type="primary" style="display:block;margin-top:15px;margin-left:auto;margin-right:auto;">Decommission</el-button>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card>
          <h4>Auto Recovery</h4>
          <el-button type="primary" circle class="circle"><span class="circle-font">xx</span></el-button>
          <el-button type="primary" style="display:block;margin-top:15px;margin-left:auto;margin-right:auto;">Recovery</el-button>
        </el-card>
      </el-col> -->
      <el-col :span="4">
        <el-card>
          <h4>Trigger</h4>
          <el-button type="primary" circle class="circle"><span class="circle-font">Trigger</span></el-button>
          <el-button type="primary" style="display:block;margin-top:15px;margin-left:auto;margin-right:auto;">Trigger</el-button>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>

import { getBookiesList, heartbeat } from '@/api/bookies'

const defaultForm = {
  cluster: '',
  bookie: ''
}

export default {
  name: 'BookieInfo',
  data() {
    return {
      postForm: Object.assign({}, defaultForm),
      bookiesListOptions: [],
      heartbeat: ''
    }
  },
  created() {
    this.postForm.cluster = this.$route.params && this.$route.params.cluster
    this.postForm.bookie = this.$route.params && this.$route.params.bookie
    this.getBookiesList()
    this.handleHeartbeat()
  },
  methods: {
    getBookiesList() {
      getBookiesList(this.postForm.cluster, {}).then(response => {
        if (response.data.enableBookieHttp) {
          for (var i = 0; i < response.data.data.length; i++) {
            this.bookiesListOptions.push(response.data.data[i].bookie)
          }
        }
      })
    },
    getBookieInfo() {

    },
    handleHeartbeat() {
      heartbeat(this.postForm.bookie).then(response => {
        if (!response.data) return
        this.heartbeat = response.data.heartbeat
      })
    },
    handleGc() {

    }
  }
}
</script>

<style>
.circle {
  height: 150px !important;
  width: 150px !important;
  border-radius: 100% !important;
  display:block;
  margin:0 auto;
}
</style>
