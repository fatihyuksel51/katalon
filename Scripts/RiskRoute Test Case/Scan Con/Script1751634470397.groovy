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
import java.text.SimpleDateFormat
import java.util.Date
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

WebDriver driver = DriverFactory.getWebDriver()
JavascriptExecutor js = (JavascriptExecutor) driver
Actions actions = new Actions(driver)


//
// Riskroute sekmesine tıkla
WebUI.navigateToUrl('https://platform.catchprobe.org/riskroute')

WebUI.waitForPageLoad(30)

CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()

WebUI.delay(3)

WebUI.waitForPageLoad(10)


WebUI.click(findTestObject('Object Repository/Scan Cron/Scan Cron'))

WebUI.delay(3)

WebUI.waitForPageLoad(10)

// CREATE CRON butonu için TestObject oluştur
TestObject createCronButtonwait = new TestObject().addProperty("xpath", ConditionType.EQUALS, "//div[text()='CREATE CRON']")

// 10 saniyeye kadar görünür mü kontrol et
if (WebUI.waitForElementVisible(createCronButtonwait, 10, FailureHandling.OPTIONAL)) {
	WebUI.comment("CREATE CRON butonu bulundu.")
} else {
	WebUI.comment("CREATE CRON butonu bulunamadı, sayfa yenileniyor...")
	WebUI.refresh()
	WebUI.waitForPageLoad(10)
	
	if (WebUI.waitForElementVisible(createCronButtonwait, 10, FailureHandling.OPTIONAL)) {
		WebUI.comment("CREATE CRON butonu refresh sonrası bulundu.")
	} else {
		KeywordUtil.markFailedAndStop("CREATE CRON butonu bulunamadı, test sonlandırılıyor.")
	}
}

// 1️⃣ Sayfadaki tüm delete butonlarını temizle
TestObject deleteButton = new TestObject().addProperty("xpath",
	ConditionType.EQUALS, "//div[contains(@class, 'bg-destructive')]")

while (WebUI.verifyElementPresent(deleteButton, 3, FailureHandling.OPTIONAL)) {
	WebUI.comment("Delete butonu bulundu, tıklanıyor...")
	WebUI.click(deleteButton)
	CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()
	WebUI.delay(2)
	WebUI.click(findTestObject('Object Repository/Asset Lİst/Page_/button_DELETE'))
	CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()
	WebUI.waitForElementVisible(findTestObject('Object Repository/Scan Cron/Delete Toast'), 15)
}
WebUI.comment("Tüm delete butonları silindi. Cron List boş.")

// 2️⃣ 'CREATE CRON' butonuna tıkla
TestObject createCronButton = new TestObject().addProperty("xpath", ConditionType.EQUALS, "//div[text()='CREATE CRON']")
WebUI.click(createCronButton)
WebUI.delay(2)

js.executeScript("document.body.style.zoom='0.8'")

// 7️⃣ Description titlre 'test' yaz
TestObject TitleInput = new TestObject().addProperty("xpath", ConditionType.EQUALS, "//input[@name='title' and @type='text']")
WebUI.setText(TitleInput, "katalon")

// 3️⃣ 'Scan' radio butonunu işaretle
TestObject autoSelectButton = new TestObject().addProperty("xpath", ConditionType.EQUALS, "//button[@role='checkbox' and contains(@class,'peer') and @type='button']")
WebUI.click(autoSelectButton)
WebUI.delay(1)

// 4️⃣ First Scan Date picker'ı aç
TestObject datePickerButton = new TestObject().addProperty("xpath", ConditionType.EQUALS, "//label[contains(.,'First Scan Date :')]/following::button[1]")
WebUI.click(datePickerButton)
WebUI.delay(1)

// 5️⃣ Bugünün gününü al ve tıkla
String todayDay = new SimpleDateFormat("d").format(new Date())
TestObject todayButton = new TestObject()
todayButton.addProperty("xpath", ConditionType.EQUALS, "//button[@name='day' and text()='" + todayDay + "']")
WebUI.click(todayButton)
WebUI.delay(1)

// 6️⃣ Saat ve Dakika seçimini yap
// Saat butonu
TestObject hourButton = new TestObject().addProperty("xpath", ConditionType.EQUALS, "(//div[@class='flex gap-2']//button[@role='combobox'])[1]")
WebUI.click(hourButton)
WebUI.delay(0.5)

// Saat select
TestObject hourSelect = new TestObject().addProperty("xpath", ConditionType.EQUALS, "(//div[@class='flex gap-2']//select)[1]")
WebUI.selectOptionByValue(hourSelect, new SimpleDateFormat("HH").format(new Date()), false)
WebUI.delay(0.5)

// Dakika butonu
TestObject minuteButton = new TestObject().addProperty("xpath", ConditionType.EQUALS, "(//div[@class='flex gap-2']//button[@role='combobox'])[2]")
WebUI.click(minuteButton)
WebUI.delay(0.5)

