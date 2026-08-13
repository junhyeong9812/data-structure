# pop/CronExpression.java

5필드 Cron 파서. `min hour day month dow` (`*`, `*/n`, `a-b`, `a,b,c` 지원).
nextExecutionTime 계산.

```java
package com.datastructure.taskscheduler.pop;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class CronExpression {
    private final Set<Integer> minutes;     // 0-59
    private final Set<Integer> hours;       // 0-23
    private final Set<Integer> daysOfMonth; // 1-31
    private final Set<Integer> months;      // 1-12
    private final Set<Integer> daysOfWeek;  // 0-6 (Sun=0)
    private final String original;

    public CronExpression(String expr) {
        String[] parts = expr.trim().split("\\s+");
        if (parts.length != 5) throw new IllegalArgumentException("5 fields required");
        this.minutes = parseField(parts[0], 0, 59);
        this.hours = parseField(parts[1], 0, 23);
        this.daysOfMonth = parseField(parts[2], 1, 31);
        this.months = parseField(parts[3], 1, 12);
        this.daysOfWeek = parseField(parts[4], 0, 6);
        this.original = expr;
    }

    public static CronExpression parse(String expr) {
        return new CronExpression(expr);
    }

    private Set<Integer> parseField(String field, int min, int max) {
        Set<Integer> result = new TreeSet<>();
        for (String token : field.split(",")) {
            int step = 1;
            String range = token;
            if (token.contains("/")) {
                String[] sp = token.split("/");
                range = sp[0];
                step = Integer.parseInt(sp[1]);
            }
            int from, to;
            if (range.equals("*")) {
                from = min;
                to = max;
            } else if (range.contains("-")) {
                String[] r = range.split("-");
                from = Integer.parseInt(r[0]);
                to = Integer.parseInt(r[1]);
            } else {
                from = to = Integer.parseInt(range);
            }
            if (from < min || to > max || from > to) {
                throw new IllegalArgumentException("invalid range: " + token);
            }
            for (int v = from; v <= to; v += step) result.add(v);
        }
        return result;
    }

    public LocalDateTime nextExecutionTime(LocalDateTime from) {
        LocalDateTime t = from.withSecond(0).withNano(0).plusMinutes(1);
        for (int safety = 0; safety < 366 * 24 * 60; safety++) {
            if (!months.contains(t.getMonthValue())) {
                t = t.withDayOfMonth(1).withHour(0).withMinute(0).plusMonths(1);
                continue;
            }
            if (!daysOfMonth.contains(t.getDayOfMonth())
                    || !daysOfWeek.contains(t.getDayOfWeek().getValue() % 7)) {
                t = t.withHour(0).withMinute(0).plusDays(1);
                continue;
            }
            if (!hours.contains(t.getHour())) {
                t = t.withMinute(0).plusHours(1);
                continue;
            }
            if (!minutes.contains(t.getMinute())) {
                t = t.plusMinutes(1);
                continue;
            }
            return t;
        }
        throw new IllegalStateException("Could not find next execution");
    }

    public boolean matches(LocalDateTime t) {
        return minutes.contains(t.getMinute())
                && hours.contains(t.getHour())
                && daysOfMonth.contains(t.getDayOfMonth())
                && months.contains(t.getMonthValue())
                && daysOfWeek.contains(t.getDayOfWeek().getValue() % 7);
    }

    @Override
    public String toString() { return original; }
}
```
