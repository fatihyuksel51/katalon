import com.kms.katalon.core.webui.common.WebUiCommonHelper as WebUiCommonHelper
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.util.KeywordUtil as KeywordUtil
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import org.openqa.selenium.WebElement
import org.openqa.selenium.JavascriptExecutor
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

import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.Keys
import org.openqa.selenium.WebElement

import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.webui.common.WebUiCommonHelper as WebUiCommonHelper
import java.text.SimpleDateFormat

/**
 * Smartdeceptive Dashboard bileşenlerini dinamik ID üzerinden kontrol eder.
 * <g> elementi var mı ve "no data" içeriyor mu diye denetler.
 * Hata varsa test fail olur.
 */

WebElement safeScrollTo(TestObject to) {
	if (to == null) {
		KeywordUtil.markFailed("❌ TestObject NULL – Repository yolunu kontrol et.")
		return null
	}
	if (!WebUI.waitForElementPresent(to, 2, FailureHandling.OPTIONAL)) {
		KeywordUtil.markFailed("❌ Element not present – scroll edilemedi: ${to.getObjectId()}")
		return null
	}
	WebElement element = WebUiCommonHelper.findWebElement(to, 2)
	JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getWebDriver()
	js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element)
	WebUI.delay(0.5)
	return element
}
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
WebUI.navigateToUrl('https://platform.catchprobe.io/')
WebUI.maximizeWindow()

// Login işlemleri
WebUI.waitForElementVisible(findTestObject('Object Repository/otp/Page_/a_PLATFORM LOGIN'), 30)
WebUI.click(findTestObject('Object Repository/otp/Page_/a_PLATFORM LOGIN'))
WebUI.waitForElementVisible(findTestObject('Object Repository/otp/Page_/input_Email Address_email'), 30)
WebUI.setText(findTestObject('Object Repository/otp/Page_/input_Email Address_email'), 'fatih@test.com')
WebUI.setEncryptedText(findTestObject('Object Repository/otp/Page_/input_Password_password'), 'v4yvAQ7Q279BF5ny4hDiTA==')
WebUI.click(findTestObject('Object Repository/otp/Page_/button_Sign in'))
WebUI.delay(5)
WebUI.waitForPageLoad(10)

// OTP işlemi
def randomOtp = (100000 + new Random().nextInt(900000)).toString()
WebUI.setText(findTestObject('otp/Page_/input_OTP Digit_vi_1_2_3_4_5'), randomOtp)

WebUI.click(findTestObject('otp/Page_/button_Verify'))
//
WebUI.delay(5)
WebUI.waitForPageLoad(10)

/*/
// Smartdeceptive sekmesine tıkla

WebUI.navigateToUrl('https://platform.catchprobe.io/smartdeceptive')

WebUI.delay(3)
WebUI.waitForPageLoad(10)
// WebDriver ve JS tanımla
WebDriver driver = DriverFactory.getWebDriver()
JavascriptExecutor js = (JavascriptExecutor) driver



// ✅ 1. Most Bar çubuğu tanımı
TestObject bar = new TestObject("firstBar")
bar.addProperty("xpath", ConditionType.EQUALS, "(//*[name()='path' and contains(@class, 'apexcharts-bar-area') and @index='0'])[1]")

if (WebUI.verifyElementPresent(bar, 10, FailureHandling.OPTIONAL)) {
	WebElement barElement = safeScrollTo(bar)
	if (barElement == null) {
		KeywordUtil.markWarning("❌ Scroll sonrası bar elementi bulunamadı!")
	}
	
	WebUI.waitForElementPresent(bar, 5)
	WebElement barElem = WebUiCommonHelper.findWebElement(bar, 5)
	String countVal = barElem.getAttribute("val")
	println("📊 Bar üzerindeki Count: " + countVal)

	// ✅ Mouse over ile tooltip'i tetikle
	WebUI.mouseOver(bar)
	WebUI.delay(1)

	// ✅ Tooltip görünene kadar dene
	TestObject tooltipIpObj = new TestObject()
	tooltipIpObj.addProperty("xpath", ConditionType.EQUALS,
    "//div[contains(@class, 'apexcharts-tooltip')]//div[contains(text(), '.') or contains(text(), ':')]")

	WebUI.waitForElementVisible(tooltipIpObj, 5)
	String tooltipText = WebUI.getText(tooltipIpObj).trim()
	println("📌 Tooltip’teki IP: " + tooltipText)

	if (!tooltipText?.trim()) {
		KeywordUtil.markWarning("❌ Tooltip içeriği alınamadı!")
	}

	println("🟢 Tooltip Text: " + tooltipText)

	// ✅ Bar'a tıkla
	WebUI.click(bar)
	WebUI.delay(1)

	// ✅ Açılan ilk input alanını bul
	TestObject inputField = new TestObject("inputField")
	inputField.addProperty("xpath", ConditionType.EQUALS, "//input[contains(@class, 'rounded-md') and contains(@class, 'border-input')]")

	if (WebUI.verifyElementPresent(inputField, 5, FailureHandling.OPTIONAL)) {
		String inputValue = WebUI.getAttribute(inputField, "value").trim()
		println("🟢 Input Değeri: " + inputValue)

		if (tooltipText.contains(inputValue)) {
			KeywordUtil.logInfo("✅ Tooltip ile input değeri eşleşiyor.")
		} else {
			KeywordUtil.markWarning("❌ Tooltip ile input değeri eşleşmiyor!\nTooltip: ${tooltipText}\nInput: ${inputValue}")
		}
	} else {
		KeywordUtil.markWarning("❌ Açılan input alanı bulunamadı!")
	}
} else {
	KeywordUtil.markWarning("❌ İlk bar path elementi bulunamadı!")
}
WebUI.delay(2)
WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Mitre Close'))

/*/ ✅ 1. Most Used Proxy Bar çubuğu tanımı
TestObject mostUsedProxyBar = new TestObject("mostUsedProxyBar")
mostUsedProxyBar.addProperty("xpath", ConditionType.EQUALS,
	"(//*[name()='g' and contains(@class,'apexcharts-series') and @seriesName='AttackxCount']" +
	"//*[name()='path' and contains(@class,'apexcharts-bar-area') and @j='0'])[1]"
)

