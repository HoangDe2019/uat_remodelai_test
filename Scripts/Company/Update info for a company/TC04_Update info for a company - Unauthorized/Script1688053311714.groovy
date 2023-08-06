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

'When Call the request to get list of companies'
ResponseObject responseCompanies = CustomKeywords.'resources.api.buildApiGetMethod'('api/v1/company/all', tokenId, '')

'Then verify response code should be 200'
WS.verifyResponseStatusCode(responseCompanies, 200)

// Parse the response body as JSON
def jsonSlurper = new JsonSlurper()
def responseBodyOfComapanies = jsonSlurper.parseText(responseCompanies.getResponseBodyContent())

'And Verify that the list contains at least one item'
assert !responseBodyOfComapanies.empty : 'No content in body!'

'Print the information for each item in the list'
responseBodyOfComapanies.each { info ->
	KeywordUtil.logInfo("Info: ${info}")
}

'When Call the request to get company by companyID'
def companyId = responseBodyOfComapanies.keySet()


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
		data = '{\n    "companyId" : "' + companyId[companyId.size()-1] + '",\n    "businessName" : "' + paramData.getValue(1, i) + randomString + '",\n    "websiteUrl": "' + paramData.getValue(2, i) + randomString +'",\n    "phoneNumber": "' + randomStringNumber +'",\n    "primaryAddress": ' + primaryAddress + ',\n        "secondaryAddress": ' + secondaryAddress + '\n}'
		break
	}
}

KeywordUtil.logInfo('Data request in body: ' + data)

'When Call the request to update infor for a company with unauthorized'
ResponseObject res1 = CustomKeywords.'resources.api.buildApiPutMethod'('api/v1/company/update', '', data)

'Then verify response code should be 401'
WS.verifyResponseStatusCode(res1, 401)

'When Call the request to update infor for a company with unauthorized'
ResponseObject res2 = CustomKeywords.'resources.api.buildApiPutMethod'('api/v1/company/update', randomString, data)

'Then verify response code should be 401'
WS.verifyResponseStatusCode(res2, 401)