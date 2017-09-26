package org.exoplatform.platform.organization.checker;

/**
 *  IDM repair data exception
 */
public class IDMRepairException extends Exception {

    public IDMRepairException() {
    }

    public IDMRepairException(String message) {
        super(message);
    }

    public IDMRepairException(String message, Throwable cause) {
        super(message, cause);
    }

    public IDMRepairException(Throwable cause) {
        super(cause);
    }
}
