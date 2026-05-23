/**
 * Classe d'exception d'EMAIL_DEJA_UTILISE
 */

package ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.exception;

public class EmailDejaUtiliseException
        extends RuntimeException {

    public EmailDejaUtiliseException(String message) {
        super(message);
    }
}