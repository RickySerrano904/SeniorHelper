package seniorhelper.model;

import java.util.List;

public class ProgressDto {

    private Integer userId;
    private List<Module> modules;

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public List<Module> getModules() { return modules; }
    public void setModules(List<Module> modules) { this.modules = modules; }

    // Module ======================================
    public static class Module {
        private Integer id;
        private String title;
        private String description;
        private List<Lesson> lessons;
        private Quiz quiz;

        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public List<Lesson> getLessons() { return lessons; }
        public void setLessons(List<Lesson> lessons) { this.lessons = lessons; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public Quiz getQuiz() { return quiz; }
        public void setQuiz(Quiz quiz) { this.quiz = quiz; }
    }

    // Lesson ================================================
    public static class Lesson {
        private Integer id;
        private String title;
        private boolean completed;

        public Lesson() {}
        public Lesson(Integer id, String title, boolean completed) {
            this.id = id;
            this.title = title;
            this.completed = completed;
        }

        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public boolean isCompleted() { return completed; }
        public void setCompleted(boolean completed) { this.completed = completed; }
    }

    // Quiz ======================================================
    public static class Quiz {
        private Integer id;
        private String name;
        private boolean completed;
        private Integer correctCount;
        private Integer totalCount;

        public Quiz() {}
        public Quiz(Integer id, String name, boolean completed, Integer correctCount, Integer totalCount) {
            this.id = id;
            this.name = name;
            this.completed = completed;
            this.correctCount = correctCount;
            this.totalCount = totalCount;
        }

        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public boolean isCompleted() { return completed; }
        public void setCompleted(boolean completed) { this.completed = completed; }

        public Integer getCorrectCount() { return correctCount; }
        public void setCorrectCount(Integer correctCount) { this.correctCount = correctCount; }

        public Integer getTotalCount() { return totalCount; }
        public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }
    }
}
