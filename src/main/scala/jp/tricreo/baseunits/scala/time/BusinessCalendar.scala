package jp.tricreo.baseunits.scala.time

import jp.tricreo.scala.ddd.base.model.spec.Specification
import jp.tricreo.baseunits.scala.intervals.Limit

/**営業日カレンダー。
 *
 * <p>営業日と非営業日を判定する責務を持つ。非営業日とは休日（祝日）及び週末（土日）を表し、営業日とは非営業日でない日を表す。
 * 週末は休日ではないが、週末かつ休日は休日である。</p>
 */
class BusinessCalendar {

  private[time] var holidaySpecs: Specification[CalendarDate] = defaultHolidaySpecs

  /**休日として取り扱う「日」を追加する。
   *
   * @param date 休日として取り扱う「日」
   */
  def addHoliday(date: CalendarDate) {
    addHolidaySpec(DateSpecification.fixed(date));
  }

  /**休日として取り扱う「日」を追加する。
   *
   * @param days 休日として取り扱う「日」
   */
  def addHolidays(days: Set[CalendarDate]) {
    for (date <- days) {
      addHolidaySpec(DateSpecification.fixed(date))
    }
  }

  /**休日として取り扱う「日付仕様」を追加する。
   *
   * @param specs 休日として取り扱う「日付仕様」
   */
  def addHolidaySpec(specs: Specification[CalendarDate]) {
    holidaySpecs = holidaySpecs.or(specs)
  }

  /**[[CalendarDate]]の反復子を受け取り、その反復子が返す[[CalendarDate]]のうち、
   * 営業日に当たる{@link CalendarDate}のみを返す反復子を返す。
   *
   * <p>このメソッドは引数に与えた反復子の状態を変更する。また、このメソッドの戻り値の反復子を利用中は、
   * 引数に与えた反復子の {@link Iterator#next()} を呼び出してはならない。</p>
   *
   * @param calendarDays 元となる反復子
   * @return 営業日のみを返す反復子
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def businessDaysOnly(calendarDays: Iterator[CalendarDate]) = {
    new Iterator[CalendarDate] {

      var lookAhead = nextBusinessDate

      override def hasNext = lookAhead != None

      override def next: CalendarDate = {
        if (hasNext == false) {
          throw new NoSuchElementException
        }
        val _next = lookAhead
        lookAhead = nextBusinessDate
        _next.get
      }

      private def nextBusinessDate: Option[CalendarDate] = {
        var result: Option[CalendarDate] = None
        do {
          result = if (calendarDays.hasNext) Some(calendarDays.next.asInstanceOf[CalendarDate])
          else None
        } while ((result == None || isBusinessDay(result.get)) == false)
        result
      }
    }
  }

  /**[[CalendarInterval]]で表す期間のうち、営業日の日数を返す。
   *
   * @param interval 期間
   * @return 営業日の日数
   */
  def getElapsedBusinessDays(interval: CalendarInterval) = {
    var tally = 0;
    val iterator = businessDaysOnly(interval.daysIterator)
    while (iterator.hasNext) {
      iterator.next
      tally += 1;
    }
    tally
  }

  /**[[CalendarDate]]が営業日に当たるかどうか調べる。
   *
   * <p>デフォルトの実装として、週末でなく休日でない日を営業日とするが、
   * 業態によってはオーバーライドの可能性があるので注意すること。</p>
   *
   * @param day 日
   * @return 営業日に当たる場合は{@code true}、そうでない場合は{@code false}
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def isBusinessDay(day: CalendarDate) =
    isWeekend(day) == false && isHoliday(day) == false

  /**[[CalendarDate]]が休日に当たるかどうか調べる。
   *
   * <p>休日とは、非営業日のうち週末以外のものである。週末を含まないことに注意すること。</p>
   *
   * @param day 日
   * @return 休日に当たる場合は{@code true}、そうでない場合は{@code false}
   */
  def isHoliday(day: CalendarDate) =
    holidaySpecs.isSatisfiedBy(day)

  /**[[CalendarDate]]が週末に当たるかどうか調べる。
   *
   * <p>週末とは、土曜日と日曜日のことである。</p>
   *
   * @param day 日
   * @return 週末に当たる場合は{@code true}、そうでない場合は{@code false}
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def isWeekend(day: CalendarDate) = {
    val dow = day.dayOfWeek
    dow == DayOfWeek.Saturday || dow == DayOfWeek.Sunday
  }

  /**開始日から数えて{@code numberOfDays}営業日前の日付を返す。
   *
   * @param startDate 開始日
   * @param numberOfDays 営業日数（現在は正数しかサポートしない）
   * @return 日付
   * @throws IllegalArgumentException 引数{@code numberOfDays}が負数の場合
   * @throws IllegalArgumentException 引数{@code startDate}に{@code null}を与えた場合
   */
  def minusBusinessDays(startDate: CalendarDate, numberOfDays: Int) = {
    if (numberOfDays < 0) {
      throw new IllegalArgumentException("Negative numberOfDays not supported")
    }
    val iterator = CalendarInterval.everPreceding(Limit(startDate)).daysInReverseIterator
    nextNumberOfBusinessDays(numberOfDays, iterator)
  }

