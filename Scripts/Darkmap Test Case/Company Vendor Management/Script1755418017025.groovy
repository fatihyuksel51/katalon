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
TestObject X(String xp){ def to=new TestObject(xp); to.addProperty("xpath", ConditionType.EQUALS, xp); return to }
JavascriptExecutor js(){ (JavascriptExecutor) DriverFactory.getWebDriver() }

void scrollIntoViewXp(String xp){ 
  try { 
    WebElement el = WebUiCommonHelper.findWebElement(X(xp), 5)
    js().executeScript("arguments[0].scrollIntoView({block:'center'});", el)
  } catch(Throwable ignore) {}
}

void safeClick(String xp, int t=15){
  if(!WebUI.waitForElementClickable(X(xp), t, FailureHandling.OPTIONAL))
    KeywordUtil.markFailedAndStop("Clickable değil: "+xp)
  try{ WebUI.click(X(xp)) } 
  catch(Throwable e){
    try{
      WebElement el = WebUiCommonHelper.findWebElement(X(xp), 3)
      js().executeScript("arguments[0].click();", el)
    } catch(Throwable ee){
      KeywordUtil.markFailedAndStop("Tıklanamadı: "+xp+" | "+ee.message)
    }
  }
}
String safeText(String xp, int t=12){
  if(!WebUI.waitForElementVisible(X(xp), t, FailureHandling.OPTIONAL))
    KeywordUtil.markFailedAndStop("Görünür değil: "+xp)
  return WebUI.getText(X(xp)).trim()
}
String getAttr(String xp, String attr, int t=6){
  WebElement el = WebUiCommonHelper.findWebElement(X(xp), t)
  return el.getAttribute(attr)
}
String switchToNewTab(int waitSec=8){
  def d = DriverFactory.getWebDriver()
  String original = d.getWindowHandle()
  long end = System.currentTimeMillis()+waitSec*1000L
  while(System.currentTimeMillis()<end && d.getWindowHandles().size()<2){ WebUI.delay(0.2) }
  for(String h: d.getWindowHandles()) if(h!=original){ d.switchTo().window(h); break }
  WebUI.waitForPageLoad(15)
  return original
}
void closeTabAndBack(String original){
  def d = DriverFactory.getWebDriver()
  try{ d.close() }catch(Throwable ignore){}
  d.switchTo().window(original)
  WebUI.waitForPageLoad(10)
}

/* Görsel doğrulaması – sayfada yüklenmiş en az bir gerçek img var mı? */
boolean waitAnyRealImageLoaded(int timeoutSec=20){
  long end = System.currentTimeMillis()+timeoutSec*1000L
  while(System.currentTimeMillis()<end){
    Boolean ok = (Boolean) js().executeScript("""
      const inV = el => { const r=el.getBoundingClientRect(); return r.width>50&&r.height>50&&r.bottom>0&&r.right>0&&r.top<innerHeight&&r.left<innerWidth; };
      return [...document.images].some(i => inV(i) && (i.currentSrc||i.src||'').includes('http') && i.naturalWidth>0);
    """)
    if(ok==Boolean.TRUE) return true
    WebUI.delay(0.5)
  }
  return false
}

boolean waitToastContains(String txt, int timeout=10){
  String xp = "//*[contains(@class,'ant-message') or contains(@class,'ant-notification') or contains(@class,'toast') or contains(@class,'alert')]" +
              "[not(contains(@style,'display: none'))]//*[contains(normalize-space(.), '"+txt.replace("'", "\\'")+"')]"
  return WebUI.waitForElementVisible(X(xp), timeout, FailureHandling.OPTIONAL)
}

