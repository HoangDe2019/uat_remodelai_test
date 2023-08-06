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

'Get key token'
def tokenId = CustomKeywords.'resources.api.loginAndGetToken'('login', GlobalVariable.email,GlobalVariable.password)

'Load list dump data'
def paramData = findTestData("Company/Setup_create_company_data")

def data = ""
//Set config which you get data in any rows other
def numberRecordInRows = 1

def primaryAddress = null
def secondaryAddress = null

//Get all values from file excel
for (int i = 1; i <= paramData.getRowNumbers(); i++) {
	if(i == numberRecordInRows) {
		KeywordUtil.logInfo('row: ' + i)
		primaryAddress = '[\n        {\n        \"city\": \"' + paramData.getValue(4, i) + randomString +'\",\n        \"state\": \"' + paramData.getValue(5, i) + randomString +'\",\n        \"streetAddressLine1\": \"' + paramData.getValue(6, i) + randomString +'\",\n        \"streetAddressLine2\" : \"' + paramData.getValue(7, i) + randomString +'\",\n        \"zipCode\": \"' + randomStringNumber +'\"\n        }\n    ]'
		secondaryAddress = '[\n        {\n        \"city\": \"' + paramData.getValue(9, i) + randomString +'\",\n        \"state\": \"' + paramData.getValue(10, i) + randomString +'\",\n        \"streetAddressLine1\": \"' + paramData.getValue(11, i) + randomString +'\",\n        \"streetAddressLine2\" : \"' + paramData.getValue(12, i) + randomString +'\",\n        \"zipCode\": \"' + paramData.getValue(13, i) + randomStringNumber +'\"\n        }\n    ]'
		'Read data from file excel, then preparing for sending request and data'
		data = '{\n    "businessName" : "' + paramData.getValue(1, i) + randomString + '",\n    "websiteUrl": "' + paramData.getValue(2, i) + randomString +'",\n    "phoneNumber": "' + randomStringNumber +'",\n    "primaryAddress": ' + primaryAddress + ',\n        "secondaryAddress": ' + secondaryAddress + '\n}'
		break
	}
}

KeywordUtil.logInfo('Data request in body: ' + data)

'When Call the request to create a new company'
ResponseObject res = CustomKeywords.'resources.api.buildApiPostMethod'('api/v1/company/signup', tokenId, data)

'Then verify response code should be 201 created'
WS.verifyResponseStatusCode(res, 201)

'And Verify response body is not null'
def jsonSlurper = new JsonSlurper()

def result = jsonSlurper.parseText(res.getResponseBodyContent())
KeywordUtil.logInfo('Actual response body: ' + result.toString())

assert !result.empty

'And Verify response body is properly'
WS.verifyElementPropertyValue(res, "businessName", paramData.getValue(1, numberRecordInRows) + randomString)
WS.verifyElementPropertyValue(res, "websiteUrl", paramData.getValue(2, numberRecordInRows) + randomString)
WS.verifyElementPropertyValue(res, "phoneNumber", randomStringNumber)
WS.verifyElementPropertyValue(res, "users", null)
WS.verifyElementPropertyValue(res, "leadForms", null)
WS.verifyElementPropertyValue(res, "chatterbots", null)
WS.verifyElementPropertyValue(res, "apiKeys", null)

//covert from array to Json, and then to string
String jsonPrimaryAddress = JsonOutput.toJson(result.primaryAddress)
KeywordUtil.logInfo('jsonString get body content: ' + jsonPrimaryAddress)

// create a new response object
ResponseObject roPrimaryAddress = new ResponseObject()

// set the response body
roPrimaryAddress.responseText = jsonPrimaryAddress
KeywordUtil.logInfo('templateSettings get body content: ' + roPrimaryAddress.getResponseBodyContent())
def resultPrimaryAddress = jsonSlurper.parseText(roPrimaryAddress.getResponseBodyContent())

'result PrimaryAddress which was gotten from body content: '
KeywordUtil.logInfo('result PrimaryAddress Settings content: ' + resultPrimaryAddress)

//covert from array to Json, and then to string
String jsonSecondaryAddress = JsonOutput.toJson(result.secondaryAddress)
KeywordUtil.logInfo('jsonString get body content: ' + jsonSecondaryAddress)

// create a new response object
ResponseObject roSecondaryAddress = new ResponseObject()

// set the response body
roSecondaryAddress.responseText = jsonSecondaryAddress
KeywordUtil.logInfo('templateSettings get body content: ' + roSecondaryAddress.getResponseBodyContent())
def resultSecondaryAddress = jsonSlurper.parseText(roSecondaryAddress.getResponseBodyContent())

'result resultSecondaryAddress which was gotten from body content: '
KeywordUtil.logInfo('result resultSecondaryAddress Settings content: ' + resultSecondaryAddress)

// get data request from data body
'covert string which was file data to Object json templateSettings'
def primaryAddressData = jsonSlurper.parseText(primaryAddress.toString().replaceAll('""', ''))
KeywordUtil.logInfo('primaryAddress get body content: ' + primaryAddressData[primaryAddressData.size()-1])

'And Verify response primaryAddressData is properly'
WS.verifyElementPropertyValue(roPrimaryAddress, "city", primaryAddressData.city)
WS.verifyElementPropertyValue(roPrimaryAddress, "state", primaryAddressData.state)
WS.verifyElementPropertyValue(roPrimaryAddress, "streetAddressLine1", primaryAddressData.streetAddressLine1)
WS.verifyElementPropertyValue(roPrimaryAddress, "streetAddressLine2", primaryAddressData.streetAddressLine2)
WS.verifyElementPropertyValue(roPrimaryAddress, "zipCode", primaryAddressData.zipCode)

'covert string which was file data to Object json templateSettings'
def secondaryAddressData = jsonSlurper.parseText(secondaryAddress.toString().replaceAll('""', ''))
KeywordUtil.logInfo('primaryAddress get body content: ' + secondaryAddressData[secondaryAddressData.size()-1])

'And Verify response secondaryAddressData is properly'
WS.verifyElementPropertyValue(roSecondaryAddress, "city", secondaryAddressData.city)
WS.verifyElementPropertyValue(roSecondaryAddress, "state", secondaryAddressData.state)
WS.verifyElementPropertyValue(roSecondaryAddress, "streetAddressLine1", secondaryAddressData.streetAddressLine1)
WS.verifyElementPropertyValue(roSecondaryAddress, "streetAddressLine2", secondaryAddressData.streetAddressLine2)
WS.verifyElementPropertyValue(roSecondaryAddress, "zipCode", secondaryAddressData.zipCode)