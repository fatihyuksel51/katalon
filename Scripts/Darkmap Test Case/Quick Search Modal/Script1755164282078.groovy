import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
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
    JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getWebDriver()
    js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element)
    WebUI.delay(0.5)
    return element
}

void scrollIntoViewXp(String xp) {
    WebElement el = WebUiCommonHelper.findWebElement(X(xp), 2)
    ((JavascriptExecutor) DriverFactory.getWebDriver())
        .executeScript("arguments[0].scrollIntoView({block:'center'});", el)
}

void scrollThenClick(String xp, int t=15) {
    WebUI.waitForElementPresent(X(xp), t, FailureHandling.OPTIONAL)
    WebUI.waitForElementVisible(X(xp), t)
    scrollIntoViewXp(xp)
    WebUI.waitForElementClickable(X(xp), t)
    WebUI.click(X(xp))
}

boolean isPlaceholder(WebElement img) {
    def driver = DriverFactory.getWebDriver()
    String cur = (String) ((JavascriptExecutor)driver)
            .executeScript("return arguments[0].currentSrc || arguments[0].src;", img)
    return cur == null || cur.contains("placeholder.webp")
}

WebElement findFirstRealThumb() {
    def driver = DriverFactory.getWebDriver()
    List<WebElement> imgs = driver.findElements(By.xpath("//img[@data-nimg='1' and contains(@class,'cursor-pointer')]"))
    for (WebElement img : imgs) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", img)
        WebUI.delay(2.2) // lazy-load için
        if (!isPlaceholder(img)) return img
    }
    return null
}
void closeNewViews() {
	def driver = DriverFactory.getWebDriver()

	// 1) Modal/overlay varsa kapat (çeşitli X/Close butonları)
	String[] closeXPaths = [
		"//button[@aria-label='Close' or @aria-label='close' or contains(@class,'close') or normalize-space()='×' or .//*[name()='svg' and (contains(@class,'lucide-x') or contains(@data-icon,'x') or contains(@data-icon,'xmark'))]]",
		"//div[@role='dialog' or contains(@class,'modal')]//button[@aria-label='Close' or contains(@class,'close') or .//*[name()='svg' and contains(@class,'lucide-x')]]"
	]
	for (String xp : closeXPaths) {
		try {
			if (WebUI.waitForElementVisible(X(xp), 2, FailureHandling.OPTIONAL)) {
				WebUI.click(X(xp))
				WebUI.delay(0.4)
				break
			}
		} catch (Throwable ignored) {}
	}

	// 2) Olmadı, ESC gönder (bazı overlay’ler ESC ile kapanır)
	try {
		TestObject body = new TestObject('body').addProperty('xpath', ConditionType.EQUALS, '//body')
		WebUI.sendKeys(body, Keys.chord(Keys.ESCAPE))
		WebUI.delay(0.2)
	} catch (Throwable ignored) {}

	// 3) Ek sekmeler açıksa hepsini kapat, orijinale dön
	try {
		String original = driver.getWindowHandle()
		def handles = new ArrayList<>(driver.getWindowHandles())
		for (String h : handles) {
			if (h != original) {
				driver.switchTo().window(h)
				driver.close()
			}
		}
		driver.switchTo().window(original)
	} catch (Throwable ignored) {}
}


WebElement findFirstPlaceholderThumb() {
    def driver = DriverFactory.getWebDriver()
    List<WebElement> imgs = driver.findElements(By.xpath("//img[@data-nimg='1' and contains(@class,'cursor-pointer')]"))
    for (WebElement img : imgs) {
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView({block:'center'});", img)
        WebUI.delay(0.2)
        if (isPlaceholder(img)) return img
    }
    return null
}

