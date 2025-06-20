package com.codewiki.codewiki.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.NaturalId;
import org.hibernate.proxy.HibernateProxy;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "roles")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NaturalId
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(length = 200)
    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "roles_privileges",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "privilege_id")
    )
    @Builder.Default
    @ToString.Exclude
    private Set<Privilege> privileges = new HashSet<>();

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private Set<User> users = new HashSet<>();

    @Column(nullable = false)
    @Builder.Default
    private Boolean isSystem = false;

    // Конструкторы
    public Role(String name) {
        this.name = name;
        this.privileges = new HashSet<>();
        this.users = new HashSet<>();
        this.isSystem = false;
    }

    public Role(String name, String description) {
        this(name);
        this.description = description;
    }

    // Методы для работы с привилегиями
    public void addPrivilege(Privilege privilege) {
        if (privilege != null) {
            this.privileges.add(privilege);
            privilege.getRoles().add(this);
        }
    }

    public void removePrivilege(Privilege privilege) {
        if (privilege != null) {
            this.privileges.remove(privilege);
            privilege.getRoles().remove(this);
        }
    }

    // Методы для работы с пользователями
    public void addUser(User user) {
        if (user != null) {
            this.users.add(user);
            user.getRoles().add(this);
        }
    }

    public void removeUser(User user) {
        if (user != null) {
            this.users.remove(user);
            user.getRoles().remove(this);
        }
    }

    // equals и hashCode
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? 
                ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? 
                ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Role role = (Role) o;
        return getId() != null && Objects.equals(getId(), role.getId());
    }

    @Override
    public final int hashCode() {
        return getClass().hashCode();
    }

    // Методы проверки
    public boolean isAdminRole() {
        return "ROLE_ADMIN".equals(this.name);
    }

    public boolean hasPrivilege(String privilegeName) {
        return this.privileges.stream()
                .anyMatch(p -> p != null && privilegeName.equals(p.getName()));
    }

    // Методы для системных ролей
    public boolean isSystemRole() {
        return Boolean.TRUE.equals(this.isSystem);
    }

    public void markAsSystemRole() {
        this.isSystem = true;
    }

    // Дополнительные геттеры
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Set<Privilege> getPrivileges() {
        return privileges;
    }

    public Set<User> getUsers() {
        return users;
    }

    public Boolean getIsSystem() {
        return isSystem;
    }

    // Дополнительные сеттеры
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrivileges(Set<Privilege> privileges) {
        this.privileges = privileges;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public void setIsSystem(Boolean isSystem) {
        this.isSystem = isSystem;
    }
}