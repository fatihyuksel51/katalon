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

import java.util.List
import java.util.Random

/**************** helpers ****************/
TestObject X(String xp){
  def to = new TestObject(xp)
  to.addProperty('xpath', ConditionType.EQUALS, xp)
  return to
}
JavascriptExecutor js(){ (JavascriptExecutor) DriverFactory.getWebDriver() }

void clickFast(TestObject to){
  try {
    WebUI.click(to)
  } catch(Exception ignore){
    try {
      WebElement el = WebUiCommonHelper.findWebElement(to, 3)
      js().executeScript("arguments[0].click();", el)
    } catch(Exception ignore2){}
  }
}

boolean waitToastContains(String txt, int timeout=8){
  String xp =
      "//*[contains(@class,'ant-message') or contains(@class,'ant-notification') or contains(@class,'toast') or contains(@class,'alert')]" +
      "[not(contains(@style,'display: none'))]//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')," +
      "'" + txt.toLowerCase().replace("'", "\\'") + "')]"
  return WebUI.waitForElementVisible(X(xp), timeout, FailureHandling.OPTIONAL)
}

boolean waitDeletedToast(int t = 4) {
  TestObject anyToast = X(
    "//*[contains(@class,'Toastify__toast-body') and contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'deleted')]" +
    " | //div[contains(@class,'ant-message') or contains(@class,'ant-notification')]//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'deleted')]" +
    " | //*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'mobile asset deleted')]"
  )
  return WebUI.waitForElementPresent(anyToast, t, FailureHandling.OPTIONAL)
}

boolean waitUiIdle(int sec=3){
  long end=System.currentTimeMillis()+sec*1000L
  while(System.currentTimeMillis()<end){
    Boolean busy=(Boolean)js().executeScript("""
      const q='[aria-busy="true"], .ant-spin-spinning, .animate-spin, [data-loading="true"]';
      return !!document.querySelector(q);
    """)
    if(!busy) return true
    WebUI.delay(0.15)
  }
  return false
}

/** Sadece select/dropdown’u gövdeden tıklayarak kapat (ESC YOK). */
void closeSelectOnly(){
  for(int i=0;i<5;i++){
    Boolean open = (Boolean) js().executeScript(
      "return !!document.querySelector('.ant-select-open,.ant-select-dropdown[style*=\"display: block\"],[role=\"listbox\"][data-state=\"open\"]');"
    )
    if(!open) break
    try {
      WebElement body = (WebElement) js().executeScript("""
        return document.querySelector('.ant-drawer-body') 
            || document.querySelector('.ant-drawer-content')
            || document.querySelector('[role="dialog"],[data-state="open"]');
      """)
      if (body!=null) js().executeScript("arguments[0].click()", body)
      else js().executeScript("document.activeElement && document.activeElement.blur && document.activeElement.blur()")
    } catch(ignore){}
    WebUI.delay(0.08)
  }
  waitUiIdle(1)
}

/** Modal/Drawer’ı hızlı kapat (ESC + olası Close butonları) — sadece create/delete sonrası kullan. */
void closeModalsFast(int tries = 4) {
  for (int i = 0; i < tries; i++) {
    try { WebUI.sendKeys(X("//body"), Keys.chord(Keys.ESCAPE)) } catch(ignore) {}
    try {
      TestObject anyClose = X(
        "//*[@role='dialog']//button[contains(@class,'close') or contains(@aria-label,'lose') or contains(.,'×') or contains(.,'Close') or contains(.,'Kapat')]" +
        " | //div[contains(@class,'ant-modal') and contains(@class,'open')]//button[contains(@class,'ant-modal-close')]" +
        " | //div[contains(@data-state,'open')]//button[contains(@class,'DialogClose') or contains(@class,'ModalClose') or contains(@class,'Close')]" +
        " | //button[@aria-label='Close' or @aria-label='Kapat']" +
        " | //button[contains(@class,'right-4') and contains(@class,'ring')]"
      )
      if (WebUI.waitForElementClickable(anyClose, 1, FailureHandling.OPTIONAL)) {
        try { WebUI.click(anyClose) } catch(ignore){}
      }
    } catch(ignore) {}
    WebUI.delay(0.12)
    Boolean stillOpen = (Boolean) js().executeScript(
      "return !!document.querySelector('.ant-modal-wrap,.ant-drawer-open,[data-state=\"open\"],[role=\"dialog\"]:not([aria-hidden=\"true\"])');")
    if (!stillOpen) break
  }
  waitUiIdle(1)
}

void scrollIntoViewXp(String xp) {
  WebElement el = WebUiCommonHelper.findWebElement(X(xp), 10)
  js().executeScript("arguments[0].scrollIntoView({block:'center'});", el)
}

