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
  <div class="dashboard-grafana">
    <div v-if="iframe==='yes'">
      <iframe ref="iframe" :src="src" class="dashboard-grafana-iframe"/>
    </div>
    <div v-if="iframe==='no'">
      <span>Apache Pulsar</span>
    </div>
  </div>
</template>

<script>

export default {
  name: 'Grafana',
  data() {
    return {
      src: '',
      iframe: 'no'
    }
  },
  created() {
    if (process.env.GRAFANA_ADDRESS.length > 0) {
      this.iframe = 'yes'
      this.src = process.env.GRAFANA_ADDRESS
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.dashboard-grafana {
  padding: 10px;
  height: 1000px;
  width: auto;
}
.dashboard-grafana-iframe {
  width: 100%;
  height: 1000px;
}
</style>
