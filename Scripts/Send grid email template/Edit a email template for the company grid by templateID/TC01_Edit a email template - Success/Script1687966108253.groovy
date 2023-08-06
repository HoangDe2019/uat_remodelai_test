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

String charset = (('a'..'z') +  ('A'..'Z') + (' ') + ('0'..'9')).join()
String randomString = RandomStringUtils.random(10, charset.toCharArray())
String charsetNumber = ('0'..'9').join()
String randomStringNumber = RandomStringUtils.random(5, charsetNumber.toCharArray())

def data = ""

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

'Print the information for each item in the list'
responseBodyOfUsers.each { info ->
	KeywordUtil.logInfo("Info: ${info}")
}

'Get list item of response body'
def itemlist = []

responseBodyOfUsers.each { info ->
	itemlist.add(info)
}

println(itemlist)

'When Call the request to get list of template'
ResponseObject responseTemplates = CustomKeywords.'resources.api.buildApiGetMethod'('api/v1/template/list?email='+itemlist[itemlist.size()-1].email, token, '')

'Then verify response code should be 200'
WS.verifyResponseStatusCode(responseTemplates, 200)

// Parse the response body as JSON
def responseBodyTemplates = jsonSlurper.parseText(responseTemplates.getResponseBodyContent())

'And Verify that the list contains at least one item'
assert !responseBodyTemplates.empty : 'No content in body!'

def elemetTemplates = []
'Print the information for each item in the list'
responseBodyTemplates.each { info ->
	KeywordUtil.logInfo("Info: ${info}")
	elemetTemplates.add(info)
}

println("Size of elmTemplates: ${elemetTemplates.size()}")

def templateId = elemetTemplates[elemetTemplates.size()-1].templateId

data = "{\r\n  \"editor\": \"${randomString}\"\r\n}"
KeywordUtil.logInfo('Data request in body: ' + data)

'When Call the request to edit a email template'
ResponseObject res = CustomKeywords.'resources.api.buildApiPostMethod'('api/v1/sendgrid/email/template/' + templateId + '/edit', token, data)

'Then verify response code should be 201 created'
WS.verifyResponseStatusCode(res, 201)

'And Verify response body is not null'
def result = jsonSlurper.parseText(res.responseText)

KeywordUtil.logInfo('Actual response body: ' + result.toString())

assert !result.empty

'And Verify response body is properly'
WS.verifyElementPropertyValue(res, "name", randomString)
WS.verifyElementPropertyValue(res, "generation", randomString.lastIndexOf(3))