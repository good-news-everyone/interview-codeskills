import FeeCalculator.FeeCalculatorException
import java.lang.Exception
import java.math.BigDecimal
import kotlin.random.Random

/**
 * Есть некий платежный шлюз, которые проводит платежи
 * Платёж можно произвести через разных платёжных провайдеров (внешние интеграции)
 * Платёжные провайдеры есть двух типов : FAST_PAYMENTS и EASY_PAYMENTS
 *
 * В рамках этого метода мы должны провести платеж
 * В первую очередь, мы должны попытаться через провайдера, который указан в платеже (payment.paymentProvider)
 * Если не удаётся, то мы выбираем из базы всех подходящих по параметрам (paymentProviderRepository.findSuitablePaymentProviders)
 * Для каждого провайдера мы должны проверить, что сумма подходит для выбранного платёжного провайдера
 * Если провайдер подходит, то мы должны получить ссылку на инвойс (invoiceProvider.providePaymentUrl(payment, paymentProvider))
 * Если ссылка = null, увеличиваем payment.retries на 1 и продолжить со следущим платёжным провайдером
 * Если ссылка != null, то устанавливаем payment.paymentProvider = подходящий пеймент провайдер
 * В случае, если после всех попыток получить ссылку не удалось, нужно сделать запись в лог и отдать null
 */
class RefactorKotlin(
    private val invoiceProvider: InvoiceProvider,
    private val paymentRepository: PaymentRepository,
    private val feeCalculator: FeeCalculator,
    private val paymentProviderRepository: PaymentProviderRepository
) {
    fun createInvoiceUrl(payment: Payment): String? {
        val paymentProviders = ArrayList<PaymentProvider>()
        paymentProviders.add(payment.paymentProvider)
        paymentProviders.addAll(paymentProviderRepository.findSuitablePaymentProviders(payment))
        var paymentUrl: String? = null
        for (paymentProvider in paymentProviders) {
            try {
                payment.fee = feeCalculator.calculateFeeOrThrow(payment.amount, paymentProvider)
            } catch (e: Exception) {
                if (e is FeeCalculatorException && paymentProvider.providerType == ProviderType.FAST_PAYMENTS) {
                    throw FastPaymentException()
                } else if (e is FeeCalculatorException && paymentProvider.providerType == ProviderType.EASY_PAYMENTS) {
                    throw EasyPaymentException()
                } else throw e
            }
            payment.retries++
            val url = invoiceProvider.providePaymentUrl(payment, paymentProvider) ?: continue
            payment.paymentProvider = paymentProvider
            paymentUrl = url
        }
        paymentRepository.save(payment)
        if (paymentUrl == null) {
            println("Payment url was not generated for some reason")
        }
        return paymentUrl
    }

    fun refactor(payment: Payment): String {
        return TODO()
    }
}

class PaymentProvider {
    lateinit var providerType: ProviderType
    lateinit var amountFrom: BigDecimal
    lateinit var amountTo: BigDecimal
}

class Payment {
    lateinit var amount: BigDecimal
    lateinit var fee: BigDecimal
    lateinit var paymentProvider: PaymentProvider
    var retries = 0
}

enum class ProviderType { FAST_PAYMENTS, EASY_PAYMENTS }
class FastPaymentException : FeeCalculatorException()
class EasyPaymentException : FeeCalculatorException()


interface InvoiceProvider {
    fun providePaymentUrl(payment: Payment, paymentProvider: PaymentProvider): String?
}

interface PaymentRepository {
    fun save(payment: Payment): Payment
}

interface PaymentProviderRepository {
    fun findSuitablePaymentProviders(payment: Payment): Collection<PaymentProvider>
}

interface FeeCalculator {
    fun calculateFeeOrThrow(amount: BigDecimal, paymentProvider: PaymentProvider): BigDecimal {
        if (amount !in (paymentProvider.amountFrom..paymentProvider.amountTo)) throw FeeCalculatorException()
        return amount.subtract(Random.nextInt(from = 0, until = 10).toBigDecimal())
    }

    open class FeeCalculatorException : RuntimeException()
}
