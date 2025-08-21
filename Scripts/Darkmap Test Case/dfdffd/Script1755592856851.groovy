import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import com.kms.katalon.core.webui.driver.DriverFactory
import org.openqa.selenium.*
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling
import java.util.Random

/* ================= helpers (CI-safe) ================= */
TestObject X(String xp){ def to=new TestObject(xp); to.addProperty("xpath", ConditionType.EQUALS, xp); return to }
JavascriptExecutor js(){ (JavascriptExecutor) DriverFactory.getWebDriver() }
boolean isCI(){ System.getenv('KATALON_AGENT_NAME')!=null || System.getenv('KATALON_TASK_ID')!=null }
int ciT(int local, int ci){ return isCI()? ci : local }

void scrollIntoViewXp(String xp){
  WebElement el = WebUiCommonHelper.findWebElement(X(xp), 5)
  js().executeScript("arguments[0].scrollIntoView({block:'center'});", el)
}
void safeClick(String xp, int t=15) {
	if (!WebUI.waitForElementClickable(X(xp), t)) {
		KeywordUtil.markFailedAndStop("❌ Tıklanabilir değil: " + xp)
	}
	WebUI.click(X(xp))
}
void waitToast(String contains, int t=10) {
    String toastXp = "//*[contains(@class,'toast') or contains(@class,'alert') or contains(@class,'notification')][contains(.,'"+contains+"')]"
    WebUI.waitForElementVisible(X(toastXp), t, FailureHandling.OPTIONAL)
}
boolean waitToast(int timeout=8){
	String xpToast = "//*[contains(@class,'ant-message') or contains(@class,'ant-notification') or contains(@class,'toast') or contains(@class,'alert')][not(contains(@style,'display: none'))]"
	return WebUI.waitForElementVisible(X(xpToast), timeout, FailureHandling.OPTIONAL)
}
void clickFast(TestObject to){
  try{ WebUI.click(to) }catch(Throwable e){
    try{ WebElement el = WebUiCommonHelper.findWebElement(to, 3); js().executeScript("arguments[0].click()", el) }catch(Throwable ee){
      KeywordUtil.markFailedAndStop("Tıklanamadı: "+to.getObjectId()+" | "+ee.message)
    }
  }
}
String safeText(String xp, int t=15) {
	if (!WebUI.waitForElementVisible(X(xp), t)) {
		KeywordUtil.markFailedAndStop("❌ Görünür değil: " + xp)
	}
	return WebUI.getText(X(xp)).trim()
}
void safeClickXp(String xp, int t=15){
  TestObject to = X(xp)
  if(!WebUI.waitForElementClickable(to, t, FailureHandling.OPTIONAL)){
    try{ scrollIntoViewXp(xp) }catch(ignore){}
    if(!WebUI.waitForElementClickable(to, 3, FailureHandling.OPTIONAL))
      KeywordUtil.markFailedAndStop("❌ Tıklanabilir değil: "+xp)
  }
  clickFast(to)
}
String safeTextXp(String xp, int t=15){
  if(!WebUI.waitForElementVisible(X(xp), t, FailureHandling.OPTIONAL))
    KeywordUtil.markFailedAndStop("❌ Görünür değil: "+xp)
  return WebUI.getText(X(xp)).trim()
}
String xpLiteral(String s){ s.contains("'") ? 'concat(\''+ s.replace("'", '\',"\'' ) + '\')' : "'"+s+"'" }
boolean isVisibleXp(String xp, int t=2){ WebUI.waitForElementVisible(X(xp), t, FailureHandling.OPTIONAL) }

