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
import internal.GlobalVariable
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import org.openqa.selenium.WebElement
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.Keys
import org.openqa.selenium.WebElement as Keys
import com.kms.katalon.core.testobject.ConditionType
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import com.kms.katalon.core.util.KeywordUtil



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
/*/ Tarayıcıyı aç ve siteye git
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
WebUI.waitForPageLoad(30)
/*/
WebUI.delay(3)


// Threatway sekmesine git
WebUI.navigateToUrl('https://platform.catchprobe.org/threatway')
WebUI.waitForPageLoad(30)




CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()
// Threat Actor linkini bul
TestObject coinsearchlink = findTestObject('Object Repository/Coin Search Engine/Coinsearchenginelink')

// WebElement olarak al
WebElement linkElement = WebUiCommonHelper.findWebElement(coinsearchlink, 10)
// JavascriptExecutor al
JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getWebDriver()





// Scroll edip görünür yap
scrollToVisible(linkElement, js)

// Tıkla
WebUI.click(coinsearchlink)
WebUI.waitForPageLoad(30)

CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()

WebUI.waitForElementClickable(findTestObject('Object Repository/Coin Search Engine/From Copy'), 10)
WebUI.click(findTestObject('Object Repository/Coin Search Engine/From Copy'))

WebUI.verifyElementText(findTestObject('Object Repository/Coin Search Engine/Toast Message'),
	'From Account copied to clipboard successfully!')
// Kopyalanan değeri clipboard'dan al
String copiedText = Toolkit.getDefaultToolkit()
						  .getSystemClipboard()
						  .getData(DataFlavor.stringFlavor)
						  .toString()

// Log'a yaz
println("📋 Kopyalanan Text: " + copiedText)

String Fromtext=WebUI.getText(findTestObject('Object Repository/Coin Search Engine/From Link'))
String Coinİptext=WebUI.getText(findTestObject('Object Repository/Coin Search Engine/Coin link'))

WebUI.click(findTestObject('Object Repository/Coin Search Engine/Theatway filterbuton'))
int maxRetries = 1
int attempt = 0
boolean isSuccess = false

while (attempt < maxRetries && !isSuccess) {
    try {
        // Input alanına copiedText'i yaz
        WebUI.waitForElementVisible(findTestObject('Object Repository/Coin Search Engine/İnput'), 10)
        WebUI.setText(findTestObject('Object Repository/Coin Search Engine/İnput'), copiedText)

        // Apply and Search butonuna tıkla
        WebUI.waitForElementClickable(findTestObject('Object Repository/Coin Search Engine/threatway button_APPLY AND SEARCH'), 10)
        WebUI.click(findTestObject('Object Repository/Coin Search Engine/threatway button_APPLY AND SEARCH'))

        // Sayfanın yüklenmesini bekle
        WebUI.waitForPageLoad(10)
		// Sayfa yenilenip yeni sonuç geldi mi kontrol et
		WebUI.verifyElementText(findTestObject('Object Repository/Coin Search Engine/From Link'),Fromtext )
		
        isSuccess = true // Başarılıysa döngüden çık
    } catch (Exception e) {
        attempt++
        println("Attempt ${attempt} failed. Refreshing the page...")

        // Sayfayı yenile
        WebUI.refresh()
		WebUI.click(findTestObject('Object Repository/Coin Search Engine/Theatway filterbuton'))
		WebUI.setText(findTestObject('Object Repository/Coin Search Engine/İnput'), copiedText)
		WebUI.waitForElementClickable(findTestObject('Object Repository/Coin Search Engine/threatway button_APPLY AND SEARCH'), 10)
		WebUI.click(findTestObject('Object Repository/Coin Search Engine/threatway button_APPLY AND SEARCH'))
		WebUI.verifyElementText(findTestObject('Object Repository/Coin Search Engine/From Link'),Fromtext )
        // Sayfa yüklenmesini bekle
        WebUI.waitForPageLoad(10)
    }
}

if (!isSuccess) {
    KeywordUtil.markFailed("Apply and Search işlemi 3 denemeye rağmen gerçekleştirilemedi.")
}

WebUI.verifyElementText(findTestObject('Object Repository/Coin Search Engine/From Link'),Fromtext )

WebUI.click(findTestObject('Object Repository/Coin Search Engine/Coin link'))

WebUI.verifyElementText(findTestObject('Object Repository/Coin Search Engine/İp Link'),Coinİptext )

WebUI.click(findTestObject('Object Repository/Coin Search Engine/Show detail Close button'))

WebUI.click(findTestObject('Object Repository/Coin Search Engine/Filter Close'))

//İkinci doğrulaması yap
WebElement SecondElement = WebUiCommonHelper.findWebElement(findTestObject('Object Repository/Coin Search Engine/Coin Second'), 10)
// Scroll ve click işlemi
if (scrollToVisible(SecondElement, js)) {
	js.executeScript("arguments[0].click();", SecondElement)
	WebUI.comment("👉 'ikinci' butonuna tıklandı.")
} else {
	WebUI.comment("❌ 'ikinci' butonu görünür değil, tıklanamadı.")
}

WebUI.delay(3)

WebUI.click(findTestObject('Object Repository/Coin Search Engine/Show detail Close button'))








