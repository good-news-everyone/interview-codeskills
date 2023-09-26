import FeeCalculator.FeeCalculatorException
import java.lang.Exception
import java.math.BigDecimal
import kotlin.random.Random

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
            val url = invoiceProvider.providePaymentUrl(payment, paymentProvider) ?: continue
            payment.retries++
            payment.paymentProvider = paymentProvider
            paymentUrl = url
        }
        paymentRepository.save(payment)
        if (paymentUrl == null) {
            println("Payment url was not generated for some reason")
        }
        return paymentUrl
    }

    fun refactor(payment: Payment): String? {
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
