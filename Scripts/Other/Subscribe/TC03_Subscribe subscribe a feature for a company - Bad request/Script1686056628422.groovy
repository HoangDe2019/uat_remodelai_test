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

String charset = (('a'..'z')+ (' ') + ('A'..'Z') + (' ') + ('0'..'9')).join()
String randomString = RandomStringUtils.random(10, charset.toCharArray())
String charsetNumber = ('0'..'9').join()
String randomStringNumber = RandomStringUtils.random(20, charsetNumber.toCharArray())

'Get key token'
def token = CustomKeywords.'resources.api.loginAndGetToken'('login', GlobalVariable.email,GlobalVariable.password)

def data1 = "{\n    \"topic\": \"sms_${randomString}\",\n    \"companyId\": \"${randomStringNumber}\",\n    \"choice\": \"SUBSCRIBE\"\n}"


KeywordUtil.logInfo('Data request in body: ' + data1)

'When Call the request to create a new subscription with invalid companyId'
ResponseObject res1 = CustomKeywords.'resources.api.buildApiPostMethod'('/api/v1/subscribe', token, data1)

'Then verify response code should be 400'
WS.verifyResponseStatusCode(res1, 400)

def data2 ="{'" + randomString + "'}"
'When Call the request to create a new subscription with invalid request body data'
ResponseObject res2 = CustomKeywords.'resources.api.buildApiPostMethod'('/api/v1/subscribe', token, data2)

'Then verify response code should be 400'
WS.verifyResponseStatusCode(res2, 400)
