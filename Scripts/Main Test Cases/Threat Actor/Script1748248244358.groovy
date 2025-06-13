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
import internal.GlobalVariable
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import org.openqa.selenium.WebElement
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.Keys
import org.openqa.selenium.WebElement as Keys
import com.kms.katalon.core.testobject.ConditionType


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
WebUI.waitForElementVisible(findTestObject('Object Repository/otp/Page_/a_PLATFORM LOGIN'), 30)
WebUI.click(findTestObject('Object Repository/otp/Page_/a_PLATFORM LOGIN'))
WebUI.waitForElementVisible(findTestObject('Object Repository/otp/Page_/input_Email Address_email'), 30)
WebUI.setText(findTestObject('Object Repository/otp/Page_/input_Email Address_email'), 'fatih.yuksel@catchprobe.com')
WebUI.setEncryptedText(findTestObject('Object Repository/otp/Page_/input_Password_password'), 'RigbBhfdqOBDK95asqKeHw==')
WebUI.click(findTestObject('Object Repository/otp/Page_/button_Sign in'))
WebUI.delay(3)

// OTP işlemi
def randomOtp = (100000 + new Random().nextInt(900000)).toString()
WebUI.setText(findTestObject('Object Repository/otp/Page_/input_OTP Digit_vi_1_2_3_4_5'), randomOtp)
WebUI.click(findTestObject('Object Repository/otp/Page_/button_Verify'))
WebUI.waitForPageLoad(30)
/*/
WebUI.delay(3)


// Threatway sekmesine git
WebUI.navigateToUrl('https://platform.catchprobe.org/threatway')
WebUI.waitForPageLoad(30)




CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()
// Threat Actor linkini bul
TestObject threatActorLink = findTestObject('Object Repository/Threat Actor/Threatactorlink')

// WebElement olarak al
WebElement linkElement = WebUiCommonHelper.findWebElement(threatActorLink, 10)
// JavascriptExecutor al
JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getWebDriver()



// Scroll edip görünür yap
scrollToVisible(linkElement, js)

// Tıkla
WebUI.click(threatActorLink)
WebUI.waitForPageLoad(30)

CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()

WebUI.verifyElementPresent(findTestObject('Object Repository/Threat Actor/Threataa/Page_/div_ascendo'), 0)


WebUI.setText(findTestObject('Object Repository/Threat Actor/Threataa/Page_/input_Cybercriminals_flex rounded-md border_26dcbc'),
	'apt')

//WebUI.setText(findTestObject('Object Repository/Threat Actor/Threataa/Page_/input_Cybercriminals_flex rounded-md border_26dcbc_1'),	'ap')

//WebUI.setText(findTestObject('Object Repository/Threat Actor/Threataa/Page_/input_Cybercriminals_flex rounded-md border_26dcbc_1_2'),	'apt')

WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/button_SEARCH'))

// DESC / ASC butonunu bul
TestObject sortButton = new TestObject().addProperty('xpath', ConditionType.EQUALS, "//button[contains(text(),'DESC') or contains(text(),'ASC')]")

// Search butonu
TestObject searchButton = new TestObject().addProperty('xpath', ConditionType.EQUALS, "//button[contains(.,'SEARCH')]")

// İlk Sonuç başlığı
TestObject firstResult = new TestObject().addProperty('xpath', ConditionType.EQUALS, "(//div[contains(@class, 'flex items-center gap-2 text-xl font-semibold')])[1]")

// Mevcut DESC/ASC değerini oku
String currentSort = WebUI.getText(sortButton)

// DESC değilse tıkla
if (!currentSort.contains('DESC')) {
	WebUI.click(sortButton)
}


// Search'e bas
WebUI.click(searchButton)
WebUI.delay(2)

// İlk sonucu kontrol et
String firstTitleDesc = WebUI.getText(firstResult)
assert firstTitleDesc.contains('APT73')

// DESC butonuna tıkla ASC yap
WebUI.click(sortButton)

// Search'e bas
WebUI.click(searchButton)
WebUI.delay(2)

// İlk sonucu kontrol et
String firstTitleAsc = WebUI.getText(firstResult)
assert firstTitleAsc.contains('APT28')

WebUI.waitForElementClickable(findTestObject('Object Repository/Threat Actor/Threataa/Page_/firstResult'), 10)
WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/firstResult'))

// MİTRE ATT&ck Kısmına tıkla
WebUI.waitForElementClickable(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Mitre Attack'), 10)
WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Mitre Attack'))
 
//Mitre ve Enterprise değerlerini eşitle

TestObject Mitredeğer = new TestObject().addProperty('xpath', ConditionType.EQUALS, "//div[contains(@class,'cursor-pointer') and contains(.,'MITRE ATT&CK')]/span")

TestObject Enterprisedeğer = new TestObject().addProperty('xpath', ConditionType.EQUALS, "//div[contains(@class,'flex items-center') and contains(.,'Enterprise Attacks')]/span")

String Mitreveri = WebUI.getText(Mitredeğer)

String Enterpriseveri = WebUI.getText(Enterprisedeğer)

assert Enterpriseveri.contains(Mitreveri)

// 1. Input Capture'a tıkla
WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Input Capture'))
WebUI.delay(4)

// 2. Input Capture Keylogging textini al
WebUI.waitForElementPresent(findTestObject('Object Repository/Threat Actor/Threataa/Page_/İnput Capture Keylogging'), 10)
String inputCaptureKeyloggingText = WebUI.getText(findTestObject('Object Repository/Threat Actor/Threataa/Page_/İnput Capture Keylogging'))
WebUI.comment("Input Capture Keylogging Text: " + inputCaptureKeyloggingText)

// 3. Mitre Close ile sayfayı kapat
WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/İnput Close'))

// 4. Keylogging textini al
WebUI.waitForElementPresent(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Keylogging'), 10)
String keyloggingText = WebUI.getText(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Keylogging'))
WebUI.comment("Keylogging Text: " + keyloggingText)

// 5. İki texti karşılaştır
WebUI.verifyMatch(inputCaptureKeyloggingText, keyloggingText, false)
WebUI.comment("Input Capture Keylogging ve Keylogging textleri eşleşti.")

// 6. Keylogging'e tıkla
WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Keylogging'))

// 7. Keyloggin First textini al
WebUI.waitForElementPresent(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Keyloggin First'), 10)
String keyloggingFirstText = WebUI.getText(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Keyloggin First'))
WebUI.comment("Keylogging First Text: " + keyloggingFirstText)

// 8. Keylogging First textini önceki textlerle karşılaştır
WebUI.verifyMatch(inputCaptureKeyloggingText, keyloggingFirstText, false)
WebUI.verifyMatch(keyloggingText, keyloggingFirstText, false)
WebUI.comment("Tüm textler eşleşti.")

// 9. Sayfayı İnput Close ile kapat
WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/İnput Close'))

// Indicators sekmesine tıkla
WebUI.waitForElementClickable(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Indicators'), 15)
WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Indicators'))

// CSV butonunun tıklanabilir olduğunu kontrol et
boolean isCsvClickable = WebUI.waitForElementClickable(findTestObject('Object Repository/Threat Actor/Threataa/Page_/CSV button'), 10, FailureHandling.OPTIONAL)

if (isCsvClickable) {
	WebUI.comment('CSV butonu tıklanabilir.')
} else {
	WebUI.comment('CSV butonu tıklanabilir değil.')
	WebUI.takeScreenshot()
	WebUI.verifyElementClickable(findTestObject('Object Repository/Threat Actor/Threataa/Page_/CSV button')) // burada step fail olur zaten
}

// Tools sekmesine tıkla
WebUI.waitForElementClickable(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Tools'), 15)
WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Tools'))

// Inputa "Downdelph" yaz
WebUI.waitForElementVisible(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Tolls İnput'), 10)
WebUI.setText(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Tolls İnput'), 'Downdelph')

// Downdelph text'inin varlığını kontrol et
WebUI.waitForElementVisible(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Tools Downdelph'), 10)
String toolName = WebUI.getText(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Tools Downdelph'))
WebUI.verifyMatch(toolName, 'Downdelph', false)

// CSV butonunun tıklanabilir olduğunu doğrula
WebUI.waitForElementClickable(findTestObject('Object Repository/Threat Actor/Threataa/Page_/button_CSV'), 10)
WebUI.comment('Tools sekmesinde CSV butonu tıklanabilir durumda.')

// Contacts sekmesine tıkla
WebUI.waitForElementClickable(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Contacts'), 10)
WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Contacts'))

// Contacts sekmesinde CSV butonunun tıklanabilir olduğunu doğrula
WebUI.waitForElementClickable(findTestObject('Object Repository/Threat Actor/Threataa/Page_/button_CSV'), 10)
WebUI.comment('Contacts sekmesinde CSV butonu tıklanabilir durumda.')

// Timeline sekmesine tıkla
WebUI.waitForElementClickable(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Timeline'), 10)
WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Timeline'))

// Mitre sekmesini kapat
WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Mitre Close'))


//WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/div_Nation State 15'))

// WebElement olarak al
TestObject paginationObject = findTestObject('Object Repository/Threat Actor/Threataa/Page_/a_2')
WebElement paginationElement = WebUiCommonHelper.findWebElement(paginationObject, 10)

// Scroll edip görünür yap
scrollToVisible(paginationElement, js)
js.executeScript("window.scrollTo(0, document.body.scrollHeight)")
WebUI.delay(1)

// Tıkla
WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/a_2'))
WebUI.delay(3)

WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/div_All'))



/*WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/span_Select Company Sector'))

WebUI.setText(findTestObject('Object Repository/Threat Actor/Threataa/Page_/input_Select Company Sector_border border-i_8cdb20'),
	'')
	 */
