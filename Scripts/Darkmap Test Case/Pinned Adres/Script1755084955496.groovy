import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.Keys
import org.openqa.selenium.WebElement
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebElement

import org.openqa.selenium.Keys

import java.util.Locale
import java.util.Arrays          // <-- eklendi (JS click param için)
import java.util.Random
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling   // <-- OPTIONAL için şart

import java.util.List
import java.util.Random

// ========================= helpers =========================
TestObject X(String xp) {
    TestObject to = new TestObject(xp)
    to.addProperty("xpath", ConditionType.EQUALS, xp)
    return to
}

void safeClick(String xp, int t = 15) {
    if (!WebUI.waitForElementClickable(X(xp), t, FailureHandling.OPTIONAL)) {
        KeywordUtil.markFailedAndStop("❌ Tıklanabilir değil: " + xp)
    }
    WebUI.click(X(xp))
}

String safeText(String xp, int t = 15) {
    if (!WebUI.waitForElementVisible(X(xp), t, FailureHandling.OPTIONAL)) {
        KeywordUtil.markFailedAndStop("❌ Görünür değil: " + xp)
    }
    return WebUI.getText(X(xp)).trim()
}

void scrollIntoView(String xp) {
    WebElement el = WebUiCommonHelper.findWebElement(X(xp), 10)
    ((JavascriptExecutor) DriverFactory.getWebDriver())
            .executeScript("arguments[0].scrollIntoView({block:'center'});", el)
}

WebElement safeScrollTo(TestObject to) {
    if (to == null) {
        KeywordUtil.markFailed("❌ TestObject NULL")
        return null
    }
    if (!WebUI.waitForElementPresent(to, 5, FailureHandling.OPTIONAL)) return null
    WebElement el = WebUiCommonHelper.findWebElement(to, 5)
    ((JavascriptExecutor) DriverFactory.getWebDriver())
            .executeScript("arguments[0].scrollIntoView({block:'center'});", el)
    WebUI.delay(0.5)
    return el
}

boolean waitToastContains(String txt, int timeout = 10) {
    String xp = "//*[contains(@class,'ant-message') or contains(@class,'ant-notification') or contains(@class,'toast') or contains(@class,'alert')]" +
            "[not(contains(@style,'display: none'))]//*[contains(normalize-space(.), '" + txt.replace("'", "\\'") + "')]"
    return WebUI.waitForElementVisible(X(xp), timeout, FailureHandling.OPTIONAL)
}

boolean waitToast(int timeout = 8) {
    String xpToast = "//*[contains(@class,'ant-message') or contains(@class,'ant-notification') or contains(@class,'toast') or contains(@class,'alert')][not(contains(@style,'display: none'))]"
    return WebUI.waitForElementVisible(X(xpToast), timeout, FailureHandling.OPTIONAL)
}

void clearAndType(String xp, String value) {
    TestObject to = X(xp)
    WebUI.click(to)
    try {
        WebUI.clearText(to)
    } catch (Throwable ignore) {
        boolean isMac = System.getProperty("os.name")?.toLowerCase()?.contains("mac")
        Keys mod = isMac ? Keys.COMMAND : Keys.CONTROL
        WebUI.sendKeys(to, Keys.chord(mod, "a"))
        WebUI.sendKeys(to, Keys.chord(Keys.DELETE))
    }
    WebUI.setText(to, value)
}

String xpLiteral(String s) {
    return s.contains("'") ? 'concat(\'' + s.replace("'", '\',"\'' ) + '\')' : "'" + s + "'"
}

// ---------- Kart/Index yardımcıları ----------
String basePinButtonsXp() {
    return "(//button[.//*[name()='svg' and contains(@class,'lucide') and contains(@class,'pin')]])"
}
String xpPinBtnByIndex(int i) {
    return basePinButtonsXp() + "[" + i + "]"
}
String xpAddrByIndex(int i) {
    return "(//button[contains(@class,'text-text-link') and contains(.,'http')])[" + i + "]"
}
String cardRootByPinIndex(int i) {
    return "(" + xpPinBtnByIndex(i) + "/ancestor::*[contains(@class,'rounded') or contains(@class,'border') or contains(@class,'shadow')][1])"
}

