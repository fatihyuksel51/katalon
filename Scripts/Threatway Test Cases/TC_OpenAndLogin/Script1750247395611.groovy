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
try {
    def agentIp = new URL("https://ifconfig.me/ip").openStream().getText().trim()
    println "👉 TestOps Agent IP Adresi: " + agentIp
} catch(Exception e) {
    println "❌ IP alınamadı: " + e.getMessage()
}



WebUI.navigateToUrl('https://platform.catchprobe.org/')

WebUI.maximizeWindow()

// Login işlemleri
WebUI.waitForElementVisible(findTestObject('Object Repository/otp/Page_/a_PLATFORM LOGIN'), 30)

WebUI.click(findTestObject('Object Repository/otp/Page_/a_PLATFORM LOGIN'))

WebUI.waitForElementVisible(findTestObject('Object Repository/otp/Page_/input_Email Address_email'), 30)

WebUI.setText(findTestObject('Object Repository/otp/Page_/input_Email Address_email'), 'fatih.yuksel@catchprobe.com')

WebUI.setEncryptedText(findTestObject('Object Repository/otp/Page_/input_Password_password'), 'RigbBhfdqOBDK95asqKeHw==')

WebUI.click(findTestObject('Object Repository/otp/Page_/button_Sign in'))

WebUI.delay(10)

// OTP işlemi
def randomOtp = (100000 + new Random().nextInt(900000)).toString()

WebUI.setText(findTestObject('Object Repository/otp/Page_/input_OTP Digit_vi_1_2_3_4_5'), randomOtp)

WebUI.click(findTestObject('Object Repository/otp/Page_/button_Verify'))

CustomKeywords.'com.catchprobe.utils.TableUtils.checkForUnexpectedToasts'()
WebUI.delay(3)

WebUI.click(findTestObject('Object Repository/Asset Lİst/Page_/Organization Butonu'))

WebUI.click(findTestObject('Object Repository/Dark/Page_/Developer'))

WebUI.delay(3)

WebUI.waitForPageLoad(30)

