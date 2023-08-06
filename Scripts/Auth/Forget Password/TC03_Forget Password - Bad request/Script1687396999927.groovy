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
import org.bouncycastle.crypto.generators.BCrypt
import org.apache.commons.lang.RandomStringUtils

String charset = (('a'..'z') + ('A'..'Z') + ('0'..'9')).join()
String randomString = RandomStringUtils.random(10, charset.toCharArray())
String emailParam = "${randomString}@gmail.com"

// Parse the response body as JSON
def jsonSlurper = new JsonSlurper()

def dataBody = "{\r\n    \"email\": \"${emailParam}\"\r\n}"

KeywordUtil.logInfo('Data request body: ' + dataBody)

'When Call the request to send pin code to email'
def res = CustomKeywords.'resources.api.buildApiPostMethod'('forgot_password', '', dataBody)

'Then verify response code should be 400'
WS.verifyResponseStatusCode(res, 400)

// Parse the response body as JSON
def responseBody = jsonSlurper.parseText(res.getResponseBodyContent())

'And Verify that the list contains at least one item'
assert !responseBody.empty : 'No content in body!'

responseBody.each { 
	key, value -> 
		KeywordUtil.logInfo('Actual response body: ' + key + ': ' + value)
		WS.verifyElementPropertyValue(res, key, value)
}

'And Verify response body is properly'
WS.verifyElementPropertyValue(res, "message", 'This email address does not exist!')