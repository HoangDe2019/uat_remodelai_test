package resources

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows

import bsh.This
import internal.GlobalVariable
import org.openqa.selenium.WebElement
import org.openqa.selenium.WebDriver
import org.openqa.selenium.By

import com.kms.katalon.core.mobile.keyword.internal.MobileDriverFactory
import com.kms.katalon.core.webui.driver.DriverFactory

import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.testobject.RestRequestObjectBuilder
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty

import com.kms.katalon.core.mobile.helper.MobileElementCommonHelper
import com.kms.katalon.core.util.KeywordUtil

import com.kms.katalon.core.webui.exception.WebElementNotFoundException
import com.kms.katalon.core.testobject.impl.HttpFormDataBodyContent
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import groovy.json.JsonSlurper
import java.net.URLEncoder

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import javax.xml.bind.DatatypeConverter

import java.io.File
import org.jsoup.nodes.Document
import org.jsoup.Jsoup
import org.apache.commons.codec.binary.Base64
import org.apache.commons.io.FileUtils

public class api {
	public static final String BASE_URL = GlobalVariable.api_base_url;

	//keyword for POST method
	@Keyword
	def buildApiPostMethod(String subPath, String token, String body) {
		String endPoint = BASE_URL + subPath;
		RequestObject ro = sampleRequest1(token, endPoint, "POST", body)
		ResponseObject respObj = WS.sendRequest(ro)

		System.out.println("Send POST request: " + endPoint)
		System.out.println("Status code: " + respObj.statusCode)
		System.out.println("Response body: " + respObj.responseBodyContent)
		return respObj
	}
	//keyword for PUT method
	@Keyword
	def buildApiPutMethod(String subPath, String token, String body) {
		String endPoint = BASE_URL + subPath;
		RequestObject ro = sampleRequest1(token, endPoint, "PUT", body)
		ResponseObject respObj = WS.sendRequest(ro)

		System.out.println("Send PUT request: " + endPoint)
		System.out.println("Status code: " + respObj.statusCode)
		System.out.println("Response body: " + respObj.responseBodyContent)
		return respObj
	}
	//key word for DELETE method
	@Keyword
	def buildApiDeleteMethod(String subPath, String token, String body) {
		String endPoint = BASE_URL + subPath;
		RequestObject ro = sampleRequest1(token, endPoint, "DELETE", body)
		ResponseObject respObj = WS.sendRequest(ro)

		System.out.println("Send DELETE request: " + endPoint)
		System.out.println("Status code: " + respObj.statusCode)
		System.out.println("Response body: " + respObj.responseBodyContent)
		return respObj
	}
	//keyword for POST,PUT method
	@Keyword
	def RequestObject sampleRequest1(String authHeader, String endPoint, String requestMethod, String body) {
		TestObjectProperty header1 = new TestObjectProperty("Authorization", ConditionType.EQUALS, authHeader)
		TestObjectProperty header2 = new TestObjectProperty("Content-Type", ConditionType.EQUALS, "application/json")
		TestObjectProperty header3 = new TestObjectProperty("Accept", ConditionType.EQUALS, "*/*")
		ArrayList defaultHeaders  = Arrays.asList(header2, header3)
		if(!authHeader.empty) {
			defaultHeaders = Arrays.asList(header1, header2, header3)
			System.out.println("defaultHeaders: " + defaultHeaders)
		}

		RequestObject ro = new RequestObject("objectId")
		ro.setRestUrl(endPoint)
		ro.setHttpHeaderProperties(defaultHeaders)
		ro.setRestRequestMethod(requestMethod)
		ro.setBodyContent(new HttpTextBodyContent(body))
		return ro
	}
	//keyword for GET method
	@Keyword
	def buildApiGetMethod(String subPath, String token, String body) {
		String endPoint = BASE_URL + subPath;
		RequestObject ro = sampleRequest2(token, endPoint, "GET")
		ResponseObject respObj = WS.sendRequest(ro)
		System.out.println("Send GET request: " + endPoint)
		System.out.println("Status code: " + respObj.statusCode)
		System.out.println("Response body: " + respObj.responseBodyContent)
		return respObj
	}
	//keyword for GET method
	@Keyword
	def RequestObject sampleRequest2(String authHeader, String endPoint, String requestMethod) {
		//		String token = loginAndGetToken('/login', GlobalVariable.email,GlobalVariable.password)
		TestObjectProperty header1 = new TestObjectProperty("Authorization", ConditionType.EQUALS, authHeader)
		TestObjectProperty header2 = new TestObjectProperty("Content-Type", ConditionType.EQUALS, "application/json")
		TestObjectProperty header3 = new TestObjectProperty("Accept", ConditionType.EQUALS, "*/*")
		ArrayList defaultHeaders = Arrays.asList(header1, header2, header3)

		RequestObject ro = new RequestObject("objectId")
		ro.setRestUrl(endPoint)
		ro.setHttpHeaderProperties(defaultHeaders)
		ro.setRestRequestMethod(requestMethod)
		return ro
	}
	@Keyword
	def loginAndGetToken(String subPath, String username, String password)
	{
		//Set the request body with the given username and password
		def body = '{\n    \"email\": \"'+ username +'\",\n    \"password\": \"'+ password +'\"\n}'

		//Send the request and get the response
		def response = buildApiPostMethod(subPath, "", body)

		//Check if the response is success
		if (response.getStatusCode() == 200) {
			//Parse the response JSON
			def jsonResponse = new JsonSlurper().parseText(response.getResponseText())

			//Return the token from the response
			println(jsonResponse.data.token)

			if(!jsonResponse.data.authToken.empty) {
				KeywordUtil.markPassed("Generated authToken valid!")
				setDataAuToken(jsonResponse.data.authToken)
				//set data company
				setDataCompany(jsonResponse.data.companyId)
			}

			KeywordUtil.markPassed("Generated token valid!")

			return jsonResponse.data.type + ' ' + jsonResponse.data.token
		}

		//If the response is not success, log the error and return null
		KeywordUtil.markFailedAndStop("Response status code not match. Expected: " + 200 + " - Actual: " + response.getStatusCode() ) // temp not using authen -> dus
		return ''
	}

