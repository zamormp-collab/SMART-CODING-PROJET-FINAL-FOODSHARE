/**
 * Exception levée quand un utilisateur tente d'accéder à une ressource
 * qui ne lui appartient pas → HTTP 403
 */
package ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.exception;

public class RessourceIntrouvableException extends RuntimeException {
    public RessourceIntrouvableException(String message) {
        super(message);
    }
}
