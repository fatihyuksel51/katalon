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

// Tarayıcıyı aç ve siteye git
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

//
// Riskroute sekmesine tıkla
WebUI.navigateToUrl('https://platform.catchprobe.org/riskroute')

WebUI.waitForPageLoad(30)

CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()

WebUI.waitForElementClickable(findTestObject('Object Repository/RiskRoute Dashboard/Page_/div_Total Assets'), 20)

WebUI.verifyElementText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/div_Total Assets'), 'Total Assets')

WebUI.verifyElementText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/div_Total Subdomains'), 'Total Subdomains')

WebUI.verifyElementText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/div_Total Vulnerabilities'), 'Total Vulnerabilities')

// WebDriver ve JS tanımla
WebDriver driver = DriverFactory.getWebDriver()
JavascriptExecutor js = (JavascriptExecutor) driver

// Buton elementi al
WebElement MostCommonİgnore = WebUiCommonHelper.findWebElement(findTestObject('Object Repository/RiskRoute Dashboard/Page_/MostCommonİgnore'), 10)

// 1️⃣ Most Common Vulnerabilities buton nesnesi oluştur
TestObject MostCommon = new TestObject()
MostCommon.addProperty("xpath", ConditionType.EQUALS, "//button[contains(@class,'peer h-5 w-5 rounded-full border')]")

if (scrollToVisible(MostCommonİgnore, js)) {
	js.executeScript("arguments[0].click();", MostCommonİgnore)
	WebUI.comment("👉 'MostCommon' butonuna tıklandı.")
	WebUI.waitForPageLoad(30)
} else {
	WebUI.comment("❌ 'MostCommon' butonu görünür değil, tıklanamadı.")
}




// 3️⃣ Aria ve data-state attribute değerini oku
String ariaChecked = WebUI.getAttribute(MostCommon, "aria-checked")
String dataState = WebUI.getAttribute(MostCommon, "data-state")

// 4️⃣ Attribute'ları kontrol et ve log bas
if (ariaChecked == "true" && dataState == "checked") {
    WebUI.comment("✅ Buton başarıyla işaretlendi.")
} else {
    WebUI.comment("❌ Buton işaretlenmedi.")
}

// 1️⃣ Most Vulnerable  buton nesnesi oluştur
TestObject MostVulnerable = new TestObject()
MostVulnerable.addProperty("xpath", ConditionType.EQUALS, "(//button[contains(@class,'peer h-5 w-5 rounded-full border')])[2]")

// 2️⃣ Butona tıkla
WebUI.click(MostVulnerable)
WebUI.waitForPageLoad(30)


// 3️⃣ Aria ve data-state attribute değerini oku
String ariaCheckedVulner = WebUI.getAttribute(MostVulnerable, "aria-checked")
String dataStateVulner = WebUI.getAttribute(MostVulnerable, "data-state")

// 4️⃣ Attribute'ları kontrol et ve log bas
if (ariaCheckedVulner == "true" && dataStateVulner == "checked") {
	WebUI.comment("✅ Buton başarıyla işaretlendi.")
} else {
	WebUI.comment("❌ Buton işaretlenmedi.")
}

// Target text al
String Targettext = WebUI.getText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/Asset List Target'))

// Targettext'ten '1 - ' kısmını temizle
Targettext = Targettext.replaceAll("^\\d+\\s-\\s", "")
WebUI.comment("Temizlenmiş Targettext : " + Targettext)

// Tıkla
WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/Asset List Target'))
WebUI.delay(3)

// Asset List Target Info'dan text al
String InfoText = WebUI.getText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/Asset List Target İnfo'))

// Karşılaştırma
assert InfoText.contains(Targettext)
WebUI.back()
WebUI.delay(3)
WebUI.waitForPageLoad(30)


WebElement vulnerabilityriskscoreelementscroll = WebUiCommonHelper.findWebElement(findTestObject('Object Repository/RiskRoute Dashboard/Page_/Vulnerability Breakdown'), 20)

if (scrollToVisible(vulnerabilityriskscoreelementscroll, js)) {
	js.executeScript("arguments[0].click();", vulnerabilityriskscoreelementscroll)
	WebUI.comment("👉 'vulnerabilityriskscoreelement' butonuna tıklandı.")
	WebUI.waitForPageLoad(30)
} else {
	WebUI.comment("❌ 'vulnerabilityriskscoreelement' butonu görünür değil, tıklanamadı.")
}

