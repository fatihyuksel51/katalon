import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.Keys
import groovy.transform.Field


/**********************************************
 * Brand Protection – Dashboard & Lists E2E
 **********************************************/

/************** CONFIG **************/
@Field final String BASE_URL        = 'https://platform.catchprobe.org'
@Field final String DASHBOARD_URL   = BASE_URL + '/darkmap/brand-protection'
@Field final String IGNORE_LIST_URL = BASE_URL + '/darkmap/brand-protection/ignore-list'

@Field String CURRENT_TAB_XPATH = null

/************** HELPERS **************/
TestObject X(String xp) {
    TestObject to = new TestObject(xp)
    to.addProperty('xpath', ConditionType.EQUALS, xp)
    return to
}

void setCurrentTab(String xp) { CURRENT_TAB_XPATH = xp }

void scrollIntoView(String xp) {
    def el = WebUiCommonHelper.findWebElement(X(xp), 10)
    ((JavascriptExecutor)DriverFactory.getWebDriver()).executeScript(
        "arguments[0].scrollIntoView({block:'center'});", el)
}

String OVERLAY_XPATH() {
    // Radix/Headless UI overlay’lerini yakalar
    return "//div[@data-state='open' and contains(@class,'fixed') and contains(@class,'inset-0')]"
}

boolean overlayPresent(int timeout = 1) {
    return WebUI.waitForElementPresent(X(OVERLAY_XPATH()), timeout, FailureHandling.OPTIONAL)
}

void waitOverlayGone(int timeout = 10) {
    WebUI.waitForElementNotPresent(X(OVERLAY_XPATH()), timeout, FailureHandling.OPTIONAL)
}

void dismissOverlayIfAny(int timeout = 5) {
    if (overlayPresent(1)) {
        try {
            WebDriver d = DriverFactory.getWebDriver()
            new Actions(d).sendKeys(Keys.ESCAPE).perform() // çoğu dialog ESC ile kapanır
        } catch (ignored) {}
        WebUI.delay(0.3)
        waitOverlayGone(timeout)
    }
}

WebElement $el(TestObject to, int timeout = 15) {
    WebUI.waitForElementPresent(to, timeout)
    WebUI.waitForElementVisible(to, timeout)
    return WebUI.findWebElement(to, timeout)
}

void jsScrollIntoView(WebElement el) {
    WebDriver driver = DriverFactory.getWebDriver()
    ((JavascriptExecutor) driver).executeScript(
        'arguments[0].scrollIntoView({block: "center"});', el)
}

void log(String msg) { KeywordUtil.logInfo("[BP] " + msg) }

WebElement safeScrollTo(TestObject to, int timeout = 15) {
    WebElement el = $el(to, timeout)
    jsScrollIntoView(el)
    WebUI.delay(0.2)
    return el
}

int intText(TestObject to, int timeout = 10) {
    WebElement el = $el(to, timeout)
    String raw = el.getText()?.trim() ?: '0'
    raw = raw.replaceAll('[^0-9]', '')
    if (raw.isEmpty()) return 0
    return Integer.parseInt(raw)
}

boolean exists(TestObject to, int timeout = 3) {
    return WebUI.waitForElementPresent(to, timeout, FailureHandling.OPTIONAL)
}

void clickTO(TestObject to, int timeout = 15) {
    safeScrollTo(to, timeout)
    WebUI.waitForElementClickable(to, timeout)
    WebUI.click(to)
}

/** Hafif yeniden çizim: aktif tab’a dokun veya aktif tab’ı otomatik bul. */
void reloadListSoft() {
    // Önce gerçekten aktif tab var mı, onu yokla
    String ACTIVE_TAB_AUTO = "//div[@role='tab' and (@data-state='active' or contains(@class,'data-[state=active]') or contains(@class,'aria-selected'))]"
    try {
        if (exists(X(ACTIVE_TAB_AUTO), 2)) {
            clickTO(X(ACTIVE_TAB_AUTO))
        } else if (CURRENT_TAB_XPATH != null && exists(X(CURRENT_TAB_XPATH), 2)) {
            clickTO(X(CURRENT_TAB_XPATH))
        }
    } catch (ignored) {}
    waitOverlayGone(10)
    WebUI.delay(0.6)
}

/** Ağır çözüm: tam sayfa refresh */
void reloadListHard() {
    WebUI.refresh()
    WebUI.waitForPageLoad(20)
    waitOverlayGone(10)
}

