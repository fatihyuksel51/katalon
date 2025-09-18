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
/************** Toplu temizlik: Tüm koleksiyonları sil **************/
void purgeAllCollections(){
  KeywordUtil.logInfo("🧹 Koleksiyonları temizleme başlıyor...")

  // Sayfa içinde “Delete” butonlarını taramak için esnek XPATH
  String DELETE_BTN_XP = "//div[contains(@class, 'cursor-pointer') and contains(@class, 'destructive-foreground')]"
  // Onay pop-up'ındaki “Delete” butonu: mevcut TestObject’i kullan
  TestObject CONFIRM_DELETE = OR.findTestObject('Object Repository/Collections/Delete button')
  // Silme toast’ı: mevcut TestObject’i kullan
  TestObject DELETE_TOAST  = OR.findTestObject('Object Repository/Collections/Delete Toast Message')

  // Pagination (genel/olasılıklı XPATH’ler; yoksa zaten düşmez)
  TestObject NEXT_PAGE_BTN  = X("//button[(@aria-label='Next' or contains(., 'Next') or contains(@class,'next')) and not(@disabled)]")
  TestObject FIRST_PAGE_BTN = X("//button[@aria-label='First' or contains(., 'First') or contains(@class,'first')]")

  JavascriptExecutor jse = (JavascriptExecutor) DriverFactory.getWebDriver()

  // Tüm sayfalar bitene kadar dön
  while (true){
	// Bu sayfadaki görünür delete butonlarını al
	List<WebElement> delBtns
	try {
	  delBtns = WebUiCommonHelper.findWebElements(X(DELETE_BTN_XP), 5)
	} catch(Throwable _){
	  delBtns = Collections.emptyList()
	}

	// Her sayfada, sondan başa sil (DOM kaymaları için daha stabil)
	boolean anyDeletedOnThisPage = false
	for (int i = delBtns.size()-1; i >= 0; i--){
	  WebElement btn = delBtns[i]
	  try {
		// Görünür yap + tıkla (JS fallback)
		try { jse.executeScript("arguments[0].scrollIntoView({block:'center',inline:'nearest'})", btn) } catch(_){}
		try { btn.click() } catch(_){ jse.executeScript("arguments[0].click()", btn) }

		// Onay kutusu ve toast bekle
		clickSmart(CONFIRM_DELETE, 10)
		WebUI.waitForElementVisible(DELETE_TOAST, 10)
		WebUI.delay(0.2) // ufak nefes

		anyDeletedOnThisPage = true
	  } catch(Throwable e){
		KeywordUtil.markWarning("Silme denemesi atlandı: " + e.getMessage())
	  }
	}

	// Bu sayfada hiç silinmediyse ve ileri sayfa yoksa biter
	boolean hasNext = WebUI.verifyElementPresent(NEXT_PAGE_BTN, 2, FailureHandling.OPTIONAL)
			

	if (!anyDeletedOnThisPage && !hasNext){
	  break
	}

	// Bu sayfada silme olduysa: aynı sayfayı yeniden tara (kalanlar olabilir)
	if (anyDeletedOnThisPage){
	  continue
	}

	// Silme yok ama “Next” var → sonraki sayfaya geç
	try {
	  WebUI.click(NEXT_PAGE_BTN)
	  WebUI.waitForPageLoad(10)
	  WebUI.delay(0.5)
	} catch(_){
	  // Next tıklanamadıysa döngüden çık
	  break
	}
  }

  // Başlangıca dön (varsa)
  if (WebUI.verifyElementPresent(FIRST_PAGE_BTN, 1, FailureHandling.OPTIONAL)){
	try {
	  WebUI.click(FIRST_PAGE_BTN)
	  WebUI.waitForPageLoad(10)
	  WebUI.delay(0.5)
	} catch(_){}
  }

  KeywordUtil.logInfo("✅ Koleksiyon temizliği bitti.")
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
  String Threat = "//span[text()='Threat']"
  WebUI.waitForElementVisible(X("//span[text()='Threat']"), 10, FailureHandling.OPTIONAL)
}

/************** TEST: Collections **************/
ensureSession()

