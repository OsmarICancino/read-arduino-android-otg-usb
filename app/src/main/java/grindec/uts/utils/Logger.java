package grindec.uts.utils;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * Created by Reynolds on 10/08/2015.
 */
public class Logger {
    public static void log( String logName, Exception msj, Context context ) {
        try {
            File appFolder = new File(context.getFilesDir(),"uts");
            if( !appFolder.exists() )
                appFolder.mkdir();
            
            if( appFolder.exists() ) {
                File archivoLog = new File(appFolder, logName);
                if( !archivoLog.exists() ) {
                	archivoLog.createNewFile();
                }
                
                if( archivoLog.exists() ) {
	                PrintWriter pw = new PrintWriter(new FileWriter(archivoLog,true));
                    msj.printStackTrace(pw);
	                pw.close();
                }
            }
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }

    public static boolean logString( String logName, String msj, Context context ) {
        try {
            File appFolder = new File(context.getFilesDir(),"uts");
            if( !appFolder.exists() )
                appFolder.mkdir();

            if( appFolder.exists() ) {
                File archivoLog = new File(appFolder, logName);
                if( !archivoLog.exists() ) {
                    archivoLog.createNewFile();
                }

                if( archivoLog.exists() ) {
                    PrintWriter pw = new PrintWriter(new FileWriter(archivoLog,true));
                    pw.println(msj);
                    pw.close();
                    return true;
                }
            }

            return false;
        } catch( Exception e ) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static String read( String logName, Context context ) {
        try {
            File appFolder = new File(context.getFilesDir(),"uts");
                        
            if( appFolder.exists() ) {
                File archivoLog = new File(appFolder, logName);
                
                if( archivoLog.exists() ) {
	                BufferedReader br = new BufferedReader( new FileReader(archivoLog) );
	                String txt = "";
	                String linea = null;
	                while( (linea=br.readLine())!=null ) {
	                	txt += linea+"\n";
	                }
	                br.close();
	                return txt;
                } else {
                	return "--No Existen Entradas--";
                }
            } else {
                return "--No Existen Entradas--";
            }
        } catch( Exception e ) {
            e.printStackTrace();
            return "--Error de Lectura--";
        }
    }
}