void openAndVerifyExternal(String linkXpath, String hostMustContain = '') {
    WebDriver driver = DriverFactory.getWebDriver()
    String original = driver.getWindowHandle()

    clickTO(X(linkXpath))
    WebUI.delay(1)

    // switch new tab
    Set<String> handles = driver.getWindowHandles()
    for (String h : handles) {
        if (h != original) { driver.switchTo().window(h); break }
    }

    if (hostMustContain) {
        String url = driver.getCurrentUrl()
        KeywordUtil.logInfo('Opened URL: ' + url)
        assert url.contains(hostMustContain) : 'External URL does not contain expected host: ' + hostMustContain
    }

    driver.close()
    driver.switchTo().window(original)
}

int ceilDiv(int total, int perPage) {
    if (total <= 0) return 0
    return (int) Math.ceil(total / (double) perPage)
}

void assertPaginationBasic(int total, int perPage) {
    int expectedPages = ceilDiv(total, perPage)
    if (expectedPages <= 1) {
        KeywordUtil.logInfo("Pagination not required (expectedPages=" + expectedPages + ")")
        return
    }
    TestObject page2 = X("//a[normalize-space(.)='2' or @aria-label='Page 2']")
    assert exists(page2, 3) : 'Expected pagination to show a page 2 button'
    clickTO(page2)
    WebUI.delay(2.5)
}

/************** XPATH BUILDERS **************/
String firstRowContainerXpath() {
    // underline’lı Page Name linki olan ilk “row/grup”
    return "//div[contains(@class,'group') and .//a[contains(@class,'underline')]]"
}
String ignorepagename() {
    return "//a[contains(@class, 'font-semibold') and contains(@class, 'underline')]"
}
String firstRowPageNameXpath() {
    return firstRowContainerXpath() + "//a[contains(@class,'underline') and contains(@class,'font-semibold')]"
}
String firstRowVerifiedCellXpath() {
    return firstRowContainerXpath() + "//span[contains(normalize-space(.),'Verified') or contains(normalize-space(.),'Not verified')]"
}
String firstRowVerifiedCelllnXpath() {
    return "(//div[contains(@class,'group') and .//a[contains(@class,'underline')]]//span[contains(normalize-space(.),'Verified') or contains(normalize-space(.),'Not verified')])[2]"
}
String firstRowCheckIcon() {
    return firstRowContainerXpath() + "//*[@class='lucide lucide-check h-4 w-4']"
}
String firstRowXIcon() {
    return firstRowContainerXpath() + "//*[@class='lucide lucide-x h-4 w-4']"
}
String updateButtonXpath() {
    return "//button[normalize-space(.)='UPDATE' or normalize-space(.)='Update']"
}
String ignoreButtonXpath() {
    return "//button[normalize-space(.)='IGNORE' or normalize-space(.)='Ignore']"
}

/************** FLOWS **************/
void toggleVerifyFlow() {
    String row1    = "//div[contains(@class,'group') and .//a[contains(@class,'underline')]]"
    String nameXp  = row1 + "//a[contains(@class,'underline') and contains(@class,'font-semibold')]"
    String verXp   = row1 + "//span[contains(.,'Verified') or contains(.,'Not Verified') or contains(.,'Not verified')]"
    String checkXp = row1 + "//*[@class='lucide lucide-check h-4 w-4']"
    String xXp     = row1 + "//*[@class='lucide lucide-x h-4 w-4']"
    String VrfyBtnXp = "//button[normalize-space(.)='Verify' or normalize-space(.)='Unverify']"

    String pageNameg = WebUI.getText(X(nameXp)).trim()
    String status0   = WebUI.getText(X(verXp)).trim()
    log("🟪 Advertisor List ROW name='" + pageNameg + "', status='" + status0 + "'")

    String pageName      = WebUI.getText(X(firstRowPageNameXpath())).trim()
    String verifyBefore  = WebUI.getText(X(firstRowVerifiedCelllnXpath())).trim()
    KeywordUtil.logInfo("Row pageName=" + pageName + ", before=" + verifyBefore)
    log("Row='" + pageName + "' | BEFORE='" + verifyBefore + "'")

    if (status0.toLowerCase().contains("verified") && exists(X(xXp), 3)) {
        clickTO(X(xXp))
        if (exists(X(VrfyBtnXp), 5)) clickTO(X(VrfyBtnXp))
        WebUI.delay(1.7)
        if (exists(X(updateButtonXpath()), 5)) {
            clickTO(X(updateButtonXpath()))
            WebUI.delay(0.7)
        }
        reloadListSoft()
        String afterClean = WebUI.getText(X(verXp)).trim()
        assert afterClean.toLowerCase().contains("not")
    }

    // CHECK -> Verified
    clickTO(X(checkXp))
    WebUI.delay(0.8)
    if (exists(X(updateButtonXpath()), 5)) {
        clickTO(X(updateButtonXpath()))
        WebUI.delay(0.7)
    }
    reloadListSoft()

    String verifyAfterCheck = WebUI.getText(X(firstRowVerifiedCelllnXpath())).trim()
    log("AFTER CHECK=" + verifyAfterCheck )
    assert verifyAfterCheck.toLowerCase().contains('verified') && !verifyAfterCheck.toLowerCase().contains('not') :
        'Expected Verified after check, got: ' + verifyAfterCheck

    // X -> Not verified
    WebUI.delay(0.8)
    clickTO(X(firstRowXIcon()))
    WebUI.delay(0.8)
    if (exists(X(updateButtonXpath()), 5)) {
        clickTO(X(updateButtonXpath()))
        WebUI.delay(0.9)
    }
    reloadListSoft()

    String verifyAfterX = WebUI.getText(X(firstRowVerifiedCelllnXpath())).trim()
    log("AFTER CHECK X=" + verifyAfterCheck )
    assert verifyAfterX.toLowerCase().contains('not') : 'Expected Not verified after X, got: ' + verifyAfterX
}