boolean waitToastContains(String txt, int timeout=10){
  String xp = "//*[contains(@class,'ant-message') or contains(@class,'ant-notification') or contains(@class,'toast') or contains(@class,'alert')]" +
              "[not(contains(@style,'display: none'))]//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), '"+txt.toLowerCase().replace("'", "\\'")+"')]"
  return WebUI.waitForElementVisible(X(xp), timeout, FailureHandling.OPTIONAL)
}
void scrollIntoView(String xp) {
	def el = WebUiCommonHelper.findWebElement(X(xp), 10)
	((JavascriptExecutor)DriverFactory.getWebDriver()).executeScript("arguments[0].scrollIntoView({block:'center'});", el)
}
boolean waitToastAny(int timeout=8){
  String xpToast = "//*[contains(@class,'ant-message') or contains(@class,'ant-notification') or contains(@class,'toast') or contains(@class,'alert')][not(contains(@style,'display: none'))]"
  return WebUI.waitForElementVisible(X(xpToast), timeout, FailureHandling.OPTIONAL)
}
void clearAndType(String xp, String value){
  TestObject to = X(xp)
  WebUI.click(to)
  try{ WebUI.clearText(to) }catch(ignore){
    boolean isMac = System.getProperty("os.name")?.toLowerCase()?.contains("mac")
    Keys mod = isMac ? Keys.COMMAND : Keys.CONTROL
    WebUI.sendKeys(to, Keys.chord(mod, "a"))
    WebUI.sendKeys(to, Keys.chord(Keys.DELETE))
  }
  WebUI.setText(to, value)
}

/* ------- card helpers: 1 olmazsa 2’yi dene ------- */
String xpPinBtnByIndex(int i){ "(//button[.//*[name()='svg' and contains(@class,'lucide') and contains(@class,'pin')]])["+i+"]" }
String xpAddrByIndex(int i){ "(//button[contains(@class,'text-text-link') and contains(.,'http')])["+i+"]" }
String cardRootByPinIndex(int i){ "("+xpPinBtnByIndex(i)+"/ancestor::*[contains(@class,'rounded') or contains(@class,'border') or contains(@class,'shadow')][1])" }

boolean isBtnEnabled(String xp){
  if(!WebUI.waitForElementPresent(X(xp), 5, FailureHandling.OPTIONAL)) return false
  try{
    WebElement btn = WebUiCommonHelper.findWebElement(X(xp), 5)
    String cls = btn.getAttribute("class") ?: ""
    boolean disabledAttr = btn.getAttribute("disabled")!=null || "true".equalsIgnoreCase(btn.getAttribute("aria-disabled"))
    boolean disabledCss  = cls.contains("pointer-events-none") || cls.contains("opacity-50") || cls.contains("disabled")
    return !(disabledAttr || disabledCss)
  }catch(ignore){ return false }
}
int pickUsableCardIndex(){
  if(isBtnEnabled(xpPinBtnByIndex(1))) return 1
  if(isBtnEnabled(xpPinBtnByIndex(2))){ KeywordUtil.logInfo("ℹ️ 1. kart uygun değil → 2. karta geçiliyor."); return 2 }
  KeywordUtil.markFailedAndStop("Tıklanabilir pin butonu bulunamadı (ilk iki kart)."); return 1
}
void clickYesIfDialogVisible(){
  String xpYes = "//button[@type='button' and normalize-space(.)='YES']"
  if(WebUI.waitForElementVisible(X(xpYes), ciT(5,10), FailureHandling.OPTIONAL)) safeClickXp(xpYes, 5)
}

/* ======================= TEST ======================= */
//  // (Login bloğu istersen aç)
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

WebUI.navigateToUrl('https://platform.catchprobe.org/darkmap/quick-search')
WebUI.delay(2)
WebUI.waitForPageLoad(20)

/* --- Search = leak --- */
String xpInput = "//input[@name='html_content']"
String xpSearch = "//button[.//span[normalize-space(.)='Search'] or normalize-space(.)='Search']"
if(!WebUI.waitForElementVisible(X(xpInput), ciT(10,15), FailureHandling.OPTIONAL)){
  KeywordUtil.logInfo("🔄 Arama input'u gelmedi, sayfa yenileniyor.")
  WebUI.refresh(); WebUI.waitForPageLoad(20)
  WebUI.verifyEqual(WebUI.waitForElementVisible(X(xpInput), ciT(10,15), FailureHandling.OPTIONAL), true)
}
clearAndType(xpInput, "leak")
safeClickXp(xpSearch, ciT(10,15))

/* --- Kart seçim: 1 olmazsa 2 --- */
int cardIdx = pickUsableCardIndex()

/* --- Seçili kartın adresini al --- */
String firstAddr = safeTextXp(xpAddrByIndex(cardIdx), ciT(15,20))
KeywordUtil.logInfo("📌 Kart("+cardIdx+") ilk adres: "+firstAddr)

