package kr.soulware.teen_mydata.entity;

public enum Role {
    GUEST(0),
    USER(1),
    ADMIN(2),
    MASTER(3);
    
    private final int level;

    Role(int level) {
        this.level = level;
    }

    public boolean hasPermission(Role required) {
        return this.level >= required.level;
    }
}