package group.project.myserver.api.userinfo;

import group.project.myserver.config.JwtService;
import group.project.myserver.user.User;
import group.project.myserver.user.UserExtra;
import group.project.myserver.user.UserExtraRepository;
import group.project.myserver.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserExtraService {
    private final UserRepository userRepository;
    private final UserExtraRepository userExtraRepository;
    private final JwtService jwtService;

    public UserExtraResponseCode createOrUpdateUserExtra(String authHeader, UserExtraRequest request) {
        String username = validateAndGetUsername(authHeader);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        UserExtra userExtra = userExtraRepository.findExtraByUserId(user.getId())
                .orElse(new UserExtra());

        userExtra.setUser(user);
        userExtra.setAge(request.getAge());
        userExtra.setHeight(request.getHeight());
        userExtra.setWeight(request.getWeight());
        userExtra.setCountry(request.getCountry());
        userExtra.setCity(request.getCity());
        userExtra.setStreet(request.getStreet());
        userExtra.setPhone(request.getPhone());

        userExtraRepository.save(userExtra);

        return UserExtraResponseCode.builder().build();
    }

    public UserExtraResponse replyUserExtra(String authHeader) {
        String username = validateAndGetUsername(authHeader);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        UserExtra userExtra = userExtraRepository.findExtraByUserId(user.getId())
                .orElse(new UserExtra());

        return UserExtraResponse.builder()
                .age(userExtra.getAge())
                .height(userExtra.getHeight())
                .weight(userExtra.getWeight())
                .country(userExtra.getCountry())
                .city(userExtra.getCity())
                .street(userExtra.getStreet())
                .phone(userExtra.getPhone())
                .build();
    }

    public UserInfoResponse userInfo(String authHeader) {
        String username = validateAndGetUsername(authHeader);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));

        return UserInfoResponse.builder()
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .build();
    }

    private String validateAndGetUsername(String authHeader) {
        if (authHeader == null || ! authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Malformed Authorization header");
        }
        return jwtService.extractUsername(authHeader.substring(7));
    }
}
