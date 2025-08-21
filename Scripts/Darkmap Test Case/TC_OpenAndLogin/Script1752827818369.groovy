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
import com.catchprobe.utils.MailReader
import static com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords.*

// ✅ Güvenli scroll fonksiyonu
WebElement safeScrollTo(TestObject to) {
	if (to == null) {
		KeywordUtil.markFailed("❌ TestObject NULL – Repository yolunu kontrol et.")
		return null
	}
	if (!WebUI.waitForElementPresent(to, 5, FailureHandling.OPTIONAL)) {
		KeywordUtil.logInfo("ℹ️ Element not present, scroll işlemi atlandı: ${to.getObjectId()}")
		return null
	}
	WebElement element = WebUiCommonHelper.findWebElement(to, 5)
	JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getWebDriver()
	js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element)
	WebUI.delay(0.5)
	return element
}
TestObject X(String xpath) {
    TestObject to = new TestObject(xpath)
    to.addProperty("xpath", ConditionType.EQUALS, xpath)
    return to
}


// Başlangıç işlemleri
WebUI.openBrowser('')
WebUI.navigateToUrl('https://platform.catchprobe.org/')
WebUI.maximizeWindow()

WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'), 30)
safeScrollTo(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'))
WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'))
WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 30)
safeScrollTo(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'))
WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 'katalon.test@catchprobe.com')
safeScrollTo(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Password_password'))
WebUI.setEncryptedText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Password_password'), 'RigbBhfdqOBDK95asqKeHw==')
safeScrollTo(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Sign in'))
WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Sign in'))
WebUI.delay(1)

WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'), 30)
String randomOtp = (100000 + new Random().nextInt(900000)).toString()
safeScrollTo(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'))
WebUI.delay(1)
WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'), randomOtp)
safeScrollTo(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Verify'))
WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Verify'))
WebUI.delay(5)
WebUI.waitForPageLoad(10)
String Threat = "//span[text()='Threat']"
WebUI.waitForElementVisible(X(Threat), 10, FailureHandling.OPTIONAL)


// 1. Sayfa yüklendikten sonra mevcut organizasyonu oku
TestObject currentOrg = new TestObject()
currentOrg.addProperty("xpath", ConditionType.EQUALS, "//div[contains(@class, 'font-semibold') and contains(text(), 'Organization')]//span[@class='font-thin']")

String currentOrgText = WebUI.getText(currentOrg)

// 2. Kontrol et: Eğer zaten Katalon ise hiçbir şey yapma
if (currentOrgText != 'Mail Test') {
	// 3. Organization butonuna tıkla
	TestObject orgButton = new TestObject()
	orgButton.addProperty("xpath", ConditionType.EQUALS, "//button[.//div[contains(text(), 'Organization :')]]")
	WebUI.click(orgButton)

	// 4. Mail Test seçeneğine tıkla
	TestObject testCompanyOption = new TestObject()
	testCompanyOption.addProperty("xpath", ConditionType.EQUALS, "//button[.//div[text()='Mail Test']]")
	WebUI.click(testCompanyOption)
}

WebUI.delay(3)
WebUI.waitForPageLoad(10)