boolean isButtonDisabled(WebElement btn) {
    String cls   = (btn.getAttribute("class") ?: "")
    String style = (btn.getAttribute("style") ?: "")
    String aria  = (btn.getAttribute("aria-disabled") ?: "")
    boolean disabledAttr = (btn.getAttribute("disabled") != null) || aria.equalsIgnoreCase("true")
    boolean disabledCss  = cls.contains("pointer-events-none") ||
            cls.contains("opacity-50") ||
            cls.matches("(?is).*\\bdisabled\\b.*") ||
            style.contains("pointer-events: none")
    return disabledAttr || disabledCss
}

/** basePinButtonsXp() içindeki ilk tıklanabilir pini bulur (1..maxTry). Bulursa index döner, yoksa -1 */
int findFirstClickablePinIndex(int maxTry = 12) {
    for (int i = 1; i <= maxTry; i++) {
        String xp = xpPinBtnByIndex(i)
        if (!WebUI.waitForElementPresent(X(xp), 2, FailureHandling.OPTIONAL)) break
        try {
            WebElement btn = WebUiCommonHelper.findWebElement(X(xp), 5)
            if (isButtonDisabled(btn)) continue
            if (WebUI.waitForElementClickable(X(xp), 3, FailureHandling.OPTIONAL)) return i
        } catch (Throwable ignore) { /* denemeye devam */ }
    }
    return -1
}

/** Quick Search’te kullanılacak: tıklanabilir pin’i olan ilk kartın index’i */
int pickUsableCardIndex(int maxTry = 12) {
    int idx = findFirstClickablePinIndex(maxTry)
    if (idx == -1) {
        KeywordUtil.markFailedAndStop("Tıklanabilir pin butonu bulunamadı (ilk " + maxTry + " kart confidential/disabled olabilir).")
    }
    if (idx > 1) KeywordUtil.logInfo("ℹ️ İlk sonuç(lar) confidential/disabled. " + idx + ". karta geçiliyor.")
    return idx
}

// ========================= TEST =========================
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

// Organizasyon
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

// === QUICK SEARCH ===
WebUI.navigateToUrl('https://platform.catchprobe.org/darkmap/quick-search')
WebUI.delay(4)
WebUI.waitForPageLoad(20)

// Kart seçim (ilk tıklanabilir pin’i olan kart)
int cardIdx = pickUsableCardIndex(12)

// Seçilen kartın adresi
String firstAddr = safeText(xpAddrByIndex(cardIdx))
KeywordUtil.logInfo("📌 Seçilen kart (" + cardIdx + ") adres: " + firstAddr)

// PIN
String xpPinBtn = xpPinBtnByIndex(cardIdx)
scrollIntoView(xpPinBtn)
safeClick(xpPinBtn)

// Pin/Unpin diyaloğu
String xpYesInDialog = "//button[@type='button' and normalize-space(text())='YES']"
if (WebUI.waitForElementVisible(X(xpYesInDialog), 10, FailureHandling.OPTIONAL)) {
    safeClick(xpYesInDialog)
}
// Toast (opsiyonel)
waitToastContains("Pinned", 10)
waitToastContains("Unpinned", 10)

// TAG paneli (seçili kartın içinden)
String cardRoot = cardRootByPinIndex(cardIdx)
String xpTagBtn = "(" + cardRoot + "//button[.//*[name()='svg' and contains(@class,'lucide-tag')]])[1]"
safeClick(xpTagBtn)

// Create Tag → “Katalon”
String xpCreateTagBtn = "//button[normalize-space()='Create tag' or normalize-space()='CREATE TAG']"
if (WebUI.waitForElementVisible(X(xpCreateTagBtn), 5, FailureHandling.OPTIONAL)) { safeClick(xpCreateTagBtn) }
String xpTagInput = "//input[@type='text' and (contains(@placeholder,'Tag') or contains(@aria-label,'Tag') or contains(@class,'input'))]"
WebUI.setText(X(xpTagInput), "Katalon")
String xpCreateBtn = "//button[normalize-space()='CREATE' or normalize-space()='Create']"
safeClick(xpCreateBtn)
waitToastContains("Tag created", 10)

