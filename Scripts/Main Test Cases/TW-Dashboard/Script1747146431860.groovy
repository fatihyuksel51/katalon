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
import com.kms.katalon.core.webui.driver.DriverFactory
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebElement
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import org.openqa.selenium.WebDriver

// ✅ Fonksiyon: Scroll edip görünür hale getir
def scrollToVisible(WebElement element, JavascriptExecutor js) {
	int currentScroll = 0
	boolean isVisible = false
	while (!isVisible && currentScroll < 3000) {
		js.executeScript("window.scrollBy(0, 200)")
		WebUI.delay(0.5)
		isVisible = element.isDisplayed()
		currentScroll += 200
	}
	return isVisible
}

// Tarayıcıyı aç ve siteye git
WebUI.openBrowser('')
WebUI.navigateToUrl('https://platform.catchprobe.org/')
WebUI.maximizeWindow()

// Login işlemleri
WebUI.waitForElementVisible(findTestObject('Object Repository/otp/Page_/a_PLATFORM LOGIN'), 30)
WebUI.click(findTestObject('Object Repository/otp/Page_/a_PLATFORM LOGIN'))
WebUI.waitForElementVisible(findTestObject('Object Repository/otp/Page_/input_Email Address_email'), 30)
WebUI.setText(findTestObject('Object Repository/otp/Page_/input_Email Address_email'), 'fatih.yuksel@catchprobe.com')
WebUI.setEncryptedText(findTestObject('Object Repository/otp/Page_/input_Password_password'), 'RigbBhfdqOBDK95asqKeHw==')
WebUI.click(findTestObject('Object Repository/otp/Page_/button_Sign in'))
WebUI.delay(3)

// OTP işlemi (dummy OTP)
def randomOtp = (100000 + new Random().nextInt(900000)).toString()
WebUI.setText(findTestObject('otp/Page_/input_OTP Digit_vi_1_2_3_4_5'), randomOtp)
WebUI.click(findTestObject('otp/Page_/button_Verify'))

// Dashboard sekmeleri
WebUI.waitForElementPresent(findTestObject('Object Repository/dashboard/Page_/newborndomain'),15)
WebUI.click(findTestObject('Object Repository/dashboard/Page_/svg_G_lucide lucide-webhook h-6 w-6'))
WebUI.click(findTestObject('Object Repository/dashboard/Page_/div_Malicious'))
WebUI.click(findTestObject('Object Repository/dashboard/Page_/div_Bad Reputation'))
WebUI.click(findTestObject('Object Repository/dashboard/Page_/div_Phishing'))
WebUI.click(findTestObject('Object Repository/dashboard/Page_/div_New Born Domain'))

// New Born Domain kontrolü
String newBornIp = WebUI.getText(findTestObject('Object Repository/dashboard/Page_/newborndomain'))
WebUI.click(findTestObject('Object Repository/dashboard/Page_/newborndomain'))
WebUI.waitForElementPresent(findTestObject('Object Repository/dashboard/Page_/Signature_ip'), 10)
assert WebUI.getText(findTestObject('Object Repository/dashboard/Page_/Signature_ip')).contains(newBornIp)
WebUI.click(findTestObject('Object Repository/dashboard/Page_/div_Dashboard'))

// Phishing kontrolü
WebUI.waitForElementPresent(findTestObject('Object Repository/dashboard/Page_/Phishing-İp'),15)
String phishingIp = WebUI.getText(findTestObject('Object Repository/dashboard/Page_/Phishing-İp'))
WebUI.click(findTestObject('Object Repository/dashboard/Page_/Phishing-İp'))
WebUI.waitForElementPresent(findTestObject('Object Repository/dashboard/Page_/Signature_ip'), 10)
assert WebUI.getText(findTestObject('Object Repository/dashboard/Page_/Signature_ip')).contains(phishingIp)
WebUI.click(findTestObject('Object Repository/dashboard/Page_/div_Dashboard'))