//Vulnerability Breakdown by Severity kısmında grafik doğrulaması yap
TestObject VulnerabilityBreakDownRisckScore = findTestObject('Object Repository/Platform IOC Discoveries/Smartdeceptive içerik')

// Div'in görünmesini bekle (maksimum 10 saniye)
if (WebUI.waitForElementVisible(VulnerabilityBreakDownRisckScore, 15)) {
	
	// Elementi bul
	WebElement vulnerabilityriskscoreelement = WebUI.findWebElement(VulnerabilityBreakDownRisckScore, 10)
	
	
	// Div'in içinde SVG olup olmadığını kontrol et
	Boolean VulnerabilitydetailsvgExistsRisk = WebUI.executeJavaScript(
		"return arguments[0].querySelector('svg') != null;",
		Arrays.asList(vulnerabilityriskscoreelement)
	)
	
	// Durumu logla
	KeywordUtil.logInfo("Vulnerability Breakdown by Severity Veri SVG var mı? : " + VulnerabilitydetailsvgExistsRisk)
	
	if (VulnerabilitydetailsvgExistsRisk) {
		KeywordUtil.logInfo("Vulnerability Breakdown by Severity Veri VAR ✅")
		
		
	} else {
		KeywordUtil.logInfo("Vulnerability Breakdown by Severity Veri YOK 🚨")
	}
	
} else {
	KeywordUtil.logInfo("Vulnerability Breakdown by Severity Veri Svg elementi görünmedi ⏰")
}
// Risk Score Veri doğrulaması yap

WebElement riskscorescroll = WebUiCommonHelper.findWebElement(findTestObject('Object Repository/RiskRoute Dashboard/Page_/Risck Score'), 20)

if (scrollToVisible(riskscorescroll, js)) {
	js.executeScript("arguments[0].click();", riskscorescroll)
	WebUI.comment("👉 'riskscorescroll' butonuna tıklandı.")
	WebUI.waitForPageLoad(30)
} else {
	WebUI.comment("❌ 'riskscorescroll' butonu görünür değil, tıklanamadı.")
}

//Vulnerability Breakdown by Severity kısmında grafik doğrulaması yap
TestObject RisckScore = findTestObject('Object Repository/RiskRoute Dashboard/Page_/Risck Score')

// Div'in görünmesini bekle (maksimum 10 saniye)
if (WebUI.waitForElementVisible(RisckScore, 15)) {
	
	// Elementi bul
	WebElement riskscoreelement = WebUI.findWebElement(RisckScore, 10)
	
	
	// Div'in içinde SVG olup olmadığını kontrol et
	Boolean RiskScoresvgExistsRisk = WebUI.executeJavaScript(
		"return arguments[0].querySelector('svg') != null;",
		Arrays.asList(riskscoreelement)
	)
	
	// Durumu logla
	KeywordUtil.logInfo("Risk Score SVG var mı? : " + RiskScoresvgExistsRisk)
	
	if (RiskScoresvgExistsRisk) {
		KeywordUtil.logInfo("Risk Score Veri VAR ✅")
		
		
	} else {
		KeywordUtil.logInfo("Risk Score Veri YOK 🚨")
	}
	
} else {
	KeywordUtil.logInfo("Risk Score Svg elementi görünmedi ⏰")
}

/*/ Most Common CVE IDs de Veri doğrulaması yap
TestObject MostCommenCVE = findTestObject('Object Repository/RiskRoute Dashboard/Page_/MostCommonCve')

// Div'in görünmesini bekle (maksimum 10 saniye)
if (WebUI.waitForElementVisible(MostCommenCVE, 15)) {
	
	// Elementi bul
	WebElement MostCommenCVEelement = WebUI.findWebElement(MostCommenCVE, 10)
	
	
	// Div'in içinde SVG olup olmadığını kontrol et
	Boolean MostCommonesvgExistsRisk = WebUI.executeJavaScript(
		"return arguments[0].querySelector('svg') != null;",
		Arrays.asList(MostCommenCVEelement)
	)
	
	// Durumu logla
	KeywordUtil.logInfo("Most Common SVG var mı? : " + MostCommonesvgExistsRisk)
	
	if (MostCommonesvgExistsRisk) {
		KeywordUtil.logInfo("Most Common Veri VAR ✅")
		
		
	} else {
		KeywordUtil.logInfo("Most Common Veri YOK 🚨")
	}
	
} else {
	KeywordUtil.logInfo("Most Common Svg elementi görünmedi ⏰")
}
/*/
// Most Common CWE IDs de Veri doğrulaması yap
WebElement mostcommoncweid = WebUiCommonHelper.findWebElement(findTestObject('Object Repository/RiskRoute Dashboard/Page_/Most Common CWE ID'), 20)

