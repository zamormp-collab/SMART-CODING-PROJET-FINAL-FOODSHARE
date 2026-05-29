/**
 * Exception levée quand une ressource est introuvable en base → HTTP 404
 */
package ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.exception;

public class StockEpuiseException extends RuntimeException {
    public StockEpuiseException(String message) {
        super(message);
    }
}
