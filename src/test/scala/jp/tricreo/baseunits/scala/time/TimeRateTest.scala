package jp.tricreo.baseunits.scala.time

import org.scalatest.junit.AssertionsForJUnit
import org.junit.Test
import java.math.RoundingMode

/**[[TimeRate]]のテストクラス。
 */
class TimeRateTest extends AssertionsForJUnit {
  /**[[TimeRate]]のインスタンス生成テスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test01_SimpleRate {
    val rate = TimeRate(100.00, Duration.minutes(1))
    assert(rate.over(Duration.hours(1)) == BigDecimal(6000.00))

    assert(rate.toString == "100.0 per 1 minute")
    assert(rate.breachEncapsulationOfQuantity == BigDecimal(100))
    assert(rate.breachEncapsulationOfUnit == Duration.minutes(1))
  }

  /**[[TimeRate#over(Duration)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test02_Rounding {
    val rate = TimeRate(100.00, Duration.minutes(3))
    try {
      rate.over(Duration.minutes(1))
      fail("ArtithmeticException should have been thrown. This case requires rounding.")
    } catch {
      case _: ArithmeticException => // success
      case _ => fail
    }
  }

  /**[[TimeRate#over(Duration, RoundingMode)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test03_RoundingRate {
    val rate = TimeRate("100.00", Duration.minutes(3))
    assert(rate.over(Duration.minutes(1), BigDecimal.RoundingMode.DOWN) == BigDecimal("33.33"))
  }

  /**[[TimeRate#over(Duration, int, RoundingMode)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test04_RoundingScalingRate {
    val rate = TimeRate("100.00", Duration.minutes(3))
    assert(rate.over(Duration.minutes(1), 3, BigDecimal.RoundingMode.DOWN) == BigDecimal("33.333"))
  }

  /**[[TimeRate#equals(Object)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test05_Equals {
    val rate = TimeRate(11, Duration.days(2))
    assert(rate == (TimeRate(11.00, Duration.days(2))))
    assert(rate.equals(rate) == (true))
    assert(rate.equals(TimeRate(11.00, Duration.days(2))) == true)
    //		assert(rate.equals(new TimeRate(11.00, Duration.days(2)) {
    //		})==(false))
    assert(rate.equals(null) == false)
    assert(rate.equals(TimeRate(11.00, Duration.days(1))) == false)
    assert(rate.equals(TimeRate(11.00, Duration.months(2))) == false)
    assert(rate.equals(TimeRate(11.01, Duration.days(2))) == false)
  }

  /**[[TimeRate#toString()]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test06_toString {
    val rate = TimeRate(100.00, Duration.minutes(1))
    assert(rate.toString == "100.0 per 1 minute")
  }
}