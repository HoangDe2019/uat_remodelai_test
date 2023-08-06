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

'Load list dump data'
def paramData = findTestData("Chat/Unsub_new_data_chat_feture")

def data = ""
//Set config which you get data in any rows other
def numberRecordInRows = 3

'Get key token'
def token = CustomKeywords.'resources.api.loginAndGetToken'('/login', GlobalVariable.email,GlobalVariable.password)

'When Call the request to get list of users & Then verify response code should be 200'
ResponseObject response = CustomKeywords.'resources.api.buildApiGetMethod'('api/v1/user/all', token, '')

'Then verify response code should be 200'
WS.verifyResponseStatusCode(response, 200)

// Parse the response body as JSON
def jsonSlurper = new JsonSlurper()
def responseBodyOfUsers = jsonSlurper.parseText(response.getResponseBodyContent())

'Verify that the list contains at least one item'
assert !responseBodyOfUsers.empty : 'No content in body!'

'Print the information for each item in the list'
responseBodyOfUsers.each { info ->
	KeywordUtil.logInfo("Info: ${info}")
}

'Get list item of response body'
def itemlist = []

responseBodyOfUsers.each { info ->
	itemlist.add(info)
}

KeywordUtil.logInfo("itemlist: " + itemlist)

//Get all values from file excel
for (int i = 1; i <= paramData.getRowNumbers(); i++) {
	if(i == numberRecordInRows) {
		'Read data from file excel, then preparing for sending request and data'
		data = "{\r\n    \"topic\": \"${paramData.getValue('topic', i)}\",\r\n    \"choice\": \"${paramData.getValue('choice', i)}\",\r\n    \"companyId\": \"${itemlist[itemlist.size()-1].companyId}\"\r\n}"
		break
	}
}

KeywordUtil.logInfo('Data request in body: ' + data)

'When Call the request to unsubscribe a feature with blank token'
ResponseObject res1 = CustomKeywords.'resources.api.buildApiPostMethod'('api/v1/unsubscribe', '', data)

'Then verify response code should be 401'
WS.verifyResponseStatusCode(res1, 401)

'When Call the request to unsubscribe a feature with invalid token'
ResponseObject res2 = CustomKeywords.'resources.api.buildApiPostMethod'('api/v1/unsubscribe', 'Bearer', data)

'Then verify response code should be 401'
WS.verifyResponseStatusCode(res2, 401)
