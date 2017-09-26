package org.exoplatform.platform.organization.checker;

import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import org.exoplatform.services.database.utils.JDBCUtils;

import java.sql.Connection;
import java.util.Map;

/**
 * Liquibase task to detect and clean duplicated idm entries
 */
public abstract class IDMDataRepair implements CustomTaskChange {

    private String tableName;

    @Override
    public void execute(Database database) throws CustomChangeException {
        JdbcConnection dbConn = (JdbcConnection) database.getConnection();

        //If table exist check and clean duplicated entries before
        // add the Unique key by hibernate DLL
        if (tableExists(tableName, dbConn.getWrappedConnection())) {
            Map<Integer, Integer> duplicatedItems = null;
            try {
                duplicatedItems = findDuplicatedItems();
                for (Integer id : duplicatedItems.keySet()) {
                    updateRelatedItems(id, duplicatedItems.get(id));
                }
            } catch (IDMRepairException e) {
                throw new CustomChangeException(e.getMessage());
            } finally {
                for (Integer id : duplicatedItems.keySet()) {
                    try {
                        removeDuplicatedItems(id);
                    } catch (IDMRepairException e) {
                        throw new CustomChangeException(e.getMessage());
                    }
                }
            }

        }
    }

    @Override
    public String getConfirmationMessage() {
        return null;
    }

    @Override
    public void setUp() throws SetupException {

    }

    @Override
    public void setFileOpener(ResourceAccessor resourceAccessor) {

    }

    /**
     * Find the list of duplicated entries
     * @return the list of duplicated items
     * @throws IDMRepairException
     */
    protected abstract Map<Integer, Integer> findDuplicatedItems() throws IDMRepairException;

    /**
     * Update the list of the tables reference the duplicated entries
     * @param id
     * @param maxID
     * @return
     * @throws IDMRepairException
     */
    protected abstract boolean updateRelatedItems(int id, int maxID) throws IDMRepairException;

    /***
     * remove the liste of duplicated entries
     * @param id
     * @return
     * @throws IDMRepairException
     */
    protected abstract boolean removeDuplicatedItems(int id) throws IDMRepairException;


    @Override
    public ValidationErrors validate(Database database) {
        return null;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public boolean tableExists(String tableName, Connection con) {
        return JDBCUtils.tableExists(tableName, con);
    }
}
