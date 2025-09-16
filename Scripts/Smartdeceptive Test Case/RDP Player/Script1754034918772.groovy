/************** Imports (temiz) **************/
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
 import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase 
 import static com.kms.katalon.core.testdata.TestDataFactory.findTestData 
 import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject 
 import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject 
 import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint 
 import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW 
 import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile 
 import com.kms.katalon.core.model.FailureHandling as FailureHandling 
 import com.kms.katalon.core.testcase.TestCase as TestCase 
 import com.kms.katalon.core.testdata.TestData as TestData 
 import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW 
 import com.kms.katalon.core.testobject.TestObject as TestObject
  import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS 
  import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI 
  import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows 
  import internal.GlobalVariable as GlobalVariable
  import org.openqa.selenium.Keys as Keys
  import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory 
import org.openqa.selenium.JavascriptExecutor as JavascriptExecutor 
import org.openqa.selenium.WebElement as WebElement
 import com.kms.katalon.core.webui.common.WebUiCommonHelper as WebUiCommonHelper 
 import org.openqa.selenium.WebDriver as WebDriver
  import com.kms.katalon.core.testobject.ConditionType as ConditionType 
  import com.kms.katalon.core.util.KeywordUtil as KeywordUtil 
  import org.openqa.selenium.By as By 
  import org.openqa.selenium.interactions.Actions as Actions 
  import org.openqa.selenium.support.ui.WebDriverWait as WebDriverWait
   import org.openqa.selenium.support.ui.ExpectedConditions as ExpectedConditions
    import com.catchprobe.utils.MailReader as MailReader 
	import static com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords.*
	 import java.text.SimpleDateFormat 
import com.kms.katalon.core.testobject.ObjectRepository as OR
 import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject 

 import com.kms.katalon.core.model.FailureHandling
  import com.kms.katalon.core.testobject.ConditionType
   import com.kms.katalon.core.testobject.TestObject
    import com.kms.katalon.core.util.KeywordUtil
	 import com.kms.katalon.core.webui.common.WebUiCommonHelper
	  import com.kms.katalon.core.webui.driver.DriverFactory
	   import org.openqa.selenium.By 
	   import org.openqa.selenium.JavascriptExecutor
 import org.openqa.selenium.WebDriver 
 import org.openqa.selenium.WebElement 
 import org.openqa.selenium.interactions.Actions

/************** Mini yardımcılar **************/
TestObject X(String xp){
    def to = new TestObject(xp)
    to.addProperty("xpath", ConditionType.EQUALS, xp)
    return to
}
TestObject makeXpathObj(String xp){ return X(xp) }

boolean isBrowserOpen(){
    try { DriverFactory.getWebDriver(); return true } catch(Throwable t){ return false }
}

WebElement safeScrollTo(TestObject to) {
    if (to == null || !WebUI.waitForElementPresent(to, 6, FailureHandling.OPTIONAL)) {
        KeywordUtil.markFailed("❌ Scroll edilemedi (bulunamadı): ${to?.getObjectId()}")
        return null
    }
    WebElement element = WebUiCommonHelper.findWebElement(to, 10)
    JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getWebDriver()
    try {
        js.executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", element)
    } catch(Exception ignore){}
    WebUI.delay(0.4)
    return element
}

void scrollToBottom() {
    JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getWebDriver()
    js.executeScript("window.scrollTo(0, document.body.scrollHeight)")
}

boolean jsClick(WebElement el){
    try {
        ((JavascriptExecutor)DriverFactory.getWebDriver()).executeScript("arguments[0].click()", el)
        return true
    } catch(Exception ignore){ return false }
}

boolean actionsClick(WebElement el){
    try {
        new Actions(DriverFactory.getWebDriver()).moveToElement(el).pause(100).click().perform()
        return true
    } catch(Exception ignore){ return false }
}

