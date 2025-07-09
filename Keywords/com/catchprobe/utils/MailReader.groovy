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
import javax.mail.*
import javax.mail.internet.MimeMultipart
import java.util.Properties


public class MailReader {
    /**
     * Mail kutusundan son gelen mailin konu satırında belirtilen anahtar kelimeyi içerip içermediğini kontrol eder.
     */
    static boolean checkLatestEmail(String host, String username, String password, String keyword) {
        Properties properties = new Properties()
        properties.put("mail.store.protocol", "imaps")
        Session emailSession = Session.getDefaultInstance(properties)

        Store store = emailSession.getStore("imaps")
        store.connect(host, username, password)

        Folder inbox = store.getFolder("INBOX")
        inbox.open(Folder.READ_ONLY)

        Message[] messages = inbox.getMessages()
        if (messages.length == 0) {
            println "📭 Mail kutusu boş"
            return false
        }

        Message latestMessage = messages[messages.length - 1]
        String subject = latestMessage.getSubject()
        String body = getTextFromMessage(latestMessage)

        println "📨 Son mailin konusu: ${subject}"
        println "📨 Son mail içeriği: ${body.take(300)}..."

        boolean result = subject.toLowerCase().contains(keyword.toLowerCase()) || body.toLowerCase().contains(keyword.toLowerCase())

        inbox.close(false)
        store.close()

        return result
    }

    private static String getTextFromMessage(Message message) {
        if (message.isMimeType("text/plain")) {
            return message.getContent().toString()
        } else if (message.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) message.getContent()
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart part = multipart.getBodyPart(i)
                if (part.isMimeType("text/plain")) {
                    return part.getContent().toString()
                }
            }
        }
        return ""
    }
}