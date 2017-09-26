package org.exoplatform.platform.organization.checker;

import liquibase.database.jvm.JdbcConnection;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Utility class contain the list of inspect and repair queries
 */
public class IDMInspectionQuery {

    private static final Log LOG                            = ExoLogger.getLogger(IDMInspectionQuery.class);


    /**
     * Table jbid_realm
     */
    //1-Find duplicated query
    protected static String FIND_DUPLICATED_JBID_REALM = "select max(ID) max ,NAME, count(*)"
            + "   FROM jbid_realm group by NAME having count(*) > 1";

    protected static String FIND_ITEM_JBID_REALM_BY_NAME = "select ID from jbid_realm where NAME=? and ID < ?";

    //2-update related table query
    protected static String UPDATE_REALM_JBID_IO_REL_NAME = "update jbid_io_rel_name set REALM=? where REALM IN ?";

    protected static String UPDATE_REALM_JBID_PROPS_REL = "update jbid_io_rel_props set PROP_ID=? where PROP_ID IN ?";

    protected static String UPDATE_REALM_JBID_IO = "update jbid_io set REALM=? where REALM IN ?";

    //3-remove duplicated query
    protected static String REMOVE_DUPLICATED_JBID_REALM = "remove from jbid_realm where ID IN ?";


    /**
     * Table jbid_io_creden
     */
    //1-Find duplicated query
    protected static String FIND_DUPLICATED_JBID_CREDEN = "select max(ID) max ,IDENTITY_OBJECT_ID, CREDENTIAL_TYPE, count(*)"
            + "   FROM jbid_io_creden group by IDENTITY_OBJECT_ID, CREDENTIAL_TYPE having count(*) > 1";

    protected static String FIND_ITEM_JBID_CREDEN_BY_KEY = "select ID from jbid_io_creden where"
            + " IDENTITY_OBJECT_ID= ? and CREDENTIAL_TYPE= ? and ID < ?";

    //2-update related table query
    //TODO

    //3-remove duplicated query
    //TODO

    /**
     * Table jbid_io_attr
     */
    //1-Find duplicated query
    protected static String FIND_DUPLICATED_JBID_ATTR = "select max(ATTRIBUTE_ID) max ,IDENTITY_OBJECT_ID, NAME, count(*)"
            + "   FROM jbid_io_attr group by IDENTITY_OBJECT_ID, NAME having count(*) > 1";


    //2-update related table query
    //TODO

    //3-remove duplicated query
    //TODO

    /**
     * Table jbid_io_rel_name
     */
    //1-Find duplicated query
    protected static String FIND_DUPLICATED_JBID_REL_NAME = "select max(ID) max ,NAME, REALM, count(*)   FROM jbid_io_rel_name"
            + " group by NAME, REALM having count(*) > 1";

    protected static String FIND_ITEM_JBID_REL_NAME =  "select ID from jbid_io_rel_name where NAME = ? and REALM = ? and ID < ?";


    //2-update related table query
    //TODO

    //3-remove duplicated query
    //TODO

    /**
     * Table jbid_io_rel
     */
    //1-Find duplicated query
    protected static String FIND_DUPLICATED_JBID_REL = "select max(ID) max ,FROM_IDENTITY, NAME, TO_IDENTITY, REL_TYPE,  count(*)"
            + "   FROM jbid_io_rel group by FROM_IDENTITY, NAME, TO_IDENTITY, REL_TYPE having count(*) > 1";



    //2-update related table query
    //TODO

    //3-remove duplicated query
    //TODO

    /**
     * Prepared Statement
     */
    protected PreparedStatement findDuplicatedRealm;

    protected PreparedStatement findDuplicatedCreden;

    protected PreparedStatement findDuplicatedAttr;

    protected PreparedStatement findDuplicatedRel;

    protected PreparedStatement findDuplicatedRelName;


    public ResultSet findDuplicatedRealm(JdbcConnection connection) throws IDMRepairException{
        try(PreparedStatement findDuplicatedRealm_ = connection.prepareStatement(FIND_DUPLICATED_JBID_REALM)
        ) {
            findDuplicatedRealm = findDuplicatedRealm_;
            ResultSet result = findDuplicatedRealm.executeQuery();
            return result;
        } catch (Exception e) {
            LOG.error("Error to get duplicated items on Realm table " + e.getMessage(), e);
            throw new IDMRepairException(e.getMessage());
        }
    }

    public ResultSet findDuplicatedCreden(JdbcConnection connection) throws IDMRepairException{
        try(PreparedStatement findDuplicatedCreden_ = connection.prepareStatement(FIND_DUPLICATED_JBID_CREDEN)
        ) {
            findDuplicatedCreden = findDuplicatedCreden_;
            ResultSet result = findDuplicatedCreden.executeQuery();
            return result;
        } catch (Exception e) {
            LOG.error("Error to get duplicated items on Creden table" + e.getMessage(), e);
            throw new IDMRepairException(e.getMessage());

        }
    }

    public ResultSet findDuplicatedAttr(JdbcConnection connection) throws IDMRepairException{
        try(PreparedStatement findDuplicatedAttr_ = connection.prepareStatement(FIND_DUPLICATED_JBID_ATTR)
        ) {
            findDuplicatedAttr = findDuplicatedAttr_;
            ResultSet result = findDuplicatedAttr.executeQuery();
            return result;
        } catch (Exception e) {
            LOG.error("Error to get duplicated items on Attr table" + e.getMessage(), e);
            throw new IDMRepairException(e.getMessage());

        }
    }

    public ResultSet findDuplicatedRel(JdbcConnection connection) throws IDMRepairException{
        try(PreparedStatement findDuplicatedRel_ = connection.prepareStatement(FIND_DUPLICATED_JBID_REL_NAME)
        ) {
            findDuplicatedRel = findDuplicatedRel_;
            ResultSet result = findDuplicatedRel.executeQuery();
            return result;
        } catch (Exception e) {
            LOG.error("Error to get duplicated items Rel table" + e.getMessage(), e);
            throw new IDMRepairException(e.getMessage());

        }
    }

    public ResultSet findDuplicatedRelName(JdbcConnection connection) throws IDMRepairException{
        try(PreparedStatement findDuplicatedRelName_ = connection.prepareStatement(FIND_DUPLICATED_JBID_ATTR)
        ) {
            findDuplicatedRelName = findDuplicatedRelName_;
            ResultSet result = findDuplicatedRelName.executeQuery();
            return result;
        } catch (Exception e) {
            LOG.error("Error to get duplicated items on Rel Name table" + e.getMessage(), e);
            throw new IDMRepairException(e.getMessage());

        }
    }


}
