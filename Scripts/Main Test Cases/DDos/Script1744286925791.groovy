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
WebUI.maximizeWindow()

WebUI.click(findTestObject('null'))

WebUI.setText(findTestObject('null'), 'fatih.yuksel@catchprobe.com')

WebUI.setEncryptedText(findTestObject('null'), 'RigbBhfdqOBDK95asqKeHw==')

WebUI.click(findTestObject('null'))

WebUI.click(findTestObject('null'))
WebUI.executeJavaScript("document.body.style.zoom='80%'", null)

WebUI.click(findTestObject('null'))

WebUI.click(findTestObject('null'))


String text = WebUI.getText(findTestObject('null'))

println('fatttsdsdsdsdsdsdsdssdsdsdsdsdsddsdsdsdsdsdsdsdsxdsds: ' + text)
String time1 = WebUI.getText(findTestObject('null'))



// Bekle (örneğin 5 saniye)
WebUI.delay(15)

// İkinci Time bilgisini al
String time2 = WebUI.getText(findTestObject('null'))

// Zaman değişti mi kontrol et
if (time1 != time2) {
	WebUI.comment("✅ Canlı akış aktif. Time değişti: $time1 → $time2" // Testi fail yapar
		)
} else {
	WebUI.comment("⚠️ Uyarı! Time aynı kaldı: $time1")

	WebUI.takeScreenshot()
	assert false : "Canlı akış algılandı → test senaryosu başarısız olmalı!"
	
}
// İlgili elementi bul


// Klavye ile scroll yap (örneğin PAGE_DOWN)
element.sendKeys(Keys.PAGE_DOWN)
WebUI.closeBrowser()

