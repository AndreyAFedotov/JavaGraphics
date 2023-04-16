public class Main {
    public static final int FIELD_SIZE = 1000;    // Размер поля (от 1000)
    public static final int POINT_COUNT = 1000;    // Количество точек
    public static final int RED_COUNT = 1;      // Количество красных
    public static final boolean CH_DIRECTION = true;    // Жертва (пойманный красным) меняет направление
    public static final boolean SHOW_BOOM = true;    // Показать столкновения
    public static final int MAX_SPEED = 5;           // Случайная скорость на такт, от 1 (1 - MAX_SPEED)

    public static void main(String[] args) {
        Chaos chaos = new Chaos(FIELD_SIZE, POINT_COUNT, RED_COUNT, CH_DIRECTION, SHOW_BOOM, MAX_SPEED);
    }
}
