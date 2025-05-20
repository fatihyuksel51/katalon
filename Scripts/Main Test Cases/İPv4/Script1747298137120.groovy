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

WebUI.openBrowser('')
WebUI.navigateToUrl('https://platform.catchprobe.org/')
WebUI.maximizeWindow()

// Login işlemleri
WebUI.waitForElementVisible(findTestObject('Object Repository/hafdii/Page_/a_PLATFORM LOGIN'), 30)
WebUI.click(findTestObject('Object Repository/hafdii/Page_/a_PLATFORM LOGIN'))

WebUI.waitForElementVisible(findTestObject('Object Repository/hafdii/Page_/input_Email Address_email'), 30)
WebUI.setText(findTestObject('Object Repository/hafdii/Page_/input_Email Address_email'), 'fatih.yuksel@catchprobe.com')
WebUI.setEncryptedText(findTestObject('Object Repository/hafdii/Page_/input_Password_password'), 'RigbBhfdqOBDK95asqKeHw==')
WebUI.click(findTestObject('Object Repository/hafdii/Page_/button_Sign in'))


WebUI.delay(3)

// OTP işlemi
def randomOtp = (100000 + new Random().nextInt(900000)).toString()
WebUI.waitForElementVisible(findTestObject('hafdii/Page_/input_OTP Digit_vi_1_2_3_4_5'), 20)
WebUI.setText(findTestObject('hafdii/Page_/input_OTP Digit_vi_1_2_3_4_5'), randomOtp)
WebUI.click(findTestObject('hafdii/Page_/button_Verify'))
WebUI.waitForPageLoad(20)


// Navigasyon işlemleri
WebUI.waitForElementClickable(findTestObject('Object Repository/Smartdeceptive/Page_/ipv4/Page_/svg_G_lucide lucide-webhook h-6 w-6'), 20)
WebUI.click(findTestObject('Object Repository/Smartdeceptive/Page_/ipv4/Page_/svg_G_lucide lucide-webhook h-6 w-6'))

WebUI.waitForElementClickable(findTestObject('Object Repository/Smartdeceptive/Page_/ipv4/Page_/div_Signature List'), 20)
WebUI.click(findTestObject('Object Repository/Smartdeceptive/Page_/ipv4/Page_/div_Signature List'))

WebUI.waitForElementClickable(findTestObject('Object Repository/Smartdeceptive/Page_/ipv4/Page_/button_FILTER OPTIONS'), 20)
WebUI.click(findTestObject('Object Repository/Smartdeceptive/Page_/ipv4/Page_/button_FILTER OPTIONS'))

// Select elementinden 'IPv4' seç
WebUI.waitForElementClickable(findTestObject('Object Repository/Smartdeceptive/Page_/ipv4/Page_/İPv4-buton'), 20)
WebUI.scrollToElement(findTestObject('Object Repository/Smartdeceptive/Page_/ipv4/Page_/İPv4-buton'), 5)
WebUI.click(findTestObject('Object Repository/Smartdeceptive/Page_/ipv4/Page_/İPv4-buton'))

WebUI.delay(3)
WebUI.click(findTestObject('Object Repository/Smartdeceptive/Page_/select_dropdown'))
WebUI.delay(3)
// Tarayıcıyı kapat
WebUI.closeBrowser()
