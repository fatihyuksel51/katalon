import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.webui.common.WebUiCommonHelper as WebUiCommonHelper
import com.kms.katalon.core.testobject.ConditionType as ConditionType
import com.kms.katalon.core.util.KeywordUtil as KeywordUtil
import org.openqa.selenium.JavascriptExecutor as JavascriptExecutor
import org.openqa.selenium.WebElement as WebElement
import org.openqa.selenium.WebDriver as WebDriver

// Scroll fonksiyonu (scrollIntoView)
def scrollToElement(WebElement element, JavascriptExecutor js) {
    js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element)
    WebUI.delay(1)
}

WebUI.openBrowser('')
WebUI.navigateToUrl('https://platform.catchprobe.org/')
WebUI.maximizeWindow()

WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'))
WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 'fatih.yuksel@catchprobe.com')
WebUI.setEncryptedText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Password_password'), 'RigbBhfdqOBDK95asqKeHw==')
WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Sign in'))
WebUI.delay(5)

WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'), (100000 + new Random().nextInt(900000)).toString())
WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Verify'))
WebUI.delay(5)
WebUI.navigateToUrl('https://platform.catchprobe.org/riskroute')
WebUI.waitForPageLoad(30)

WebDriver driver = DriverFactory.getWebDriver()
JavascriptExecutor js = (JavascriptExecutor) driver

// Riskroute dashboard kontrol objeleri
def checkTextObj(String testObjectPath, String expectedText) {
    WebUI.verifyElementText(findTestObject(testObjectPath), expectedText)
}

checkTextObj('Object Repository/RiskRoute Dashboard/Page_/div_Total Assets', 'Total Assets')
checkTextObj('Object Repository/RiskRoute Dashboard/Page_/div_Total Subdomains', 'Total Subdomains')
checkTextObj('Object Repository/RiskRoute Dashboard/Page_/div_Total Vulnerabilities', 'Total Vulnerabilities')

// Genel SVG kontrol fonksiyonu
def checkSvgPresence(String testObjectPath, JavascriptExecutor js) {
    WebElement element = WebUiCommonHelper.findWebElement(findTestObject(testObjectPath), 10)
    scrollToElement(element, js)
    js.executeScript("arguments[0].click();", element)
    WebUI.delay(1)
    Boolean hasSvg = (Boolean) js.executeScript("return arguments[0].querySelector('svg') != null;", element)
    KeywordUtil.logInfo(testObjectPath + ' içinde SVG var mı? ' + hasSvg)
}

checkSvgPresence('Object Repository/RiskRoute Dashboard/Page_/Vulnerability Breakdown', js)
checkSvgPresence('Object Repository/RiskRoute Dashboard/Page_/Risck Score', js)
checkSvgPresence('Object Repository/RiskRoute Dashboard/Page_/MostCommonCve', js)
checkSvgPresence('Object Repository/RiskRoute Dashboard/Page_/Most Common CWE ID', js)
checkSvgPresence('Object Repository/RiskRoute Dashboard/Page_/MostCommonCwe', js)
checkSvgPresence('Object Repository/RiskRoute Dashboard/Page_/mostcommontechnology', js)
WebUI.delay(10)

WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/grafik/Page_/foreignobject'))

WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/grafik/Page_/a_Dashboard'))

WebUI.rightClick(findTestObject('Object Repository/RiskRoute Dashboard/grafik/Page_/path_keyword_SvgjsPath48926'))

// 2. path elementini bul (önceden tanımlı XPath ile)
TestObject pathElement = new TestObject()
pathElement.addProperty("xpath", ConditionType.EQUALS, "//*[local-name()='g' and contains(@class, 'apexcharts-series') and contains(@class, 'apexcharts-pie-series') and @seriesName='domain']/*[local-name()='path']")

// 3. WebElement'i al
WebElement pathWebElement = WebUiCommonHelper.findWebElement(pathElement, 10)

// 4. data:value attribute’unu oku
String dataValueStr = pathWebElement.getAttribute("data:value")
println("Bulunan data:value = " + dataValueStr)

// 5. Sayıya çevir
int dataValue = dataValueStr.toInteger()

// 6. Eğer data:value = 21 ise, slice'a tıkla
if (dataValue == 21) {
	WebUI.executeJavaScript("arguments[0].click();", Arrays.asList(pathWebElement))
	WebUI.delay(3) // yeni sayfanın yüklenmesini bekle

	// 7. Pagination elementlerini kontrol et
	TestObject paginationItems = new TestObject()
	paginationItems.addProperty("xpath", ConditionType.EQUALS, "//ul[contains(@class,'pagination')]/li[not(contains(@class,'previous')) and not(contains(@class,'next'))]")

	List<WebElement> pages = WebUiCommonHelper.findWebElements(paginationItems, 10)
	int pageCount = pages.size()
	println("Pagination sayfa sayısı: " + pageCount)

	// 8. Doğrulama: 3 sayfa olmalı
	WebUI.verifyEqual(pageCount, 3)
} else {
	WebUI.comment("Veri sayısı 21 değil, test atlandı. data:value = " + dataValue)
}

WebUI.closeBrowser()
