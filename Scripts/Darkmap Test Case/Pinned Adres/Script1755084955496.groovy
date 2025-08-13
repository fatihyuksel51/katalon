import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import com.kms.katalon.core.webui.driver.DriverFactory
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebElement
import org.openqa.selenium.Keys as Keys
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling   // <-- OPTIONAL için şart

// ---------- helpers ----------
TestObject X(String xp) {
	TestObject to = new TestObject(xp)
	to.addProperty("xpath", ConditionType.EQUALS, xp)
	return to
}
void safeClick(String xp, int t=15) {
	if (!WebUI.waitForElementClickable(X(xp), t)) {
		KeywordUtil.markFailedAndStop("❌ Tıklanabilir değil: " + xp)
	}
	WebUI.click(X(xp))
}
String safeText(String xp, int t=15) {
	if (!WebUI.waitForElementVisible(X(xp), t)) {
		KeywordUtil.markFailedAndStop("❌ Görünür değil: " + xp)
	}
	return WebUI.getText(X(xp)).trim()
}
void waitToast(String contains, int t=10) {
	String toastXp = "//*[contains(@class,'toast') or contains(@class,'alert') or contains(@class,'notification')][contains(.,'"+contains+"')]"
	WebUI.waitForElementVisible(X(toastXp), t, FailureHandling.OPTIONAL)
}
void scrollIntoView(String xp) {
	def el = WebUiCommonHelper.findWebElement(X(xp), 5)
	((JavascriptExecutor)DriverFactory.getWebDriver()).executeScript("arguments[0].scrollIntoView({block:'center'});", el)
}
// ✅ Güvenli scroll fonksiyonu (Repository objeleri için)
WebElement safeScrollTo(TestObject to) {
	if (to == null) {
		KeywordUtil.markFailed("❌ TestObject NULL – Repository yolunu kontrol et.")
		return null
	}
	if (!WebUI.waitForElementPresent(to, 2, FailureHandling.OPTIONAL)) {
		KeywordUtil.logInfo("ℹ️ Element not present, scroll atlandı: ${to.getObjectId()}")
		return null
	}
	WebElement element = WebUiCommonHelper.findWebElement(to, 2)
	JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getWebDriver()
	js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element)
	WebUI.delay(0.5)
	return element
}

/************** Quick Search: leak + pagination + fingerprint **************/
void scrollToBottomQS() {
    ((JavascriptExecutor) DriverFactory.getWebDriver())
        .executeScript("window.scrollTo(0, document.body.scrollHeight)")
}
void zoomOutPage() {
	JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getWebDriver()
	js.executeScript("document.body.style.zoom='0.85'")
}
// Sadece END tuşu spami (window/inner scroll yakalasın diye body'ye gönder)
void endSpam(int times = 10, double pause = 0.2) {
	TestObject body = new TestObject('body').addProperty('xpath', ConditionType.EQUALS, '//body')
	for (int i = 0; i < times; i++) {
		WebUI.sendKeys(body, Keys.chord(Keys.END))
		WebUI.delay(pause)
	}
	// küçük bir fallback: aktif elemana da bir-iki kez gönder
	try {
		def driver = DriverFactory.getWebDriver()
		for (int i = 0; i < 2; i++) {
			driver.switchTo().activeElement().sendKeys(Keys.END)
			WebUI.delay(pause)
		}
	} catch (Throwable ignore) {}
}
void scrollThenClick(String xp, int t=15) {
	WebUI.waitForElementPresent(X(xp), t, FailureHandling.OPTIONAL)
	WebUI.waitForElementVisible(X(xp), t)
	scrollIntoView(xp)
	ensureClickableAndClick(xp, t)
}

void ensureClickableAndClick(String xp, int t=15) {
	if (!WebUI.waitForElementClickable(X(xp), t)) {
		KeywordUtil.markFailedAndStop("❌ Tıklanabilir değil: " + xp)
	}
	WebUI.click(X(xp))
}
// ---------- TEST ----------
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

// Organizasyon seçimi
TestObject currentOrg = new TestObject().addProperty("xpath", ConditionType.EQUALS, "//div[contains(@class,'font-semibold') and contains(text(),'Organization')]//span[@class='font-thin']")
String currentOrgText = WebUI.getText(currentOrg)
if (currentOrgText != 'Mail Test') {
	TestObject orgButton = new TestObject().addProperty("xpath", ConditionType.EQUALS, "//button[.//div[contains(text(),'Organization :')]]")
	WebUI.click(orgButton)
	TestObject testCompanyOption = new TestObject().addProperty("xpath", ConditionType.EQUALS, "//button[.//div[text()='Mail Test']]")
	WebUI.click(testCompanyOption)
}
WebUI.delay(3)
WebUI.waitForPageLoad(20)