// Threatway → Collections
WebUI.navigateToUrl('https://platform.catchprobe.org/threatway')
WebUI.waitForPageLoad(20)
WebUI.delay(1)
WebUI.navigateToUrl('https://platform.catchprobe.org/threatway/collections')
WebUI.waitForPageLoad(20)
WebUI.delay(1)
try { CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'() } catch(_){}

purgeAllCollections()

/*** Create Collection ***/
clickSmart(findTestObject('Object Repository/Collections/Create Collection'), 10)
WebUI.waitForElementVisible(findTestObject('Object Repository/Collections/İnput'), 10)
WebUI.setText(findTestObject('Object Repository/Collections/İnput'), 'katalon')
clickSmart(findTestObject('Object Repository/Collections/File Create'), 10)

WebUI.waitForElementVisible(findTestObject('Object Repository/Collections/Toast Message'), 10)
WebUI.verifyElementText(findTestObject('Object Repository/Collections/Toast Message'), 'Collection created successfully.')

/*** Filter ***/
WebUI.waitForElementVisible(findTestObject('Object Repository/Collections/Theatway filterbuton'), 10)
WebUI.click(findTestObject('Object Repository/Collections/Theatway filterbuton'))
WebUI.waitForElementVisible(findTestObject('Object Repository/Collections/Filter İnput'), 10)
clearAndType(findTestObject('Object Repository/Collections/Filter İnput'), 'katalon')
clickSmart(findTestObject('Object Repository/Collections/threatway button_APPLY AND SEARCH'), 10)
WebUI.delay(1)

/*** Active/Passive toggle ***/
// Dinamik buton (metin normalize edilmiş; büyük/küçük harfe takılmaz)
TestObject toggleButton = X("//button[normalize-space(.)='Passive' or normalize-space(.)='Active']")
WebUI.waitForElementVisible(toggleButton, 10)

String stateNow = WebUI.getText(toggleButton)?.trim()?.toUpperCase()
KeywordUtil.logInfo("Toggle initial state: " + stateNow)

if(stateNow == 'PASSIVE'){
  WebUI.click(toggleButton)
  clickSmart(findTestObject('Object Repository/Collections/Ok Butonu'), 10)
  WebUI.waitForElementVisible(findTestObject('Object Repository/Collections/Active-Passive Toast Message'), 10)
  String stateAfter = WebUI.getText(toggleButton)?.trim()?.toUpperCase()
  assert stateAfter == 'ACTIVE' : "Beklenen ACTIVE ama: " + stateAfter
  KeywordUtil.logInfo("Toggle PASSIVE → ACTIVE")
}else if(stateNow == 'ACTIVE'){
  WebUI.click(toggleButton)
  clickSmart(findTestObject('Object Repository/Collections/Ok Butonu'), 10)
  WebUI.waitForElementVisible(findTestObject('Object Repository/Collections/Active-Passive Toast Message'), 10)
  String stateAfter = WebUI.getText(toggleButton)?.trim()?.toUpperCase()
  assert stateAfter == 'PASSIVE' : "Beklenen PASSIVE ama: " + stateAfter
  KeywordUtil.logInfo("Toggle ACTIVE → PASSIVE")
}else{
  KeywordUtil.markWarning("Toggle buton metni beklenmeyen: " + stateNow)
}

/*** Edit ***/
clickSmart(findTestObject('Object Repository/Collections/Edit Buton'), 10)
WebUI.waitForElementVisible(findTestObject('Object Repository/Collections/İnput'), 10)
WebUI.click(findTestObject('Object Repository/Collections/İnput'))
WebUI.clearText(findTestObject('Object Repository/Collections/İnput'))
WebUI.setText(findTestObject('Object Repository/Collections/İnput'), 'katalon text')
WebUI.delay(1)
clickSmart(findTestObject('Object Repository/Collections/Save Butonu'), 10)
WebUI.waitForElementVisible(findTestObject('Object Repository/Collections/Edit toast message'), 10)

// Tablo satırı doğrulaması
WebUI.verifyElementText(findTestObject('Object Repository/Collections/Table text'), 'katalon text')

/*** Delete ***/
clickSmart(findTestObject('Object Repository/Collections/Delete Butonu'), 10)
WebUI.delay(1)
clickSmart(findTestObject('Object Repository/Collections/Delete button'), 10)
WebUI.waitForElementVisible(findTestObject('Object Repository/Collections/Delete Toast Message'), 10)

// Filter paneli açıksa kapat
if(WebUI.verifyElementPresent(findTestObject('Object Repository/Collections/Filter Close'), 3, FailureHandling.OPTIONAL)){
  WebUI.click(findTestObject('Object Repository/Collections/Filter Close'))
}
WebUI.delay(2)


/*** Pagination (2. sayfa) ***/
/*/
TestObject pagination2 = findTestObject('Object Repository/Collections/2.pagination')
WebElement paginationEl = WebUiCommonHelper.findWebElement(pagination2, 10)
scrollToVisible(paginationEl)
((JavascriptExecutor)DriverFactory.getWebDriver()).executeScript("window.scrollTo(0, document.body.scrollHeight)")
WebUI.delay(0.5)
clickSmart(pagination2, 10)
/*/

KeywordUtil.logInfo("✅ Collections senaryosu tamamlandı.")
