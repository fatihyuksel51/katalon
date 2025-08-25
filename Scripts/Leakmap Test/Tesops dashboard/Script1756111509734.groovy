/***************************************
 * Leakmap Dashboard — TestOps Ready
 * - ensureSession(): driver yoksa açar, login + OTP
 * - (Opsiyonel) ensureOrg(): org değişimini sağlam yapar
 * - Hızlı JS click/scroll helper’lar
 * - Mail kontrol: INBOX temizle + polling
 ***************************************/
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebElement
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.interactions.Actions

import com.catchprobe.utils.MailReader   // mevcut projedeki util

/************ Helpers ************/
TestObject X(String xp){ def to=new TestObject(xp); to.addProperty("xpath", ConditionType.EQUALS, xp); return to }
JavascriptExecutor js(){ (JavascriptExecutor) DriverFactory.getWebDriver() }

boolean isBrowserOpen() {
    try { DriverFactory.getWebDriver(); return true } catch(Throwable t) { return false }
}

void jsScrollIntoViewTO(TestObject to, int t=8){
    WebElement el = WebUiCommonHelper.findWebElement(to, t)
    js().executeScript("arguments[0].scrollIntoView({block:'center'});", el)
}

void jsClickTO(TestObject to, int t=8){
    WebElement el = WebUiCommonHelper.findWebElement(to, t)
    js().executeScript("arguments[0].click();", el)
}

boolean waitToastContains(String txt, int timeout=10){
    String xp = "//*[contains(@class,'ant-message') or contains(@class,'ant-notification') or contains(@class,'toast') or contains(@class,'alert')]" +
            "[not(contains(@style,'display: none'))]//*[contains(normalize-space(.), '"+txt.replace("'", "\\'")+"')]"
    return WebUI.waitForElementVisible(X(xp), timeout, FailureHandling.OPTIONAL)
}

boolean scrollToVisible(WebElement element) {
    int current = 0
    while (current < 3000) {
        try { if (element.isDisplayed()) return true } catch (ignored) {}
        js().executeScript("window.scrollBy(0, 250)")
        WebUI.delay(0.2)
        current += 250
    }
    return false
}

/************ Session & Org ************/
void ensureSession() {
    if (isBrowserOpen()) return

    WebUI.openBrowser('')
    WebUI.maximizeWindow()
    WebUI.navigateToUrl('https://platform.catchprobe.org/')

    WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'), 30)
    WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'))

    WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 30)
    WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 'katalon.test@catchprobe.com')
    WebUI.setEncryptedText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Password_password'), 'RigbBhfdqOBDK95asqKeHw==')
    WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Sign in'))

    WebUI.delay(3)
    // test ortamı için dummy OTP
    def otp = (100000 + new Random().nextInt(900000)).toString()
    WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'), otp)
    WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Verify'))
    WebUI.delay(2)
}

boolean ensureOrg(String desired, int retries=2) {
    TestObject cur = X("//div[contains(@class,'font-semibold') and contains(.,'Organization')]/span[@class='font-thin']")
    WebUI.waitForElementVisible(cur, 15)
    String now = WebUI.getText(cur).trim()
    if (now == desired) return true

    while (retries-- >= 0) {
        TestObject orgBtn = X("//button[.//div[contains(text(),'Organization :')]]")
        jsClickTO(orgBtn, 6)
        TestObject opt = X("//button[.//div[normalize-space(text())='"+desired+"']]")
        WebUI.waitForElementClickable(opt, 10)
        jsClickTO(opt, 6)
        WebUI.delay(1)
        now = WebUI.getText(cur).trim()
        if (now == desired) return true
    }
    return false
}

/************ Test Start ************/
ensureSession()                   // TestOps’ta “Browser is not opened” hatasını önler
WebUI.navigateToUrl('https://platform.catchprobe.org/leakmap')
WebUI.waitForPageLoad(10)

// (İsterseniz) org kilitleyin — Leakmap verisi hangi org’da ise onu yazın
// ensureOrg('Mail Test')