// 2) Pin
String xpPinBtn = "(.//*[name()='svg' and contains(@class,'lucide lucide-pin h-4 w-4')]/ancestor::button)[1]"
scrollIntoView(xpPinBtn)
safeClick(xpPinBtn)

// 🔔 Pin -> Unpin confirm popup'ı çıkarsa YES'e bas
String xpYesInDialog = "//button[@type='button' and normalize-space(text())='YES']"
if (WebUI.waitForElementVisible(X(xpYesInDialog), 10, FailureHandling.OPTIONAL)) {
    safeClick(xpYesInDialog)
}

// her iki durumda da olası toast'ları opsiyonel bekle
waitToast("Pinned", 10)
waitToast("Unpinned", 10)


// 3) Tag
String xpTagBtn = "(.//*[name()='svg' and contains(@class,'lucide-tag')]/ancestor::button)[1]"
safeClick(xpTagBtn)

// 4) Create Tag → “Katalon”
String xpCreateTagBtn = "//button[normalize-space()='Create tag' or normalize-space()='CREATE TAG']"
if (WebUI.waitForElementVisible(X(xpCreateTagBtn), 5, FailureHandling.OPTIONAL)) { safeClick(xpCreateTagBtn) }
String xpTagInput = "//input[@type='text' and (contains(@placeholder,'Tag') or contains(@aria-label,'Tag') or contains(@class,'input'))]"
WebUI.setText(X(xpTagInput), "Katalon")
String xpCreateBtn = "//button[normalize-space()='CREATE' or normalize-space()='Create']"
safeClick(xpCreateBtn)
waitToast("Tag created")

// Switch ON + Mark as Seen
String xpKatalonSwitch = "//*[translate(normalize-space(text()),'KATALON','katalon')='katalon']/ancestor::*[self::div or self::li][1]//*[(@role='switch' or self::button) and (@aria-checked='false' or not(@aria-checked))]"
safeClickXp(xpKatalonSwitch, 10)
String xpKatalonOn = "//*[translate(normalize-space(text()),'KATALON','katalon')='katalon']/ancestor::*[self::div or self::li][1]//*[(@role='switch' or self::button)][@aria-checked='true']"
WebUI.verifyElementPresent(X(xpKatalonOn), 10)
safeClickXp("//button[normalize-space(.)='Mark as Seen']", 10)
waitToastContains("tags saved", ciT(6,10))

/* ================= Tag Management ================= */
WebUI.navigateToUrl("https://platform.catchprobe.org/darkmap/tag-management")
WebUI.waitForPageLoad(15)
// İlk tag değerini al
String xpTagSpanFirst = "(//td[contains(@class,'ant-table-cell')]/span)[1]"
String tagVal = safeTextXp(xpTagSpanFirst, 15)
KeywordUtil.logInfo("Güncel Tag: "+tagVal)

// 3) Aynı satırdaki göz (eye) ikonuna bas
String xpRowByTag   = "//tr[.//td[contains(@class,'ant-table-cell')]/span[normalize-space(.)="+xpLiteral(tagVal)+"]]"
String xpEyeInRow   = xpRowByTag + "//*[@class='lucide lucide-eye h-4 w-4']"
if(!WebUI.waitForElementPresent(X(xpEyeInRow), 2, FailureHandling.OPTIONAL)){
// fallback: ilk göz
xpEyeInRow = "(//*[@class='lucide lucide-eye h-4 w-4'])[1]"
}
scrollIntoViewXp(xpEyeInRow)
safeClickXp(xpEyeInRow, 5)


// 5) Quick Search/popup içerik doğrulaması
String xpPopup = "(//span[@class='px-2'])[1]"
if(!WebUI.waitForElementVisible(X(xpPopup), 12, FailureHandling.OPTIONAL)){
KeywordUtil.markFailedAndStop("Pop-up içeriği görünmedi (//span[@class='px-2']).")
}
scrollIntoViewXp(xpPopup)

