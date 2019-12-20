import krangl.*
import java.io.File
import java.math.BigInteger

class Account {
    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            var readData = DataFrame.readCSV("data/AccountRaw")
            var readData2 = readData.addRowNumber("rowNumber")
            val records = readData2.rows.map { row ->
                AccountModel(
                    Status = row["Status"] as String?,
                    EndDate = row["EndDate"] as String?,
                    EffectiveDate = row["EffectiveDate"] as String?,
                    ActionType = row["ActionType"] as String,
                    isCurrent = row["isCurrent"] as Boolean?,
                    BatchID = row["BatchID"] as Int?,
                    AccountDesc = row["AccountDesc"] as String?,
                    AccountID = (row["AccountID"] as String?).let {
                        if (!it.isNullOrBlank()) {
                            it.trim().toInt()
                        } else {
                            null
                        }
                    },
                    ActionTS = row["ActionTS"] as String?,
                    C_ID = row["C_ID"] as Int,
                    CA_B_ID = (row["CA_B_ID"] as String?).let {
                        if (!it.isNullOrBlank()) {
                            it.trim().toInt()
                        } else {
                            null
                        }
                    },
                    TaxStatus = (row["TaxStatus"] as String?).let {
                        if (!it.isNullOrBlank()) {
                            it.trim().toInt()
                        } else {
                            null
                        }
                    }
                    , fakesk = row["rowNumber"] as Int
                    , SK_CustomerID = null
                )
            }

            val tempRecords = records.toMutableList()

            val filterCustomerRecords = tempRecords.filter {
                it.ActionType?.trim() == "NEW" || it.ActionType?.trim() == "UPDCUST" || it.ActionType?.trim() == "INACT"
            }.map { it.copy() }

            var accountCustomerDictionary = filterCustomerRecords.groupBy {
                it.C_ID
            }

            val sortedAccountCustomerDictionary: MutableMap<Int, List<AccountModel>> = mutableMapOf()
            accountCustomerDictionary.forEach {
                sortedAccountCustomerDictionary.put(it.key, it.value.sortedBy { it.EffectiveDate?.toDate() })
            }


//            Get Customer Records

            var readDataCustomer = DataFrame.readCSV("data/customerdim")

            val customerRecords = readDataCustomer.rows.map { row ->
                CustomerJoin(
                    CustomerID = row["CustomerID"] as Int,
                    EndDate = row["EndDate"] as String?,
                    EffectiveDate = row["EffectiveDate"] as String?,
                    SK_CustomerID = row["SK_CustomerID"] as Int
                )
            }

            var customerRecordsMutable = customerRecords.toMutableList().sortedBy {
                it.EffectiveDate?.toDate()
            }
            var customerDictionary = customerRecordsMutable.groupBy {
                it.CustomerID
            }

            var customerDictionarySorted: MutableMap<Int, List<CustomerJoin>> = mutableMapOf()
            customerDictionary.forEach {
                customerDictionarySorted.put(it.key, it.value.sortedBy { it.EffectiveDate })
            }


            customerDictionarySorted.forEach { dic1 ->
                dic1.value.forEachIndexed { index, customerJoin ->
                    sortedAccountCustomerDictionary[dic1.key]!![index].SK_CustomerID = customerJoin.SK_CustomerID
                }
            }

            val finalAccountCustomer = sortedAccountCustomerDictionary
            val finalaccountCustomerRecords: MutableList<AccountModel> = mutableListOf()
            val finalCustomer = customerDictionarySorted
            val b = true

            finalAccountCustomer?.forEach {
                var list = it.value
                list.forEach {
                    finalaccountCustomerRecords.add(it)
                }
            }

            tempRecords.forEach { accAll ->
                accAll.SK_CustomerID = finalaccountCustomerRecords.find { accCustomer ->
                    accCustomer.fakesk == accAll.fakesk
                }?.SK_CustomerID
            }


            var accountRecords = tempRecords.map { it.copy() }
            val tempAccountRecords = tempRecords.filter {
                it.ActionType != "UPDCUST" && it.ActionType != "INACT"
            }.map { it.copy() }.toMutableList()

            val onlyUpdates = tempRecords.filter {
                it.ActionType == "UPDCUST" || it.ActionType == "INACT"
            }.map { it.copy() }

