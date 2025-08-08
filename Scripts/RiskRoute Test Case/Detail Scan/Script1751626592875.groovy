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

/*/ Tarayıcıyı aç ve siteye git
WebUI.openBrowser('')

WebUI.navigateToUrl('https://platform.catchprobe.org/')

WebUI.maximizeWindow()

// Login işlemleri
WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'), 30)

WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'))

WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 30)

WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 'fatih.yuksel@catchprobe.com')

WebUI.setEncryptedText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Password_password'), 'RigbBhfdqOBDK95asqKeHw==')

WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Sign in'))

WebUI.delay(5)

// OTP işlemi
def randomOtp = (100000 + new Random().nextInt(900000)).toString()

WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'), randomOtp)

WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Verify'))

WebUI.delay(5)

WebUI.waitForPageLoad(30)

/*/
// Riskroute sekmesine tıkla

//WebUI.navigateToUrl('https://platform.catchprobe.org/riskroute')

WebUI.delay(2)


WebUI.navigateToUrl('https://platform.catchprobe.org/riskroute/detail-scan')

WebUI.waitForPageLoad(10)


WebDriver driver = DriverFactory.getWebDriver()
JavascriptExecutor js = (JavascriptExecutor) driver
Actions actions = new Actions(driver)

// 2️⃣ Continue'ye tıklamadan önce scroll
WebElement continueButton1 = WebUiCommonHelper.findWebElement(findTestObject('Object Repository/Detail Scan/Continue'), 5)
scrollToVisible(continueButton1, js)

// 1️⃣ Passive Scan'e tıkla
WebUI.click(findTestObject('Object Repository/Detail Scan/Passive Scan'))
WebUI.delay(1)

js.executeScript("window.scrollTo(0, document.body.scrollHeight)")
WebUI.delay(1)

// 2️⃣ Continue'ye tıkla
WebUI.click(findTestObject('Object Repository/Detail Scan/Continue'))
WebUI.delay(1)

// 3️⃣ Search Keyword alanına 'teknosa' yaz
WebUI.setText(findTestObject('Object Repository/Detail Scan/Search Keyword'), 'teknosa')
WebUI.delay(0.5)

// 4️⃣ Search Email alanına 'fatih.yuksel@catchprobe.com' yaz
WebUI.setText(findTestObject('Object Repository/Detail Scan/Search email'), 'fatih.yuksel@catchprobe.com')
WebUI.delay(0.5)

// 5️⃣ Continue'ye tıkla
WebUI.click(findTestObject('Object Repository/Detail Scan/Continue'))
WebUI.delay(1)

// 6️⃣ Domain Name alanına 'teknosa.com' yaz
WebUI.setText(findTestObject('Object Repository/Detail Scan/Domain name'), 'teknosa.com')
WebUI.delay(0.5)

// 7️⃣ IP Address alanına '1.1.1.1' yaz
WebUI.setText(findTestObject('Object Repository/Detail Scan/Ip Address'), '1.1.1.1')
WebUI.delay(0.5)

// 8️⃣ Continue'ye tıkla
WebUI.click(findTestObject('Object Repository/Detail Scan/Continue'))
WebUI.delay(1)

// 9️⃣ Alarm On butonuna tıkla
WebUI.click(findTestObject('Object Repository/Detail Scan/Alarm On'))
WebUI.delay(1)

// 🔟 Start Scan butonuna tıkla
WebUI.click(findTestObject('Object Repository/Detail Scan/Start Scan'))
WebUI.delay(1)

// 1️⃣1️⃣ Toast mesajını 30 saniye içinde bekle
boolean isToastVisible = WebUI.waitForElementVisible(findTestObject('Object Repository/Detail Scan/Toast'), 30)

if (isToastVisible) {
	println "✅ Scan başarıyla başlatıldı, toast mesajı göründü."
} else {
	KeywordUtil.markFailedAndStop("❌ Scan toast mesajı 30 saniye içinde görünmedi!")
}
WebUI.delay(1)
WebUI.refresh()
WebUI.delay(3)


// 1️⃣3️⃣ Scan History'de Domain Name kontrolü
TestObject scanHistoryDomainName = new TestObject()
scanHistoryDomainName.addProperty("xpath", ConditionType.EQUALS, "//span[contains(@class, 'absolute bottom') and contains(., 'teknosa.com')]")

boolean isDomainPresent = WebUI.verifyElementPresent(scanHistoryDomainName, 15, FailureHandling.OPTIONAL)

if (isDomainPresent) {
	println "✅ Scan History sayfasında 'teknosa.com' bulundu."
} else {
	KeywordUtil.markFailedAndStop("❌ Scan History sayfasında 'teknosa.com' bulunamadı!")
}