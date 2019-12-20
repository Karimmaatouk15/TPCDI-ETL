import java.math.BigInteger
import java.text.SimpleDateFormat
import java.util.*

data class CompanyModel (var SK_CompanyID:Int?, var companyID: Int?, var status:String?, var name:String?,
                         var industry:String?, var SPRating:String?, var isLowGrade :Boolean?, var ceo:String?,
                         var addressLine1:String?, var addressLine2:String?, var postalCode:String?, var city:String?,
                         var stateProv:String?, var country:String?, var description:String?, var foundingDate:String?="",
                         var isCurrent:Boolean?, var batchID:Int?, var effectiveDate: String?, var endDate: String?) {

    fun getFoundingDate(): Date? {
        return foundingDate?.substring(0, 9)?.toDate() ?: null
    }

    fun getEndDate(): Date? {
        return endDate?.substring(0, 9)?.toDate() ?: null
    }

    fun getEffectiveDate(): Date? {
        return effectiveDate?.substring(0, 9)?.toDate() ?: null
    }

}

    data class SecurityModel(var symbol:String?,var Issue:String?,var Name:String?,var Status:String?,
                        var ExchangeID:String?,var SharesOutstanding:Int?,var FirstTrade:String?,var FirstTradeOnExchange:String?,
                        var Dividend:Double?,var SK_CompanyID:Int?,var EffectiveDate:String?,var EndDate:String?,
                        var IsCurrent:Boolean?,var BatchID:Int?,var SK_SecurityID:Int?)


    data class CustomerModel(var CustomerID:Int, var ActionType:String?, var TaxId:String?, var Tier:Int?, var DOB:String?,
                             var LastName:String?,var FirstName:String?, var MiddleInitial:String?,var AddressLine1:String?,var AddressLine2:String?
                             ,var PostalCode:String?, var City:String?,var StateProv:String?,var Country:String?,var Email1:String?,var Email2:String?
                             ,var LocalTaxID:String?, var NationalTaxID:String?,var Phone1:String?,var Phone2:String?,var Phone3:String?,var Status:String?
                             ,var Gender:String?,var EffectiveDate:String?,var EndDate:String?, var isCurrent:Boolean?,var SK_CustomerID:Int?):Comparable<CustomerModel> {

        fun updateRecordWithOldData(old: CustomerModel) {
            if (this.TaxId?.trim().isNullOrBlank() && !old.TaxId?.trim().isNullOrBlank()) {
                this.TaxId = old.TaxId
            }
            if (this.Tier == null && old.Tier != null) {
                this.Tier = old.Tier
            }
            if (this.DOB?.trim().isNullOrBlank() && !old.DOB?.trim().isNullOrBlank()) {
                this.DOB = old.DOB
            }
            if (this.LastName?.trim().isNullOrBlank() && !old.LastName?.trim().isNullOrBlank()) {
                this.LastName = old.LastName
            }
            if (this.FirstName?.trim().isNullOrBlank() && !old.FirstName?.trim().isNullOrBlank()) {
                this.FirstName = old.FirstName
            }
            if (this.MiddleInitial?.trim().isNullOrBlank() && !old.MiddleInitial?.trim().isNullOrBlank()) {
                this.MiddleInitial = old.MiddleInitial
            }
            if (this.AddressLine1?.trim().isNullOrBlank() && !old.AddressLine1?.trim().isNullOrBlank()) {
                this.AddressLine1 = old.AddressLine1
            }
            if (this.AddressLine2?.trim().isNullOrBlank() && !old.AddressLine2?.trim().isNullOrBlank()) {
                this.AddressLine2 = old.AddressLine2
            }
            if (this.PostalCode?.trim().isNullOrBlank() && !old.PostalCode?.trim().isNullOrBlank()) {
                this.PostalCode = old.PostalCode
            }
            if (this.City?.trim().isNullOrBlank() && !old.City?.trim().isNullOrBlank()) {
                this.City = old.City
            }
            if (this.StateProv?.trim().isNullOrBlank() && !old.StateProv?.trim().isNullOrBlank()) {
                this.StateProv = old.StateProv
            }
            if (this.Country?.trim().isNullOrBlank() && !old.Country?.trim().isNullOrBlank()) {
                this.Country = old.Country
            }
            if (this.Email1?.trim().isNullOrBlank() && !old.Email1?.trim().isNullOrBlank()) {
                this.Email1 = old.Email1
            }
            if (this.Email2?.trim().isNullOrBlank() && !old.Email2?.trim().isNullOrBlank()) {
                this.Email2 = old.Email2
            }
            if (this.LocalTaxID?.trim().isNullOrBlank() && !old.LocalTaxID?.trim().isNullOrBlank()) {
                this.LocalTaxID = old.LocalTaxID
            }
            if (this.NationalTaxID?.trim().isNullOrBlank() && !old.NationalTaxID?.trim().isNullOrBlank()) {
                this.NationalTaxID = old.NationalTaxID
            }
            if (this.Phone1?.trim().isNullOrBlank() && !old.Phone1?.trim().isNullOrBlank()) {
                this.Phone1 = old.Phone1
            }
            if (this.Phone2?.trim().isNullOrBlank() && !old.Phone2?.trim().isNullOrBlank()) {
                this.Phone2 = old.Phone2
            }
            if (this.Phone2?.trim().isNullOrBlank() && !old.Phone2?.trim().isNullOrBlank()) {
                this.Phone2 = old.Phone2
            }

            if (this.Phone3?.trim().isNullOrBlank() && !old.Phone3?.trim().isNullOrBlank()) {
                this.Phone3 = old.Phone3
            }

            if ((this.Gender?.trim().isNullOrBlank() || this.Gender == "U") && !old.Gender?.trim().isNullOrBlank()) {
                this.Gender = old.Gender
            }

        }

        override fun compareTo(other: CustomerModel): Int {
            return this.EffectiveDate?.toDate()?.compareTo(other.EffectiveDate?.toDate()) ?: 0
        }
    }


        data class AccountModel(var ActionType: String?, var ActionTS: String?,var C_ID: Int,
                                var AccountID: Int?,var TaxStatus: Int?, var CA_B_ID: Int?,
                                var AccountDesc: String?,var Status: String?,
                                var EffectiveDate: String?,var EndDate: String?,var isCurrent: Boolean?, var BatchID:Int?,
                                var fakesk:Int, var SK_CustomerID:Int?){
            fun updateRecordWithOldData(old: AccountModel) {
                if (this.TaxStatus == null && old.TaxStatus!=null){
                    this.TaxStatus = old.TaxStatus
                }
                if (CA_B_ID== null && old.CA_B_ID!=null){
                    this.CA_B_ID = old.CA_B_ID
                }
                if (AccountDesc?.trim().isNullOrBlank() && !old.AccountDesc?.trim().isNullOrBlank()){
                    this.AccountDesc = old.AccountDesc
                }
                if (SK_CustomerID == null && old.SK_CustomerID!=null){
                    this.SK_CustomerID = old.SK_CustomerID
                }
                if (Status?.trim().isNullOrBlank() && !old.Status?.trim().isNullOrBlank()){
                    this.Status = old.Status
                }
            }
        }
        data class CustomerJoin(var CustomerID:Int,var EffectiveDate:String?,var EndDate:String?,var SK_CustomerID:Int?)



