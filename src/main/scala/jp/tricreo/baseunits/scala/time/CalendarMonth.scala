/*
 * Copyright 2011 Tricreo Inc and the Others.
 * lastModified : 2011/04/22
 *
 * This file is part of Tricreo.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package jp.tricreo.baseunits.scala.time

import java.util.{Calendar, TimeZone}

/**カレンダー上の特定の「年月」を表すクラス。
 *
 * <p>[[java.util.Date]]と異なり、月未満（日以下）の概念を持っていない。また、[[TimePoint]]と異なり、
 * その月1ヶ月間全ての範囲を表すクラスであり、特定の瞬間をモデリングしたものではない。</p>
 */
@serializable
class CalendarMonth private[time]
(private[time] val year: Int,
 private[time] val month: MonthOfYear)
  extends Ordered[CalendarMonth] {

  /**このインスタンスが表現する年月の1日からその月末までの、期間を生成する。
   *
   * <p>生成する期間の開始日と終了日は期間に含む（閉じている）開区間を生成する。</p>
   *
   * @return このインスタンスが表現する年月の1日からその月末までを表現する期間
   */
  def asCalendarInterval =
    CalendarInterval.month(year, month)

  /**
   * 指定したタイムゾーンにおける、このインスタンスが表す「年月」の1日0時0分0秒0ミリ秒の瞬間について [[TimePoint]] 型のインスタンスを返す。
   *
   * @param timeZone タイムゾーン
   * @return [[TimePoint]]
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def asTimePoint(timeZone: TimeZone) =
    TimePoint.at(year, month, DayOfMonth(1), 0, 0, 0, 0, timeZone)

  /**
   * このインスタンスが表現する年月を含む年の元旦からその大晦日までの、期間を生成する。
   *
   * <p>生成する期間の開始日と終了日は期間に含む（閉じている）開区間を生成する。</p>
   *
   * @return このインスタンスが表現する年月の1日からその月末までを表現する期間
   */
  def asYearInterval =
    CalendarInterval.year(year)

  /**このインスタンスが表す年月で、引数{@code day}で表す日を表す年月日を返す。
   *
   * @param day 日（1〜31）
   * @return 日時
   * @throws IllegalArgumentException 引数{@code day}がこの月に存在しない場合
   */
  def at(day: DayOfMonth) =
    CalendarDate.from(year, month, day)

  /**
   * このオブジェクトの[[#month]]フィールド（月）を返す。
   *
   * <p>CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。</p>
   *
   * @return 月
   */
  def breachEncapsulationOfMonth = month

  /**
   * このオブジェクトの[[#year]]フィールド（西暦年をあらわす数）を返す。
   *
   * <p>CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。</p>
   *
   * @return 西暦年をあらわす数
   */
  def breachEncapsulationOfYear = year

  /**
   * 年月日同士の比較を行う。
   *
   * <p>相対的に過去である方を「小さい」と判断する。</p>
   *
   * @param other 比較対象
   * @return [[Comparable#compareTo(Object)]]に準じる
   * @throws NullPointerException 引数に{@code null}を与えた場合
   */
  def compare(other: CalendarMonth): Int =
    if (isBefore(other)) {
      -1
    } else if (isAfter(other)) {
      1;
    } else 0

  override def equals(obj: Any) = obj match {
    case that: CalendarMonth => this.month == that.month && this.year == that.year
    case _ => false
  }

  /**
   * 月末の日付を取得する。
   *
   * @return [[DayOfMonth]]
   */
  def getLastDay =
    CalendarDate.from(year, month, getLastDayOfMonth)

  /**
   * 月末の日を取得する。
   *
   * @return [[DayOfMonth]]
   */
  def getLastDayOfMonth =
    month.getLastDayOfThisMonth(year)

  override def hashCode: Int = month.hashCode + year.hashCode


  /**
   * 指定した日 {@code other} が、このオブジェクトが表現する日よりも過去であるかどうかを検証する。
   *
   * <p>{@code other} が {@code null} である場合は {@code false} を返す。
   * また、お互いが同一日時である場合は {@code false} を返す。</p>
   *
   * @param other 対象日時
   * @return 過去である場合は{@code true}、そうでない場合は{@code false}
   */
  def isAfter(other: CalendarMonth) =
    isBefore(other) == false && equals(other) == false

  /**
   * 指定した年月 {@code other} が、このオブジェクトが表現する年月よりも未来であるかどうかを検証する。
   *
   * <p>{@code other} が {@code null} である場合は {@code false} を返す。
   * また、お互いが同一日時である場合は {@code false} を返す。</p>
   *
   * @param other 対象年月
   * @return 未来である場合は{@code true}、そうでない場合は{@code false}
   */
  def isBefore(other: CalendarMonth) =
    if (year < other.year) {
      true
    } else if (year > other.year) {
      false
    } else month.isBefore(other.month)

  /**
   * このインスタンスが表現する年月の翌月を返す。
   *
   * @return 翌月
   */
  def nextMonth = plusMonths(1)

  /**
   * このオブジェクトが表現する日付に、指定した長さの時間を加えた、未来の日付を取得する。
   *
   * <p>引数の長さの単位が "月" 未満である場合は、元の年月をそのまま返す。<p>
   *
   * @param length 時間の長さ
   * @return 未来の年月
   */
  def plus(length: Duration) =
    length.addedTo(this)

  /**
   * このインスタンスが表現する年月の {@code increment} ヶ月後を返す。
   *
   * <p> {@code increment}に負数を与えてもよい。</p>
   *
   * @param increment 加える月数
   * @return 計算結果
   */
  def plusMonths(increment: Int) = {
    val calendar = asJavaCalendarUniversalZoneMidnight
    calendar.add(Calendar.MONTH, increment)
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1
    CalendarMonth.from(year, month)
  }

  /**
   * このインスタンスが表現する年月の前月を返す。
   *
   * @return 前月
   */
  def previousDay = plusMonths(-1)

  /**
   * この日付の文字列表現を取得する。
   *
   * <p>[[SimpleDateFormat]]の使用に基づく {@code "yyyy-MM"}のパターンで整形する。</p>
   *
   * @see java.lang.Object#toString()
   */
  override def toString = toString("yyyy-MM")

  //default for console

  /**
   * この日付を、指定したパターンで整形し、その文字列表現を取得する。
   *
   * @param pattern パターン
   * @return 文字列表現
   */
  def toString(pattern: String) = {
    // Any timezone works, as long as the same one is used throughout.
    val arbitraryZone = TimeZone.getTimeZone("Universal")
    val point = asTimePoint(arbitraryZone)
    point.toString(pattern, arbitraryZone)
  }

  def asJavaCalendarUniversalZoneMidnight = {
    val zone = TimeZone.getTimeZone("Universal")
    val calendar = Calendar.getInstance(zone)
    calendar.set(Calendar.YEAR, year)
    calendar.set(Calendar.MONTH, month.value - 1)
    calendar.set(Calendar.DATE, 1)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    calendar;
  }
}

