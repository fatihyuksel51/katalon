/************** Imports **************/
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.ObjectRepository as OR
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.Keys
import java.util.Random
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor

/************** Mini yardımcılar **************/
TestObject X(String xp){ def to=new TestObject(xp); to.addProperty("xpath",ConditionType.EQUALS,xp); return to }

boolean isBrowserOpen(){ try{ DriverFactory.getWebDriver(); return true }catch(Throwable t){ return false } }

/** Elemanı görünür olana kadar küçük adımlarla sayfayı kaydır */
void scrollToVisible(WebElement el){
    int sc=0
    JavascriptExecutor jse = (JavascriptExecutor) DriverFactory.getWebDriver()
    while(sc<3000 && el!=null && !el.isDisplayed()){
        jse.executeScript("window.scrollBy(0, 200)")
        WebUI.delay(0.2)
        sc += 200
    }
}

/** hızlı yaz – olmazsa JS fallback */
void clearAndType(TestObject to, String text, int t=10){
    WebUI.waitForElementVisible(to,t,FailureHandling.OPTIONAL)
    WebElement e = WebUiCommonHelper.findWebElement(to,t)
    try{ e.clear(); e.sendKeys(text); return }catch(_){}
    try{
        JavascriptExecutor jse = (JavascriptExecutor) DriverFactory.getWebDriver()
        jse.executeScript("arguments[0].value=''; arguments[0].dispatchEvent(new Event('input',{bubbles:true}))", e)
        jse.executeScript("arguments[0].value=arguments[1]; arguments[0].dispatchEvent(new Event('input',{bubbles:true}))", e, text)
    }catch(_){
        WebUI.setText(to,text)
    }
}

/** Clipboard güvenli okuma (başarısızsa fallback döner) */
String getClipboardTextOr(String fallback){
    try{
        def cb = Toolkit.getDefaultToolkit().getSystemClipboard()
        def data = cb.getData(DataFlavor.stringFlavor)
        if(data!=null) return data.toString()
    }catch(Throwable _){}
    return fallback
}

/************** Oturum **************/
void ensureSession(){
    if(isBrowserOpen()) return
    WebUI.openBrowser('')
    WebUI.maximizeWindow()
    WebUI.navigateToUrl('https://platform.catchprobe.org/')

    WebUI.waitForElementVisible(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'), 30)
    WebUI.click(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'))

    WebUI.waitForElementVisible(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 30)
    WebUI.setText(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 'katalon.test@catchprobe.com')
    WebUI.setEncryptedText(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Password_password'), 'RigbBhfdqOBDK95asqKeHw==')
    WebUI.click(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Sign in'))

    WebUI.delay(3)
    String otp = (100000 + new Random().nextInt(900000)).toString()
    WebUI.setText(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'), otp)
    WebUI.click(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Verify'))
    WebUI.delay(2)

    WebUI.waitForElementVisible(X("//span[text()='Threat']"), 10, FailureHandling.OPTIONAL)
}

/************** TEST: Coin Search Engine **************/
ensureSession()

// Doğrudan sayfaya git
WebUI.navigateToUrl('https://platform.catchprobe.org/threatway/coin-search-engine')
WebUI.waitForPageLoad(30)
try { CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'() } catch(_){}

// 1) "From Copy" → panoya kopyala ve toast doğrula
TestObject btnFromCopy = findTestObject('Object Repository/Coin Search Engine/From Copy')
WebUI.waitForElementClickable(btnFromCopy, 15)
WebUI.click(btnFromCopy)

TestObject toast = findTestObject('Object Repository/Coin Search Engine/Toast Message')
WebUI.waitForElementVisible(toast, 10)
WebUI.verifyElementText(toast, 'From Account copied to clipboard successfully!')

// 2) Ekrandaki "From Link" ve "Coin link" metinlerini al
TestObject fromLink = findTestObject('Object Repository/Coin Search Engine/From Link')
TestObject coinLink = findTestObject('Object Repository/Coin Search Engine/Coin link')
WebUI.waitForElementVisible(fromLink, 15)
WebUI.waitForElementVisible(coinLink, 15)

String fromTextScreen = WebUI.getText(fromLink) ?: ""
String coinTextScreen = WebUI.getText(coinLink) ?: ""

// 3) Clipboard’tan oku; olmazsa From Link’i kullan
String fromTextClipboard = getClipboardTextOr(fromTextScreen)
KeywordUtil.logInfo("Clipboard/From value: " + fromTextClipboard)

// 4) Filtreyi aç, değeri gir, Apply & Search
TestObject btnOpenFilter = findTestObject('Object Repository/Coin Search Engine/Theatway filterbuton')
WebUI.waitForElementClickable(btnOpenFilter, 10)
WebUI.click(btnOpenFilter)

TestObject filterInput = findTestObject('Object Repository/Coin Search Engine/İnput')
WebUI.waitForElementVisible(filterInput, 10)
clearAndType(filterInput, fromTextClipboard)

TestObject btnApplyAndSearch = findTestObject('Object Repository/Coin Search Engine/threatway button_APPLY AND SEARCH')
WebUI.waitForElementClickable(btnApplyAndSearch, 10)
WebUI.click(btnApplyAndSearch)

// 5) Sonuçlar yenilensin ve From Link aynı kalsın
WebUI.waitForPageLoad(15)
WebUI.waitForElementVisible(fromLink, 15)
WebUI.verifyElementText(fromLink, fromTextScreen)

// 6) Coin detayını aç → IP link doğrula
WebUI.waitForElementClickable(coinLink, 10)
WebUI.click(coinLink)

TestObject ipLink = findTestObject('Object Repository/Coin Search Engine/İp Link')
// Burada iş kuralına göre doğrulama yapılıyor.
// Senin akışında IP panelindeki metni coin link ile eşliyorsun:
WebUI.waitForElementVisible(ipLink, 10)
WebUI.verifyElementText(ipLink, coinTextScreen)

// Detayı kapat
TestObject btnDetailClose = findTestObject('Object Repository/Coin Search Engine/Show detail Close button')
WebUI.waitForElementClickable(btnDetailClose, 10)
WebUI.click(btnDetailClose)

// Filtre paneli açıksa kapat (opsiyonel)
TestObject btnFilterClose = findTestObject('Object Repository/Coin Search Engine/Filter Close')
if(WebUI.verifyElementPresent(btnFilterClose, 3, FailureHandling.OPTIONAL)){
    WebUI.click(btnFilterClose)
}

// 7) İkinci öğe doğrulaması (görünür yap & tıkla)
TestObject secondCoin = findTestObject('Object Repository/Coin Search Engine/Coin Second')
WebElement secondEl = WebUiCommonHelper.findWebElement(secondCoin, 10)
scrollToVisible(secondEl)
((JavascriptExecutor)DriverFactory.getWebDriver()).executeScript("arguments[0].click();", secondEl)
WebUI.comment("👉 'Coin Second' tıklandı.")
WebUI.delay(2)

// Detayı kapat
WebUI.waitForElementClickable(btnDetailClose, 10)
WebUI.click(btnDetailClose)

WebUI.comment('✅ Coin Search Engine senaryosu tamamlandı.')
