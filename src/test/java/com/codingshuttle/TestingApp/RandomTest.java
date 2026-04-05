package com.codingshuttle.TestingApp;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;


class RandomTest{
    @Test
void testContainer() {
    PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:15");
    container.start();
}
}
