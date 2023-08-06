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

'When Call the request to delete appointment with blank data request body'
ResponseObject res1 = CustomKeywords.'resources.api.buildApiPostMethodByFormData'('delete', '', '')

'Then verify response code should be 400'
WS.verifyResponseStatusCode(res1, 400)

'Init params request'
def data = ["id": null]

def quersyData = CustomKeywords.'resources.api.http_build_query'(data, '', '&', 'UTF-8').replaceAll(" ", "")
KeywordUtil.logInfo('Data request in body with query string: ' + quersyData)

'When Call the request to delete appointment with null id'
ResponseObject res2 = CustomKeywords.'resources.api.buildApiPostMethodByFormData'('delete', '', quersyData)

'Then verify response code should be 400'
WS.verifyResponseStatusCode(res2, 400)