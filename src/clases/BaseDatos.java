/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Esta clase permite conectarse y gestionar la base de datos del programa
 *
 * @author Marcos
 */
public class BaseDatos {

    // <editor-fold desc="Atributos de la clase">
    private Connection conexion; //Objeto para gestionar la consulta a la base de datos
    private Statement consulta; //Para ejecutar consultas a la base de datos
    private ResultSet resultado;//Para guardar el resultado de una consulta
    // </editor-fold>

    /**
     * Constructor de la clase
     */
    public BaseDatos() throws Exception {
        try {
            Class.forName("org.sqlite.JDBC"); //Resitramos el driver de SQlite para la conexion
            conexion = DriverManager.getConnection("jdbc:sqlite:Peluquegest.db"); //Conectamos a la base de datos
            consulta = conexion.createStatement();
            resultado = null;
            System.out.println(conexion.getSchema());
        } catch (Exception ex) {
            throw new Exception("No se puede conectar a la base de datos");
        }
    }

    //<editor-fold desc="Metodos">
    /**
     *
     * @return
     */
    public ResultSet consultarServicios() throws Exception {
        try {
            String c = "select nombre, precio, duracion from SERVICIOS";
            resultado = consulta.executeQuery(c);
            return resultado;

        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        } finally {
        }

    }
    //</editor-fold>
}
