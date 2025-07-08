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

// Tarayıcıyı aç ve siteye git
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

//
// Riskroute sekmesine tıkla
WebUI.navigateToUrl('https://platform.catchprobe.org/riskroute')

WebUI.waitForPageLoad(30)

CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()
WebUI.click(findTestObject('Object Repository/Asset Lİst/Page_/Organization Butonu'))

WebUI.click(findTestObject('Object Repository/Asset Lİst/Page_/Organization Seçimi'))

WebUI.delay(3)

WebUI.waitForPageLoad(30)


WebUI.click(findTestObject('Object Repository/Host List/Hosts'))

WebUI.delay(1)

WebUI.click(findTestObject('Object Repository/Product List/Product List'))

WebUI.waitForPageLoad(30)

WebDriver driver = DriverFactory.getWebDriver()
JavascriptExecutor js = (JavascriptExecutor) driver
Actions actions = new Actions(driver)

// Sayfa güncellenmesi için bekle
String taskValuekatalon = "katalon"

// 🔍 Arama input'u bul ve yazı gir
TestObject searchInputkatalon = new TestObject()
searchInputkatalon.addProperty("xpath", ConditionType.EQUALS, "//input[@placeholder='Target or Product']")

WebUI.setText(searchInputkatalon, taskValuekatalon)
WebUI.delay(1)

// 🔍 SEARCH butonuna tıkla
TestObject searchButtonkatalon = new TestObject()
searchButtonkatalon.addProperty("xpath", ConditionType.EQUALS, "//button[.//text()='SEARCH']")

WebUI.click(searchButtonkatalon)

// ⏳ Sayfanın yüklenmesini bekle
WebUI.delay(3)

// ✅ 'Records not found.' mesajını kontrol et
TestObject notFoundMsg = new TestObject()
notFoundMsg.addProperty("xpath", ConditionType.EQUALS, "//div[contains(@class,'justify-center') and text()='Records not found.']")

WebUI.waitForElementVisible(notFoundMsg, 10)
String notFoundText = WebUI.getText(notFoundMsg)

assert notFoundText.trim() == "Records not found." : "❌ Beklenen mesaj görünmedi: '${notFoundText}'"
WebUI.comment("✅ 'Records not found.' mesajı başarıyla doğrulandı.")

WebUI.delay(1)
WebUI.click(searchInputkatalon)
WebUI.clearText(searchInputkatalon)
WebUI.click(findTestObject('Object Repository/Product List/İnput Close'))
WebUI.delay(2)
WebUI.click(searchButtonkatalon)

WebUI.delay(2)

WebUI.waitForPageLoad(30)



// Target text'ini al
String targetText = js.executeScript("""
    return document.evaluate("//h3[.//strong[text()='Target:']]", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null)
        .singleNodeValue.textContent.trim();
""")

// "Target: " kısmını çıkar
String taskValue = targetText.replace("Target: ", "").trim()

// Konsola yaz
println("Bulunan Task Değeri: " + taskValue)

// Search input nesnesini oluştur
TestObject searchInput = new TestObject()
searchInput.addProperty("xpath", ConditionType.EQUALS, "//input[@placeholder='Target or Product']")

// Input'a taskValue'yi yaz
WebUI.setText(searchInput, taskValue)

// Search butonuna tıkla
TestObject searchButton = new TestObject()
searchButton.addProperty("xpath", ConditionType.EQUALS, "//button[.//text()='SEARCH']")

WebUI.click(searchButton)
WebUI.delay(2)

TestObject cpeElement = new TestObject()
cpeElement.addProperty("xpath", ConditionType.EQUALS, "//td[@class='flex w-1/4 items-center gap-2 px-6 py-4 text-sm']//div[contains(@class, 'inline-flex') and contains(@class, 'font-semibold') and contains(@class, 'cursor-pointer')]")

WebUI.click(cpeElement)
WebUI.delay(2)

TestObject cveElement = new TestObject()
cveElement.addProperty("xpath", ConditionType.EQUALS, "//a[contains(@href, '/riskroute/cve-list/')]//div[contains(@class, 'font-medium')]")

boolean isCvePresent = WebUI.verifyElementPresent(cveElement, 10, FailureHandling.OPTIONAL)

// Duruma göre aksiyon
if (isCvePresent) {
    WebUI.comment("✅ CVE kaydı bulundu")
} else {
    WebUI.comment("ℹ️ CVE kaydı bulunamadı")
    
 }

js.executeScript("document.body.style.zoom='0.8'")

WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Mitre Close'))





