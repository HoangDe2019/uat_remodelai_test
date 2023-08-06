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
import com.kms.katalon.core.configuration.RunConfiguration as RunConfiguration
import com.kms.katalon.core.testobject.FormDataBodyParameter as FormDataBodyParameter
import com.kms.katalon.core.util.internal.Base64

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
KeywordUtil.logInfo("itemlist: " + itemlist)

'When Call the request to get chatbot of a company'
def responseChatbots = CustomKeywords.'resources.api.buildApiGetMethod'("api/v1/chatbot/company/${itemlist[0].companyId}", token, '')

'Then verify response code should be 200'
WS.verifyResponseStatusCode(responseChatbots, 200)

//Parse the response body as JSON
def responseBodyOfChatbots = jsonSlurper.parseText(responseChatbots.getResponseBodyContent())

'And Verify that the list contains at least one item'
assert !responseBodyOfChatbots.empty

'Prepare request body multipartData'
List<FormDataBodyParameter> multipartData = []

def fileURL = RunConfiguration.getProjectDir() + String.format('/Data Files/Chatbot/avatar_8k_test.jpg')
KeywordUtil.logInfo('File image URL: ' + fileURL)

def responseBase64Img = CustomKeywords.'resources.api.uploadFile'(fileURL.toString())
def dataRequest = "{\r\n    \"avatar\": \"${responseBase64Img.toString()}\"\r\n}"
KeywordUtil.logInfo('Data request in body: ' + dataRequest)

'When Call the request to update chatbot avatar with blank token'
def responseChatbotAvatar1 = CustomKeywords.'resources.api.buildApiPostMethod'("api/v1/chatbot/${responseBodyOfChatbots[responseBodyOfChatbots.size()-1].chatBotId}/avatar", '', dataRequest)

'Then verify response code should be 401'
WS.verifyResponseStatusCode(responseChatbotAvatar1, 401)

'When Call the request to update chatbot avatar with invalid token'
def responseChatbotAvatar2 = CustomKeywords.'resources.api.buildApiPostMethod'("api/v1/chatbot/${responseBodyOfChatbots[responseBodyOfChatbots.size()-1].chatBotId}/avatar", 'Bearer', dataRequest)

'Then verify response code should be 401'
WS.verifyResponseStatusCode(responseChatbotAvatar2, 401)