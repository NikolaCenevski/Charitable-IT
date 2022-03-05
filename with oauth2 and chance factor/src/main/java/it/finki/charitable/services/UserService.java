package it.finki.charitable.services;

import it.finki.charitable.entities.AppUser;
import it.finki.charitable.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public AppUser loadUserByUsername(String s) throws UsernameNotFoundException {
        return userRepository.findByEmail(s).orElse(null);
    }

    public void saveUser(AppUser user) {
        userRepository.save(user);
    }

    public void deleteUser(AppUser user) {
        userRepository.delete(user);
    }
}
