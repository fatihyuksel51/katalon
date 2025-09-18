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
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI




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

/*/ Tarayıcıyı aç ve siteye git
WebUI.openBrowser('')

WebUI.navigateToUrl('https://platform.catchprobe.org/')

WebUI.maximizeWindow()

// Login işlemleri
WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'), 30)

WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'))

WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 30)

WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 'fatih.yuksel@catchprobe.com')

WebUI.setEncryptedText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Password_password'), 'RigbBhfdqOBDK95asqKeHw==')

WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Sign in'))

WebUI.delay(5)

// OTP işlemi
def randomOtp = (100000 + new Random().nextInt(900000)).toString()

WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'), randomOtp)

WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Verify'))

WebUI.delay(5)

WebUI.waitForPageLoad(30)

/*/
WebUI.navigateToUrl('https://platform.catchprobe.org/riskroute')

WebUI.waitForPageLoad(10)


WebUI.click(findTestObject('Object Repository/Riskroute/APK Analyzer/APK Analyzer'))

WebUI.delay(3)

WebUI.waitForPageLoad(30)

WebDriver driver = DriverFactory.getWebDriver()
JavascriptExecutor js = (JavascriptExecutor) driver
Actions actions = new Actions(driver)

// Delete butonu tanımı
TestObject deleteButton = new TestObject().addProperty("xpath",
	com.kms.katalon.core.testobject.ConditionType.EQUALS,
	"//div[contains(@class, 'bg-destructive')]")

// Buton var mı kontrol edip varsa tıklayarak döngü
while (WebUI.verifyElementPresent(deleteButton, 3, FailureHandling.OPTIONAL)) {
	WebUI.comment("Delete butonu bulundu, tıklanıyor...")
	WebUI.click(deleteButton)
	CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()
	WebUI.delay(2)
	WebUI.click(findTestObject('Object Repository/Channel Management/Page_/button_DELETE'))
	CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()
	WebUI.waitForElementVisible(findTestObject('Object Repository/Riskroute/APK Analyzer/Toast'), 5)
}

// İşlem bitti
WebUI.comment("Tüm delete butonları silindi. Asset List boş.")


println("Tüm delete ikonları silindi.")
// Create butonu tıkla

WebUI.click(findTestObject('Object Repository/Riskroute/APK Analyzer/Create butonu'))

WebUI.delay(2)

WebUI.waitForPageLoad(30)

// Proje dizin yolunu al
String projectDir = RunConfiguration.getProjectDir()

// Dosyanın tam yolunu oluştur
String filePath = projectDir + "/Include/testfiles/bo.xapk"

// Dosyayı yükle
WebUI.uploadFile(findTestObject('Object Repository/Riskroute/APK Analyzer/uploadFile'), filePath)

// Kontrol için yükleme sonrası bir element görünürlüğü kontrol edilebilir mesela
WebUI.verifyElementPresent(findTestObject('Object Repository/Riskroute/APK Analyzer/uploadSuccess'), 10)


WebUI.click(findTestObject('Object Repository/Riskroute/APK Analyzer/button_CREATE'))

WebUI.delay(2)

WebUI.waitForPageLoad(30)

String expectedFileName = "bo.xapk"


TestObject fileNameCell = new TestObject()
fileNameCell.addProperty("xpath", ConditionType.EQUALS,
	"//span[contains(@class, 'ant-table-cell') and text()='" + expectedFileName + "']")

WebUI.waitForElementVisible(fileNameCell, 10)
String actualFileName = WebUI.getText(fileNameCell)

assert actualFileName == expectedFileName : "❌ Dosya adı eşleşmiyor: " + actualFileName

int retryCount = 0
int maxRetries = 2
boolean isSuccess = false

while (retryCount < maxRetries) {
    TestObject statusCell = new TestObject()
    statusCell.addProperty("xpath", ConditionType.EQUALS,
        "//td[contains(@class, 'ant-table-cell')]//span[text()='success' or text()='pending']")

    WebUI.waitForElementVisible(statusCell, 10)

    String statusText = WebUI.getText(statusCell).trim().toUpperCase()

    println "🔍 Deneme ${retryCount + 1}: Durum -> " + statusText

    if (statusText == "SUCCESS") {
        isSuccess = true
        break
    } else {
        WebUI.refresh()
        WebUI.waitForPageLoad(30)
        WebUI.delay(5)
        retryCount++
    }
}

assert isSuccess : "❌ Status 'SUCCESS' olmadı. 2 denemede de durum başarılı değil."

// ✅ Go Detail butonuna tıkla
TestObject goDetailButton = new TestObject()
goDetailButton.addProperty("xpath", ConditionType.EQUALS,
    "(//div[contains(@class, '50 bg-primary')]/*[name()='svg'])[1]")

WebUI.waitForElementClickable(goDetailButton, 10)
WebUI.click(goDetailButton)
WebUI.delay(2)

WebUI.waitForPageLoad(30)

// Sayfada severity circle geldiğini doğrula
TestObject circle = findTestObject('Object Repository/Riskroute/APK Analyzer/Stix Circle')

// Div'in görünmesini bekle (maksimum 10 saniye)
if (WebUI.waitForElementVisible(circle, 10)) {
	
	// Elementi bul
	WebElement circleelement = WebUI.findWebElement(circle, 10)
	
	// Div'in içinde circle olup olmadığını kontrol et
	Boolean circleExistsRisk = WebUI.executeJavaScript(
		"return arguments[0].querySelector('circle') != null;",
		Arrays.asList(circleelement)
	)
	
	// Durumu logla
	KeywordUtil.logInfo("Stix Package Circle var mı? : " + circleExistsRisk)
	
	if (circleExistsRisk) {
		KeywordUtil.logInfo("Stix Package Circle Veri VAR ✅")
	} else {
		KeywordUtil.logInfo("Stix Package Circle Veri YOK 🚨")
	}
	
} else {
	KeywordUtil.logInfo("Stix Package Circle elementi görünmedi ⏰")
}

WebUI.delay(1)

WebUI.click(findTestObject('Object Repository/Riskroute/APK Analyzer/APK Analyzer'))

WebUI.delay(1)

WebUI.waitForPageLoad(30)



// Buton var mı kontrol edip varsa tıklayarak döngü
while (WebUI.verifyElementPresent(deleteButton, 3, FailureHandling.OPTIONAL)) {
	WebUI.comment("Delete butonu bulundu, tıklanıyor...")
	WebUI.click(deleteButton)
	CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()
	WebUI.delay(2)
	WebUI.click(findTestObject('Object Repository/Channel Management/Page_/button_DELETE'))
	CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()
	WebUI.waitForElementVisible(findTestObject('Object Repository/Riskroute/APK Analyzer/Toast'), 5)
}

// İşlem bitti
WebUI.comment("Tüm delete butonları silindi. Asset List boş.")


println("Tüm delete ikonları silindi.")