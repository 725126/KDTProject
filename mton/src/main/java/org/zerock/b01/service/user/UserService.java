package org.zerock.b01.service.user;

import org.zerock.b01.domain.user.User;
import org.zerock.b01.dto.user.UserCreateDTO;
import org.zerock.b01.dto.user.UserResponseDTO;

import java.util.Optional;

public interface UserService {

    boolean createUser(UserCreateDTO request);

    UserResponseDTO getUserInfoByEmail(String email);

    boolean isExistedUEmail(String email);

    void sendResetPasswordEmail(String email);

    boolean checkResetTokenValid(String token);

    void resetPassword(String token, String newPassword);

    boolean isBusinessNoAvailable(String businessNo);

    Optional<String> findEmailByNameAndPhone(String name, String phone);
}
