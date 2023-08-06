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

String charset = (('a'..'z') + ('A'..'Z') + ('0'..'9')).join()
String randomString = RandomStringUtils.random(10, charset.toCharArray())
String charsetNumber = ('0'..'9').join()
String randomStringNumber = RandomStringUtils.random(10, charsetNumber.toCharArray())

'Load list dump data'
def paramData = findTestData("Chat/Creates_new_data_completion")

def data = ""
//Set config which you get data in any rows other
def numberRecordInRows = 3

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
	if(info.email.equals(GlobalVariable.email)) {
		itemlist.add(info)
	}
}

KeywordUtil.logInfo("itemlist: " + itemlist)

//Get all values from file excel
for (int i = 1; i <= paramData.getRowNumbers(); i++) {
	if(i == numberRecordInRows) {
		'covert string to Object json chatBotSettings'
		def messagesObj = paramData.getValue("messages", i)
		def messageData = jsonSlurper.parseText(messagesObj)
		println("data messagesObj: ${messageData}")
		
		def stopObj = paramData.getValue("stop", i)
		def stopData = jsonSlurper.parseText(stopObj)
		println("data stopObj: ${stopData}")
		
		def logitBiasObj = paramData.getValue("logitBias", i)
		def logitBiasData = jsonSlurper.parseText(logitBiasObj)
		println("data logitBiasObj: ${logitBiasData}")
		
		'Read data from file excel, then preparing for sending request and data'
		data = "{\r\n    \"model\":\"nglan_2023_${randomString}\",\r\n    \"messages\":[\r\n        ${messageData}\r\n    ],\r\n    \"temperature\":${randomStringNumber},\r\n    \"topP\":${paramData.getValue("topP", i)},\r\n    \"n\":${randomStringNumber},\r\n    \"stream\":${paramData.getValue("stream", i)},\r\n    \"stop\":[${stopData}],\r\n    \"maxTokens\":${paramData.getValue("maxTokens", i)},\r\n    \"presencePenalty\":${randomStringNumber},\r\n    \"frequencyPenalty\":${paramData.getValue("frequencyPenalty", i)},\r\n    \"logitBias\":${logitBiasData},\r\n    \"user\":\"${itemlist[itemlist.size()-1].lastName}\"\r\n}"
		break
	}
}

KeywordUtil.logInfo('Data request in body: ' + data)

def paramURI = ["companyId": itemlist[itemlist.size()-1].companyId]

def paramRequestURI = CustomKeywords.'resources.api.http_build_query'(paramURI, '', '&', 'UTF-8').replace(' ', '')
KeywordUtil.logInfo('Data request URL: ' + paramRequestURI)

'When Call the request to create a completions with URL not mapped'
ResponseObject res = CustomKeywords.'resources.api.buildApiPostMethod'("api/v1/chat/completion?${paramRequestURI}", token, data)

'Then verify response code should be 404'
WS.verifyResponseStatusCode(res, 404)

'And Verify response body is properly'
WS.verifyElementPropertyValue(res, "error", 'Not Found')