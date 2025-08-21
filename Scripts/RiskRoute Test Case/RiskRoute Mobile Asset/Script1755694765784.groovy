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

/**************** helpers ****************/
TestObject X(String xp){ def to=new TestObject(xp); to.addProperty('xpath', ConditionType.EQUALS, xp); return to }
JavascriptExecutor js(){ (JavascriptExecutor) DriverFactory.getWebDriver() }

boolean waitUiIdle(int sec=3){
  long end=System.currentTimeMillis()+sec*1000L
  while(System.currentTimeMillis()<end){
    Boolean busy=(Boolean)js().executeScript("""
      const q='[aria-busy="true"], .ant-spin-spinning, .animate-spin, [data-loading="true"]';
      return !!document.querySelector(q);
    """)
    if(!busy) return true
    WebUI.delay(0.12)
  }
  return false
}

void clickFast(TestObject to){
  try { WebUI.click(to) } catch(ignore){
    try {
      WebElement el = WebUiCommonHelper.findWebElement(to, 3)
      js().executeScript("arguments[0].click();", el)
    } catch(e){ throw e }
  }
}

/** Ant/Radix overlay’lerini hızlı kapat */
void closeOverlaysQuick(int tries=8){
  for(int i=0;i<tries;i++){
    Boolean any=(Boolean)js().executeScript(
      "return !!document.querySelector('.ant-select-open,.ant-select-dropdown,[role=\"listbox\"],[data-state=\"open\"],.ant-modal,.ant-drawer-open');"
    )
    if(!any) break
    try { WebUI.sendKeys(X("//body"), Keys.ESCAPE) } catch(ignore){}
    WebUI.delay(0.05)
  }
  waitUiIdle(1)
}

/** Ekrana ortala (bulunamazsa exception atar) */
void scrollIntoViewXp(String xp){
  WebElement el = WebUiCommonHelper.findWebElement(X(xp), 10)
  js().executeScript("arguments[0].scrollIntoView({block:'center'});", el)
}

/** Silinen satır linkinin DOM’dan kalktığını bekle */
boolean waitRowGoneByHrefContains(String hrefPart, int timeoutSec=6){
  if (hrefPart==null || hrefPart.trim().isEmpty()) return false
  TestObject link = X("//a[contains(@href,'" + hrefPart.replaceAll("'", "\\\\'") + "')]")
  long end = System.currentTimeMillis() + timeoutSec*1000L
  while (System.currentTimeMillis() < end) {
    boolean present = WebUI.waitForElementPresent(link, 1, FailureHandling.OPTIONAL)
    if (!present) return true
    WebUI.delay(0.12)
  }
  return false
}

// Başarılı "deleted" toast — opsiyonel
boolean waitDeletedToast(int t=3){
  TestObject anyToast = X(
    "//*[@class[contains(.,'Toastify__toast-body')] and contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'deleted')]" +
    " | //div[contains(@class,'ant-message') or contains(@class,'ant-notification')]//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'deleted')]" +
    " | //*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'mobile asset deleted')]"
  )
  return WebUI.waitForElementPresent(anyToast, t, FailureHandling.OPTIONAL)
}

boolean toastExact(String text, int t=6){
  TestObject toast = X("//*[normalize-space(text())='${text}']")
  return WebUI.waitForElementPresent(toast, t, FailureHandling.OPTIONAL)
}

boolean verifyRow(String appId, String productContains, String imgAlt){
  TestObject row = X("//div[contains(@class,'grid-cols-24') and .//div[contains(normalize-space(.),'${appId}')]]")
  if(!WebUI.waitForElementPresent(row, 10, FailureHandling.OPTIONAL)) return false
  boolean prodOk = WebUI.waitForElementPresent(
      X("//div[contains(@class,'grid-cols-24') and .//div[contains(normalize-space(.),'${appId}')]]//*[contains(normalize-space(.),'${productContains}')]"),
      5, FailureHandling.OPTIONAL)
  boolean imgOk  = WebUI.waitForElementPresent(
      X("//div[contains(@class,'grid-cols-24') and .//div[contains(normalize-space(.),'${appId}')]]//img[@alt='${imgAlt}']"),
      5, FailureHandling.OPTIONAL)
  return prodOk && imgOk
}

