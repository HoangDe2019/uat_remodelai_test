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

'Get key token'
def token = CustomKeywords.'resources.api.loginAndGetToken'('login', GlobalVariable.email2,GlobalVariable.password)

'When Call the request to get list of forms'
ResponseObject response = CustomKeywords.'resources.api.buildApiGetMethod'('api/v1/leadForm/all', token, '')

'Then verify response code should be 200'
WS.verifyResponseStatusCode(response, 200)

// Parse the response body as JSON
def jsonSlurper = new JsonSlurper()
def responseBody = jsonSlurper.parseText(response.getResponseBodyContent())

'And Verify that the list contains at least one item'
assert !responseBody.empty : 'No content in body!'

'Print the information for each item in the list'
responseBody.each { info ->
	KeywordUtil.logInfo("Info forms: ${info}")
}

'Get list item of response body'
def itemlist = []
responseBody.each { info ->
	if(info.leadFormName.toString().contains('nglan')) {
		itemlist.add(info)
	}
}

KeywordUtil.logInfo("itemlist: " + itemlist)

'Load list dump data'
def paramData = findTestData("LeadForm/Create_new_date_form")

def data = ""
//Set config which you get data in any rows other
def numberRecordInRows = 1 

//Get all values from file excel
for (int i = 1; i <= paramData.getRowNumbers(); i++) {
	if(i == numberRecordInRows) {
		KeywordUtil.logInfo('row: ' + i)
		'Read data from file excel, then preparing for sending request and data'
		data = '{\n  \"companyId\": \"'+ itemlist[itemlist.size()-1].companyId +'\",\n  \"leadFormId\": \"' + itemlist[itemlist.size()-1].leadFormId + '\",\n  \"leadFormName\": \"' + itemlist[itemlist.size()-1].leadFormName + '\",\n  \"templateId\": \"' + itemlist[itemlist.size()-1].templateId + '\"\n}'
		break
	}
}

KeywordUtil.logInfo('Data request in body: ' + data)

'When Call the request to update lead form setting'
ResponseObject res = CustomKeywords.'resources.api.buildApiPutMethod'('api/v1/leadForm/leadFormSetting/update/' + itemlist[itemlist.size()-1].leadFormId, token, data)

'Then Verify response code should be 200'
WS.verifyResponseStatusCode(res, 200)

'And Verify response body is not null'
def result = jsonSlurper.parseText(res.responseText)

assert !result.empty

'Print the information for each item in the list'
result.each { info ->
	KeywordUtil.logInfo("Info forms: ${info}")
}

'And Verify response body is properly'
WS.verifyElementPropertyValue(res, "companyId", itemlist[itemlist.size()-1].companyId)
WS.verifyElementPropertyValue(res, "leadFormId", itemlist[itemlist.size()-1].leadFormId)
WS.verifyElementPropertyValue(res, "templateId", itemlist[itemlist.size()-1].templateId)
WS.verifyElementPropertyValue(res, "leadFormName", itemlist[itemlist.size()-1].leadFormName)
WS.verifyElementPropertyValue(res, "version", itemlist[itemlist.size()-1].version + 1)