/* Checkbox state setter: (//button[@role='checkbox'])[idx] → data-state checked/unchecked */
void setCheckbox(int idx, boolean shouldBeChecked){
  String xp = "(//button[@role='checkbox'])["+idx+"]"
  if(!WebUI.waitForElementVisible(X(xp), 8, FailureHandling.OPTIONAL))
    KeywordUtil.markFailedAndStop("Checkbox görünmedi: "+xp)
  String state = getAttr(xp, "data-state", 5) ?: ""
  boolean isChecked = state.equalsIgnoreCase("checked")
  if(isChecked != shouldBeChecked){
    safeClick(xp, 8)
    WebUI.delay(0.3)
    String state2 = getAttr(xp, "data-state", 5) ?: ""
    if(shouldBeChecked && !state2.equalsIgnoreCase("checked"))
      KeywordUtil.markFailedAndStop("Checkbox 'checked' olmadı: "+xp)
    if(!shouldBeChecked && !state2.equalsIgnoreCase("unchecked"))
      KeywordUtil.markFailedAndStop("Checkbox 'unchecked' olmadı: "+xp)
  }
}

/* -------------------- TEST -------------------- */
try{
  WebUI.openBrowser('')
  WebUI.navigateToUrl('https://platform.catchprobe.org/')
  WebUI.maximizeWindow()

  // Login
  WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'), 30)
  WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'))
  WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 30)
  WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 'fatih.yuksel@catchprobe.com')
  WebUI.setEncryptedText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Password_password'), 'RigbBhfdqOBDK95asqKeHw==')
  WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Sign in'))
  WebUI.delay(2)
  String otp = (100000 + new Random().nextInt(900000)).toString()
  WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'), otp)
  WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Verify'))
  WebUI.delay(8)
  WebUI.waitForPageLoad(15)

  // Sol menü → Company Vendor Management
  WebUI.navigateToUrl("https://platform.catchprobe.org/darkmap/company-vendor-management")
  WebUI.delay(5)
  WebUI.waitForPageLoad(15)

  // SECURITY sekmesi
  String xpSecurity = "//button[div[normalize-space()='SECURITY']]"
  safeClick(xpSecurity, 15)

  // İlk 2 checkbox → checked
  setCheckbox(1, true)
  setCheckbox(2, true)

  // SAVE + toast
  String xpSave = "(//button[normalize-space(translate(.,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'))='SAVE' or normalize-space(.)='Save'])[1]"
  scrollIntoViewXp(xpSave)
  safeClick(xpSave, 10)
  if(!waitToastContains("Vendors saved successfully.", 8))
    KeywordUtil.markFailed("Toast beklenen değil (SAVE sonrası).")

  // data-state doğrulama
  if(!getAttr("(//button[@role='checkbox'])[1]", "data-state", 5).equalsIgnoreCase("checked"))  KeywordUtil.markFailed("1. checkbox checked değil.")
  if(!getAttr("(//button[@role='checkbox'])[2]", "data-state", 5).equalsIgnoreCase("checked"))  KeywordUtil.markFailed("2. checkbox checked değil.")

  // Vendor isimlerini al
  String vendor1 = safeText("(//span[preceding-sibling::button[@role='checkbox']])[1]", 10)
  String vendor2 = safeText("(//span[preceding-sibling::button[@role='checkbox']])[2]", 10)
  KeywordUtil.logInfo("Seçilen vendorlar: ["+vendor1+"], ["+vendor2+"]")

  // Vendor Result
  String xpVendorResult = "//button[normalize-space(.)='Vendor Result']"
  safeClick(xpVendorResult, 12)
  WebUI.waitForPageLoad(10)

  // İçeride 'Security' tuşu olmalı ve target linkleri vendor isimleri ile eşleşmeli
  String xpInnerSecurityBtn = "//button[normalize-space(.)='SECURITY']"
  if(!WebUI.waitForElementVisible(X(xpInnerSecurityBtn), 8, FailureHandling.OPTIONAL))
    KeywordUtil.markFailed("Vendor Result içinde 'Security' bulunamadı.")
  safeClick(xpInnerSecurityBtn, 10)

  String xpT1 = "(//a[@target='_blank'])[1]"
  String xpT2 = "(//a[@target='_blank'])[2]"
  String t1 = safeText(xpT1, 10)
  String t2 = safeText(xpT2, 10)

  if(!(t1.toLowerCase().contains(vendor1.toLowerCase())))
    KeywordUtil.markFailed("1. target vendor1 ile eşleşmiyor. target='"+t1+"', vendor1='"+vendor1+"'")
  if(!(t2.toLowerCase().contains(vendor2.toLowerCase())))
    KeywordUtil.markFailed("2. target vendor2 ile eşleşmiyor. target='"+t2+"', vendor2='"+vendor2+"'")

  // İlk targeta tıkla → yeni sekme → keyword doğrula → görsel doğrulama → geri
  safeClick(xpT1, 10)
  String original = switchToNewTab(8)
  String kw = safeText("//span[@class='px-2']", 12)
  if(!kw.equalsIgnoreCase(vendor1))
    KeywordUtil.markFailed("Quick Search keyword '"+kw+"' vendor1 '"+vendor1+"' ile eşleşmedi.")
  if(!waitAnyRealImageLoaded(20))
    KeywordUtil.markWarning("Görsel doğrulaması: sayfada yüklenmiş img bulunamadı.")
  closeTabAndBack(original)

  // Return Vendors
  String xpReturnVendors = "//button[normalize-space(.)='Return Vendors']"
  safeClick(xpReturnVendors, 12)
  WebUI.waitForPageLoad(10)

  // SECURITY → ilk 2 checkbox'ı kaldır (unchecked) → SAVE → toast → unchecked doğrula
  safeClick(xpSecurity, 12)
  setCheckbox(1, false)
  setCheckbox(2, false)
  scrollIntoViewXp(xpSave); safeClick(xpSave, 10)
  if(!waitToastContains("Vendors saved successfully.", 8))
    KeywordUtil.markFailed("Toast beklenen değil (SAVE/unchecked sonrası).")
  if(!getAttr("(//button[@role='checkbox'])[1]", "data-state", 5).equalsIgnoreCase("unchecked"))
    KeywordUtil.markFailed("1. checkbox unchecked değil.")
  if(!getAttr("(//button[@role='checkbox'])[2]", "data-state", 5).equalsIgnoreCase("unchecked"))
    KeywordUtil.markFailed("2. checkbox unchecked değil.")

  // Vendor Result'ta artık Security butonu olmamalı
  safeClick(xpVendorResult, 10)
  if(WebUI.waitForElementVisible(X(xpInnerSecurityBtn), 5, FailureHandling.OPTIONAL))
    KeywordUtil.markFailed("Security butonu görünmemeliydi, ama görünüyor.")
  safeClick(xpReturnVendors, 12)
  WebUI.waitForPageLoad(8)

  safeClick(xpSecurity, 12)

  // + (yeni vendor ekleme)
  String xpPlus = "//span[contains(@class,'cursor-pointer ml-2')]"
  safeClick(xpPlus, 10)

  // Form 5 sn içinde gelmeli
  String xpInput = "//input[@type='text' and @value='']"
  if(!WebUI.waitForElementVisible(X(xpInput), 5, FailureHandling.OPTIONAL))
    KeywordUtil.markFailedAndStop("Yeni vendor formu açılmadı (input görünmedi).")
  WebUI.setText(X(xpInput), "katalon")

  // İkinci mavi + butonu
  String xpBluePlus2 = "(//div[@class='flex gap-4']//button[@type='button' and contains(@class,'px-4 py-2')])[1]"
  safeClick(xpBluePlus2, 10)
  WebUI.delay(3)

  // CREATE
  String xpCreate = "//button[normalize-space(.)='CREATE' or normalize-space(.)='Create']"
  safeClick(xpCreate, 10)

  // Onay toast (birebir)
  if(!waitToastContains("After approval vendor will be added to your vendor list.", 12))
    KeywordUtil.markFailed("Onay toast'ı birebir gelmedi.")

  KeywordUtil.markPassed("✅ Company Vendor Management testi başarıyla tamamlandı.")
} finally {
  // WebUI.closeBrowser()
}