if (scrollToVisible(mostcommoncweid, js)) {
	js.executeScript("arguments[0].click();", mostcommoncweid)
	WebUI.comment("👉 'mostcommoncwetext' butonuna tıklandı.")
	WebUI.waitForPageLoad(30)
} else {
	WebUI.comment("❌ 'mostcommoncwetext' butonu görünür değil, tıklanamadı.")
}
TestObject MostCommenCWE = findTestObject('Object Repository/RiskRoute Dashboard/Page_/MostCommonCwe')

// Div'in görünmesini bekle (maksimum 10 saniye)
if (WebUI.waitForElementVisible(MostCommenCWE, 15)) {
	
	// Elementi bul
	WebElement MostCommenCWEelement = WebUI.findWebElement(MostCommenCWE, 10)
	
	
	// Div'in içinde SVG olup olmadığını kontrol et
	Boolean MostCommonCWEesvgExistsRisk = WebUI.executeJavaScript(
		"return arguments[0].querySelector('svg') != null;",
		Arrays.asList(MostCommenCWEelement)
	)
	
	// Durumu logla
	KeywordUtil.logInfo("Most Common SVG var mı? : " + MostCommonCWEesvgExistsRisk)
	
	if (MostCommonCWEesvgExistsRisk) {
		KeywordUtil.logInfo("Most Common CWE Veri VAR ✅")
		
		
	} else {
		KeywordUtil.logInfo("Most Common CWE Veri YOK 🚨")
	}
	
} else {
	KeywordUtil.logInfo("Most Common CWE Svg elementi görünmedi ⏰")
}


// Most Common Vulnerability Tags da Veri doğrulaması yap

TestObject mostcommonvulnerabilitytag = findTestObject('Object Repository/RiskRoute Dashboard/Page_/MostCommonCwe')

// Div'in görünmesini bekle (maksimum 10 saniye)
if (WebUI.waitForElementVisible(mostcommonvulnerabilitytag, 15)) {
	
	// Elementi bul
	WebElement mostcommonvulnerabilitytagelement = WebUI.findWebElement(mostcommonvulnerabilitytag, 10)
	
	
	// Div'in içinde SVG olup olmadığını kontrol et
	Boolean MostcommonvulnerabilitytagsvgExistsRisk = WebUI.executeJavaScript(
		"return arguments[0].querySelector('svg') != null;",
		Arrays.asList(mostcommonvulnerabilitytagelement)
	)
	
	// Durumu logla
	KeywordUtil.logInfo("Most Common Vulnerability Tag SVG var mı? : " + MostcommonvulnerabilitytagsvgExistsRisk)
	
	if (MostcommonvulnerabilitytagsvgExistsRisk) {
		KeywordUtil.logInfo("Most Common Vulnerability Tag Veri VAR ✅")
		
		
	} else {
		KeywordUtil.logInfo("Most Common Vulnerability Tag Veri YOK 🚨")
	}
	
} else {
	KeywordUtil.logInfo("Most Common Vulnerability Tag Svg elementi görünmedi ⏰")
}

