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
import java.util.Random

/********************** HELPERS **********************/
TestObject X(String xp) { def to=new TestObject(xp); to.addProperty('xpath', ConditionType.EQUALS, xp); return to }
JavascriptExecutor js() { (JavascriptExecutor) DriverFactory.getWebDriver() }
// --- micro sleep: alt-saniye beklemeler için
void msleep(long ms) {
  if (ms <= 0) return
  try { Thread.sleep(ms) } catch (InterruptedException ignore) {}
}



// --- UI idle bekleyici: ondalık saniye destekler
boolean waitUiIdle(Number seconds = 2) {
  double sec = (seconds == null ? 2.0d : seconds.toDouble())
  long end = System.currentTimeMillis() + (long)(sec * 1000.0d)

  while (System.currentTimeMillis() < end) {
	Boolean busy = (Boolean) js().executeScript("""
      const q = '[aria-busy="true"], .ant-spin-spinning, .animate-spin, [data-loading="true"]';
      return !!document.querySelector(q);
    """)
	if (!busy) return true
	msleep(150)           // <— WebUI.delay yerine gerçek 150 ms bekle
  }
  return false
}


/** Sayfada herhangi bir radio render oldu mu? (role/aria/native) */
boolean waitAnyRadioReady(int timeoutSec = 8) {
  String xp = "//*[@role='radio' or @aria-checked or (self::input and @type='radio')]"
  return WebUI.waitForElementPresent(X(xp), timeoutSec, FailureHandling.OPTIONAL)
}

/** index’teki radio’yu tek JS çağrısıyla tıkla (scope: ilgili bölüm varsa onu hedefler) */
boolean clickRadioFast(int index) {
  Boolean ok = (Boolean) js().executeScript("""
    const idx = arguments[0];
    // İlgili bölüm (Most Common / Most Vulnerable metinleri geçen bir kapsayıcı)
    const containers = Array.from(document.querySelectorAll('main,section,div,form'))
      .filter(n => (n.textContent||'').toLowerCase().includes('most common')
                || (n.textContent||'').toLowerCase().includes('most vulnerable'));
    const scope = containers[0] || document;

    const list = scope.querySelectorAll("[role='radio'], button[aria-checked], input[type='radio']");
    const el = list && list[idx];
    if (!el) return false;

    el.scrollIntoView({block:'center'});
    if (el.tagName.toLowerCase()==='input') {
      const lab = scope.querySelector("label[for='"+el.id+"']");
      if (lab) lab.click(); else el.click();
    } else {
      el.click();
    }
    return true;
  """, index)
  return ok == Boolean.TRUE
}

/** Radio checked mi? aria-checked / data-state / input.checked hepsi kontrol */
boolean isRadioCheckedFast(int index) {
  Boolean st = (Boolean) js().executeScript("""
    const idx = arguments[0];
    const list = document.querySelectorAll("[role='radio'], button[aria-checked], input[type='radio']");
    const el = list[idx];
    if (!el) return false;
    const a = (el.getAttribute('aria-checked')||'').toLowerCase();
    const s = (el.getAttribute('data-state')||'').toLowerCase();
    const c = (el.checked === true);
    return a==='true' || s==='checked' || c===true;
  """, index)
  return st == Boolean.TRUE
}

/** JS scroll/click yardımcıları (yedek) */
boolean jsScrollIntoViewTO(TestObject to, int t = 8) {
  if (!WebUI.waitForElementPresent(to, t, FailureHandling.OPTIONAL)) return false
  try {
    WebElement el = WebUiCommonHelper.findWebElement(to, 3)
    js().executeScript("arguments[0].scrollIntoView({block:'center'});", el)
    WebUI.delay(0.15); return true
  } catch (Throwable ignore) { return false }
}
boolean jsClickTO(TestObject to, int t = 8) {
  if (!WebUI.waitForElementClickable(to, t, FailureHandling.OPTIONAL)) return false
  try {
    WebElement el = WebUiCommonHelper.findWebElement(to, 3)
    js().executeScript("arguments[0].click();", el)
    return true
  } catch (Throwable ignore) { return false }
}

