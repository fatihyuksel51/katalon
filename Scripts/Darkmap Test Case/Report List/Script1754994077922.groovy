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
TestObject X(String xp) { def to=new TestObject(xp); to.addProperty("xpath", ConditionType.EQUALS, xp); return to }
JavascriptExecutor js() { (JavascriptExecutor) DriverFactory.getWebDriver() }
void scrollIntoViewXp(String xp) {
    WebElement el = WebUiCommonHelper.findWebElement(X(xp), 10)
    js().executeScript("arguments[0].scrollIntoView({block:'center'});", el)
}
void safeClickXp(String xp, int t=15) {
    TestObject to = X(xp)
    if (!WebUI.waitForElementClickable(to, t, FailureHandling.OPTIONAL)) {
        KeywordUtil.markFailedAndStop("Clickable değil: " + xp)
    }
    try { WebUI.click(to) }
    catch (Throwable e) {
        try { WebElement el = WebUiCommonHelper.findWebElement(to, 3); js().executeScript("arguments[0].click();", el) }
        catch (Throwable ee) { KeywordUtil.markFailedAndStop("Tıklanamadı: " + xp + " | " + ee.message) }
    }
}
/** Modal/görsel viewer kapatma (kısa) */
void closeNewViews() {
    try { WebUI.sendKeys(X("//body"), Keys.chord(Keys.ESCAPE)) } catch (Throwable ignore) {}
    WebUI.delay(0.2)
    String xpClose = "("+
        "//button[@aria-label='Close' or .//*[name()='svg' and (contains(@class,'lucide-x') or @data-icon='x')]]"+
    ")[last()]"
    if (WebUI.waitForElementClickable(X(xpClose), 1, FailureHandling.OPTIONAL)) {
        try { WebUI.click(X(xpClose)) } catch (Throwable t) {
            try { WebElement b = WebUiCommonHelper.findWebElement(X(xpClose), 2); js().executeScript("arguments[0].click();", b) } catch(Throwable ignore){}
        }
    }
}

