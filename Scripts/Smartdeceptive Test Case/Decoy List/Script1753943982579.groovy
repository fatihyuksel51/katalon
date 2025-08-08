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
import com.kms.katalon.core.testobject.ConditionType as ConditionType
import com.kms.katalon.core.util.KeywordUtil as KeywordUtil
import org.openqa.selenium.By as By
import org.openqa.selenium.interactions.Actions as Actions
import org.openqa.selenium.support.ui.WebDriverWait as WebDriverWait
import org.openqa.selenium.support.ui.ExpectedConditions as ExpectedConditions
import com.catchprobe.utils.MailReader as MailReader
import static com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords.*
import java.text.SimpleDateFormat

/*/ ✅ Güvenli scroll fonksiyonu

WebUI.openBrowser('')

WebUI.navigateToUrl('https://platform.catchprobe.io/')

WebUI.maximizeWindow()

WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'), 30)

safeScrollTo(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'))

WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'))

WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 30)

safeScrollTo(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'))

WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 'fatih@test.com')

safeScrollTo(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Password_password'))

WebUI.setEncryptedText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Password_password'), 'v4yvAQ7Q279BF5ny4hDiTA==')

safeScrollTo(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Sign in'))

WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Sign in'))

WebUI.delay(5)

String randomOtp = (100000 + new Random().nextInt(900000)).toString()

safeScrollTo(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'))

WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'), randomOtp)

safeScrollTo(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Verify'))

WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Verify'))

WebUI.delay(5)

WebUI.waitForPageLoad(10)

CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()

safeScrollTo(findTestObject('Object Repository/Riskroute/Asset Lİst/Page_/Organization Butonu'))

WebUI.click(findTestObject('Object Repository/Riskroute/Asset Lİst/Page_/Organization Butonu'))

WebUI.delay(2)

safeScrollTo(findTestObject('Object Repository/Prod SmartDeceptive/Test Company Organization'))

WebUI.click(findTestObject('Object Repository/Prod SmartDeceptive/Test Company Organization'))

WebUI.delay(3)

WebUI.waitForPageLoad(10)
/*/




WebUI.navigateToUrl('https://platform.catchprobe.io/smartdeceptive/deception-operations/decoy-list')

WebUI.delay(5) 
WebUI.waitForPageLoad(10)

// Sap olan satırdaki IP linkine tıkla
TestObject sapIpLink = new TestObject()
sapIpLink.addProperty("xpath", ConditionType.EQUALS, "//td[span[normalize-space(text())='Sap']]/following-sibling::td[2]//a")
safeScrollTo(sapIpLink)
WebUI.click(sapIpLink)

// Yeni sekmeye geç
WebUI.switchToWindowIndex(1)

// Sekme başlığı kontrol et
String actualTitle = WebUI.getWindowTitle()
assert actualTitle.contains("User Management, SAP AG")  // buraya gerçek başlık yazılmalı
//Username e yaz
TestObject input5 = new TestObject()
input5.addProperty("xpath", ConditionType.EQUALS, "(//input)[5]")
WebUI.setText(input5, "e")

// 6. Passworda '1' yaz
TestObject input6 = new TestObject()
input6.addProperty("xpath", ConditionType.EQUALS, "(//input)[6]")
WebUI.setText(input6, "1")

// 7. Logona tıkla
TestObject input7 = new TestObject()
input7.addProperty("xpath", ConditionType.EQUALS, "(//input)[7]")
WebUI.click(input7)

// Sekmeyi kapat
WebUI.closeWindowTitle('User Management, SAP AG')


// İlk sekmeye geri dön
WebUI.switchToWindowIndex(0)


WebElement safeScrollTo(TestObject to) {
	if (to == null || !WebUI.waitForElementPresent(to, 2)) {
		KeywordUtil.markFailed("❌ Scroll edilemedi: ${to.getObjectId()}")
		return null
	}
	WebElement element = WebUiCommonHelper.findWebElement(to, 5)
	JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getWebDriver()
	js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element)
	WebUI.delay(0.5)
	return element
}

