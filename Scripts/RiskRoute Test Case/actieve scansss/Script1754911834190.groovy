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

/*/
// Riskroute sekmesine tıkla
WebUI.navigateToUrl('https://platform.catchprobe.org/riskroute/scan-cron')

WebUI.waitForPageLoad(30)

//
// Trigger butonuna bas
TestObject triggerButton = new TestObject().addProperty("xpath", ConditionType.EQUALS, "//div[contains(@class, 'bg-emerald')]")
WebUI.click(triggerButton)
WebUI.comment("Trigger butonuna tıklandı")
WebUI.delay(2)
WebUI.click(findTestObject('Object Repository/Scan Cron/TRIGGER'))
CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()
WebUI.waitForElementVisible(findTestObject('Object Repository/Scan Cron/Trigger Toast'), 15)
WebUI.refresh()
WebUI.delay(2)
WebUI.waitForPageLoad(30)

WebUI.delay(3)
//

WebUI.click(findTestObject('Object Repository/Scan/Scan'))

WebUI.waitForPageLoad(30)

WebUI.click(findTestObject('Object Repository/Riskroute/Active Scan/Active Scans'))

WebDriver driver = DriverFactory.getWebDriver()
JavascriptExecutor js = (JavascriptExecutor) driver
Actions actions = new Actions(driver)

// 2️⃣ Tabloda veri var mı kontrol et
TestObject tableRowObj = new TestObject()
tableRowObj.addProperty("xpath", ConditionType.EQUALS, "(//div[contains(@class, 'group relative mb-2')])[2]")

if (WebUI.verifyElementPresent(tableRowObj, 5, FailureHandling.OPTIONAL)) {
	
	// Target textini al
	String targetToClick = "catchprobe.org"

	TestObject rowContainingTarget = new TestObject()
	rowContainingTarget.addProperty("xpath", ConditionType.EQUALS, "(//div[contains(@class, 'transition-colors hover') and .//div[text()='catchprobe.org']]//div)[2]")

	WebUI.waitForElementPresent(rowContainingTarget, 10)
	String targetText = WebUI.getText(rowContainingTarget)
	println "Target: " + targetText
	
	// Go Scan detail butonuna tıkla
	TestObject goScanButton = new TestObject()
	goScanButton.addProperty("xpath", ConditionType.EQUALS, "//div[contains(@class, 'transition-colors hover') and .//div[text()='" + targetToClick + "']]//button[@data-state='closed']/a[contains(@href,'/riskroute/recon')]")
	
	WebUI.click(goScanButton)
	
	// 3️⃣ Scan History sayfasında target kontrolü
	TestObject scanHistoryTarget = new TestObject()
	scanHistoryTarget.addProperty("xpath", ConditionType.EQUALS, "//div[contains(@class, 'h-20') and contains(@class, 'rounded-full')]//span")
	
	WebUI.waitForElementVisible(scanHistoryTarget, 10)
	String scanTargetText = WebUI.getText(scanHistoryTarget)
	println "Scan History Target: " + scanTargetText
	
	assert scanTargetText.trim() == targetText.trim() : "❌ Scan History sayfasında target eşleşmedi!"

	// 4️⃣ Geri gel
	WebUI.back()
	WebUI.waitForPageLoad(10)

    // 5️⃣ Revoke Scan varsa tıkla
    TestObject revokeScanButton = new TestObject()
    revokeScanButton.addProperty("xpath", ConditionType.EQUALS, "(//button[@data-state='closed']/a[contains(@href,'/active-scans')])[2]")

    if (WebUI.verifyElementPresent(revokeScanButton, 5, FailureHandling.OPTIONAL)) {
        println "🟠 Revoke Scan butonu bulundu, tıklanıyor..."
        WebUI.click(revokeScanButton)
        WebUI.delay(1)

        // Revoke confirm butonu
        WebUI.click(findTestObject('Object Repository/Riskroute/Active Scan/Revoke'))
        WebUI.waitForElementVisible(findTestObject('Object Repository/Riskroute/Active Scan/Revoke succes'), 15)

        // Kapat
        WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Mitre Close'))
        WebUI.refresh()
        WebUI.delay(1)
        WebUI.waitForPageLoad(10)
    }

    // 6️⃣ Revoke Cron varsa tıkla
    TestObject revokeCronButton = new TestObject()
    revokeCronButton.addProperty("xpath", ConditionType.EQUALS, "(//button[@data-state='closed']/a[contains(@href, '/scans/active-scans')])[1]")

    if (WebUI.verifyElementPresent(revokeCronButton, 5, FailureHandling.OPTIONAL)) {
        println "🟠 Revoke Cron butonu bulundu, tıklanıyor..."
        WebUI.click(revokeCronButton)
        WebUI.delay(1)

        // Revoke confirm butonu
        WebUI.click(findTestObject('Object Repository/Riskroute/Active Scan/Revoke'))
        WebUI.waitForElementVisible(findTestObject('Object Repository/Riskroute/Active Scan/Revoke succes'), 15)

        // Kapat
        WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Mitre Close'))
        WebUI.refresh()
        WebUI.delay(1)
        WebUI.waitForPageLoad(10)
    }

    // 7️⃣ Tablo kalmadıysa testi bitir
    if (!WebUI.verifyElementPresent(tableRowObj, 5, FailureHandling.OPTIONAL)) {
        println "✅ Tablo boş, test tamamlandı."
    }

} else {
    println "✅ Aktif scan yok, test atlandı."
}