if (WebUI.verifyElementPresent(mostUsedProxyBar, 10, FailureHandling.OPTIONAL)) {
	WebElement barElement = safeScrollTo(mostUsedProxyBar)
	if (barElement == null) {
		KeywordUtil.markWarning("❌ Scroll sonrası mostUsedProxyBar elementi bulunamadı!")
	}

	WebUI.waitForElementPresent(mostUsedProxyBar, 5)
	WebElement barElem = WebUiCommonHelper.findWebElement(mostUsedProxyBar, 5)
	String countVal = barElem.getAttribute("val")
	println("📊 Most Used Proxy Bar üzerindeki Count: " + countVal)

	// ✅ Mouse over ile tooltip'i tetikle
	WebUI.mouseOver(mostUsedProxyBar)
	WebUI.delay(1)

	// ✅ Tooltip görünene kadar dene
	TestObject tooltipProxyObj = new TestObject("tooltipProxy")
	tooltipProxyObj.addProperty("xpath", ConditionType.EQUALS,
		"//div[contains(@class, 'apexcharts-tooltip')]//div[contains(text(), '.') or contains(text(), ':')]"
	)

	WebUI.waitForElementVisible(tooltipProxyObj, 5)
	String tooltipText = WebUI.getText(tooltipProxyObj).trim()
	println("📌 Tooltip’teki Proxy/IP: " + tooltipText)

	if (!tooltipText?.trim()) {
		KeywordUtil.markWarning("❌ Tooltip içeriği alınamadı!")
	}

	println("🟢 Tooltip Text: " + tooltipText)

	// ✅ Bar'a tıkla
	WebUI.click(mostUsedProxyBar)
	WebUI.delay(1)

	// ✅ Açılan ilk input alanını bul
	TestObject inputField = new TestObject("inputField")
	inputField.addProperty("xpath", ConditionType.EQUALS,
		"//input[contains(@class, 'rounded-md') and contains(@class, 'border-input')]"
	)

	if (WebUI.verifyElementPresent(inputField, 5, FailureHandling.OPTIONAL)) {
		String inputValue = WebUI.getAttribute(inputField, "value").trim()
		println("🟢 Input Değeri: " + inputValue)

		if (tooltipText.contains(inputValue)) {
			KeywordUtil.logInfo("✅ Tooltip ile input değeri eşleşiyor.")
		} else {
			KeywordUtil.markWarning("❌ Tooltip ile input değeri eşleşmiyor!\nTooltip: ${tooltipText}\nInput: ${inputValue}")
		}
	} else {
		KeywordUtil.markWarning("❌ Açılan input alanı bulunamadı!")
	}
} else {
	KeywordUtil.markWarning("❌ Most Used Proxy bar path elementi bulunamadı!")
}

