
package database;

public class SomethingDatabase extends MySQLDatabase{
    public SomethingDatabase() throws Exception{
        super("localhost", 3306, "gestionmateriel", "root", "");
    }
   
}