// XPath tanımı - butonun class'ına göre (görseldeki yapıya uygun)
String closeButtonXPath = "//button[contains(@class, 'bottom-3') and contains(@class, 'right-3') and contains(@class, 'justify-start')]"

// Çarpı butonunun var olup olmadığını kontrol et
TestObject closeButton = new TestObject().addProperty("xpath", com.kms.katalon.core.testobject.ConditionType.EQUALS, closeButtonXPath)

if (WebUI.verifyElementPresent(closeButton, 3, FailureHandling.OPTIONAL)) {
	WebUI.click(closeButton)
	WebUI.comment("Çarpı butonuna tıklandı.")
} else {
	WebUI.comment("Çarpı butonu görünmüyor.")
}

// 2. Start Date butonuna tıkla
TestObject startDateButton = new TestObject()
startDateButton.addProperty("xpath", ConditionType.EQUALS, "//button[contains(.,'Select Start Date')]")
WebUI.click(startDateButton)

// 3. Takvimde gösterilen ay 'January 2025' değilse, önceki ay butonuna bas
TestObject monthTitle = new TestObject()
monthTitle.addProperty("xpath", ConditionType.EQUALS, "//div[contains(@class,'text-sm font-medium') and text()='January 2025']")

if (!WebUI.verifyElementPresent(monthTitle, 2, FailureHandling.OPTIONAL)) {
    TestObject prevMonthButton = new TestObject()
    prevMonthButton.addProperty("xpath", ConditionType.EQUALS, "//button[@aria-label='Go to previous month']")
    // İstediğin aya gelene kadar önceki aya tıkla
    while (!WebUI.verifyElementPresent(monthTitle, 2, FailureHandling.OPTIONAL)) {
        WebUI.click(prevMonthButton)
		WebUI.delay(2)
    }
}

