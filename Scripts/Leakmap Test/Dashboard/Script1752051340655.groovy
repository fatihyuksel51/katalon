import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import org.openqa.selenium.JavascriptExecutor as JavascriptExecutor
import org.openqa.selenium.WebElement as WebElement
import com.kms.katalon.core.webui.common.WebUiCommonHelper as WebUiCommonHelper
import org.openqa.selenium.WebDriver as WebDriver
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.util.KeywordUtil
import org.openqa.selenium.By
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.WebDriverWait
import org.openqa.selenium.support.ui.ExpectedConditions
import com.catchprobe.utils.MailReader
import static com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords.*


// ✅ Fonksiyon: Scroll edip görünür hale getir
def scrollToVisible(WebElement element, JavascriptExecutor js) {
	int currentScroll = 0
	boolean isVisible = false
	while (!isVisible && currentScroll < 3000) {
		js.executeScript("window.scrollBy(0, 200)")
		WebUI.delay(0.5)
		isVisible = element.isDisplayed()
		currentScroll += 200
	}
	return isVisible
}
TestObject X(String xp){ def to=new TestObject(xp); to.addProperty("xpath", ConditionType.EQUALS, xp); return to }
JavascriptExecutor js(){ (JavascriptExecutor) DriverFactory.getWebDriver() }

void scrollIntoViewXp(String xp){
  try {
	WebElement el = WebUiCommonHelper.findWebElement(X(xp), 5)
	js().executeScript("arguments[0].scrollIntoView({block:'center'});", el)
  } catch(Throwable ignore) {}
}

boolean waitToastContains(String txt, int timeout=10){
	String xp = "//*[contains(@class,'ant-message') or contains(@class,'ant-notification') or contains(@class,'toast') or contains(@class,'alert')]" +
				"[not(contains(@style,'display: none'))]//*[contains(normalize-space(.), '"+txt.replace("'", "\\'")+"')]"
	return WebUI.waitForElementVisible(X(xp), timeout, FailureHandling.OPTIONAL)
  }

// Tarayıcıyı aç ve siteye git
WebUI.openBrowser('')

WebUI.navigateToUrl('https://platform.catchprobe.org/')

WebUI.maximizeWindow()

// Login işlemleri
WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'), 30)

WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'))

WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 30)

WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 'katalon.test@catchprobe.com')

WebUI.setEncryptedText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Password_password'), 'RigbBhfdqOBDK95asqKeHw==')

WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Sign in'))

WebUI.delay(5)

// OTP işlemi
def randomOtp = (100000 + new Random().nextInt(900000)).toString()

WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'), randomOtp)

WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Verify'))

WebUI.delay(5)

WebUI.waitForPageLoad(30)

//
// Leakmap sekmesine tıkla
WebUI.navigateToUrl('https://platform.catchprobe.org/leakmap')

WebUI.waitForPageLoad(10)

CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()

WebUI.delay(3)

WebUI.waitForPageLoad(10)
WebDriver driver = DriverFactory.getWebDriver()
JavascriptExecutor js = (JavascriptExecutor) driver
Actions actions = new Actions(driver)

//Government Issued IDs kısmında grafik doğrulaması yap
TestObject GovernmentID = findTestObject('Object Repository/Leakmap/Dashboard/GovernmentID')

// Div'in görünmesini bekle (maksimum 10 saniye)
if (WebUI.waitForElementVisible(GovernmentID, 15)) {
	
	// Elementi bul
	WebElement GovernmentIDelement = WebUI.findWebElement(GovernmentID, 10)
	
	
	// Div'in içinde SVG olup olmadığını kontrol et
	Boolean GovernmentIDelementRisk = WebUI.executeJavaScript(
		"return arguments[0].querySelector('svg') != null;",
		Arrays.asList(GovernmentIDelement)
	)
	
	// Durumu logla
	KeywordUtil.logInfo("Government Issued IDs SVG var mı? : " + GovernmentIDelementRisk)
	
	if (GovernmentIDelementRisk) {
		KeywordUtil.logInfo("Government Issued IDs Veri VAR ✅")
		
		
	} else {
		KeywordUtil.logInfo("Government Issued IDs Veri YOK 🚨")
	}
	
} else {
	KeywordUtil.logInfo("Government Issued IDs Svg elementi görünmedi ⏰")
}

// EMAIL DOMAINS
TestObject EmailDomains = findTestObject('Object Repository/Leakmap/Dashboard/EmailDomains')

if (WebUI.waitForElementVisible(EmailDomains, 15)) {
	WebElement EmailDomainsElement = WebUI.findWebElement(EmailDomains, 10)

	Boolean EmailDomainsElementRisk = WebUI.executeJavaScript(
		"return arguments[0].querySelector('svg') != null;",
		Arrays.asList(EmailDomainsElement)
	)

	KeywordUtil.logInfo("Email Domains SVG var mı? : " + EmailDomainsElementRisk)

	if (EmailDomainsElementRisk) {
		KeywordUtil.logInfo("Email Domains Veri VAR ✅")
	} else {
		KeywordUtil.logInfo("Email Domains Veri YOK 🚨")
	}
} else {
	KeywordUtil.logInfo("Email Domains Svg elementi görünmedi ⏰")
}


