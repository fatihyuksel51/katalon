import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import com.kms.katalon.core.webui.driver.DriverFactory
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebElement
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
    def el = WebUiCommonHelper.findWebElement(X(xp), 10)
    ((JavascriptExecutor)DriverFactory.getWebDriver()).executeScript("arguments[0].scrollIntoView({block:'center'});", el)
}
// ✅ Güvenli scroll fonksiyonu (Repository objeleri için)
WebElement safeScrollTo(TestObject to) {
    if (to == null) {
        KeywordUtil.markFailed("❌ TestObject NULL – Repository yolunu kontrol et.")
        return null
    }
    if (!WebUI.waitForElementPresent(to, 5, FailureHandling.OPTIONAL)) {
        KeywordUtil.logInfo("ℹ️ Element not present, scroll atlandı: ${to.getObjectId()}")
        return null
    }
    WebElement element = WebUiCommonHelper.findWebElement(to, 5)
    JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getWebDriver()
    js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element)
    WebUI.delay(0.5)
    return element
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

// 1) Quick Search adresi
String xpAddrBtn = "//button[contains(@class,'text-text-link') and contains(.,'http')]"
WebUI.waitForElementClickable(X(xpAddrBtn), 20)
String firstAddr = safeText(xpAddrBtn)
KeywordUtil.logInfo("📌 İlk adres: " + firstAddr)

// 2) Pin
String xpPinBtn = "(.//*[name()='svg' and contains(@class,'lucide-pin')]/ancestor::button)[1]"
scrollIntoView(xpPinBtn)
safeClick(xpPinBtn)

// 🔔 Pin -> Unpin confirm popup'ı çıkarsa YES'e bas
String xpYesInDialog = "//button[@type='button' and normalize-space(text())='YES']"
if (WebUI.waitForElementVisible(X(xpYesInDialog), 10, FailureHandling.OPTIONAL)) {
    safeClick(xpYesInDialog)
}

// her iki durumda da olası toast'ları opsiyonel bekle
waitToast("Pinned", 10)
waitToast("Unpinned", 10)


// 3) Tag
String xpTagBtn = "(.//*[name()='svg' and contains(@class,'lucide-tag')]/ancestor::button)[1]"
safeClick(xpTagBtn)

// 4) Create Tag → “Katalon”
String xpCreateTagBtn = "//button[normalize-space()='Create tag' or normalize-space()='CREATE TAG']"
if (WebUI.waitForElementVisible(X(xpCreateTagBtn), 5, FailureHandling.OPTIONAL)) { safeClick(xpCreateTagBtn) }
String xpTagInput = "//input[@type='text' and (contains(@placeholder,'Tag') or contains(@aria-label,'Tag') or contains(@class,'input'))]"
WebUI.setText(X(xpTagInput), "Katalon")
String xpCreateBtn = "//button[normalize-space()='CREATE' or normalize-space()='Create']"
safeClick(xpCreateBtn)
waitToast("Tag created")

// 5) “Katalon” switch ON + Mark as Seen
String xpKatalonSwitch = "//*[translate(normalize-space(text()),'KATALON','katalon')='katalon']/ancestor::*[self::div or self::li][1]//*[(@role='switch' or self::button) and (@aria-checked='false' or not(@aria-checked))]"
safeClick(xpKatalonSwitch)
String xpKatalonSwitchOn = "//*[translate(normalize-space(text()),'KATALON','katalon')='katalon']/ancestor::*[self::div or self::li][1]//*[(@role='switch' or self::button)][@aria-checked='true']"
WebUI.verifyElementPresent(X(xpKatalonSwitchOn), 10)
String xpMarkSeen = "//button[normalize-space()='Mark as Seen']"
safeClick(xpMarkSeen)
waitToast("Tags saved")

// ---------------------
// 6) (İSTENEN WAIT) Filter Options'a geçmeden önce 20 sn'lik explicit wait
// ---------------------
String xpFilterOpts = "//button[normalize-space()='FILTER OPTIONS' or contains(translate(normalize-space(.),'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'FILTER OPTIONS')]"
WebUI.waitForElementClickable(X(xpFilterOpts), 20)
safeClick(xpFilterOpts)

