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
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import org.openqa.selenium.JavascriptExecutor as JavascriptExecutor
import org.openqa.selenium.WebElement as WebElement
import com.kms.katalon.core.webui.common.WebUiCommonHelper as WebUiCommonHelper
import org.openqa.selenium.WebDriver as WebDriver
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.util.KeywordUtil
import org.openqa.selenium.By
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.WebDriverWait
import org.openqa.selenium.support.ui.ExpectedConditions
import com.catchprobe.utils.MailReader
import static com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords.*
import com.kms.katalon.core.testobject.ObjectRepository as OR
WebElement safeScrollTo(TestObject to) {
	if (to == null) {
		KeywordUtil.markFailed("❌ TestObject NULL – Repository yolunu kontrol et.")
		return null
	}
	if (!WebUI.waitForElementPresent(to, 5, FailureHandling.OPTIONAL)) {
		KeywordUtil.logInfo("ℹ️ Element not present, scroll işlemi atlandı: ${to.getObjectId()}")
		return null
	}
	WebElement element = WebUiCommonHelper.findWebElement(to, 5)
	JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getWebDriver()
	js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element)
	WebUI.delay(0.5)
	return element
}

// Başlangıç işlemleri
/*/
WebUI.openBrowser('')
WebUI.navigateToUrl('https://platform.catchprobe.org/')
WebUI.maximizeWindow()

WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'), 30)
safeScrollTo(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'))
WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'))
WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 30)
safeScrollTo(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'))
WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 'katalon.test@catchprobe.com')
safeScrollTo(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Password_password'))
WebUI.setEncryptedText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Password_password'), 'RigbBhfdqOBDK95asqKeHw==')
safeScrollTo(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Sign in'))
WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Sign in'))
WebUI.delay(5)

String randomOtp = (100000 + new Random().nextInt(900000)).toString()
safeScrollTo(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'))
WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'), randomOtp)
safeScrollTo(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Verify'))
WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Verify'))
WebUI.delay(5)
WebUI.waitForPageLoad(10)
CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()
/*/
// Threatway sekmesine git
WebUI.navigateToUrl('https://platform.catchprobe.org/threatway')
WebUI.waitForPageLoad(30)
CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()
// Shared With Me sayfasına git
WebUI.navigateToUrl('https://platform.catchprobe.org/threatway/shares/shared-with-me')

// Sayfa tamamen yüklenene kadar maksimum 30 saniye bekle
WebUI.waitForPageLoad(30)

// Delete butonu tanımı
TestObject deleteButton = new TestObject().addProperty("xpath",
	com.kms.katalon.core.testobject.ConditionType.EQUALS,
	"//div[contains(@class, 'bg-destructive')]")

// Buton var mı kontrol edip varsa tıklayarak döngü
while (WebUI.verifyElementPresent(deleteButton, 3, FailureHandling.OPTIONAL)) {
	WebUI.comment("Delete butonu bulundu, tıklanıyor...")
	WebUI.click(deleteButton)
	CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()	
	WebUI.delay(2)
	WebUI.click(findTestObject('Object Repository/Channel Management/Page_/button_DELETE'))
	CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()	
	WebUI.waitForElementVisible(findTestObject('Object Repository/Channel Management/Page_/Share_Delete_Toast'), 5)
}

// İşlem bitti
WebUI.comment("Tüm delete butonları silindi. Shared With Me boş.")

// My Shared  sayfasına git
WebUI.navigateToUrl('https://platform.catchprobe.org/threatway/shares/my-shared')

// Sayfa tamamen yüklenene kadar maksimum 30 saniye bekle
WebUI.waitForPageLoad(30)

// Delete butonu tanımı
TestObject deleteButton1 = new TestObject().addProperty("xpath",
	com.kms.katalon.core.testobject.ConditionType.EQUALS,
	"//div[contains(@class, 'bg-destructive')]")

// Buton var mı kontrol edip varsa tıklayarak döngü
while (WebUI.verifyElementPresent(deleteButton1, 3, FailureHandling.OPTIONAL)) {
	WebUI.comment("Delete butonu bulundu, tıklanıyor...")
	WebUI.click(deleteButton1)
	CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()	
	WebUI.delay(2)
	WebUI.click(findTestObject('Object Repository/Channel Management/Page_/button_DELETE'))
	CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()
	
	WebUI.waitForElementVisible(findTestObject('Object Repository/Channel Management/Page_/Share_Delete_Toast'), 5)
}

