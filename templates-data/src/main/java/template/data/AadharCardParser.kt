package template.data

internal fun String.parseAadharCardData(): String {
    try {
        var name = ""
        var dob = ""
        var gender = ""
        var aadharNumber = ""
        val arr = this.split("\n")
        var dobIndex = -1
        for (index in arr.indices) {
            val regex = Regex("\\d{1,2}/\\d{2}/\\d{4}")
            val matchResult = regex.find(arr[index])
            if (null != matchResult) {
                dob = matchResult.value
                dobIndex = index
                break
            }
        }
        name = if (-1 == dobIndex) {
            arr[1]
        } else {
            arr[dobIndex - 1]
        }

        gender = if (-1 == dobIndex) {
            if (arr[3].contains("MALE", true)) {
                "MALE"
            } else {
                "FEMALE"
            }
        } else {
            if (arr[dobIndex + 1].contains("MALE", true)) {
                "MALE"
            } else {
                "FEMALE"
            }
        }

        for (index in arr.indices) {
            val regex = Regex("\\d{4} \\d{4} \\d{4}")
            val matchResult = regex.find(arr[index])
            if (null != matchResult) {
                aadharNumber = matchResult.value
                break
            }
        }

        return "Name : $name" +
                "\n Date of Birth : $dob" +
                "\n Gender : $gender" +
                "\n Aadhar Number : $aadharNumber"
    } catch (e: Exception) {
        // Some exception occurred
        e.printStackTrace()
    }
    return ""
}