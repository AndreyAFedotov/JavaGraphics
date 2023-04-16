public class Point {
    public int x;
    public int y;
    public int stepX;
    public int stepY;
    public boolean down;
    public boolean right;
    public boolean white;
    public boolean boom;
    public int boomDelay;

    Point(int x, int y, int stepX, int stepY, boolean down, boolean right){
        this.x = x;
        this.y = y;
        this.stepX = stepX;
        this.stepY = stepY;
        this.down = down;
        this.right = right;
        this.white = true;
        this.boom = false;
        this.boomDelay = 0;
    }
}
