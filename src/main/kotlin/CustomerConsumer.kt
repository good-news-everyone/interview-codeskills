/**
 * Необходимо получить всех клиентов по апи из внешнего сервиса. Сервис может отдавать результаты только постранично
  */

data class Customer(val id: Long, val fullName :String, val age: Int)

interface CustomerProvider {
    fun getPage(pageIndex: Int, pageSize: Int = 100): List<Customer>
}

class CustomerConsumer(private val customerProvider: CustomerProvider) {

    fun getAllCustomers(): List<Customer> {
        TODO()
    }
}
