/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
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
            conectarBaseDatos();
            resultado = null;

        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

    //<editor-fold desc="Metodos">
    private void conectarBaseDatos() throws Exception {
        try {
            conexion = DriverManager.getConnection("jdbc:sqlite:Peluquegest.db"); //Conectamos a la base de datos
            if (conexion != null) {
                consulta = conexion.createStatement();
                crearTablas();
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

    private void crearTablas() throws Exception {
        try {
            String c = "CREATE TABLE IF NOT EXISTS SERVICIOS (";
            c += "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, ";
            c += "nombre TEXT (50) NOT NULL, ";
            c += "precio REAL NOT NULL, ";
            c += "duracion TEXT (50) NOT NULL ";
            c += ");";

            consulta.execute(c);
            consulta.close();
            
            c = "CREATE TABLE IF NOT EXISTS TAREAS (\n"
                    + "	fecha	TEXT ( 50 ) NOT NULL,\n"
                    + "	horaInicio	INTEGER NOT NULL,\n"
                    + "	nombreCliente	TEXT ( 50 ) NOT NULL,\n"
                    + "	nombreServicio	TEXT ( 50 ) NOT NULL,\n"
                    + "	precio	TEXT ( 50 ) NOT NULL,\n"
                    + "	duracion	TEXT ( 50 ) NOT NULL\n"
                    + ");";

            consulta.execute(c);
            consulta.close();

        } catch (Exception e) {
            throw new Exception(e.getMessage());
            
        }
    }

    public String getDuracionTarea(String servicio) throws Exception {
        try {
            resultado.close();
            String c = "select duracion from SERVICIOS where nombre = '" + servicio + "'";
            resultado = consulta.executeQuery(c);
            String duracion = "";
            if (!resultado.isClosed()) {
                duracion = resultado.getString(1);
                consulta.close();

            }
            return duracion;

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

    public int eliminarTarea(String fecha, int horaInicio) throws Exception {
        try {
            String c = "delete from TAREAS where fecha = '" + fecha + "' and horaInicio = " + horaInicio + "";
            int resultado = consulta.executeUpdate(c);
            return resultado;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

    /**
     * Metodo para leer todas la tareas de la fecha que reciba por paramentro
     */
    public ResultSet leerTareas(String fecha) throws Exception {
        try {
            String c = "select horaInicio, nombreCliente, nombreServicio, duracion ";
            c += "from TAREAS ";
            c += "where fecha = '" + fecha + "' order by horaInicio";
            resultado = consulta.executeQuery(c);
            return resultado;

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Metodo para añadir tareas a la base de datos
     *
     * @param fecha
     * @param horaInicio
     * @param nombreCliente
     * @param nombreServicio
     * @param precio
     * @param duracion
     */
    public int añadirTarea(String fecha, int horaInicio, String nombreCliente, String nombreServicio, String precio, String duracion) throws Exception {
        try {
            String c = "insert into TAREAS values ('" + fecha + "', '" + horaInicio + "', '" + nombreCliente + "', '" + nombreServicio + "', '" + precio + "', '" + duracion + "')";
            int resultado = consulta.executeUpdate(c);
            return resultado;

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     *
     * @return
     */
    public ResultSet consultarServicios() throws Exception {
        try {
            consulta.close();
            String c = "select nombre, printf('%.2f', precio), duracion from SERVICIOS";
            resultado = consulta.executeQuery(c);
            return resultado;

        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public void insertarNuevoServicio(String nombre, float precio, String duracion) throws Exception {
        try {
            consulta.executeUpdate("insert into SERVICIOS(nombre, precio, duracion) values ('" + nombre + "', '" + precio + "', '" + duracion + "')");
            consulta.close();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

    public int eliminarServicio(String nombre) throws Exception {
        try {
            String c = "delete from SERVICIOS where nombre = '" + nombre + "'";
            int resultado = consulta.executeUpdate(c);
            consulta.close();
            return resultado;

        } catch (Exception ex) {
            throw new Exception(ex.getMessage());

        }

    }
    //</editor-fold>
}
