package liquibase.change;

import liquibase.database.*;
import liquibase.database.sql.SqlStatement;
import liquibase.database.sql.RawSqlStatement;
import liquibase.database.structure.Column;
import liquibase.database.structure.DatabaseObject;
import liquibase.database.structure.Table;
import liquibase.exception.UnsupportedChangeException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Makes an existing column into an auto-increment column.
 * This change is only valid for databases with auto-increment/identity columns.
 * The current version does not support MS-SQL.
 */
public class AddAutoIncrementChange extends AbstractChange {

    private String tableName;
    private String columnName;
    private String columnDataType;

    public AddAutoIncrementChange() {
        super("addAutoIncrement", "Set Column as Auto-Increment");
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnDataType() {
        return columnDataType;
    }

    public void setColumnDataType(String columnDataType) {
        this.columnDataType = columnDataType;
    }

    public SqlStatement[] generateStatements(Database database) throws UnsupportedChangeException {
        if (database instanceof OracleDatabase) {
            throw new UnsupportedChangeException("Oracle does not support auto-increment columns");
        } else if (database instanceof MSSQLDatabase) {
            throw new UnsupportedChangeException("MS SQL Server does not support marking existing columns as auto-increment");
        } else if (database instanceof PostgresDatabase) {
            throw new UnsupportedChangeException("PostgreSQL does not support auto-increment columns");
        } else if (database instanceof DerbyDatabase) {
            throw new UnsupportedChangeException("Derby does not support adding auto-increment to existing columns");
        } else if (database instanceof HsqlDatabase) {
            return new SqlStatement[] { new RawSqlStatement("ALTER TABLE "+getTableName()+" ALTER COLUMN "+getColumnName()+" "+getColumnDataType()+" GENERATED BY DEFAULT AS IDENTITY IDENTITY") };
        } else if (database instanceof CacheDatabase) {
            throw new UnsupportedChangeException("Add Auto-Increment change not currently supported for Cache");
        }

        return new SqlStatement[]{
                new RawSqlStatement("ALTER TABLE " + getTableName() + " MODIFY " + getColumnName() + " " + getColumnDataType() + " AUTO_INCREMENT"),
        };
    }

    public String getConfirmationMessage() {
        return "Column Set as Auto-Increment";
    }

    public Element createNode(Document currentChangeLogFileDOM) {
        Element node = currentChangeLogFileDOM.createElement("addAutoIncrement");
        node.setAttribute("tableName", getTableName());
        node.setAttribute("columnName", getColumnName());

        return node;
    }

    public Set<DatabaseObject> getAffectedDatabaseObjects() {
        Column column = new Column();

        Table table = new Table();
        table.setName(tableName);
        column.setTable(table);

        column.setName(columnName);

        return new HashSet<DatabaseObject>(Arrays.asList(table, column));

    }
    
}