WebUI.navigateToUrl('https://platform.catchprobe.org/darkmap/quick-search')
WebUI.delay(4)
WebUI.waitForPageLoad(20)


/* 1) Arama kutusuna 'leak' yaz + Enter */
String xpSearchInput = "//input[@name='html_content' and (contains(@placeholder,'Enter a Keyword to Search') or contains(@aria-aria-describedby,'item') or contains(@class,'flex'))]"
WebUI.waitForElementVisible(X(xpSearchInput), 20)
WebUI.setText(X(xpSearchInput), "leak")
WebUI.sendKeys(X(xpSearchInput), Keys.chord(Keys.ENTER))
WebUI.waitForPageLoad(15)

/* 2) Sayfanın en altına in → 2. sayfaya tıkla */

scrollToBottomQS()
WebUI.delay(2)
String xpPage2 = "//a[normalize-space(text())='2']"
zoomOutPage()
WebUI.delay(2)
endSpam()
ensureClickableAndClick(xpPage2, 15)
WebUI.waitForPageLoad(10)

/* 3) İlk karttaki adres butonuna kadar scroll (adres metni almıyoruz, sadece pozisyonlamak için) */
String xpFirstAddrBtn = "(//button[contains(@class,'text-text-link') and contains(.,'http')])[1]"
WebUI.waitForElementVisible(X(xpFirstAddrBtn), 20)
scrollIntoView(xpFirstAddrBtn)

/* 4) Fingerprint ikonuna tıkla → yeni pencereye geç */
String xpFingerprintBtn = "(.//*[name()='svg' and contains(@class,'lucide-fingerprint')]/ancestor::button)[1]"
scrollThenClick(xpFingerprintBtn, 15)


/* yeni pencere/taba geçiş */
def driver = DriverFactory.getWebDriver()
String originalHandle = driver.getWindowHandle()
int spin = 0
while (driver.getWindowHandles().size() == 1 && spin < 20) { WebUI.delay(0.5); spin++ }
for (String h : driver.getWindowHandles()) {
    if (h != originalHandle) { driver.switchTo().window(h); break }
}
WebUI.delay(4) // içerik yüklenmesi için kısa bekleme

/* 5) Yeni pencerede 'leak' var mı kontrol et (page source, case-insensitive) */
String pageSrc = driver.getPageSource()?.toLowerCase() ?: ""
if (pageSrc.contains("leak")) {
    KeywordUtil.logInfo("🔎 Yeni pencerede 'leak' bulundu.")
} else {
    KeywordUtil.markFailed("❌ Yeni pencerede 'leak' bulunamadı.")
}

/* pencereyi kapat ve eski pencereye dön */
driver.close()
driver.switchTo().window(originalHandle)
WebUI.delay(1)
/************** Quick Search: receipt **************/
String xpReceiptBtn = "(.//*[name()='svg' and contains(@class,'lucide-receipt')]/ancestor::button)[1]"
scrollThenClick(xpReceiptBtn, 15)

/* yeni pencere/taba geçiş */
def driver2 = DriverFactory.getWebDriver()
String originalHandle2 = driver2.getWindowHandle()
int spin2 = 0
while (driver2.getWindowHandles().size() == 1 && spin2 < 20) { WebUI.delay(0.5); spin2++ }
for (String h2 : driver2.getWindowHandles()) {
	if (h2 != originalHandle2) { driver2.switchTo().window(h2); break }
}
WebUI.delay(4) // içerik yüklenmesi için kısa bekleme

/* İçerikte 'receipt' var mı kontrol et (case-insensitive) */
String pageSrc2 = driver2.getPageSource()?.toLowerCase() ?: ""
if (pageSrc2.contains("leak")) {
	KeywordUtil.logInfo("🧾 Yeni pencerede 'receipt' bulundu.")
} else {
	KeywordUtil.markFailed("❌ Yeni pencerede 'receipt' bulunamadı.")
}

/* pencereyi kapat ve geri dön */
driver2.close()
driver2.switchTo().window(originalHandle2)
WebUI.delay(1)
/************** /Quick Search: receipt **************/

