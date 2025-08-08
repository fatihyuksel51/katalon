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
import com.kms.katalon.core.testobject.ConditionType as ConditionType
import com.kms.katalon.core.util.KeywordUtil as KeywordUtil
import org.openqa.selenium.By as By
import org.openqa.selenium.interactions.Actions as Actions
import org.openqa.selenium.support.ui.WebDriverWait as WebDriverWait
import org.openqa.selenium.support.ui.ExpectedConditions as ExpectedConditions
import com.catchprobe.utils.MailReader as MailReader
import static com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords.*
import java.text.SimpleDateFormat

/*/ ✅ Güvenli scroll fonksiyonu

WebUI.openBrowser('')

WebUI.navigateToUrl('https://platform.catchprobe.io/')

WebUI.maximizeWindow()

WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'), 30)

safeScrollTo(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'))

WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'))

WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 30)

safeScrollTo(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'))

WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 'fatih@test.com')

safeScrollTo(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Password_password'))

WebUI.setEncryptedText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Password_password'), 'v4yvAQ7Q279BF5ny4hDiTA==')

safeScrollTo(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Sign in'))

WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Sign in'))

WebUI.delay(5)

String randomOtp = (100000 + new Random().nextInt(900000)).toString()

safeScrollTo(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'))

WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'), randomOtp)

safeScrollTo(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Verify'))

WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Verify'))

WebUI.delay(5)

WebUI.waitForPageLoad(10)



// 1. Sayfa yüklendikten sonra mevcut organizasyonu oku
TestObject currentOrg = new TestObject()
currentOrg.addProperty("xpath", ConditionType.EQUALS, "//div[contains(@class, 'font-semibold') and contains(text(), 'Organization')]//span[@class='font-thin']")

String currentOrgText = WebUI.getText(currentOrg)

// 2. Kontrol et: Eğer zaten TEST COMPANY ise hiçbir şey yapma
if (currentOrgText != 'TEST COMPANY') {
	// 3. Organization butonuna tıkla
	TestObject orgButton = new TestObject()
	orgButton.addProperty("xpath", ConditionType.EQUALS, "//button[.//div[contains(text(), 'Organization :')]]")
	WebUI.click(orgButton)

	// 4. TEST COMPANY seçeneğine tıkla
	TestObject testCompanyOption = new TestObject()
	testCompanyOption.addProperty("xpath", ConditionType.EQUALS, "//button[.//div[text()='TEST COMPANY']]")
	WebUI.click(testCompanyOption)
}
WebUI.waitForPageLoad(10)
/*/

WebUI.navigateToUrl('https://platform.catchprobe.io/smartdeceptive/rdp-player')

WebUI.delay(5) 
WebUI.waitForPageLoad(10)

// 🔹 Pagination ile sayfa geçişi ve signature kontrolü
List<String> pageNumbers = ['2','3','4','5']
for (String pageNum : pageNumbers) {
	try {
		TestObject pageLink = makeXpathObj("//a[text()='" + pageNum + "']")
		scrollToBottom()
		WebUI.delay(2)
		WebUI.click(pageLink)
		WebUI.delay(2)
	} catch (Exception e) {
		WebUI.comment("⚠️ Sayfa ${pageNum} tıklanamadı: ${e.message}")
	}
}
def driver = DriverFactory.getWebDriver()

// 1. Duration hücresindeki saniyeyi al (.//td[contains(@class, 'ant-table-cell')])[5])
TestObject durationCell = makeXpathObj("(.//td[contains(@class, 'ant-table-cell')])[5]")
safeScrollTo(durationCell)
WebElement durationCellt = driver.findElement(By.xpath("(.//td[contains(@class, 'ant-table-cell')])[5]"))
String durationText = durationCellt.getText().trim()
println "Duration hücresi değeri: " + durationText

// Saniyeyi tam sayı olarak al (örn. 3.42 → 3)
int expectedSeconds = Integer.parseInt(durationText.split("\\.")[0])
println "Beklenen saniye: " + expectedSeconds

// 1. Play tuşuna tıkla
WebElement playButton = driver.findElement(By.xpath("(.//td[contains(@class, 'ant-table-cell')])[6]"))
playButton.click()
println "▶️ Play tuşuna basıldı."
WebUI.delay(3.5)

// 2. Video DOM'da görünüyor mu diye kontrol et (maks 5 sn)
int videoWaitMax = 10
boolean videoExists = false
for (int i = 0; i < videoWaitMax; i++) {
    def present = WebUI.executeJavaScript("return document.querySelector('video') !== null;", null)
    if (present) {
        videoExists = true
        break
    }
    WebUI.delay(0.5)
}

if (!videoExists) {
    KeywordUtil.markFailed("❌ Video elementi DOM'a gelmedi.")
    return
}
println "✅ Video elementi DOM’da bulundu."

// 3. Video duration süresini oku
WebUI.delay(1)  // video yüklenmesi için 1 sn buffer

def durationVal = WebUI.executeJavaScript("return document.querySelector('video').duration;", null)
int actualDuration = Math.round(durationVal)
println "🎯 Videonun duration değeri (yuvarlanmış): ${actualDuration}"
println "📦 Duration hücresinden alınan beklenen saniye: ${expectedSeconds}"

// 4. Karşılaştır
if (Math.abs(actualDuration - expectedSeconds) <= 1) {
    println "✅ Test Başarılı: Beklenen = ${expectedSeconds}, Gerçek = ${actualDuration}"
} else {
    KeywordUtil.markFailed("❌ Test Başarısız: Beklenen = ${expectedSeconds}, Gerçek = ${actualDuration}")
}
// Yardımcı fonksiyonlar
TestObject makeXpathObj(String xpath) {
	TestObject to = new TestObject()
	to.addProperty("xpath", ConditionType.EQUALS, xpath)
	return to
}

WebElement safeScrollTo(TestObject to) {
	if (to == null || !WebUI.waitForElementPresent(to, 2)) {
		KeywordUtil.markFailed("❌ Scroll edilemedi: ${to.getObjectId()}")
		return null
	}
	WebElement element = WebUiCommonHelper.findWebElement(to, 5)
	JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getWebDriver()
	js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element)
	WebUI.delay(0.5)
	return element
}

void scrollToBottom() {
	JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getWebDriver()
	js.executeScript("window.scrollTo(0, document.body.scrollHeight)")
}

