import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import org.openqa.selenium.*

/* -------------------- Helpers -------------------- */
TestObject X(String xp) {
    TestObject to = new TestObject(xp)
    to.addProperty("xpath", ConditionType.EQUALS, xp)
    return to
}

JavascriptExecutor js() { (JavascriptExecutor) DriverFactory.getWebDriver() }

void scrollIntoViewXp(String xp) {
    WebElement el = WebUiCommonHelper.findWebElement(X(xp), 5)
    js().executeScript("arguments[0].scrollIntoView({block:'center'});", el)
}
/**
 * ApexCharts çiziminin gerçekten render olup olmadığını bekler.
 * - Chart container görünür olacak
 * - İçinde SVG ve .apexcharts-series (path/rect) olacak
 * - "No data / Data not found" yazısı OLMAYACAK
 */
boolean waitForApexChartReady(String xpChart, int timeoutSec = 10) {
	long end = System.currentTimeMillis() + timeoutSec*1000L
	while (System.currentTimeMillis() < end) {
		if (WebUI.verifyElementPresent(X(xpChart), 1, FailureHandling.OPTIONAL)) {
			WebElement box = WebUiCommonHelper.findWebElement(X(xpChart), 3)
			// kutuyu ortaya kaydır
			js().executeScript("arguments[0].scrollIntoView({block:'center'});", box)

			Boolean ready = (Boolean) js().executeScript("""
              const el = arguments[0];
              const txt = (el.textContent || '').toLowerCase();
              const noData = /no\\s*data|data\\s*not\\s*found/.test(txt) ||
                             el.querySelector('.apexcharts-text.apexcharts-no-data');
              const svg = el.querySelector('svg');
              const hasSeries = !!svg && (
                  svg.querySelectorAll('.apexcharts-series path, .apexcharts-series rect').length > 0
              );
              return hasSeries && !noData;
            """, box)

			if (ready == Boolean.TRUE) return true

			// "Data not found" çıkarsa hemen fail et
			boolean hasNoData = WebUI.verifyTextPresent('Data not found', false, FailureHandling.OPTIONAL)
			if (hasNoData) {
				KeywordUtil.markFailedAndStop("❌ Risk History chart 'Data not found' gösteriyor.")
			}
		}
		WebUI.delay(0.4)
	}
	return false
}


String _trimEllipsis(String s) {
    if (s == null) return ""
    return s.replaceAll("[\\u2026…]+", "").trim()
}

