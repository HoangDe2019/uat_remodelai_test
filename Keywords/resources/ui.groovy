package resources
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.eviware.soapui.config.TestSuite
import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.checkpoint.CheckpointFactory
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testcase.TestCaseFactory
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testdata.TestDataFactory
import com.kms.katalon.core.testobject.ObjectRepository
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords
import org.openqa.selenium.JavascriptExecutor

import internal.GlobalVariable

import org.openqa.selenium.WebElement
import org.openqa.selenium.WebDriver
import org.openqa.selenium.By

import com.kms.katalon.core.mobile.keyword.internal.MobileDriverFactory
import com.kms.katalon.core.webui.driver.DriverFactory

import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.context.TestCaseContext
import com.kms.katalon.core.context.TestSuiteContext
import com.kms.katalon.core.mobile.helper.MobileElementCommonHelper
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.entity.global.GlobalVariableEntity
import com.kms.katalon.core.webui.exception.WebElementNotFoundException
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW


class ui {
	/**
	 * Refresh browser
	 */
	@Keyword
	def refreshBrowser() {
		KeywordUtil.logInfo("Refreshing")
		WebDriver webDriver = DriverFactory.getWebDriver()
		webDriver.navigate().refresh()
		KeywordUtil.markPassed("Refresh successfully")
	}

	/**
	 * Click element
	 * @param to Katalon test object
	 */
	@Keyword
	def clickElement(TestObject to) {
		try {
			WebElement element = WebUiBuiltInKeywords.findWebElement(to);
			KeywordUtil.logInfo("Clicking element")
			element.click()
			KeywordUtil.markPassed("Element has been clicked")
		} catch (WebElementNotFoundException e) {
			KeywordUtil.markFailed("Element not found")
		} catch (Exception e) {
			KeywordUtil.markFailed("Fail to click on element")
		}
	}

	/**
	 * Get all rows of HTML table
	 * @param table Katalon test object represent for HTML table
	 * @param outerTagName outer tag name of TR tag, usually is TBODY
	 * @return All rows inside HTML table
	 */
	@Keyword
	def List<WebElement> getHtmlTableRows(TestObject table, String outerTagName) {
		WebElement mailList = WebUiBuiltInKeywords.findWebElement(table)
		List<WebElement> selectedRows = mailList.findElements(By.xpath("./" + outerTagName + "/tr"))
		return selectedRows
	}

	@Keyword
	def OpenAndNavigateHomPage (String url) {
		WebUI.openBrowser(null)
		WebUI.maximizeWindow()
		WebUI.navigateToUrl(url)
		clearCacheAndStorage()
	}

	@Keyword
	def InputUserNameAndPassWordToLogin (String userName, String passWord) {
		WebUI.waitForElementVisible(findTestObject('Login_page/inp_username'), 3)
		WebUI.setText(findTestObject('Login_page/inp_username'), userName)
		WebUI.setText(findTestObject('Login_page/inp_password'), passWord)
		clickElement(findTestObject('Login_page/btn_login'))
	}

	@Keyword
	def TakeScreenShots (String screenshotName, String folderName) {
		//Capture Screenshot
		String testCaseId = GlobalVariable.currentTestCaseId
		String testSuiteId = GlobalVariable.currentTestSuiteId
		String captureFileName = testCaseId + '_' + screenshotName
		WebUI.takeScreenshot((((RunConfiguration.getProjectDir() + '\\Screenshots\\' + testSuiteId + '\\' + testCaseId + '\\' + captureFileName))) + '.png')
	}


	@Keyword
	def InputEmailToResetPass (String email) {
		WebUI.waitForElementVisible(findTestObject('Forget_password_page/inp_email'), 3)
		WebUI.setText(findTestObject('Forget_password_page/inp_email'), email)
		clickElement(findTestObject('Forget_password_page/btn_confirm'))
	}

	@Keyword
	def clearCacheAndStorage() {
		WebDriver driver = DriverFactory.getWebDriver()
		//Delete all cookies
		driver.manage().deleteAllCookies()
		//Clear local storage
		JavascriptExecutor javascriptExecutor = ((driver) as JavascriptExecutor)
		javascriptExecutor.executeScript('window.localStorage.clear()')
		javascriptExecutor.executeScript('window.sessionStorage.clear()')
		refreshBrowser()
	}
}