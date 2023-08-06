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
import groovy.json.JsonParserType
import groovy.json.JsonSlurper
import com.kms.katalon.core.util.KeywordUtil
import static org.assertj.core.api.Assertions.*
import  org.codehaus.groovy.ast.stmt.ContinueStatement
import org.apache.poi.hssf.record.PageBreakRecord.Break
import org.apache.commons.lang.RandomStringUtils

String charset = (('A'..'Z') + ('0'..'9')).join()
String randomString = RandomStringUtils.random(10, charset.toCharArray())
String charsetNumber = ('0'..'9').join()
String randomStringNumber = RandomStringUtils.random(10, charsetNumber.toCharArray())

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
KeywordUtil.logInfo("itemlist" + itemlist)

'When Call the request to get chatbot of a company'
def responseChatbots = CustomKeywords.'resources.api.buildApiGetMethod'("api/v1/chatbot/company/${itemlist[itemlist.size()-1].companyId}", token, '')

'Then verify response code should be 200'
WS.verifyResponseStatusCode(responseChatbots, 200)

// Parse the response body as JSON
def responseBodyOfChatbots = jsonSlurper.parseText(responseChatbots.getResponseBodyContent())

'Print the information for each item in the list'
responseBodyOfChatbots.each { info ->
	KeywordUtil.logInfo("Info: ${info}")
}

'get chatbot Id'
def ObjChatbots = responseBodyOfChatbots[responseBodyOfChatbots.size()-1]

'When Call the request to get list of LeadForms'
ResponseObject responseLeadForms = CustomKeywords.'resources.api.buildApiGetMethod'('api/v1/leadForm/all', token, '')

'Then verify response code should be 200'
WS.verifyResponseStatusCode(responseLeadForms, 200)

// Parse the response body as JSON
def responseBodyOfForm = jsonSlurper.parseText(responseLeadForms.getResponseBodyContent())

'Verify that the list contains at least one item'
assert !responseBodyOfForm.empty : 'No content in body!'

'Print the information for each item in the list'
responseBodyOfForm.each { info ->
	KeywordUtil.logInfo("Info LeadForms: ${info}")
}

'Get lead form from response body'
def itemLeadform = []

responseBodyOfForm.each { info ->
		itemLeadform.add(info)
}

println(itemLeadform)

def paramData = findTestData("LeadForm Response/Create_new_lead_form_response")

def data = ""
//Set config which you get data in any rows other
def numberRecordInRows = 1 

//Get all values from file excel
for (int i = 1; i <= paramData.getRowNumbers(); i++) {
	if(i == numberRecordInRows) {
		'Covert string to Object json'
		def objectFormAnswerList = paramData.getValue("formAnswerList", i)
		def formAnswerList  = jsonSlurper.parseText(objectFormAnswerList)
		println("data formAnswerList: ${formAnswerList}")
	
		'Read data from file excel, then preparing for sending request and data'
		data = "{\n  \"chatBotId\": \"${ObjChatbots.chatBotId}\",\n  \"companyId\": \"${itemlist[itemlist.size()-1].companyId}\",\n  \"formAnswerList\": [\n      ${formAnswerList}\n  ],\n  \"leadFormId\": \"${itemLeadform[itemLeadform.size()-1].leadFormId}\"\n}"	
		break
	}
}

KeywordUtil.logInfo('Data request in body: ' + data)

'When Call the request to save a lead form response'
ResponseObject responseFormResponse = CustomKeywords.'resources.api.buildApiPostMethod'('api/v1/leadFormResponse/save', token, data)

'Then verify response code should be 201'
WS.verifyResponseStatusCode(responseFormResponse, 201)

KeywordUtil.logInfo('Response body: ' + responseFormResponse.getResponseText())

'And Verify response body is properly'
WS.verifyEqual(responseFormResponse.getResponseText(), 'Form response Submitted successfully')

