/************** Imports (temiz) **************/
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.testobject.ObjectRepository as OR


import groovy.transform.Field

import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.interactions.Actions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Random
import java.util.Arrays
import java.util.ArrayList
import java.util.Set
import java.util.LinkedHashSet

/************** Sabitler **************/
@Field final String EXPORT_CSV_BTN_XP = "//button[normalize-space(.)='Export as CSV']"
@Field final String TOAST_EXPORT_OK   = "Signature list exported successfully"

/************** Yardımcılar **************/
ensureSession()
TestObject X(String xp) {
    TestObject to = new TestObject(xp)
    to.addProperty("xpath", ConditionType.EQUALS, xp)
    return to
}
WebElement el(TestObject to, int timeout = 10) {
    return WebUiCommonHelper.findWebElement(to, timeout)
}
JavascriptExecutor js() { (JavascriptExecutor) DriverFactory.getWebDriver() }
WebDriver drv() { DriverFactory.getWebDriver() }
String curHandle() { drv().getWindowHandle() }

boolean visibleCenter(TestObject to, int timeoutSec) {
    if (!WebUI.waitForElementVisible(to, timeoutSec, FailureHandling.OPTIONAL)) return false
    WebElement e = WebUiCommonHelper.findWebElement(to, Math.min(timeoutSec, 3))
    js().executeScript("arguments[0].scrollIntoView({block:'center', inline:'nearest'})", e)
    return true
}
String lower(String s){ return (s==null) ? "" : s.toLowerCase() }
String allText(WebElement el) {
    try {
        return js().executeScript(
            "return arguments[0].innerText || arguments[0].textContent || '';", Arrays.asList(el)
        ) as String
    } catch(Throwable t){ return el.getText() }
}
boolean waitToastGeneric(int sec){
    return WebUI.waitForElementVisible(
        X("//*[contains(@class,'toast') or contains(@class,'sonner') or @role='status' or contains(@class,'react-hot-toast')]"),
        sec, FailureHandling.OPTIONAL)
}
int windowCount(){ return drv().getWindowHandles().size() }
void switchToLastWindow(){
    def handles = new ArrayList<>(drv().getWindowHandles())
    WebUI.switchToWindowIndex(handles.size() - 1)
}
WebElement scrollCenter(TestObject to, int timeout = 10) {
    WebElement e = el(to, timeout)
    js().executeScript("arguments[0].scrollIntoView({block:'center', inline:'nearest'});", e)
    return e
}
void clickSmart(TestObject to, int timeout = 10) {
    WebElement e = el(to, timeout)
    try { e.click() } catch (Throwable _){
        js().executeScript("arguments[0].click()", e)
    }
}
void waitToast(String text, int sec = 8) {
    WebUI.waitForElementVisible(X("//*[normalize-space(.)='${text}']"), sec)
}
void pressEsc() {
    new Actions(drv()).sendKeys(Keys.ESCAPE).perform()
}
void hover(TestObject to, int timeout=6) {
    WebElement e = el(to, timeout)
    new Actions(drv()).moveToElement(e).perform()
}

/** Bir konteyner içinde SVG var mı? (hızlı) */
boolean svgPresentInside(TestObject container, int timeoutSec = 6) {
    long end = System.currentTimeMillis() + timeoutSec * 1000
    WebElement root = el(container, Math.min(timeoutSec, 3))
    while (System.currentTimeMillis() < end) {
        if (!root.findElements(By.xpath(".//*[name()='svg']")).isEmpty()) return true
        WebUI.delay(0.25)
    }
    return false
}

/** sighting chart var mı? */
boolean hasChartGraphics(TestObject root, int timeoutSec) {
    try {
        def svgNodes = WebUiCommonHelper.findWebElements(
            X(root.findPropertyValue("xpath") + "//*[name()='svg']//*[name()='path' or name()='rect' or name()='g']"),
            timeoutSec
        )
        if (!svgNodes.isEmpty()) return true
        def canvasNodes = WebUiCommonHelper.findWebElements(
            X(root.findPropertyValue("xpath") + "//canvas"),
            Math.max(2, Math.min(timeoutSec, 5))
        )
        return !canvasNodes.isEmpty()
    } catch (Throwable t) {
        KeywordUtil.logInfo("hasChartGraphics() hata: " + t.message)
        return false
    }
}

