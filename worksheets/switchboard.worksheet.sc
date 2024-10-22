import scala.collection.mutable

object Switchboard {
  type S = Account
  var routes: mutable.Map[S, mutable.HashSet[Observer[S]]] =
    mutable.Map()

  def addObserver(subject: S, observer: Observer[S]): Unit =
    routes.get(subject) match {
      case Some(_) => routes(subject).add(observer)
      case None    => routes(subject) = mutable.HashSet(observer)
    }

  def remObserver(subject: S, observer: Observer[S]): Unit =
    routes.get(subject) match {
      case Some(obs) => obs.remove(observer)
      case None      =>
    }

  def notify(subject: S): Unit =
    routes.get(subject) match {
      case Some(subj) => subj.foreach(_.receiveUpdate(subject))
      case None       =>
    }
}

trait Observer[S] {
  def receiveUpdate(subject: S): Unit
}

trait Subject {
  this: Account =>
  type S = Account
  def addObserver(observer: Observer[S]): Unit =
    Switchboard.addObserver(this, observer)

  def remObserver(observer: Observer[S]): Unit =
    Switchboard.remObserver(this, observer)
}

class Account(initialBalance: Double) {
  private var currentBalance = initialBalance
  def balance = currentBalance
  def deposit(amount: Double) = currentBalance += amount
  def withdraw(amount: Double) = currentBalance -= amount
}

class ObservedAccount(initialBalance: Double)
    extends Account(initialBalance)
    with Subject {
  override def deposit(amount: Double) = {
    super.deposit(amount)
    Switchboard.notify(this)
  }
  override def withdraw(amount: Double) = {
    super.withdraw(amount)
    Switchboard.notify(this)
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