// 4. 6 Ocak butonuna tıkla
TestObject dayButton = new TestObject()
dayButton.addProperty("xpath", ConditionType.EQUALS, "//button[@name='day' and text()='6']")
WebUI.click(dayButton)

// End Date butonuna tıkla
TestObject endDateButton = new TestObject()
endDateButton.addProperty("xpath", ConditionType.EQUALS, "//button[contains(.,'Select End Date')]")
WebUI.click(endDateButton)

// Month Title obje tanımı
TestObject monthTitleend = new TestObject()
monthTitleend.addProperty("xpath", ConditionType.EQUALS, "//div[contains(@class,'text-sm font-medium') and text()='January 2025']")

if (!WebUI.verifyElementPresent(monthTitleend, 2, FailureHandling.OPTIONAL)) {
    TestObject prevMonthButton = new TestObject() // ← bu burada local scope'ta kalıyor
    prevMonthButton.addProperty("xpath", ConditionType.EQUALS, "//button[@aria-label='Go to previous month']")
    while (!WebUI.verifyElementPresent(monthTitleend, 2, FailureHandling.OPTIONAL)) {
        WebUI.click(prevMonthButton)
		WebUI.delay(2)
    }
}


// 8 Ocak butonuna tıkla
TestObject day8Button = new TestObject()
day8Button.addProperty("xpath", ConditionType.EQUALS, "//button[@name='day' and text()='8']")
WebUI.click(day8Button)