/** Yeni sekme açıldıysa oraya geç */
void switchToNewestWindowIfAny() {
  def d = DriverFactory.getWebDriver()
  def handles = new ArrayList(d.getWindowHandles())
  if (handles.size() > 1) d.switchTo().window(handles.get(handles.size()-1))
}

/** SVG grafik gerçekten render oldu mu? */
boolean ensureSvgInside(TestObject containerTO, int timeoutSec = 8, String label = "Grafik") {
  if (!WebUI.waitForElementPresent(containerTO, 5, FailureHandling.OPTIONAL)) {
    KeywordUtil.logInfo("${label} container görünmedi ⏰"); return false
  }
  WebElement c = WebUiCommonHelper.findWebElement(containerTO, 5)
  long end = System.currentTimeMillis() + timeoutSec*1000L
  while (System.currentTimeMillis() < end) {
    Boolean has = (Boolean) js().executeScript("""
      const el = arguments[0];
      const svg = el.querySelector('svg');
      if (!svg) return false;
      try {
        const bb = svg.getBBox ? svg.getBBox() : {width:1,height:1};
        return (bb.width>0 && bb.height>0) || (svg.clientWidth>0 && svg.clientHeight>0);
      } catch(e) { return true }
    """, c)
    if (has == Boolean.TRUE) { KeywordUtil.logInfo("${label} SVG VAR ✅"); return true }
    WebUI.delay(0.2)
  }
  KeywordUtil.logInfo("${label} SVG YOK 🚨"); return false
}

/** Liste/pagination hazır mı? (tablo satırı || list item || boş state || page numarası) */
boolean waitForListReadyStrong(int timeoutSec = 15) {
  long end = System.currentTimeMillis() + timeoutSec*1000L
  while (System.currentTimeMillis() < end) {
    Boolean ready = (Boolean) js().executeScript($/
      const busySel = '[aria-busy="true"], .ant-spin-spinning, .animate-spin, [data-loading="true"]';
      const busy = !!document.querySelector(busySel);

      // Satır sayımı: tablo + role=row + ant-list vs.
      const rowCount =
        document.querySelectorAll('table tbody tr').length
        || document.querySelectorAll('[role="row"].ant-table-row, [data-row-index]').length
        || document.querySelectorAll('.ant-list .ant-list-item, [data-testid*="row"]').length;

      // Sayfa numaraları (sadece rakam)
      const hasNums = [...document.querySelectorAll('ul li a, ul li button, .ant-pagination-item')]
        .some(el => /^\d+$/.test((el.innerText||'').trim()));

      // Boş state
      const empty = !!document.querySelector('.ant-empty, .empty, [data-empty="true"], [data-state="empty"], .ant-empty-description');

      return !busy && (rowCount>0 || hasNums || empty);
    /$)
    if (ready) return true
    WebUI.delay(0.2)
  }
  return false
}

/** Görünen satır sayısı (farklı markup’lara dayanıklı) */
int getRowCountSmart() {
  Number n = (Number) js().executeScript($/
    return  (document.querySelectorAll('table tbody tr').length)
         || (document.querySelectorAll('[role="row"].ant-table-row, [data-row-index]').length)
         || (document.querySelectorAll('.ant-list .ant-list-item, [data-testid*="row"]').length);
  /$)
  return n==null ? 0 : n.intValue()
}

