import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

object Utils {


}

fun String.toDate():Date{
    val simpleDatFormat =  SimpleDateFormat("yyyy-MM-dd")
    return  simpleDatFormat.parse(this)
}

fun String.toDate2():Date{
    val simpleDatFormat =  SimpleDateFormat("yyyyMMdd")
    return  simpleDatFormat.parse(this)
}