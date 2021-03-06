/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfaces;

import clases.BaseDatos;
import java.awt.Font;
import java.awt.Image;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.paint.Color;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Marcos
 */
public class ListaServicios extends javax.swing.JDialog {

    private BaseDatos db;
    private DefaultTableModel modeloTabla;
    private boolean aceptarNuevo;
    private String nombreServicio;
    private String precio;
    private String duracion;

    /**
     * Creates new form ListaServicios
     */
    public ListaServicios(java.awt.Frame parent, boolean modal) throws Exception {
        super(parent, modal);
        initComponents();
        aceptarNuevo = false;
        nombreServicio = "";
        precio = "";
        duracion = "";
        modeloTabla = (DefaultTableModel) tablaServicios.getModel(); //Obtenemos el modelo de la tabla
        db = new BaseDatos();
        this.setIconImage(new ImageIcon("src/imagenes/Icono.png").getImage());
        crearTabla();
        listarServicios();
        btnSeleccionar.setVisible(false);

    }

    public boolean getAceptar() {
        return aceptarNuevo;
    }

    public String getNombreServicio() {
        return nombreServicio;
    }

    public String getPrecio() {
        return precio;
    }

    public String getDuracion() {
        return duracion;
    }

    /**
     * Metod para guardar los valores del servicio
     */
    private void aceptarSerevicio() {
        try {
            if (tablaServicios.getSelectedRow() != -1) {
                nombreServicio = tablaServicios.getValueAt(tablaServicios.getSelectedRow(), 0).toString();
                precio = tablaServicios.getValueAt(tablaServicios.getSelectedRow(), 1).toString();
                duracion = tablaServicios.getValueAt(tablaServicios.getSelectedRow(), 2).toString();
                aceptarNuevo = true;
                this.setVisible(false);
            }else{
                JOptionPane.showMessageDialog(null, "No se ha seleccionado ningun servicio de la lista", "Seleccionar servicio", JOptionPane.WARNING_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Excepción!!", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Metodo para ocultar botones
     */
    public void mostrarBotones() {
        btnSeleccionar.setVisible(true);
        btnAñadir.setVisible(false);
        btnEliminar.setVisible(false);
    }

    /**
     * Metodo para añadir un nuevo servicio a la base de datos y a la tabla
     */
    private void añadirNuevoServicio() {
        try {
            CrearServicio cs = new CrearServicio(null, rootPaneCheckingEnabled); //Creamos una nueva ventana para añadir el servicio.
            cs.setVisible(true);
            if (cs.getAceptar()) {
                cs.setVisible(false);
                db.insertarNuevoServicio(cs.getNombreServicio(), cs.getPrecioServicio(), cs.getDuracionServicio());
                listarServicios();
                JOptionPane.showMessageDialog(null, "Nuevo servicio creado con exito", "Añadir nuevo servicio", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Excepción!!", JOptionPane.WARNING_MESSAGE);
        }

    }

    /**
     * Metodo para buscar en la tabla
     */
    private void buscarDatoTabla() {
        try {
            TableRowSorter filtro = new TableRowSorter(modeloTabla); //Creo el filtro
            tablaServicios.setRowSorter(filtro); //Se lo añado a la tabla
            filtro.setRowFilter(RowFilter.regexFilter(txtBuscar.getText(), 0)); //Le paso los parametros a filtrar
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Excepción!!", JOptionPane.WARNING_MESSAGE);
        }

    }

    /**
     * Metodo para mejorar la estetica de la tabla
     */
    private void crearTabla() {
        tablaServicios.getTableHeader().setFont(new Font("Garamond", Font.BOLD, 12));
        tablaServicios.getTableHeader().setForeground(java.awt.Color.darkGray);

    }

    /**
     * Metodo para eliminar un servicio de la tabla y la base de datos
     */
    private void eliminarServicio() {
        try {
            if (tablaServicios.getSelectedRow() != -1) { //Compruebo que tengo seleccionada una fila
                String nombreservicio = tablaServicios.getValueAt(tablaServicios.getSelectedRow(), 0).toString();

                if (db.eliminarServicio(nombreservicio) != 0) {
                    modeloTabla.removeRow(tablaServicios.getSelectedRow());
                    JOptionPane.showMessageDialog(null, "Se ha eliminado el servicio", "Elimninar servicio", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "No se ha seleccionado ningún servcio de la lista", "Error al eliminar", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Excepción!!", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Metodo para cargar la lista de servicios en la tabla
     */
    private void listarServicios() throws Exception {
        try {
            modeloTabla.setRowCount(0); //Limpio la tabla
            ResultSet resultados = db.consultarServicios();
            Object datos[] = new Object[3]; //Numero de campos(columnas) de la consulta

            while (resultados.next()) {
                for (int i = 0; i < 3; i++) {
                    datos[i] = resultados.getObject(i + 1); //En el resulset el indice empieza en 1
                }
                modeloTabla.addRow(datos);
            }
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

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaServicios = new javax.swing.JTable();
        txtBuscar = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        btnAñadir = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnSeleccionar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Peluquegest-Añadir servicio");
        setBackground(new java.awt.Color(153, 153, 255));
        setIconImage(null);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(230, 230, 235));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 255), 9, true), "Lista de Servicios", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Century Gothic", 0, 36), new java.awt.Color(51, 51, 255))); // NOI18N
        jPanel1.setForeground(new java.awt.Color(153, 204, 255));
        jPanel1.setToolTipText("");

        tablaServicios.setBackground(new java.awt.Color(204, 255, 204));
        tablaServicios.setBorder(javax.swing.BorderFactory.createEtchedBorder(null, new java.awt.Color(255, 0, 0)));
        tablaServicios.setFont(new java.awt.Font("Yu Gothic UI Semilight", 1, 12)); // NOI18N
        tablaServicios.setForeground(new java.awt.Color(102, 102, 255));
        tablaServicios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Servicio", "Precio(en Euros)", "Duración"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaServicios.setOpaque(false);
        jScrollPane1.setViewportView(tablaServicios);

        txtBuscar.setFont(new java.awt.Font("Trebuchet MS", 0, 14)); // NOI18N
        txtBuscar.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(51, 51, 255), null));
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                teclaPulsada(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Yu Gothic", 1, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/buscar.png"))); // NOI18N
        jLabel1.setText("Buscar servicio");

        btnAñadir.setBackground(new java.awt.Color(204, 204, 255));
        btnAñadir.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        btnAñadir.setForeground(new java.awt.Color(102, 0, 204));
        btnAñadir.setText("Añadir servicio");
        btnAñadir.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 255)));
        btnAñadir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonPulsado(evt);
            }
        });

