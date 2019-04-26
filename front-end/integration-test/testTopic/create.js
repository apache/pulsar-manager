var request = require('request');
var uuidGenerate = require('uuid/v1');
var uuidTopic = uuidGenerate();
var serviceUrl = "http://localhost:8080";

module.exports = {
  'Topic create': function (browser) {
    browser
      .url(browser.launch_url + '/#/management/topics')
      .waitForElementVisible('body', 10000)
      .pause(1000)
      .waitForElementVisible('#app > div > div.main-container > section > div')
      .pause(1000)
      .useXpath()
      .click('//*[@id="app"]/div/div[2]/section/div/div[2]/button[2]')
      .useXpath()
      .setValue('//*[@id="app"]/div/div[2]/section/div/div[4]/div/div[2]/form/div/div[1]/div/div/input', 'test_create_topic_' + uuidTopic)
      .pause(1000)
      .useXpath()
      .click('//*[@id="app"]/div/div[2]/section/div/div[4]/div/div[3]/div/button[2]')
      .pause(1000);
  },
  'Tenants create check': function (browser) {
    request(serviceUrl + '/admin/v2/persistent/public/default/test_create_topic_' + uuidTopic + '/stats', function (error, response, body) {
      if (error != null) {
        throw error;
      }
      if (response.statusCode === 200) {
        console.log("Create topic pass");
      } else {
        var err = new Error("Create topic test_create_topic" + uuidTopic + " error");
        throw  err;
      }
    });
  }
};
