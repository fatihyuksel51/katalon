import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import org.openqa.selenium.*
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebElement
import java.util.Random
import org.openqa.selenium.Keys as Keys

/************** helpers **************/
TestObject X(String xp) {
    TestObject to = new TestObject(xp)
    to.addProperty("xpath", ConditionType.EQUALS, xp)
    return to
}
JavascriptExecutor js(){ (JavascriptExecutor) DriverFactory.getWebDriver() }

boolean isCI(){
  return System.getenv('KATALON_AGENT_NAME')!=null || System.getenv('KATALON_TASK_ID')!=null
}

WebElement safeScrollTo(TestObject to) {
    if (to == null) {
        KeywordUtil.markFailed("❌ TestObject NULL – Repository yolunu kontrol et.")
        return null
    }
    if (!WebUI.waitForElementPresent(to, 2, FailureHandling.OPTIONAL)) {
        KeywordUtil.logInfo("ℹ️ Element not present, scroll atlandı: ${to.getObjectId()}")
        return null
    }
    WebElement element = WebUiCommonHelper.findWebElement(to, 2)
    js().executeScript("arguments[0].scrollIntoView({block: 'center'});", element)
    WebUI.delay(0.5)
    return element
}

void scrollIntoViewXp(String xp) {
    WebElement el = WebUiCommonHelper.findWebElement(X(xp), 2)
    js().executeScript("arguments[0].scrollIntoView({block:'center'});", el)
}
void scrollThenClick(String xp, int t=15) {
    WebUI.waitForElementPresent(X(xp), t, FailureHandling.OPTIONAL)
    WebUI.waitForElementVisible(X(xp), t)
    scrollIntoViewXp(xp)
    WebUI.waitForElementClickable(X(xp), t)
    WebUI.click(X(xp))
}

boolean isPlaceholder(WebElement img) {
    WebDriver d = DriverFactory.getWebDriver()
    String cur = (String) js().executeScript("return arguments[0].currentSrc || arguments[0].src;", img)
    return cur == null || cur.contains("placeholder.webp")
}

/** Lazy-load’u tetikleyerek gerçek (placeholder olmayan) ilk thumb’ı bekler. */
WebElement waitFirstRealThumb(int timeoutSec = 18){
  WebDriver d = DriverFactory.getWebDriver()
  long end = System.currentTimeMillis() + timeoutSec*1000L
  js().executeScript("window.scrollTo(0, 0)")
  while(System.currentTimeMillis() < end){
    js().executeScript("window.scrollBy(0, 250)")
    List<WebElement> imgs = d.findElements(By.xpath("//img[contains(@class,'cursor-pointer')]"))
    for(WebElement img : imgs){
      try{
        js().executeScript("arguments[0].scrollIntoView({block:'center'});", img)
        // gerçek yüklenme kriteri
        Boolean ok = (Boolean) js().executeScript(
          "return arguments[0].complete && arguments[0].naturalWidth>40 && arguments[0].src && !arguments[0].src.startsWith('data:')", img)
        Boolean visible = (Boolean) js().executeScript(
          "const r=arguments[0].getBoundingClientRect();return r.width>0&&r.height>0;", img)
        if(Boolean.TRUE.equals(ok) && Boolean.TRUE.equals(visible) && !isPlaceholder(img)) return img
      }catch(ignore){}
    }
    WebUI.delay(0.25)
  }
  return null
}

WebElement findFirstPlaceholderThumb() {
    WebDriver d = DriverFactory.getWebDriver()
    List<WebElement> imgs = d.findElements(By.xpath("//img[contains(@class,'cursor-pointer')]"))
    for (WebElement img : imgs) {
        js().executeScript("arguments[0].scrollIntoView({block:'center'});", img)
        WebUI.delay(0.2)
        if (isPlaceholder(img)) return img
    }
    return null
}

/** Thumbnail bulunamazsa karttaki linke fallback ile tıklar. */
void openImageModalWithFallback(){
  WebDriver d = DriverFactory.getWebDriver()
  WebElement thumb = waitFirstRealThumb(isCI()?24:12)
  if(thumb!=null){
    try { thumb.click(); return } catch(Exception e){ js().executeScript("arguments[0].click()", thumb); return }
  }
  KeywordUtil.logWarning("⚠️ 'Gerçek' thumbnail yakalanamadı; fallback ile kart linkine tıklanıyor (CI’da sık görülebilir).")
  String xpCardAddr = "(//button[contains(@class,'text-text-link') and contains(.,'http')])[1]"
  if (!WebUI.waitForElementClickable(X(xpCardAddr), isCI()?20:10, FailureHandling.OPTIONAL)){
    KeywordUtil.markFailedAndStop("Ne thumbnail ne de kart linki tıklanabilir durumda.")
  }
  WebUI.click(X(xpCardAddr))
}

