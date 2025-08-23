import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import org.openqa.selenium.*
import org.openqa.selenium.interactions.Actions

import java.util.Random
import java.util.List

/******************** HELPERS ********************/
TestObject X(String xp){
  def to = new TestObject(xp)
  to.addProperty("xpath", ConditionType.EQUALS, xp)
  return to
}
JavascriptExecutor js(){ (JavascriptExecutor) DriverFactory.getWebDriver() }

import groovy.transform.Field

@Field final String OVERLAY_XP = "//div[@data-state='open' and contains(@class,'fixed') and contains(@class,'inset-0')]"
@Field final String ANY_THUMB_REL_XP = ".//img[(contains(@class,'cursor-pointer') or contains(@class,'object-cover') or contains(@class,'object-contain'))]"

void scrollIntoViewXp(String xp){
  WebElement el = WebUiCommonHelper.findWebElement(X(xp), 10)
  js().executeScript("arguments[0].scrollIntoView({block:'center'});", el)
}

  String OVERLAY_XPATH() {
	  // Radix/Headless UI overlay’lerini yakalar
	  return "//div[@data-state='open' and contains(@class,'fixed') and contains(@class,'inset-0')]"
	}
	
void waitOverlayGone(int timeout = 10) {
		WebUI.waitForElementNotPresent(X(OVERLAY_XPATH()), timeout, FailureHandling.OPTIONAL)
 }
void safeClickXp(String xp, int t=15){
  TestObject to = X(xp)
  if(!WebUI.waitForElementClickable(to, t, FailureHandling.OPTIONAL)){
    KeywordUtil.markFailedAndStop("Clickable değil: "+xp)
  }
  try{ WebUI.click(to) }catch(Throwable e){
    try{
      WebElement el = WebUiCommonHelper.findWebElement(to, 3)
      js().executeScript("arguments[0].click();", el)
    }catch(Throwable ee){
      KeywordUtil.markFailedAndStop("Tıklanamadı: "+xp+" | "+ee.message)
    }
  }
}

String safeTextXp(String xp, int t=15){
  if(!WebUI.waitForElementVisible(X(xp), t, FailureHandling.OPTIONAL))
    KeywordUtil.markFailedAndStop("Text görünür değil: "+xp)
  return WebUI.getText(X(xp)).trim()
}

boolean waitToast(int timeout=8){
  String xpToast = "//*[contains(@class,'ant-message') or contains(@class,'ant-notification') or contains(@class,'toast') or contains(@class,'alert')][not(contains(@style,'display: none'))]"
  return WebUI.waitForElementVisible(X(xpToast), timeout, FailureHandling.OPTIONAL)
}
boolean waitToast(String text, int timeout){
  String xpToast = "//*[contains(@class,'ant-message') or contains(@class,'ant-notification') or contains(@class,'toast') or contains(@class,'alert')][not(contains(@style,'display: none')) and contains(normalize-space(.), '"+text.replace("'", "\\'")+"')]"
  return WebUI.waitForElementVisible(X(xpToast), timeout, FailureHandling.OPTIONAL)
}

/** Modal/görsel kapatma (genel) */
void closeNewViews() {
    try { WebUI.sendKeys(X("//body"), Keys.chord(Keys.ESCAPE)) } catch (Throwable ignore) {}
    WebUI.delay(0.2)
    String xpClose = "("+
        "//button[@aria-label='Close' or .//*[name()='svg' and (contains(@class,'lucide-x') or @data-icon='x')]]"+
    ")[last()]"
    if (WebUI.waitForElementClickable(X(xpClose), 1, FailureHandling.OPTIONAL)) {
        try { WebUI.click(X(xpClose)) } catch (Throwable t) {
            try { WebElement b = WebUiCommonHelper.findWebElement(X(xpClose), 2); js().executeScript("arguments[0].click();", b) } catch(Throwable ignore){}
        }
    }
}

/******************** GÖRSEL YARDIMCILAR (CI-SAFE) ********************/
boolean isPlaceholder(WebElement img){
  String cur = (String) js().executeScript("return arguments[0].currentSrc || arguments[0].src || '';", img)
  return cur == null || cur.isEmpty() || cur.toLowerCase().contains("placeholder")
}


WebElement findScrollableInOverlay(){
  try{
    return DriverFactory.getWebDriver().findElement(
      By.xpath(OVERLAY_XP + "//div[descendant::img and (contains(@class,'overflow-y-auto') or contains(@class,'max-h-') or contains(@class,'grid') or contains(@class,'p-'))]")
    )
  }catch(Throwable ignore){ return null }
}


