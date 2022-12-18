package at.ac.tuwien.sepm.groupphase.backend.help;

import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import org.junit.jupiter.api.BeforeEach;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.Locale;

import static org.assertj.core.api.Assertions.tuple;

public class CalendarViewHelp {
    public static org.assertj.core.groups.Tuple generateTupleOfCompetition(Competition t) {
        return tuple(t.getName(),
            t.getBeginOfRegistration().toLocalDate().toString(),
            t.getEndOfRegistration().toLocalDate().toString(),
            t.getBeginOfCompetition().toLocalDate().toString(),
            t.getEndOfCompetition().toLocalDate().toString(),
            t.getDescription(),
            t.getPicturePath(),
            t.getPublic(),
            t.getDraft(),
            t.getEmail(),
            t.getPhone());
    }

    public static org.assertj.core.groups.Tuple generateTupleOfCalendarViewCompetition(Competition t) {
        return tuple(t.getName(),
            t.getBeginOfCompetition().toLocalDate().toString(),
            t.getEndOfCompetition().toLocalDate().toString(),
            t.getDescription(),
            t.getPicturePath());
    }

    public static final int CURRENT_YEAR;
    public static final int CURRENT_WEEK_NUMBER;

    static {
        LocalDateTime startOfThisWeek = LocalDateTime.now();
        while (startOfThisWeek.getDayOfWeek() != DayOfWeek.MONDAY) {
            startOfThisWeek = startOfThisWeek.minusDays(1);
        }
        CURRENT_YEAR = startOfThisWeek.getYear();
        CURRENT_WEEK_NUMBER = startOfThisWeek.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()) - 1;
    }
}
