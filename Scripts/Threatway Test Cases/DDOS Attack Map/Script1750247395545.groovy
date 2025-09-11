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

import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebElement
import java.util.Random

/************** Mini yardımcılar **************/
TestObject X(String xp){
  def to=new TestObject(xp)
  to.addProperty("xpath",ConditionType.EQUALS,xp)
  return to
}

boolean isBrowserOpen(){
  try { DriverFactory.getWebDriver(); return true } catch(Throwable t){ return false }
}

/** Elemanı görünür olana kadar küçük adımlarla sayfayı kaydır */
void scrollToVisible(WebElement el){
  if(el==null) return
  int sc=0
  JavascriptExecutor jse = (JavascriptExecutor) DriverFactory.getWebDriver()
  while(sc<3000 && !el.isDisplayed()){
    jse.executeScript("window.scrollBy(0, 200)")
    WebUI.delay(0.2)
    sc+=200
  }
}

/** Görünür + tıklanabilir bekleyip tıkla */
void clickSmart(TestObject to, int t=10){
  WebUI.waitForElementVisible(to, t)
  WebUI.waitForElementClickable(to, t)
  WebUI.click(to)
}

/** Input’a hızlı yaz – JS fallback */
void clearAndType(TestObject to, String text, int t=10){
  WebUI.waitForElementVisible(to,t,FailureHandling.OPTIONAL)
  WebElement e = WebUiCommonHelper.findWebElement(to,t)
  try { e.clear(); e.sendKeys(text); return } catch(_){}
  try {
    JavascriptExecutor jse = (JavascriptExecutor) DriverFactory.getWebDriver()
    jse.executeScript("arguments[0].value=''; arguments[0].dispatchEvent(new Event('input',{bubbles:true}))", e)
    jse.executeScript("arguments[0].value=arguments[1]; arguments[0].dispatchEvent(new Event('input',{bubbles:true}))", e, text)
  } catch(_){
    WebUI.setText(to,text)
  }
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

/************** TEST: Collections **************/
ensureSession()


// Threatway sekmesine git
WebUI.navigateToUrl('https://platform.catchprobe.org/threatway')
WebUI.waitForPageLoad(30)
WebUI.delay(5)


WebUI.navigateToUrl('https://platform.catchprobe.org/threatway/ddos/attack-map')

// Iframe'i tanımla
TestObject iframeObj = new TestObject('iframe')
iframeObj.addProperty('id', ConditionType.EQUALS, 'fullScreenThreatwayAttackMap')

// Iframe'e geçiş yap
WebUI.waitForElementVisible(iframeObj, 10)
WebUI.switchToFrame(iframeObj, 10)

// Scroll yapılacak alanı tanımla
TestObject scrollableDiv = new TestObject('scrollableInsideIframe')
scrollableDiv.addProperty('xpath', ConditionType.EQUALS, "//div[@class='col-md-12 live-attacks-timestamp']")

int maxAttempts = 3
int attempt = 1
boolean elementFound = false

while (attempt <= maxAttempts) {
    if (WebUI.verifyElementPresent(scrollableDiv, 10, FailureHandling.OPTIONAL)) {
        elementFound = true
        break
    } else {
        WebUI.comment("❌ Element bulunamadı. Sayfa yenileniyor... Deneme: $attempt")
        WebUI.switchToDefaultContent() // iframe’den çık
        WebUI.refresh()
        WebUI.waitForPageLoad(10)
        WebUI.waitForElementVisible(iframeObj, 10)
        WebUI.switchToFrame(iframeObj, 10)
    }
    attempt++
}

if (!elementFound) {
    WebUI.comment("❌ Element $maxAttempts denemede bulunamadı. Test durduruluyor.")
    WebUI.takeScreenshot()
    assert false : "Element bulunamadı."
}

// Bulunduysa devam et
WebUI.comment("✅ Element bulundu, işleme devam ediliyor.")
WebUI.executeJavaScript('arguments[0].scrollTop = 300', Arrays.asList(WebUI.findWebElement(scrollableDiv)))
WebUI.waitForElementVisible(findTestObject('Object Repository/otp/Page_/ddos_source'), 15)

String time1 = WebUI.getText(findTestObject('Object Repository/otp/Page_/ddos_source'))
println(time1)
WebUI.delay(10)
String time2 = WebUI.getText(findTestObject('Object Repository/otp/Page_/ddos_source'))
println(time2)

if (time1 != time2) {
    WebUI.comment("✅ Canlı akış aktif. Time değişti: $time1 → $time2")
} else {
    WebUI.comment("⚠️ Uyarı! Time aynı kaldı: $time1")
    WebUI.takeScreenshot()
    assert false : 'Canlı akış algılandı → test senaryosu başarısız olmalı!'
}

WebUI.switchToDefaultContent()