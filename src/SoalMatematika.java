package model;

import java.util.Random;

public class SoalMatematika extends Soal {
    private String question;
    private String[] options;
    private int answerIndex;

    public SoalMatematika() {
        generateRandomQuestion();
    }

    private void generateRandomQuestion() {
        Random rand = new Random();
        int a = rand.nextInt(10) + 1;
        int b = rand.nextInt(10) + 1;
        int op = rand.nextInt(4);

        int correctAnswer = 0;
        switch (op) {
            case 0:
                question = a + " + " + b + " = ?";
                correctAnswer = a + b;
                break;
            case 1:
                question = a + " - " + b + " = ?";
                correctAnswer = a - b;
                break;
            case 2:
                question = a + " × " + b + " = ?";
                correctAnswer = a * b;
                break;
            case 3:
                int dividend = a * b;
                question = dividend + " ÷ " + a + " = ?";
                correctAnswer = b;
                break;
        }

        options = new String[4];
        answerIndex = rand.nextInt(4);
        options[answerIndex] = String.valueOf(correctAnswer);

        for (int i = 0; i < 4; i++) {
            if (i != answerIndex) {
                int wrongAnswer;
                do {
                    int offset = rand.nextInt(10) - 5;
                    if (offset >= 0) offset++;
                    wrongAnswer = correctAnswer + offset;
                } while (wrongAnswer == correctAnswer || wrongAnswer < 0 ||
                        isAnswerAlreadyUsed(wrongAnswer, options, i));
                options[i] = String.valueOf(wrongAnswer);
            }
        }
    }

    private boolean isAnswerAlreadyUsed(int answer, String[] options, int currentIndex) {
        for (int i = 0; i < currentIndex; i++) {
            if (options[i] != null && options[i].equals(String.valueOf(answer))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean checkAnswer(int selectedIndex) {
        return selectedIndex == answerIndex;
    }

    @Override
    public String getQuestion() {
        return question;
    }

    @Override
    public String[] getOptions() {
        return options;
    }

    @Override
    public int getAnswerIndex() {
        return answerIndex;
    }

    // Tambahan agar tidak error jika method ini dipanggil
    public int getCorrectAnswerIndex() {
        return getAnswerIndex();
    }
}