boolean toastExact(String text, int t=6){
  TestObject toast=X("//*[normalize-space(text())='${text}']")
  return WebUI.waitForElementPresent(toast, t, FailureHandling.OPTIONAL)
}

/** Satır tamamen DOM’dan kalktı mı? (hrefPart ile) */
boolean waitRowGoneByHrefContains(String hrefPart, int timeoutSec = 6) {
  if (!hrefPart) return false
  TestObject link = X("//a[contains(@href,'" + hrefPart.replaceAll("'", "\\'") + "')]")
  long end = System.currentTimeMillis() + timeoutSec * 1000L
  while (System.currentTimeMillis() < end) {
    boolean present = WebUI.waitForElementPresent(link, 1, FailureHandling.OPTIONAL)
    if (!present) return true
    WebUI.delay(0.15)
  }
  return false
}

/** Liste sayısı azaldı mı? (fallback) */
boolean waitCountDecrease(TestObject selector, int before, int timeoutSec = 10) {
  long end = System.currentTimeMillis() + timeoutSec * 1000L
  while (System.currentTimeMillis() < end) {
    int after = 0
    try { after = WebUiCommonHelper.findWebElements(selector, 1)?.size() ?: 0 } catch(ignore) {}
    if (after < before) return true
    WebUI.delay(0.3)
  }
  return false
}

/** Listede görünen TÜM çöp ikonlarını ardışık sil. Dönen: silinen kayıt adedi. */
int deleteAllTrash(int maxRounds = 30) {
  // 1) sağlam referans: href içeren anchor
  String xpAnchor = "//a[contains(@href,'/riskroute/mobile-asset-list/') and contains(@href,'/delete')]"
  // 2) yedekler: trash ikonlu anchor/button
  String xpAlt = "(" +
      "//*[name()='svg' and contains(@class,'lucide') and contains(@class,'trash')]/ancestor::a|" +
      "//*[name()='svg' and contains(@class,'lucide') and contains(@class,'trash')]/ancestor::button|" +
      "//button[.//*[name()='svg' and (contains(@class,'trash') or contains(@class,'trash2'))]]" +
    ")"

  TestObject selPrimary = X(xpAnchor)
  TestObject selUnion   = X(xpAnchor + " | " + xpAlt)

  int deleted = 0

  for (int round = 0; round < maxRounds; round++) {
    List<WebElement> btns
    try { btns = WebUiCommonHelper.findWebElements(selUnion, 2) } catch(ignore) { btns = [] }
    if (btns == null || btns.isEmpty()) break

    int before = btns.size()
    WebElement target = btns.get(0)

    // hrefPart çıkar (anchor ise)
    String hrefPart = null
    try {
      if ("a".equalsIgnoreCase(target.getTagName())) {
        String delHref = target.getAttribute("href")
        if (delHref != null) {
          int i = delHref.indexOf("mobile-asset-list/")
          if (i >= 0) {
            int j = delHref.indexOf("/", i + "mobile-asset-list/".length())
            hrefPart = (j > 0) ? delHref.substring(i, j) : delHref.substring(i)
          }
        }
      }
    } catch(ignore) {}

    // görünür alana al & tıkla
    try { js().executeScript("arguments[0].scrollIntoView({block:'center'});", target) } catch(ignore) {}
    try { target.click() } catch(Exception e) {
      try { js().executeScript("arguments[0].click();", target) } catch(ignore2){}
    }

    // onay
    TestObject confirmDel = X("//button[normalize-space(.)='Delete' or normalize-space(.)='DELETE' or normalize-space(.)='Yes' or normalize-space(.)='YES']")
    if (WebUI.waitForElementClickable(confirmDel, 5, FailureHandling.OPTIONAL)) {
      clickFast(confirmDel)
    }

    // modal kapanışı / idle
    closeModalsFast(2)
    waitUiIdle(1)

    // doğrulama: toast -> row gone -> count decrease
    boolean ok = waitDeletedToast(2)
    if (!ok && hrefPart != null) ok = waitRowGoneByHrefContains(hrefPart, 10)
    if (!ok) ok = waitCountDecrease(selUnion, before, 5)

    if (!ok) {
      WebUI.comment("⚠️ Silme sonrası beklenen azalma/row removal görülmedi (UI gecikmesi olabilir). Devam ediyorum.")
    } else {
      deleted++
    }
    WebUI.delay(0.2)
  }

  WebUI.comment("🗑️ Silinen kayıt sayısı: ${deleted}")
  return deleted
}

