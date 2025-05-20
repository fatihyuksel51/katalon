package utils

import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords

import internal.GlobalVariable

import org.openqa.selenium.WebElement
import org.openqa.selenium.WebDriver
import org.openqa.selenium.By

import com.kms.katalon.core.webui.exception.WebElementNotFoundException
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ConditionType
import org.openqa.selenium.JavascriptExecutor

class DateUtils {

	/**
	 * Sayfayı refresh eder
	 */
	@Keyword
	def refreshBrowser() {
		KeywordUtil.logInfo("Refreshing")
		WebDriver webDriver = DriverFactory.getWebDriver()
		webDriver.navigate().refresh()
		KeywordUtil.markPassed("Refresh successfully")
	}

	/**
	 * Elemente tıklar
	 * @param to Katalon test object
	 */
	@Keyword
	def clickElement(TestObject to) {
		try {
			WebElement element = WebUiBuiltInKeywords.findWebElement(to)
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
	 * HTML tablosundaki tüm satırları getirir
	 * @param table Katalon test object (tablo)
	 * @param outerTagName TR tag'inin dış tag adı (genelde TBODY)
	 * @return Tablodaki tüm satırlar (List<WebElement>)
	 */
	@Keyword
	def List<WebElement> getHtmlTableRows(TestObject table, String outerTagName) {
		WebElement mailList = WebUiBuiltInKeywords.findWebElement(table)
		List<WebElement> selectedRows = mailList.findElements(By.xpath("./" + outerTagName + "/tr"))
		return selectedRows
	}

	/**
	 * Bugünkü tarihi içeren hücrenin sayfada olup olmadığını kontrol eder
	 */
	@Keyword
	def verifyTodayDateCellPresent() {
		// Bugünün tarihi (dd/MM/yyyy)
		def dateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy")
		def currentDate = dateFormat.format(new Date())

		KeywordUtil.logInfo("Bugünün tarihi: " + currentDate)

		// Dinamik TestObject oluştur
		TestObject dateCell = new TestObject("dynamicDateCell")
		dateCell.addProperty("xpath", ConditionType.EQUALS, "//td[.//span[contains(text(), '"+ currentDate +"')]]")

		// Element var mı kontrol et
		WebUiBuiltInKeywords.verifyElementPresent(dateCell, 10)

		KeywordUtil.markPassed("Bugünün tarihini içeren hücre bulundu: " + currentDate)
	}
	@Keyword
	def getCurrentDate() {
		def dateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy")
		return dateFormat.format(new Date())
	}

	@Keyword
	def boolean scrollToFindText(WebElement container, String expectedText) {
		def js = (JavascriptExecutor) DriverFactory.getWebDriver()
		int currentScroll = 0
		boolean isFound = false
		String currentText = ''

		while (!isFound && currentScroll < 5000) {
			List<WebElement> spans = container.findElements(By.tagName('span'))

			for (WebElement span : spans) {
				currentText = span.getText()
				if (currentText.equals(expectedText)) {
					isFound = true
					break
				}
			}

			if (!isFound) {
				js.executeScript("arguments[0].scrollLeft += 200", container)
				WebUI.delay(0.5)
				currentScroll += 200
			}
		}

		return isFound
	}
}