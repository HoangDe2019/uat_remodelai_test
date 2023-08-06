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

def jsonSlurper = new JsonSlurper()

'Get key token'
def token = CustomKeywords.'resources.api.loginAndGetToken'('login', GlobalVariable.email,GlobalVariable.password)

'When Call the request to get list of users'
ResponseObject responseUsers = CustomKeywords.'resources.api.buildApiGetMethod'('api/v1/user/all', token, '')

'Then verify response code should be 200'
WS.verifyResponseStatusCode(responseUsers, 200)

// Parse the response body as JSON
def responseBodyOfUsers = jsonSlurper.parseText(responseUsers.getResponseBodyContent())

'And Verify that the list contains at least one item'
assert !responseBodyOfUsers.empty : 'No content in body!'

'Print the information for each item in the list'
def itemlist = []

responseBodyOfUsers.each { info ->
	KeywordUtil.logInfo("Info users: ${info}")
	if(info.email.equals(GlobalVariable.email)) {
		itemlist.add(info)
	}
}

println(itemlist)

'When Call the request to get list of templates'
ResponseObject responseTemplates = CustomKeywords.'resources.api.buildApiGetMethod'("api/v1/template/list?email="+itemlist[itemlist.size()-1].email, token, '')

'Then verify response code should be 200'
WS.verifyResponseStatusCode(responseTemplates, 200)

// Parse the response body as JSON
def responseBodyTemplates = jsonSlurper.parseText(responseTemplates.getResponseBodyContent())

'And Verify that the list contains at least one item'
assert !responseBodyTemplates.empty : 'No content in body!'

'Print the information for each item in the list'
def elmTempls = []
responseBodyTemplates.each { info ->
	KeywordUtil.logInfo("Info: ${info}")
	elmTempls.add(info)
}

println(elmTempls)

def templateId = elmTempls[elmTempls.size()-1].templateId + 'invalid'

'When Call the request to get template by invalid templateId'
ResponseObject responseTemplate = CustomKeywords.'resources.api.buildApiGetMethod'("api/v1/template/${templateId}", token, '')

'Then verify response code should be 400'
WS.verifyResponseStatusCode(responseTemplate, 400)