//==============================================================================

// Copyright (c) 2026 net.gauntlet. All rights reserved.

//==============================================================================

package net.gauntlet.quarkusbce.users.control;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.transaction.Transactional;
import net.gauntlet.quarkusbce.Control;
import net.gauntlet.quarkusbce.users.entity.User;

import java.util.Optional;

@Control
@Transactional
public class UsersControl implements PanacheRepositoryBase<User, Long> {

    private static final System.Logger LOG = System.getLogger(UsersControl.class.getName());

    public User create(String name, String email) {
        User user = new User(name, email);
        persist(user);
        LOG.log(System.Logger.Level.INFO, "User created with id: {0}", user.id);
        return user;
    }

    public Optional<User> getById(Long id) {
        return findByIdOptional(id);
    }

    public Optional<User> update(Long id, String name, String email) {
        User user = findByIdOptional(id).orElse(null);
        if (user == null) {
            return Optional.empty();
        }
        if (name != null) {
            user.name = name;
        }
        if (email != null) {
            user.email = email;
        }
        persist(user);
        return Optional.of(user);
    }

    public boolean delete(Long id) {
        boolean deleted = deleteById(id);
        if (deleted) {
            LOG.log(System.Logger.Level.INFO, "User deleted with id: {0}", id);
        }
        return deleted;
    }
}
