import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.model.FailureHandling
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebElement
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.util.KeywordUtil

import com.kms.katalon.core.model.FailureHandling
// ---------- küçük yardımcılar ----------
TestObject X(String xp) {
    TestObject to = new TestObject(xp)
    to.addProperty("xpath", ConditionType.EQUALS, xp)
    return to
}
JavascriptExecutor js() { (JavascriptExecutor) DriverFactory.getWebDriver() }

boolean clickWithFallback(TestObject to, int t=8) {
    try {
        if (WebUI.waitForElementClickable(to, t, FailureHandling.OPTIONAL)) {
            WebUI.click(to); return true
        }
    } catch (ignored) {}
    try {
        WebElement el = WebUiCommonHelper.findWebElement(to, t)
        js().executeScript("arguments[0].scrollIntoView({block:'center'});", el)
        js().executeScript("arguments[0].click()", el)
        return true
    } catch (e) {
        KeywordUtil.logInfo("clickWithFallback failed: " + e.getMessage())
        return false
    }
}

boolean waitTextEquals(TestObject to, String expected, int timeoutSec=20) {
    long end = System.currentTimeMillis() + timeoutSec*1000
    while (System.currentTimeMillis() < end) {
        try {
            String txt = WebUI.getText(to).trim()
            if (txt.equalsIgnoreCase(expected)) return true
        } catch (ignored) {}
        WebUI.delay(0.25)
    }
    return false
}

// ---------- oturum garanti altına al (tarayıcı kapalıysa aç + login bloğunu senin ortamına göre doldur) ----------
void ensureLoggedIn() {
    boolean needLogin = false
    try { DriverFactory.getWebDriver() } catch (Throwable t) { needLogin = true }
    if (!needLogin) return

WebUI.openBrowser('')
WebUI.maximizeWindow()

WebUI.navigateToUrl('https://platform.catchprobe.org/')

// Login adımları
WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'), 30)
WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'))

WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 'katalon.test@catchprobe.com')
WebUI.setEncryptedText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Password_password'), 'RigbBhfdqOBDK95asqKeHw==')
WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Sign in'))

WebUI.delay(5)

// Random OTP
def randomOtp = (100000 + new Random().nextInt(900000)).toString()
WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'), randomOtp)
WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Verify'))
WebUI.delay(2)
//

WebUI.navigateToUrl('https://platform.catchprobe.org/riskroute')
WebUI.waitForPageLoad(10)
}

// ---------- SAĞLAM organizasyon değiştirme ----------
boolean switchOrganization(String targetOrg, int timeoutSec=20) {
    TestObject orgValue = X("//div[contains(@class,'font-semibold') and contains(normalize-space(.),'Organization')]" +
                            "//span[contains(@class,'font-thin')]")

    if (!WebUI.waitForElementVisible(orgValue, 10, FailureHandling.OPTIONAL)) {
        KeywordUtil.markWarning("Org etiketi görünür değil; sayfa henüz yüklenmemiş olabilir.")
    }

    String current = ""
    try { current = WebUI.getText(orgValue).trim() } catch (ignored) {}

    if (current.equalsIgnoreCase(targetOrg)) {
        KeywordUtil.logInfo("✅ Zaten '${targetOrg}' organizasyonundasın.")
        return true
    }

    TestObject orgBtn = X("//button[.//div[contains(normalize-space(.),'Organization :')]]")
    // menüyü kesin aç
    for (int i=0; i<3; i++) {
        if (clickWithFallback(orgBtn, 8)) { WebUI.delay(0.2); break }
        WebUI.delay(0.3)
    }

    // seçenek locatorda 3 varyasyon: button > div, role=menuitem, düz button text
    String choiceXp = "(" +
        "//button[.//div[normalize-space(text())='"+targetOrg+"']] | " +
        "//*[@role='menuitem' and normalize-space(text())='"+targetOrg+"'] | " +
        "//button[normalize-space(.)='"+targetOrg+"']" +
        ")[1]"
    TestObject choice = X(choiceXp)

    // menü yavaşsa birkaç deneme
    long end = System.currentTimeMillis() + timeoutSec*1000
    boolean clicked = false
    while (System.currentTimeMillis() < end) {
        if (WebUI.verifyElementVisible(choice, 1, FailureHandling.OPTIONAL)) {
            clicked = clickWithFallback(choice, 5)
            if (clicked) break
        } else {
            // menüde değilse biraz scroll dene (varsa)
            try {
                WebElement el = WebUiCommonHelper.findWebElement(choice, 2)
                js().executeScript("arguments[0].scrollIntoView({block:'nearest'});", el)
            } catch (ignored) {}
        }
        WebUI.delay(0.3)
    }

    if (!clicked) {
        KeywordUtil.markFailed("❌ '${targetOrg}' seçeneği tıklanamadı.")
        return false
    }

    // başlıktaki org değeri değişene kadar bekle
    if (!waitTextEquals(orgValue, targetOrg, timeoutSec)) {
        KeywordUtil.markFailed("❌ Organization '${targetOrg}' seçimi görünür olmadı (header güncellenmedi).")
        return false
    }

    KeywordUtil.logInfo("✅ Organization '${targetOrg}' seçildi.")
    return true
}
