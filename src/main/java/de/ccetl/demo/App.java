package de.ccetl.demo;

import de.ccetl.jparticles.core.Renderer;
import de.ccetl.jparticles.core.shape.Shape;
import de.ccetl.jparticles.core.shape.ShapeType;
import de.ccetl.jparticles.event.MouseEvent;
import de.ccetl.jparticles.systems.LineSystem;
import de.ccetl.jparticles.systems.ParticleSystem;
import de.ccetl.jparticles.systems.SnowSystem;
import de.ccetl.jparticles.types.particle.Direction;
import de.ccetl.jparticles.types.particle.LineShape;
import de.ccetl.jparticles.types.particle.Obstacle;
import de.ccetl.jparticles.types.particle.SpawnRegion;
import de.ccetl.jparticles.util.Utils;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class App extends Application {
    private Stage stage;
    private BorderPane particles;
    private BorderPane snow;
    private BorderPane lines;
    private AnimationTimer timer;
    private Effect current;
    private LineSystem line;
    private double mouseX;
    private double mouseY;
    private boolean initialized;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setTitle("JParticles4j");

        current = Effect.PARTICLES;
        initParticles();
        Scene scene = new Scene(particles, 1800, 1000);

        scene.setOnMouseMoved(mouseEvent -> {
            mouseX = mouseEvent.getX();
            mouseY = mouseEvent.getY();
        });
        stage.setScene(scene);
        stage.show();
        initialized = true;
    }

    private void initLines() {
        lines = new BorderPane();
        Canvas canvas = new Canvas(1600, 1000);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        LineSystem.DefaultConfig config = new LineSystem.DefaultConfig() {
            @Override
            public Renderer getRenderer() {
                return new JavaFxRenderer(gc);
            }

            @Override
            public Supplier<Integer> getColorSupplier() {
                return () -> getColor(Utils.getRandomInRange(0, 1), Utils.getRandomInRange(0, 1), Utils.getRandomInRange(0, 1), Utils.getRandomInRange(0.3, 1));
            }
        };
        line = new LineSystem(config, 1600, 1000);
        lines.getChildren().add(canvas);
        line.start();

        ScrollPane scrollPane = new ScrollPane();
        VBox sidebar = new VBox();
        sidebar.setPadding(new Insets(10));
        sidebar.setSpacing(10);

        addSwitch(sidebar);
        addButton(sidebar, "Reset", line::bootstrap);
        addButton(sidebar, "Pause", line::pause);
        addButton(sidebar, "Start", line::start);
        addSlider(sidebar, "Number", config.getNumber(), 0, 1000, v -> config.setNumber(v.intValue()));
        addSlider(sidebar, "Max Width", config.getMaxWidth(), 0, 10, config::setMaxWidth);
        addSlider(sidebar, "Min Width", config.getMinWidth(), 0, 10, config::setMinWidth);
        addSlider(sidebar, "Max Speed", config.getMaxSpeed(), 0, 10, config::setMaxSpeed);
        addSlider(sidebar, "Min Speed", config.getMinSpeed(), 0, 10, config::setMinSpeed);
        addSlider(sidebar, "Max Degree", config.getMaxDegree(), 0, 180, config::setMaxDegree);
        addSlider(sidebar, "Min Degree", config.getMinDegree(), 0, 180, config::setMinDegree);
        addCheckBox(sidebar, "Create On Click", config.isCreateOnClick(), config::setCreateOnClick);
        addSlider(sidebar, "Number Of Creations", config.getNumberOfCreations(), 0, 10, v -> config.setNumberOfCreations(v.intValue()));
        addCheckBox(sidebar, "Remove On Overflow", config.isRemoveOnOverflow(), config::setRemoveOnOverflow);
        addSlider(sidebar, "Overflow Compensation", config.getOverflowCompensation(), 0, 100, config::setOverflowCompensation);
        addSlider(sidebar, "Reserved Lines", config.getReservedLines(), 0, 20, v -> config.setReservedLines(v.intValue()));

        scrollPane.setContent(sidebar);
        scrollPane.setPrefWidth(200);
        lines.setRight(scrollPane);

        timer = new AnimationTimer() {
            public void handle(long currentNanoTime) {
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                line.draw(mouseX, mouseY);
            }
        };
        timer.start();
    }

    private void initSnow() {
        snow = new BorderPane();
        Canvas canvas = new Canvas(1600, 1000);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        SnowSystem.DefaultConfig config = new SnowSystem.DefaultConfig() {
            @Override
            public Renderer getRenderer() {
                return new JavaFxRenderer(gc);
            }
        };
        SnowSystem snow = new SnowSystem(config, 1600, 1000);
        this.snow.getChildren().add(canvas);
        snow.start();

        ScrollPane scrollPane = new ScrollPane();
        VBox sidebar = new VBox();
        sidebar.setPadding(new Insets(10));
        sidebar.setSpacing(10);

        addSwitch(sidebar);
        addButton(sidebar, "Reset", snow::bootstrap);
        addButton(sidebar, "Pause", snow::pause);
        addButton(sidebar, "Start", snow::start);
        addDropDown(sidebar, "Shape", ShapeType.CIRCLE, ShapeType.values(), v -> config.setShapeSupplier(() -> {
            Shape shape = new Shape(v);
            shape.setId(2);
            shape.setDent(4);
            shape.setSides(5);
            return shape;
        }));
        addSlider(sidebar, "Number", config.getNumber(), 0, 1000, v -> config.setNumber(v.intValue()));
        addSlider(sidebar, "Min Radius", config.getMinRadius(), 0, 100, config::setMinRadius);
        addSlider(sidebar, "Max Radius", config.getMaxRadius(), 0, 100, config::setMaxRadius);
        addSlider(sidebar, "Min Speed", config.getMinSpeed(), 0, 1, config::setMinSpeed);
        addSlider(sidebar, "Max Speed", config.getMaxSpeed(), 0, 1, config::setMaxSpeed);
        addCheckBox(sidebar, "First Random", config.isFirstRandom(), config::setFirstRandom);
        addCheckBox(sidebar, "strict", config.isStrict(), config::setStrict);
        addCheckBox(sidebar, "Swing", config.isSwing(), config::setSwing);
        addSlider(sidebar, "Duration", config.getDuration(), 0, 5000, config::setDuration);
        addButton(sidebar, "Fall Again", snow::fallAgain);
        addSlider(sidebar, "Interval", config.getSwingInterval(), 0, 5000, v -> config.setSwingInterval(v.intValue()));
        addSlider(sidebar, "Probability", config.getSwingProbability(), 0, 1, config::setSwingProbability);

        scrollPane.setContent(sidebar);
        scrollPane.setPrefWidth(200);
        this.snow.setRight(scrollPane);

        timer = new AnimationTimer() {
            public void handle(long currentNanoTime) {
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                gc.setFill(Color.LIGHTBLUE);
                gc.fillRect(0, 0, 1600, 1000);
                snow.draw(mouseX, mouseY); // Call render on each frame
            }
        };
        timer.start();
    }

    private void initParticles() {
        particles = new BorderPane();
        Canvas canvas = new Canvas(1600, 1000);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        ParticleSystem.DefaultConfig config = new ParticleSystem.DefaultConfig() {
            @Override
            public Renderer getRenderer() {
                return new JavaFxRenderer(gc);
            }

            @Override
            public Supplier<Integer> getColorSupplier() {
                return () -> getColor(Utils.getRandomInRange(0, 1), Utils.getRandomInRange(0, 1), Utils.getRandomInRange(0, 1), Utils.getRandomInRange(0.3, 1));
            }
        };
        ParticleSystem particle = new ParticleSystem(config, 1600, 1000);
        particle.start();
        particles.getChildren().add(canvas);

        ScrollPane scrollPane = new ScrollPane();
        VBox sidebar = new VBox();
        sidebar.setPadding(new Insets(10));
        sidebar.setSpacing(10);

        addSwitch(sidebar);
        addButton(sidebar, "Reset", particle::bootstrap);
        addButton(sidebar, "Pause", particle::pause);
        addButton(sidebar, "Start", particle::start);
        addDropDown(sidebar, "Shape", ShapeType.CIRCLE, ShapeType.values(), v -> config.setShapeSupplier(() -> {
            Shape shape = new Shape(v);
            shape.setId(1);
            shape.setDent(4);
            shape.setSides(5);
            return shape;
        }));
        addSlider(sidebar, "Number", config.getNumber(), 0, 1000, v -> config.setNumber(v.intValue()));
        addSlider(sidebar, "Min Radius", config.getMinRadius(), 0, 100, config::setMinRadius);
        addSlider(sidebar, "Max Radius", config.getMaxRadius(), 0, 100, config::setMaxRadius);
        addSlider(sidebar, "Min Speed", config.getMinSpeed(), 0, 100, config::setMinSpeed);
        addSlider(sidebar, "Max Speed", config.getMaxRadius(), 0, 100, config::setMaxSpeed);
        addDropDown(sidebar, "Direction", config.getDirection(), Direction.values(), config::setDirection);
        addDropDown(sidebar, "Spawn Region", config.getSpawnRegion(), SpawnRegion.values(), config::setSpawnRegion);
        addDropDown(sidebar, "Edge", config.getCollisionEdge(), Obstacle.values(), config::setCollisionEdge);
        addDropDown(sidebar, "Other", config.getCollisionIntern(), Obstacle.values(), config::setCollisionIntern);
        addSlider(sidebar, "Proximity", config.getProximity(), 0, 100, config::setProximity);
        addSlider(sidebar, "Range", config.getRange(), 0, 5000, config::setRange);
        addSlider(sidebar, "Line Width", config.getLineWidth(), 0, 10, config::setLineWidth);
        addDropDown(sidebar, "Line Shape", config.getLineShape(), LineShape.values(), config::setLineShape);
        addCheckBox(sidebar, "Center Lines", config.isCenterLines(), config::setCenterLines);
        addCheckBox(sidebar, "Parallax", config.isParallax(), config::setParallax);
        addIntegerArrayInput(sidebar, "Parallax-Layers", "1, 2, 3", v -> config.setParallaxLayer(Arrays.stream(v).mapToInt(Integer::intValue).toArray()));
        addSlider(sidebar, "Strength", config.getParallaxStrength(), 0, 100, config::setParallaxStrength);
        addCheckBox(sidebar, "Hover Repulse", config.isHoverRepulse(), config::setHoverRepulse);
        addSlider(sidebar, "Repulse radius", config.getRepulseRadius(), 0, 500, config::setRepulseRadius);

        scrollPane.setContent(sidebar);
        scrollPane.setPrefWidth(200);
        particles.setRight(scrollPane);

        timer = new AnimationTimer() {
            public void handle(long currentNanoTime) {
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                particle.draw(mouseX, mouseY);
            }
        };
        timer.start();
    }

    private void addButton(VBox vbox, String label, Runnable onAction) {
        Button button = new Button(label);
        button.setOnAction(e -> onAction.run());
        vbox.getChildren().add(button);
    }

    private void addSlider(VBox vbox, String label, double initial, double min, double max, Consumer<Double> onChange) {
        Slider slider = new Slider(min, max, (max - min) / 2);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit((max - min) / 4);
        slider.setBlockIncrement((max - min) / 20);
        Label titleLabel = new Label(label + ": ");
        Label valueLabel = new Label(Double.toString(slider.getValue()));

        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            valueLabel.setText(String.format("%.2f", newValue));
            onChange.accept(newValue.doubleValue());
        });

        slider.setValue(initial);
        vbox.getChildren().addAll(titleLabel, slider, valueLabel);
    }

    private void addCheckBox(VBox vbox, String label, boolean initial, Consumer<Boolean> onChange) {
        CheckBox checkBox = new CheckBox(label);
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> onChange.accept(newValue));
        checkBox.selectedProperty().setValue(initial);
        vbox.getChildren().add(checkBox);
    }

    private <T> void addDropDown(VBox vbox, String label, T initial, T[] values, Consumer<T> onChange) {
        Label titleLabel = new Label(label + ": ");
        ChoiceBox<Object> choiceBox = new ChoiceBox<>(FXCollections.observableArrayList(values));
        choiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> onChange.accept((T) newValue));
        choiceBox.setValue(initial);
        vbox.getChildren().addAll(titleLabel, choiceBox);
    }

    private void addIntegerArrayInput(VBox vbox, String label, String initial, Consumer<Integer[]> onChange) {
        Label titleLabel = new Label(label + ": ");
        TextField integerArrayInput = new TextField();
        integerArrayInput.setText(initial);
        integerArrayInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                onChange.accept(parseIntegerArrayInput(integerArrayInput.getText()));
            }
        });
        vbox.getChildren().addAll(titleLabel, integerArrayInput);
    }

    private void addSwitch(VBox vbox) {
        addDropDown(vbox, "Scene", current, Effect.values(), v -> {
            if (!initialized || current == v) {
                return;
            }

            initialized = false;
            current = v;
            AtomicReference<Scene> scene = new AtomicReference<>();
            switch (v) {
                case SNOW:
                    initSnow();
                    scene.set(new Scene(snow, 1800, 1000));
                    break;
                case LINES:
                    initLines();
                    scene.set(new Scene(lines, 1800, 1000));
                    scene.get().setOnMousePressed(event -> {
                        if (mouseY < 1000 && mouseX < 1800) {
                            line.onMouseCLick(new MouseEvent(mouseX));
                        }
                    });
                    line.start();
                    break;
                case PARTICLES:
                    initParticles();
                    scene.set(new Scene(particles, 1800, 1000));
                    break;
            }
            scene.get().setOnMouseMoved(mouseEvent -> {
                mouseX = mouseEvent.getX();
                mouseY = mouseEvent.getY();
            });
            stage.setScene(scene.get());
            initialized = true;
        });
    }

    private Integer[] parseIntegerArrayInput(String input) {
        String[] parts = input.split(",");
        Integer[] array = new Integer[parts.length];
        for (int i = 0; i < parts.length; i++) {
            try {
                array[i] = Integer.parseInt(parts[i].trim());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return array;
    }

    private int getColor(double r, double g, double b, double a) {
        return getColor255((int) (r * 255D + 0.5), (int) (g * 255D + 0.5), (int) (b * 255D + 0.5), (int) (a * 255D + 0.5));
    }

    private int getColor255(int r, int g, int b, int a) {
        return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF) << 0);
    }

    public static void main(String[] args) {
        launch();
    }
}
