module.exports = {
  'Namespaces create': function (browser) {
    browser
      .url(browser.launchUrl + '/#/management/namespaces')
      .waitForElementVisible('body')
      .pause(1000)
      .waitForElementVisible('div[class=app-container]')
      .useXpath()
      .click("//div[contains(@class, 'el-form-item')]/div[contains(@class, 'el-select')]/div[contains(@class, 'el-input')]")
      .pause(1000)
      .click("//div[@x-placement='bottom-start']")
      .pause(1000)
      .click("//section[@class='app-main']/div[@class='app-container']/div[@class='filter-container']/button/span[text()='Add']")
      .pause(1000)
      .useCss()
      .setValue("input[placeholder='Please input namespace']", 'test-integrations-namespace')
      .pause(1000)
      .useXpath()
      .click("//div[contains(@class, 'dialog-footer')]/button/span[text()='Confirm']")
      .pause(1000)
  },
  'Namespace search': function (browser) {
    browser
      .useCss()
      .setValue("input[placeholder='Namespace']", 'test-integrations-namespace')
      .useXpath()
      .click("//span[text()='Search']")
      .pause(1000)
  },
  'Namespace stats': function (browser) {
    browser
      .click("//span[text()='stats']")
      .pause(1000)
  },
  'Quotas get': function (browser) {
    browser
      .useXpath()
      .click("//span[text()='Quotas']")
      .pause(1000)
      .click("//ul[contains(@class, 'el-dropdown-menu')]/li[text()='get']")
      .pause(1000)
      .click("//div[contains(@class, 'dialog-footer')]/button/span[text()='Confirm']")
      .pause(2000)
  },
  'Quotas set': function (browser) {
    browser
      .useXpath()
      .click("//span[text()='Quotas']")
      .pause(1000)
      .click("//ul[contains(@class, 'el-dropdown-menu')]/li[text()='set']")
      .pause(1000)
      .useCss()
      .setValue("input[placeholder='expected memory usage (Mbytes)']", '1024')
      .pause(1000)
      .useXpath()
      .click("//div[contains(@class, 'dialog-footer')]/button/span[text()='Confirm']")
      .pause(1000)
  },
  'Quotas reset': function (browser) {
    browser
      .click("//span[text()='Quotas']")
      .pause(1000)
      .click("//ul[contains(@class, 'el-dropdown-menu')]/li[text()='reset']")
      .pause(2000)
      .click("//div[contains(@class, 'dialog-footer')]/button/span[text()='Confirm']")
      .pause(1000)
  },
  'Namespace set-clusters': function (browser) {
    browser
      .useCss()
      .setValue("input[placeholder='select options']", 'set-clusters')
      .pause(1000)
      .useXpath()
      .click("//li[@role='option']")
      .pause(1000)
      .click("//div[@class='el-dialog']")
      .pause(1000)
      .click("//div[@x-placement='bottom-start']")
      .pause(1000)
      .click("//div[contains(@class, 'dialog-footer')]/button/span[text()='Confirm']")
      .pause(1000)
  },
  'Namespace set-backlog-quota': function (browser) {
    browser
      .useCss()
      .setValue("input[placeholder='select options']", 'set-backlog-quota')
      .pause(1000)
      .useXpath()
      .click("//li[@role='option']")
      .pause(1000)
      .useCss()
      .setValue("input[placeholder='Please select limit'", '1024')
      .pause(1000)
      .useXpath()
      .click("//input[@placeholder='Please select polices']")
      .pause(1000)
      .click("//div[@x-placement='bottom-start']")
      .pause(1000)
      .click("//div[contains(@class, 'dialog-footer')]/button/span[text()='Confirm']")
      .pause(1000)
  },
  'Namespace remove-backlog-quota': function (browser) {
    browser
      .useCss()
      .setValue("input[placeholder='select options']", 'remove-backlog-quota')
      .pause(1000)
      .useXpath()
      .click("//li[@role='option']")
      .pause(1000)
      .click("//div[contains(@class, 'dialog-footer')]/button/span[text()='Confirm']")
      .pause(1000)
  },
  'Namespace set-persistence': function (browser) {
    browser
      .useCss()
      .setValue("input[placeholder='select options']", 'set-persistence')
      .pause(1000)
      .useXpath()
      .click("//li[@role='option']")
      .pause(1000)
      .click("//div[contains(@class, 'dialog-footer')]/button/span[text()='Confirm']")
      .pause(1000)
  },
  'Namespace set-message-ttl': function (browser) {
    browser
      .useCss()
      .setValue("input[placeholder='select options']", 'set-message-ttl')
      .pause(1000)
      .useXpath()
      .click("//li[@role='option']")
      .pause(1000)
      .click("//div[contains(@class, 'dialog-footer')]/button/span[text()='Confirm']")
      .pause(1000)
  },
  'Namespace set-anti-affinity-group': function (browser) {
    browser
      .useCss()
      .setValue("input[placeholder='select options']", 'set-anti-affinity-group')
      .pause(1000)
      .useXpath()
      .click("//li[@role='option']")
      .pause(1000)
      .useCss()
      .setValue("input[placeholder='Please input group'", 'test')
      .pause(1000)
      .useXpath()
      .click("//div[contains(@class, 'dialog-footer')]/button/span[text()='Confirm']")
      .pause(1000)
  },
  'Namespace delete-anti-affinity-group': function (browser) {
    browser
      .useCss()
      .setValue("input[placeholder='select options']", 'delete-anti-affinity-group')
      .pause(1000)
      .useXpath()
      .click("//li[@role='option']")
      .pause(1000)
      .click("//div[contains(@class, 'dialog-footer')]/button/span[text()='Confirm']")
      .pause(1000)
  },
  'Namespace set-deduplication': function (browser) {
    browser
      .useCss()
      .setValue("input[placeholder='select options']", 'set-deduplication')
      .pause(1000)
      .useXpath()
      .click("//li[@role='option']")
      .pause(1000)
      .click("//div[contains(@class, 'dialog-footer')]/button/span[text()='Confirm']")
      .pause(1000)
  },
  'Namespace set-retention': function (browser) {
    browser
      .useCss()
      .setValue("input[placeholder='select options']", 'set-retention')
      .pause(1000)
      .useXpath()
      .click("//li[@role='option']")
      .pause(1000)
      .useCss()
      .setValue("input[placeholder='Please input retentionSize']", '10M')
      .pause(1000)
      .setValue("input[placeholder='Please input retentionTime']", '100m')
      .pause(1000)
      .useXpath()
      .click("//div[contains(@class, 'dialog-footer')]/button/span[text()='Confirm']")
      .pause(1000)
  },
  'Namespace unload': function (browser) {
    browser
      .useCss()
      .setValue("input[placeholder='select options']", 'unload')
      .pause(1000)
      .useXpath()
      .click("//li[@role='option']")
      .pause(1000)
      .useXpath()
      .click("//div[contains(@class, 'dialog-footer')]/button/span[text()='Confirm']")
      .pause(1000)
  },
  'Namespace split-bundle': function (browser) {
    browser
      .useCss()
      .setValue("input[placeholder='select options']", 'split-bundle')
      .pause(1000)
      .useXpath()
      .click("//li[@role='option']")
      .pause(1000)
      .useXpath()
      .click("//div[contains(@class, 'dialog-footer')]/button/span[text()='Confirm']")
      .pause(1000)
  },
  'Namespace set-dispatch-rate': function (browser) {
    browser
      .useCss()
      .setValue("input[placeholder='select options']", 'set-dispatch-rate')
      .pause(1000)
      .useXpath()
      .click("//li[@role='option']")
      .pause(1000)
      .useXpath()
      .click("//div[contains(@class, 'dialog-footer')]/button/span[text()='Confirm']")
      .pause(1000)
  },
  'Namespace clear-backlog': function (browser) {
    browser
      .useCss()
      .setValue("input[placeholder='select options']", 'clear-backlog')
      .pause(1000)
      .useXpath()
      .click("//li[@role='option']")
      .pause(1000)
      .useXpath()
      .click("//div[contains(@class, 'dialog-footer')]/button/span[text()='Confirm']")
      .pause(1000)
  },
  'Namespace unsubscribe': function (browser) {
    browser
      .useCss()
      .setValue("input[placeholder='select options']", 'unsubscribe')
      .pause(1000)
      .useXpath()
      .click("//li[@role='option']")
      .pause(1000)
      .useCss()
      .setValue("input[placeholder='Please input subName']", 'test')
      .pause(1000)
      .useXpath()
      .click("//div[contains(@class, 'dialog-footer')]/button/span[text()='Confirm']")
      .pause(1000)
  },
  'Namespace set-encryption-required': function (browser) {
    browser
      .useCss()
      .setValue("input[placeholder='select options']", 'set-encryption-required')
      .pause(1000)
      .useXpath()
      .click("//li[@role='option']")
      .pause(1000)
      .useXpath()
      .click("//div[contains(@class, 'dialog-footer')]/button/span[text()='Confirm']")
      .pause(1000)
  },
  'Namespace set-subscription-auth-mode': function (browser) {
    browser
      .useCss()
      .setValue("input[placeholder='select options']", 'set-subscription-auth-mode')
      .pause(1000)
      .useXpath()
      .click("//li[@role='option']")
      .pause(1000)
      .useXpath()
      .click("//input[@placeholder='Please select authMode']")
      .pause(1000)
      .click("//div[@x-placement='bottom-start']")
      .pause(1000)
      .useXpath()
      .click("//div[contains(@class, 'dialog-footer')]/button/span[text()='Confirm']")
      .pause(1000)
  },
  'Namespace set-max-producers-per-topic': function (browser) {
    browser
      .useCss()
      .setValue("input[placeholder='select options']", 'set-max-producers-per-topic')
      .pause(1000)
      .useXpath()
      .click("//li[@role='option']")
      .pause(1000)
      .useXpath()
      .click("//div[contains(@class, 'dialog-footer')]/button/span[text()='Confirm']")
      .pause(1000)
  },
  'Namespace set-max-consumers-per-topic': function (browser) {
    browser
      .useCss()
      .setValue("input[placeholder='select options']", 'set-max-consumers-per-topic')
      .pause(1000)
      .useXpath()
      .click("//li[@role='option']")
      .pause(1000)
      .useXpath()
      .click("//div[contains(@class, 'dialog-footer')]/button/span[text()='Confirm']")
      .pause(1000)
  },
  'Namespace set-max-consumers-per-subscription': function (browser) {
    browser
      .useCss()
      .setValue("input[placeholder='select options']", 'set-max-consumers-per-subscription')
      .pause(1000)
      .useXpath()
      .click("//li[@role='option']")
      .pause(1000)
      .useXpath()
      .click("//div[contains(@class, 'dialog-footer')]/button/span[text()='Confirm']")
      .pause(1000)
  },
  'Namespace set-compaction-threshold': function (browser) {
    browser
      .useCss()
      .setValue("input[placeholder='select options']", 'set-compaction-threshold')
      .pause(1000)
      .useXpath()
      .click("//li[@role='option']")
      .pause(1000)
      .useXpath()
      .click("//div[contains(@class, 'dialog-footer')]/button/span[text()='Confirm']")
      .pause(1000)
  },
  'Namespace set-offload-threshold': function (browser) {
    browser
      .useCss()
      .setValue("input[placeholder='select options']", 'set-offload-threshold')
      .pause(1000)
      .useXpath()
      .click("//li[@role='option']")
      .pause(1000)
      .useXpath()
      .click("//div[contains(@class, 'dialog-footer')]/button/span[text()='Confirm']")
      .pause(1000)
  },
  'Namespace set-offload-deletion-lag': function (browser) {
    browser
      .useCss()
      .setValue("input[placeholder='select options']", 'set-offload-deletion-lag')
      .pause(1000)
      .useXpath()
      .click("//li[@role='option']")
      .pause(1000)
      .useXpath()
      .click("//div[contains(@class, 'dialog-footer')]/button/span[text()='Confirm']")
      .pause(1000)
  },
  'Namespace clear-offload-deletion-lag': function (browser) {
    browser
      .useCss()
      .setValue("input[placeholder='select options']", 'clear-offload-deletion-lag')
      .pause(1000)
      .useXpath()
      .click("//li[@role='option']")
      .pause(1000)
      .useXpath()
      .click("//div[contains(@class, 'dialog-footer')]/button/span[text()='Confirm']")
      .pause(1000)
  },
  'Namespace set-schema-autoupdate-strategy': function (browser) {
    browser
      .useCss()
      .setValue("input[placeholder='select options']", 'set-schema-autoupdate-strategy')
      .pause(1000)
      .useXpath()
      .click("//li[@role='option']")
      .pause(1000)
      .useXpath()
      .click("//div[contains(@class, 'dialog-footer')]/button/span[text()='Confirm']")
      .pause(1000)
  },
  'Namespace delete': function (browser) {
    browser
      .click("//span[text()='Delete']")
  },
  'Namespace end': function (browser) {
    browser
      .pause(2000)
      .end()
  }
}