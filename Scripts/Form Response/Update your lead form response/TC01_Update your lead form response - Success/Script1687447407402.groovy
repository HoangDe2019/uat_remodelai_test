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
import java.text.SimpleDateFormat

String charset = (('A'..'Z') + ('0'..'9')).join()
String randomString = RandomStringUtils.random(10, charset.toCharArray())
String charsetNumber = ('0'..'9').join()
String randomStringNumber = RandomStringUtils.random(10, charsetNumber.toCharArray())

'Get key token'
def token = CustomKeywords.'resources.api.loginAndGetToken'('login', GlobalVariable.email,GlobalVariable.password)

'When Call the request to get list of users'
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
	if(info.email.equals(GlobalVariable.email)) {
		itemlist.add(info)
	}
}
KeywordUtil.logInfo("itemlist" + itemlist)

'When Call the request to get list of lead form response of a company'
def companyId = itemlist[0].companyId
ResponseObject responseFormResponse = CustomKeywords.'resources.api.buildApiGetMethod'('api/v1/leadFormResponse/company/' + companyId, token, '')

'Then verify response code should be 200'
WS.verifyResponseStatusCode(responseFormResponse, 200)

def responseBodyOfFormResponse = jsonSlurper.parseText(responseFormResponse.getResponseBodyContent())

'Load list dump data'
def paramData = findTestData("LeadForm Response/Create_new_lead_form_response")

//init data and call api update leadformResposne
def data = ""
//Set config which you get data in any rows other
def numberRecordInRows = 1 

// Get the current date and time
Date date = new Date()
SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
outputFormat.setTimeZone(TimeZone.getTimeZone("UTC"))
def getCurrentTime = outputFormat.format(date).toString()

//Get all values from file excel
for (int i = 1; i <= paramData.getRowNumbers(); i++) {
	if(i == numberRecordInRows) {
		'Covert string to Object json'
		def objectFormAnswerList = paramData.getValue("formAnswerList", i)
		def formAnswerList  = jsonSlurper.parseText(objectFormAnswerList)
		println("data formAnswerList: ${formAnswerList}")
		
		KeywordUtil.logInfo('row: ' + i)
		'Read data from file excel, then preparing for sending request and data'
		data = "{\n  \"chatBotId\": \"${responseBodyOfFormResponse[0].chatBotId}\",\n  \"leadFormResponseId\": \"${responseBodyOfFormResponse[0].leadFormResponseId}\",\n  \"companyId\": \"${itemlist[0].companyId}\",\n  \"formAnswerList\": [\n      ${formAnswerList}\n  ],\n  \"formModifiedDateAndTime\": \"${getCurrentTime}\",\n  \"formSubmissionDateAndTime\": \"${getCurrentTime}\",\n  \"leadFormId\": \"${responseBodyOfFormResponse[0].leadFormId}\"\n}"	
		break
	}
}

KeywordUtil.logInfo('Data request in body: ' + data)

'When Call the request to update lead form response'
ResponseObject res = CustomKeywords.'resources.api.buildApiPutMethod'('api/v1/leadFormResponse/update', token, data)

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
WS.verifyElementPropertyValue(res, "companyId", itemlist[0].companyId)
WS.verifyElementPropertyValue(res, "leadFormId", responseBodyOfFormResponse[0].leadFormId)
WS.verifyElementPropertyValue(res, "chatBotId",responseBodyOfFormResponse[0].chatBotId)
WS.verifyElementPropertyValue(res, "leadFormResponseId", responseBodyOfFormResponse[0].leadFormResponseId)

