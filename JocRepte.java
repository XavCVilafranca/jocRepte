package joc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;

public class JocRepte {

    private JFrame loginFrame;
    private JFrame preguntasFrame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel lblPregunta;
    private JLabel lblPartidesJugades;
    private JLabel lblPuntuacio;
    private PreguntaRespuesta preguntaActual;

    private List<PreguntaRespuesta> preguntes;

    private String nomUsuari;

    private String nomUsuariActual1;
    private String nomUsuariActual2;
    private int partidesJugades;
    private int puntuacioTotalUsuari;
    private int unJugadorN = 0;
    private int multijugadorN = 0;

    private static int numPreguntes = 0;
    private static boolean preguntesComptades = false;

    private Map<String, Jugador> jugadorsRegistrats;
    private int puntuacioTotalUsuari1;
    private int puntuacioTotalUsuari2;

    // arxius
    private static final String JUGADORS_FILE = "media\\jugadors.txt";
    private static final String REPTES_FILE = "media\\reptes.txt";
    private static final String ESTADISTIQUES_FILE = "media\\estadistiques.txt";

    public static void main(String[] args) {

        if (!preguntesComptades) {

            File arx = new File(REPTES_FILE);
            if (arx.exists()) {
                try (Scanner sc = new Scanner(arx)) {
                    while (sc.hasNextLine()) {
                        numPreguntes++;
                        System.out.println(numPreguntes);
                        sc.nextLine();
                    }
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            preguntesComptades = true;

        }
        // Verificar si estadistiques existeix i si no el cream
        File arxiu = new File(ESTADISTIQUES_FILE);
        if (!arxiu.exists()) {
            try {
                arxiu.createNewFile();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    JocRepte window = new JocRepte();
                    window.loginFrame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

    }

    public JocRepte() {
        initialize();
    }

    private void initialize() {

        // CREACIÓ DE TOTS ELS FRAMES I PANELLS
        loginFrame = new JFrame();
        loginFrame.setResizable(false);
        loginFrame.getContentPane().setBackground(new Color(255, 179, 255));
        loginFrame.setBounds(100, 100, 866, 536);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.getContentPane().setLayout(null);

        JPanel loginPanel = new JPanel();
        loginPanel.setForeground(new Color(0, 0, 0));
        loginPanel.setBackground(new Color(255, 217, 255));
        loginPanel.setBounds(32, 78, 782, 389);
        loginFrame.getContentPane().add(loginPanel);
        loginPanel.setLayout(null);

        usernameField = new JTextField();
        usernameField.setBounds(164, 84, 200, 30);
        loginPanel.add(usernameField);
        usernameField.setColumns(10);

        passwordField = new JPasswordField();
        passwordField.setBounds(164, 131, 200, 30);
        loginPanel.add(passwordField);

        JLabel lblUsuario = new JLabel("Usuari:");
        lblUsuario.setBounds(81, 83, 70, 30);
        loginPanel.add(lblUsuario);

        JLabel lblPassword = new JLabel("Contrassenya:");
        lblPassword.setBounds(50, 130, 150, 30);
        loginPanel.add(lblPassword);

        JButton btnLogin = new JButton("ENTRAR");
        btnLogin.setFont(new Font("Nirmala UI", Font.BOLD, 11));
        btnLogin.setForeground(new Color(255, 255, 255));
        btnLogin.setBackground(new Color(255, 0, 255));
        btnLogin.setBounds(228, 171, 89, 41);
        loginPanel.add(btnLogin);

        JButton btnCarregarPartida = new JButton("CARREGAR PARTIDA");
        btnCarregarPartida.setFont(new Font("Nirmala UI", Font.BOLD, 11));
        btnCarregarPartida.setForeground(new Color(255, 255, 255));
        btnCarregarPartida.setBackground(new Color(255, 0, 255));
        btnCarregarPartida.setBounds(189, 215, 163, 41);
        loginPanel.add(btnCarregarPartida);

        JLabel lblJocRepteLol = new JLabel("JOC REPTE SOLO");
        lblJocRepteLol.setFont(new Font("Bradley Hand ITC", Font.BOLD, 40));
        lblJocRepteLol.setBounds(85, 44, 396, 30);
        loginPanel.add(lblJocRepteLol);

        preguntes = new ArrayList<>();
        unJugadorN = 0;
        jugadorsRegistrats = new HashMap<>();

        final JLabel lblLoginMal = new JLabel(new ImageIcon("login.gif"));
        lblLoginMal.setHorizontalAlignment(SwingConstants.CENTER);
        lblLoginMal.setForeground(new Color(255, 0, 0));
        lblLoginMal.setFont(new Font("Yu Gothic", Font.BOLD, 15));
        lblLoginMal.setBounds(144, 260, 510, 119);
        loginPanel.add(lblLoginMal);

        JButton btnMultijugador = new JButton("");
        btnMultijugador.setForeground(new Color(255, 128, 255));
        btnMultijugador.setToolTipText("");
        btnMultijugador.setBackground(new Color(255, 217, 255));
        btnMultijugador.setBounds(491, 43, 211, 191);
        loginPanel.add(btnMultijugador);

        // Ajustar mida de la imatge
        ImageIcon icono = new ImageIcon("media\\multijugador.png");
        Image imagen = icono.getImage();
        Image nuevaImagen = imagen.getScaledInstance(btnMultijugador.getWidth(), btnMultijugador.getHeight(), Image.SCALE_SMOOTH);

      
        btnMultijugador.setIcon(new ImageIcon(nuevaImagen));
        
        JLabel lblMultijugador = new JLabel("JOC VERSUS");
        lblMultijugador.setFont(new Font("Bradley Hand ITC", Font.BOLD, 42));
        lblMultijugador.setBounds(464, -16, 343, 81);
        loginPanel.add(lblMultijugador);

        btnMultijugador.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                abrirVentanaMultijugador();
            }
        });



        //assignam a aquest lbl la puntuació total de les preguntes
        JLabel lblPuntuacioTotalPreguntes = new JLabel(
                "Puntuació màxima obtenible dins aquesta versió: " + obtenerPuntuacionTotalPreguntas());
        lblPuntuacioTotalPreguntes.setForeground(new Color(129, 129, 129));
        lblPuntuacioTotalPreguntes.setFont(new Font("Bradley Hand ITC", Font.BOLD, 20));
        lblPuntuacioTotalPreguntes.setBounds(69, 10, 565, 25);
        loginFrame.getContentPane().add(lblPuntuacioTotalPreguntes);

        JLabel lblRecord = new JLabel("Récord: " + obtenerRecord());
        lblRecord.setBounds(76, 45, 158, 25);
        loginFrame.getContentPane().add(lblRecord);
        lblRecord.setFont(new Font("Bradley Hand ITC", Font.BOLD, 25));

        lblPartidesJugades = new JLabel("Partides guardades: " + obtenerPartidesJugades());

        lblPartidesJugades.setFont(new Font("Bradley Hand ITC", Font.BOLD, 25));
        lblPartidesJugades.setBounds(281, 43, 301, 25);
        loginFrame.getContentPane().add(lblPartidesJugades);

        JMenuBar menuBar = new JMenuBar();
        menuBar.setFont(new Font("Bradley Hand ITC", Font.BOLD, 15));
        menuBar.setMargin(new Insets(0, 0, 2, 0));
        menuBar.setForeground(new Color(255, 128, 192));
        loginFrame.setJMenuBar(menuBar);

        JMenu mnLogin = new JMenu("Login");
        menuBar.add(mnLogin);

        JMenu mnCrearUsuari = new JMenu("Crear usuari");
        mnLogin.add(mnCrearUsuari);

        JMenuItem mntmNewMenuItem = new JMenuItem("A wana bun bam bam chinobario");
        mnCrearUsuari.add(mntmNewMenuItem);

        JMenu mnNewMenu_2 = new JMenu("Iniciar sessió");
        mnLogin.add(mnNewMenu_2);

        JMenuItem mntmNewMenuItem_1 = new JMenuItem("A wana bun bam bam chinobario");
        mntmNewMenuItem_1.setBackground(new Color(255, 255, 255));
        mnNewMenu_2.add(mntmNewMenuItem_1);

        JMenu mnEstadistiques = new JMenu("Estadístiques");
        menuBar.add(mnEstadistiques);

        JMenuItem mntmTreureStats = new JMenuItem("✔Puntuacions");
        mnEstadistiques.add(mntmTreureStats);

        JMenu mnReptesMaker = new JMenu("Reptes-Maker");
        menuBar.add(mnReptesMaker);

        JMenuItem mntmCrearRepte = new JMenuItem("Crear reptes *");
        mntmCrearRepte.setIcon(new ImageIcon("media\\crear.png"));
        mnReptesMaker.add(mntmCrearRepte);

        mntmCrearRepte.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                obrirMenuCrearRepte();
            }

        });

        JMenuItem mntmEditarRepte = new JMenuItem("Editar reptes");
        mntmEditarRepte.setIcon(new ImageIcon("media\\editar.png"));
        mnReptesMaker.add(mntmEditarRepte);

        mntmEditarRepte.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                obrirMenuEditarRepte();
            }

        });

        JMenuItem mntmEsborrarRepte = new JMenuItem("Esborrar reptes");
        mntmEsborrarRepte.setIcon(new ImageIcon("media\\borrar.png"));
        mnReptesMaker.add(mntmEsborrarRepte);

        mntmEsborrarRepte.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                obrirMenuEsborrarRepte();
            }

        });
        // Acciones que passen en pitjar el botó login
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Obtenir nom d'usuari i contrasenya
                String username = usernameField.getText().toLowerCase();
                String password = passwordField.getText();

                // validem l'usuari amb el mètode creat i verificam que no estigui buit
                if (validarUsuario(username, password) && !(username.isEmpty())) {
                    loginFrame.setVisible(false);
                    carregarPreguntes();
                    abrirVentanaPreguntas();

                } else {
                    // si ha fet cosa malament lleva els cors i mostra un missatge de realitat
                    lblLoginMal.setIcon(null);
                    lblLoginMal.setText(
                            "<html><body> Mala creació o iniciació de jugador, possiblement passi que: <br> - Espais en blanc  <br> - Usuari ja en ús <br> - Contrassenya errònea  <br> - Espavila....</body></html>");
                }

                usernameField.setText("");
                passwordField.setText("");
            }
        });

        btnCarregarPartida.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Obtenir nom d'usuari i contrasenya
                String username = usernameField.getText().toLowerCase();
                String password = passwordField.getText();

                // validem l'usuari amb el mètode creat i verificam que no estigui buit
                if (validarUsuario(username, password) && !(username.isEmpty())) {
                    obrirMenuPartidesGuardades();
                } else {
                    // si ha fet cosa malament lleva els cors i mostra un missatge de realitat
                    lblLoginMal.setIcon(null);
                    lblLoginMal.setText(
                            "<html><body> Mala creació o iniciació de jugador, possiblement passi que: <br> - Espais en blanc  <br> - L'usuari ja està en ús <br> - Contrassenya errònea  <br> - Espavila....</body></html>");
                }

                usernameField.setText("");
                passwordField.setText("");
            }
        });

        // ActionListener para el JMenuItem mntmTreureStats
        mntmTreureStats.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mostrarPuntuaciones();
            }
        });
        // Carregar jugadores desde el archivo
        carregarJugadores();
    }

    private void abrirVentanaPreguntas() {
        preguntasFrame = new JFrame();
        preguntasFrame.getContentPane().setBackground(new Color(255, 217, 255));
        preguntasFrame.setBounds(100, 100, 566, 465);
        preguntasFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        preguntasFrame.getContentPane().setLayout(null);

        final JPanel preguntasPanel = new JPanel();
        preguntasPanel.setBackground(new Color(235, 207, 235));
        preguntasPanel.setBounds(21, 62, 506, 341);
        preguntasFrame.getContentPane().add(preguntasPanel);
        preguntasPanel.setLayout(null);

        lblPregunta = new JLabel("");
        lblPregunta.setBounds(10, 20, 486, 27);
        preguntasPanel.add(lblPregunta);

        JLabel lblPuntuacionPregunta = new JLabel("Puntuació de la pregunta: ");
        lblPuntuacionPregunta.setBounds(10, 57, 180, 27);
        preguntasPanel.add(lblPuntuacionPregunta);

        lblPuntuacio = new JLabel("");
        lblPuntuacio.setBounds(200, 57, 60, 27);
        preguntasPanel.add(lblPuntuacio);

        final JTextArea textArea = new JTextArea();
        textArea.setBounds(20, 99, 360, 60);
        preguntasPanel.add(textArea);

        final JButton btnResponder = new JButton("Respondre");
        btnResponder.setBackground(new Color(255, 255, 255));
        btnResponder.setBounds(180, 253, 150, 30);
        preguntasPanel.add(btnResponder);

        JButton btnSalir = new JButton("Acabar aquí");
        btnSalir.setBackground(new Color(255, 255, 255));
        btnSalir.setBounds(180, 294, 150, 30);
        preguntasPanel.add(btnSalir);

        btnSalir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Guardar puntuación total en el archivo de estadísticas
                guardarPuntuacion(nomUsuari, obtenerPuntuacionTotalUsuario());

                puntuacioTotalUsuari = 0;// torna a posar la puntuació a 0 per a la pròxima run
                unJugadorN = 0;

                partidesJugades = obtenerPartidesJugades(); // torna a canviar el camp de partides guardades
                lblPartidesJugades.setText("Partides guardades: " + obtenerPartidesJugades());
                preguntasFrame.dispose();
                loginFrame.setVisible(true);
                obtenerRecord(); // actualitza el récord
                mostrarPuntuaciones(); // obre el frame de pyuntuacions

            }
        });

        btnResponder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (unJugadorN < preguntes.size()) {
                    PreguntaRespuesta pregunta = preguntes.get(unJugadorN);

                    String respuestaUsuario = textArea.getText().toLowerCase().trim();
                    if (pregunta.validarRespuesta(respuestaUsuario)) {
                        // Obtener la puntuación de la pregunta actual antes de incrementar
                        // unJugadorN
                        int puntuacion = pregunta.getPuntuacion();
                        unJugadorN++;
                        if (unJugadorN < preguntes.size()) {
                            pregunta = preguntes.get(unJugadorN);

                            lblPregunta.setText(pregunta.getPregunta());
                            lblPuntuacio.setText(Integer.toString(pregunta.getPuntuacion()));
                            textArea.setText("");
                            cambiarColor(preguntasPanel);

                        } else {

                            // Se respondieron todas las preguntas
                            lblPregunta.setText("Fin. bravo");
                            lblPuntuacio.setText("");
                            btnResponder.setEnabled(false);
                        }

                        // Actualizar puntuación total del usuario
                        actualizarPuntuacionTotalUsuario(puntuacion);
                    } else {
                        // Respuesta incorrecta
                        pregunta.restarPuntuacion();
                        lblPregunta.setText(pregunta.getPregunta());
                        lblPuntuacio.setText(Integer.toString(pregunta.getPuntuacion()));
                    }
                }
            }
        });

        if (preguntes.size() > 0) {
            preguntaActual = preguntes.get(unJugadorN);
            lblPregunta.setText(preguntaActual.getPregunta());
            lblPuntuacio.setText(Integer.toString(preguntaActual.getPuntuacion()));
        } else {
            lblPregunta.setText("No hi ha preguntes");
            lblPuntuacio.setText("");
            btnResponder.setEnabled(false);
        }

        preguntasFrame.setVisible(true);
    }

    private int obtenerPuntuacionTotalPreguntas() {
        int puntuacionTotalPreguntas = 0;

        try {

            Scanner scanner = new Scanner(new File(REPTES_FILE));

            // Leer cada línea del archivo y sumar los puntos
            while (scanner.hasNextLine()) {
                String linea = scanner.nextLine();
                String[] partes = linea.split(":");

                if (partes.length >= 3) {
                    int puntos = Integer.parseInt(partes[2]);
                    puntuacionTotalPreguntas += puntos;
                }
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return puntuacionTotalPreguntas;
    }

    private int obtenerPuntuacionTotalUsuario() {

        return puntuacioTotalUsuari;
    }

    private void actualizarPuntuacionTotalUsuario(int puntuacion) {
        puntuacioTotalUsuari += puntuacion;
    }

    private void guardarPuntuacion(String nombreUsuario, int puntuacion) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ESTADISTIQUES_FILE, true))) {
            String linea = nombreUsuario + ":" + puntuacion + ":" + unJugadorN + "\n";
            writer.write(linea);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean validarUsuario(String username, String password) {
        Jugador jugador = jugadorsRegistrats.get(username);
        if (jugador != null) {
            boolean contrasenaValida = jugador.validarContrasena(password);
            if (contrasenaValida) {
                nomUsuari = username; // Asignar el nombre de usuario actual
            }
            return contrasenaValida;
        } else {
            jugador = new Jugador(username, password);
            jugadorsRegistrats.put(username, jugador);
            guardarJugadores();
            nomUsuari = username; // Asignar el nombre de usuario actual para las estadísticas
            return true;
        }
    }

    private void guardarJugadores() {

        try {
            File arxiu = new File(JUGADORS_FILE);
            FileWriter writer = new FileWriter(arxiu);
            for (Jugador jugador : jugadorsRegistrats.values()) {
                writer.write(jugador.getNombre() + ":" + jugador.getContrasena() + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void carregarJugadores() {
        try {
            File arxiu = new File(JUGADORS_FILE);
            if (arxiu.exists()) {
                Scanner sc = new Scanner(arxiu);
                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        String username = parts[0];
                        String password = parts[1];
                        jugadorsRegistrats.put(username, new Jugador(username, password));
                    }
                }
                sc.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void carregarPreguntes() {
        try {
            File arxiu = new File(REPTES_FILE);
            if (arxiu.exists()) {
                Scanner sc = new Scanner(arxiu);
                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    String[] parts = line.split(":");
                    if (parts.length == 3) {
                        String pregunta = parts[0];
                        String respuesta = parts[1];
                        int puntuacion = Integer.parseInt(parts[2]);
                        preguntes.add(new PreguntaRespuesta(pregunta, respuesta, puntuacion));
                    }
                }
                sc.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cambiarColor(JPanel panel) {

        // assignam a cada espai del Color un nombre aleatori i ho feim d'aquesta manera
        // per a que siguin si o si colors brillants (sempre serà més de 100 com a mínim
        // i el valor máxim es 236 i no 256 per a que no sigui massa blanc)

        int rojos = (int) ((Math.random() * 136) + 100);
        int verdes = (int) ((Math.random() * 136) + 100);
        int azules = (int) ((Math.random() * 136) + 100);
        Color color = new Color(rojos, verdes, azules);
        panel.setBackground(color);
    }

    private int obtenerRecord() {

        int record = 0;

        // feim el bucle per a comparar tots els records
        try {
            File archivo = new File(ESTADISTIQUES_FILE);
            Scanner scanner = new Scanner(archivo);

            while (scanner.hasNextLine()) {
                String linea = scanner.nextLine();
                String[] partes = linea.split(":");
                if (partes.length == 3) {
                    int puntuacion = Integer.parseInt(partes[1]);

                    // si sa puntuació es major destronam a l'anterior récord.
                    if (puntuacion > record) {
                        record = puntuacion;
                    }
                }
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return record;
    }

    private void mostrarPuntuaciones() {
        // Crear JFrame para ventana de puntuaciones
        JFrame puntuacionesFrame = new JFrame();
        puntuacionesFrame.getContentPane().setBackground(new Color(255, 255, 215));
        puntuacionesFrame.setBounds(100, 100, 400, 600);
        puntuacionesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        puntuacionesFrame.getContentPane().setLayout(null);
        puntuacionesFrame.setResizable(false);

        // Crear JPanel para contener componentes
        JPanel puntuacionesPanel = new JPanel();
        puntuacionesPanel.setBackground(new Color(255, 245, 215));
        puntuacionesPanel.setBounds(10, 12, 364, 449);
        puntuacionesFrame.getContentPane().add(puntuacionesPanel);
        puntuacionesPanel.setLayout(null);

        // Crear JLabel para título
        JLabel lblTitulo = new JLabel("PUNTUACIONS");
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblTitulo.setBounds(10, 11, 344, 20);
        puntuacionesPanel.add(lblTitulo);

        // Crear JTextArea para mostrar puntuaciones
        JTextArea jugadorsPuntuacions = new JTextArea();
        jugadorsPuntuacions.setEditable(false);

        // Crear JScrollPane para el JTextArea
        JScrollPane scrollPane = new JScrollPane(jugadorsPuntuacions);
        scrollPane.setBounds(10, 60, 344, 377);
        puntuacionesPanel.add(scrollPane);

        // Leer puntuaciones del archivo y guardarlas en el Map
        Map<Integer, List<String>> puntuaciones = new HashMap<>();

        File archivo = new File(ESTADISTIQUES_FILE);

        try (Scanner sc = new Scanner(archivo)) {
            while (sc.hasNextLine()) {
                String linea = sc.nextLine();
                System.out.println(linea);
                String[] partes = linea.split(":");
                System.out.println(partes.length);

                if (partes.length == 3 || partes.length == 2) {

                    int puntuacion = Integer.parseInt(partes[1]);
                    String nombre = partes[0];

                    // Verificar si la puntuación ya existe en el Map
                    if (puntuaciones.containsKey(puntuacion)) {
                        // Obtener la lista de nombres para la puntuación existente y agregar el nuevo
                        // nombre
                        List<String> nombres = puntuaciones.get(puntuacion);
                        nombres.add(nombre);
                    } else {
                        // Crear una nueva lista de nombres y agregarla al Map
                        List<String> nombres = new ArrayList<>();
                        nombres.add(nombre);
                        puntuaciones.put(puntuacion, nombres);
                    }
                }
            }

            // crear la llista
            List<Integer> clausOrdenades = new ArrayList<>(puntuaciones.keySet());

            // Ordenar 
            Collections.sort(clausOrdenades);

            // de manera descendent
            Collections.reverse(clausOrdenades);

            StringBuilder punts = new StringBuilder();

            // Recorrer la llista 
            for (Integer puntuacion : clausOrdenades) {
                List<String> nombres = puntuaciones.get(puntuacion);

                // Mostrar totes les puntuacions
                for (String nombre : nombres) {
                    punts.append("    " + nombre.toUpperCase()).append("\t    -\t       -\t").append(puntuacion)
                            .append("\t\n");
                }
            }

            jugadorsPuntuacions.setText(punts.toString());
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Mostrar ventana puntuaciones
        puntuacionesFrame.setVisible(true);
    }

    private int obtenerPartidesJugades() {
        int partidesJugades = 0;

        try {
            File archivo = new File(ESTADISTIQUES_FILE);
            Scanner scanner = new Scanner(archivo);

            while (scanner.hasNextLine()) {
                String linea = scanner.nextLine();
                String[] partes = linea.split(":");

                if (partes.length >= 2) {
                    partidesJugades++;
                }
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return partidesJugades;
    }

    ///////////////////////////////////////////////
    ///////////////////////////////////////////////

    ///////////////////////////////////////////////
    ///////////////////////////////////////////////

    ///////////////////////////////////////////////
    ///////////////////////////////////////////////

    ///////////////////////////////////////////////
    ///////////////////////////////////////////////

    ///////////////////////////////////////////////
    ///////////////////////////////////////////////

    ///////////////////////////////////////////////
    ///////////////////////////////////////////////

    ///////////////////////////////////////////////
    ///////////////////////////////////////////////

    ///////////////////////////////////////////////
    ///////////////////////////////////////////////

    ///////////////////////////////////////////////
    ///////////////////////////////////////////////
    // CARREGAR PARTIDA GUARDADA

    private void obrirMenuPartidesGuardades() {

        JFrame triarPartidaFrame = new JFrame();
        triarPartidaFrame.setVisible(true);
        triarPartidaFrame.getContentPane().setBackground(new Color(255, 217, 255));
        triarPartidaFrame.setBounds(100, 100, 400, 400);
        triarPartidaFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        triarPartidaFrame.getContentPane().setLayout(null);
        triarPartidaFrame.setResizable(false);

        // JPanel general
        JPanel triarPartidaPanel = new JPanel();
        triarPartidaPanel.setBackground(new Color(255, 200, 255));
        triarPartidaPanel.setBounds(10, 12, 364, 349);
        triarPartidaFrame.getContentPane().add(triarPartidaPanel);
        triarPartidaPanel.setLayout(null);

        

        // llista d'opcions
        List<String> opciones = obtenirPartidesGuardades(nomUsuari);
        JList<String> llistaPreguntes = new JList<>(opciones.toArray(new String[0]));
        llistaPreguntes.setBackground(new Color(233, 200, 255));
        llistaPreguntes.setBounds(10, 40, 334, 250);

        // scrollPane per sa llista
        JScrollPane scrollPane = new JScrollPane(llistaPreguntes);
        scrollPane.setBackground(new Color(233, 200, 255));
        scrollPane.setBounds(10, 40, 334, 230);
        triarPartidaPanel.add(scrollPane);

        JButton btnCarregar = new JButton("CARREGAR PARTIDA");
        btnCarregar.setBounds(10, 11, 344, 40);
        btnCarregar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String partidaSeleccionada = llistaPreguntes.getSelectedValue();
                if (partidaSeleccionada != null) {
                    carregarPartida(partidaSeleccionada);
                    
                                        carregarPreguntes();

                    abrirVentanaPreguntas();
                    triarPartidaFrame.dispose();
                }
            }
        });
        triarPartidaFrame.getContentPane().add(btnCarregar);

    }

    private List<String> obtenirPartidesGuardades(String nombreUsuario) {
        List<String> partidasGuardadas = new ArrayList<>();
        File arxiu = new File(ESTADISTIQUES_FILE);

        try (Scanner scanner = new Scanner(arxiu)) {

            while (scanner.hasNextLine()) {

                String line = scanner.nextLine();
                String[] fields = line.split(":");
                if (fields.length == 3) {
                    String jugador = fields[0];

                    if (jugador.equals(nombreUsuario)) {

                        partidasGuardadas.add("      Puntuació: " + fields[1]
                                + " punts          -               Panell: " + fields[2] + " de " + numPreguntes);

                                //assignam a aquesta variable el nombre del panell en el que estava per a que comenci per on toca
                                unJugadorN = Integer.parseInt(fields[2]);
                                puntuacioTotalUsuari = Integer.parseInt(fields[2]);
                    }
                }

                // Obtener el índice de la pregunta guardada en fields[2]
                if (fields.length >= 3) {
                    String preguntaGuardada = fields[2];
                    for (int i = 0; i < preguntes.size(); i++) {
                        if (preguntes.get(i).getPregunta().equals(preguntaGuardada)) {
                            unJugadorN = i;
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return partidasGuardadas;
    }

    public void carregarPartida(String partidaSeleccionada) {
        try (Scanner scanner = new Scanner(ESTADISTIQUES_FILE)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] fields = line.split(":");

                String partida = fields[0];

                if (partida.equals(partidaSeleccionada)) {
                    String jugador = fields[0];
                    String pregunta = fields[1];
                    int puntuacion = Integer.parseInt(fields[2]);

                  

                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

   
    ///////////////////////////////////////////////
    ///////////////////////////////////////////////

    ///////////////////////////////////////////////
    ///////////////////////////////////////////////

    ///////////////////////////////////////////////
    ///////////////////////////////////////////////

    ///////////////////////////////////////////////
    ///////////////////////////////////////////////

    ///////////////////////////////////////////////
    ///////////////////////////////////////////////

    ///////////////////////////////////////////////
    ///////////////////////////////////////////////

    ///////////////////////////////////////////////
    ///////////////////////////////////////////////

    ///////////////////////////////////////////////
    ///////////////////////////////////////////////

    ///////////////////////////////////////////////
    ///////////////////////////////////////////////
    // MULTIJUGADOR

    private void abrirVentanaMultijugador() {
        loginFrame.setVisible(false);
        multijugadorFrame();

    }

    private void multijugadorFrame() {
        JFrame frame = new JFrame();
        frame.getContentPane().setBackground(new Color(255, 209, 220)); // Color rosa pastel
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setResizable(false);
        frame.getContentPane().setLayout(null);

        // Título grande
        JLabel lblTitulo = new JLabel("Ventana Multijugador");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24)); // Tamaño y estilo del texto
        lblTitulo.setBounds(180, 10, 300, 30);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER); // Centrar el texto horizontalmente
        frame.getContentPane().add(lblTitulo);

        JLabel lblJugador1 = new JLabel("Jugador 1:");
        lblJugador1.setBounds(90, 50, 100, 30);
        frame.getContentPane().add(lblJugador1);

        JTextField jugador1Field = new JTextField();
        jugador1Field.setBounds(200, 50, 200, 30);
        frame.getContentPane().add(jugador1Field);
        jugador1Field.setColumns(10);

        JLabel lblJugador1Password = new JLabel("Contraseña:");
        lblJugador1Password.setBounds(90, 100, 100, 30);
        frame.getContentPane().add(lblJugador1Password);

        JPasswordField jugador1PasswordField = new JPasswordField();
        jugador1PasswordField.setBounds(200, 100, 200, 30);
        frame.getContentPane().add(jugador1PasswordField);

        JLabel lblJugador2 = new JLabel("Jugador 2:");
        lblJugador2.setBounds(90, 180, 100, 30);
        frame.getContentPane().add(lblJugador2);

        JTextField jugador2Field = new JTextField();
        jugador2Field.setBounds(200, 180, 200, 30);
        frame.getContentPane().add(jugador2Field);
        jugador2Field.setColumns(10);

        JLabel lblJugador2Password = new JLabel("Contraseña:");
        lblJugador2Password.setBounds(90, 230, 100, 30);
        frame.getContentPane().add(lblJugador2Password);

        JPasswordField jugador2PasswordField = new JPasswordField();
        jugador2PasswordField.setBounds(200, 230, 200, 30);
        frame.getContentPane().add(jugador2PasswordField);

        // Botón para cerrar el marco actual
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setBounds(150, 290, 100, 30);
        btnCerrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                loginFrame.setVisible(true);
            }
        });
        frame.getContentPane().add(btnCerrar);
        // Botón para abrir el marco "abrirPreguntasVS"
        JButton btnAbrirPreguntas = new JButton("Abrir Preguntas VS");
        btnAbrirPreguntas.setBounds(300, 290, 150, 30);
        btnAbrirPreguntas.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                String jugador1 = jugador1Field.getText().toLowerCase();
                String jugador1Password = new String(jugador1PasswordField.getPassword());
                String jugador2 = jugador2Field.getText().toLowerCase();
                String jugador2Password = new String(jugador2PasswordField.getPassword());

                if (validarUsuariosVS(jugador1, jugador1Password, jugador2, jugador2Password)
                        && !jugador1.equals(jugador2) && !jugador1.equals("") && !jugador2.equals("")) {

                    nomUsuariActual1 = jugador1;
                    nomUsuariActual2 = jugador2;

                    frame.setVisible(false);
                    carregarPreguntasVS();
                    abrirVentanaPreguntasVS();
                } else {
                    JOptionPane.showMessageDialog(frame, "Los usuarios o las contraseñas no son válidos.",
                            "Error de autenticación", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        frame.getContentPane().add(btnAbrirPreguntas);

        // Centrar el marco en la pantalla
        frame.setLocationRelativeTo(null);

        frame.setVisible(true);
    }

    private void abrirVentanaPreguntasVS() {
        preguntasFrame = new JFrame();
        preguntasFrame.getContentPane().setBackground(new Color(255, 217, 255));
        preguntasFrame.setBounds(100, 100, 706, 565);
        preguntasFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        preguntasFrame.getContentPane().setLayout(null);

        // Título grande
        JLabel lblTitulo = new JLabel("");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 30));
        lblTitulo.setBounds(200, 10, 300, 30);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        preguntasFrame.getContentPane().add(lblTitulo);

        final JPanel preguntasPanel = new JPanel();
        preguntasPanel.setBackground(new Color(235, 207, 235));
        preguntasPanel.setBounds(21, 62, 646, 441);
        preguntasFrame.getContentPane().add(preguntasPanel);
        preguntasPanel.setLayout(null);

        lblPregunta = new JLabel("");
        lblPregunta.setBounds(10, 20, 486, 27);
        preguntasPanel.add(lblPregunta);

        JLabel lblPuntuacionPregunta = new JLabel("Puntuació de la pregunta: ");
        lblPuntuacionPregunta.setBounds(10, 57, 180, 27);
        preguntasPanel.add(lblPuntuacionPregunta);

        final JLabel lblPuntuacion = new JLabel("");
        lblPuntuacion.setBounds(200, 57, 60, 27);
        preguntasPanel.add(lblPuntuacion);

        final JTextArea textArea = new JTextArea();
        textArea.setBounds(20, 99, 360, 60);
        preguntasPanel.add(textArea);

        final JButton btnResponder = new JButton("Respondre");
        btnResponder.setBackground(new Color(255, 255, 255));
        btnResponder.setBounds(180, 253, 150, 30);
        preguntasPanel.add(btnResponder);

        JButton btnRendirse = new JButton("Rendirse");
        btnRendirse.setBackground(new Color(255, 255, 255));
        btnRendirse.setBounds(180, 335, 150, 30);
        preguntasPanel.add(btnRendirse);

        final JButton btnSalir = new JButton("Acabar aqui");
        btnSalir.setBackground(new Color(255, 255, 255));
        btnSalir.setBounds(180, 294, 150, 30);
        preguntasPanel.add(btnSalir);

        lblTitulo.setText(nomUsuariActual1.toUpperCase());

        btnResponder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (unJugadorN < preguntes.size()) {
                    PreguntaRespuesta pregunta = preguntes.get(unJugadorN);

                    String respuestaUsuario = textArea.getText().toLowerCase().trim();
                    if (pregunta.validarRespuesta(respuestaUsuario)) {
                        // Obtener la puntuación de la pregunta actual antes de incrementar
                        int puntuacion = pregunta.getPuntuacion();
                        unJugadorN++;

                        // Actualizar la puntuación del jugador correspondiente
                        if (multijugadorN % 2 == 0) {
                            actualizarPuntuacionTotalUsuario1(puntuacion);
                        } else {
                            actualizarPuntuacionTotalUsuario2(puntuacion);
                        }

                        if (unJugadorN < preguntes.size()) {
                            pregunta = preguntes.get(unJugadorN);

                            lblPregunta.setText(pregunta.getPregunta());
                            lblPuntuacion.setText(Integer.toString(pregunta.getPuntuacion()));
                            textArea.setText("");

                            // Cambiar al siguiente jugador
                            multijugadorN++;

                            if (multijugadorN % 2 == 0) {
                                lblTitulo.setText(nomUsuariActual1.toUpperCase());
                                preguntasPanel.setBackground(new Color(235, 217, 235));
                                preguntasFrame.getContentPane().setBackground(new Color(255, 217, 255));
                            } else {
                                lblTitulo.setText(nomUsuariActual2.toUpperCase());
                                preguntasFrame.getContentPane().setBackground(new Color(217, 255, 217));
                                preguntasPanel.setBackground(new Color(217, 235, 217));
                            }
                        } else {
                            // Se respondieron todas las preguntas
                            lblPregunta.setText("Fin. bravo");
                            lblPuntuacion.setText("");
                            btnResponder.setEnabled(false);
                        }

                        // Actualizar puntuación total del usuario
                    } else {
                        // Respuesta incorrecta
                        pregunta.restarPuntuacion();
                        lblPregunta.setText(pregunta.getPregunta());
                        lblPuntuacion.setText(Integer.toString(pregunta.getPuntuacion()));
                    }
                }
            }
        });

        btnSalir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                // Guardar puntuación total en el archivo de estadísticas
                guardarPuntuacionVS(nomUsuariActual1, obtenerPuntuacionTotalUsuario1(), nomUsuariActual2,
                        obtenerPuntuacionTotalUsuario2());

                puntuacioTotalUsuari = 0;// torna a posar la puntuació a 0 per a la pròxima run
                unJugadorN = 0;
                partidesJugades = obtenerPartidesJugades(); // torna a canviar el camp de partides guardades
                lblPartidesJugades.setText("Partides guardades: " + obtenerPartidesJugades());
                preguntasFrame.dispose();
                loginFrame.setVisible(true);
                obtenerRecord(); // actualitza el récord

                if (obtenerPuntuacionTotalUsuario1() > obtenerPuntuacionTotalUsuario2()) {
                    mostrarGanador(nomUsuariActual1);
                } else if (obtenerPuntuacionTotalUsuario1() < obtenerPuntuacionTotalUsuario2()) {
                    mostrarGanador(nomUsuariActual2);

                } else {
                    mostrarGanador("");
                }
            }
        });

        btnRendirse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                System.out.println(multijugadorN);
                if (multijugadorN % 2 == 0) {
                    mostrarGanador(nomUsuariActual2);
                } else {
                    mostrarGanador(nomUsuariActual1);
                }
            }
        });

        // Obtener primera pregunta y validar que no esté vacía
        if (preguntes.size() > 0) {
            PreguntaRespuesta pregunta = preguntes.get(0);
            lblPregunta.setText(pregunta.getPregunta());
            lblPuntuacion.setText(Integer.toString(pregunta.getPuntuacion()));
        } else {
            lblPregunta.setText("No hi ha preguntes");
            lblPuntuacion.setText("");
            btnResponder.setEnabled(false);
        }

        preguntasFrame.setVisible(true);
    }

    private void carregarPreguntasVS() {
        try {
            File arxiu = new File(REPTES_FILE);
            if (arxiu.exists()) {
                Scanner sc = new Scanner(arxiu);
                String line = sc.nextLine();

                for (int i = 0; i < (numPreguntes * 2); i++) {
                    if (i % 2 == 0 && i != 0) {
                        line = sc.nextLine();

                    }

                    System.out.println(line);

                    String[] parts = line.split(":");
                    if (parts.length == 3) {
                        String pregunta = parts[0];
                        String respuesta = parts[1];
                        int puntuacion = Integer.parseInt(parts[2]);
                        preguntes.add(new PreguntaRespuesta(pregunta, respuesta, puntuacion));

                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean validarUsuariosVS(String username1, String password1, String username2, String password2) {
        Jugador jugador1 = jugadorsRegistrats.get(username1);
        Jugador jugador2 = jugadorsRegistrats.get(username2);

        if (jugador1 != null && jugador2 != null) {
            boolean contrasenaValida = jugador1.validarContrasena(password1) && jugador2.validarContrasena(password2);
            ;
            System.out.println(contrasenaValida);
            if (contrasenaValida) {
                nomUsuariActual1 = username1;
                nomUsuariActual2 = username2;
            }
            return contrasenaValida;
        } else {
            jugador1 = new Jugador(username1, password1);
            jugador2 = new Jugador(username2, password2);

            jugadorsRegistrats.put(username1, jugador1);
            jugadorsRegistrats.put(username2, jugador2);

            guardarJugadores();
            nomUsuari = username1; // Asignar el nombre de usuario actual para las estadísticas
            return true;
        }
    }

    private void actualizarPuntuacionTotalUsuario1(int puntuacion) {
        puntuacioTotalUsuari1 += puntuacion;
    }

    private void actualizarPuntuacionTotalUsuario2(int puntuacion) {
        puntuacioTotalUsuari2 += puntuacion;
    }

    private int obtenerPuntuacionTotalUsuario1() {

        return puntuacioTotalUsuari1;
    }

    private int obtenerPuntuacionTotalUsuario2() {

        return puntuacioTotalUsuari2;
    }

    private void guardarPuntuacionVS(String nombreUsuario1, int puntuacion1, String nombreUsuario2, int puntuacion2) {
        try {
            FileWriter fw = new FileWriter(ESTADISTIQUES_FILE, true);
            fw.write(nombreUsuario1 + ":" + puntuacion1 + "\n");
            fw.write(nombreUsuario2 + ":" + puntuacion2 + "\n");
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarGanador(String nombreGanador) {
        JFrame frame = new JFrame();
        frame.getContentPane().setBackground(new Color(255, 209, 220)); // Color rosa pastel

        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        if (nombreGanador == nomUsuariActual1 || nombreGanador == nomUsuariActual2) {

            JLabel lblGanador = new JLabel("¡" + nombreGanador.toUpperCase() + " ÉS EL GUANYADOR!");
            lblGanador.setFont(new Font("Helvetica", Font.BOLD, 50));
            lblGanador.setHorizontalAlignment(SwingConstants.CENTER);
            frame.getContentPane().add(lblGanador);

        } else {
            JLabel lblGanador = new JLabel("EMPATADOS!");
            lblGanador.setFont(new Font("Arial", Font.BOLD, 50));
            lblGanador.setHorizontalAlignment(SwingConstants.CENTER);
            frame.getContentPane().add(lblGanador);
        }

        multijugadorN = 0;
        nomUsuariActual1 = "";
        nomUsuariActual2 = "";
        preguntasFrame.setVisible(false);
        loginFrame.setVisible(true);
        frame.setVisible(true);
    }

    ///////////////////////////////////////////////
    ///////////////////////////////////////////////

    ///////////////////////////////////////////////
    ///////////////////////////////////////////////

    ///////////////////////////////////////////////
    ///////////////////////////////////////////////

    ///////////////////////////////////////////////
    ///////////////////////////////////////////////

    ///////////////////////////////////////////////
    ///////////////////////////////////////////////

    ///////////////////////////////////////////////
    ///////////////////////////////////////////////

    ///////////////////////////////////////////////
    ///////////////////////////////////////////////

    ///////////////////////////////////////////////
    ///////////////////////////////////////////////

    ///////////////////////////////////////////////
    ///////////////////////////////////////////////
    // REPTES MAKER

    private void obrirMenuCrearRepte() {
        JFrame crearRepteFrame = new JFrame();
        crearRepteFrame.getContentPane().setBackground(new Color(255, 217, 255));
        crearRepteFrame.setBounds(100, 100, 566, 465);
        crearRepteFrame.getContentPane().setLayout(null);

        JPanel crearReptePanel = new JPanel();
        crearReptePanel.setBackground(new Color(235, 207, 235));
        crearReptePanel.setBounds(21, 62, 506, 341);
        crearRepteFrame.getContentPane().add(crearReptePanel);
        crearReptePanel.setLayout(null);

        JLabel lblTitulo = new JLabel("Crear repte");
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblTitulo.setBounds(10, 20, 486, 27);
        crearReptePanel.add(lblTitulo);

        JLabel lblExplicacion = new JLabel("Te lo explico crack");
        lblExplicacion.setBounds(10, 57, 486, 27);
        crearReptePanel.add(lblExplicacion);

        JLabel lblPregunta = new JLabel("Pregunta:");
        lblPregunta.setBounds(10, 100, 80, 27);
        crearReptePanel.add(lblPregunta);

        JTextField preguntaField = new JTextField();
        preguntaField.setBounds(100, 100, 200, 27);
        crearReptePanel.add(preguntaField);
        preguntaField.setColumns(10);

        JLabel lblRespuesta = new JLabel("Respuesta:");
        lblRespuesta.setBounds(10, 140, 80, 27);
        crearReptePanel.add(lblRespuesta);

        JTextField respuestaField = new JTextField();
        respuestaField.setBounds(100, 140, 200, 27);
        crearReptePanel.add(respuestaField);
        respuestaField.setColumns(10);

        lblPuntuacio = new JLabel("Puntuación:");
        lblPuntuacio.setBounds(10, 180, 80, 27);
        crearReptePanel.add(lblPuntuacio);

        JTextField puntuacionField = new JTextField();
        puntuacionField.setBounds(100, 180, 200, 27);
        crearReptePanel.add(puntuacionField);
        puntuacionField.setColumns(10);

        JButton btnSeguir = new JButton("Crear");
        btnSeguir.setBackground(new Color(217, 255, 217));
        btnSeguir.setBounds(10, 220, 160, 30);
        crearReptePanel.add(btnSeguir);

        btnSeguir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String pregunta = preguntaField.getText();
                String respuesta = respuestaField.getText();
                String puntuacionStr = puntuacionField.getText();

                if (!pregunta.isEmpty() && !respuesta.isEmpty() && !puntuacionStr.isEmpty()) {
                    try {
                        int puntuacion = Integer.parseInt(puntuacionStr);
                        guardarPregunta(REPTES_FILE, pregunta, respuesta, puntuacion);

                        preguntaField.setText("");
                        respuestaField.setText("");
                        puntuacionField.setText("");
                        JOptionPane.showMessageDialog(null, "Pregunta creada amb èxit.");
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "La puntuació no té que ser un nombre enter");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Queden camps buids");
                }
            }
        });

        JButton btnVolver = new JButton("Sortir");
        btnVolver.setBackground(new Color(255, 0, 0));
        btnVolver.setBounds(180, 220, 200, 30);
        crearReptePanel.add(btnVolver);

        btnVolver.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                crearRepteFrame.setVisible(false);
                loginFrame.setVisible(true);

            }
        });

        crearRepteFrame.setVisible(true);
    }

    private void guardarPregunta(String file, String pregunta, String respuesta, int puntuacion) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            BufferedReader br = new BufferedReader(new FileReader(file));

            if (br.readLine() != null) {
                bw.newLine();
            }

            bw.write(pregunta + ":" + respuesta.toLowerCase() + ":" + puntuacion);
            bw.close();

            // Eliminar líneas en blanco
            List<String> lines = Files.readAllLines(Paths.get(file));
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    writer.write(line);
                    writer.newLine();
                }
            }
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ////////////////////////////////////////////////////////////////////////

    private void obrirMenuEsborrarRepte() {
        // Crear el JFrame para el menú de borrado de repte
        JFrame borrarRepteFrame = new JFrame();
        borrarRepteFrame.getContentPane().setBackground(new Color(255, 217, 255));
        borrarRepteFrame.setBounds(100, 100, 450, 300);
        borrarRepteFrame.getContentPane().setLayout(null);

        // Crear el JPanel para el menú de borrado de repte
        JPanel borrarReptePanel = new JPanel();
        borrarReptePanel.setBackground(new Color(235, 207, 235));
        borrarReptePanel.setBounds(10, 10, 414, 241);
        borrarRepteFrame.getContentPane().add(borrarReptePanel);
        borrarReptePanel.setLayout(null);

        // titulo
        JLabel lblTitulo = new JLabel("Tria un repte");
        lblTitulo.setFont(new Font("Tahoma", Font.PLAIN, 24));
        lblTitulo.setBounds(10, 20, 200, 30);
        borrarReptePanel.add(lblTitulo);

        JLabel lblExplicacion = new JLabel("Te lo vuelvo a explicar máquina");
        lblExplicacion.setBounds(10, 60, 300, 30);
        borrarReptePanel.add(lblExplicacion);

        // llista d'opcions de reptes
        List<String> opciones = obtenirPreguntesDArxiu();
        JComboBox<String> comboBoxPreguntas = new JComboBox<>(opciones.toArray(new String[0]));
        comboBoxPreguntas.setBounds(10, 100, 300, 30);
        borrarReptePanel.add(comboBoxPreguntas);

        // Botó esborrar
        JButton btnBorrar = new JButton("Borrar");
        btnBorrar.setBounds(90, 150, 100, 30);
        borrarReptePanel.add(btnBorrar);
        btnBorrar.setBackground(new Color(200, 255, 200));

        // Acción al hacer clic en el botón de borrar
        btnBorrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String preguntaSeleccionada = (String) comboBoxPreguntas.getSelectedItem();
                if (preguntaSeleccionada != null) {
                    // Borrar el reto seleccionado del archivo
                    borrarReto(preguntaSeleccionada);

                    // Actualizar la lista de opciones con las preguntas
                    List<String> opcionesActualizadas = obtenirPreguntesDArxiu();
                    comboBoxPreguntas.setModel(new DefaultComboBoxModel<>(opcionesActualizadas.toArray(new String[0])));
                }
            }
        });

        JButton btnVolver = new JButton("Sortir");
        btnVolver.setBackground(new Color(255, 0, 0));
        btnVolver.setBounds(180, 205, 100, 20);
        borrarReptePanel.add(btnVolver);

        btnVolver.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                borrarRepteFrame.setVisible(false);
                loginFrame.setVisible(true);

            }
        });

        // Mostrar el menú de borrado de repte
        borrarRepteFrame.setVisible(true);
    }

    private List<String> obtenirPreguntesDArxiu() {
        List<String> preguntas = new ArrayList<>();

        try {

            String arxiu = REPTES_FILE;

            BufferedReader reader = new BufferedReader(new FileReader(arxiu));

            String linea;
            // afegir les linies que no estàn buides a la List
            while ((linea = reader.readLine()) != null) {
                preguntas.add(linea);
            }

            reader.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return preguntas;
    }

    private void borrarReto(String reto) {
        try {
            // Ruta del archivo que contiene los reptes
            String archivo = REPTES_FILE;

            // Leer todos los reptes del archivo
            List<String> reptes = Files.readAllLines(Paths.get(archivo));

            FileWriter writer = new FileWriter(archivo);

            // Recorrer tots es reptes menos es que volem esborrar
            for (String linea : reptes) {
                if (!linea.equals(reto)) {
                    writer.write(linea + System.lineSeparator());
                }
            }

            writer.close();
            JOptionPane.showMessageDialog(null, "El reto ha sido borrado exitosamente.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    ///////////////////////////////////////////////////////////////////

    private void obrirMenuEditarRepte() {
        // Crear el JFrame para el menú de edición de repte
        JFrame editarRepteFrame = new JFrame();
        editarRepteFrame.getContentPane().setBackground(new Color(255, 217, 255));
        editarRepteFrame.setBounds(100, 100, 450, 300);
        editarRepteFrame.getContentPane().setLayout(null);

        // Crear el JPanel para el menú de edición de repte
        JPanel editarReptePanel = new JPanel();
        editarReptePanel.setBackground(new Color(235, 207, 235));
        editarReptePanel.setBounds(10, 10, 414, 241);
        editarRepteFrame.getContentPane().add(editarReptePanel);
        editarReptePanel.setLayout(null);

        // titulo
        JLabel lblTitulo = new JLabel("Tria un repte per editar");
        lblTitulo.setFont(new Font("Tahoma", Font.PLAIN, 24));
        lblTitulo.setBounds(10, 20, 300, 30);
        editarReptePanel.add(lblTitulo);

        JLabel lblExplicacion = new JLabel("Te lo vuelvo a explicar máquina");
        lblExplicacion.setBounds(10, 60, 300, 30);
        editarReptePanel.add(lblExplicacion);

        // llista d'opcions de reptes
        List<String> opciones = obtenirPreguntesDArxiu();
        JComboBox<String> comboBoxPreguntas = new JComboBox<>(opciones.toArray(new String[0]));
        comboBoxPreguntas.setBounds(10, 100, 300, 30);
        editarReptePanel.add(comboBoxPreguntas);

        // Botó editar
        JButton btnEditar = new JButton("Editar");
        btnEditar.setBounds(90, 150, 120, 50);
        editarReptePanel.add(btnEditar);

        // Acción al hacer clic en el botón de editar
        btnEditar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String preguntaSeleccionada = (String) comboBoxPreguntas.getSelectedItem();
                if (preguntaSeleccionada != null) {
                    // Obtener los datos del reto seleccionado
                    String[] partes = preguntaSeleccionada.split(":");
                    String pregunta = partes[0];
                    String respuesta = partes[1];
                    String puntuacion = partes[2];

                    // Mostrar el menú de edición del reto
                    mostrarMenuEditarRepte(pregunta, respuesta, puntuacion);
                }
            }
        });
        JButton btnVolver = new JButton("Sortir");
        btnVolver.setBackground(new Color(255, 0, 0));
        btnVolver.setBounds(180, 205, 100, 20);
        editarReptePanel.add(btnVolver);

        btnVolver.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                editarRepteFrame.setVisible(false);
                loginFrame.setVisible(true);

            }
        });
        // Mostrar el JFrame del menú de edición de repte
        editarRepteFrame.setVisible(true);
    }

    private void mostrarMenuEditarRepte(String pregunta, String respuesta, String puntuacion) {
        // Crear el JFrame para el menú de edición de reto
        JFrame editarRetoFrame = new JFrame();
        editarRetoFrame.getContentPane().setBackground(new Color(255, 217, 255));
        editarRetoFrame.setBounds(100, 100, 450, 300);
        editarRetoFrame.getContentPane().setLayout(null);

        // Crear el JPanel para el menú de edición de reto
        JPanel editarRetoPanel = new JPanel();
        editarRetoPanel.setBackground(new Color(235, 207, 235));
        editarRetoPanel.setBounds(10, 10, 414, 241);
        editarRetoFrame.getContentPane().add(editarRetoPanel);
        editarRetoPanel.setLayout(null);

        // Etiqueta y campo de texto para la pregunta
        JLabel lblPregunta = new JLabel("Pregunta:");
        lblPregunta.setBounds(10, 20, 100, 30);
        editarRetoPanel.add(lblPregunta);

        JTextField txtPregunta = new JTextField(pregunta);
        txtPregunta.setBounds(120, 20, 280, 30);
        editarRetoPanel.add(txtPregunta);

        // Etiqueta y campo de texto para la respuesta
        JLabel lblRespuesta = new JLabel("Respuesta:");
        lblRespuesta.setBounds(10, 70, 100, 30);
        editarRetoPanel.add(lblRespuesta);

        JTextField txtRespuesta = new JTextField(respuesta);
        txtRespuesta.setBounds(120, 70, 280, 30);
        editarRetoPanel.add(txtRespuesta);

        // Etiqueta y campo de texto para la puntuación
        JLabel lblPuntuacion = new JLabel("Puntuación:");
        lblPuntuacion.setBounds(10, 120, 100, 30);
        editarRetoPanel.add(lblPuntuacion);

        JTextField txtPuntuacion = new JTextField(puntuacion);
        txtPuntuacion.setBounds(120, 120, 280, 30);
        editarRetoPanel.add(txtPuntuacion);

        // Botón guardar cambios
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setBounds(10, 170, 100, 30);
        editarRetoPanel.add(btnGuardar);

        // Acción al hacer clic en el botón de guardar cambios
        btnGuardar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Obtener los valores actualizados de los campos de texto
                String nuevaPregunta = txtPregunta.getText();
                String nuevaRespuesta = txtRespuesta.getText();
                String nuevaPuntuacion = txtPuntuacion.getText();

                // Actualizar el reto con los nuevos valores
                actualitzarRepte(pregunta, respuesta, puntuacion, nuevaPregunta, nuevaRespuesta, nuevaPuntuacion);

                // Cerrar el JFrame de edición de reto
                editarRetoFrame.dispose();
            }
        });

        // Mostrar el JFrame del menú de edición de reto
        editarRetoFrame.setVisible(true);
    }

    private void actualitzarRepte(String pregunta, String respuesta, String puntuacion, String nuevaPregunta,
            String nuevaRespuesta, String nuevaPuntuacion) {
        try {
            // Obtener todos los reptes del archivo
            List<String> reptes = obtenirPreguntesDArxiu();

            // Crear un FileWriter para escribir en el archivo
            FileWriter writer = new FileWriter(REPTES_FILE);

            // Recorrer todos los reptes y buscar el que se desea actualizar
            for (String reto : reptes) {
                String[] partes = reto.split(":");
                String preg = partes[0];
                String resp = partes[1];
                String punt = partes[2];

                // Verificar si el reto actual coincide con el que se desea actualizar
                if (preg.equals(pregunta) && resp.equals(respuesta) && punt.equals(puntuacion)) {
                    // Actualizar el reto con los nuevos valores
                    reto = nuevaPregunta + ":" + nuevaRespuesta + ":" + nuevaPuntuacion;
                }

                // Escribir el reto en el archivo
                writer.write(reto + System.lineSeparator());
            }

            writer.close();
            JOptionPane.showMessageDialog(null, "El reto ha sido actualizado exitosamente.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}