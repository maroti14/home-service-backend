package com.homeservice.security;



import com.homeservice.domain.auth.entity.User;
import com.homeservice.domain.customer.repository.CustomerRepository;
import com.homeservice.domain.worker.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl
        implements UserDetailsService {

    private final CustomerRepository customerRepo;
    private final WorkerRepository workerRepo;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        // try customer first, then worker
        User user = customerRepo.findByEmail(email)
                .map(c -> (User) c)
                .orElseGet(() ->
                    workerRepo.findByEmail(email)
                        .map(w -> (User) w)
                        .orElseThrow(() ->
                            new UsernameNotFoundException(
                                "User not found: " + email)));

        return new UserDetailsImpl(user);
    }
}