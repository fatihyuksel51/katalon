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

WebUI.click(findTestObject('Object Repository/Dark/Page_/a_PLATFORM LOGIN'))

WebUI.maximizeWindow()

WebUI.executeJavaScript("document.body.style.zoom='80%'", null)

WebUI.setText(findTestObject('Object Repository/Dark/Page_/input_Email Address_email'), 'fatih.yuksel@catchprobe.com')

WebUI.setEncryptedText(findTestObject('Object Repository/Dark/Page_/input_Password_password'), 'RigbBhfdqOBDK95asqKeHw==')

WebUI.sendKeys(findTestObject('Object Repository/Dark/Page_/input_Password_password'), Keys.chord(Keys.ENTER))

WebUI.delay(13)



WebUI.click(findTestObject('Object Repository/Dark/Page_/div_Threat Intelligence'))


WebUI.verifyElementText(findTestObject('Object Repository/Dark/Page_/div_Threat Intelligence'), 'Threat Intelligence')


WebUI.click(findTestObject('Object Repository/Dark/Page_/svg_G_lucide lucide-dna h-6 w-6'))


WebUI.verifyElementText(findTestObject('Object Repository/Dark/Page_/span_Dashboard'), 'Dashboard')

WebUI.click(findTestObject('Object Repository/Dark/Page_/div_Template Settings'))

WebUI.click(findTestObject('Object Repository/Dark/Page_/span_Deception Operations'))

WebUI.verifyElementText(findTestObject('Object Repository/Dark/Page_/div_Decoy List'), 'Decoy List')


WebUI.click(findTestObject('Object Repository/Dark/Page_/div_Decoy List'))

WebUI.verifyElementClickable(findTestObject('Object Repository/Dark/Page_/a_65.21.148.13180'))

WebUI.click(findTestObject('Object Repository/Dark/Page_/td_65.21.148.13180'))

WebUI.switchToWindowTitle('Alpet Satış Portalı | Giriş')

WebUI.scrollFromViewportOffset(0, 0, 0, 0)

WebUI.verifyElementText(findTestObject('Object Repository/Dark/Page_Alpet Sat Portal  Giri/img'), '')

WebUI.closeWindowIndex(1)

WebUI.switchToWindowTitle('')


WebUI.click(findTestObject('Object Repository/Dark/Page_/path'))

WebUI.click(findTestObject('Object Repository/Dark/Page_/div_Quick Search'))

WebUI.click(findTestObject('Object Repository/Dark/Page_/div_CatchProbe AI AnalystSurface Webillegal_1cfec2'))

WebUI.waitForElementVisible(findTestObject('Object Repository/Dark/Page_/button_Takedown'), 0)

WebUI.mouseOver(findTestObject('Object Repository/Dark/Page_/img_FILTER OPTIONS_h-full w-full cursor-poi_6e6839'))

WebUI.verifyElementClickable(findTestObject('Object Repository/Dark/Page_/img_FILTER OPTIONS_h-full w-full cursor-poi_6e6839'))

WebUI.click(findTestObject('Object Repository/Dark/Page_/img_FILTER OPTIONS_h-full w-full cursor-poi_6e6839'))

WebUI.verifyElementText(findTestObject('Object Repository/Dark/Page_/figure_Image_sc-bcXHqe iaMfJy loaded fullVi_120cb0'), 
    '')

WebUI.closeBrowser()

