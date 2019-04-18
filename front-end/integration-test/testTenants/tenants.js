var request = require('request')

var serviceUrl = "http://localhost:8080"

module.exports = {
  'Tenants create' : function (browser) {
    browser
      .url(browser.launchUrl + '/#/management/tenants')
      .waitForElementVisible('body')
      .pause(1000)
      .waitForElementVisible('div[class=app-container]')
      .useXpath()
      .click("//section[@class='app-main']/div[@class='app-container']/div[@class='filter-container']/button/span[text()='Add']")
      .useCss()
      .setValue("input[placeholder='Please input tenant']", 'test-integrations')
      .useXpath()
      .click("//div[contains(@class, 'el-form-item')]/div[contains(@class, 'el-select')]/div[contains(@class, 'el-input')]")
      .pause(1000)
      .useXpath()
      .click("//div[@x-placement='bottom-start']")
      .useXpath()
      .click("//div[contains(@class, 'dialog-footer')]/button/span[text()='Confirm']")
      .pause(2000)
  },
  'Tenants create check': function(browser) {
    request(serviceUrl + '/admin/v2/tenants/test-integrations', function(error, response, body) {
        if (response.statusCode == 200) {
          console.log("Create tenants pass")
        } else {
          console.log("Create tenants failed")
        }
      }
    )
  },
  'Tenants search': function (browser) {
    browser
      .useCss()
      .setValue("input[placeholder='Tenant']", 'test-integrations')
      .useXpath()
      .click("//span[text()='Search']")
  },
  'Tenants config': function (browser) {
    browser
      .click("//span[text()='config']")
      .pause(1000)
  },
  'Tenants edit': function (browser) {
    browser
    .click("//span[text()='Edit']")
    .click("//div[contains(@class, 'dialog-footer')]/button/span[text()='Confirm']")
    .pause(1000)
  },
  'Tenants delete': function (browser) {
    browser
      .click("//span[text()='Delete']")
      .pause(2000)
      .end();
  },
  'Tenants delete check': function(browser) {
    request(serviceUrl + '/admin/v2/tenants/test-integrations', function(error, response, body) {
        if (response.statusCode == 404) {
          console.log("Delete tenants pass")
        } else {
          console.log("Delete tenants failed")
        }
      }
    )
  }
};