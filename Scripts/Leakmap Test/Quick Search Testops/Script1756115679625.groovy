import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebElement

/************ Helpers ************/
TestObject X(String xp){ def to=new TestObject(xp); to.addProperty("xpath", ConditionType.EQUALS, xp); return to }
JavascriptExecutor js(){ (JavascriptExecutor) DriverFactory.getWebDriver() }
boolean isBrowserOpen(){ try{ DriverFactory.getWebDriver(); return true } catch(Throwable t){ return false } }
void setZoom(double z) {
	try { js().executeScript("document.body.style.zoom='" + z + "'") } catch (Throwable t) {
	  KeywordUtil.markWarning("zoom set edilemedi: " + t.message)
	}
  }
  
void ensureSession(){
  if (isBrowserOpen()) return
  WebUI.openBrowser(''); WebUI.maximizeWindow()
  WebUI.navigateToUrl('https://platform.catchprobe.org/')
  WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'), 30)
  WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'))
  WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 30)
  WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'),'katalon.test@catchprobe.com')
  WebUI.setEncryptedText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Password_password'),'RigbBhfdqOBDK95asqKeHw==')
  WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Sign in'))
  WebUI.delay(2)
  def otp=(100000+new Random().nextInt(900000)).toString()
  WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'), otp)
  WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Verify'))
  WebUI.delay(2)
}
WebElement safeScrollTo(TestObject to){
  if(to==null) return null
  if(!WebUI.waitForElementPresent(to,8,FailureHandling.OPTIONAL)) return null
  WebElement el=WebUiCommonHelper.findWebElement(to,8)
  js().executeScript("arguments[0].scrollIntoView({block:'center'});", el)
  WebUI.delay(0.2); return el
}
void jsClickTO(TestObject to,int t=8){ WebElement el=WebUiCommonHelper.findWebElement(to,t); js().executeScript("arguments[0].click();", el) }

/************ TEST ************/
ensureSession()
// ensureOrg('Mail Test')

WebUI.navigateToUrl('https://platform.catchprobe.org/leakmap/quick-search')
WebUI.waitForPageLoad(10)

safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/İnput'))
WebUI.setText(findTestObject('Object Repository/Leakmap/QuickSearch/İnput'), 'CEYHAN SALDANLI')
safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Button_Search'))
WebUI.click(findTestObject('Object Repository/Leakmap/QuickSearch/Button_Search'))
WebUI.delay(1)

TestObject seenIcon = findTestObject('Object Repository/Leakmap/QuickSearch/Icon_Seen_Tik')
boolean isSeen = WebUI.verifyElementPresent(seenIcon, 5, FailureHandling.OPTIONAL)

def chooseFilter = { String opt ->
  safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Dropdown_ShowFilter'))
  WebUI.click(findTestObject('Object Repository/Leakmap/QuickSearch/Dropdown_ShowFilter'))
  TestObject option = (opt=='Seen'
        ? findTestObject('Object Repository/Leakmap/QuickSearch/Dropdown_Option_Seen')
        : findTestObject('Object Repository/Leakmap/QuickSearch/Dropdown_Option_Unseen'))
  safeScrollTo(option); WebUI.click(option)
  safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Button_Search'))
  WebUI.click(findTestObject('Object Repository/Leakmap/QuickSearch/Button_Search'))
  try{ CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'() }catch(e){}
}

if (isSeen) {
  // Seen’leri gizle -> sonuç yok bekle
  chooseFilter('Unseen')
  safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Text_NoResults'))
  WebUI.verifyElementPresent(findTestObject('Object Repository/Leakmap/QuickSearch/Text_NoResults'), 10)

  // Sadece Seen -> detay aç, AI insight, Unseen yap
  chooseFilter('Seen')
  safeScrollTo(seenIcon)
  WebUI.verifyElementPresent(seenIcon, 10)

  safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Button_Eye_Detail')); WebUI.click(findTestObject('Object Repository/Leakmap/QuickSearch/Button_Eye_Detail'))
  WebUI.delay(1)
  WebUI.click(findTestObject('Object Repository/Leakmap/Dashboard/AI INSIGHT'))
  TestObject insight = X("(//div[@data-radix-scroll-area-viewport])[4]")
  WebUI.waitForElementVisible(insight, 20)
  WebUI.click(findTestObject('Object Repository/Leakmap/QuickSearch/AiİnsightClose'))

  safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Button_UnseenInsidePopup'))
  WebUI.click(findTestObject('Object Repository/Leakmap/QuickSearch/Button_UnseenInsidePopup'))
  safeScrollTo(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Mitre Close')); WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Mitre Close'))
  WebUI.verifyElementNotPresent(seenIcon, 5)

  chooseFilter('Seen')
  safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Text_NoResults'))
  WebUI.verifyElementPresent(findTestObject('Object Repository/Leakmap/QuickSearch/Text_NoResults'), 10)

  chooseFilter('Unseen')
  safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Button_Eye_Detail'))
  WebUI.verifyElementPresent(findTestObject('Object Repository/Leakmap/QuickSearch/Button_Eye_Detail'), 10)

} else {
  KeywordUtil.logInfo("🔍 Başlangıç UNSEEN; filtre senaryosu ters çalıştırılıyor.")
  chooseFilter('Seen')
  safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Text_NoResults'))
  WebUI.verifyElementPresent(findTestObject('Object Repository/Leakmap/QuickSearch/Text_NoResults'), 10)

  chooseFilter('Unseen')
  safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Button_Eye_Detail'))
  WebUI.verifyElementPresent(findTestObject('Object Repository/Leakmap/QuickSearch/Button_Eye_Detail'), 10)

  WebUI.click(findTestObject('Object Repository/Leakmap/QuickSearch/Button_Eye_Detail'))
  WebUI.delay(1)

  setZoom(0.6)           // veya js().executeScript("document.body.style.zoom='0.6'")
  WebUI.click(findTestObject('Object Repository/Leakmap/Dashboard/AI INSIGHT'))
  TestObject insight = X("(//div[@data-radix-scroll-area-viewport])[4]")
  WebUI.waitForElementVisible(insight, 20)
  WebUI.click(findTestObject('Object Repository/Leakmap/QuickSearch/AiİnsightClose'))
  safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Button_SeenInsidePopup'))
  WebUI.click(findTestObject('Object Repository/Leakmap/QuickSearch/Button_SeenInsidePopup'))
  safeScrollTo(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Mitre Close')); WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Mitre Close'))

  safeScrollTo(seenIcon); WebUI.verifyElementPresent(seenIcon, 5)

  chooseFilter('Unseen')
  safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Text_NoResults'))
  WebUI.verifyElementPresent(findTestObject('Object Repository/Leakmap/QuickSearch/Text_NoResults'), 10)

  chooseFilter('Seen')
  safeScrollTo(findTestObject('Object Repository/Leakmap/QuickSearch/Button_Eye_Detail'))
  WebUI.verifyElementPresent(findTestObject('Object Repository/Leakmap/QuickSearch/Button_Eye_Detail'), 10)
}
