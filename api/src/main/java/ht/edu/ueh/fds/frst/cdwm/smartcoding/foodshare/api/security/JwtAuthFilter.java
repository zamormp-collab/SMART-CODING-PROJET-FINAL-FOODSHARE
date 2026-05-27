/**
 * Implémentation d'un filtre d'authentification JWT qui intercepte chaque requête HTTP pour verifier le JWT de l'utilisateur,
 * valider le token et charger l'utilisateur dans le contexte Spring Security.
 */

package ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.security;

import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.repository.UtilisateurRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component // Annotation pour indiquer que cette classe est un composant géré automatiquement par Spring
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UtilisateurRepository utilisateurRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Lire l'en-tête d'autorisation
        String authHeader = request.getHeader("Authorization");

        // 2. Si pas de token → laisser passer (les routes publiques sont gérées par SecurityConfig)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extraire le token (enlever "Bearer ")
        String token = authHeader.substring(7);

        // 4. Valider le token
        if (!jwtService.isTokenValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 5. Extraire l'email et charger l'utilisateur
        String email = jwtService.extractEmail(token);

        // 6. Si utilisateur pas encore authentifié dans le contexte Spring
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            utilisateurRepository.findByEmail(email).ifPresent(utilisateur -> {

                // 7. Créer l'objet d'authentification Spring Security
                var authToken = new UsernamePasswordAuthenticationToken(
                        utilisateur,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + utilisateur.getRole().name()))
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 8. Enregistrer dans le contexte de sécurité
                SecurityContextHolder.getContext().setAuthentication(authToken);
            });
        }

        filterChain.doFilter(request, response);
    }
}