package org.zerock.b01.service.user;

import org.zerock.b01.domain.user.User;
import org.zerock.b01.dto.user.FindIdDTO;
import org.zerock.b01.dto.user.UserCreateDTO;
import org.zerock.b01.dto.user.UserResponseDTO;
import org.zerock.b01.dto.user.UserUpdateDTO;

import java.util.Optional;

public interface UserService {

    boolean createUser(UserCreateDTO request);

    UserResponseDTO getUserInfoByEmail(String email);

    User getUserByEmail(String email);

    boolean isExistedUEmail(String email);

    void sendResetPasswordEmail(String email);

    boolean checkResetTokenValid(String token);

    void resetPassword(String token, String newPassword);

    boolean isBusinessNoAvailable(String businessNo);

    Optional<String> findEmailByFindIdDTO(FindIdDTO findIdDTO);

    boolean checkPasswordMatch(String currentPassword, String userPassword);

    void changePassword(User user, String newPassword);

    void updateUserInfo(String uEmail, UserUpdateDTO dto);
}