/************** Yeni sekme/kapama yardımcıları **************/
/** Yeni handle’ı bekle ve döndür */
String waitNewHandle(Set<String> before, int timeoutSec = 8) {
    long end = System.currentTimeMillis() + timeoutSec*1000
    while (System.currentTimeMillis() < end) {
        def now = drv().getWindowHandles()
        if (now.size() > before.size()) {
            for (String h : now) if (!before.contains(h)) return h
        }
        WebUI.delay(0.2)
    }
    return null
}
/** Güvenli kapat + fallback ve uygun handle’a dönüş */
void closeCurrentWindowSafely(String fallbackHandle) {
    try {
        drv().close() // native
    } catch(Throwable t1) {
        try {
            js().executeScript("window.close();") // JS fallback
        } catch(Throwable t2) {
            KeywordUtil.markWarning("Pencere kapatılamadı: " + t2.message)
        }
    }
    // geri dön
    def handles = drv().getWindowHandles()
    if (!handles.isEmpty()) {
        String target = handles.contains(fallbackHandle) ? fallbackHandle : handles.iterator().next()
        drv().switchTo().window(target)
    }
}
boolean isBrowserOpen() {
  try { DriverFactory.getWebDriver(); return true } catch(Throwable t) { return false }
}

void ensureSession() {
  if (isBrowserOpen()) return

  WebUI.openBrowser('')
  WebUI.maximizeWindow()
  WebUI.navigateToUrl('https://platform.catchprobe.org/')

  WebUI.waitForElementVisible(
	  OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'), 30)
  WebUI.click(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'))

  WebUI.setText(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'),
				'katalon.test@catchprobe.com')
  WebUI.setEncryptedText(
	  OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Password_password'),
	  'RigbBhfdqOBDK95asqKeHw==')
  WebUI.click(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Sign in'))

  WebUI.delay(3) // sayfa ve OTP kutuları gelsin

  // basit OTP (senin akışındaki gibi dummy)
  def otp = (100000 + new Random().nextInt(900000)).toString()
  WebUI.setText(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'), otp)
  WebUI.click(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Verify'))
  WebUI.delay(2)
  String Threat = "//span[text()='Threat']"
  WebUI.waitForElementVisible(X(Threat), 10, FailureHandling.OPTIONAL)
}
/************** Test Akışı **************/


// Threatway -> Signature List
WebUI.navigateToUrl('https://platform.catchprobe.org/threatway')
WebUI.waitForPageLoad(30)

WebUI.waitForElementVisible(findTestObject('Object Repository/dashboard/Page_/Signature List'), 15)
clickSmart(findTestObject('Object Repository/dashboard/Page_/Signature List'))

// Bugünün tarihi
String today = new SimpleDateFormat("dd/MM/yyyy").format(new Date())
KeywordUtil.logInfo("Today: " + today)
WebUI.delay(1)

// Tablo satırını bul & tıkla (senin TableUtils)
CustomKeywords.'com.catchprobe.utils.TableUtils.scrollTableAndClickLocation'(today)

// Filtreler
WebUI.waitForElementClickable(findTestObject('Object Repository/dashboard/Page_/Threatway button_Download CSV'), 30)

WebUI.waitForElementClickable(findTestObject('Object Repository/dashboard/Page_/Threatway İPv4-buton'), 30)
WebUI.click(findTestObject('Object Repository/dashboard/Page_/Threatway İPv4-buton'))

TestObject ipv4Item = X("//div[@data-value='IPv4']")
clickSmart(ipv4Item)
WebUI.delay(1)

WebUI.waitForElementClickable(findTestObject('Object Repository/dashboard/Page_/Threatway-Choese One'), 20)
WebUI.click(findTestObject('Object Repository/dashboard/Page_/Threatway-Choese One'))

WebUI.waitForElementClickable(findTestObject('Object Repository/dashboard/Page_/Thteatway Chose Button One'), 20)
WebUI.click(findTestObject('Object Repository/dashboard/Page_/Thteatway Chose Button One'))

// End Date seçimi
TestObject endDateBtn = X("//button[contains(.,'Select End Date')]")
clickSmart(endDateBtn)
WebUI.delay(0.4)

String d = new SimpleDateFormat("d").format(new Date())
String dayXp = (d.toInteger() >= 26)
        ? "(//button[@name='day' and normalize-space(text())='${d}'])[2]"
        : "(//button[@name='day' and normalize-space(text())='${d}'])[1]"
clickSmart(X(dayXp))
WebUI.delay(1)

TestObject btnApply   = findTestObject('Object Repository/dashboard/Page_/threatway button_APPLY AND SEARCH')
TestObject inpKeyword = findTestObject('Object Repository/dashboard/Page_/Threatway input_Keyword_search_filters')
TestObject firstSig   = findTestObject('Object Repository/dashboard/Page_/Threatway first signature')

// 1) Önce input görünür ve odaklanır
visibleCenter(inpKeyword, 10)
WebUI.click(inpKeyword)

// 2) Signature metnini al (satır sonu vs. temizle)
String signature = WebUI.getText(firstSig)
signature = signature?.replaceAll("\\s+", " ")?.trim()

// 3) React kontrollü input'a JS ile yaz ve gerekli event'leri gönder
WebElement inpEl = WebUiCommonHelper.findWebElement(inpKeyword, 10)
String js = """
  var node = arguments[0];
  var val  = arguments[1] || '';
  node.focus();
  node.value = val;
  node.dispatchEvent(new Event('input',  { bubbles: true }));
  node.dispatchEvent(new Event('change', { bubbles: true }));
"""
WebUI.executeJavaScript(js, Arrays.asList(inpEl, signature))

// 4) Şimdi tek kez Apply'a bas
visibleCenter(btnApply, 10)
WebUI.click(btnApply)

// 5) Sonucun geldiğini doğrula
WebUI.waitForElementVisible(firstSig, 20)

// Export CSV (varsa)
if (WebUI.waitForElementPresent(X(EXPORT_CSV_BTN_XP), 5, FailureHandling.OPTIONAL)) {
    clickSmart(X(EXPORT_CSV_BTN_XP))
    waitToast(TOAST_EXPORT_OK, 6)
} else {
    KeywordUtil.logInfo("Export CSV butonu bulunamadı; metin farklı olabilir.")
}

// Favori (ikon sınıfına göre ekle/çıkar)
TestObject favButton = X("//*[contains(@class,'lucide lucide-star') and (contains(@class,'h-4 w-4') or contains(@class,'text-warning'))]")
WebUI.waitForElementVisible(favButton, 15)
String cls = (String) WebUI.executeJavaScript(
    "var el=document.evaluate(\"//*[contains(@class,'lucide lucide-star') and (contains(@class,'h-4 w-4') or contains(@class,'text-warning'))]\"," +
    "document,null,XPathResult.FIRST_ORDERED_NODE_TYPE,null).singleNodeValue; return el?el.getAttribute('class'):null;", null)
KeywordUtil.logInfo("Star class: " + cls)

TestObject toastAdded   = X("//div[contains(text(),'Signature successfully added to favorites.')]")
TestObject toastRemoved = X("//div[contains(text(),'Signature successfully removed from favorites.')]")

if (cls != null && cls.contains('h-4 w-4')) {
    WebUI.click(favButton)
    WebUI.click(findTestObject('Object Repository/dashboard/Page_/Threatway button_Add_'))
    WebUI.waitForElementVisible(toastAdded, 10)
    KeywordUtil.logInfo("Favoriye eklendi ✅")
    pressEsc(); WebUI.delay(0.2)
} else if (cls != null && cls.contains('text-warning')) {
    WebUI.click(favButton)
    WebUI.click(findTestObject('Object Repository/dashboard/Page_/Threatway button_Add_'))
    WebUI.waitForElementVisible(toastRemoved, 10)
    KeywordUtil.logInfo("Favoriden çıkarıldı ✅")
    pressEsc(); WebUI.delay(0.2)
} else {
    KeywordUtil.markWarning("Yıldız ikon sınıfı beklenenden farklı: " + cls)
}

// İlk IP detayına gir
WebUI.waitForElementClickable(findTestObject('Object Repository/dashboard/Page_/Threatway first ip'), 20)
WebUI.click(findTestObject('Object Repository/dashboard/Page_/Threatway first ip'))
WebUI.delay(1)

/*********** 0) Signature metni hazırla ***********/
if (binding.hasVariable('signature') == false || (signature == null || signature.trim().isEmpty())) {
    try {
        signature = WebUI.getText(findTestObject("Object Repository/dashboard/Page_/Threatway first signature")).trim()
        KeywordUtil.logInfo("Signature set: " + signature)
    } catch(Throwable t) {
        KeywordUtil.markWarning("Signature text alınamadı, URL kontrolü kısıtlı olabilir.")
        signature = ""
    }
}

/*********** 1) WHOIS Record (turuncu buton) ***********/
TestObject btnWhois = X("//button[contains(@class,'bg-orange-500')]")
if (visibleCenter(btnWhois, 6)) {         // hız için küçük hover
    WebUI.delay(0.1)
    WebUI.click(btnWhois)
    TestObject whoisPanel = X("(//*[@role='dialog' or contains(@class,'dialog') or contains(@class,'sheet') or contains(@class,'drawer') or contains(@class,'card')])[last()]")
    if (WebUI.waitForElementVisible(whoisPanel, 8, FailureHandling.OPTIONAL)) {
        WebElement panelEl = WebUiCommonHelper.findWebElement(whoisPanel, 3)
        String txt = lower(allText(panelEl))
        boolean hasNetName  = txt.contains("net name")
        boolean hasNetRange = txt.contains("net range")
        KeywordUtil.logInfo("WHOIS -> Net name: " + hasNetName + ", Net range: " + hasNetRange)
        if (!hasNetName || !hasNetRange) KeywordUtil.markWarning("WHOIS içinde beklenen alan(lar) yok")
        pressEsc(); WebUI.delay(0.2)
    } else {
        KeywordUtil.markWarning("WHOIS paneli açılmadı.")
    }
} else {
    KeywordUtil.markWarning("WHOIS butonu görünmedi.")
}

/*********** 2) Export as PDF -> Download -> toast ***********/
TestObject btnExportIcon = X("//*[contains(@class,'lucide') and contains(@class,'file-down')]")
if (visibleCenter(btnExportIcon, 6)) {
    WebUI.click(btnExportIcon)
    TestObject btnDownload = X("//button[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'download') or contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'pdf')] | //a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'download') or contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'pdf')]")
    if (WebUI.waitForElementClickable(btnDownload, 6, FailureHandling.OPTIONAL)) {
        WebUI.click(btnDownload)
        boolean toastOk = waitToastGeneric(6)
        if (!toastOk) KeywordUtil.markWarning("PDF export/download toast görünmedi.")
        else KeywordUtil.logInfo("✅ PDF indirme/toast göründü.")
    } else {
        KeywordUtil.markWarning("Download/PDF seçeneği görünmedi.")
    }
} else {
    KeywordUtil.markWarning("Export (file-down) ikonu görünmedi.")
}

/*********** 3) AI Insight ***********/
TestObject btnAI = X("//*[contains(@class,'lucide') and contains(@class,'bot')]")
if (visibleCenter(btnAI, 6)) {
    WebUI.click(btnAI)
    TestObject aiPanel = X("(//*[@role='dialog' or contains(@class,'dialog') or contains(@class,'sheet') or contains(@class,'drawer')])[last()]")
    if (WebUI.waitForElementVisible(aiPanel, 8, FailureHandling.OPTIONAL)) {
        WebElement elp = WebUiCommonHelper.findWebElement(aiPanel, 3)
        String txt = lower(allText(elp)).trim()
        KeywordUtil.logInfo("AI Insight text len: " + txt.length())
        if (txt.isEmpty() || txt.contains("data not found") || txt.contains("no data")) {
            KeywordUtil.markWarning("🚨 AI Insight içerik zayıf/boş.")
        } else {
            KeywordUtil.logInfo("✅ AI Insight içerik var.")
        }
        pressEsc(); WebUI.delay(0.2)
    } else {
        KeywordUtil.markWarning("AI Insight paneli görünmedi.")
    }
} else {
    KeywordUtil.markWarning("AI Insight (bot) butonu görünmedi.")
}

/*********** 4) Score History ***********/
TestObject btnHistory = X("//*[contains(@class,'lucide-history') and contains(@class,'cursor-pointer')]")
if (visibleCenter(btnHistory, 6)) {
    WebUI.click(btnHistory)
    TestObject histPanel = X("(//*[@role='dialog' or contains(@class,'dialog') or contains(@class,'sheet')])[last()]")
    if (WebUI.waitForElementVisible(histPanel, 8, FailureHandling.OPTIONAL)) {
        WebElement elp = WebUiCommonHelper.findWebElement(histPanel, 3)
        String txt = allText(elp).trim()
        boolean looksGood = (txt != null && txt.length() >= 30) && !lower(txt).contains("data not found")
        if (!looksGood) KeywordUtil.markWarning("⚠ Score History açıklaması zayıf.")
        else KeywordUtil.logInfo("✅ Score History açıklaması göründü.")
        pressEsc(); WebUI.delay(0.2)
    } else {
        KeywordUtil.markWarning("Score History paneli görünmedi.")
    }
} else {
    KeywordUtil.markWarning("Score History butonu görünmedi.")
}

/*********** 5) 3 nokta menü -> VT / urlscan / GSB (handle bazlı kapanış) ***********/
TestObject btnMenu3 = X("//*[@class='lucide lucide-menu h-4 w-4']")
if (visibleCenter(btnMenu3, 6)) {

    def clickMenuAndOpen = { String linkXp, String mustContain, String sig ->
        String mainHandle = curHandle()
        Set<String> before = new LinkedHashSet<>(drv().getWindowHandles())

        WebUI.click(btnMenu3)  // menüyü her seferinde aç
        TestObject item = X(linkXp)
        if (!WebUI.waitForElementClickable(item, 6, FailureHandling.OPTIONAL)) {
            KeywordUtil.markWarning("Menü öğesi tıklanamadı: " + linkXp)
            return
        }
        WebUI.click(item)

        String newHandle = waitNewHandle(before, 8)
        if (newHandle == null) {
            KeywordUtil.markWarning("Yeni sekme açılmadı: " + linkXp)
            return
        }

        drv().switchTo().window(newHandle)
        WebUI.delay(0.5)

        String url = WebUI.getUrl()
        KeywordUtil.logInfo("Yeni sekme URL: " + url)

        boolean urlOk = lower(url).contains(mustContain)
        boolean sigOk = (sig != null && !sig.isEmpty()) ? url.contains(sig) : true
        if (!urlOk || !sigOk) {
            KeywordUtil.markWarning("URL doğrulaması başarısız. mustContain=" + mustContain + ", sigMatch=" + sigOk)
        } else {
            KeywordUtil.logInfo("✅ URL doğrulandı (base & signature).")
        }

        // güvenli kapat ve ana pencereye dön
        closeCurrentWindowSafely(mainHandle)
    }

    clickMenuAndOpen("//a[normalize-space(text())='VirusTotal']", "virustotal", signature)
    clickMenuAndOpen("//a[normalize-space(text())='urlscan']", "urlscan", signature)
    clickMenuAndOpen("//a[normalize-space(text())='GoogleSafeBrowsing']", "google", signature)

} else {
    KeywordUtil.markWarning("3 nokta menüsü görünmedi.")
}

// Number of Views
TestObject numberOfViews = findTestObject('Object Repository/dashboard/Page_/Threatway Number of Views')
if (visibleCenter(numberOfViews, 6)) {
    boolean ok = svgPresentInside(numberOfViews, 6)
    KeywordUtil.logInfo("Number of Views SVG: " + ok)
} else {
    KeywordUtil.logInfo("Number of Views görünmedi.")
}

// Risk Score
TestObject riskScore = findTestObject('Object Repository/dashboard/Page_/Threatway Risk Score')
if (visibleCenter(riskScore, 6)) {
    boolean ok = svgPresentInside(riskScore, 6)
    KeywordUtil.logInfo("Risk Score SVG: " + ok)
} else {
    KeywordUtil.logInfo("Risk Score görünmedi.")
}

// IOC detail aç
TestObject iocDetailBtn = findTestObject('Object Repository/dashboard/Page_/Threatway iocDetailButton')
if (visibleCenter(iocDetailBtn, 8)) {
    clickSmart(iocDetailBtn)
    KeywordUtil.logInfo("IOC detail açıldı.")
} else {
    KeywordUtil.logInfo("IOC detail butonu görünmedi.")
}

// IndicatorText == signature
TestObject indicatorTo = X("//a[contains(@href,'/smartdeceptive/attack-flow?ip_address=')] ")
if (visibleCenter(indicatorTo, 10)) {
    String indicatorText = WebUI.getText(indicatorTo)
    KeywordUtil.logInfo("Indicator: " + indicatorText)
    WebUI.verifyMatch(indicatorText ?: "", signature ?: "", false)
} else {
    KeywordUtil.markWarning("IndicatorText görünmedi.")
}

// Show detail
TestObject btnShowDetail = findTestObject('Object Repository/dashboard/Page_/button_show detail')
if (visibleCenter(btnShowDetail, 8)) {
    WebUI.click(btnShowDetail)
} else {
    KeywordUtil.markWarning("Show detail butonu görünmedi.")
}

// Show detail – IP profile text == signature
TestObject toIpProfile = findTestObject('Object Repository/dashboard/Page_/Show detail text')

if (visibleCenter(toIpProfile, 10)) {
    WebElement ipProfileEl = WebUiCommonHelper.findWebElement(toIpProfile, 10)

    // value varsa onu, yoksa text'i al
    String ipProfileVal = ipProfileEl.getAttribute('value')
    if (ipProfileVal == null || ipProfileVal.trim().isEmpty()) {
        ipProfileVal = ipProfileEl.getText()
    }

    // normalize et
    String normProfile = (ipProfileVal ?: '').replaceAll('\\s+', ' ').trim()
    String normSig     = (signature   ?: '').replaceAll('\\s+', ' ').trim()

    KeywordUtil.logInfo("IP Profile: " + normProfile)

    if (normProfile && !normProfile.equalsIgnoreCase('No')) {
        WebUI.verifyMatch(normProfile, normSig, false)   // tam eşleşme
        // Eğer kısmi arama istersen:
        // assert normProfile.contains(normSig)
    } else {
        KeywordUtil.markWarning("IP Profile boş/No.")
    }
}

// Show detail – Risk Score (SVG)
TestObject showDetailRisk = findTestObject('Object Repository/dashboard/Page_/Show detail circle')
if (visibleCenter(showDetailRisk, 10)) {
    boolean ok = svgPresentInside(showDetailRisk, 6)
    KeywordUtil.logInfo("Show Detail Risk Score SVG: " + ok)
} else {
    KeywordUtil.logInfo("Show Detail Risk Score görünmedi.")
}

// Show sightings map
TestObject btnSightingMap = findTestObject('Object Repository/dashboard/Page_/button_show sightings map')
if (visibleCenter(btnSightingMap, 8)) {
    clickSmart(btnSightingMap)
    KeywordUtil.logInfo("Sighting Map açıldı.")
} else {
    KeywordUtil.markWarning("Sighting Map butonu görünmedi.")
}

TestObject sightingMapRoot = X("//*[@id='ipProfileChartdiv']")
if (visibleCenter(sightingMapRoot, 12)) {
    boolean ok = hasChartGraphics(sightingMapRoot, 6)
    KeywordUtil.logInfo("Sighting Map grafik durumu: " + ok)
    if (!ok) KeywordUtil.markWarning("⚠ Sighting Map bulundu ama grafik YOK (boş olabilir)")
} else {
    KeywordUtil.markWarning("⚠ Sighting Map görünmedi")
}

// Zoom out/in (isteğe bağlı)
WebUI.executeJavaScript("document.body.style.zoom='0.7'", null)

WebUI.delay(0.2)


// Show detail kapat
TestObject showDetailClose = findTestObject('Object Repository/dashboard/Page_/Show detail Close button')
if (WebUI.waitForElementClickable(showDetailClose, 6, FailureHandling.OPTIONAL)) {
    clickSmart(showDetailClose)
}

// IOC detail kapat (toggle)
if (WebUI.waitForElementClickable(iocDetailBtn, 6, FailureHandling.OPTIONAL)) {
    clickSmart(iocDetailBtn)
}

// STIX package
TestObject stixBtn = findTestObject('Object Repository/dashboard/Page_/StixPackageButton')
if (visibleCenter(stixBtn, 8)) {
    clickSmart(stixBtn)
    KeywordUtil.logInfo("STIX Package açıldı.")
} else {
    KeywordUtil.markWarning("STIX Package butonu görünmedi.")
}

TestObject stixCircle = findTestObject('Object Repository/dashboard/Page_/Stix Cricle')
if (visibleCenter(stixCircle, 8)) {
    boolean ok = !el(stixCircle, 5).findElements(By.xpath(".//*[name()='circle']")).isEmpty()
    KeywordUtil.logInfo("STIX circle: " + ok)
}

// STIX kapat (toggle)
if (WebUI.waitForElementClickable(stixBtn, 5, FailureHandling.OPTIONAL)) {
    clickSmart(stixBtn)
}
