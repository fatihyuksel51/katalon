import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import com.kms.katalon.core.model.FailureHandling
import org.openqa.selenium.*
import org.openqa.selenium.interactions.Actions
import java.util.Random
import java.util.List
import java.util.Map
import groovy.transform.Field
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

/******************** Helpers ********************/
TestObject xp(String x){ def to=new TestObject(); to.addProperty("xpath", ConditionType.EQUALS, x); return to }
TestObject X(String x){ return xp(x) }

void jsClick(WebElement el){ ((JavascriptExecutor)DriverFactory.getWebDriver()).executeScript("arguments[0].click()", el) }
void pressEsc(){ new Actions(DriverFactory.getWebDriver()).sendKeys(Keys.ESCAPE).perform() }
List<WebElement> $$(String x, int t=10){ return WebUiCommonHelper.findWebElements(xp(x), t) }
WebElement $(String x, int t=10){ return WebUiCommonHelper.findWebElement(xp(x), t) }

void waitToast(String text, int sec=8){ WebUI.waitForElementVisible(xp("//*[normalize-space(.)='${text}']"), sec) }
void waitGoneOverlay(){ waitOverlayGone(10) }

void clickTO(TestObject to){
  WebElement el = WebUiCommonHelper.findWebElement(to, 10)
  try{ el.click() }catch(Exception e){ jsClick(el) }
}
void safeScrollTo(TestObject to, int timeout=10){
  WebElement el = WebUiCommonHelper.findWebElement(to, timeout)
  ((JavascriptExecutor)DriverFactory.getWebDriver()).executeScript("arguments[0].scrollIntoView({block:'center'});", el)
  WebUI.delay(0.3)
}
void waitOverlayGone(int timeout=10){
  String overlayXp = "//div[@data-state='open' and contains(@class,'fixed') and contains(@class,'inset-0')]"
  WebUI.waitForElementNotPresent(xp(overlayXp), timeout, FailureHandling.OPTIONAL)
}

/******************** Sabitler ********************/
@Field final String PAGE_URL            = "https://platform.catchprobe.org/darkmap/blackcard"

// ESKİ: grid-cols-24'a bağımlıydı; YENİ: satırı switch/göz ikonu ile tanımlıyoruz (responsive güvenli)
@Field final String ROW_XP              = "//div[contains(@class,'group') and contains(@class,'bg-card')][.//button[@role='switch'] or .//*[contains(@class,'lucide-eye')]]"

@Field final String ROW_EYE_XP          = ".//a[.//*[contains(@class,'lucide-eye')]]"
@Field final String ROW_SWITCH_XP       = ".//button[@role='switch']"
@Field final String LIST_SWITCH_ON_XP   = "//button[@role='switch' and @data-state='checked']"
@Field final String LIST_SWITCH_OFF_XP  = "//button[@role='switch' and @data-state='unchecked']"
@Field final String SEARCH_BTN_XP       = "//button[normalize-space(.)='SEARCH']"
@Field final String BIN_INP_XP          = "//input[@placeholder='Bin Code']"
@Field final String PAN_INP_XP          = "//input[@placeholder='PAN']"
@Field final String SEEN_SELECT_XP      = "//span[normalize-space(.)='Select Is Seen']"
@Field final String OPT_YES_XP          = "//*[normalize-space(.)='Yes']"
@Field final String EXPORT_CSV_BTN_XP   = "//button[normalize-space(.)='Export as CSV']"

@Field final String FORM_IS_SEEN_VAL    = "//span[normalize-space(.)='Is Seen']/following-sibling::*[1]"
@Field final String FORM_PAN_VAL        = "//span[normalize-space(.)='PAN']/following-sibling::span[1]"
@Field final String FORM_BIN_VAL        = "//span[normalize-space(.)='Bin Code']/following-sibling::span[1]"

@Field final String TOAST_SEEN_OK       = "Black Card seen status updated successfully"
@Field final String TOAST_EXPORT_OK     = "Black Card exported successfully"

// Boş sonuç mesajı (destructive temalı kırmızı uyarı kutusu)
@Field final String EMPTY_MSG_XP        = "//div[contains(@class,'col-span-12') and contains(@class,'text-destructive')]"

