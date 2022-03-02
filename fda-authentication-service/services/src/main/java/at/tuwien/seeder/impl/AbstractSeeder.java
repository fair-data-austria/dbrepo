package at.tuwien.seeder.impl;

import at.tuwien.entities.user.RoleType;
import at.tuwien.entities.user.User;

import java.util.List;

public abstract class AbstractSeeder {

    public final static Long USER_1_ID = 1L;
    public final static String USER_1_USERNAME = "system";
    public final static String USER_1_FIRSTNAME = "System";
    public final static String USER_1_LASTNAME = "Default";
    public final static String USER_1_EMAIL = "system.default@example.com";
    public final static String USER_1_PASSWORD = "system";
    public final static List<RoleType> USER_1_ROLES = List.of(RoleType.ROLE_RESEARCHER);

    public final static User USER_1 = User.builder()
            .id(USER_1_ID)
            .username(USER_1_USERNAME)
            .firstname(USER_1_FIRSTNAME)
            .lastname(USER_1_LASTNAME)
            .email(USER_1_EMAIL)
            .roles(USER_1_ROLES)
            .build();

    public final static Long USER_2_ID = 2L;
    public final static String USER_2_USERNAME = "researcher";
    public final static String USER_2_TITLES_BEFORE = "Dr.";
    public final static String USER_2_TITLES_AFTER = "BSc";
    public final static String USER_2_FIRSTNAME = "Max";
    public final static String USER_2_LASTNAME = "Mustermann";
    public final static String USER_2_EMAIL = "max.mustermann@example.com";
    public final static String USER_2_PASSWORD = "researcher";
    public final static List<RoleType> USER_2_ROLES = List.of(RoleType.ROLE_RESEARCHER);

    public final static User USER_2 = User.builder()
            .id(USER_2_ID)
            .username(USER_2_USERNAME)
            .titlesBefore(USER_2_TITLES_BEFORE)
            .firstname(USER_2_FIRSTNAME)
            .lastname(USER_2_LASTNAME)
            .titlesBefore(USER_2_TITLES_AFTER)
            .email(USER_2_EMAIL)
            .roles(USER_2_ROLES)
            .build();

    public final static Long USER_3_ID = 3L;
    public final static String USER_3_USERNAME = "developer";
    public final static String USER_3_FIRSTNAME = "Maria";
    public final static String USER_3_LASTNAME = "Mustermann";
    public final static String USER_3_EMAIL = "maria.mustermann@example.com";
    public final static String USER_3_PASSWORD = "developer";
    public final static List<RoleType> USER_3_ROLES = List.of(RoleType.ROLE_DEVELOPER);

    public final static User USER_3 = User.builder()
            .id(USER_3_ID)
            .username(USER_3_USERNAME)
            .firstname(USER_3_FIRSTNAME)
            .lastname(USER_3_LASTNAME)
            .email(USER_3_EMAIL)
            .roles(USER_3_ROLES)
            .build();

    public final static Long USER_4_ID = 4L;
    public final static String USER_4_USERNAME = "datasteward";
    public final static String USER_4_FIRSTNAME = "Eva";
    public final static String USER_4_LASTNAME = "Mustermann";
    public final static String USER_4_EMAIL = "eva.mustermann@example.com";
    public final static String USER_4_PASSWORD = "datasteward";
    public final static List<RoleType> USER_4_ROLES = List.of(RoleType.ROLE_DATA_STEWARD);

    public final static User USER_4 = User.builder()
            .id(USER_4_ID)
            .username(USER_4_USERNAME)
            .firstname(USER_4_FIRSTNAME)
            .lastname(USER_4_LASTNAME)
            .email(USER_4_EMAIL)
            .roles(USER_4_ROLES)
            .build();

}
