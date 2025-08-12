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

CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()

safeScrollTo(findTestObject('Object Repository/Riskroute/Asset Lİst/Page_/Organization Butonu'))

WebUI.click(findTestObject('Object Repository/Riskroute/Asset Lİst/Page_/Organization Butonu'))

WebUI.delay(2)

safeScrollTo(findTestObject('Object Repository/Prod SmartDeceptive/Test Company Organization'))

WebUI.click(findTestObject('Object Repository/Prod SmartDeceptive/Test Company Organization'))

WebUI.delay(3)

WebUI.waitForPageLoad(10)
/*/




WebUI.navigateToUrl('https://platform.catchprobe.io/smartdeceptive/attack-flow')

WebUI.delay(5) 
WebUI.waitForPageLoad(10)

// 🔥 
attackFlowTest()


void attackFlowTest() {
    // 🔹 İlk satır verilerini al
	TestObject attackNameObj = makeXpathObj("(.//td[contains(@class, 'ant-table-cell')])[1]")
	TestObject ipAddressObj  = makeXpathObj("(.//td[contains(@class, 'ant-table-cell')])[2]")
	TestObject timeStampObj  = makeXpathObj("(.//td[contains(@class, 'ant-table-cell')])[3]")

	safeScrollTo(attackNameObj)
	String attackName = WebUI.getText(attackNameObj)

	safeScrollTo(ipAddressObj)
	String ipAddress = WebUI.getText(ipAddressObj)

	safeScrollTo(timeStampObj)
	String timeStamp = WebUI.getText(timeStampObj)

	WebUI.comment("🔍 AttackName: ${attackName}, IP: ${ipAddress}, Timestamp: ${timeStamp}")


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
   String sortedTime = WebUI.getText(makeXpathObj("(.//td[contains(@class, 'ant-table-cell')])[3]"))

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
TestObject ipCellObj = makeXpathObj("(.//td[contains(@class, 'ant-table-cell')])[2]")
TestObject attackNameCellObj = makeXpathObj("(.//td[contains(@class, 'ant-table-cell')])[1]")

safeScrollTo(ipCellObj)
String page3Ip = WebUI.getText(ipCellObj)

safeScrollTo(attackNameCellObj)
String page3AttackName = WebUI.getText(attackNameCellObj)

WebUI.comment("📋 Alınan değerler – IP: ${page3Ip}, AttackName: ${page3AttackName}")

// 🔹 IP filtresi (Combobox'a scroll ve seçim)
safeScrollTo(makeXpathObj("//div[text()='FILTER OPTIONS']"))
WebUI.click(makeXpathObj("//div[text()='FILTER OPTIONS']"))
WebUI.delay(4)
TestObject ipComboBox = makeXpathObj("(//button[@role='combobox'])[2]")

safeScrollTo(ipComboBox)
WebUI.click(ipComboBox)

TestObject ipInput = makeXpathObj("//input[@role='combobox' and @type='text']")
safeScrollTo(ipInput)
WebUI.setText(ipInput, page3Ip)
WebUI.sendKeys(ipInput, Keys.chord(Keys.ENTER)) // dropdown kapanması için

// 🔹 Attack Name (keyword input)
TestObject keywordInput = makeXpathObj("(//input[@type='text' and not(@role='combobox')])[1]") // sade text input
safeScrollTo(keywordInput)
WebUI.setText(keywordInput, page3AttackName)

// 🔹 Apply & Search
TestObject applyButton = makeXpathObj("//button[normalize-space(text())='APPLY AND SEARCH']")
safeScrollTo(applyButton)
WebUI.click(applyButton)
WebUI.delay(2)

TestObject View = makeXpathObj("(.//td[contains(@class, 'ant-table-cell')])[6]")

safeScrollTo(View)
WebUI.click(View)
WebUI.delay(2)

// ✅ Açılan ilk input alanını bul
TestObject viewField = new TestObject("viewField")
viewField.addProperty("xpath", ConditionType.EQUALS, "(//div[contains(@class, 'mb-1') and contains(@class, 'font-semibold') and contains(@class, 'text-muted-foreground')]/following-sibling::div[1])[3]")

if (WebUI.verifyElementPresent(viewField, 2, FailureHandling.OPTIONAL)) {
	String ViewValue = WebUI.getText(viewField)
	println("🟢 View Değeri: " + ViewValue)

	if (page3AttackName.contains(ViewValue)) {
		KeywordUtil.logInfo("✅ Message ile Attackname değeri eşleşiyor.")
	} else {
		KeywordUtil.markWarning("❌ Message ile Attack name değeri eşleşmiyor!\nAttackname: ${page3AttackName}\nMessage: ${ViewValue}")
	}
} else {
	KeywordUtil.markWarning("❌ Açılan saydafa Mesaj alanı bulunamadı!")
}
WebUI.delay(2)
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

	if (page3Ip.contains(inputValue)) {
		KeywordUtil.logInfo("✅ İP ile input değeri eşleşiyor.")
	} else {
		KeywordUtil.markWarning("❌ İp ile input değeri eşleşmiyor!\nTooltip: ${page3Ip}\nInput: ${inputValue}")
	}
} else {
	KeywordUtil.markWarning("❌ Açılan input alanı bulunamadı!")
}

WebUI.comment("✅ Filtreleme tamamlandı: IP '${page3Ip}', AttackName '${page3AttackName}'")

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
	WebUI.click(findTestObject("Object Repository/SmartDeceptive/Show Attacker Map Button"))
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
		WebUI.click(findTestObject("Object Repository/SmartDeceptive/Back to IP Profile Button"))
		WebUI.waitForPageLoad(10)

	// 4️⃣ Cyber Kill Chain tıkla
	WebUI.click(findTestObject("Object Repository/SmartDeceptive/Cyber Kill Chain Button"))
	WebUI.delay(2)

	boolean foundValidButton = false
// 5️⃣ Butonlar arasında 0 < count < 1000 olanı bul
for (int i = 1; i <= 6; i++) {
	TestObject countObj = new TestObject()
	countObj.addProperty("xpath", ConditionType.EQUALS, "(//div[contains(@class,'text-lg font-semibold opacity')])[" + i + "]")
	String countText = WebUI.getText(countObj).trim()

	if (countText.isNumber()) {
		int count = Integer.parseInt(countText)
		if (count > 0 && count < 1000) {
			WebUI.comment("📌 ${i}. buton verisi: ${count}")

			TestObject clickableBtn = new TestObject()
			clickableBtn.addProperty("xpath", ConditionType.EQUALS, "(//div[contains(@class,'text-lg font-semibold opacity')])[" + i + "]")

			WebUI.mouseOver(clickableBtn)
			WebUI.delay(1)
			WebUI.click(clickableBtn)
			WebUI.waitForPageLoad(10)

			// ✅ Buradan sonra "bulunduysa yapılacaklar" kısmı:
			verifyPagination(count)

			// İstersen log veya ek işlemler burada
			WebUI.comment("✅ Pagination kontrolü tamamlandı.")
			
			// Örnek: özel veri çekme, detay açma vs.
			// getTooltipOrVerifyTableData(count)

			foundValidButton = true
			break
		}
	}
}

// 🔚 Eğer geçerli buton bulunmadıysa:
if (!foundValidButton) {
	WebUI.comment("⚠️ Hiçbir buton 0 < count < 1000 aralığında değil. İşlem yapılmadı.")
	KeywordUtil.markWarning("Uygun buton bulunamadı, test devam etmedi.")
	// KeywordUtil.markFailed("Test durduruldu: Uygun buton bulunamadı.") // istersen fail et
	WebUI.delay(2)
	WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Mitre Close'))
	
}

	
void verifyPagination(int totalCount) {
    int expectedPageCount = (int) Math.ceil(totalCount / 10.0)
    WebUI.comment("🎯 Beklenen Sayfa Sayısı: ${expectedPageCount}")

    // 🔽 Sayfa numaralarının XPath'i
    TestObject pageLinkObj = new TestObject()
    pageLinkObj.addProperty("xpath", ConditionType.EQUALS, "//ul[contains(@class,'flex')]/li[a[not(contains(@aria-label,'previous')) and not(contains(@aria-label,'next'))]]/a")

    // 🔽 Sayfanın altına in (scroll) ve yüklenmesini bekle
    WebUI.comment("⬇️ Sayfanın altına iniliyor ve sayfa numaraları bekleniyor...")
    safeScrollTo(pageLinkObj)
    WebUI.waitForElementVisible(pageLinkObj, 2)
    WebUI.delay(1.5)  // ek sabitleme

    // 🔽 Sayfa linklerini bul ve en yüksek numarayı al
    List<WebElement> pageLinks = WebUiCommonHelper.findWebElements(pageLinkObj, 10)
    int actualLastPage = 0
    for (WebElement el : pageLinks) {
        String text = el.getText().trim()
        if (text.isInteger()) {
            int num = Integer.parseInt(text)
            actualLastPage = Math.max(actualLastPage, num)
        }
    }

    WebUI.comment("🔢 Gerçek Son Sayfa: ${actualLastPage}")
    WebUI.verifyEqual(actualLastPage, expectedPageCount)
}

// Yardımcı fonksiyonlar
TestObject makeXpathObj(String xpath) {
    TestObject to = new TestObject()
    to.addProperty("xpath", ConditionType.EQUALS, xpath)
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

void scrollToBottom() {
    JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getWebDriver()
    js.executeScript("window.scrollTo(0, document.body.scrollHeight)")
}





