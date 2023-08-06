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
import org.json.JSONObject
import com.kms.katalon.core.util.KeywordUtil
import static org.assertj.core.api.Assertions.*
import  org.codehaus.groovy.ast.stmt.ContinueStatement
import org.apache.poi.hssf.record.PageBreakRecord.Break
import org.apache.commons.lang.RandomStringUtils

String charset = (('A'..'Z') + ('0'..'9')).join()
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

//Parse the response body as JSON
def responseBodyOfChatbots = jsonSlurper.parseText(responseChatbots.getResponseBodyContent())

'And Verify that the list contains at least one item'
assert !responseBodyOfChatbots.empty

JSONObject chatBotSettings1 = new JSONObject(responseBodyOfChatbots[0].chatBotSettings)

//println("Data chatBotSettings: ${chatBotSettings1}")
//'Read data from file excel, then preparing for sending request and data'
//data = '{\r\n    \"chatBotName\": \"' + responseBodyOfChatbots[0].chatBotName + '\",\r\n    \"agentName\": \"' + responseBodyOfChatbots[0].agentName + '\",\r\n    \"companyId\": \"' + itemlist[0].companyId + '\",\r\n    \"chatBotColor\": \"' + responseBodyOfChatbots[0].chatBotColor + '\",\r\n    \"buttonColor\": \"' + responseBodyOfChatbots[0].buttonColor + '\",\r\n    \"chatBotSettings\": ' + chatBotSettings1 + '\r\n}'
//
//KeywordUtil.logInfo('Data request in body: ' + data)
//
//'When Call the request to create a chatbot with information is the same as the existing chatbot'
//ResponseObject res1 = CustomKeywords.'resources.api.buildApiPostMethod'('api/v1/chatbot/create', token, data)
//
//'Then verify response code should be 400'
//WS.verifyResponseStatusCode(res1, 400)
//
//'And Verify response body is properly'
//WS.verifyElementPropertyValue(res1, "messageCode", 'InvalidValue')
//WS.verifyElementPropertyValue(res1, "message", 'Duplicate resource Found for the given chatterbot')

def data2 = ""
//Get all values from file excel
for (int i = 1; i <= paramData.getRowNumbers(); i++) {
	if(i == numberRecordInRows) {
		'covert string to Object json chatBotSettings'
		def object = paramData.getValue("chatBotSettings", i)
		def chatBotSettings2 = jsonSlurper.parseText(object)
		println("data chatBotSettings: ${chatBotSettings2}")
		
		'Read data from file excel, then preparing for sending request and data'
		data2 = "{\r\n    \"chatBotName\": \"${paramData.getValue('chatBotName', i) + randomString}\",\r\n    \"agentName\": \"${paramData.getValue("agentName", i) + randomString}\",\r\n    \"companyId\": \"0000000000000000\",\r\n    \"chatBotSettings\": ${chatBotSettings2}\r\n}"
		break
	}
}

'When Call the request to create a chatbot with invalid companyId'
ResponseObject res2 = CustomKeywords.'resources.api.buildApiPostMethod'('api/v1/chatbot/create', token, data2)

'Then verify response code should be 400'
WS.verifyResponseStatusCode(res2, 400)


def data3 = "{'00000000000000000'}"

'When Call the request to create a chatbot with invalid request body'
ResponseObject res3 = CustomKeywords.'resources.api.buildApiPostMethod'('api/v1/chatbot/create', token, data3)

'Then verify response code should be 400'
WS.verifyResponseStatusCode(res3, 400)