/** Çok katmanlı güvenli tıkla (TO üstünden) */
boolean safeClick(TestObject to, int t=10){
    try {
        WebUI.waitForElementVisible(to, t, FailureHandling.OPTIONAL)
        WebUI.waitForElementClickable(to, t, FailureHandling.OPTIONAL)
        WebUI.click(to)
        return true
    } catch(Exception ex1){
        try {
            WebElement elem = WebUiCommonHelper.findWebElement(to, t)
            if (elem != null){
                try { elem.click(); return true } catch (Exception ex2) {}
                if (jsClick(elem)) return true
                if (actionsClick(elem)) return true
                try { elem.sendKeys(org.openqa.selenium.Keys.ENTER); return true } catch (Exception ex3) {}
            }
        } catch(Exception ex4){}
    }
    return false
}

/** Play hücresinin içinde tıklanabilir torunları (button/svg/role=button/tabindex) deneyen helper */
boolean safeClickPlay(WebElement playCell){
    if (playCell == null) return false
    WebDriver driver = DriverFactory.getWebDriver()
    JavascriptExecutor js = (JavascriptExecutor) driver
    try { js.executeScript("arguments[0].scrollIntoView({block:'center'});", playCell) } catch(Exception ignore){}
    WebUI.delay(0.2)

    // Tıklanabilir adaylar
    List<WebElement> candidates = new ArrayList<>()
    try { candidates.addAll(playCell.findElements(By.cssSelector("button, [role='button'], [tabindex]"))) } catch(Exception ignore){}
    if (candidates.isEmpty()){
        try { candidates.addAll(playCell.findElements(By.cssSelector("svg, svg *"))) } catch(Exception ignore){}
    }
    // Hücrenin kendisini de deneriz (en sona)
    candidates.add(playCell)

    // Sırayla click dene
    for (WebElement c : candidates){
        try { c.click(); return true } catch(Exception ignore){}
        if (jsClick(c)) return true
        if (actionsClick(c)) return true
    }
    return false
}

/** Üst dokümanda player var mı? (video/canvas) */
boolean hasPlayerInThisContext(){
    return (Boolean) WebUI.executeJavaScript(
        "return !!(document.querySelector('video, canvas, [data-player], [role=video]'));", null)
}

/** İframe(ler) dahil player var mı? */
boolean waitForPlayerAnyContext(int timeoutSec=12){
    WebDriver d = DriverFactory.getWebDriver()
    long end = System.currentTimeMillis() + timeoutSec*1000L

    while(System.currentTimeMillis() < end){
        // 1) Top document
        try { if (hasPlayerInThisContext()) return true } catch(Exception ignore){}

        // 2) Tek katman iframe tara
        List<WebElement> iframes = d.findElements(By.tagName("iframe"))
        for(int i=0; i<iframes.size(); i++){
            try{
                d.switchTo().frame(iframes.get(i))
                if (hasPlayerInThisContext()) return true
            } catch(Exception ignore) {
            } finally {
                d.switchTo().defaultContent()
            }
        }
        WebUI.delay(0.5)
    }
    return false
}

/** Video playable/duration hazır mı? (top veya iframe) */
boolean waitUntilVideoReadyAny(int timeoutSec=10){
    WebDriver d = DriverFactory.getWebDriver()
    long end = System.currentTimeMillis() + timeoutSec*1000L

    while(System.currentTimeMillis() < end){
        // top + iframes sırayla duration kontrol
        List<WebElement> frames = d.findElements(By.tagName("iframe"))
        List<Integer> idxs = new ArrayList<>()
        for(int i=0;i<frames.size();i++) idxs.add(i)
        idxs.add(-1) // -1 = top document

        for(Integer idx : idxs){
            try{
                if(idx >= 0) d.switchTo().frame(frames.get(idx))
                Object ok = ((JavascriptExecutor)d).executeScript("""
                    var v = document.querySelector('video');
                    if(!v) return false;
                    if (isNaN(v.duration) || !isFinite(v.duration)) return false;
                    if (v.readyState >= 1) return true;
                    return false;
                """)
                if (ok == Boolean.TRUE) return true
            } catch(Exception ignore){
            } finally {
                if(idx >= 0) d.switchTo().defaultContent()
            }
        }
        WebUI.delay(0.5)
    }
    return false
}

