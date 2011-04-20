package jp.tricreo.baseunits.scala.time

import org.scalatest.junit.AssertionsForJUnit
import org.junit.Test

/**
 * Created by IntelliJ IDEA.
 * User: junichi
 * Date: 11/04/18
 * Time: 14:54
 * To change this template use File | Settings | File Templates.
 */

class MinuteOfHourTest extends AssertionsForJUnit {

  /**
   * [[MinuteOfHour#valueOf(int)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test01_Simple {
    assert(MinuteOfHour(11).breachEncapsulationOfValue == 11)
    assert(MinuteOfHour(23) == MinuteOfHour(23))
  }

  /**
   * [[MinuteOfHour#valueOf(int)]]の不正引数テスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test02_IllegalLessThanZero {
    try {
      MinuteOfHour(-1)
      fail
    } catch {
      case e: IllegalArgumentException => // success
      case _ => fail
    }
  }

  /**
   * [[MinuteOfHour#valueOf(int)]]の不正引数テスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test03_GreaterThan {
    try {
      MinuteOfHour(60)
      fail
    } catch {
      case e: IllegalArgumentException => // success
      case _ => fail
    }
  }

  /**
   * [[MinuteOfHour#isAfter(MinuteOfHour)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test04_LaterAfterEarlier {
    val later = MinuteOfHour(45)
    val earlier = MinuteOfHour(15)
    assert(later.isAfter(earlier) == true)
  }

  /**
   * [[MinuteOfHour#isAfter(MinuteOfHour)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test05_EarlierAfterLater {
    val earlier = MinuteOfHour(15)
    val later = MinuteOfHour(45)
    assert(earlier.isAfter(later) == false)
  }

  /**
   * [[MinuteOfHour#isAfter(MinuteOfHour)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test06_EqualAfterEqual {
    val anMinute = MinuteOfHour(45)
    val anotherMinute = MinuteOfHour(45)
    assert(anMinute.isAfter(anotherMinute) == (false))
  }

  /**
   * [[MinuteOfHour#isBefore(MinuteOfHour)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test07_LaterBeforeEarlier {
    val later = MinuteOfHour(45)
    val earlier = MinuteOfHour(15)
    assert(later.isBefore(earlier) == (false))
  }

  /**
   * [[MinuteOfHour#isBefore(MinuteOfHour)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test08_EarlierBeforeLater {
    val earlier = MinuteOfHour(15)
    val later = MinuteOfHour(45)
    assert(earlier.isBefore(later) == true)
  }

  /**
   * [[MinuteOfHour#isBefore(MinuteOfHour)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test09_EqualBeforeEqual {
    val anMinute = MinuteOfHour(15)
    val anotherMinute = MinuteOfHour(15)
    assert(anMinute.isBefore(anotherMinute) == (false))
  }

  /**
   * [[MinuteOfHour#equals(Object)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test10_equals {
    val m14 = MinuteOfHour(14)
    assert(m14.equals(m14) == true)
    assert(m14.equals(MinuteOfHour(14)) == true)
    assert(m14.equals(MinuteOfHour(15)) == false)
    assert(m14.equals(null) == false)
    assert(m14.equals(new MinuteOfHour(14)) == (true))
//    assert(m14.equals(new MinuteOfHour(14) {
//    }) == (false))
  }

  /**
   * [[MinuteOfHour#toString]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test11_toString {
    for (i <- 1 until 10) {
      val m = MinuteOfHour(i)
      assert(m.toString == "%02d".format(i))
    }
    for (i <- 10 until 60) {
      val m = MinuteOfHour(i)
      assert(m.toString == i.toString)
    }
  }
}