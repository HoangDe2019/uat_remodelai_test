import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
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
import org.apache.commons.lang.RandomStringUtils

String charset = (('a'..'z')+ (' ') + ('A'..'Z') + (' ') + ('0'..'9')).join()
String randomString = RandomStringUtils.random(10, charset.toCharArray())
String charsetNumber = ('0'..'9').join()
String randomStringNumber = RandomStringUtils.random(20, charsetNumber.toCharArray())

def jsonSlurper = new JsonSlurper()

'Load list dump data'
def paramData = findTestData("Template/Create_new_template_data_request")

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
def responseBodyOfUsers = jsonSlurper.parseText(response.getResponseBodyContent())

'Verify that the list contains at least one item'
assert !responseBodyOfUsers.empty : 'No content in body!'

'Print the information for each item in the list'
def itemlist = []
responseBodyOfUsers.each { info ->
	KeywordUtil.logInfo("Info: ${info}")
	if(info.email.equals(GlobalVariable.email)) {
		itemlist.add(info)
	}
}

println(itemlist)

//Get all values from file excel
for (int i = 1; i <= paramData.getRowNumbers(); i++) {
	if(i == numberRecordInRows) {
		KeywordUtil.logInfo('row: ' + i)
		'Read data from file excel, then preparing for sending request and data'
		data = '{\n    \"companyId\": \"' + itemlist[itemlist.size()-1].companyId + '\",\n    \"userId\": \"' + itemlist[itemlist.size()-1].userId + '\",\n    \"email\": \"' + itemlist[itemlist.size()-1].email + '\",\n    \"displayName\": \"' + paramData.getValue("displayName", i) + randomString + '\",\n    \"singleOptionQuestions\": [\n        {\n            \"questionText\": \"' + paramData.getValue("singleOptionQuestions__questionText", i) + randomString + '\"\n        },\n        {\n            \"questionText\": \"' + paramData.getValue("singleOptionQuestions__questionText", (i+1)) + randomString + '\"\n        }\n    ],\n    \"multipleChoiceQuestions\": [\n        {\n            \"questionText\": \"' + paramData.getValue("multipleChoiceQuestions__questionText", i) + randomString + '\",\n            \"options\": [\n                \"' + paramData.getValue("multipleChoiceQuestions__options", i) + randomString + '\",\n                \"' + paramData.getValue("multipleChoiceQuestions__options", (i+1)) + randomString + '\",\n                \"' + paramData.getValue("multipleChoiceQuestions__options", (i+2)) + randomString + '\",\n                \"' + paramData.getValue("multipleChoiceQuestions__options", (i+3)) + randomString + '\"\n            ]\n        }\n    ]\n}'
		break
	}
}

KeywordUtil.logInfo('Data request in body: ' + data)

'When Call the request to create new template'
ResponseObject resCreated = CustomKeywords.'resources.api.buildApiPostMethod'('api/v1/template/create', token, data)

'Then verify response code should be 201 Created'
WS.verifyResponseStatusCode(resCreated, 201)

'And Verify response body is not null'
def result = jsonSlurper.parseText(resCreated.responseText)

assert !result.empty

KeywordUtil.logInfo('Response body: ' + result)


'When Call the request to get list of templates'
ResponseObject responseTemplates = CustomKeywords.'resources.api.buildApiGetMethod'("api/v1/template/list?email="+itemlist[itemlist.size()-1].email, token, '')

'Then verify response code should be 200'
WS.verifyResponseStatusCode(responseTemplates, 200)

// Parse the response body as JSON
def responseBodyTemplates = jsonSlurper.parseText(responseTemplates.getResponseBodyContent())

'And Verify that the list contains at least one item'
assert !responseBodyTemplates.empty : 'No content in body!'

'Print the information for each item in the list'
def elmTempls = []
responseBodyTemplates.each { info ->
	KeywordUtil.logInfo("Info: ${info}")
	elmTempls.add(info)
}

println(elmTempls)

def templateId = elmTempls[elmTempls.size() - elmTempls.size()].templateId

'When Call the request to delete template'
ResponseObject res = CustomKeywords.'resources.api.buildApiDeleteMethod'('api/v1/template/delete/' + templateId, token, '')

'Then verify response code should be 200'
WS.verifyResponseStatusCode(res, 200)
