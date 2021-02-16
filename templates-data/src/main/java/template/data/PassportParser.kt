package template.data

import android.util.Log

internal fun String.parsePassportData(): String {
    try {
        val arr = this.split("\n")
        val last = arr[arr.size - 1].replace(" ", "")
        val secondLast = arr[arr.size - 2].replace(" ", "")

        var countryCode = ""
        var lastName = ""
        var passportNo = ""
        var firstName = ""
        var dateOfBirth = ""
        var gender = ""
        var dateOfExpiry = ""

        if (secondLast.startsWith("P<D<<")) {
            // German passport
            val nameSplitArr = secondLast.split("<<")
            countryCode = "D"
            lastName = nameSplitArr[1].replace("<", " ")
            firstName = nameSplitArr[2].replace("<", " ")

            val valArr = last.split("<<")
            passportNo = valArr[0].substring(0, valArr[0].length - 2)
            val dateOfBirthComplete = valArr[1].substring(0, 6).reversed()
            dateOfBirth = dateOfBirthComplete.substring(0, 2).reversed() +
                    "/" + dateOfBirthComplete.substring(2, 4).reversed() +
                    "/" + dateOfBirthComplete.substring(4, 6).reversed()

            gender = if (valArr[1].substring(7, 8) == "M") "Male" else "Female"
            val dateOfExpiryComplete = valArr[1].substring(8, 14).reversed()
            dateOfExpiry = dateOfExpiryComplete.substring(0, 2).reversed() +
                    "/" + dateOfExpiryComplete.substring(2, 4).reversed() +
                    "/" + dateOfExpiryComplete.substring(4, 6).reversed()
        } else {
            // Indian passport
            val nameSplitArr = secondLast.split("<<")
            countryCode = nameSplitArr[0].substring(2, 5)
            lastName = nameSplitArr[0].substring(5).replace("<", " ")
            firstName = nameSplitArr[1].replace("<", " ")


            if (last.contains("<")) {
                val valArr = last.split("<")
                passportNo = valArr[0]
                val dateOfBirthComplete = valArr[1].substring(4, 10).reversed()
                dateOfBirth = dateOfBirthComplete.substring(0, 2).reversed() +
                        "/" + dateOfBirthComplete.substring(2, 4).reversed() +
                        "/" + dateOfBirthComplete.substring(4, 6).reversed()

                gender = if (valArr[1].substring(11, 12) == "M") "Male" else "Female"
                val dateOfExpiryComplete = valArr[1].substring(12, 18).reversed()
                dateOfExpiry = dateOfExpiryComplete.substring(0, 2).reversed() +
                        "/" + dateOfExpiryComplete.substring(2, 4).reversed() +
                        "/" + dateOfExpiryComplete.substring(4, 6).reversed()
            }
        }

        Log.d(
            "FirebaseUseCaseImpl", "Values : " +
                    "$countryCode , $lastName, $firstName, " +
                    "$passportNo, $dateOfBirth, $gender, $dateOfExpiry"
        )
        return "Country Code : $countryCode" +
                "\n Last Name : $lastName" +
                "\n First Name :  $firstName" +
                "\n Passport No : $passportNo" +
                "\n Date of Birth : $dateOfBirth" +
                "\n Gender : $gender" +
                "\n Date of Expiry : $dateOfExpiry"
    } catch (e: Exception) {
        // Some exception occurred
        e.printStackTrace()
    }
    return ""
}