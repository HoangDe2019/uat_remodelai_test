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

import java.nio.file.Files
import java.nio.file.Paths

import org.openqa.selenium.Keys as Keys
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import com.kms.katalon.core.util.KeywordUtil
import static org.assertj.core.api.Assertions.*


'Get key token'
def tokenId = CustomKeywords.'resources.api.loginAndGetToken'('login', GlobalVariable.email,GlobalVariable.password)

'When Call the request to get chatbot file'
ResponseObject response = CustomKeywords.'resources.api.buildApiGetMethod'('api/v1/chatbot.js', tokenId, '')

'Then verify response code should be 200'
WS.verifyResponseStatusCode(response, 200)

def dateObj = new Date()
// get url Diretory
def dir = new File('.').absolutePath.toString()
def convertedDir = dir.replaceAll('\\\\', '/').replaceFirst("/", "//").replace('.', '')

// Save the HTML file from the response
// Replace 'path/to/save/filename.html' with the desired path where the HTML file should be saved
def subPath = "Reports/DownloadsFile/ChatBot_File/chatbot_file_${dateObj.getTimeImpl()}.js"

def pathFile = convertedDir +  subPath
KeywordUtil.logInfo('pathFile to be downloaded: ' + pathFile)

Files.write(Paths.get(pathFile), response.getResponseBodyContent().getBytes())

// Read the HTML file content
def htmlContent = new String(Files.readAllBytes(Paths.get(pathFile)))
KeywordUtil.logInfo('htmlContent: ' + htmlContent)

// Parse the HTML content using Jsoup library
Document doc = Jsoup.parse(htmlContent.toString())
KeywordUtil.logInfo('Document: ' + doc)

// Verify that the HTML content contains a specific element or text
// Replace 'element_selector' and 'expected_text' with the desired HTML element selector and expected text
def expectedText = "/*! For license information please see chatbot.js.LICENSE.txt */"
boolean textExists = doc.toString().contains(expectedText) && doc != null
assert textExists, "The expected text '${expectedText}' was not found in the HTML response."