  /**指定した日の直近営業日を取得する。
   *
   * <p>指定日が営業日であれば当日、そうでなければ翌営業日を返す。</p>
   *
   * @param day 基準日
   * @return 営業日
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def nearestNextBusinessDay(day: CalendarDate) =
    if (isBusinessDay(day)) {
      day
    } else {
      nextBusinessDay(day)
    }

  /**指定した日の直近過去営業日を取得する。
   *
   * <p>指定日が営業日であれば当日、そうでなければ前営業日を返す。</p>
   *
   * @param day 基準日
   * @return 営業日
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def nearestPrevBusinessDay(day: CalendarDate) =
    if (isBusinessDay(day)) {
      day
    } else {
      prevBusinessDay(day)
    }

  /**指定した日の翌営業日を取得する。
   *
   * @param startDate 基準日
   * @return 翌営業日
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def nextBusinessDay(startDate: CalendarDate): CalendarDate =
    if (isBusinessDay(startDate)) {
      plusBusinessDays(startDate, 1)
    } else {
      plusBusinessDays(startDate, 0)
    }

  /**開始日から数えて{@code numberOfDays}営業日目の日付を返す。
   *
   * @param startDate 開始日
   * @param numberOfDays 営業日数（現在は正数しかサポートしない）. {@code 0}の場合、開始日を含む翌営業日を返す
   * @return 日付
   * @throws IllegalArgumentException 引数{@code numberOfDays}が負数の場合
   * @throws IllegalArgumentException 引数{@code startDate}に{@code null}を与えた場合
   */
  def plusBusinessDays(startDate: CalendarDate, numberOfDays: Int) = {
    if (numberOfDays < 0) {
      throw new IllegalArgumentException("Negative numberOfDays not supported");
    }
    val iterator = CalendarInterval.everFrom(Limit(startDate)).daysIterator
    nextNumberOfBusinessDays(numberOfDays, iterator)
  }

  /**指定した日の前営業日を取得する。
   *
   * @param startDate 基準日
   * @return 前営業日
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def prevBusinessDay(startDate: CalendarDate) =
    if (isBusinessDay(startDate)) {
      minusBusinessDays(startDate, 1)
    } else {
      minusBusinessDays(startDate, 0)
    }

  /**Should be overriden for each particular organization.
   *
   * @return 営業日の{@link Set}
   */
  protected def defaultHolidaySpecs =
    DateSpecification.never

  /**{@code calendarDays}の先頭から数えて{@code numberOfDays}営業日目の日付を返す。
   *
   * @param numberOfDays 営業日数. {@code 0}の場合、イテレータの先頭
   * @param calendarDays 日付イテレータ
   * @return 営業日
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  private def nextNumberOfBusinessDays(numberOfDays: Int,
                                       calendarDays: Iterator[CalendarDate]) = {
    val businessDays = businessDaysOnly(calendarDays)
    var result: Option[CalendarDate] = None
    for (i <- 0 to numberOfDays) {
      result = Some(businessDays.next)
    }
    result.get
  }

  /*
   * boolean isBusinessHours(TimePoint now) { Calendar date =
   * now.asJavaCalendar(); int theHour = date.get(Calendar.HOUR_OF_DAY); int
   * theMinute = date.get(Calendar.MINUTE); int timeAsMinutes = (theHour * 60) +
   * theMinute; return timeAsMinutes >= openForBusiness && timeAsMinutes <=
   * closeForBusiness; }
   *
   * boolean isInBusiness(TimePoint point) { return isBusinessDay(point) &&
   * isBusinessHours(point); }
   *
   * Returns true if <now> is a holiday. An alternative to using
   * <Holidays.ALL>
   *
   * It makes no effort to recognize "half-day holidays", such as the
   * Wednesday before Thanksgiving. Currently, it only recognizes these
   * holidays: New Year's Day MLK Day President's Day Memorial Day
   * Independence Day Labor Day Thanksgiving Christmas
   *
   *
   * boolean isFederalHoliday(TimePoint point) { Calendar javaCal =
   * point.asJavaCalendar(); int[] month_date = { Calendar.JANUARY, 1,
   * Calendar.JULY, 4, Calendar.DECEMBER, 25, }; int[] month_weekday_monthweek = {
   * Calendar.JANUARY, Calendar.MONDAY, 3, // MLK Day, 3rd monday in Jan
   * Calendar.FEBRUARY, Calendar.MONDAY, 3, // President's day
   * Calendar.SEPTEMBER, Calendar.MONDAY, 1, // Labor day Calendar.NOVEMBER,
   * Calendar.THURSDAY, 4, // Thanksgiving }; // Columbus Day is a federal
   * holiday. // it is the second Monday in October int mm =
   * javaCal.get(Calendar.MONTH); int dd = javaCal.get(Calendar.DAY_OF_MONTH);
   * int dw = javaCal.get(Calendar.DAY_OF_WEEK); int wm =
   * javaCal.get(Calendar.WEEK_OF_MONTH); // go over the month/day-of-month
   * entries, return true on full match for (int i = 0; i < month_date.length;
   * i += 2) { if ((mm == month_date[i + 0]) && (dd == month_date[i + 1]))
   * return true; } // go over month/weekday/week-of-month entries, return
   * true on full match for (int i = 0; i < month_weekday_monthweek.length; i +=
   * 3) { if ((mm == month_weekday_monthweek[i + 0]) && (dw ==
   * month_weekday_monthweek[i + 1]) && (wm == month_weekday_monthweek[i +
   * 2])) return true; }
   *
   * if ((mm == Calendar.MAY) && (dw == Calendar.MONDAY) && (wm ==
   * javaCal.getMaximum(Calendar.WEEK_OF_MONTH))) // last week in May return
   * true;
   *
   * return false; }
   */
}