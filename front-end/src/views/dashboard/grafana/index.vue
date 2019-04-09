<template>
  <div class="dashboard-prometheus">
    <div v-if="iframe==='yes'">
      <iframe ref="iframe" :src="src" class="dashboard-prometheus-iframe"/>
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
.dashboard-prometheus {
  padding: 10px;
  height: 1000px;
  width: auto;
}
.dashboard-prometheus-iframe {
  width: 100%;
  height: 1000px;
}
</style>
