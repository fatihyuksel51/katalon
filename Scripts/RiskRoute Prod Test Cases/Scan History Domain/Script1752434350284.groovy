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


// ✅ Güvenli scroll fonksiyonu
WebElement safeScrollTo(TestObject to) {
	if (to == null) {
		KeywordUtil.markFailed("❌ TestObject NULL – Repository yolunu kontrol et.")
		return null
	}
	if (!WebUI.waitForElementPresent(to, 5, FailureHandling.OPTIONAL)) {
		KeywordUtil.logInfo("ℹ️ Element not present, scroll işlemi atlandı: ${to.getObjectId()}")
		return null
	}
	WebElement element = WebUiCommonHelper.findWebElement(to, 5)
	JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getWebDriver()
	js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element)
	WebUI.delay(0.5)
	return element
}

// Tarayıcıyı aç ve siteye git
WebUI.openBrowser('')

WebUI.navigateToUrl('https://platform.catchprobe.org/')

WebUI.maximizeWindow()

// Login işlemleri
WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'), 30)

WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'))

WebUI.waitForElementVisible(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 30)

WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 'katalon.test@catchprobe.com')

WebUI.setEncryptedText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Password_password'), 'RigbBhfdqOBDK95asqKeHw==')

WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Sign in'))

WebUI.delay(5)

// OTP işlemi
def randomOtp = (100000 + new Random().nextInt(900000)).toString()

WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'), randomOtp)

WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Verify'))

WebUI.delay(5)

WebUI.waitForPageLoad(10)


WebUI.delay(3)

safeScrollTo(findTestObject('Object Repository/Riskroute/Asset Lİst/Page_/Organization Butonu'))
WebUI.click(findTestObject('Object Repository/Riskroute/Asset Lİst/Page_/Organization Butonu'))
WebUI.delay(2)
safeScrollTo(findTestObject('Object Repository/Riskroute/MailTest Organization'))
WebUI.click(findTestObject('Object Repository/Riskroute/MailTest Organization'))

WebUI.delay(3)

WebUI.navigateToUrl('https://platform.catchprobe.org/riskroute/scan-history')

WebUI.waitForPageLoad(10)

WebUI.delay(3)


WebDriver driver = DriverFactory.getWebDriver()
JavascriptExecutor js = (JavascriptExecutor) driver
Actions actions = new Actions(driver)

SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm")
Date now = new Date()
String todayStr = new SimpleDateFormat("dd/MM/yyyy").format(now)
long limitMillis = 2 * 60 * 60 * 1000

int rowCount = WebUiCommonHelper.findWebElements(
	new TestObject().addProperty("xpath", ConditionType.EQUALS, "//tr[contains(@class, 'ant-table-row')]"),	10).size()
KeywordUtil.logInfo("📌 Scan History satır sayısı: " + rowCount)

long selectedDiff = -1
int selectedIndex = -1

for (int i = 1; i <= rowCount; i++) {
	TestObject updatedAtObj = new TestObject()
	updatedAtObj.addProperty("xpath", ConditionType.EQUALS, "(//tbody//tr[contains(@class,'ant-table-row')]/td[8]/span)[" + i + "]")

	String updatedAtStr = WebUI.getText(updatedAtObj).trim()
	if (updatedAtStr.startsWith(todayStr)) {
		Date updatedAtDate = dateFormat.parse(updatedAtStr)
		long diffMillis = now.getTime() - updatedAtDate.getTime()

		if (diffMillis >= limitMillis) {
			if (selectedDiff == -1 || diffMillis < selectedDiff) {
				selectedDiff = diffMillis
				selectedIndex = i
			}
		}
	}
}

if (selectedIndex != -1) {
	KeywordUtil.logInfo("✅ 2 saatten eski en yeni satır bulundu: Satır " + selectedIndex)

	TestObject goDetailButton = new TestObject()
	goDetailButton.addProperty("xpath", ConditionType.EQUALS,
		"(//tr[contains(@class, 'ant-table-row')])[" + selectedIndex + "]//button[1]")

	WebElement detailButtonEl = WebUiCommonHelper.findWebElement(goDetailButton, 5)	
	js.executeScript("arguments[0].scrollIntoView({block: 'center'});", detailButtonEl)
	WebUI.delay(0.5)

	WebUI.click(goDetailButton)
	WebUI.waitForPageLoad(10)
} else {
	KeywordUtil.markFailed("❌ 2 saatten eski satır bulunamadı.")
}