            val otherThanUpdates = accountRecords.map { it.copy() }.toMutableList().filter {
                it.ActionType != "UPDCUST" && it.ActionType != "INACT"
            }.map { it.copy() }

            onlyUpdates.sortedBy { it.C_ID }.forEach {
                val idList = getAccountIDsForCustomer(
                    records = otherThanUpdates,
                    cid = it.C_ID,
                    effectiveDate = it.EffectiveDate!!
                )
                idList.forEach { id ->
                    var temp = it.copy()
                    temp.AccountID = id
                    tempAccountRecords.add(temp)
                }
            }

            val rt = tempAccountRecords.toList().toMutableList().groupBy {
                it.AccountID
            }

            val accountrecordsgroupCID = tempRecords.groupBy {
                it.C_ID
            }

            val accounts = tempAccountRecords.map { it.copy() }.groupBy { it.AccountID }
            val sortedAccounts : MutableMap<Int, List<AccountModel>> = mutableMapOf()
            accounts.forEach{
                sortedAccounts.put(it.key!!, it.value.sortedBy { it.EffectiveDate?.toDate()})
            }

            sortedAccounts.forEach{all->
                all.value.forEach {
                    if (it.ActionType == "NEW"|| it.ActionType=="ADDACCT"){
                        it.Status = "ACTIVE"
                    }else if (it.ActionType=="CLOSEACCT" || it.ActionType=="INACT"){
                        it.Status = "INACTIVE"
                    }else {
                        it.Status = null
                    }
                }
            }

            sortedAccounts.forEach {
                it.value.forEach {acc->
                    if (acc.ActionType=="ADDACCT"){
                        acc.SK_CustomerID = customerRecordsMutable.find {
                            it.CustomerID == acc.C_ID && acc.EffectiveDate!!.toDate() >= it.EffectiveDate!!.toDate() && acc.EffectiveDate!!.toDate() <= it.EndDate!!.toDate()
                        }?.SK_CustomerID
                    }
                }
            }

            sortedAccounts.forEach {
                val list = it.value
                if (list.size>1){
                    for (i in 0..list.size-1) {
                        if (i < list.size - 1) {
                            list[i + 1].updateRecordWithOldData(list[i])
                        }
                        }
                    }
                }

            var recordsWithoutUpdates:MutableList<AccountModel> = mutableListOf()
            sortedAccounts.forEach {
                var list = it.value
                list.forEach {
                    recordsWithoutUpdates.add(it)
                }
            }

            recordsWithoutUpdates.removeIf {
                it?.ActionType == "UPDCUST" || it?.ActionType == "INACT"
            }

            val recordsdic = recordsWithoutUpdates.map { it.copy() }.groupBy { it.AccountID }

            val newSortedAccounts : MutableMap<Int, List<AccountModel>> = mutableMapOf()
            recordsdic.forEach{
                newSortedAccounts.put(it.key!!, it.value.sortedBy { it.EffectiveDate?.toDate()})
            }

            newSortedAccounts.forEach {
                val list = it.value
                if (list.size>1){
                    for (i in 0..list.size-1) {
                        if (i < list.size - 1) {
                            list[i].EndDate = list[i + 1].EffectiveDate
                            list[i].isCurrent = false
                        }else {
                            list[i]?.isCurrent = true
                        }
                    }
                }else {
                    list[0].isCurrent = true
                }
            }



            var nullContaining = newSortedAccounts.filter {
                it.value.any {
                    it.SK_CustomerID == null || it.AccountID == null || it.Status.isNullOrBlank() || it.CA_B_ID == null || it.TaxStatus==null
                }
            }

            var recordsFixed:MutableList<AccountModel?> = mutableListOf()
            newSortedAccounts.forEach {
                var list = it.value
                list.forEach {
                    recordsFixed.add(it)
                }
            }

            val fixedCustomerDataFrame = recordsFixed.asDataFrame()

            fixedCustomerDataFrame.writeCSV(File("data/AccountFinal.csv"))
            val d = 0
        }



        fun getAccountIDsForCustomer(records: List<AccountModel>, cid: Int, effectiveDate: String): List<Int?> {
            return records.filter {
                it.C_ID == cid && it.EffectiveDate!!.toDate() <= effectiveDate.toDate()
            }.map {
                it.AccountID
            }.toSet().toList()
        }
    }
}