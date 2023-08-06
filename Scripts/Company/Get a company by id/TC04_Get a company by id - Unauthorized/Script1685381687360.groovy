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

'When Call the request to get list of companies'
ResponseObject response = CustomKeywords.'resources.api.buildApiGetMethod'('api/v1/company/all', tokenId, '')

'Then verify response code should be 200'
WS.verifyResponseStatusCode(response, 200)

// Parse the response body as JSON
def jsonSlurper = new JsonSlurper()
def responseBodyOfComapanies = jsonSlurper.parseText(response.getResponseBodyContent())

'And Verify that the list contains at least one item'
assert !responseBodyOfComapanies.empty : 'No content in body!'

'Print the information for each item in the list'
responseBodyOfComapanies.each { info ->
	KeywordUtil.logInfo("Info: ${info}")
}

'When Call the request to get company with unauthorized'
def companyId = responseBodyOfComapanies.keySet()
ResponseObject responseCompany1 = CustomKeywords.'resources.api.buildApiGetMethod'('api/v1/company/' + companyId[0], '', '')

'Then verify response code should be 401'
WS.verifyResponseStatusCode(responseCompany1, 401)

'When Call the request to get company with unauthorized'
ResponseObject responseCompany2 = CustomKeywords.'resources.api.buildApiGetMethod'('api/v1/company' + companyId[0], 'Bearer', '')

'Then verify response code should be 401'
WS.verifyResponseStatusCode(responseCompany2, 401)