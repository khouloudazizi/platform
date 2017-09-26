package org.exoplatform.platform.organization.checker.impl;

import org.exoplatform.platform.organization.checker.IDMDataRepair;
import org.exoplatform.platform.organization.checker.IDMRepairException;

import java.util.Map;

/**
 * Repair jbid_realm utility class
 */
public class JbidRealmRepair extends IDMDataRepair {
    @Override
    protected Map<Integer, Integer> findDuplicatedItems() throws IDMRepairException {
        //TODO
        return null;
    }

    @Override
    protected boolean updateRelatedItems(int id, int maxID) throws IDMRepairException {
        //TODO
        return true;
    }

    @Override
    protected boolean removeDuplicatedItems(int id) throws IDMRepairException {
        //TODO
        return true;
    }
}
