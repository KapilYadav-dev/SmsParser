package `in`.mrkaydev.banksmsparser.models


import com.google.gson.annotations.SerializedName

data class SmsList(
    @SerializedName("sms")
    var smsList: List<SmsData> = listOf()
)