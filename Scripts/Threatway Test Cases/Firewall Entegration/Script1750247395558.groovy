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
/*/
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

/*/

// Threatway sekmesine git
WebUI.navigateToUrl('https://platform.catchprobe.org/threatway')
WebUI.waitForPageLoad(30)
WebUI.delay(3)

CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()
// Firewall Entegration sekmesine git
WebUI.navigateToUrl('https://platform.catchprobe.org/threatway/firewall-integration')
WebUI.waitForPageLoad(30)

// Delete butonu tanımı
TestObject deleteButton1 = new TestObject().addProperty("xpath",
	com.kms.katalon.core.testobject.ConditionType.EQUALS,
	"//div[contains(@class, 'cursor-pointer') and contains(@class, 'destructive-foreground')]")

// Buton var mı kontrol edip varsa tıklayarak döngü
while (WebUI.verifyElementPresent(deleteButton1, 3, FailureHandling.OPTIONAL)) {
	WebUI.comment("Delete butonu bulundu, tıklanıyor...")
	WebUI.click(deleteButton1)
	CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()
	WebUI.delay(2)
	WebUI.click(findTestObject('Object Repository/Firewall Entegration/Delete button'))
	CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()
	
	WebUI.waitForElementVisible(findTestObject('Object Repository/Firewall Entegration/Delete Toast Message'), 5)
}

// İşlem bitti
WebUI.comment("Tüm delete butonları silindi. Firewall  boş.")

// Cloudflare tipinde firewall ekle
WebUI.click(findTestObject('Object Repository/Firewall Entegration/Create butonu'))
WebUI.waitForElementVisible(findTestObject('Object Repository/Firewall Entegration/Cloudflare button'), 5)
WebUI.click(findTestObject('Object Repository/Firewall Entegration/Cloudflare button'))

WebUI.delay(1)
WebUI.click(findTestObject('Object Repository/Firewall Entegration/Account İd'))
WebUI.clearText(findTestObject('Object Repository/Firewall Entegration/Account İd'))
WebUI.setText(findTestObject('Object Repository/Firewall Entegration/Account İd'), '7a29e990931ffa7ed245b8ea44c90a42')

WebUI.delay(1)
WebUI.click(findTestObject('Object Repository/Firewall Entegration/Update Time'))
WebUI.clearText(findTestObject('Object Repository/Firewall Entegration/Update Time'))
WebUI.setText(findTestObject('Object Repository/Firewall Entegration/Update Time'), '1')

WebUI.delay(1)
// Dropdown combobox buttonuna tıkla (gizli select aktif etmek için)
WebUI.click(findTestObject('Object Repository/Firewall Entegration/CloudflareDropdownButton'))
WebUI.sendKeys(findTestObject('Object Repository/Firewall Entegration/CloudflareDropdownButton'), 'F')
WebUI.click(findTestObject('Object Repository/Firewall Entegration/CloudflareDropdownButton'))


// cloudflareIntegration seçeneğini seç
WebUI.selectOptionByLabel(findTestObject('Object Repository/Firewall Entegration/CloudflareDropdownSelect'), 'Firewall', false)

// Seçimi kontrol et (opsiyonel)
def selectedOption = WebUI.getAttribute(findTestObject('Object Repository/Firewall Entegration/CloudflareDropdownSelect'), 'value')
WebUI.comment('Seçilen Option: ' + selectedOption)
WebUI.click(findTestObject('Object Repository/Firewall Entegration/Next Butonu'))

WebUI.click(findTestObject('Object Repository/Firewall Entegration/Title text'))
WebUI.setText(findTestObject('Object Repository/Firewall Entegration/Title text'),'katalon')

WebUI.click(findTestObject('Object Repository/Firewall Entegration/Tag Text'))

WebUI.setText(findTestObject('Object Repository/Firewall Entegration/Tag Text'),'katalon')

WebUI.delay(1)
WebUI.click(findTestObject('Object Repository/Firewall Entegration/Total number of'))
WebUI.clearText(findTestObject('Object Repository/Firewall Entegration/Total number of'))
WebUI.setText(findTestObject('Object Repository/Firewall Entegration/Total number of'), '1')

WebUI.click(findTestObject('Object Repository/Firewall Entegration/Done butonu'))
WebUI.delay(3)
WebUI.waitForElementVisible(findTestObject('Object Repository/Firewall Entegration/Toast Message - Create'), 5)
WebUI.waitForPageLoad(30)
//Collection textini doğrula
WebUI.verifyElementText(findTestObject('Object Repository/Firewall Entegration/Table Text'), 'katalon')

WebUI.click(findTestObject('Object Repository/Firewall Entegration/Refresh butonu'))
WebUI.delay(1)
WebUI.click(findTestObject('Object Repository/Firewall Entegration/REFRESH ok'))

WebUI.waitForElementVisible(findTestObject('Object Repository/Firewall Entegration/Toast Message'), 5)

WebUI.click(findTestObject('Object Repository/Firewall Entegration/artı butonu'))

WebUI.delay(1)
WebUI.click(findTestObject('Object Repository/Firewall Entegration/Edit İnput button'))
WebUI.clearText(findTestObject('Object Repository/Firewall Entegration/Edit İnput button'))
WebUI.setText(findTestObject('Object Repository/Firewall Entegration/Edit İnput button'), 'katalon')

WebUI.click(findTestObject('Object Repository/Firewall Entegration/Second artı butonu'))
WebUI.delay(1)
WebUI.click(findTestObject('Object Repository/Firewall Entegration/SAVE BUTONU'))

//Filter Options butonuna bas
WebUI.waitForElementVisible(findTestObject('Object Repository/Collections/Theatway filterbuton'), 5)
WebUI.click(findTestObject('Object Repository/Collections/Theatway filterbuton'))
WebUI.waitForElementVisible(findTestObject('Object Repository/Collections/Filter İnput'), 5)

WebUI.setText(findTestObject('Object Repository/Collections/Filter İnput'), 'katalon')

WebUI.click(findTestObject('Object Repository/Collections/threatway button_APPLY AND SEARCH'))

WebUI.delay(2)
WebUI.click(findTestObject('Object Repository/Firewall Entegration/Filter Close'))

WebUI.delay(2)

// Delete butonu tanımı
TestObject deleteButton = new TestObject().addProperty("xpath",
	com.kms.katalon.core.testobject.ConditionType.EQUALS,
	"//div[contains(@class, 'cursor-pointer') and contains(@class, 'destructive-foreground')]")

// Buton var mı kontrol edip varsa tıklayarak döngü
while (WebUI.verifyElementPresent(deleteButton, 5, FailureHandling.OPTIONAL)) {
	WebUI.comment("Delete butonu bulundu, tıklanıyor...")
	WebUI.click(deleteButton)
	CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()
	WebUI.delay(2)
	WebUI.click(findTestObject('Object Repository/Firewall Entegration/Delete button'))
	CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()
	
	WebUI.waitForElementVisible(findTestObject('Object Repository/Firewall Entegration/Delete Toast Message'), 5)
}

















