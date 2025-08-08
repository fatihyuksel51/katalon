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

String mailHost = "imap.gmail.com"
String mailUser = "catchprobe.testmail@gmail.com"
String mailPassword = "eqebhxweatxsocbx"
String keyword = "LeakMap"

// 📭 1. Test başlamadan önce INBOX'ı temizle
MailReader.clearFolder(mailHost, mailUser, mailPassword, "INBOX")

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