void toggleVerifyFlowln() {
    String row1    = "//div[contains(@class,'group') and .//a[contains(@class,'underline')]]"
    String nameXp  = row1 + "//a[contains(@class,'underline') and contains(@class,'font-semibold')]"
    String verXp   = row1 + "//span[contains(.,'Verified') or contains(.,'Not Verified') or contains(.,'Not verified')]"
    String checkXp = row1 + "//*[@class='lucide lucide-check h-4 w-4']"
    String xXp     = row1 + "//*[@class='lucide lucide-x h-4 w-4']"
    String VrfyBtnXp = "//button[normalize-space(.)='Verify' or normalize-space(.)='Unverify']"

    String pageNameg = WebUI.getText(X(nameXp)).trim()
    String status0   = WebUI.getText(X(verXp)).trim()

    String pageName      = WebUI.getText(X(firstRowPageNameXpath())).trim()
    String verifyBefore  = WebUI.getText(X(firstRowVerifiedCelllnXpath())).trim()
    KeywordUtil.logInfo("Row pageName=" + pageName + ", before=" + verifyBefore)
    log("Row='" + pageName + "' | BEFORE='" + verifyBefore + "'")

    if (status0.toLowerCase().contains("verified") && exists(X(xXp), 3)) {
        clickTO(X(xXp))
        if (exists(X(VrfyBtnXp), 5)) clickTO(X(VrfyBtnXp))
        WebUI.delay(1.7)
        if (exists(X(updateButtonXpath()), 5)) {
            clickTO(X(updateButtonXpath()))
            WebUI.delay(0.7)
        }
        reloadListSoft()
        String afterClean = WebUI.getText(X(verXp)).trim()
        assert afterClean.toLowerCase().contains("not")
    }

    clickTO(X(checkXp))
    WebUI.delay(1.1)
    if (exists(X(updateButtonXpath()), 5)) {
        clickTO(X(updateButtonXpath()))
        WebUI.delay(1.0)
    }
    reloadListSoft()

    String verifyAfterCheck = WebUI.getText(X(firstRowVerifiedCelllnXpath())).trim()
    log("AFTER CHECK=" + verifyAfterCheck )
    assert verifyAfterCheck.toLowerCase().contains('verified') && !verifyAfterCheck.toLowerCase().contains('not') :
        'Expected Verified after check, got: ' + verifyAfterCheck

    WebUI.delay(0.8)
    clickTO(X(firstRowXIcon()))
    WebUI.delay(0.8)
    if (exists(X(updateButtonXpath()), 5)) {
        clickTO(X(updateButtonXpath()))
        WebUI.delay(0.4)
    }
    reloadListSoft()

    String verifyAfterX = WebUI.getText(X(firstRowVerifiedCelllnXpath())).trim()
    log("AFTER CHECK X=" + verifyAfterCheck )
    assert verifyAfterX.toLowerCase().contains('not') : 'Expected Not verified after X, got: ' + verifyAfterX
}