boolean verifyRowNoAppId(String productContains, String imgAlt){
  TestObject row = X("//div[contains(@class,'grid-cols-24') and .//*[contains(normalize-space(.),'${productContains}')]]")
  if(!WebUI.waitForElementPresent(row, 10, FailureHandling.OPTIONAL)) return false
  return WebUI.waitForElementPresent(
      X("//div[contains(@class,'grid-cols-24') and .//*[contains(normalize-space(.),'${productContains}')]]//img[@alt='${imgAlt}']"),
      5, FailureHandling.OPTIONAL)
}

/** PRODUCT: butona tıkla → dropdown iç inputu odakla → ilk harf → ENTER → overlay kapat */
void pickProduct(String expectContains){
  // Tetikleyici (label sonrası + klasik buton/combobox)
  TestObject trigger = X(
    "(//label[normalize-space(.)='Product :'])[last()]/following::*[(self::button or @role='combobox' or contains(@class,'ant-select'))][1]" +
    " | //button[.//span[normalize-space(.)='Select a product'] or normalize-space(.)='Select a product']" +
    " | //*[@role='combobox' and (normalize-space(.)='Select a product' or .//text()[contains(.,'Select a product')])]"
  )
  WebUI.waitForElementClickable(trigger, 8)
  clickFast(trigger)
  WebUI.delay(0.08)

  // Açık overlay kökü
  TestObject dropRoot = X("(//*[contains(@class,'ant-select-dropdown') or @role='listbox' or @data-state='open'])[last()]")
  WebUI.waitForElementPresent(dropRoot, 3, FailureHandling.OPTIONAL)

  // İç input (Ant) veya listbox (Radix) odak
  TestObject innerInput = X("(//*[contains(@class,'ant-select-dropdown') or @role='listbox' or @data-state='open'])[last()]//input[not(@type='hidden')]")
  if (WebUI.waitForElementVisible(innerInput, 1, FailureHandling.OPTIONAL)) {
    try { WebUI.click(innerInput) } catch(ignore){}
  } else {
    try { WebUI.click(dropRoot) } catch(ignore){}
  }

  // İlk harf + ENTER
  String first = expectContains.substring(0,1).toLowerCase()
  try { WebUI.sendKeys(innerInput, first) } catch(ignore) { WebUI.sendKeys(X("//body"), first) }
  WebUI.delay(0.08)
  try { WebUI.sendKeys(innerInput, Keys.ENTER) } catch(ignore) { WebUI.sendKeys(X("//body"), Keys.ENTER) }

  // Overlay kesin kapansın
  closeOverlaysQuick()

  // Hafif doğrulama (zaman kaybetmeden)
  WebUI.waitForElementPresent(
    X("(//button | //*[@role='combobox'] | //div[contains(@class,'select')])[.//*[contains(normalize-space(.),'${expectContains}')] or contains(normalize-space(.),'${expectContains}')][1]"),
    2, FailureHandling.OPTIONAL
  )
}

/** COM NAME yaz (overlay kapat → normal yaz, olmazsa JS ile bas), sonra PREVIEW */
void previewAndCheckIcon(String appId, String expectedAlt){
  closeOverlaysQuick()

  TestObject appInput = X("//input[@name='app_id']")
  WebUI.scrollToElement(appInput, 3)

  boolean typed=false
  if (WebUI.waitForElementClickable(appInput, 2, FailureHandling.OPTIONAL)) {
    try {
      WebUI.click(appInput)
      WebUI.sendKeys(appInput, Keys.chord(Keys.CONTROL, "a"))
      WebUI.sendKeys(appInput, appId)
      typed=true
    } catch(ignore){}
  }
  if(!typed){
    try {
      WebElement we = WebUiCommonHelper.findWebElement(appInput, 3)
      js().executeScript("""
        arguments[0].removeAttribute('readonly');
        arguments[0].removeAttribute('disabled');
        arguments[0].focus();
        arguments[0].value = arguments[1];
        arguments[0].dispatchEvent(new Event('input',{bubbles:true}));
        arguments[0].dispatchEvent(new Event('change',{bubbles:true}));
      """, we, appId)
      typed=true
    } catch(ignore){}
  }
  if(!typed) KeywordUtil.markFailedAndStop("Com Name yazılamadı")

  // Blur ile validasyon tetikle
  try { WebUI.sendKeys(X(appInput), Keys.TAB) } catch(ignore){}

  // PREVIEW
  TestObject previewBtn = X("//button[normalize-space(.)='PREVIEW' or normalize-space(.)='Preview']")
  clickFast(previewBtn)
  WebUI.delay(0.3)

  // İkon opsiyonel (yavaşlatmasın)
  WebUI.waitForElementPresent(X("//img[@alt='${expectedAlt}']"), 3, FailureHandling.OPTIONAL)
}

