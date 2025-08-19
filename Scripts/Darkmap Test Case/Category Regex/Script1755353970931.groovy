import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import org.openqa.selenium.Keys
import org.openqa.selenium.WebElement

import java.util.Locale
import java.util.Arrays          // <-- eklendi (JS click param için)
import java.util.Random          // <-- eklendi (OTP için)

/* ============ Mini yardımcılar ============ */
TestObject X(String xp){
    def to = new TestObject(xp)
    to.addProperty("xpath", ConditionType.EQUALS, xp)
    return to
}

void safeClick(String xp, int t=15){
    if (!WebUI.waitForElementClickable(X(xp), t, FailureHandling.OPTIONAL)) {
        KeywordUtil.markFailedAndStop("Clickable değil: " + xp)
    }
    try {
        WebUI.click(X(xp))
    } catch (Throwable e) {
        WebElement el = WebUiCommonHelper.findWebElement(X(xp), 3)
        try { WebUI.executeJavaScript("arguments[0].scrollIntoView({block:'center'});", Arrays.asList(el)) } catch (Throwable ignore) {}
        WebUI.executeJavaScript("arguments[0].click();", Arrays.asList(el))
    }
}
void scrollIntoViewXp(String xp){
	try {
		WebElement el = WebUiCommonHelper.findWebElement(X(xp), 5)
		WebUI.executeJavaScript("arguments[0].scrollIntoView({block:'center'});", Arrays.asList(el))
	} catch (Throwable ignore) {
		// eleman henüz bulunamazsa en alta kaydır, sonra wait çalışacak
		WebUI.executeJavaScript("window.scrollTo(0, document.body.scrollHeight);", null)
	}
	WebUI.delay(0.3)
}


String safeText(String xp, int t=10){
    if (!WebUI.waitForElementVisible(X(xp), t, FailureHandling.OPTIONAL)) {
        KeywordUtil.markFailedAndStop("Görünür değil: " + xp)
    }
    return WebUI.getText(X(xp)).trim()
}

void clearAndType(String xp, String value) {
    def to = X(xp)
    WebUI.click(to)
    // Önce normal clear
    try {
        WebUI.clearText(to)
    } catch (Throwable ignore) {
        // Klavye fallback (Mac/Win)
        boolean isMac = System.getProperty("os.name")?.toLowerCase()?.contains("mac")
        def mod = isMac ? Keys.COMMAND : Keys.CONTROL
        WebUI.sendKeys(to, Keys.chord(mod, "a"))
        WebUI.sendKeys(to, Keys.chord(Keys.DELETE))   // BACK_SPACE yerine DELETE daha stabil
    }
    WebUI.setText(to, value)
}

boolean waitToast(int timeout=8){
    String xpToast = "//*[contains(@class,'ant-message') or contains(@class,'ant-notification') or contains(@class,'toast') or contains(@class,'alert')][not(contains(@style,'display: none'))]"
    return WebUI.waitForElementVisible(X(xpToast), timeout, FailureHandling.OPTIONAL)
}

/** Combobox: butona tıkla, filtre tuşlarını gönder, hedef seçeneği seç (locale güvenli) */
void selectFromCombo(String btnXp, String filterKeys, String expectedText) {
    safeClick(btnXp, 15)
    WebUI.delay(0.2)

    if (filterKeys != null && !filterKeys.isEmpty()) {
        try { WebUI.sendKeys(X("//body"), filterKeys) } catch (Throwable ignore) {}
        WebUI.delay(0.3)
    }

    String low   = expectedText.toLowerCase(Locale.ROOT) // TR locale bug fix
    String xpBase= "//*[@role='option' or contains(@class,'option') or contains(@class,'ant-select-item') or contains(@class,'dropdown') or @role='menuitem']"
    String xpEq  = xpBase + "[normalize-space(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'))='" + low + "']"
    String xpC   = xpBase + "[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'" + low + "')]"

    if (!WebUI.waitForElementVisible(X(xpEq), 2, FailureHandling.OPTIONAL)) {
        safeClick(xpC, 10)
    } else {
        safeClick(xpEq, 10)
    }
}

/* ============ Test akışı ============ */
WebUI.openBrowser('')
WebUI.navigateToUrl('https://platform.catchprobe.org/')
WebUI.maximizeWindow()

// ---- Login
WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'), 30)
WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'))
WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 30)
WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 'fatih.yuksel@catchprobe.com')
WebUI.setEncryptedText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Password_password'), 'RigbBhfdqOBDK95asqKeHw==')
WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Sign in'))
WebUI.delay(2)
String randomOtp = (100000 + new Random().nextInt(900000)).toString()
WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'), randomOtp)
WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Verify'))
WebUI.delay(12)
WebUI.waitForPageLoad(15)

// ---- Sayfaya git
WebUI.navigateToUrl('https://platform.catchprobe.org/darkmap/category-regex')
WebUI.waitForPageLoad(20)

// 1) CREATE
String xpCreate1 = "//button[normalize-space(.)='CREATE']"
safeClick(xpCreate1, 15)

