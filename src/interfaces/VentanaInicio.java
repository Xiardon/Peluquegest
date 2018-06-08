/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfaces;

import clases.BaseDatos;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import jdk.nashorn.internal.parser.DateParser;

/**
 *
 * @author Marcos
 */
public class VentanaInicio extends javax.swing.JFrame {

    private DefaultTableModel modeloTablaHoras;
    private BaseDatos db;
    private ResultSet resultado;
    private DefaultTableModel modeloTablasTareas;
    private String fecha;
    private int filasEliminadas;
    private int filasAEliminar;
    private Color color1;
    private Color color2;
    private Color colorAsigando;

    /**
     * Creates new form VentanaInicio
     */
    public VentanaInicio() {
        initComponents();
        db = null;
        resultado = null;
        modeloTablaHoras = (DefaultTableModel) tablaHoras.getModel();
        modeloTablasTareas = (DefaultTableModel) tablaTareas.getModel();
        fecha = new SimpleDateFormat("dd-MM-yyyy").format(calendario.getDate()); //Obtenermos la fecha actual
        filasEliminadas = 0;
        filasAEliminar = 0;
        color1 = new Color(224, 129, 129);
        color2 = new Color(211, 211, 137);
        colorAsigando = null;
        crearAgenda(fecha);
        darEsteticaTablas();

    }

