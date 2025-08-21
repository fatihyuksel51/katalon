import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.util.List


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

void clickFast(TestObject to){
  try { WebUI.click(to) } catch(Exception ignore){
    try { WebElement el = WebUiCommonHelper.findWebElement(to, 3); js().executeScript("arguments[0].click();", el) } catch(ignore2){}
  }
}
boolean waitDeletedToast(int t = 3) {
	TestObject anyToast = X(
	  "//*[@class[contains(.,'Toastify__toast-body')] and contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'deleted')]" +
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
/** Listede görünen TÜM çöp ikonlarını ardışık sil.
 *  Her silme için: Delete modalı → onay → toast/row-gone bekle.
 *  Dönen değer: silinen kayıt adedi.
 */
int deleteAllTrash(int maxRounds = 12) {
  TestObject anyDelete = X("//a[contains(@href,'/riskroute/mobile-asset-list/') and contains(@href,'/delete')]")
  int deleted = 0

  for (int round = 0; round < maxRounds; round++) {
	List<WebElement> btns
	try { btns = WebUiCommonHelper.findWebElements(anyDelete, 2) } catch (ignore) { btns = [] }
	if (btns == null || btns.isEmpty()) break

	// İlk görünen delete linkinin href’ini al (row-gone beklemek için)
	WebElement delEl = btns.get(0)
	String delHref = null
	try { delHref = delEl.getAttribute("href") } catch(ignore) {}
	String hrefPart = null
	if (delHref != null) {
	  int i = delHref.indexOf("mobile-asset-list/")
	  if (i >= 0) {
		int j = delHref.indexOf("/", i + "mobile-asset-list/".length())
		hrefPart = (j > 0) ? delHref.substring(i, j) : delHref.substring(i)
	  }
	}

	// Tıkla → Delete onayı
	try { delEl.click() } catch(ignore) { WebUI.click(anyDelete) }

	TestObject confirmDel = X("//button[normalize-space(.)='Delete']")
	if (WebUI.waitForElementClickable(confirmDel, 4, FailureHandling.OPTIONAL)) {
	  try { WebUI.click(confirmDel) } catch(ignore) {}
	}

	// Modal kapat ve UI’nin idle olmasını bekle
	closeModalsFast(3)
	waitUiIdle(1)

	// Toast ya da row’un DOM’dan tamamen kalkması
	boolean ok = waitDeletedToast(2)
	if (!ok) {
	  ok = waitRowGoneByHrefContains(hrefPart, 6)
	}
	if (!ok) {
	  WebUI.comment("⚠️ Silme doğrulanamadı (toast/row-gone gelmedi). Döngüyü sonlandırıyorum.")
	  break
	}
	deleted++
	// bir sonraki delete için minik nefes
	WebUI.delay(0.2)
  }

  WebUI.comment("🗑️ Silinen kayıt sayısı: ${deleted}")
  return deleted
}


void scrollIntoViewXp(String xp) {
  WebElement el = WebUiCommonHelper.findWebElement(X(xp), 10)
  js().executeScript("arguments[0].scrollIntoView({block:'center'});", el)
}

boolean toastExact(String text, int t=6){
  TestObject toast=X("//*[normalize-space(text())='${text}']")
  return WebUI.waitForElementPresent(toast, t, FailureHandling.OPTIONAL)
}

/** Silinen satır linkinin DOM’dan kalktığını bekle */
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

/** Satır doğrulama: App ID (eşit), Product (eşit) ve ikon alt (eşit) */
boolean verifyRow(String appIdExact, String productExact, String imgAltExact){
  // Satırı App ID’ye göre bul (eşitlik)
  String rowXp = "//div[contains(@class,'grid-cols-24') and .//*[normalize-space(.)='${appIdExact}']]"
  TestObject row = X(rowXp)
  if(!WebUI.waitForElementPresent(row, 10, FailureHandling.OPTIONAL)) return false

  // Product hücresi: eşitlik
  boolean productOk = WebUI.waitForElementPresent(
      X("${rowXp}//*[normalize-space(.)='${productExact}']"),
      5, FailureHandling.OPTIONAL)

  // App ID hücresi: eşitlik (rowXp zaten appId içeriyor ama yine de açıkça doğrulayalım)
  boolean appOk = WebUI.waitForElementPresent(
      X("${rowXp}//*[normalize-space(.)='${appIdExact}']"),
      5, FailureHandling.OPTIONAL)

  // İkon alt metni: eşitlik
  boolean imgOk  = WebUI.waitForElementPresent(
      X("${rowXp}//img[@alt='${imgAltExact}']"),
      5, FailureHandling.OPTIONAL)

  return productOk && appOk && imgOk
}


void pickProduct(String productName) {
  // Aç
  TestObject trigger = X(
    "(//label[normalize-space(.)='Product :'])[last()]/following::*[(self::button or @role='combobox' or contains(@class,'ant-select'))][1]"
    + " | //button[.//span[normalize-space(.)='Select a product'] or normalize-space(.)='Select a product']"
    + " | //*[@role='combobox' and (normalize-space(.)='Select a product' or .//text()[contains(.,'Select a product')])]"
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
    WebUI.click(optionExact)
  } else {
    // 2) İç input varsa tam adını yaz → Enter
    TestObject innerInput = X("(//*[contains(@class,'ant-select-dropdown') or @role='listbox' or @data-state='open'])[last()]//input[not(@type='hidden')]")
    if (WebUI.waitForElementVisible(innerInput, 2, FailureHandling.OPTIONAL)) {
      WebUI.setText(innerInput, productName)
      WebUI.delay(0.1)
      WebUI.sendKeys(innerInput, Keys.chord(Keys.ENTER))
    } else {
      // 3) Son çare: body'ye TAM isim + Enter (tek harf yok!)
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
    if (WebUI.waitForElementClickable(optionExact, 3, FailureHandling.OPTIONAL)) WebUI.click(optionExact)
    closeSelectOnly()
    WebUI.verifyEqual(WebUI.waitForElementPresent(chosen, 3, FailureHandling.OPTIONAL), true)
  }
}


/** Drawer içindeki Com Name’ı etikete göre bul, yaz ve PREVIEW */
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

// 1) PREVIEW butonuna ÖNCE SCROLL, sonra tıkla
String previewXp = "//button[normalize-space(.)='PREVIEW' or normalize-space(.)='Preview']"
scrollIntoViewXp(previewXp)                                  // <-- SCROLL ÖNCELİK
TestObject previewBtn = X(previewXp)
WebUI.waitForElementClickable(previewBtn, 5, FailureHandling.OPTIONAL)
clickFast(previewBtn)
WebUI.delay(0.3)


// 2) İkon görseline ÖNCE SCROLL, sonra görünürlüğünü/doğruluğunu bekle
String imgXp = "${drawer}//img[@alt='${expectedAlt}']"
WebUI.waitForElementPresent(X(imgXp), 4, FailureHandling.OPTIONAL)
scrollIntoViewXp(imgXp)                                      // <-- SCROLL ÖNCELİK
WebUI.waitForElementVisible(X(imgXp), 2, FailureHandling.OPTIONAL)
}

/** Create akışı (ESC yok) */
void createAsset(String productName, String appId, String expectedAlt){
  clickFast(X("//button[.//text()[contains(.,'Create Mobile Asset')]]"))
  pickProduct(productName)
  previewAndCheckIcon(appId, expectedAlt)

  String drawer = "(//*[contains(@class,'ant-drawer') and contains(@class,'open')] | //*[@role='dialog' and not(@aria-hidden='true')] | //*[@data-state='open'])[last()]"
  String createXp = "${drawer}//button[normalize-space(.)='CREATE' or normalize-space(.)='Create']"
  scrollIntoViewXp(createXp)
  clickFast(X(createXp))
}

/** Yukarıdaki create sonrası: toast bekle → drawer’ı kapat → satırı doğrula */
void createAssetAndVerify(String productName, String appId, String expectedAlt){
  createAsset(productName, appId, expectedAlt)

  WebUI.verifyEqual(toastExact("Mobile asset created successfully", 8), true)
  closeModalsFast(3)

  // satır doğrulaması (appId + ürün + ikon alt)
  WebUI.verifyEqual(verifyRow(appId, productName, expectedAlt), true)
}

/** Liste sayfasında tek bir silme: ilk delete linki varsa */
void deleteOneIfTrashExists(){
  TestObject anyDelete = X("//a[contains(@href,'/riskroute/mobile-asset-list/') and contains(@href,'/delete')]")
  if (!WebUI.waitForElementPresent(anyDelete, 3, FailureHandling.OPTIONAL)) return

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
  if (WebUI.waitForElementClickable(confirmDel, 4, FailureHandling.OPTIONAL)) clickFast(confirmDel)

  closeModalsFast(3)                 // modal kapansın
  waitUiIdle(1)

  // satırın kalktığını ya da toast’ı bekle
  boolean gone = waitRowGoneByHrefContains(hrefPart, 6)
  if (!gone) WebUI.comment("⚠️ Silinen satır görünür olabilir (UI yavaş), devam ediyorum.")
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
WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'), randomOtp)
clickFast(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Verify'))

WebUI.delay(5)
WebUI.waitForPageLoad(15)
WebUI.waitForElementClickable(X("//span[text()='Threat']"), 10, FailureHandling.OPTIONAL)
/*/

WebUI.navigateToUrl('https://platform.catchprobe.org/riskroute/mobile-asset-list')
waitUiIdle(2)

/* 1) Başta tek silme (çöp varsa) */
deleteAllTrash()

/* 2) Ekle → toast → ESC/Close → satır doğrula */

createAssetAndVerify('Google Play Store', 'com.whatsapp', 'WhatsApp Messenger')
createAssetAndVerify('Xiaomi', 'com.whatsapp', 'WhatsApp')
createAssetAndVerify('Xiaomi', 'com.turkcell.bip', 'BiP - Messenger, Video Call')
createAssetAndVerify('Apple Store', 'net.whatsapp.WhatsApp', 'WhatsApp Messenger')


WebUI.comment('✅ Mobile Asset: varsa ilk kaydı sildi, yeni kaydı oluşturdu ve doğruladı.')