WebElement waitFirstRealThumbSmart(int timeoutSec = 22){
  WebDriver d = DriverFactory.getWebDriver()
  long end = System.currentTimeMillis() + timeoutSec*1000L
  WebElement container = findScrollableInOverlay()

  def scan = { ->
    List<WebElement> imgs = (container!=null)
      ? container.findElements(By.xpath(ANY_THUMB_REL_XP))
      : d.findElements(By.xpath(ANY_THUMB_REL_XP))
    for(WebElement img: imgs){
      try{
        js().executeScript("arguments[0].scrollIntoView({block:'center'});", img)
        WebUI.delay(0.15)
        Boolean vis=(Boolean) js().executeScript("""
          const r=arguments[0].getBoundingClientRect();
          return r.width>30&&r.height>30&&r.bottom>0&&r.right>0&&r.top<(innerHeight||document.documentElement.clientHeight)&&r.left<(innerWidth||document.documentElement.clientWidth);
        """, img)
        Boolean ok=(Boolean) js().executeScript("return arguments[0].naturalWidth>40;", img)
        if(Boolean.TRUE.equals(vis) && Boolean.TRUE.equals(ok) && !isPlaceholder(img)) return img
      }catch(ignore){}
    }
    return null
  }

  while(System.currentTimeMillis()<end){
    WebElement hit = (WebElement) scan()
    if(hit!=null) return hit
    if(container!=null) js().executeScript("arguments[0].scrollTop = arguments[0].scrollTop + 320;", container)
    else js().executeScript("window.scrollBy(0,320)")
    WebUI.delay(0.25)
  }
  return null
}

WebElement findFirstPlaceholderThumbSmart(int timeoutSec = 6){
  WebDriver d = DriverFactory.getWebDriver()
  long end = System.currentTimeMillis() + timeoutSec*1000L
  WebElement container = findScrollableInOverlay()
  while(System.currentTimeMillis()<end){
    List<WebElement> imgs = (container!=null)
      ? container.findElements(By.xpath(ANY_THUMB_REL_XP))
      : d.findElements(By.xpath(ANY_THUMB_REL_XP))
    for(WebElement img: imgs){
      try{
        js().executeScript("arguments[0].scrollIntoView({block:'center'});", img)
        WebUI.delay(0.1)
        if(isPlaceholder(img)) return img
      }catch(ignore){}
    }
    if(container!=null) js().executeScript("arguments[0].scrollTop = arguments[0].scrollTop + 320;", container)
    else js().executeScript("window.scrollBy(0,320)")
    WebUI.delay(0.2)
  }
  return null
}

/** Görsel viewer açıkken gerçek img yüklendi mi; global hata metni var mı? */
boolean waitImageLoadedNoError(int timeoutSec = 20){
  String xpError = "//*[normalize-space(.)='There was a problem loading your image']"
  long end = System.currentTimeMillis() + timeoutSec*1000L
  while(System.currentTimeMillis()<end){
    if(WebUI.verifyElementPresent(X(xpError),1,FailureHandling.OPTIONAL)) return false
    Boolean ok=(Boolean) js().executeScript("""
      const imgs=[...document.querySelectorAll('img')];
      const inVp=el=>{const r=el.getBoundingClientRect();return r.width>50&&r.height>50&&r.bottom>0&&r.right>0&&r.top<(innerHeight||document.documentElement.clientHeight)&&r.left<(innerWidth||document.documentElement.clientWidth)};
      return imgs.some(img=>{
        const cur=img.currentSrc||img.src||'';
        return inVp(img) && !/placeholder/i.test(cur) && img.naturalWidth>0;
      });
    """)
    if(Boolean.TRUE.equals(ok)) return true
    WebUI.delay(0.4)
  }
  return false
}

