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



// ✅ Güvenli scroll fonksiyonu
WebElement safeScrollTo(TestObject to) {
	if (to == null) {
		KeywordUtil.markFailed("❌ TestObject NULL – Repository yolunu kontrol et.")
		return null
	}
	if (!WebUI.waitForElementPresent(to, 5, FailureHandling.OPTIONAL)) {
		KeywordUtil.logInfo("ℹ️ Element not present, scroll işlemi atlandı: ${to.getObjectId()}")
		return null
	}
	WebElement element = WebUiCommonHelper.findWebElement(to, 1)
	JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getWebDriver()
	js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element)
	WebUI.delay(0.5)
	return element
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
WebUI.navigateToUrl('https://platform.catchprobe.org/riskroute')

WebDriver driver = DriverFactory.getWebDriver()
JavascriptExecutor js = (JavascriptExecutor) driver
Actions actions = new Actions(driver)

WebUI.navigateToUrl('https://platform.catchprobe.org/riskroute/cwe-list')

WebUI.delay(2)
WebUI.waitForPageLoad(30)

// CREATE CRON butonu için TestObject oluştur
TestObject İdtext = new TestObject().addProperty("xpath", ConditionType.EQUALS, "//td[contains(@class,'ant-table-cell-fix-left')]/span")

// 10 saniyeye kadar görünür mü kontrol et
if (WebUI.waitForElementVisible(İdtext, 10, FailureHandling.OPTIONAL)) {
	WebUI.comment("İdtext butonu bulundu.")
} else {
	WebUI.comment("İdtext butonu bulunamadı, sayfa yenileniyor...")
	WebUI.refresh()
	WebUI.waitForPageLoad(10)
	
	if (WebUI.waitForElementVisible(İdtext, 10, FailureHandling.OPTIONAL)) {
		WebUI.comment("İdtext butonu refresh sonrası bulundu.")
	} else {
		KeywordUtil.markFailedAndStop("İdtext butonu bulunamadı, test sonlandırılıyor.")
	}
}

String Cwetext=WebUI.getText(findTestObject('Object Repository/CVE/cwe id'))
println("📋 Kopyalanan Text: " + Cwetext)

// 1. Filter Options butonuna tıkla
TestObject filterButton = findTestObject('Object Repository/CVE/FilterOptions')
// WebElement olarak al
WebUI.delay(2)
// Scroll edip görünür yap
safeScrollTo(findTestObject('Object Repository/CVE/FilterOptions'))
WebUI.waitForElementClickable(filterButton, 10)
WebUI.click(filterButton)

// 2. input'a idText değerini yaz
TestObject searchInput = findTestObject('Object Repository/CVE/SearchInput')
WebUI.waitForElementVisible(searchInput, 10)
WebUI.setText(searchInput, Cwetext)
WebUI.delay(1)

// 3. Apply and Search butonuna bas
TestObject applyButton = findTestObject('Object Repository/CVE/ApplyAndSearchButton')
WebUI.waitForElementClickable(applyButton, 10)
WebUI.click(applyButton)
WebUI.delay(2)
WebUI.waitForPageLoad(10)

// 4. İlk sonucu al
TestObject firstCveIdObject = findTestObject('Object Repository/CVE/cwe id')
WebUI.waitForElementVisible(firstCveIdObject, 10)
String firstCveId = WebUI.getText(firstCveIdObject)
WebUI.delay(1)
println("📋 Kopyalanan Text: " + firstCveId)
WebUI.verifyMatch(Cwetext, firstCveId, false)

// 6. Go CVE Detail butonuna tıkla
TestObject goCveDetailButton = findTestObject('Object Repository/CVE/GoCveDetailButton')
WebUI.waitForElementClickable(goCveDetailButton, 10)
WebUI.click(goCveDetailButton)
WebUI.delay(3)

// 7. Yeni sayfada başlık değerini al
TestObject detailTitleObject = findTestObject('Object Repository/CVE/CweDetailPageTitle')
WebUI.waitForElementVisible(detailTitleObject, 10)
String detailTitle = WebUI.getText(detailTitleObject)
WebUI.delay(1)

// 8. Beklenen text ile karşılaştır
assert detailTitle.contains(Cwetext)
