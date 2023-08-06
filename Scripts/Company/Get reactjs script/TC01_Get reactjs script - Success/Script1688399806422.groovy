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
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.json.StringEscapeUtils

import com.kms.katalon.core.util.KeywordUtil
import static org.assertj.core.api.Assertions.*
import org.apache.commons.lang.RandomStringUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.net.URLDecoder

'Get key token'
def token = CustomKeywords.'resources.api.loginAndGetToken'('login', GlobalVariable.email,GlobalVariable.password)

'When Call the request get reactjs script'
ResponseObject res = CustomKeywords.'resources.api.buildApiGetMethod'('api/v1/reactjs', token, '')

'Then verify response code should be 200'
WS.verifyResponseStatusCode(res, 200)

'And Verify response body is not null'
def result = res.getResponseBodyContent()
KeywordUtil.logInfo('Actual response body: ' + result.toString())

assert !result.empty

def parsedHtml = Jsoup.parse(res.getResponseBodyContent())

//Get the table rows, adjust the selector according to your table's structure
Elements rows = parsedHtml.select("script")
KeywordUtil.logInfo('Elements in HTML: ' + rows)

//Init elmObj default value is null
def elmObj = null

//Loop through the rows and extract the IDs
for (int i=0; i < rows.size(); i++) {
	//Get elements new create and map with ID of new record
	int elementtRows = i
	KeywordUtil.logInfo('Rows i : ' + elementtRows)
	
	Element row = rows.get(elementtRows)
	KeywordUtil.logInfo('idIdx in HTML: ' + row.allElements)
	
	String valueFromTagObj = row.getElementsByTag("script")
	
	KeywordUtil.logInfo('valueFromTagScript in HTML: ' + valueFromTagObj)	
	//Check idText from HTML of list appointments have exits and id with idx of table or not
	if(valueFromTagObj.length() > 0 && valueFromTagObj.contains("window.Chatbot.renderChatbot")) {
		elmObj = valueFromTagObj
		break
	}
}

KeywordUtil.logInfo('elmObj get body content: ' + elmObj)

def bodyContentFromTagName = elmObj.toString()
KeywordUtil.logInfo('bodyContentFromTagName content: ' + bodyContentFromTagName)

//regex to get elemts in script tag
def pattern = /(\w+):\s+("[^"]+")/
def map = [:]


def matcher = (bodyContentFromTagName =~ pattern)
while (matcher.find()) {
    map[matcher.group(1)] = matcher.group(2).replace('"', '') // remove "" from value
}

//covert from array to Json, and then to string
String jsonString = JsonOutput.toJson(map)
KeywordUtil.logInfo('jsonString get body content: ' + jsonString)

// create a new response object
ResponseObject ro = new ResponseObject()
// set the response body
ro.responseText = jsonString

println('elmObj get body content: ' + ro.getResponseBodyContent())
def jsonSlurper = new JsonSlurper()

def resultChatBotFromTagScript = jsonSlurper.parseText(ro.getResponseBodyContent())

KeywordUtil.logInfo('resultChatBotFromTagScript get body content: ' + resultChatBotFromTagScript)

'Verify elm in tag script for each items'
WS.verifyElementPropertyValue(ro, "companyId", CustomKeywords.'resources.api.getCompanyInfoAccout'())
WS.verifyElementPropertyValue(ro, "token", CustomKeywords.'resources.api.getAuthToken'())