void imageOpenCloseFlow() {
    clickTO(X("(//img[contains(@class,'cursor-pointer')])[1]"))
    WebUI.waitForElementVisible(X("//img[@data-nimg='fill' and contains(@class,'object-contain')]"), 15)

    TestObject closeBtn = X("//button[.//span[normalize-space(text())='Close']]")
    if (WebUI.waitForElementPresent(closeBtn, 2, FailureHandling.OPTIONAL)) {
        clickTO(closeBtn)
    } else {
        WebDriver d = DriverFactory.getWebDriver()
        new org.openqa.selenium.interactions.Actions(d).sendKeys(org.openqa.selenium.Keys.ESCAPE).perform()
    }
    waitOverlayGone(10)
    WebUI.delay(1.5)
}

void ignoreFlowAndValidate(String networkTabXpath, String expectedNetworkLabel = 'Linkedin') {
    setCurrentTab(networkTabXpath)

    String pageName = WebUI.getText(X(firstRowPageNameXpath())).trim()

    clickTO(X("(//*[@class='lucide lucide-ban h-4 w-4'])[1]"))
    WebUI.delay(2.6)
    if (exists(X(ignoreButtonXpath()), 5)) {
        clickTO(X(ignoreButtonXpath()))
        WebUI.delay(2.4)
    }

    WebUI.navigateToUrl(IGNORE_LIST_URL)
    WebUI.waitForPageLoad(20)
    waitOverlayGone(10)

    WebUI.waitForElementClickable(X(networkTabXpath), 10)
    clickTO(X(networkTabXpath))

    String ignoreName = WebUI.getText(X(ignorepagename())).trim()
    KeywordUtil.logInfo("🟪 Karttaki adres: " + ignoreName)
    WebUI.verifyMatch(ignoreName, pageName, false)

    clickTO(X("(//*[@class='lucide lucide-ban h-4 w-4'])[1]"))
    WebUI.delay(2.6)
    if (exists(X(ignoreButtonXpath()), 5)) {
        clickTO(X(ignoreButtonXpath()))
        WebUI.delay(2.4)
    }
    WebUI.delay(2.4)

    String IGNORE_CLOSE_BY_ICON = "//button[contains(@class, 'absolute right-4 ') and contains(@class, 'opacity-70 ring')]"
    TestObject ignoreClose = X(IGNORE_CLOSE_BY_ICON)
    clickTO(ignoreClose)
    WebUI.delay(3.4)

    String xpNoData = "//div[contains(@class,'col-span-12 flex') and normalize-space(text())='No data found.']"
    scrollIntoView(xpNoData)
    WebUI.waitForElementVisible(X(xpNoData), 20)
    WebUI.verifyElementPresent(X(xpNoData), 10)
}

