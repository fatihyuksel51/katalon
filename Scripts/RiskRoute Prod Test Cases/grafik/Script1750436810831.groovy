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
WebUI.openBrowser('')
WebUI.navigateToUrl('https://platform.catchprobe.org/')
WebUI.maximizeWindow()

WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/a_PLATFORM LOGIN'))
WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Email Address_email'), 'fatih.yuksel@catchprobe.com')
WebUI.setEncryptedText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_Password_password'), 'RigbBhfdqOBDK95asqKeHw==')
WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Sign in'))
WebUI.delay(5)

WebUI.setText(findTestObject('Object Repository/RiskRoute Dashboard/Page_/input_OTP Digit_vi_1_2_3_4_5'), (100000 + new Random().nextInt(900000)).toString())
WebUI.click(findTestObject('Object Repository/RiskRoute Dashboard/Page_/button_Verify'))
WebUI.delay(5)
WebUI.navigateToUrl('https://platform.catchprobe.org/riskroute')
WebUI.waitForPageLoad(30)


// WebDriver ve JS tanımla
WebDriver driver = DriverFactory.getWebDriver()
JavascriptExecutor js = (JavascriptExecutor) driver
Actions actions = new Actions(driver)

// Asset Daily Count grafiğine scroll
WebElement assetDailyCountElement = WebUiCommonHelper.findWebElement(
    findTestObject('Object Repository/RiskRoute Dashboard/grafik/Page_/Asset Daily Count Title'), 20)

if (scrollToVisible(assetDailyCountElement, js)) {
    js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", assetDailyCountElement)
    WebUI.comment("👉 'Asset Daily Count' grafiğine başarıyla scroll yapıldı.")
    WebUI.delay(1)
} else {
    WebUI.comment("❌ 'Asset Daily Count' grafiği görünür değil, scroll başarısız.")
}

// X eksenindeki noktalar
// Grafik alanını bul
WebElement chartCanvas = WebUiCommonHelper.findWebElement(
    findTestObject('Object Repository/RiskRoute Dashboard/grafik/Page_/Apexcharts Canvas'), 10)

if (chartCanvas == null) {
    WebUI.comment("❌ Apexcharts Canvas bulunamadı.")
    WebUI.stopTestCase()
}

// chartCanvas'ın içindeki SVG elementini bul
// Fare etkileşimlerini genellikle SVG elementinin içinde yapmak daha güvenilirdir.
WebElement targetElementForHover = null;
try {
    targetElementForHover = chartCanvas.findElement(By.tagName("svg"));
    WebUI.comment("SVG elementini hedef olarak kullanılıyor.")
} catch (Exception e) {
    WebUI.comment("SVG elementi bulunamadı, chartCanvas hedef olarak kullanılacak. Hata: " + e.getMessage())
    targetElementForHover = chartCanvas; // SVG bulunamazsa, div'i kullanmaya devam et
}

// Grafik alanının boyutlarını ve konumunu al
// Bu boyutlar targetElementForHover'ın boyutları olacaktır.
int chartWidth = targetElementForHover.getSize().getWidth()
int chartHeight = targetElementForHover.getSize().getHeight()

// Debug amaçlı boyutları logla
KeywordUtil.logInfo("Hedef Element (SVG/Canvas) Boyutları - Genişlik: ${chartWidth}, Yükseklik: ${chartHeight}")
KeywordUtil.logInfo("Hedef Element (SVG/Canvas) Konumu - X: ${targetElementForHover.getLocation().getX()}, Y: ${targetElementForHover.getLocation().getY()}")


// X eksenindeki denemek istediğimiz noktaların yüzdesi (0.0 ile 1.0 arası)
// Kenarlardan daha fazla içeriden başlamak için yüzdeleri değiştirdim
List<Double> xPercentages = [0.15, 0.25, 0.35, 0.45, 0.55, 0.65, 0.75, 0.85] // Başlangıç 0.05'ten 0.15'e, bitiş 0.95'ten 0.85'e çekildi

