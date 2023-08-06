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

String charset = (('a'..'z') + ('A'..'Z') + ('0'..'9')).join()
String randomString = RandomStringUtils.random(10, charset.toCharArray())
String charsetNumber = ('0'..'9').join()
String randomStringNumber = RandomStringUtils.random(10, charsetNumber.toCharArray())

'Load list dump data'
def paramData = findTestData("Plugins/create_new_data_reactjs")

def data = ""
//Set config which you get data in any rows other
def numberRecordInRows = 1

'Get key token'
def token = CustomKeywords.'resources.api.loginAndGetToken'('login', GlobalVariable.email,GlobalVariable.password)

'When Call the request to get list of companies'
ResponseObject response = CustomKeywords.'resources.api.buildApiGetMethod'('api/v1/company/all', token, '')

'Then verify response code should be 200'
//WS.verifyResponseStatusCode(response, 200)

// Parse the response body as JSON
def jsonSlurper = new JsonSlurper()
def responseBodyOfComapanies = jsonSlurper.parseText(response.getResponseBodyContent())

'And Verify that the list contains at least one item'
assert !responseBodyOfComapanies.empty : 'No content in body!'

'Print the information for each item in the list'
responseBodyOfComapanies.each { info ->
	KeywordUtil.logInfo("Info: ${info}")
}

'Get companyId'
def companyId = responseBodyOfComapanies.keySet()

//Get all values from file excel
for (int i = 1; i <= paramData.getRowNumbers(); i++) {
	if(i == numberRecordInRows) {
		'Read data from file excel, then preparing for sending request and data'
		data = "{\n    \"name\" : \"${paramData.getValue('name', i) + randomString}\",\n    \"title\": \"${paramData.getValue('title', i) + randomString}\",\n    \"avatarUrl\": \"${paramData.getValue('avatarUrl', i)}\",\n    \"color\": \"${paramData.getValue('color', i)}\",\n    \"companyId\": \"${companyId[responseBodyOfComapanies.size()-1]}\",\n    \"buttonName\" : \"${paramData.getValue('buttonName', i) + randomString}\",\n    \"buttonColor\": \"${paramData.getValue('buttonColor', i)}\"\n}"
		break
	}
}

KeywordUtil.logInfo('Data request in body: ' + data)

'When Call the request to create a new reactjs script'
ResponseObject res = CustomKeywords.'resources.api.buildApiPostMethod'('api/v1/reactjs', token, data)

'Then verify response code should be 201'
WS.verifyResponseStatusCode(res, 201)

'And Verify response body is not null'
def result = res.getResponseBodyContent()
KeywordUtil.logInfo('Actual response body: ' + result.toString())

assert !result.empty

'And Verify response body is properly'
assert result.contains('Please put the script below into the header of your website.')

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
def resultChatBotFromTagScript = jsonSlurper.parseText(ro.getResponseBodyContent())

KeywordUtil.logInfo('resultChatBotFromTagScript get body content: ' + resultChatBotFromTagScript)

WS.verifyElementPropertyValue(ro, "name", paramData.getValue("name", numberRecordInRows) + randomString)
WS.verifyElementPropertyValue(ro, "title", paramData.getValue("title", numberRecordInRows) + randomString)
WS.verifyElementPropertyValue(ro, "avatar", paramData.getValue("avatarUrl", numberRecordInRows))
WS.verifyElementPropertyValue(ro, "bgColor", paramData.getValue("color", numberRecordInRows))
WS.verifyElementPropertyValue(ro, "buttonName", paramData.getValue("buttonName", numberRecordInRows) + randomString)
WS.verifyElementPropertyValue(ro, "companyId", companyId[responseBodyOfComapanies.size()-1])
WS.verifyElementPropertyValue(ro, "buttonColor", paramData.getValue("buttonColor", numberRecordInRows))