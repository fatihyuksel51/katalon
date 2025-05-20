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

// Threatway sekmesine tıkla
WebUI.click(findTestObject('Object Repository/dashboard/Page_/threatway'))

// Signature List öğesinin görünmesini bekle ve tıkla
WebUI.waitForElementVisible(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/div_Signature List'), 30)
WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/div_Signature List'))


// Bugünün tarihini al
String today = new SimpleDateFormat("dd/MM/yyyy").format(new Date())
println("tarih"+ today)
WebUI.delay(5)


// 📌 Keyword metodunu çağır
  // Tabloyu scroll et ve location’a tıkla
CustomKeywords.'com.catchprobe.utils.TableUtils.scrollTableAndClickLocation'( today)

WebUI.waitForElementClickable(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/button_Download CSV'), 30)
WebUI.waitForElementClickable(findTestObject('Object Repository/dashboard/Page_/filterbuton'), 40)
WebUI.click(findTestObject('Object Repository/dashboard/Page_/filterbuton'))

WebUI.waitForElementClickable(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/span_Choose a Date'), 30)
WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/span_Choose a Date'))

WebUI.waitForElementClickable(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/button_1'), 30)
WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/button_1'))

WebUI.scrollToElement(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/nav_Previous12More pages177095Next'), 5)
WebUI.waitForElementVisible(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/nav_Previous12More pages177095Next'), 5)
WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/nav_Previous12More pages177095Next'))
String startdate = WebUI.getText(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/button_Choose a Date'))
println('start date '+  startdate  )




WebUI.waitForElementClickable(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/span_Choose a Date_1'), 30)
WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/span_Choose a Date_1'))
String todayDay = new SimpleDateFormat("d").format(new Date())
// Bugünün gününe tıkla
TestObject todayButton = new TestObject()
todayButton.addProperty("xpath", ConditionType.EQUALS, "//button[@name='day' and text()='" + todayDay + "']")

WebUI.click(todayButton)
WebUI.delay(2)





WebUI.scrollToElement(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/nav_Previous12More pages177095Next'), 5)
WebUI.waitForElementVisible(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/nav_Previous12More pages177095Next'), 5)
WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/nav_Previous12More pages177095Next'))


//WebUI.waitForElementClickable(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/button_13'), 30)
//WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/button_13'))

WebUI.scrollToElement(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/nav_Previous12More pages177095Next'), 5)
WebUI.waitForElementVisible(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/nav_Previous12More pages177095Next'), 5)
WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/nav_Previous12More pages177095Next'))
WebUI.delay(2)
WebUI.waitForElementClickable(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/span_Choose a Date_1'), 30)


// Select elementinden 'IPv4' seç
WebUI.waitForElementClickable(findTestObject('Object Repository/Smartdeceptive/Page_/ipv4/Page_/İPv4-buton'), 20)
WebUI.scrollToElement(findTestObject('Object Repository/Smartdeceptive/Page_/ipv4/Page_/İPv4-buton'), 5)
WebUI.click(findTestObject('Object Repository/Smartdeceptive/Page_/ipv4/Page_/İPv4-buton'))

WebUI.delay(2)
WebUI.click(findTestObject('Object Repository/Smartdeceptive/Page_/select_dropdown'))
WebUI.delay(2)
String obserble = WebUI.getText(findTestObject('Object Repository/Smartdeceptive/Page_/ipv4/Page_/İPv4-buton'))
println('start date '+  obserble )
// tik
WebUI.scrollToElement(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/nav_Previous12More pages177095Next'), 5)
WebUI.waitForElementVisible(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/nav_Previous12More pages177095Next'), 5)
WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/nav_Previous12More pages177095Next'))


WebUI.scrollToElement(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/nav_Previous12More pages177095Next'), 5)

WebUI.waitForElementClickable(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/nav_Previous12More pages177095Next'), 30)
WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/nav_Previous12More pages177095Next'))
WebUI.scrollToElement(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/button_APPLY AND SEARCH'), 5)
WebUI.waitForElementClickable(findTestObject('Object Repository/dashboard/Page_/Page_/favori_ip'), 30)


WebUI.waitForElementClickable(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/button_APPLY AND SEARCH'), 30)
WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/button_APPLY AND SEARCH'))

WebUI.waitForElementVisible(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/a_113.27.28.61'), 30)
String FavoriIp = WebUI.getText(findTestObject('Object Repository/dashboard/Page_/Page_/favori_ip'))
println('favori ip'+ FavoriIp  )

WebUI.verifyElementText(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/a_113.27.28.61'), FavoriIp )


WebUI.scrollToElement(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/input_Keyword_search_filters'), 5)
WebUI.waitForElementVisible(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/input_Keyword_search_filters'), 30)



try {
	// Arama alanı görünür mü diye kontrol et
	if (WebUI.waitForElementVisible(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/input_Keyword_search_filters'), 5)) {
		// Eğer görünürse text'i set et
		WebUI.setText(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/input_Keyword_search_filters'), FavoriIp)
		KeywordUtil.logInfo("Arama alanına IP yazıldı ✅")
	} else {
		throw new StepFailedException("Arama alanı görünmedi.")
	}

} catch (Exception e) {
	// Hata alırsa filter butonuna tıklayıp yeniden dene
	KeywordUtil.logInfo("Arama alanı bulunamadı. Filter butonuna basılıyor 🔄")

	WebUI.waitForElementClickable(findTestObject('Object Repository/dashboard/Page_/filterbuton'), 10)
	WebUI.click(findTestObject('Object Repository/dashboard/Page_/filterbuton'))
	WebUI.delay(1)

	// Tekrar arama alanının gelmesini bekle ve setText dene
	WebUI.waitForElementVisible(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/input_Keyword_search_filters'), 10)
	WebUI.setText(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/input_Keyword_search_filters'), FavoriIp)
	KeywordUtil.logInfo("Arama alanına IP tekrar yazıldı ✅")
}


WebUI.waitForElementClickable(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/button_APPLY AND SEARCH'), 30)
WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/button_APPLY AND SEARCH'))
WebUI.waitForElementClickable(findTestObject('Object Repository/dashboard/Page_/Page_/favori_ip'), 30)
WebUI.verifyElementText(findTestObject('Object Repository/dashboard/Page_/SignatureList-CollectionName'), obserble )
WebUI.verifyElementText(findTestObject('Object Repository/dashboard/Page_/Keywordpop'), FavoriIp )
String actualDate = WebUI.getText(findTestObject('Object Repository/dashboard/Page_/Startdatepop'))
actualDate = actualDate.replace('/', '.')
WebUI.verifyMatch(actualDate, startdate, false)
String actualendDate = WebUI.getText(findTestObject('Object Repository/dashboard/Page_/Page_/Enddatepop'))

WebUI.verifyMatch(actualendDate, today , false)

// Doğru XPath ile butonu tanımla


// Favori butonunun XPath'ine göre dinamik TestObject oluştur
TestObject favButton = new TestObject()
favButton.addProperty("xpath", ConditionType.EQUALS, "//div[contains(@class, 'cursor-pointer') and (contains(@class, 'bg-primary') or contains(@class, 'bg-warning'))]/ancestor::button[1]")

WebUI.waitForElementVisible(favButton, 20)

// JavaScript ile butonun class'ını al
String buttonClass = WebUI.executeJavaScript(
    """
    var el = document.evaluate("//div[contains(@class, 'cursor-pointer') and (contains(@class, 'bg-primary') or contains(@class, 'bg-warning'))]", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;
    return el ? el.className : null;
    """, null
)

KeywordUtil.logInfo("Buton class: " + buttonClass)

// Başarı mesajları için dinamik TestObject'ler
TestObject addedMessage = new TestObject()
addedMessage.addProperty("xpath", ConditionType.EQUALS, "//div[contains(text(), 'added to favorite successfully')]")

TestObject removedMessage = new TestObject()
removedMessage.addProperty("xpath", ConditionType.EQUALS, "//div[contains(text(), 'removed to favorite successfully')]")  // <- bu senin belirttiğin doğru mesaj

// Butonun rengine göre işlem yap
if (buttonClass != null && buttonClass.contains('bg-warning')) {
    WebUI.click(favButton)
    WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/button_Add_'))

    WebUI.waitForElementVisible(removedMessage, 10)
    WebUI.verifyElementText(removedMessage, 'Signature removed to favorite successfully')

    KeywordUtil.logInfo("Favori kaldırıldı ✅")
}
else if (buttonClass != null && buttonClass.contains('bg-primary')) {
    WebUI.click(favButton)
    WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/button_Add_'))

    WebUI.waitForElementVisible(addedMessage, 10)
    WebUI.verifyElementText(addedMessage, 'Signature added to favorite successfully')

    KeywordUtil.logInfo("Favori eklendi ✅")
}
else {
    KeywordUtil.markFailed("Favori butonunun class bilgisi okunamadı 🚨: " + buttonClass)
}


WebUI.waitForElementClickable(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/a_113.27.28.61'), 30)
WebUI.click(findTestObject('Object Repository/dashboard/Page_/Page_/Page_/a_113.27.28.61'))

// Test Object'i al
TestObject divObject = findTestObject('Object Repository/dashboard/Page_/Number of Views')

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
TestObject riskscore = findTestObject('Object Repository/dashboard/Page_/Risk Score')

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
WebElement iocDetailButton = WebUiCommonHelper.findWebElement(findTestObject('Object Repository/Dark/Page_/iocDetailButton'), 10)

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
WebElement indicatorTextElement = WebUiCommonHelper.findWebElement(findTestObject('Object Repository/Dark/Page_/İndicatortext'), 10)

// Scroll ve text çekme işlemi
String indicatorText = ''
if (scrollToVisible(indicatorTextElement, js)) {
    indicatorText = indicatorTextElement.getText()
    println("📌 IndicatorText : " + indicatorText)
} else {
    WebUI.comment("❌ 'IndicatorText' görünür değil.")
}

// İlgili doğrulamayı yap
WebUI.waitForElementClickable(findTestObject('Object Repository/Dark/Page_/İndicatortext'), 10)
WebUI.verifyMatch(indicatorText, FavoriIp, false)
//Threatway doğrulaması yap
WebElement ThreatwayTextElement = WebUiCommonHelper.findWebElement(findTestObject('Object Repository/dashboard/Page_/button_THREATWAY'), 10)
String ThreatwayText=''
if (scrollToVisible(ThreatwayTextElement, js)) {
	ThreatwayText = ThreatwayTextElement.getText()
	println("📌 ThreatwayText : " + ThreatwayText)
	
} else {
	WebUI.comment("❌ 'ThreatwayText' görünür değil.")
}


//Show detail butonuna tıkla
WebUI.waitForElementClickable(findTestObject('Object Repository/dashboard/Page_/button_show detail'), 10)
WebUI.click(findTestObject('Object Repository/dashboard/Page_/button_show detail'))

// İp adress ve Risk Score doğrula
WebElement İpProfileTextElement = WebUiCommonHelper.findWebElement(findTestObject('Object Repository/dashboard/Page_/Show detail text'), 18)
String İpProfileText = ''

if (scrollToVisible(İpProfileTextElement, js)) {
	// Text değil, value attribute'unu alıyoruz
	İpProfileText = İpProfileTextElement.getAttribute("value")
	println("📌 İpProfileText : " + İpProfileText)
	WebUI.verifyMatch(İpProfileText, indicatorText , false)
} else {
	WebUI.comment("❌ 'İpProfileText' görünür değil.")
}
// Risk Score Object'i al
TestObject showdetailriskscore = findTestObject('Object Repository/dashboard/Page_/Show detail circle')

// Div'in görünmesini bekle (maksimum 10 saniye)
if (WebUI.waitForElementVisible(showdetailriskscore, 15)) {
	
	// Elementi bul
	WebElement showriskscoreelement = WebUI.findWebElement(showdetailriskscore, 10)
	
	// Div'in içinde SVG olup olmadığını kontrol et
	Boolean ShowdetailsvgExistsRisk = WebUI.executeJavaScript(
		"return arguments[0].querySelector('svg') != null;",
		Arrays.asList(showriskscoreelement)
	)
	
	// Durumu logla
	KeywordUtil.logInfo("Show Detail Risk Score SVG var mı? : " + ShowdetailsvgExistsRisk)
	
	if (ShowdetailsvgExistsRisk) {
		KeywordUtil.logInfo("Show Detail Risk Score Veri VAR ✅")
	} else {
		KeywordUtil.logInfo("Show Detail Risk Score Veri YOK 🚨")
	}
	
} else {
	KeywordUtil.logInfo("Show Detail Risk Score Div elementi görünmedi ⏰")
}

//Show sigthins butonuna scroll ol ve tıkla
WebElement SightingMapButton = WebUiCommonHelper.findWebElement(findTestObject('Object Repository/dashboard/Page_/button_show sightings map'), 15)

if (scrollToVisible(SightingMapButton, js)) {
	js.executeScript("arguments[0].click();", SightingMapButton)
	WebUI.comment("👉 'Sighting Map butonu tıklandı.")
} else {
	WebUI.comment("❌ Sighting Map butonu görünür değil, kapatılamadı.")
}

//Sighting Map Profile de path doğrulaması yap
TestObject sightingmappath =new TestObject().addProperty("id", ConditionType.EQUALS, "ipProfileChartdiv")
//TestObject showdetailriskscore = findTestObject('Object Repository/dashboard/Page_/Sighting Map circle')

// SVG yüklensin diye biraz bekle
WebUI.delay(2)

// Sighting Map elementi görünür mü?
if (WebUI.waitForElementVisible(sightingmappath, 15)) {

	WebElement sightingmappathelement = WebUI.findWebElement(sightingmappath, 10)

	// İçinde <g> ya da <image> gibi grafik var mı kontrol et
	Boolean hasGraphics = WebUI.executeJavaScript(
		"""
		return arguments[0].querySelector('g[id^="id-"]') != null || arguments[0].querySelector('image') != null;
		""",
		Arrays.asList(sightingmappathelement)
	)

	if (hasGraphics) {
		KeywordUtil.logInfo("✅ Sighting Map içinde grafik var.")
	} else {
		KeywordUtil.markFailedAndStop("🚨 Sighting Map elementi bulundu ama içinde grafik yok!")
	}

} else {
	KeywordUtil.markFailedAndStop("⛔ Sighting Map elementi 15 saniye içinde görünmedi!")
}


// Close butonuna tıkla

WebElement ShowDetailClose = WebUiCommonHelper.findWebElement(findTestObject('Object Repository/dashboard/Page_/Page_/Show detail Close button'), 20)

if (scrollToVisible(ShowDetailClose, js)) {
	js.executeScript("arguments[0].click();", ShowDetailClose)
	WebUI.comment("👉 'show detail' butonu kapatıldı.")
} else {
	WebUI.comment("❌ 'show detail close' butonu görünür değil, kapatılamadı.")
}



/*// ioc detail popup'ını kapat
WebElement iocDetailButtonClose = WebUiCommonHelper.findWebElement(findTestObject('Object Repository/Dark/Page_/iocDetailButton'), 10)

if (scrollToVisible(iocDetailButtonClose, js)) {
    js.executeScript("arguments[0].click();", iocDetailButtonClose)
    WebUI.comment("👉 'ioc detail' butonu kapatıldı.")
} else {
    WebUI.comment("❌ 'ioc detail' butonu görünür değil, kapatılamadı.")
}

// Stic package butonu tıkla
WebElement StixPackageButton = WebUiCommonHelper.findWebElement(findTestObject('Object Repository/Dark/Page_/StixPackageButton'), 10)
// Scroll ve click işlemi
if (scrollToVisible(StixPackageButton, js)) {
	js.executeScript("arguments[0].click();", StixPackageButton)
	WebUI.comment("👉 stix package butonuna tıklandı.")
} else {
	WebUI.comment("❌ stix package butonu görünür değil, tıklanamadı.")
}
// Stix package Circle Object'i al
TestObject circle = findTestObject('Object Repository/dashboard/Page_/Stix Cricle')

// Div'in görünmesini bekle (maksimum 10 saniye)
if (WebUI.waitForElementVisible(circle, 10)) {
	
	// Elementi bul
	WebElement circleelement = WebUI.findWebElement(circle, 10)
	
	// Div'in içinde circle olup olmadığını kontrol et
	Boolean circleExistsRisk = WebUI.executeJavaScript(
		"return arguments[0].querySelector('circle') != null;",
		Arrays.asList(circleelement)
	)
	
	// Durumu logla
	KeywordUtil.logInfo("Stix Package Circle var mı? : " + circleExistsRisk)
	
	if (circleExistsRisk) {
		KeywordUtil.logInfo("Stix Package Circle Veri VAR ✅")
	} else {
		KeywordUtil.logInfo("Stix Package Circle Veri YOK 🚨")
	}
	
} else {
	KeywordUtil.logInfo("Stix Package Circle elementi görünmedi ⏰")
}
// stix package popup'ını kapat
WebUI.click(findTestObject('Object Repository/Dark/Page_/StixPackageButton'))




/*/ //Tarayıcıyı kapat
 WebUI.closeBrowser()