    /**
     * Metodo para crear la agenda
     */
    private void crearAgenda(String fecha) {
        try {
            db = new BaseDatos();
            resultado = db.leerTareas(fecha);

            System.out.println(tablaTareas.getRowCount());

            modeloTablasTareas = (DefaultTableModel) tablaTareas.getModel();
            int row = tablaTareas.getRowCount();

            for (int i = 0; i < row; i++) {
                System.out.println(tablaTareas.getRowCount());
                modeloTablasTareas.removeRow(0);
            }

            for (int i = 0; i < 26; i++) {
                modeloTablasTareas.addRow(new Object[0]);
            }

            Object datos[] = new Object[4]; //Numero de campos(columnas) de la consulta
            int posicion = 0;
            while (resultado.next()) {
                for (int i = 0; i < 4; i++) {
                    datos[i] = resultado.getObject(i + 1); //En el resulset el indice empieza en 1
                }

                filasAEliminar = 0;
                String tarea = datos[1] + "---" + datos[2]; //Servicio y cliente
                posicion = posicionAgenda(datos[0].toString()); //Obtenemos la posicion.
                String duracion = datos[3].toString();

                tablaTareas.setValueAt(tarea, posicion, 0);
                ajustarAjenda(posicion, convertirDuracion(duracion));
                eliminarFilas(posicion);
                filasEliminadas += filasAEliminar;
                darEsteticaTablas();

            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Excepción crearagenda!!", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void asignarColor(int posicion) {
        if (color1 == colorAsigando) {
        }
    }

    /**
     * Metodo para altura de las filas y estetica
     *
     */
    private void ajustarAjenda(int posicion, int altura) {
        tablaTareas.setRowHeight(posicion, altura);
    }

    /**
     * Metodo para saber la posicion en la agenda de una tarea en funcion de la
     * hora de inicio.
     */
    private int posicionAgenda(String hora) {

        boolean buscar = true;
        int i = 0; // poscion en tabla horas
        int diferencia = 0;
        int x = 0; // posicion en tabla tareas

        while (i < tablaHoras.getRowCount() && buscar) {
            String h[] = tablaHoras.getValueAt(i, 0).toString().split(":");
            String h2 = h[0] + h[1];

            if (h2.equals(hora)) {
                buscar = false;
            }
            i++;
        }
        if (tablaHoras.getRowCount() > tablaTareas.getRowCount()) {
            diferencia = tablaHoras.getRowCount() - tablaTareas.getRowCount();
            i = i - diferencia;
        }

        return i - 1;
    }

    /**
     * Metodo para abrir la ventana de nueva tarea
     */
    private void nuevaTarea() {
        int fila = tablaTareas.getSelectedRow();
        int filasLibres = 0;
        try {
            if (tablaHoras.getSelectedRow() != -1 && tablaTareas.getValueAt(fila, 0) == null) { //Comprobamos que la fila este libre y que este selec
                int i = 1;

                while (i < (tablaTareas.getRowCount() - tablaTareas.getSelectedRow()) && tablaTareas.getValueAt(tablaTareas.getSelectedRow() + i, 0) == null) {
                    filasLibres++;
                    i++;
                }

                Date date = calendario.getDate();
                fecha = new SimpleDateFormat("dd-MM-yyyy").format(date); //Obtenemos la fecha del calendario en el formato dia mes año
                NuevaTarea nt = new NuevaTarea(this, rootPaneCheckingEnabled);
                nt.añadirTiempos(fecha, tablaHoras.getValueAt(tablaHoras.getSelectedRow(), 0).toString(), filasLibres); //Obtengo la fechan y la hora
                nt.setVisible(true);
                if (nt.getAceptar()) { //Controlamos si se ha aceptado la tarea.
                    crearAgenda(fecha);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Seleccione una hora valida para el inicio de la tarea", "Nueva Tarea", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Excepción!!", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Metodo para mantener enlazada la seleccion entre tablas
     */
    private void sincornizarSeleccion(String nombreTabla) {
        try {
            if (nombreTabla == "tablaTareas") {
                int posicion = tablaTareas.getSelectedRow();
                int numeroFilasSumar = 0;
                for (int i = 0; i < posicion; i++) {
                    if (tablaTareas.getValueAt(i, 0) != null) {
                        String tarea[] = tablaTareas.getValueAt(i, 0).toString().split("---");
                        String nombre = tarea[1];
                        String duracion = db.getDuracionTarea(nombre);
                        convertirDuracion(duracion);
                        numeroFilasSumar += filasAEliminar;
                    }
                }

                tablaHoras.changeSelection(tablaTareas.getSelectedRow() + numeroFilasSumar, 0, false, false); //Selecciona la misma fila en la otra tabla
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Excepciónsincronizar!!", JOptionPane.WARNING_MESSAGE);
        }

    }

    /**
     * Metodo para obtener covertir en pixeles la duracion para el alto de las
     * filas
     *
     * @param duracion
     * @return
     */
    public int convertirDuracion(String duracion) {
        int altoFila = tablaHoras.getRowHeight();
        filasAEliminar = 0;

        switch (duracion) {
            case "30 minutos":
                altoFila = altoFila;
                filasAEliminar = 0;
                break;
            case "1 hora":
                altoFila = altoFila * 2;
                filasAEliminar = 1;
                break;
            case "1 hora 30 minutos":
                altoFila = altoFila * 3;
                filasAEliminar = 2;
                break;
            case "2 horas":
                altoFila = altoFila * 4;
                filasAEliminar = 3;
                break;
            case "2 horas 30 minutos":
                altoFila = altoFila * 5;
                filasAEliminar = 4;
                break;
            case "3 horas":
                altoFila = altoFila * 6;
                filasAEliminar = 5;
                break;
            case "3 horas 30 minutos":
                altoFila = altoFila * 7;
                filasAEliminar = 6;
                break;
            case "4 horas":
                altoFila = altoFila * 8;
                filasAEliminar = 7;
                break;
            default:
                altoFila = altoFila;
                break;
        }

        return altoFila;
    }

    /**
     * Metodo para eliminar filas en funcion de la duracion
     */
    private void eliminarFilas(int posicion) {
        try {
            int i = 0;
            while (i < filasAEliminar) {
                modeloTablasTareas.removeRow(posicion + 1);
                i++;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Excepción eliminarFilas!!", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * *
     * Metodo para mejorar la presentacion de las tablas
     */
    private void darEsteticaTablas() {
        DefaultTableCellRenderer Alinear = new DefaultTableCellRenderer();
        Alinear.setHorizontalAlignment(SwingConstants.CENTER);
        tablaHoras.getColumnModel().getColumn(0).setCellRenderer(Alinear);
        tablaTareas.getColumnModel().getColumn(0).setCellRenderer(Alinear);

        tablaTareas.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() { //para dar color a las celdas
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

                if (table.getValueAt(row, col) != null) {
                    if (color1 == colorAsigando) {
                        setBackground(color2);
                        colorAsigando = color2;
                    } else {
                        setBackground(color1);
                        colorAsigando = color1;
                    }
                }
                return this;
            }
        });

    }

    /**
     * Metodo para mostrar la ventana con la lista de servicios
     */
    private void abrirServicios() {
        try {
            ListaServicios ls = new ListaServicios(this, rootPaneCheckingEnabled);
            ls.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Excepción!!", JOptionPane.WARNING_MESSAGE);
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fondo = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaHoras = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false; //Disallow the editing of any cell
            }
        };
        tablaHoras = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaTareas = new javax.swing.JTable();
        calendario = new org.freixas.jcalendar.JCalendarCombo();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Peluquegest");
        setResizable(false);

        fondo.setBackground(new java.awt.Color(154, 186, 243));
        fondo.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 255), 10, true));
        fondo.setFocusable(false);

        jLabel1.setBackground(new java.awt.Color(216, 185, 248));
        jLabel1.setFont(new java.awt.Font("Garamond", 3, 48)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(102, 151, 151));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("PELUQUEGEST");
        jLabel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 153), 5, true));
        jLabel1.setOpaque(true);

        jLabel2.setBackground(new java.awt.Color(204, 204, 255));
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/Icono.png"))); // NOI18N

        jPanel1.setBackground(new java.awt.Color(173, 207, 173));
        jPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 255, 204), 5, true));

        jButton3.setBackground(new java.awt.Color(204, 255, 204));
        jButton3.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        jButton3.setForeground(new java.awt.Color(120, 120, 227));
        jButton3.setText("Nueva Tarea");
        jButton3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 255)));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonPulsado(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(204, 255, 204));
        jButton5.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        jButton5.setForeground(new java.awt.Color(120, 120, 227));
        jButton5.setText("Servicios");
        jButton5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 255)));
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonPulsado(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(116, 116, 116)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(101, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(119, 153, 153));
        jPanel2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(100, 159, 167), 8, true));

        tablaHoras.setBackground(new java.awt.Color(177, 172, 198));
        tablaHoras.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 102, 102), 1, true));
        tablaHoras.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        tablaHoras.setForeground(new java.awt.Color(0, 51, 51));
        tablaHoras.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"8:00"},
                {"8:30"},
                {"9:00"},
                {"9:30"},
                {"10:00"},
                {"10:30"},
                {"11:00"},
                {"11:30"},
                {"12:00"},
                {"12:30"},
                {"13:00"},
                {"13:30"},
                {"14:00"},
                {"14:30"},
                {"15:00"},
                {"15:30"},
                {"16:00"},
                {"16:30"},
                {"17:00"},
                {"17:30"},
                {"18:00"},
                {"18:30"},
                {"19:00"},
                {"19:30"},
                {"20:00"},
                {"20:30"}
            },
            new String [] {
                "Horas"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaHoras.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tablaHoras.setAutoscrolls(false);
        tablaHoras.setEnabled(false);
        tablaHoras.setFocusable(false);
        tablaHoras.setName("tablaHoras"); // NOI18N
        tablaHoras.setRowHeight(20);
        tablaHoras.setRowMargin(0);
        tablaHoras.setShowVerticalLines(false);
        tablaHoras.setTableHeader(null);
        tablaHoras.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clickRaton(evt);
            }
        });
        jScrollPane1.setViewportView(tablaHoras);

        tablaTareas.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 102, 102), 1, true));
        tablaTareas.setFont(new java.awt.Font("Sitka Small", 0, 18)); // NOI18N
        tablaTareas.setForeground(new java.awt.Color(0, 0, 102));
        tablaTareas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "Tareas"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaTareas.setName("tablaTareas"); // NOI18N
        tablaTareas.setRowHeight(20);
        tablaTareas.setTableHeader(null);
        tablaTareas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clickRaton(evt);
            }
        });
        jScrollPane2.setViewportView(tablaTareas);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 522, Short.MAX_VALUE)
                    .addComponent(jScrollPane2)))
        );

        calendario.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(86, 86, 151), 5), "Calendario", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(88, 88, 220))); // NOI18N
        calendario.setToolTipText("");
        calendario.setTimeFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N

        javax.swing.GroupLayout fondoLayout = new javax.swing.GroupLayout(fondo);
        fondo.setLayout(fondoLayout);
        fondoLayout.setHorizontalGroup(
            fondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fondoLayout.createSequentialGroup()
                .addGroup(fondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(fondoLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(fondoLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(fondoLayout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(calendario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(8, 8, 8)
                .addGroup(fondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(fondoLayout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 561, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        fondoLayout.setVerticalGroup(
            fondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fondoLayout.createSequentialGroup()
                .addGroup(fondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(fondoLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2))
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(fondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(fondoLayout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addComponent(calendario, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, fondoLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(fondo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 807, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(fondo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void botonPulsado(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonPulsado
        // TODO add your handling code here:
        JButton boton = (JButton) evt.getSource();

        switch (boton.getText()) {
            case "Servicios":
                abrirServicios();
                break;
            case "Nueva Tarea":
                nuevaTarea();
                break;
        }
    }//GEN-LAST:event_botonPulsado

    private void clickRaton(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clickRaton
        // TODO add your handling code here:
        JTable t = (JTable) evt.getSource();

        sincornizarSeleccion(t.getName());

    }//GEN-LAST:event_clickRaton

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel((UIManager.getSystemLookAndFeelClassName()));
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VentanaInicio.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VentanaInicio.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VentanaInicio.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VentanaInicio.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VentanaInicio().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.freixas.jcalendar.JCalendarCombo calendario;
    private javax.swing.JPanel fondo;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tablaHoras;
    private javax.swing.JTable tablaTareas;
    // End of variables declaration//GEN-END:variables
}