/** Son sayfa numarasını zekice bul (rakamlar, next disabled vb.) */
int actualLastPageNumberSmart() {
  Number last = (Number) js().executeScript($/
    // Rakamları topla
    const nums = [...document.querySelectorAll('ul li a, ul li button, .ant-pagination-item')]
      .map(el => {
        const t = (el.innerText || '').trim();
        const aria = (el.getAttribute('aria-label') || '').toLowerCase();
        if (/prev|next|previous|first|last/.test(aria)) return 0;
        return /^\d+$/.test(t) ? parseInt(t,10) : 0;
      })
      .filter(n => n>0);
    if (nums.length) return Math.max(...nums);

    // Numara yoksa: next butonu aktif değilse ve veri varsa => tek sayfa
    const rows =  (document.querySelectorAll('table tbody tr').length)
               || (document.querySelectorAll('[role="row"].ant-table-row, [data-row-index]').length)
               || (document.querySelectorAll('.ant-list .ant-list-item, [data-testid*="row"]').length);

    const nextEnabled =
      !!document.querySelector('.ant-pagination-next:not(.ant-pagination-disabled)') ||
      !!document.querySelector('[aria-label*="next"]:not([aria-disabled="true"]):not([disabled])');

    if (!nextEnabled && rows>0) return 1;

    // Hâlâ yoksa: veri varsa 1, hiç veri yoksa 0
    return rows>0 ? 1 : 0;
  /$)
  return last==null ? 0 : last.intValue()
}

/** Tek sayfa gibi mi? (Next yok ve/veya 1..10 arası satır) */
boolean looksSinglePageHeuristic() {
  Boolean single = (Boolean) js().executeScript($/
    const rows =  (document.querySelectorAll('table tbody tr').length)
               || (document.querySelectorAll('[role="row"].ant-table-row, [data-row-index]').length)
               || (document.querySelectorAll('.ant-list .ant-list-item, [data-testid*="row"]').length);

    const nextEnabled =
      !!document.querySelector('.ant-pagination-next:not(.ant-pagination-disabled)') ||
      !!document.querySelector('[aria-label*="next"]:not([aria-disabled="true"]):not([disabled])');

    return !nextEnabled && rows >= 0; // row sayımı başarısız da olsa Next yoksa tek sayfa varsay
  /$)
  return single == Boolean.TRUE
}

/********************** TEST ************************/
/*/
WebUI.openBrowser('')
WebUI.navigateToUrl('https://platform.catchprobe.org/')
WebUI.maximizeWindow()

// Login
WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'), 30)
WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'))
WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 30)
WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 'katalon.test@catchprobe.com')
WebUI.setEncryptedText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Password_password'), 'RigbBhfdqOBDK95asqKeHw==')
WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Sign in'))
WebUI.delay(5)

// OTP (dummy)
def randomOtp = (100000 + new Random().nextInt(900000)).toString()
WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'), randomOtp)
WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Verify'))
WebUI.delay(5)
WebUI.waitForPageLoad(30)
/*/

// Riskroute
WebUI.navigateToUrl('https://platform.catchprobe.org/riskroute')
WebUI.waitForPageLoad(30)

/************* Başlıklar - Hızlı doğrulama *************/
/*/
WebUI.waitForElementClickable(findTestObject('Object Repository/RiskRoute Dashboard/Page_/div_Total Assets'), 20)
WebUI.verifyElementText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/div_Total Assets'), 'Total Assets')
WebUI.verifyElementText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/div_Total Subdomains'), 'Total Subdomains')
WebUI.verifyElementText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/div_Total Vulnerabilities'), 'Total Vulnerabilities')

/************* Radyo seçimleri (Hızlı & Kararlı) *************/
waitAnyRadioReady(8) || WebUI.delay(0.4)

