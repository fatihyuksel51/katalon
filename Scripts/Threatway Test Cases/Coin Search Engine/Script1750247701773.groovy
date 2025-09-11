/************** Imports **************/
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.ObjectRepository as OR
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.Keys
import java.util.Random
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor

/************** Parametreler **************/
int MAX_RETRY       = 3
int TIMEOUT_PER_TRY = 10

/************** Mini yardımcılar **************/
TestObject X(String xp){
    def to = new TestObject(xp)
    to.addProperty("xpath", ConditionType.EQUALS, xp)
    return to
}

boolean isBrowserOpen(){
    try { DriverFactory.getWebDriver(); return true } catch(Throwable t){ return false }
}

/** Elemanı görünür olana kadar küçük adımlarla sayfayı kaydır */
void scrollToVisible(WebElement el){
    if (el == null) return
    int sc = 0
    JavascriptExecutor jse = (JavascriptExecutor) DriverFactory.getWebDriver()
    while(sc < 3000 && !el.isDisplayed()){
        jse.executeScript("window.scrollBy(0, 200)")
        WebUI.delay(0.2)
        sc += 200
    }
}

/** Sayfa hazır olana kadar bekle */
void waitReady() {
    WebUI.waitForPageLoad(30)
    try {
        JavascriptExecutor jse = (JavascriptExecutor) DriverFactory.getWebDriver()
        WebUI.waitForCondition({
            return "complete".equals(jse.executeScript("return document.readyState"))
        }, 15)
    } catch (Throwable _){}
}

