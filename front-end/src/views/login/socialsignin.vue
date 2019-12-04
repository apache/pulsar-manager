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
  <div class="social-signup-container">
    <div class="sign-btn" @click="githubHandleClick('github')">
      <span class="github-container"><svg-icon icon-class="github" class="icon"/></span> GitHub
    </div>
  </div>
</template>

<script>
import openWindow from '@/utils/openWindow'
import { getGithubLoginHost } from '@/api/socialsignin'

export default {
  name: 'SocialSignin',
  methods: {
    githubHandleClick(thirdpart) {
      getGithubLoginHost().then(response => {
        if (!response.data) return
        if (response.data.message === 'success') {
          openWindow(decodeURIComponent(response.data.url), thirdpart, 540, 540)
        } else {
          this.$notify({
            title: 'failed',
            message: response.data.message,
            type: 'error',
            duration: 3000
          })
        }
      })
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  .social-signup-container {
    margin: 20px 0;
    .sign-btn {
      display: inline-block;
      cursor: pointer;
    }
    .icon {
      color: #fff;
      font-size: 24px;
      margin-top: 8px;
    }
    .github-container {
      display: inline-block;
      width: 40px;
      height: 40px;
      line-height: 40px;
      text-align: center;
      padding-top: 1px;
      border-radius: 4px;
      margin-bottom: 20px;
      margin-right: 5px;
    }
    .github-container {
      background-color: #8ada53;
    }
    .qq-svg-container {
      background-color: #6BA2D6;
      margin-left: 50px;
    }
  }
</style>
