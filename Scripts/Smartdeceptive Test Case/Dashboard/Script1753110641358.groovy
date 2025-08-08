import com.kms.katalon.core.webui.common.WebUiCommonHelper as WebUiCommonHelper
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.util.KeywordUtil as KeywordUtil
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import org.openqa.selenium.WebElement
import org.openqa.selenium.JavascriptExecutor
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
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import org.openqa.selenium.JavascriptExecutor as JavascriptExecutor
import org.openqa.selenium.WebElement as WebElement
import com.kms.katalon.core.webui.common.WebUiCommonHelper as WebUiCommonHelper
import org.openqa.selenium.WebDriver as WebDriver
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.util.KeywordUtil
import org.openqa.selenium.By
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.WebDriverWait
import org.openqa.selenium.support.ui.ExpectedConditions

import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.Keys
import org.openqa.selenium.WebElement

import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.webui.common.WebUiCommonHelper as WebUiCommonHelper
import java.text.SimpleDateFormat

/**
 * Smartdeceptive Dashboard bileşenlerini dinamik ID üzerinden kontrol eder.
 * <g> elementi var mı ve "no data" içeriyor mu diye denetler.
 * Hata varsa test fail olur.
 */

WebElement safeScrollTo(TestObject to) {
	if (to == null) {
		KeywordUtil.markFailed("❌ TestObject NULL – Repository yolunu kontrol et.")
		return null
	}
	if (!WebUI.waitForElementPresent(to, 2, FailureHandling.OPTIONAL)) {
		KeywordUtil.markFailed("❌ Element not present – scroll edilemedi: ${to.getObjectId()}")
		return null
	}
	WebElement element = WebUiCommonHelper.findWebElement(to, 1)
	JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getWebDriver()
	js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element)
	WebUI.delay(0.5)
	return element
}
/*/ Tarayıcıyı aç ve siteye git
WebUI.openBrowser('')
WebUI.navigateToUrl('https://platform.catchprobe.io/')
WebUI.maximizeWindow()

// Login işlemleri
WebUI.waitForElementVisible(findTestObject('Object Repository/otp/Page_/a_PLATFORM LOGIN'), 30)
WebUI.click(findTestObject('Object Repository/otp/Page_/a_PLATFORM LOGIN'))
WebUI.waitForElementVisible(findTestObject('Object Repository/otp/Page_/input_Email Address_email'), 30)
WebUI.setText(findTestObject('Object Repository/otp/Page_/input_Email Address_email'), 'fatih@test.com')
WebUI.setEncryptedText(findTestObject('Object Repository/otp/Page_/input_Password_password'), 'v4yvAQ7Q279BF5ny4hDiTA==')
WebUI.click(findTestObject('Object Repository/otp/Page_/button_Sign in'))
WebUI.delay(3)

// OTP işlemi
def randomOtp = (100000 + new Random().nextInt(900000)).toString()
WebUI.setText(findTestObject('otp/Page_/input_OTP Digit_vi_1_2_3_4_5'), randomOtp)
WebUI.click(findTestObject('otp/Page_/button_Verify'))
WebUI.delay(3)
WebUI.waitForPageLoad(10)
// 1. Sayfa yüklendikten sonra mevcut organizasyonu oku
TestObject currentOrg = new TestObject()
currentOrg.addProperty("xpath", ConditionType.EQUALS, "//div[contains(@class, 'font-semibold') and contains(text(), 'Organization')]//span[@class='font-thin']")

String currentOrgText = WebUI.getText(currentOrg)

// 2. Kontrol et: Eğer zaten TEST COMPANY ise hiçbir şey yapma
if (currentOrgText != 'TEST COMPANY') {
	// 3. Organization butonuna tıkla
	TestObject orgButton = new TestObject()
	orgButton.addProperty("xpath", ConditionType.EQUALS, "//button[.//div[contains(text(), 'Organization :')]]")
	WebUI.click(orgButton)

	// 4. TEST COMPANY seçeneğine tıkla
	TestObject testCompanyOption = new TestObject()
	testCompanyOption.addProperty("xpath", ConditionType.EQUALS, "//button[.//div[text()='TEST COMPANY']]")
	WebUI.click(testCompanyOption)
}
WebUI.waitForPageLoad(10)
/*/
// Smartdeceptive sekmesine tıkla

WebUI.navigateToUrl('https://platform.catchprobe.io/smartdeceptive')
WebUI.delay(3)
WebUI.waitForPageLoad(10)


/**
 * Smartdeceptive Dashboard bileşenlerini dinamik ID üzerinden kontrol eder.
 * <g> veya <canvas> elementi var mı ve "no data" içeriyor mu diye denetler.
 * Hata varsa test fail olur.
 */