String currentSortseen = WebUI.getText(sortButton)
// DESC değilse tıkla
if (!currentSortseen.contains('DESC')) {
	WebUI.click(sortButton)
}

//First Seen kontrolü yap
// "Sort By" dropdown butonu
TestObject sortDropdown = new TestObject().addProperty("xpath", ConditionType.EQUALS, "//button[contains(text(),'First Seen') or contains(text(),'Last Seen')]")

// "First Seen" menü öğesi
TestObject firstSeenOption = new TestObject().addProperty("xpath", ConditionType.EQUALS, "//div[@role='option' and contains(., 'First Seen')]")

// "Last Seen" menü öğesi
TestObject lastSeenOption = new TestObject().addProperty("xpath", ConditionType.EQUALS, "//div[@role='option' and contains(., 'Last Seen')]")

// "Sort By" alanındaki mevcut değeri al
String currentSortText = WebUI.getText(sortDropdown)

// Eğer "First Seen" seçili değilse önce onu seç
if (!currentSortText.contains("First Seen")) {
	WebUI.click(sortDropdown)
	WebUI.waitForElementClickable(firstSeenOption, 5)
	WebUI.click(firstSeenOption)
	WebUI.comment("First Seen seçildi.")
}


// 8. Search butonuna bas
WebUI.click(searchButton)
WebUI.delay(2)

// 9. İlk sonucun 'Black Widow' olduğunu doğrula
String firstResultText = WebUI.getText(firstResult)
WebUI.verifyMatch(firstResultText, "ascendo", false)


//10 Ardından tekrar dropdown'a tıklayıp Last Seen seç
WebUI.click(sortDropdown)
WebUI.waitForElementClickable(lastSeenOption, 5)
WebUI.click(lastSeenOption)
WebUI.comment("Last Seen seçildi.")

// 8. Search butonuna bas
WebUI.click(searchButton)
WebUI.delay(2)

//11. İlk sonucun 'Black Widow' olduğunu doğrula
String firstResultTextEnd = WebUI.getText(firstResult)
WebUI.verifyMatch(firstResultTextEnd, "Black Widow", false)





// 12. DESC butonuna tıkla ASC yap
WebUI.click(sortButton)
WebUI.delay(2)

// 13. Search butonuna bas
WebUI.click(searchButton)
WebUI.delay(2)

// 14. İlk sonucun 'Black Widow' olduğunu doğrula
String firstResultTextAsc = WebUI.getText(firstResult)
WebUI.verifyMatch(firstResultTextAsc, "Black Widow", false)



WebUI.comment('Threat Actor Test Senaryosu Tamamlandı.')