/* --------- Görsel tespiti (Dashboard ile AYNI) --------- */
boolean isPlaceholder(WebElement img) {
    def cur = js().executeScript("return arguments[0].currentSrc || arguments[0].src;", img) as String
    return cur == null || cur.contains("placeholder.webp")
}
WebElement findFirstRealThumbOnPage() {
    def driver = DriverFactory.getWebDriver()
    List<WebElement> imgs = driver.findElements(By.xpath("//img[@data-nimg='1' and contains(@class,'cursor-pointer')]"))
    for (WebElement img : imgs) {
        js().executeScript("arguments[0].scrollIntoView({block:'center'});", img)
        WebUI.delay(0.3)
        if (!isPlaceholder(img)) return img
    }
    return null
}
WebElement findFirstRealThumbOnPage2() {
	def driver = DriverFactory.getWebDriver()
	List<WebElement> imgs = driver.findElements(By.xpath("(//img[@data-nimg='1' and contains(@class,'cursor-pointer')])[2]"))
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
WebElement findFirstPlaceholder2ThumbOnPage() {
	def driver = DriverFactory.getWebDriver()
	List<WebElement> imgs = driver.findElements(By.xpath("(//img[@data-nimg='1' and contains(@class,'cursor-pointer')])[1]"))
	for (WebElement img : imgs) {
		js().executeScript("arguments[0].scrollIntoView({block:'center'});", img)
		WebUI.delay(0.2)
		if (isPlaceholder(img)) return img
	}
	return null
}
boolean waitImageLoadedNoError(int timeoutSec = 20) {
    // Web Int Dashboard’da kullandığımız hata mesajı kontrolü
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
String safeTextXp(String xp, int t=15) {
	if (!WebUI.waitForElementVisible(X(xp), t, FailureHandling.OPTIONAL)) {
		KeywordUtil.markFailedAndStop("Text görünür değil: "+xp)
	}
	return WebUI.getText(X(xp)).trim()
}
void doImageAssertionsOnCurrentPage() {
    // TC1: Gerçek görsel – açılmalı ve hata almamalı
    WebElement realImg = findFirstRealThumbOnPage()
    if (realImg == null) KeywordUtil.markFailedAndStop("Gerçek görsel bulunamadı!")
    try { realImg.click() } catch (Throwable t) { js().executeScript("arguments[0].click();", realImg) }
    if (!waitImageLoadedNoError(20)) KeywordUtil.markFailed("Görsel 20 sn içinde yüklenmedi veya hata aldı.")
    closeNewViews()

    // TC2: Placeholder varsa, tıklanınca hata beklenir; yoksa step atlanır
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
void doImageAssertionsOnCurrentPage2() {
	// TC1: Gerçek görsel – açılmalı ve hata almamalı
	WebElement realImg = findFirstRealThumbOnPage2()
	if (realImg == null) KeywordUtil.markFailedAndStop("Gerçek görsel bulunamadı!")
	try { realImg.click() } catch (Throwable t) { js().executeScript("arguments[0].click();", realImg) }
	if (!waitImageLoadedNoError(20)) KeywordUtil.markFailed("Görsel 20 sn içinde yüklenmedi veya hata aldı.")
	closeNewViews()

	// TC2: Placeholder varsa, tıklanınca hata beklenir; yoksa step atlanır
	WebElement ph = findFirstPlaceholder2ThumbOnPage()
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

/* =================== TEST =================== */
	/*/
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
    WebUI.delay(12)
    WebUI.waitForPageLoad(15)
    /*/

    // ---- Webint Dashboard’a git ----
    WebUI.navigateToUrl('https://platform.catchprobe.org/darkmap/report')
    WebUI.waitForPageLoad(20)

/* 1) Create */
String xpCreateBtn = "//button[normalize-space(.)='Create'] | //a[normalize-space(.)='CREATE']"
safeClickXp(xpCreateBtn, 15)
WebUI.waitForPageLoad(15)

/* 2) Form alanlarını doldur (katalon) */
String xpTitleInput = "//input[@type='text' and @name='title']"
String xpCommentTxt = "//textarea[@name='comment']"
if (!WebUI.waitForElementVisible(X(xpTitleInput), 15, FailureHandling.OPTIONAL)) {
    KeywordUtil.markFailedAndStop("Report form yüklenmedi (title input görünmedi).")
}
WebUI.setText(X(xpTitleInput), "katalon")
if (!WebUI.waitForElementVisible(X(xpCommentTxt), 15, FailureHandling.OPTIONAL)) {
    KeywordUtil.markFailedAndStop("Report form yüklenmedi (analyst comment görünmedi).")
}
WebUI.setText(X(xpCommentTxt), "katalon")

/* 3) Keywords sekmesi */
String xpKeywordsBtn = "//button[@type='button' and contains(normalize-space(.), 'Keywords')]"
scrollIntoViewXp(xpKeywordsBtn)
safeClickXp(xpKeywordsBtn, 10)

/* 4) Yeşil + (bg-success) */
String xpGreenPlus = "(//*[name()='button' and contains(@class,'bg-success')])[2]"
scrollIntoViewXp(xpGreenPlus)
safeClickXp(xpGreenPlus, 10)

/* 5) Görsel doğrulaması — Web Int Dashboard ile AYNI */
doImageAssertionsOnCurrentPage()

/* 6) Mavi + → çöp’e dönüş kontrolü */
String xpBluePlus = "//*[name()='button' and contains(@class, 'bg-primary') and .//*[name()='svg' and contains(@class,'lucide-plus')]]"
scrollIntoViewXp(xpBluePlus)
safeClickXp(xpBluePlus, 10)

String xpTrashBtn = "//*[name()='button' and contains(@class,'bg-destructive') and .//*[name()='svg' and contains(@class,'lucide-trash')]]"
if (!WebUI.waitForElementVisible(X(xpTrashBtn), 10, FailureHandling.OPTIONAL)) {
    KeywordUtil.markFailedAndStop("Mavi + tıklandı ama buton çöp (destructive) durumuna geçmedi.")
}
KeywordUtil.logInfo("✅ Öğenin eklendiği doğrulandı; buton çöp ikonuna döndü.")

/* 7) Close */
String xpCloseTextBtn = "//button[normalize-space(.)='CLOSE']"
if (WebUI.waitForElementClickable(X(xpCloseTextBtn), 5, FailureHandling.OPTIONAL)) {
    WebUI.click(X(xpCloseTextBtn))
} else {
    closeNewViews()
}

KeywordUtil.logInfo("OK: Report List -> Create -> Keywords -> Gorsel dogrulama (Dashboard ile ayni) -> Ekle/Cop -> Close tamam.")

/* ================== REPORT – DEVAM BLOĞU ================== */
/* 1) Küçük resme tıkla → büyük görsel yüklenmesini doğrula (Dashboard ile aynı mantık) */
String xpThumb1 = "//img[@data-nimg='1' and contains(@class,'cursor-pointer')]"
String xpThumb2 = "(//img[@data-nimg='1' and contains(@class,'cursor-pointer')])[2]"

if (!WebUI.waitForElementClickable(X(xpThumb1), 10, FailureHandling.OPTIONAL)) {
	KeywordUtil.markFailedAndStop("Küçük görsel (thumb) bulunamadı/kliklenemiyor.")
}
scrollIntoViewXp(xpThumb1)
WebUI.click(X(xpThumb1))

// WebInt Dashboard’daki gibi: herhangi bir gerçek img görünür ve hata yoksa OK
if (!waitImageLoadedNoError(20)) {
	KeywordUtil.markFailedAndStop("Görsel 20 sn içinde yüklenmedi ya da hata verdi.")
}

// Ek olarak 2. img ile de doğrula (placeholder olmasın, gerçek boyutlansın)
if (WebUI.waitForElementVisible(X(xpThumb2), 10, FailureHandling.OPTIONAL)) {
	WebElement big = WebUiCommonHelper.findWebElement(X(xpThumb2), 5)
	Boolean ok2 = (Boolean) js().executeScript("""
        const i=arguments[0];
        const s=i.currentSrc||i.src||'';
        const r=i.getBoundingClientRect();
        return r.width>50 && r.height>50 && !s.includes('placeholder.webp') && i.naturalWidth>0;
    """, big)
	if (ok2 != Boolean.TRUE) {
		KeywordUtil.markFailedAndStop("İkinci görsel gerçek yüklenmiş görünmüyor.")
	}
}
// modal/overlay kapat
doImageAssertionsOnCurrentPage2()


/* 2) Pencil (düzenle) butonuna bas → 'katalon' yorumunu ekle */
String xpPencilBtn = "//*[name()='button' and @type='button' and .//*[name()='svg' and contains(@class,'lucide-pencil')]]"
safeClickXp(xpPencilBtn, 15)

String xpComment = "(//textarea[@name='comment'])[2]"
if (!WebUI.waitForElementVisible(X(xpComment), 10, FailureHandling.OPTIONAL)) {
	KeywordUtil.markFailedAndStop("Comment alanı görünmedi.")
}
WebUI.setText(X(xpComment), "katalon")

/* 3) ADD ADDRESS butonuna scroll + tıkla */
String xpAddAddrBtn = "//button[normalize-space(.)='ADD ADDRESS']"
scrollIntoViewXp(xpAddAddrBtn)
safeClickXp(xpAddAddrBtn, 15)

/* 4) 'katalon' metni ve Keywords rozet sayısı = 1 doğrula */
String xpKatalonText   = "//span[contains(@class,'col-span-4') and normalize-space(.)='katalon']"
String xpKeywordsBadge = "(//span[contains(@class,'bg-accent') and contains(@class,'rounded-full')])[1]"

WebUI.verifyElementPresent(X(xpKatalonText), 10)
String badgeTxt = safeTextXp(xpKeywordsBadge, 10).replaceAll("[^0-9]", "")
WebUI.verifyEqual(badgeTxt, "1")

/* 5) Create Report’a scroll + tıkla */
String xpCreateReport = "//button[normalize-space(.)='Create Report']"
scrollIntoViewXp(xpCreateReport)
safeClickXp(xpCreateReport, 20)

/* 6) İlk durum: In the queue (destructive chip) ve Download yok */
String xpStatusDestructive = "//td//span[contains(@class,'bg-destructive') and contains(@class,'rounded-full')]"
String xpStatusChipAny     = "(//td//span[contains(@class,'rounded-full')])[1]"
String xpDownloadBtn       = "//div[contains(@class,'bg-muted') and .//*[name()='svg' and contains(@class,'lucide-download')]]"
String xpDeleteBtn       = "//div[contains(@class,'bg-destructive') and .//*[name()='svg' and contains(@class,'lucide-trash2')]]"
 

// Status chip görünsün
WebUI.verifyElementPresent(X(xpStatusChipAny), 10)
// İlk etapta kırmızı (destructive) chip bekleniyor
WebUI.verifyElementPresent(X(xpStatusDestructive), 5)
// Download ilk anda olmamalı
WebUI.verifyElementNotPresent(X(xpDownloadBtn), 2)

/* 7) 20 sn bekle; başarı yoksa en fazla 2 kez refresh → 'success' ve Download gelsin */
boolean done = false
int refreshCount = 0

while (!done && refreshCount <= 2) {
	long end = System.currentTimeMillis() + 12_000L
	while (System.currentTimeMillis() < end) {
		String st = ""
		try { st = safeTextXp(xpStatusChipAny, 5).toLowerCase() } catch (Throwable ignore) {}
		boolean dl = WebUI.verifyElementPresent(X(xpDownloadBtn), 1, FailureHandling.OPTIONAL)
		if ((st.contains("COMPLETED") || st.contains("completed") || st.contains("finished")) && dl) {
			done = true
			break
		}
		WebUI.delay(1)
	}
	if (!done && refreshCount < 2) {
		KeywordUtil.logInfo("⟳ Durum tamamlanmadı, refresh deneniyor... (" + (refreshCount+1) + "/2)")
		WebUI.refresh()
		WebUI.waitForPageLoad(10)
		WebUI.delay(1)
	}
	refreshCount++
}

if (!done) {
	KeywordUtil.markWarning("Status 'Completed' olmadı veya Download butonu gelmedi (2 refresh sonrasında da).")
} else {
	KeywordUtil.logInfo("✅ Rapor üretimi tamam: Status success + Download hazır.")
}
String xpDeleteBtn       = "//div[contains(@class,'bg-destructive') and .//*[name()='svg' and contains(@class,'lucide-trash2')]]"
scrollIntoViewXp(xpDeleteBtn)
safeClickXp(xpDeleteBtn, 20)

String xpNoData = "//div[@class='ant-empty-description' and normalize-space(text())='No data']"
scrollIntoViewXp(xpNoData)
if (!WebUI.waitForElementVisible(X(xpNoData), 10, FailureHandling.OPTIONAL))
	KeywordUtil.markFailed("No data bekleniyordu, görünmedi.")
else
	KeywordUtil.logInfo("✅ Silme sonrası 'No data' doğrulandı.")

/* ================== REPORT – DEVAM BLOĞU SON ================== */



