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

WebUI.navigateToUrl('https://platform.catchprobe.io/smartdeceptive/pcap-list')

WebUI.delay(5) 
WebUI.waitForPageLoad(10)

// 🔥 
pcaplistTest()


void pcaplistTest() {
    // 🔹 İlk satır verilerini al
	TestObject attackNameObj = makeXpathObj("(.//td[contains(@class, 'ant-table-cell')])[1]")
	TestObject filenameObj  = makeXpathObj("(.//td[contains(@class, 'ant-table-cell')])[2]")
	TestObject timeStampObj  = makeXpathObj("(.//td[contains(@class, 'ant-table-cell')])[4]")

	safeScrollTo(attackNameObj)
	String attackName = WebUI.getText(attackNameObj)

	safeScrollTo(filenameObj)
	String ipAddress = WebUI.getText(filenameObj)

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
   String sortedTime = WebUI.getText(makeXpathObj("(.//td[contains(@class, 'ant-table-cell')])[4]"))

   // dd/MM/yyyy HH:mm formatlı iki stringi Date'e çevir
   SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm")
   Date sortedDate = sdf.parse(sortedTime)
   Date originalDate = sdf.parse(timeStamp)

   // Karşılaştırmayı yap
  // assert sortedDate.before(originalDate)

    // 🔹 Pagination ile sayfa geçişi ve signature kontrolü
    List<String> pageNumbers = ['2','3','4','5']
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
TestObject filename = makeXpathObj("(.//td[contains(@class, 'ant-table-cell')])[1]")
TestObject UsernameKeyword = makeXpathObj("(.//td[contains(@class, 'ant-table-cell')])[2]")

safeScrollTo(filename)
String page3Ip2 = WebUI.getText(filename)
String page3Ip = page3Ip2.split("-")[-1]  
println ("🟢 Honeypoot Name: " + page3Ip)

safeScrollTo(UsernameKeyword)
String page3AttackName = WebUI.getText(UsernameKeyword)

WebUI.comment("📋 Alınan değerler – IP: ${page3Ip}, AttackName: ${page3AttackName}")

// 🔹 IP filtresi (Combobox'a scroll ve seçim)
safeScrollTo(makeXpathObj("//div[text()='FILTER OPTIONS']"))
WebUI.click(makeXpathObj("//div[text()='FILTER OPTIONS']"))
WebUI.delay(4)
/*/
TestObject ipComboBox = makeXpathObj("(//button[@role='combobox'])[2]")

safeScrollTo(ipComboBox)
WebUI.click(ipComboBox)

TestObject ipInput = makeXpathObj("//input[@role='combobox' and @type='text']")
safeScrollTo(ipInput)
WebUI.setText(ipInput, page3Ip)
WebUI.sendKeys(ipInput, Keys.chord(Keys.ENTER)) // dropdown kapanması için
/*/

// 🔹 Attack Name (keyword input)
TestObject keywordInput = makeXpathObj("(//input[@type='text' and not(@role='combobox')])[1]") // sade text input
safeScrollTo(keywordInput)
if (page3Ip == null || page3AttackName.trim().toLowerCase() == "n/a") {
    WebUI.setText(keywordInput, "")
} else {
    WebUI.setText(keywordInput, page3Ip)
}

// 🔹 Apply & Search
TestObject applyButton = makeXpathObj("//button[normalize-space(text())='APPLY AND SEARCH']")
safeScrollTo(applyButton)
WebUI.click(applyButton)
WebUI.delay(2)
/*/

TestObject View = makeXpathObj("(.//td[contains(@class, 'ant-table-cell')])[7]")

safeScrollTo(View)
WebUI.click(View)
WebUI.delay(2)

// ✅ Açılan ilk input alanını bul
TestObject viewField = new TestObject("viewField")
viewField.addProperty("xpath", ConditionType.EQUALS, "(//div[contains(@class, 'mb-1') and contains(@class, 'font-semibold') and contains(@class, 'text-muted-foreground')]/following-sibling::div[1])[5]")

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
/*/

safeScrollTo(filename)
WebUI.click(filename)
WebUI.delay(4)
WebUI.waitForPageLoad(10)

// ✅ Açılan ilk input alanını bul
TestObject inputField = new TestObject("inputField")
inputField.addProperty("xpath", ConditionType.EQUALS, "(.//td[contains(@class, 'ant-table-cell')])[1]")

if (WebUI.verifyElementPresent(inputField, 2, FailureHandling.OPTIONAL)) {
	String inputValue = WebUI.getText(inputField)
	println("🟢 File Name: " + inputValue)

	if (page3Ip2.contains(inputValue)) {
		KeywordUtil.logInfo("✅ File Name ile input değeri eşleşiyor.")
	} else {
		KeywordUtil.markWarning("❌ File Name ile input değeri eşleşmiyor!\nTooltip: ${page3Ip}\nInput: ${inputValue}")
	}
} else {
	KeywordUtil.markWarning("❌ Açılan input alanı bulunamadı!")
}

WebUI.comment("✅ Filtreleme tamamlandı: IP '${page3Ip}', AttackName '${page3AttackName}'")

}

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
	WebElement element = WebUiCommonHelper.findWebElement(to, 2)
	JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getWebDriver()
	js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element)
	WebUI.delay(0.5)
	return element
}

void scrollToBottom() {
	JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getWebDriver()
	js.executeScript("window.scrollTo(0, document.body.scrollHeight)")
}