/** Herhangiı context’te (top/iframe) ilk video’nun duration’ını oku; yoksa -1 döner */
double readVideoDurationAny(){
    WebDriver d = DriverFactory.getWebDriver()

    // top
    try {
        Object dv = ((JavascriptExecutor)d).executeScript("var v=document.querySelector('video'); return v? v.duration : null;")
        if (dv instanceof Number) return ((Number)dv).doubleValue()
    } catch(Exception ignore){}

    // iframes
    List<WebElement> frames = d.findElements(By.tagName("iframe"))
    for(int i=0;i<frames.size();i++){
        try{
            d.switchTo().frame(frames.get(i))
            Object dv = ((JavascriptExecutor)d).executeScript("var v=document.querySelector('video'); return v? v.duration : null;")
            if (dv instanceof Number) return ((Number)dv).doubleValue()
        } catch(Exception ignore){
        } finally {
            d.switchTo().defaultContent()
        }
    }
    return -1d
}

/** Metinden saniye (tamsayı) çıkar: "2.767" / "2,767" / "2" → 2 */
int parseSeconds(String text){
    if (text == null) return 0
    String cleaned = text.trim().replace(',', '.')
    String head = cleaned.split("\\.")[0]
    try { return Integer.parseInt(head) } catch(Exception ignore){ return 0 }
}