// “Katalon” switch ON + Mark as Seen
String xpKatalonSwitch = "//*[translate(normalize-space(text()),'KATALON','katalon')='katalon']/ancestor::*[self::div or self::li][1]//*[(@role='switch' or self::button) and (@aria-checked='false' or not(@aria-checked))]"
safeClick(xpKatalonSwitch)
String xpKatalonSwitchOn = "//*[translate(normalize-space(text()),'KATALON','katalon')='katalon']/ancestor::*[self::div or self::li][1]//*[(@role='switch' or self::button)][@aria-checked='true']"
WebUI.verifyElementPresent(X(xpKatalonSwitchOn), 10)
String xpMarkSeen = "//button[normalize-space()='Mark as Seen']"
safeClick(xpMarkSeen)
waitToastContains("Tags saved", 10)

// Filter Options
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

// Kart adresi (görsel doğrulamaya dokunma)
String xpCardAddr = "(//button[contains(@class,'text-text-link') and contains(.,'http')])[1]"
WebUI.waitForElementVisible(X(xpCardAddr), 20)
String currentAddr = safeText(xpCardAddr)
KeywordUtil.logInfo("🟪 Karttaki adres: " + currentAddr)
WebUI.verifyMatch(currentAddr, firstAddr, false)

// Tag panelini tekrar aç ve switch açık mı?
safeClick(xpTagBtn)
WebUI.verifyElementPresent(X(xpKatalonSwitchOn), 10)

// Switch’i kapat → Mark as Not Seen
String xpKatalonSwitchOnClickable = "//*[translate(normalize-space(text()),'KATALON','katalon')='katalon']/ancestor::*[self::div or self::li][1]//*[(@role='switch' or self::button)][@aria-checked='true']"
safeClick(xpKatalonSwitchOnClickable)
String xpKatalonSwitchOff = "//*[translate(normalize-space(text()),'KATALON','katalon')='katalon']/ancestor::*[self::div or self::li][1]//*[(@role='switch' or self::button)][@aria-checked='false']"
WebUI.verifyElementPresent(X(xpKatalonSwitchOff), 10)
String xpMarkNotSeen = "//button[normalize-space()='Mark as Not Seen' or normalize-space()='Mark as not seen']"
safeClick(xpMarkNotSeen)
waitToastContains("Not Seen", 10)

// “No data”
String xpNoData = "//div[@class='ant-empty-description' and normalize-space(text())='No data']"
WebUI.waitForElementVisible(X(xpNoData), 20)
WebUI.verifyElementPresent(X(xpNoData), 10)

KeywordUtil.markPassed("✅ Darkmap Quick Search: pin → tag → filter → doğrulamalar tamam.")

// === PINNED ADDRESS ===
WebUI.navigateToUrl('https://platform.catchprobe.org/darkmap/pinned-address')
WebUI.delay(4)
WebUI.waitForPageLoad(20)

String xpCardAddrpin = "(//button[contains(@class,'text-text-link') and contains(.,'http')])[1]"
WebUI.waitForElementVisible(X(xpCardAddrpin), 20)
String currentAddrpin = safeText(xpCardAddrpin)
KeywordUtil.logInfo("🟪 Pinned kart adres: " + currentAddrpin)
WebUI.verifyMatch(currentAddr, currentAddrpin, false)

// Unpin (ilk tıklanabilir pin’i dene)
int pinIdxPinned = findFirstClickablePinIndex(6)   // pinned sayfasında genelde 1 olur; yine de güvenli
if (pinIdxPinned == -1) {
    KeywordUtil.markFailedAndStop("Pinned listede tıklanabilir pin bulunamadı.")
}
String xpPinBtnpin = xpPinBtnByIndex(pinIdxPinned)
scrollIntoView(xpPinBtnpin)
safeClick(xpPinBtnpin)

