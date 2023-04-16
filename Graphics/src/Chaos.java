import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.util.*;
import java.util.List;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class Chaos {
    public static final int DOWN_SHIFT = 30;
    JFrame frame;
    DrawPanel drawPanel;
    Random rnd;
    private final int fieldSize;
    private final int pointCount;
    private final boolean changeDirection;
    private final List<Point> points;
    private final boolean showBoom;
    private final int maxSpeed;
    private int whiteCount;
    private int redCount;
    private Long prevDim;
    private Long start;
    private List<Integer> dimensions;

    Chaos(int fieldSize, int pointCount, int redCount, boolean changeDirection, boolean showBoom, int maxSpeed) {
        if (fieldSize < 1000) {
            this.fieldSize = 1000;
        } else {
            this.fieldSize = fieldSize;
        }
        this.pointCount = pointCount;
        if (redCount > pointCount) {
            this.redCount = pointCount;
        } else if (redCount == 0) {
            this.redCount = 1;
        } else {
            this.redCount = redCount;
        }
        this.whiteCount = pointCount - redCount;
        this.changeDirection = changeDirection;
        this.showBoom = showBoom;
        this.points = new ArrayList<>();
        this.rnd = new Random();
        this.start = System.currentTimeMillis();
        this.prevDim = System.currentTimeMillis();
        this.dimensions = new ArrayList<>();
        if (maxSpeed == 0) {
            this.maxSpeed = 1;
        } else {
            this.maxSpeed = maxSpeed;
        }
        frame = new JFrame("Chaos. Points: " + pointCount + ". Red: " + redCount + ".");
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        drawPanel = new DrawPanel();
        frame.getContentPane().add(BorderLayout.CENTER, drawPanel);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setSize(fieldSize, fieldSize);
        frame.setLocation(0, 0);
        createPoints();
        moveAll();
    }

    public Duration getDuration() {
        return Duration.ofMillis(System.currentTimeMillis() - start);
    }

    public void createPoints() {
        for (int i = 1; i <= pointCount; i++) {
            int x = rnd.nextInt(fieldSize - 1);
            int y = rnd.nextInt(fieldSize - 1);
            int stepX = rnd.nextInt(maxSpeed) + 1;
            int stepY = rnd.nextInt(maxSpeed) + 1;
            boolean down = rnd.nextBoolean();
            boolean right = rnd.nextBoolean();
            points.add(new Point(x, y, stepX, stepY, down, right));
        }
        int nowRed = 0;
        while (nowRed < redCount) {
            Point point = points.get(rnd.nextInt(pointCount));
            if (point.white) {
                point.white = false;
                nowRed++;
            }
        }
    }

    class DrawPanel extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
            printHeader(g);
            printChart(g);
            try {
                for (Point point : points) {
                    if (point.white) {
                        g.setColor(Color.WHITE);
                        g.fillOval(point.x, point.y, 2, 2);
                    } else {
                        if (point.boom) {
                            g.setColor(Color.YELLOW);
                            g.fillOval(point.x, point.y, 20, 20);
                            point.boomDelay++;
                            if (point.boomDelay == 50) point.boom = false;
                        } else {
                            g.setColor(Color.RED);
                            g.fillOval(point.x, point.y, 2, 2);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Упс... : " + e.fillInStackTrace());
            }
        }

        private void printChart(Graphics g) {
            if (((System.currentTimeMillis() - prevDim)) > 15L) {
                prevDim = System.currentTimeMillis();
                dimensions.add(redCount);
            }
            g.setColor(Color.PINK);
            int startPos = 150;
            int dimCount = 0;
            if (dimensions.size() > 70) {
                dimCount = 70;
            } else {
                dimCount = dimensions.size();
            }
            int minVal = whiteCount + redCount;
            int maxVal = 0;
            for (Integer val : dimensions) {
                if (val < minVal) minVal = val;
                if (val > maxVal) maxVal = val;
            }
            int step = (maxVal - minVal) / 35; // Цена деления
            if (step < 1) step = 1;
            for (int i = 0; i < dimCount; i++) {
                int dimStep = dimensions.size() / 70;
                if (dimStep < 1) dimStep = 1;
                int shiftCount = dimensions.get(i * dimStep) / step;
                if (shiftCount > 35) shiftCount = 35;
                g.fillRect(startPos, 40 - (shiftCount), 10, 40 - (40 - (shiftCount)));
                startPos += 12;
            }
        }

        private void printHeader(Graphics g) {
            g.setFont(new Font("Monospaced", Font.PLAIN, 12));
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, this.getWidth(), 50);
            g.setColor(Color.BLACK);
            g.drawString("   White: " + whiteCount, 10, 15);
            g.drawString("     Red: " + redCount, 10, 30);
            Duration duration = Duration.ofMillis(System.currentTimeMillis() - start);
            String min = String.valueOf(duration.toMinutesPart());
            if (min.length() == 1) min = "0" + min;
            String sec = String.valueOf(duration.toSecondsPart());
            if (sec.length() == 1) sec = "0" + sec;
            g.drawString("Duration: " + min + ":" + sec, 10, 45);
        }
    }

    public void moveAll() {
        while (true) {
            int myColors[][] = new int[fieldSize][fieldSize];
            for (Point point : points) {
                // Движение
                if (point.down && point.y < fieldSize - 1) point.y += point.stepY;
                if (!point.down && point.y > 1) point.y -= point.stepY;
                if (point.right && point.x < fieldSize - 1) point.x += point.stepX;
                if (!point.right && point.x > 1) point.x -= point.stepX;
                // Смены напрявлений
                if (point.down && point.y >= fieldSize - DOWN_SHIFT) {
                    point.down = false;
                    point.y = fieldSize - DOWN_SHIFT;
                }
                if (!point.down && point.y <= 50) {
                    point.down = true;
                    point.y = 50;
                }
                if (point.right && point.x >= fieldSize - 1) {
                    point.right = false;
                    point.x = fieldSize - 1;
                }
                if (!point.right && point.x <= 1) {
                    point.right = true;
                    point.x = 1;
                }
            }
            // Смена цвета (собираем все)
            for (int i = 0; i < pointCount; i++) {
                Point point = points.get(i);
                if (myColors[point.x][point.y] != 1 && myColors[point.x][point.y] != 2) {
                    if (point.white) {
                        myColors[point.x][point.y] = 1;
                    } else {
                        myColors[point.x][point.y] = 2;
                    }
                } else if (myColors[point.x][point.y] == 1 && !point.white) {
                    myColors[point.x][point.y] = 2;
                    //Она красная и там уже есть не красная, красная меняет направление
                    if (changeDirection) {
                        point.right = !point.right;
                        point.down = !point.down;
                        point.stepX = rnd.nextInt(maxSpeed) + 1;
                        point.stepY = rnd.nextInt(maxSpeed) + 1;
                    }
                }
            }
            // Меняется ли цвет?
            for (Point point : points) {
                if (point.white && myColors[point.x][point.y] == 2) {
                    point.white = false;
                    whiteCount--;
                    redCount++;
                    if (changeDirection) {
                        point.right = !point.right;
                        point.down = !point.down;
                        point.stepX = rnd.nextInt(maxSpeed) + 1;
                        point.stepY = rnd.nextInt(maxSpeed) + 1;
                    }
                    if (showBoom) {
                        point.boom = true;
                    }
                }
            }
            frame.repaint();
            if (whiteCount == 0) {
                break;
            }
            try {
                Thread.sleep(10);
            } catch (Exception exc) {
                System.exit(0);
            }
        }

    }
}