// 📌 Sekme ve component eşleşmesi
def tabComponents = [
	'Recon': ['OS Intelligence', 'DNS Intelligence', 'WhoIs Intelligence', 'Subdomain Intelligence', 'Http Analysis', 'Certificate Analysis'],
	'Network': ['Service Fingerprinting'],
	'Vulnerability': ['Vulnerability Intelligence'],
	'OSINT': ['Phishing Domain Lists', 'Network Intelligence', 'Netlas', 'Content Intelligence'],
	'Tools': ['BGP (Border Gateway Protocol)', 'Ping Results', 'Traceroute Intelligence'],
	'DarkMap': ['Darkmap Intelligence'],
	'ThreatWay': ['Threatway Intelligence'],
	'SmartDeceptive': ['Smartdeceptive Intelligence']
]

js.executeScript("document.body.style.zoom='0.9'")

// 📌 Eklenen asset listeleri
def addedAssets = []
List<String> addedAssetTexts = []

// 🔁 Sekmeleri gez
tabComponents.each { tabName, components ->
	KeywordUtil.logInfo("📂 Sekme: ${tabName}")

	TestObject tabButton = findTestObject("Object Repository/Scan History-Domain/${tabName}")
	WebElement tabButtonEl = WebUiCommonHelper.findWebElement(tabButton, 5)
	js.executeScript("arguments[0].scrollIntoView(false);", tabButtonEl)
	WebUI.delay(1)
	WebUI.click(tabButton)
	WebUI.delay(1)

	// ✅ Sekmeye bir kez tıklandı, şimdi component’leri sırayla işle (yeniden tıklama yok)
	components.each { component ->
		KeywordUtil.logInfo("🔍 Component: ${component}")

		// Başlık kontrolü
		TestObject titleObj = findTestObject("Object Repository/Scan History-Domain/${component.replaceAll(' ', '')}_Title")
		if (WebUI.verifyElementPresent(titleObj, 5, FailureHandling.OPTIONAL)) {
			WebElement titleObjEl = WebUiCommonHelper.findWebElement(titleObj, 5)
			try {
				js.executeScript("arguments[0].scrollIntoView({block: 'center'});", titleObjEl)
				WebUI.delay(2)
			} catch (Exception e) {
				KeywordUtil.logInfo("⚠️ Title scroll hatası: ${e.message}")
			}
			KeywordUtil.logInfo("✔️ ${component} başlığı bulundu")
		} else {
			KeywordUtil.markWarning("⚠️ ${component} başlığı bulunamadı")
		}

		// SCAN DIFFERENCE butonuna tıkla (varsa)
		TestObject scanDiffButton = findTestObject("Object Repository/Scan History-Domain/${component.replaceAll(' ', '')}_ScanDiffButton")
		if (WebUI.verifyElementPresent(scanDiffButton, 2, FailureHandling.OPTIONAL)) {
			WebElement scanDiffButtonEl = WebUiCommonHelper.findWebElement(scanDiffButton, 5)
			try {
				//js.executeScript("arguments[0].scrollIntoView(false);", scanDiffButtonEl)
				WebUI.delay(1.5)
			} catch (Exception e) {
				KeywordUtil.logInfo("⚠️ ScanDiff scroll hatası: ${e.message}")
			}
			WebUI.click(scanDiffButton)
			WebUI.delay(1)
			CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()
			KeywordUtil.logInfo("✅ SCAN DIFFERENCE tıklandı.")
			WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Mitre Close'))
		} else {
			KeywordUtil.logInfo("➖ ${component} için SCAN DIFFERENCE butonu yok.")
		}

		// Completed At kontrolü
		TestObject completedAtObj = findTestObject("Object Repository/Scan History-Domain/${component.replaceAll(' ', '')}_CompletedAt")
		WebElement completedAtEl = WebUiCommonHelper.findWebElement(completedAtObj, 5)
		//js.executeScript("arguments[0].scrollIntoView(true);", completedAtEl)
		WebUI.delay(1)
		KeywordUtil.logInfo("✅ Completed At Bulundu.")
		String completedAtText = WebUI.getText(completedAtObj).trim()
		if (completedAtText.equalsIgnoreCase('In Progress')) {
			KeywordUtil.markFailed("❌ ${component} In Progress — bu component tamamlanmamış!")
		}

		// ➕ Add işlemi sadece özel component'lerde yapılır
		if (component in ['Network Intelligence', 'Netlas']) {
			TestObject addButton = findTestObject("Object Repository/Scan History-Domain/${component.replaceAll(' ', '')}_AddButton")
			WebElement addButtonEl = WebUiCommonHelper.findWebElement(addButton, 5)

			if (WebUI.verifyElementPresent(addButton, 2, FailureHandling.OPTIONAL)) {
				try {
					js.executeScript("arguments[0].scrollIntoView({block: 'center'});", addButtonEl)
					WebUI.delay(3)
				} catch (Exception e) {
					KeywordUtil.logInfo("⚠️ Add buton scroll edilemedi.")
				}
				WebUI.click(addButton)
				WebUI.delay(1)

				def toastText = WebUI.getText(findTestObject('Object Repository/Scan History-Domain/Toast_Message')).trim()
				KeywordUtil.logInfo("📝 Toast mesajı: ${toastText}")

				if (toastText.toLowerCase().contains('asset added')) {
					TestObject assetTextObject = findTestObject("Object Repository/Scan History-Domain/${component.replaceAll(' ', '')}_AssetText")
					WebElement assetTextObjectEl = WebUiCommonHelper.findWebElement(assetTextObject, 5)
					if (WebUI.verifyElementPresent(assetTextObject, 2, FailureHandling.OPTIONAL)) {
						js.executeScript("arguments[0].scrollIntoView({block: 'center'});", assetTextObjectEl)
						WebUI.delay(3)
						String assetText = WebUI.getText(assetTextObject).trim()
						KeywordUtil.logInfo("📌 ${component} Asset Text: ${assetText}")
						addedAssetTexts.add(assetText)
					}
					if (!addedAssets.contains(component)) {
						addedAssets.add(component)
					}
				} else {
					KeywordUtil.markFailed("❌ İlk basışta 'Asset Added' gelmedi: ${toastText}")
				}

				// 2. defa tıklama
				TestObject addButtonAgain = findTestObject("Object Repository/Scan History-Domain/${component.replaceAll(' ', '')}_AddButton")
				WebUI.waitForElementVisible(addButtonAgain, 5)
				WebElement add2ButtonEl = WebUiCommonHelper.findWebElement(addButtonAgain, 5)
				js.executeScript("arguments[0].scrollIntoView({block: 'center'});", add2ButtonEl)
				WebUI.delay(3)
				WebUI.click(addButtonAgain)
				WebUI.delay(1)
				def secondToastText = WebUI.getText(findTestObject('Object Repository/Scan History-Domain/SecondToast_Message')).trim()
				if (secondToastText.toLowerCase().contains('already exists')) {
					KeywordUtil.logInfo("✔️ 2. basışta 'already exists' geldi.")
				} else {
					KeywordUtil.markFailed("❌ 2. basışta 'already exists' gelmedi: ${secondToastText}")
				}
			}
		}

		KeywordUtil.logInfo("✔️ ${component} kontrolü tamamlandı.")
		WebUI.delay(0.5)
	}
}

