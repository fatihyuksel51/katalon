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

WebUI.openBrowser('')

WebUI.navigateToUrl('https://platform.catchprobe.org/')

WebUI.click(findTestObject('null'))

WebUI.setText(findTestObject('null'), 'fatih.yuksel@catchprobe.com')

WebUI.setEncryptedText(findTestObject('null'), 'RigbBhfdqOBDK95asqKeHw==')

WebUI.sendKeys(findTestObject('null'), Keys.chord(Keys.ENTER))

WebUI.click(findTestObject('null'))

WebUI.click(findTestObject('null'))

WebUI.verifyElementText(findTestObject('null'), 'Dashboard')

WebUI.click(findTestObject('null'))

WebUI.click(findTestObject('null'))

WebUI.click(findTestObject('null'))

TestObject iframeObj = findTestObject('null')

WebUI.scrollToElement(iframeObj, 5)
WebUI.executeJavaScript("document.getElementById('fullScreenThreatwayAttackMap').scrollTop = 438;", null)




WebUI.delay(5)

// 300 px aşağı kaydırır
String time1 = WebUI.getText(findTestObject('Object Repository/hafdii/Page_/ddos_source'))

window.print(time1 + 'fattttatatatta')

// Bekle (örneğin 5 saniye)
WebUI.delay(15)

// İkinci Time bilgisini al
String time2 = WebUI.getText(findTestObject('Object Repository/hafdii/Page_/ddos_source'))

// Zaman değişti mi kontrol et
if (time1 != time2) {
    WebUI.comment("✅ Canlı akış aktif. Time değişti: $time1 → $time2" // Testi fail yapar
        )
} else {
    WebUI.comment("⚠️ Uyarı! Time aynı kaldı: $time1")

    WebUI.takeScreenshot()

    WebUI.verifyMatch(time1, time2, false)
}

WebUI.closeBrowser()

