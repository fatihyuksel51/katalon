package com.catchprobe.utils
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.checkpoint.CheckpointFactory
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testcase.TestCaseFactory
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testdata.TestDataFactory
import com.kms.katalon.core.testobject.ObjectRepository
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords

import internal.GlobalVariable

import org.openqa.selenium.WebElement
import org.openqa.selenium.WebDriver
import org.openqa.selenium.By

import com.kms.katalon.core.mobile.keyword.internal.MobileDriverFactory
import com.kms.katalon.core.webui.driver.DriverFactory

import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty

import com.kms.katalon.core.mobile.helper.MobileElementCommonHelper
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.exception.WebElementNotFoundException
import jakarta.mail.Flags
import jakarta.mail.*
import jakarta.mail.search.*
import java.util.Properties

public class MailUtils {

	static void checkMailBetweenTimes(String mailAdres, String mailSifre, String subjectAranan, Date startDate, Date endDate) {
		try {
			Properties properties = new Properties()
			properties.put("mail.store.protocol", "imaps")

			Session emailSession = Session.getDefaultInstance(properties)
			Store store = emailSession.getStore("imaps")
			store.connect("imap.gmail.com", mailAdres, mailSifre)

			Folder inbox = store.getFolder("INBOX")
			inbox.open(Folder.READ_WRITE) // ✅ Silmek için READ_WRITE olmalı

			SearchTerm subjectTerm = new SubjectTerm(subjectAranan)
			Message[] messages = inbox.search(subjectTerm)

			boolean mailBulundu = false
			KeywordUtil.logInfo("📬 INBOX'taki '${subjectAranan}' başlıklı toplam ${messages.length} mail bulundu.")

			messages.each { Message message ->
				Date mailDate = message.getReceivedDate()
				String subject = message.getSubject()
				KeywordUtil.logInfo("📨 Mail: ${subject} - ${mailDate}")

				// 📌 Tarih aralığında mı?
				if (mailDate != null && mailDate.after(startDate) && mailDate.before(endDate)) {
					mailBulundu = true
					KeywordUtil.logInfo("✅ Eşleşen mail bulundu: ${subject} - ${mailDate}")
				}
			}

			if (mailBulundu) {
				KeywordUtil.markPassed("✅ Belirtilen tarih aralığında '${subjectAranan}' başlıklı mail bulundu.")
			} else {
				KeywordUtil.markFailed("❌ Belirtilen tarih aralığında '${subjectAranan}' başlıklı mail GELMEDİ.")
			}

			// 🔥 Eski mailleri sil, son 5 tanesi kalsın
			if (messages.length > 5) {
				Arrays.sort(messages, { a, b -> b.getReceivedDate() <=> a.getReceivedDate() }) // Yeni → Eski sıralama
				for (int i = 5; i < messages.length; i++) {
					messages[i].setFlag(Flags.Flag.SEEN, true)     // ✅ Okundu yap
					messages[i].setFlag(Flags.Flag.DELETED, true)  // 🗑️ Sil
					KeywordUtil.logInfo("🗑️ Eski mail silindi: ${messages[i].getSubject()}")
				}
				inbox.close(true) // ✅ Silinenleri uygula
			} else {
				inbox.close(false) // ❌ Silme yoksa sadece kapat
			}

			store.close()
		} catch (Exception e) {
			KeywordUtil.markFailed("❌ Mail kontrolünde hata: ${e.message}")
		}
	}
}