// birden fazla px-2 olabilir; hepsinde ara
List<String> popupTexts = (List<String>) WebUI.executeJavaScript(
"return Array.from(document.querySelectorAll('span.px-2')).map(e=>e.textContent.trim());", []
)
boolean matched = popupTexts.any{ it?.toLowerCase()?.contains(tagVal.toLowerCase()) }
if(!matched){
KeywordUtil.markFailed("Pop-up metinleri Tag değerini içermiyor. Tag: '"+tagVal+"' | Pop-up: "+popupTexts)
} else {
KeywordUtil.logInfo("✅ Pop-up doğrulandı. Tag '"+tagVal+"' bulundu.")
}
String filterclose="//span[contains(@class, 'cursor-pointer') and contains(@class, 'rounded-full bg-destructive')]"
WebUI.waitForElementClickable(X(filterclose), 20)
safeClickXp(filterclose)

/* --- FILTER OPTIONS → Katalon --- */
String xpFilterOpts = "//button[normalize-space()='FILTER OPTIONS' or contains(translate(normalize-space(.),'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'FILTER OPTIONS')]"
safeClickXp(xpFilterOpts, ciT(10,15))
String xpSelectTags = "//button[.//span[normalize-space(.)='Select Tags'] or contains(@class,'select')][1]"
safeClickXp(xpSelectTags, 10)
String xpTagKatalonInList = "//*[contains(@role,'option') or self::div or self::button][translate(normalize-space(.),'KATALON','katalon')='katalon']"
safeClickXp(xpTagKatalonInList, 10)
String xpApplySearch = "//button[normalize-space(.)='APPLY AND SEARCH' or normalize-space(.)='Apply & Search' or normalize-space(.)='Apply and Search']"
safeClickXp(xpApplySearch, 10)
WebUI.waitForPageLoad(15)

/* --- Kart adresinin doğrulanması --- */
String xpCardAddr = xpAddrByIndex(1)
WebUI.waitForElementVisible(X(xpCardAddr), ciT(15,20))
String currentAddr = safeTextXp(xpCardAddr, 10)
KeywordUtil.logInfo("🟪 Karttaki adres: "+currentAddr)
WebUI.verifyMatch(currentAddr, firstAddr, false)

/* --- Tag panelini tekrar aç ve switch açık mı --- */
safeClickXp(xpTagBtn, 10)
WebUI.verifyElementPresent(X(xpKatalonOn), 10)

/* --- Switch kapat → Mark as Not Seen --- */
String xpKatalonOnClickable = xpKatalonOn
safeClickXp(xpKatalonOnClickable, 10)
String xpKatalonOff = "//*[translate(normalize-space(text()),'KATALON','katalon')='katalon']/ancestor::*[self::div or self::li][1]//*[(@role='switch' or self::button)][@aria-checked='false']"
WebUI.verifyElementPresent(X(xpKatalonOff), 10)
String xpMarkNotSeen = "//button[normalize-space(.)='Mark as Not Seen' or normalize-space(.)='Mark as not seen']"
safeClickXp(xpMarkNotSeen, 10)
waitToastContains("not seen", ciT(6,10))

/* --- No data bekle --- */
String xpNoData = "//div[@class='ant-empty-description' and normalize-space(text())='No data']"
WebUI.waitForElementVisible(X(xpNoData), ciT(15,20))
WebUI.verifyElementPresent(X(xpNoData), 10)
KeywordUtil.markPassed("✅ Quick Search: pin → tag → filter → doğrulama tamam.")

/* ================= Pinned Address ================= */
WebUI.navigateToUrl('https://platform.catchprobe.org/darkmap/pinned-address')
WebUI.delay(2)
WebUI.waitForPageLoad(20)

String xpCardAddrPin = xpAddrByIndex(1)
WebUI.waitForElementVisible(X(xpCardAddrPin), ciT(15,20))
String currentAddrPin = safeTextXp(xpCardAddrPin, 10)
KeywordUtil.logInfo("🟪 Pinned kart adres: "+currentAddrPin)
WebUI.verifyMatch(currentAddrPin, currentAddr, false)

// Unpin
String xpPinBtnPin = "(.//*[name()='svg' and contains(@class,'lucide-pin')]/ancestor::button)[1]"
scrollIntoViewXp(xpPinBtnPin)
safeClickXp(xpPinBtnPin, 10)
clickYesIfDialogVisible()
waitToastContains("unpinned", ciT(6,10))

