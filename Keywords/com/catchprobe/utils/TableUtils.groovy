package com.catchprobe.utils

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import java.text.SimpleDateFormat
import java.util.Date
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.model.FailureHandling



class TableUtils {
	static void clickDateInTable(WebDriver driver, WebElement tableContainer, String currentDate) {
		List<WebElement> cells = tableContainer.findElements(By.cssSelector("td"))
		boolean isFound = false

		for (WebElement cell : cells) {
			if (cell.getText().trim().equals(currentDate)) {
				cell.click()
				isFound = true
				println "✅ Tarih bulundu ve tıklandı: " + currentDate
				break
			}
		}

		if (!isFound) {
			println "❌ Aranan tarih bulunamadı: " + currentDate
		}
	}

	@Keyword
	def scrollTableAndClickLocation(String locationText) {
		WebDriver driver = DriverFactory.getWebDriver()
		JavascriptExecutor js = (JavascriptExecutor) driver

		try {
			WebElement tableContainer = driver.findElement(By.cssSelector(".ant-table-content"))
			String xpath = "//span[contains(text(),'" + locationText + "')]"
			List<WebElement> locationElements = driver.findElements(By.xpath(xpath))

			if (locationElements.isEmpty()) {
				WebUI.comment("❌ Aranan text bulunamadı: " + locationText)
				KeywordUtil.markFailed("Aranan text bulunamadı: " + locationText)
				return
			}

			WebElement targetElement = locationElements.get(0)
			int currentScroll = 0
			boolean isVisible = false

			while (!isVisible) {
				try {
					isVisible = targetElement.isDisplayed()
				} catch (Exception e) {
					isVisible = false
				}

				if (isVisible) {
					break
				}

				if (currentScroll >= 5000) {
					WebUI.comment("❌ Maksimum scroll limitine ulaşıldı.")
					break
				}

				js.executeScript("arguments[0].scrollLeft += 200", tableContainer)
				WebUI.delay(0.1)
				currentScroll += 200
			}

			if (isVisible) {
				WebUI.comment("📌 Text bulundu: " + targetElement.getText())
				js.executeScript("arguments[0].click();", targetElement)
				WebUI.comment("👉 Tıklama işlemi yapıldı.")
			} else {
				WebUI.comment("❌ Scroll yapıldı ama element görünür olmadı.")
				KeywordUtil.markFailed("Scroll yapıldı ama element görünür olmadı.")
			}
		} catch (Exception e) {
			WebUI.comment("❌ Hata oluştu: " + e.getMessage())
			KeywordUtil.markFailed("❌ Hata oluştu: " + e.getMessage())
		}
	}

	@Keyword
	def checkMySharedTableAndAssert(String expectedName, String expectedCollection, String expectedUser, String expectedStatus) {
		WebDriver driver = DriverFactory.getWebDriver()
		Date today = new Date()
		String todayStr = new SimpleDateFormat("dd/MM/yyyy").format(today)
		WebUI.comment("📅 Bugünün tarihi: " + todayStr)

		boolean isMatchFound = false

		try {
			List<WebElement> rows = driver.findElements(By.cssSelector("tbody.ant-table-tbody tr.ant-table-row"))
			WebUI.comment("🔍 Toplam satır sayısı: " + rows.size())

			for (WebElement row : rows) {
				List<WebElement> cells = row.findElements(By.tagName("td"))

				if (cells.size() > 0) {
					String sharedName = cells.get(0).getText()
					String CollectionName = cells.get(1).getText()
					String UserText = cells.get(2).getText()
					String dateText = cells.get(3).getText()
					String statusText = cells.get(4).getText()

					WebUI.comment("👉 Paylaşım Adı: " + sharedName + " | Collection: " + CollectionName + " | Kullanıcı: " + UserText + " | Tarih: " + dateText + " | Durum: " + statusText)

					if (sharedName.equals(expectedName) && CollectionName.equals(expectedCollection) && UserText.equals(expectedUser) && dateText.contains(todayStr) && statusText.equalsIgnoreCase(expectedStatus)) {
						WebUI.comment("✅ Eşleşme bulundu!")
						isMatchFound = true
						break
					}
				}
			}

			if (isMatchFound) {
				KeywordUtil.markPassed("🎉 Tablo satırında beklenen veri bulundu.")
			} else {
				KeywordUtil.markFailedAndStop("❌ Beklenen veri tablo satırlarında bulunamadı.")
			}
		} catch (Exception e) {
			WebUI.comment("❌ Hata oluştu: " + e.getMessage())
			KeywordUtil.markFailedAndStop("❌ Hata oluştu: " + e.getMessage())
		}
	}
	@Keyword
	def static checkForUnexpectedToasts() {
		// Toast elementi tanımı
		TestObject toastMessage = new TestObject().addProperty("xpath",
			ConditionType.CONTAINS, "//li[contains(@class, 'bg-destructive')]")

		// Eğer ekranda toast varsa
		if (WebUI.verifyElementPresent(toastMessage, 2, FailureHandling.OPTIONAL)) {
			String toastText = WebUI.getText(toastMessage)

			// Planlı beklenen mesajlar
			List<String> allowedToasts = ['Operation successful', 'User created successfully']

			// Hata/şüpheli içerikler
			List<String> errorKeywords = ['error', 'invalid', 'exception', 'failed', 'an error occurred']

			// Hata içeriyor mu kontrolü
			boolean containsError = errorKeywords.any { toastText.toLowerCase().contains(it) }

			if (!allowedToasts.contains(toastText) || containsError) {
				WebUI.comment("❌ Beklenmeyen veya hata içeren toast mesajı tespit edildi: ${toastText}")
				throw new StepFailedException("Unexpected or error toast message: ${toastText}")
			}
		}
	}
}
