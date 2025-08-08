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
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.webui.common.WebUiCommonHelper as WebUiCommonHelper
import com.kms.katalon.core.util.KeywordUtil
import java.text.SimpleDateFormat
import java.util.Date
import org.openqa.selenium.WebElement
import org.openqa.selenium.JavascriptExecutor
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.testobject.ConditionType as ConditionType

// Tarayıcıyı aç ve siteye git
// ✅ Güvenli scroll fonksiyonu
WebElement safeScrollTo(TestObject to) {
	if (to == null) {
		KeywordUtil.markFailed("❌ TestObject NULL – Repository yolunu kontrol et.")
		return null
	}
	if (!WebUI.waitForElementPresent(to, 5, FailureHandling.OPTIONAL)) {
		KeywordUtil.logInfo("ℹ️ Element not present, scroll işlemi atlandı: ${to.getObjectId()}")
		return null
	}
	WebElement element = WebUiCommonHelper.findWebElement(to, 5)
	JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getWebDriver()
	js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element)
	WebUI.delay(0.5)
	return element
}
/*/

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

WebUI.delay(2)

WebUI.waitForPageLoad(5)
/*/


WebUI.delay(3)

WebUI.waitForPageLoad(10)
// 1. Sayfa yüklendikten sonra mevcut organizasyonu oku
TestObject currentOrg = new TestObject()
currentOrg.addProperty("xpath", ConditionType.EQUALS, "//div[contains(@class, 'font-semibold') and contains(text(), 'Organization')]//span[@class='font-thin']")

String currentOrgText = WebUI.getText(currentOrg)

// 2. Kontrol et: Eğer zaten TEST COMPANY ise hiçbir şey yapma
if (currentOrgText != 'Mail Test') {
	// 3. Organization butonuna tıkla
	TestObject orgButton = new TestObject()
	orgButton.addProperty("xpath", ConditionType.EQUALS, "//button[.//div[contains(text(), 'Organization :')]]")
	WebUI.click(orgButton)

	// 4. Mail Test seçeneğine tıkla
	TestObject testCompanyOption = new TestObject()
	testCompanyOption.addProperty("xpath", ConditionType.EQUALS, "//button[.//div[text()='Mail Test']]")
	WebUI.click(testCompanyOption)
}

WebUI.delay(3)

//Scan crona git
WebUI.navigateToUrl('https://platform.catchprobe.org/riskroute/scan-cron')
WebUI.maximizeWindow()
WebUI.waitForPageLoad(10)

// 📌 Last Cron At tarihini al
TestObject cronDateObj = findTestObject('Object Repository/Scan Cron/LastCronAt')
String cronDateStr = WebUI.getText(cronDateObj).trim()
SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm")
Date cronDate = dateFormat.parse(cronDateStr)
Date now = new Date()

long diffMillis = now.getTime() - cronDate.getTime()

// ⛔ 135 dakikadan (2 saat 15 dk) eski cron fail
if (diffMillis > 135 * 60 * 1000) {
	KeywordUtil.markFailed("❌ Last Cron At değeri 135 dakikadan eski: ${cronDateStr}")
}


// ✅ 0–15 dk arasıysa → Scan History Updated At üzerinden test
else if (diffMillis >= 0 && diffMillis <= 15 * 60 * 1000) {
	KeywordUtil.logInfo("⏳ Cron taze, Scan History kontrolü yapılacak: ${cronDateStr}")

	WebUI.navigateToUrl('https://platform.catchprobe.org/riskroute/scan-history')
	WebUI.waitForPageLoad(10)

	TestObject updatedAtObj = new TestObject()
	updatedAtObj.addProperty("xpath", com.kms.katalon.core.testobject.ConditionType.EQUALS,
		"(//tbody//tr[contains(@class,'ant-table-row')]/td[8]/span)[2]")

	WebElement updatedAtEl = WebUiCommonHelper.findWebElement(updatedAtObj, 5)
	JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getWebDriver()
	js.executeScript("arguments[0].scrollIntoView(true);", updatedAtEl)
	WebUI.delay(0.5)

	String updatedAtStr = WebUI.getText(updatedAtObj).trim()
	KeywordUtil.logInfo("📌 Scan History 2. satır Updated At: ${updatedAtStr}")

	Date updatedAtDate = dateFormat.parse(updatedAtStr)
	Date startDate = new Date(updatedAtDate.getTime() - 5 * 60 * 1000)
	Date endDate   = new Date(updatedAtDate.getTime() + 20 * 60 * 1000)

	KeywordUtil.logInfo("📧 Mail kontrolü başlatılıyor (Scan History): ${startDate} - ${endDate}")
	CustomKeywords.'com.catchprobe.utils.MailUtils.checkMailBetweenTimes'(
		"alarm.rule@gmail.com",
		"cxdiuswtfvknhlte",
		"Katalon Mail Triggered",
		startDate,
		endDate
	)
	KeywordUtil.logInfo("📧 Mail kontrolü tamamlandı (SCAN HISTORY üzerinden).")
}

// ✅ 15 dk–2 saat arasıysa → doğrudan cron saatiyle mail kontrolü
else {
	KeywordUtil.logInfo("⏳ Cron tarihi 15 dk ile 2 saat arasında, mail kontrolü cron saati ile yapılacak.")

	Date startDate = cronDate
	Date endDate   = new Date(cronDate.getTime() + 20 * 60 * 1000)

	KeywordUtil.logInfo("📧 Mail kontrolü başlatılıyor (CRON üzerinden): ${startDate} - ${endDate}")
	CustomKeywords.'com.catchprobe.utils.MailUtils.checkMailBetweenTimes'(
		"alarm.rule@gmail.com",
		"cxdiuswtfvknhlte",
		"Katalon Mail Triggered",
		startDate,
		endDate
	)
	KeywordUtil.logInfo("📧 Mail kontrolü tamamlandı (CRON üzerinden).")
}