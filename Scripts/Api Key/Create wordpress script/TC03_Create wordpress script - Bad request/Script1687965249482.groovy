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
import org.apache.commons.lang.RandomStringUtils

String charset = (('A'..'Z') + ('0'..'9')).join()
String randomString = RandomStringUtils.random(10, charset.toCharArray())
String charsetNumber = ('0'..'9').join()
String randomStringNumber = RandomStringUtils.random(10, charsetNumber.toCharArray())

'Load list dump data'
def paramData = findTestData("Plugins/create_new_data_plugins")

def data = ""
//Set config which you get data in any rows other
def numberRecordInRows = 1

'Get key token'
def token = CustomKeywords.'resources.api.loginAndGetToken'('login', GlobalVariable.email,GlobalVariable.password)

//Get all values from file excel
for (int i = 1; i <= paramData.getRowNumbers(); i++) {
	if(i == numberRecordInRows) {
		'Read data from file excel, then preparing for sending request and data'
		data = "{\n    \"name\" : \"${paramData.getValue('name', i) + randomString}\",\n    \"title\": \"${paramData.getValue('title', i) + randomString}\",\n    \"avatarUrl\": \"${paramData.getValue('avatarUrl', i)}\",\n    \"color\": \"${paramData.getValue('color', i)}\",\n    \"companyId\": \"${randomStringNumber}\",\n    \"buttonName\" : \"${paramData.getValue('buttonName', i) + randomString}\",\n    \"buttonColor\": \"${paramData.getValue('buttonColor', i)}\"\n}"
		break
	}
}

KeywordUtil.logInfo('Data request in body: ' + data)

'When Call the request to create a new wordpress script with invalid companyId'
ResponseObject res = CustomKeywords.'resources.api.buildApiPostMethod'('api/v1/wordpress', token, '')

'Then verify response code should be 400'
WS.verifyResponseStatusCode(res, 400)

def dataInvalid = "{'${randomString}'}"
'When Call the request to create a new wordpress script with invalid request body data'
ResponseObject res2 = CustomKeywords.'resources.api.buildApiPostMethod'('api/v1/wordpress', token, dataInvalid)

'Then verify response code should be 400'
WS.verifyResponseStatusCode(res2, 400)