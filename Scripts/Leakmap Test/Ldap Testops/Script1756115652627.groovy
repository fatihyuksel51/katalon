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
void ensureSession(){
  if (isBrowserOpen()) return
  WebUI.openBrowser('')
  WebUI.maximizeWindow()
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
  WebUI.delay(0.3); return el
}
void jsClickTO(TestObject to,int t=8){ WebElement el=WebUiCommonHelper.findWebElement(to,t); js().executeScript("arguments[0].click();", el) }

/************ TEST ************/
ensureSession()
// ensureOrg('Mail Test')  // gerekirse açın

WebUI.navigateToUrl('https://platform.catchprobe.org/ldap')
WebUI.waitForPageLoad(10)

// Create
WebUI.click(findTestObject('Object Repository/Leakmap/Ldap/Create Ldap'))
WebUI.setText(findTestObject('Object Repository/Leakmap/Ldap/Titleinput'), 'catchprobe')
WebUI.setText(findTestObject('Object Repository/Detail Scan/Search email'), 'cn={username},cn=Users,dc=cpad,dc=local')
WebUI.setText(findTestObject('Object Repository/Leakmap/Ldap/Server İnput'), 'ldap://138.199.152.248')
WebUI.click(findTestObject('Object Repository/Leakmap/Ldap/Keywordsbuton'))
WebUI.click(findTestObject('Object Repository/Leakmap/Ldap/Catchprobeldap'))
safeScrollTo(findTestObject('Object Repository/Leakmap/Ldap/Create')); WebUI.click(findTestObject('Object Repository/Leakmap/Ldap/Create'))
try{ CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'() }catch(e){}
WebUI.waitForElementVisible(findTestObject('Object Repository/Leakmap/Ldap/Createsuccessfully'), 15)
WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Mitre Close'))

// View / close
WebUI.click(findTestObject('Object Repository/Leakmap/Ldap/Eyebutton'))
try{ CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'() }catch(e){}
WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Mitre Close'))

//Edit butonu ile statusu actieve et
WebUI.click(findTestObject('Object Repository/Leakmap/Ldap/Editbutonu'))
CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()
safeScrollTo(findTestObject('Object Repository/Leakmap/Ldap/Statusactive'))
WebUI.click(findTestObject('Object Repository/Leakmap/Ldap/Statusactive'))
WebUI.delay(1)
WebUI.sendKeys(findTestObject('Object Repository/Leakmap/Ldap/Statusactive'), 'a')
WebUI.click(findTestObject('Object Repository/Leakmap/Ldap/Statusactive'))

safeScrollTo(findTestObject('Object Repository/Leakmap/Ldap/Update'))
WebUI.click(findTestObject('Object Repository/Leakmap/Ldap/Update'))
CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()

// suuces mesajını doğrula
WebUI.waitForElementVisible(findTestObject('Object Repository/Leakmap/Ldap/Createsuccessfully'), 15)
WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Mitre Close'))

//Actieve yazısını doğrula
TestObject actieve = findTestObject('Object Repository/Leakmap/Ldap/Actievetext')
WebUI.waitForElementVisible(actieve, 10)
String actievetext = WebUI.getText(actieve)

// Sonuç doğrulama
assert actievetext.contains("Active")

//Ldapi sil
WebUI.waitForElementVisible(findTestObject('Object Repository/Leakmap/Ldap/Deletebutonu'), 15)
WebUI.click(findTestObject('Object Repository/Leakmap/Ldap/Deletebutonu'))
WebUI.waitForElementVisible(findTestObject('Object Repository/Leakmap/Ldap/Deleteok'), 15)
WebUI.click(findTestObject('Object Repository/Leakmap/Ldap/Deleteok'))

//delete mesajını doğrula
WebUI.waitForElementVisible(findTestObject('Object Repository/Leakmap/Ldap/Deletesucces'), 15)
WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Mitre Close'))

