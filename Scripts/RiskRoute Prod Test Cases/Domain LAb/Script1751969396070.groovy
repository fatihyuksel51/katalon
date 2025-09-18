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
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI




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

WebUI.navigateToUrl('https://platform.catchprobe.io/')

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

//
// Riskroute sekmesine tıkla
WebUI.navigateToUrl('https://platform.catchprobe.io/riskroute')

WebUI.waitForPageLoad(30)



WebUI.delay(3)

WebUI.waitForPageLoad(30)

WebUI.delay(1)

WebUI.navigateToUrl('https://platform.catchprobe.io/riskroute/quick-search/domain')
WebDriver driver = DriverFactory.getWebDriver()
JavascriptExecutor js = (JavascriptExecutor) driver
Actions actions = new Actions(driver)

// 📥 Arama yap (sen manuel yapacaksan bu kısmı çıkar)
WebUI.setText(findTestObject('Object Repository/Labs/input_SearchBox'), 'catchprobe.org')
WebUI.click(findTestObject('Object Repository/Labs/button_Scan'))
WebUI.delay(1)
WebUI.waitForElementVisible(findTestObject('Object Repository/Riskroute/Asset Lİst/Page_/Toast Message'), 15)

// ⏳ Sayfa yüklensin
WebUI.waitForPageLoad(30)
js.executeScript("document.body.style.zoom='0.8'")

// 🔁 Tüm component listesi
def components = [
    'Phishing Domain Lists',
    'Os Details',
    'Threatway Details',
    'Darkmap Details',
    'DNS Info',
    'WhoIs Record',
    'Subdomain Details',
    'Http Details',
    'Certificate Details',
    'Censys',
    'Netlas',
    'Zoomeye',
    'Network Data',
    'Vulnerability Detail',
    'BGP (Border Gateway Protocol)',
    'Ping Results',
    'Traceroute Details',
    'Smartdeceptive Details'
]
	
for (def component : components) {
    String componentKey = component.replaceAll(' ', '') // Örn: OSDetails
    KeywordUtil.logInfo("🧪 Test başlıyor: ${component}")

    // 🔽 Scroll et
    try {
        WebUI.scrollToElement(findTestObject("Object Repository/Labs/${componentKey}_Title"), 5)
    } catch (Exception e) {
        KeywordUtil.markWarning("⚠️ ${component} başlığına scroll edilemedi: ${e.message}")
    }

    // 📅 Completed At alanını oku
    def completedAtObj = findTestObject("Object Repository/Labs/${componentKey}_CompletedAt")
    WebUI.waitForElementPresent(completedAtObj, 10)
    String completedAtText = WebUI.getText(completedAtObj).trim()
    KeywordUtil.logInfo("${component} - Completed At: ${completedAtText}")
	
	// Retry mekanizması ile CompletedAt kontrolü
	int maxRetry = 5
	int retryCount = 0
	boolean stillInProgress = true
	
	while (retryCount < maxRetry && stillInProgress) {
		completedAtText = WebUI.getText(completedAtObj).trim()
		
		if (completedAtText.equalsIgnoreCase('In Progress')) {
			KeywordUtil.logInfo("⏳ ${component} hala taranıyor... (${retryCount + 1}/${maxRetry})")
			WebUI.delay(3)
			retryCount++
		} else {
			stillInProgress = false
		}
	}
	
	if (stillInProgress) {
		KeywordUtil.markWarning("❌ ${component} hala 'In Progress' olarak görünüyor, test bu haliyle devam ediyor.")
		
		TestObject scanningContinuesObj = new TestObject()
		scanningContinuesObj.addProperty("xpath", ConditionType.EQUALS,
			"(//div[contains(@class, 'text-primary') and contains(text(), 'Scanning continues')])[1]")
	
		WebUI.verifyElementPresent(scanningContinuesObj, 5)
		KeywordUtil.logInfo("✅ 'Scanning continues' mesajı bulundu.")
	} else {
		KeywordUtil.logInfo("✅ ${component} tamamlandı, veri kontrolü başlatılıyor...")
	
		TestObject dataNotFoundObj = new TestObject()
		dataNotFoundObj.addProperty("xpath", ConditionType.EQUALS,
			"//div[.//span[text()='${component}']]/div[contains(text(), 'Data not found')]")
	
		boolean isDataMissing = WebUI.verifyElementPresent(dataNotFoundObj, 3, FailureHandling.OPTIONAL)
	
		if (isDataMissing) {
			KeywordUtil.logInfo("📭 ${component} - 'Data not found' mesajı görüntülendi.")
		} else {
			KeywordUtil.logInfo("📊 ${component} - Veri mevcut, detaylar listeleniyor.")
		}
	}
	


    KeywordUtil.logInfo("✅ ${component} testi tamamlandı.\n------------------------------------")
}

// WebUI.closeBrowser() // İşin sonunda kapatmak istersen aç