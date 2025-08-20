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

/** liste/pagination gerçekten hazır mı? (satır>0 || numara var || boş state) */
boolean waitForListReady(int timeoutSec = 15) {
    long end = System.currentTimeMillis() + timeoutSec*1000L
    while (System.currentTimeMillis() < end) {
        Boolean ready = (Boolean) js().executeScript($/
          const busySel = '[aria-busy="true"], .ant-spin-spinning, .animate-spin, [data-loading="true"]';
          const busy = !!document.querySelector(busySel);
          const rows = document.querySelectorAll('table tbody tr').length;
          const hasNums = [...document.querySelectorAll('ul li a, ul li button')]
              .some(el => /^\d+$/.test((el.innerText||'').trim()));
          const empty = !!document.querySelector('.ant-empty, .empty, [data-empty="true"], [data-state="empty"]');
          return !busy && (rows>0 || hasNums || empty);
        /$)
        if (ready) return true
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

/** sayfa numaralarını evrensel olarak bulup son sayıyı döndürür */
int actualLastPageNumberDomain() {
    Number last = (Number) js().executeScript($/
        // 1) öncelikle tüm ul/li/a|button numaralarını topla
        const nums = [...document.querySelectorAll('ul li a, ul li button')]
          .map(el => {
            const t = (el.innerText || '').trim();
            const aria = (el.getAttribute('aria-label') || '').toLowerCase();
            // previous/next butonlarını dışla
            if (/prev|next|previous/.test(aria)) return 0;
            return /^\d+$/.test(t) ? parseInt(t,10) : 0;
          })
          .filter(n => n>0);

        if (nums.length) return Math.max(...nums);

        // 2) numara yoksa: next disabled + satır varsa -> tek sayfa
        const rowCount = document.querySelectorAll('table tbody tr').length;
        const nextEnabledGlobal = !!document.querySelector(
          '.ant-pagination-next:not(.ant-pagination-disabled),' +
          ' [aria-label*="next"]:not([aria-disabled="true"]):not([disabled])'
        );
        if (!nextEnabledGlobal && rowCount>0) return 1;

        // 3) hâlâ yoksa: tablo varsa 1, hiç veri yoksa 0
        return rowCount>0 ? 1 : 0;
    /$)
    return last == null ? 0 : last.intValue()
}

/** tablo görünür satır sayısı */
int getRowCount() {
    Number n = (Number) js().executeScript($/return document.querySelectorAll('table tbody tr').length;/$)
    return n == null ? 0 : n.intValue()
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
        js().executeScript("arguments[0].scrollIntoView({block:'center'});", el)
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

/******************** HIZLI KONTROLLER ********************/
WebUI.waitForElementClickable(findTestObject('Object Repository/RiskRoute Dashboard/Page_/div_Total Assets'), 20)
WebUI.verifyElementText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/div_Total Assets'), 'Total Assets')
WebUI.verifyElementText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/div_Total Subdomains'), 'Total Subdomains')
WebUI.verifyElementText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/div_Total Vulnerabilities'), 'Total Vulnerabilities')

// radios
WebElement mostCommonBtn = WebUiCommonHelper.findWebElement(
    findTestObject('Object Repository/RiskRoute Dashboard/Page_/MostCommonİgnore'), 10)
js().executeScript("arguments[0].scrollIntoView({block:'center'});", mostCommonBtn)
js().executeScript("arguments[0].click();", mostCommonBtn)
waitUiIdle(1)
WebUI.comment( (mostCommonBtn.getAttribute("aria-checked")=="true"
        && mostCommonBtn.getAttribute("data-state")=="checked")
        ? "✅ Most Common seçildi" : "❌ Most Common seçilemedi" )

TestObject toMostVuln = X("(//button[contains(@class,'peer h-5 w-5 rounded-full border')])[2]")
jsScrollIntoViewTO(toMostVuln, 8); jsClickTO(toMostVuln, 8); waitUiIdle(1)
WebElement mv = WebUiCommonHelper.findWebElement(toMostVuln, 3)
WebUI.comment( (mv.getAttribute("aria-checked")=="true" && mv.getAttribute("data-state")=="checked")
        ? "✅ Most Vulnerable seçildi" : "❌ Most Vulnerable seçilemedi" )

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
            // liste render olana kadar bekle
            waitForListReady(15)

            int expectedPages = (int) Math.ceil(dataValue / 10.0)
            int actualLast = actualLastPageNumberDomain()

            if (actualLast == 0) {
                int rows = getRowCount()
                if (rows > 0 && rows <= 10) actualLast = 1
            }

            WebUI.comment("🎯 Beklenen pagination (Domain): ${expectedPages}")
            WebUI.comment("🔢 Gerçek son pagination numarası (Domain): ${actualLast}")

            boolean looksSinglePage =
                (actualLast == 1) ||
                (actualLast == 0 && getRowCount() > 0 && getRowCount() <= 10)

            if (expectedPages == 1 && looksSinglePage) {
                WebUI.comment("✅ Domain pagination doğru (tek sayfa).")
            } else if (expectedPages == actualLast && actualLast > 0) {
                WebUI.comment("✅ Domain pagination doğru.")
            } else {
                int rowsNow = getRowCount()
                if (rowsNow > 0) {
                    int pageSizeGuess = Math.min(Math.max(rowsNow, 1), 50)
                    int expectedFromGuess = (int) Math.ceil(dataValue / (double) pageSizeGuess)
                    if (expectedFromGuess == actualLast || (expectedFromGuess == 1 && looksSinglePage)) {
                        WebUI.comment("✅ Domain pagination doğru (page size ≠10, tahmin=${pageSizeGuess}).")
                    } else {
                        KeywordUtil.markFailed("❌ Domain pagination hatalı. Beklenen: ${expectedPages} (veya ~${expectedFromGuess}), Bulunan: ${actualLast}, rowsFirst=${rowsNow}, data=${dataValue}")
                    }
                } else {
                    KeywordUtil.markFailed("❌ Domain pagination hatalı. Beklenen: ${expectedPages}, Bulunan: ${actualLast}")
                }
            }

            WebUI.back()
            waitUiIdle(1)
        }
    }
}

WebUI.comment('--- Finished Asset Detail (Domain) Pagination Test ---')
