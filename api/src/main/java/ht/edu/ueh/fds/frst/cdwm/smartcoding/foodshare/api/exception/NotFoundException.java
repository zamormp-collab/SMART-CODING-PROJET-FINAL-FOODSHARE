/**
 * Exception levée quand une ressource est introuvable en base → HTTP 404
 */
package ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
