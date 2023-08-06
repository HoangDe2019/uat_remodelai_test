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
import org.codehaus.groovy.ast.stmt.ContinueStatement
import org.apache.poi.hssf.record.PageBreakRecord.Break
import java.text.SimpleDateFormat
import java.util.Date
import org.apache.commons.lang.RandomStringUtils

//Define the custom date/time format
String customDateFormat = "MM-dd-yyyy hh:mma"

//Create a new SimpleDateFormat object with custom format and locale
SimpleDateFormat sdf = new SimpleDateFormat(customDateFormat, Locale.ENGLISH)

//Create a new Date object for the current date and time
Date currentDate = new Date()

//Format the date and time using SimpleDateFormat
String formattedDate = sdf.format(currentDate)


String charset = (('A'..'Z') + ('0'..'9')).join()
String randomString = RandomStringUtils.random(10, charset.toCharArray())

'Load list dump data'
def paramData = findTestData("Reminders/Create new a appointment")

def data = ""
//Set config which you get data in any rows other
def numberRecordInRows = 1

//Get all values from file excel
for (int i = 1; i <= paramData.getRowNumbers(); i++) {
	if(i == numberRecordInRows) {
		KeywordUtil.logInfo('row: ' + i)
		'Read data from file excel, then preparing for sending request and data'
		data = [
			"name" : 'nguyenlan_' + randomString, 
			"phoneNumber": paramData.getValue("phoneNumber", i),
			"delta": paramData.getValue("delta", i), // delta is integer only have value from 15 until max 60 mins
			"timeZone": paramData.getValue("timeZone", i),
			"date": formattedDate
		]
		break
	}
}

data = CustomKeywords.'resources.api.http_build_query'(data, '', '&', 'UTF-8').replace(' ', '')
KeywordUtil.logInfo('Data request in body: ' + data)

'When Call the request to create a new appointment'
ResponseObject res = CustomKeywords.'resources.api.buildApiPostMethodByFormData'("create", '', data)

'Then verify response code should be 302'
WS.verifyResponseStatusCode(res, 302)