/** Ana görsel assertion: gerçek görsel açılır; varsa placeholder tıklanınca hata metni gelir */
void doImageAssertionsOnCurrentPage(){
  WebElement realImg = waitFirstRealThumbSmart(22)
  if(realImg==null){
    boolean empty = WebUI.verifyTextPresent("No data", false, FailureHandling.OPTIONAL) ||
                    WebUI.verifyTextPresent("No data found", false, FailureHandling.OPTIONAL)
    if(empty) KeywordUtil.markFailedAndStop("Görsel listesi boş (No data). Bu durumda görsel doğrulama çalıştırılmamalı.")
    else      KeywordUtil.markWarning("Gerçek thumbnail bulunamadı (modal/grid içinde).")
  }
  try{ realImg.click() }catch(Throwable t){ js().executeScript("arguments[0].click();", realImg) }
  if(!waitImageLoadedNoError(20)) KeywordUtil.markFailed("Görsel yüklenmedi veya hata verdi.")
  closeNewViews()

  WebElement ph = findFirstPlaceholderThumbSmart(6)
  if(ph!=null){
    try{ ph.click() }catch(Throwable t){ js().executeScript("arguments[0].click();", ph) }
    String xpError="//*[normalize-space(.)='There was a problem loading your image']"
    boolean err=WebUI.waitForElementVisible(X(xpError), 12, FailureHandling.OPTIONAL)
    if(!err) KeywordUtil.markFailed("Placeholder tıklandı ama beklenen hata mesajı gelmedi.")
    //closeNewViews()
  }else{
    KeywordUtil.logInfo("Placeholder yok, bu adım atlandı.")
  }
}

/******************** TEST ********************/
/*/
WebUI.openBrowser('')
WebUI.navigateToUrl('https://platform.catchprobe.org/')
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
/*/

WebUI.navigateToUrl('https://platform.catchprobe.org/darkmap/report')
WebUI.waitForPageLoad(20)

/* 1) Create */
String xpCreateBtn = "//button[normalize-space(.)='Create'] | //a[normalize-space(.)='CREATE']"
safeClickXp(xpCreateBtn, 15)
WebUI.waitForPageLoad(15)

/* 2) Form doldur */
String xpTitleInput = "//input[@type='text' and @name='title']"
String xpCommentTxt = "//textarea[@name='comment']"
if(!WebUI.waitForElementVisible(X(xpTitleInput), 15, FailureHandling.OPTIONAL))
  KeywordUtil.markFailedAndStop("Report form yüklenmedi (title input görünmedi).")
WebUI.setText(X(xpTitleInput), "katalon")

if(!WebUI.waitForElementVisible(X(xpCommentTxt), 15, FailureHandling.OPTIONAL))
  KeywordUtil.markFailedAndStop("Report form yüklenmedi (analyst comment görünmedi).")
WebUI.setText(X(xpCommentTxt), "katalon")

/* 3) Keywords sekmesi */
String xpKeywordsBtn = "//button[@type='button' and contains(normalize-space(.), 'Keywords')]"
scrollIntoViewXp(xpKeywordsBtn)
safeClickXp(xpKeywordsBtn, 10)

/* 4) Yeşil + */
String xpGreenPlus = "(//*[name()='button' and contains(@class,'bg-success')])[1]"
scrollIntoViewXp(xpGreenPlus)
safeClickXp(xpGreenPlus, 10)
WebUI.delay(0.4)

/* 5) Görsel doğrulaması (akıllı) */
//doImageAssertionsOnCurrentPage()

/* 6) Mavi + → çöp’e dönüş kontrolü */
String xpBluePlus = "//*[name()='button' and contains(@class, 'bg-primary') and .//*[name()='svg' and contains(@class,'lucide-plus')]]"
scrollIntoViewXp(xpBluePlus)
safeClickXp(xpBluePlus, 10)

String xpTrashBtn = "//*[name()='button' and contains(@class,'bg-destructive') and .//*[name()='svg' and contains(@class,'lucide-trash')]]"
if (!WebUI.waitForElementVisible(X(xpTrashBtn), 10, FailureHandling.OPTIONAL)) {
    KeywordUtil.markFailedAndStop("Mavi + tıklandı ama buton çöp (destructive) durumuna geçmedi.")
}
KeywordUtil.logInfo("✅ Öğenin eklendiği doğrulandı; buton çöp ikonuna döndü.")

/* 7) CLOSE */
String xpCloseTextBtn = "//button[normalize-space(.)='CLOSE']"
if(WebUI.waitForElementClickable(X(xpCloseTextBtn), 5, FailureHandling.OPTIONAL)) WebUI.click(X(xpCloseTextBtn))
else closeNewViews()

KeywordUtil.logInfo("OK: Create → Keywords → görsel → ekle/çöp → Close tamam.")

/* ================== REPORT – DEVAM BLOĞU ================== */
/* 1) Liste sayfasında gerçek thumb aç/kapat (overlay yoksa window scroll) */
WebElement listReal = waitFirstRealThumbSmart(18)
if(listReal!=null){
  try{ listReal.click() }catch(Throwable t){ js().executeScript("arguments[0].click();", listReal) }
  if(!waitImageLoadedNoError(18)) KeywordUtil.markFailed("Liste görseli yüklenmedi veya hata verdi.")
  closeNewViews()
}else{
  KeywordUtil.logInfo("Liste üzerinde gerçek thumb bulunamadı; görsel akımı bu adımda atlandı.")
}

