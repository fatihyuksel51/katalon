import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import com.kms.katalon.core.testobject.ConditionType

import internal.GlobalVariable

import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor

import com.kms.katalon.core.util.KeywordUtil 
import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import com.kms.katalon.core.model.FailureHandling as FailureHandling

import java.text.SimpleDateFormat
import java.util.Arrays

import com.catchprobe.utils.TableUtils
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject




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
WebUI.waitForElementVisible(findTestObject('Object Repository/otp/Page_/a_PLATFORM LOGIN'), 30)
WebUI.click(findTestObject('Object Repository/otp/Page_/a_PLATFORM LOGIN'))
WebUI.waitForElementVisible(findTestObject('Object Repository/otp/Page_/input_Email Address_email'), 30)
WebUI.setText(findTestObject('Object Repository/otp/Page_/input_Email Address_email'), 'fatih.yuksel@catchprobe.com')
WebUI.setEncryptedText(findTestObject('Object Repository/otp/Page_/input_Password_password'), 'RigbBhfdqOBDK95asqKeHw==')
WebUI.click(findTestObject('Object Repository/otp/Page_/button_Sign in'))
WebUI.delay(3)

// OTP işlemi
def randomOtp = (100000 + new Random().nextInt(900000)).toString()
WebUI.setText(findTestObject('Object Repository/otp/Page_/input_OTP Digit_vi_1_2_3_4_5'), randomOtp)
WebUI.click(findTestObject('Object Repository/otp/Page_/button_Verify'))
WebUI.waitForPageLoad(30)
WebUI.delay(3)
/*/


/*/ Threatway sekmesine git
WebUI.navigateToUrl('https://platform.catchprobe.org/threatway')
WebUI.waitForPageLoad(30)

// Signature List öğesinin görünmesini bekle ve tıkla
WebUI.waitForElementVisible(findTestObject('Object Repository/dashboard/Page_/Signature List'), 10)
WebUI.click(findTestObject('Object Repository/dashboard/Page_/Signature List'))

WebUI.waitForElementClickable(findTestObject('Object Repository/dashboard/Page_/Threatway button_Download CSV'), 30)
WebUI.waitForElementClickable(findTestObject('Object Repository/dashboard/Page_/Theatway filterbuton'), 40)
WebUI.click(findTestObject('Object Repository/dashboard/Page_/Theatway filterbuton'))

WebUI.delay(2)

WebUI.scrollToElement(findTestObject('Object Repository/dashboard/Page_/threatway button_APPLY AND SEARCH'), 5)
WebUI.waitForElementClickable(findTestObject('Object Repository/dashboard/Page_/Threatway first signature'), 30)


WebUI.waitForElementClickable(findTestObject('Object Repository/dashboard/Page_/threatway button_APPLY AND SEARCH'), 30)
WebUI.click(findTestObject('Object Repository/dashboard/Page_/threatway button_APPLY AND SEARCH'))

WebUI.waitForElementVisible(findTestObject('Object Repository/dashboard/Page_/Threatway first ip'), 30)

WebUI.scrollToElement(findTestObject('Object Repository/dashboard/Page_/Threatway input_Keyword_search_filters'), 5)
WebUI.waitForElementVisible(findTestObject('Object Repository/dashboard/Page_/Threatway input_Keyword_search_filters'), 10)

try {
	// Arama alanı görünür mü diye kontrol et
	if (WebUI.waitForElementVisible(findTestObject('Object Repository/dashboard/Page_/Threatway input_Keyword_search_filters'), 5)) {
		// Eğer görünürse text'i set et
			WebUI.setText(findTestObject('Object Repository/dashboard/Page_/Threatway input_Keyword_search_filters'), '176.9.66.101')

		KeywordUtil.logInfo("Arama alanına IP yazıldı ✅")
	} else {
		throw new StepFailedException("Arama alanı görünmedi.")
	}

} catch (Exception e) {
	// Hata alırsa filter butonuna tıklayıp yeniden dene
	KeywordUtil.logInfo("Arama alanı bulunamadı. Filter butonuna basılıyor 🔄")

	WebUI.waitForElementClickable(findTestObject('Object Repository/dashboard/Page_/Theatway filterbuton'), 10)
	WebUI.click(findTestObject('Object Repository/dashboard/Page_/Theatway filterbuton'))
	WebUI.delay(1)

	// Tekrar arama alanının gelmesini bekle ve setText dene
	WebUI.waitForElementVisible(findTestObject('Object Repository/dashboard/Page_/Threatway input_Keyword_search_filters'), 10)
	WebUI.setText(findTestObject('Object Repository/dashboard/Page_/Threatway input_Keyword_search_filters'), '176.9.66.101')
	KeywordUtil.logInfo("Arama alanına IP tekrar yazıldı ✅")
}


