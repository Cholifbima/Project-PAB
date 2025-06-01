package com.example.pabproject.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow

class MainViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    // Text QR
    private val _textContent = savedStateHandle.getStateFlow("textContent", "")
    val textContent: StateFlow<String> = _textContent

    // URL QR
    private val _urlContent = savedStateHandle.getStateFlow("urlContent", "")
    val urlContent: StateFlow<String> = _urlContent

    // Email QR
    private val _emailAddress = savedStateHandle.getStateFlow("emailAddress", "")
    val emailAddress: StateFlow<String> = _emailAddress
    
    private val _emailSubject = savedStateHandle.getStateFlow("emailSubject", "")
    val emailSubject: StateFlow<String> = _emailSubject
    
    private val _emailBody = savedStateHandle.getStateFlow("emailBody", "")
    val emailBody: StateFlow<String> = _emailBody

    // SMS QR
    private val _phoneNumber = savedStateHandle.getStateFlow("phoneNumber", "")
    val phoneNumber: StateFlow<String> = _phoneNumber
    
    private val _smsMessage = savedStateHandle.getStateFlow("smsMessage", "")
    val smsMessage: StateFlow<String> = _smsMessage

    // WiFi QR
    private val _wifiSSID = savedStateHandle.getStateFlow("wifiSSID", "")
    val wifiSSID: StateFlow<String> = _wifiSSID
    
    private val _wifiPassword = savedStateHandle.getStateFlow("wifiPassword", "")
    val wifiPassword: StateFlow<String> = _wifiPassword
    
    private val _wifiEncryption = savedStateHandle.getStateFlow("wifiEncryption", "WPA")
    val wifiEncryption: StateFlow<String> = _wifiEncryption

    // Twitter QR
    private val _twitterUsername = savedStateHandle.getStateFlow("twitterUsername", "")
    val twitterUsername: StateFlow<String> = _twitterUsername
    
    private val _tweetText = savedStateHandle.getStateFlow("tweetText", "")
    val tweetText: StateFlow<String> = _tweetText

    // Common states
    private val _isQrGenerated = savedStateHandle.getStateFlow("isQrGenerated", false)
    val isQrGenerated: StateFlow<Boolean> = _isQrGenerated

    private val _errorMessage = savedStateHandle.getStateFlow<String?>("errorMessage", null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Text QR functions
    fun setTextContent(content: String) {
        savedStateHandle["textContent"] = content
        savedStateHandle["isQrGenerated"] = false
    }

    // URL QR functions
    fun setUrlContent(url: String) {
        savedStateHandle["urlContent"] = url
        savedStateHandle["isQrGenerated"] = false
    }

    // Email QR functions
    fun setEmailAddress(email: String) {
        savedStateHandle["emailAddress"] = email
        savedStateHandle["isQrGenerated"] = false
    }

    fun setEmailSubject(subject: String) {
        savedStateHandle["emailSubject"] = subject
        savedStateHandle["isQrGenerated"] = false
    }

    fun setEmailBody(body: String) {
        savedStateHandle["emailBody"] = body
        savedStateHandle["isQrGenerated"] = false
    }

    // SMS QR functions
    fun setPhoneNumber(phone: String) {
        savedStateHandle["phoneNumber"] = phone
        savedStateHandle["isQrGenerated"] = false
    }

    fun setSmsMessage(message: String) {
        savedStateHandle["smsMessage"] = message
        savedStateHandle["isQrGenerated"] = false
    }

    // WiFi QR functions
    fun setWifiSSID(ssid: String) {
        savedStateHandle["wifiSSID"] = ssid
        savedStateHandle["isQrGenerated"] = false
    }

    fun setWifiPassword(password: String) {
        savedStateHandle["wifiPassword"] = password
        savedStateHandle["isQrGenerated"] = false
    }

    fun setWifiEncryption(encryption: String) {
        savedStateHandle["wifiEncryption"] = encryption
        savedStateHandle["isQrGenerated"] = false
    }

    // Twitter QR functions
    fun setTwitterUsername(username: String) {
        savedStateHandle["twitterUsername"] = username
        savedStateHandle["isQrGenerated"] = false
    }

    fun setTweetText(tweet: String) {
        savedStateHandle["tweetText"] = tweet
        savedStateHandle["isQrGenerated"] = false
    }

    // Generate QR code based on type
    fun generateQrCode(type: String): Boolean {
        savedStateHandle["errorMessage"] = null
        
        when (type) {
            "TEXT" -> {
                if (_textContent.value.isBlank()) {
                    savedStateHandle["errorMessage"] = "Text content cannot be empty"
                    return false
                }
            }
            "URL" -> {
                if (_urlContent.value.isBlank()) {
                    savedStateHandle["errorMessage"] = "URL cannot be empty"
                    return false
                }
            }
            "EMAIL" -> {
                if (_emailAddress.value.isBlank()) {
                    savedStateHandle["errorMessage"] = "Email address cannot be empty"
                    return false
                }
            }
            "SMS" -> {
                if (_phoneNumber.value.isBlank()) {
                    savedStateHandle["errorMessage"] = "Phone number cannot be empty"
                    return false
                }
            }
            "WIFI" -> {
                if (_wifiSSID.value.isBlank()) {
                    savedStateHandle["errorMessage"] = "WiFi SSID cannot be empty"
                    return false
                }
            }
            "TWITTER" -> {
                if (_twitterUsername.value.isBlank()) {
                    savedStateHandle["errorMessage"] = "Twitter username cannot be empty"
                    return false
                }
            }
        }

        savedStateHandle["isQrGenerated"] = true
        return true
    }

}