/**
 * Exception levée quand une opération est impossible
 * sur une ressource dans son état actuel → HTTP 409
 */
package ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.exception;

public class OperationNonAutoriseeException extends RuntimeException {
    public OperationNonAutoriseeException(String message) {
        super(message);
    }
}
