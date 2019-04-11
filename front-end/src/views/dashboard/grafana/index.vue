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
