import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import com.kms.katalon.core.util.KeywordUtil

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
import internal.GlobalVariable
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import org.openqa.selenium.WebElement
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.Keys
import org.openqa.selenium.WebElement as Keys
import com.kms.katalon.core.testobject.ConditionType


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
//
WebUI.openBrowser('')

WebUI.navigateToUrl('https://platform.catchprobe.org/')

WebUI.maximizeWindow()

// Login işlemleri
WebUI.waitForElementVisible(findTestObject('Object Repository/otp/Page_/a_PLATFORM LOGIN'), 30)

WebUI.click(findTestObject('Object Repository/otp/Page_/a_PLATFORM LOGIN'))

WebUI.waitForElementVisible(findTestObject('Object Repository/otp/Page_/input_Email Address_email'), 30)

WebUI.setText(findTestObject('Object Repository/otp/Page_/input_Email Address_email'), 'katalon.test@catchprobe.com')

WebUI.setEncryptedText(findTestObject('Object Repository/otp/Page_/input_Password_password'), 'RigbBhfdqOBDK95asqKeHw==')

WebUI.click(findTestObject('Object Repository/otp/Page_/button_Sign in'))

WebUI.delay(6)

// OTP işlemi
def randomOtp = (100000 + new Random().nextInt(900000)).toString()

WebUI.setText(findTestObject('Object Repository/otp/Page_/input_OTP Digit_vi_1_2_3_4_5'), randomOtp)

WebUI.click(findTestObject('Object Repository/otp/Page_/button_Verify'))

CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()
//


// Threatway sekmesine git
WebUI.navigateToUrl('https://platform.catchprobe.org/threatway')
WebUI.waitForPageLoad(30)
WebUI.delay(3)

CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()
// Collections sekmesine git
WebUI.navigateToUrl('https://platform.catchprobe.org/threatway/collections')
WebUI.waitForPageLoad(10)
WebUI.delay(2)


/// ✅ Collection isimleri için örnek jeneratör
List<String> collectionNames = (1..25).collect { "Collection_col_${it}" }

for (String name in collectionNames) {
	try {
		// Create Collection butonuna tıkla
		WebUI.click(findTestObject('Object Repository/Collections/Create Collection'))

		// Input görünene kadar bekle
		WebUI.waitForElementVisible(findTestObject('Object Repository/Collections/İnput'), 5)

		// Collection adını yaz
		WebUI.setText(findTestObject('Object Repository/Collections/İnput'), name)

		// Create butonuna bas
		WebUI.click(findTestObject('Object Repository/Collections/File Create'))

		// Toast mesajı kontrol et
		WebUI.waitForElementVisible(findTestObject('Object Repository/Collections/Toast Message'), 5)
		String toastMessage = WebUI.getText(findTestObject('Object Repository/Collections/Toast Message')).trim()

		if (toastMessage == 'Collection created successfully.') {
			KeywordUtil.logInfo("✅ ${name} başarıyla oluşturuldu.")
		} else {
			KeywordUtil.markWarning("⚠️ ${name} oluşturulamadı! Toast mesajı: ${toastMessage}")
		}

		// Biraz bekle, arayüz kasmasın
		WebUI.delay(1)

	} catch (Exception e) {
		KeywordUtil.markWarning("❌ ${name} oluşturulurken hata: " + e.getMessage())
	}
}