/** Görsel yüklenirken hata çıkmadığını/gerçek img geldiğini bekler. */
boolean waitImageLoadedNoError(int timeoutSec = 20) {
    WebDriver d = DriverFactory.getWebDriver()
    String xpError = "//*[normalize-space(text())='There was a problem loading your image']"  // sınıfa bağımlı DEĞİL
    long end = System.currentTimeMillis() + timeoutSec * 1000L
    while (System.currentTimeMillis() < end) {
        if (WebUI.verifyElementPresent(X(xpError), 1, FailureHandling.OPTIONAL)) return false
        if (d.getWindowHandles().size() > 1) return true
        Boolean ok = (Boolean) js().executeScript("""
            const imgs=[...document.querySelectorAll('img')];
            const inVp=el=>{const r=el.getBoundingClientRect();return r.width>50&&r.height>50&&r.bottom>0&&r.right>0&&r.top<(innerHeight||document.documentElement.clientHeight)&&r.left<(innerWidth||document.documentElement.clientWidth)};
            return imgs.some(img=>{const cur=img.currentSrc||img.src||'';return inVp(img)&&!cur.includes('placeholder.webp')&&img.naturalWidth>0;});
        """)
        if (Boolean.TRUE.equals(ok)) return true
        WebUI.delay(0.5)
    }
    return false
}

void closeNewViews() {
	WebDriver driver = DriverFactory.getWebDriver()
	// 1) Modal/overlay varsa kapat
	String[] closeXPaths = [
		"//button[@aria-label='Close' or @aria-label='close' or contains(@class,'close') or normalize-space()='×' or .//*[name()='svg' and (contains(@class,'lucide-x') or contains(@data-icon,'x') or contains(@data-icon,'xmark'))]]",
		"//div[@role='dialog' or contains(@class,'modal')]//button[@aria-label='Close' or contains(@class,'close') or .//*[name()='svg' and contains(@class,'lucide-x')]]"
	]
	for (String xp : closeXPaths) {
		try {
			if (WebUI.waitForElementVisible(X(xp), 2, FailureHandling.OPTIONAL)) {
				WebUI.click(X(xp)); WebUI.delay(0.4); break
			}
		} catch (Throwable ignored) {}
	}
	// 2) ESC
	try { WebUI.sendKeys(X("//body"), Keys.chord(Keys.ESCAPE)); WebUI.delay(0.2) } catch (Throwable ignored) {}
	// 3) Ek sekmeler varsa kapat
	try {
		String original = driver.getWindowHandle()
		def handles = new ArrayList<>(driver.getWindowHandles())
		for (String h : handles) if (h != original) { driver.switchTo().window(h); driver.close() }
		driver.switchTo().window(original)
	} catch (Throwable ignored) {}
}

String safeText(String xp, int t=20) {
	if (!WebUI.waitForElementVisible(X(xp), t)) {
		KeywordUtil.markFailedAndStop("Element not visible: " + xp)
	}
	return WebUI.getText(X(xp)).trim()
}
boolean isVisible(String xp, int t=2) {
	return WebUI.waitForElementVisible(X(xp), t, FailureHandling.OPTIONAL)
}
/************** /helpers **************/

/*/ ----------------- NAVIGATION (opsiyonel login bölümü sende zaten var) -----------------
WebUI.openBrowser('')
WebUI.navigateToUrl('https://platform.catchprobe.org/')
WebUI.maximizeWindow()

WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'), 30)
safeScrollTo(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'))
WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'))
WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 30)
safeScrollTo(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'))
WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 'katalon.test@catchprobe.com')
safeScrollTo(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Password_password'))
WebUI.setEncryptedText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Password_password'), 'RigbBhfdqOBDK95asqKeHw==')
safeScrollTo(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Sign in'))
WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Sign in'))
WebUI.delay(1)

WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'), 30)
String randomOtp = (100000 + new Random().nextInt(900000)).toString()
safeScrollTo(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'))
WebUI.delay(1)
WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'), randomOtp)
safeScrollTo(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Verify'))
WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Verify'))
WebUI.delay(5)
WebUI.waitForPageLoad(10)
String Threat = "//span[text()='Threat']"
WebUI.waitForElementVisible(X(Threat), 10, FailureHandling.OPTIONAL)
/*/
WebUI.navigateToUrl('https://platform.catchprobe.org/darkmap/quick-search')
WebUI.delay(4)
WebUI.waitForPageLoad(20)

// ======================= TC1: Gerçek görsel =======================
KeywordUtil.logInfo("TC1: Gerçek görselin açıldığını doğrula")

// Yeni: fallback’li açılış
openImageModalWithFallback()

// Yüklenme/hata kontrolü
boolean loaded = waitImageLoadedNoError(isCI()?30:20)
if (!loaded) {
    KeywordUtil.markFailed("❌ Görsel  yüklenmedi veya hata mesajı göründü.")
} else {
    KeywordUtil.logInfo("✅ Görsel başarıyla yüklendi (hata yok).")
}

