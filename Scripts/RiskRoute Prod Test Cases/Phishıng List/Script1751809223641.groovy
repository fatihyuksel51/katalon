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
WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'), 30)

WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'))

WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 30)

WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 'katalon.test@catchprobe.com')

WebUI.setEncryptedText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Password_password'), 'RigbBhfdqOBDK95asqKeHw==')

WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Sign in'))

WebUI.delay(5)

// OTP işlemi
def randomOtp = (100000 + new Random().nextInt(900000)).toString()

WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'), randomOtp)

WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Verify'))

WebUI.delay(5)

WebUI.waitForPageLoad(30)

/*/
// Riskroute sekmesine tıkla
WebUI.navigateToUrl('https://platform.catchprobe.io/riskroute')


WebUI.delay(1)


WebDriver driver = DriverFactory.getWebDriver()
JavascriptExecutor js = (JavascriptExecutor) driver
Actions actions = new Actions(driver)

// Threat Actor linkini bul
TestObject capeclistlink = findTestObject('Object Repository/Phishing List/Phishing List')

// WebElement olarak al
WebElement linkElement = WebUiCommonHelper.findWebElement(capeclistlink, 10)
// Scroll edip görünür yap
scrollToVisible(linkElement, js)
WebUI.click(findTestObject('Object Repository/Phishing List/Phishing List'))

WebUI.waitForPageLoad(10)

//Facebook phishings kısmına tıkla
WebUI.click(findTestObject('Object Repository/Phishing List/Facebook Phishing'))

WebUI.delay(3)

// "Target" dropdown butonu
TestObject sortDropdown = new TestObject().addProperty("xpath", ConditionType.EQUALS, "//button[.//span[normalize-space(.)='Select Target']]")

// "Teknosa" menü öğesi
TestObject firstSeenOption = new TestObject().addProperty("xpath", ConditionType.EQUALS, "//div[@data-value='teknosa']")

WebUI.click(sortDropdown)
WebUI.waitForElementClickable(firstSeenOption, 5)
WebUI.click(firstSeenOption)
WebUI.comment("Teknosa seçildi.")


// Favori butonunun XPath'ine göre dinamik TestObject oluştur
TestObject favButton = new TestObject()
favButton.addProperty("xpath", ConditionType.EQUALS, "//div[contains(@class, 'cursor-pointer') and (contains(@class, 'bg-primary') or contains(@class, 'bg-warning'))]/ancestor::button[1]")

WebUI.waitForElementVisible(favButton, 20)

// Status butonunun class'ını al
String statusClass = WebUI.executeJavaScript(
    """
    var el = document.evaluate("//span[contains(@class, 'items-center') and (contains(@class, 'bg-success') or contains(@class, 'bg-red-500'))]", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;
    return el ? el.className : null;
    """, null
)

KeywordUtil.logInfo("Durum class'ı: " + statusClass)



// "Verified" dropdown butonu
TestObject VerifiedDropdown = new TestObject().addProperty("xpath", ConditionType.EQUALS, "//button[contains(@class, 'inline-flex items-center cursor-pointer')]/span[text()='Verify Status']")

// "Verified" menü öğesi
TestObject Verified = new TestObject().addProperty("xpath", ConditionType.EQUALS, "//div[contains(@class,'relative flex cursor-default')]")

// "Not verified" menü öğesi
TestObject NotVeriifed = new TestObject().addProperty("xpath", ConditionType.EQUALS, "(//div[contains(@class,'relative flex cursor-default')])[2]")


// Başarı mesajı TestObject'i oluştur
TestObject successMessage = new TestObject()
successMessage.addProperty("xpath", ConditionType.EQUALS, "//div[contains(@class, 'text-success') and contains(text(), 'Phishing status updated successfully')]")

// Verify/Change Status butonunu temsil eden TestObject (Sen kendi sayfana göre uyarlayacaksın)
TestObject verifyButton = new TestObject()
verifyButton.addProperty("xpath", ConditionType.EQUALS, "//button[.//*[local-name()='svg' and contains(@class, 'lucide-pencil')]]")

//Update buton
TestObject updatebutton = new TestObject()
updatebutton.addProperty("xpath", ConditionType.EQUALS, "//button[.//*[local-name()='svg' and contains(@class, 'lucide-pencil')] and contains(., 'Update')]")

// Duruma göre işlem yap
if (statusClass == null) {
    KeywordUtil.markFailed("Durum butonu bulunamadı ❌")
} else if (statusClass.contains("bg-success")) {
	WebUI.click(verifyButton)
	WebUI.waitForElementClickable(updatebutton, 5)
	WebUI.click(updatebutton)
	WebUI.waitForElementVisible(successMessage, 10)
	WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Mitre Close'))
	WebUI.comment("Not Verified seçildi.")
	WebUI.delay(3)
	WebUI.click(VerifiedDropdown)
	WebUI.waitForElementClickable(Verified, 5)
	WebUI.click(NotVeriifed)	
	
    KeywordUtil.logInfo("Şu an Verified durumda ✅ — İşlem yapılmadı.")
} 
    if (statusClass.contains("bg-red-500")) {
	WebUI.click(verifyButton)
	WebUI.waitForElementClickable(updatebutton, 5)
	WebUI.click(updatebutton)
	WebUI.waitForElementVisible(successMessage, 10)
	WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Mitre Close'))
	WebUI.comment("Verified seçildi.")
	WebUI.delay(3)
	WebUI.click(VerifiedDropdown)
	WebUI.waitForElementClickable(Verified, 5)
	WebUI.click(Verified)
	
    KeywordUtil.logInfo("Şu an Verified durumda ❌ — Güncelleniyor...")

    // Logla
    KeywordUtil.logInfo("Phishing status başarıyla güncellendi ✅")
} 
else {
    KeywordUtil.markWarning("Beklenmeyen bir class geldi: " + statusClass)
}