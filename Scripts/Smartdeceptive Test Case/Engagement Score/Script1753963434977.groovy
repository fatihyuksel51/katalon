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


//safeScrollTo(findTestObject('Object Repository/Riskroute/Asset Lİst/Page_/Organization Butonu'))

//WebUI.click(findTestObject('Object Repository/Riskroute/Asset Lİst/Page_/Organization Butonu'))

//WebUI.delay(2)

//safeScrollTo(findTestObject('Object Repository/Prod SmartDeceptive/Test Company Organization'))

//WebUI.click(findTestObject('Object Repository/Prod SmartDeceptive/Test Company Organization'))

WebUI.delay(3)

WebUI.waitForPageLoad(10)
/*/

TestObject X(String xp) {
	TestObject to = new TestObject(xp)
	to.addProperty("xpath", ConditionType.EQUALS, xp)
	return to
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

WebUI.navigateToUrl('https://platform.catchprobe.io/smartdeceptive/engagement-score')

WebUI.delay(5) 
WebUI.waitForPageLoad(10)

// 🔥 
attackFlowTest()


void attackFlowTest() {
    // 🔹 İlk satır verilerini al
	TestObject hostNameObj = makeXpathObj("(.//td[contains(@class, 'ant-table-cell')])[2]")
	TestObject ipAddressObj  = makeXpathObj("(.//td[contains(@class, 'ant-table-cell')])[1]")
	TestObject timeStampObj  = makeXpathObj("(.//td[contains(@class, 'ant-table-cell')])[5]")

	

	safeScrollTo(ipAddressObj)
	String ipAddress = WebUI.getText(ipAddressObj)
	
	safeScrollTo(hostNameObj)
	String HostAddress = WebUI.getText(hostNameObj)

	safeScrollTo(timeStampObj)
	String timeStamp = WebUI.getText(timeStampObj)

	WebUI.comment("🔍 AttackName: ${hostNameObj}, IP: ${ipAddress}, Timestamp: ${timeStamp}")
	    // 🔹 Timestamp kontrol
    String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date())
   Date parsedTimestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(timeStamp)
   String formattedTimestamp = new SimpleDateFormat("yyyy-MM-dd").format(parsedTimestamp)

   assert formattedTimestamp == today


    /*/ 🔹 Filter Options işlemleri
    safeScrollTo(makeXpathObj("//div[text()='FILTER OPTIONS']"))
    WebUI.click(makeXpathObj("//div[text()='FILTER OPTIONS']"))
	WebUI.delay(4)

    safeScrollTo(makeXpathObj("(//button[@role='combobox'])[5]"))
    WebUI.click(makeXpathObj("(//button[@role='combobox'])[5]"))
	safeScrollTo(makeXpathObj("//option[text()='Ascending']"))
	 WebUI.click(makeXpathObj("//option[text()='Ascending']"))
	

     safeScrollTo(makeXpathObj("(//button[@role='combobox'])[6]"))
    WebUI.click(makeXpathObj("(//button[@role='combobox'])[6]"))
	safeScrollTo(makeXpathObj("//option[text()='Date']"))
    WebUI.click(makeXpathObj("//option[text()='Date']"))

    WebUI.click(makeXpathObj("//button[normalize-space(text())='APPLY AND SEARCH']"))
	/*/

    // 🔹 Tarih karşılaştırması
   String sortedTime = WebUI.getText(makeXpathObj("(.//td[contains(@class, 'ant-table-cell')])[5]"))

   // dd/MM/yyyy HH:mm formatlı iki stringi Date'e çevir
   SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm")
   Date sortedDate = sdf.parse(sortedTime)
   Date originalDate = sdf.parse(timeStamp)

   // Karşılaştırmayı yap
  // assert sortedDate.before(originalDate)

    // 🔹 Pagination ile sayfa geçişi ve signature kontrolü
    List<String> pageNumbers = ['2']
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

  // 🔹 3. sayfadaki ilk IP ve attack name’i al
TestObject ipCellObj = makeXpathObj("(.//td[contains(@class, 'ant-table-cell')])[1]")


safeScrollTo(ipCellObj)




WebUI.comment("📋 Alınan değerler – IP:  ${ipAddress}")

// 🔹 IP filtresi (Combobox'a scroll ve seçim)
safeScrollTo(makeXpathObj("//div[text()='FILTER OPTIONS']"))
WebUI.click(makeXpathObj("//div[text()='FILTER OPTIONS']"))
WebUI.delay(4)

TestObject ipInput = makeXpathObj("//input[@placeholder='Search IP Address']")
safeScrollTo(ipInput)
WebUI.setText(ipInput, ipAddress)


// 🔹 Apply & Search
TestObject applyButton = makeXpathObj("//button[normalize-space(text())='APPLY AND SEARCH']")
safeScrollTo(applyButton)
WebUI.click(applyButton)
WebUI.delay(2)

//Hostname alanını bul
TestObject Hostnameelement = makeXpathObj("//span[contains(@class,'bg-warning')"+
      " and contains(@class,'text-warning-foreground')"+
     " and contains(@class,'rounded-full')]")
safeScrollTo(Hostnameelement)
WebUI.delay(2)
WebUI.click(Hostnameelement)
WebUI.delay(2)

// ✅ Açılan hostname  alanını bul
TestObject hostname = new TestObject("hostname")
hostname.addProperty("xpath", ConditionType.EQUALS, "(//span[@data-type='string' and contains(@class, 'w-rjv-value')])[7]")

if (WebUI.verifyElementPresent(hostname, 2, FailureHandling.OPTIONAL)) {
	String hostnameip = WebUI.getText(hostname)
	println("🟢 İp Değeri: " + hostnameip)

	if (HostAddress.contains(hostnameip)) {
		KeywordUtil.logInfo("✅ ip ile Hostname değeri eşleşiyor.")
	} else {
		KeywordUtil.markWarning("❌ip ile Hostname değeri  eşleşmiyor!\nAHostname: ${hostnameip}\nHostMessage: ${HostAddress}")
	}
} else {
	KeywordUtil.markWarning("❌ Açılan saydafa İp alanı bulunamadı!")
}
WebUI.delay(2)
//
WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Mitre Close'))

//View alanını bul
TestObject View = makeXpathObj("//div[contains(@class,'inline-flex') and contains(@class,'cursor-pointer')]  [./*[name()='svg' and contains(@class,'lucide-info')]]")

safeScrollTo(View)
WebUI.delay(2)
WebUI.click(View)
KeywordUtil.logInfo("✅ View butonu tıklandı")
WebUI.delay(2)

// 2. 5. path'in üstündeki g' elementi — mouseOver yapılacak
TestObject targetIcon = new TestObject()
targetIcon.addProperty("xpath", ConditionType.EQUALS, "//*[(@stroke='#3b82f6' or @stroke='#8b5cf6' or @stroke='#ef4444') and @focusable='true']")

WebUI.mouseOver(targetIcon)
WebUI.delay(2) // tooltipin çıkması için bekle

// 3. Tooltip içerisindeki JSON'u içeren div'i bul
TestObject tooltipElement = new TestObject()
tooltipElement.addProperty("xpath", ConditionType.EQUALS, "//*[@fill='#67b7dc' and @fill-opacity='0.9' and @stroke='#ffffff' and @stroke-width='1']")

// 4. Tooltip text'ini al
String tooltipText = WebUI.getAttribute(tooltipElement, 'textContent')
println("Tooltip Text:\n" + tooltipText)

// 5. JSON gibi parse edip 'attacker_ip' içindeki değeri kontrol et
if (tooltipText.contains(ipAddress)) {
	println("✅ Tooltipte IP bulundu: " + ipAddress)
} else {
	KeywordUtil.markWarning("❌ Tooltip IP eşleşmedi! Beklenen: " + ipAddress + "\nGerçek: " + tooltipText)
}

WebUI.delay(2)

JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getWebDriver()
js.executeScript("document.body.style.zoom='0.65'")
WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Mitre Close'))

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

	if (ipAddress.contains(inputValue)) {
		KeywordUtil.logInfo("✅ İP ile input değeri eşleşiyor.")
	} else {
		KeywordUtil.markWarning("❌ İp ile input değeri eşleşmiyor!\nTooltip: ${ipAddress}\nInput: ${inputValue}")
	}
} else {
	KeywordUtil.markWarning("❌ Açılan input alanı bulunamadı!")
}

WebUI.comment("✅ Filtreleme tamamlandı: IP '${ipAddress}'")

}
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
	TestObject showattackermapbutton = new TestObject()
	showattackermapbutton.addProperty("xpath", ConditionType.EQUALS, "//button[normalize-space(.)='Show Attacker Map']")

	WebUI.click(showattackermapbutton)
	WebUI.delay(2)
	// Sayfada severity circle geldiğini doğrula
	TestObject circle = findTestObject('Object Repository/Smartdeceptive/Stroke Circle')

	// Div'in görünmesini bekle (maksimum 10 saniye)
	if (WebUI.waitForElementVisible(circle, 10)) {

	// Elementi bul
		WebElement circleelement = WebUI.findWebElement(circle, 3)

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
		TestObject backtoipprofile = new TestObject()
		backtoipprofile.addProperty("xpath", ConditionType.EQUALS, "//button[normalize-space(.)='Back to IP Profile']")
	
		WebUI.click(backtoipprofile)
		WebUI.delay(2)
		
		WebUI.waitForPageLoad(10)



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


