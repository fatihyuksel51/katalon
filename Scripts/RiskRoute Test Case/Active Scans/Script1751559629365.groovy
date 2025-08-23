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
TestObject X(String xp) {
	TestObject to = new TestObject(xp)
	to.addProperty("xpath", ConditionType.EQUALS, xp)
	return to
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
/*/ Riskroute sekmesine tıkla
WebUI.navigateToUrl('https://platform.catchprobe.org/riskroute/scan-cron')

WebUI.waitForPageLoad(30)

/*/
/*/ Trigger butonuna bas
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
/*/

WebUI.navigateToUrl('https://platform.catchprobe.org/scans/active-scans')
WebUI.delay(3)
WebUI.waitForPageLoad(10)

WebDriver driver = DriverFactory.getWebDriver()
JavascriptExecutor js = (JavascriptExecutor) driver
Actions actions = new Actions(driver)

// 2️⃣ Tabloda veri var mı kontrol et
TestObject tableRowObj = new TestObject()
tableRowObj.addProperty("xpath", ConditionType.EQUALS, "(//div[contains(@class, 'group relative mb-2')])[2]")


String xpSearch   = "//button[.//span[normalize-space(.)='Search'] or normalize-space(.)='SEARCH']"
// 1) 10 sn bekle; gelmezse refresh
if (!WebUI.waitForElementVisible(X(xpSearch), 20, FailureHandling.OPTIONAL)) {
	KeywordUtil.logInfo("🔄 Arama input'u görünmedi (10 sn). Sayfa refresh ediliyor...")
	WebUI.refresh()
	WebUI.waitForPageLoad(20)
	WebUI.delay(1)

	if (!WebUI.waitForElementVisible(X(xpSearch), 10, FailureHandling.OPTIONAL)) {
		KeywordUtil.markFailedAndStop("Arama input'u refresh sonrası da görünmedi.")
	}
}

if (WebUI.verifyElementPresent(tableRowObj, 5, FailureHandling.OPTIONAL)) {
	
	// Target textini al
	String targetToClick = "catchprobe.org"

	TestObject rowContainingTarget = new TestObject()
	rowContainingTarget.addProperty("xpath", ConditionType.EQUALS, "(//div[contains(@class, 'transition-colors hover') and .//div[text()='catchprobe.org']]//div)[2]")
	safeScrollTo(rowContainingTarget)
	WebUI.waitForElementPresent(rowContainingTarget, 10)
	String targetText = WebUI.getText(rowContainingTarget)
	println "Target: " + targetText
	
	// Go Scan detail butonuna tıkla
	TestObject goScanButton = new TestObject()
	goScanButton.addProperty("xpath", ConditionType.EQUALS, "//div[contains(@class, 'transition-colors hover') and .//div[text()='" + targetToClick + "']]//button[@data-state='closed']/a[contains(@href,'/riskroute/recon')]")
	safeScrollTo(goScanButton)
	WebUI.click(goScanButton)
	
	// 3️⃣ Scan History sayfasında target kontrolü
	TestObject scanHistoryTarget = new TestObject()
	scanHistoryTarget.addProperty("xpath", ConditionType.EQUALS, "//div[contains(@class, 'h-20') and contains(@class, 'rounded-full')]//span")
	safeScrollTo(scanHistoryTarget)
	
	WebUI.waitForElementVisible(scanHistoryTarget, 10)
	String scanTargetText = WebUI.getText(scanHistoryTarget)
	println "Scan History Target: " + scanTargetText
	
	assert scanTargetText.trim() == targetText.trim() : "❌ Scan History sayfasında target eşleşmedi!"

	// 4️⃣ Geri gel
	WebUI.back()
	WebUI.waitForPageLoad(10)
	
	// 6️⃣ Revoke Cron varsa tıkla
	TestObject revokeCronButton = new TestObject()
	revokeCronButton.addProperty("xpath", ConditionType.EQUALS, "(//button[@data-state='closed']/a[contains(@href, '/scans/active-scans')])[1]")
	safeScrollTo(revokeCronButton)
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

    // 5️⃣ Revoke Scan butonlarını bitene kadar iptal et
TestObject revokeScanButton = new TestObject('revokeScanButton')
revokeScanButton.addProperty(
        "xpath",
        ConditionType.EQUALS,
        "(//button[@data-state='closed']/a[contains(@href,'/active-scans')])[1]"
)

// güvenlik için sonsuz döngü koruması
int maxLoops = 20
int revokedCount = 0

for (int i = 0; i < maxLoops; i++) {
    // görünür kısma al
    safeScrollTo(revokeScanButton)

    // hâlâ Revoke Scan var mı?
    if (!WebUI.verifyElementPresent(revokeScanButton, 4, FailureHandling.OPTIONAL)) {
        WebUI.comment("✅ Revoke Scan temizliği bitti. Toplam iptal: ${revokedCount}")
        break
    }

    WebUI.comment("🟠 Revoke Scan #${revokedCount + 1} tıklanıyor…")

    // tıkla (gerekirse JS fallback ekleyebilirsin)
    if (WebUI.waitForElementClickable(revokeScanButton, 5, FailureHandling.OPTIONAL)) {
        WebUI.click(revokeScanButton)
    } else {
        // son bir deneme: kaydır + tekrar dene
        safeScrollTo(revokeScanButton)
        WebUI.click(revokeScanButton, FailureHandling.OPTIONAL)
    }

    // onay diyalogu
    TestObject toRevokeConfirm = findTestObject('Object Repository/Riskroute/Active Scan/Revoke')
    if (WebUI.waitForElementClickable(toRevokeConfirm, 6, FailureHandling.OPTIONAL)) {
        WebUI.click(toRevokeConfirm)
    }

    // başarı mesajı/toast
    WebUI.waitForElementVisible(
        findTestObject('Object Repository/Riskroute/Active Scan/Revoke succes'),
        15,
        FailureHandling.OPTIONAL
    )

    // dialog kapat (varsa)
    TestObject toClose = findTestObject('Object Repository/Threat Actor/Threataa/Page_/Mitre Close')
    if (WebUI.verifyElementPresent(toClose, 3, FailureHandling.OPTIONAL)) {
        WebUI.click(toClose)
    }

    revokedCount++

    // listeyi güncelle: refresh + kısa idle
    WebUI.refresh()
    WebUI.waitForPageLoad(10)
    WebUI.delay(1)   // kendi helper'ın varsa; yoksa WebUI.delay(1) kullan
}

// hâlâ buton görünüyor ve koruma sınırı dolduysa bilgi ver
if (WebUI.verifyElementPresent(revokeScanButton, 2, FailureHandling.OPTIONAL)) {
    WebUI.comment("⚠️ Koruma sınırı (maxLoops=${maxLoops}) aşıldı; hâlâ Revoke Scan görünüyor.")
}




    // 7️⃣ Tablo kalmadıysa testi bitir
    if (!WebUI.verifyElementPresent(tableRowObj, 5, FailureHandling.OPTIONAL)) {
        println "✅ Tablo boş, test tamamlandı."
    }

} else {
    println "✅ Aktif scan yok, test atlandı."
}

