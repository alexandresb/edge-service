package com.polarbookshop.edgeservice.user;

import java.util.List;

//représentation du principal
public record User(
       String username,
       String firstName,
       String lastName,
       List<String> roles
) {}