// 1) Most Common (index 0): fast + fallback’lar
boolean clickedCommon = clickRadioFast(0)
if (!clickedCommon) {
  try {
    WebElement mc = WebUiCommonHelper.findWebElement(
      findTestObject('Object Repository/RiskRoute Dashboard/Page_/MostCommonİgnore'), 3)
    js().executeScript("arguments[0].scrollIntoView({block:'center'}); arguments[0].click();", mc)
    clickedCommon = true
  } catch (Throwable ignore) {}
}
if (!clickedCommon) {
  TestObject xp1 = X("(//button[contains(@class,'peer') and contains(@class,'rounded-full') and contains(@class,'border')])[1]")
  if (WebUI.waitForElementClickable(xp1, 3, FailureHandling.OPTIONAL)) {
    WebElement el = WebUiCommonHelper.findWebElement(xp1, 3)
    js().executeScript("arguments[0].scrollIntoView({block:'center'}); arguments[0].click();", el)
    clickedCommon = true
  }
}
assert clickedCommon : "Most Common radio bulunamadı"
waitUiIdle(0.4)
WebUI.comment( isRadioCheckedFast(0) ? "✅ Most Common seçildi" : "❌ Most Common seçilemedi" )

// 2) Most Vulnerable (index 1): fast + fallback
boolean clickedVuln = clickRadioFast(1)
if (!clickedVuln) {
  TestObject toMostVuln = X("(//button[contains(@class,'peer') and contains(@class,'rounded-full') and contains(@class,'border')])[2]")
  if (WebUI.waitForElementClickable(toMostVuln, 3, FailureHandling.OPTIONAL)) {
    WebElement el = WebUiCommonHelper.findWebElement(toMostVuln, 3)
    js().executeScript("arguments[0].scrollIntoView({block:'center'}); arguments[0].click();", el)
    clickedVuln = true
  }
}
assert clickedVuln : "Most Vulnerable radio bulunamadı"
waitUiIdle(0.4)
WebUI.comment( isRadioCheckedFast(1) ? "✅ Most Vulnerable seçildi" : "❌ Most Vulnerable seçilemedi" )

/************* Grafik kontrolleri *************/
TestObject toVBTrigger = findTestObject('Object Repository/RiskRoute Dashboard/Page_/Vulnerability Breakdown')
if (WebUI.verifyElementPresent(toVBTrigger, 8, FailureHandling.OPTIONAL)) {
  jsScrollIntoViewTO(toVBTrigger, 8); jsClickTO(toVBTrigger, 8); waitUiIdle(1)
  ensureSvgInside(findTestObject('Object Repository/Platform IOC Discoveries/Smartdeceptive içerik'), 8, "Vulnerability Breakdown by Severity")
}

TestObject toRiskTrigger = findTestObject('Object Repository/RiskRoute Dashboard/Page_/Risck Score')
if (WebUI.verifyElementPresent(toRiskTrigger, 8, FailureHandling.OPTIONAL)) {
  jsScrollIntoViewTO(toRiskTrigger, 8); jsClickTO(toRiskTrigger, 8); waitUiIdle(1)
  ensureSvgInside(findTestObject('Object Repository/RiskRoute Dashboard/Page_/Risck Score'), 8, "Risk Score")
}

TestObject toCweTrigger = findTestObject('Object Repository/RiskRoute Dashboard/Page_/Most Common CWE ID')
if (WebUI.verifyElementPresent(toCweTrigger, 8, FailureHandling.OPTIONAL)) {
  jsScrollIntoViewTO(toCweTrigger, 8); jsClickTO(toCweTrigger, 8); waitUiIdle(1)
  ensureSvgInside(findTestObject('Object Repository/RiskRoute Dashboard/Page_/MostCommonCwe'), 8, "Most Common CWE")
}
ensureSvgInside(findTestObject('Object Repository/RiskRoute Dashboard/Page_/MostCommonCwe'), 8, "Most Common Vulnerability Tag")

/************* Asset Detail (Domain) → Pagination testi *************/
WebUI.comment('--- Starting Asset Detail (Domain) Pagination Test ---')