	@Keyword
	def http_build_query(Object data, Object numeric_prefix, String arg_separator, String enc_type = "UTF-8") {
		def queryString = data.collect {  key, value ->
			"${URLEncoder.encode(key, enc_type)} = ${URLEncoder.encode(value.toString(), enc_type)}"
		}.join(arg_separator)

		KeywordUtil.logInfo("queryString: ${queryString}")
		// remove space in url query param
		return queryString
	}

	//keyword for POST method
	@Keyword
	def buildApiPostMethodByFormData(String subPath, String token, String body) {
		String endPoint = BASE_URL + subPath;
		RequestObject ro = sampleRequest3(token, endPoint, "POST", body)
		ResponseObject respObj = WS.sendRequest(ro)

		System.out.println("Send POST request: " + endPoint)
		System.out.println("Status code: " + respObj.statusCode)
		System.out.println("Response body: " + respObj.responseBodyContent)
		return respObj
	}

	//keyword for POST,PUT method
	@Keyword
	def RequestObject sampleRequest3(String authHeader, String endPoint, String requestMethod, def body) {
		TestObjectProperty header1 = new TestObjectProperty("Authorization", ConditionType.EQUALS, authHeader)
		TestObjectProperty header2 = new TestObjectProperty("Content-Type", ConditionType.EQUALS, "application/x-www-form-urlencoded")
		TestObjectProperty header3 = new TestObjectProperty("Accept", ConditionType.EQUALS, "*/*")
		ArrayList defaultHeaders  = Arrays.asList(header2, header3)
		if(!authHeader.empty) {
			defaultHeaders = Arrays.asList(header1, header2, header3)
		}

		RequestObject ro = new RequestObject()
		ro.setRestUrl(endPoint)
		ro.setHttpHeaderProperties(defaultHeaders)
		ro.setRestRequestMethod(requestMethod)
		ro.setBodyContent(new HttpTextBodyContent(body))
		return ro
	}


	/*
	 * @param String: path of file local
	 * @return String: type data is encode after upload and read file, then covert to base64encode
	 */
	@Keyword
	def uploadFile(String filePath)
	{
		File file = new File(filePath)

		// Read the HTML file content
		def pathUrl = file.getPath()
		KeywordUtil.logInfo("pathUrl: ${pathUrl}")

		//define encodedfile default is null
		String encodedfile = ''
		String extension = null

		//get FIle name
		String fileName = file.getName()
		String[] listTypeSupport = ['jpg', 'webp']

		// set default is false
		Boolean isVaild = false

		try {
			FileInputStream fileInputStreamReader = new FileInputStream(file)

			// Read file and covert to array byte
			byte[] fileContent = new byte[(int)file.length()]
			Double sizeOfFIle = fileContent.length / 1024
			fileInputStreamReader.read(fileContent)

			//validate FIle Type
			int i =  fileName.lastIndexOf('.')
			if(i >= 0) extension = fileName.substring(i+1)

			// check if the specified element
			// is present in the array or not
			// using contains() method and size of file
			isVaild = sizeOfFIle < 1024 && Arrays.asList(listTypeSupport).contains(extension)
			// Print the result
			System.out.println("Is " + extension
					+ " present in the array: " + isVaild);
			if(isVaild) {
				// convert to base 64 encoding
				encodedfile = Base64.encodeBase64(fileContent).toString()
				'Validate file was uploaded successfully!'
				KeywordUtil.markPassed("There is no error - The file uploaded with success")
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			KeywordUtil.markFailed("No file was uploaded - Actual: " + e.getMessage())
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			KeywordUtil.markFailed("An error occurred while uploading a file: " + e.getMessage())
			e.printStackTrace();
		}

		assert isVaild : "File's type uploaded inccorect or File size error!"

		return encodedfile;
	}

	//keyword for POST method
	@Keyword
	def buildApiPostMethodForUploadFile(String subPath, String token, String body) {
		String endPoint = BASE_URL + subPath;
		RequestObject ro = sampleRequest1(token, endPoint, "POST", body)

		//update Content-Type is form-data
		ro.getHttpHeaderProperties().each { headerObj ->
			if(headerObj.getName().equals("Content-Type") && headerObj.getValue().equals("application/json")) {
				headerObj.setValue("multipart/form-data")
			}
		}

		ResponseObject respObj = WS.sendRequest(ro)
		System.out.println("Send POST request: " + endPoint)
		System.out.println("Status code: " + respObj.statusCode)
		System.out.println("Response body: " + respObj.responseBodyContent)
		return respObj
	}

	//keyword for POST method
	@Keyword
	def getAuthToken() {
		return GlobalVariable.auToken.toString()
	}

	public void setDataAuToken(String newAuToken) {
		GlobalVariable.auToken = newAuToken
	}

	public void setDataCompany(String newCompanyId) {
		GlobalVariable.newCompanyId = newCompanyId
	}

	@Keyword
	def getCompanyInfoAccout() {
		return GlobalVariable.newCompanyId.toString()
	}
}
