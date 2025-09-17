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

TestObject X(String xp) {
    TestObject to = new TestObject(xp)
    to.addProperty("xpath", ConditionType.EQUALS, xp)
    return to
}
WebElement safeScrollTo(TestObject to) {
	if (to == null || !WebUI.waitForElementPresent(to, 1)) {
		KeywordUtil.markFailed("❌ Scroll edilemedi: ${to.getObjectId()}")
		return null
	}
	WebElement element = WebUiCommonHelper.findWebElement(to, 1)
	JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getWebDriver()
	js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element)
	WebUI.delay(0.5)
	return element
}

boolean isBrowserOpen() {
    try { DriverFactory.getWebDriver(); return true } catch(Throwable t){ return false }
}

void ensureSession() {
    if (isBrowserOpen()) return
    WebUI.openBrowser('')
    WebUI.maximizeWindow()
    WebUI.navigateToUrl('https://platform.catchprobe.io/')

    WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'), 30)
    WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'))

    WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 30)
    WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 'fatih@test.com')
    WebUI.setEncryptedText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Password_password'), 'v4yvAQ7Q279BF5ny4hDiTA==')
    WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Sign in'))

    WebUI.delay(3)
    String otp = (100000 + new Random().nextInt(900000)).toString()
    WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'), otp)
    WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Verify'))
    WebUI.delay(2)

    WebUI.waitForElementVisible(X("//span[text()='Threat']"), 10, FailureHandling.OPTIONAL)
}



ensureSession()

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

// ---- Doğrudan <path> slice'ı hedefle ----
// Not: j='3' ve/veya donut-slice-3 sınıfı MySQL slice'a karşılık geliyor (ekranda gördüğünle uyumlu).
TestObject mysqlSliceTO = new TestObject("mysqlSliceTO")
mysqlSliceTO.addProperty("xpath", ConditionType.EQUALS,
  // seriesName ile grubu sabitle, içindeki path'i al
  "//*[local-name()='g' and contains(@class,'apexcharts-pie-series')" +
  "   and contains(translate(@seriesName,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'APP-DETECT')]" +
  "//*/self::*[local-name()='path' and contains(@class,'apexcharts-pie-area') and (@j='3' or contains(@class,'apexcharts-donut-slice'))]"
)

// WebElement'i al
WebElement mysqlSliceEl = WebUiCommonHelper.findWebElement(mysqlSliceTO, 10)

// ---- data:value'ı JS ile oku (getAttribute yerine) ----
String dataValueStrMysql = WebUI.executeJavaScript(
    "return arguments[0].getAttribute('data:value') || arguments[0].getAttribute('data-value');",
    [mysqlSliceEl]
) as String

KeywordUtil.logInfo("Bulunan data:value (MySQL) = " + dataValueStrMysql)

// Güvenli parse
int dataValueMysql = 0
try {
    dataValueMysql = Integer.parseInt((dataValueStrMysql ?: "0").trim())
} catch (Exception e) {
    KeywordUtil.markWarning("data:value sayı değil veya okunamadı: " + dataValueStrMysql)
}

if (dataValueMysql > 0) {
    // Slice'a tıkla (gerekirse JS fallback)
    try {
        mysqlSliceEl.click()
    } catch (Exception e) {
        WebUI.executeJavaScript("arguments[0].click();", [mysqlSliceEl])
    }
    WebUI.delay(3) // yeni sayfa/yükleme

    // Beklenen sayfa sayısı (10 kayıt/sayfa varsayımı)
    int expectedPageCountMysql = (int) Math.ceil(dataValueMysql / 10.0)
    println("🎯 Beklenen pagination sayısı (MySQL - 10 kayıt/sayfa): " + expectedPageCountMysql)

    // Pagination linkleri
    TestObject pageNumberLinksMysql = new TestObject("pageNumberLinksMysql")
    pageNumberLinksMysql.addProperty("xpath", ConditionType.EQUALS,
        "//ul[contains(@class,'flex')]/li[a[not(contains(@aria-label,'previous')) and not(contains(@aria-label,'next'))]]/a")

    List<WebElement> visiblePageNumberElementsMysql = WebUiCommonHelper.findWebElements(pageNumberLinksMysql, 10)

    // Pagination'a in
    if (!visiblePageNumberElementsMysql.isEmpty()) {
        scrollToVisible(visiblePageNumberElementsMysql.get(0), js)
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)")
        WebUI.delay(1)
    }

    int actualLastPageNumberMysql = 0
    for (WebElement pageElement : visiblePageNumberElementsMysql) {
        String pageText = pageElement.getText().trim()
        if (pageText.matches("\\d+")) {
            actualLastPageNumberMysql = Math.max(actualLastPageNumberMysql, Integer.parseInt(pageText))
        }
    }

    println("🔢 Gerçek son pagination numarası (MySQL): " + actualLastPageNumberMysql)
    // WebUI.verifyEqual(actualLastPageNumberMysql, expectedPageCountMysql)

} else {
    WebUI.comment("❗ Test atlandı çünkü data:value (MySQL) 0/boş: " + dataValueMysql)
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

	// === Helpers: scrollToVisible ===
	boolean scrollToVisible(WebElement el, JavascriptExecutor js, int maxTries = 6) {
		if (el == null) return false
		for (int i = 0; i < maxTries; i++) {
			// viewport içinde mi?
			boolean inView = (Boolean) js.executeScript(
				"var r=arguments[0].getBoundingClientRect();" +
				"var h=(window.innerHeight||document.documentElement.clientHeight);" +
				"var w=(window.innerWidth||document.documentElement.clientWidth);" +
				"return r.top>=0 && r.left>=0 && r.bottom<=h && r.right<=w;", el)
	
			if (inView && el.isDisplayed()) return true
	
			// merkeze getir, sticky header için hafif yukarı kaydır
			js.executeScript("arguments[0].scrollIntoView({block:'center', inline:'nearest'});", el)
			js.executeScript("window.scrollBy(0,-80);")  // header offset
			WebUI.delay(0.3)
	
			// ek fallback: mouse move
			try {
				new org.openqa.selenium.interactions.Actions(DriverFactory.getWebDriver())
					.moveToElement(el).perform()
			} catch (Exception ignore) {}
		}
		return (Boolean) js.executeScript(
			"var r=arguments[0].getBoundingClientRect();" +
			"var h=(window.innerHeight||document.documentElement.clientHeight);" +
			"var w=(window.innerWidth||document.documentElement.clientWidth);" +
			"return r.top>=0 && r.left>=0 && r.bottom<=h && r.right<=w;", el)
	}
	
	// TestObject ile kullanmak istersen:
	boolean scrollToVisible(TestObject to, JavascriptExecutor js, int timeout = 10) {
		if (!WebUI.waitForElementPresent(to, timeout, FailureHandling.OPTIONAL)) return false
		WebElement el = WebUiCommonHelper.findWebElement(to, timeout)
		return scrollToVisible(el, js)
	}
	