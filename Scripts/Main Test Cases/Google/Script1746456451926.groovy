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

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

// Tarayıcıyı aç
WebUI.openBrowser('')

// Google'a git
WebUI.navigateToUrl('https://www.google.com')

// Arama inputuna metin yaz ve arat
WebUI.setText(findTestObject('Object Repository/GoogleSearchInput'), 'Katalon TestOps IP Test')
WebUI.sendKeys(findTestObject('Object Repository/GoogleSearchInput'), Keys.chord(Keys.ENTER))

// Biraz bekle ki sayfa gelsin
WebUI.delay(3)

// TestOps agent IP'sini al ve log'a bas
try {
	def agentIp = new URL("https://ifconfig.me/ip").openStream().getText().trim()
	println "👉 TestOps Agent IP Adresi: " + agentIp
} catch(Exception e) {
	println "❌ IP alınamadı: " + e.getMessage()
}

// Tarayıcıyı kapat
WebUI.closeBrowser()
