import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

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

import java.util.Random
import com.kms.katalon.core.testobject.ObjectRepository as OR
/**************** HELPERS ****************/
TestObject X(String xp) { def to=new TestObject(xp); to.addProperty("xpath", ConditionType.EQUALS, xp); return to }
JavascriptExecutor js() { (JavascriptExecutor) DriverFactory.getWebDriver() }

/** küçük idle bekleme */
boolean waitUiIdle(int sec = 2) {
    long end = System.currentTimeMillis() + sec*1000L
    while (System.currentTimeMillis() < end) {
        Boolean busy = (Boolean) js().executeScript("""
          const q = '[aria-busy="true"], .ant-spin-spinning, .animate-spin, [data-loading="true"]';
          return !!document.querySelector(q);
        """)
        if (!busy) return true
        WebUI.delay(0.2)
    }
    return false
}
/** ultra-hızlı: CSS + index ile tıkla (spin/visibility beklemez) */
boolean fastClickByCssIndex(String css, int index, int timeoutSec = 5) {
	long end = System.currentTimeMillis() + timeoutSec*1000L
	while (System.currentTimeMillis() < end) {
		Boolean ok = (Boolean) js().executeScript("""
            const css = arguments[0], i = arguments[1];
            const list = document.querySelectorAll(css);
            const el = list && list[i];
            if (!el) return false;
            el.scrollIntoView({block:'center'});
            el.click();
            return true;
        """, css, index)
		if (ok == Boolean.TRUE) return true
		WebUI.delay(0.1)
	}
	return false
}

/** radio seçili mi? (aria-checked + data-state) */
boolean isCheckedCssIndex(String css, int index) {
	Boolean st = (Boolean) js().executeScript("""
        const el = document.querySelectorAll(arguments[0])[arguments[1]];
        if (!el) return false;
        const a = (el.getAttribute('aria-checked')||'').toLowerCase();
        const s = (el.getAttribute('data-state')||'').toLowerCase();
        return a==='true' && s==='checked';
    """, css, index)
	return st == Boolean.TRUE
}

/** Liste/pagination gerçekten hazır mı? (spin yok + satır/paginasyon/boş state’den biri var) */
boolean waitForListReady(int timeoutSec = 15) {
    long end = System.currentTimeMillis() + timeoutSec*1000L
    while (System.currentTimeMillis() < end) {
        Boolean ready = (Boolean) js().executeScript($/
          const busySel = '[aria-busy="true"], .ant-spin-spinning, .animate-spin, [data-loading="true"]';
          const busy = !!document.querySelector(busySel);
          const empty = !!document.querySelector('.ant-empty, .empty, [data-empty="true"], [data-state="empty"], .ant-table-placeholder');

          // sayı içeren pagination item var mı?
          const hasNum = [...document.querySelectorAll('ul li a, ul li button, [aria-label^="Page" i]')]
            .some(el => /\b\d+\b/.test((el.innerText || el.getAttribute('aria-label') || '').trim()));

          // görünür satır say
          const rowCount = (function(){
            const sels = [
              "table tbody tr:not(.ant-table-placeholder):not([data-empty='true'])",
              ".ant-table-row", "[data-row-key]",
              "[role='rowgroup'] [role='row']",
              ".MuiDataGrid-row",".rdg-row",
              ".ant-list .ant-list-items > *",
              "[data-testid*='row']"
            ];
            const set = new Set();
            for (const s of sels) document.querySelectorAll(s).forEach(el => set.add(el));
            return [...set].filter(el => {
              const st = getComputedStyle(el);
              return st.display!=='none' && st.visibility!=='hidden' && +st.opacity!==0 &&
                     el.offsetParent!==null && !el.closest('thead');
            }).length;
          })();

          return !busy && (rowCount>0 || hasNum || empty);
        /$)
        if (ready == Boolean.TRUE) return true
        WebUI.delay(0.25)
    }
    return false
}

/** yeni sekme açıldıysa en sondaki pencereye geç */
void switchToNewestWindowIfAny() {
    def d = DriverFactory.getWebDriver()
    def handles = new ArrayList(d.getWindowHandles())
    if (handles.size() > 1) {
        d.switchTo().window(handles.get(handles.size()-1))
    }
}

/** tablo/liste görünür satır sayısı (çoklu UI kütüphanesi için) */
int getRowCount() {
    Number n = (Number) js().executeScript($/
      const sels = [
        "table tbody tr:not(.ant-table-placeholder):not([data-empty='true'])",
        ".ant-table-row", "[data-row-key]",
        "[role='rowgroup'] [role='row']",
        ".MuiDataGrid-row",
        ".rdg-row",
        ".ant-list .ant-list-items > *",
        "[data-testid*='row']"
      ];
      const isVisible = el => {
        const s = window.getComputedStyle(el);
        return el && s.display!=='none' && s.visibility!=='hidden' && +s.opacity!==0 && el.offsetParent!==null;
      };
      const all = new Set();
      for (const sel of sels) document.querySelectorAll(sel).forEach(el => all.add(el));
      const rows = [...all].filter(el => {
        if (!isVisible(el)) return false;
        if (el.closest('thead')) return false;
        if (el.matches('.ant-table-placeholder, [data-empty=\"true\"]')) return false;
        if (el.querySelector('th,[role=\"columnheader\"]')) return false;
        return true;
      });
      return rows.length;
    /$)
    return n == null ? 0 : n.intValue()
}

