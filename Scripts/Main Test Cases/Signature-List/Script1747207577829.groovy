import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
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

// Tarayıcıyı aç ve siteye git

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

WebUI.setText(findTestObject('Object Repository/hafdii/Page_/input_OTP Digit_vi_1_2_3_4_5'), randomOtp)

WebUI.click(findTestObject('Object Repository/hafdii/Page_/button_Verify'))

WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/svg_G_lucide lucide-webhook h-6 w-6'))

WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/div_Signature List'))

WebUI.verifyElementClickable(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/button_Download CSV'))





WebUI.verifyElementText(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/span_14052025 1109'), '14/05/2025 11:09')

WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/span_Choose a Date'))

WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/button_1'))



WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/nav_Previous12More pages177095Next'))

WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/span_Choose a Date_1'))

WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/button_13'))

WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/nav_Previous12More pages177095Next'))

WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/button_IPv4'))

WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/nav_Previous12More pages177095Next'))

WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/button_APPLY AND SEARCH'))

WebUI.verifyElementText(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/a_113.27.28.61'), '113.27.28.61')



WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/button_ADD'))

WebUI.verifyElementText(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/div_Signature added to favorite successfully'), 
    'Signature added to favorite successfully')

WebUI.setText(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/input_Keyword_search_filters'), '113.27.28.61')

WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/button_APPLY AND SEARCH'))

WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/a_113.27.28.61'))

WebUI.verifyElementPresent(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/path_stamparm-ipsum_SvgjsPath1011_1'), 
    0)

WebUI.verifyElementPresent(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/circle_Risk Score_SvgjsCircle1049'), 
    0)

WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/path'))

WebUI.verifyElementClickable(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/a_113.27.28.61_1'))

WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/div_IOC Detail IOC Information'))

WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/svg_IOC Information_h-4 w-4 shrink-0 text-m_f534db'))

WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/button_Show Detail'))

WebUI.verifyElementPresent(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/circle_Risk Score_SvgjsCircle1354'), 
    0)

WebUI.verifyElementText(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/input_IP Address_flex h-12 w-full rounded-m_5a09b1'), 
    '')

WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/path'))

WebUI.verifyElementText(findTestObject('Object Repository/dashboard/Page_/button_THREATWAY'), 'THREATWAY')

WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/svg_Signature  IoC Details_h-4 w-4 shrink-0_362a8f'))

WebUI.verifyElementText(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/circle'), '')

WebUI.verifyElementPresent(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/circle'), 0)

WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/button_STIX Package Signature  IoC Details'))

WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/svg_Signature  IoC Details_h-4 w-4 shrink-0_362a8f'))

WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/a_Signature List'))

WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/button_FILTER OPTIONS'))

WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/button_All_1_2'))

WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/button_All_1_2'))

WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/button_All_1_2_3'))

WebUI.selectOptionByValue(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/select_AllFavorites1984shabuseabuse-exporta_8ee627'), 
    'favorites', true)

WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/button_APPLY AND SEARCH'))

WebUI.setText(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/input_Keyword_search_filters_1'), '113.27.28.61')

WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/button_APPLY AND SEARCH'))

WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/div_TLPCLEAR_inline-flex cursor-pointer rou_181ff8'))

WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/button_REMOVE'))

WebUI.verifyElementText(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/div_Signature removed to favorite successfully'), 
    'Signature removed to favorite successfully')

WebUI.verifyElementText(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/div_No data'), 'No data')

WebUI.verifyElementText(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/span_favorites'), 'favorites')

WebUI.verifyElementText(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/span_113.27.28.61'), '113.27.28.61')

WebUI.closeBrowser()

