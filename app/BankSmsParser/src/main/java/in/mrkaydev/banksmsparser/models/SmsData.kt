package `in`.mrkaydev.banksmsparser.models


import com.google.gson.annotations.SerializedName

data class SmsData(
    @SerializedName("body")
    var body: String = "",
    @SerializedName("sender")
    var sender: String = "",
    @SerializedName("amount")
    var amount: String = "",
    @SerializedName("transactionType")
    var transactionType: String = "",
    @SerializedName("parsed")
    var parsed: String = "",
    @SerializedName("cardType")
    var cardType: String = "",
    @SerializedName("accountNumber")
    var accountNumber: String = "",
    @SerializedName("refNumber")
    var refNumber: String = "",
    @SerializedName("payName")
    var payiName: String = "",
    @SerializedName("avlBal")
    var avlBal: String = "",
    @SerializedName("date")
    var date: String = ""


)