/** Satır doğrulama: App ID, Product ve ikon altı birebir eşit. */
boolean verifyRow(String appIdExact, String productExact, String imgAltExact){
  String rowXp = "//div[contains(@class,'grid-cols-24') and .//*[normalize-space(.)='${appIdExact}']]"
  TestObject row = X(rowXp)
  if(!WebUI.waitForElementPresent(row, 10, FailureHandling.OPTIONAL)) return false

  boolean productOk = WebUI.waitForElementPresent(
      X("${rowXp}//*[normalize-space(.)='${productExact}']"),
      5, FailureHandling.OPTIONAL)

  boolean appOk = WebUI.waitForElementPresent(
      X("${rowXp}//*[normalize-space(.)='${appIdExact}']"),
      5, FailureHandling.OPTIONAL)

  boolean imgOk  = WebUI.waitForElementPresent(
      X("${rowXp}//img[@alt='${imgAltExact}']"),
      5, FailureHandling.OPTIONAL)

  return productOk && appOk && imgOk
}

/** Ürün seçimi: tam isimle; mümkünse açılırdaki opsiyona tıkla, yoksa input’a yazıp Enter. */
void pickProduct(String productName) {
  // Aç
  TestObject trigger = X(
    "(//label[normalize-space(.)='Product :'])[last()]/following::*[(self::button or @role='combobox' or contains(@class,'ant-select'))][1]" +
    " | //button[.//span[normalize-space(.)='Select a product'] or normalize-space(.)='Select a product']" +
    " | //*[@role='combobox' and (normalize-space(.)='Select a product' or .//text()[contains(.,'Select a product')])]"
  )
  WebUI.waitForElementClickable(trigger, 8)
  clickFast(trigger)

  // Dropdown kökü
  TestObject dropRoot = X("(//*[contains(@class,'ant-select-dropdown') or @role='listbox' or @data-state='open'])[last()]")
  WebUI.waitForElementPresent(dropRoot, 5, FailureHandling.OPTIONAL)

  // 1) Tam metinle opsiyonu tıkla
  TestObject optionExact = X(
    "(//*[contains(@class,'ant-select-dropdown') or @role='listbox' or @data-state='open'])[last()]//*[normalize-space(.)='${productName}']"
  )
  if (WebUI.waitForElementClickable(optionExact, 3, FailureHandling.OPTIONAL)) {
    clickFast(optionExact)
  } else {
    // 2) İç input varsa yaz → Enter
    TestObject innerInput = X("(//*[contains(@class,'ant-select-dropdown') or @role='listbox' or @data-state='open'])[last()]//input[not(@type='hidden')]")
    if (WebUI.waitForElementVisible(innerInput, 2, FailureHandling.OPTIONAL)) {
      WebUI.setText(innerInput, productName)
      WebUI.delay(0.1)
      WebUI.sendKeys(innerInput, Keys.chord(Keys.ENTER))
    } else {
      // 3) Son çare: body'ye TAM isim + Enter
      WebUI.sendKeys(X("//body"), productName)
      WebUI.delay(0.1)
      WebUI.sendKeys(X("//body"), Keys.chord(Keys.ENTER))
    }
  }

  // Açılır menüyü kapat ve seçim doğrulaması
  closeSelectOnly()
  TestObject chosen = X(
    "(//label[normalize-space(.)='Product :'])[last()]/following::*[(self::button or @role='combobox' or contains(@class,'ant-select'))][1]//*[normalize-space(.)='${productName}']"
  )
  boolean selectedOk = WebUI.waitForElementPresent(chosen, 3, FailureHandling.OPTIONAL)
  if (!selectedOk) {
    WebUI.comment("⚠️ Product seçimi onaylanamadı, tekrar deniyorum: ${productName}")
    clickFast(trigger)
    if (WebUI.waitForElementClickable(optionExact, 3, FailureHandling.OPTIONAL)) clickFast(optionExact)
    closeSelectOnly()
    WebUI.verifyEqual(WebUI.waitForElementPresent(chosen, 3, FailureHandling.OPTIONAL), true)
  }
}