// İşlem bitti
WebUI.comment("Tüm delete butonları silindi. My Shared  boş.")

//Channel Management sayfasına git

WebUI.navigateToUrl('https://platform.catchprobe.org/threatway/channel-management')



WebUI.verifyElementText(findTestObject('Object Repository/Channel Management/Page_/Channel Management Text'), 'Channel Management')

WebUI.click(findTestObject('Object Repository/Channel Management/Page_/Username copy'))



WebUI.verifyElementText(findTestObject('Object Repository/Channel Management/Page_/Username_Copy_ToastMessage'), 'Username copied to clipboard successfully!')

WebUI.click(findTestObject('Object Repository/Channel Management/Page_/Password Copy'))



WebUI.verifyElementText(findTestObject('Object Repository/Channel Management/Page_/Password_Copy_ToastMessage'), 
    'Password copied to clipboard successfully!')

// İlk şifreyi al
String firstPassword = WebUI.getText(findTestObject('Object Repository/Channel Management/Page_/Password Text'))

// İlk generate tıklaması
WebUI.click(findTestObject('Object Repository/Channel Management/Page_/button_GENERATE'))

WebUI.delay(2)

// Yeni şifreyi al
String secondPassword = WebUI.getText(findTestObject('Object Repository/Channel Management/Page_/Password Text'))

// Eğer şifre aynı kaldıysa ikinci kez generate et
if (firstPassword == secondPassword) {
	WebUI.comment("⚠️ İlk generate sonrası şifre değişmedi, tekrar denenecek...")

	WebUI.click(findTestObject('Object Repository/Channel Management/Page_/button_GENERATE'))
	
	WebUI.delay(2)

	// Son şifreyi al
	secondPassword = WebUI.getText(findTestObject('Object Repository/Channel Management/Page_/Password Text'))
}

// Son kontrol
if (firstPassword != secondPassword) {
	WebUI.comment("✅ Password değişti: '${secondPassword}'")
} else {
	WebUI.comment("❌ Password aynı kaldı: '${secondPassword}' → Test Fail!")
	WebUI.takeScreenshot()
	assert false : 'Password aynı kaldı → test fail!'
}
/*/
// DOCUMENT butonuna tıkla
WebUI.click(findTestObject('Object Repository/Channel Management/Page_/a_DOCUMENT'))

// Yeni sekmeye geç
WebUI.switchToWindowTitle('Introduction – Threatway APIv1 Documentation')

// URL doğrulaması yap
String currentUrl = WebUI.getUrl()
WebUI.verifyMatch(currentUrl, 'https://docs-threatway.catchprobe.io/#introduction', false)

// Sayfada 'Introduction' yazısını kontrol et
WebUI.verifyElementText(findTestObject('Object Repository/Channel Management/Page_Introduction  Threatway APIv1 Documentation/a_Introduction'), 
    'Introduction')

// Sekmeyi kapat
WebUI.closeWindowTitle('Introduction – Threatway APIv1 Documentation')

// İlk pencereye dön
WebUI.switchToWindowIndex(0)
//



WebUI.verifyElementText(findTestObject('Object Repository/Channel Management/Page_/Channel name'), 'mail-test')
/*/

WebUI.waitForElementClickable(findTestObject('Object Repository/Channel Management/Page_/button_CHANNEL LIST_ADD'), 5)
WebUI.click(findTestObject('Object Repository/Channel Management/Page_/button_CHANNEL LIST_ADD'))
CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()


WebUI.waitForElementVisible(findTestObject('Object Repository/Channel Management/Page_/input_Name_name'), 5)
WebUI.setText(findTestObject('Object Repository/Channel Management/Page_/input_Name_name'), 'katalon')

WebUI.waitForElementVisible(findTestObject('Object Repository/Channel Management/Page_/textarea_katalon'), 5)
WebUI.setText(findTestObject('Object Repository/Channel Management/Page_/textarea_katalon'), 'katalon')