/*/ Most Common Technology Tags da Veri doğrulaması yap

TestObject mostcommontechnology = findTestObject('Object Repository/RiskRoute Dashboard/Page_/mostcommontechnology')

// Div'in görünmesini bekle (maksimum 10 saniye)
if (WebUI.waitForElementVisible(mostcommontechnology, 15)) {
	
	// Elementi bul
	WebElement mostcommontechnologytagelement = WebUI.findWebElement(mostcommontechnology, 10)
	
	
	// Div'in içinde SVG olup olmadığını kontrol et
	Boolean MostcommontechnologysvgExistsRisk = WebUI.executeJavaScript(
		"return arguments[0].querySelector('svg') != null;",
		Arrays.asList(mostcommontechnologytagelement)
	)
	
	// Durumu logla
	KeywordUtil.logInfo("Most Common Technolog SVG var mı? : " + MostcommontechnologysvgExistsRisk)
	
	if (MostcommontechnologysvgExistsRisk) {
		KeywordUtil.logInfo("Most Common Technolog Veri VAR ✅")
		
		
	} else {
		KeywordUtil.logInfo("Most Common Technology Veri YOK 🚨")
	}
	
} else {
	KeywordUtil.logInfo("Most Common Technolog Svg elementi görünmedi ⏰")
}
/*/
// Asset Detail (Domain) Pagination Test
// =========================================================================

WebUI.comment("--- Starting Asset Detail (Domain) Pagination Test ---")

// Scroll to Asset Detail graph
WebElement assetDetailGraphElement = WebUiCommonHelper.findWebElement(
    findTestObject('Object Repository/RiskRoute Dashboard/Page_/Asset Detail'), 20)

if (scrollToVisible(assetDetailGraphElement, js)) {
    js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", assetDetailGraphElement)
    WebUI.comment("👉 'Asset Daily Count' grafiğine başarıyla scroll yapıldı.")
    WebUI.delay(1)
} else {
    WebUI.comment("❌ 'Asset Detail' grafiği görünür değil, scroll başarısız.")
}

// Find path element for 'domain' series
TestObject pathElementDomain = new TestObject()
pathElementDomain.addProperty("xpath", ConditionType.EQUALS, "//*[local-name()='g' and contains(@class, 'apexcharts-series') and contains(@class, 'apexcharts-pie-series') and @seriesName='domain']/*[local-name()='path']")

// Get WebElement for 'domain' path
WebElement pathWebElementDomain = WebUiCommonHelper.findWebElement(pathElementDomain, 10)

// Read data:value attribute for 'domain'
String dataValueStrDomain = pathWebElementDomain.getAttribute("data:value")
println("Bulunan data:value (Domain) = " + dataValueStrDomain)

// Convert to integer
int dataValueDomain = dataValueStrDomain.toInteger()

if (dataValueDomain > 0) {
    // Click on the path
    pathWebElementDomain.click()
    WebUI.delay(3) // Wait for the new page to load

    // Calculate expected pagination count (assuming 10 records per page)
    int expectedPageCountDomain = (int) Math.ceil(dataValueDomain / 10.0)
    println("🎯 Beklenen pagination sayısı (Domain - 10 kayıt/sayfa): " + expectedPageCountDomain)

    // Find all visible page number links
    TestObject pageNumberLinksDomain = new TestObject()
    pageNumberLinksDomain.addProperty("xpath", ConditionType.EQUALS,
        "//ul[contains(@class,'flex')]/li[a[not(contains(@aria-label,'previous')) and not(contains(@aria-label,'next'))]]/a")

    List<WebElement> visiblePageNumberElementsDomain = WebUiCommonHelper.findWebElements(pageNumberLinksDomain, 10)

    // Scroll to the pagination if not visible
    if (!visiblePageNumberElementsDomain.isEmpty()) {
        scrollToVisible(visiblePageNumberElementsDomain.get(0), js) // Scroll to the first page number
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)") // Ensure the entire pagination is visible
        WebUI.delay(1)
    }

    int actualLastPageNumberDomain = 0
    if (!visiblePageNumberElementsDomain.isEmpty()) {
        for (WebElement pageElement : visiblePageNumberElementsDomain) {
            String pageText = pageElement.getText().trim()
            if (pageText.matches("\\d+")) { // Check if the text is a number
                int currentPageNumber = Integer.parseInt(pageText)
                if (currentPageNumber > actualLastPageNumberDomain) {
                    actualLastPageNumberDomain = currentPageNumber
                }
            }
        }
    }

    println("🔢 Gerçek son pagination numarası (Domain): " + actualLastPageNumberDomain)

    // Verify the page count
    if (expectedPageCountDomain == actualLastPageNumberDomain) {
    WebUI.comment("✅ Domain pagination sayısı doğru: ${actualLastPageNumberDomain}")
} else {
    KeywordUtil.markFailed("❌ Domain pagination count verification failed. Beklenen: ${expectedPageCountDomain}, Bulunan: ${actualLastPageNumberDomain}")
}

} else {
    WebUI.comment("❗ Test atlandı çünkü data:value (Domain) 0 veya negatif: " + dataValueDomain)
}