// Unpin YES
String xpYesInDialogpinsdr = "//button[@type='button' and normalize-space(text())='YES']"
if (WebUI.waitForElementVisible(X(xpYesInDialogpinsdr), 10, FailureHandling.OPTIONAL)) {
    safeClick(xpYesInDialogpinsdr)
}
waitToastContains("Unpinned", 10)

// “No data”
String xpNoDatapin = "//div[@class='ant-empty-description' and normalize-space(text())='No data']"
scrollIntoView(xpNoDatapin)
WebUI.waitForElementVisible(X(xpNoDatapin), 20)
WebUI.verifyElementPresent(X(xpNoDatapin), 10)
KeywordUtil.markPassed("✅ Darkmap Pinned Address: unpinned → doğrulamalar tamam.")

// === TAG MANAGEMENT ===
WebUI.navigateToUrl("https://platform.catchprobe.org/darkmap/tag-management")
WebUI.waitForPageLoad(15)

// Edit (pencil)
String xpPencilInRow = "//*[@class='lucide lucide-pencil h-4 w-4']"
scrollIntoView(xpPencilInRow)
safeClick(xpPencilInRow, 15)

// Güncelle
String xpMatchTag = "//input[@type='text' and @name='tag']"
clearAndType(xpMatchTag, "Katalon Text")
// SAVE
String xpSave = "//button[normalize-space(.)='SAVE']"
safeClick(xpSave, 15)
if (!waitToast(8)) KeywordUtil.markWarning("Başarı bildirimi görünmedi (SAVE).")

// İlk Tag değerini al
String xpTagSpanFirst = "(//td[contains(@class,'ant-table-cell')]/span)[1]"
String tagVal = safeText(xpTagSpanFirst, 15)
KeywordUtil.logInfo("Güncel Tag: " + tagVal)

// Aynı satırdaki göz
String xpRowByTag = "//tr[.//td[contains(@class,'ant-table-cell')]/span[normalize-space(.)=" + xpLiteral(tagVal) + "]]"
String xpEyeInRow = xpRowByTag + "//*[@class='lucide lucide-eye h-4 w-4']"
if (!WebUI.waitForElementPresent(X(xpEyeInRow), 5, FailureHandling.OPTIONAL)) {
    xpEyeInRow = "(//*[@class='lucide lucide-eye h-4 w-4'])[1]"
}
scrollIntoView(xpEyeInRow)
safeClick(xpEyeInRow, 15)

// Pop-up doğrulama (görsel XPath’ına dokunmuyoruz)
List<String> popupTexts = (List<String>) WebUI.executeJavaScript(
        "return Array.from(document.querySelectorAll('span.px-2')).map(e=>e.textContent.trim());", []
)
boolean matched = popupTexts.any { it?.toLowerCase()?.contains(tagVal.toLowerCase()) }
if (!matched) {
    KeywordUtil.markFailed("Pop-up metinleri Tag değerini içermiyor. Tag: '" + tagVal + "' | Pop-up: " + popupTexts)
} else {
    KeywordUtil.logInfo("✅ Pop-up doğrulandı. Tag '" + tagVal + "' bulundu.")
}

// DELETE
String xpDelete = "//*[@class='lucide lucide-trash2 h-4 w-4']"
safeClick(xpDelete, 10)

String xpDeleteText = "//button[normalize-space(.)='DELETE']"
if (!WebUI.waitForElementVisible(X(xpDeleteText), 8, FailureHandling.OPTIONAL))
    KeywordUtil.markFailedAndStop("Delete onayı görünmedi.")

String xpDeleteBtn = "//button[normalize-space(.)='DELETE']"
safeClick(xpDeleteBtn, 10)
waitToast(8)
if (!waitToastContains("Tag deleted successfully", 12))
    KeywordUtil.markFailed("Onay toast'ı birebir gelmedi.")

KeywordUtil.markPassed("✅ Tag Management testi başarıyla tamamlandı.")
