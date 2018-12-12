<template>
  <div class="app-container">
    <codeEditor :value="codeValue" :mode="codeMode"/>
  </div>
</template>

<script>
import codeEditor from '@/components/CodeEditor'
export default {
  name: 'Functions',
  components: { codeEditor },
  data() {
    return {
      codeValue: null,
      // codeMode: 'text/x-cython'
      codeMode: 'text/x-java'
    }
  }
}
</script>