WebUI.waitForElementClickable(findTestObject('Object Repository/dashboard/Page_/threatway button_APPLY AND SEARCH'), 30)
WebUI.click(findTestObject('Object Repository/dashboard/Page_/threatway button_APPLY AND SEARCH'))







WebUI.waitForElementClickable(findTestObject('Object Repository/dashboard/Page_/Threatway first ip'), 30)
WebUI.click(findTestObject('Object Repository/dashboard/Page_/Threatway first ip'))



// Test Object'i al
TestObject divObject = findTestObject('Object Repository/dashboard/Page_/Threatway Number of Views')

// Div'in görünmesini bekle (maksimum 10 saniye)
if (WebUI.waitForElementVisible(divObject, 5)) {
    
    // Elementi bul
    WebElement divElement = WebUI.findWebElement(divObject, 10)
    
    // Div'in içinde SVG olup olmadığını kontrol et
    Boolean svgExists = WebUI.executeJavaScript(
        "return arguments[0].querySelector('svg') != null;", 
        Arrays.asList(divElement)
    )
    
    // Durumu logla
    KeywordUtil.logInfo("SVG var mı? : " + svgExists)
    
    if (svgExists) {
        KeywordUtil.logInfo("Veri VAR ✅")
    } else {
        KeywordUtil.logInfo("Veri YOK 🚨")
    }
    
} else {
    KeywordUtil.logInfo("Div elementi görünmedi ⏰")
}

// Risk Score Object'i al
TestObject riskscore = findTestObject('Object Repository/dashboard/Page_/Threatway Risk Score')

// Div'in görünmesini bekle (maksimum 10 saniye)
if (WebUI.waitForElementVisible(riskscore, 5)) {
	
	// Elementi bul
	WebElement riskscoreelement = WebUI.findWebElement(riskscore, 10)
	
	// Div'in içinde SVG olup olmadığını kontrol et
	Boolean svgExistsRisk = WebUI.executeJavaScript(
		"return arguments[0].querySelector('svg') != null;",
		Arrays.asList(riskscoreelement)
	)
	
	// Durumu logla
	KeywordUtil.logInfo("Risk Score SVG var mı? : " + svgExistsRisk)
	
	if (svgExistsRisk) {
		KeywordUtil.logInfo("Risck score Veri VAR ✅")
	} else {
		KeywordUtil.logInfo("Risk Score Veri YOK 🚨")
	}
	
} else {
	KeywordUtil.logInfo("Risk Score Div elementi görünmedi ⏰")
}



// Buton elementi al
WebElement iocDetailButton = WebUiCommonHelper.findWebElement(findTestObject('Object Repository/dashboard/Page_/Threatway iocDetailButton'), 10)

// WebDriver ve JS tanımla
WebDriver driver = DriverFactory.getWebDriver()
JavascriptExecutor js = (JavascriptExecutor) driver



// Scroll ve click işlemi
if (scrollToVisible(iocDetailButton, js)) {    
    js.executeScript("arguments[0].click();", iocDetailButton)
    WebUI.comment("👉 'ioc detail' butonuna tıklandı.")
} else {
    WebUI.comment("❌ 'ioc detail' butonu görünür değil, tıklanamadı.")
}

// İlgili indicator elementini al
WebElement indicatorTextElement = WebUiCommonHelper.findWebElement(findTestObject('Object Repository/dashboard/Page_/Threatway İndicatortext'), 10)

// Scroll ve text çekme işlemi
String indicatorText = ''
if (scrollToVisible(indicatorTextElement, js)) {
    indicatorText = indicatorTextElement.getText()
    println("📌 IndicatorText : " + indicatorText)
} else {
    WebUI.comment("❌ 'IndicatorText' görünür değil.")
}

