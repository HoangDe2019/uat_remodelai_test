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
def token = CustomKeywords.'resources.api.loginAndGetToken'('login', GlobalVariable.email,GlobalVariable.password)

'When Call the request to get list of forms'
ResponseObject response = CustomKeywords.'resources.api.buildApiGetMethod'('api/v1/leadForm/all', token, '')

'Then verify response code should be 200'
WS.verifyResponseStatusCode(response, 200)

// Parse the response body as JSON
def jsonSlurper = new JsonSlurper()
def responseBodyOfForm = jsonSlurper.parseText(response.getResponseBodyContent())

'And Verify that the list contains at least one item'
assert !responseBodyOfForm.empty : 'No content in body!'

'Print the information for each item in the list'
responseBodyOfForm.each { info ->
	KeywordUtil.logInfo("Info LeadForms: ${info}")
}

'When Call the request to get a lead form by leadId with URL not mapped'
def formId = responseBodyOfForm[0].leadFormId
def paramURI = ["token": CustomKeywords.'resources.api.getAuthToken'()]
def paramRequestURI = CustomKeywords.'resources.api.http_build_query'(paramURI, '', '&', 'UTF-8').replace(' ', '')

ResponseObject responseFormDetail = CustomKeywords.'resources.api.buildApiGetMethod'('api/vhai/leadForm/' + formId + '?' + paramRequestURI, token, '')

'Then verify response code should be 404'
WS.verifyResponseStatusCode(responseFormDetail, 404)

'And Verify response body is properly'
WS.verifyElementPropertyValue(responseFormDetail, "error", 'Not Found')