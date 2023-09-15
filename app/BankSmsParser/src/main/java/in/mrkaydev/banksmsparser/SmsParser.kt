package `in`.mrkaydev.banksmsparser

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.provider.Telephony
import androidx.core.content.ContextCompat
import `in`.mrkaydev.banksmsparser.models.SmsData
import `in`.mrkaydev.banksmsparser.models.SmsList
import java.util.Locale
import java.util.regex.Pattern

class SmsParser(private val context: Context) {

    init {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            throw Exception("Please give read sms permission to this sweet library.")
        }
    }

    fun getParsedSmsList(): List<SmsData>  {
        val parsedSmsList = mutableListOf<SmsData>()
        val unParsedSmsList = getAllSms()
        val list = unParsedSmsList[0].smsList.map { sms ->
            parseSmsData(sms)
        }

        for (result in list) {
            result?.let { parsedSmsList.add(it) }
        }
        return parsedSmsList
    }

    private fun getAllSms(): ArrayList<SmsList> {
        val sms = ArrayList<SmsList>()
        val cr = context.contentResolver
        val c: Cursor? = cr.query(
            Telephony.Sms.Inbox.CONTENT_URI,
            arrayOf(
                Telephony.Sms.Inbox.SUBSCRIPTION_ID,
                Telephony.Sms.Inbox.ADDRESS,
                Telephony.Sms.Inbox.DATE,
                Telephony.Sms.Inbox.BODY
            ),
            null, null, Telephony.Sms.Inbox.DEFAULT_SORT_ORDER
        )
        val totalSMS: Int = c?.count!!
        if (c.moveToFirst()) {
            val list = ArrayList<SmsData>()
            for (i in 0 until totalSMS) {
                val address = c.getString(1)
                val date = c.getLong(2)
                val body = c.getString(3)
                list.add(SmsData(body = body, sender = address, date = date.toString()))
                c.moveToNext()
            }
            sms.add(SmsList(list))
        } else {
            throw RuntimeException("You have no SMS in Inbox")
        }
        c.close()
        return sms
    }

    private fun findCreditCardOrDebitCard(msg: String, sender: String): String {
        if (sender.trim().contains("+918586980859", true)
            || sender.contains("08586980869", true)
            || sender.contains("085869", true)
            || sender.contains("ICICIB", true)
            || sender.contains("HDFCBK", true)
            || sender.contains("SBMSMS", true)
            || sender.contains("SBIINB", true)
            || sender.contains("SCISMS", true)
            || sender.contains("CBSSBI", true)
            || sender.contains("SBIPSG", true)
            || sender.contains("SBIUPI", true)
            || sender.contains("SBICRD", true)
            || sender.contains("ATMSBI", true)
            || sender.contains("QPMYAMEX", true)
            || sender.contains("IDFCFB", true)
            || sender.contains("UCOBNK", true)
            || sender.contains("CANBNK", true)
            || sender.contains("BOIIND", true)
            || sender.contains("AXISBK", true)
            || sender.contains("PAYTMB", true)
            || sender.contains("UnionB", true)
            || sender.contains("INDBNK", true)
            || sender.contains("KOTAKB", true)
            || sender.contains("CENTBK", true)
            || sender.contains("SCBANK", true)
            || sender.contains("PNBSMS", true)
            || sender.contains("DOPBNK", true)
            || sender.contains("YESBNK", true)
            || sender.contains("IDBIBK", true)
            || sender.contains("ALBANK", true)
            || sender.contains("CITIBK", true)
            || sender.contains("ANDBNK", true)
            || sender.contains("BOBTXN", true)
            || sender.contains("IOBCHN", true)
            || sender.contains("MAHABK", true)
            || sender.contains("OBCBNK", true)
            || sender.contains("RBLBNK", true)
            || sender.contains("RBLCRD", true)
            || sender.contains("SPRCRD", true)
            || sender.contains("HSBCBK", true)
            || sender.contains("HSBCIN", true)
            || sender.contains("INDUSB", true)
        ) {
            return if (msg.contains("CREDIT CARD", ignoreCase = true) ||
                msg.contains("SBICARD", ignoreCase = true)
            ) {
                "credit card"
            } else {
                "debit card"
            }
        }
        return ""

    }

    private fun checkSenderIsValid(sender: String): Boolean {
        return (sender.trim().contains("+918586980859", true)
                || sender.contains("08586980869", true)
                || sender.contains("085869", true)
                || sender.contains("ICICIB", true)
                || sender.contains("HDFCBK", true)
                || sender.contains("SBIINB", true)
                || sender.contains("SBMSMS", true)
                || sender.contains("SCISMS", true)
                || sender.contains("CBSSBI", true)
                || sender.contains("SBIPSG", true)
                || sender.contains("SBIUPI", true)
                || sender.contains("SBICRD", true)
                || sender.contains("ATMSBI", true)
                || sender.contains("QPMYAMEX", true)
                || sender.contains("IDFCFB", true)
                || sender.contains("UCOBNK", true)
                || sender.contains("CANBNK", true)
                || sender.contains("BOIIND", true)
                || sender.contains("AXISBK", true)
                || sender.contains("PAYTMB", true)
                || sender.contains("UnionB", true)
                || sender.contains("INDBNK", true)
                || sender.contains("KOTAKB", true)
                || sender.contains("CENTBK", true)
                || sender.contains("SCBANK", true)
                || sender.contains("PNBSMS", true)
                || sender.contains("DOPBNK", true)
                || sender.contains("YESBNK", true)
                || sender.contains("IDBIBK", true)
                || sender.contains("ALBANK", true)
                || sender.contains("CITIBK", true)
                || sender.contains("ANDBNK", true)
                || sender.contains("BOBTXN", true)
                || sender.contains("IOBCHN", true)
                || sender.contains("MAHABK", true)
                || sender.contains("OBCBNK", true)
                || sender.contains("RBLBNK", true)
                || sender.contains("RBLCRD", true)
                || sender.contains("SPRCRD", true)
                || sender.contains("HSBCBK", true)
                || sender.contains("HSBCIN", true)
                || sender.contains("INDUSB", true))
    }

    private fun getFirstWord(text: String): String {
        val index = text.indexOf(' ')
        return if (index > -1) { // Check if there is more than one word.
            text.substring(0, index).trim { it <= ' ' } // Extract first word.
        } else {
            text// Text is the first word itself.
        }
    }

    private fun parseSmsData(smsDto: SmsData): SmsData? {
        val regEx = Pattern.compile("(?i)(?:RS|INR|MRP)?(?:(?:RS|INR|MRP)\\.?\\s?)(\\d+(:?\\,\\d+)?(\\,\\d+)?(\\.\\d{1,2})?)+")
        // Find instance of pattern matches
        val m = regEx.matcher(smsDto.body)
        if (m.find()) {
            try {
                if (checkSenderIsValid(smsDto.sender)) {
                    if (!smsDto.body.contains("stmt", true)) {
                        // found out debit and credit
                        if (smsDto.body.contains("withdrawn", true)
                            || smsDto.body.contains("debited", true)
                            || smsDto.body.contains("spent", true)
                            || smsDto.body.contains("paying", true)
                            || smsDto.body.contains("payment", true)
                            || smsDto.body.contains("deducted", true)
                            || smsDto.body.contains("debited", true)
                            || smsDto.body.contains("purchase", true)
                            || smsDto.body.contains("dr", true)
                            && !smsDto.body.contains("otp", true)
                            || smsDto.body.contains("txn", true)
                            || smsDto.body.contains("transfer", true)
                            && !smsDto.body.contains("We are pleased to inform that", true)
                            && !smsDto.body.contains("has been opened", true)
                        ) {
                            var amount = m.group(0).replace("inr".toRegex(), "")
                            amount = amount.replace("rs".toRegex(), "")
                            amount = amount.replace("inr".toRegex(), "")
                            amount = amount.replace(" ".toRegex(), "")
                            amount = amount.replace(",".toRegex(), "")
                            smsDto.amount = amount
                            smsDto.transactionType = "debited"
                        } else if (smsDto.body.contains("credited", true)
                            || smsDto.body.contains("cr", true)
                            || smsDto.body.contains("deposited", true)
                            || smsDto.body.contains("deposit", true)
                            || smsDto.body.contains("received", true)
                            && !smsDto.body.contains("otp", true)
                            && !smsDto.body.contains("emi", true)
                        ) {
                            var amount = m.group(0).replace("inr".toRegex(), "")
                            amount = amount.replace("rs".toRegex(), "")
                            amount = amount.replace("inr".toRegex(), "")
                            amount = amount.replace(" ".toRegex(), "")
                            amount = amount.replace(",".toRegex(), "")
                            smsDto.amount = amount
                            when {
                                smsDto.body.contains("UPDATE:AVAILABLE Bal in", true) -> {
                                    smsDto.transactionType = "balance"
                                    smsDto.avlBal = smsDto.amount
                                }

                                smsDto.body.contains("UPDATE: AVAILABLE Bal in", true) -> {
                                    smsDto.transactionType = "balance"
                                    smsDto.avlBal = smsDto.amount
                                }

                                else -> {
                                    smsDto.transactionType = "credited"
                                }
                            }

                        }
                        smsDto.parsed = "1"
                        when {
                            smsDto.body.contains("SBIDrCARD", true)
                                    && smsDto.body.contains("tx#", true) -> {
                                val dataList = smsDto.body.lowercase(Locale.getDefault()).split("sbidrcard ")
                                val data: String = if (dataList.size > 1) {
                                    dataList[1].trim()
                                } else {
                                    dataList[0]
                                }
                                smsDto.accountNumber = getFirstWord(data)
                            }

                            smsDto.body.contains("Customer ID ", true) -> {
                                val dataList = smsDto.body.lowercase(Locale.getDefault()).split("customer id ")
                                val data: String = if (dataList.size > 1) {
                                    dataList[1]
                                } else {
                                    dataList[0]
                                }
                                smsDto.accountNumber = getFirstWord(data)
                            }

                            smsDto.body.contains("Deposit No ", true) -> {
                                val dataList = smsDto.body.lowercase(Locale.getDefault()).split("deposit no ")
                                val data = if (dataList.size > 1) {
                                    dataList[1]
                                } else {
                                    dataList[0]
                                }
                                smsDto.accountNumber = getFirstWord(data)
                            }

                            smsDto.body.contains("credit card ending", true) -> {
                                val dataList = smsDto.body.split("ending ")
                                val data = if (dataList.size > 1) {
                                    dataList[1]
                                } else {
                                    dataList[0]
                                }
                                smsDto.accountNumber = getFirstWord(data)
                            }

                            smsDto.body.contains("UPI", true) -> {
                                val dataList = smsDto.body.split("frm")
                                val p1 =
                                    Pattern.compile("[0-9]*[Xx\\*]*[0-9]*[Xx\\*]+[0-9]{3,}")
                                var data = ""
                                if (dataList.size == 2) {
                                    val m1 = p1.matcher(dataList[1])
                                    while (m1.find()) {
                                        data = m1.group()
                                        break
                                    }
                                } else {
                                    val m1 = p1.matcher(dataList[0])
                                    while (m1.find()) {
                                        data = m1.group()
                                        break
                                    }
                                }
                                smsDto.accountNumber = data
                            }

                            smsDto.body.contains("a/c", true) -> {
                                if (smsDto.body.contains("no.", true)) {
                                    val dataList = smsDto.body.split("no.")
                                    val p1 =
                                        Pattern.compile("[0-9]*[Xx\\*]*[0-9]*[Xx\\*]+[0-9]{3,}")
                                    var data = ""
                                    if (dataList.size == 2) {
                                        val m1 = p1.matcher(dataList[1])
                                        while (m1.find()) {
                                            data = m1.group()
                                            break
                                        }
                                    } else {
                                        val m1 = p1.matcher(dataList[0])
                                        while (m1.find()) {
                                            data = m1.group()
                                            break
                                        }
                                    }
                                    smsDto.accountNumber = data
                                } else if (smsDto.body.contains("A/c No", true)) {
                                    if (smsDto.body.contains("XX", true)) {
                                        val dataList = smsDto.body.split("A/c No")
                                        val p1 =
                                            Pattern.compile("[0-9]*[Xx\\*]*[0-9]*[Xx\\*]+[0-9]{3,}")
                                        var data = ""
                                        if (dataList.size == 2) {
                                            val m1 = p1.matcher(dataList[1])
                                            while (m1.find()) {
                                                data = m1.group()
                                                break
                                            }
                                        } else {
                                            val m1 = p1.matcher(dataList[0])
                                            while (m1.find()) {
                                                data = m1.group()
                                                break
                                            }
                                        }
                                        smsDto.accountNumber = data
                                    } else {
                                        val dataList = smsDto.body.split("a/c no ")
                                        var data = ""
                                        data = if (dataList.size > 1) {
                                            dataList[1]
                                        } else {
                                            dataList[0]
                                        }
                                        smsDto.accountNumber = data.lowercase(Locale.getDefault()).split("as")[0]
                                    }

                                } else if (smsDto.body.contains("a/c no ", true)) {
                                    val dataList = smsDto.body.split("a/c no ")
                                    val data = if (dataList.size > 1) {
                                        dataList[1]
                                    } else {
                                        dataList[0]
                                    }
                                    smsDto.accountNumber = data.lowercase(Locale.getDefault()).split("as")[0]

                                } else if (smsDto.body.contains("a/c", true)) {
                                    if (!smsDto.body.contains("xx", true)
                                        && !smsDto.body.contains("x", true)
                                    ) {

                                        val dataList = smsDto.body.lowercase(Locale.getDefault()).split("a/c ")
                                        val data = if (dataList.size > 1) {
                                            dataList[1]
                                        } else {
                                            dataList[0]
                                        }
                                        if (data.contains("as")) {
                                            smsDto.accountNumber =
                                                data.lowercase(Locale.getDefault()).split("as")[0]
                                        } else {
                                            smsDto.accountNumber = getFirstWord(
                                                data.lowercase(Locale.getDefault()).split("\\s")[0]
                                            ).filter { it.isDigit() }
                                        }
                                    } else {
                                        val dataList = smsDto.body.lowercase(Locale.getDefault()).split("a/c ")
                                        val p1 =
                                            Pattern.compile("[0-9]*[Xx\\*]*[0-9]*[Xx\\*]+[0-9]{3,}")
                                        var data = ""
                                        if (dataList.size > 1) {
                                            val m1 = p1.matcher(dataList[1])
                                            while (m1.find()) {
                                                data = m1.group()
                                                break
                                            }
                                        } else {
                                            val m1 = p1.matcher(dataList[0])
                                            while (m1.find()) {
                                                data = m1.group()
                                                break
                                            }
                                        }
                                        smsDto.accountNumber = data
                                    }

                                }
                            }

                            smsDto.body.contains("Acct", true) -> {
                                var dataList = smsDto.body.split("Acct ")
                                val p1 =
                                    Pattern.compile("[0-9]*[Xx\\*]*[0-9]*[Xx\\*]+[0-9]{3,}")
                                var data = ""
                                if (dataList.size > 1) {
                                    val m1 = p1.matcher(dataList[1])
                                    while (m1.find()) {
                                        data = m1.group()
                                        break
                                    }
                                } else {
                                    val m1 = p1.matcher(dataList[0])
                                    while (m1.find()) {
                                        data = m1.group()
                                        break
                                    }
                                }
                                smsDto.accountNumber = data
                            }

                            smsDto.body.contains("Card ending", true) -> {
                                if (smsDto.body.contains("ending", true)) {
                                    if (smsDto.body.contains("XX", true)) {
                                        var dataList =
                                            smsDto.body.lowercase(Locale.getDefault()).split("ending ")
                                        val p1 =
                                            Pattern.compile("[0-9]*[Xx\\*]*[0-9]*[Xx\\*]+[0-9]{3,}")
                                        var data = ""
                                        if (dataList.size > 1) {
                                            val m1 = p1.matcher(dataList[1])
                                            while (m1.find()) {
                                                data = m1.group()
                                                break
                                            }
                                        } else {
                                            val m1 = p1.matcher(dataList[0])
                                            while (m1.find()) {
                                                data = m1.group()
                                                break
                                            }
                                        }
                                        smsDto.accountNumber = data
                                    } else {
                                        var dataList =
                                            smsDto.body.lowercase(Locale.getDefault()).split("ending ")
                                        var data = dataList[1].trim()
                                        smsDto.accountNumber = getFirstWord(data)
                                    }

                                } else if (smsDto.body.contains("end", true)) {
                                    var dataList = smsDto.body.lowercase(Locale.getDefault()).split("end")
                                    val p1 =
                                        Pattern.compile("[0-9]*[Xx\\*]*[0-9]*[Xx\\*]+[0-9]{3,}")
                                    var data = ""
                                    if (dataList.size == 2) {
                                        val m1 = p1.matcher(dataList[1])
                                        while (m1.find()) {
                                            data = m1.group()
                                            break
                                        }
                                    } else {
                                        val m1 = p1.matcher(dataList[0])
                                        while (m1.find()) {
                                            data = m1.group()
                                            break
                                        }
                                    }
                                    smsDto.accountNumber = data
                                }

                            }

                            smsDto.body.contains("account", true) -> {
                                var dataList = smsDto.body.lowercase(Locale.getDefault()).split("account ")
                                val p1 =
                                    Pattern.compile("[0-9]*[Xx\\*]*[0-9]*[Xx\\*]+[0-9]{3,}")
                                var data = ""
                                if (dataList.size > 1) {
                                    val m1 = p1.matcher(dataList[1])
                                    while (m1.find()) {
                                        data = m1.group()
                                        break
                                    }
                                } else {
                                    val m1 = p1.matcher(dataList[0])
                                    while (m1.find()) {
                                        data = m1.group()
                                        break
                                    }
                                }
                                smsDto.accountNumber = data
                            }

                            smsDto.body.contains("Card", true) -> {
                                var dataList = smsDto.body.lowercase(Locale.getDefault()).split("card")
                                val p1 =
                                    Pattern.compile("[0-9]*[Xx\\*]*[0-9]*[Xx\\*]+[0-9]{3,}")
                                var data = ""
                                if (dataList.size == 2) {
                                    val m1 = p1.matcher(dataList[1])
                                    while (m1.find()) {
                                        data = m1.group()
                                        break
                                    }
                                } else {
                                    val m1 = p1.matcher(dataList[0])
                                    while (m1.find()) {
                                        data = m1.group()
                                        break
                                    }
                                }
                                smsDto.accountNumber = data
                            }

                            smsDto.body.contains("Ac", true) -> {
                                var dataList = smsDto.body.lowercase(Locale.getDefault()).split("ac ")
                                val p1 =
                                    Pattern.compile("[0-9]*[Xx\\*]*[0-9]*[Xx\\*]+[0-9]{3,}")
                                var data = ""
                                if (dataList.size == 2) {
                                    val m1 = p1.matcher(dataList[1])
                                    while (m1.find()) {
                                        data = m1.group()
                                        break
                                    }
                                } else {
                                    val m1 = p1.matcher(dataList[0])
                                    while (m1.find()) {
                                        data = m1.group()
                                        break
                                    }
                                }
                                smsDto.accountNumber = data
                            }

                        }
                        // check message is otp or not
                        if (!smsDto.body.contains("OTP", true)
                            && !smsDto.body.contains("minimum", true)
                            && !smsDto.body.contains("importance", true)
                            && !smsDto.body.contains("request", true)
                            && !smsDto.body.contains("limit", true)
                            && !smsDto.body.contains("convert", true)
                            && !smsDto.body.contains("emi", true)
                            && !smsDto.body.contains("avoid paying", true)
                            && !smsDto.body.contains("autopay", true)
                            && !smsDto.body.contains("E-statement", true)
                            && !smsDto.body.contains("funds are blocked", true)
                            && !smsDto.body.contains("SmartPay", true)
                            && !smsDto.body.contains("We are pleased to inform that", true)
                            && !smsDto.body.contains("has been opened", true)
                            && !smsDto.transactionType.trim().isNullOrEmpty()
                        ) {
                            // bank wise filter
                            getAvailableBalance(smsDto)
                            smsDto.refNumber = getRefNumber(smsDto.body)
                            var cardType =
                                findCreditCardOrDebitCard(smsDto.body, smsDto.sender)
                            smsDto.cardType = cardType
                            return smsDto
                        } else {
                            return null
                        }

                    } else {
                        return null
                    }
                } else {
                    return null
                }

            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                return null
            }
        } else {
            return null
        }
    }

    private fun getAvailableBalance(smsDto: SmsData) {
        val regEx =
            Pattern.compile("(?i)(?:RS|INR|MRP)?(?:(?:RS|INR|MRP)\\.?\\s?)(\\d+(:?\\,\\d+)?(\\,\\d+)?(\\.\\d{1,2})?)+")
        // Find instance of pattern matches
        if (smsDto.body.contains("curr o/s - ", true)) {
            var newBody = smsDto.body.split("o/s - ")
            val m = regEx.matcher(newBody[1].trim())
            if (m.find()) {
                var amount = m.group(0).replace("inr".toRegex(), "")
                amount = amount.replace("rs".toRegex(), "")
                amount = amount.replace("inr".toRegex(), "")
                amount = amount.replace(" ".toRegex(), "")
                amount = amount.replace(",".toRegex(), "")
                smsDto.avlBal = amount
            }
        } else if (smsDto.body.contains("The Balance is", true)) {
            var newBody = smsDto.body.split("The Balance is ")
            val m = regEx.matcher(newBody[1].trim())
            if (m.find()) {
                var amount = m.group(0).replace("inr".toRegex(), "")
                amount = amount.replace("rs".toRegex(), "")
                amount = amount.replace("inr".toRegex(), "")
                amount = amount.replace(" ".toRegex(), "")
                amount = amount.replace(",".toRegex(), "")
                smsDto.avlBal = amount
            }
        } else if (smsDto.body.contains("The Available Balance is", true)) {
            var newBody = smsDto.body.split("The Available Balance is ")
            val m = regEx.matcher(newBody[1].trim())
            if (m.find()) {
                var amount = m.group(0).replace("inr".toRegex(), "")
                amount = amount.replace("rs".toRegex(), "")
                amount = amount.replace("inr".toRegex(), "")
                amount = amount.replace(" ".toRegex(), "")
                amount = amount.replace(",".toRegex(), "")
                smsDto.avlBal = amount
            }
        } else if (smsDto.body.contains("Avbl Lmt:", true)) {
            var newBody = smsDto.body.split("Avbl Lmt:")
            val m = regEx.matcher(newBody[1].trim())
            if (m.find()) {
                var amount = m.group(0).replace("inr".toRegex(), "")
                amount = amount.replace("rs".toRegex(), "")
                amount = amount.replace("inr".toRegex(), "")
                amount = amount.replace(" ".toRegex(), "")
                amount = amount.replace(",".toRegex(), "")
                smsDto.avlBal = amount
            }
        } else if (smsDto.body.contains("Avlbal", true)) {
            var newBody = smsDto.body.split("Avlbal")
            val m = regEx.matcher(newBody[1].trim())
            if (m.find()) {
                var amount = m.group(0).replace("inr".toRegex(), "")
                amount = amount.replace("rs".toRegex(), "")
                amount = amount.replace("inr".toRegex(), "")
                amount = amount.replace(" ".toRegex(), "")
                amount = amount.replace(",".toRegex(), "")
                smsDto.avlBal = amount
            }
        } else if (smsDto.body.contains("balance is", true)) {
            var newBody = smsDto.body.lowercase(Locale.getDefault()).split("balance is ")
            val m = regEx.matcher(newBody[1].trim())
            if (m.find()) {
                var amount = m.group(0).replace("inr".toRegex(), "")
                amount = amount.replace("rs".toRegex(), "")
                amount = amount.replace("inr".toRegex(), "")
                amount = amount.replace(" ".toRegex(), "")
                amount = amount.replace(",".toRegex(), "")
                smsDto.avlBal = amount
            }
        } else if (smsDto.body.contains("AvBl Bal:", true)) {
            var newBody = smsDto.body.lowercase(Locale.getDefault()).split("avbl bal: ")
            val m = regEx.matcher(newBody[1].trim())
            if (m.find()) {
                var amount = m.group(0).replace("inr".toRegex(), "")
                amount = amount.replace("rs".toRegex(), "")
                amount = amount.replace("inr".toRegex(), "")
                amount = amount.replace(" ".toRegex(), "")
                amount = amount.replace(",".toRegex(), "")
                smsDto.avlBal = amount
            }
        } else if (smsDto.body.contains("Avl. Bal:", true)) {
            var newBody = smsDto.body.lowercase(Locale.getDefault()).split("avl. bal:")
            val m = regEx.matcher(newBody[1].trim())
            if (m.find()) {
                var amount = m.group(0).replace("inr".toRegex(), "")
                amount = amount.replace("rs".toRegex(), "")
                amount = amount.replace("inr".toRegex(), "")
                amount = amount.replace(" ".toRegex(), "")
                amount = amount.replace(",".toRegex(), "")
                smsDto.avlBal = amount
            }
        } else if (smsDto.body.contains("AVl BAL", true)) {
            if (smsDto.body.contains("Avl. Bal:", true)) {
                var newBody = smsDto.body.lowercase(Locale.getDefault()).split("avl. bal:")
                val m = regEx.matcher(newBody[1].trim())
                if (m.find()) {
                    var amount = m.group(0).replace("inr".toRegex(), "")
                    amount = amount.replace("rs".toRegex(), "")
                    amount = amount.replace("inr".toRegex(), "")
                    amount = amount.replace(" ".toRegex(), "")
                    amount = amount.replace(",".toRegex(), "")
                    smsDto.avlBal = amount
                }
            } else {
                var newBody = smsDto.body.lowercase(Locale.getDefault()).split("avl bal ")
                val m = regEx.matcher(newBody[1].trim())
                if (m.find()) {
                    var amount = m.group(0).replace("inr".toRegex(), "")
                    amount = amount.replace("rs".toRegex(), "")
                    amount = amount.replace("inr".toRegex(), "")
                    amount = amount.replace(" ".toRegex(), "")
                    amount = amount.replace(",".toRegex(), "")
                    smsDto.avlBal = amount
                }
            }
        } else if (smsDto.body.contains("Avail Bal", true)) {
            var newBody = smsDto.body.lowercase(Locale.getDefault()).split("avail bal ")
            val m = regEx.matcher(newBody[1].trim().replace("\\s".toRegex(), ""))
            if (m.find()) {
                var amount = m.group(0).replace("inr".toRegex(), "")
                amount = amount.replace("rs".toRegex(), "")
                amount = amount.replace("inr".toRegex(), "")
                amount = amount.replace(" ".toRegex(), "")
                amount = amount.replace(",".toRegex(), "")
                smsDto.avlBal = amount
            }
        } else if (smsDto.body.contains("The combine BAL is", true)) {
            var newBody = smsDto.body.lowercase(Locale.getDefault()).split("bal is ")
            val m = regEx.matcher(newBody[1].trim())
            if (m.find()) {
                var amount = m.group(0).replace("inr".toRegex(), "")
                amount = amount.replace("rs".toRegex(), "")
                amount = amount.replace("inr".toRegex(), "")
                amount = amount.replace(" ".toRegex(), "")
                amount = amount.replace(",".toRegex(), "")
                smsDto.avlBal = amount
            }
        } else if (smsDto.body.contains("The balance in", true)) {
            var newBody = smsDto.body.lowercase(Locale.getDefault()).split("balance in ")
            val m = regEx.matcher(newBody[1].trim())
            if (m.find()) {
                var amount = m.group(0).replace("inr".toRegex(), "")
                amount = amount.replace("rs".toRegex(), "")
                amount = amount.replace("inr".toRegex(), "")
                amount = amount.replace(" ".toRegex(), "")
                amount = amount.replace(",".toRegex(), "")
                smsDto.avlBal = "N/A"
                smsDto.amount = amount
                smsDto.transactionType = "balance"
            }
        } else if (smsDto.body.contains("Available balance:", true)) {
            var newBody = smsDto.body.lowercase(Locale.getDefault()).split("available balance:")
            val m = regEx.matcher(newBody[1].trim())
            if (m.find()) {
                var amount = m.group(0).replace("inr".toRegex(), "")
                amount = amount.replace("rs".toRegex(), "")
                amount = amount.replace("inr".toRegex(), "")
                amount = amount.replace(" ".toRegex(), "")
                amount = amount.replace(",".toRegex(), "")
                if (smsDto.body.contains("credited", true)
                    || smsDto.body.contains("cash deposit", true)
                ) {
                    smsDto.transactionType = "credited"
                    smsDto.avlBal = amount
                } else if (smsDto.body.contains("debited", true)
                    || smsDto.body.contains("withdrawn", true)
                ) {
                    smsDto.transactionType = "debited"
                    smsDto.avlBal = amount
                } else {
                    smsDto.transactionType = "balance"
                    smsDto.avlBal = "N/A"
                    smsDto.amount = amount
                }
            }
        } else {
            smsDto.avlBal = "N/A"
        }

    }

    private fun getRefNumber(body: String): String {
        //Info, At, Linked to, NEFT, Ref, transfer from, transfer to, for, of, IMPS
        //Till dot Space, on, has
        var refNumber = ""
        if (body.contains("NetBanking", true)) {
            // refNumber = " NetBanking"
            if (body.lowercase(Locale.getDefault()).contains(" to ", true)) {
                var dataList = body.lowercase(Locale.getDefault()).split(" to ")
                var data = ""
                data = if (dataList.size > 1) {
                    dataList[1]
                } else {
                    dataList[0]
                }
                refNumber = when {
                    data.contains(". ", true) -> {
                        data.split(". ")[0]
                    }

                    data.contains(" on ", true) -> {
                        data.split(" on ")[0]
                    }

                    data.contains(")", true) -> {
                        data.split(")")[0]
                    }

                    else -> {
                        //data
                        "NetBanking"
                    }
                }
            } else if (body.lowercase(Locale.getDefault()).contains(" for ", true)) {
                var dataList = body.lowercase(Locale.getDefault()).split(" for ")
                var data = ""
                data = if (dataList.size > 1) {
                    dataList[1]
                } else {
                    dataList[0]
                }
                refNumber = when {
                    data.contains(". ", true) -> {
                        data.split(". ")[0]
                    }

                    data.contains(" on ", true) -> {
                        data.split(" on ")[0]
                    }

                    data.contains(")", true) -> {
                        data.split(")")[0]
                    }

                    else -> {
                        //data
                        "NetBanking"
                    }
                }
            }

        } else if (body.contains("Cash Deposit", true)) {
            refNumber = "Cash Deposit"
        } else if (body.contains("withdrawn", true)) {
            var dataList = body.lowercase(Locale.getDefault()).split(" at ")
            var data = ""
            data = if (dataList.size > 1) {
                dataList[1]
            } else {
                dataList[0]
            }
            refNumber = "withdrawn at " + when {
                data.contains("on", true) -> {
                    data.split(" on")[0]
                }

                data.contains(".", true) -> {
                    data.split(". ")[0]
                }

                data.contains(")", true) -> {
                    data.split(")")[0]
                }

                else -> {
                    data
                }
            }
        } else if (body.contains("towards", true)) {
            var dataList = body.lowercase(Locale.getDefault()).split("towards ")
            var data = ""
            data = if (dataList.size > 1) {
                dataList[1]
            } else {
                dataList[0]
            }
            refNumber = when {
                data.contains(" avl ", true) -> {
                    data.split(" avl ")[0]
                }

                data.contains(". ", true) -> {
                    data.split(". ")[0]
                }

                data.contains("on", true) -> {
                    data.split(" on")[0]
                }

                data.contains(")", true) -> {
                    data.split(")")[0]
                }

                else -> {
                    data
                }
            }

        } else if (body.contains("thru", true)) {
            if (!body.contains("thru clg", true)) {
                var dataList = body.lowercase(Locale.getDefault()).split("thru ")
                var data = ""
                data = if (dataList.size > 1) {
                    dataList[1]
                } else {
                    dataList[0]
                }
                refNumber = when {
                    data.contains(". ", true) -> {
                        data.split(".")[0]
                    }

                    data.contains("on", true) -> {
                        data.split(" on")[0]
                    }

                    data.contains(")", true) -> {
                        data.split(")")[0]
                    }

                    else -> {
                        data
                    }
                }
            }

        } else if (body.contains("Credit card ending", true)) {
            if (body.contains("has been", true) && body.contains("from", true)) {
                var dataList = body.lowercase(Locale.getDefault()).split("from ")
                var data = ""
                data = if (dataList.size > 1) {
                    dataList[1]
                } else {
                    dataList[0]
                }
                refNumber = when {
                    data.contains(" on", true) -> {
                        data.split(" on")[0]
                    }

                    data.contains(". ", true) -> {
                        data.split(". ")[0]
                    }

                    data.contains(")", true) -> {
                        data.split(")")[0]
                    }

                    else -> {
                        data
                    }
                }
            } else if (body.contains("has been", true)) {
                var dataList = body.lowercase(Locale.getDefault()).split("has been ")
                var data = ""
                data = if (dataList.size > 1) {
                    dataList[1]
                } else {
                    dataList[0]
                }
                refNumber = when {
                    data.contains("on", true) -> {
                        data.split(" on")[0]
                    }

                    data.contains(")", true) -> {
                        data.split(")")[0]
                    }

                    data.contains(". ", true) -> {
                        data.split(".")[0]
                    }

                    else -> {
                        data
                    }
                }
            } else {
                var dataList = body.lowercase(Locale.getDefault()).split("from ")
                var data = ""
                data = if (dataList.size > 1) {
                    dataList[1]
                } else {
                    dataList[0]
                }
                refNumber = when {
                    data.contains("on", true) -> {
                        data.split(" on")[0]
                    }

                    data.contains(")", true) -> {
                        data.split(")")[0]
                    }

                    data.contains(". ", true) -> {
                        data.split(".")[0]
                    }

                    else -> {
                        data
                    }
                }
            }

        } else if (body.contains("NEFT", true)) {
            var dataList = body.lowercase(Locale.getDefault()).split("neft")
            //val p1 = Pattern.compile("([0-9]+).*")
            var data = ""
            if (dataList.size > 1) {
                data = dataList[1].trim()
            } else {
                data = dataList[0].trim()
            }
            refNumber = "NEFT " + when {
                data.contains(")", true) -> {
                    data.split(")")[0]
                }

                data.contains(".", true) -> {
                    data.split(". ")[0]
                }

                data.contains("-", true) -> {
                    data.split("-")[0]
                }

                else -> {
                    data
                }
            }
        } else if (body.contains("IMPS", true)) {
            if (body.contains("Ref no")) {
                var dataList = body.split("Ref no")
                val p1 = Pattern.compile("([0-9]+).*")
                var data = ""
                if (dataList.size > 1) {
                    val m1 = p1.matcher(dataList[1])
                    while (m1.find()) {
                        data = m1.group()
                        break
                    }
                } else {
                    val m1 = p1.matcher(dataList[0])
                    while (m1.find()) {
                        data = m1.group()
                        break
                    }
                }
                when {
                    data.contains(")", true) -> {
                        refNumber = "IMPS Ref no" + data.split(")")[0]
                    }

                    data.contains(".", true) -> {
                        refNumber = "IMPS Ref no" + data.split(".")[0]
                    }

                    else -> {
                        refNumber = data
                    }
                }
            } else {
                var dataList = body.split("IMPS")
                val p1 = Pattern.compile("([0-9]+).*")
                var data = ""
                if (dataList.size > 1) {
                    val m1 = p1.matcher(dataList[1])
                    while (m1.find()) {
                        data = m1.group()
                        break
                    }
                } else {
                    val m1 = p1.matcher(dataList[0])
                    while (m1.find()) {
                        data = m1.group()
                        break
                    }
                }
                when {
                    data.contains(")", true) -> {
                        refNumber = "IMPS " + data.split(")")[0]
                    }

                    data.contains(".", true) -> {
                        refNumber = "IMPS " + data.split(". ")[0]
                    }

                    else -> {
                        refNumber = data
                    }
                }
            }


        } else if (body.contains("RefNo", true)) {
            var dataList = body.split("RefNo")
            val p1 = Pattern.compile("([0-9]+).*")
            var data = ""
            if (dataList.size == 2) {
                val m1 = p1.matcher(dataList[1])
                while (m1.find()) {
                    data = m1.group()
                    break
                }
            } else {
                val m1 = p1.matcher(dataList[0])
                while (m1.find()) {
                    data = m1.group()
                    break
                }
            }
            when {
                data.contains(")", true) -> {
                    refNumber = "RefNo " + data.split(")")[0]
                }

                data.contains(".", true) -> {
                    refNumber = "RefNo " + data.split(". ")[0]
                }

                data.contains("on", true) -> {
                    refNumber = "RefNo " + data.lowercase(Locale.getDefault()).split(" on")[0]
                }

                data.contains("has", true) -> {
                    refNumber = "RefNo " + data.lowercase(Locale.getDefault()).split(" has")[0]
                }

                else -> {
                    refNumber = data
                }
            }

        } else if (body.contains("Ref no", true)) {
            if (body.contains("VPA", true)) {
                var dataList = body.lowercase(Locale.getDefault()).split("vpa ")
                var data = ""
                data = if (dataList.size > 1) {
                    dataList[1]
                } else {
                    dataList[0]
                }
                when {
                    data.contains(".", true) -> {
                        refNumber = "VPA " + data.split(". ")[0]
                    }

                    data.contains(")", true) -> {
                        refNumber = "VPA " + data.split(")")[0]
                    }

                    data.contains("on", true) -> {
                        refNumber = "VPA " + data.split(" on")[0]
                    }

                    data.contains("has", true) -> {
                        refNumber = "VPA " + data.split(" has")[0]
                    }

                    else -> {
                        refNumber = data
                    }
                }
            } else {
                var dataList = body.lowercase(Locale.getDefault()).split("ref no")
                var data = ""
                data = if (dataList.size > 1) {
                    dataList[1]
                } else {
                    dataList[0]
                }
                when {
                    data.contains(")", true) -> {
                        refNumber = "Ref no" + data.split(")")[0]
                    }

                    data.contains(".", true) -> {
                        refNumber = "Ref no" + data.split(". ")[0]
                    }

                    data.contains("on", true) -> {
                        refNumber = "Ref no" + data.split(" on")[0]
                    }

                    data.contains("has", true) -> {
                        refNumber = "Ref no" + data.split(" has")[0]
                    }

                    else -> {
                        refNumber = data
                    }
                }
            }


        } else if (body.contains("Ref#", true)) {
            var dataList = body.split("Ref#")
            //val p1 = Pattern.compile("([0-9]+).*")
            var data = ""
            if (dataList.size == 2) {
                data = dataList[1]
            } else {
                data = dataList[0]
            }
            when {
                data.decapitalize().contains("on", true) -> {
                    refNumber = "Ref no" + data.split(" on")[0]
                }

                data.decapitalize().contains("has", true) -> {
                    refNumber = "Ref no" + data.split(" has")[0]
                }

                data.contains(")", true) -> {
                    refNumber = "Ref no" + data.split(")")[0]
                }

                data.contains(".", true) -> {
                    refNumber = "Ref no" + data.split(".")[0]
                }

                else -> {
                    refNumber = data
                }
            }
        } else if (body.contains("Info", true)) {
            var dataList = body.split("Info")
            //val p1 = Pattern.compile("([0-9]+).*")
            var data = ""
            if (dataList.size == 2) {
                data = dataList[1]
            } else {
                data = dataList[0]
            }
            when {
                data.contains(")", true) -> {
                    refNumber = data.split(")")[0]
                }

                data.contains(".", true) -> {
                    refNumber = data.split(".")[0]
                }

                else -> {
                    refNumber = data
                }
            }
        } else if (body.contains("Received", true)) {
            if (body.contains("via", true)) {
                var dataList = body.split("via")
                //val p1 = Pattern.compile("([0-9]+).*")
                var data = ""
                data = if (dataList.size > 1) {
                    dataList[1]
                } else {
                    dataList[0]
                }
                refNumber = "VIA " + when {
                    data.decapitalize().contains("on", true) -> {
                        data.split(" on")[0]
                    }

                    data.contains(")", true) -> {
                        data.split(")")[0]
                    }

                    data.contains(". ", true) -> {
                        data.split(".")[0]
                    }

                    else -> {
                        data
                    }
                }

            } else if (body.contains("has been", true)) {
                var dataList = body.split("has ")
                //val p1 = Pattern.compile("([0-9]+).*")
                var data = ""
                data = if (dataList.size > 1) {
                    dataList[2]
                } else {
                    dataList[0]
                }
                refNumber = when {
                    data.decapitalize().contains(" on", true) -> {
                        data.split("on")[0]
                    }

                    data.contains(")", true) -> {
                        data.split(")")[0]
                    }

                    data.contains(". ", true) -> {
                        data.split(".")[0]
                    }

                    else -> {
                        data
                    }
                }
            }

        } else if (body.contains("ATM", true)) {
            if (body.contains("txn#", true)) {
                var dataList = body.split("ATM")
                //val p1 = Pattern.compile("([0-9]+).*")
                var data = ""
                if (dataList.size > 1) {
                    if (dataList.size > 2) {
                        data = dataList[2]
                    } else {
                        data = dataList[1]
                    }
                } else {
                    data = dataList[0]
                }
                when {
                    data.decapitalize().contains("fm", true) -> {
                        refNumber = data.split("fm")[0]
                    }

                    data.decapitalize().contains("has", true) -> {
                        refNumber = data.split(" has")[0]
                    }

                    data.contains(")", true) -> {
                        refNumber = data.split(")")[0]
                    }

                    data.contains(".", true) -> {
                        refNumber = data.split(". ")[0]
                    }

                    else -> {
                        refNumber = data
                    }
                }
            } else if (body.contains("tx", true)) {
                var dataList = body.split("tx#")
                //val p1 = Pattern.compile("([0-9]+).*")
                var data = ""
                data = if (dataList.size > 1) {
                    dataList[1]
                } else {
                    dataList[0]
                }
                when {
                    data.contains("fm ", true) -> {
                        refNumber = "ATM " + data.split("fm ")[0]
                    }

                    data.contains("for", true) -> {
                        refNumber = "ATM " + data.split("for ")[0]
                    }

                    data.contains(")", true) -> {
                        refNumber = "ATM " + data.split(")")[0]
                    }

                    data.contains(".", true) -> {
                        refNumber = "ATM " + data.split(". ")[0]
                    }

                    data.contains("has", true) -> {
                        refNumber = "ATM " + data.split(" has")[0]
                    }

                    else -> {
                        refNumber = data
                    }
                }
            } else if (body.contains("withdrawn", true)) {
                var dataList = body.lowercase(Locale.getDefault()).split("at ")
                //val p1 = Pattern.compile("([0-9]+).*")
                var data = ""
                if (dataList.size > 1) {
                    if (dataList.size > 2) {
                        data = dataList[2]
                    } else {
                        data = dataList[1]
                    }
                } else {
                    data = dataList[0]
                }
                when {
                    data.lowercase(Locale.getDefault()).contains("on", true) -> {
                        refNumber = data.split(" on")[0]
                    }

                    data.lowercase(Locale.getDefault()).contains("has", true) -> {
                        refNumber = data.split(" has")[0]
                    }

                    data.contains(")", true) -> {
                        refNumber = data.split(")")[0]
                    }

                    data.contains(".", true) -> {
                        refNumber = data.split(". ")[0]
                    }

                    else -> {
                        refNumber = "ATM " + data
                    }
                }
            } else if (body.contains("tx", true)) {
                var dataList = body.split("tx#")
                //val p1 = Pattern.compile("([0-9]+).*")
                var data = ""
                data = if (dataList.size > 1) {
                    dataList[1]
                } else {
                    dataList[0]
                }
                when {
                    data.contains("fm ", true) -> {
                        refNumber = "ATM " + data.split("fm ")[0]
                    }

                    data.contains("for", true) -> {
                        refNumber = "ATM " + data.split("for ")[0]
                    }

                    data.contains(")", true) -> {
                        refNumber = "ATM " + data.split(")")[0]
                    }

                    data.contains(".", true) -> {
                        refNumber = "ATM " + data.split(". ")[0]
                    }

                    data.contains("has", true) -> {
                        refNumber = "ATM " + data.split(" has")[0]
                    }

                    else -> {
                        refNumber = data
                    }
                }
            } else if (body.contains("has been", true)) {
                var dataList = body.lowercase(Locale.getDefault()).split("by")
                var data = ""
                data = if (dataList.size > 1) {
                    dataList[1].trim()
                } else {
                    dataList[0]
                }
                refNumber = when {
                    data.contains(" on", true) -> {
                        data.split(" on")[0]
                    }

                    data.contains(". ", true) -> {
                        data.split(". ")[0]
                    }

                    data.contains(")", true) -> {
                        data.split(")")[0]
                    }

                    else -> {
                        data
                    }
                }
            }

        } else if (body.contains("by transfer", true)) {
            if (body.contains("Deposit by", true)) {
                var dataList = body.lowercase(Locale.getDefault()).split("deposit by ")
                //val p1 = Pattern.compile("([0-9]+).*")
                var data = ""
                if (dataList.size > 1) {
                    data = dataList[1]
                } else {
                    data = dataList[0]
                }
                refNumber = when {
                    data.contains(" avl ", true) -> {
                        data.split(" avl ")[0]
                    }

                    data.contains(".", true) -> {
                        data.split(".")[0]
                    }

                    data.contains("-", true) -> {
                        data.split("-")[0]
                    }

                    data.contains(")", true) -> {
                        data.split(")")[0]
                    }

                    else -> {
                        data
                    }
                }
            } else {
                refNumber = "Transfer"
            }
        } else if (body.contains("for UPI", true)) {
            var dataList = body.lowercase(Locale.getDefault()).split("upi-")
            //val p1 = Pattern.compile("([0-9]+).*")
            var data = ""
            if (dataList.size == 2) {
                data = dataList[1]
            } else {
                data = dataList[0]
            }
            when {
                data.contains(")", true) -> {
                    refNumber = data.split(")")[0]
                }

                data.contains(".", true) -> {
                    refNumber = data.split(". ")[0]
                }

                else -> {
                    refNumber = data
                }
            }
        } else if (body.contains("Credit Card", true)) {
            if (body.contains("Credit card ending", true)) {
                var dataList = body.lowercase(Locale.getDefault()).split("from ")
                //val p1 = Pattern.compile("([0-9]+).*")
                var data = ""
                if (dataList.size > 1) {
                    data = dataList[1].trim()
                } else {
                    data = dataList[0]
                }
                when {
                    data.contains("on", true) -> {
                        refNumber = getFirstWord(data.split(" on")[0])
                    }

                    data.contains(")", true) -> {
                        refNumber = getFirstWord(data.split(")")[0])
                    }

                    data.contains(".", true) -> {
                        refNumber = getFirstWord(data.split(". ")[0])
                    }

                    else -> {
                        refNumber = getFirstWord(data)
                    }
                }

            } else if (body.contains("form", true)) {
                var dataList = body.lowercase(Locale.getDefault()).split("from ")
                //val p1 = Pattern.compile("([0-9]+).*")
                var data = ""
                if (dataList.size == 2) {
                    data = dataList[1]
                } else {
                    data = dataList[0]
                }
                when {
                    data.contains("on", true) -> {
                        refNumber = data.split(" on")[0]
                    }

                    data.contains(")", true) -> {
                        refNumber = data.split(")")[0]
                    }

                    data.contains(".", true) -> {
                        refNumber = data.split(". ")[0]
                    }

                    else -> {
                        refNumber = data
                    }
                }
            } else if (body.contains("spent", true)) {
                var dataList = body.lowercase(Locale.getDefault()).split("at ")
                //val p1 = Pattern.compile("([0-9]+).*")
                var data = ""
                data = if (dataList.size > 1) {
                    dataList[1]
                } else {
                    dataList[0]
                }
                when {
                    data.contains(" on ", true) -> {
                        refNumber = data.split(" on ")[0]
                    }

                    data.contains(")", true) -> {
                        refNumber = data.split(")")[0]
                    }

                    data.contains(".", true) -> {
                        refNumber = data.split(".")[0]
                    }

                    else -> {
                        refNumber = data
                    }
                }
            } else {
                var dataList = body.lowercase(Locale.getDefault()).split("at")
                //val p1 = Pattern.compile("([0-9]+).*")
                var data = ""
                data = if (dataList.size > 1) {
                    dataList[1]
                } else {
                    dataList[0]
                }
                when {
                    data.contains(" on ", true) -> {
                        refNumber = data.split(" on ")[0]
                    }

                    data.contains(")", true) -> {
                        refNumber = data.split(")")[0]
                    }

                    data.contains(".", true) -> {
                        refNumber = data.split(". ")[0]
                    }

                    else -> {
                        refNumber = data
                    }
                }
            }

        } else if (body.contains("payment", true) && !body.contains("spent", true)) {
            var dataList = body.lowercase(Locale.getDefault()).split("for")
            //val p1 = Pattern.compile("([0-9]+).*")
            var data = ""
            data = if (dataList.size > 1) {
                dataList[1]
            } else {
                dataList[0]
            }
            refNumber = when {
                data.contains("-", true) -> {
                    data.split("-")[0]
                }

                data.contains(")", true) -> {
                    data.split(")")[0]
                }

                data.contains(".", true) -> {
                    data.split(".")[0]
                }

                else -> {
                    data
                }
            }
        } else if (body.contains("spent", true)) {
            var dataList = body.lowercase(Locale.getDefault()).split(" at ")
            //val p1 = Pattern.compile("([0-9]+).*")
            var data = ""
            data = if (dataList.size > 1) {
                dataList[1].trim()
            } else {
                dataList[0]
            }
            when {
                data.contains(" on ", true) -> {
                    refNumber = data.split(" on ")[0]
                }

                data.contains(".", true) -> {
                    refNumber = data.split(". ")[0]
                }

                data.contains(")", true) -> {
                    refNumber = data.split(")")[0]
                }

                else -> {
                    refNumber = data
                }
            }
        } else if (body.contains("cheque Number", true)
            || body.contains("cheque No", true)
        ) {
            if (body.contains("cheque No", true)) {
                var dataList = body.lowercase(Locale.getDefault()).split("cheque no ")
                //val p1 = Pattern.compile("([0-9]+).*")
                var data = ""
                if (dataList.size == 2) {
                    data = dataList[1]
                } else {
                    data = dataList[0]
                }
                var temp = when {
                    data.contains(".", true) -> {
                        data.split(".")[0]
                    }

                    data.contains("-", true) -> {
                        data.split("-")[0]
                    }

                    data.contains(")", true) -> {
                        data.split(")")[0]
                    }

                    else -> {
                        data
                    }
                }
                refNumber = "Cheque No " + getFirstWord(temp.trim())
            } else if (body.contains("cheque Number", true)) {
                var dataList = body.lowercase(Locale.getDefault()).split("cheque number ")
                //val p1 = Pattern.compile("([0-9]+).*")
                var data = ""
                if (dataList.size == 2) {
                    data = dataList[1]
                } else {
                    data = dataList[0]
                }
                when {
                    data.contains(".", true) -> {
                        refNumber = data.split(".")[0]
                    }

                    data.contains("-", true) -> {
                        refNumber = data.split("-")[0]
                    }

                    data.contains(")", true) -> {
                        refNumber = data.split(")")[0]
                    }

                    else -> {
                        refNumber = data
                    }
                }
            }


        } else if (body.contains("credit for", true)) {
            var dataList = body.lowercase(Locale.getDefault()).split("credit for ")
            //val p1 = Pattern.compile("([0-9]+).*")
            var data = ""
            if (dataList.size == 2) {
                data = dataList[1]
            } else {
                data = dataList[0]
            }
            refNumber = "Credit " + when {
                data.contains(" of ", true) -> {
                    data.split(" of ")[0]
                }

                data.contains(".", true) -> {
                    data.split(".")[0]
                }

                data.contains("-", true) -> {
                    data.split("-")[0]
                }

                data.contains(")", true) -> {
                    data.split(")")[0]
                }

                else -> {
                    data
                }
            }
        } else if (body.contains("Deposit by ", true)) {
            var dataList = body.lowercase(Locale.getDefault()).split("Deposit by ")
            //val p1 = Pattern.compile("([0-9]+).*")
            var data = ""
            if (dataList.size > 1) {
                data = dataList[1].trim()
            } else {
                data = dataList[0]
            }
            refNumber = when {
                data.contains(" avl ", true) -> {
                    data.split(" of ")[0]
                }

                data.contains(".", true) -> {
                    data.split(".")[0]
                }

                data.contains("-", true) -> {
                    data.split("-")[0]
                }

                data.contains(")", true) -> {
                    data.split(")")[0]
                }

                else -> {
                    data
                }
            }
        } else if (body.contains("ref", true)) {
            var dataList = body.lowercase(Locale.getDefault()).split("ref")
            //val p1 = Pattern.compile("([0-9]+).*")
            var data = ""
            data = if (dataList.size > 1) {
                dataList[1].trim()
            } else {
                dataList[0]
            }

            refNumber = "Ref " + getFirstWord(data)
        } else if (body.contains("cheque of", true)) {
            var dataList = body.lowercase(Locale.getDefault()).split("cheque of ")

            refNumber = "Cheque"
        } else if (body.contains("UPI", true)) {
            var dataList = body.lowercase(Locale.getDefault()).split("upi")
            //val p1 = Pattern.compile("([0-9]+).*")
            var data = ""
            if (dataList.size > 1) {
                data = dataList[1].trim()
            } else {
                data = dataList[0]
            }
            refNumber = "UPI" + when {
                data.contains(".", true) -> {
                    data.split(".")[0]
                }

                data.contains("-", true) -> {
                    data.split("-")[0]
                }

                data.contains(")", true) -> {
                    data.split(")")[0]
                }

                else -> {
                    data
                }
            }
        } else if (body.contains("Credited", true)) {
            var dataList = body.lowercase(Locale.getDefault()).split(" account of ")
            //val p1 = Pattern.compile("([0-9]+).*")
            var data = ""
            if (dataList.size > 1) {
                data = dataList[1].trim()
            } else {
                data = dataList[0]
            }
            refNumber = "Credited:" + when {
                data.contains("a/c", true) -> {
                    data.split("a/c")[0]
                }

                data.contains(".", true) -> {
                    data.split(".")[0]
                }

                data.contains("-", true) -> {
                    data.split("-")[0]
                }

                data.contains(")", true) -> {
                    data.split(")")[0]
                }

                else -> {
                    data
                }
            }
        } else if (body.contains("deducted", true)) {
            var dataList = body.lowercase(Locale.getDefault()).split(" for ")
            //val p1 = Pattern.compile("([0-9]+).*")
            var data = ""
            if (dataList.size > 1) {
                data = dataList[1].trim()
            } else {
                data = dataList[0]
            }
            refNumber = "Credited:" + when {
                data.contains("a/c", true) -> {
                    data.split("a/c")[0]
                }

                data.contains(".", true) -> {
                    data.split(".")[0]
                }

                data.contains("-", true) -> {
                    data.split("-")[0]
                }

                data.contains(")", true) -> {
                    data.split(")")[0]
                }

                else -> {
                    data
                }
            }
        }

        // var dataList = smsDto.body.split("Ref")

        return refNumber

    }
}