WebUI.waitForElementClickable(findTestObject('Object Repository/Channel Management/Page_/button_CREATE'), 5)
WebUI.click(findTestObject('Object Repository/Channel Management/Page_/button_CREATE'))
CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()


WebUI.waitForElementVisible(findTestObject('Object Repository/Channel Management/Page_/Channel created toast'), 5)
WebUI.verifyElementText(findTestObject('Object Repository/Channel Management/Page_/Channel created toast'), 'Channel created successfully.')
/*/

WebUI.waitForElementClickable(findTestObject('Object Repository/Channel Management/Page_/Göz button'), 5)
WebUI.click(findTestObject('Object Repository/Channel Management/Page_/Göz button'))



WebUI.delay(4)

//WebUI.verifyElementClickable(findTestObject('Object Repository/Channel Management/Page_/button_Add Collection'), 5)

WebUI.waitForElementVisible(findTestObject('Object Repository/Channel Management/Page_/button_Collection'), 5)
WebUI.click(findTestObject('Object Repository/Channel Management/Page_/button_Collection'))



WebUI.waitForElementVisible(findTestObject('Object Repository/Channel Management/Page_/colection share name'), 5)
String collectionsharetext = WebUI.getText(findTestObject('Object Repository/Channel Management/Page_/colection share name'))

WebUI.delay(4)
WebUI.click(findTestObject('Object Repository/Channel Management/Page_/button_Share'))



// TestObject tanımı
def selectedCollectionsObj = OR.findTestObject('Object Repository/Channel Management/Page_/Selected Collections Text')

// Elementi 15 saniye bekle
boolean isVisible = WebUI.waitForElementVisible(selectedCollectionsObj, 15, FailureHandling.OPTIONAL)

if (!isVisible) {
    // Sayfa yenile
    WebUI.refresh()
    // Yeniden bekle (opsiyonel)
    WebUI.waitForPageLoad(10)
	WebUI.click(findTestObject('Object Repository/Channel Management/Page_/button_Share'))
    WebUI.waitForElementVisible(selectedCollectionsObj, 10)
}
String sellectcollections = WebUI.getText(findTestObject('Object Repository/Channel Management/Page_/Selected Collections Text')).toLowerCase()

//WebUI.verifyMatch(sellectcollections, collectionsharetext, false)

WebUI.waitForElementVisible(findTestObject('Object Repository/Channel Management/Page_/Button_Select Organization'), 5)
WebUI.click(findTestObject('Object Repository/Channel Management/Page_/Button_Select Organization'))


WebUI.waitForElementVisible(findTestObject('Object Repository/Channel Management/Page_/Share organization name'), 5)
WebUI.click(findTestObject('Object Repository/Channel Management/Page_/Share organization name'))



WebUI.delay(2)

WebUI.waitForElementVisible(findTestObject('Object Repository/Channel Management/Page_/Selected Collections Text'), 5)
WebUI.click(findTestObject('Object Repository/Channel Management/Page_/Selected Collections Text'))



WebUI.waitForElementVisible(findTestObject('Object Repository/Channel Management/Page_/button_SHARE (1)'), 5)
WebUI.click(findTestObject('Object Repository/Channel Management/Page_/button_SHARE (1)'))
CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()


WebUI.waitForElementVisible(findTestObject('Object Repository/Channel Management/Page_/Collections shared toast'), 5)
WebUI.verifyElementText(findTestObject('Object Repository/Channel Management/Page_/Collections shared toast'), 'Collections shared successfully.')

WebUI.waitForElementVisible(findTestObject('Object Repository/Channel Management/Page_/Collection Go detail'), 5)
WebUI.click(findTestObject('Object Repository/Channel Management/Page_/Collection Go detail'))
CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()


WebUI.waitForElementVisible(findTestObject('Object Repository/Channel Management/Page_/Signatureİp'), 30)
CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()

WebUI.verifyElementText(findTestObject('Object Repository/Channel Management/Page_/Signatureİp'), sellectcollections)

WebUI.navigateToUrl('https://platform.catchprobe.org/threatway/channel-management')
CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()

/*/
WebUI.waitForElementVisible(findTestObject('Object Repository/Channel Management/Page_/Delete Channel'), 5)
WebUI.click(findTestObject('Object Repository/Channel Management/Page_/Delete Channel'))

