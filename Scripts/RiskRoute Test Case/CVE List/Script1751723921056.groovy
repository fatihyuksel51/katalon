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
import com.kms.katalon.core.testobject.ObjectRepository as OR




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
boolean isBrowserOpen(){ try{ DriverFactory.getWebDriver(); return true }catch(Throwable t){ return false } }
TestObject X(String xp) {
	TestObject to = new TestObject(xp)
	to.addProperty("xpath", ConditionType.EQUALS, xp)
	return to
}

void openFilters() {
	TestObject filterBtn = X("//div[text()='FILTER OPTIONS']")
	safeScrollTo(filterBtn)
	WebUI.waitForElementClickable(filterBtn, 10)
	WebUI.click(filterBtn)
	WebUI.delay(1)
}

void typeIntoNthInput(int n, String text) {
	TestObject inp = X("(//input)[" + n + "]")
	WebUI.waitForElementVisible(inp, 10)
	WebUI.setText(inp, text)
}

void clickApplyAndWait() {
	TestObject applyBtn = X("//button[text()='APPLY AND SEARCH']")
	WebUI.waitForElementClickable(applyBtn, 10)
	WebUI.click(applyBtn)
	WebUI.delay(1)
	WebUI.waitForPageLoad(10)
}

void expectNoDataAndScroll() {
	TestObject noData = X("//div[@class='ant-empty-description' and normalize-space(text())='No data']")
	safeScrollTo(noData)
	WebUI.waitForElementVisible(noData, 10)
	WebUI.verifyElementVisible(noData)
}

void clearWithXOnce() {
	TestObject xBtn = X("//*[local-name()='svg' and contains(@class,'lucide-x')]")
	if (WebUI.waitForElementClickable(xBtn, 3, FailureHandling.OPTIONAL)) {
		WebUI.click(xBtn)
		WebUI.delay(0.5)
	} else {
		KeywordUtil.logInfo("ℹ️ Temizleme için X butonu bulunamadı (zaten temiz olabilir).")
	}
}

/************** Oturum **************/
void ensureSession(){
    if(isBrowserOpen()) return
    WebUI.openBrowser('')
    WebUI.maximizeWindow()
    WebUI.navigateToUrl('https://platform.catchprobe.org/')

    WebUI.waitForElementVisible(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'), 30)
    WebUI.click(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'))

    WebUI.waitForElementVisible(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 30)
    WebUI.setText(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 'katalon.test@catchprobe.com')
    WebUI.setEncryptedText(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Password_password'), 'RigbBhfdqOBDK95asqKeHw==')
    WebUI.click(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Sign in'))

    WebUI.delay(3)
    String otp = (100000 + new Random().nextInt(900000)).toString()
    WebUI.setText(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'), otp)
    WebUI.click(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Verify'))
    WebUI.delay(2)

    WebUI.waitForElementVisible(X("//span[text()='Threat']"), 10, FailureHandling.OPTIONAL)
}

/************** TEST: Coin Search Engine **************/
ensureSession()

//
// Riskroute sekmesine tıkla
WebUI.navigateToUrl('https://platform.catchprobe.org/riskroute')

WebDriver driver = DriverFactory.getWebDriver()
JavascriptExecutor js = (JavascriptExecutor) driver
Actions actions = new Actions(driver)

WebUI.waitForPageLoad(30)

WebUI.click(findTestObject('Object Repository/CVE/CVE'))

WebUI.waitForPageLoad(10)

WebUI.click(findTestObject('Object Repository/CVE/CVE List'))

// CREATE CRON butonu için TestObject oluştur
TestObject İdtext = new TestObject().addProperty("xpath", ConditionType.EQUALS, "(//td[contains(@class,'ant-table-cell')]//span[contains(text(),'CVE-')])[1]")

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

String Cvetext=WebUI.getText(findTestObject('Object Repository/CVE/Cve id'))
println("📋 Kopyalanan Text: " + Cvetext)

/************** NEGATİF FİLTRE TESTLERİ **************/
// 1) Search -> ğğğğ -> No data
openFilters()
typeIntoNthInput(1, "ğğğğ")
clickApplyAndWait()
expectNoDataAndScroll()
clearWithXOnce()

// 2) Product -> ğğğ -> No data

typeIntoNthInput(2, "ğğğ")
clickApplyAndWait()
expectNoDataAndScroll()
clearWithXOnce()

// 3) Vendor -> ğğğ -> No data

typeIntoNthInput(3, "ğğğ")
clickApplyAndWait()
expectNoDataAndScroll()
clearWithXOnce()




// 2. input'a idText değerini yaz
TestObject searchInput = findTestObject('Object Repository/CVE/SearchInput')
WebUI.waitForElementVisible(searchInput, 10)
WebUI.setText(searchInput, Cvetext)
WebUI.delay(1)

// 3. Apply and Search butonuna bas
TestObject applyButton = findTestObject('Object Repository/CVE/ApplyAndSearchButton')
WebUI.waitForElementClickable(applyButton, 10)
WebUI.click(applyButton)
WebUI.delay(2)
WebUI.waitForPageLoad(10)

// 4. İlk sonucu al
TestObject firstCveIdObject = findTestObject('Object Repository/CVE/Cve id')
WebUI.waitForElementVisible(firstCveIdObject, 10)
String firstCveId = WebUI.getText(firstCveIdObject)
WebUI.delay(1)
println("📋 Kopyalanan Text: " + firstCveId)
WebUI.verifyMatch(Cvetext, firstCveId, false)

// 6. Go CVE Detail butonuna tıkla
TestObject goCveDetailButton = findTestObject('Object Repository/CVE/GoCveDetailButton')
WebUI.waitForElementClickable(goCveDetailButton, 10)
WebUI.click(goCveDetailButton)
WebUI.delay(3)

// 7. Yeni sayfada başlık değerini al
TestObject detailTitleObject = findTestObject('Object Repository/CVE/Cvedetailpage')
WebUI.waitForElementVisible(detailTitleObject, 5)
String detailTitle = WebUI.getText(detailTitleObject)
WebUI.delay(1)

// 8. Beklenen text ile karşılaştır
WebUI.verifyMatch(detailTitle, firstCveId, false)