void advertisorList(String networkTabXpath, String expectedNetworkLabel = 'Linkedin') {
    setCurrentTab(networkTabXpath)

    String row1        = "//div[contains(@class,'group') and .//a[contains(@class,'underline')]]"
    String nameXp      = row1 + "//a[contains(@class,'underline') and contains(@class,'font-semibold')]"
    String verXp       = row1 + "//span[contains(.,'Verified') or contains(.,'Not Verified') or contains(.,'Not verified')]"
    String checkXp     = row1 + "//*[@class='lucide lucide-check h-4 w-4']"
    String xXp         = row1 + "//*[@class='lucide lucide-x h-4 w-4']"
    String VrfyBtnXp   = "//button[normalize-space(.)='Verify' or normalize-space(.)='Unverify']"
    String IGN_CLOSE   = "//button[contains(@class, 'absolute right-4 ') and contains(@class, 'opacity-70 ring')]"
    TestObject ignoreClose = X(IGN_CLOSE)

    WebUI.navigateToUrl(BASE_URL + "/darkmap/brand-protection/advertisor-list")
    WebUI.waitForPageLoad(20)

    WebUI.waitForElementClickable(X(networkTabXpath), 10)
    clickTO(X(networkTabXpath))

    String pageName = WebUI.getText(X(nameXp)).trim()
    String status0  = WebUI.getText(X(verXp)).trim()
    log("🟪 Advertisor List ROW name='" + pageName + "', status='" + status0 + "'")

    if (status0.toLowerCase().contains("verified") && exists(X(xXp), 3)) {
        clickTO(X(xXp))
        if (exists(X(VrfyBtnXp), 5)) clickTO(X(VrfyBtnXp))
        WebUI.delay(1.7)
        clickTO(ignoreClose)
        WebUI.delay(1.4)
        reloadListSoft()
        String afterClean = WebUI.getText(X(verXp)).trim()
        assert afterClean.toLowerCase().contains("not")
    }

    clickTO(X(checkXp))
    if (exists(X(VrfyBtnXp), 5)) clickTO(X(VrfyBtnXp))
    WebUI.delay(1.7)
    clickTO(ignoreClose)
    WebUI.delay(1.4)
    reloadListSoft()

    String status1 = WebUI.getText(X(verXp)).trim()
    log("[ADVL] AFTER CHECK = " + status1)
    assert status1.toLowerCase().contains("verified") && !status1.toLowerCase().contains("not")

    int baseWinIndex   = WebUI.getWindowIndex()
    def driver         = DriverFactory.getWebDriver()
    int beforeWinCount = driver.getWindowHandles().size()

    clickTO(X(nameXp))
    for (int i = 0; i < 40; i++) {
        WebUI.delay(0.25)
        if (DriverFactory.getWebDriver().getWindowHandles().size() > beforeWinCount) break
    }
    int afterCount = DriverFactory.getWebDriver().getWindowHandles().size()
    assert afterCount == beforeWinCount + 1 : "Yeni sekme açılamadı!"

    WebUI.switchToWindowIndex(afterCount - 1)
    WebUI.waitForPageLoad(20)
    waitOverlayGone(10)

    String dRow1   = "//div[contains(@class,'group') and .//a[contains(@class,'underline')]]"
    String dNameXp = dRow1 + "//a[contains(@class,'underline') and contains(@class,'font-semibold')]"
    String dVerXp  = "(//div[contains(@class,'group') and .//a[contains(@class,'underline')]]//span[contains(.,'Verified') or contains(.,'Not verified') or contains(.,'Not Verified')])[1]"
    String dXXp    = dRow1 + "//*[@class='lucide lucide-x h-4 w-4']"

    safeScrollTo(X(dNameXp), 10)
    WebUI.waitForElementVisible(X(dNameXp), 10)
    String dName = WebUI.getText(X(dNameXp)).trim()
    assert dName.equalsIgnoreCase(pageName) : "Dashboard ilk satır adı beklenen değil. Beklenen: ${pageName}, Bulunan: ${dName}"

    WebUI.waitForElementVisible(X(dVerXp), 10)
    String dStatus = WebUI.getText(X(dVerXp)).trim().toLowerCase()
    assert dStatus.contains("verified") : "Dashboard status 'Verified' değil: ${dStatus}"

    clickTO(X(dXXp))
    if (exists(X(updateButtonXpath()), 5)) {
        clickTO(X(updateButtonXpath()))
        WebUI.delay(1.0)
    }
    waitOverlayGone(10)

    String dAfterX = WebUI.getText(X(dVerXp)).trim().toLowerCase()
    log("[ADVD] AFTER X = " + dAfterX)
    assert dAfterX.contains("not") : "Unverify sonrası durum 'Not Verified' olmadı: ${dAfterX}"

    WebUI.closeWindowIndex(WebUI.getWindowIndex())
    WebUI.switchToWindowIndex(baseWinIndex)

    WebUI.navigateToUrl(BASE_URL + "/darkmap/brand-protection/advertisor-list")
    WebUI.waitForPageLoad(20)
    waitOverlayGone(10)

    WebUI.waitForElementClickable(X(networkTabXpath), 10)
    clickTO(X(networkTabXpath))
    waitOverlayGone(10)

    String nameAgain = WebUI.getText(X(nameXp)).trim()
    assert nameAgain.equalsIgnoreCase(pageName) : "Advertisor listte beklenen sayfa adı bulunamadı."
    String statusFinal = WebUI.getText(X(verXp)).trim().toLowerCase()
    log("[ADVL] FINAL STATUS = " + statusFinal)
    assert statusFinal.contains("not") : "Advertisor List final durum 'Not Verified' değil!"

    ignoreFlowAndValidate(networkTabXpath, expectedNetworkLabel)
}