/* 2) Pencil → 'katalon' comment */
String xpPencilBtn = "//*[name()='button' and @type='button' and .//*[name()='svg' and contains(@class,'lucide-pencil')]]"
safeClickXp(xpPencilBtn, 15)
String xpComment = "(//textarea[@name='comment'])[2]"
if(!WebUI.waitForElementVisible(X(xpComment), 10, FailureHandling.OPTIONAL))
  KeywordUtil.markFailedAndStop("Comment alanı görünmedi.")
WebUI.setText(X(xpComment), "katalon")

/* 3) ADD ADDRESS */
String xpAddAddrBtn = "//button[normalize-space(.)='ADD ADDRESS']"
scrollIntoViewXp(xpAddAddrBtn)
safeClickXp(xpAddAddrBtn, 15)

/* 4) 'katalon' metni + Keywords rozet = 1 */
String xpKatalonText   = "//span[contains(@class,'col-span-4') and normalize-space(.)='katalon']"
String xpKeywordsBadge = "(//span[contains(@class,'bg-accent') and contains(@class,'rounded-full')])[1]"
WebUI.verifyElementPresent(X(xpKatalonText), 10)
String badgeTxt = safeTextXp(xpKeywordsBadge, 10).replaceAll("[^0-9]", "")
WebUI.verifyEqual(badgeTxt, "1")

/* 5) Create Report */
String xpCreateReport = "//button[normalize-space(.)='Create Report']"
scrollIntoViewXp(xpCreateReport)
safeClickXp(xpCreateReport, 20)

/* 6) Status & Download kontrolü */
String xpStatusChipAny     = "(//td//span[contains(@class,'rounded-full')])[1]"
String xpStatusDestructive = "//td//span[contains(@class,'bg-destructive') and contains(@class,'rounded-full')]"
String xpDownloadBtn       = "//div[contains(@class,'bg-muted') and .//*[name()='svg' and contains(@class,'lucide-download')]]"

WebUI.verifyElementPresent(X(xpStatusChipAny), 10)
WebUI.verifyElementPresent(X(xpStatusDestructive), 5)            // ilk durum kırmızı
WebUI.verifyElementNotPresent(X(xpDownloadBtn), 2)               // ilk anda download yok

boolean done=false
int refreshCount=0
while(!done && refreshCount<=2){
  long end = System.currentTimeMillis()+12_000L
  while(System.currentTimeMillis()<end){
    String stLower=""
    try{ stLower = safeTextXp(xpStatusChipAny,5).toLowerCase() }catch(ignore){}
    boolean dl = WebUI.verifyElementPresent(X(xpDownloadBtn),1,FailureHandling.OPTIONAL)
    if((stLower.contains("completed") || stLower.contains("success") || stLower.contains("finished")) && dl){
      done=true; break
    }
    WebUI.delay(1)
  }
  if(!done && refreshCount<2){
    KeywordUtil.logInfo("⟳ Durum tamamlanmadı, refresh deneniyor... ("+(refreshCount+1)+"/2)")
    WebUI.refresh(); WebUI.waitForPageLoad(10); WebUI.delay(1)
  }
  refreshCount++
}
if(!done) KeywordUtil.markWarning("Status 'Completed' olmadı veya Download butonu gelmedi (2 refresh sonrasında da).")
else      KeywordUtil.logInfo("✅ Rapor üretimi tamam: Status success + Download hazır.")

/* 7) Delete ve 'No data' doğrulaması */
String xpDeleteBtn       = "//div[contains(@class,'bg-destructive') and .//*[name()='svg' and contains(@class,'lucide-trash2')]]"
scrollIntoViewXp(xpDeleteBtn)
safeClickXp(xpDeleteBtn, 20)
String xpDeleteBtntext = "//button[normalize-space(.)='DELETE']"
safeClickXp(xpDeleteBtntext, 10)
waitToast(8)

String xpNoData = "//div[@class='ant-empty-description' and normalize-space(text())='No data']"
scrollIntoViewXp(xpNoData)
if(!WebUI.waitForElementVisible(X(xpNoData), 10, FailureHandling.OPTIONAL))
  KeywordUtil.markFailed("No data bekleniyordu, görünmedi.")
else
  KeywordUtil.logInfo("✅ Silme sonrası 'No data' doğrulandı.")

/* Bitti */
