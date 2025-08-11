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

import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.Arrays
import java.util.Random

// ----------------- Yardımcı: görünürlüğe scroll -----------------
def scrollToVisible(WebElement element, JavascriptExecutor js) {
	int currentScroll = 0
	boolean isVisible = false
	while (!isVisible && currentScroll < 3000) {
		js.executeScript("window.scrollBy(0, 200)")
		WebUI.delay(0.5)
		try { isVisible = element.isDisplayed() } catch (ignored) {}
		currentScroll += 200
	}
	return isVisible
}

/*/ ----------------- Başlangıç / Login -----------------
WebUI.openBrowser('')
WebUI.navigateToUrl('https://platform.catchprobe.org/')
WebUI.maximizeWindow()

WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'), 30)
WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'))

WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 30)
WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 'katalon.test@catchprobe.com')
WebUI.setEncryptedText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Password_password'), 'RigbBhfdqOBDK95asqKeHw==')
WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Sign in'))
WebUI.delay(5)

// OTP (dummy)
def randomOtp = (100000 + new Random().nextInt(900000)).toString()
WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'), randomOtp)
WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Verify'))
WebUI.delay(5)
WebUI.waitForPageLoad(30)
/*/

// ----------------- APK Analyzer sayfası -----------------
WebUI.navigateToUrl('https://platform.catchprobe.org/riskroute')
WebUI.waitForPageLoad(20)

WebUI.click(findTestObject('Object Repository/Riskroute/APK Analyzer/APK Analyzer'))
WebUI.delay(3)
WebUI.waitForPageLoad(30)

WebDriver driver = DriverFactory.getWebDriver()
JavascriptExecutor js = (JavascriptExecutor) driver
Actions actions = new Actions(driver)

// ----------------- Varsa eski kayıtları temizle -----------------
TestObject deleteButton = new TestObject('apkDeleteBtn')
deleteButton.addProperty('xpath', ConditionType.EQUALS, "//div[contains(@class,'bg-destructive') or contains(@class,'bg-danger')]")

