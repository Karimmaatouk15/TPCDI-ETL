import krangl.*

class Company {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            var readData = DataFrame.readCSV("data/company.csv")

           val records=  readData.rows.map { row->
                CompanyModel(companyID = row["CompanyID"] as Int?,addressLine1 = row["AddressLine1"] as String?,
                    addressLine2 = row["AddressLine2"] as String?, batchID =row["BatchID"] as Int? ,ceo = row["CEO"] as String?,city = row["City"] as String?,country =row["Country"] as String? ,
                    description = row["Description"] as String?, effectiveDate = row["EffectiveDate"] as String?, endDate =row["EndDate"] as String? ,foundingDate = row["FoundingDate"] as String?,industry = row["Industry"] as String?,
                    isCurrent = row["IsCurrent"] as Boolean?,isLowGrade = row["isLowGrade"] as Boolean?,
                    name = row["Name"] as String?,postalCode =row["PostalCode"] as String? ,SK_CompanyID = row["SK_CompanyID"] as Int?,SPRating =row["SPrating"] as String?,stateProv = row["StateProv"] as String?, status = row["Status"] as String?)
            }

            val tempRecords = records.toMutableList()

            val dictionary = tempRecords.groupBy {
                it.companyID
            }.toMutableMap()

            dictionary.forEach { companyID, list ->
                val tempList = list.sortedBy { it.effectiveDate?.toDate() }
                if (list.size > 1) {
                    for (i in 0..tempList.size - 1) {
                        if (i < tempList.size - 1) {
                            tempList[i].endDate = tempList[i + 1].effectiveDate
                            tempList[i].isCurrent = false

                        } else {
                            tempList[i].isCurrent = true
                        }
                        list[list.indexOfFirst { it.SK_CompanyID == tempList[i].SK_CompanyID }].endDate =
                            tempList[i].endDate
                        list[list.indexOfFirst { it.SK_CompanyID == tempList[i].SK_CompanyID }].isCurrent =
                            tempList[i].isCurrent
                    }
                }
            }

            var recordsFixed:MutableList<CompanyModel?> = mutableListOf()
            dictionary.forEach { id, list ->
                list.forEach {
                    recordsFixed.add(it)
                }
            }
            var y = recordsFixed.filter {
                it?.endDate.isNullOrBlank()|| it?.effectiveDate.isNullOrBlank() || it?.effectiveDate == "null"
            }



            var t = recordsFixed
            val fixedCompanyDataFrame = recordsFixed.asDataFrame()
//            fixedCompanyDataFrame.writeCSV(File("data/companyfinal.csv"))


        }
    }
}