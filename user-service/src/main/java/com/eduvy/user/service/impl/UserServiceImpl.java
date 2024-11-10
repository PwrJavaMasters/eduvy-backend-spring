import com.eduvy.user.UserInfoDetails;
import com.eduvy.user.dto.user.details.UserDetailsCheckResponse;
import com.eduvy.user.model.UserDetails;
import com.eduvy.user.repository.UserDetailsRepository;
import com.eduvy.user.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import com.eduvy.user.dto.user.details.FillUserDetailsRequest;
import com.eduvy.user.dto.user.details.UserDetailsCheckResponse;
import com.eduvy.user.dto.user.details.UserDetailsResponse;
import com.eduvy.user.model.UserDetails;
import org.springframework.http.ResponseEntity;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDetailsRepository userDetailsRepository;



    public ResponseEntity<UserDetailsCheckResponse> userDetailsFilled() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserInfoDetails userDetails = (UserInfoDetails) authentication.getPrincipal();

        String email = userDetails.getEmail();


        if (email == null) {
            return ResponseEntity.status(422).build();
        }

        UserDetails userData = userDetailsRepository.findByEmail(email);
        if (userDetails == null) {
            return ResponseEntity.ok(new UserDetailsCheckResponse(false));
        }

        boolean userDetailsFilled = userData.getEmail() != null &&
                userData.getFirstName() != null &&
                userData.getLastName() != null &&
                userData.getDateOfBirth() != null &&
                userData.getIsTeacher() != null &&
                userData.getIsStudent() != null &&
                userData.getIsNewsletter() != null;

        return ResponseEntity.ok(new UserDetailsCheckResponse(userDetailsFilled));
    }

    public ResponseEntity<UserDetailsResponse> getUserDetails() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserInfoDetails userDetails = (UserInfoDetails) authentication.getPrincipal();

        String email = userDetails.getEmail();

        if (email == null) {
            return ResponseEntity.status(422).build();
        }

        System.out.println("Looking for user details for " + email);
        UserDetails userData = userDetailsRepository.findByEmail(email);
        if (userData == null) return ResponseEntity.status(404).build();

        UserDetailsResponse userDetailsResponse = new UserDetailsResponse(
                userData.getEmail(),
                userData.getFirstName(),
                userData.getLastName(),
                userData.getDateOfBirth(),
                userData.getIsAdmin(),
                userData.getIsTeacher(),
                userData.getIsStudent(),
                userData.getIsNewsletter()
        );

        return ResponseEntity.ok().body(userDetailsResponse);
    }

    @Transactional
    public ResponseEntity<Void> fillUserDetails(FillUserDetailsRequest fillUserDetailsRequest) {
        if (fillUserDetailsRequest == null || fillUserDetailsRequest.email == null) {
            return ResponseEntity.status(422).build();
        }

        UserDetails userData = userDetailsRepository.findByEmail(fillUserDetailsRequest.email);
        if (userData == null) {
            userData = new UserDetails();
            userData.setEmail(fillUserDetailsRequest.email);
        }

        if (fillUserDetailsRequest.firstName == null ||
                fillUserDetailsRequest.lastName == null ||
                fillUserDetailsRequest.dateOfBirth == null ||
                fillUserDetailsRequest.isAdmin == null ||
                fillUserDetailsRequest.isTeacher == null ||
                fillUserDetailsRequest.isStudent == null ||
                fillUserDetailsRequest.isNewsletter == null ||
                fillUserDetailsRequest.isDeleted == null) {
            return ResponseEntity.status(422).build();
        }

        userData.setUserDetails(fillUserDetailsRequest);
        userDetailsRepository.save(userData);

        return ResponseEntity.ok().build();
    }
}
