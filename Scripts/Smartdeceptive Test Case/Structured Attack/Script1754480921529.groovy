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
// 1️⃣ Structured Attack sayfasına git
WebUI.navigateToUrl('https://platform.catchprobe.io/smartdeceptive/structured-attacks')

WebUI.delay(5)
WebUI.waitForPageLoad(10)

// 2️⃣ Tablo'dan Keyword'ü al
TestObject keywordObj = new TestObject().addProperty("xpath", ConditionType.EQUALS, "(.//td[contains(@class, 'ant-table-cell')])[1]")
WebUI.waitForElementVisible(keywordObj, 10)
String tableKeyword = WebUI.getText(keywordObj).trim()
println("📌 Tablo Keyword: " + tableKeyword)

// 3️⃣ Detay butonuna tıkla
TestObject detailBtn = new TestObject().addProperty("xpath", ConditionType.EQUALS, "//td[contains(@class, 'ant-table-cell-fix-right-first')]//button[@data-state='closed']")
WebUI.waitForElementClickable(detailBtn, 10)
WebUI.click(detailBtn)
WebUI.delay(2) 
WebUI.waitForPageLoad(10)// pencere yüklenme süresi

// 4️⃣ Açılan panelde Attack Keyword'ü al
TestObject attackKeywordObj = new TestObject().addProperty("xpath", ConditionType.EQUALS, "//div[@class='col-span-6 truncate font-semibold md:col-span-3']")
WebUI.waitForElementVisible(attackKeywordObj, 10)
String detailKeyword = WebUI.getText(attackKeywordObj).trim()
println("🔍 Detail Panel Keyword: " + detailKeyword)

// 5️⃣ Tablo keyword ile eşit mi kontrol et
WebUI.verifyMatch(tableKeyword, detailKeyword, false, FailureHandling.STOP_ON_FAILURE)
//İp profile detay eşleşmesi yap
TestObject ipCellObj = makeXpathObj("//span[@class='cursor-pointer font-semibold underline']")


safeScrollTo(ipCellObj)
String page3Ip = WebUI.getText(ipCellObj)
safeScrollTo(ipCellObj)
WebUI.click(ipCellObj)
WebUI.delay(4)
WebUI.waitForPageLoad(10)

// ✅ Açılan ilk input alanını bul
TestObject inputField = new TestObject("inputField")
inputField.addProperty("xpath", ConditionType.EQUALS, "(//input[contains(@class, 'rounded-md') and contains(@class, 'border-input')])[2]")

if (WebUI.verifyElementPresent(inputField, 2, FailureHandling.OPTIONAL)) {
	String inputValue = WebUI.getAttribute(inputField, "value").trim()
	println("🟢 Input Değeri: " + inputValue)

	if (page3Ip.contains(inputValue)) {
		KeywordUtil.logInfo("✅ İP ile input değeri eşleşiyor.")
	} else {
		KeywordUtil.markWarning("❌ İp ile input değeri eşleşmiyor!\nTooltip: ${page3Ip}\nInput: ${inputValue}")
	}
} else {
	KeywordUtil.markWarning("❌ Açılan input alanı bulunamadı!")
}

WebUI.comment("✅ Filtreleme tamamlandı: IP '${page3Ip}', AttackName '${page3Ip}'")


JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getWebDriver()
js.executeScript("document.body.style.zoom='0.65'")

WebUI.comment("🔍 Detay Testi Başladı")

	// 1️⃣ Risk Score kontrolü (g elementi içinde 0'dan büyük değer)
	TestObject riskTextObj = new TestObject()
	riskTextObj.addProperty("xpath", ConditionType.EQUALS, "//*[@class='apexcharts-text apexcharts-datalabel-value']")

	String riskTextRaw = WebUI.getText(riskTextObj).trim() // Elementten metni al ve boşlukları temizle
	String riskTextCleaned = riskTextRaw.replace('%', '').trim() // '%' işaretini kaldır ve tekrar boşlukları temizle

	// Ondalık sayıya dönüştürme (çünkü loglarda "7.8" gibi değerler görüyoruz)
	double riskScoreDouble = Double.parseDouble(riskTextCleaned)

	// Eğer risk skorunu tam sayı olarak kullanmak zorundaysanız ve ondalık kısmı atmanız gerekiyorsa:
	int riskScore = (int) riskScoreDouble // Örneğin 7.8 -> 7 olacaktır.

	WebUI.comment("📊 Risk Score: " + riskScore)
	assert riskScore > 0 : "Risk skoru 0'dan büyük olmalı!"

	// 2️⃣ Show Attacker Map butonuna tıkla
	WebUI.click(findTestObject("Object Repository/SmartDeceptive/Show Attacker Map Button"))
	WebUI.delay(2)
	// Sayfada severity circle geldiğini doğrula
	TestObject circle = findTestObject('Object Repository/Smartdeceptive/Stroke Circle')

	// Div'in görünmesini bekle (maksimum 10 saniye)
	if (WebUI.waitForElementVisible(circle, 10)) {

	// Elementi bul
		WebElement circleelement = WebUI.findWebElement(circle, 10)

		// SVG içinde stroke attribute'u olan bir eleman olup olmadığını kontrol et
		Boolean circleExistsRisk = WebUI.executeJavaScript(
		"return arguments[0].querySelector('[stroke]') !== null;",
			Arrays.asList(circleelement)
		)

		// Durumu logla
		KeywordUtil.logInfo("Show Detail Stroke var mı? : " + circleExistsRisk)

		if (circleExistsRisk) {
		KeywordUtil.logInfo("✅ Show Detail Stroke Veri VAR")
		} else {
		KeywordUtil.markWarning("🚨 Show Detail Stroke Veri YOK")
		}

		} else {
			KeywordUtil.logInfo("⏰ Show Detail Stroke elementi görünmedi")
		}

		// 3️⃣ Back to IP Profile tıkla
		WebUI.click(findTestObject("Object Repository/SmartDeceptive/Back to IP Profile Button"))
		WebUI.waitForPageLoad(10)
		
		WebUI.delay(2)
		WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Mitre Close'))

// 6️⃣ Detail on Leakmap butonuna tıkla
TestObject leakmapBtn = new TestObject().addProperty("xpath", ConditionType.EQUALS, "//div[@class='col-span-6 flex items-center justify-end md:col-span-2']//button")
WebUI.waitForElementClickable(leakmapBtn, 10)
WebUI.click(leakmapBtn)
WebUI.delay(3) // leakmap sayfası yüklenme süresi

// 6️⃣ Yeni sekmeye geç
WebUI.switchToWindowIndex(1)
WebUI.delay(2) // leakmap sayfasının yüklenmesini bekle

// 7️⃣ Input'taki keyword değerini al
TestObject inputObj = new TestObject().addProperty("xpath", ConditionType.EQUALS, "//input[@name='keyword']")
WebUI.waitForElementPresent(inputObj, 10) // DOM'da var mı kontrol et
String inputKeyword = WebUI.getAttribute(inputObj, "value").trim()
println("🔐 Leakmap Input Keyword: " + inputKeyword)

// 8️⃣ Doğrulama
WebUI.verifyMatch(detailKeyword, inputKeyword, false)

// ✅ Test Bitti









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
	WebElement element = WebUiCommonHelper.findWebElement(to, 1)
	JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getWebDriver()
	js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element)
	WebUI.delay(0.5)
	return element
}

void scrollToBottom() {
	JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getWebDriver()
	js.executeScript("window.scrollTo(0, document.body.scrollHeight)")
}
