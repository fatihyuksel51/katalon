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

/*/
// Riskroute sekmesine tıkla
WebUI.navigateToUrl('https://platform.catchprobe.io/riskroute')
WebUI.waitForPageLoad(10)



WebUI.click(findTestObject('Object Repository/Host List/Hosts'))

WebUI.waitForPageLoad(30)

WebUI.click(findTestObject('Object Repository/Host List/Host List'))

WebDriver driver = DriverFactory.getWebDriver()
JavascriptExecutor js = (JavascriptExecutor) driver
Actions actions = new Actions(driver)

// Target listesi
List<String> targetList = [
	'catchprobe.org',
	'teknosa',
	'176.9.66.101'
]

// Tablodan verileri oku
List<String> actualTargetList = []


for (int i = 1; i <= targetList.size(); i++) {
	// Target
	String targetXpath = "(//td[contains(@class, 'ant-table-cell-fix-left')])[" + i + "]"
	TestObject targetObj = new TestObject().addProperty("xpath", ConditionType.EQUALS, targetXpath)
	WebElement targetElem = WebUI.findWebElement(targetObj, 10)
	js.executeScript("arguments[0].scrollIntoView(true);", targetElem)
	WebUI.delay(0.2)
	actualTargetList.add(targetElem.getText())

}

// ✅ Bağımsız kontrol — listedeki tüm değerler tabloda var mı kontrol et
assert actualTargetList.containsAll(targetList)


// ✅ Log ile kontrol
println "Tablodaki Target'lar: " + actualTargetList
println "Beklenen Target'lar: " + targetList

// catchprobe.org satır index'inde asset flow kontrolü yap bul
List<WebElement> assetListTargetCellsOrg = driver.findElements(By.xpath("//td[contains(@class, 'ant-table-cell-fix-left')]"))

int targetIndexCatchprobeOrgRow = -1
for (int i = 0; i < assetListTargetCellsOrg.size(); i++) {
	if (assetListTargetCellsOrg[i].getText().trim() == 'catchprobe.org') {
		targetIndexCatchprobeOrgRow = i + 1
		break
	}
}
assert targetIndexCatchprobeOrgRow != -1 : "catchprobe.org satırı bulunamadı!"

// Go scan ikonunu bul ve mouse over yap

String xpathAssetFlowIconCatchprobeOrg = "(//div[contains(@class, 'bg-cyan-500')]/*[name()='svg'])[" + targetIndexCatchprobeOrgRow + "]"
WebElement elementAssetFlowIconCatchprobeOrg = driver.findElement(By.xpath(xpathAssetFlowIconCatchprobeOrg))
js.executeScript("arguments[0].scrollIntoView(true);", elementAssetFlowIconCatchprobeOrg)
actions.moveToElement(elementAssetFlowIconCatchprobeOrg).perform()
WebUI.delay(2.5)


// Tooltip elementini bekle ve text’ini al
TestObject tooltipObjectCatchprobeOrg = new TestObject()
tooltipObjectCatchprobeOrg.addProperty("xpath", ConditionType.EQUALS, "//div[p[text()]]")
WebUI.waitForElementVisible(tooltipObjectCatchprobeOrg, 5)

WebElement elementTooltipCatchprobeOrg = WebUI.findWebElement(tooltipObjectCatchprobeOrg, 5)
String textTooltipCatchprobeOrg = elementTooltipCatchprobeOrg.getText()
println "catchprobe.org Go Scan Tooltip Text: " + textTooltipCatchprobeOrg

// Kontrol ve aksiyon
if (textTooltipCatchprobeOrg.contains("Go Scan")) {
	println "catchprobe.org için Go Scan bulundu, tıklanıyor..."
	
} else if (textTooltipCatchprobeOrg.equalsIgnoreCase("No Scan")) {
	KeywordUtil.markFailedAndStop("❌ catchprobe.org için tooltip 'Go Scan' geldi, test durduruldu!")
} else {
	println "catchprobe.org için tooltip: '${textTooltipCatchprobeOrg}', işlem yapılmadı."
}

elementAssetFlowIconCatchprobeOrg.click()

// Tıklama sonrası Scan History sayfasının yüklenmesi için bekle
WebUI.delay(3)  // veya waitForElementVisible ile de yapılabilir

// Scan History sayfasındaki target değerini kontrol et
TestObject scanHistoryTargetObj = new TestObject()
scanHistoryTargetObj.addProperty("xpath", ConditionType.EQUALS, "//div[contains(@class, 'h-20') and contains(@class, 'rounded-full')]//span")

// Elementin gelmesini bekle
WebUI.waitForElementVisible(scanHistoryTargetObj, 10)

// Elementin text'ini al
String scanHistoryTargetText = WebUI.getText(scanHistoryTargetObj)
println "Scan History Sayfasındaki Target: " + scanHistoryTargetText

// Beklenen değerle karşılaştır
assert scanHistoryTargetText.trim() == "catchprobe.org" : "❌ Scan History sayfasında beklenen target bulunamadı!"









//



//