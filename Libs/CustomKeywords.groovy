
/**
 * This class is generated automatically by Katalon Studio and should not be modified or deleted.
 */

import com.kms.katalon.core.testobject.TestObject

import java.lang.String

import java.lang.Object


 /**
	 * Refresh browser
	 */ 
def static "resources.ui.refreshBrowser"() {
    (new resources.ui()).refreshBrowser()
}

 /**
	 * Click element
	 * @param to Katalon test object
	 */ 
def static "resources.ui.clickElement"(
    	TestObject to	) {
    (new resources.ui()).clickElement(
        	to)
}

 /**
	 * Get all rows of HTML table
	 * @param table Katalon test object represent for HTML table
	 * @param outerTagName outer tag name of TR tag, usually is TBODY
	 * @return All rows inside HTML table
	 */ 
def static "resources.ui.getHtmlTableRows"(
    	TestObject table	
     , 	String outerTagName	) {
    (new resources.ui()).getHtmlTableRows(
        	table
         , 	outerTagName)
}


def static "resources.ui.OpenAndNavigateHomPage"(
    	String url	) {
    (new resources.ui()).OpenAndNavigateHomPage(
        	url)
}


def static "resources.ui.InputUserNameAndPassWordToLogin"(
    	String userName	
     , 	String passWord	) {
    (new resources.ui()).InputUserNameAndPassWordToLogin(
        	userName
         , 	passWord)
}


def static "resources.ui.TakeScreenShots"(
    	String screenshotName	
     , 	String folderName	) {
    (new resources.ui()).TakeScreenShots(
        	screenshotName
         , 	folderName)
}


def static "resources.ui.InputEmailToResetPass"(
    	String email	) {
    (new resources.ui()).InputEmailToResetPass(
        	email)
}


def static "resources.ui.clearCacheAndStorage"() {
    (new resources.ui()).clearCacheAndStorage()
}


def static "resources.api.buildApiPostMethod"(
    	String subPath	
     , 	String token	
     , 	String body	) {
    (new resources.api()).buildApiPostMethod(
        	subPath
         , 	token
         , 	body)
}


def static "resources.api.buildApiPutMethod"(
    	String subPath	
     , 	String token	
     , 	String body	) {
    (new resources.api()).buildApiPutMethod(
        	subPath
         , 	token
         , 	body)
}


def static "resources.api.buildApiDeleteMethod"(
    	String subPath	
     , 	String token	
     , 	String body	) {
    (new resources.api()).buildApiDeleteMethod(
        	subPath
         , 	token
         , 	body)
}


def static "resources.api.sampleRequest1"(
    	String authHeader	
     , 	String endPoint	
     , 	String requestMethod	
     , 	String body	) {
    (new resources.api()).sampleRequest1(
        	authHeader
         , 	endPoint
         , 	requestMethod
         , 	body)
}


def static "resources.api.buildApiGetMethod"(
    	String subPath	
     , 	String token	
     , 	String body	) {
    (new resources.api()).buildApiGetMethod(
        	subPath
         , 	token
         , 	body)
}


def static "resources.api.sampleRequest2"(
    	String authHeader	
     , 	String endPoint	
     , 	String requestMethod	) {
    (new resources.api()).sampleRequest2(
        	authHeader
         , 	endPoint
         , 	requestMethod)
}


def static "resources.api.loginAndGetToken"(
    	String subPath	
     , 	String username	
     , 	String password	) {
    (new resources.api()).loginAndGetToken(
        	subPath
         , 	username
         , 	password)
}


def static "resources.api.http_build_query"(
    	Object data	
     , 	Object numeric_prefix	
     , 	String arg_separator	
     , 	String enc_type	) {
    (new resources.api()).http_build_query(
        	data
         , 	numeric_prefix
         , 	arg_separator
         , 	enc_type)
}


def static "resources.api.buildApiPostMethodByFormData"(
    	String subPath	
     , 	String token	
     , 	String body	) {
    (new resources.api()).buildApiPostMethodByFormData(
        	subPath
         , 	token
         , 	body)
}


def static "resources.api.sampleRequest3"(
    	String authHeader	
     , 	String endPoint	
     , 	String requestMethod	
     , 	Object body	) {
    (new resources.api()).sampleRequest3(
        	authHeader
         , 	endPoint
         , 	requestMethod
         , 	body)
}


def static "resources.api.uploadFile"(
    	String filePath	) {
    (new resources.api()).uploadFile(
        	filePath)
}


def static "resources.api.buildApiPostMethodForUploadFile"(
    	String subPath	
     , 	String token	
     , 	String body	) {
    (new resources.api()).buildApiPostMethodForUploadFile(
        	subPath
         , 	token
         , 	body)
}


def static "resources.api.getAuthToken"() {
    (new resources.api()).getAuthToken()
}


def static "resources.api.getCompanyInfoAccout"() {
    (new resources.api()).getCompanyInfoAccout()
}


def static "resources.api.http_build_query"(
    	Object data	
     , 	Object numeric_prefix	
     , 	String arg_separator	) {
    (new resources.api()).http_build_query(
        	data
         , 	numeric_prefix
         , 	arg_separator)
}
