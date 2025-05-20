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
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import utils.DateUtils
import com.kms.katalon.core.webui.driver.DriverFactory
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.By
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import org.openqa.selenium.JavascriptExecutor

// Sağ tarafa doğru kaydırarak elementi görünür yap
def scrollHorizontallyToVisible(WebElement element, JavascriptExecutor js) {
	int currentScroll = 0
	boolean isVisible = false
	while (!isVisible && currentScroll < 3000) {
		js.executeScript("window.scrollBy(200, 0)")  // ← Sağa kaydır
		WebUI.delay(0.5)
		try {
			isVisible = element.isDisplayed()
		} catch (Exception e) {
			isVisible = false
		}
		currentScroll += 200
	}
	return isVisible
}

// Tarayıcıyı aç ve siteye git
WebUI.openBrowser('')
WebUI.navigateToUrl('https://platform.catchprobe.org/')

WebUI.maximizeWindow()

// Login işlemleri
WebUI.waitForElementVisible(findTestObject('Object Repository/hafdii/Page_/a_PLATFORM LOGIN'), 30)

WebUI.click(findTestObject('Object Repository/hafdii/Page_/a_PLATFORM LOGIN'))

WebUI.waitForElementVisible(findTestObject('Object Repository/hafdii/Page_/input_Email Address_email'), 30)

WebUI.setText(findTestObject('Object Repository/hafdii/Page_/input_Email Address_email'), 'fatih.yuksel@catchprobe.com')

WebUI.setEncryptedText(findTestObject('Object Repository/hafdii/Page_/input_Password_password'), 'RigbBhfdqOBDK95asqKeHw==')

WebUI.click(findTestObject('Object Repository/hafdii/Page_/button_Sign in'))

WebUI.delay(3)

// OTP işlemi
def randomOtp = (100000 + new Random().nextInt(900000)).toString()

WebUI.setText(findTestObject('Object Repository/hafdii/Page_/input_OTP Digit_vi_1_2_3_4_5'), randomOtp)

WebUI.click(findTestObject('Object Repository/hafdii/Page_/button_Verify'))

WebUI.click(findTestObject('Object Repository/dashboard/Page_/threatway'))

// Signature List öğesinin görünmesini bekle ve tıkla
WebUI.waitForElementVisible(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/div_Signature List'), 30)
WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/div_Signature List'))
// Bugünün tarihini al
def todayDate = CustomKeywords.'utils.DateUtils.getCurrentDate'()
// ==== 📌  On Today ====
WebUI.waitForElementPresent(findTestObject('Object Repository/dashboard/Page_/Page_/Date-today'), 5)
WebDriver driver = DriverFactory.getWebDriver()
JavascriptExecutor js = (JavascriptExecutor) driver
WebElement basedOnTodayElement = WebUiCommonHelper.findWebElement(findTestObject('Object Repository/dashboard/Page_/Page_/Date-today'), 10)

String basedOnTodayText = ''
if (scrollHorizontallyToVisible(basedOnTodayElement, js)) {
	basedOnTodayText = basedOnTodayElement.getText()
	println("📌 Based On Today Text: " + basedOnTodayText)
	js.executeScript("arguments[0].click();", basedOnTodayElement)
	WebUI.comment("👉 'Based On Today' tıklandı.")
} else {
	WebUI.comment("❌ 'Based On Today' görünür değil, tıklanamadı.")
}



// Tarayıcıyı kapat
WebUI.closeBrowser()