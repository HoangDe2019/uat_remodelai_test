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

'Get key token'
def tokenId = CustomKeywords.'resources.api.loginAndGetToken'('login', GlobalVariable.email,GlobalVariable.password)

'When Call the request to get list of users'
ResponseObject response = CustomKeywords.'resources.api.buildApiGetMethod'('api/v1/user/all', tokenId, '')

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

def companyId = itemlist[itemlist.size()-1].companyId

def paramURI = ["token": CustomKeywords.'resources.api.getAuthToken'()]

def paramRequestURI = CustomKeywords.'resources.api.http_build_query'(paramURI, '', '&', 'UTF-8').replace(' ', '')
KeywordUtil.logInfo('Data request URL: ' + paramRequestURI)

'When Call the request to get a chatbot for a given company by companyID'
ResponseObject responseChatBotCmpany = CustomKeywords.'resources.api.buildApiGetMethod'('api/v2/chatbot/company/' + companyId + '?' + paramRequestURI, tokenId, '')

'Then verify response code should be 200'
WS.verifyResponseStatusCode(responseChatBotCmpany, 200)

// Parse the response body as JSON
def responseBodyOfChatBotCompanyDetail = jsonSlurper.parseText(responseChatBotCmpany.getResponseBodyContent())

'And Verify that the list contains at least one item'
assert !responseBodyOfChatBotCompanyDetail.empty && responseBodyOfChatBotCompanyDetail.toString() != null

KeywordUtil.logInfo("Info chat bot for a company detail: ${responseBodyOfChatBotCompanyDetail}")

//
//'And Verify response body is properly'
//WS.verifyElementPropertyValue(responseCompany, "companyId", companyId[companyId.size()-1])
//assert responseBodyOfCompanyDetail.businessName != null
//assert responseBodyOfCompanyDetail.phoneNumber != null
//assert responseBodyOfCompanyDetail.primaryAddress != null
//assert responseBodyOfCompanyDetail.secondaryAddress != null
//assert responseBodyOfCompanyDetail.websiteUrl != null
//assert responseBodyOfCompanyDetail.users != null