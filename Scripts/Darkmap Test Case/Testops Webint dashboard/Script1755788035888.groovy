import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import org.openqa.selenium.*

/* -------------------- Helpers (CI-safe) -------------------- */
TestObject X(String xp){ def to=new TestObject(xp); to.addProperty("xpath", ConditionType.EQUALS, xp); return to }
JavascriptExecutor js(){ (JavascriptExecutor) DriverFactory.getWebDriver() }
boolean isCI(){ System.getenv('KATALON_AGENT_NAME')!=null || System.getenv('KATALON_TASK_ID')!=null }
int ciT(int local, int ci){ return isCI()? ci : local }

void scrollIntoViewXp(String xp){
  WebElement el = WebUiCommonHelper.findWebElement(X(xp), 5)
  js().executeScript("arguments[0].scrollIntoView({block:'center'});", el)
}

void safeClickXp(String xp, int t=15){
  TestObject to = X(xp)
  if(!WebUI.waitForElementClickable(to, t, FailureHandling.OPTIONAL)){
    // son bir kez görünür alana getirip tekrar dene
    try{ scrollIntoViewXp(xp) }catch(ignore){}
    if(!WebUI.waitForElementClickable(to, 3, FailureHandling.OPTIONAL))
      KeywordUtil.markFailedAndStop("Clickable değil: "+xp)
  }
  try{ WebUI.click(to) }catch(Throwable e){
    try{
      WebElement el = WebUiCommonHelper.findWebElement(to, 3)
      js().executeScript("arguments[0].click()", el)
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

int parseIntSafe(String s){
  if(s==null) return 0
  s = s.replaceAll("[^0-9]","")
  return s.isEmpty()? 0 : Integer.parseInt(s)
}

String switchToNewTab(int waitSec = 6){
  def d = DriverFactory.getWebDriver()
  String original = d.getWindowHandle()
  long end = System.currentTimeMillis()+waitSec*1000L
  while(System.currentTimeMillis()<end && d.getWindowHandles().size()<2){ WebUI.delay(0.2) }
  for(String h: d.getWindowHandles()) if(h!=original){ d.switchTo().window(h); break }
  WebUI.waitForPageLoad(15); return original
}
void closeTabAndBack(String original){
  def d=DriverFactory.getWebDriver(); try{ d.close() }catch(ignore){}
  d.switchTo().window(original); WebUI.waitForPageLoad(10)
}

/* --------- Overlay/sekme kapatma --------- */
void closeNewViews(){
  def d=DriverFactory.getWebDriver()
  String original = d.getWindowHandle()
  if(d.getWindowHandles().size()>1){
    for(String h: d.getWindowHandles()) if(h!=original){ d.switchTo().window(h); d.close() }
    d.switchTo().window(original); WebUI.delay(0.3)
  }
  // ESC → X → overlay click → force-hide
  try{ WebUI.sendKeys(X("//body"), Keys.chord(Keys.ESCAPE)) }catch(ignore){}
  WebUI.delay(0.2)
  String xpOverlay="(//div[@data-state='open' and contains(@class,'fixed') and contains(@class,'inset-0') and contains(@class,'z-50')])[2]"
  if(!WebUI.waitForElementVisible(X(xpOverlay),1,FailureHandling.OPTIONAL)) return
  String xpCloseBtn="(//button[@aria-label='Close' or contains(@class,'top-4') and contains(@class,'right-4')])[last()]"
  try{ if(WebUI.waitForElementPresent(X(xpCloseBtn),2,FailureHandling.OPTIONAL)){ WebElement b=WebUiCommonHelper.findWebElement(X(xpCloseBtn),2); js().executeScript("arguments[0].click()", b) } }catch(ignore){}
  if(!WebUI.verifyElementPresent(X(xpOverlay),1,FailureHandling.OPTIONAL)) return
  try{ WebElement ov=WebUiCommonHelper.findWebElement(X(xpOverlay),2); js().executeScript("arguments[0].click()", ov) }catch(ignore){}
  if(!WebUI.verifyElementPresent(X(xpOverlay),1,FailureHandling.OPTIONAL)) return
  try{
    js().executeScript("document.querySelectorAll(\"div[data-state='open'].fixed.inset-0.z-50\").forEach(el=>{el.style.display='none';el.style.pointerEvents='none'})")
  }catch(ignore){}
}

/* --------- URL/host yardımcıları --------- */
String _trimEllipsis(String s){ if(s==null) return ""; return s.replaceAll("[\\u2026…]+","").trim() }
String hostOfUrlLike(String s){
  s=_trimEllipsis(s); if(s==null) return ""
  try{
    String tmp=s; if(!tmp.matches("^[a-zA-Z][a-zA-Z0-9+.-]*:.*")) tmp="http://"+tmp
    java.net.URL u=new java.net.URL(tmp); String h=u.getHost(); if(h?.toLowerCase()?.startsWith("www.")) h=h.substring(4); return h?: ""
  }catch(Throwable t){
    def m=(s =~ /^(?:https?:\\/\\/)?([^\\/\\?#]+)/); if(m.find()){ String h=m.group(1); if(h?.toLowerCase()?.startsWith("www.")) h=h.substring(4); return h?: "" }
    return ""
  }
}

/* --------- Görsel yardımcıları (CI-robust) --------- */
boolean isPlaceholder(WebElement img){
  String cur = (String) js().executeScript("return arguments[0].currentSrc || arguments[0].src;", img)
  return cur==null || cur.contains("placeholder.webp")
}

/** Sayfadaki ilk GERÇEK (placeholder olmayan) thumb’ı tarar; lazy-load tetikler. */
WebElement waitFirstRealThumb(int timeoutSec = 18){
  def d=DriverFactory.getWebDriver()
  long end=System.currentTimeMillis()+timeoutSec*1000L
  js().executeScript("window.scrollTo(0,0)")
  while(System.currentTimeMillis()<end){
    js().executeScript("window.scrollBy(0,250)")
    java.util.List<WebElement> imgs = d.findElements(By.xpath("//img[contains(@class,'cursor-pointer')]"))
    for(WebElement img: imgs){
      try{
        js().executeScript("arguments[0].scrollIntoView({block:'center'});", img)
        Boolean ok   = (Boolean) js().executeScript("return arguments[0].complete && arguments[0].naturalWidth>40 && !!(arguments[0].currentSrc||arguments[0].src)", img)
        Boolean vis  = (Boolean) js().executeScript("const r=arguments[0].getBoundingClientRect();return r.width>0&&r.height>0;", img)
        if(Boolean.TRUE.equals(ok) && Boolean.TRUE.equals(vis) && !isPlaceholder(img)) return img
      }catch(ignore){}
    }
    WebUI.delay(0.25)
  }
  return null
}

/** Thumbnail bulunamazsa ilk kart linkine (http) fallback yapar. */
void openImageModalWithFallback(){
  def d=DriverFactory.getWebDriver()
  WebElement thumb = waitFirstRealThumb(ciT(12,24))
  if(thumb!=null){ try{ thumb.click(); return }catch(e){ js().executeScript("arguments[0].click()", thumb); return } }
  KeywordUtil.logWarning("⚠️ Gerçek thumbnail bulunamadı; kart linkine fallback.")
  String xpCardAddr="(//button[contains(@class,'text-text-link') and contains(.,'http')])[1]"
  if(!WebUI.waitForElementClickable(X(xpCardAddr), ciT(10,20), FailureHandling.OPTIONAL))
    KeywordUtil.markFailedAndStop("Ne thumbnail ne de kart linki tıklanabilir.")
  WebUI.click(X(xpCardAddr))
}

/** Hata metnine sınıf bağımsız bakarak görselin yüklenmesini doğrular. */
boolean waitImageLoadedNoError(int timeoutSec = 20){
  String xpError = "//*[normalize-space(.)='There was a problem loading your image']"
  long end = System.currentTimeMillis()+timeoutSec*1000L
  while(System.currentTimeMillis()<end){
    if(WebUI.verifyElementPresent(X(xpError),1,FailureHandling.OPTIONAL)) return false
    // yeni sekme açıldıysa da kabul
    if(DriverFactory.getWebDriver().getWindowHandles().size()>1) return true
    Boolean ok=(Boolean) js().executeScript("""
      const imgs=[...document.querySelectorAll('img')];
      const inVp=el=>{const r=el.getBoundingClientRect();return r.width>50&&r.height>50&&r.bottom>0&&r.right>0&&r.top<(innerHeight||document.documentElement.clientHeight)&&r.left<(innerWidth||document.documentElement.clientWidth)};
      return imgs.some(img=>{const cur=img.currentSrc||img.src||'';return inVp(img)&&!cur.includes('placeholder.webp')&&img.naturalWidth>0;});
    """)
    if(Boolean.TRUE.equals(ok)) return true
    WebUI.delay(0.5)
  }
  return false
}

WebElement findFirstPlaceholderThumb(){
  def d=DriverFactory.getWebDriver()
  java.util.List<WebElement> imgs = d.findElements(By.xpath("//img[contains(@class,'cursor-pointer')]"))
  for(WebElement img: imgs){
    js().executeScript("arguments[0].scrollIntoView({block:'center'});", img)
    WebUI.delay(0.2)
    if(isPlaceholder(img)) return img
  }
  return null
}

void doImageAssertionsOnCurrentPage(){
  // TC1: gerçek görsel (fallback’li aç)
  openImageModalWithFallback()
  if(!waitImageLoadedNoError(ciT(20,30)))
    KeywordUtil.markFailed("Görsel yüklenmedi veya hata mesajı çıktı.")
  closeNewViews()

  // TC2: placeholder varsa tıkla → hata bekle
  WebElement ph = findFirstPlaceholderThumb()
  if(ph!=null){
    try{ ph.click() }catch(t){ js().executeScript("arguments[0].click();", ph) }
    String xpError="//*[normalize-space(.)='There was a problem loading your image']"
    boolean err = WebUI.waitForElementVisible(X(xpError), ciT(20,25), FailureHandling.OPTIONAL)
    if(!err) KeywordUtil.markFailed("Placeholder tıklandı ama beklenen hata mesajı gelmedi.")
    closeNewViews()
  }else{
    KeywordUtil.logInfo("Placeholder yok, bu adım atlandı.")
  }
}

/* --------- ApexCharts hazır mı? --------- */
boolean waitForApexChartReady(String xpChart, int timeoutSec = 10){
  long end=System.currentTimeMillis()+timeoutSec*1000L
  while(System.currentTimeMillis()<end){
    if(WebUI.verifyElementPresent(X(xpChart),1,FailureHandling.OPTIONAL)){
      WebElement box = WebUiCommonHelper.findWebElement(X(xpChart),3)
      js().executeScript("arguments[0].scrollIntoView({block:'center'});", box)
      Boolean ready=(Boolean) js().executeScript("""
        const el=arguments[0];
        const txt=(el.textContent||'').toLowerCase();
        const noData=/no\\s*data|data\\s*not\\s*found/.test(txt) || el.querySelector('.apexcharts-no-data, .apexcharts-text.apexcharts-no-data');
        const svg=el.querySelector('svg');
        const series = svg && svg.querySelectorAll('.apexcharts-series path, .apexcharts-series rect').length>0;
        return series && !noData;
      """, box)
      if(Boolean.TRUE.equals(ready)) return true
      if(WebUI.verifyTextPresent('Data not found', false, FailureHandling.OPTIONAL))
        KeywordUtil.markFailedAndStop("❌ Risk History: Data not found")
    }
    WebUI.delay(0.4)
  }
  return false
}

/* --------- Webint Dashboard hazır mı? --------- */
boolean awaitWebintLoaded(int sec=20){
  String xpTotalBox="(//div[contains(@class,'flex h-32 items-center justify-center text-3xl font-bold')])[1]"
  String xpLinkOpenVuln="//a[contains(@class,'font-semibold') and contains(@class,'text-text-link') and contains(@class,'underline')]"
  long end=System.currentTimeMillis()+sec*1000L
  while(System.currentTimeMillis()<end){
    boolean ok1=WebUI.verifyElementPresent(X(xpTotalBox),1,FailureHandling.OPTIONAL)
    boolean ok2=WebUI.verifyElementPresent(X(xpLinkOpenVuln),1,FailureHandling.OPTIONAL)
    if(ok1 && ok2) return true
    WebUI.delay(0.5)
  }
  return false
}

/* --------- Pagination doğrulaması --------- */
void verifyPaginationByTotalCount(int totalCount){
  int expected=(int) Math.ceil(totalCount/10.0d)
  js().executeScript("window.scrollTo(0, document.body.scrollHeight)")
  TestObject pageLinks = X("//ul[contains(@class,'flex')]/li[a[not(contains(@aria-label,'previous')) and not(contains(@aria-label,'next'))]]/a")
  java.util.List<WebElement> els = WebUiCommonHelper.findWebElements(pageLinks, 10)
  int actualMax=0
  for(WebElement e: els){
    String txt=e.getText()?.trim()
    if(txt?.matches("\\d+")){ int n=Integer.parseInt(txt); if(n>actualMax) actualMax=n }
  }
  KeywordUtil.logInfo("Beklenen sayfa: "+expected+" | Gerçek liste max: "+actualMax)
  WebUI.verifyEqual(actualMax, expected)
}

/* =================== TEST =================== */
try{
  /*/ 
   
	WebUI.openBrowser('')
	WebUI.navigateToUrl('https://platform.catchprobe.org/')
	WebUI.maximizeWindow()

	// ---- Login (repo’daki objeler) ----
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
	/*/
  WebUI.navigateToUrl('https://platform.catchprobe.org/webint-dashboard')
  WebUI.waitForPageLoad(20)

  // arama: leak
  String xpInput   = "//form[contains(@class,'flex-w')]/div[contains(@class,'group')]/input"
  String xpLeakOpt = "//div[normalize-space(.)='leak']"

  if(!WebUI.waitForElementVisible(X(xpInput), ciT(10,10), FailureHandling.OPTIONAL)){
    KeywordUtil.logInfo("🔄 Arama input'u gelmedi; refresh.")
    WebUI.refresh(); WebUI.waitForPageLoad(20); WebUI.delay(1)
    if(!WebUI.waitForElementVisible(X(xpInput), ciT(10,10), FailureHandling.OPTIONAL))
      KeywordUtil.markFailedAndStop("Arama input'u hâlâ görünmedi.")
  }

  WebUI.setText(X(xpInput), "leak")
  if(WebUI.waitForElementVisible(X(xpLeakOpt), ciT(5,10), FailureHandling.OPTIONAL)){
    WebUI.click(X(xpLeakOpt))
  }else{
    KeywordUtil.logInfo("ℹ️ 'leak' önerisi yok; ENTER ile geçiliyor.")
    WebUI.sendKeys(X(xpInput), Keys.chord(Keys.ENTER))
  }

  if(!awaitWebintLoaded(ciT(20,25))){
    KeywordUtil.logInfo("İlk deneme başarısız → modal kapat + retry")
    try{ WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Mitre Close')) }catch(ignore){}
    WebUI.setText(X(xpInput),"leak")
    if(WebUI.waitForElementVisible(X(xpLeakOpt), ciT(8,12), FailureHandling.OPTIONAL)) WebUI.click(X(xpLeakOpt))
    WebUI.verifyEqual(awaitWebintLoaded(ciT(25,30)), true)
  }

  /* ------ Total Vulnerabilities ------ */
  String xpTotalV   = "(//div[contains(@class,'flex h-32 items-center justify-center text-3xl font-bold')])[1]"
  String xpOpenVBtn = "(//a[contains(@class,'font-semibold') and contains(@class,'text-text-link') and contains(@class,'underline')])[1]"
  int totalV = parseIntSafe(safeTextXp(xpTotalV, ciT(10,12)))
  KeywordUtil.logInfo("Total Vulnerabilities: "+totalV)

  if(totalV>0){
    safeClickXp(xpOpenVBtn, ciT(15,20))
    WebUI.waitForPageLoad(10)
    verifyPaginationByTotalCount(totalV)
    doImageAssertionsOnCurrentPage()

    // ilk link → yeni sekme → ilk http buton host eşleşmesi
    String xpFirstAddressLink="(//a[contains(@class,'text-text-link') and contains(.,'http')])[1]"
    if(WebUI.waitForElementVisible(X(xpFirstAddressLink), ciT(10,15), FailureHandling.OPTIONAL)){
      String firstAddrRaw = safeTextXp(xpFirstAddressLink, 10)
      String firstHost = hostOfUrlLike(firstAddrRaw)
      WebUI.click(X(xpFirstAddressLink))
      def d=DriverFactory.getWebDriver(); String orig=d.getWindowHandle(); WebUI.delay(1)
      for(String h: d.getWindowHandles()) if(h!=orig){ d.switchTo().window(h); break }
      WebUI.waitForPageLoad(15)

      String xpFirstAddrBtn="(//button[contains(@class,'text-text-link') and contains(.,'http')])[1]"
      if(!WebUI.waitForElementVisible(X(xpFirstAddrBtn), 10, FailureHandling.OPTIONAL))
        KeywordUtil.markFailedAndStop("Yeni sayfadaki http butonu bulunamadı.")
      String btnAddrRaw = safeTextXp(xpFirstAddrBtn, 10)
      String btnHost = hostOfUrlLike(btnAddrRaw)
      boolean hostMatch = firstHost.equalsIgnoreCase(btnHost)
      boolean textMatch = btnAddrRaw.toLowerCase().contains(firstHost.toLowerCase()) || firstAddrRaw.toLowerCase().contains(btnHost.toLowerCase())

      if(!(hostMatch||textMatch)){
        KeywordUtil.markFailed("Yeni sayfa host eşleşmedi.\nfirst: "+firstAddrRaw+"\nbtn:   "+btnAddrRaw+"\nhostFirst: "+firstHost+" | hostBtn: "+btnHost)
      }else{
        KeywordUtil.logInfo("✅ Host eşleşti: "+firstHost+" | "+btnHost)
      }
      d.close(); d.switchTo().window(orig)
    }
  }else{
    KeywordUtil.logInfo("Total Vulnerabilities = 0, liste akımı atlandı.")
  }

  /* ------ Overview + Total 3rd Parties ------ */
  String xpOverviewTab="//div[normalize-space(.)='Overview']"
  safeClickXp(xpOverviewTab, ciT(15,20))
  WebUI.waitForPageLoad(10)

  String xpTotal3rd="(//div[contains(@class,'flex h-32 items-center justify-center text-3xl font-bold')])[2]"
  String xp3rdBtn  ="(//a[contains(@class,'font-semibold') and contains(@class,'text-text-link') and contains(@class,'underline')])[2]"
  int total3rd = parseIntSafe(safeTextXp(xpTotal3rd, ciT(10,12)))
  KeywordUtil.logInfo("Total 3rd Parties: "+total3rd)

  if(total3rd>0){
    safeClickXp(xp3rdBtn, ciT(15,20))
    WebUI.waitForPageLoad(10)
    verifyPaginationByTotalCount(total3rd)
    doImageAssertionsOnCurrentPage()

    String xpFirstAddressLink2="(//a[contains(@class,'text-text-link') and contains(.,'http')])[1]"
    if(WebUI.waitForElementVisible(X(xpFirstAddressLink2), ciT(10,15), FailureHandling.OPTIONAL)){
      String firstAddrRaw = safeTextXp(xpFirstAddressLink2, 10)
      String firstHost = hostOfUrlLike(firstAddrRaw)
      WebUI.click(X(xpFirstAddressLink2))
      def d=DriverFactory.getWebDriver(); String orig=d.getWindowHandle(); WebUI.delay(1)
      for(String h: d.getWindowHandles()) if(h!=orig){ d.switchTo().window(h); break }
      WebUI.waitForPageLoad(15)

      String xpFirstAddrBtn="(//button[contains(@class,'text-text-link') and contains(.,'http')])[1]"
      if(!WebUI.waitForElementVisible(X(xpFirstAddrBtn), 10, FailureHandling.OPTIONAL))
        KeywordUtil.markFailedAndStop("Yeni sayfadaki http butonu bulunamadı.")
      String btnAddrRaw = safeTextXp(xpFirstAddrBtn, 10)
      String btnHost = hostOfUrlLike(btnAddrRaw)
      boolean hostMatch = firstHost.equalsIgnoreCase(btnHost)
      boolean textMatch = btnAddrRaw.toLowerCase().contains(firstHost.toLowerCase()) || firstAddrRaw.toLowerCase().contains(btnHost.toLowerCase())

      if(!(hostMatch||textMatch)){
        KeywordUtil.markFailed("Yeni sayfa host eşleşmedi.\nfirst: "+firstAddrRaw+"\nbtn:   "+btnAddrRaw+"\nhostFirst: "+firstHost+" | hostBtn: "+btnHost)
      }else{
        KeywordUtil.logInfo("✅ Host eşleşti: "+firstHost+" | "+btnHost)
      }
      d.close(); d.switchTo().window(orig)
    }
  }else{
    KeywordUtil.logInfo("Total 3rd Parties = 0, liste akımı atlandı.")
  }

  /* ------ Risk History (chart + görsel kontrol) ------ */
  String xpRiskHistoryTitle="//div[normalize-space(.)='Risk History' or normalize-space(.)='Risk history']"
  if(WebUI.waitForElementVisible(X(xpRiskHistoryTitle),5,FailureHandling.OPTIONAL)){
    WebUI.click(X(xpRiskHistoryTitle)); WebUI.waitForPageLoad(8)
    js().executeScript("arguments[0].scrollIntoView({block:'center'});", WebUiCommonHelper.findWebElement(X(xpRiskHistoryTitle),5))
    WebUI.delay(0.5)
    String xpRiskHistoryChart="//*[@id='apexchartsdarkmap_keyword_search_risk_history_chart']"
    if(!WebUI.waitForElementVisible(X(xpRiskHistoryChart),5,FailureHandling.OPTIONAL)){
      js().executeScript("window.scrollBy(0,300)")
      WebUI.waitForElementVisible(X(xpRiskHistoryChart),3,FailureHandling.OPTIONAL)
    }
    boolean chartOk = waitForApexChartReady(xpRiskHistoryChart, ciT(10,14))
    if(!chartOk) KeywordUtil.markFailedAndStop("❌ Risk History chart render olmadı.")
    else KeywordUtil.logInfo("✅ Risk History chart render OK.")

    // varsa sayfa 2 → görsel kontrol; yoksa mevcut sayfada
    String xpPage2="//a[normalize-space(.)='2']"
    if(WebUI.waitForElementVisible(X(xpPage2),4,FailureHandling.OPTIONAL)){
      js().executeScript("window.scrollTo(0, document.body.scrollHeight)")
      WebUI.click(X(xpPage2)); WebUI.waitForPageLoad(8)
      doImageAssertionsOnCurrentPage()
    }else{
      doImageAssertionsOnCurrentPage()
    }
  }

  
  KeywordUtil.markPassed("✅ WebInt Dashboard senaryosu tamamlandı.")
} finally {
  // WebUI.closeBrowser()
}

/* =================== SON BLOK: Ek bileşenler =================== */
try{
  // External Threat Signals
  String xpExternalTitle="//div[normalize-space(.)='External Threat Signals']"
  if(WebUI.waitForElementVisible(X(xpExternalTitle),20,FailureHandling.OPTIONAL)){
    scrollIntoViewXp(xpExternalTitle); WebUI.delay(2.5)
    WebUI.click(X(xpExternalTitle))

    String xpThreatLinkGeneric="//a[@target='_blank' and contains(@href,'/threatway/signature-ioc-details/')]"
    if(WebUI.waitForElementPresent(X(xpThreatLinkGeneric),8,FailureHandling.OPTIONAL)){
      scrollIntoViewXp(xpThreatLinkGeneric); WebUI.delay(1.0)
      WebUI.verifyElementClickable(X(xpThreatLinkGeneric), FailureHandling.OPTIONAL)
      WebUI.click(X(xpThreatLinkGeneric))
      String original=switchToNewTab(8)
      String curUrl=WebUI.getUrl()
      if(!curUrl.contains("/threatway/signature-ioc-details"))
        KeywordUtil.markFailed("ThreatWay URL beklenen değil: "+curUrl)
      else KeywordUtil.logInfo("✅ ThreatWay URL doğrulandı: "+curUrl)
      closeTabAndBack(original)
    }else{
      KeywordUtil.logInfo("ℹ️ ETS altında signature linki bulunamadı (dinamik olabilir).")
    }
  }else{
    KeywordUtil.logInfo("ℹ️ External Threat Signals görünmedi; atlandı.")
  }
}catch(Throwable t){ KeywordUtil.logInfo("Final ETS uyarı: "+t.message) }

try{
  // Darkmap Intelligence
  String xpDarkmapTitle="//span[text()='Darkmap Intelligence']"
  if(WebUI.waitForElementVisible(X(xpDarkmapTitle),20,FailureHandling.OPTIONAL)){
    scrollIntoViewXp(xpDarkmapTitle); WebUI.delay(0.5)
    String xpDarkmapBtn="//a[@target='_blank' and contains(@href, '/darkmap/quick-search?searched_address=')]"
    if(WebUI.waitForElementClickable(X(xpDarkmapBtn),10,FailureHandling.OPTIONAL)){
      WebUI.click(X(xpDarkmapBtn)); String original=switchToNewTab(8)
      String xpDomainRelBtn="(//button[contains(normalize-space(.),'Domain Related Intelligence')])[1]"
      if(!WebUI.waitForElementClickable(X(xpDomainRelBtn),12,FailureHandling.OPTIONAL))
        KeywordUtil.markFailed("Domain Related Intelligence butonu clickable değil!")
      else { WebUI.click(X(xpDomainRelBtn)); KeywordUtil.logInfo("✅ Domain Related Intelligence tıklandı.") }
      closeTabAndBack(original)
    }else{
      KeywordUtil.logInfo("ℹ️ Darkmap butonu clickable değil.")
    }
  }
}catch(Throwable t){ KeywordUtil.logInfo("Darkmap final uyarı: "+t.message) }

/* Diğer component kontrolleri */
try{
  js().executeScript("document.body.style.zoom='0.9'")
  def components=['Network Banner Intelligence','Exposed Bucket Intelligence','Account Intelligence','Social Ad Intelligence: Facebook']
  components.each{ component ->
    String repoName=component.replaceAll('[: ]','')
    try{
      TestObject titleObj=findTestObject("Object Repository/Darkmap/WebIntDashboard/${repoName}_Title")
      if(WebUI.verifyElementPresent(titleObj,5,FailureHandling.OPTIONAL)){
        WebElement titleEl=WebUiCommonHelper.findWebElement(titleObj,5)
        js().executeScript("arguments[0].scrollIntoView({block:'center'});", titleEl)
        WebUI.delay(0.8); KeywordUtil.logInfo("✔️ ${component} başlığı bulundu")
      }else KeywordUtil.markWarning("⚠️ ${component} başlığı yok")
    }catch(Throwable t){ KeywordUtil.markWarning("⚠️ ${component} başlık kontrolü hata: ${t.message}") }

    try{
      TestObject scanDiff=findTestObject("Object Repository/Darkmap/WebIntDashboard/${repoName}_ScanDiffButton")
      if(WebUI.verifyElementPresent(scanDiff,3,FailureHandling.OPTIONAL)){
        WebUI.click(scanDiff); WebUI.delay(1)
        try{ CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'() }catch(ignore){}
        KeywordUtil.logInfo("✅ SCAN DIFFERENCE tıklandı.")
        try{ WebUI.click(findTestObject('Object Repository/Smartdeceptive/İp close')) }catch(ignore){}
      }else KeywordUtil.logInfo("➖ ${component} için SCAN DIFFERENCE yok.")
    }catch(Throwable t){ KeywordUtil.markWarning("⚠️ ${component} SCAN DIFFERENCE hata: ${t.message}") }

    try{
      TestObject completedAt=findTestObject("Object Repository/Darkmap/WebIntDashboard/${repoName}_CompletedAt")
      if(WebUI.verifyElementPresent(completedAt,5,FailureHandling.OPTIONAL)){
        String txt=WebUI.getText(completedAt).trim()
        KeywordUtil.logInfo("✅ Completed At: ${txt}")
        if(txt.equalsIgnoreCase('In Progress')) KeywordUtil.markFailed("❌ ${component} In Progress!")
      }else KeywordUtil.markWarning("⚠️ ${component} Completed At yok")
    }catch(Throwable t){ KeywordUtil.markWarning("⚠️ ${component} Completed At hata: ${t.message}") }

    WebUI.delay(0.5)
  }
  KeywordUtil.logInfo("✅ WebInt Dashboard ek kontroller tamam.")
}catch(Throwable t){ KeywordUtil.logInfo("Son blok uyarı: "+t.message) }