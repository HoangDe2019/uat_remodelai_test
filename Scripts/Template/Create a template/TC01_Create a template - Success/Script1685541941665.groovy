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

KeywordUtil.logInfo("itemlist: " + itemlist)

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
ResponseObject res = CustomKeywords.'resources.api.buildApiPostMethod'('api/v1/template/create', token, data)

'Then verify response code should be 201 Created'
WS.verifyResponseStatusCode(res, 201)

'And Verify response body is not null'
def result = jsonSlurper.parseText(res.responseText)

assert !result.empty

KeywordUtil.logInfo('Response body: ' + result)

'And Verify response body is properly'
assert result.templateId != null
WS.verifyElementPropertyValue(res, "companyId", itemlist[itemlist.size()-1].companyId)
WS.verifyElementPropertyValue(res, "userId", itemlist[itemlist.size()-1].userId)
WS.verifyElementPropertyValue(res, "email", itemlist[itemlist.size()-1].email)
WS.verifyElementPropertyValue(res, "displayName", paramData.getValue("displayName", numberRecordInRows) + randomString)
WS.verifyElementPropertyValue(res, "singleOptionQuestions.questionText[0]", paramData.getValue("singleOptionQuestions__questionText", numberRecordInRows) + randomString)
WS.verifyElementPropertyValue(res, "singleOptionQuestions.questionText[1]", paramData.getValue("singleOptionQuestions__questionText", numberRecordInRows + 1) + randomString)
WS.verifyElementPropertyValue(res, "multipleChoiceQuestions.questionText[0]", paramData.getValue("multipleChoiceQuestions__questionText", numberRecordInRows) + randomString)
def optionList =[paramData.getValue("multipleChoiceQuestions__options",  numberRecordInRows) + randomString, paramData.getValue("multipleChoiceQuestions__options",  numberRecordInRows + 1) + randomString, paramData.getValue("multipleChoiceQuestions__options",  numberRecordInRows + 2) + randomString, paramData.getValue("multipleChoiceQuestions__options",  numberRecordInRows + 3) + randomString]
WS.verifyElementPropertyValue(res, "multipleChoiceQuestions.options[0]", optionList)