WebUI.back()
WebUI.delay(3)
WebUI.waitForPageLoad(30)
WebUI.comment("--- Finished Asset Detail (Domain) Pagination Test ---")


// =========================================================================
// Asset Detail (IP) Pagination Test
// =========================================================================

WebUI.comment("--- Starting Asset Detail (IP) Pagination Test ---")

// Scroll to Asset Detail graph for IP
WebElement assetDetailGraphElementIp = WebUiCommonHelper.findWebElement(
    findTestObject('Object Repository/RiskRoute Dashboard/Page_/Asset Detail'), 20)

if (scrollToVisible(assetDetailGraphElementIp, js)) {
    js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", assetDetailGraphElementIp)
    WebUI.comment("👉 'Asset Detail' grafiğine başarıyla scroll yapıldı (IP).")
    WebUI.delay(1)
} else {
    WebUI.comment("❌ 'Asset Detail' grafiği görünür değil, scroll başarısız (IP).")
}

// Find path element for 'ip' series
TestObject pathElementIp = new TestObject()
pathElementIp.addProperty("xpath", ConditionType.EQUALS, "//*[local-name()='g' and contains(@class, 'apexcharts-series') and contains(@class, 'apexcharts-pie-series') and @seriesName='ip']/*[local-name()='path']")

// Get WebElement for 'ip' path
WebElement pathWebElementIp = WebUiCommonHelper.findWebElement(pathElementIp, 10)

// Read data:value attribute for 'ip'
String dataValueStrIp = pathWebElementIp.getAttribute("data:value")
println("Bulunan data:value (IP) = " + dataValueStrIp)

// Convert to integer
int dataValueIp = dataValueStrIp.toInteger()

if (dataValueIp > 0) {
    // Click on the path
    pathWebElementIp.click()
    WebUI.delay(3) // Wait for the new page to load

    // Beklenen pagination sayısını hesapla (her sayfa 10 kayıt alıyor varsayımıyla)
    int expectedPageCountIp = (int) Math.ceil(dataValueIp / 10.0)
    println("🎯 Beklenen pagination sayısı (IP - 10 kayıt/sayfa): " + expectedPageCountIp)

    // Find all visible page number links for IP
    TestObject pageNumberLinksIp = new TestObject()
    pageNumberLinksIp.addProperty("xpath", ConditionType.EQUALS,
        "//ul[contains(@class,'flex')]/li[a[not(contains(@aria-label,'previous')) and not(contains(@aria-label,'next'))]]/a")

    List<WebElement> visiblePageNumberElementsIp = WebUiCommonHelper.findWebElements(pageNumberLinksIp, 10)

    // Scroll to the pagination if not visible
    if (!visiblePageNumberElementsIp.isEmpty()) {
        scrollToVisible(visiblePageNumberElementsIp.get(0), js) // Scroll to the first page number
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)") // Ensure the entire pagination is visible
        WebUI.delay(1)
    }

    int actualLastPageNumberIp = 0
    if (!visiblePageNumberElementsIp.isEmpty()) {
        for (WebElement pageElement : visiblePageNumberElementsIp) {
            String pageText = pageElement.getText().trim()
            if (pageText.matches("\\d+")) { // Check if the text is a number
                int currentPageNumber = Integer.parseInt(pageText)
                if (currentPageNumber > actualLastPageNumberIp) {
                    actualLastPageNumberIp = currentPageNumber
                }
            }
        }
    }

    println("🔢 Gerçek son pagination numarası (IP): " + actualLastPageNumberIp)

    // Verify the page count
    WebUI.verifyEqual(actualLastPageNumberIp, expectedPageCountIp)

} else {
    WebUI.comment("❗ Test atlandı çünkü data:value (IP) 0 veya negatif: " + dataValueIp)
}

WebUI.back()
WebUI.delay(3)
WebUI.waitForPageLoad(30)
WebUI.comment("--- Finished Asset Detail (IP) Pagination Test ---")