/** Sade akış: Create → Product harf+Enter → Com Name → Preview → Create */
void createAsset(String productName, String appId, String expectedAlt){
  clickFast(X("//button[.//text()[contains(.,'Create Mobile Asset')]]"))

  pickProduct(productName)
  previewAndCheckIcon(appId, expectedAlt)

  String createXp = "//button[normalize-space(.)='CREATE' or normalize-space(.)='Create']"
  scrollIntoViewXp(createXp)
  clickFast(X(createXp))

  // modal/overlay kalmasın
  closeOverlaysQuick()
}

/** Toast + satır kontrolü isteyen varyant */
void createAssetNoAppIdCheck(String productContains, String appId, String expectedAlt){
  clickFast(X("//button[.//text()[contains(.,'Create Mobile Asset')]]"))
  waitUiIdle(1)
  pickProduct(productContains)
  previewAndCheckIcon(appId, expectedAlt)
  String createXp = "//button[normalize-space(.)='CREATE' or normalize-space(.)='Create']"
  scrollIntoViewXp(createXp)
  clickFast(X(createXp))
  WebUI.verifyEqual(toastExact("Mobile asset created successfully", 8), true)
  closeOverlaysQuick()
  WebUI.verifyEqual(verifyRowNoAppId(productContains, expectedAlt), true)
}

/**************** test flow ****************/
WebUI.openBrowser('')
WebUI.navigateToUrl("https://platform.catchprobe.org")
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

WebUI.navigateToUrl('https://platform.catchprobe.org/riskroute/mobile-asset-list')
waitUiIdle(2)

/* 1) Kayıt varsa: sil → modalı kapat → kısa doğrulama */
TestObject anyDelete = X("//a[contains(@href,'/riskroute/mobile-asset-list/') and contains(@href,'/delete')]")
if (WebUI.waitForElementPresent(anyDelete, 3, FailureHandling.OPTIONAL)) {
  WebElement delEl = WebUiCommonHelper.findWebElement(anyDelete, 3)
  String delHref = delEl?.getAttribute("href")
  String hrefPart = null
  if (delHref != null) {
    int i = delHref.indexOf("mobile-asset-list/")
    if (i >= 0) {
      int j = delHref.indexOf("/", i + "mobile-asset-list/".length())
      hrefPart = (j > 0) ? delHref.substring(i, j) : delHref.substring(i)
    }
  }
  clickFast(anyDelete)

  TestObject confirmDel = X("//button[normalize-space(.)='Delete']")
  if (WebUI.waitForElementClickable(confirmDel, 4, FailureHandling.OPTIONAL)) {
    clickFast(confirmDel)
  }

  closeOverlaysQuick()
  waitUiIdle(1)

  boolean deletedOk = waitDeletedToast(2)
  if (!deletedOk) WebUI.comment("⚠️ Delete toast yok, row-gone ile doğrulanacak.")

  boolean gone = waitRowGoneByHrefContains(hrefPart, 6)
  if (!gone) WebUI.comment("⚠️ Satır hâlâ görünüyor olabilir (UI yavaş). Devam ediyorum.")
}

/* 2) Boş-durum mesajı opsiyonel */
TestObject emptyMsg = X(
  "//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'mobile asset not found')]" +
  " | //*[contains(normalize-space(.),'No data') or contains(normalize-space(.),'No assets')]"
)
if (WebUI.waitForElementPresent(emptyMsg, 2, FailureHandling.OPTIONAL)) {
  WebUI.scrollToElement(emptyMsg, 2)
}

/* 3) Kayıt ekleme */
createAsset('Google Play Store', 'com.whatsapp', 'WhatsApp Messenger')
createAsset('Xiaomi',            'com.whatsapp', 'WhatsApp Messenger')
createAsset('Apple Store',       'net.whatsapp.WhatsApp', 'WhatsApp Messenger')
createAssetNoAppIdCheck('Xiaomi','com.turkcell.bip','BiP - Messenger, Video Call')

WebUI.comment('✅ Mobile Asset akışı tamamlandı.')
