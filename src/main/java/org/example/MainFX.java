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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class MainFX extends Application {

    private static final int INTERVALO_SINCRONIZACION_SEG = 15;

    private GestorAlumno admin1 = new GestorAlumno();
    private TableView<AlumnoCalificado> table = new TableView<>();
    private ObservableList<AlumnoCalificado> data = FXCollections.observableArrayList();
    private Label totalAlumnosLabel = new Label("0");

    private HiloSincronizacion hiloSincronizacion;
    private boolean operacionEnCurso = false;
    private ProgressIndicator progressCarga = new ProgressIndicator();
    private Label lblEstadoSistema = new Label("Sistema listo");

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
        actualizarDatosTabla(false);
    }

    private void actualizarDatosTabla(boolean silencioso) {
        if (operacionEnCurso) {
            return;
        }

        ejecutarTarea(
                silencioso ? "Sincronizando lista de alumnos..." : "Cargando lista de alumnos...",
                () -> List.copyOf(admin1.obtenerAlumnos()),
                calificados -> {
                    data.setAll(calificados);
                    actualizarResumenComedor();
                    lblEstadoSistema.setText("Lista actualizada (" + calificados.size() + " alumnos)");
                },
                error -> {
                    lblEstadoSistema.setText("No se pudo actualizar la lista");
                    if (!silencioso) {
                        mostrarAlerta("Error de Base de Datos", "No se pudo cargar la lista de alumnos:\n" + error.getMessage());
                    }
                },
                "HiloCargaAlumnos"
        );
    }


    private <T> void ejecutarTarea(String mensajeEstado, Callable<T> trabajo, java.util.function.Consumer<T> alCompletar,
                                   java.util.function.Consumer<Exception> alFallar, String nombreHilo, Node... controles) {
        if (operacionEnCurso) {
            return;
        }

        operacionEnCurso = true;
        establecerControles(false, controles);
        lblEstadoSistema.setText(mensajeEstado);
        progressCarga.setVisible(true);

        TareaComedor<T> tarea = new TareaComedor<>(
                nombreHilo,
                trabajo,
                resultado -> {
                    operacionEnCurso = false;
                    progressCarga.setVisible(false);
                    establecerControles(true, controles);
                    alCompletar.accept(resultado);
                },
                error -> {
                    operacionEnCurso = false;
                    progressCarga.setVisible(false);
                    establecerControles(true, controles);
                    alFallar.accept(error);
                }
        );
        tarea.start();
    }

    private void establecerControles(boolean habilitado, Node... controles) {
        for (Node control : controles) {
            control.setDisable(!habilitado);
        }
    }

    private void iniciarSincronizacionAutomatica() {
        detenerSincronizacionAutomatica();
        hiloSincronizacion = new HiloSincronizacion(INTERVALO_SINCRONIZACION_SEG, () -> actualizarDatosTabla(true));
        hiloSincronizacion.start();
        lblEstadoSistema.setText("Sincronización automática activa");
    }

    private void detenerSincronizacionAutomatica() {
        if (hiloSincronizacion != null && hiloSincronizacion.isAlive()) {
            hiloSincronizacion.detener();
        }
        hiloSincronizacion = null;
    }

    private void detenerHilos() {
        detenerSincronizacionAutomatica();
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
        TextField txtCarrera = new TextField(); txtCarrera.setPromptText("Ciclo: I, II, III...X");
        TextField txtCodigo = new TextField(); txtCodigo.setPromptText("Ingrese Código");
        TextField txtEdad = new TextField(); txtEdad.setPromptText("Ingrese Edad");
        TextField txtDias = new TextField(); txtDias.setPromptText("Ej: LUNES,MIERCOLES,VIERNES");

        addFormRow(grid, "Nombre:", txtNombre, 0);
        addFormRow(grid, "Apellido:", txtApellido, 1);
        addFormRow(grid, "Ciclo:", txtCarrera, 2);
        addFormRow(grid, "Código:", txtCodigo, 3);
        addFormRow(grid, "Edad:", txtEdad, 4);
        addFormRow(grid, "Días solicitados:", txtDias, 5);

        Button btnGuardar = new Button("Guardar Alumno");
        GridPane.setMargin(btnGuardar, new Insets(15, 0, 0, 0));
        grid.add(btnGuardar, 1, 6);

        Label lblMensajeForm = new Label();
        lblMensajeForm.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        grid.add(lblMensajeForm, 1, 7);

        btnGuardar.setOnAction(e -> {
            String nombre = txtNombre.getText().trim();
            String apellido = txtApellido.getText().trim();
            String ciclo = txtCarrera.getText().trim().toUpperCase();
            String strCodigo = txtCodigo.getText().trim();
            String strEdad = txtEdad.getText().trim();
            String dias = txtDias.getText().trim().toUpperCase();

            if (nombre.isEmpty() || apellido.isEmpty() || ciclo.isEmpty() || strCodigo.isEmpty() || strEdad.isEmpty()) {
                mostrarAlerta("Campos vacíos", "Por favor complete todos los datos del alumno.");
                return;
            }

            try {
                long codigo = Long.parseLong(strCodigo);
                int edad = Integer.parseInt(strEdad);
                String diasFinal = dias.isEmpty() ? "NO ESPECIFICADO" : dias;
                AlumnoCalificado nuevoAlumno = new AlumnoCalificado(nombre, apellido, ciclo, codigo, edad, 0, true, diasFinal);

                ejecutarTarea(
                        "Guardando alumno en la base de datos...",
                        () -> {
                            admin1.agregarAlumno(nuevoAlumno);
                            return nuevoAlumno;
                        },
                        guardado -> {
                            lblMensajeForm.setText("✔ Alumno agregado exitosamente.");
                            lblMensajeForm.setStyle("-fx-text-fill: #388e3c; -fx-font-weight: bold;");
                            txtNombre.clear();
                            txtApellido.clear();
                            txtCarrera.clear();
                            txtCodigo.clear();
                            txtEdad.clear();
                            txtDias.clear();
                            actualizarDatosTabla();
                        },
                        error -> mostrarAlerta("Error de Base de Datos", "No se pudo guardar el alumno: " + error.getMessage()),
                        "HiloGuardarAlumno",
                        btnGuardar
                );
            } catch (NumberFormatException ex) {
                lblMensajeForm.setText("✖ Error: Código y Edad deben ser números.");
                lblMensajeForm.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
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
        // Usamos un Stream para filtrar y contar SOLO a los alumnos que siguen activos
        long totalActivos = data.stream()
                .filter(AlumnoCalificado::isHorarioAprobado)
                .count();

        // Actualizamos la etiqueta gigante con el nuevo número
        totalAlumnosLabel.setText(String.valueOf(totalActivos));
    }

    private Tab createTabLista() {
        Tab tab = new Tab("Lista de Alumnos");

        VBox content = new VBox(20);
        content.setPadding(new Insets(30));

        Label lblHeader = new Label("Lista de Alumnos Calificados");
        lblHeader.getStyleClass().add("header-label");

        TableColumn<AlumnoCalificado, Long> colCodigo = new TableColumn<>("Código");
        colCodigo.setMinWidth(120);
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));

        TableColumn<AlumnoCalificado, String> colNombre = new TableColumn<>("Nombres");
        colNombre.setMinWidth(180);
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<AlumnoCalificado, String> colApellido = new TableColumn<>("Apellidos");
        colApellido.setMinWidth(180);
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));

        TableColumn<AlumnoCalificado, String> colCarrera = new TableColumn<>("Ciclo");
        colCarrera.setMinWidth(100);
        colCarrera.setCellValueFactory(new PropertyValueFactory<>("ciclo"));

        TableColumn<AlumnoCalificado, String> colDias = new TableColumn<>("Días Solicitados");
        colDias.setMinWidth(180);
        colDias.setCellValueFactory(new PropertyValueFactory<>("diasSolicitados"));

        TableColumn<AlumnoCalificado, Integer> colEdad = new TableColumn<>("Edad");
        colEdad.setMinWidth(70);
        colEdad.setCellValueFactory(new PropertyValueFactory<>("edad"));

        TableColumn<AlumnoCalificado, Integer> colFaltas = new TableColumn<>("Faltas");
        colFaltas.setMinWidth(70);
        colFaltas.setCellValueFactory(new PropertyValueFactory<>("faltas"));

        TableColumn<AlumnoCalificado, Boolean> colCalificado = new TableColumn<>("Calificado");
        colCalificado.setMinWidth(110);
        colCalificado.setCellValueFactory(new PropertyValueFactory<>("horarioAprobado"));

        // Mantenemos tu diseño visual (status Activo/Retirado) intacto
        // Nueva lógica visual para el estado del alumno (Activo, En riesgo, Retirado)
        colCalificado.setCellFactory(column -> new TableCell<AlumnoCalificado, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);

                // Verificamos que la fila exista y tenga un alumno cargado
                if (empty || item == null || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    // Extraemos el objeto AlumnoCalificado completo de esta fila
                    AlumnoCalificado alumno = getTableRow().getItem();
                    int faltas = alumno.getFaltas();

                    Label status = new Label();

                    if (faltas < 3) {
                        // Menos de 3 faltas: Verde (Activo)
                        status.setText("✔ Activo");
                        status.setStyle("-fx-background-color: #e8f5e9; -fx-text-fill: #2e7d32; -fx-padding: 3 8; -fx-background-radius: 10; -fx-font-weight: bold;");
                    } else if (faltas == 3) {
                        // Exactamente 3 faltas: Ámbar/Naranja (En riesgo)
                        status.setText("⚠ En riesgo");
                        status.setStyle("-fx-background-color: #fff3e0; -fx-text-fill: #e65100; -fx-padding: 3 8; -fx-background-radius: 10; -fx-font-weight: bold;");
                    } else {
                        // Más de 3 faltas: Rojo (Retirado)
                        status.setText("❌ Retirado");
                        status.setStyle("-fx-background-color: #ffebee; -fx-text-fill: #c62828; -fx-padding: 3 8; -fx-background-radius: 10; -fx-font-weight: bold;");
                    }

                    setGraphic(status);
                    setText(null);
                }
            }
        });

        table.getColumns().setAll(List.of(
                colCodigo, colNombre, colApellido, colCarrera, colDias, colEdad, colFaltas, colCalificado
        ));
        table.setItems(data);
        table.setPlaceholder(new Label("No hay alumnos registrados en el sistema o error de BD."));
        VBox.setVgrow(table, Priority.ALWAYS);

        progressCarga.setMaxSize(24, 24);
        progressCarga.setVisible(false);

        CheckBox chkSincronizacion = new CheckBox("Mantener lista actualizada automáticamente");
        chkSincronizacion.setOnAction(e -> {
            if (chkSincronizacion.isSelected()) {
                iniciarSincronizacionAutomatica();
            } else {
                detenerSincronizacionAutomatica();
                lblEstadoSistema.setText("Sincronización automática desactivada");
            }
        });

        HBox estadoSistema = new HBox(12);
        estadoSistema.setAlignment(Pos.CENTER_LEFT);
        estadoSistema.getStyleClass().add("thread-status-bar");
        estadoSistema.getChildren().addAll(progressCarga, lblEstadoSistema);

        Button btnActualizar = new Button("Actualizar Lista");
        btnActualizar.getStyleClass().add("btn-secondary");
        btnActualizar.setOnAction(e -> actualizarDatosTabla());

        HBox accionesLista = new HBox(20);
        accionesLista.setAlignment(Pos.CENTER_LEFT);
        accionesLista.getChildren().addAll(btnActualizar, chkSincronizacion);

        content.getChildren().addAll(lblHeader, table, estadoSistema, accionesLista);
        tab.setContent(content);
        return tab;
    }

    @Override
    public void stop() throws Exception {
        detenerHilos();
        super.stop();
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
            String val = txtCodigoBuscar.getText().trim();
            if (val.isEmpty()) return;

            try {
                long codigo = Long.parseLong(val);
                ejecutarTarea(
                        "Buscando alumno...",
                        () -> admin1.buscarAlumnos(codigo),
                        encontrado -> {
                            lblResultadoBusqueda.setText("✔ Encontrado:\n" + encontrado.getNombre() + " " + encontrado.getApellido()
                                    + "\nCiclo: " + encontrado.getCiclo() + "\nFaltas: " + encontrado.getFaltas()
                                    + "\nCódigo: " + encontrado.getCodigo()
                                    + "\nDías: " + encontrado.getDiasSolicitados());
                            lblResultadoBusqueda.setStyle("-fx-background-color: #e3f2fd; -fx-border-color: #bbdefb; -fx-text-fill: #0277bd; -fx-font-weight: bold;");
                            lblResultadoBusqueda.setVisible(true);
                        },
                        error -> {
                            lblResultadoBusqueda.setText(error.getMessage());
                            lblResultadoBusqueda.setStyle("-fx-background-color: #ffebee; -fx-border-color: #ffcdd2; -fx-text-fill: #c62828; -fx-font-weight: bold;");
                            lblResultadoBusqueda.setVisible(true);
                        },
                        "HiloBuscarAlumno",
                        btnBuscar, btnFalta
                );
            } catch (NumberFormatException ex) {
                lblResultadoBusqueda.setText("Ingrese un código numérico válido.");
                lblResultadoBusqueda.setStyle("-fx-background-color: #ffebee; -fx-border-color: #ffcdd2; -fx-text-fill: #c62828; -fx-font-weight: bold;");
                lblResultadoBusqueda.setVisible(true);
            }
        });

        btnFalta.setOnAction(e -> {
            lblResultadoBusqueda.setVisible(false);
            String val = txtCodigoBuscar.getText().trim();
            if (val.isEmpty()) return;

            try {
                long codigo = Long.parseLong(val);
                ejecutarTarea(
                        "Registrando falta...",
                        () -> admin1.ponerFalta(codigo),
                        alm -> {
                            actualizarDatosTabla();
                            if (alm.getFaltas() > 3) {
                                lblResultadoBusqueda.setText("⚠ ¡El alumno " + alm.getNombre() + " superó las 3 faltas y fue retirado!");
                                lblResultadoBusqueda.setStyle("-fx-background-color: #ffebee; -fx-border-color: #ef9a9a; -fx-text-fill: #c62828; -fx-font-weight: bold;");
                            } else {
                                lblResultadoBusqueda.setText("✔ Falta registrada. Total de faltas: " + alm.getFaltas());
                                lblResultadoBusqueda.setStyle("-fx-background-color: #e8f5e9; -fx-border-color: #a5d6a7; -fx-text-fill: #2e7d32; -fx-font-weight: bold;");
                            }
                            lblResultadoBusqueda.setVisible(true);
                        },
                        error -> {
                            lblResultadoBusqueda.setText(error.getMessage());
                            lblResultadoBusqueda.setStyle("-fx-background-color: #ffebee; -fx-border-color: #ffcdd2; -fx-text-fill: #c62828; -fx-font-weight: bold;");
                            lblResultadoBusqueda.setVisible(true);
                        },
                        "HiloRegistrarFalta",
                        btnBuscar, btnFalta
                );
            } catch (NumberFormatException ex) {
                lblResultadoBusqueda.setText("Ingrese un código numérico válido.");
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
            String val = txtCodigoRevocar.getText().trim();
            if (val.isEmpty()) return;

            try {
                long codigo = Long.parseLong(val);
                ejecutarTarea(
                        "Verificando alumno...",
                        () -> admin1.buscarAlumnos(codigo),
                        encontrado -> {
                            lblAlumnoInfo.setText("Alumno: " + encontrado.getNombre() + " " + encontrado.getApellido()
                                    + "\nFaltas actuales: " + encontrado.getFaltas());
                            spinQuitar.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                                    1, Math.max(1, encontrado.getFaltas()), 1));
                            revokeControls.setVisible(true);
                        },
                        error -> {
                            lblResultadoRevoke.setText(error.getMessage());
                            lblResultadoRevoke.setStyle("-fx-background-color: #ffebee; -fx-border-color: #ffcdd2; -fx-text-fill: #c62828; -fx-font-weight: bold;");
                            lblResultadoRevoke.setVisible(true);
                        },
                        "HiloVerificarAlumno",
                        btnVerificarRevoke, btnConfirmarRevoke
                );
            } catch (NumberFormatException ex) {
                lblResultadoRevoke.setText("Ingrese un código válido.");
                lblResultadoRevoke.setStyle("-fx-background-color: #ffebee; -fx-border-color: #ffcdd2; -fx-text-fill: #c62828; -fx-font-weight: bold;");
                lblResultadoRevoke.setVisible(true);
            }
        });

        // Campo de justificación (requerido al quitar faltas)
        Label lblJustificacion = new Label("Justificación:");
        lblJustificacion.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333;");
        TextField txtJustificacion = new TextField();
        txtJustificacion.setPromptText("Ingrese el motivo de la revocación");
        txtJustificacion.setPrefWidth(300);
        revokeControls.getChildren().addAll(lblJustificacion, txtJustificacion);

        btnConfirmarRevoke.setOnAction(e -> {
            try {
                long codigo = Long.parseLong(txtCodigoRevocar.getText().trim());
                int cantidad = spinQuitar.getValue();
                String justificacion = txtJustificacion.getText().trim();

                if (justificacion.isEmpty()) {
                    lblResultadoRevoke.setText("⚠ Debe ingresar una justificación para quitar la falta.");
                    lblResultadoRevoke.setStyle("-fx-background-color: #fff3e0; -fx-border-color: #ffb74d; -fx-text-fill: #e65100; -fx-font-weight: bold;");
                    lblResultadoRevoke.setVisible(true);
                    return;
                }

                ejecutarTarea(
                        "Revocando faltas...",
                        () -> admin1.revocarFalta(codigo, cantidad, justificacion),
                        alm -> {
                            actualizarDatosTabla();
                            lblResultadoRevoke.setText("✔ Faltas reducidas. Total actual de faltas: " + alm.getFaltas() + "\nJustificación guardada.");
                            lblResultadoRevoke.setStyle("-fx-background-color: #e8f5e9; -fx-border-color: #a5d6a7; -fx-text-fill: #2e7d32; -fx-font-weight: bold;");
                            lblResultadoRevoke.setVisible(true);
                            lblAlumnoInfo.setText("Alumno: " + alm.getNombre() + " " + alm.getApellido() + "\nFaltas actuales: " + alm.getFaltas());
                            spinQuitar.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                                    1, Math.max(1, alm.getFaltas()), 1));
                            txtJustificacion.clear();
                        },
                        error -> {
                            lblResultadoRevoke.setText("Error: " + error.getMessage());
                            lblResultadoRevoke.setStyle("-fx-background-color: #ffebee; -fx-border-color: #ffcdd2; -fx-text-fill: #c62828; -fx-font-weight: bold;");
                            lblResultadoRevoke.setVisible(true);
                        },
                        "HiloRevocarFalta",
                        btnVerificarRevoke, btnConfirmarRevoke
                );
            } catch (NumberFormatException ex) {
                lblResultadoRevoke.setText("Ingrese un código válido.");
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