// İlgili doğrulamayı yap
WebUI.waitForElementClickable(findTestObject('Object Repository/dashboard/Page_/Threatway İndicatortext'), 15)
WebUI.verifyMatch(indicatorText, '176.9.66.101', false)
//Threatway doğrulaması yap
WebElement ThreatwayTextElement = WebUiCommonHelper.findWebElement(findTestObject('Object Repository/dashboard/Page_/button_THREATWAY'), 10)
String ThreatwayText=''
if (scrollToVisible(ThreatwayTextElement, js)) {
	ThreatwayText = ThreatwayTextElement.getText()
	println("📌 ThreatwayText : " + ThreatwayText)
	
} else {
	WebUI.comment("❌ 'ThreatwayText' görünür değil.")
}
//Darkmap doğrulaması yap
WebElement DarkmapElement = WebUiCommonHelper.findWebElement(findTestObject('Object Repository/Platform IOC Discoveries/Darkmap Button'), 10)
// Scroll ve click işlemi
if (scrollToVisible(DarkmapElement, js)) {
	js.executeScript("arguments[0].click();", DarkmapElement)
	WebUI.comment("👉 'ioc detail' butonuna tıklandı.")
} else {
	WebUI.comment("❌ 'ioc detail' butonu görünür değil, tıklanamadı.")
}

WebUI.waitForElementClickable(findTestObject('Object Repository/Platform IOC Discoveries/Darkmap Link'), 10)
String DarkmapİOC=WebUI.getText(findTestObject('Object Repository/Platform IOC Discoveries/Darkmap Link'))
WebUI.click(findTestObject('Object Repository/Platform IOC Discoveries/Darkmap Link'))
WebUI.switchToWindowIndex(1)

WebUI.waitForElementClickable(findTestObject('Object Repository/Platform IOC Discoveries/Darkmap Qs Lİnk'), 10)
WebUI.verifyElementText(findTestObject('Object Repository/Platform IOC Discoveries/Darkmap Qs Lİnk'), DarkmapİOC )
WebUI.closeWindowIndex(1)
WebUI.switchToWindowIndex(0)
WebUI.waitForElementClickable(findTestObject('Object Repository/Platform IOC Discoveries/Darkmap Button'), 10)
WebUI.click(findTestObject('Object Repository/Platform IOC Discoveries/Darkmap Button'))

//Smartdeceptive Doğrulaması yap
WebElement SmartdeceptiveElement = WebUiCommonHelper.findWebElement(findTestObject('Object Repository/Platform IOC Discoveries/Smartdeceptive Button'), 10)
// Scroll ve click işlemi
if (scrollToVisible(SmartdeceptiveElement, js)) {
	js.executeScript("arguments[0].click();", SmartdeceptiveElement)
	WebUI.comment("👉 'smartdeceptive detail' butonuna tıklandı.")
} else {
	WebUI.comment("❌ 'smartdeceptive detail' butonu görünür değil, tıklanamadı.")
}
WebUI.scrollToElement(findTestObject('Object Repository/Platform IOC Discoveries/Smartdeceptive içerik'), 10)


// Smartdeceptive Risk Score al
TestObject smartdeceptiverisckscore = findTestObject('Object Repository/Platform IOC Discoveries/Smartdeceptive içerik')

// Div'in görünmesini bekle (maksimum 10 saniye)
if (WebUI.waitForElementVisible(smartdeceptiverisckscore, 15)) {
	
	// Elementi bul
	WebElement smartdeceptiveriskscoreelement = WebUI.findWebElement(smartdeceptiverisckscore, 10)
	
	// Div'in içinde SVG olup olmadığını kontrol et
	Boolean SmartdeceptivedetailsvgExistsRisk = WebUI.executeJavaScript(
		"return arguments[0].querySelector('svg') != null;",
		Arrays.asList(smartdeceptiveriskscoreelement)
	)
	
	// Durumu logla
	KeywordUtil.logInfo("Smartdeceptive  Detail Risk Score SVG var mı? : " + SmartdeceptivedetailsvgExistsRisk)
	
	if (SmartdeceptivedetailsvgExistsRisk) {
		KeywordUtil.logInfo("Smartdeceptive  Detail Risk Score Veri VAR ✅")
		
		
	} else {
		KeywordUtil.logInfo("Smartdeceptive  Detail Risk Score Veri YOK 🚨")
	}
	
} else {
	KeywordUtil.logInfo("Smartdeceptive  Detail Risk Score Div elementi görünmedi ⏰")
}





// Önce sayfanın en üstüne çık
js.executeScript("window.scrollTo(0, 0);")
WebUI.delay(1)

//Abuse Report butonuna tıkla
WebUI.waitForElementClickable(findTestObject('Object Repository/Platform IOC Discoveries/Abuse Report'), 30)
WebUI.click(findTestObject('Object Repository/Platform IOC Discoveries/Abuse Report'))

