import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.mobile.keyword.builtin.VerifyElementExistKeyword
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import com.kms.katalon.core.util.KeywordUtil
import static org.assertj.core.api.Assertions.*
import  org.codehaus.groovy.ast.stmt.ContinueStatement
import org.apache.poi.hssf.record.PageBreakRecord.Break
import org.apache.commons.lang.RandomStringUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

String charset = (('a'..'z')+ (' ') + ('A'..'Z') + (' ') + ('0'..'9')).join()
String randomString = RandomStringUtils.random(10, charset.toCharArray())
String charsetNumber = ('0'..'9').join()
String randomStringNumber = RandomStringUtils.random(20, charsetNumber.toCharArray())

def data = ""
//Set config which you get data in any rows other
def numberRecordInRows = 1

'Get key token'
def token = CustomKeywords.'resources.api.loginAndGetToken'('login', GlobalVariable.email,GlobalVariable.password)

'When Call the request to get list of users'
ResponseObject response = CustomKeywords.'resources.api.buildApiGetMethod'('api/v1/user/all', token, '')

'Then verify response code should be 200'
WS.verifyResponseStatusCode(response, 200)

// Parse the response body as JSON
def jsonSlurper = new JsonSlurper()
def responseBodyOfUsers = jsonSlurper.parseText(response.getResponseBodyContent())

'Verify that the list contains at least one item'
assert !responseBodyOfUsers.empty : 'No content in body!'

def itemlist = []
'Print the information for each item in the list'
responseBodyOfUsers.each { info ->
	KeywordUtil.logInfo("Info: ${info}")
	if(info.email.equals(GlobalVariable.email)) {
		itemlist.add(info)
	}
}
KeywordUtil.logInfo("itemlist: " + itemlist)

