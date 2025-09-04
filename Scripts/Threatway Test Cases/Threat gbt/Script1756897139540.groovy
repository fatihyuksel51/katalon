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

import groovy.transform.Field
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.interactions.Actions
import java.util.Random

/************** Kısa yardımcılar **************/
TestObject X(String xp){ def to=new TestObject(xp); to.addProperty("xpath",ConditionType.EQUALS,xp); return to }
WebDriver drv(){ DriverFactory.getWebDriver() }
JavascriptExecutor js(){ (JavascriptExecutor) drv() }
WebElement we(TestObject to,int t=10){ WebUiCommonHelper.findWebElement(to,t) }
void pressEsc(){ new Actions(drv()).sendKeys(Keys.ESCAPE).perform() }

boolean isBrowserOpen(){ try{ drv(); return true }catch(Throwable t){ return false } }
/** Başlıktan güvenli APT numarası çıkar (yoksa null) */
Integer extractAptNum(String title) {
	if (title == null) return null
	def m = (title =~ /(?i)\bAPT\s*(\d+)\b/)
	return m.find() ? Integer.parseInt(m.group(1)) : null
}

/** İlk n başlıktan APT numaralarını topla (yalnızca eşleşenleri döndürür) */
List<Integer> aptNumsFromTop(int n = 5) {
	def nums = []
	for (int i = 1; i <= n; i++) {
		TestObject t = X("${titleXpRoot}[${i}]")
		if (WebUI.verifyElementPresent(t, 2, FailureHandling.OPTIONAL)) {
			String txt = WebUI.getText(t) ?: ""
			Integer val = extractAptNum(txt)
			if (val != null) nums << val
		}
	}
	return nums
}

void domReady(int timeoutSec=20){
  long end=System.currentTimeMillis()+timeoutSec*1000
  while(System.currentTimeMillis()<end){
    try{
      String s = js().executeScript("return document.readyState") as String
      if("complete".equalsIgnoreCase(s)) return
    }catch(_){}
    WebUI.delay(0.2)
  }
}

void ensureInView(TestObject to,int t=8){
  WebElement e = we(to, t)
  js().executeScript("arguments[0].scrollIntoView({block:'center', inline:'nearest'})", e)
}

void clickSmartOnce(TestObject to,int t=8){
  WebUI.waitForElementVisible(to, t, FailureHandling.OPTIONAL)
  ensureInView(to, Math.min(t, 4))
  WebElement e = we(to, t)
  try{ e.click(); return }catch(_){}
  try{ new Actions(drv()).moveToElement(e).pause(120).click().perform(); return }catch(_){}
  try{ js().executeScript("arguments[0].click()", e); return }catch(_){}
  WebUI.click(to) // son çare
}
boolean clickSmartRetry(TestObject to, int tries=3, int t=10){
  for(int i=1;i<=tries;i++){
    try{
      WebUI.waitForElementClickable(to, t, FailureHandling.OPTIONAL)
      clickSmartOnce(to,t)
      return true
    }catch(Throwable err){
      WebUI.delay(0.4)
    }
  }
  return false
}

/** hızlı yaz – olmazsa JS */
void clearAndType(TestObject to, String text, int t=10){
  WebUI.waitForElementVisible(to,t,FailureHandling.OPTIONAL)
  ensureInView(to, Math.min(t,4))
  WebElement e = we(to,t)
  try{ e.clear(); e.sendKeys(text); return }catch(_){}
  try{
    js().executeScript("arguments[0].value=''; arguments[0].dispatchEvent(new Event('input',{bubbles:true}))", e)
    js().executeScript("arguments[0].value=arguments[1]; arguments[0].dispatchEvent(new Event('input',{bubbles:true}))", e, text)
  }catch(_){ WebUI.setText(to,text) }
}

