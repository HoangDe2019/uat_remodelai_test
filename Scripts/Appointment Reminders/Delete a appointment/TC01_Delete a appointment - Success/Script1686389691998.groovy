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
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.net.URLDecoder

def jsonSlurper = new JsonSlurper()

'When Call the request to get list of appointments'
ResponseObject responseAppointment = CustomKeywords.'resources.api.buildApiGetMethod'("", '', '')

'Then verify response code should be 200'
WS.verifyResponseStatusCode(responseAppointment, 200)

def parsedHtml = Jsoup.parse(responseAppointment.getResponseText())

//Get the table rows, adjust the selector according to your table's structure
Elements rows = parsedHtml.select("table tbody tr td form input")
KeywordUtil.logInfo('Elements in HTML: ' + rows)

//Init id default value is 0
int id = 0

//Loop through the rows and extract the IDs
for (int i=0; i < rows.size(); i++) {
	//Get elements new create and map with ID of new record
	int elementtRows = rows.size() - (i + 1)	
	KeywordUtil.logInfo('Rows i : ' + elementtRows)
	
    Element row = rows.get(elementtRows)
    String valueFromTagInput = row.select("input").attr("value")
	
	int idText = Integer.parseInt(valueFromTagInput)
	KeywordUtil.logInfo('idIdx in HTML: ' + idText)
	
	//Check idText from HTML of list appointments have exits and id with idx of table or not
	if(idText > 0 && !idText.toString().empty) {
		id = idText
		break
	}
}

KeywordUtil.logInfo('Get id from HTML is: ' + id)

'Init params request'
def data = ["id": id]

def quersyData = CustomKeywords.'resources.api.http_build_query'(data, '', '&', 'UTF-8').replaceAll(" ", "")
KeywordUtil.logInfo('Data request in body with query string: ' + quersyData)

'When Call the request to delete appointment'
ResponseObject res = CustomKeywords.'resources.api.buildApiPostMethodByFormData'('delete', '', quersyData)

'Then verify response code should be 302'
WS.verifyResponseStatusCode(res, 302)