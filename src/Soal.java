package model;

public abstract class Soal {
    /**
     * Mengecek apakah jawaban yang dipilih benar
     * @param selectedIndex indeks jawaban yang dipilih pemain (0-3)
     * @return true jika benar, false jika salah
     */
    public abstract boolean checkAnswer(int selectedIndex);

    /**
     * Mengembalikan teks soal yang akan ditampilkan ke pemain
     * @return String teks soal
     */
    public abstract String getQuestion();

    /**
     * Mengembalikan daftar opsi jawaban
     * @return Array of 4 opsi jawaban (String)
     */
    public abstract String[] getOptions();

    /**
     * Mengembalikan indeks jawaban yang benar (0-3)
     * @return indeks dari jawaban yang benar
     */
    public abstract int getAnswerIndex();
}
