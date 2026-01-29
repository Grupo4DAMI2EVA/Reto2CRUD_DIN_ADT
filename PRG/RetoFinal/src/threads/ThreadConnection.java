package threads;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.ConnectionPool;

/**
 * Thread class to obtain a database connection asynchronously. Useful to avoid blocking the main GUI thread.
 *
 * Author: acer
 */
public class ThreadConnection extends Thread {

    private int delay = 30;
    private boolean end = false;
    private boolean ready = false;
    private Connection con;

    public ThreadConnection(int delay) {
        this.delay = delay;
    }

    /**
     * Returns the connection obtained by the thread.
     * @return 
     */
    public Connection getConnection() {
        return con;
    }

    /**
     * Returns true if the connection is ready.
     * @return 
     */
    public boolean isReady() {
        return ready;
    }

    /**
     * Signals the thread to release the connection and stop running.
     */
    public void releaseConnection() {
        this.end = true;
        this.interrupt();
    }

    @Override
    public void run() {
        try {
            try {
                con = ConnectionPool.getConnection();
            } catch (SQLException ex) {
                Logger.getLogger(ThreadConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            ready = true;

            while (!end) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    if (end) {
                        break;
                    }
                    Thread.currentThread().interrupt();
                }
            }

            try {
                Thread.sleep(delay * 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    Logger.getLogger(ThreadConnection.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