/******************** Drawer hızlı okuma ********************/
String readIsSeenSmart(int timeoutMs=4000){
  long end = System.currentTimeMillis()+timeoutMs
  while(System.currentTimeMillis()<end){
    try{
      if(WebUI.waitForElementPresent(xp(FORM_IS_SEEN_VAL),1,FailureHandling.OPTIONAL)){
        String v = WebUI.getText(xp(FORM_IS_SEEN_VAL))?.trim()
        if(v) return v
      }
    }catch(ignore){}
    WebUI.delay(0.1)
  }
  try{ return $(FORM_IS_SEEN_VAL,3).getText()?.trim() }catch(e){ return "" }
}

/******************** Yardımcı Metodlar ********************/
WebElement firstRow(){ return $("(${ROW_XP})[1]", 10) }

void closeAllSeenOnPage(){
  int guard=0
  while(true){
    List<WebElement> onList = $$(LIST_SWITCH_ON_XP, 5)
    if(onList.isEmpty()) break
    for(WebElement sw: onList){
      try{ ((JavascriptExecutor)DriverFactory.getWebDriver()).executeScript("arguments[0].scrollIntoView({block:'center'});", sw) }catch(ignore){}
      try{ sw.click() }catch(e){ jsClick(sw) }
      WebUI.delay(0.25)
    }
    if(++guard>10) break
  }
}

Map getPanBinFromForm(){
  String pan = $(FORM_PAN_VAL,10).getText().trim()
  String bin = $(FORM_BIN_VAL,10).getText().trim()
  return [pan:pan, bin:bin]
}

/** Arama sonrası liste durumunun kararlı olmasını bekleyen yardımcı. */
void clickSearchAndWait(){
  clickTO(xp(SEARCH_BTN_XP))
  waitGoneOverlay()

  long end = System.currentTimeMillis()+12000
  int stableHits = 0
  while(System.currentTimeMillis()<end){
    List<WebElement> rows = $$(ROW_XP, 2)
    boolean noData = WebUI.waitForElementPresent(xp(EMPTY_MSG_XP),1,FailureHandling.OPTIONAL)

    // iki ardışık aynı durum (ya 0+noData ya da >=1 ve noData yok)
    if( (noData && rows.isEmpty()) || (!noData && !rows.isEmpty()) ){
      stableHits++
      if(stableHits>=2) return
    }else{
      stableHits=0
    }
    WebUI.delay(0.3)
  }

  // Hâlâ “flaky” ise bir kez daha dene
  clickTO(xp(SEARCH_BTN_XP))
  waitGoneOverlay()
  WebUI.delay(0.7)
}

/** Tek satırı sabit şekilde yakalamak için dayanıklı okuma. */
WebElement expectSingleRow(int timeoutSec=15){
  long end = System.currentTimeMillis()+timeoutSec*1000L

  try{ ((JavascriptExecutor)DriverFactory.getWebDriver()).executeScript("window.scrollTo(0,0)") }catch(ignore){}
  while(System.currentTimeMillis()<end){
    List<WebElement> rows = $$(ROW_XP,2)
    if(rows.size()==1){
      WebElement r = rows.get(0)
      try{ ((JavascriptExecutor)DriverFactory.getWebDriver()).executeScript("arguments[0].scrollIntoView({block:'center'});", r) }catch(ignore){}
      WebUI.delay(0.2)
      if($$(ROW_XP,2).size()==1) return r   // iki ardışık teyit
    }
    if(WebUI.waitForElementPresent(xp(EMPTY_MSG_XP),1,FailureHandling.OPTIONAL)){
      break
    }
    WebUI.delay(0.3)
  }
  KeywordUtil.markFailedAndStop("Tek satır bekleniyordu, ancak kararlı şekilde bulunamadı.")
  return null
}

