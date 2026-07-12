package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.SQLException;
import java.util.List;

public class MainFX extends Application {

    private GestorAlumno admin1 = new GestorAlumno();
    private TableView<AlumnoCalificado> table = new TableView<>();
    private ObservableList<AlumnoCalificado> data = FXCollections.observableArrayList();
    private Label totalAlumnosLabel = new Label("0");

    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.initStyle(StageStyle.UNDECORATED);

        // Iniciamos mostrando la escena de Login
        Scene loginScene = createLoginScene(primaryStage);
        primaryStage.setScene(loginScene);
        primaryStage.setTitle("Acceso al Sistema - UNDC");
        primaryStage.show();
    }

    // ==========================================
    // Escena de Login
    // ==========================================
    private Scene createLoginScene(Stage stage) {
        VBox mainContainer = new VBox(0);
        mainContainer.setStyle("-fx-background-color: #f0f2f5; -fx-padding: 1; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc;");

        // Barra de título para la ventana de Login
        HBox titleBar = createCustomTitleBar(stage, "Ingreso al Sistema");
        mainContainer.getChildren().add(titleBar);

        // Contenedor del formulario de Login (Tarjeta moderna)
        VBox loginCard = new VBox(20);
        loginCard.getStyleClass().add("card");
        loginCard.setAlignment(Pos.CENTER);
        loginCard.setMaxWidth(400);
        loginCard.setPadding(new Insets(30, 40, 30, 40));

        Label lblWelcome = new Label("BIENVENIDO");
        lblWelcome.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e1e1e;");

        Label lblSub = new Label("Control de Comedor EPIS - UNDC");
        lblSub.setStyle("-fx-font-size: 13px; -fx-text-fill: #666666;");

        VBox fields = new VBox(15);
        fields.setAlignment(Pos.CENTER_LEFT);

        Label lblUser = new Label("Usuario:");
        lblUser.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333;");
        TextField txtUser = new TextField();
        txtUser.setPromptText("Ingrese su usuario");

        Label lblPass = new Label("Contraseña:");
        lblPass.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333;");
        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("Ingrese su contraseña");

        fields.getChildren().addAll(lblUser, txtUser, lblPass, txtPass);

        Button btnLogin = new Button("Iniciar Sesión");
        btnLogin.setPrefWidth(Double.MAX_VALUE);

        Label lblError = new Label();
        lblError.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold; -fx-font-size: 13px;");
        lblError.setVisible(false);

        btnLogin.setOnAction(e -> {
            String user = txtUser.getText();
            String pass = txtPass.getText();

            if ("admin0".equals(user) && "Sistemas".equals(pass)) {
                // Credenciales correctas: Cargar el Dashboard principal
                Scene dashboardScene = createDashboardScene(stage);
                stage.setScene(dashboardScene);
                actualizarDatosTabla();
            } else {
                lblError.setText("Usuario o contraseña incorrectos.");
                lblError.setVisible(true);
            }
        });

        loginCard.getChildren().addAll(lblWelcome, lblSub, fields, btnLogin, lblError);

        // Centrar la tarjeta en la ventana
        StackPane rootPane = new StackPane(loginCard);
        rootPane.setPadding(new Insets(50, 0, 50, 0));
        VBox.setVgrow(rootPane, Priority.ALWAYS);

        mainContainer.getChildren().add(rootPane);

        Scene scene = new Scene(mainContainer, 500, 500);
        scene.getStylesheets().add(getClass().getResource("/styleModern.css").toExternalForm());
        return scene;
    }

    // ==========================================
    // Escena del Dashboard Principal
    // ==========================================
    private Scene createDashboardScene(Stage stage) {
        BorderPane root = new BorderPane();

        // Barra de título personalizada
        HBox customTitleBar = createCustomTitleBar(stage, "Comedor Universitario - Ingeniería de Sistemas");
        root.setTop(customTitleBar);

        // Contenedor central con pestañas modernas
        TabPane tabPane = new TabPane();

        Tab tabGestion = createTabGestion();
        Tab tabLista = createTabLista();
        Tab tabBusquedaReportes = createTabBusquedaReportes();

        tabPane.getTabs().addAll(tabGestion, tabLista, tabBusquedaReportes);

        for (Tab tab : tabPane.getTabs()) {
            tab.setClosable(false);
        }

        root.setCenter(tabPane);

        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/styleModern.css").toExternalForm());
        return scene;
    }

    private void actualizarDatosTabla() {
        try {
            System.out.println("[DEBUG] Iniciando actualizarDatosTabla()...");
            List<Alumno> alumnos = admin1.obtenerAlumnos();
            System.out.println("[DEBUG] Alumnos recuperados de la BD: " + (alumnos == null ? "null" : alumnos.size()));
            data.clear();
            if (alumnos != null) {
                for (Alumno a : alumnos) {
                    System.out.println("[DEBUG] Evaluando alumno: " + a.getNombre() + " " + a.getApellido() + " (tipo: " + a.getClass().getName() + ")");
                    if (a instanceof AlumnoCalificado) {
                        data.add((AlumnoCalificado) a);
                        System.out.println("[DEBUG] Alumno agregado a la lista observable.");
                    } else {
                        System.out.println("[DEBUG] ¡El alumno no es instancia de AlumnoCalificado!");
                    }
                }
            }
            System.out.println("[DEBUG] Tamaño final de 'data' (lista observable): " + data.size());
            System.out.println("[DEBUG] TableView setItems: " + (table.getItems() == data ? "Correctamente vinculada" : "¡DESVINCULADA!"));
            actualizarResumenComedor();
        } catch (SQLException e) {
            System.out.println("[ERROR] Error al cargar alumnos: " + e.getMessage());
            mostrarAlerta("Error de Base de Datos", "No se pudo cargar la lista de alumnos:\n" + e.getMessage());
        }
    }

    private HBox createCustomTitleBar(Stage stage, String subtitle) {
        HBox titleBar = new HBox(10);
        titleBar.setId("titleBar");
        titleBar.setPadding(new Insets(5, 10, 5, 10));
        titleBar.setAlignment(Pos.CENTER_LEFT);

        Label logo = new Label("UNDC");
        logo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0277bd; -fx-padding: 0 10 0 0;");

        Label titleLabel = new Label(subtitle);
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        HBox windowButtons = new HBox(8);
        windowButtons.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(windowButtons, Priority.ALWAYS);

        Button btnMin = createCircleButton(Color.web("#fbc02d")); // Amarillo
        btnMin.setOnAction(e -> stage.setIconified(true));

        Button btnMax = createCircleButton(Color.web("#4caf50")); // Verde
        btnMax.setOnAction(e -> stage.setMaximized(!stage.isMaximized()));

        Button btnClose = createCircleButton(Color.web("#e53935")); // Rojo
        btnClose.setOnAction(e -> Platform.exit());

        windowButtons.getChildren().addAll(btnMin, btnMax, btnClose);

        titleBar.getChildren().addAll(logo, titleLabel, windowButtons);

        titleBar.setOnMousePressed(event -> {
            xOffset = stage.getX() - event.getScreenX();
            yOffset = stage.getY() - event.getScreenY();
        });
        titleBar.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() + xOffset);
            stage.setY(event.getScreenY() + yOffset);
        });

        return titleBar;
    }

    private Button createCircleButton(Color color) {
        Button button = new Button();
        button.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        button.setGraphic(new Circle(6, color));
        button.setOnMouseEntered(e -> button.setEffect(new javafx.scene.effect.DropShadow()));
        button.setOnMouseExited(e -> button.setEffect(null));
        return button;
    }

    private Tab createTabGestion() {
        Tab tab = new Tab("Gestión y Datos");

        VBox content = new VBox(20);
        content.setPadding(new Insets(30));

        Label lblHeader = new Label("Gestión de Alumnos y Estado de Comedor");
        lblHeader.getStyleClass().add("header-label");

        HBox dataResumenHBox = new HBox(30);
        dataResumenHBox.setAlignment(Pos.TOP_LEFT);

        GridPane grid = new GridPane();
        grid.getStyleClass().add("card");
        grid.setVgap(15);
        grid.setHgap(20);

        TextField txtNombre = new TextField(); txtNombre.setPromptText("Ingrese Nombres");
        TextField txtApellido = new TextField(); txtApellido.setPromptText("Ingrese Apellidos");
        TextField txtCarrera = new TextField(); txtCarrera.setPromptText("Ingrese Carrera");
        TextField txtCodigo = new TextField(); txtCodigo.setPromptText("Ingrese Código");
        TextField txtEdad = new TextField(); txtEdad.setPromptText("Ingrese Edad");

        addFormRow(grid, "Nombre:", txtNombre, 0);
        addFormRow(grid, "Apellido:", txtApellido, 1);
        addFormRow(grid, "Carrera:", txtCarrera, 2);
        addFormRow(grid, "Código:", txtCodigo, 3);
        addFormRow(grid, "Edad:", txtEdad, 4);

        Button btnGuardar = new Button("Guardar Alumno");
        GridPane.setMargin(btnGuardar, new Insets(15, 0, 0, 0));
        grid.add(btnGuardar, 1, 5);

        Label lblMensajeForm = new Label();
        lblMensajeForm.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        grid.add(lblMensajeForm, 1, 6);

        btnGuardar.setOnAction(e -> {
            try {
                String nombre = txtNombre.getText().trim();
                String apellido = txtApellido.getText().trim();
                String carrera = txtCarrera.getText().trim();
                String strCodigo = txtCodigo.getText().trim();
                String strEdad = txtEdad.getText().trim();

                if (nombre.isEmpty() || apellido.isEmpty() || carrera.isEmpty() || strCodigo.isEmpty() || strEdad.isEmpty()) {
                    mostrarAlerta("Campos vacíos", "Por favor complete todos los datos del alumno.");
                    return;
                }

                long codigo = Long.parseLong(strCodigo);
                int edad = Integer.parseInt(strEdad);

                admin1.agregarAlumno(new AlumnoCalificado(nombre, apellido, carrera, codigo, edad, 0, true));

                lblMensajeForm.setText("✔ Alumno agregado exitosamente.");
                lblMensajeForm.setStyle("-fx-text-fill: #388e3c; -fx-font-weight: bold;");
                txtNombre.clear(); txtApellido.clear(); txtCarrera.clear(); txtCodigo.clear(); txtEdad.clear();
                actualizarDatosTabla();
            } catch (NumberFormatException ex) {
                lblMensajeForm.setText("✖ Error: Código y Edad deben ser números.");
                lblMensajeForm.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
            } catch (Exception ex) {
                mostrarAlerta("Error de Base de Datos", "No se pudo guardar el alumno: " + ex.getMessage());
            }
        });

        VBox resumenComedor = createResumenComedor();
        HBox.setHgrow(resumenComedor, Priority.ALWAYS);

        dataResumenHBox.getChildren().addAll(grid, resumenComedor);
        content.getChildren().addAll(lblHeader, dataResumenHBox);
        tab.setContent(content);
        return tab;
    }

    private void addFormRow(GridPane grid, String labelText, Node inputNode, int row) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");
        grid.add(label, 0, row);
        grid.add(inputNode, 1, row);
    }

    private VBox createResumenComedor() {
        VBox resumen = new VBox(15);
        resumen.setId("resumenCard");
        resumen.setAlignment(Pos.CENTER);
        resumen.setPadding(new Insets(30));

        Label titleResumen = new Label("Total Calificados");
        titleResumen.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #bbdefb;");

        totalAlumnosLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 72));
        totalAlumnosLabel.setStyle("-fx-text-fill: white;");

        Label subtitleTotal = new Label("Ingeniería de Sistemas");
        subtitleTotal.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label yearExample = new Label("Comedor Universitario UNDC");
        yearExample.setStyle("-fx-font-size: 13px; -fx-text-fill: #e0e0e0;");

        resumen.getChildren().addAll(titleResumen, totalAlumnosLabel, subtitleTotal, yearExample);
        return resumen;
    }

    private void actualizarResumenComedor() {
        totalAlumnosLabel.setText(String.valueOf(data.size()));
    }

    private Tab createTabLista() {
        Tab tab = new Tab("Lista de Alumnos");

        VBox content = new VBox(20);
        content.setPadding(new Insets(30));

        Label lblHeader = new Label("Lista de Alumnos Calificados");
        lblHeader.getStyleClass().add("header-label");

        TableColumn<AlumnoCalificado, Long> colCodigo = new TableColumn<>("Código");
        colCodigo.setMinWidth(120);
        colCodigo.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getCodigo()));

        TableColumn<AlumnoCalificado, String> colNombre = new TableColumn<>("Nombres");
        colNombre.setMinWidth(180);
        colNombre.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNombre()));

        TableColumn<AlumnoCalificado, String> colApellido = new TableColumn<>("Apellidos");
        colApellido.setMinWidth(180);
        colApellido.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getApellido()));

        TableColumn<AlumnoCalificado, String> colCarrera = new TableColumn<>("Carrera");
        colCarrera.setMinWidth(180);
        colCarrera.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCarrera()));

        TableColumn<AlumnoCalificado, Integer> colEdad = new TableColumn<>("Edad");
        colEdad.setMinWidth(70);
        colEdad.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getEdad()));

        TableColumn<AlumnoCalificado, Integer> colFaltas = new TableColumn<>("Faltas");
        colFaltas.setMinWidth(70);
        colFaltas.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getFaltas()));

        TableColumn<AlumnoCalificado, Boolean> colCalificado = new TableColumn<>("Calificado");
        colCalificado.setMinWidth(110);
        colCalificado.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().isHorarioAprobado()));
        colCalificado.setCellFactory(column -> new TableCell<AlumnoCalificado, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label status = new Label(item ? "Activo" : "Retirado");
                    status.setStyle(item ? "-fx-background-color: #e8f5e9; -fx-text-fill: #2e7d32; -fx-padding: 3 8; -fx-background-radius: 10; -fx-font-weight: bold;" 
                                         : "-fx-background-color: #ffebee; -fx-text-fill: #c62828; -fx-padding: 3 8; -fx-background-radius: 10; -fx-font-weight: bold;");
                    setGraphic(status);
                }
            }
        });

        table.getColumns().clear();
        table.getColumns().addAll(colCodigo, colNombre, colApellido, colCarrera, colEdad, colFaltas, colCalificado);
        table.setItems(data);
        table.setPlaceholder(new Label("No hay alumnos registrados en el sistema o error de BD."));
        VBox.setVgrow(table, Priority.ALWAYS);

        Button btnActualizar = new Button("Actualizar Lista");
        btnActualizar.getStyleClass().add("btn-secondary");
        btnActualizar.setOnAction(e -> actualizarDatosTabla());

        content.getChildren().addAll(lblHeader, table, btnActualizar);
        return tab;
    }

    private Tab createTabBusquedaReportes() {
        Tab tab = new Tab("Búsqueda y Faltas");

        VBox content = new VBox(25);
        content.setPadding(new Insets(30));

        Label lblHeader = new Label("Búsqueda y Control de Inasistencias");
        lblHeader.getStyleClass().add("header-label");

        // 4.1. Sección de búsqueda y Registro de Falta
        HBox topBox = new HBox(20);
        topBox.setAlignment(Pos.TOP_CENTER);

        VBox searchBox = new VBox(15);
        searchBox.getStyleClass().add("card");
        searchBox.setPrefWidth(450);

        Label lblInstruccion = new Label("Buscar / Registrar Falta:");
        lblInstruccion.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
        
        HBox searchRow = new HBox(10);
        searchRow.setAlignment(Pos.CENTER_LEFT);
        TextField txtCodigoBuscar = new TextField(); 
        txtCodigoBuscar.setPromptText("Código Alumno");
        txtCodigoBuscar.setPrefWidth(150);
        
        Button btnBuscar = new Button("Buscar");
        Button btnFalta = new Button("Falta (+1)");
        btnFalta.getStyleClass().add("btn-danger");
        searchRow.getChildren().addAll(txtCodigoBuscar, btnBuscar, btnFalta);

        Label lblResultadoBusqueda = new Label();
        lblResultadoBusqueda.setStyle("-fx-font-size: 13px; -fx-padding: 12; -fx-border-radius: 6; -fx-background-radius: 6;");
        lblResultadoBusqueda.setVisible(false);
        lblResultadoBusqueda.setPrefWidth(Double.MAX_VALUE);

        btnBuscar.setOnAction(e -> {
            lblResultadoBusqueda.setVisible(false);
            try {
                String val = txtCodigoBuscar.getText().trim();
                if (val.isEmpty()) return;
                long codigo = Long.parseLong(val);
                AlumnoCalificado encontrado = admin1.buscarAlumnos(codigo);
                lblResultadoBusqueda.setText("✔ Encontrado:\n" + encontrado.getNombre() + " " + encontrado.getApellido() + "\nCarrera: " + encontrado.getCarrera() + "\nFaltas: " + encontrado.getFaltas() + "\nCódigo: " + encontrado.getCodigo());
                lblResultadoBusqueda.setStyle("-fx-background-color: #e3f2fd; -fx-border-color: #bbdefb; -fx-text-fill: #0277bd; -fx-font-weight: bold;");
                lblResultadoBusqueda.setVisible(true);
            } catch (NumberFormatException ex) {
                lblResultadoBusqueda.setText("Ingrese un código numérico válido.");
                lblResultadoBusqueda.setStyle("-fx-background-color: #ffebee; -fx-border-color: #ffcdd2; -fx-text-fill: #c62828; -fx-font-weight: bold;");
                lblResultadoBusqueda.setVisible(true);
            } catch (Exception ex) {
                lblResultadoBusqueda.setText(ex.getMessage());
                lblResultadoBusqueda.setStyle("-fx-background-color: #ffebee; -fx-border-color: #ffcdd2; -fx-text-fill: #c62828; -fx-font-weight: bold;");
                lblResultadoBusqueda.setVisible(true);
            }
        });

        btnFalta.setOnAction(e -> {
            lblResultadoBusqueda.setVisible(false);
            try {
                String val = txtCodigoBuscar.getText().trim();
                if (val.isEmpty()) return;
                long codigo = Long.parseLong(val);
                AlumnoCalificado alm = admin1.ponerFalta(codigo);

                actualizarDatosTabla(); 

                if (alm.getFaltas() > 3) {
                    lblResultadoBusqueda.setText("⚠ ¡El alumno " + alm.getNombre() + " superó las 3 faltas y fue retirado!");
                    lblResultadoBusqueda.setStyle("-fx-background-color: #ffebee; -fx-border-color: #ef9a9a; -fx-text-fill: #c62828; -fx-font-weight: bold;");
                } else {
                    lblResultadoBusqueda.setText("✔ Falta registrada. Total de faltas: " + alm.getFaltas());
                    lblResultadoBusqueda.setStyle("-fx-background-color: #e8f5e9; -fx-border-color: #a5d6a7; -fx-text-fill: #2e7d32; -fx-font-weight: bold;");
                }
                lblResultadoBusqueda.setVisible(true);
            } catch (NumberFormatException ex) {
                lblResultadoBusqueda.setText("Ingrese un código numérico válido.");
                lblResultadoBusqueda.setStyle("-fx-background-color: #ffebee; -fx-border-color: #ffcdd2; -fx-text-fill: #c62828; -fx-font-weight: bold;");
                lblResultadoBusqueda.setVisible(true);
            } catch (Exception ex) {
                lblResultadoBusqueda.setText(ex.getMessage());
                lblResultadoBusqueda.setStyle("-fx-background-color: #ffebee; -fx-border-color: #ffcdd2; -fx-text-fill: #c62828; -fx-font-weight: bold;");
                lblResultadoBusqueda.setVisible(true);
            }
        });

        searchBox.getChildren().addAll(lblInstruccion, searchRow, lblResultadoBusqueda);

        // 4.2. Sección para Revocar Inasistencias (Quitar Faltas)
        VBox revokeBox = new VBox(15);
        revokeBox.getStyleClass().add("card");
        revokeBox.setPrefWidth(450);

        Label lblRevokeTitle = new Label("Revocar Inasistencia (Quitar Faltas):");
        lblRevokeTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");

        HBox revokeRow1 = new HBox(10);
        revokeRow1.setAlignment(Pos.CENTER_LEFT);
        TextField txtCodigoRevocar = new TextField();
        txtCodigoRevocar.setPromptText("Código Alumno");
        txtCodigoRevocar.setPrefWidth(150);
        Button btnVerificarRevoke = new Button("Verificar Alumno");
        btnVerificarRevoke.getStyleClass().add("btn-secondary");
        revokeRow1.getChildren().addAll(txtCodigoRevocar, btnVerificarRevoke);

        VBox revokeControls = new VBox(10);
        revokeControls.setVisible(false);

        Label lblAlumnoInfo = new Label();
        lblAlumnoInfo.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333;");

        HBox revokeRow2 = new HBox(10);
        revokeRow2.setAlignment(Pos.CENTER_LEFT);
        Label lblQuitarCant = new Label("Faltas a quitar:");
        Spinner<Integer> spinQuitar = new Spinner<>(1, 10, 1);
        spinQuitar.setPrefWidth(80);
        Button btnConfirmarRevoke = new Button("Quitar Faltas");
        revokeRow2.getChildren().addAll(lblQuitarCant, spinQuitar, btnConfirmarRevoke);

        revokeControls.getChildren().addAll(lblAlumnoInfo, revokeRow2);

        Label lblResultadoRevoke = new Label();
        lblResultadoRevoke.setStyle("-fx-font-size: 13px; -fx-padding: 12; -fx-border-radius: 6; -fx-background-radius: 6;");
        lblResultadoRevoke.setVisible(false);

        btnVerificarRevoke.setOnAction(e -> {
            lblResultadoRevoke.setVisible(false);
            revokeControls.setVisible(false);
            try {
                String val = txtCodigoRevocar.getText().trim();
                if (val.isEmpty()) return;
                long codigo = Long.parseLong(val);
                AlumnoCalificado encontrado = admin1.buscarAlumnos(codigo);
                lblAlumnoInfo.setText("Alumno: " + encontrado.getNombre() + " " + encontrado.getApellido() + "\nFaltas actuales: " + encontrado.getFaltas());
                spinQuitar.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Math.max(1, encontrado.getFaltas()), 1));
                revokeControls.setVisible(true);
            } catch (NumberFormatException ex) {
                lblResultadoRevoke.setText("Ingrese un código válido.");
                lblResultadoRevoke.setStyle("-fx-background-color: #ffebee; -fx-border-color: #ffcdd2; -fx-text-fill: #c62828; -fx-font-weight: bold;");
                lblResultadoRevoke.setVisible(true);
            } catch (Exception ex) {
                lblResultadoRevoke.setText(ex.getMessage());
                lblResultadoRevoke.setStyle("-fx-background-color: #ffebee; -fx-border-color: #ffcdd2; -fx-text-fill: #c62828; -fx-font-weight: bold;");
                lblResultadoRevoke.setVisible(true);
            }
        });

        btnConfirmarRevoke.setOnAction(e -> {
            try {
                long codigo = Long.parseLong(txtCodigoRevocar.getText().trim());
                int cantidad = spinQuitar.getValue();
                AlumnoCalificado alm = admin1.revocarFalta(codigo, cantidad);

                actualizarDatosTabla();

                lblResultadoRevoke.setText("✔ Faltas reducidas. Total actual de faltas: " + alm.getFaltas());
                lblResultadoRevoke.setStyle("-fx-background-color: #e8f5e9; -fx-border-color: #a5d6a7; -fx-text-fill: #2e7d32; -fx-font-weight: bold;");
                lblResultadoRevoke.setVisible(true);

                // Actualizar info en pantalla
                lblAlumnoInfo.setText("Alumno: " + alm.getNombre() + " " + alm.getApellido() + "\nFaltas actuales: " + alm.getFaltas());
                spinQuitar.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Math.max(1, alm.getFaltas()), 1));
            } catch (Exception ex) {
                lblResultadoRevoke.setText("Error: " + ex.getMessage());
                lblResultadoRevoke.setStyle("-fx-background-color: #ffebee; -fx-border-color: #ffcdd2; -fx-text-fill: #c62828; -fx-font-weight: bold;");
                lblResultadoRevoke.setVisible(true);
            }
        });

        revokeBox.getChildren().addAll(lblRevokeTitle, revokeRow1, revokeControls, lblResultadoRevoke);

        topBox.getChildren().addAll(searchBox, revokeBox);
        content.getChildren().addAll(lblHeader, topBox);
        tab.setContent(content);
        return tab;
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}