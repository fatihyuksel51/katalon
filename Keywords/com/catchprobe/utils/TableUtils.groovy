package com.catchprobe.utils

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

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
}