WebUI.waitForElementVisible(findTestObject('Object Repository/Channel Management/Page_/button_DELETE'), 5)
WebUI.click(findTestObject('Object Repository/Channel Management/Page_/button_DELETE'))
CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()


WebUI.waitForElementVisible(findTestObject('Object Repository/Channel Management/Page_/Channel deleted toast'), 5)
WebUI.verifyElementText(findTestObject('Object Repository/Channel Management/Page_/Channel deleted toast'), 'Channel deleted successfully')

// Sayfayı yenile
WebUI.refresh()
/*/

WebUI.delay(5)

// My Shared sayfasına git
WebUI.navigateToUrl('https://platform.catchprobe.org/threatway/shares/my-shared')

// Sayfa tamamen yüklenene kadar maksimum 30 saniye bekle
WebUI.waitForPageLoad(30)

//Sharing Status durumlarını kontrol et
CustomKeywords.'com.catchprobe.utils.TableUtils.checkMySharedTableAndAssert'("Mail Test", sellectcollections, "Katalon Studio", "WAITING")

// Share With Me Sayfasına git ve Collection sil
WebUI.navigateToUrl('https://platform.catchprobe.org/threatway/shares/shared-with-me')
CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()


// Sayfa tamamen yüklenene kadar maksimum 30 saniye bekle
WebUI.waitForPageLoad(30)
// Satırdaki Accept butonunu bul
TestObject acceptButton = findTestObject('Object Repository/Channel Management/Page_/Accept_Button')

// Butonun class'ını al
String buttonClass = WebUI.getAttribute(acceptButton, 'class')

// Eğer mavi ise, tıkla
if (buttonClass.contains('bg-primary')) {
	WebUI.click(acceptButton)
	CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()	
	WebUI.delay(2) // Bekleme koyabilirsin refresh yapıyorsa
	WebUI.click(findTestObject('Object Repository/Channel Management/Page_/button_ACCEPT'))
	CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()	
	WebUI.waitForElementVisible(findTestObject('Object Repository/Channel Management/Page_/Share_Accept_Toast'), 5)	
	KeywordUtil.logInfo("Favoriye ekleme işlemi başarılı ✅")
	// Sayfayı yenile
	WebUI.refresh()	
	WebUI.delay(5)
	
	// Tıklamadan sonra aynı butonun class'ını tekrar al
	String updatedClass = WebUI.getAttribute(acceptButton, 'class')
	
	if (updatedClass.contains('bg-success')) {
		KeywordUtil.logInfo("Accept işlemi başarılı ✅")
	} else {
		KeywordUtil.markFailed("Accept işlemi sonrası buton yeşile dönmedi ❌")
	}
} else if (buttonClass.contains('bg-success')) {
	KeywordUtil.logInfo("Zaten Accepted durumda, işlem yapılmadı ✅")
} else {
	KeywordUtil.markFailed("Buton beklenen class'lara sahip değil ❌ Class: " + buttonClass)
}

WebUI.delay(2)

// İlk delete butonuna tıkla



WebUI.waitForElementVisible(findTestObject('Object Repository/Channel Management/Page_/Delete_button'), 5)
WebUI.click(findTestObject('Object Repository/Channel Management/Page_/Delete_button'))
CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()
WebUI.delay(2)
WebUI.click(findTestObject('Object Repository/Channel Management/Page_/button_DELETE'))
WebUI.waitForElementVisible(findTestObject('Object Repository/Channel Management/Page_/Share_Delete_Toast'), 5)
CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()


// Sayfanın kendini güncellemesi için biraz bekle
WebUI.delay(2)

// Sayfada hâlâ delete butonu var mı kontrol et
boolean isDeleteButtonExist = WebUI.verifyElementNotPresent(deleteButton, 5, FailureHandling.OPTIONAL)

if (!isDeleteButtonExist) {
	// Hâlâ varsa test fail olsun
	WebUI.comment("Hata: Delete butonu hâlâ sayfada.")
	assert false : "Sayfada hâlâ delete butonu var, veri silinemedi."
} else {
	WebUI.comment("Başarılı: Delete butonu silindi, veri kalmadı.")
}