// Bad Reputation kontrolü
WebUI.waitForElementPresent(findTestObject('Object Repository/dashboard/Page_/Bad Reputation'),15)
String badRepoIp = WebUI.getText(findTestObject('Object Repository/dashboard/Page_/Bad Reputation'))
WebUI.click(findTestObject('Object Repository/dashboard/Page_/Bad Reputation'))
WebUI.waitForElementPresent(findTestObject('Object Repository/dashboard/Page_/Signature_ip'), 10)
assert WebUI.getText(findTestObject('Object Repository/dashboard/Page_/Signature_ip')).contains(badRepoIp)
WebUI.click(findTestObject('Object Repository/dashboard/Page_/div_Dashboard'))

// Malicious kontrolü
WebUI.waitForElementPresent(findTestObject('Object Repository/dashboard/Page_/Malicious'),15)
String maliciousIp = WebUI.getText(findTestObject('Object Repository/dashboard/Page_/Malicious'))
WebUI.click(findTestObject('Object Repository/dashboard/Page_/Malicious'))
WebUI.waitForElementPresent(findTestObject('Object Repository/dashboard/Page_/Signature_ip'), 10)
assert WebUI.getText(findTestObject('Object Repository/dashboard/Page_/Signature_ip')).contains(maliciousIp)
WebUI.click(findTestObject('Object Repository/dashboard/Page_/div_Dashboard'))

// Sonuçları log’a yazdır
println("✅ New Born IP: " + newBornIp)
println("✅ Phishing IP: " + phishingIp)
println("✅ Bad Reputation IP: " + badRepoIp)
println("✅ Malicious IP: " + maliciousIp)

// ==== 📌 Based On Today ====
WebUI.waitForElementPresent(findTestObject('Object Repository/dashboard/Page_/Basedontoday'), 5)
WebDriver driver = DriverFactory.getWebDriver()
JavascriptExecutor js = (JavascriptExecutor) driver
WebElement basedOnTodayElement = WebUiCommonHelper.findWebElement(findTestObject('Object Repository/dashboard/Page_/Basedontoday'), 10)

String basedOnTodayText = ''
if (scrollToVisible(basedOnTodayElement, js)) {
	basedOnTodayText = basedOnTodayElement.getText()
	println("📌 Based On Today Text: " + basedOnTodayText)
	js.executeScript("arguments[0].click();", basedOnTodayElement)
	WebUI.comment("👉 'Based On Today' tıklandı.")
} else {
	WebUI.comment("❌ 'Based On Today' görünür değil, tıklanamadı.")
}
String todayResult = basedOnTodayText.replaceAll(/\d+$/, '').trim()
todayResult = todayResult.substring(todayResult.lastIndexOf(":") + 1).trim()
WebUI.waitForElementPresent(findTestObject('Object Repository/dashboard/Page_/Threatway SignatureList-CollectionName'), 10)
assert WebUI.getText(findTestObject('Object Repository/dashboard/Page_/Threatway SignatureList-CollectionName')).contains(todayResult)

// ==== 📌 Based On Yesterday ====
WebUI.click(findTestObject('Object Repository/dashboard/Page_/div_Dashboard'))
WebUI.waitForElementPresent(findTestObject('Object Repository/dashboard/Page_/Basedonyesterdaycollection'), 10)
WebUI.scrollToElement(findTestObject('Object Repository/dashboard/Page_/Basedonyesterdaycollection'), 5)

WebElement basedOnYesterdayElement = WebUiCommonHelper.findWebElement(findTestObject('Object Repository/dashboard/Page_/Basedonyesterdaycollection'), 10)

String basedOnYesterdayText = ''
if (scrollToVisible(basedOnYesterdayElement, js)) {
	basedOnYesterdayText = basedOnYesterdayElement.getText()
	println("📌 Based On Yesterday Text: " + basedOnYesterdayText)
	js.executeScript("arguments[0].click();", basedOnYesterdayElement)
	WebUI.comment("👉 'Based On Yesterday' tıklandı.")
} else {
	WebUI.comment("❌ 'Based On Yesterday' görünür değil, tıklanamadı.")
}
String yesterdayResult = basedOnYesterdayText.replaceAll(/\d+$/, '').trim()
yesterdayResult = yesterdayResult.substring(yesterdayResult.lastIndexOf(":") + 1).trim()
WebUI.waitForElementPresent(findTestObject('Object Repository/dashboard/Page_/Threatway SignatureList-CollectionName'), 10)
assert WebUI.getText(findTestObject('Object Repository/dashboard/Page_/Threatway SignatureList-CollectionName')).contains(yesterdayResult)


