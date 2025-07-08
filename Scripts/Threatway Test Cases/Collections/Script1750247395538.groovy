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

WebUI.setText(findTestObject('Object Repository/otp/Page_/input_Email Address_email'), 'fatih.yuksel@catchprobe.com')

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
// Collections sekmesine git
WebUI.navigateToUrl('https://platform.catchprobe.org/threatway/collections')
WebUI.waitForPageLoad(10)
// Sayfadaki tüm Delete butonlarını bul
TestObject deleteButtons = new TestObject('dynamicDeleteButtons')
deleteButtons.addProperty('xpath', ConditionType.EQUALS, "//div[contains(@class, 'cursor-pointer') and contains(@class, 'destructive-foreground')]")

// Kaç adet Delete butonu var, say
int deleteCount = WebUI.findWebElements(deleteButtons, 5).size()

WebUI.comment("🗑️ Toplam $deleteCount adet koleksiyon silinecek.")

while (WebUI.findWebElements(deleteButtons, 5).size() > 0) {
	WebUI.comment("Silme işlemi başlatılıyor...")

	// İlk Delete butonuna tıkla
	WebUI.click(deleteButtons)
	WebUI.delay(1)

	// Silme onay butonuna tıkla
	WebUI.click(findTestObject('Object Repository/Collections/Delete button'))

	// Toast mesajının çıkmasını bekle
	WebUI.waitForElementVisible(findTestObject('Object Repository/Collections/Delete Toast Message'), 5)

	// Toast kapanmasını beklemek için biraz gecikme koy
	WebUI.delay(1)
}

//Create collection butonuna bas
WebUI.click(findTestObject('Object Repository/Collections/Create Collection'))

WebUI.waitForElementVisible(findTestObject('Object Repository/Collections/İnput'), 5)
WebUI.setText(findTestObject('Object Repository/Collections/İnput'), 'katalon')
WebUI.click(findTestObject('Object Repository/Collections/File Create'))
WebUI.verifyElementText(findTestObject('Object Repository/Collections/Toast Message'),
	'Collection created successfully.')
//Filter Options butonuna bas
WebUI.waitForElementVisible(findTestObject('Object Repository/Collections/Theatway filterbuton'), 5)
WebUI.click(findTestObject('Object Repository/Collections/Theatway filterbuton'))
WebUI.waitForElementVisible(findTestObject('Object Repository/Collections/Filter İnput'), 5)

WebUI.setText(findTestObject('Object Repository/Collections/Filter İnput'), 'katalon')

WebUI.click(findTestObject('Object Repository/Collections/threatway button_APPLY AND SEARCH'))



// Buton obje tanımı (Dynamic obje — XPATH ile)
TestObject toggleButton = new TestObject()
toggleButton.addProperty("xpath", ConditionType.EQUALS, "//button[contains(text(),'Passive') or contains(text(),'Active')]")

// Sayfanın yüklendiğinden emin ol
WebUI.waitForElementPresent(toggleButton, 10)

// Mevcut buton text'ini al
String buttonText = WebUI.getText(toggleButton)
println("Butonun mevcut durumu: " + buttonText)

// Eğer Passive ise bas ve Active olduğunu kontrol et
if (buttonText == 'PASSIVE') {
	WebUI.click(toggleButton)
	WebUI.click(findTestObject('Object Repository/Collections/Ok Butonu'))
	WebUI.waitForElementVisible(findTestObject('Object Repository/Collections/Active-Passive Toast Message'), 5)
	WebUI.verifyElementText(toggleButton, 'ACTIVE')
	println("Buton Active oldu.")
}
// Eğer Active ise bas ve Passive olduğunu kontrol et
else if (buttonText == 'ACTIVE') {
	WebUI.click(toggleButton)
	WebUI.click(findTestObject('Object Repository/Collections/Ok Butonu'))
	WebUI.waitForElementVisible(findTestObject('Object Repository/Collections/Active-Passive Toast Message'), 5)
	WebUI.verifyElementText(toggleButton, 'PASSIVE')
	println("Buton Passive oldu.")
}
else {
	println("Buton durumu tanımlanamadı: " + buttonText)
}

//Edit butonuna bas
// Kalem ikonuna bas
WebUI.click(findTestObject('Object Repository/Collections/Edit Buton'))

// 1-2 saniye input alanının gelmesi için bekle
WebUI.delay(1)
WebUI.click(findTestObject('Object Repository/Collections/İnput'))
WebUI.clearText(findTestObject('Object Repository/Collections/İnput'))
WebUI.setText(findTestObject('Object Repository/Collections/İnput'), 'katalon text')

WebUI.delay(2)
WebUI.click(findTestObject('Object Repository/Collections/Save Butonu'))
WebUI.waitForElementVisible(findTestObject('Object Repository/Collections/Edit toast message'), 5)

// Edit texti doğrula
//Completed textini doğrula
WebUI.verifyElementText(findTestObject('Object Repository/Collections/Table text'), 'katalon text')

// Collectionu sil
WebUI.click(findTestObject('Object Repository/Collections/Delete Butonu'))
WebUI.delay(2)
WebUI.click(findTestObject('Object Repository/Collections/Delete button'))
WebUI.waitForElementVisible(findTestObject('Object Repository/Collections/Delete Toast Message'), 5)
WebUI.click(findTestObject('Object Repository/Collections/Filter Close'))
WebUI.delay(3)

// Scroll edip görünür yap
// JavascriptExecutor al
JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getWebDriver()
TestObject paginationObject = findTestObject('Object Repository/Collections/2.pagination')
WebElement paginationElement = WebUiCommonHelper.findWebElement(paginationObject, 10)
scrollToVisible(paginationElement, js)
js.executeScript("window.scrollTo(0, document.body.scrollHeight)")
WebUI.delay(1)

// Tıkla
WebUI.click(findTestObject('Object Repository/Collections/2.pagination'))




