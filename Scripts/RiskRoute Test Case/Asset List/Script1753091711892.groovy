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
import java.text.SimpleDateFormat
import java.util.Date
import com.kms.katalon.core.testobject.ObjectRepository as OR


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
TestObject X(String xp){
	def to=new TestObject(xp)
	to.addProperty("xpath",ConditionType.EQUALS,xp)
	return to
  }

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
boolean isBrowserOpen(){
	try { DriverFactory.getWebDriver(); return true } catch(Throwable t){ return false }
  }
  
/************** Oturum **************/
void ensureSession(){
  if(isBrowserOpen()) return

  WebUI.openBrowser('')
  WebUI.maximizeWindow()
  WebUI.navigateToUrl('https://platform.catchprobe.org/')

  WebUI.waitForElementVisible(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'), 30)
  WebUI.click(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'))

  WebUI.waitForElementVisible(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 30)
  WebUI.setText(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 'katalon.test@catchprobe.com')
  WebUI.setEncryptedText(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Password_password'), 'RigbBhfdqOBDK95asqKeHw==')
  WebUI.click(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Sign in'))

  WebUI.delay(3)
  String otp = (100000 + new Random().nextInt(900000)).toString()
  WebUI.setText(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'), otp)
  WebUI.click(OR.findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Verify'))
  WebUI.delay(2)
  String Threat = "//span[text()='Threat']"
  WebUI.waitForElementVisible(X("//span[text()='Threat']"), 10, FailureHandling.OPTIONAL)
}

/************** TEST: Collections **************/
ensureSession()
/*/ Riskroute sekmesine tıkla
WebUI.navigateToUrl('https://platform.catchprobe.org/riskroute')

WebUI.delay(3)

//
safeScrollTo(findTestObject('Object Repository/Riskroute/Asset Lİst/Page_/Organization Butonu'))
WebUI.click(findTestObject('Object Repository/Riskroute/Asset Lİst/Page_/Organization Butonu'))
WebUI.delay(2)
safeScrollTo(findTestObject('Object Repository/Riskroute/MailTest Organization'))
WebUI.click(findTestObject('Object Repository/Riskroute/MailTest Organization'))

WebUI.delay(3)
/*/


WebUI.navigateToUrl('https://platform.catchprobe.org/riskroute/asset-list')

WebUI.waitForPageLoad(10)


WebDriver driver = DriverFactory.getWebDriver()
JavascriptExecutor js = (JavascriptExecutor) driver
Actions actions = new Actions(driver)

// Tüm target hücrelerini bul 
List<WebElement> assetListTargetCellsOrg = driver.findElements(By.xpath("//td[contains(@class, 'ant-table-cell-fix-left')]"))

// 'catchprobe.org' satır index'ini bul
int targetIndexCatchprobeOrgRow = -1
for (int i = 0; i < assetListTargetCellsOrg.size(); i++) {
    if (assetListTargetCellsOrg[i].getText().trim() == 'catchprobe.org') {
        targetIndexCatchprobeOrgRow = i + 1
        break
    }
}

if (targetIndexCatchprobeOrgRow == -1) {
    // Satır bulunamadıysa logla ve devam et
    KeywordUtil.logInfo("❕ 'catchprobe.org' satırı tabloda bulunamadı, işlem atlandı.")
} else {
    // Satır bulunduysa işlemleri yap

    // Asset Flow ikonunu bul ve mouse over yap
    String xpathAssetFlowIconCatchprobeOrg = "(//div[contains(@class, 'bg-emerald-500')]/*[name()='svg'])[" + targetIndexCatchprobeOrgRow + "]"
    WebElement elementAssetFlowIconCatchprobeOrg = driver.findElement(By.xpath(xpathAssetFlowIconCatchprobeOrg))
    js.executeScript("arguments[0].scrollIntoView(true);", elementAssetFlowIconCatchprobeOrg)
    actions.moveToElement(elementAssetFlowIconCatchprobeOrg).perform()
    WebUI.delay(2)

    // Tooltip elementini bekle ve text’ini al
    TestObject tooltipObjectCatchprobeOrg = new TestObject()
    tooltipObjectCatchprobeOrg.addProperty("xpath", ConditionType.EQUALS, "//div[p[text()]]")
    WebUI.waitForElementVisible(tooltipObjectCatchprobeOrg, 5)

    WebElement elementTooltipCatchprobeOrg = WebUI.findWebElement(tooltipObjectCatchprobeOrg, 5)
    String textTooltipCatchprobeOrg = elementTooltipCatchprobeOrg.getText()
    println "catchprobe.org Asset Flow Tooltip Text: " + textTooltipCatchprobeOrg

  if (textTooltipCatchprobeOrg.contains("Asset Flow")) {
    println "✅ 'catchprobe.org' için Asset Flow bulundu, ikon tıklanıyor..."
    elementAssetFlowIconCatchprobeOrg.click()

    // Download Image butonu kontrolü
    TestObject downloadImageButton = new TestObject()
    downloadImageButton.addProperty("xpath", ConditionType.EQUALS, "//span[text()='DOWNLOAD IMAGE']")
    boolean isClickable = WebUI.verifyElementClickable(downloadImageButton)
    println("Download Image butonu clickable mı?: " + isClickable)

    // Image Zoom kontrolü
    TestObject imageZoom = new TestObject()
    imageZoom.addProperty("id", ConditionType.EQUALS, "imageZoom")
    WebUI.waitForElementVisible(imageZoom, 15)
    WebUI.verifyElementVisible(imageZoom)
    println "✅ 'Asset Flow' sonrası görsel başarıyla yüklendi."

    // Close butonuna tıkla
    WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Mitre Close'))
    WebUI.delay(3)

  } else if (textTooltipCatchprobeOrg.equalsIgnoreCase("No Scan")) {
    KeywordUtil.logInfo("❌ 'catchprobe.org' için tooltip 'No Scan' geldi, test devam!")
  } else {
    println "ℹ️ 'catchprobe.org' için tooltip: '${textTooltipCatchprobeOrg}', işlem yapılmadı."
  }
}


// Delete butonu tanımı
TestObject deleteButton = new TestObject().addProperty("xpath",
    com.kms.katalon.core.testobject.ConditionType.EQUALS,
    "//div[contains(@class, 'bg-destructive')]")

// Buton var mı kontrol edip varsa tıklayarak döngü
while (WebUI.verifyElementPresent(deleteButton, 3, FailureHandling.OPTIONAL)) {
    WebUI.comment("Delete butonu bulundu, tıklanıyor...")

    try {
        // WebElement'ini bul
        WebElement deleteButtonElem = WebUI.findWebElement(deleteButton, 3)

        // Ekrana kaydır
        js.executeScript("arguments[0].scrollIntoView(true);", deleteButtonElem)
        WebUI.delay(0.5)

        // Tıkla
        deleteButtonElem.click()

        CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()    
        WebUI.delay(2)

        // Onay butonuna tıkla
        WebUI.click(findTestObject('Object Repository/Riskroute/Asset Lİst/Page_/button_DELETE'))
        CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()    

        // Toast kontrolü
        WebUI.waitForElementVisible(findTestObject('Object Repository/Riskroute/Asset Lİst/Page_/Delete Toast'), 5)

    } catch (Exception e) {
        // Eğer buton yoksa veya işlem sırasında hata olursa logla ve döngüden çık
        WebUI.comment("Delete butonu bulunamadı veya tıklama sırasında hata oluştu: " + e.getMessage())
        break
    }
}

// İşlem bitti
WebUI.comment("Tüm delete butonları silindi ya da hiç yoktu. Asset List temiz.")
println("Tüm delete ikonları silindi ya da yoktu.")

WebUI.click(findTestObject('Object Repository/Riskroute/Asset Lİst/Page_/Create Asset'))

WebUI.waitForElementVisible(findTestObject('Object Repository/Riskroute/Asset Lİst/Page_/Single Target'), 30)

WebUI.setText(findTestObject('Object Repository/Riskroute/Asset Lİst/Page_/Single Target'), '8.8.8.8/30')

// Description alanına 'katalon' yaz
WebUI.setText(findTestObject('Object Repository/Riskroute/Asset Lİst/Page_/YourDescriptionInputObject'), 'katalon')

// Create butonuna tıkla
WebUI.click(findTestObject('Object Repository/Riskroute/Asset Lİst/Page_/Create buton'))

WebUI.waitForElementVisible(findTestObject('Object Repository/Riskroute/Asset Lİst/Page_/Asset successfully'), 15)

WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Mitre Close'))




// Target listesi
List<String> SubnettargetList = [
	'8.8.8.9',
	'8.8.8.10'
]


List<String> SubnettypeList = [
	'IP',
	'IP'
]
// Description listesi
List<String> SubnetdescList = []
for (int i = 0; i < SubnettargetList.size(); i++) {
	SubnetdescList.add('katalon')
}

// Tablodan verileri oku
List<String> actualSubnetTargetList = []
List<String> actualSubnetTypeList = []
List<String> actualSubnetDescList = []

for (int i = 1; i <= SubnettargetList.size(); i++) {
	// Target
	String targetXpath = "(//td[contains(@class, 'ant-table-cell-fix-left')])[" + i + "]"
	TestObject targetObj = new TestObject().addProperty("xpath", ConditionType.EQUALS, targetXpath)
	WebElement targetElem = WebUI.findWebElement(targetObj, 10)
	js.executeScript("arguments[0].scrollIntoView(true);", targetElem)
	WebUI.delay(0.2)
	actualSubnetTargetList.add(targetElem.getText())

	// Description (2. sütun)
	String descXpath = "(//tr[@class='ant-table-row ant-table-row-level-0'])[" + i + "]/td[2]"
	TestObject descObj = new TestObject().addProperty("xpath", ConditionType.EQUALS, descXpath)
	WebElement descElem = WebUI.findWebElement(descObj, 10)
	js.executeScript("arguments[0].scrollIntoView(true);", descElem)
	WebUI.delay(0.2)
	actualSubnetDescList.add(descElem.getText())

	// Type (3. sütun)
	String typeXpath = "(//tr[@class='ant-table-row ant-table-row-level-0'])[" + i + "]/td[3]"
	TestObject typeObj = new TestObject().addProperty("xpath", ConditionType.EQUALS, typeXpath)
	WebElement typeElem = WebUI.findWebElement(typeObj, 10)
	js.executeScript("arguments[0].scrollIntoView(true);", typeElem)
	WebUI.delay(0.2)
	actualSubnetTypeList.add(typeElem.getText())
}

// ✅ Bağımsız kontrol — listedeki tüm değerler tabloda var mı kontrol et
assert actualSubnetTargetList.containsAll(SubnettargetList)
assert actualSubnetTypeList.containsAll(SubnettypeList)
assert actualSubnetDescList.containsAll(SubnetdescList)

// Delete butonu tanımı
TestObject deleteButtonsubnet = new TestObject().addProperty("xpath",
	com.kms.katalon.core.testobject.ConditionType.EQUALS,
	"//div[contains(@class, 'bg-destructive')]")



// Buton var mı kontrol edip varsa tıklayarak döngü
while (WebUI.verifyElementPresent(deleteButtonsubnet, 3, FailureHandling.OPTIONAL)) {
	WebUI.comment("Delete butonu bulundu, tıklanıyor...")

	// WebElement'ini bul
	WebElement subnetdeleteButtonElem = WebUI.findWebElement(deleteButtonsubnet, 5)

	// Ekrana kaydır
	js.executeScript("arguments[0].scrollIntoView(true);", subnetdeleteButtonElem)
	WebUI.delay(0.5)

	// Tıkla
	subnetdeleteButtonElem.click()

	CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()
	WebUI.delay(2)

	WebUI.click(findTestObject('Object Repository/Riskroute/Asset Lİst/Page_/button_DELETE'))
	CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()
	WebUI.waitForElementVisible(findTestObject('Object Repository/Riskroute/Asset Lİst/Page_/Delete Toast'), 5)
}

// İşlem bitti
WebUI.comment("Tüm delete butonları silindi. Asset List boş.")
println("Tüm delete ikonları silindi.")

//WebUI.refresh()
// CREATE CRON butonu için TestObject oluştur
TestObject EDİTBUTTON1 = new TestObject().addProperty("xpath", ConditionType.EQUALS, "//div[@class='ant-empty-description' and normalize-space()='No data']")

// 10 saniyeye kadar görünür mü kontrol et
if (WebUI.waitForElementVisible(EDİTBUTTON1, 10, FailureHandling.OPTIONAL)) {
	WebUI.comment("EDİTBUTTON butonu bulundu.")
} else {
	WebUI.comment("EDİTBUTTON butonu bulunamadı, sayfa yenileniyor...")
	WebUI.refresh()
	WebUI.waitForPageLoad(10)
	
	if (WebUI.waitForElementVisible(EDİTBUTTON1, 10, FailureHandling.OPTIONAL)) {
		WebUI.comment("EDİTBUTTON butonu refresh sonrası bulundu.")
	} else {
		KeywordUtil.markFailedAndStop("EDİTBUTTON butonu bulunamadı, test sonlandırılıyor.")
	}
}


WebUI.delay(3)




// Target listesi
List<String> targetList = [
	'catchprobe.org',	
	'teknosa',
	'176.9.66.101'
]

List<String> typeList = [
	'Domain',	
	'Keyword',	
	'Keyword',
	'IP'
]

List<String> AssetList = [
	'catchprobe.org',	
	'teknosa',
	'176.9.66.101'
	
]

WebUI.click(findTestObject('Object Repository/Riskroute/Asset Lİst/Page_/Create Asset'))
WebUI.delay(2)
WebUI.click(findTestObject('Object Repository/Riskroute/Asset Lİst/Page_/Add Multiple Assets'))


// Target input alanına targetList değerlerini virgülle ekle
String targets = targetList.join(', ')
WebUI.setText(findTestObject('Object Repository/Riskroute/Asset Lİst/Page_/TargetInputObject'), targets)

// Description alanına 'katalon' yaz
WebUI.setText(findTestObject('Object Repository/Riskroute/Asset Lİst/Page_/YourDescriptionInputObject'), 'katalon')

// Create butonuna tıkla
WebUI.click(findTestObject('Object Repository/Riskroute/Asset Lİst/Page_/Create buton'))

WebUI.waitForElementVisible(findTestObject('Object Repository/Riskroute/Asset Lİst/Page_/Asset successfully'), 15)

WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/Mitre Close'))


// Description listesi
List<String> descList = []
for (int i = 0; i < targetList.size(); i++) {
	descList.add('katalon')
}

// Tablodan verileri oku
List<String> actualTargetList = []
List<String> actualTypeList = []
List<String> actualDescList = []

for (int i = 1; i <= AssetList.size(); i++) {
	// Target
	String targetXpath = "(//td[contains(@class, 'ant-table-cell-fix-left')])[" + i + "]"
	TestObject targetObj = new TestObject().addProperty("xpath", ConditionType.EQUALS, targetXpath)
	WebElement targetElem = WebUI.findWebElement(targetObj, 10)
	js.executeScript("arguments[0].scrollIntoView(true);", targetElem)
	WebUI.delay(0.2)
	actualTargetList.add(targetElem.getText())

	// Description (2. sütun)
	String descXpath = "(//tr[@class='ant-table-row ant-table-row-level-0'])[" + i + "]/td[2]"
	TestObject descObj = new TestObject().addProperty("xpath", ConditionType.EQUALS, descXpath)
	WebElement descElem = WebUI.findWebElement(descObj, 10)
	js.executeScript("arguments[0].scrollIntoView(true);", descElem)
	WebUI.delay(0.2)
	actualDescList.add(descElem.getText())

	// Type (3. sütun)
	String typeXpath = "(//tr[@class='ant-table-row ant-table-row-level-0'])[" + i + "]/td[3]"
	TestObject typeObj = new TestObject().addProperty("xpath", ConditionType.EQUALS, typeXpath)
	WebElement typeElem = WebUI.findWebElement(typeObj, 10)
	js.executeScript("arguments[0].scrollIntoView(true);", typeElem)
	WebUI.delay(0.2)
	actualTypeList.add(typeElem.getText())
}

// ✅ Bağımsız kontrol — listedeki tüm değerler tabloda var mı kontrol et
assert actualTargetList.containsAll(AssetList)
assert actualTypeList.containsAll(typeList)
assert actualDescList.containsAll(descList)

// ✅ Log ile kontrol
println "Tablodaki Target'lar: " + actualTargetList
println "Beklenen Target'lar: " + AssetList

println "Tablodaki Type'lar: " + actualTypeList
println "Beklenen Type'lar: " + typeList

println "Tablodaki Description'lar: " + actualDescList
println "Beklenen Description'lar: " + descList
//


// Tüm target hücrelerini bul
List<WebElement> targetCells = driver.findElements(By.xpath("//td[contains(@class, 'ant-table-cell-fix-left')]"))

// catchprobe.org satır index'ini bul
int targetIndex = -1
for (int i = 0; i < targetCells.size(); i++) {
	if (targetCells[i].getText().trim() == 'catchprobe.org') {
		targetIndex = i + 1
		break
	}
}

assert targetIndex != -1 : "catchprobe.org tablo satırında bulunamadı!"

// Asset Flow ikonuna mouse over yap ve No Scan kontrol et
String assetFlowIconXpath = "(.//*[normalize-space(text()) and normalize-space(.)='info'])[" + targetIndex + "]/following::*[name()='svg'][5]"

WebElement assetFlowIcon = driver.findElement(By.xpath(assetFlowIconXpath))
js.executeScript("arguments[0].scrollIntoView(true);", assetFlowIcon)
actions.moveToElement(assetFlowIcon).perform()
WebUI.delay(0.5)

// ToolTip elementini bekle
TestObject tooltipObject = new TestObject()
tooltipObject.addProperty("xpath", ConditionType.EQUALS, "//div[p[text()='No Scan']]")
WebUI.waitForElementVisible(tooltipObject, 5)

// ToolTip text'ini al ve logla
WebElement tooltipElem = WebUI.findWebElement(tooltipObject, 5)
String tooltipText = tooltipElem.getText()
println "Tooltip Text: " + tooltipText

assert tooltipText.contains("No Scan") : "İlk durumda tooltip 'No Scan' değil!"

// Quick Search ikonuna tıkla
String quickSearchIconXpath = "(.//*[normalize-space(text()) and normalize-space(.)='info'])[" + targetIndex + "]/following::*[name()='svg'][4]"
WebElement quickSearchIcon = driver.findElement(By.xpath(quickSearchIconXpath))
js.executeScript("arguments[0].scrollIntoView(true);", quickSearchIcon)
quickSearchIcon.click()

WebUI.delay(1)
WebUI.waitForElementVisible(findTestObject('Object Repository/Riskroute/Asset Lİst/Page_/Toast Message'), 15)



 // sayfa yüklenme süresi

// Asset List sayfasına geri dön
WebUI.navigateToUrl('https://platform.catchprobe.org/riskroute/asset-list')

WebUI.delay(3)
WebUI.waitForPageLoad(30)

// Sayfa dönünce target hücrelerini yeniden bul
List<WebElement> refreshedTargetCells = driver.findElements(By.xpath("//td[contains(@class, 'ant-table-cell-fix-left')]"))

// catchprobe.org satır index'ini bul
int targetIndextwo = -1
for (int i = 0; i < refreshedTargetCells.size(); i++) {
	if (refreshedTargetCells[i].getText().trim() == 'catchprobe.org') {
		targetIndextwo = i + 1
		break
	}
}
// Scan History ikonuna mouse over yap ve Asset Flow yazısı kontrol et
String refreshedAssetFlowIconXpath = "(.//*[normalize-space(text()) and normalize-space(.)='info'])[" + targetIndextwo + "]/following::*[name()='svg'][6]"
WebElement refreshedAssetFlowIcon = driver.findElement(By.xpath(refreshedAssetFlowIconXpath))
js.executeScript("arguments[0].scrollIntoView(true);", refreshedAssetFlowIcon)
actions.moveToElement(refreshedAssetFlowIcon).perform()
WebUI.delay(0.5)

// Tooltip elementini bekle
TestObject assetFlowTooltipObject = new TestObject()
assetFlowTooltipObject.addProperty("xpath", ConditionType.EQUALS, "//div[p[text()='Scan History']]")
WebUI.waitForElementVisible(assetFlowTooltipObject, 5)

// Tooltip text'ini al ve logla
WebElement assetFlowTooltipElem = WebUI.findWebElement(assetFlowTooltipObject, 5)
String assetFlowTooltipText = assetFlowTooltipElem.getText()
println "Asset Flow Tooltip Text: " + assetFlowTooltipText

// Tooltip kontrolü
assert assetFlowTooltipText.contains("Scan History") : "Son durumda tooltip 'Scan History' değil!"

// Edit butonunu bul
String editButtonXpath = "(.//*[normalize-space(text()) and normalize-space(.)='info'])[" + targetIndextwo + "]/following::*[name()='svg'][2]"
 WebElement editButton = driver.findElement(By.xpath(editButtonXpath))
 js.executeScript("arguments[0].scrollIntoView(true);", editButton)
 editButton.click()
 WebUI.delay(0.5)
 

WebUI.delay(2)

WebUI.verifyElementNotClickable(findTestObject('Object Repository/Riskroute/Asset Lİst/Page_/SAVE bUTONU'))

WebUI.delay(1)
TestObject descInput = findTestObject('Object Repository/Riskroute/Asset Lİst/Page_/input_Description_description')
WebUI.waitForElementVisible(descInput, 5)
WebUI.waitForElementClickable(descInput, 5)
WebUI.click(descInput)
WebUI.clearText(descInput)
WebUI.setText(descInput, 'Katalon Text')
WebUI.verifyElementClickable(findTestObject('Object Repository/Riskroute/Asset Lİst/Page_/button_SAVE'))

WebUI.click(findTestObject('Object Repository/Riskroute/Asset Lİst/Page_/button_SAVE'))

WebUI.verifyElementText(findTestObject('Object Repository/Riskroute/Asset Lİst/Page_/div_Asset edited successfully'),
	'Asset edited successfully')

// Tüm target hücrelerini bul
List<WebElement> targetCellstext = driver.findElements(By.xpath("//td[contains(@class, 'ant-table-cell-ellipsis !bg-card')]"))

// katalon text satır index'ini bul
int targetIndexthree = -1
for (int i = 0; i < targetCellstext.size(); i++) {
    println "Satır " + i + " : " + targetCellstext[i].getText().trim()
    if (targetCellstext[i].getText().trim().equalsIgnoreCase('katalon text')) {
        targetIndexthree = i
        break
    }
}

// Eğer bulunduysa scroll ve üzerine gel
if (targetIndexthree != -1) {
    WebElement katalonTextCell = targetCellstext[targetIndexthree]
    js.executeScript("arguments[0].scrollIntoView(true);", katalonTextCell)
    actions.moveToElement(katalonTextCell).perform()
    WebUI.delay(1)
    println "Bulunan hücre texti: " + katalonTextCell.getText()
} else {
    println "katalon text satırı bulunamadı!"
}

WebUI.navigateToUrl('https://platform.catchprobe.org/riskroute/scan-cron')

WebUI.delay(5)

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



/*/
WebUI.delay(3)
safeScrollTo(findTestObject('Object Repository/Riskroute/Asset Lİst/Page_/Organization Butonu'))
WebUI.click(findTestObject('Object Repository/Riskroute/Asset Lİst/Page_/Organization Butonu'))
WebUI.delay(2)
safeScrollTo(findTestObject('Object Repository/Riskroute/Katalon Organization'))
WebUI.click(findTestObject('Object Repository/Riskroute/Katalon Organization'))

WebUI.delay(3)
WebUI.waitForPageLoad(10)
/*/



/*/ Tüm target hücrelerini bul 
List<WebElement> targetCellsIp176 = driver.findElements(By.xpath("//td[contains(@class, 'ant-table-cell-fix-left')]"))

// 176.9.66.101 satır index'ini bul
int targetIndexIp176 = -1
for (int i = 0; i < targetCellsIp176.size(); i++) {
	if (targetCellsIp176[i].getText().trim() == '176.9.66.101') {
		targetIndexIp176 = i + 1
		break
	}
}

assert targetIndexIp176 != -1 : "176.9.66.101 tablo satırında bulunamadı!"

// Asset Flow ikonuna mouse over yap ve No Scan kontrol et
String assetFlowIp176Xpath = "(.//*[normalize-space(text()) and normalize-space(.)='info'])[${targetIndexIp176}]/following::*[name()='svg'][5]"
WebElement assetFlowIconIp176 = driver.findElement(By.xpath(assetFlowIp176Xpath))
js.executeScript("arguments[0].scrollIntoView(true);", assetFlowIconIp176)
actions.moveToElement(assetFlowIconIp176).perform()
WebUI.delay(0.5)

// ToolTip elementini bekle
TestObject tooltipIp176 = new TestObject()
tooltipIp176.addProperty("xpath", ConditionType.EQUALS, "//div[p[text()='No Scan']]")
WebUI.waitForElementVisible(tooltipIp176, 5)

// ToolTip text'ini al ve logla
WebElement tooltipElemIp176 = WebUI.findWebElement(tooltipIp176, 5)
String tooltipTextIp176 = tooltipElemIp176.getText()
println "Tooltip Text: " + tooltipTextIp176

assert tooltipTextIp176.contains("No Scan") : "İlk durumda tooltip 'No Scan' değil!"

// Quick Search ikonuna tıkla
String quickSearchIp176Xpath = "(.//*[normalize-space(text()) and normalize-space(.)='info'])[${targetIndexIp176}]/following::*[name()='svg'][4]"
WebElement quickSearchIconIp176 = driver.findElement(By.xpath(quickSearchIp176Xpath))
js.executeScript("arguments[0].scrollIntoView(true);", quickSearchIconIp176)
quickSearchIconIp176.click()


WebUI.delay(1)
WebUI.waitForElementVisible(findTestObject('Object Repository/Riskroute/Asset Lİst/Page_/Toast Message'), 15)


// Asset List sayfasına geri dön

WebElement assetListElement176 = WebUI.findWebElement(assetListButton, 10)
js.executeScript("arguments[0].scrollIntoView(false);", assetListElement176)
WebUI.delay(0.5)
assetListElement176.click()

WebUI.delay(3)
WebUI.waitForPageLoad(30)
/*/
// Sayfa dönünce target hücrelerini yeniden bul