// Dakika select
TestObject minuteSelect = new TestObject().addProperty("xpath", ConditionType.EQUALS, "(//div[@class='flex gap-2']//select)[2]")
int nextMinute = (new Date().getMinutes() + 1) % 60
WebUI.selectOptionByValue(minuteSelect, nextMinute.toString().padLeft(2, '0'), false)
WebUI.delay(0.5)

int cronMinute = nextMinute

// 7️⃣ Description inputuna 'test' yaz
TestObject descriptionInput = new TestObject().addProperty("xpath", ConditionType.EQUALS, "//input[@name='description' and @type='text']")
WebUI.setText(descriptionInput, "test")

// Save butonunun elementini bul
TestObject saveButton = new TestObject().addProperty("xpath", ConditionType.EQUALS, "//button[text()='SAVE']")
WebElement saveElement = WebUiCommonHelper.findWebElement(saveButton, 10)

// Save butonuna scroll yap
js.executeScript("arguments[0].scrollIntoView(true);", saveElement)
WebUI.delay(1)

// Tıklama işlemi
WebUI.click(saveButton)
WebUI.delay(3)
WebUI.comment("Cron başarıyla oluşturuldu.")
WebUI.refresh()
WebUI.delay(3)
WebUI.waitForPageLoad(30)

// CREATE CRON butonu için TestObject oluştur
TestObject EDİTBUTTON = new TestObject().addProperty("xpath", ConditionType.EQUALS, "//div[contains(@class, 'bg-warning')]")

// 10 saniyeye kadar görünür mü kontrol et
if (WebUI.waitForElementVisible(EDİTBUTTON, 10, FailureHandling.OPTIONAL)) {
	WebUI.comment("EDİTBUTTON butonu bulundu.")
} else {
	WebUI.comment("EDİTBUTTON butonu bulunamadı, sayfa yenileniyor...")
	WebUI.refresh()
	WebUI.waitForPageLoad(10)
	
	if (WebUI.waitForElementVisible(EDİTBUTTON, 10, FailureHandling.OPTIONAL)) {
		WebUI.comment("EDİTBUTTON butonu refresh sonrası bulundu.")
	} else {
		KeywordUtil.markFailedAndStop("EDİTBUTTON butonu bulunamadı, test sonlandırılıyor.")
	}
}


// Edit butonunu bul
String editButtonXpath = "//div[contains(@class, 'bg-warning')]"
 WebElement editButton = driver.findElement(By.xpath(editButtonXpath))
 js.executeScript("arguments[0].scrollIntoView(true);", editButton)
 editButton.click()
 WebUI.delay(1.5)
 js.executeScript("document.body.style.zoom='0.8'")
 

WebUI.delay(2)

WebElement editButtonElement = WebUiCommonHelper.findWebElement(findTestObject('Object Repository/Scan Cron/EDIT BUTONU'), 10)
// Scroll işlemi
js.executeScript("arguments[0].scrollIntoView(true);", editButtonElement)
WebUI.delay(1)


WebUI.verifyElementNotClickable(findTestObject('Object Repository/Scan Cron/EDIT BUTONU'))

WebUI.delay(1)
TestObject descInput = findTestObject('Object Repository/Scan Cron/İnput title')
// descInput elementini bul ve scroll yap
WebElement descInputElement = WebUiCommonHelper.findWebElement(descInput, 10)
js.executeScript("arguments[0].scrollIntoView(true);", descInputElement)
WebUI.delay(1)
WebUI.waitForElementVisible(descInput, 5)
WebUI.waitForElementClickable(descInput, 5)
WebUI.click(descInput)
WebUI.clearText(descInput)
WebUI.setText(descInput, 'Katalon Text')
// Scroll işlemi
js.executeScript("arguments[0].scrollIntoView(true);", editButtonElement)
WebUI.delay(1)
WebUI.verifyElementClickable(findTestObject('Object Repository/Scan Cron/EDIT BUTONU'))

WebUI.click(findTestObject('Object Repository/Scan Cron/EDIT BUTONU'))

WebUI.waitForElementVisible(findTestObject('Object Repository/Scan Cron/Edit Toast'),15)
WebUI.refresh()
WebUI.delay(3)
	
WebUI.waitForElementVisible(findTestObject('Object Repository/Scan Cron/Katalon Text'), 15)

// 4 dakika bekle
WebUI.delay(240)
WebUI.refresh()
WebUI.delay(3)
WebUI.waitForPageLoad(30)
// CREATE CRON butonu için TestObject oluştur
TestObject createCronButtonwaitTEXT = new TestObject().addProperty("xpath", ConditionType.EQUALS, "//div[text()='CREATE CRON']")

