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
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import static org.assertj.core.api.Assertions.*
import org.codehaus.groovy.ast.stmt.ContinueStatement as ContinueStatement
import org.apache.poi.hssf.record.PageBreakRecord.Break as Break
import org.apache.commons.lang.RandomStringUtils as RandomStringUtils
import groovy.json.JsonSlurper as JsonSlurper

def body = '{\n    \"email\": \"'+ GlobalVariable.email +'\",\n    \"password\": \"'+ GlobalVariable.password +'\"\n}'
'When Call the request to login system'
response = CustomKeywords.'resources.api.buildApiPostMethod'('login', '', body)

'Then verify response code should be 200'
WS.verifyResponseStatusCode(response, 200)

// Parse the response body as JSON
def jsonSlurper = new JsonSlurper()
def responseBodyOfUsers = jsonSlurper.parseText(response.getResponseBodyContent())

'And Verify that the list contains at least one item'
assert !responseBodyOfUsers.empty : 'No content in body!'

def dataBody = "{\r\n    \"refreshToken\": \"${responseBodyOfUsers.data.refreshToken}\"\r\n}"

'When Call the request to refresh token with invalid URL'
def responseRefreshToken = CustomKeywords.'resources.api.buildApiPostMethod'('vmot/refresh_token', '', dataBody)

'Then verify response code should be 401'
WS.verifyResponseStatusCode(responseRefreshToken, 401)