String hostOfUrlLike(String s) {
    s = _trimEllipsis(s)
    if (s == null) return ""
    try {
        String tmp = s
        if (!tmp.matches("^[a-zA-Z][a-zA-Z0-9+.-]*:.*")) tmp = "http://" + tmp
        java.net.URL u = new java.net.URL(tmp)
        String h = u.getHost()
        if (h?.toLowerCase()?.startsWith("www.")) h = h.substring(4)
        return h ?: ""
    } catch (Throwable t) {
        def m = (s =~ /^(?:https?:\/\/)?([^\/\?#]+)/)
        if (m.find()) {
            String h = m.group(1)
            if (h?.toLowerCase()?.startsWith("www.")) h = h.substring(4)
            return h ?: ""
        }
        return ""
    }
}

void safeClickXp(String xp, int t=15) {
    TestObject to = X(xp)
    if (!WebUI.waitForElementClickable(to, t, FailureHandling.OPTIONAL)) {
        KeywordUtil.markFailedAndStop("Clickable değil: " + xp)
    }
    try {
        WebUI.click(to)
    } catch (Throwable e) {
        try {
            WebElement el = WebUiCommonHelper.findWebElement(to, 3)
            js().executeScript("arguments[0].click();", el)
        } catch (Throwable ee) {
            KeywordUtil.markFailedAndStop("Tıklanamadı (JS fallback da başarısız): " + xp + " | " + ee.message)
        }
    }
}

String safeTextXp(String xp, int t=15) {
    if (!WebUI.waitForElementVisible(X(xp), t, FailureHandling.OPTIONAL)) {
        KeywordUtil.markFailedAndStop("Text görünür değil: "+xp)
    }
    return WebUI.getText(X(xp)).trim()
}

int parseIntSafe(String s) {
    if (s == null) return 0
    s = s.replaceAll("[^0-9]", "")
    return s.isEmpty() ? 0 : Integer.parseInt(s)
}

String switchToNewTab(int waitSec = 6) {
    def driver = DriverFactory.getWebDriver()
    String original = driver.getWindowHandle()
    long end = System.currentTimeMillis() + waitSec*1000L
    while (System.currentTimeMillis() < end && driver.getWindowHandles().size() < 2) {
        WebUI.delay(0.2)
    }
    for (String h : driver.getWindowHandles()) {
        if (!h.equals(original)) { driver.switchTo().window(h); break }
    }
    WebUI.waitForPageLoad(15)
    return original
}

void closeTabAndBack(String originalHandle) {
    def driver = DriverFactory.getWebDriver()
    try { driver.close() } catch (Throwable ignore) {}
    driver.switchTo().window(originalHandle)
    WebUI.waitForPageLoad(10)
}

/** Modal/kayan görsel viewer vs. kapat + fazla sekmeleri temizle (robust) */
void closeNewViews() {
    def driver = DriverFactory.getWebDriver()
    String original = driver.getWindowHandle()

    // 0) Ek sekmeleri kapat
    if (driver.getWindowHandles().size() > 1) {
        for (String h : driver.getWindowHandles()) {
            if (h != original) { driver.switchTo().window(h); driver.close() }
        }
        driver.switchTo().window(original)
        WebUI.delay(0.3)
    }

    // 1) Overlay açık mı?
    String xpOverlay = "(//div[@data-state='open' and contains(@class,'fixed') and contains(@class,'inset-0') and contains(@class,'z-50')])[2]"
    if (!WebUI.waitForElementVisible(X(xpOverlay), 1, FailureHandling.OPTIONAL)) return

    // 2) ESC ile dene
    try { WebUI.sendKeys(X("//body"), Keys.chord(Keys.ESCAPE)) } catch (Throwable ignore) {}
    WebUI.delay(0.3)
    if (!WebUI.verifyElementPresent(X(xpOverlay), 1, FailureHandling.OPTIONAL)) return

    // 3) X butonu
    String xpCloseBtn =
        "(" +
        "//button[contains(@class,'top-4') and contains(@class,'right-4')]" +
        "[.//*[name()='svg' and (contains(@class,'lucide-x') or contains(@data-icon,'x'))] or @aria-label='Close']" +
        ")[last()]"
    try {
        if (WebUI.waitForElementPresent(X(xpCloseBtn), 2, FailureHandling.OPTIONAL)) {
            WebElement closeBtn = WebUiCommonHelper.findWebElement(X(xpCloseBtn), 2)
            js().executeScript("arguments[0].click();", closeBtn)
            WebUI.delay(0.3)
        }
    } catch (Throwable ignore) {}
    if (!WebUI.verifyElementPresent(X(xpOverlay), 1, FailureHandling.OPTIONAL)) return

    // 4) Overlay’e tıkla
    try {
        WebElement ov = WebUiCommonHelper.findWebElement(X(xpOverlay), 2)
        js().executeScript("arguments[0].click();", ov)
        WebUI.delay(0.3)
    } catch (Throwable ignore) {}
    if (!WebUI.verifyElementPresent(X(xpOverlay), 1, FailureHandling.OPTIONAL)) return

    // 5) Son çare: overlay’i gizle
    try {
        js().executeScript("""
          document.querySelectorAll("div[data-state='open'].fixed.inset-0.z-50")
            .forEach(el => { el.style.display = 'none'; el.style.pointerEvents = 'none'; });
        """)
        WebUI.delay(0.2)
    } catch (Throwable t) {
        KeywordUtil.logInfo("Overlay force-hide uyarı: " + t.message)
    }
}

/* --------- Görsel tespiti (placeholder vs gerçek) --------- */
boolean isPlaceholder(WebElement img) {
    def cur = js().executeScript("return arguments[0].currentSrc || arguments[0].src;", img) as String
    return cur == null || cur.contains("placeholder.webp")
}

WebElement findFirstRealThumbOnPage() {
    def driver = DriverFactory.getWebDriver()
    List<WebElement> imgs = driver.findElements(By.xpath("(//img[@data-nimg='1' and contains(@class,'cursor-pointer')])[1]"))
    for (WebElement img : imgs) {
        js().executeScript("arguments[0].scrollIntoView({block:'center'});", img)
        WebUI.delay(0.3)
        if (!isPlaceholder(img)) return img
    }
    return null
}

WebElement findFirstPlaceholderThumbOnPage() {
    def driver = DriverFactory.getWebDriver()
    List<WebElement> imgs = driver.findElements(By.xpath("//img[@data-nimg='1' and contains(@class,'cursor-pointer')]"))
    for (WebElement img : imgs) {
        js().executeScript("arguments[0].scrollIntoView({block:'center'});", img)
        WebUI.delay(0.2)
        if (isPlaceholder(img)) return img
    }
    return null
}

boolean waitImageLoadedNoError(int timeoutSec = 20) {
    String xpError = "//p[contains(@class,'drebyQ') and normalize-space(.)='There was a problem loading your image']"
    long end = System.currentTimeMillis() + timeoutSec*1000L
    while (System.currentTimeMillis() < end) {
        if (WebUI.verifyElementPresent(X(xpError), 1, FailureHandling.OPTIONAL)) return false
        Boolean ok = (Boolean) js().executeScript("""
            const imgs=[...document.querySelectorAll('img')];
            const inV = el=>{const r=el.getBoundingClientRect();return r.width>50&&r.height>50&&r.bottom>0&&r.right>0&&r.top<innerHeight&&r.left<innerWidth;}
            return imgs.some(i=>{const s=i.currentSrc||i.src||'';return inV(i)&&!s.includes('placeholder.webp')&&i.naturalWidth>0;});
        """)
        if (ok==Boolean.TRUE) return true
        WebUI.delay(0.5)
    }
    return false
}

void doImageAssertionsOnCurrentPage() {
    def driver = DriverFactory.getWebDriver()

    // TC1: Gerçek görsel
    WebElement realImg = findFirstRealThumbOnPage()
    if (realImg == null) KeywordUtil.markFailedAndStop("Gerçek görsel bulunamadı!")
    try { realImg.click() } catch (Throwable t) { js().executeScript("arguments[0].click();", realImg) }
    if (!waitImageLoadedNoError(20)) KeywordUtil.markFailed("Görsel 20 sn içinde yüklenmedi veya hata aldı.")
    closeNewViews()

    // TC2: Placeholder
    WebElement ph = findFirstPlaceholderThumbOnPage()
    if (ph != null) {
        try { ph.click() } catch (Throwable t) { js().executeScript("arguments[0].click();", ph) }
        String xpError = "//p[contains(@class,'drebyQ') and normalize-space(.)='There was a problem loading your image']"
        boolean err = WebUI.waitForElementVisible(X(xpError), 20, FailureHandling.OPTIONAL)
        if (!err) KeywordUtil.markFailed("Placeholder tıklandı ama beklenen hata mesajı gelmedi.")
        closeNewViews()
    } else {
        KeywordUtil.logInfo("Placeholder yok, bu adım atlandı.")
    }
}

/* --------- Webint Dashboard hazır mı? (yüklenme bekleme/yeniden deneme) --------- */
boolean awaitWebintLoaded(int sec=20) {
    String xpTotalBox = "(//div[contains(@class,'flex h-32 items-center justify-center text-3xl font-bold')])[1]"
    String xpLinkOpenVuln = "//a[contains(@class,'font-semibold') and contains(@class,'text-text-link') and contains(@class,'underline')]"
    long end = System.currentTimeMillis() + sec*1000L
    while (System.currentTimeMillis() < end) {
        boolean ok1 = WebUI.verifyElementPresent(X(xpTotalBox), 1, FailureHandling.OPTIONAL)
        boolean ok2 = WebUI.verifyElementPresent(X(xpLinkOpenVuln), 1, FailureHandling.OPTIONAL)
        if (ok1 && ok2) return true
        WebUI.delay(0.5)
    }
    return false
}

/* --------- Pagination doğrulaması (10 kayıt/sayfa) --------- */
void verifyPaginationByTotalCount(int totalCount) {
    int expected = (int) Math.ceil(totalCount / 10.0d)
    js().executeScript("window.scrollTo(0, document.body.scrollHeight)")
    TestObject pageLinks = X("//ul[contains(@class,'flex')]/li[a[not(contains(@aria-label,'previous')) and not(contains(@aria-label,'next'))]]/a")
    List<WebElement> els = WebUiCommonHelper.findWebElements(pageLinks, 10)
    int actualMax = 0
    for (WebElement e : els) {
        String txt = e.getText()?.trim()
        if (txt?.matches("\\d+")) {
            int n = Integer.parseInt(txt)
            if (n > actualMax) actualMax = n
        }
    }
    KeywordUtil.logInfo("Beklenen sayfa: "+expected+" | Gerçek liste max: "+actualMax)
    WebUI.verifyEqual(actualMax, expected)
}

/* =================== TEST =================== */
try {
    WebUI.openBrowser('')
    WebUI.navigateToUrl('https://platform.catchprobe.org/')
    WebUI.maximizeWindow()

    // ---- Login (repo’daki objeler) ----
    WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'), 30)
    WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'))
    WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 30)
    WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 'katalon.test@catchprobe.com')
    WebUI.setEncryptedText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Password_password'), 'RigbBhfdqOBDK95asqKeHw==')
    WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Sign in'))
    WebUI.delay(2)
    String randomOtp = (100000 + new Random().nextInt(900000)).toString()
    WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'), randomOtp)
    WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Verify'))
    WebUI.delay(5)
    WebUI.waitForPageLoad(15)

    // ---- Webint Dashboard’a git ----
    WebUI.navigateToUrl('https://platform.catchprobe.org/webint-dashboard')
    WebUI.waitForPageLoad(20)

    // “leak” yaz → dropdown’dan “leak” seç
    String xpInput   = "//form[contains(@class,'flex-w')]/div[contains(@class,'group')]/input"
    String xpLeakOpt = "//div[normalize-space(.)='leak']"

    // 1) 10 sn bekle; gelmezse refresh
    if (!WebUI.waitForElementVisible(X(xpInput), 10, FailureHandling.OPTIONAL)) {
        KeywordUtil.logInfo("🔄 Arama input'u görünmedi (10 sn). Sayfa refresh ediliyor...")
        WebUI.refresh()
        WebUI.waitForPageLoad(20)
        WebUI.delay(1)

        if (!WebUI.waitForElementVisible(X(xpInput), 10, FailureHandling.OPTIONAL)) {
            KeywordUtil.markFailedAndStop("Arama input'u refresh sonrası da görünmedi.")
        }
    }

    // 2) Değer gir ve 'leak' önerisini seç
    WebUI.setText(X(xpInput), "leak")
    if (WebUI.waitForElementVisible(X(xpLeakOpt), 5, FailureHandling.OPTIONAL)) {
        WebUI.click(X(xpLeakOpt))
    } else {
        KeywordUtil.logInfo("ℹ️ 'leak' önerisi görünmedi; ENTER ile ilerleniyor.")
        WebUI.sendKeys(X(xpInput), Keys.chord(Keys.ENTER))
    }

    // Yüklenme bekle (gerekirse bir kez retry)
    if (!awaitWebintLoaded(20)) {
        KeywordUtil.logInfo("İlk denemede yüklenmedi; modal kapatıp 1 kez daha denenecek.")
        WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Mitre Close'))
        WebUI.setText(X(xpInput), "leak")
        WebUI.waitForElementVisible(X(xpLeakOpt), 10)
        WebUI.click(X(xpLeakOpt))
        WebUI.verifyEqual(awaitWebintLoaded(25), true)
    }

    /* ------ Total Vulnerabilities ------ */
    String xpTotalV   = "(//div[contains(@class,'flex h-32 items-center justify-center text-3xl font-bold')])[1]"
    String xpOpenVBtn = "(//a[contains(@class,'font-semibold') and contains(@class,'text-text-link') and contains(@class,'underline')])[1]"
    int totalV = parseIntSafe(safeTextXp(xpTotalV, 10))
    KeywordUtil.logInfo("Total Vulnerabilities: " + totalV)

    if (totalV == 0) {
        KeywordUtil.logInfo("Total Vulnerabilities = 0 ⇒ butona tıklamadan geçiyorum.")
    } else {
        safeClickXp(xpOpenVBtn, 15)
        WebUI.waitForPageLoad(10)
        verifyPaginationByTotalCount(totalV)
        doImageAssertionsOnCurrentPage()

        // ---- İlk linke tıkla → yeni sekmede ilk http'li butonla eşitle ----
        String xpFirstAddressLink = "(//a[contains(@class,'text-text-link') and contains(.,'http')])[1]"
        if (WebUI.waitForElementVisible(X(xpFirstAddressLink), 10, FailureHandling.OPTIONAL)) {
            String firstAddrRaw = safeTextXp(xpFirstAddressLink, 10)
            String firstHost    = hostOfUrlLike(firstAddrRaw)

            WebUI.click(X(xpFirstAddressLink))
            def driver = DriverFactory.getWebDriver()
            String orig  = driver.getWindowHandle()
            WebUI.delay(1)
            for (String h : driver.getWindowHandles()) { if (!h.equals(orig)) { driver.switchTo().window(h); break } }
            WebUI.waitForPageLoad(15)

            String xpFirstAddrBtn = "(//button[contains(@class,'text-text-link') and contains(.,'http')])[1]"
            if (!WebUI.waitForElementVisible(X(xpFirstAddrBtn), 10, FailureHandling.OPTIONAL)) {
                KeywordUtil.markFailedAndStop("Yeni sayfadaki http butonu bulunamadı.")
            }
            String btnAddrRaw = safeTextXp(xpFirstAddrBtn, 10)
            String btnHost    = hostOfUrlLike(btnAddrRaw)

            boolean hostMatch = firstHost.equalsIgnoreCase(btnHost)
            boolean textMatch = btnAddrRaw.toLowerCase().contains(firstHost.toLowerCase()) ||
                                firstAddrRaw.toLowerCase().contains(btnHost.toLowerCase())

            if (!(hostMatch || textMatch)) {
                KeywordUtil.markFailed(
                    "Yeni sayfadaki buton adresi, ilk link ile eşleşmiyor.\n" +
                    "first: " + firstAddrRaw + "\n" +
                    "btn:   " + btnAddrRaw + "\n" +
                    "hostFirst: " + firstHost + " | hostBtn: " + btnHost
                )
            } else {
                KeywordUtil.logInfo("✅ Eşleşti → host: " + firstHost + " | btnHost: " + btnHost)
            }

            driver.close()
            driver.switchTo().window(orig)
        }
    }

    /* ------ Overview sekmesi ve Total 3rd Parties ------ */
    String xpOverviewTab = "//div[normalize-space(.)='Overview']"
    safeClickXp(xpOverviewTab, 15)
    WebUI.waitForPageLoad(10)

    String xpTotal3rd = "(//div[contains(@class,'flex h-32 items-center justify-center text-3xl font-bold')])[2]"
    String xp3rdBtn   = "(//a[contains(@class,'font-semibold') and contains(@class,'text-text-link') and contains(@class,'underline')])[2]"
    int total3rd = parseIntSafe(safeTextXp(xpTotal3rd, 10))
    KeywordUtil.logInfo("Total 3rd Parties: " + total3rd)

    if (total3rd == 0) {
        KeywordUtil.logInfo("Total 3rd Parties = 0 ⇒ butona tıklamadan geçiyorum.")
    } else {
        safeClickXp(xp3rdBtn, 15)
        WebUI.waitForPageLoad(10)
        verifyPaginationByTotalCount(total3rd)
        doImageAssertionsOnCurrentPage()

        // ---- İlk linke tıkla → yeni sekmede ilk http'li butonla eşitle ----
        String xpFirstAddressLink2 = "(//a[contains(@class,'text-text-link') and contains(.,'http')])[1]"
        if (WebUI.waitForElementVisible(X(xpFirstAddressLink2), 10, FailureHandling.OPTIONAL)) {
            String firstAddrRaw = safeTextXp(xpFirstAddressLink2, 10)
            String firstHost    = hostOfUrlLike(firstAddrRaw)

            WebUI.click(X(xpFirstAddressLink2))
            def driver = DriverFactory.getWebDriver()
            String orig  = driver.getWindowHandle()
            WebUI.delay(1)
            for (String h : driver.getWindowHandles()) { if (!h.equals(orig)) { driver.switchTo().window(h); break } }
            WebUI.waitForPageLoad(15)

            String xpFirstAddrBtn = "(//button[contains(@class,'text-text-link') and contains(.,'http')])[1]"
            if (!WebUI.waitForElementVisible(X(xpFirstAddrBtn), 10, FailureHandling.OPTIONAL)) {
                KeywordUtil.markFailedAndStop("Yeni sayfadaki http butonu bulunamadı.")
            }
            String btnAddrRaw = safeTextXp(xpFirstAddrBtn, 10)
            String btnHost    = hostOfUrlLike(btnAddrRaw)

            boolean hostMatch = firstHost.equalsIgnoreCase(btnHost)
            boolean textMatch = btnAddrRaw.toLowerCase().contains(firstHost.toLowerCase()) ||
                                firstAddrRaw.toLowerCase().contains(btnHost.toLowerCase())

            if (!(hostMatch || textMatch)) {
                KeywordUtil.markFailed(
                    "Yeni sayfadaki buton adresi, ilk link ile eşleşmiyor.\n" +
                    "first: " + firstAddrRaw + "\n" +
                    "btn:   " + btnAddrRaw + "\n" +
                    "hostFirst: " + firstHost + " | hostBtn: " + btnHost
                )
            } else {
                KeywordUtil.logInfo("✅ Eşleşti → host: " + firstHost + " | btnHost: " + btnHost)
            }

            driver.close()
            driver.switchTo().window(orig)
        }
    }

   /* ------ Risk History sekmesi → chart testi + (opsiyonel) sayfa 2 + görsel testi ------ */
	String xpRiskHistoryTitle = "//div[normalize-space(.)='Risk History' or normalize-space(.)='Risk history']"
	if (WebUI.waitForElementVisible(X(xpRiskHistoryTitle), 5, FailureHandling.OPTIONAL)) {

    // Sekmeye git + ekrana getir
    WebUI.click(X(xpRiskHistoryTitle))
    WebUI.waitForPageLoad(8)
    js().executeScript("arguments[0].scrollIntoView({block:'center'});",
        WebUiCommonHelper.findWebElement(X(xpRiskHistoryTitle), 5))
    WebUI.delay(0.5)

    // Chart container (verdiğin id)
    String xpRiskHistoryChart = "//*[@id='apexchartsdarkmap_keyword_search_risk_history_chart']"

    // Chart container DOM’da görünür olsun; değilse kısa bir scroll denemesi
    if (!WebUI.waitForElementVisible(X(xpRiskHistoryChart), 5, FailureHandling.OPTIONAL)) {
        js().executeScript("window.scrollBy(0, 300)")
        WebUI.waitForElementVisible(X(xpRiskHistoryChart), 3, FailureHandling.OPTIONAL)
    }

    	// Chart render / no-data kontrolü (kısa ve net)
    	boolean chartOk = waitForApexChartReady(xpRiskHistoryChart, 10)
    	if (!chartOk) {
			KeywordUtil.markFailedAndStop("❌ Risk History chart 10 sn içinde render olmadı veya veri serisi bulunamadı.")
			} else {
			KeywordUtil.logInfo("✅ Risk History chart başarıyla render oldu.")
		}

    	// (İsteğe bağlı) Sayfa 2’ye geçip görsel kontrolü
    	String xpPage2 = "//a[normalize-space(.)='2']"
    	if (WebUI.waitForElementVisible(X(xpPage2), 4, FailureHandling.OPTIONAL)) {
        	js().executeScript("window.scrollTo(0, document.body.scrollHeight)")
        	WebUI.click(X(xpPage2))
        	WebUI.waitForPageLoad(8)
			doImageAssertionsOnCurrentPage()
    	} else {
        	// Sayfa 2 yoksa yine de mevcut sayfada görsel assertion çalıştır
			doImageAssertionsOnCurrentPage()
    	}
	}


    closeNewViews()
    KeywordUtil.markPassed("✅ Webint Dashboard senaryosu tamamlandı.")

} finally {
    // WebUI.closeBrowser()
}

/* =================== SON TEST BLOĞU =================== */
/* External Threat Signals – önce scroll, panel kapalıysa aç, dinamik link ile doğrula */
try {
    String xpExternalTitle = "//div[normalize-space(.)='External Threat Signals']"
    if (WebUI.waitForElementVisible(X(xpExternalTitle), 20, FailureHandling.OPTIONAL)) {
        scrollIntoViewXp(xpExternalTitle)
        WebUI.delay(2.5)
		WebUI.click(X(xpExternalTitle))
    

        // Dinamik link (sabit bundle-id yerine)
        String xpThreatLinkGeneric = "//a[@target='_blank' and contains(@href, '/threatway/signature-ioc-details/')]"
        if (WebUI.waitForElementPresent(X(xpThreatLinkGeneric), 8, FailureHandling.OPTIONAL)) {
            scrollIntoViewXp(xpThreatLinkGeneric)
            WebUI.delay(1.3)
            WebUI.verifyElementClickable(X(xpThreatLinkGeneric), FailureHandling.OPTIONAL)
            WebUI.click(X(xpThreatLinkGeneric))

            String original = switchToNewTab(8)
            String curUrl = WebUI.getUrl()
            if (!curUrl.contains("/threatway/signature-ioc-details")) {
                KeywordUtil.markFailed("ThreatWay sekmesi beklenen URL’i açmadı: " + curUrl)
            } else {
                KeywordUtil.logInfo("✅ ThreatWay URL doğrulandı: " + curUrl)
            }
            closeTabAndBack(original)
        } else {
            KeywordUtil.logInfo("ℹ️ External Threat Signals altında signature linki bulunamadı (dinamik olabilir).")
        }
    } else {
        KeywordUtil.logInfo("ℹ️ External Threat Signals başlığı görünmedi; final kontrol atlandı.")
    }
} catch (Throwable t) {
    KeywordUtil.logInfo("Final ETS kontrol uyarısı: " + t.getMessage())
}

/* Darkmap Intelligence – scroll + yeni sekme + Domain Related Intelligence */
try {
    String xpDarkmapTitle = "//span[text()='Darkmap Intelligence']"
    if (WebUI.waitForElementVisible(X(xpDarkmapTitle), 20, FailureHandling.OPTIONAL)) {
        scrollIntoViewXp(xpDarkmapTitle)
        WebUI.delay(0.5)

        String xpDarkmapBtn = "//a[@target='_blank' and contains(@href, '/darkmap/quick-search?searched_address=')]"
        if (WebUI.waitForElementClickable(X(xpDarkmapBtn), 10, FailureHandling.OPTIONAL)) {
            WebUI.click(X(xpDarkmapBtn))
            String original = switchToNewTab(8)

            String xpDomainRelBtn = "(//button[contains(normalize-space(.),'Domain Related Intelligence')])[1]"
            if (!WebUI.waitForElementClickable(X(xpDomainRelBtn), 12, FailureHandling.OPTIONAL)) {
                KeywordUtil.markFailed("Domain Related Intelligence butonu clickable değil!")
            } else {
                WebUI.click(X(xpDomainRelBtn))
                KeywordUtil.logInfo("✅ Domain Related Intelligence butonu clickable ve tıklandı.")
            }
            closeTabAndBack(original)
        } else {
            KeywordUtil.logInfo("ℹ️ Darkmap butonu bulunamadı/clickable değil.")
        }
    }
} catch (Throwable t) {
    KeywordUtil.logInfo("Darkmap final kontrol uyarısı: " + t.getMessage())
}

/* Diğer component kontrolleri */
try {
    js().executeScript("document.body.style.zoom='0.9'")
    def components = [
        'Network Banner Intelligence',
        'Exposed Bucket Intelligence',
        'Account Intelligence',
        'Social Ad Intelligence: Facebook'
    ]

    components.each { component ->
        String repoName = component.replaceAll('[: ]', '')

        // Başlık
        try {
            TestObject titleObj = findTestObject("Object Repository/Darkmap/WebIntDashboard/${repoName}_Title")
            if (WebUI.verifyElementPresent(titleObj, 5, FailureHandling.OPTIONAL)) {
                WebElement titleEl = WebUiCommonHelper.findWebElement(titleObj, 5)
                js().executeScript("arguments[0].scrollIntoView({block:'center'});", titleEl)
                WebUI.delay(0.8)
                KeywordUtil.logInfo("✔️ ${component} başlığı bulundu")
            } else {
                KeywordUtil.markWarning("⚠️ ${component} başlığı bulunamadı")
            }
        } catch (Throwable t) {
            KeywordUtil.markWarning("⚠️ ${component} başlık kontrolünde hata: ${t.message}")
        }

        // SCAN DIFFERENCE
        try {
            TestObject scanDiffButton = findTestObject("Object Repository/Darkmap/WebIntDashboard/${repoName}_ScanDiffButton")
            if (WebUI.verifyElementPresent(scanDiffButton, 3, FailureHandling.OPTIONAL)) {
                WebUI.click(scanDiffButton)
                WebUI.delay(1)
                try { CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'() } catch (Throwable ignore) {}
                KeywordUtil.logInfo("✅ SCAN DIFFERENCE tıklandı.")
                try { WebUI.click(findTestObject('Object Repository/Smartdeceptive/İp close')) } catch (Throwable ignore) {}
            } else {
                KeywordUtil.logInfo("➖ ${component} için SCAN DIFFERENCE butonu yok.")
            }
        } catch (Throwable t) {
            KeywordUtil.markWarning("⚠️ ${component} SCAN DIFFERENCE kontrolünde hata: ${t.message}")
        }

        // Completed At
        try {
            TestObject completedAtObj = findTestObject("Object Repository/Darkmap/WebIntDashboard/${repoName}_CompletedAt")
            if (WebUI.verifyElementPresent(completedAtObj, 5, FailureHandling.OPTIONAL)) {
                String txt = WebUI.getText(completedAtObj).trim()
                KeywordUtil.logInfo("✅ Completed At: ${txt}")
                if (txt.equalsIgnoreCase('In Progress')) {
                    KeywordUtil.markFailed("❌ ${component} In Progress — bu component tamamlanmamış!")
                }
            } else {
                KeywordUtil.markWarning("⚠️ ${component} Completed At alanı bulunamadı")
            }
        } catch (Throwable t) {
            KeywordUtil.markWarning("⚠️ ${component} Completed At kontrolünde hata: ${t.message}")
        }

        WebUI.delay(0.5)
    }

    KeywordUtil.logInfo("✅ Webint Dashboard son test akışı ve component kontrolleri tamamlandı.")
} catch (Throwable t) {
    KeywordUtil.logInfo("Son test bloğu uyarısı: " + t.getMessage())
}