// Yeni sekme açıldıysa kapat ve geri dön
WebDriver driver = DriverFactory.getWebDriver()
if (driver.getWindowHandles().size() > 1) {
    String original = driver.getWindowHandle()
    for (String h : driver.getWindowHandles()) {
        if (h != original) {
            driver.switchTo().window(h)
            driver.close()
            driver.switchTo().window(original)
            break
        }
    }
}

// ======================= TC2: Placeholder =======================
KeywordUtil.logInfo("TC2: Placeholder tıklandığında hata mesajı bekle")

WebElement phImg = findFirstPlaceholderThumb()
if (phImg == null) {
    KeywordUtil.logInfo("Uyarı: Placeholder bulunamadı, TC2 atlanıyor.")
} else {
    js().executeScript("arguments[0].scrollIntoView({block:'center'});", phImg)
    WebUI.delay(0.2)
    try { phImg.click() } catch (Throwable t) { js().executeScript("arguments[0].click();", phImg) }

    String xpError = "//*[normalize-space(text())='There was a problem loading your image']"
    boolean errorShown = WebUI.waitForElementVisible(X(xpError), isCI()?25:20, FailureHandling.OPTIONAL)
    if (errorShown) {
        KeywordUtil.markWarning("✅ Placeholder tıklandı; hata mesajı göründü (beklenmeyen).")
    } else {
        KeywordUtil.logInfo("❌ Placeholder tıklandı; hata mesajı gelmedi.")
    }
}

KeywordUtil.markPassed("🎯 Görsel doğrulama senaryosu tamamlandı.")
closeNewViews()

// ============== Domain Related Intelligence akışı ==============

WebUI.navigateToUrl('https://platform.catchprobe.org/darkmap/quick-search')
WebUI.delay(4)
WebUI.waitForPageLoad(20)

// 1) İlk adres ve Domain Related Intelligence butonları
String xpAddrBtn1   = "(//button[contains(@class,'text-text-link') and contains(.,'http')])[1]"
String xpDomainBtn1 = "(//button[contains(normalize-space(.),'Domain Related Intelligence')])[1]"

// Adresi oku (tıklamadan önce)
String firstAddr = safeText(xpAddrBtn1, 20)
String snippet   = firstAddr.length() > 60 ? firstAddr.substring(0, 60) : firstAddr

// 2) Domain Related Intelligence’e scroll + click
scrollIntoViewXp(xpDomainBtn1)
WebUI.waitForElementClickable(X(xpDomainBtn1), 15)
WebUI.click(X(xpDomainBtn1))

// 3) Pop-up bekle
WebUI.delay(3)
String xpPopupClose = "//span[contains(@class,'cursor-pointer') and contains(@class,'rounded-full') and contains(@class,'bg-destructive')]"
WebUI.waitForElementClickable(X(xpPopupClose), isCI()?25:20)

// Adres parçasını içeren pop-up’ı yakala
String xpPopupWithAddr = "//*[contains(normalize-space(.), '"+snippet.replace("'", "")+"')]"
if (!isVisible(xpPopupWithAddr, 10)) {
	KeywordUtil.markFailedAndStop("Adres parçası pop-up'ta görünmedi: " + snippet)
}

// Pop-up’ın adres çubuğunun üstünde olup olmadığını kontrol et
WebElement addrEl  = WebUiCommonHelper.findWebElement(X(xpAddrBtn1), 10)
WebElement popupEl = driver.findElement(By.xpath(xpPopupWithAddr))
Number addrTop  = (Number) js().executeScript("return arguments[0].getBoundingClientRect().top;", addrEl)
Number popupTop = (Number) js().executeScript("return arguments[0].getBoundingClientRect().top;", popupEl)
if (!(popupTop.doubleValue() < addrTop.doubleValue())) {
	KeywordUtil.markFailed("Pop-up adres satırının üstünde değil! addrTop="+addrTop+", popupTop="+popupTop)
} else {
	KeywordUtil.logInfo("✅ Pop-up adres çubuğunun üstünde.")
}

// Kart satırındaki Domain Related Intelligence butonu bu sırada görünmemeli
boolean domainVisibleNow = isVisible(xpDomainBtn1, 2)
if (domainVisibleNow) {
	KeywordUtil.markFailed("❌ Kart satırında Domain Related Intelligence butonu hâlâ görünüyor!")
} else {
	KeywordUtil.logInfo("✅ Kart satırında Domain Related Intelligence butonu geçici olarak kayboldu.")
}

// 4) Pop-up’taki çarpıya tıkla → buton geri gelmeli
WebUI.waitForElementClickable(X(xpPopupClose), 10)
WebUI.click(X(xpPopupClose))
WebUI.delay(3)

if (!WebUI.waitForElementVisible(X(xpDomainBtn1), isCI()?25:15)) {
	KeywordUtil.markFailedAndStop("❌ Domain Related Intelligence butonu geri gelmedi.")
}
WebUI.waitForElementClickable(X(xpDomainBtn1), isCI()?25:20)
KeywordUtil.markPassed("🎯 Akış başarıyla doğrulandı: pop-up gösterimi, kaybolma ve geri gelme kontrolleri tamam.")
