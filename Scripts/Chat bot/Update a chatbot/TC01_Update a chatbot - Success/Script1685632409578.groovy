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

'Load list dump data'
def paramData = findTestData("Chatbot/Create_new_data_chatbot")

def data = ""
//Set config which you get data in any rows other
def numberRecordInRows = 2

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
println(itemlist)

'When Call the request to get chatbot of a company'
def responseChatbots = CustomKeywords.'resources.api.buildApiGetMethod'("api/v1/chatbot/company/${itemlist[0].companyId}", token, '')

'Then verify response code should be 200'
WS.verifyResponseStatusCode(responseChatbots, 200)

// Parse the response body as JSON
def responseBodyOfChatbots = jsonSlurper.parseText(responseChatbots.getResponseBodyContent())

'Print the information for each item in the list'
def itemList = []
responseBodyOfChatbots.each { info ->
	KeywordUtil.logInfo("Info: ${info}")
	itemList.add(info)

}

def ObjChatbots = itemList[itemList.size()-1]

KeywordUtil.logInfo("chatbotId: " + ObjChatbots.chatBotId)

def chatBotSettingTemp = null
//Get all values from file excel
for (int i = 1; i <= paramData.getRowNumbers(); i++) {
	if(i == numberRecordInRows) {
		'covert string to Object json'
		def object = paramData.getValue("chatBotSettings", i)
		def chatBotSettings = jsonSlurper.parseText(object)
		println("data chatBotSettings: ${chatBotSettings}")
		chatBotSettingTemp = chatBotSettings
		'Read data from file excel, then preparing for sending request and data'
		data = "{\r\n    \"chatBotName\": \"${ObjChatbots.chatBotName}\",\r\n    \"chatBotId\": \"${ObjChatbots.chatBotId}\",\r\n    \"agentName\": \"${ObjChatbots.agentName}\",\r\n    \"companyId\": \"${itemlist[0].companyId}\",\r\n    \"chatBotSettings\": ${chatBotSettings}\r\n}"
		break
	}
}

KeywordUtil.logInfo('Data request in body: ' + data)

'When Call the request to update a chatbot'
ResponseObject res = CustomKeywords.'resources.api.buildApiPutMethod'('api/v1/chatbot/update', token, data)

'Then verify response code should be 200'
WS.verifyResponseStatusCode(res, 200)

'And Verify response body is not null'
def result = jsonSlurper.parseText(res.responseText)

KeywordUtil.logInfo('Actual response body: ' + result.toString())

assert !result.empty

KeywordUtil.logInfo('Response body: ' + jsonSlurper.parseText(res.responseText).toString())

'And Verify response body is properly'
WS.verifyElementPropertyValue(res, "chatBotId", ObjChatbots.chatBotId)
WS.verifyElementPropertyValue(res, "companyId", itemlist[0].companyId)
WS.verifyElementPropertyValue(res, "chatBotName", ObjChatbots.chatBotName)
WS.verifyElementPropertyValue(res, "agentName", ObjChatbots.agentName)

//covert from array to Json, and then to string
String jsonString = JsonOutput.toJson(result.chatBotSettings)
KeywordUtil.logInfo('jsonString get body content: ' + jsonString)

// create a new response object
ResponseObject ro = new ResponseObject()

// set the response body
ro.responseText = jsonString
KeywordUtil.logInfo('elmObj get body content: ' + ro.getResponseBodyContent())
def resultChatBotSetting = jsonSlurper.parseText(ro.getResponseBodyContent())

'resultChatBotSetting which was gotten from body content: '
KeywordUtil.logInfo('resultChatBotSetting content: ' + resultChatBotSetting)

'covert string which was file data to Object json chatBotSettings'
def chatbtSettingData = jsonSlurper.parseText(chatBotSettingTemp.toString().replaceAll('""', ''))
KeywordUtil.logInfo('jsonObject get body content: ' + chatbtSettingData)

'And Verify response chatbotsetting is properly'
WS.verifyElementPropertyValue(ro, "companyBackground", chatbtSettingData.companyBackground)
WS.verifyElementPropertyValue(ro, "productsAndServices", chatbtSettingData.productsAndServices)
WS.verifyElementPropertyValue(ro, "pricing", chatbtSettingData.pricing)
WS.verifyElementPropertyValue(ro, "prompt", chatbtSettingData.prompt)
WS.verifyElementPropertyValue(ro, "laborAndWarranty", chatbtSettingData.laborAndWarranty)
WS.verifyElementPropertyValue(ro, "serviceArea", chatbtSettingData.serviceArea)
WS.verifyElementPropertyValue(ro, "extraInformation", chatbtSettingData.extraInformation)
WS.verifyElementPropertyValue(ro, "customerServicePhoneNumber", chatbtSettingData.customerServicePhoneNumber)
WS.verifyElementPropertyValue(ro, "customerServiceEmail", chatbtSettingData.customerServiceEmail)
