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

WebUI.verifyElementPresent(findTestObject('Object Repository/Threat Actor/Threataa/Page_/div_ascendo'), 0)

WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/div_All'))

WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/span_2626'))

WebUI.setText(findTestObject('Object Repository/Threat Actor/Threataa/Page_/input_Cybercriminals_flex rounded-md border_26dcbc'), 
    'a')

WebUI.setText(findTestObject('Object Repository/Threat Actor/Threataa/Page_/input_Cybercriminals_flex rounded-md border_26dcbc_1'), 
    'ap')

WebUI.setText(findTestObject('Object Repository/Threat Actor/Threataa/Page_/input_Cybercriminals_flex rounded-md border_26dcbc_1_2'), 
    'apt')

WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/button_SEARCH'))

WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/div_Nation State 15'))

WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/a_2'))

WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/div_All 17'))

WebUI.verifyElementText(findTestObject('Object Repository/Threat Actor/Threataa/Page_/button_DESC'), 'DESC')

WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/button_SEARCH'))

WebUI.verifyElementText(findTestObject('Object Repository/Threat Actor/Threataa/Page_/button_DESC'), 'DESC')

WebUI.verifyElementText(findTestObject('Object Repository/Threat Actor/Threataa/Page_/div_APT73'), 'APT73')

WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/button_DESC'))

WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/button_SEARCH'))

WebUI.verifyElementText(findTestObject('Object Repository/Threat Actor/Threataa/Page_/button_ASC'), 'ASC')

WebUI.verifyElementText(findTestObject('Object Repository/Threat Actor/Threataa/Page_/div_APT28'), 'APT28')

WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/span_Select Company Sector'))

WebUI.setText(findTestObject('Object Repository/Threat Actor/Threataa/Page_/input_Select Company Sector_border border-i_8cdb20'), 
    '')

WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/div_MITRE ATTCK 0'))

WebUI.verifyElementText(findTestObject('Object Repository/Threat Actor/Threataa/Page_/span_14'), '14')

WebUI.verifyElementText(findTestObject('Object Repository/Threat Actor/Threataa/Page_/span_14_1'), '14')

WebUI.verifyElementText(findTestObject('Object Repository/Threat Actor/Threataa/Page_/a_Input Capture1'), 'Input Capture1')

WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/a_Input Capture1'))

WebUI.verifyElementText(findTestObject('Object Repository/Threat Actor/Threataa/Page_/div_Input Capture'), 'Input Capture')

WebUI.verifyElementText(findTestObject('Object Repository/Threat Actor/Threataa/Page_/div_Keylogging'), 'Keylogging')

WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/path'))

WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/a_Keylogging'))

WebUI.verifyElementText(findTestObject('Object Repository/Threat Actor/Threataa/Page_/span_Keylogging'), 'Keylogging')

WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/svg_Detection_h-4 w-4'))

WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/div_Indicators 0'))

WebUI.verifyElementText(findTestObject('Object Repository/Threat Actor/Threataa/Page_/span_10'), '10')

WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/div_Tools 0'))

WebUI.verifyElementClickable(findTestObject('Object Repository/Threat Actor/Threataa/Page_/button_CSV'))

WebUI.setText(findTestObject('Object Repository/Threat Actor/Threataa/Page_/input_Timeline_flex rounded-md border borde_927c96'), 
    'Downdelph')

WebUI.sendKeys(findTestObject('Object Repository/Threat Actor/Threataa/Page_/input_Timeline_flex rounded-md border borde_927c96'), 
    Keys.chord(Keys.ENTER))

WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/div_Contacts 0'))

WebUI.verifyElementClickable(findTestObject('Object Repository/Threat Actor/Threataa/Page_/button_CSV'))

WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/button_CSV'))

WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/div_Timeline'))

WebUI.click(findTestObject('Object Repository/Threat Actor/Threataa/Page_/button_View All'))

WebUI.closeBrowser()

