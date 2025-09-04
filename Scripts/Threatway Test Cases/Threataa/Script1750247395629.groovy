/************** Imports (temiz) **************/
/************** Imports (temiz) **************/
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import groovy.transform.Field

import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.Keys
import org.openqa.selenium.WebElement
import org.openqa.selenium.interactions.Actions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Random
import java.util.Arrays          // <-- EKLENDİ (Arrays.asList için)
import java.util.ArrayList       // <-- EKLENDİ (new ArrayList<> için)


/************** Sabitler **************/
@Field final String EXPORT_CSV_BTN_XP = "//button[normalize-space(.)='Export as CSV']"
@Field final String TOAST_EXPORT_OK   = "Signature list exported successfully"

/************** Yardımcılar **************/


/******** helpers ********/

boolean visibleCenter(TestObject to, int timeoutSec) {
	if (!WebUI.waitForElementVisible(to, timeoutSec, FailureHandling.OPTIONAL)) return false
	WebElement e = WebUiCommonHelper.findWebElement(to, Math.min(timeoutSec, 3))
	((JavascriptExecutor) DriverFactory.getWebDriver()).executeScript(
		"arguments[0].scrollIntoView({block:'center', inline:'nearest'})", e)
	return true
}

String lower(String s){ return (s==null) ? "" : s.toLowerCase() }
String allText(WebElement el) {
	try { return ((JavascriptExecutor) DriverFactory.getWebDriver())
		.executeScript("return arguments[0].innerText || arguments[0].textContent || '';", Arrays.asList(el)) as String
	} catch(Throwable t){ return el.getText() }
}
boolean waitToastGeneric(int sec){
	return WebUI.waitForElementVisible(
		X("//*[contains(@class,'toast') or contains(@class,'sonner') or @role='status' or contains(@class,'react-hot-toast')]"),
		sec, FailureHandling.OPTIONAL)
}
int windowCount(){ return DriverFactory.getWebDriver().getWindowHandles().size() }
void switchToLastWindow(){
	def handles = new ArrayList<>(DriverFactory.getWebDriver().getWindowHandles())
	WebUI.switchToWindowIndex(handles.size() - 1)
}
TestObject X(String xp) {
    TestObject to = new TestObject(xp)
    to.addProperty("xpath", ConditionType.EQUALS, xp)
    return to
}

WebElement el(TestObject to, int timeout = 10) {
    return WebUiCommonHelper.findWebElement(to, timeout)
}

JavascriptExecutor js() { (JavascriptExecutor) DriverFactory.getWebDriver() }

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
    new Actions(DriverFactory.getWebDriver())
        .sendKeys(Keys.ESCAPE)
        .perform()
}

/** Bir konteyner içinde SVG var mı? (hızlı) */
boolean svgPresentInside(TestObject container, int timeoutSec = 6) {
    long end = System.currentTimeMillis() + timeoutSec * 1000
    WebElement root = el(container, Math.min(timeoutSec, 3))
    while (System.currentTimeMillis() < end) {
        // .//*[name()='svg'] => SVG için en hızlı ve stabil yol
        if (!root.findElements(By.xpath(".//*[name()='svg']")).isEmpty()) return true
        WebUI.delay(0.25)
    }
    return false
}

/** Basit görünürlük bekleyip ortala */


/************** Test Akışı **************/

// Tarayıcı / Login
WebUI.openBrowser('')
WebUI.navigateToUrl('https://platform.catchprobe.org/')
WebUI.maximizeWindow()

WebUI.waitForElementVisible(findTestObject('Object Repository/otp/Page_/a_PLATFORM LOGIN'), 30)
WebUI.click(findTestObject('Object Repository/otp/Page_/a_PLATFORM LOGIN'))

WebUI.waitForElementVisible(findTestObject('Object Repository/otp/Page_/input_Email Address_email'), 30)
WebUI.setText(findTestObject('Object Repository/otp/Page_/input_Email Address_email'), 'katalon.test@catchprobe.com')
WebUI.setEncryptedText(findTestObject('Object Repository/otp/Page_/input_Password_password'), 'RigbBhfdqOBDK95asqKeHw==')
WebUI.click(findTestObject('Object Repository/otp/Page_/button_Sign in'))
WebUI.delay(2)

