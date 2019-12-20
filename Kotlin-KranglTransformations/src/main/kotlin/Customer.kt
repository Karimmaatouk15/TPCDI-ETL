import krangl.DataFrame
import krangl.asDataFrame
import krangl.readCSV
import krangl.writeCSV
import java.io.File

class Customer {
    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            var readData = DataFrame.readCSV("data/customeroutputcsv")
            readData.addColumn("isCurrent"){ true}
            val records = readData.rows.map { row ->
                CustomerModel(
                    CustomerID = row["CustomerID"] as Int,
                    Status = row["Status"] as String?,
                    EndDate = row["EndDate"] as String?,
                    EffectiveDate = row["EffectiveDate"] as String?,
                    ActionType = row["ActionType"] as String,
                    AddressLine1 = row["AddressLine1"] as String?,
                    AddressLine2 = row["AddressLine2"] as String?,
                    City = row["City"] as String?,
                    Country = row["Country"] as String?,
                    DOB = row["DOB"] as String?,
                    Email1 = row["Email1"] as String?,
                    Email2 = row["Email2"] as String?,
                    FirstName = row["FirstName"] as String?,
                    Gender = row["Gender"] as String?,
                    LastName = row["LastName"] as String?,
                    LocalTaxID = row["LocalTaxID"] as String?,
                    MiddleInitial = row["MiddleInitial"] as String?,
                    NationalTaxID = row["NationalTaxID"] as String?,
                    Phone1 = row["Phone1"] as String?,
                    Phone2 = row["Phone2"] as String?,
                    Phone3 = row["Phone3"] as String?,
                    PostalCode = row["PostalCode"] as String?,
                    StateProv = row["StateProv"] as String?,
                    TaxId = row["TaxID"] as String?,
                    Tier = (row["Tier"] as String?)?.let {
                        if (!it.isNullOrBlank()) {
                            it.trim()?.toInt()
                        }else null
                    },
                    isCurrent = row["isCurrent"] as Boolean?,
                    SK_CustomerID = null
                )
            }

            val tempRecords = records.toMutableList()

            val dictionary = tempRecords.groupBy {
                it.CustomerID
            }.toMutableMap()

//            val sortedDictionary = dictionary.entries.sortedWith(compareBy {
//                it.value[0].EffectiveDate?.toDate()
//            })

            val sortedDictionary : MutableMap<Int, List<CustomerModel>> = mutableMapOf()
            dictionary.forEach {
                sortedDictionary.put(it.key, it.value.sortedBy { it.EffectiveDate?.toDate() })
            }

            sortedDictionary.forEach {
                var list = it.value
                if (list.size>1){
                for (i in 0..list.size-1) {
                    if (i < list.size - 1) {
                        list[i].EndDate = list[i + 1].EffectiveDate
                        list[i].isCurrent = false
                        list [i+1].updateRecordWithOldData(list[i])
                    } else {
                        list[i].isCurrent = true
                    }
                }
                }else {
                    list[0].isCurrent = true
                }
            }

            var nullContaining = sortedDictionary.filter {
                it.value.any {
                    it.FirstName.isNullOrBlank()
                }
            }

            var recordsFixed:MutableList<CustomerModel?> = mutableListOf()
            sortedDictionary.forEach {
                var list = it.value
                list.forEach {
                    recordsFixed.add(it)
                }
            }

            val fixedCustomerDataFrame = recordsFixed.asDataFrame()

            fixedCustomerDataFrame.writeCSV(File("data/customerFinal.csv"))
        }
    }
}