// =========================================================================
// Alarm History (ScanxStart) Pagination Test
// =========================================================================

WebUI.comment("--- Starting Alarm History (ScanxStart) Pagination Test ---")

// Scroll to Alarm History graph
WebElement alarmHistoryGraphElementScanStart = WebUiCommonHelper.findWebElement(
    findTestObject('Object Repository/RiskRoute Dashboard/Page_/Alarm History'), 20)

if (scrollToVisible(alarmHistoryGraphElementScanStart, js)) {
    js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", alarmHistoryGraphElementScanStart)
    WebUI.comment("👉 'Alarm History' grafiğine başarıyla scroll yapıldı (ScanxStart).")
    WebUI.delay(1)
} else {
    WebUI.comment("❌ 'Alarm History' grafiği görünür değil, scroll başarısız (ScanxStart).")
}

// Find path element for 'ScanxStart' series
TestObject pathElementScanStart = new TestObject()
pathElementScanStart.addProperty("xpath", ConditionType.EQUALS, "//*[local-name()='g' and contains(@class, 'apexcharts-series') and contains(@class, 'apexcharts-pie-series') and @seriesName='ScanxStart']/*[local-name()='path']")

// Get WebElement for 'ScanxStart' path
WebElement pathWebElementScanStart = WebUiCommonHelper.findWebElement(pathElementScanStart, 10)

// 4. data:value attribute’unu oku
String dataValueStrScanStart = pathWebElementScanStart.getAttribute("data:value")
println("Bulunan data:value (ScanxStart) = " + dataValueStrScanStart)

// Convert to integer
int dataValueScanStart = dataValueStrScanStart.toInteger()

if (dataValueScanStart > 0) {
    // Click on the path
    pathWebElementScanStart.click()
    WebUI.delay(3) // Wait for the new page to load

    // Calculate expected pagination count (assuming 20 records per page for this section)
    int expectedPageCountScanStart = (int) Math.ceil(dataValueScanStart / 20.0)
    println("🎯 Beklenen pagination sayısı (ScanxStart - 20 kayıt/sayfa): " + expectedPageCountScanStart)

    // Find all visible page number links for ScanxStart
    TestObject pageNumberLinksScanStart = new TestObject()
    pageNumberLinksScanStart.addProperty("xpath", ConditionType.EQUALS,
        "//ul[contains(@class,'flex')]/li[a[not(contains(@aria-label,'previous')) and not(contains(@aria-label,'next'))]]/a")

    List<WebElement> visiblePageNumberElementsScanStart = WebUiCommonHelper.findWebElements(pageNumberLinksScanStart, 10)

    // Scroll to the pagination if not visible
    if (!visiblePageNumberElementsScanStart.isEmpty()) {
        scrollToVisible(visiblePageNumberElementsScanStart.get(0), js) // Scroll to the first page number
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)") // Ensure the entire pagination is visible
        WebUI.delay(1)
    }

    int actualLastPageNumberScanStart = 0
    if (!visiblePageNumberElementsScanStart.isEmpty()) {
        for (WebElement pageElement : visiblePageNumberElementsScanStart) {
            String pageText = pageElement.getText().trim()
            if (pageText.matches("\\d+")) { // Check if the text is a number
                int currentPageNumber = Integer.parseInt(pageText)
                if (currentPageNumber > actualLastPageNumberScanStart) {
                    actualLastPageNumberScanStart = currentPageNumber
                }
            }
        }
    }

    println("🔢 Gerçek son pagination numarası (ScanxStart): " + actualLastPageNumberScanStart)

    // Verify the page count
    WebUI.verifyEqual(actualLastPageNumberScanStart, expectedPageCountScanStart)

} else {
    WebUI.comment("❗ Test atlandı çünkü data:value (ScanxStart) 0 veya negatif: " + dataValueScanStart)
}
WebUI.comment("--- Finished Alarm History (ScanxStart) Pagination Test ---")

WebUI.back()
WebUI.delay(3)
WebUI.waitForPageLoad(30)
WebUI.comment("--- Finished Asset Detail (IP) Pagination Test ---")

