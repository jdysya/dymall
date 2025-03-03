package com.hmall.user.enums;

public enum UserRoles {
    SuperAdmin(1),
    ItemAdmin(2),
    OrderAdmin(3),
    Guest(4);

    private final int id;

    UserRoles(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static UserRoles fromId(int id) {
        for (UserRoles role : UserRoles.values()) {
            if (role.getId() == id) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role ID: " + id);
    }
}
