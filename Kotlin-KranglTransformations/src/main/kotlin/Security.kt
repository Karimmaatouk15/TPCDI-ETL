import krangl.DataFrame
import krangl.asDataFrame
import krangl.readCSV
import krangl.writeCSV
import java.io.File
import java.math.BigInteger
import kotlin.math.roundToInt

class Security {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            var readData = DataFrame.readCSV("data/DimSecuritySorted")

            val records = readData.rows.map {row->
                SecurityModel(symbol =row["Symbol"] as String? ,SK_CompanyID =row["SK_CompanyID"] as Int? ,BatchID =row["BatchID"] as Int?
                    ,Dividend = row["Dividend"] as Double?,EffectiveDate = row["EffectiveDate"] as String?,EndDate = row["EndDate"] as String?
                    ,ExchangeID = row["ExchangeID"] as String?,FirstTrade = row["FirstTrade"] as String?, FirstTradeOnExchange = row["FirstTradeOnExchange"] as String?,
                    IsCurrent = row["IsCurrent"] as Boolean?,Issue = row["Issue"] as String?,Name = row["Name"] as String?,SharesOutstanding = (row["SharesOutstanding"] as Double).toInt(),
                    SK_SecurityID = row["SK_SecurityID"] as Int?, Status = row["Status"] as String?)
            }

            val tempRecords = records.toMutableList()

            val dictionary = tempRecords.groupBy {
                it.symbol
            }.toMutableMap()

            dictionary.forEach { symbol, list ->
                val tempList = list.sortedBy { it.EffectiveDate?.toDate()}
                if (list.size > 1 ){
                    for (i in 0..tempList.size-1){
                        if (i < tempList.size-1) {
                            tempList[i].EndDate = tempList[i + 1].EffectiveDate
                            tempList[i].IsCurrent = false

                        }else {
                            tempList[i].IsCurrent =true
                        }
                        list[list.indexOfFirst { it.SK_SecurityID ==tempList[i].SK_SecurityID }].EndDate=tempList[i].EndDate
                        list[list.indexOfFirst { it.SK_SecurityID ==tempList[i].SK_SecurityID }].IsCurrent=tempList[i].IsCurrent
                    }
                }
            }

            var recordsFixed:MutableList<SecurityModel?> = mutableListOf()
            dictionary.forEach { id, list ->
                list.forEach {
                    recordsFixed.add(it)
                }
            }
            var y = recordsFixed.filter {
                it?.EndDate.isNullOrBlank()|| it?.EffectiveDate.isNullOrBlank() || it?.EffectiveDate == "null"
            }

            var R = recordsFixed.filter {
                it?.Issue.isNullOrBlank() || it?.EffectiveDate == "null"
            }

            var t = recordsFixed
            val fixedCompanyDataFrame = recordsFixed.asDataFrame()
            fixedCompanyDataFrame.writeCSV(File("data/securityFinal.csv"))

        }
    }
}