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

//Get all values from file excel
for (int i = 1; i <= paramData.getRowNumbers(); i++) {
	if(i == numberRecordInRows) {
		KeywordUtil.logInfo('row: ' + i)
		'Read data from file excel, then preparing for sending request and data'
		data = '{\n    "businessName" : "' + paramData.getValue(1, i) + randomString + '",\n    "websiteUrl": "' + paramData.getValue(2, i) + randomString +'",\n    "phoneNumber": "' + randomStringNumber +'",\n    "primaryAddress": [{\n        "city" : "' + paramData.getValue(4, i) + randomString +'",\n        "state": "' + paramData.getValue(5, i) + randomString +'",\n        "streetAddressLine1": "' + paramData.getValue(6, i) + randomString +'",\n        "streetAddressLine2" : "' + paramData.getValue(7, i) + randomString +'",\n        "zipCode": "' + paramData.getValue(8, i) + randomString +'"\n    }],\n        "secondaryAddress": [{\n        "city" : "' + paramData.getValue(9, i) + randomString +'",\n        "state": "' + paramData.getValue(10, i) + randomString +'",\n        "streetAddressLine1": "' + paramData.getValue(11, i) + randomString +'",\n        "streetAddressLine2" : "' + paramData.getValue(12, i) + randomString +'",\n        "zipCode": "' + paramData.getValue(13, i) + randomString +'"\n    }]\n}'
		break
	}
}

KeywordUtil.logInfo('Data request in body: ' + data)

'When Call the request to create a new company with URL not mapped'
ResponseObject res = CustomKeywords.'resources.api.buildApiPostMethod'('api/v1/company' + randomString + '/signup', tokenId, data)

'Then verify response code should be 404'
WS.verifyResponseStatusCode(res, 404)

'And Verify response body is properly'
WS.verifyElementPropertyValue(res, "error", 'Not Found')