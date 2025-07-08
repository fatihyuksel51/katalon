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



/*// Tarayıcıyı aç ve siteye git
WebUI.openBrowser('')
WebUI.navigateToUrl('https://platform.catchprobe.org/')
WebUI.maximizeWindow()

// Login işlemleri
WebUI.waitForElementVisible(findTestObject('Object Repository/otp/Page_/a_PLATFORM LOGIN'), 30)
WebUI.click(findTestObject('Object Repository/otp/Page_/a_PLATFORM LOGIN'))
WebUI.waitForElementVisible(findTestObject('Object Repository/otp/Page_/input_Email Address_email'), 30)
WebUI.setText(findTestObject('Object Repository/otp/Page_/input_Email Address_email'), 'fatih.yuksel@catchprobe.com')
WebUI.setEncryptedText(findTestObject('Object Repository/otp/Page_/input_Password_password'), 'RigbBhfdqOBDK95asqKeHw==')
WebUI.click(findTestObject('Object Repository/otp/Page_/button_Sign in'))
WebUI.delay(3)

// OTP işlemi
def randomOtp = (100000 + new Random().nextInt(900000)).toString()
WebUI.setText(findTestObject('Object Repository/otp/Page_/input_OTP Digit_vi_1_2_3_4_5'), randomOtp)
WebUI.click(findTestObject('Object Repository/otp/Page_/button_Verify'))
CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()
/*/ 
//Threatway sekmesine tıkla
// Threatway sekmesine git
WebUI.navigateToUrl('https://platform.catchprobe.org/threatway')
WebUI.waitForPageLoad(30)
WebUI.delay(5)


WebUI.navigateToUrl('https://platform.catchprobe.org/threatway/ddos/attack-map')

// Iframe'i tanımla
TestObject iframeObj = new TestObject('iframe')
iframeObj.addProperty('id', ConditionType.EQUALS, 'fullScreenThreatwayAttackMap')

// Iframe'e geçiş yap
WebUI.waitForElementVisible(iframeObj, 10)
WebUI.switchToFrame(iframeObj, 10)

// Scroll yapılacak alanı tanımla
TestObject scrollableDiv = new TestObject('scrollableInsideIframe')
scrollableDiv.addProperty('xpath', ConditionType.EQUALS, "//div[@class='col-md-12 live-attacks-timestamp']")

int maxAttempts = 3
int attempt = 1
boolean elementFound = false

while (attempt <= maxAttempts) {
    if (WebUI.verifyElementPresent(scrollableDiv, 10, FailureHandling.OPTIONAL)) {
        elementFound = true
        break
    } else {
        WebUI.comment("❌ Element bulunamadı. Sayfa yenileniyor... Deneme: $attempt")
        WebUI.switchToDefaultContent() // iframe’den çık
        WebUI.refresh()
        WebUI.waitForPageLoad(10)
        WebUI.waitForElementVisible(iframeObj, 10)
        WebUI.switchToFrame(iframeObj, 10)
    }
    attempt++
}

if (!elementFound) {
    WebUI.comment("❌ Element $maxAttempts denemede bulunamadı. Test durduruluyor.")
    WebUI.takeScreenshot()
    assert false : "Element bulunamadı."
}

// Bulunduysa devam et
WebUI.comment("✅ Element bulundu, işleme devam ediliyor.")
WebUI.executeJavaScript('arguments[0].scrollTop = 300', Arrays.asList(WebUI.findWebElement(scrollableDiv)))
WebUI.waitForElementVisible(findTestObject('Object Repository/otp/Page_/ddos_source'), 15)

String time1 = WebUI.getText(findTestObject('Object Repository/otp/Page_/ddos_source'))
println(time1)
WebUI.delay(10)
String time2 = WebUI.getText(findTestObject('Object Repository/otp/Page_/ddos_source'))
println(time2)

if (time1 != time2) {
    WebUI.comment("✅ Canlı akış aktif. Time değişti: $time1 → $time2")
} else {
    WebUI.comment("⚠️ Uyarı! Time aynı kaldı: $time1")
    WebUI.takeScreenshot()
    assert false : 'Canlı akış algılandı → test senaryosu başarısız olmalı!'
}

WebUI.switchToDefaultContent()