/************** Oturum **************/
void ensureSession(){
    if(isBrowserOpen()) return

    WebUI.openBrowser('')
    WebUI.maximizeWindow()
    WebUI.navigateToUrl('https://platform.catchprobe.io/')

    WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'), 30)
    safeClick(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'), 10)

    WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 30)
    WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 'fatih@test.com')
    WebUI.setEncryptedText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Password_password'), 'v4yvAQ7Q279BF5ny4hDiTA==')
    safeClick(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Sign in'), 10)

    WebUI.delay(3)
    String otp = (100000 + new Random().nextInt(900000)).toString()
    WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'), otp)
    safeClick(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Verify'), 10)
    WebUI.delay(2)

    // Login sonrası bekleme
    WebUI.waitForElementVisible(X("//span[text()='Threat']"), 10, FailureHandling.OPTIONAL)

    // (Opsiyonel) Organizasyonu TEST COMPANY yap
    try {
        TestObject currentOrg = X("//div[contains(@class,'font-semibold') and contains(.,'Organization')]/span[contains(@class,'font-thin')]")
        if (WebUI.verifyElementPresent(currentOrg, 5, FailureHandling.OPTIONAL)){
            String currentOrgText = WebUI.getText(currentOrg)?.trim()
            if (currentOrgText != 'TEST COMPANY'){
                TestObject orgButton = X("//button[.//div[contains(.,'Organization :')]]")
                safeClick(orgButton, 5)
                TestObject testCompanyOption = X("//button[.//div[normalize-space()='TEST COMPANY']]")
                safeClick(testCompanyOption, 5)
                WebUI.waitForPageLoad(10)
            }
        }
    } catch(Exception ignore){}
}

/************** TEST: RDP Player **************/
ensureSession()
WebUI.navigateToUrl('https://platform.catchprobe.io/smartdeceptive/rdp-player')
WebUI.delay(5)
WebUI.waitForPageLoad(15)

// Tablo yüklendi mi (ant-table varlığı)
WebUI.waitForElementPresent(X("//div[contains(@class,'ant-table') or contains(@class,'rdp')]"), 10, FailureHandling.OPTIONAL)

// 🔹 Pagination 2 ve 3’e sırayla geç (varsa)
List<String> pageNumbers = ['2','3']
for (String pageNum : pageNumbers) {
    try {
        TestObject pageLink = X("//a[normalize-space(text())='${pageNum}']")
        scrollToBottom()
        WebUI.delay(0.6)
        if (WebUI.verifyElementPresent(pageLink, 3, FailureHandling.OPTIONAL)){
            safeClick(pageLink, 5)
            WebUI.delay(1.0)
        }
    } catch (Throwable e) {
        WebUI.comment("⚠️ Sayfa ${pageNum} tıklanamadı: ${e.message}")
    }
}

// 1) Duration hücresindeki saniyeyi al (ilk satırın 5. hücresi varsayımı)
WebDriver driver = DriverFactory.getWebDriver()
TestObject durationCellTO = X("(//td[contains(@class,'ant-table-cell')])[5]")
WebElement durationCellEl = safeScrollTo(durationCellTO)
if (durationCellEl == null){
    KeywordUtil.markFailed("❌ Duration hücresi bulunamadı.")
    return
}
String durationText = durationCellEl.getText()?.trim()
KeywordUtil.logInfo("Duration hücresi değeri: " + durationText)
int expectedSeconds = parseSeconds(durationText)
KeywordUtil.logInfo("Beklenen saniye (tamsayı): " + expectedSeconds)

// 2) Play hücresi (6. hücre) içinde play'i tıkla
WebElement playCell = driver.findElement(By.xpath("(//td[contains(@class,'ant-table-cell')])[6]"))
boolean clicked = safeClickPlay(playCell)
if (!clicked){
    // Son bir ihtimal: sayfadaki "play" label’lı tıklanabilirleri dene
    try {
        List<WebElement> fallbacks = driver.findElements(By.cssSelector("button, [role='button'], [tabindex]"))
        for (WebElement fb : fallbacks){
            String aria = ""
            try { aria = fb.getAttribute("aria-label") ?: "" } catch(Exception ignore){}
            String txt  = ""
            try { txt = fb.getText() ?: "" } catch(Exception ignore){}
            if (aria.toLowerCase().contains("play") || txt.toLowerCase().contains("play")){
                if (safeClickPlay(fb)){ clicked = true; break }
            }
        }
    } catch(Exception ignore){}
}
if (!clicked){
    KeywordUtil.markFailed("❌ Play tıklanamadı.")
    return
}
KeywordUtil.logInfo("▶️ Play tuşuna basıldı.")
WebUI.delay(1.0)

// 3) Player (video/canvas) top veya iframe içinde geliyor mu?
if (!waitForPlayerAnyContext(12)){
    KeywordUtil.markFailed("❌ Player bulunamadı (video/canvas top/iframe hiçbirinde).")
    return
}
KeywordUtil.logInfo("✅ Player bulundu.")

// 4) Video playable/duration hazır mı?
if (!waitUntilVideoReadyAny(10)){
    // Bir şans daha: videoyu resume etmeyi dene (top + iframe’lerde ayrı ayrı deneyemiyoruz; top deneyelim)
    try {
        WebUI.executeJavaScript("var v=document.querySelector('video'); if(v){ try{ v.play(); }catch(e){} }", null)
    } catch(Exception ignore){}
    if (!waitUntilVideoReadyAny(8)){
        KeywordUtil.markFailed("❌ Video hazır hale gelmedi (duration okunamadı).")
        return
    }
}

// 5) Duration oku ve karşılaştır
double dur = readVideoDurationAny()
if (dur <= 0){
    KeywordUtil.markFailed("❌ Video duration okunamadı (<= 0).")
    return
}
int actualDuration = Math.round((float)dur)
KeywordUtil.logInfo("🎯 Videonun duration (yuvarlanmış): ${actualDuration}")
KeywordUtil.logInfo("📦 Duration hücresinden beklenen: ${expectedSeconds}")

int tolerance = 1 // saniye toleransı
if (Math.abs(actualDuration - expectedSeconds) <= tolerance) {
    KeywordUtil.logInfo("✅ Test Başarılı: Beklenen=${expectedSeconds}, Gerçek=${actualDuration}")
} else {
    KeywordUtil.markFailed("❌ Test Başarısız: Beklenen=${expectedSeconds}, Gerçek=${actualDuration}")
}
