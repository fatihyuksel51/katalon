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

WebUI.waitForPageLoad(10)

CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()

WebUI.click(findTestObject('Object Repository/CVE/CVE'))

WebUI.delay(1)


WebDriver driver = DriverFactory.getWebDriver()
JavascriptExecutor js = (JavascriptExecutor) driver
Actions actions = new Actions(driver)

// Threat Actor linkini bul
TestObject capeclistlink = findTestObject('Object Repository/CVE/Vendor List')

// WebElement olarak al
WebElement linkElement = WebUiCommonHelper.findWebElement(capeclistlink, 10)
// Scroll edip görünür yap
safeScrollTo(findTestObject('Object Repository/CVE/Vendor List'))
WebUI.delay(2)
WebUI.click(findTestObject('Object Repository/CVE/Vendor List'))

WebUI.waitForPageLoad(10)

// Sayfa güncellenmesi için bekle
WebUI.delay(2)
// CREATE CRON butonu için TestObject oluştur
TestObject İdtext = new TestObject().addProperty("xpath", ConditionType.EQUALS, "//td[contains(@class,'ant-table-cell-fix')]//span[@class='truncate']")

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

String Vendortext=WebUI.getText(findTestObject('Object Repository/CVE/Vendorİd'))
println("📋 Kopyalanan Text: " + Vendortext)

/************** NEGATİF FİLTRE TESTLERİ **************/
// 1) Search -> ğğğğ -> No data
openFilters()
typeIntoNthInput(1, "ğğğğ")
clickApplyAndWait()
expectNoDataAndScroll()
clearWithXOnce()



// 2. input'a idText değerini yaz
TestObject searchInput = findTestObject('Object Repository/CVE/SearchInput')
WebUI.waitForElementVisible(searchInput, 10)
WebUI.setText(searchInput, Vendortext)
WebUI.delay(1)

// 3. Apply and Search butonuna bas
TestObject applyButton = findTestObject('Object Repository/CVE/ApplyAndSearchButton')
WebUI.waitForElementClickable(applyButton, 10)
WebUI.click(applyButton)
WebUI.delay(2)
WebUI.waitForPageLoad(10)



// Vendor Pagination Test
// =========================================================================
WebUI.comment("--- Starting Vendor Count Pagination Test ---")

// 1. Count değerini al
TestObject countElement = new TestObject()
countElement.addProperty("xpath", ConditionType.EQUALS, "//span[@class='text-text-light']")

WebElement countWebElement = WebUiCommonHelper.findWebElement(countElement, 10)
String countText = countWebElement.getText().trim()
int countValue = countText.toInteger()

WebUI.comment("🔢 Toplam vendor count değeri: ${countValue}")

// 6. Go CVE Detail butonuna tıkla
TestObject goCveDetailButton = findTestObject('Object Repository/CVE/GoCveDetailButton')
WebUI.waitForElementClickable(goCveDetailButton, 10)
WebUI.click(goCveDetailButton)
WebUI.delay(3)
//


TestObject Moretextwait = new TestObject().addProperty("xpath", ConditionType.EQUALS, "//button[@class='text-xs font-bold text-primary underline-offset-4 hover:underline']")

// 10 saniyeye kadar görünür mü kontrol et
if (WebUI.waitForElementVisible(Moretextwait, 10, FailureHandling.OPTIONAL)) {
	WebUI.comment("Moretextwait butonu bulundu.")
} else {
	WebUI.comment("Moretextwait butonu bulunamadı, sayfa yenileniyor...")
	WebUI.refresh()
	WebUI.waitForPageLoad(10)
	
	if (WebUI.waitForElementVisible(Moretextwait, 10, FailureHandling.OPTIONAL)) {
		WebUI.comment("Moretextwait butonu refresh sonrası bulundu.")
	} else {
		KeywordUtil.markFailedAndStop("Moretextwait butonu bulunamadı, test sonlandırılıyor.")
	}
}


// 7. Yeni sayfada başlık değerini al
TestObject detailTitleObject = findTestObject('Object Repository/CVE/CVemore')
WebUI.waitForElementVisible(detailTitleObject, 10)
WebUI.click(detailTitleObject)
WebUI.delay(1)
TestObject MoreText = findTestObject('Object Repository/CVE/MoreText')
WebUI.waitForElementVisible(MoreText, 10)
String Descriptiontitle = WebUI.getText(MoreText)
WebUI.delay(1)

// 8. Beklenen text ile karşılaştır

assert Descriptiontitle.contains(Vendortext)

// More sekmesini kapat
WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Mitre Close'))
WebUI.delay(2)

WebUI.click(goCveDetailButton)
WebUI.delay(3)

// 1. Count değerini al
TestObject VendorElement = new TestObject()
VendorElement.addProperty("xpath", ConditionType.EQUALS, "(//div[contains(concat(' ', normalize-space(@class), ' '), ' col-spam-1 ')])[3]")

WebElement VendorWebElement = WebUiCommonHelper.findWebElement(VendorElement, 10)
String VendorText = VendorWebElement.getText()

WebUI.verifyMatch(Vendortext, VendorText, false)
/*/
// 2. Sayfa başına kaç kayıt olduğunu varsay
int recordsPerPage = 10
int expectedPageCount = (int) Math.ceil(countValue / (double) recordsPerPage)

WebUI.comment("🎯 Beklenen pagination sayısı: ${expectedPageCount}")

// 3. Sayfa numaralarını bul
TestObject pageNumberLinks = new TestObject()
pageNumberLinks.addProperty("xpath", ConditionType.EQUALS,
	"//ul[contains(@class,'flex')]/li[a[not(contains(@aria-label,'previous')) and not(contains(@aria-label,'next'))]]/a")

List<WebElement> pageElements = WebUiCommonHelper.findWebElements(pageNumberLinks, 10)

if (!pageElements.isEmpty()) {
	WebElement firstPageElement = pageElements.get(0)
	
	// Scroll işlemi yapılır
	if (scrollToVisible(firstPageElement, js)) {
		js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", firstPageElement)
		WebUI.comment("📜 Pagination görünür hale getirildi.")
		WebUI.delay(1)
	} else {
		WebUI.comment("⚠️ Pagination scroll başarısız.")
	}

	// Ek olarak tüm sayfa sonuna scroll yapılabilir
	js.executeScript("window.scrollTo(0, document.body.scrollHeight);")
	WebUI.delay(1)
}

// 4. Son sayfa numarasını al
int actualLastPageNumber = 0
for (WebElement pageElement : pageElements) {
	String pageText = pageElement.getText().trim()
	if (pageText.matches("\\d+")) {
		int pageNumber = Integer.parseInt(pageText)
		if (pageNumber > actualLastPageNumber) {
			actualLastPageNumber = pageNumber
		}
	}
}

WebUI.comment("📄 Gerçek bulunan son sayfa numarası: ${actualLastPageNumber}")

// 5. Doğrulama
if (expectedPageCount == actualLastPageNumber) {
	WebUI.comment("✅ Pagination sayısı doğru: ${actualLastPageNumber}")
} else {
	KeywordUtil.markFailed("❌ Pagination yanlış! Beklenen: ${expectedPageCount}, Bulunan: ${actualLastPageNumber}")
}

WebUI.comment("--- Finished Vendor Count Pagination Scroll Test ---")
/*/