/************ Leakmap ************/
WebUI.navigateToUrl('https://platform.catchprobe.org/leakmap')
WebUI.waitForPageLoad(12)
try { CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'() } catch(Throwable ignored){}

/* ---- Kart/SVG kontrolleri (hızlı) ---- */
def checkSvg = { TestObject box, String name ->
    if (WebUI.waitForElementVisible(box, 12)) {
        WebElement el = WebUI.findWebElement(box, 8)
        Boolean ok = WebUI.executeJavaScript("return arguments[0].querySelector('svg')!=null;", [el])
        KeywordUtil.logInfo("${name} SVG? ${ok}")
    } else {
        KeywordUtil.logInfo("${name} görünmedi ⏰")
    }
}
checkSvg(findTestObject('Object Repository/Leakmap/Dashboard/GovernmentID'), "Government Issued IDs")
checkSvg(findTestObject('Object Repository/Leakmap/Dashboard/EmailDomains'),  "Email Domains")
checkSvg(findTestObject('Object Repository/Leakmap/Dashboard/WebsiteURLs'),  "Website URLs")

/* ---- AI Insight aç/kapat ---- */
WebUI.click(findTestObject('Object Repository/Leakmap/Dashboard/AI INSIGHT'))
TestObject insightViewport = X("(//div[@data-radix-scroll-area-viewport])[2]")
WebUI.waitForElementVisible(insightViewport, 20)
WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Mitre Close'))

/* ---- Recent Inserts Size ---- */
TestObject recentTO = findTestObject('Object Repository/Leakmap/Dashboard/Recent Inserts Size')
WebElement recentEl = WebUiCommonHelper.findWebElement(recentTO, 15)
if (scrollToVisible(recentEl)) {
    js().executeScript("arguments[0].click();", recentEl)
    WebUI.waitForPageLoad(8)
}

/* ---- Download Data -> Email to List ---- */
WebElement dlEl = WebUiCommonHelper.findWebElement(findTestObject('Object Repository/Leakmap/Dashboard/Download data'), 10)
if (scrollToVisible(dlEl)) js().executeScript("arguments[0].click();", dlEl)

WebUI.waitForElementVisible(findTestObject('Object Repository/Leakmap/Dashboard/Email to List'), 8)
WebUI.click(findTestObject('Object Repository/Leakmap/Dashboard/Email to List'))
try { CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'() } catch(Throwable ignored){}

TestObject emailRow = findTestObject('Object Repository/Leakmap/Dashboard/catchprobetestmail')
jsScrollIntoViewTO(emailRow, 8)
WebUI.waitForElementVisible(emailRow, 8)
WebUI.click(emailRow)

WebUI.waitForElementVisible(findTestObject('Object Repository/Leakmap/Dashboard/Emaillisttext'), 8)
WebUI.click(findTestObject('Object Repository/Leakmap/Dashboard/Emaillisttext'))

WebUI.waitForElementVisible(findTestObject('Object Repository/Leakmap/Dashboard/downloaddatabutton'), 8)
WebUI.click(findTestObject('Object Repository/Leakmap/Dashboard/downloaddatabutton'))

WebUI.waitForElementVisible(findTestObject('Object Repository/Leakmap/Dashboard/DownloadToast'), 12)

/* ---- Mail kontrol (INBOX temizle + polling) ---- */
String mailHost = "imap.gmail.com"
String mailUser = "catchprobe.testmail@gmail.com"
String mailPassword = "eqebhxweatxsocbx"
String keyword = "LeakMap"

try { MailReader.clearFolder(mailHost, mailUser, mailPassword, "INBOX") } catch(Throwable ignored){}

int maxWait = 180, step = 10, waited = 0
boolean mailArrived = false
while (waited < maxWait) {
    WebUI.comment("📨 Mail kontrol ediliyor... (${waited}/${maxWait}s)")
    mailArrived = MailReader.checkLatestEmailWithSpam(mailHost, mailUser, mailPassword, keyword)
    if (mailArrived) { WebUI.comment("✅ Mail geldi!"); break }
    WebUI.delay(step); waited += step
}
if (!mailArrived) {
    WebUI.takeScreenshot()
    KeywordUtil.markFailedAndStop("❌ Beklenen mail ${maxWait} sn içinde gelmedi.")
}
try { MailReader.clearFolder(mailHost, mailUser, mailPassword, "INBOX") } catch(Throwable ignored){}

/* ---- CREATE REPORT -> Download Report ---- */
TestObject createReport = findTestObject('Object Repository/Leakmap/Dashboard/CREATE REPORT')
jsScrollIntoViewTO(createReport, 10)
jsClickTO(createReport, 10)

WebUI.waitForElementVisible(findTestObject('Object Repository/Leakmap/Dashboard/DownloadReport'), 35)
WebUI.click(findTestObject('Object Repository/Leakmap/Dashboard/DownloadReport'))
if (!waitToastContains("Report downloaded successfully.", 15))
    KeywordUtil.markFailed("Onay toast gelmedi: Report downloaded successfully.")

WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Mitre Close'))

/* ---- PWNED: ilk sonucu al, 3. sayfaya git, search ile doğrula ---- */
TestObject firstResult = findTestObject('Object Repository/Leakmap/Dashboard/Pwnedfirst')
WebElement firstEl = WebUiCommonHelper.findWebElement(firstResult, 12)
scrollToVisible(firstEl)
WebUI.waitForElementVisible(firstResult, 10)
String firstTitle = WebUI.getText(firstResult).trim()

TestObject pagination2 = findTestObject('Object Repository/Malwares/Pagination')
WebElement pagEl = WebUiCommonHelper.findWebElement(pagination2, 10)
scrollToVisible(pagEl)
js().executeScript("window.scrollTo(0, document.body.scrollHeight)")
WebUI.waitForElementClickable(pagination2, 10)
WebUI.click(pagination2)
WebUI.waitForElementClickable(findTestObject('Object Repository/Leakmap/Dashboard/3.pagination'), 10)
WebUI.click(findTestObject('Object Repository/Leakmap/Dashboard/3.pagination'))
WebUI.delay(1)

TestObject searchInput = findTestObject('Object Repository/Malwares/İnput')
WebElement inEl = WebUiCommonHelper.findWebElement(searchInput, 10)
scrollToVisible(inEl)
WebUI.click(searchInput)
WebUI.setText(searchInput, firstTitle)
WebUI.sendKeys(searchInput, Keys.chord(Keys.ENTER))
WebUI.delay(1)

TestObject resultAgain = findTestObject('Object Repository/Leakmap/Dashboard/Pwnedfirst')
WebUI.waitForElementVisible(resultAgain, 10)
String secondTitle = WebUI.getText(resultAgain).trim()
assert secondTitle.contains(firstTitle)


