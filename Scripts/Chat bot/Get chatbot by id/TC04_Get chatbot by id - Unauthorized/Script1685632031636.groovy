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
def responseChatbots1 = CustomKeywords.'resources.api.buildApiGetMethod'("api/v1/chatbot/company/${itemlist[0].companyId}", token, '')

'Then verify response code should be 200'
WS.verifyResponseStatusCode(responseChatbots1, 200)

//Parse the response body as JSON
def responseBodyOfChatbots1 = jsonSlurper.parseText(responseChatbots1.getResponseBodyContent())

'And Verify that the list contains at least one item'
assert !responseBodyOfChatbots1.empty

'Print the information for each item in the list'
responseBodyOfChatbots1.each { info ->
	KeywordUtil.logInfo("Info: ${info}")
}

def chatbotObj = responseBodyOfChatbots1[0].chatBotId

'When Call the request to get chatbot by chatbotId with blank token'
def responseChatbot2 = CustomKeywords.'resources.api.buildApiGetMethod'("api/v1/chatbot/${chatbotObj}", '', '')

'Then verify response code should be 401'
WS.verifyResponseStatusCode(responseChatbot2, 401)

'When Call the request to get chatbot by chatbotId with invalid token'
def responseChatbot3 = CustomKeywords.'resources.api.buildApiGetMethod'("api/v1/chatbot/${chatbotObj}", 'Bearer', '')

'Then verify response code should be 401'
WS.verifyResponseStatusCode(responseChatbot3, 401)
