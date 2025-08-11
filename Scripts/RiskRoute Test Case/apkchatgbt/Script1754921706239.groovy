import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
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
import com.kms.katalon.core.configuration.RunConfiguration
import java.nio.file.Files
import java.nio.file.Paths
import java.net.URL
import java.nio.file.StandardCopyOption
import org.openqa.selenium.*
import java.nio.file.*
import java.net.URL
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.driver.DriverFactory
import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.remote.LocalFileDetector
import org.openqa.selenium.chromium.ChromiumDriver


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

WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 'katalon.test@catchprobe.com')

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

// === TEST BAŞLANGICI ===
WebUI.navigateToUrl('https://platform.catchprobe.org/riskroute')
WebUI.waitForPageLoad(10)

WebUI.click(findTestObject('Object Repository/Riskroute/APK Analyzer/APK Analyzer'))
WebUI.delay(3)
WebUI.waitForPageLoad(30)

WebDriver driver = DriverFactory.getWebDriver()
JavascriptExecutor js = (JavascriptExecutor) driver
Actions actions = new Actions(driver)

// Delete butonu tanımı
TestObject deleteButton = new TestObject().addProperty("xpath",
	ConditionType.EQUALS, "//div[contains(@class, 'bg-destructive')]")

// Delete butonlarına tıkla
while (WebUI.verifyElementPresent(deleteButton, 3, FailureHandling.OPTIONAL)) {
	WebUI.comment("Delete butonu bulundu, tıklanıyor...")
	WebUI.click(deleteButton)
	CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()
	WebUI.delay(2)
	WebUI.click(findTestObject('Object Repository/Channel Management/Page_/button_DELETE'))
	CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()
	WebUI.waitForElementVisible(findTestObject('Object Repository/Riskroute/APK Analyzer/Toast'), 5)
}
WebUI.comment("Tüm delete butonları silindi. Asset List boş.")

// Create butonu tıkla
WebUI.click(findTestObject('Object Repository/Riskroute/APK Analyzer/Create butonu'))
WebUI.delay(2)
WebUI.waitForPageLoad(30)

// ✅ TestCloud uyumlu dosya indirme
// === Dosya yolu ayarları ===
String fileName = "bo.xapk"
String fileUrl  = "https://raw.githubusercontent.com/fatihyuksl/apk/main/" + fileName

// 1) TEMP'e indir (testin koştuğu taraf)
Path tmp = Paths.get(System.getProperty("java.io.tmpdir")).resolve(fileName)
Files.createDirectories(tmp.getParent())
Files.copy(new URL(fileUrl).openStream(), tmp, StandardCopyOption.REPLACE_EXISTING)
KeywordUtil.logInfo("📥 TEMP'e indirildi: " + tmp + " (" + Files.size(tmp) + " bayt)")

// 2) <input type="file"> görünür değilse görünür yap
WebUI.executeJavaScript("""
  var el = document.querySelector("input[type='file']");
  if (el && (getComputedStyle(el).display==='none' || 
             getComputedStyle(el).visibility==='hidden' ||
             el.offsetParent===null)) {
    el.style.display='block';
    el.style.visibility='visible';
    el.style.opacity='1';
    el.style.position='fixed';
    el.style.zIndex='2147483647';
    el.style.left='12px';
    el.style.top='12px';
    el.style.width='320px';
    el.style.height='36px';
  }
""", [])

// 3) Dinamik TestObject ile upload
TestObject fileInput = new TestObject('apkFileInput')
fileInput.addProperty('xpath', ConditionType.EQUALS, "//input[@type='file']")
WebUI.waitForElementPresent(fileInput, 15)

// 3-a) Uzak sürücüde çalışıyorsak dosya transferini aç
WebDriver wd = DriverFactory.getWebDriver()
try {
  if (wd instanceof RemoteWebDriver && !(wd instanceof ChromiumDriver)) {
    ((RemoteWebDriver) wd).setFileDetector(new LocalFileDetector())
    KeywordUtil.logInfo("🌐 RemoteWebDriver: LocalFileDetector etkin.")
  } else {
    KeywordUtil.logInfo("💻 Local/ChromiumDriver: FileDetector ayarı gereksiz, atlandı.")
  }
} catch (Throwable t) {
  KeywordUtil.logInfo("⚠ FileDetector set edilemedi (önemsiz): " + t.getMessage())
}