// WEBSITE URLs
TestObject WebsiteURLs = findTestObject('Object Repository/Leakmap/Dashboard/WebsiteURLs')

if (WebUI.waitForElementVisible(WebsiteURLs, 15)) {
	WebElement WebsiteURLsElement = WebUI.findWebElement(WebsiteURLs, 10)

	Boolean WebsiteURLsElementRisk = WebUI.executeJavaScript(
		"return arguments[0].querySelector('svg') != null;",
		Arrays.asList(WebsiteURLsElement)
	)

	KeywordUtil.logInfo("Website URLs SVG var mı? : " + WebsiteURLsElementRisk)

	if (WebsiteURLsElementRisk) {
		KeywordUtil.logInfo("Website URLs Veri VAR ✅")
	} else {
		KeywordUtil.logInfo("Website URLs Veri YOK 🚨")
	}
} else {
	KeywordUtil.logInfo("Website URLs Svg elementi görünmedi ⏰")
}

// AI Insight butonuna tıkla
WebUI.click(findTestObject('Object Repository/Leakmap/Dashboard/AI INSIGHT'))

// AI Insight içeriği bekle
TestObject insightContent = new TestObject()
insightContent.addProperty("xpath", ConditionType.EQUALS, "(//div[@data-radix-scroll-area-viewport])[2]")

WebUI.waitForElementVisible(insightContent, 20)

// Report sekmesini kapat
WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Mitre Close'))

// 'Recent Inserts Size' scroll ve tıklama işlemi
WebElement recentInsertScroll = WebUiCommonHelper.findWebElement(findTestObject('Object Repository/Leakmap/Dashboard/Recent Inserts Size'), 20)

if (scrollToVisible(recentInsertScroll, js)) {
	js.executeScript("arguments[0].click();", recentInsertScroll)
	WebUI.delay(2)
	WebUI.comment("👉 'Recent Inserts Size' butonuna tıklandı.")
	WebUI.waitForPageLoad(10)
} else {
	WebUI.comment("❌ 'Recent Inserts Size' butonu görünür değil, tıklanamadı.")
}

// 'Recent Inserts Size' kısmında grafik doğrulaması
TestObject recentInsertGraph = findTestObject('Object Repository/Leakmap/Dashboard/Recent Inserts Size')

if (WebUI.waitForElementVisible(recentInsertGraph, 15)) {
	WebElement recentInsertElement = WebUI.findWebElement(recentInsertGraph, 10)

	Boolean recentInsertSvgExists = WebUI.executeJavaScript(
		"return arguments[0].querySelector('svg') != null;",
		Arrays.asList(recentInsertElement)
	)

	KeywordUtil.logInfo("Recent Inserts Size SVG var mı? : " + recentInsertSvgExists)

	if (recentInsertSvgExists) {
		KeywordUtil.logInfo("Recent Inserts Size Veri VAR ✅")
	} else {
		KeywordUtil.logInfo("Recent Inserts Size Veri YOK 🚨")
	}
} else {
	KeywordUtil.logInfo("Recent Inserts Size Svg elementi görünmedi ⏰")
}

// Buton elementi al
WebElement DownloadData = WebUiCommonHelper.findWebElement(findTestObject('Object Repository/Leakmap/Dashboard/Download data'), 10)


// Scroll ve click işlemi
if (scrollToVisible(DownloadData, js)) {
	js.executeScript("arguments[0].click();", DownloadData)
	WebUI.comment("👉 'ioc detail' butonuna tıklandı.")
} else {
	WebUI.comment("❌ 'ioc detail' butonu görünür değil, tıklanamadı.")
}


WebUI.waitForElementVisible(findTestObject('Object Repository/Leakmap/Dashboard/Email to List'), 5)
WebUI.click(findTestObject('Object Repository/Leakmap/Dashboard/Email to List'))
CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()
WebUI.delay(2)
// TestObject tanımı
TestObject emailElement = findTestObject('Object Repository/Leakmap/Dashboard/catchprobetestmail')

// Elementi bul
WebElement targetElement = WebUiCommonHelper.findWebElement(emailElement, 10)

// Scroll işlemi (JS ile)
js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", targetElement)
WebUI.comment("🔄 'catchprobetestmail' elementi scroll edildi.")

// İsteğe bağlı küçük bir gecikme
WebUI.delay(1)

// Elementin görünür olmasını bekle
WebUI.waitForElementVisible(emailElement, 5)

// Tıklama işlemi
WebUI.click(emailElement)
WebUI.comment("✅ 'catchprobetestmail' elementine tıklandı.")
CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()
WebUI.delay(1)

WebUI.waitForElementVisible(findTestObject('Object Repository/Leakmap/Dashboard/Emaillisttext'), 5)
WebUI.click(findTestObject('Object Repository/Leakmap/Dashboard/Emaillisttext'))
CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()

WebUI.waitForElementVisible(findTestObject('Object Repository/Leakmap/Dashboard/downloaddatabutton'), 5)
WebUI.click(findTestObject('Object Repository/Leakmap/Dashboard/downloaddatabutton'))

