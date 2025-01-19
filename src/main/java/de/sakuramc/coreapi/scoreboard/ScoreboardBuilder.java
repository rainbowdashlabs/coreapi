package de.sakuramc.coreapi.scoreboard;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardBuilder {

    private String title;
    private final List<ScoreboardLine> lines = new ArrayList<>();

    public ScoreboardBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public ScoreboardBuilder addLine(String text, int score) {
        lines.add(new ScoreboardLine(text, score));
        return this;
    }

    public Scoreboard build() {
        return new Scoreboard(title, lines);
    }

    public static class Scoreboard {
        private final String title;
        private final List<ScoreboardLine> lines;

        public Scoreboard(String title, List<ScoreboardLine> lines) {
            this.title = title;
            this.lines = lines;
        }

        public String getTitle() {
            return title;
        }

        public List<ScoreboardLine> getLines() {
            return lines;
        }
    }

    public static class ScoreboardLine {
        private final String text;
        private final int score;

        public ScoreboardLine(String text, int score) {
            this.text = text;
            this.score = score;
        }

        public String getText() {
            return text;
        }

        public int getScore() {
            return score;
        }
    }
}
