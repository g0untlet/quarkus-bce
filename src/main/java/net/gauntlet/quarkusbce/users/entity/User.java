//==============================================================================

// Copyright (c) 2026 net.gauntlet. All rights reserved.

//==============================================================================

package net.gauntlet.quarkusbce.users.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String name;

    public String email;

    public User() {
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
