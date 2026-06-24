package com.openclassrooms.test.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;

class UserDetailsTest {

    @Test
    void equalsShouldReturnTrueForSameInstance() {
        UserDetailsImpl user = UserDetailsImpl.builder().id(1L).build();
        assertEquals(user, user);
    }

    @Test
    void equalsShouldReturnTrueForSameId() {
        UserDetailsImpl user1 = UserDetailsImpl.builder().id(1L).username("a").build();
        UserDetailsImpl user2 = UserDetailsImpl.builder().id(1L).username("b").build();
        assertEquals(user1, user2); // même id => égaux, même si autres champs diffèrent
    }

    @Test
    void equalsShouldReturnFalseForDifferentId() {
        UserDetailsImpl user1 = UserDetailsImpl.builder().id(1L).build();
        UserDetailsImpl user2 = UserDetailsImpl.builder().id(2L).build();
        assertNotEquals(user1, user2);
    }

    @Test
    void equalsShouldReturnFalseForNull() {
        UserDetailsImpl user = UserDetailsImpl.builder().id(1L).build();
        assertFalse(user.equals(null)); // NOSONAR java:S5785 - equals() itself is the subject under test
    }

    @SuppressWarnings("unlikely-arg-type")
    @Test
    void equalsShouldReturnFalseForDifferentClass() {
        UserDetailsImpl user = UserDetailsImpl.builder().id(1L).build();
        assertFalse(user.equals("not a user")); // NOSONAR java:S5785 - equals() itself is the subject under test
    }

    @Test
    void hashCodeShouldBeEqualForObjectsWithSameId() {
        UserDetailsImpl user1 = UserDetailsImpl.builder().id(1L).username("a").build();
        UserDetailsImpl user2 = UserDetailsImpl.builder().id(1L).username("b").build();
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void hashCodeShouldDifferForObjectsWithDifferentId() {
        UserDetailsImpl user1 = UserDetailsImpl.builder().id(1L).build();
        UserDetailsImpl user2 = UserDetailsImpl.builder().id(2L).build();
        assertNotEquals(user1.hashCode(), user2.hashCode());
    }

}