// 10 saniyeye kadar görünür mü kontrol et
if (WebUI.waitForElementVisible(createCronButtonwaitTEXT, 10, FailureHandling.OPTIONAL)) {
	WebUI.comment("CREATE CRON butonu bulundu.")
} else {
	WebUI.comment("CREATE CRON butonu bulunamadı, sayfa yenileniyor...")
	WebUI.refresh()
	WebUI.waitForPageLoad(10)
	
	if (WebUI.waitForElementVisible(createCronButtonwaitTEXT, 10, FailureHandling.OPTIONAL)) {
		WebUI.comment("CREATE CRON butonu refresh sonrası bulundu.")
	} else {
		KeywordUtil.markFailedAndStop("CREATE CRON butonu bulunamadı, test sonlandırılıyor.")
	}
}
js.executeScript("document.body.style.zoom='0.9'")

// Tablodaki son cron tarihini al
TestObject timeCell = new TestObject()
timeCell.addProperty("xpath", ConditionType.EQUALS, "(//td[@class='ant-table-cell ant-table-cell-ellipsis !bg-card !text-card-foreground']/span)[3]")

String actualDateTimeText = WebUI.getText(timeCell)
WebUI.comment("Tablodan okunan değer: ${actualDateTimeText}")

// Tarihi parse et
SimpleDateFormat fullFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm")
Date actualDateTime = fullFormat.parse(actualDateTimeText)

// Cron kurulan dakika + 5
Calendar cal = Calendar.getInstance()
cal.setTime(new Date())
cal.set(Calendar.MINUTE, cronMinute)
cal.add(Calendar.MINUTE, 5)
Date maxExpectedTime = cal.getTime()

// Gelen tarih bu aralıkta mı kontrol
if (actualDateTime.after(maxExpectedTime)) {
	WebUI.comment("❌ Tablodaki tarih cron kurulumu +5 dk'dan fazla ileri.")
	WebUI.verifyMatch("false", "true") // Fail ettir
} else {
	WebUI.comment("✅ Tablodaki tarih cron kurulumu +5 dk içinde.")
}


WebUI.refresh()
WebUI.delay(3)
WebUI.waitForPageLoad(30)
// Trigger butonuna bas
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

// Cron tablosundaki Last Cron At değeri
TestObject lastCronAtObj = new TestObject().addProperty("xpath", ConditionType.EQUALS, "(//span[contains(@class,'text-text-light')])[2]")
String lastCronAtText = WebUI.getText(lastCronAtObj)
WebUI.comment("Scan Cron tablosundaki Last Cron At: " + lastCronAtText)

// Tarihi Date objesine çevir
SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm")
Date cronDate = dateFormat.parse(lastCronAtText)

// Scan History sayfasına git
WebUI.click(findTestObject('Object Repository/Scan/Scan'))
WebUI.delay(1)

WebUI.waitForPageLoad(30)

WebUI.click(findTestObject('Object Repository/Scan History'))
WebUI.delay(1)
WebUI.waitForPageLoad(10)

// CREATE CRON butonu için TestObject oluştur
TestObject createCronButtonTrigger = new TestObject().addProperty("xpath", ConditionType.EQUALS, "//div[text()='CREATE SCAN']")

// 10 saniyeye kadar görünür mü kontrol et
if (WebUI.waitForElementVisible(createCronButtonTrigger, 10, FailureHandling.OPTIONAL)) {
	WebUI.comment("CREATE CRON butonu bulundu.")
} else {
	WebUI.comment("CREATE CRON butonu bulunamadı, sayfa yenileniyor...")
	WebUI.refresh()
	WebUI.waitForPageLoad(10)
	
	if (WebUI.waitForElementVisible(createCronButtonTrigger, 10, FailureHandling.OPTIONAL)) {
		WebUI.comment("CREATE CRON butonu refresh sonrası bulundu.")
	} else {
		KeywordUtil.markFailedAndStop("CREATE CRON butonu bulunamadı, test sonlandırılıyor.")
	}
}



// Scan History tablosundaki en son tarih
TestObject lastHistoryCronAtObj = new TestObject().addProperty("xpath", ConditionType.EQUALS, "(//td[@class='ant-table-cell ant-table-cell-ellipsis !bg-card !text-card-foreground']/span)[3]")
String lastHistoryCronAtText = WebUI.getText(lastHistoryCronAtObj)
WebUI.comment("Scan History tablosundaki Last Cron At: " + lastHistoryCronAtText)

// Tarihi Date objesine çevir
Date historyDate = dateFormat.parse(lastHistoryCronAtText)

// Aradaki farkı hesapla
long diffMillis = Math.abs(cronDate.getTime() - historyDate.getTime())
long diffMinutes = diffMillis / (60 * 1000)

// Sonuç kontrol
if (diffMinutes <= 1) {
	KeywordUtil.markPassed("Tarih değerleri uyumlu, aradaki fark ${diffMinutes} dakika.")
} else {
	KeywordUtil.markFailed("Tarih farkı 1 dakikadan fazla: ${diffMinutes} dakika.")
}