/** spinner bitene kadar kısa bekleme */
boolean waitCalm(int sec=8){
  long end = System.currentTimeMillis()+sec*1000
  while(System.currentTimeMillis()<end){
    try{
      boolean any = WebUI.verifyElementPresent(
        X("//*[contains(@class,'spinner') or contains(@class,'loading') or @role='progressbar']"),
        1, FailureHandling.OPTIONAL)
      if(!any) return true
    }catch(_){}
    WebUI.delay(0.2)
  }
  return false
}

/************** Re-usable alanlar (TEK KEZ) **************/
@Field TestObject searchInput = findTestObject('Object Repository/Threat Actor/Threataa/Page_/input_Cybercriminals_flex rounded-md border_26dcbc')
@Field TestObject btnSearch   = X("//button[contains(.,'SEARCH')]")
@Field String     titleXpRoot = "(//div[contains(@class,'flex items-center gap-2 text-xl font-semibold')])"
@Field TestObject firstResult = X("${titleXpRoot}[1]")
@Field TestObject sortBtn     = X("//button[contains(text(),'DESC') or contains(text(),'ASC')]")

/************** Dinamik beklemeler **************/
/** ilk kart başlığı */
String getFirstTitle() {
  return WebUI.verifyElementPresent(firstResult, 2, FailureHandling.OPTIONAL) ? WebUI.getText(firstResult) : ""
}
/** bir elementin text’i değişene kadar bekle */
boolean waitTextChanged(TestObject el, String oldText, int timeout=10){
  long end=System.currentTimeMillis()+timeout*1000
  while(System.currentTimeMillis()<end){
    if(WebUI.verifyElementPresent(el,2,FailureHandling.OPTIONAL)){
      String t = WebUI.getText(el)
      if(t != oldText && t.trim()!="") return true
    }
    WebUI.delay(0.25)
  }
  return false
}