def templateSettings = "{\n        \"from\": \"${GlobalVariable.email}\",\n        \"subject\": \"test email ${randomStringNumber}\",\n        \"serviceRequested\": \"SR-${randomString}\",\n        \"company\": \"test company (TEST-${randomString})\",\n        \"body\": \"test email for email template\",\n        \"dynamicTags\": [\n            \"Name\",\n            \"Date\",\n            \"PhoneNumber\"\n        ],\n        \"generation\": \"dynamic\",\n        \"html_content\": \"<!DOCTYPE html>\\r\\n<html>\\r\\n<head>\\r\\n    <meta charset=\\\"UTF-8\\\">\\r\\n    <meta name=\\\"viewport\\\" content=\\\"width=device-width, initial-scale=1.0\\\">\\r\\n    <title>Appointment Confirmation<\\/title>\\r\\n    <style>\\r\\n\\t\\t\\/* Global styles \\/\\r\\n\\t\\t {\\r\\n\\t\\t\\tmargin: 0;\\r\\n\\t\\t\\tpadding: 0;\\r\\n\\t\\t\\tbox-sizing: border-box;\\r\\n\\t\\t}\\r\\n\\r\\n\\t\\tbody {\\r\\n\\t\\t\\tfont-family: 'Roboto', sans-serif;\\r\\n\\t\\t\\tfont-size: 16px;\\r\\n\\t\\t\\tline-height: 1.5;\\r\\n\\t\\t\\tcolor: #333;\\r\\n\\t\\t\\tbackground-color: #f7f7f7;\\r\\n\\t\\t\\tpadding: 20px;\\r\\n\\t\\t}\\r\\n\\r\\n\\t\\th1, h2 {\\r\\n\\t\\t\\tfont-weight: 700;\\r\\n\\t\\t\\tmargin-bottom: 10px;\\r\\n\\t\\t}\\r\\n\\r\\n\\t\\tp {\\r\\n\\t\\t\\tmargin-bottom: 10px;\\r\\n\\t\\t}\\r\\n\\r\\n\\t\\ta {\\r\\n\\t\\t\\tcolor: #007bff;\\r\\n\\t\\t\\ttext-decoration: none;\\r\\n\\t\\t}\\r\\n\\r\\n\\t\\ttable {\\r\\n\\t\\t\\twidth: 100%;\\r\\n\\t\\t\\tborder-collapse: collapse;\\r\\n\\t\\t\\tmargin-top: 20px;\\r\\n\\t\\t\\tmargin-bottom: 20px;\\r\\n\\t\\t}\\r\\n\\r\\n\\t\\ttable td, table th {\\r\\n\\t\\t\\tborder: 1px solid #ccc;\\r\\n\\t\\t\\tpadding: 10px;\\r\\n\\t\\t\\ttext-align: left;\\r\\n\\t\\t}\\r\\n\\r\\n\\t\\ttable th {\\r\\n\\t\\t\\tbackground-color: #f2f2f2;\\r\\n\\t\\t\\tfont-weight: 700;\\r\\n\\t\\t}\\r\\n\\r\\n\\t\\t.container {\\r\\n\\t\\t\\tmax-width: 600px;\\r\\n\\t\\t\\tmargin: 0 auto;\\r\\n\\t\\t\\tbackground-color: #ebf5ff;\\r\\n\\t\\t\\tpadding: 20px;\\r\\n\\t\\t\\tborder: 1px solid #ccc;\\r\\n\\t\\t\\tborder-radius: 5px;\\r\\n\\t\\t\\tbox-shadow: 0 0 10px rgba(0,0,0,.15);\\r\\n\\t\\t}\\r\\n\\r\\n\\t\\t\\/* Media queries \\/\\r\\n\\t\\t@media only screen and (max-width: 767px) {\\r\\n\\t\\t\\th1 {\\r\\n\\t\\t\\t\\tfont-size: 24px;\\r\\n\\t\\t\\t}\\r\\n\\r\\n\\t\\t\\th2 {\\r\\n\\t\\t\\t\\tfont-size: 20px;\\r\\n\\t\\t\\t}\\r\\n\\r\\n\\t\\t\\tp {\\r\\n\\t\\t\\t\\tfont-size: 14px;\\r\\n\\t\\t\\t}\\r\\n\\r\\n\\t\\t\\ttable td, table th {\\r\\n\\t\\t\\t\\tfont-size: 14px;\\r\\n\\t\\t\\t\\tpadding: 8px;\\r\\n\\t\\t\\t}\\r\\n\\t\\t}\\r\\n\\r\\n\\t\\t\\/ Colors *\\/\\r\\n\\t\\t.primary {\\r\\n\\t\\t\\tcolor: #007bff;\\r\\n\\t\\t\\tbackground-color: #ebf5ff;\\r\\n\\t\\t}\\r\\n\\r\\n\\t\\t.secondary {\\r\\n\\t\\t\\tcolor: #6c757d;\\r\\n\\t\\t\\tbackground-color: #f8f9fa;\\r\\n\\t\\t}\\r\\n\\r\\n\\t\\t.success {\\r\\n\\t\\t\\tcolor: #28a745;\\r\\n\\t\\t\\tbackground-color: #e9f7ef;\\r\\n\\t\\t}\\r\\n\\r\\n\\t\\t.danger {\\r\\n\\t\\t\\tcolor: #dc3545;\\r\\n\\t\\t\\tbackground-color: #f8d7da;\\r\\n\\t\\t}\\r\\n\\r\\n\\t\\t.warning {\\r\\n\\t\\t\\tcolor: #ffc107;\\r\\n\\t\\t\\tbackground-color: #fff3cd;\\r\\n\\t\\t}\\r\\n\\r\\n\\t\\t.info {\\r\\n\\t\\t\\tcolor: #17a2b8;\\r\\n\\t\\t\\tbackground-color: #e2f3f7;\\r\\n\\t\\t}\\r\\n\\t<\\/style>\\r\\n<\\/head>\\r\\n<body>\\r\\n<div class=\\\"container\\\">\\r\\n    <h1 class=\\\"primary\\\">Appointment Confirmation<\\/h1>\\r\\n    <p>Dear {{Name}},<\\/p>\\r\\n    <p>This is to confirm your appointment with us on <strong>{{Date}}<\\/strong> at <strong>{{Time}}<\\/strong>.<\\/p>\\r\\n    <p>Below are the appointment details:<\\/p>\\r\\n    <table>\\r\\n        <tr>\\r\\n            <th>Appointment Time<\\/th>\\r\\n            <td>{{Time}}<\\/td>\\r\\n        <\\/tr>\\r\\n        <tr>\\r\\n            <th>Appointment Date<\\/th>\\r\\n            <td>{{Date}}<\\/td>\\r\\n        <\\/tr>\\r\\n        <tr>\\r\\n            <th>Location<\\/th>\\r\\n            <td>{{Location}}<\\/td>\\r\\n        <\\/tr>\\r\\n        <tr>\\r\\n            <th>Service<\\/th>\\r\\n            <td>{{Service}}<\\/td>\\r\\n        <\\/tr>\\r\\n        <tr>\\r\\n            <th>PhoneNumber<\\/th>\\r\\n            <td>{{PhoneNumber}}<\\/td>\\r\\n        <\\/tr>\\r\\n    <\\/table>\\r\\n    <p>If you need to cancel or reschedule this appointment, please contact us as soon as possible.<\\/p>\\r\\n    <p>Thank you for choosing us for your {{Service}} needs.<\\/p>\\r\\n    <p>Best regards,<\\/p>\\r\\n    <p>The {{Company}} Team<\\/p>\\r\\n<\\/div>\\r\\n<\\/body>\\r\\n<\\/html>\\r\\n\"\n    }"
//param request
data = "{\n    \"companyId\": \"${itemlist[itemlist.size()-1].companyId}\",\n    \"name\": \"test email template No.${randomString}\",\n    \"email\": \"${itemlist[itemlist.size()-1].email}\",\n    \"description\": \"sample template to save it into our database\",\n    \"templateSettings\": ${templateSettings}\n}"

