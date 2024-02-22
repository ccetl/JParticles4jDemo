package de.ccetl.demo;

import de.ccetl.jparticles.core.Renderer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

public class JavaFxRenderer implements Renderer {
    private static final Map<Integer, ImageView> IMAGE_MAP = new HashMap<>();
    private final GraphicsContext gc;

    public JavaFxRenderer(GraphicsContext gc) {
        this.gc = gc;
    }

    @Override
    public void drawLine(double x, double y, double x1, double y1, double width, int color) {
        gc.setStroke(Color.rgb((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, ((color >> 24) & 0xFF) / 255D));
        gc.setLineWidth(width);
        gc.strokeLine(x, y, x1, y1);
    }

    @Override
    public void drawLineRotated(double x, double y, double x1, double y1, double translationX, double translationY, double width, double radians, int color) {
        gc.save();
        gc.setStroke(Color.rgb((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, ((color >> 24) & 0xFF) / 255D));
        gc.setLineWidth(width);
        gc.translate(translationX, translationY);
        gc.rotate(Math.toDegrees(radians));
        gc.strokeLine(x, y, x1, y1);
        gc.restore();
    }

    @Override
    public void drawCircle(double x, double y, double radius, int color) {
        gc.setFill(Color.rgb((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, ((color >> 24) & 0xFF) / 255D));
        gc.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);
    }

    @Override
    public void drawTriangle(double x, double y, double radius, int color) {
        gc.setFill(Color.rgb((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, ((color >> 24) & 0xFF) / 255D));
        double[] xPoints = {x, x + radius, x - radius};
        double[] yPoints = {y - radius, y + radius, y + radius};
        gc.fillPolygon(xPoints, yPoints, 3);
    }

    @Override
    public void drawStar(double x, double y, double radius, int sides, double dent, int color) {
        gc.setFill(Color.rgb((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, ((color >> 24) & 0xFF) / 255D));
        double innerRadius = radius - dent * dent;
        double angleIncrement = Math.PI / sides;
        double[] xPoints = new double[(int) (2 * sides)];
        double[] yPoints = new double[(int) (2 * sides)];
        for (int i = 0; i < 2 * sides; i++) {
            double angle = i * angleIncrement - Math.PI / 2;
            double r = (i % 2 == 0) ? radius : innerRadius;
            xPoints[i] = x + r * Math.cos(angle);
            yPoints[i] = y + r * Math.sin(angle);
        }
        gc.fillPolygon(xPoints, yPoints, (int) (2 * sides));
    }

    @Override
    public void drawImage(double x, double y, double radius, int id) {
        gc.drawImage(IMAGE_MAP.get(id).getImage(), x - radius, y - radius, radius * 2, radius * 2);
    }

    static {
        new Thread(() -> {
            IMAGE_MAP.put(1, new ImageView(new Image("https://raw.githubusercontent.com/Barrior/assets/main/bubble-colorful.png")));
            IMAGE_MAP.put(2, new ImageView(new Image("https://raw.githubusercontent.com/Barrior/assets/main/gift.png")));
        }).start();
    }
}
