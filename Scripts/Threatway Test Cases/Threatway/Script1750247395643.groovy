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
import com.kms.katalon.core.testobject.ConditionType as ConditionType
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.webui.driver.DriverFactory
import org.openqa.selenium.JavascriptExecutor
WebUI.openBrowser('')

// IP adresini logla (opsiyonel)
try {
    def agentIp = new URL("https://ifconfig.me/ip").openStream().getText().trim()
    println "👉 TestOps Agent IP Adresi: " + agentIp
} catch(Exception e) {
    println "❌ IP alınamadı: " + e.getMessage()
}

// URL'ye git
WebUI.navigateToUrl('https://platform.catchprobe.org/')
JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getWebDriver()
js.executeScript("window.scrollTo(0, document.body.scrollHeight)")
WebUI.delay(5)


// Headless kontrolü
def driverPrefs = RunConfiguration.getDriverPreferencesProperties()
def argsList = driverPrefs?.get("args")?.toString() ?: ""

if (argsList.contains("headless")) {
    WebUI.setViewPortSize(1920, 1080)
    WebUI.comment("👉 Headless modda: viewport set edildi.")
} else {
    WebUI.maximizeWindow()
    WebUI.comment("👉 Normal modda: pencere maximize edildi.")
}
/*/
 Login adımları
WebUI.waitForElementClickable(findTestObject('Object Repository/otp/Page_/a_PLATFORM LOGIN'), 40)
WebUI.click(findTestObject('Object Repository/otp/Page_/a_PLATFORM LOGIN'))

WebUI.waitForElementVisible(findTestObject('Object Repository/otp/Page_/input_Email Address_email'), 50)
WebUI.setText(findTestObject('Object Repository/otp/Page_/input_Email Address_email'), 'katalon.test@catchprobe.com')

WebUI.waitForElementVisible(findTestObject('Object Repository/otp/Page_/input_Password_password'), 20)
WebUI.setEncryptedText(findTestObject('Object Repository/otp/Page_/input_Password_password'), 'RigbBhfdqOBDK95asqKeHw==')

WebUI.waitForElementClickable(findTestObject('Object Repository/otp/Page_/button_Sign in'), 20)
WebUI.click(findTestObject('Object Repository/otp/Page_/button_Sign in'))

// OTP ekranına geçiş için bekle
WebUI.waitForElementVisible(findTestObject('otp/Page_/input_OTP Digit_vi_1_2_3_4_5'), 30)
def randomOtp = (100000 + new Random().nextInt(900000)).toString()

WebUI.setText(findTestObject('otp/Page_/input_OTP Digit_vi_1_2_3_4_5'), randomOtp)
WebUI.click(findTestObject('otp/Page_/button_Verify'))
//

// Login sonrası ana ekran elementini bekle
if (WebUI.waitForElementVisible(findTestObject('Object Repository/otp/Page_/svg_G_lucide lucide-webhook h-6 w-6'), 30)) {
    WebUI.comment("✅ Login başarılı.")
} else {
    WebUI.comment("❌ Login başarısız.")
    WebUI.takeScreenshot()
    WebUI.closeBrowser()
    assert false : "Login sonrası element bulunamadı!"
}

WebUI.delay(3)
WebUI.navigateToUrl('https://platform.catchprobe.org/threatway/ddos/attack-map')

// iframe’e tıklamak yerine doğrudan geçiş
TestObject iframeObj = new TestObject('iframe')
iframeObj.addProperty('id', ConditionType.EQUALS, 'fullScreenThreatwayAttackMap')

WebUI.waitForElementVisible(iframeObj, 20)
WebUI.switchToFrame(iframeObj, 10)

// Scroll ve time okuma
TestObject scrollableDiv = new TestObject('scrollableInsideIframe')
scrollableDiv.addProperty('xpath', ConditionType.EQUALS, "//div[@class='col-md-12 live-attacks-timestamp']")

WebUI.executeJavaScript('arguments[0].scrollTop = 300', Arrays.asList(WebUI.findWebElement(scrollableDiv)))
WebUI.waitForElementVisible(findTestObject('Object Repository/otp/Page_/ddos_source'), 15)

String time1 = WebUI.getText(findTestObject('Object Repository/otp/Page_/ddos_source'))
println "⏰ İlk zaman: $time1"

WebUI.delay(10)
String time2 = WebUI.getText(findTestObject('Object Repository/otp/Page_/ddos_source'))
println "⏰ İkinci zaman: $time2"

// Zaman kontrolü
if (time1 != time2) {
    WebUI.comment("✅ Canlı akış aktif. Zaman değişti: $time1 → $time2")
} else {
    WebUI.comment("⚠️ Uyarı! Zaman değişmedi: $time1")
    WebUI.takeScreenshot()
    assert false : "Canlı akış durdu: zaman değişmedi!"
}

WebUI.switchToDefaultContent()
WebUI.closeBrowser()
/*/

