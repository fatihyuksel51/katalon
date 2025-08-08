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

// Ldap sayfasına git
WebUI.navigateToUrl('https://platform.catchprobe.org/ldap')
WebUI.waitForPageLoad(10)

// 1️⃣ Create ldape tıkla
WebUI.click(findTestObject('Object Repository/Leakmap/Ldap/Create Ldap'))
WebUI.delay(1)

// 3️⃣ title alanına 'catchprobe' yaz
WebUI.setText(findTestObject('Object Repository/Leakmap/Ldap/Titleinput'), 'catchprobe')
WebUI.delay(0.5)

// 4️⃣ Dn Format alanına 'server ' yaz
WebUI.setText(findTestObject('Object Repository/Detail Scan/Search email'), 'cn={username},cn=Users,dc=cpad,dc=local')
WebUI.delay(0.5)

// 5 Server alanına 'server ' yaz
WebUI.setText(findTestObject('Object Repository/Leakmap/Ldap/Server İnput'), 'ldap://138.199.152.248')
WebUI.delay(0.5)

// 6 Keywordse  tıkla
WebUI.click(findTestObject('Object Repository/Leakmap/Ldap/Keywordsbuton'))
WebUI.delay(1)

// 7 Keywordse catchprobe seç
WebUI.click(findTestObject('Object Repository/Leakmap/Ldap/Catchprobeldap'))
WebUI.delay(1)

safeScrollTo(findTestObject('Object Repository/Leakmap/Ldap/Create'))
WebUI.click(findTestObject('Object Repository/Leakmap/Ldap/Create'))
CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()

// suuces mesajını doğrula
WebUI.waitForElementVisible(findTestObject('Object Repository/Leakmap/Ldap/Createsuccessfully'), 15)

WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Mitre Close'))

//Göz butonu ile kontrol et kapat
WebUI.click(findTestObject('Object Repository/Leakmap/Ldap/Eyebutton'))
CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()
WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Mitre Close'))

//Edit butonu ile statusu actieve et
WebUI.click(findTestObject('Object Repository/Leakmap/Ldap/Editbutonu'))
CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()
safeScrollTo(findTestObject('Object Repository/Leakmap/Ldap/Statusactive'))
WebUI.click(findTestObject('Object Repository/Leakmap/Ldap/Statusactive'))
WebUI.delay(1)
WebUI.sendKeys(findTestObject('Object Repository/Leakmap/Ldap/Statusactive'), 'a')
WebUI.click(findTestObject('Object Repository/Leakmap/Ldap/Statusactive'))

safeScrollTo(findTestObject('Object Repository/Leakmap/Ldap/Update'))
WebUI.click(findTestObject('Object Repository/Leakmap/Ldap/Update'))
CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()

// suuces mesajını doğrula
WebUI.waitForElementVisible(findTestObject('Object Repository/Leakmap/Ldap/Createsuccessfully'), 15)
WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Mitre Close'))

//Actieve yazısını doğrula
TestObject actieve = findTestObject('Object Repository/Leakmap/Ldap/Actievetext')
WebUI.waitForElementVisible(actieve, 10)
String actievetext = WebUI.getText(actieve)

// Sonuç doğrulama
assert actievetext.contains("Active")

//Ldapi sil
WebUI.waitForElementVisible(findTestObject('Object Repository/Leakmap/Ldap/Deletebutonu'), 15)
WebUI.click(findTestObject('Object Repository/Leakmap/Ldap/Deletebutonu'))
WebUI.waitForElementVisible(findTestObject('Object Repository/Leakmap/Ldap/Deleteok'), 15)
WebUI.click(findTestObject('Object Repository/Leakmap/Ldap/Deleteok'))

//delete mesajını doğrula
WebUI.waitForElementVisible(findTestObject('Object Repository/Leakmap/Ldap/Deletesucces'), 15)
WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Mitre Close'))