'Param request Data'
KeywordUtil.logInfo('Data request in body: ' + data)

'When Call the request to create new email template'
ResponseObject res = CustomKeywords.'resources.api.buildApiPostMethod'('api/v1/email/template/create', token, data)

'Then verify response code should be 201 Created'
WS.verifyResponseStatusCode(res, 201)

'And Verify response body is not null'
def result = jsonSlurper.parseText(res.responseText)

assert !result.empty
result.each{
	info -> KeywordUtil.logInfo('Response body: ' + info)
}

'And Verify response body is properly'
assert result.emailTemplateId != null
WS.verifyElementPropertyValue(res, "companyId", itemlist[itemlist.size()-1].companyId)
WS.verifyElementPropertyValue(res, "name", "test email template No.${randomString}")
WS.verifyElementPropertyValue(res, "description", "sample template to save it into our database")
WS.verifyElementPropertyValue(res, "email", itemlist[itemlist.size()-1].email)
WS.verifyElementPropertyValue(res, "active", false)

//covert from array to Json, and then to string
String jsonString = JsonOutput.toJson(result.templateSettings)
KeywordUtil.logInfo('jsonString get body content: ' + jsonString)

// create a new response object
ResponseObject ro = new ResponseObject()

// set the response body
ro.responseText = jsonString
KeywordUtil.logInfo('templateSettings get body content: ' + ro.getResponseBodyContent())
def resultTemplateSettings = jsonSlurper.parseText(ro.getResponseBodyContent())

'result templateSettings which was gotten from body content: '
KeywordUtil.logInfo('result Template Settings content: ' + resultTemplateSettings)

'covert string which was file data to Object json templateSettings'
def templateSettingsData = jsonSlurper.parseText(templateSettings.toString().replaceAll('""', ''))
KeywordUtil.logInfo('templateSettings get body content: ' + templateSettingsData)

'And Verify response templateSettings is properly'
WS.verifyElementPropertyValue(ro, "from", templateSettingsData.from)
WS.verifyElementPropertyValue(ro, "subject", templateSettingsData.subject)
WS.verifyElementPropertyValue(ro, "serviceRequested", templateSettingsData.serviceRequested)
WS.verifyElementPropertyValue(ro, "company", templateSettingsData.company)
WS.verifyElementPropertyValue(ro, "body", templateSettingsData.body)
WS.verifyElementPropertyValue(ro, "html_content", templateSettingsData.html_content)
WS.verifyEqual(resultTemplateSettings.dynamicTags, templateSettingsData.dynamicTags)