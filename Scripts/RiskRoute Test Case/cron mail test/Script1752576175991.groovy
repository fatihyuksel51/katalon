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
String gmailUser   = "alarm.rule@gmail.com"
String gmailPass   = "cxdiuswtfvknhlte" // Gmail App Password (16 haneli)
String mailSubject = "Katalon Mail Triggered"

if (!gmailPass?.trim()) {
    KeywordUtil.markFailedAndStop("❌ Gmail App Password boş!")
}


/*/ === Tarayıcı aç / Login / OTP ===
WebUI.openBrowser('')
WebUI.maximizeWindow()

WebUI.navigateToUrl('https://platform.catchprobe.org/')

// Login adımları
WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'), 30)
WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'))

WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 'katalon.test@catchprobe.com')
WebUI.setEncryptedText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Password_password'), 'RigbBhfdqOBDK95asqKeHw==')
WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Sign in'))

WebUI.delay(5)

// Random OTP
def randomOtp = (100000 + new Random().nextInt(900000)).toString()
WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'), randomOtp)
WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Verify'))
WebUI.delay(2)
/*/

// === Organization kontrol ===
TestObject currentOrg = new TestObject().addProperty("xpath", ConditionType.EQUALS,
    "//div[contains(@class, 'font-semibold') and contains(text(), 'Organization')]//span[@class='font-thin']")
String currentOrgText = WebUI.getText(currentOrg)

if (currentOrgText != 'Mail Test') {
    TestObject orgButton = new TestObject().addProperty("xpath", ConditionType.EQUALS,
        "//button[.//div[contains(text(), 'Organization :')]]")
    WebUI.click(orgButton)

    TestObject mailTestOpt = new TestObject().addProperty("xpath", ConditionType.EQUALS,
        "//button[.//div[normalize-space(text())='Mail Test']]")
    WebUI.click(mailTestOpt)
}

WebUI.delay(3)

// === Scan Cron sayfası ===
WebUI.navigateToUrl('https://platform.catchprobe.org/riskroute/scan-cron')
WebUI.waitForPageLoad(10)

TestObject cronDateObj = findTestObject('Object Repository/Scan Cron/LastCronAt')
String cronDateStr = WebUI.getText(cronDateObj).trim()

SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm")
dateFormat.setLenient(false)
dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Istanbul"))  // <-- burası
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
    WebUI.navigateToUrl('https://platform.catchprobe.org/riskroute/scan-history')
    WebUI.waitForPageLoad(10)

    TestObject updatedAtObj = new TestObject().addProperty("xpath", ConditionType.EQUALS,
        "(//tbody//tr[contains(@class,'ant-table-row')]/td[8]/span)[2]")
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

WebUI.closeBrowser()