/************** Login/OTP **************/
void ensureSession(){
  if(isBrowserOpen()) return
  WebUI.openBrowser('')
  WebUI.maximizeWindow()
  WebUI.navigateToUrl('https://platform.catchprobe.org/')
  WebUI.waitForElementVisible(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'), 30)
  WebUI.click(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'))

  WebUI.setText(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 'katalon.test@catchprobe.com')
  WebUI.setEncryptedText(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Password_password'), 'RigbBhfdqOBDK95asqKeHw==')
  WebUI.click(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Sign in'))

  WebUI.delay(2) // OTP kutuları gelsin
  String otp = (100000 + new Random().nextInt(900000)).toString()
  WebUI.setText(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'), otp)
  WebUI.click(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Verify'))
  WebUI.delay(2)
  WebUI.waitForElementVisible(X("//span[text()='Threat']"), 10, FailureHandling.OPTIONAL)
}

/************** Uygulama yardımcıları **************/
/** APT filtresi – hızlı/dinamik */
String applyAptFilter(int tries=3){
  for(int i=1;i<=tries;i++){
    String before = getFirstTitle()
    clearAndType(searchInput, "apt")
    WebUI.sendKeys(searchInput, Keys.chord(Keys.ENTER))
    clickSmartRetry(btnSearch, 1)
    waitCalm(3)
    if(waitTextChanged(firstResult, before, 7)){
      String now = getFirstTitle()
      KeywordUtil.logInfo("Search updated (#${i}) -> ${now}")
      return now
    }else{
      KeywordUtil.logInfo("Search didn’t update yet (#${i}), retrying…")
    }
  }
  return getFirstTitle()
}

/** sıralama değişene kadar bekle ve doğrula */

boolean isDesc(List<Integer> a){ for(int i=1;i<a.size();i++) if(a[i] > a[i-1]) return false; true }
boolean isAsc (List<Integer> a){ for(int i=1;i<a.size();i++) if(a[i] < a[i-1]) return false; true }

boolean toggleSortAndWait(String target /* "DESC" or "ASC" */, int timeout=10){
  List<Integer> before = aptNumsFromTop(5)
  clickSmartRetry(sortBtn,1)
  long end = System.currentTimeMillis()+timeout*1000
  while(System.currentTimeMillis()<end){
    waitCalm(1)
    List<Integer> after = aptNumsFromTop(5)
    if(after!=before && after.size()>=2){
      if(target=="DESC" && isDesc(after)) return true
      if(target=="ASC"  && isAsc(after))  return true
    }
    WebUI.delay(0.3)
  }
  return false
}

/** İlk kartı aç – içerik gelmezse ESC ile kapatıp tekrar dene */
boolean openFirstCardRobust(int maxTries=3){
  for(int i=1;i<=maxTries;i++){
	  
    clickSmartRetry(X("${titleXpRoot}[1]"),2)
    // sheet içindeki “Info” tab’ı ve üst şerit görünsün
    TestObject proof = X("//div[contains(@class,'fixed')]//button[contains(.,'Info')]")
    if(WebUI.waitForElementVisible(proof, 5, FailureHandling.OPTIONAL)){
      // Ek olarak, sheet içinde herhangi bir hücre render oldu mu?
      TestObject anyCell = X("//div[contains(@class,'fixed')]//*[contains(.,'Actor Details') or contains(.,'MITRE ATT&CK') or contains(.,'Indicators')]")
      if(WebUI.waitForElementVisible(anyCell, 3, FailureHandling.OPTIONAL)) return true
    }
    // olmadı → ESC ile kapa ve tekrar dene
    pressEsc()
    WebUI.delay(0.5)
	clickSmartRetry(btnSearch, 1)
  }
  return false
}

/************** TEST AKIŞI **************/
ensureSession()

// Threatway -> Threat Actor
WebUI.navigateToUrl('https://platform.catchprobe.org/threatway')
domReady(20); waitCalm(10)
clickSmartRetry(findTestObject('Object Repository/Threat Actor/Threatactorlink'), 3)
domReady(15); waitCalm(4)
WebUI.verifyElementPresent(findTestObject('Object Repository/Threat Actor/Threataa/Page_/div_ascendo'), 2)

/** APT filtresini uygula (dinamik) */
String firstAfterFilter = applyAptFilter(3)
if(!((firstAfterFilter =~ /(?i)APT\s*\d+/).find())){
  KeywordUtil.markWarning("İlk sonuç APT formatında değil: '${firstAfterFilter}'. Devam ediyorum.")
}

/** DESC → ASC hızlı kontrol */
String currentBtn = WebUI.getText(sortBtn)
if(!currentBtn.contains("DESC")) { toggleSortAndWait("DESC", 2) }
List<Integer> descNums = aptNumsFromTop(5); WebUI.verifyEqual(true, isDesc(descNums))

toggleSortAndWait("ASC", 2)
clickSmartRetry(btnSearch, 1)
List<Integer> ascNums = aptNumsFromTop(5);  WebUI.verifyEqual(true, isAsc(ascNums))

/** İlk kartı aç (robust) */
if(!openFirstCardRobust(2)){
  KeywordUtil.markFailed("Kart sheet açılmadı (3 deneme).")
}

/************** MITRE ATT&CK **************/
TestObject tabMitre   = findTestObject('Object Repository/Threat Actor/Threataa/Page_/Mitre Attack')
TestObject proofMitre = X("//div[contains(@class,'cursor-pointer') and contains(.,'MITRE ATT&CK')]")
int attempts = 0
while (attempts < 3 && !WebUI.waitForElementVisible(proofMitre, 2, FailureHandling.OPTIONAL)) {
  clickSmartRetry(tabMitre, 1)
  WebUI.delay(0.5)
  attempts++
}

// sayaç karşılaştır
TestObject toMitreCount = X("//div[contains(@class,'cursor-pointer') and contains(.,'MITRE ATT&CK')]/span")
TestObject toEntCount   = X("//div[contains(@class,'flex items-center') and contains(.,'Enterprise Attacks')]/span")
String mitreCount = WebUI.getText(toMitreCount)
String entCount   = WebUI.getText(toEntCount)
WebUI.verifyMatch(entCount, ".*${mitreCount}.*", true)

// Input Capture → Keylogging
clickSmartRetry(findTestObject('Object Repository/Threat Actor/Thretaa/Page_/Input Capture'),3)
WebUI.waitForElementPresent(findTestObject('Object Repository/Threat Actor/Thretaa/Page_/İnput Capture Keylogging'), 10)
String captureKeyLogTxt = WebUI.getText(findTestObject('Object Repository/Threat Actor/Thretaa/Page_/İnput Capture Keylogging'))
clickSmartRetry(findTestObject('Object Repository/Threat Actor/Thretaa/Page_/İnput Close'),2)
WebUI.waitForElementPresent(findTestObject('Object Repository/Threat Actor/Thretaa/Page_/Keylogging'), 10)
String keyloggingTxt = WebUI.getText(findTestObject('Object Repository/Threat Actor/Thretaa/Page_/Keylogging'))
WebUI.verifyMatch(captureKeyLogTxt, keyloggingTxt, false)
clickSmartRetry(findTestObject('Object Repository/Threat Actor/Thretaa/Page_/Keylogging'),2)
String keyloggingFirst = WebUI.getText(findTestObject('Object Repository/Threat Actor/Thretaa/Page_/Keyloggin First'))
WebUI.verifyMatch(keyloggingFirst, keyloggingTxt, false)
clickSmartRetry(findTestObject('Object Repository/Threat Actor/Thretaa/Page_/İnput Close'),2)

/************** Indicators / Tools / Contacts / Timeline **************/
def openSectionRetry = { TestObject tabBtn, TestObject proof, int tries=3 ->
  for(int i=1;i<=tries;i++){
    clickSmartRetry(tabBtn,1)
    if(WebUI.waitForElementVisible(proof,6,FailureHandling.OPTIONAL)) return true
    WebUI.delay(0.4)
  }
  return false
}

openSectionRetry(findTestObject('Object Repository/Threat Actor/Thretaa/Page_/Indicators'),
                 findTestObject('Object Repository/Threat Actor/Thretaa/Page_/CSV button'),3)

boolean toolsOk = openSectionRetry(findTestObject('Object Repository/Threat Actor/Thretaa/Page_/Tools'),
                                   findTestObject('Object Repository/Threat Actor/Thretaa/Page_/Tolls İnput'),3)
if(toolsOk){
  clearAndType(findTestObject('Object Repository/Threat Actor/Thretaa/Page_/Tolls İnput'), 'Downdelph')
  WebUI.waitForElementVisible(findTestObject('Object Repository/Threat Actor/Thretaa/Page_/Tools Downdelph'), 10)
  String toolName = WebUI.getText(findTestObject('Object Repository/Threat Actor/Thretaa/Page_/Tools Downdelph'))
  WebUI.verifyMatch(toolName, 'Downdelph', false)
  WebUI.waitForElementClickable(findTestObject('Object Repository/Threat Actor/Thretaa/Page_/button_CSV'), 8)
}

openSectionRetry(findTestObject('Object Repository/Threat Actor/Thretaa/Page_/Contacts'),
                 findTestObject('Object Repository/Threat Actor/Thretaa/Page_/button_CSV'),3)

openSectionRetry(findTestObject('Object Repository/Threat Actor/Thretaa/Page_/Timeline'),
                 X("//div[contains(@class,'timeline') or contains(.,'Timeline')]"),3)

// sheet kapat
clickSmartRetry(findTestObject('Object Repository/Threat Actor/Thretaa/Page_/Mitre Close'),2)

KeywordUtil.logInfo("✅ Threat Actor senaryosu tamamlandı.")