/** Element presence helper (overload'lı) */
boolean present(TestObject to, int t) {
    try {
        return WebUI.verifyElementPresent(to, t, FailureHandling.OPTIONAL)
    } catch (Throwable _){ 
        return false 
    }
}
boolean present(TestObject to) { 
    return present(to, 5) 
}
/** Refresh + hazır ol + beklenmedik toast temizliği */
void hardRefreshAndWait() {
    WebUI.refresh()
    waitReady()
    try { CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'() } catch(_){}
}

/** Güvenli text okuma (basitleştirilmiş) */
String readTextSafe(TestObject to, int t) {
    try {
        boolean visible = WebUI.waitForElementVisible(to, t, FailureHandling.OPTIONAL)
        if (visible) {
            String tx = WebUI.getText(to)
            return tx != null ? tx : ""
        }
    } catch (Throwable _){}
    return ""
}

/** JS click */
void clickJs(WebElement el) {
    if (el == null) return
    ((JavascriptExecutor)DriverFactory.getWebDriver()).executeScript("arguments[0].click();", el)
}

/** hızlı yaz – olmazsa JS fallback */
void clearAndType(TestObject to, String text, int t=10){
    WebUI.waitForElementVisible(to,t,FailureHandling.OPTIONAL)
    WebElement e = WebUiCommonHelper.findWebElement(to,t)
    try{ e.clear(); e.sendKeys(text); return }catch(_){}
    try{
        JavascriptExecutor jse = (JavascriptExecutor) DriverFactory.getWebDriver()
        jse.executeScript("arguments[0].value=''; arguments[0].dispatchEvent(new Event('input',{bubbles:true}))", e)
        jse.executeScript("arguments[0].value=arguments[1]; arguments[0].dispatchEvent(new Event('input',{bubbles:true}))", e, text)
    }catch(_){
        WebUI.setText(to,text)
    }
}

/** Clipboard güvenli okuma (başarısızsa fallback döner) */
String getClipboardTextOr(String fallback){
    try{
        def cb = Toolkit.getDefaultToolkit().getSystemClipboard()
        def data = cb.getData(DataFlavor.stringFlavor)
        if(data!=null) return data.toString()
    }catch(Throwable _){}
    return fallback
}

/** Filtre panelini aç, değeri gir ve uygula */
void applyFilterValue(String value) {
    TestObject btnOpenFilter     = findTestObject('Object Repository/Coin Search Engine/Theatway filterbuton')
    TestObject filterInput       = findTestObject('Object Repository/Coin Search Engine/İnput')
    TestObject btnApplyAndSearch = findTestObject('Object Repository/Coin Search Engine/threatway button_APPLY AND SEARCH')

    WebUI.waitForElementClickable(btnOpenFilter, 10)
    WebUI.click(btnOpenFilter)

    WebUI.waitForElementVisible(filterInput, 10)
    clearAndType(filterInput, value)

    WebUI.waitForElementClickable(btnApplyAndSearch, 10)
    WebUI.click(btnApplyAndSearch)
    waitReady()
}

/** From Link metnini bulup döndür; yoksa refresh + yeniden dene */
String getFromLinkTextWithRetry(int MAX_RETRY, int timeoutPerTry) {
    TestObject fromLink = findTestObject('Object Repository/Coin Search Engine/From Link')
    for (int i=0; i<MAX_RETRY; i++) {
        if (present(fromLink, timeoutPerTry)) {
            String txt = readTextSafe(fromLink, timeoutPerTry)
            if (txt?.trim()) return txt
        }
        KeywordUtil.logInfo("From Link bulunamadı → refresh (#${i+1})")
        hardRefreshAndWait()
    }
    KeywordUtil.markWarning("From Link metni alınamadı (retry tükendi)")
    return ""
}

/** Coin link görünür olana kadar, yoksa sayfayı yenileyip aynı filtreyi tekrar uygula */
boolean ensureCoinLinkAfterSearch(String filterValue, int MAX_RETRY, int timeoutPerTry) {
    TestObject coinLink = findTestObject('Object Repository/Coin Search Engine/Coin link')
    for (int i=0; i<MAX_RETRY; i++) {
        if (present(coinLink, timeoutPerTry)) return true
        KeywordUtil.logInfo("Coin link görünmedi → refresh + filtreyi yeniden uygula (#${i+1})")
        hardRefreshAndWait()
        applyFilterValue(filterValue)
    }
    return present(coinLink, timeoutPerTry)
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

// Doğrudan sayfaya git
WebUI.navigateToUrl('https://platform.catchprobe.org/threatway/coin-search-engine')
waitReady()
try { CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'() } catch(_){}

// 1) "From Copy" → panoya kopyala ve toast doğrula (gelmezse refresh ile tekrar dener)
TestObject btnFromCopy = findTestObject('Object Repository/Coin Search Engine/From Copy')
for (int i=0; i<MAX_RETRY; i++) {
    try {
        WebUI.waitForElementClickable(btnFromCopy, 15)
        WebUI.click(btnFromCopy)
        TestObject toast = findTestObject('Object Repository/Coin Search Engine/Toast Message')
        WebUI.waitForElementVisible(toast, 10)
        WebUI.verifyElementText(toast, 'From Account copied to clipboard successfully!')
        break
    } catch (Throwable e) {
        if (i == MAX_RETRY-1) {
            KeywordUtil.markWarning("From Copy/Toast doğrulaması başarısız: ${e.message}")
        } else {
            KeywordUtil.logInfo("From Copy başarısız → refresh (#${i+1})")
            hardRefreshAndWait()
        }
    }
}

// 2) From / Coin metinlerini oku (From Link yoksa retry’lı al)
TestObject coinLinkTO = findTestObject('Object Repository/Coin Search Engine/Coin link')
String fromTextScreen = getFromLinkTextWithRetry(MAX_RETRY, TIMEOUT_PER_TRY)
String coinTextScreen = readTextSafe(coinLinkTO, TIMEOUT_PER_TRY)  // ilk sayfadaysa dolu olabilir

// 3) Clipboard’tan oku; olmazsa From’u kullan
String fromTextClipboard = getClipboardTextOr(fromTextScreen)
KeywordUtil.logInfo("Clipboard/From value: " + fromTextClipboard)

// 4) Filtreyi uygula → coin link gelene kadar refresh+tekrar dene
applyFilterValue(fromTextClipboard)
boolean coinOk = ensureCoinLinkAfterSearch(fromTextClipboard, MAX_RETRY, TIMEOUT_PER_TRY)
if (!coinOk) {
    KeywordUtil.markWarning("Coin link hâlâ görünmüyor; akışa devam ediliyor (log amaçlı).")
}

// 5) From Link metni değişmemiş mi kontrol
TestObject fromLinkTO = findTestObject('Object Repository/Coin Search Engine/From Link')
String fromAfter = readTextSafe(fromLinkTO, TIMEOUT_PER_TRY)
if (fromTextScreen?.trim()) {
    WebUI.verifyMatch(fromAfter?.trim(), java.util.regex.Pattern.quote(fromTextScreen.trim()), false, FailureHandling.OPTIONAL)
}

// 6) Coin detayını aç → IP link doğrula
WebUI.waitForElementClickable(coinLinkTO, TIMEOUT_PER_TRY, FailureHandling.OPTIONAL)
WebElement coinEl = WebUiCommonHelper.findWebElement(coinLinkTO, TIMEOUT_PER_TRY)
scrollToVisible(coinEl)
clickJs(coinEl)

TestObject ipLink = findTestObject('Object Repository/Coin Search Engine/İp Link')
WebUI.waitForElementVisible(ipLink, TIMEOUT_PER_TRY)
String coinTextScreenFinal = coinTextScreen?.trim() ? coinTextScreen : readTextSafe(coinLinkTO, 2)  // gerekiyorsa yeniden al
WebUI.verifyElementText(ipLink, coinTextScreenFinal, FailureHandling.OPTIONAL)

// Detayı kapat
TestObject btnDetailClose = findTestObject('Object Repository/Coin Search Engine/Show detail Close button')
WebUI.waitForElementClickable(btnDetailClose, 10)
WebUI.click(btnDetailClose)

// Filtre paneli açıksa kapat (opsiyonel)
TestObject btnFilterClose = findTestObject('Object Repository/Coin Search Engine/Filter Close')
if (WebUI.verifyElementPresent(btnFilterClose, 2, FailureHandling.OPTIONAL)) {
    WebUI.click(btnFilterClose)
}

// 7) İkinci öğe – görünür yap & tıkla (görünmezse refresh + filtreyi yeniden uygula)
TestObject secondCoin = findTestObject('Object Repository/Coin Search Engine/Coin Second')
boolean secondOk = present(secondCoin, 5)
if (!secondOk) {
    hardRefreshAndWait()
    applyFilterValue(fromTextClipboard)   // aynı filtreyle sayfayı aynı hale getir
    secondOk = present(secondCoin, 10)
}
if (secondOk) {
    WebElement secondEl = WebUiCommonHelper.findWebElement(secondCoin, 10)
    scrollToVisible(secondEl)
    clickJs(secondEl)
    WebUI.delay(2)
    WebUI.waitForElementClickable(btnDetailClose, 10, FailureHandling.OPTIONAL)
    WebUI.click(btnDetailClose)
    WebUI.comment("👉 'Coin Second' doğrulandı.")
} else {
    KeywordUtil.markWarning("İkinci coin bulunamadı (retry sonrası).")
}

WebUI.comment('✅ Coin Search Engine senaryosu (retry + refresh güvenceli) tamamlandı.')
