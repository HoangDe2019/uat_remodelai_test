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

String charset = (('a'..'z')+ (' ') + ('A'..'Z') + (' ') + ('0'..'9')).join()
String randomString = RandomStringUtils.random(10, charset.toCharArray())
String charsetNumber = ('0'..'9').join()
String randomStringNumber = RandomStringUtils.random(20, charsetNumber.toCharArray())

'Get key token'
def token = CustomKeywords.'resources.api.loginAndGetToken'('login', GlobalVariable.email,GlobalVariable.password)

'Load list dump data'
def paramData = findTestData("Template/Create_new_template_data_request")

def data = ""
//Set config which you get data in any rows other
def numberRecordInRows = 3

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
def itemlist = []
responseBodyOfUsers.each { info ->
	KeywordUtil.logInfo("Info: ${info}")
	if(info.email.equals(GlobalVariable.email)) {
		itemlist.add(info)
	}
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

'Print the information for each item in the list'
def elmTemplates = []
responseBodyTemplates.each { info ->
	KeywordUtil.logInfo("Info: ${info}")
	elmTemplates.add(info)
}
println("Size of elmTemplates: ${elmTemplates.size()}")

def templateId = elmTemplates[elmTemplates.size()-1].templateId

//Get all values from file excel
for (int i = 1; i <= paramData.getRowNumbers(); i++) {
	if(i == numberRecordInRows) {
		KeywordUtil.logInfo('row: ' + i)
		'Read data from file excel, then preparing for sending request and data'
		data = '{\n    \"companyId\": \"' + randomString + '\",\n    \"templateId\": \"' + templateId + '\",\n    \"userId\": \"' + itemlist[itemlist.size()-1].userId + '\",\n    \"email\": \"' + itemlist[itemlist.size()-1].email + '\",\n    \"displayName\": \"' + paramData.getValue("displayName", i) + '\",\n    \"singleOptionQuestions\": [\n        {\n            \"questionText\": \"' + paramData.getValue("singleOptionQuestions__questionText", i) + '\"\n        },\n        {\n            \"questionText\": \"' + paramData.getValue("singleOptionQuestions__questionText", (i+1)) + '\"\n        }\n    ],\n    \"multipleChoiceQuestions\": [\n        {\n            \"questionText\": \"' + paramData.getValue("multipleChoiceQuestions__questionText", i) + '\",\n            \"options\": [\n                \"' + paramData.getValue("multipleChoiceQuestions__options", i) + '\",\n                \"' + paramData.getValue("multipleChoiceQuestions__options", (i+1)) + '\",\n                \"' + paramData.getValue("multipleChoiceQuestions__options", (i+2)) + '\",\n                \"' + paramData.getValue("multipleChoiceQuestions__options", (i+3)) + '\"\n            ]\n        }\n    ]\n}'
		break
	}
}

KeywordUtil.logInfo('Data request in body: ' + data)

'When Call the request to update a template with invalid companyId'
ResponseObject res1 = CustomKeywords.'resources.api.buildApiPutMethod'('api/v1/template/update', token, data)

'Then verify response code should be 400'
WS.verifyResponseStatusCode(res1, 400)


def data2 = "{'" + randomString + "'}"

'When Call the request to update a template with invalid request body'
ResponseObject res2 = CustomKeywords.'resources.api.buildApiPutMethod'('api/v1/template/update', token, data2)

'Then verify response code should be 400'
WS.verifyResponseStatusCode(res2, 400)