String Abusedetail = WebUI.getText(findTestObject('Object Repository/Platform IOC Discoveries/Abuse detail'))
WebUI.verifyElementText(findTestObject('Object Repository/Platform IOC Discoveries/Abuse detail'), '176.9.66.101' )
WebUI.click(findTestObject('Object Repository/Platform IOC Discoveries/Submit Butonu'))
WebUI.waitForElementVisible(findTestObject('Object Repository/Platform IOC Discoveries/Alresdy Toast'), 30)
WebUI.verifyElementText(findTestObject('Object Repository/Platform IOC Discoveries/Alresdy Toast'),
	'The resource already exists.')
/*/

//Tekrar Signature Lİst Sayfası Filter bölümüne gel
// Threatway sekmesine git
WebUI.navigateToUrl('https://platform.catchprobe.org/threatway')
WebUI.waitForPageLoad(30)

// Signature List öğesinin görünmesini bekle ve tıkla
WebUI.waitForElementVisible(findTestObject('Object Repository/dashboard/Page_/Signature List'), 10)
WebUI.click(findTestObject('Object Repository/dashboard/Page_/Signature List'))




try {
	// Arama alanı görünür mü diye kontrol et
	if (WebUI.waitForElementVisible(findTestObject('Object Repository/dashboard/Page_/Threatway input_Keyword_search_filters'), 5)) {
		// Eğer görünürse text'i set et
			WebUI.setText(findTestObject('Object Repository/dashboard/Page_/Threatway input_Keyword_search_filters'), 'nygptysn.cn')

		KeywordUtil.logInfo("Arama alanına IP yazıldı ✅")
	} else {
		throw new StepFailedException("Arama alanı görünmedi.")
	}

} catch (Exception e) {
	// Hata alırsa filter butonuna tıklayıp yeniden dene
	KeywordUtil.logInfo("Arama alanı bulunamadı. Filter butonuna basılıyor 🔄")

	WebUI.waitForElementClickable(findTestObject('Object Repository/dashboard/Page_/Theatway filterbuton'), 10)
	WebUI.click(findTestObject('Object Repository/dashboard/Page_/Theatway filterbuton'))
	WebUI.delay(1)

	// Tekrar arama alanının gelmesini bekle ve setText dene
	WebUI.waitForElementVisible(findTestObject('Object Repository/dashboard/Page_/Threatway input_Keyword_search_filters'), 10)
	WebUI.setText(findTestObject('Object Repository/dashboard/Page_/Threatway input_Keyword_search_filters'), 'nygptysn.cn')
	KeywordUtil.logInfo("Arama alanına IP tekrar yazıldı ✅")
}

WebUI.waitForElementClickable(findTestObject('Object Repository/dashboard/Page_/threatway button_APPLY AND SEARCH'), 30)
WebUI.click(findTestObject('Object Repository/dashboard/Page_/threatway button_APPLY AND SEARCH'))

WebUI.waitForElementClickable(findTestObject('Object Repository/dashboard/Page_/Threatway first ip'), 30)
WebUI.click(findTestObject('Object Repository/dashboard/Page_/Threatway first ip'))

//Abuse Report butonuna tıkla
WebUI.waitForElementClickable(findTestObject('Object Repository/Platform IOC Discoveries/Abuse Report'), 30)
WebUI.click(findTestObject('Object Repository/Platform IOC Discoveries/Abuse Report'))

String Abusedetailsecond = WebUI.getText(findTestObject('Object Repository/Platform IOC Discoveries/Abuse detail'))
WebUI.verifyElementText(findTestObject('Object Repository/Platform IOC Discoveries/Abuse detail'), 'nygptysn.cn' )
WebUI.click(findTestObject('Object Repository/Platform IOC Discoveries/Submit Butonu'))
WebUI.waitForElementVisible(findTestObject('Object Repository/Platform IOC Discoveries/Toast Message'), 45)
WebUI.verifyElementText(findTestObject('Object Repository/Platform IOC Discoveries/Toast Message'),
	'Abuse report submitted successfully')
//

WebUI.navigateToUrl('https://platform.catchprobe.org/service-desk')
WebUI.waitForPageLoad(30)
WebUI.delay(4)
assert WebUI.getText(findTestObject('Object Repository/Platform IOC Discoveries/Abuse service desk')).contains(Abusedetailsecond)
WebUI.delay(2)


