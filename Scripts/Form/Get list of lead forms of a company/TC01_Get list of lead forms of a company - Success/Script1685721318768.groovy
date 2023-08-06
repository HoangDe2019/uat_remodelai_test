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

String charset = (('A'..'Z') + ('0'..'9')).join()
String randomString = RandomStringUtils.random(10, charset.toCharArray())
String charsetNumber = ('0'..'9').join()
String randomStringNumber = RandomStringUtils.random(10, charsetNumber.toCharArray())

'Load list dump data'
def paramData = findTestData("LeadForm/Create_new_date_form")

def data = ""
//Set config which you get data in any rows other
def numberRecordInRows = 2 

'Get key token'
def token = CustomKeywords.'resources.api.loginAndGetToken'('login', GlobalVariable.email,GlobalVariable.password)

'When Call the request to get list of users'
ResponseObject responseUsers = CustomKeywords.'resources.api.buildApiGetMethod'('api/v1/user/all', token, '')

'Then verify response code should be 200'
WS.verifyResponseStatusCode(responseUsers, 200)

//Parse the response body as JSON
def jsonSlurper = new JsonSlurper()
def responseBodyOfUsers = jsonSlurper.parseText(responseUsers.getResponseBodyContent())

'And Verify that the list contains at least one item'
assert !responseBodyOfUsers.empty : 'No content in body!'

def itemlist = []
'Print the information for each item in the list'
responseBodyOfUsers.each { info ->
	KeywordUtil.logInfo("Info users: ${info}")
	if(info.email.equals(GlobalVariable.email)) {
		itemlist.add(info)
	}
}
KeywordUtil.logInfo("itemlist" + itemlist)

'When Call the request to get list of templates'
ResponseObject responseTemplates = CustomKeywords.'resources.api.buildApiGetMethod'("api/v1/template/list?email=" + itemlist[itemlist.size()-1].email, token, '')

'Then verify response code should be 200'
WS.verifyResponseStatusCode(responseTemplates, 200)

// Parse the response body as JSON
def responseBodyTemplates = jsonSlurper.parseText(responseTemplates.getResponseBodyContent())

'And Verify that the list contains at least one item'
assert !responseBodyTemplates.empty : 'No content in body!'

'Print the information for each item in the list'
responseBodyTemplates.each { info ->
	KeywordUtil.logInfo("Info: ${info}")
}

def templateId = responseBodyTemplates[0].templateId
def companyId = responseBodyTemplates[0].companyId

//Get all values from file excel
for (int i = 1; i <= paramData.getRowNumbers(); i++) {
	if(i == numberRecordInRows) {
		KeywordUtil.logInfo('row: ' + i)
		'Read data from file excel, then preparing for sending request and data'
		data = '{\n  \"companyId\": \"'+ companyId +'\",\n  \"leadFormName\": \"' + paramData.getValue("leadFormName", i) + randomString + '\",\n  \"templateId\": \"' + templateId + '\"\n}'
		break
	}
}
KeywordUtil.logInfo('Data request in body: ' + data)

'When Call the request to create new lead form'
ResponseObject res = CustomKeywords.'resources.api.buildApiPostMethod'('api/v1/leadForm/create', token, data)

'Then verify response code should be 201 Created'
WS.verifyResponseStatusCode(res, 201)

'And Verify response body is not null'
def result = jsonSlurper.parseText(res.responseText)

assert !result.empty

KeywordUtil.logInfo('Response body: ' + jsonSlurper.parseText(res.responseText).toString())

'And Verify response body is properly'
assert result.leadFormId !=null
WS.verifyElementPropertyValue(res, "companyId", companyId)
WS.verifyElementPropertyValue(res, "templateId", templateId)
WS.verifyElementPropertyValue(res, "leadFormName", paramData.getValue("leadFormName", numberRecordInRows) + randomString)

'When Call the request to get lead forms of a company by companyId'
ResponseObject responseFormDetail = CustomKeywords.'resources.api.buildApiGetMethod'('api/v1/leadForm/company/' + companyId, token, '')

' Then verify response code should be 200'
WS.verifyResponseStatusCode(responseFormDetail, 200)

// Parse the response body as JSON
def responseBodyOfFormDetail = jsonSlurper.parseText(responseFormDetail.getResponseBodyContent())

'And Verify that the list contains at least one item'
assert !responseBodyOfFormDetail.empty

'Print the information for each item in the list'
responseBodyOfFormDetail.each { info ->
	KeywordUtil.logInfo("Info lead forms: ${info}")
	assert info.leadFormId != null
	assert info.leadFormName != null
	assert info.companyId == companyId
	assert info.templateId == templateId
}