String xpSelectTags = "//button[.//span[normalize-space(text())='Select Tags'] or contains(@class,'select')][1]"
safeClick(xpSelectTags)
String xpTagKatalonInList = "//*[contains(@role,'option') or self::div or self::button][normalize-space()='Katalon' or normalize-space()='katalon']"
safeClick(xpTagKatalonInList)
String xpApplySearch = "//button[normalize-space()='APPLY AND SEARCH' or normalize-space()='Apply & Search' or normalize-space()='Apply and Search']"
safeClick(xpApplySearch)
WebUI.waitForPageLoad(15)

// ---------------------
// 7) (İSTENEN WAIT) Kart adresini okumadan önce 20 sn görünürlük bekle
// ---------------------
String xpCardAddr = "(//button[contains(@class,'text-text-link') and contains(.,'http')])[1]"
WebUI.waitForElementVisible(X(xpCardAddr), 20)
String currentAddr = safeText(xpCardAddr)
KeywordUtil.logInfo("🟪 Karttaki adres: " + currentAddr)
WebUI.verifyMatch(currentAddr, firstAddr, false)

// 8) Tag panelini yeniden aç ve switch açık mı kontrol et
safeClick(xpTagBtn)
WebUI.verifyElementPresent(X(xpKatalonSwitchOn), 10)

// 9) Switch’i kapat → Mark as Not Seen
String xpKatalonSwitchOnClickable = "//*[translate(normalize-space(text()),'KATALON','katalon')='katalon']/ancestor::*[self::div or self::li][1]//*[(@role='switch' or self::button)][@aria-checked='true']"
safeClick(xpKatalonSwitchOnClickable)
String xpKatalonSwitchOff = "//*[translate(normalize-space(text()),'KATALON','katalon')='katalon']/ancestor::*[self::div or self::li][1]//*[(@role='switch' or self::button)][@aria-checked='false']"
WebUI.verifyElementPresent(X(xpKatalonSwitchOff), 10)
String xpMarkNotSeen = "//button[normalize-space()='Mark as Not Seen' or normalize-space()='Mark as not seen']"
safeClick(xpMarkNotSeen)
waitToast("Not Seen")

// ---------------------
// 10) (İSTENEN WAIT) “No data” görünmeden doğrulama yapma → 20 sn bekle
// ---------------------
String xpNoData = "//div[@class='ant-empty-description' and normalize-space(text())='No data']"
WebUI.waitForElementVisible(X(xpNoData), 20)
WebUI.verifyElementPresent(X(xpNoData), 10)

KeywordUtil.markPassed("✅ Darkmap Quick Search: pin → tag → filter → doğrulamalar tamam.")



WebUI.navigateToUrl('https://platform.catchprobe.org/darkmap/pinned-address')
WebUI.delay(4)
WebUI.waitForPageLoad(20)

// 11) (İSTENEN WAIT) Kart adresini okumadan önce 20 sn görünürlük bekle
// ---------------------
String xpCardAddrpin = "(//button[contains(@class,'text-text-link') and contains(.,'http')])[1]"
WebUI.waitForElementVisible(X(xpCardAddrpin), 20)
String currentAddrpin = safeText(xpCardAddrpin)
KeywordUtil.logInfo("🟪 Karttaki adres: " + currentAddrpin)
WebUI.verifyMatch(currentAddr, currentAddrpin, false)

// 12) Pin
String xpPinBtnpin = "(.//*[name()='svg' and contains(@class,'lucide-pin')]/ancestor::button)[1]"
scrollIntoView(xpPinBtnpin)
safeClick(xpPinBtnpin)

// 🔔 Pin -> Unpin confirm popup'ı çıkarsa YES'e bas
String xpYesInDialogpinsdr = "//button[@type='button' and normalize-space(text())='YES']"
if (WebUI.waitForElementVisible(X(xpYesInDialog), 10, FailureHandling.OPTIONAL)) {
	safeClick(xpYesInDialogpinsdr)
}

// her iki durumda da olası toast'ları opsiyonel bekle
waitToast("Unpinned", 10)

// 13) (İSTENEN WAIT) “No data” görünmeden doğrulama yapma → 20 sn bekle
// ---------------------
String xpNoDatapin = "//div[@class='ant-empty-description' and normalize-space(text())='No data']"
scrollIntoView(xpNoDatapin)
WebUI.waitForElementVisible(X(xpNoDatapin), 20)
WebUI.verifyElementPresent(X(xpNoDatapin), 10)

KeywordUtil.markPassed("✅ Darkmap Pinned Adres: →pinned → doğrulamalar tamam.")

