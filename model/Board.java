package model;

public class Board {
    public static final int SIZE = 30;

    public boolean isSafeCell(int index) {
        return index == 25; // بيت الجمال (تقدّر توسعها)
    }

    public boolean isSpecialCell(int index) {
        return index >= 25;
    }
    // تأثير الخانة على الحركة
    public int getCellEffect(int index, Piece piece) {
        switch (index) {
            case 14: // 15 بالـ 0-indexed
                return 0; // Rebirth: يتم التعامل معها عند إعادة حجر
            case 25: // 26 House of Happiness
                return 0; // ممنوع القفز → نتحقق في GameController
            case 26: // 27 House of Water
                return -10; // ترجع إلى House of Rebirth (رقم 15)
            case 27: // 28 House of Three Truths
                return 3; // شرط الخروج → يتحقق في Controller
            case 28: // 29 House of Re-Atoum
                return 2; // شرط الخروج → يتحقق في Controller
            case 29: // 30 House of Horus
                return -15; // يمكن الخروج بأي رمية → نتحكم في Controller
            default:
                return 0; // لا تأثير
        }
    }



    public boolean canExit(int position, int roll) {
        return position + roll == SIZE;
    }
}