void advertisorListGoogle(String networkTabXpath, String expectedNetworkLabel = 'Google') {
    setCurrentTab(networkTabXpath)

    String row1        = "//div[contains(@class,'group') and .//a[contains(@class,'underline')]]"
    String nameXp      = row1 + "//a[contains(@class,'underline') and contains(@class,'font-semibold')]"
    String verXp       = row1 + "//span[contains(.,'Verified') or contains(.,'Not Verified') or contains(.,'Not verified')]"
    String checkXp     = row1 + "//*[@class='lucide lucide-check h-4 w-4']"
    String xXp         = row1 + "//*[@class='lucide lucide-x h-4 w-4']"
    String VrfyBtnXp   = "//button[normalize-space(.)='Verify' or normalize-space(.)='Unverify']"
    String IGN_CLOSE   = "//button[contains(@class, 'absolute right-4 ') and contains(@class, 'opacity-70 ring')]"
    TestObject ignoreClose = X(IGN_CLOSE)

    WebUI.navigateToUrl(BASE_URL + "/darkmap/brand-protection/advertisor-list")
    WebUI.waitForPageLoad(20)

    WebUI.waitForElementClickable(X(networkTabXpath), 10)
    clickTO(X(networkTabXpath))

    String pageName = WebUI.getText(X(nameXp)).trim()
    String status0  = WebUI.getText(X(verXp)).trim()
    log("🟪 Advertisor List ROW name='" + pageName + "', status='" + status0 + "'")

    if (status0.toLowerCase().contains("verified") && exists(X(xXp), 3)) {
        clickTO(X(xXp))
        if (exists(X(VrfyBtnXp), 5)) clickTO(X(VrfyBtnXp))
        WebUI.delay(0.7)
        clickTO(ignoreClose)
        WebUI.delay(1.4)
        reloadListSoft()
        String afterClean = WebUI.getText(X(verXp)).trim()
        assert afterClean.toLowerCase().contains("not")
    }

    clickTO(X(checkXp))
    if (exists(X(VrfyBtnXp), 5)) clickTO(X(VrfyBtnXp))
    WebUI.delay(0.7)
    clickTO(ignoreClose)
    WebUI.delay(1.4)
    reloadListSoft()

    String status1 = WebUI.getText(X(verXp)).trim()
    log("[ADVL] AFTER CHECK = " + status1)
    assert status1.toLowerCase().contains("verified") && !status1.toLowerCase().contains("not")

    int baseWinIndex   = WebUI.getWindowIndex()
    def driver         = DriverFactory.getWebDriver()
    int beforeWinCount = driver.getWindowHandles().size()

    clickTO(X(nameXp))
    for (int i = 0; i < 40; i++) {
        WebUI.delay(0.25)
        if (DriverFactory.getWebDriver().getWindowHandles().size() > beforeWinCount) break
    }
    int afterCount = DriverFactory.getWebDriver().getWindowHandles().size()
    assert afterCount == beforeWinCount + 1 : "Yeni sekme açılamadı!"

    WebUI.switchToWindowIndex(afterCount - 1)
    WebUI.waitForPageLoad(20)
    waitOverlayGone(10)

    String dRow1   = "//div[contains(@class,'group') and .//a[contains(@class,'underline')]]"
    String dNameXp = dRow1 + "//a[contains(@class,'underline') and contains(@class,'font-semibold')]"
    String dVerXp  = "(//div[contains(@class,'group') and .//a[contains(@class,'underline')]]//span[contains(.,'Verified') or contains(.,'Not verified') or contains(.,'Not Verified')])[2]"
    String dXXp    = dRow1 + "//*[@class='lucide lucide-x h-4 w-4']"

    safeScrollTo(X(dNameXp), 10)
    WebUI.waitForElementVisible(X(dNameXp), 10)
    String dName = WebUI.getText(X(dNameXp)).trim()
    assert dName.equalsIgnoreCase(pageName) : "Dashboard ilk satır adı beklenen değil. Beklenen: ${pageName}, Bulunan: ${dName}"

    WebUI.waitForElementVisible(X(dVerXp), 10)
    String dStatus = WebUI.getText(X(dVerXp)).trim().toLowerCase()
    assert dStatus.contains("verified") : "Dashboard status 'Verified' değil: ${dStatus}"

    clickTO(X(dXXp))
    if (exists(X(updateButtonXpath()), 5)) {
        clickTO(X(updateButtonXpath()))
        WebUI.delay(1.0)
    }
    waitOverlayGone(10)

    String dAfterX = WebUI.getText(X(dVerXp)).trim().toLowerCase()
    log("[ADVD] AFTER X = " + dAfterX)
    assert dAfterX.contains("not") : "Unverify sonrası durum 'Not Verified' olmadı: ${dAfterX}"

    WebUI.closeWindowIndex(WebUI.getWindowIndex())
    WebUI.switchToWindowIndex(baseWinIndex)

    WebUI.navigateToUrl(BASE_URL + "/darkmap/brand-protection/advertisor-list")
    WebUI.waitForPageLoad(20)
    waitOverlayGone(10)

    if (exists(X("//div[normalize-space(.)='Google']"), 2)) { clickTO(X("//div[normalize-space(.)='Google']")) }
    waitOverlayGone(10)

    String nameAgain = WebUI.getText(X(nameXp)).trim()
    assert nameAgain.equalsIgnoreCase(pageName) : "Advertisor listte beklenen sayfa adı bulunamadı."
    String statusFinal = WebUI.getText(X(verXp)).trim().toLowerCase()
    log("[ADVL] FINAL STATUS = " + statusFinal)
    assert statusFinal.contains("not") : "Advertisor List final durum 'Not Verified' değil!"

    ignoreFlowAndValidate("//div[normalize-space(.)='Google']", 'Google')
}