/**
 * Standart SVG grafik içeren component'leri ID üzerinden kontrol eder
 */
def checkComponentById(String elementId, String logName) {
	TestObject dynObj = new TestObject(logName)
	dynObj.addProperty("id", ConditionType.EQUALS, elementId)

	if (WebUI.verifyElementPresent(dynObj, 5, FailureHandling.OPTIONAL)) {
		WebElement el = safeScrollTo(dynObj)
		WebUI.delay(1)

		if (el != null) {
			Boolean hasGraphics = WebUI.executeJavaScript(
				"""
				const el = arguments[0];
				return el.querySelector('g') !== null &&
				       !el.innerText.toLowerCase().includes('no data') &&
				       !el.innerText.toLowerCase().includes('data not found');
				""",
				Arrays.asList(el)
			)

			if (hasGraphics) {
				KeywordUtil.logInfo("✅ ${logName} içinde grafik ve veri bulundu.")
			} else {
				KeywordUtil.markWarning("❌ ${logName} bileşeni boş veya 'no data' içeriyor!")
			}
		} else {
			KeywordUtil.markWarning("❌ ${logName} elementi scroll edilemedi.")
		}
	} else {
		KeywordUtil.markWarning("❌ ${logName} elementi DOM'da bulunamadı!")
	}
}

/**
 * Canvas içeren WordCloud component'leri kontrol eder (ID dinamikse XPath ile)
 */
def checkWordCloudCanvas(String logName) {
	TestObject dynObj = new TestObject(logName)
	dynObj.addProperty("xpath", ConditionType.EQUALS, "//div[starts-with(@id, 'wordCloudChart_')]")

	if (WebUI.verifyElementPresent(dynObj, 5, FailureHandling.OPTIONAL)) {
		WebElement el = safeScrollTo(dynObj)
		WebUI.delay(1)

		if (el != null) {
			Boolean hasCanvas = WebUI.executeJavaScript(
				"""
				const el = arguments[0];
				return el.querySelector('canvas') !== null &&
				       !el.innerText.toLowerCase().includes('no data') &&
				       !el.innerText.toLowerCase().includes('data not found');
				""",
				Arrays.asList(el)
			)

			if (hasCanvas) {
				KeywordUtil.logInfo("✅ ${logName} içinde canvas ve veri bulundu.")
			} else {
				KeywordUtil.markWarning("❌ ${logName} bileşeni boş veya 'no data' içeriyor!")
			}
		} else {
			KeywordUtil.markWarning("❌ ${logName} elementi scroll edilemedi.")
		}
	} else {
		KeywordUtil.markWarning("❌ ${logName} elementi DOM'da bulunamadı!")
	}
}


// ✅ ID ile Smartdeceptive dashboard component kontrolleri
checkComponentById("apexchartssmartdeceptive_chart_dashboard_attack_country", "The Most Attacking Countries")
checkComponentById("apexchartssmartdeceptive_chart_dashboard_attack_ip", "The Most Attacking IP Address")
checkComponentById("apexchartssmartdeceptive_chart_dashboard_hacker_group", "The Most Active Hacker Groups")
checkComponentById("apexchartssmartdeceptive_chart_dashboard_used_method", "The Most Used Methods")
checkComponentById("apexchartssmartdeceptive_chart_dashboard_used_techniques", "The Most Used Techniques")
checkComponentById("apexchartssmartdeceptive_chart_dashboard_most_used_proxies", "The Most Used Proxy & VPN")
checkComponentById("apexchartssmartdeceptive_chart_dashboard_attacked_services", "The Most Attacked Services")
checkComponentById("apexchartssmartdeceptive_chart_dashboard_highest_attacked_country", "Countries With The Highest Attacks")
checkComponentById("apexchartssmartdeceptive_chart_dashboard_most_used_ip_addresses_with_bad_reputation", "The Most Used IPs With Bad Reputation")
checkComponentById("apexchartssmartdeceptive_chart_dashboard_most_attacked_decoys", "The Most Attacked Decoys")
checkComponentById("apexchartssmartdeceptive_chart_dashboard_most_used_malwares", "The Most Used Malwares")
checkComponentById("apexchartssmartdeceptive_chart_dashboard_most_shell_codes", "The Most Used ShellCodes")
checkComponentById("apexchartssmartdeceptive_chart_dashboard_structure_attacks", "The Most Structure Attacks")
checkComponentById("apexchartssmartdeceptive_chart_dashboard_most_used_tor_nodes", "The Most Used Tor Nodes")
checkWordCloudCanvas("The Most Tried Passwords")