for (Double xPct : xPercentages) {
    // X koordinatını hesapla (hedef element içindeki piksel)
    // Minimum 15 piksel içeriden başla ve maksimum (genişlik - 15) piksele kadar git
    int xCoordInCanvas = (int) (chartWidth * xPct)
    xCoordInCanvas = Math.max(15, Math.min(xCoordInCanvas, chartWidth - 15)) // Kenarlardan 15 piksel içeride kal

    // Y koordinatını, grafiğin veri çizgisine yakın bir yer seçmeye çalışın.
    // Yüksekliğin %40'ı ile %80'i arası deneyebiliriz. Önceki 0.6 (%60) hala iyi bir başlangıç.
    int yCoordForInteraction = (int) (chartHeight * 0.6);
    yCoordForInteraction = Math.max(15, Math.min(yCoordForInteraction, chartHeight - 15)) // Kenarlardan 15 piksel içeride kal

    WebUI.comment("Denenecek X koordinatı: ${xCoordInCanvas}, Y koordinatı: ${yCoordForInteraction}")

    // Fareyi hedef element üzerinde belirli bir noktaya taşı
    try {
        // Fareyi SVG elementine göre hareket ettiriyoruz
        actions.moveToElement(targetElementForHover, xCoordInCanvas, yCoordForInteraction).perform()
        WebUI.delay(2) // Tooltip'in tetiklenmesi ve görünmesi için daha uzun bekle

        // Tooltip'in görünmesini bekle
        WebElement tooltip = null
        try {
            tooltip = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'apexcharts-tooltip') and contains(@style, 'opacity: 1')]//span[contains(@class,'apexcharts-tooltip-text-y-value') and string-length(text()) > 0]")))
            WebUI.comment("Tooltip elementi bulundu ve görünür.")
        } catch (Exception e) {
            println("X=${xCoordInCanvas} noktasında tooltip elementi bulunamadı veya görünür olmadı: ${e.getMessage()}")
            continue // Bir sonraki x noktasına geç
        }

        // Tooltip'in Asset Count etiketini kontrol et
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'apexcharts-tooltip')]//span[contains(@class,'apexcharts-tooltip-text-y-label') and text()='Asset Count: ']")))
            WebUI.comment("Tooltip üzerinde 'Asset Count:' etiketi bulundu.")
        } catch (Exception e) {
            println("X=${xCoordInCanvas} noktasında tooltip üzerinde 'Asset Count:' etiketi bulunamadı: ${e.getMessage()}")
            continue
        }

        int count = 0
        try {
            String countText = tooltip.getText().trim()
            if (!countText.isEmpty()) {
                count = Integer.parseInt(countText)
                println("X=${xCoordInCanvas} noktasında Tooltip Count: ${count}")
            } else {
                println("X=${xCoordInCanvas} noktasında Tooltip Count metni boş geldi.")
                continue
            }
        } catch (NumberFormatException e) {
            println("X=${xCoordInCanvas} noktasında Tooltip Count metni sayıya dönüştürülemedi: '${tooltip.getText()}' - ${e.getMessage()}")
            continue
        }

        if (count > 0) {
            println("Tooltip değeri ${count}, şimdi aynı noktaya click gönderiyorum...")

            actions.moveToElement(targetElementForHover, xCoordInCanvas, yCoordForInteraction).click().perform()
            WebUI.delay(7)

            List<WebElement> pages = driver.findElements(By.xpath("//ul[contains(@class,'flex')]/li"))
            if (!pages.isEmpty()) {
                println("📄 Sayfa sayısı: ${pages.size()} (detay sayfasına geçilmiş olabilir)")
            } else {
                println("📄 Yeni sayfada beklenen elemanlar bulunamadı. Sayfa geçişi gerçekleşmemiş olabilir.")
            }

            WebUI.navigateToUrl("https://platform.catchprobe.org/riskroute")
            WebUI.waitForPageLoad(30)
            WebUI.delay(2)

        } else {
            println("X=${xCoordInCanvas} noktasında tooltip değeri 0 veya daha az, tıklama yapılmadı.")
        }

    } catch (Exception e) {
        println("Genel hata oluştu X=${xCoordInCanvas} noktasında: ${e.getMessage()}")
        // 'move target out of bounds' hatası bu blokta yakalanır.
        // Hata durumunda döngünün devam etmesi için 'continue' kullanıldı.
        continue
    }
}

WebUI.closeBrowser()