/************** TEST START **************/
try {
    /*/ kullanıcı zaten login varsayımıyla:
       WebUI.openBrowser('')   
	WebUI.navigateToUrl(BASE_URL)
    WebUI.maximizeWindow()
	WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'), 30)
	WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'))
	WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 30)
	WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 'katalon.test@catchprobe.com')
	WebUI.setEncryptedText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Password_password'), 'RigbBhfdqOBDK95asqKeHw==')
	WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Sign in'))
	WebUI.delay(2)
	
	WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'), 30)
	String randomOtp = (100000 + new Random().nextInt(900000)).toString()
	WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'), randomOtp)
	WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Verify'))
	WebUI.delay(5)
	WebUI.waitForPageLoad(15)
	String Threat = "//span[text()='Threat']"
	WebUI.waitForElementClickable(X(Threat), 10, FailureHandling.OPTIONAL)
    waitOverlayGone(10)
    /*/
	WebUI.navigateToUrl(DASHBOARD_URL)
	WebUI.waitForPageLoad(30)
	waitOverlayGone(10)

    /************** GOOGLE SECTION **************/
    int gVerified   = intText(X("(//span[@class='text-xl font-bold'])[3]"))
    int gUnverified = intText(X("(//span[@class='text-xl font-bold'])[4]"))
    int gTotal      = gVerified + gUnverified
    KeywordUtil.logInfo("Google verified=" + gVerified + ", unverified=" + gUnverified + ", total=" + gTotal)

    if (gTotal > 0) {
        clickTO(X("//div[normalize-space(text())='Google Advert List']"))
        WebUI.waitForPageLoad(20)
        assertPaginationBasic(gTotal, 10)

        TestObject mediaBtn = X("//button[.//span[normalize-space(.)='Select Page Name']]")
        clickTO(mediaBtn)
        WebUI.waitForPageLoad(20)
        WebUI.delay(1.5)
        TestObject FilterBtn = X("(//div[contains(@class,'items-center') and contains(.,'Teknosa')])[2]")
        clickTO(FilterBtn)
        WebUI.waitForPageLoad(20)
        WebUI.delay(1.5)

        if (exists(X("//a[normalize-space(.)='2' or @aria-label='Page 2']"), 2)) {
            clickTO(X("//a[normalize-space(.)='2' or @aria-label='Page 2']"))
            WebUI.delay(0.5)
        }
        if (exists(X("//a[normalize-space(.)='1' or @aria-label='Page 1']"), 2)) {
            clickTO(X("//a[normalize-space(.)='1' or @aria-label='Page 1']"))
            WebUI.delay(0.5)
        }

        // soft refresh için aktif tabı işaretle
        setCurrentTab("//div[normalize-space(.)='Google']")

        toggleVerifyFlow()
        imageOpenCloseFlow()

        ignoreFlowAndValidate("//div[normalize-space(.)='Google']", 'Google')
        advertisorListGoogle("//div[normalize-space(.)='Google']", 'Google')

        WebUI.navigateToUrl(DASHBOARD_URL)
        WebUI.waitForPageLoad(20)
        waitOverlayGone(10)
    } else {
        KeywordUtil.logInfo('Google total == 0, skipping list & pagination as requested')
    }

    /************** META SECTION **************/
    int mVerified   = intText(X("(//span[@class='text-xl font-bold'])[7]"))
    int mUnverified = intText(X("(//span[@class='text-xl font-bold'])[8]"))
    int mTotal      = mVerified + mUnverified
    KeywordUtil.logInfo("LinkedIn verified=" + mVerified + ", unverified=" + mUnverified + ", total=" + mTotal)

    if (mTotal > 0) {
        TestObject lnBtn = X("//div[normalize-space(text())='Meta Advert List' or normalize-space(text())='Meta Advert List']")
        clickTO(lnBtn)
        WebUI.waitForPageLoad(20)
        assertPaginationBasic(mTotal, 10)

        TestObject mediaBtn = X("//button[.//span[normalize-space(.)='Select Page Name']]")
        clickTO(mediaBtn)
        WebUI.waitForPageLoad(20)
        WebUI.delay(1.5)
        TestObject FilterBtn = X("(//div[contains(@class,'items-center') and contains(.,'On Medya')])[2]")
        clickTO(FilterBtn)
        WebUI.waitForPageLoad(20)
        WebUI.delay(1.5)

        String targetValue = WebUI.getText(X("(//div[@class='col-span-2 justify-left text-left flex items-center gap-2 text-sm flex-wrap p-2 pl-4'])[1]"))
        KeywordUtil.logInfo('Captured target: ' + targetValue)
        WebUI.setText(X("//input[@placeholder='Target']"), targetValue)
        if (exists(X("//button[.//span[normalize-space(.)='Search'] or normalize-space(.)='SEARCH']"), 5)) {
            clickTO(X("//button[.//span[normalize-space(.)='Search'] or normalize-space(.)='SEARCH']"))
            WebUI.delay(1)
        }

        setCurrentTab("//div[normalize-space(.)='Meta']")

        toggleVerifyFlow()
        openAndVerifyExternal("(//*[contains(@class,'lucide') and contains(@class,'instagram')])[1]", 'facebook.com')

        ignoreFlowAndValidate("//div[normalize-space(.)='Meta' or normalize-space(.)='Meta']", 'Meta')
        advertisorList("//div[normalize-space(.)='Meta' or normalize-space(.)='Meta']", 'Meta')

        WebUI.navigateToUrl(DASHBOARD_URL)
        WebUI.waitForPageLoad(20)
        waitOverlayGone(10)
    } else {
        KeywordUtil.logInfo('Meta total == 0, skipping list & pagination as requested')
    }

    /************** LINKEDIN SECTION **************/
    int lVerified   = intText(X("(//span[@class='text-xl font-bold'])[1]"))
    int lUnverified = intText(X("(//span[@class='text-xl font-bold'])[2]"))
    int lTotal      = lVerified + lUnverified
    KeywordUtil.logInfo("LinkedIn verified=" + lVerified + ", unverified=" + lUnverified + ", total=" + lTotal)

    if (lTotal > 0) {
        TestObject lnBtn = X("//div[normalize-space(text())='LinkedIn Advert List' or normalize-space(text())='Linkedin Advert List']")
        clickTO(lnBtn)
        WebUI.waitForPageLoad(20)
        assertPaginationBasic(lTotal, 10)

        TestObject mediaBtn = X("//button[.//span[normalize-space(.)='Select Page Name']]")
        clickTO(mediaBtn)
        WebUI.waitForPageLoad(20)
        WebUI.delay(1.5)
        TestObject FilterBtn = X("(//div[contains(@class,'items-center') and contains(.,'Teknosa')])[2]")
        clickTO(FilterBtn)
        WebUI.waitForPageLoad(20)
        WebUI.delay(1.5)

        String targetValue = WebUI.getText(X("(//div[@class='col-span-2 justify-left text-left flex items-center gap-2 text-sm flex-wrap p-2 pl-4'])[1]"))
        KeywordUtil.logInfo('Captured target: ' + targetValue)
        WebUI.setText(X("//input[@placeholder='Target']"), targetValue)
        if (exists(X("//button[.//span[normalize-space(.)='Search'] or normalize-space(.)='SEARCH']"), 5)) {
            clickTO(X("//button[.//span[normalize-space(.)='Search'] or normalize-space(.)='SEARCH']"))
            WebUI.delay(1)
        }

        setCurrentTab("//div[normalize-space(.)='Linkedin' or normalize-space(.)='LinkedIn']")

        toggleVerifyFlowln()
        openAndVerifyExternal("(//*[contains(@class,'lucide') and contains(@class,'linkedin')])[1]/ancestor::button | (//*[contains(@class,'lucide') and contains(@class,'linkedin')])[1]", 'linkedin.com')
        imageOpenCloseFlow()

        ignoreFlowAndValidate("//div[normalize-space(.)='Linkedin' or normalize-space(.)='LinkedIn']", 'LinkedIn')
        advertisorList("//div[normalize-space(.)='Linkedin' or normalize-space(.)='Linkedin']", 'Linkedin')

        WebUI.navigateToUrl(DASHBOARD_URL)
        WebUI.waitForPageLoad(20)
        waitOverlayGone(10)
    } else {
        KeywordUtil.logInfo('LinkedIn total == 0, skipping list & pagination as requested')
    }
} finally {
    // WebUI.closeBrowser()
}