/*/ Alarm History (NetworkOS) Pagination Test
// =========================================================================

WebUI.comment("--- Starting Alarm History (NetworkOS) Pagination Test ---")

// Scroll to Alarm History graph
WebElement alarmHistoryGraphElementNetworkOS = WebUiCommonHelper.findWebElement(
	findTestObject('Object Repository/RiskRoute Dashboard/Page_/Alarm History'), 20)

if (scrollToVisible(alarmHistoryGraphElementNetworkOS, js)) {
	js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", alarmHistoryGraphElementNetworkOS)
	WebUI.comment("👉 'Alarm History' grafiğine başarıyla scroll yapıldı (NetworkOS).")
	WebUI.delay(1)
} else {
	WebUI.comment("❌ 'Alarm History' grafiği görünür değil, scroll başarısız (NetworkOS).")
}

// Find path element for 'NetworkOS' series using the provided image information
TestObject pathElementNetworkOS = new TestObject()
pathElementNetworkOS.addProperty("xpath", ConditionType.EQUALS, "//*[local-name()='g' and contains(@class, 'apexcharts-series') and contains(@class, 'apexcharts-pie-series') and @seriesName='NetworkxOS']/*[local-name()='path']")

// Get WebElement for 'NetworkOS' path
WebElement pathWebElementNetworkOS = WebUiCommonHelper.findWebElement(pathElementNetworkOS, 10)

// Read data:value attribute for 'NetworkOS'
String dataValueStrNetworkOS = pathWebElementNetworkOS.getAttribute("data:value")
println("Bulunan data:value (NetworkOS) = " + dataValueStrNetworkOS)

// Convert to integer
int dataValueNetworkOS = dataValueStrNetworkOS.toInteger()

if (dataValueNetworkOS > 0) {
	// Click on the path
	pathWebElementNetworkOS.click()
	WebUI.delay(3) // Wait for the new page to load

	// Calculate expected pagination count (assuming 20 records per page, consistent with previous Alarm History)
	int expectedPageCountNetworkOS = (int) Math.ceil(dataValueNetworkOS / 20.0)
	println("🎯 Beklenen pagination sayısı (NetworkOS - 20 kayıt/sayfa): " + expectedPageCountNetworkOS)

	// Find all visible page number links for NetworkOS
	TestObject pageNumberLinksNetworkOS = new TestObject()
	pageNumberLinksNetworkOS.addProperty("xpath", ConditionType.EQUALS,
		"//ul[contains(@class,'flex')]/li[a[not(contains(@aria-label,'previous')) and not(contains(@aria-label,'next'))]]/a")

	List<WebElement> visiblePageNumberElementsNetworkOS = WebUiCommonHelper.findWebElements(pageNumberLinksNetworkOS, 10)

	// Scroll to the pagination if not visible
	if (!visiblePageNumberElementsNetworkOS.isEmpty()) {
		scrollToVisible(visiblePageNumberElementsNetworkOS.get(0), js) // Scroll to the first page number
		js.executeScript("window.scrollTo(0, document.body.scrollHeight)") // Ensure the entire pagination is visible
		WebUI.delay(1)
	}

	int actualLastPageNumberNetworkOS = 0
	if (!visiblePageNumberElementsNetworkOS.isEmpty()) {
		for (WebElement pageElement : visiblePageNumberElementsNetworkOS) {
			String pageText = pageElement.getText().trim()
			if (pageText.matches("\\d+")) { // Check if the text is a number
				int currentPageNumber = Integer.parseInt(pageText)
				if (currentPageNumber > actualLastPageNumberNetworkOS) {
					actualLastPageNumberNetworkOS = currentPageNumber
				}
			}
		}
	}

	println("🔢 Gerçek son pagination numarası (NetworkOS): " + actualLastPageNumberNetworkOS)

	// Verify the page count using assert
	WebUI.verifyEqual(actualLastPageNumberNetworkOS, expectedPageCountNetworkOS)
	assert actualLastPageNumberNetworkOS == expectedPageCountNetworkOS : "NetworkOS pagination count verification failed. Expected: ${expectedPageCountNetworkOS}, Actual: ${actualLastPageNumberNetworkOS}"

} else {
	WebUI.comment("❗ Test atlandı çünkü data:value (NetworkOS) 0 veya negatif: " + dataValueNetworkOS)
}
WebUI.comment("--- Finished Alarm History (NetworkOS) Pagination Test ---")
/*/