object CalendarMonth {

  def apply(year: Int, month: Int) = from(year, month)

  def unapply(calendarMonth: CalendarMonth) =
    Some(calendarMonth.year, calendarMonth.month)

  /**指定した年月を表す、[[CalendarMonth]]のインスタンスを生成する。
   *
   * @param year 西暦年をあらわす数
   * @param month 月をあらわす正数（1〜12）
   * @return [[CalendarDate]]
   * @throws IllegalArgumentException 引数{@code month}が1〜12の範囲ではない場合
   */
  def from(year: Int, month: Int) =
    new CalendarMonth(year, MonthOfYear(month))

  /**指定した年月を表す、[[CalendarMonth]]のインスタンスを生成する。
   *
   * @param year 年
   * @param month 月
   * @return [[CalendarMonth]]
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def from(year: Int, month: MonthOfYear) =
    new CalendarMonth(year, month)

  /**
   * 指定した年月を表す、[[CalendarMonth]]のインスタンスを生成する。
   *
   * @param dateString 年月を表す文字列
   * @param pattern 解析パターン文字列
   * @return [[CalendarMonth]]
   * @throws ParseException 文字列の解析に失敗した場合
   */
  def parse(dateString: String, pattern: String) = {
    val arbitraryZone = TimeZone.getTimeZone("Universal")
    //Any timezone works, as long as the same one is used throughout.
    val point = TimePoint.parse(dateString, pattern, arbitraryZone)
    CalendarMonth.from(point, arbitraryZone)
  }

  /**
   * 指定したタイムゾーン上で指定した瞬間が属する日付を元に、[[CalendarDate]]のインスタンスを生成する。
   *
   * @param timePoint 瞬間
   * @param zone タイムゾーン
   * @return [[CalendarDate]]
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def from(timePoint: TimePoint, zone: TimeZone): CalendarMonth = {
    val calendar = timePoint.asJavaCalendar
    calendar.setTimeZone(zone)
    CalendarMonth.from(calendar)
  }

  private[time] def from(calendar: Calendar): CalendarMonth = {
    // CHECKSTYLE IGNORE THIS LINE
    // Use timezone already set in calendar.
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1 // T&M Lib counts January as 1
    CalendarMonth.from(year, month)
  }

}