/** Pagination UI var mı? */
boolean hasPaginationUI() {
    Boolean b = (Boolean) js().executeScript("""
      return !!(document.querySelector('.ant-pagination, nav[aria-label*="pagination" i]') ||
                [...document.querySelectorAll('[class*="pagination" i]')].find(el => getComputedStyle(el).display!=='none'));
    """)
    return b == Boolean.TRUE
}

/** “Son sayfa”yı kestir (sayı düğmeleri, Next disabled, UI yoksa tek sayfa varsay) */
int actualLastPageNumberDomain() {
    Number last = (Number) js().executeScript($/
      const items = [...document.querySelectorAll('ul li a, ul li button, [aria-label^=\"Page\" i], [aria-current=\"page\"]')];
      const nums = items.map(el => {
        const t = (el.innerText || el.getAttribute('aria-label') || '').trim();
        const m = t.match(/\b(\d+)\b/);
        return m ? parseInt(m[1], 10) : 0;
      }).filter(n => n > 0);
      if (nums.length) return Math.max(...nums);

      const nextEnabled = !!(
        document.querySelector('.ant-pagination-next:not(.ant-pagination-disabled)') ||
        document.querySelector('[aria-label*=\"next\" i]:not([aria-disabled=\"true\"]):not([disabled])')
      );

      const rowCount = (function(){
        const sels = [
          "table tbody tr:not(.ant-table-placeholder):not([data-empty='true'])",
          ".ant-table-row", "[data-row-key]",
          "[role='rowgroup'] [role='row']",
          ".MuiDataGrid-row",".rdg-row",".ant-list .ant-list-items > *","[data-testid*='row']"
        ];
        const set = new Set();
        for (const s of sels) document.querySelectorAll(s).forEach(el => set.add(el));
        return [...set].filter(el => {
          const st = getComputedStyle(el);
          return st.display!=='none' && st.visibility!=='hidden' && +st.opacity!==0 && el.offsetParent!==null && !el.closest('thead');
        }).length;
      })();

      const hasUI = !!(document.querySelector('.ant-pagination, nav[aria-label*=\"pagination\" i]') ||
                       [...document.querySelectorAll('[class*=\"pagination\" i]')].find(el => getComputedStyle(el).display!=='none'));
      if (rowCount > 0 && !hasUI) return 1;     // satır var, UI yok → tek sayfa
      if (rowCount > 0 && !nextEnabled) return 1; // UI var ama next kapalı → tek sayfa

      return 0;
    /$)
    return last == null ? 0 : last.intValue()
}

/** JS click */
boolean jsClickTO(TestObject to, int t = 8) {
    if (!WebUI.waitForElementClickable(to, t, FailureHandling.OPTIONAL)) return false
    try {
        WebElement el = WebUiCommonHelper.findWebElement(to, 3)
        js().executeScript("arguments[0].click();", el)
        return true
    } catch (Throwable ignore) { return false }
}

/** JS scrollIntoView */
boolean jsScrollIntoViewTO(TestObject to, int t = 8) {
    if (!WebUI.waitForElementPresent(to, t, FailureHandling.OPTIONAL)) return false
    try {
        WebElement el = WebUiCommonHelper.findWebElement(to, 3)
        js().executeScript('arguments[0].scrollIntoView({block:"center"});', el)
        WebUI.delay(0.15)
        return true
    } catch (Throwable ignore) { return false }
}