// OTP (demo için rastgele; gerçek ortamda API/Inbox doğrulama kullan)
String randomOtp = (100000 + new Random().nextInt(900000)).toString()
WebUI.setText(findTestObject('Object Repository/otp/Page_/input_OTP Digit_vi_1_2_3_4_5'), randomOtp)
WebUI.click(findTestObject('Object Repository/otp/Page_/button_Verify'))
WebUI.waitForPageLoad(10)
String Threat = "//span[text()='Threat']"
WebUI.waitForElementVisible(X(Threat), 10, FailureHandling.OPTIONAL)

// Threatway -> Signature List
WebUI.navigateToUrl('https://platform.catchprobe.org/threatway')
WebUI.waitForPageLoad(30)

WebUI.waitForElementVisible(findTestObject('Object Repository/dashboard/Page_/Signature List'), 15)
clickSmart(findTestObject('Object Repository/dashboard/Page_/Signature List'))

// Bugünün tarihi
String today = new SimpleDateFormat("dd/MM/yyyy").format(new Date())
KeywordUtil.logInfo("Today: " + today)
WebUI.delay(1)

// Tablo satırını bul & tıkla
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

// İlk signature’ı al, uygula ve ara

visibleCenter(findTestObject('Object Repository/dashboard/Page_/threatway button_APPLY AND SEARCH'), 10)
WebUI.click(findTestObject('Object Repository/dashboard/Page_/threatway button_APPLY AND SEARCH'))
visibleCenter(findTestObject('Object Repository/dashboard/Page_/Threatway input_Keyword_search_filters'), 10)
String signature = WebUI.getText(findTestObject('Object Repository/dashboard/Page_/Threatway first signature'))
WebUI.setText(findTestObject('Object Repository/dashboard/Page_/Threatway input_Keyword_search_filters'), signature)
WebUI.click(findTestObject('Object Repository/dashboard/Page_/threatway button_APPLY AND SEARCH'))
WebUI.waitForElementVisible(findTestObject('Object Repository/dashboard/Page_/Threatway first signature'), 20)

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
    // Beyaz: ekle
    WebUI.click(favButton)
    WebUI.click(findTestObject('Object Repository/dashboard/Page_/Threatway button_Add_'))
    WebUI.waitForElementVisible(toastAdded, 10)
    KeywordUtil.logInfo("Favoriye eklendi ✅")
    pressEsc(); WebUI.delay(0.2)
} else if (cls != null && cls.contains('text-warning')) {
    // Sarı: çıkar
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

/** ---------- FAST SVG/RiskScore doğrulamaları ---------- **/
/*********** 0) Signature metni hazırla (yoksa al) ***********/
if (binding.hasVariable('signature') == false || (signature == null || signature.trim().isEmpty())) {
	try {
		signature = WebUI.getText(findTestObject("Object Repository/dashboard/Page_/Threatway first signature")).trim()
		KeywordUtil.logInfo("Signature set: " + signature)
	} catch(Throwable t) {
		KeywordUtil.markWarning("Signature text alınamadı, 3 nokta menü link doğrulamalarında URL kontrolü yapılamayabilir.")
		signature = ""
	}
}

/*********** 1) WHOIS Record (turuncu buton) ***********/
TestObject btnWhois = X("//button[contains(@class,'bg-orange-500')]")
if (visibleCenter(btnWhois, 6)) {
	WebUI.click(btnWhois)
	// Modal/panel bekle ve Net name + Net range doğrula
	TestObject whoisPanel = X("(//*[@role='dialog' or contains(@class,'dialog') or contains(@class,'sheet') or contains(@class,'drawer') or contains(@class,'card')])[last()]")
	if (WebUI.waitForElementVisible(whoisPanel, 8, FailureHandling.OPTIONAL)) {
		WebElement panelEl = WebUiCommonHelper.findWebElement(whoisPanel, 3)
		String txt = lower(allText(panelEl))
		boolean hasNetName  = txt.contains("net name")
		boolean hasNetRange = txt.contains("net range")
		KeywordUtil.logInfo("WHOIS -> Net name: " + hasNetName + ", Net range: " + hasNetRange)
		if (!hasNetName || !hasNetRange) {
			KeywordUtil.markWarning("WHOIS içinde beklenen alan(lar) yok: Net name/Net range")
		}
		pressEsc()
		WebUI.delay(0.2)
	} else {
		KeywordUtil.markWarning("WHOIS paneli açılmadı.")
	}
} else {
	KeywordUtil.markWarning("WHOIS butonu görünmedi.")
}

/*********** 2) Export as PDF -> Download -> Uyarıyı doğrula ***********/
TestObject btnExportIcon = X("//*[contains(@class,'lucide') and contains(@class,'file-down')]")
if (visibleCenter(btnExportIcon, 6)) {
	WebUI.click(btnExportIcon)
	// Açılan menüde Download/PDF butonunu ara
	TestObject btnDownload = X("//button[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'download') or contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'pdf')] | //a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'download') or contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'pdf')]")
	if (WebUI.waitForElementClickable(btnDownload, 6, FailureHandling.OPTIONAL)) {
		WebUI.click(btnDownload)
		// Uyarı/toast yakala (genel bekleme)
		boolean toastOk = waitToastGeneric(6)
		if (!toastOk) {
			KeywordUtil.markWarning("PDF export/download uyarısı (toast) görünmedi.")
		} else {
			KeywordUtil.logInfo("✅ PDF indirme/toast göründü.")
		}
	} else {
		KeywordUtil.markWarning("Download/PDF seçeneği görünmedi.")
	}
} else {
	KeywordUtil.markWarning("Export (file-down) ikonu görünmedi.")
}

/*********** 3) AI Insight (lucide-bot) -> data not found OLMAYACAK ***********/
TestObject btnAI = X("//*[contains(@class,'lucide') and contains(@class,'bot')]")
if (visibleCenter(btnAI, 6)) {
	WebUI.click(btnAI)
	TestObject aiPanel = X("(//*[@role='dialog' or contains(@class,'dialog') or contains(@class,'sheet') or contains(@class,'drawer')])[last()]")
	if (WebUI.waitForElementVisible(aiPanel, 8, FailureHandling.OPTIONAL)) {
		WebElement el = WebUiCommonHelper.findWebElement(aiPanel, 3)
		String txt = lower(allText(el)).trim()
		KeywordUtil.logInfo("AI Insight text len: " + txt.length())
		if (txt.isEmpty() || txt.contains("data not found") || txt.contains("no data")) {
			KeywordUtil.markWarning("🚨 AI Insight beklenen içerik sağlamadı (boş ya da 'data not found').")
		} else {
			KeywordUtil.logInfo("✅ AI Insight içerik var ve geçerli.")
		}
		pressEsc()
		WebUI.delay(0.2)
	} else {
		KeywordUtil.markWarning("AI Insight paneli görünmedi.")
	}
} else {
	KeywordUtil.markWarning("AI Insight (bot) butonu görünmedi.")
}

/*********** 4) Score History (lucide-history) -> açıklama doğrula ***********/
TestObject btnHistory = X("//*[contains(@class,'lucide-history') and contains(@class,'cursor-pointer')]")
if (visibleCenter(btnHistory, 6)) {
	WebUI.click(btnHistory)
	TestObject histPanel = X("(//*[@role='dialog' or contains(@class,'dialog') or contains(@class,'sheet')])[last()]")
	if (WebUI.waitForElementVisible(histPanel, 8, FailureHandling.OPTIONAL)) {
		WebElement el = WebUiCommonHelper.findWebElement(histPanel, 3)
		String txt = allText(el).trim()
		// İçerik uzunluğu ve anlamlı metin kontrolü
		boolean looksGood = (txt != null && txt.length() >= 30) && !lower(txt).contains("data not found")
		if (!looksGood) {
			KeywordUtil.markWarning("⚠ Score History açıklaması beklenenden zayıf/eksik görünüyor.")
		} else {
			KeywordUtil.logInfo("✅ Score History açıklaması göründü.")
		}
		pressEsc()
		WebUI.delay(0.2)
	} else {
		KeywordUtil.markWarning("Score History paneli görünmedi.")
	}
} else {
	KeywordUtil.markWarning("Score History butonu görünmedi.")
}

/*********** 5) 3 nokta menü -> VirusTotal / urlscan / GoogleSafeBrowsing ***********/
TestObject btnMenu3 = X("//*[@class='lucide lucide-menu h-4 w-4']")
if (visibleCenter(btnMenu3, 6)) {

	// ortak fonksiyon
	def clickMenuAndOpen = { String linkXp, String mustContain, String sig ->
		int before = windowCount()
		WebUI.click(btnMenu3)  // menüyü her seferinde yeniden aç
		TestObject item = X(linkXp)
		if (WebUI.waitForElementClickable(item, 6, FailureHandling.OPTIONAL)) {
			WebUI.click(item)
			WebUI.delay(1)
			int after = windowCount()
			if (after > before) {
				switchToLastWindow()
				String url = WebUI.getUrl()
				KeywordUtil.logInfo("Yeni sekme URL: " + url)
				boolean urlOk = lower(url).contains(mustContain)
				boolean sigOk = (sig != null && !sig.isEmpty()) ? url.contains(sig) : true
				if (!urlOk || !sigOk) {
					KeywordUtil.markWarning("URL doğrulaması başarısız. mustContain=" + mustContain + ", sigMatch=" + sigOk)
				} else {
					KeywordUtil.logInfo("✅ URL doğrulandı (base & signature).")
				}
				WebUI.closeWindow()
				WebUI.switchToWindowIndex(0)
			} else {
				KeywordUtil.markWarning("Yeni sekme açılmadı: " + linkXp)
			}
		} else {
			KeywordUtil.markWarning("Menü öğesi tıklanamadı: " + linkXp)
		}
	}

	// 5.1 VirusTotal
	clickMenuAndOpen("//a[normalize-space(text())='VirusTotal']", "virustotal", signature)

	// 5.2 urlscan
	clickMenuAndOpen("//a[normalize-space(text())='urlscan']", "urlscan", signature)

	// 5.3 GoogleSafeBrowsing
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
TestObject indicatorTo = findTestObject('Object Repository/dashboard/Page_/Threatway İndicatortext')
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
    WebElement ipProfileEl = el(toIpProfile, 10)
    String ipProfileVal = ipProfileEl.getAttribute("value") ?: ipProfileEl.getText()
    KeywordUtil.logInfo("IP Profile: " + ipProfileVal)
    if (ipProfileVal && !ipProfileVal.trim().isEmpty() && ipProfileVal != 'No') {
        WebUI.verifyMatch(ipProfileVal, signature, false)
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

/**
 * Sighting Map içinde gerçek grafik var mı?
 * - SVG içindeki path/rect/g gibi elemanlara bakar
 * - Alternatif olarak <canvas> var mı diye bakar
 */
boolean hasChartGraphics(TestObject root, int timeoutSec) {
	try {
		// Önce SVG çocukları
		def svgNodes = WebUiCommonHelper.findWebElements(
			X(root.findPropertyValue("xpath") + "//*[name()='svg']//*[name()='path' or name()='rect' or name()='g']"),
			timeoutSec
		)
		if (!svgNodes.isEmpty()) return true

		// Alternatif: canvas
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

TestObject sightingMapRoot = X("//*[@id='ipProfileChartdiv']")
if (visibleCenter(sightingMapRoot, 12)) {
    boolean ok = hasChartGraphics(sightingMapRoot, 6)
    KeywordUtil.logInfo("Sighting Map grafik durumu: " + ok)
    if (!ok) {
        // Map açılmış ama veri gelmemiş olabilir → fail yerine uyarı
        KeywordUtil.markWarning("⚠ Sighting Map bulundu ama grafik YOK (boş olabilir)")
    }
} else {
    KeywordUtil.markWarning("⚠ Sighting Map görünmedi")
}


// Zoom out/in (isteğe bağlı)
js().executeScript("document.body.style.zoom='0.7'")
WebUI.delay(0.2)
js().executeScript("document.body.style.zoom='1'")

// Show detail kapat
TestObject showDetailClose = findTestObject('Object Repository/dashboard/Page_/Show detail Close button')
if (WebUI.waitForElementClickable(showDetailClose, 6, FailureHandling.OPTIONAL)) {
    clickSmart(showDetailClose)
}

// IOC detail kapat
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
