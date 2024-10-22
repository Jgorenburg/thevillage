import scala.collection.mutable

trait Observer[S] {
  def receiveUpdate(subject: S): Unit
}

trait Subject[S] {
  this: S =>
  private var observers: mutable.HashSet[Observer[S]] = mutable.HashSet.empty
  def addObserver(observer: Observer[S]) = observers.add(observer)
  def remObserver(observer: Observer[S]) = observers.remove(observer)

  def notifyObservers() = observers.foreach(_.receiveUpdate(this))
}

class Account(initialBalance: Double) {
  private var currentBalance = initialBalance
  def balance = currentBalance
  def deposit(amount: Double) = currentBalance += amount
  def withdraw(amount: Double) = currentBalance -= amount
}

class ObservedAccount(initialBalance: Double)
    extends Account(initialBalance)
    with Subject[Account] {
  override def deposit(amount: Double) = {
    super.deposit(amount)
    notifyObservers()
  }
  override def withdraw(amount: Double) = {
    super.withdraw(amount)
    notifyObservers()
  }
}

class AccountReporter extends Observer[Account] {
  def receiveUpdate(account: Account) =
    println("Observed balance change: " + account.balance)
}

val oa = new ObservedAccount(100.0)
val ar = new AccountReporter
oa.addObserver(ar)
oa.deposit(40.0)
oa.withdraw(40.0)
val br = new AccountReporter
oa.addObserver(br)
oa.deposit(40.0)
oa.withdraw(50.0)
oa.remObserver(br)
oa.deposit(40.0)
oa.withdraw(50.0)