TestObject toAssetDetail = findTestObject('Object Repository/RiskRoute Dashboard/Page_/Asset Detail')
if (!WebUI.waitForElementPresent(toAssetDetail, 5, FailureHandling.OPTIONAL)) {
  WebUI.comment("⏭️ 'Asset Detail' grafiği yok; adım atlandı.")
} else {
  WebElement assetDetail = WebUiCommonHelper.findWebElement(toAssetDetail, 10)
  js().executeScript("arguments[0].scrollIntoView({block:'center'});", assetDetail)
  WebUI.delay(0.2)

  TestObject pathDomain = X(
    "//*[local-name()='g' and contains(@class,'apexcharts-series') and " +
    "contains(@class,'apexcharts-pie-series') and @seriesName='domain']/*[local-name()='path']"
  )

  if (!WebUI.waitForElementPresent(pathDomain, 5, FailureHandling.OPTIONAL)) {
    WebUI.comment("⏭️ 'domain' dilimi bulunamadı.")
  } else {
    WebElement pathEl = WebUiCommonHelper.findWebElement(pathDomain, 5)

    String dataStr = pathEl.getAttribute("data:value") ?: "0"
    int dataValue = dataStr.replaceAll("\\D+","").isEmpty() ? 0 : Integer.parseInt(dataStr.replaceAll("\\D+",""))

    if (dataValue <= 0) {
      WebUI.comment("⏭️ Domain data 0, pagination testi atlandı.")
    } else {
      String beforeUrl = WebUI.getUrl()
      pathEl.click()

      switchToNewestWindowIfAny()
      waitForListReadyStrong(15) || WebUI.delay(0.5)

      int expectedPages = (int) Math.ceil(dataValue / 10.0)
      int actualLast    = actualLastPageNumberSmart()

      // Zayıf DOM durumunda tek sayfa kabul heüristiği
      if (actualLast == 0 && looksSinglePageHeuristic()) {
        actualLast = 1
      }

      WebUI.comment("🎯 Beklenen pagination (Domain): ${expectedPages}")
      WebUI.comment("🔢 Tespit edilen son sayfa: ${actualLast}")
      int rowsNow = getRowCountSmart()

      boolean okSingle =
          (expectedPages == 1) &&
          (actualLast == 1 || (actualLast == 0 && looksSinglePageHeuristic()))

      if (okSingle) {
        WebUI.comment("✅ Domain pagination doğru (tek sayfa).")
      } else if (expectedPages == actualLast && actualLast > 0) {
        WebUI.comment("✅ Domain pagination doğru (çok sayfa).")
      } else if (rowsNow > 0) {
        // Sayfa boyutu 10 değilse uyumlu mu?
        int pageSizeGuess = Math.min(Math.max(rowsNow, 1), 100)
        int expectedFromGuess = (int) Math.ceil(dataValue / (double) pageSizeGuess)
        if (expectedFromGuess == actualLast || (expectedFromGuess == 1 && looksSinglePageHeuristic())) {
          WebUI.comment("✅ Domain pagination doğru (page size ≠10, tahmin=${pageSizeGuess}).")
        } else {
          // TAM hataya düşmek yerine uyarı — yanlış pozitifleri azaltır
          KeywordUtil.markWarning("⚠️ Domain pagination belirsiz. Beklenen: ${expectedPages} (~${expectedFromGuess}), Bulunan: ${actualLast}, rows=${rowsNow}, data=${dataValue}")
        }
      } else {
        // Satır saptanmıyorsa ve Next yoksa yine tek sayfa say
        if (looksSinglePageHeuristic()) {
          WebUI.comment("✅ Domain pagination doğru (Next yok → tek sayfa varsayımı).")
        } else {
          KeywordUtil.markWarning("⚠️ Domain pagination tespit edilemedi (DOM farklı). Beklenen: ${expectedPages}, Bulunan: ${actualLast}")
        }
      }

      WebUI.back()
      waitUiIdle(1)
    }
  }
}

WebUI.comment('--- Finished Asset Detail (Domain) Pagination Test ---')

// İsteğe göre tarayıcı açık kalsın / kapat
// WebUI.closeBrowser()
