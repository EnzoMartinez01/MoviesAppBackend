package com.moviesapp.movies.Services.Common;

import com.moviesapp.movies.Models.Authentication.CustomUserDetails;
import com.moviesapp.movies.Models.Authentication.Users;
import com.moviesapp.movies.Repositories.Authentication.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommonService {
    private static final Logger logger = LoggerFactory.getLogger(CommonService.class);

    private final UsersRepository usersRepository;

    public CommonService (UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Transactional(readOnly = true)
    public Users authenticationCurrent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        Users currentUser = customUserDetails.getUsers();

        return usersRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
