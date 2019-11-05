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
  <div class="code-editor">
    <textarea ref="textarea"/>
  </div>
</template>

<script>
import CodeMirror from 'codemirror'
import 'codemirror/addon/lint/lint.css'
import 'codemirror/lib/codemirror.css'
import 'codemirror/theme/rubyblue.css'
require('script-loader!jsonlint')
import 'codemirror/mode/javascript/javascript'
import 'codemirror/mode/python/python'
import 'codemirror/mode/clike/clike'
import 'codemirror/mode/sql/sql'
import 'codemirror/addon/lint/lint'
import 'codemirror/addon/lint/json-lint'

export default {
  name: 'CodeEditor',
  /* eslint-disable vue/require-prop-types */
  props: ['value', 'mode'],
  data() {
    return {
      codeEditor: false
    }
  },
  watch: {
    value(value) {
      const editor_value = this.codeEditor.getValue()
      if (value !== editor_value) {
        this.codeEditor.setValue(JSON.stringify(this.value, null, 2))
      }
    }
  },
  mounted() {
    this.codeEditor = CodeMirror.fromTextArea(this.$refs.textarea, {
      lineNumbers: true,
      mode: this.mode,
      gutters: ['CodeMirror-lint-markers'],
      theme: 'rubyblue',
      lint: true
    })

    this.codeEditor.setValue(JSON.stringify(this.value, null, 2))
    this.codeEditor.on('change', cm => {
      this.$emit('changed', cm.getValue())
      this.$emit('input', cm.getValue())
    })
  },
  methods: {
    getValue() {
      return this.codeEditor.getValue()
    }
  }
}
</script>

<style scoped>
.code-editor{
  height: 100%;
  position: relative;
}
.code-editor >>> .CodeMirror {
  height: auto;
  min-height: 300px;
}
.code-editor >>> .CodeMirror-scroll{
  min-height: 300px;
}
.code-editor >>> .cm-s-rubyblue span.cm-string {
  color: #F08047;
}
</style>