WebUI.delay(2)
WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Mitre Close'))
/*/

WebUI.comment("--- Starting Asset Detail (MySQL Server) Pagination Test ---")

// Scroll to Asset Detail graph for MySQL Server
WebElement assetDetailGraphElementMysql = WebUiCommonHelper.findWebElement(
	findTestObject('Object Repository/Smartdeceptive/Page_/Dashboard-UsedTecniques'), 20)

if (scrollToVisible(assetDetailGraphElementMysql, js)) {
	js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", assetDetailGraphElementMysql)
	WebUI.comment("👉 'Asset Detail' grafiğine başarıyla scroll yapıldı (MySQL Server).")
	WebUI.delay(1)
} else {
	WebUI.comment("❌ 'Asset Detail' grafiği görünür değil, scroll başarısız (MySQL Server).")
}

// Find path element for 'MySQL Server' series
TestObject pathElementMysql = new TestObject("pathElementMysql")
pathElementMysql.addProperty("xpath", ConditionType.EQUALS,
	"(//*[local-name()='g' and contains(@class, 'apexcharts-series') and contains(@class, 'apexcharts-pie-series') and @seriesName='APP-DETECTxremotexdesktopxprotocolxattemptedxadministratorxconnectionxrequest']/*[local-name()='path'])[1]")

// Get WebElement for 'MySQL Server' path
WebElement pathWebElementMysql = WebUiCommonHelper.findWebElement(pathElementMysql, 10)

// Read data:value attribute for 'MySQL Server'
String dataValueStrMysql = pathWebElementMysql.getAttribute("data:value")
println("Bulunan data:value (MySQL Server) = " + dataValueStrMysql)

// Convert to integer
int dataValueMysql = dataValueStrMysql.toInteger()

if (dataValueMysql > 0) {
	// Click on the path
	pathWebElementMysql.click()
	WebUI.delay(3) // Wait for the new page to load

	// Beklenen pagination sayısını hesapla (her sayfa 10 kayıt alıyor varsayımıyla)
	int expectedPageCountMysql = (int) Math.ceil(dataValueMysql / 10.0)
	println("🎯 Beklenen pagination sayısı (MySQL - 10 kayıt/sayfa): " + expectedPageCountMysql)

	// Find all visible page number links for MySQL
	TestObject pageNumberLinksMysql = new TestObject("pageNumberLinksMysql")
	pageNumberLinksMysql.addProperty("xpath", ConditionType.EQUALS,
		"//ul[contains(@class,'flex')]/li[a[not(contains(@aria-label,'previous')) and not(contains(@aria-label,'next'))]]/a")

	List<WebElement> visiblePageNumberElementsMysql = WebUiCommonHelper.findWebElements(pageNumberLinksMysql, 10)

	// Scroll to the pagination if not visible
	if (!visiblePageNumberElementsMysql.isEmpty()) {
		scrollToVisible(visiblePageNumberElementsMysql.get(0), js) // Scroll to the first page number
		js.executeScript("window.scrollTo(0, document.body.scrollHeight)") // Ensure the entire pagination is visible
		WebUI.delay(1)
	}

	int actualLastPageNumberMysql = 0
	if (!visiblePageNumberElementsMysql.isEmpty()) {
		for (WebElement pageElement : visiblePageNumberElementsMysql) {
			String pageText = pageElement.getText().trim()
			if (pageText.matches("\\d+")) { // Check if the text is a number
				int currentPageNumber = Integer.parseInt(pageText)
				if (currentPageNumber > actualLastPageNumberMysql) {
					actualLastPageNumberMysql = currentPageNumber
				}
			}
		}
	}

	println("🔢 Gerçek son pagination numarası (MySQL): " + actualLastPageNumberMysql)

	// Verify the page count
	//WebUI.verifyEqual(actualLastPageNumberMysql, expectedPageCountMysql)

} else {
	WebUI.comment("❗ Test atlandı çünkü data:value (MySQL) 0 veya negatif: " + dataValueMysql)
}

WebUI.back()
WebUI.delay(3)
WebUI.waitForPageLoad(30)
WebUI.comment("--- Finished Asset Detail (MySQL Server) Pagination Test ---")


// ✅ 1. BadreputaionBar çubuğu tanımı (Attackxcount - j=0)
TestObject badReputationBar = new TestObject("badReputationBar")
badReputationBar.addProperty("xpath", ConditionType.EQUALS,
	"(//*[name()='g' and contains(@class,'apexcharts-series') and @seriesName='Attackxcount']" +
	"//*[name()='path' and contains(@class,'apexcharts-bar-area') and @j='0'])[1]")

if (WebUI.verifyElementPresent(badReputationBar, 10, FailureHandling.OPTIONAL)) {
	WebElement barElement = safeScrollTo(badReputationBar)
	if (barElement == null) {
		KeywordUtil.markWarning("❌ Scroll sonrası badReputationBar elementi bulunamadı!")
	}

	WebUI.waitForElementPresent(badReputationBar, 5)
	WebElement barElem = WebUiCommonHelper.findWebElement(badReputationBar, 5)
	String countVal = barElem.getAttribute("val")
	println("📊 badReputationBar üzerindeki Count: " + countVal)
	int dataValueIp = countVal.toInteger()
	
	if (dataValueIp > 0) {
		WebUI.delay(3) // Sayfa yüklenmesi için bekle
		
		// ✅ Mouse over ile tooltip'i tetikle
		WebUI.mouseOver(badReputationBar)
		WebUI.delay(1)
	
		// ✅ Tooltip görünene kadar dene
		TestObject tooltipIpObj = new TestObject("tooltipIp")
		tooltipIpObj.addProperty("xpath", ConditionType.EQUALS,
			"//div[contains(@class, 'apexcharts-tooltip')]" +
			"//div[contains(text(), '.') or contains(text(), ':')]"
		)
	
		WebUI.waitForElementVisible(tooltipIpObj, 5)
		String tooltipText = WebUI.getText(tooltipIpObj).trim()
		println("📌 Tooltip’teki IP: " + tooltipText)
	
		if (!tooltipText) {
			KeywordUtil.markWarning("❌ Tooltip içeriği alınamadı!")
		}
	
		println("🟢 Tooltip Text: " + tooltipText)
	
		// ✅ Bar'a tıkla
	WebUI.click(badReputationBar)
	WebUI.delay(3)	
		

	// Beklenen sayfa sayısını hesapla (10 kayıt/sayfa)
	int expectedPageCountIp = (int) Math.ceil(dataValueIp / 10.0)
	println("🎯 Beklenen sayfa sayısı (IP - 10 kayıt/sayfa): " + expectedPageCountIp)
		
	WebUI.waitForPageLoad(10)		
	js.executeScript("window.scrollTo(0, document.body.scrollHeight)")

	// Sayfa numaralarını bul
	TestObject pageNumberLinksIp = new TestObject("pageNumberLinksIp")
		pageNumberLinksIp.addProperty("xpath", ConditionType.EQUALS,
			"//ul[contains(@class,'flex')]/li[a[not(contains(@aria-label,'previous')) and not(contains(@aria-label,'next'))]]/a")
		
	js.executeScript("window.scrollTo(0, document.body.scrollHeight)")
	List<WebElement> visiblePageNumberElementsIp = WebUiCommonHelper.findWebElements(pageNumberLinksIp, 10)
	scrollToVisible(visiblePageNumberElementsIp.get(0), js)

		if (!visiblePageNumberElementsIp.isEmpty()) {
			// İlk <a> etiketli sayfa numarası için dinamik TestObject oluştur
			String firstPageText = visiblePageNumberElementsIp.get(0).getText()
			println("🎯 Beklenen sayfa sayısı: " + firstPageText)

			TestObject scrollTarget = new TestObject("PageScrollTarget")
			scrollTarget.addProperty("xpath", ConditionType.EQUALS,
				"//ul[contains(@class,'flex')]/li/a[normalize-space(text())='" + firstPageText + "']")

			// safeScrollTo fonksiyonunu kullan
			WebElement element = safeScrollTo(scrollTarget)

			if (element != null) {				
				js.executeScript("window.scrollTo(0, document.body.scrollHeight)")
				WebUI.delay(1)
				}
		} else {
			KeywordUtil.logInfo("❗ Sayfa numarası bulunamadı.")
	}


		// Gerçek sayfa numarasını al
		int actualLastPageNumberIp = 0
		for (WebElement pageElement : visiblePageNumberElementsIp) {
			String pageText = pageElement.getText().trim()
			if (pageText.matches("\\d+")) {
				int currentPage = Integer.parseInt(pageText)
				if (currentPage > actualLastPageNumberIp) {
					actualLastPageNumberIp = currentPage
				}
			}
		}

		println("🔢 Gerçek son sayfa numarası (IP): " + actualLastPageNumberIp)
		//WebUI.verifyEqual(actualLastPageNumberIp, expectedPageCountIp)

	} else {
		WebUI.comment("❗ Test atlandı çünkü Count değeri 0 veya negatif: " + dataValueIp)
	}

} else {
	KeywordUtil.markWarning("❌ Bad Reputation bar path elementi bulunamadı!")
	}	
	
	WebUI.back()
	WebUI.delay(3)
	WebUI.waitForPageLoad(30)

	// ✅ 1. MalwareBar çubuğu tanımı (Malwarexcount - j=0)
	TestObject malwareBar = new TestObject("malwareBar")
	malwareBar.addProperty("xpath", ConditionType.EQUALS,"(//*[name()='g' and contains(@class,'apexcharts-series') and @seriesName='Attackxcount']" +
			"//*[name()='path' and contains(@class,'apexcharts-bar-area') and @j='0'])[3]"	)
	
	if (WebUI.verifyElementPresent(malwareBar, 10, FailureHandling.OPTIONAL)) {
		WebElement barElement = safeScrollTo(malwareBar)
		if (barElement == null) {
			KeywordUtil.markWarning("❌ Scroll sonrası malwareBar elementi bulunamadı!")
		}
	
		WebUI.waitForElementPresent(malwareBar, 5)
		WebElement barElem = WebUiCommonHelper.findWebElement(malwareBar, 5)
		String countVal = barElem.getAttribute("val")
		println("📊 malwareBar üzerindeki Count: " + countVal)
		int dataValueMalware = countVal.toInteger()
		
		if (dataValueMalware > 0) {
			WebUI.delay(3) // Sayfa yüklenmesi için bekle	
				
	
			// ✅ Bar'a tıkla
			WebUI.click(malwareBar)
			WebUI.delay(3)
	
			// Beklenen sayfa sayısını hesapla (10 kayıt/sayfa)
			int expectedPageCountMalware = (int) Math.ceil(dataValueMalware / 10.0)
			println("🎯 Beklenen sayfa sayısı (Malware - 10 kayıt/sayfa): " + expectedPageCountMalware)
	
			WebUI.waitForPageLoad(10)
			js.executeScript("window.scrollTo(0, document.body.scrollHeight)")
	
			// Sayfa numaralarını bul
			TestObject pageNumberLinksMalware = new TestObject("pageNumberLinksMalware")
			pageNumberLinksMalware.addProperty("xpath", ConditionType.EQUALS,
				"//ul[contains(@class,'flex')]/li[a[not(contains(@aria-label,'previous')) and not(contains(@aria-label,'next'))]]/a")
	
			js.executeScript("window.scrollTo(0, document.body.scrollHeight)")
			List<WebElement> visiblePageNumberElementsMalware = WebUiCommonHelper.findWebElements(pageNumberLinksMalware, 10)
			scrollToVisible(visiblePageNumberElementsMalware.get(0), js)
	
			if (!visiblePageNumberElementsMalware.isEmpty()) {
				// İlk <a> etiketli sayfa numarası için dinamik TestObject oluştur
				String firstPageText = visiblePageNumberElementsMalware.get(0).getText()
				println("🎯 İlk sayfa numarası: " + firstPageText)
	
				TestObject scrollTarget = new TestObject("PageScrollTarget")
				scrollTarget.addProperty("xpath", ConditionType.EQUALS,
					"//ul[contains(@class,'flex')]/li/a[normalize-space(text())='" + firstPageText + "']")
	
				// safeScrollTo fonksiyonunu kullan
				WebElement element = safeScrollTo(scrollTarget)
	
				if (element != null) {
					js.executeScript("window.scrollTo(0, document.body.scrollHeight)")
					WebUI.delay(1)
				}
			} else {
				KeywordUtil.logInfo("❗ Sayfa numarası bulunamadı.")
			}
	
			// Gerçek sayfa numarasını al
			int actualLastPageNumberMalware = 0
			for (WebElement pageElement : visiblePageNumberElementsMalware) {
				String pageText = pageElement.getText().trim()
				if (pageText.matches("\\d+")) {
					int currentPage = Integer.parseInt(pageText)
					if (currentPage > actualLastPageNumberMalware) {
						actualLastPageNumberMalware = currentPage
					}
				}
			}
	
			println("🔢 Gerçek son sayfa numarası (Malware): " + actualLastPageNumberMalware)
			WebUI.verifyEqual(actualLastPageNumberMalware, expectedPageCountMalware)
	
		} else {
			WebUI.comment("❗ Test atlandı çünkü Count değeri 0 veya negatif: " + dataValueMalware)
		}
	
	} else {
		KeywordUtil.markWarning("❌ Malware bar path elementi bulunamadı!")
	}

