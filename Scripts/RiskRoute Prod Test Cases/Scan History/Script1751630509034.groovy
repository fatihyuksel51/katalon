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

WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 'fatih.yuksel@catchprobe.com')

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

WebUI.delay(5)


WebDriver driver = DriverFactory.getWebDriver()
JavascriptExecutor js = (JavascriptExecutor) driver
Actions actions = new Actions(driver)

CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()

WebUI.delay(3)

WebUI.waitForPageLoad(10)


WebUI.click(findTestObject('Object Repository/Scan/Scan'))

WebUI.waitForPageLoad(30)

WebUI.click(findTestObject('Object Repository/Scan History'))

WebUI.delay(3)

WebUI.waitForPageLoad(10)



// CREATE Scan  butonu için TestObject oluştur
TestObject createScanButtonwait = new TestObject().addProperty("xpath", ConditionType.EQUALS, "//div[text()='CREATE SCAN']")

// 10 saniyeye kadar görünür mü kontrol et
if (WebUI.waitForElementVisible(createScanButtonwait, 10, FailureHandling.OPTIONAL)) {
	WebUI.comment("CREATE SCAN butonu bulundu.")
} else {
	WebUI.comment("CREATE SCAN butonu bulunamadı, sayfa yenileniyor...")
	WebUI.refresh()
	WebUI.waitForPageLoad(10)
	
	if (WebUI.waitForElementVisible(createScanButtonwait, 10, FailureHandling.OPTIONAL)) {
		WebUI.comment("CREATE CRON butonu refresh sonrası bulundu.")
	} else {
		KeywordUtil.markFailedAndStop("CREATE SCAN butonu bulunamadı, test sonlandırılıyor.")
	}
}


// ✅ İlk satırdaki domain değerini al
TestObject firstRowDomainCell = new TestObject()
firstRowDomainCell.addProperty("xpath", ConditionType.EQUALS, "(//tr[contains(@class, 'ant-table-row')])[1]/td[1]/span")

String firstDomainText = WebUI.getText(firstRowDomainCell)
println "📌 İlk satırdaki Domain: " + firstDomainText

// ✅ Go Detail (info icon) butonunu bul
TestObject goDetailButton = new TestObject()
goDetailButton.addProperty("xpath", ConditionType.EQUALS, "(//tr[contains(@class, 'ant-table-row')])[1]//button[1]")

// Görünür yapıp tıkla
WebElement detailBtn = WebUiCommonHelper.findWebElement(goDetailButton, 5)
//js.executeScript("arguments[0].scrollIntoView(true);", detailBtn)
WebUI.delay(0.5)
WebUI.click(goDetailButton)
CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()

WebUI.waitForPageLoad(10)

// ✅ Scan History sayfasında target'ı al
TestObject scanHistoryTarget = new TestObject()
scanHistoryTarget.addProperty("xpath", ConditionType.EQUALS, "//div[contains(@class, 'h-20') and contains(@class, 'rounded-full')]//span")

WebUI.waitForElementVisible(scanHistoryTarget, 10)
String scanTargetText = WebUI.getText(scanHistoryTarget)
println "📌 Scan History Target: " + scanTargetText

// ✅ Eşleşme kontrolü
assert scanTargetText.trim() == firstDomainText.trim() : "❌ Scan History target eşleşmedi!"

println "✅ Eşleşme başarılı: '${firstDomainText}'"