        btnEliminar.setBackground(new java.awt.Color(204, 204, 255));
        btnEliminar.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        btnEliminar.setForeground(new java.awt.Color(102, 0, 204));
        btnEliminar.setText("Eliminar servicio");
        btnEliminar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 255)));
        btnEliminar.setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonPulsado(evt);
            }
        });

        btnSeleccionar.setBackground(new java.awt.Color(204, 204, 255));
        btnSeleccionar.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        btnSeleccionar.setForeground(new java.awt.Color(102, 0, 204));
        btnSeleccionar.setText("Seleccionar");
        btnSeleccionar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 255)));
        btnSeleccionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonPulsado(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(66, 66, 66)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(144, 144, 144))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnAñadir, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSeleccionar, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAñadir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnSeleccionar, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void teclaPulsada(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_teclaPulsada
        // TODO add your handling code here:
        buscarDatoTabla();
    }//GEN-LAST:event_teclaPulsada

    private void botonPulsado(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonPulsado
        // TODO add your handling code here:
        JButton boton = (JButton) evt.getSource();
        switch (boton.getText()) {
            case "Eliminar servicio":
                eliminarServicio();
                break;
            case "Añadir servicio":
                añadirNuevoServicio();
                break;
            case "Seleccionar":
                aceptarSerevicio();
        }
    }//GEN-LAST:event_botonPulsado

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
            java.util.logging.Logger.getLogger(ListaServicios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ListaServicios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ListaServicios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ListaServicios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ListaServicios dialog = new ListaServicios(new javax.swing.JFrame(), true);
                    dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosing(java.awt.event.WindowEvent e) {
                            System.exit(0);
                        }
                    });
                    dialog.setVisible(true);
                } catch (Exception ex) {
                    Logger.getLogger(ListaServicios.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAñadir;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnSeleccionar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tablaServicios;
    private javax.swing.JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables

}