/** Sayfadaki akışı (detay → arama → toggle → export → geri) koşturur. */
void runFlowOnCurrentPage(){
  waitGoneOverlay()
  closeAllSeenOnPage()

  // 1) İlk satır detay
  WebElement row1 = firstRow()
  WebElement eyeInRow = row1.findElement(By.xpath(ROW_EYE_XP))
  safeScrollTo(xp(ROW_XP),5)
  try{ eyeInRow.click() }catch(e){ jsClick(eyeInRow) }

  String seenVal0 = readIsSeenSmart(4000)
  assert seenVal0.equalsIgnoreCase("No") : "Detay formunda Is Seen 'No' bekleniyordu, bulunan: ${seenVal0}"

  def pb = getPanBinFromForm()
  KeywordUtil.logInfo("PAN=${pb.pan} | BIN=${pb.bin}")

  pressEsc(); WebUI.delay(0.3); waitGoneOverlay()

  // 2) Arama
  WebUI.setText(xp(BIN_INP_XP), pb.bin)
  WebUI.setText(xp(PAN_INP_XP), pb.pan)
  clickSearchAndWait()
  WebElement row = expectSingleRow()

  // 3) Is Seen -> checked
  WebElement sw = row.findElement(By.xpath(ROW_SWITCH_XP))
  if(!"checked".equalsIgnoreCase(sw.getAttribute("data-state"))){
    try{ sw.click() }catch(e){ jsClick(sw) }
  }
  waitToast(TOAST_SEEN_OK,6)

  // 4) Doğrula (Yes)
  WebElement eye = row.findElement(By.xpath(ROW_EYE_XP))
  try{ eye.click() }catch(e){ jsClick(eye) }
  String seenVal1 = readIsSeenSmart(4000)
  assert seenVal1.equalsIgnoreCase("Yes") : "Detay formunda Is Seen 'Yes' bekleniyordu, bulunan: ${seenVal1}"
  pressEsc(); WebUI.delay(0.3)

  // 5) Filter: Yes
  clickTO(xp(SEEN_SELECT_XP)); WebUI.delay(0.2)
  clickTO(xp(OPT_YES_XP));     WebUI.delay(0.2)
  clickSearchAndWait()
  WebElement rowYes = expectSingleRow()

  // 6) Export CSV
  if(WebUI.waitForElementPresent(xp(EXPORT_CSV_BTN_XP),5,FailureHandling.OPTIONAL)){
    clickTO(xp(EXPORT_CSV_BTN_XP))
    waitToast(TOAST_EXPORT_OK,6)
  }else{
    KeywordUtil.logInfo("Export CSV butonu bulunamadı; metin değişmiş olabilir.")
  }

  // 7) Is Seen -> unchecked + doğrula (No)
  WebElement sw2 = rowYes.findElement(By.xpath(ROW_SWITCH_XP))
  if(!"unchecked".equalsIgnoreCase(sw2.getAttribute("data-state"))){
    try{ sw2.click() }catch(e){ jsClick(sw2) }
    waitToast(TOAST_SEEN_OK,6)
  }
  WebElement eye2 = rowYes.findElement(By.xpath(ROW_EYE_XP))
  try{ eye2.click() }catch(e){ jsClick(eye2) }
  String seenVal2 = readIsSeenSmart(4000)
  assert seenVal2.equalsIgnoreCase("No") : "Detay formunda Is Seen 'No' bekleniyordu, bulunan: ${seenVal2}"
  pressEsc(); WebUI.delay(0.3)

  // 8) Filter Yes + Search -> boş mesajı doğrula
  clickSearchAndWait()
  WebUI.waitForElementVisible(xp(EMPTY_MSG_XP),10)
  KeywordUtil.logInfo("✅ 'Black Card list not found' (veya eşdeğeri) mesajı göründü.")
}

/******************** Test Çalıştırma ********************/
/*/
WebUI.openBrowser('')
WebUI.navigateToUrl("https://platform.catchprobe.org/")
WebUI.maximizeWindow()
try{ WebUI.setViewPortSize(1640, 1000) }catch(ignore){}   // TestOps/headless için sabit viewport

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

// Sayfaya git
WebUI.navigateToUrl(PAGE_URL)
WebUI.waitForPageLoad(30)

// Sayfa 1
runFlowOnCurrentPage()

// (opsiyonel) tekrar git
WebUI.navigateToUrl(PAGE_URL)
WebUI.waitForPageLoad(30)

// Sayfa 2 (varsa)
if(WebUI.waitForElementPresent(xp("//a[normalize-space(.)='2' or @aria-label='Page 2']"),5,FailureHandling.OPTIONAL)){
  clickTO(xp("//a[normalize-space(.)='2' or @aria-label='Page 2']"))
  WebUI.delay(1.0)
  waitGoneOverlay()
  runFlowOnCurrentPage()
}else{
  KeywordUtil.logInfo("Sayfa 2 yok; yalnızca 1. sayfada akış çalıştırıldı.")
}