// 4) Yüklemeyi gönder
WebUI.uploadFile(fileInput, tmp.toString())
KeywordUtil.logInfo("📤 Upload tetiklendi: " + tmp)

// (Opsiyonel) kısa bekleme ve ekranda dosya adını kontrol etmeye çalış
WebUI.delay(2)

// CREATE butonu
WebUI.click(findTestObject('Object Repository/Riskroute/APK Analyzer/button_CREATE'))
WebUI.delay(2)
WebUI.waitForPageLoad(30)

// ✅ Dosya adı kontrol
String expectedFileName = "bo.xapk"
TestObject fileNameCell = new TestObject()
fileNameCell.addProperty("xpath", ConditionType.EQUALS,
	"//span[contains(@class, 'ant-table-cell') and text()='" + expectedFileName + "']")
WebUI.waitForElementVisible(fileNameCell, 10)
String actualFileName = WebUI.getText(fileNameCell)
assert actualFileName == expectedFileName : "❌ Dosya adı eşleşmiyor: " + actualFileName

// ✅ Status kontrol
int retryCount = 0
int maxRetries = 4
boolean isSuccess = false
while (retryCount < maxRetries) {
	TestObject statusCell = new TestObject()
	statusCell.addProperty("xpath", ConditionType.EQUALS,
		"//td[contains(@class, 'ant-table-cell')]//span[text()='success' or text()='pending']")
	WebUI.waitForElementVisible(statusCell, 10)
	String statusText = WebUI.getText(statusCell).trim().toUpperCase()
	println "🔍 Deneme ${retryCount + 1}: Durum -> " + statusText
	if (statusText == "SUCCESS") {
		isSuccess = true
		break
	} else {
		WebUI.refresh()
		WebUI.waitForPageLoad(30)
		WebUI.delay(5)
		retryCount++
	}
}
assert isSuccess : "❌ Status 'SUCCESS' olmadı."

// Go Detail
TestObject goDetailButton = new TestObject()
goDetailButton.addProperty("xpath", ConditionType.EQUALS,
	"(//div[contains(@class, '50 bg-primary')]/*[name()='svg'])[1]")
WebUI.waitForElementClickable(goDetailButton, 10)
WebUI.click(goDetailButton)
WebUI.delay(2)
WebUI.waitForPageLoad(30)

// Circle kontrol
TestObject circle = findTestObject('Object Repository/Riskroute/APK Analyzer/Stix Circle')
if (WebUI.waitForElementVisible(circle, 10)) {
	WebElement circleelement = WebUI.findWebElement(circle, 10)
	Boolean circleExistsRisk = WebUI.executeJavaScript(
		"return arguments[0].querySelector('circle') != null;",
		Arrays.asList(circleelement)
	)
	KeywordUtil.logInfo("Stix Package Circle var mı? : " + circleExistsRisk)
	if (circleExistsRisk) {
		KeywordUtil.logInfo("Stix Package Circle Veri VAR ✅")
	} else {
		KeywordUtil.logInfo("Stix Package Circle Veri YOK 🚨")
	}
} else {
	KeywordUtil.logInfo("Stix Package Circle elementi görünmedi ⏰")
}

// Tekrar listeye dön
WebUI.click(findTestObject('Object Repository/Riskroute/APK Analyzer/APK Analyzer'))
WebUI.delay(1)
WebUI.waitForPageLoad(30)

// Delete butonları tekrar temizle
while (WebUI.verifyElementPresent(deleteButton, 3, FailureHandling.OPTIONAL)) {
	WebUI.comment("Delete butonu bulundu, tıklanıyor...")
	WebUI.click(deleteButton)
	CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()
	WebUI.delay(2)
	WebUI.click(findTestObject('Object Repository/Channel Management/Page_/button_DELETE'))
	CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()
	WebUI.waitForElementVisible(findTestObject('Object Repository/Riskroute/APK Analyzer/Toast'), 5)
}
WebUI.comment("Tüm delete butonları silindi. Asset List boş.")