boolean waitImageLoadedNoError(int timeoutSec = 20) {
    def driver = DriverFactory.getWebDriver()
    String xpError = "//p[@class='sc-gswNZR drebyQ' and normalize-space(text())='There was a problem loading your image']"

    long end = System.currentTimeMillis() + timeoutSec * 1000L
    while (System.currentTimeMillis() < end) {
        // 1) Hata mesajı göründüyse başarısız
        if (WebUI.verifyElementPresent(X(xpError), 1, FailureHandling.OPTIONAL)) {
            return false
        }

        // 2) Başarı kriterleri:
        // 2a) Yeni sekme açıldıysa (bazı görseller yeni sekmede açılabiliyor)
        if (driver.getWindowHandles().size() > 1) {
            return true
        }

        // 2b) Viewport’ta placeholder olmayan ve doğal genişliği olan bir img görünüyor mu?
        Boolean ok = (Boolean) ((JavascriptExecutor)driver).executeScript("""
            const imgs = [...document.querySelectorAll('img')];
            const inVp = el => {
              const r = el.getBoundingClientRect();
              return r.width>50 && r.height>50 && r.bottom>0 && r.right>0 &&
                     r.top < (window.innerHeight||document.documentElement.clientHeight) &&
                     r.left < (window.innerWidth||document.documentElement.clientWidth);
            };
            return imgs.some(img => {
              const cur = img.currentSrc || img.src || '';
              return inVp(img) && !cur.includes('placeholder.webp') && img.naturalWidth > 0;
            });
        """)
        if (ok != null && ok) return true

        WebUI.delay(0.5)
    }
    return false
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

// ----------------- NAVIGATION -----------------
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

WebUI.delay(5)
String randomOtp = (100000 + new Random().nextInt(900000)).toString()
safeScrollTo(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'))
WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'), randomOtp)
safeScrollTo(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Verify'))
WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Verify'))
WebUI.delay(5)
WebUI.waitForPageLoad(10)

// Opsiyonel uyarı kontrolünüz
CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()

// Organizasyon seçimi
TestObject currentOrg = new TestObject()
currentOrg.addProperty("xpath", ConditionType.EQUALS, "//div[contains(@class,'font-semibold') and contains(text(),'Organization')]//span[@class='font-thin']")
String currentOrgText = WebUI.getText(currentOrg)
if (currentOrgText != 'Mail Test') {
    TestObject orgButton = new TestObject()
    orgButton.addProperty("xpath", ConditionType.EQUALS, "//button[.//div[contains(text(),'Organization :')]]")
    WebUI.click(orgButton)

    TestObject testCompanyOption = new TestObject()
    testCompanyOption.addProperty("xpath", ConditionType.EQUALS, "//button[.//div[text()='Mail Test']]")
    WebUI.click(testCompanyOption)
}
WebUI.delay(3)
WebUI.waitForPageLoad(20)

WebUI.navigateToUrl('https://platform.catchprobe.org/darkmap/quick-search')
WebUI.delay(4)
WebUI.waitForPageLoad(20)

// ======================= TC1: Gerçek görsel =======================
KeywordUtil.logInfo("TC1: Gerçek görselin açıldığını doğrula")

def driver = DriverFactory.getWebDriver()
WebElement realImg = findFirstRealThumb()
if (realImg == null) {
    KeywordUtil.markFailedAndStop("Gerçek görsel (placeholder olmayan) bulunamadı!")
}

// Tıklamadan önce görünür alana getir
((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView({block:'center'});", realImg)
WebUI.delay(0.2)

// Tıkla (gerekirse JS click fallback)
try {
    realImg.click()
} catch (Throwable t) {
    ((JavascriptExecutor)driver).executeScript("arguments[0].click();", realImg)
}

// Yüklenme/hata kontrolü – 20 sn
boolean loaded = waitImageLoadedNoError(20)
if (!loaded) {
    KeywordUtil.markFailed("❌ Gerçek görsel tıklandı fakat 20 sn içinde yüklenmedi veya hata mesajı göründü.")
} else {
    KeywordUtil.logInfo("✅ Gerçek görsel başarıyla yüklendi (hata mesajı yok).")
}

// Yeni sekme açıldıysa kapat ve geri dön
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
    ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView({block:'center'});", phImg)
    WebUI.delay(0.2)
    try {
        phImg.click()
    } catch (Throwable t) {
        ((JavascriptExecutor)driver).executeScript("arguments[0].click();", phImg)
    }

    String xpError = "//p[@class='sc-gswNZR drebyQ' and normalize-space(text())='There was a problem loading your image']"
    boolean errorShown = WebUI.waitForElementVisible(X(xpError), 20, FailureHandling.OPTIONAL)
    if (errorShown) {
        KeywordUtil.logInfo("✅ Placeholder tıklandı; hata mesajı göründü (beklenen).")
    } else {
        KeywordUtil.markFailed("❌ Placeholder tıklandı; 20 sn içinde beklenen hata mesajı gelmedi.")
    }
}

KeywordUtil.markPassed("🎯 Görsel doğrulama senaryosu tamamlandı.")
closeNewViews()
WebUI.navigateToUrl('https://platform.catchprobe.org/darkmap/quick-search')
WebUI.delay(4)
WebUI.waitForPageLoad(20)
/*** Bu test “quick-search” sayfasındayken çalışacak şekilde tasarlandı. ***/
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

// 3) 20 sn bekle → pop-up’ta adres parçası görünsün, kartta buton kaybolsun
WebUI.delay(3)
String xpPopupClose = "//span[contains(@class,'cursor-pointer') and contains(@class,'rounded-full') and contains(@class,'bg-destructive')]"
WebUI.waitForElementClickable(X(xpPopupClose), 20)

// Adres parçasını içeren pop-up’ı yakala (genel metin araması; en yakın eşleşme)
String xpPopupWithAddr = "//*[contains(normalize-space(.), '"+snippet.replace("'", "")+"')]"
if (!isVisible(xpPopupWithAddr, 10)) {
	KeywordUtil.markFailedAndStop("Adres parçası pop-up'ta görünmedi: " + snippet)
}

// Pop-up’ın gerçekten “adres çubuğunun üstünde” olduğunu kontrol et

WebElement addrEl  = WebUiCommonHelper.findWebElement(X(xpAddrBtn1), 10)
WebElement popupEl = driver.findElement(By.xpath(xpPopupWithAddr))
Number addrTop  = (Number)((JavascriptExecutor)driver).executeScript("return arguments[0].getBoundingClientRect().top;", addrEl)
Number popupTop = (Number)((JavascriptExecutor)driver).executeScript("return arguments[0].getBoundingClientRect().top;", popupEl)
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

// 4) Pop-up’taki çarpıya tıkla → 20 sn sonra buton geri gelmeli

WebUI.waitForElementClickable(X(xpPopupClose), 10)
WebUI.click(X(xpPopupClose))

WebUI.delay(3)

// Domain Related Intelligence butonu tekrar görünmeli ve tıklanabilir olmalı
if (!WebUI.waitForElementVisible(X(xpDomainBtn1), 15)) {
	KeywordUtil.markFailedAndStop("❌ Domain Related Intelligence butonu geri gelmedi.")
}
WebUI.waitForElementClickable(X(xpDomainBtn1), 20)
KeywordUtil.markPassed("🎯 Akış başarıyla doğrulandı: pop-up gösterimi, kaybolma ve geri gelme kontrolleri tamam.")


