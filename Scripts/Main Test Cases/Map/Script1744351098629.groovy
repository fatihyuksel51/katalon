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
import com.kms.katalon.core.webui.common.WebUiCommonHelper as WebUiCommonHelper
import org.openqa.selenium.WebElement as WebElement
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import org.openqa.selenium.WebElement
import org.openqa.selenium.interactions.Actions

WebUI.openBrowser('')

WebUI.navigateToUrl('https://platform.catchprobe.org/')

WebUI.maximizeWindow()

WebUI.click(findTestObject('null'))

WebUI.setText(findTestObject('null'), 'fatih.yuksel@catchprobe.com')

WebUI.setEncryptedText(findTestObject('null'), 'RigbBhfdqOBDK95asqKeHw==')

WebUI.click(findTestObject('null'))

WebUI.click(findTestObject('null'))

WebUI.executeJavaScript('document.body.style.zoom=\'80%\'', null)

WebUI.click(findTestObject('null'))

WebUI.click(findTestObject('null'))

WebUI.click(findTestObject('null'))
WebUI.click(findTestObject('null'))



WebUI.delay(5)
Actions actions = new Actions(WebUI.getWebDriver())
actions.moveToElement(findTestObject('null')).perform()

// 4. Bekle (2 saniye)
WebUI.delay(2)

// 5. Fare tekerleği ile aşağıya kaydır (örnek olarak 3 kez)
for (int i = 0; i < 3; i++) {
	WebUI.executeJavaScript("window.scrollBy(0, 200);", null)
	WebUI.delay(1)
}

WebUI.executeJavaScript('window.scrollBy(0, 300)', null)

WebUI.closeBrowser()