int guard = 0
while (WebUI.verifyElementPresent(deleteButton, 2, FailureHandling.OPTIONAL) && guard < 20) {
	WebUI.comment("🗑 Delete butonu bulundu, tıklanıyor...")
	WebUI.click(deleteButton)
	try { CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'() } catch (ignored) {}
	WebUI.delay(1)
	if (WebUI.verifyElementPresent(findTestObject('Object Repository/Channel Management/Page_/button_DELETE'), 5, FailureHandling.OPTIONAL)) {
		WebUI.click(findTestObject('Object Repository/Channel Management/Page_/button_DELETE'))
	}
	try { CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'() } catch (ignored) {}
	WebUI.delay(1)
	guard++
}
WebUI.comment("✅ Asset List temiz görünüyor")

// ----------------- Create ekranını aç -----------------
WebUI.click(findTestObject('Object Repository/Riskroute/APK Analyzer/Create butonu'))
WebUI.delay(1)
WebUI.waitForPageLoad(30)

// ----------------- GitHub -> TEMP'e indir -----------------
// Dosya adı ve yolu
String fileName = "bo.xapk"
String fileUrl = "https://raw.githubusercontent.com/fatihyuksl/apk/main/" + fileName

// Temp klasörüne indir
import java.nio.file.*
Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"))
Path destPath = tempDir.resolve(fileName)
Files.createDirectories(tempDir)

KeywordUtil.logInfo("📥 TEMP'e indiriliyor: " + destPath.toString())
Files.copy(new URL(fileUrl).openStream(), destPath, StandardCopyOption.REPLACE_EXISTING)
KeywordUtil.logInfo("✅ İndirme OK, boyut: " + Files.size(destPath) + " bayt")

// File input görünür yap
WebUI.executeJavaScript("""
  var el = document.evaluate("//input[@type='file' and not(@disabled)]", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;
  if (el) {
    el.style.display='block';
    el.style.visibility='visible';
    el.style.opacity='1';
    el.style.position='fixed';
    el.style.zIndex='99999';
    el.style.left='0px';
    el.style.top='0px';
    el.style.width='200px';
    el.style.height='30px';
  }
""", [])

// TestObject oluştur
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ConditionType
TestObject fileInput = new TestObject('dynamicFileInput')
fileInput.addProperty('xpath', ConditionType.EQUALS, "//input[@type='file' and not(@disabled)]")

// Yükleme işlemi
WebUI.waitForElementPresent(fileInput, 10)
WebUI.uploadFile(fileInput, destPath.toString())
KeywordUtil.logInfo("📤 Upload tetiklendi: " + destPath.toString())

// CREATE butonuna tıkla
TestObject createBtn = findTestObject('Object Repository/Riskroute/APK Analyzer/button_CREATE')
WebUI.waitForElementClickable(createBtn, 10)
WebUI.click(createBtn)
KeywordUtil.logInfo("✅ CREATE butonuna basıldı")
WebUI.delay(2)
WebUI.waitForPageLoad(30)



// ----------------- Dosya adı doğrulama -----------------
TestObject fileNameCell = new TestObject('fileNameCell')
fileNameCell.addProperty('xpath', ConditionType.EQUALS, "//td[normalize-space(.)='" + fileName + "']")
WebUI.waitForElementVisible(fileNameCell, 15)
String actualFileName = WebUI.getText(fileNameCell).trim()
assert actualFileName == fileName : "❌ Dosya adı eşleşmiyor: " + actualFileName

// ----------------- Status = SUCCESS olana kadar bekle (yenilemeli) -----------------
int retryCount = 0
int maxRetries = 6
boolean isSuccess = false

while (retryCount < maxRetries) {
	TestObject statusCell = new TestObject('statusCell')
	statusCell.addProperty('xpath', ConditionType.EQUALS,
		"//tr[.//td[normalize-space(.)='" + fileName + "']]//span[normalize-space(text())='success' or normalize-space(text())='pending' or contains(@class,'success') or contains(@class,'pending')]"
	)

	if (WebUI.waitForElementVisible(statusCell, 10, FailureHandling.OPTIONAL)) {
		String statusText = WebUI.getText(statusCell).trim().toUpperCase()
		KeywordUtil.logInfo("🔍 Deneme ${retryCount + 1}: Status -> " + statusText)
		if (statusText.contains("SUCCESS")) {
			isSuccess = true
			break
		}
	}

	WebUI.refresh()
	WebUI.waitForPageLoad(30)
	WebUI.delay(5)
	retryCount++
}

assert isSuccess : "❌ Status 'SUCCESS' olmadı."

// ----------------- Go Detail -----------------
TestObject goDetailButton = new TestObject('goDetail')
goDetailButton.addProperty('xpath', ConditionType.EQUALS, "(//div[contains(@class,'bg-primary')]/*[name()='svg' or local-name()='svg'])[1]")
WebUI.waitForElementClickable(goDetailButton, 15)
WebUI.click(goDetailButton)
WebUI.delay(2)
WebUI.waitForPageLoad(30)

// ----------------- Circle kontrol -----------------
TestObject circle = findTestObject('Object Repository/Riskroute/APK Analyzer/Stix Circle')
if (WebUI.waitForElementVisible(circle, 10)) {
	WebElement circleelement = WebUI.findWebElement(circle, 10)
	Boolean circleExistsRisk = WebUI.executeJavaScript(
		"return arguments[0].querySelector('circle') != null;",
		Arrays.asList(circleelement)
	)
	KeywordUtil.logInfo("Stix Package Circle var mı? : " + circleExistsRisk)
	if (Boolean.TRUE.equals(circleExistsRisk)) {
		KeywordUtil.logInfo("Stix Package Circle Veri VAR ✅")
	} else {
		KeywordUtil.logInfo("Stix Package Circle Veri YOK 🚨")
	}
} else {
	KeywordUtil.logInfo("Stix Package Circle elementi görünmedi ⏰")
}

// ----------------- Listeye dön -----------------
WebUI.click(findTestObject('Object Repository/Riskroute/APK Analyzer/APK Analyzer'))
WebUI.delay(1)
WebUI.waitForPageLoad(30)

// ----------------- Kayıtları tekrar temizle (idempotent) -----------------
guard = 0
while (WebUI.verifyElementPresent(deleteButton, 2, FailureHandling.OPTIONAL) && guard < 20) {
	WebUI.comment("🗑 Delete butonu bulundu, tıklanıyor...")
	WebUI.click(deleteButton)
	try { CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'() } catch (ignored) {}
	WebUI.delay(1)
	if (WebUI.verifyElementPresent(findTestObject('Object Repository/Channel Management/Page_/button_DELETE'), 5, FailureHandling.OPTIONAL)) {
		WebUI.click(findTestObject('Object Repository/Channel Management/Page_/button_DELETE'))
	}
	try { CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'() } catch (ignored) {}
	WebUI.delay(1)
	guard++
}
WebUI.comment("✅ Tüm delete butonları temizlendi. Asset List boş.")