// 📌 Sonuç logları
KeywordUtil.logInfo("✅ Tüm sekme ve component kontrolleri tamamlandı.")
KeywordUtil.logInfo("📝 Eklenen asset'ler: ${addedAssets.join(', ')}")
KeywordUtil.logInfo("📋 Eklenen asset text'leri: ${addedAssetTexts.join(', ')}")

// Asset List sayfasına git
WebUI.navigateToUrl("https://platform.catchprobe.org/riskroute/asset-list")
WebUI.waitForPageLoad(10)
WebUI.delay(3)

// Silinecek asset listesi döngüsü
for (String assetText in addedAssetTexts) {
	try {
		// Her döngüde taze DOM'dan target hücreleri al
		WebUI.delay(1)
		List<WebElement> targetCells = driver.findElements(By.xpath("//td[contains(@class, 'ant-table-cell-fix-left')]"))

		int targetIndex = -1
		for (int i = 0; i < targetCells.size(); i++) {
			if (targetCells[i].getText().trim() == assetText) {
				targetIndex = i + 1
				break
			}
		}

		assert targetIndex != -1 : "❌ '${assetText}' Asset List tablosunda bulunamadı!"

		// XPath ile silme butonunu bul
		String deleteIconXpath = "(.//*[normalize-space(text()) and normalize-space(.)='info'])[" + targetIndex + "]/following::*[name()='svg'][3]"
		WebElement deleteIcon = driver.findElement(By.xpath(deleteIconXpath))

		// Scroll + hover + click
		js.executeScript("arguments[0].scrollIntoView(true);", deleteIcon)
		actions.moveToElement(deleteIcon).perform()
		WebUI.delay(0.5)
		deleteIcon.click()

		// Toast kontrol ve onay
		CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()
		WebUI.delay(2)

		WebUI.click(findTestObject('Object Repository/Scan History-Domain/Deletebutonu'))
		CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()
		WebUI.waitForElementVisible(findTestObject('Object Repository/Riskroute/Asset Lİst/Page_/Delete Toast'), 5)

		KeywordUtil.logInfo("🗑️ '${assetText}' başarıyla silindi.")
		
		// Sayfa güncellenmiş olabilir, yeniden bekle
		WebUI.delay(3)

	} catch (Exception e) {
		KeywordUtil.markWarning("⚠️ '${assetText}' silinirken hata oluştu: ${e.message}")
	}
}

KeywordUtil.logInfo("✅ Tüm asset silme işlemleri tamamlandı.")



