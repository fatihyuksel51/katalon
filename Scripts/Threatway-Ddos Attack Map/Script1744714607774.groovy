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

WebUI.openBrowser('')

WebUI.navigateToUrl('https://platform.catchprobe.org/')

WebUI.maximizeWindow()

WebUI.click(findTestObject('Object Repository/otp/Page_/Page_/a_PLATFORM LOGIN'))

WebUI.setText(findTestObject('Object Repository/otp/Page_/Page_/input_Email Address_email'), 'fatih.yuksel@catchprobe.com')

WebUI.setEncryptedText(findTestObject('Object Repository/otp/Page_/Page_/input_Password_password'), 'RigbBhfdqOBDK95asqKeHw==')

WebUI.click(findTestObject('Object Repository/otp/Page_/Page_/button_Sign in'))

WebUI.click(findTestObject('Object Repository/otp/Page_/Page_/svg_G_lucide lucide-webhook h-6 w-6'))

WebUI.click(findTestObject('otp/Page_/Page_/svg_DDOS_lucide lucide-chevron-right h-4 w-_2b2afb'))

WebUI.click(findTestObject('Object Repository/otp/Page_/Page_/div_DDOS Attack Map'))

WebUI.click(findTestObject('Object Repository/otp/Page_/Page_/iframe_DDOS Attack Map_fullScreenThreatwayA_fe63ad'))

TestObject iframeObj = new TestObject('iframe')

iframeObj.addProperty('id', ConditionType.EQUALS, 'fullScreenThreatwayAttackMap')

WebUI.switchToFrame(iframeObj, 10)

TestObject scrollableDiv = new TestObject('scrollableInsideIframe')

scrollableDiv.addProperty('xpath', ConditionType.EQUALS, '//div[@class=\'col-md-12 live-attacks-timestamp\']' // örnek
    )

WebUI.executeJavaScript('arguments[0].scrollTop = 300', Arrays.asList(WebUI.findWebElement(scrollableDiv)))

String time1 = WebUI.getText(findTestObject('Object Repository/otp/Page_/ddos_source'))

println('faaa' + time1)

// Bekle (örneğin 5 saniye)
WebUI.delay(10)

// İkinci Time bilgisini al
String time2 = WebUI.getText(findTestObject('Object Repository/otp/Page_/ddos_source'))

println('faaa' + time2)

// Zaman değişti mi kontrol et
if (time1 != time2) {
    WebUI.comment("✅ Canlı akış aktif. Time değişti: $time1 → $time2")
} else {
    WebUI.comment("⚠️ Uyarı! Time aynı kaldı: $time1")

    WebUI.takeScreenshot()

    assert false : 'Canlı akış algılandı → test senaryosu başarısız olmalı!'
}

WebUI.switchToDefaultContent()

WebUI.closeBrowser()

