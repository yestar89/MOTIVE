package com.example.motive_v1.Goal;

import org.threeten.bp.LocalDate;
import org.threeten.bp.Period;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Goal implements Serializable{

    private String goalText;
    public String getGoalText() {
        return goalText;
    }
    public void setGoalText(String goalText) {
        this.goalText = goalText;
    }

    private String title;
    private String selectedFrequency;
    public String selectedDate;
    private String formattedDate;
    private String key;
    private String id;
    private long startMillis;  // Define startMillis
    private long endMillis;    // Define endMillis
    private boolean isChecked;
    private String startDate;
    private String endDate;

    private String dDayText;
    private int Dday;
    private int number;
    private int numberOfDays; // numberOfDays 변수 추가
    private boolean selectedDay;

    private int timeInSeconds;
    private String userId;

    private Period dateRange;
    private boolean checkboxState;

    // 유영 홈-오늘목표 추가
    private static ArrayList<Goal> sharedGoals = new ArrayList<>();

    public static ArrayList<Goal> getSharedGoals() {
        return sharedGoals;
    }

    public static void setSharedGoals(ArrayList<Goal> goals) {
        sharedGoals = goals;
    }



    // 예성 추가 49-59 line
    public int getTimeInSeconds() {
        return timeInSeconds;
    }

    public void setTimeInSeconds(int timeInSeconds) {
        this.timeInSeconds = timeInSeconds;
    }
    public Goal(int timeInSeconds) {
        this.timeInSeconds = timeInSeconds;
    }

    private String date;
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    private Map<String, Boolean> checkedDates;


    // Getter and Setter for dates
    public void setCheckedOnDate(LocalDate date, boolean isChecked) {
        this.checkedDates.put(date.format(DateTimeFormatter.ofPattern("yyyyMMdd")), isChecked);
    }

    public Map<String, Boolean> getCheckedDates() {
        return checkedDates;
    }

    public void setCheckedDates(Map<String, Boolean> checkedDates) {
        this.checkedDates = checkedDates;
    }

    private Map<String, DateCheckedItem> dates;

    public Map<String, DateCheckedItem> getDates() {
        return dates;
    }

    public void setDates(Map<String, DateCheckedItem> dates) {
        this.dates = dates;
    }

    public static class DateCheckedItem {
        private boolean checked;
        public DateCheckedItem() {}

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }
    }



    //프로그래스
    private int progress;
    public int getProgress() {
        return progress;
    }
    public void setProgress(int progress) {
        this.progress = progress;
    }

    private int completedCount; // 완료된 목표의 수를 추적하는 필드

    // 완료된 목표의 수를 설정하는 메서드
    public void setCompletedCount(int completedCount) {
        this.completedCount = completedCount;
    }

    // 완료된 항목의 수를 반환하는 메서드
    public int getCompletedCount() {
        int count = 0;
        for (boolean checked : checkedDates.values()) {
            if (checked) count++;
        }
        return count;
    }

    // 전체 항목의 수를 반환하는 메서드
    public int getTotalCount() {
        return checkedDates.size();
    }



    //게터 세터 메소드
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getSelectedFrequency() {
        return selectedFrequency;
    }
    public void setSelectedFrequency(String selectedFrequency) {
        this.selectedFrequency = selectedFrequency;
    }

    public String getSelectedDate() {
        return selectedDate;
    }
    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }


    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }


    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public void setStartMillis(long startMillis) {
        this.startMillis = startMillis;
    }
    public long getStartMillis() {
        return startMillis;
    }

    public void setEndMillis(long endMillis) {
        this.endMillis = endMillis; // 클래스 필드에 값 설정
    }
    public long getEndMillis() {
        return endMillis;
    }

    public boolean isChecked() {
        return isChecked;
    }
    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getStartDate() {
        return startDate;
    }
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getDday() {
        return (int) Dday;
    }
    public void setDday(long daysDifference) {
        this.Dday = (int) daysDifference;
    }

    public String getdDayText() {
        return dDayText;
    }

    public void setdDayText(String dDayText) {
        this.dDayText = dDayText;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setNumberOfDays(int numberOfDays) {
        this.numberOfDays = numberOfDays;
    }

    public void setSelectedDay(boolean selectedDay) {
        this.selectedDay = selectedDay;
    }

    public void setDateRange(Period dateRange) {
        this.dateRange = dateRange;
    }
    public Period getDateRange() {
        return dateRange;
    }

    public boolean isCheckboxState() {
        return checkboxState;
    }

    public void setCheckboxState(boolean checkboxState) {
        this.checkboxState = checkboxState;
    }

    //포멧
    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }
    public String getFormattedDate() {
        return this.formattedDate;
    }


    public Goal() {
        this.checkedDates = new HashMap<>(); // 빈 HashMap으로 초기화
        this.isChecked = false; // 기본값으로 false 설정
        this.progress = 0; // 기본값으로 0 설정
    }


    public Goal(String key, String goalText, String title, String selectedDate, boolean isChecked, HashMap<String, Boolean> checkedDates) {
        this.key = key;
        this.goalText = goalText;
        this.title = title;
        this.selectedDate = selectedDate;
        this.isChecked = isChecked;
        this.checkedDates = (checkedDates != null) ? checkedDates : new HashMap<>();
    }

    public Goal(String key, String title, String selectedDate, boolean isChecked, HashMap<String, Boolean> checkedDates) {
        this.key = key;
        this.title = title;
        this.selectedDate = selectedDate;
        this.isChecked = isChecked;
        this.checkedDates = (checkedDates != null) ? checkedDates : new HashMap<>();
    }


    //

    public Goal(String title) {
        this.title = title;
    }

    public Goal(String title, String selectedDate, String selectedTime, String selectedFrequency) {
        this.title = title;
        this.selectedFrequency = selectedFrequency;
        this.selectedDate = selectedDate; // selectedDate를 먼저 설정합니다.
        // selectedDate에서 시작 날짜와 종료 날짜 추출
        String[] dates = selectedDate.split(" ~ ");
        this.startDate = dates[0];
        this.endDate = dates[1];


        // startMillis와 endMillis를 설정합니다.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd", Locale.US);
        try {
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);
            this.startMillis = start.getTime(); // 시작일의 Milliseconds
            this.endMillis = end.getTime(); // 종료일의 Milliseconds
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    public String convertDateToDesiredFormat(String selectedDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
            SimpleDateFormat inputSdf = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
            Date parsedDate = inputSdf.parse(selectedDate);

            if (parsedDate != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(parsedDate);

                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                String dayOfWeekString = getDayOfWeekString(dayOfWeek);

                return calendar.get(Calendar.DAY_OF_MONTH) + "일(" + dayOfWeekString + ")";
            } else {
                return "날짜 변환 오류";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return "날짜 변환 오류";
        }
    }


    private String getDayOfWeekString(int dayOfWeek) {
        String[] daysOfWeek = new String[]{"일", "월", "화", "수", "목", "금", "토"};
        return daysOfWeek[dayOfWeek - 1];
    }

}

