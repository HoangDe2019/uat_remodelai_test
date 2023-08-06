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

'Get key token'
def tokenId = CustomKeywords.'resources.api.loginAndGetToken'('login', GlobalVariable.email,GlobalVariable.password)

'Define body data'
def dataInvalid = ''

'When Call the request to create a new company with invalid request body'
ResponseObject res1 = CustomKeywords.'resources.api.buildApiPostMethod'('api/v1/company/signup', tokenId, dataInvalid)

'Then verify response code should be 400'
WS.verifyResponseStatusCode(res1, 400)

//-------------------------------------------------//
//Scenario Create a company with body data that is the same as the existing company information
'When Call the request to get list of companies'
ResponseObject response = CustomKeywords.'resources.api.buildApiGetMethod'('api/v1/company/all', tokenId, '')

'Then verify response code should be 200'
WS.verifyResponseStatusCode(response, 200)

// Parse the response body as JSON
def jsonSlurper = new JsonSlurper()
def responseBody = jsonSlurper.parseText(response.getResponseBodyContent())

'Verify that the list contains at least one item'
assert !responseBody.empty : 'No content in body!'

'When Call the request to get company by companyID'
def companyId = responseBody.keySet()
ResponseObject responseCompany = CustomKeywords.'resources.api.buildApiGetMethod'('api/v1/company/' + companyId[0], tokenId, '')

'Then verify response code should be 200'
WS.verifyResponseStatusCode(responseCompany, 200)

// Parse the response body as JSON
def responseBodyOfCompanyDetail = jsonSlurper.parseText(responseCompany.getResponseBodyContent())

'And Verify that the list contains at least one item'
assert !responseBodyOfCompanyDetail.empty

KeywordUtil.logInfo('Actual response body: ' + responseBodyOfCompanyDetail.toString())

'Preparing request body data'
def dataExisting = '{\n    "businessName" : "' + responseBodyOfCompanyDetail.businessName + '",\n    "websiteUrl": "' + responseBodyOfCompanyDetail.websiteUrl +'",\n    "phoneNumber": "' + responseBodyOfCompanyDetail.phoneNumber +'",\n    "primaryAddress": [{\n        "city" : "' + responseBodyOfCompanyDetail.primaryAddress[0].city +'",\n        "state": "' + responseBodyOfCompanyDetail.primaryAddress[0].state +'",\n        "streetAddressLine1": "' + responseBodyOfCompanyDetail.primaryAddress[0].streetAddressLine1 +'",\n        "streetAddressLine2" : "' +responseBodyOfCompanyDetail.primaryAddress[0].streetAddressLine2 +'",\n        "zipCode": "' + responseBodyOfCompanyDetail.primaryAddress[0].zipCode +'"\n    }],\n        "secondaryAddress": [{\n        "city" : "' +  responseBodyOfCompanyDetail.secondaryAddress[0].city +'",\n        "state": "' + responseBodyOfCompanyDetail.secondaryAddress[0].state +'",\n        "streetAddressLine1": "' + responseBodyOfCompanyDetail.secondaryAddress[0].streetAddressLine1 +'",\n        "streetAddressLine2" : "' + responseBodyOfCompanyDetail.secondaryAddress[0].streetAddressLine2 +'",\n        "zipCode": "' + responseBodyOfCompanyDetail.secondaryAddress[0].zipCode +'"\n}]\n}'

KeywordUtil.logInfo('Data request in body: ' + dataExisting)

'When Call the request to create a new company already exists'
ResponseObject res2 = CustomKeywords.'resources.api.buildApiPostMethod'('api/v1/company/signup', tokenId, dataExisting)

'Then verify response code should be 400'
WS.verifyResponseStatusCode(res2, 400)

'And Verify response body is not null'
//def jsonSlurper = new JsonSlurper()
def result = jsonSlurper.parseText(res2.responseText)

assert !result.empty

KeywordUtil.logInfo('Actual response body: ' + result.toString())

'Verify the response message is properly'
WS.verifyElementPropertyValue(res2, "message", 'Duplicate resource FoundCompany already exists')