WebUI.waitForElementVisible(findTestObject('Object Repository/Leakmap/Dashboard/DownloadToast'), 5)


String mailHost = "imap.gmail.com"
String mailUser = "catchprobe.testmail@gmail.com"
String mailPassword = "eqebhxweatxsocbx"
String keyword = "LeakMap"

// 📭 1. Test başlamadan önce INBOX'ı temizle
//MailReader.clearFolder(mailHost, mailUser, mailPassword, "INBOX")

// Maksimum bekleme süresi (saniye cinsinden)
int maxWaitTime = 180
// Kontrol aralığı (kaç saniyede bir baksın)
int pollingInterval = 10

boolean mailFound = false
int elapsedTime = 0

while (elapsedTime < maxWaitTime) {
	WebUI.comment("📨 Mail kontrol ediliyor... Geçen süre: ${elapsedTime} saniye")

	mailFound = MailReader.checkLatestEmailWithSpam(mailHost, mailUser, mailPassword, keyword)

	if (mailFound) {
		WebUI.comment("✅ Mail geldi!")
		break
	}

	WebUI.delay(pollingInterval)
	elapsedTime += pollingInterval
}

if (!mailFound) {
	WebUI.comment("❌ Mail ${maxWaitTime} saniye içinde gelmedi.")
	WebUI.takeScreenshot()
	assert false : "Beklenen mail gelmedi"
}
// 📭 1. Test bittikten sonra INBOX'ı temizle
MailReader.clearFolder(mailHost, mailUser, mailPassword, "INBOX")


// TestObject Create Report tanımı
TestObject CreateReportElement = findTestObject('Object Repository/Leakmap/Dashboard/CREATE REPORT')

// Elementi bul
WebElement createreport = WebUiCommonHelper.findWebElement(CreateReportElement, 10)

// Scroll işlemi (JS ile)
js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", createreport)
WebUI.comment("🔄 'createreport' elementi scroll edildi.")


// İsteğe bağlı küçük bir gecikme
WebUI.delay(1)
WebUI.click(CreateReportElement)

// Elementin görünür olmasını bekle
WebUI.waitForElementVisible(findTestObject('Object Repository/Leakmap/Dashboard/DownloadReport'), 35)
WebUI.delay(1)
//Download reporta tıkla
WebUI.click(findTestObject('Object Repository/Leakmap/Dashboard/DownloadReport'))

// Onay toast (birebir)
if(!waitToastContains("Report downloaded successfully.", 12))
  KeywordUtil.markFailed("Onay toast'ı birebir gelmedi.")



// Report sekmesini kapat
WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Mitre Close'))



//PWNED Kısmında ilk sonucu git ve al
TestObject firstResult = findTestObject('Object Repository/Leakmap/Dashboard/Pwnedfirst')
WebElement firstResultElement = WebUiCommonHelper.findWebElement(firstResult, 10)

scrollToVisible(firstResultElement, js)
WebUI.delay(1)
WebUI.waitForElementVisible(firstResult, 10)
String firstTitleDesc = WebUI.getText(firstResult)

//PWNED kısmında 2.paginationa git ve filtre at doğrula
TestObject paginationObject = findTestObject('Object Repository/Malwares/Pagination')
WebElement paginationElement = WebUiCommonHelper.findWebElement(paginationObject, 10)

// Pagination görünür hale gelene kadar bekle ve scroll et
WebUI.waitForElementVisible(paginationObject, 10)
scrollToVisible(paginationElement, js)
js.executeScript("window.scrollTo(0, document.body.scrollHeight)")
WebUI.delay(1)

// Pagination öğesine tıkla
WebUI.waitForElementClickable(paginationObject, 10)
WebUI.click(paginationObject)
WebUI.delay(1)

WebUI.waitForElementClickable(findTestObject('Object Repository/Leakmap/Dashboard/3.pagination'),10)
WebUI.click(findTestObject('Object Repository/Leakmap/Dashboard/3.pagination'))
WebUI.delay(2)

// Arama kutusunailk sonucu yaz
TestObject inputObject = findTestObject('Object Repository/Malwares/İnput')
WebElement inputObjectElement = WebUiCommonHelper.findWebElement(inputObject, 10)

scrollToVisible(inputObjectElement, js)
WebUI.waitForElementVisible(inputObject, 10)
WebUI.click(inputObject)
WebUI.setText(inputObject, firstTitleDesc)
WebUI.sendKeys(inputObject, Keys.chord(Keys.ENTER))
WebUI.delay(1)

/*/ Arama butonuna tıkla
TestObject searchButton = findTestObject('Object Repository/Malwares/Searchbutton')
WebUI.waitForElementClickable(searchButton, 10)
WebUI.click(searchButton)
WebUI.delay(2)
/*/

// İkinci sonucu al ve kontrol et
TestObject secondResult = findTestObject('Object Repository/Leakmap/Dashboard/Pwnedfirst')
WebUI.waitForElementVisible(secondResult, 10)
String secondResultDesc = WebUI.getText(secondResult)

// Sonuç doğrulama
assert secondResultDesc.contains(firstTitleDesc)


