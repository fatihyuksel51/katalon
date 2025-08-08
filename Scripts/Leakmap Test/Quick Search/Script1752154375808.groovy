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
	WebElement element = WebUiCommonHelper.findWebElement(to, 1)
	JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getWebDriver()
	js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element)
	WebUI.delay(0.5)
	return element
}


/*/ Başlangıç işlemleri
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
WebUI.delay(5)

String randomOtp = (100000 + new Random().nextInt(900000)).toString()
safeScrollTo(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'))
WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'), randomOtp)
safeScrollTo(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Verify'))
WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Verify'))
WebUI.delay(5)
WebUI.waitForPageLoad(30)
/*/
//
WebDriver driver = DriverFactory.getWebDriver()
JavascriptExecutor js = (JavascriptExecutor) driver

// Quick Search sayfasına git
WebUI.navigateToUrl('https://platform.catchprobe.org/leakmap/quick-search')
WebUI.waitForPageLoad(10)
safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/İnput'))
WebUI.setText(findTestObject('Object Repository/Leakmap/QuickSearch/İnput'), 'CEYHAN SALDANLI')
safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Button_Search'))
WebUI.click(findTestObject('Object Repository/Leakmap/QuickSearch/Button_Search'))
WebUI.delay(2)

TestObject seenIcon = findTestObject('Object Repository/Leakmap/QuickSearch/Icon_Seen_Tik')
boolean isSeen = WebUI.verifyElementPresent(seenIcon, 5, FailureHandling.OPTIONAL)

