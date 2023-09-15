# SMS Parser Library

The SMS Parser Library is a simple Android library designed to help you parse SMS messages on Android devices. It allows you to extract relevant information from SMS messages, particularly those related to banking transactions, and stores them in a structured format. This can be useful for various financial and analytical applications where you need to process SMS messages for specific data.

## Installation
To get started with the SMS Parser Library, you'll need to follow these steps:

### 1. Add Permissions
Make sure your Android app has the required permissions to read SMS messages. You'll need to add the following permission to your AndroidManifest.xml:

```xml
<uses-permission android:name="android.permission.READ_SMS" />
```


### 2. Gradle 
Add the Library: Add the library to your project. You can do this by including it in your app-level build.gradle file:
```kotlin
dependencies {
     implementation 'com.github.KapilYadav-dev:SmsParser:Tag'
}
```
### 3. Usage
Here's a quick guide on how to use the SMS Parser Library:

Initialize the SmsParser:

```kotlin
val parser = SmsParser(context)
```
Get the Parsed SMS List:

```kotlin
val list = parser.getParsedSmsList()
```
The getParsedSmsList() function returns a list of SmsData objects, where each object contains parsed information from an SMS message.

### SmsData Class
The SmsData class is used to represent the parsed data from SMS messages. It includes the following fields:
```kotlin
body: The body of the SMS message.
sender: The sender of the SMS message.
amount: The transaction amount (if applicable).
transactionType: The type of transaction (e.g., "Credit," "Debit," etc.).
parsed: Any additional parsed information.
cardType: The type of card used (if applicable).
accountNumber: The associated account number (if applicable).
refNumber: The reference number for the transaction (if applicable).
payName: The name of the payee or recipient (if applicable).
avlBal: The available balance (if applicable).
date: The date of the SMS message.
```
Limitations
It's important to note that the SMS Parser Library is primarily designed to work with SMS messages related to banking transactions. It may not be suitable for parsing SMS messages from non-banking services or vendors such as food delivery apps (e.g., Zomato) or other non-financial services.

Example
Here's a simple example of how you can use the library to parse SMS messages:

```kotlin
val parser = SmsParser(context)
val smsList = parser.getParsedSmsList()

for (smsData in smsList) {
    // Access parsed data fields as needed
    val sender = smsData.sender
    val amount = smsData.amount
    val transactionType = smsData.transactionType
    // ... and so on
}
```

License
This library is provided under the MIT License.

Contributions
Contributions to this library are welcome! If you encounter any issues or have ideas for improvements, please feel free to open an issue or submit a pull request on the GitHub repository.

Credits
The SMS Parser Library was developed by https://github.com/vikashstm and is maintained by mrkaydev.

For any questions or support, please contact infokaydev@gmail.com.

GitHub Repository | Report an Issue

If you find this library helpful, consider giving it a ⭐️ on GitHub. Thank you!
