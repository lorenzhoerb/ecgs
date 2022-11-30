package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

//TODO: replace this class with a correct ApplicationUser JPARepository implementation
@Repository
public class UserRepository {

    private final ApplicationUser user;
    private final ApplicationUser admin;

    @Autowired
    public UserRepository(PasswordEncoder passwordEncoder) {
        //@TODO: replace with new users
        //user = new ApplicationUser("user@email.com", passwordEncoder.encode("password"), false);
        //admin = new ApplicationUser("admin@email.com", passwordEncoder.encode("password"), true);
        user = new ApplicationUser();
        admin = new ApplicationUser();
    }

    public ApplicationUser findUserByEmail(String email) {
        //@TODO: fix for new user
        /*
        if (email.equals(user.getEmail())) {
            return user;
        }
        if (email.equals(admin.getEmail())) {
            return admin;
        }
        */
        return null; // In this case null is returned to fake Repository behavior
    }


}