if (isSeen) {
	safeScrollTo(seenIcon)
safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Dropdown_ShowFilter'))
	WebUI.click(findTestObject('Object Repository/Leakmap/QuickSearch/Dropdown_ShowFilter'))
safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Dropdown_Option_Unseen'))
	WebUI.click(findTestObject('Object Repository/Leakmap/QuickSearch/Dropdown_Option_Unseen'))
safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Button_Search'))
	WebUI.click(findTestObject('Object Repository/Leakmap/QuickSearch/Button_Search'))
safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Text_NoResults'))
	WebUI.verifyElementPresent(findTestObject('Object Repository/Leakmap/QuickSearch/Text_NoResults'), 10)

safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Dropdown_ShowFilter'))
	WebUI.click(findTestObject('Object Repository/Leakmap/QuickSearch/Dropdown_ShowFilter'))
safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Dropdown_Option_Seen'))
	WebUI.click(findTestObject('Object Repository/Leakmap/QuickSearch/Dropdown_Option_Seen'))
safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Button_Search'))
	WebUI.click(findTestObject('Object Repository/Leakmap/QuickSearch/Button_Search'))
	CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()
safeScrollTo(seenIcon)
	WebUI.verifyElementPresent(seenIcon, 10)

	safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Button_Eye_Detail')).click()
	WebUI.delay(2)
	js.executeScript("document.body.style.zoom='0.6'")
	// AI Insight butonuna tıkla
	WebUI.click(findTestObject('Object Repository/Leakmap/Dashboard/AI INSIGHT'))
	
	// AI Insight içeriği bekle
	TestObject insightContent = new TestObject()
	insightContent.addProperty("xpath", ConditionType.EQUALS, "(//div[@data-radix-scroll-area-viewport])[4]")
	// ilgili sekmeyi kapat
	WebUI.waitForElementVisible(insightContent, 20)
	WebUI.click(findTestObject('Object Repository/Leakmap/QuickSearch/AiİnsightClose'))
	safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Button_UnseenInsidePopup'))
	WebUI.waitForElementVisible(findTestObject('Object Repository/Leakmap/QuickSearch/Button_UnseenInsidePopup'), 10)
	WebUI.click(findTestObject('Object Repository/Leakmap/QuickSearch/Button_UnseenInsidePopup'))
	WebUI.delay(1)
	
	safeScrollTo(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Mitre Close'))
	WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Mitre Close'))
	WebUI.delay(2)

	WebUI.verifyElementNotPresent(seenIcon, 5)

safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Dropdown_ShowFilter'))
	WebUI.click(findTestObject('Object Repository/Leakmap/QuickSearch/Dropdown_ShowFilter'))
safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Dropdown_Option_Seen'))
	WebUI.click(findTestObject('Object Repository/Leakmap/QuickSearch/Dropdown_Option_Seen'))
safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Button_Search'))
	WebUI.click(findTestObject('Object Repository/Leakmap/QuickSearch/Button_Search'))
	CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()
safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Text_NoResults'))
	WebUI.verifyElementPresent(findTestObject('Object Repository/Leakmap/QuickSearch/Text_NoResults'), 10)

safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Dropdown_ShowFilter'))
	WebUI.click(findTestObject('Object Repository/Leakmap/QuickSearch/Dropdown_ShowFilter'))
safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Dropdown_Option_Unseen'))
	WebUI.click(findTestObject('Object Repository/Leakmap/QuickSearch/Dropdown_Option_Unseen'))
safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Button_Search'))
	WebUI.click(findTestObject('Object Repository/Leakmap/QuickSearch/Button_Search'))
safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Button_Eye_Detail'))
	WebUI.verifyElementPresent(findTestObject('Object Repository/Leakmap/QuickSearch/Button_Eye_Detail'), 10)

} else {
	KeywordUtil.logInfo("🔍 Seen ikonu görünmedi, veri UNSEEN durumda başlıyor.")
safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Dropdown_ShowFilter'))
	WebUI.click(findTestObject('Object Repository/Leakmap/QuickSearch/Dropdown_ShowFilter'))
safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Dropdown_Option_Seen'))
	WebUI.click(findTestObject('Object Repository/Leakmap/QuickSearch/Dropdown_Option_Seen'))
safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Button_Search'))
	WebUI.click(findTestObject('Object Repository/Leakmap/QuickSearch/Button_Search'))
	CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()
safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Text_NoResults'))
	WebUI.verifyElementPresent(findTestObject('Object Repository/Leakmap/QuickSearch/Text_NoResults'), 10)

safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Dropdown_ShowFilter'))
	WebUI.click(findTestObject('Object Repository/Leakmap/QuickSearch/Dropdown_ShowFilter'))
safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Dropdown_Option_Unseen'))
	WebUI.click(findTestObject('Object Repository/Leakmap/QuickSearch/Dropdown_Option_Unseen'))
safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Button_Search'))
	WebUI.click(findTestObject('Object Repository/Leakmap/QuickSearch/Button_Search'))
	CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()
safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Button_Eye_Detail'))
	WebUI.verifyElementPresent(findTestObject('Object Repository/Leakmap/QuickSearch/Button_Eye_Detail'), 10)

	safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Button_Eye_Detail')).click()	
	WebUI.delay(3)
	js.executeScript("document.body.style.zoom='0.6'")
	// AI Insight butonuna tıkla
	WebUI.click(findTestObject('Object Repository/Leakmap/Dashboard/AI INSIGHT'))
	
	// AI Insight içeriği bekle
	TestObject insightContent = new TestObject()
	insightContent.addProperty("xpath", ConditionType.EQUALS, "(//div[@data-radix-scroll-area-viewport])[4]")
	
	//ilgili sekmeyei kapat
	WebUI.waitForElementVisible(insightContent, 20)
	WebUI.click(findTestObject('Object Repository/Leakmap/QuickSearch/AiİnsightClose'))
	WebUI.delay(2)
	safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Button_SeenInsidePopup'))
	WebUI.waitForElementVisible(findTestObject('Object Repository/Leakmap/QuickSearch/Button_SeenInsidePopup'), 10)
	WebUI.click(findTestObject('Object Repository/Leakmap/QuickSearch/Button_SeenInsidePopup'))
	WebUI.delay(2)
	safeScrollTo(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Mitre Close'))
	WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Mitre Close'))
	WebUI.delay(1)

	safeScrollTo(seenIcon)
	WebUI.verifyElementPresent(seenIcon, 5)

safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Dropdown_ShowFilter'))
	WebUI.click(findTestObject('Object Repository/Leakmap/QuickSearch/Dropdown_ShowFilter'))
safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Dropdown_Option_Unseen'))
	WebUI.click(findTestObject('Object Repository/Leakmap/QuickSearch/Dropdown_Option_Unseen'))
safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Button_Search'))
	WebUI.click(findTestObject('Object Repository/Leakmap/QuickSearch/Button_Search'))
	CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()
safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Text_NoResults'))
	WebUI.verifyElementPresent(findTestObject('Object Repository/Leakmap/QuickSearch/Text_NoResults'), 10)

safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Dropdown_ShowFilter'))
	WebUI.click(findTestObject('Object Repository/Leakmap/QuickSearch/Dropdown_ShowFilter'))
safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Dropdown_Option_Seen'))
	WebUI.click(findTestObject('Object Repository/Leakmap/QuickSearch/Dropdown_Option_Seen'))
safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Button_Search'))
	WebUI.click(findTestObject('Object Repository/Leakmap/QuickSearch/Button_Search'))
	CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()
safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Button_Eye_Detail'))
	WebUI.verifyElementPresent(findTestObject('Object Repository/Leakmap/QuickSearch/Button_Eye_Detail'), 10)
}

