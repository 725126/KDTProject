package org.zerock.b01.service.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.b01.domain.user.User;
import org.zerock.b01.domain.user.UserStatus;
import org.zerock.b01.dto.user.FindIdDTO;
import org.zerock.b01.dto.user.UserCreateDTO;
import org.zerock.b01.dto.user.UserResponseDTO;
import org.zerock.b01.dto.user.UserUpdateDTO;

import java.util.List;
import java.util.Optional;

public interface UserService {

    boolean createUser(UserCreateDTO request);

    UserResponseDTO getUserInfoByEmail(String email);

    UserResponseDTO getUserInfoById(Long userId);

    User getUserByEmail(String email);

    boolean isExistedUEmail(String email);

    void sendResetPasswordEmail(String email);

    boolean checkResetTokenValid(String token);

    void resetPassword(String token, String newPassword);

    boolean isBusinessNoAvailable(String businessNo);

    Optional<String> findEmailByFindIdDTO(FindIdDTO findIdDTO);

    boolean checkPasswordMatch(String currentPassword, String userPassword);

    void changePassword(User user, String newPassword);

    void updateUserInfo(Long userId, UserUpdateDTO dto);

    void deactivateUser(Long userId, String reason);

    List<User> findByStatus(UserStatus status);

    Page<User> getPendingUsers(Pageable pageable);

    void activateUser(String uEmail);

    Page<User> getPendingUsersWithFilter(String keyword, String role, Pageable pageable);

    Page<User> findUsersByKeywordAndRole(String keyword, String role, Pageable pageable);

    Page<User> findUsersByFilters(String keyword, String role, String status, Pageable pageable);
}
