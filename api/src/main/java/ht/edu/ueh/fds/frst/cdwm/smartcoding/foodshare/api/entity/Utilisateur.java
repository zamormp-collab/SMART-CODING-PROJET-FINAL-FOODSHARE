/**
 * Creation de la classe Utilisateur pour la gestion des utilisateurs.
 * Utilisation de Spring Data JPA (@Entity, @Table, ...) pour transformer l'objet Java 'Utilisateur' en table SQL 'utilisateurs' et vice versa.
 * Utilisation de Lombok pour creer les getters, les setters et les constructeurs par défaut automatiquement
 * et de Hibernate Annotations pour la gestion des timestamps.
 */
package ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.entity;

import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity // Annotation JPA pour indiquer que cette classe est une entité qui doit être persistée en base de données
@Table(name = "utilisateurs") // Annotation JPA pour indiquer le nom de la table dans la base de donnees
@Getter // Annotation Lombok pour créer les getters par défaut automatiquement
@Setter // Annotation Lombok pour créer les setters par défaut automatiquement
@NoArgsConstructor // Annotation Lombok pour créer un constructeur par défaut vide
@AllArgsConstructor // Annotation Lombok pour créer un constructeur avec tous les paramètres
@Builder // Annotation Lombok pour générer automatiquement une API fluide pour construire les objets.
public class Utilisateur {

    @Id // Annotation pour indiquer que c'est la clé primaire
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Annotation pour indiquer que ce champ est autoincrémenté
    private Long id;

    @Column(nullable = false) // Annotation JPA pour indiquer que la colonne est obligatoire
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String motDePasse;

    @Enumerated(EnumType.STRING) // Annotation JPA pour indiquer que la colonne est une enum
    @Column(nullable = false)
    private Role role;

    @Column(length = 500) // Annotation JPA pour indiquer la longueur maximale de la colonne, c'est-à-dire une adresse ne peut contenir plus de 500 caractères
    private String adresse;

    @Column(length = 20)
    private String telephone;

    @CreationTimestamp // Annotation JPA pour indiquer que la colonne est une date de création
    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @UpdateTimestamp // Annotation JPA pour indiquer que la colonne est une date de modification
    @Column(nullable = false)
    private LocalDateTime dateModification;
}