<script>
export default {
  name: 'Index',
  watch: {
    $route: {
      handler: function(route) {
        this.$store.dispatch('LoginByCasdoor', route.query.code, route.query.state).then(() => {
          this.$router.push({ path: '/' })
        }).catch(() => {
          console.log('login error!!')
        })
      },
      immediate: true
    }

  }
}
</script>