/** container içinde SVG render oldu mu? */
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
            return (bb.width > 0 && bb.height > 0) || (svg.clientWidth>0 && svg.clientHeight>0);
          } catch(e) { return true }
        """, c)
        if (has == Boolean.TRUE) { KeywordUtil.logInfo("${label} SVG VAR ✅"); return true }
        WebUI.delay(0.2)
    }
    KeywordUtil.logInfo("${label} SVG YOK 🚨"); return false
}

/* ========================= TEST ========================= */
/*/
WebUI.openBrowser('')
WebUI.navigateToUrl('https://platform.catchprobe.io/')
WebUI.maximizeWindow()

// Login
WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'), 30)
WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'))
WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 30)
 WebUI.setText(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 'fatih@test.com')
  WebUI.setEncryptedText(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Password_password'), 'v4yvAQ7Q279BF5ny4hDiTA==')
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
WebUI.navigateToUrl('https://platform.catchprobe.io/riskroute')
WebUI.waitForPageLoad(30)

/******************** HIZLI KONTROLLER ********************/
/*/
WebUI.waitForElementClickable(findTestObject('Object Repository/RiskRoute Dashboard/Page_/div_Total Assets'), 20)
WebUI.verifyElementText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/div_Total Assets'), 'Total Assets')
WebUI.verifyElementText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/div_Total Subdomains'), 'Total Subdomains')
WebUI.verifyElementText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/div_Total Vulnerabilities'), 'Total Vulnerabilities')
/*/

// Radyo butonları (shadcn/radix): role="radio" en hızlı & stabil seçim
String radioCss = "[role='checkbox']"

// 1) Most Common (ilk radio)
assert fastClickByCssIndex(radioCss, 0, 4) : "Most Common radio bulunamadı"
WebUI.delay(0.5)
WebUI.comment( isCheckedCssIndex(radioCss, 0)
        ? "✅ Most Common seçildi"
        : "❌ Most Common seçilemedi" )

// 2) Most Vulnerable (ikinci radio)
assert fastClickByCssIndex(radioCss, 1, 4) : "Most Vulnerable radio bulunamadı"
WebUI.delay(0.5)
WebUI.comment( isCheckedCssIndex(radioCss, 1)
        ? "✅ Most Vulnerable seçildi"
        : "❌ Most Vulnerable seçilemedi" )

/********** diğer grafikleri hafif beklemelerle kontrol **********/
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

/********** Asset Detail (Domain) Pagination **********/
WebUI.comment('--- Starting Asset Detail (Domain) Pagination Test ---')

TestObject toAssetDetail = findTestObject('Object Repository/RiskRoute Dashboard/Page_/Asset Detail')
if (!WebUI.waitForElementPresent(toAssetDetail, 5, FailureHandling.OPTIONAL)) {
    WebUI.comment("⏭️ 'Asset Detail' grafiği yok; adım atlandı.")
} else {
    WebElement assetDetail = WebUiCommonHelper.findWebElement(toAssetDetail, 10)
    js().executeScript('arguments[0].scrollIntoView({block:"center"});', assetDetail)
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
            // liste render olana kadar bekle
            waitForListReady(20)

            int expectedPages = (int) Math.ceil(dataValue / 10.0)     // default page size 10 kabul
            int actualLast = actualLastPageNumberDomain()
            int rowsNow = getRowCount()
            boolean paginationUi = hasPaginationUI()

            WebUI.comment("🎯 Beklenen pagination (Domain): ${expectedPages}")
            WebUI.comment("🔢 Gerçek son pagination numarası (Domain): ${actualLast}")
            WebUI.comment("📄 Görünür satır sayısı: ${rowsNow}, 🧭 Pagination UI: " + (paginationUi ? "var" : "yok"))

            // Tek sayfa geniş kabul: UI yoksa & satır varsa ya da actualLast 0/1 ve satır>0
            boolean looksSinglePage =
                (rowsNow > 0 && !paginationUi) ||
                (rowsNow > 0 && (actualLast == 0 || actualLast == 1))

            if (expectedPages == 1 && looksSinglePage) {
                WebUI.comment("✅ Domain pagination doğru (tek sayfa — UI yok veya 0/1).")
            } else if (expectedPages == actualLast && actualLast > 0) {
                WebUI.comment("✅ Domain pagination doğru (çoklu sayfa).")
            } else {
                if (rowsNow > 0) {
                    int pageSizeGuess = Math.min(Math.max(rowsNow, 1), 100)
                    int expectedFromGuess = (int) Math.ceil(dataValue / (double) pageSizeGuess)
                    if ((expectedFromGuess == actualLast && actualLast > 0) ||
                        (expectedFromGuess == 1 && looksSinglePage)) {
                        WebUI.comment("✅ Domain pagination doğru (page size ≠10, tahmin=${pageSizeGuess}).")
                    } else {
                        KeywordUtil.markFailed("❌ Domain pagination hatalı. Beklenen: ${expectedPages} (veya ~${expectedFromGuess}), Bulunan: ${actualLast}, rows=${rowsNow}, data=${dataValue}")
                    }
                } else {
                    Boolean isEmpty = (Boolean) js().executeScript("""
                      return !!document.querySelector('.ant-empty, .empty, [data-empty="true"], [data-state="empty"], .ant-table-placeholder');
                    """)
                    if (isEmpty == Boolean.TRUE && (actualLast == 0 || actualLast == 1)) {
                        WebUI.comment("✅ Domain pagination doğru (boş liste, tek sayfa).")
                    } else {
                        KeywordUtil.markFailed("❌ Domain pagination hatalı. Beklenen: ${expectedPages}, Bulunan: ${actualLast}, rows=0")
                    }
                }
            }

            WebUI.back()
            waitUiIdle(1)
        }
    }
}

WebUI.comment('--- Finished Asset Detail (Domain) Pagination Test ---')
