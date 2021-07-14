package com.moblize.ms.dailyops.utils;

import com.google.common.base.Objects;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static java.sql.Statement.EXECUTE_FAILED;
import static java.sql.Statement.SUCCESS_NO_INFO;

@Slf4j
public class MetricsLogger {

  private static final String FORMAT_DB_DEFAULT = "metrics.db.%s.secs=%s";
  private static final String FORMAT_DB_COUNT = "metrics.db.%s.count=%s";
  private static final String FORMAT_ANALYTIC_DEFAULT = "metrics.analytic.%s.secs=%s";
  private static final String FORMAT_NDL_DEFAULT = "metrics.node_drilling_logs.%s.rate_per_sec=%s";

  private static final CountRunner countRunner = new CountRunner();
  private static final RateRunner rateRunner = new RateRunner();

  static {
    Executors.newSingleThreadScheduledExecutor()
        .scheduleWithFixedDelay(countRunner, 15, 15, TimeUnit.SECONDS);
    Executors.newSingleThreadScheduledExecutor()
        .scheduleWithFixedDelay(rateRunner, 15, 15, TimeUnit.SECONDS);
  }

  public static void dbBatchResults(String table, int[] resultCodes) {
    int success = 0;
    int rowsUpdated = 0;
    int fail = 0;
    for (int i : resultCodes) {
      if (i == SUCCESS_NO_INFO) {
        success++;
      } else if (i >= 0) {
        success++;
        rowsUpdated += i;
      } else if (i == EXECUTE_FAILED) {
        fail++;
      }
    }
    MetricsLogger.dbCount(table + ".fail", fail);
    MetricsLogger.dbCount(table + ".success", success);
    MetricsLogger.dbCount(table + ".row_updated", rowsUpdated);
  }

  public static void dbCount(String measurement, int value) {
    countRunner.update(FORMAT_DB_COUNT, measurement, value);
  }

  public static void dbTime(String measurement, float value) {
    log.info(FORMAT_DB_DEFAULT, measurement, value);
  }

  public static void dbTime(String measurement, long start, long end) {
    log.info(FORMAT_DB_DEFAULT, measurement, calcDurationInSecs(start, end));
  }

  public static void nodeDrillingLogsRate(String measurement, long start, long end, long count) {
    rateRunner.update(FORMAT_NDL_DEFAULT, measurement, calcDurationInSecs(start, end), count);
  }

  public static void analyticTime(String measurement, float value) {
    log.info(FORMAT_ANALYTIC_DEFAULT, measurement, value);
  }

  public static float calcDurationInSecs(long start, long end) {
    return (end - start) / 1000.0f;
  }

  public static void printRate(Rate rate) {
    if (rate.duration != 0.0f) {
      log.info(rate.format, rate.measurement, rate.count / rate.duration);
    }
  }

  public static void printCount(Count count) {
    log.info(count.format, count.measurement, count.count);
  }

  private static final class RateRunner implements Runnable {

    private final ReentrantLock rateLock = new ReentrantLock();
    private final Map<String, Rate> rateMap = new ConcurrentHashMap<>();

    public void update(String format, String measurement, float duration, long count) {
      rateLock.lock();
      try {
        Rate rate = rateMap.get(format + measurement);
        if (rate == null) {
          rate = new Rate(format, measurement, duration, count);
          rateMap.put(format + measurement, rate);
        } else {
          rate.count += count;
          rate.duration += duration;
        }
      } finally {
        rateLock.unlock();
      }
    }

    @Override
    public void run() {
      String threadName = Thread.currentThread().getName();
      Thread.currentThread().setName("metrics_rate_log");
      rateLock.lock();
      try {
        rateMap.values().forEach(MetricsLogger::printRate);
        rateMap.clear();
      } finally {
        rateLock.unlock();
        Thread.currentThread().setName(threadName);
      }
    }
  }

  private static final class Rate {
    private final String format;
    private final String measurement;
    private float duration;
    private long count;

    private Rate(String format, String measurement, float duration, long count) {
      this.format = format;
      this.measurement = measurement;
      this.duration = duration;
      this.count = count;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      Rate rate = (Rate) o;
      return Float.compare(rate.duration, duration) == 0
          && count == rate.count
          && Objects.equal(format, rate.format)
          && Objects.equal(measurement, rate.measurement);
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(format, measurement, duration, count);
    }
  }

  private static final class CountRunner implements Runnable {

    private final ReentrantLock countLock = new ReentrantLock();
    private final Map<String, Count> countMap = new ConcurrentHashMap<>();

    public void update(String format, String measurement, long count) {
      countLock.lock();
      try {
        Count rate = countMap.get(format + measurement);
        if (rate == null) {
          rate = new Count(format, measurement, count);
          countMap.put(format + measurement, rate);
        } else {
          rate.count += count;
        }
      } finally {
        countLock.unlock();
      }
    }

    @Override
    public void run() {
      String threadName = Thread.currentThread().getName();
      Thread.currentThread().setName("metrics_rate_log");
      countLock.lock();
      try {
        countMap.values().forEach(MetricsLogger::printCount);
        countMap.clear();
      } finally {
        countLock.unlock();
        Thread.currentThread().setName(threadName);
      }
    }
  }

  private static final class Count {
    private final String format;
    private final String measurement;
    private long count;

    private Count(String format, String measurement, long count) {
      this.format = format;
      this.measurement = measurement;
      this.count = count;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      Count count1 = (Count) o;
      return count == count1.count
          && Objects.equal(format, count1.format)
          && Objects.equal(measurement, count1.measurement);
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(format, measurement, count);
    }
  }
}