// No data
String xpNoDataPin = "//div[@class='ant-empty-description' and normalize-space(text())='No data']"
WebUI.waitForElementVisible(X(xpNoDataPin), ciT(15,20))
WebUI.verifyElementPresent(X(xpNoDataPin), 10)
KeywordUtil.markPassed("✅ Pinned Address: unpin → no data tamam.")

/* ================= Tag Management ================= */
WebUI.navigateToUrl("https://platform.catchprobe.org/darkmap/tag-management")
WebUI.waitForPageLoad(15)




// 3) Aynı satırdaki pencil  ikonuna bas

String xpPencilInRow   =  "//*[@class='lucide lucide-pencil h-4 w-4']"
if(!WebUI.waitForElementPresent(X(xpPencilInRow), 5, FailureHandling.OPTIONAL)){
}
scrollIntoViewXp(xpPencilInRow)
safeClickXp(xpPencilInRow, 15)

// 8) Güncelle + alarm OFF
String xpMatchTag   = "//input[@type='text' and @name='tag']"
clearAndType(xpMatchTag,   "Katalon Text")
// 9) SAVE
String xpSave = "//button[normalize-space(.)='SAVE']"
safeClickXp(xpSave, 15)
	if (!waitToast(8)) KeywordUtil.markWarning("Başarı bildirimi görünmedi (SAVE).")
	
// 2) İlk Tag değerini al
String xpTagSpanSecond = "(//td[contains(@class,'ant-table-cell')]/span)[1]"
String tagVal2 = safeText(xpTagSpanSecond, 15)
KeywordUtil.logInfo("İkinci Tag: " + tagVal2)
// 3) Aynı satırdaki göz (eye) ikonuna bas
String xpRowByTag2   = "//tr[.//td[contains(@class,'ant-table-cell')]/span[normalize-space(.)="+xpLiteral(tagVal)+"]]"
String xpEyeInRow2   = xpRowByTag2 + "//*[@class='lucide lucide-eye h-4 w-4']"
if(!WebUI.waitForElementPresent(X(xpEyeInRow2), 5, FailureHandling.OPTIONAL)){
// fallback: ilk göz
xpEyeInRow = "(//*[@class='lucide lucide-eye h-4 w-4'])[1]"
}
scrollIntoViewXp(xpEyeInRow)
safeClickXp(xpEyeInRow, 15)

// birden fazla px-2 olabilir; hepsinde ara
List<String> popupTexts2 = (List<String>) WebUI.executeJavaScript(
"return Array.from(document.querySelectorAll('span.px-2')).map(e=>e.textContent.trim());", []
)
boolean matched2 = popupTexts.any{ it?.toLowerCase()?.contains(tagVal.toLowerCase()) }
if(!matched2){
KeywordUtil.markFailed("Pop-up metinleri Tag değerini içermiyor. Tag: '"+tagVal2+"' | Pop-up: "+popupTexts2)
} else {
KeywordUtil.logInfo("✅ Pop-up doğrulandı. Tag '"+tagVal2+"' bulundu.")
}
scrollIntoViewXp(xpNoData)
WebUI.waitForElementVisible(X(xpNoData), 20)
WebUI.verifyElementPresent(X(xpNoData), 10)

WebUI.navigateToUrl("https://platform.catchprobe.org/darkmap/tag-management")
WebUI.waitForPageLoad(15)

// DELETE
String xpDelete = "//*[@class='lucide lucide-trash2 h-4 w-4']"
safeClickXp(xpDelete, 10)

String xpDeleteText = "//button[normalize-space(.)='DELETE']"
if (!WebUI.waitForElementVisible(X(xpDeleteText), 8, FailureHandling.OPTIONAL))
	KeywordUtil.markFailedAndStop("Delete onayı görünmedi.")

String xpDeleteBtn = "//button[normalize-space(.)='DELETE']"
safeClickXp(xpDeleteBtn, 10)
waitToast(8)

// Onay toast (birebir)
if(!waitToastContains("Tag deleted successfully", 12))
  KeywordUtil.markFailed("Onay toast'ı birebir gelmedi.")

KeywordUtil.markPassed("✅ TAg Management testi başarıyla tamamlandı.")
