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

import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

// --- TESTİN EN BAŞI ---
ensureSession()   // << tarayıcı yoksa açar, login + OTP yapar
// ----------------------


// === jakarta.mail classpath kontrolü (fail hızlı) ===
try {
  Class.forName('jakarta.mail.Session')
  KeywordUtil.logInfo('✅ jakarta.mail classpath OK')
} catch (ClassNotFoundException e) {
  KeywordUtil.markFailedAndStop('❌ jakarta.mail bulunamadı. Jar dosyaları repo’da değil. Drivers veya Include/jars commit et ve tekrar dene.')
}

// === Yardımcı Fonksiyon ===
WebElement safeScrollTo(TestObject to) {
    if (to == null) {
        KeywordUtil.markFailed("❌ TestObject NULL – Repository yolunu kontrol et.")
        return null
    }
    if (!WebUI.waitForElementPresent(to, 8, FailureHandling.OPTIONAL)) {
        KeywordUtil.logInfo("ℹ️ Element not present: ${to.getObjectId()}")
        return null
    }
    WebElement el = WebUiCommonHelper.findWebElement(to, 8)
    JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getWebDriver()
    js.executeScript("arguments[0].scrollIntoView({block:'center'});", el)
    WebUI.delay(0.5)
    return el
}

// === Gmail bilgileri (direkt kod içinde) ===
String gmailUser   = "alarm.prod.test@gmail.com"
String gmailPass   = "hqdbomloqrnrbsyj" // Gmail App Password (16 haneli)
String mailSubject = "Katalon Mail Triggered"

if (!gmailPass?.trim()) {
    KeywordUtil.markFailedAndStop("❌ Gmail App Password boş!")
}




WebUI.navigateToUrl('https://platform.catchprobe.io/riskroute')
WebUI.waitForPageLoad(10)
// 1. Sayfa yüklendikten sonra mevcut organizasyonu oku
TestObject currentOrg = new TestObject()
currentOrg.addProperty("xpath", ConditionType.EQUALS, "//div[contains(@class, 'font-semibold') and contains(text(), 'Organization')]//span[@class='font-thin']")

String currentOrgText = WebUI.getText(currentOrg)

// 2. Kontrol et: Eğer zaten Katalon ise hiçbir şey yapma
if (currentOrgText != 'Company Test') {
	// 3. Organization butonuna tıkla
	TestObject orgButton = new TestObject()
	orgButton.addProperty("xpath", ConditionType.EQUALS, "//button[.//div[contains(text(), 'Organization :')]]")
	WebUI.click(orgButton)

	// 4. Mail Test seçeneğine tıkla
	TestObject testCompanyOption = new TestObject()
	testCompanyOption.addProperty("xpath", ConditionType.EQUALS, "//button[.//div[text()='Company Test']]")
	WebUI.click(testCompanyOption)
}


WebUI.delay(3)

// === Scan Cron sayfası ===
WebUI.navigateToUrl('https://platform.catchprobe.io/riskroute/scan-cron')
WebUI.waitForPageLoad(10)

TestObject cronDateObj = findTestObject('Object Repository/Scan Cron/LastCronAt')
String cronDateStr = WebUI.getText(cronDateObj).trim()

SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm")
dateFormat.setLenient(false)
String tzId = ((JavascriptExecutor) DriverFactory.getWebDriver())
  .executeScript("return Intl.DateTimeFormat().resolvedOptions().timeZone") as String
dateFormat.setTimeZone(TimeZone.getTimeZone(tzId ?: TimeZone.getDefault().getID()))
 // <-- burası
Date cronDate = dateFormat.parse(cronDateStr)
Date now = new Date()

long diffMillis = now.getTime() - cronDate.getTime()
long diffMinutes = diffMillis / (60 * 1000)

if (diffMinutes > 135) {
    KeywordUtil.markFailed("❌ Last Cron At değeri 135 dakikadan eski: ${cronDateStr}")
}

// === Mail kontrol ===
if (diffMinutes >= 0 && diffMinutes <= 15) {
    // Scan History kontrol
    WebUI.navigateToUrl('https://platform.catchprobe.io/riskroute/scan-history')
    WebUI.waitForPageLoad(10)

    TestObject updatedAtObj = new TestObject().addProperty("xpath", ConditionType.EQUALS,
        "(//tbody//tr[contains(@class,'ant-table-row')]/td[7]/span)[2]")
    safeScrollTo(updatedAtObj)

    String updatedAtStr = WebUI.getText(updatedAtObj).trim()
    Date updatedAtDate = dateFormat.parse(updatedAtStr)

    Date startDate = new Date(updatedAtDate.getTime() - 5 * 60 * 1000)
    Date endDate   = new Date(updatedAtDate.getTime() + 20 * 60 * 1000)

    KeywordUtil.logInfo("📧 Scan History zamanı: ${updatedAtStr}")
    CustomKeywords.'com.catchprobe.utils.MailUtils.checkMailBetweenTimes'(
        gmailUser, gmailPass, mailSubject, startDate, endDate
    )

} else {
    // Cron saati kontrol
    Date startDate = cronDate
    Date endDate   = new Date(cronDate.getTime() + 20 * 60 * 1000)

    CustomKeywords.'com.catchprobe.utils.MailUtils.checkMailBetweenTimes'(
        gmailUser, gmailPass, mailSubject, startDate, endDate
    )
}
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.testobject.ObjectRepository as OR

boolean isBrowserOpen() {
  try { DriverFactory.getWebDriver(); return true } catch(Throwable t) { return false }
}

void ensureSession() {
  if (isBrowserOpen()) return

  WebUI.openBrowser('')
  WebUI.maximizeWindow()
  WebUI.navigateToUrl('https://platform.catchprobe.io/')

  WebUI.waitForElementVisible(
	  OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'), 30)
  WebUI.click(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'))

  WebUI.setText(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'),
				'fatih@test.com')
  WebUI.setEncryptedText(
	  OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Password_password'),
	  'v4yvAQ7Q279BF5ny4hDiTA==')
  WebUI.click(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Sign in'))

  WebUI.delay(3) // sayfa ve OTP kutuları gelsin

  // basit OTP (senin akışındaki gibi dummy)
  def otp = (100000 + new Random().nextInt(900000)).toString()
  WebUI.setText(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'), otp)
  WebUI.click(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Verify'))
  WebUI.delay(2)
}


WebUI.closeBrowser()