// 2) Category → Hacking
String xpCatCombo = "//button[@type='button' and @role='combobox' and .//span[normalize-space(.)='Choose a Category']]"
selectFromCombo(xpCatCombo, "h", "Hacking")

// 3) Regex Type → IPV4
String xpRegexTypeCombo = "//button[@type='button' and @role='combobox' and .//span[normalize-space(.)='Choose a Regex Type']]"
selectFromCombo(xpRegexTypeCombo, "i", "IPV4")

// 4) Input’lar
String xpMatchRegex   = "//input[@type='text' and @placeholder='Add Matching Regex']"
String xpMatchKeyword = "//input[@type='text' and @placeholder='Add Matching Keyword']"
clearAndType(xpMatchRegex,   "1.1.1.1")
clearAndType(xpMatchKeyword, "katalon")

// 5) CREATE (modal)
String xpCreate2 = "(//button[normalize-space(.)='CREATE'])[2]"
safeClick(xpCreate2, 15)
if (!waitToast(8)) KeywordUtil.markWarning("Başarı bildirimi görünmedi (CREATE).")

// 6) Tablo doğrulaması (case-insensitive ve Locale.ROOT ile)
String xpRow2 = "//tbody[@class='ant-table-tbody']//tr[2]"
if (!WebUI.waitForElementVisible(X(xpRow2), 10, FailureHandling.OPTIONAL))
    KeywordUtil.markFailedAndStop("Tablo satırı görünmedi.")
String row2Txt   = safeText(xpRow2, 10)
String row2Lower = row2Txt.toLowerCase(Locale.ROOT)
if (!(row2Lower.contains("hacking")
   && row2Lower.contains("1.1.1.1")
   && row2Lower.contains("katalon"))) {
    KeywordUtil.markFailed("Tablo beklenen değerleri içermiyor: " + row2Txt)
}

// 7) Edit (katalon satırı)
String xpEditBtn = "//tr[.//span[text()='katalon']]//div[contains(@class,'bg-warning')]"
safeClick(xpEditBtn, 15)

// 8) Güncelle + alarm OFF
clearAndType(xpMatchRegex,   "2.2.2.2")
clearAndType(xpMatchKeyword, "testops")

String xpAlarmOn  = "//button[@role='switch' and @aria-checked='true']"
if (WebUI.waitForElementVisible(X(xpAlarmOn), 5, FailureHandling.OPTIONAL)) {
    safeClick(xpAlarmOn, 10)
    String xpAlarmOff = "//button[@role='switch' and @aria-checked='false']"
    if (!WebUI.waitForElementVisible(X(xpAlarmOff), 5, FailureHandling.OPTIONAL))
        KeywordUtil.markFailed("Alarm switch OFF durumuna geçmedi.")
}

// 9) SAVE
String xpSave = "//button[normalize-space(.)='SAVE']"
safeClick(xpSave, 15)
if (!waitToast(8)) KeywordUtil.markWarning("Başarı bildirimi görünmedi (SAVE).")

// 10) Güncellenen satır kontrolleri
String xpEditedRow = "//tbody[@class='ant-table-tbody']//tr[2]"
if (!WebUI.waitForElementVisible(X(xpEditedRow), 10, FailureHandling.OPTIONAL))
    KeywordUtil.markFailedAndStop("Güncellenen satır (TESTOPS) bulunamadı.")
String editedTxt = safeText(xpEditedRow, 10)
if (!editedTxt.toUpperCase(Locale.ROOT).contains("CLOSED"))
    KeywordUtil.markFailed("Alarm status 'Closed' değil: " + editedTxt)
if (!editedTxt.toUpperCase(Locale.ROOT).contains("TESTOPS"))
    KeywordUtil.markFailed("Matching Keyword büyük harfli değil (TESTOPS bekleniyordu): " + editedTxt)
if (!editedTxt.contains("2.2.2.2"))
    KeywordUtil.markFailed("Son eklenen regex görünmüyor (2.2.2.2): " + editedTxt)

// 11) Sil → Delete → No data
String xpTrash = "//div[contains(@class,'bg-destructive') and contains(@class,'cursor-pointer')]"
safeClick(xpTrash, 15)

String xpDeleteText = "//button[normalize-space(.)='DELETE']"
if (!WebUI.waitForElementVisible(X(xpDeleteText), 8, FailureHandling.OPTIONAL))
    KeywordUtil.markFailedAndStop("Delete onayı görünmedi.")

String xpDeleteBtn = "//button[normalize-space(.)='DELETE']"
safeClick(xpDeleteBtn, 10)
waitToast(8)

String xpNoData = "//div[@class='ant-empty-description' and normalize-space(text())='No data']"
scrollIntoViewXp(xpNoData)
if (!WebUI.waitForElementVisible(X(xpNoData), 10, FailureHandling.OPTIONAL))
    KeywordUtil.markFailed("No data bekleniyordu, görünmedi.")
else
    KeywordUtil.logInfo("✅ Silme sonrası 'No data' doğrulandı.")
