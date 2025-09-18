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
import java.text.SimpleDateFormat
import java.util.Date
import com.kms.katalon.core.testobject.ObjectRepository as OR



TestObject X(String xp){
	def to=new TestObject(xp)
	to.addProperty("xpath",ConditionType.EQUALS,xp)
	return to
  }
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
boolean isBrowserOpen(){
	try { DriverFactory.getWebDriver(); return true } catch(Throwable t){ return false }
  }
  
/************** Oturum **************/
void ensureSession(){
  if(isBrowserOpen()) return

  WebUI.openBrowser('')
  WebUI.maximizeWindow()
  WebUI.navigateToUrl('https://platform.catchprobe.io/')

  WebUI.waitForElementVisible(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'), 30)
  WebUI.click(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'))

  WebUI.waitForElementVisible(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 30)
    WebUI.setText(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 'fatih@test.com')
    WebUI.setEncryptedText(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Password_password'), 'v4yvAQ7Q279BF5ny4hDiTA==')
  WebUI.click(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Sign in'))

  WebUI.delay(3)
  String otp = (100000 + new Random().nextInt(900000)).toString()
  WebUI.setText(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'), otp)
  WebUI.click(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Verify'))
  WebUI.delay(2)
  String Threat = "//span[text()='Threat']"
  WebUI.waitForElementVisible(X("//span[text()='Threat']"), 10, FailureHandling.OPTIONAL)
}

/************** TEST: Collections **************/
ensureSession()

// Riskroute sekmesine tıkla
WebUI.navigateToUrl('https://platform.catchprobe.io/riskroute')

WebUI.waitForPageLoad(30)

CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()
//WebUI.click(findTestObject('Object Repository/Riskroute/Asset Lİst/Page_/Organization Butonu'))

//WebUI.click(findTestObject('Object Repository/Riskroute/Asset Lİst/Page_/Organization Seçimi'))

WebUI.delay(3)

WebUI.waitForPageLoad(30)

WebUI.delay(1)

WebUI.navigateToUrl('https://platform.catchprobe.io/riskroute/quick-search/domain')
WebDriver driver = DriverFactory.getWebDriver()
// JavaScript Executor
JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getWebDriver()


// 📥 Arama yap (sen manuel yapacaksan bu kısmı çıkar)
WebUI.setText(findTestObject('Object Repository/Labs/input_SearchBox'), 'catchprobe.org')
WebUI.click(findTestObject('Object Repository/Labs/button_Scan'))
WebUI.delay(1)
WebUI.waitForElementVisible(findTestObject('Object Repository/Riskroute/Asset Lİst/Page_/Toast Message'), 15)

// ⏳ Sayfa yüklensin
WebUI.waitForPageLoad(30)


// 📌 Tüm component listesi
def components = [
    'Phishing Domain Lists',
    'OS Intelligence',
    'Threatway Intelligence',
    'Darkmap Intelligence',
    'DNS Intelligence',
    'WhoIs Intelligence',
    'Subdomain Intelligence',
    'Http Analysis',
    'Certificate Analysis', 
	'Network Intelligence',
    'Netlas',
    'Content Intelligence',    
	'Service Fingerprinting',
    'Vulnerability Intelligence',
    'BGP (Border Gateway Protocol)',
    'Ping Results',
    'Traceroute Intelligence',
    'Smartdeceptive Intelligence'    
]

for (int i = 0; i < components.size(); i++) {
    def component = components[i]
    String componentKey = component.replaceAll(' ', '')
    KeywordUtil.logInfo("🧪 Test başlıyor: ${component}")

    // 📌 Title scroll ve kontrol
    TestObject titleObj = findTestObject("Object Repository/Labs/${componentKey}_Title")
    if (WebUI.verifyElementPresent(titleObj, 5, FailureHandling.OPTIONAL)) {
        WebElement titleEl = WebUiCommonHelper.findWebElement(titleObj, 5)
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", titleEl)
        WebUI.delay(1)
    } else {
        KeywordUtil.markFailed("❌ ${component} için başlık bulunamadı, test durduruluyor.")
    }

    // 📅 Completed At
    TestObject completedAtObj = findTestObject("Object Repository/Labs/${componentKey}_CompletedAt")
    if (!WebUI.verifyElementPresent(completedAtObj, 5, FailureHandling.OPTIONAL)) {
        KeywordUtil.markFailed("❌ ${component} için Completed At bulunamadı.")
    }

    String completedAtText = WebUI.getText(completedAtObj).trim()
    KeywordUtil.logInfo("${component} - Completed At: ${completedAtText}")

    // ⏳ In Progress ise ilgili sıradaki Scanning continues mesajını kontrol et
    if (completedAtText.equalsIgnoreCase('In Progress')) {
    KeywordUtil.logInfo("⏳ ${component} In Progress — hızlı şekilde 'Scanning continues' mesajı aranıyor...")

    // ✅ component adı geçen parent içinde scanning continues ara (en doğru ve sağlam yöntem)
    TestObject scanningContinuesObj = new TestObject()
    scanningContinuesObj.addProperty("xpath", ConditionType.EQUALS,
        "//div[.//span[text()='${component}']]//div[contains(text(), 'Scanning continues')]")

    	if (WebUI.verifyElementPresent(scanningContinuesObj, 2, FailureHandling.OPTIONAL)) {
        WebElement scanEl = WebUiCommonHelper.findWebElement(scanningContinuesObj, 2)
        
        // ⏱️ Çok kısa delay ve hızlı scroll
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", scanEl)
        WebUI.delay(0.5)

        KeywordUtil.logInfo("✅ ${component} için 'Scanning continues' mesajı bulundu ve scroll edildi.")
		} else {
        KeywordUtil.markWarning("❌ ${component} için 'Scanning continues' mesajı bulunamadı!")
		}
    } else {
        // ✅ Tamamlandıysa Data not found kontrolü
        KeywordUtil.logInfo("✅ ${component} tamamlandı, veri kontrolü başlatılıyor...")

        TestObject dataNotFoundObj = new TestObject()
        dataNotFoundObj.addProperty("xpath", ConditionType.EQUALS,
            "//div[.//span[text()='${component}']]/div[contains(text(), 'Data not found')]")

        boolean isDataMissing = WebUI.verifyElementPresent(dataNotFoundObj, 2, FailureHandling.OPTIONAL)

        if (isDataMissing) {
            KeywordUtil.logInfo("📭 ${component} - 'Data not found' mesajı görüntülendi.")
        } else {
            KeywordUtil.logInfo("📊 ${component} - Veri mevcut, detaylar listeleniyor.")
        }
    }

    KeywordUtil.logInfo("✅ ${component} testi tamamlandı.\n------------------------------------")
}

// WebUI.closeBrowser() // İşin sonunda kapatmak istersen aç