/** Drawer içinde Com Name yaz + PREVIEW + ikon’u doğrula (hepsinde SCROLL öncelik) */
void previewAndCheckIcon(String appId, String expectedAlt){
  closeSelectOnly()

  String drawer = "(//*[contains(@class,'ant-drawer') and contains(@class,'open')] | //*[@role='dialog' and not(@aria-hidden='true')] | //*[@data-state='open'])[last()]"
  String appInputXp ="//input[@name='app_id']"
  TestObject appInput = X(appInputXp)

  boolean typed = false
  if (WebUI.waitForElementClickable(appInput, 3, FailureHandling.OPTIONAL)) {
    try {
      clickFast(appInput)
      WebUI.sendKeys(appInput, Keys.chord(Keys.CONTROL, "a"))
      WebUI.sendKeys(appInput, appId)
      typed = true
    } catch(ignore){}
  }
  if (!typed) {
    try {
      WebElement we2 = WebUiCommonHelper.findWebElement(appInput, 2)
      js().executeScript("""
        arguments[0].removeAttribute('readonly');
        arguments[0].removeAttribute('disabled');
        arguments[0].focus();
        arguments[0].value = arguments[1];
        arguments[0].dispatchEvent(new Event('input',{bubbles:true}));
        arguments[0].dispatchEvent(new Event('change',{bubbles:true}));
      """, we2, appId)
      typed = true
    } catch(ignore){}
  }
  if (!typed) KeywordUtil.markFailedAndStop("Com Name alanı bulunamadı/yazılamadı.")

  // PREVIEW → önce scroll, sonra tık
  String previewXp = "//button[normalize-space(.)='PREVIEW' or normalize-space(.)='Preview']"
  scrollIntoViewXp(previewXp)
  TestObject previewBtn = X(previewXp)
  WebUI.waitForElementClickable(previewBtn, 5, FailureHandling.OPTIONAL)
  clickFast(previewBtn)
  WebUI.delay(0.3)

  // ikon → önce scroll, sonra görünürlük
  String imgXp = "${drawer}//img[@alt='${expectedAlt}']"
  WebUI.waitForElementPresent(X(imgXp), 4, FailureHandling.OPTIONAL)
  scrollIntoViewXp(imgXp)
  WebUI.waitForElementVisible(X(imgXp), 2, FailureHandling.OPTIONAL)
}

/** Create akışı (ESC yok) */
void createAsset(String productName, String appId, String expectedAlt){
  TestObject btnCreate = X("//button[.//text()[contains(.,'Create Mobile Asset')]]")
  WebUI.waitForElementClickable(btnCreate, 10, FailureHandling.OPTIONAL)
  clickFast(btnCreate)

  pickProduct(productName)
  previewAndCheckIcon(appId, expectedAlt)

  String drawer = "(//*[contains(@class,'ant-drawer') and contains(@class,'open')] | //*[@role='dialog' and not(@aria-hidden='true')] | //*[@data-state='open'])[last()]"
  String createXp = "${drawer}//button[normalize-space(.)='CREATE' or normalize-space(.)='Create']"
  scrollIntoViewXp(createXp)
  clickFast(X(createXp))
}

/** create → toast (birebir/contains) → kapat → satır doğrula */
void createAssetAndVerify(String productName, String appId, String expectedAlt){
  createAsset(productName, appId, expectedAlt)

  boolean toastOk = toastExact("Mobile asset created successfully", 8)
  if (!toastOk) {
    // bazı ortamlarda farklı casing/mesaj olabiliyor → contains fallback
    toastOk = waitToastContains("created", 6)
  }
  if (!toastOk) {
    KeywordUtil.markWarning("Başarı bildirimi görünmedi; satır varlığıyla doğrulanacak.")
  }

  closeModalsFast(3)
  boolean rowOk = verifyRow(appId, productName, expectedAlt)
  WebUI.verifyEqual(rowOk, true)
}

/**************** test flow ****************/
/*/
WebUI.openBrowser('')
WebUI.navigateToUrl("https://platform.catchprobe.org")
WebUI.maximizeWindow()

WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'), 30)
clickFast(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'))

WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 30)
WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 'katalon.test@catchprobe.com')
WebUI.setEncryptedText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Password_password'), 'RigbBhfdqOBDK95asqKeHw==')
clickFast(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Sign in'))

WebUI.delay(2)
WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'), 30)
String randomOtp = (100000 + new Random().nextInt(900000)).toString()
WebUI.delay(2)
WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'), randomOtp)
clickFast(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Verify'))

WebUI.delay(5)
WebUI.waitForPageLoad(15)
WebUI.waitForElementClickable(X("//span[text()='Threat']"), 10, FailureHandling.OPTIONAL)
/*/

WebUI.navigateToUrl('https://platform.catchprobe.org/riskroute/mobile-asset-list')
waitUiIdle(2)


/* 1) Baştaki tüm çöpleri temizle */
deleteAllTrash()

/* 2) Ekle → doğrula */
createAssetAndVerify('Google Play Store', 'com.whatsapp', 'WhatsApp Messenger')
createAssetAndVerify('Xiaomi',            'com.whatsapp', 'WhatsApp')
createAssetAndVerify('Xiaomi',            'com.turkcell.bip', 'BiP - Messenger, Video Call')
createAssetAndVerify('Apple Store',       'net.whatsapp.WhatsApp', 'WhatsApp Messenger')

WebUI.comment('✅ Mobile Asset: çöpler temizlendi, kayıtlar oluşturuldu ve doğrulandı.')
