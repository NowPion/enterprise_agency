package tech.wetech.admin3.sys.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.wetech.admin3.common.BusinessException;
import tech.wetech.admin3.common.CommonResultStatus;
import tech.wetech.admin3.common.SecurityUtil;
import tech.wetech.admin3.sys.model.UserCredential;
import tech.wetech.admin3.sys.repository.UserCredentialRepository;

import static tech.wetech.admin3.sys.model.UserCredential.IdentityType.PASSWORD;

/**
 * 密码服务
 */
@Service
public class PasswordService {

    private final UserCredentialRepository userCredentialRepository;

    public PasswordService(UserCredentialRepository userCredentialRepository) {
        this.userCredentialRepository = userCredentialRepository;
    }

    /**
     * 修改密码
     */
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        UserCredential credential = userCredentialRepository.findByUserId(userId, PASSWORD)
            .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST, "用户凭证不存在"));
        
        // 验证旧密码
        if (!credential.doCredentialMatch(oldPassword)) {
            throw new BusinessException(CommonResultStatus.PARAM_ERROR, "原密码不正确");
        }
        
        // 设置新密码（BCrypt 加密）
        credential.setPassword(newPassword);
        userCredentialRepository.save(credential);
    }

    /**
     * 重置密码（管理员操作）
     */
    @Transactional
    public void resetPassword(Long userId, String newPassword) {
        UserCredential credential = userCredentialRepository.findByUserId(userId, PASSWORD)
            .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST, "用户凭证不存在"));
        
        credential.setPassword(newPassword);
        userCredentialRepository.save(credential);
    }

    /**
     * 升级旧密码格式为 BCrypt（登录成功后调用）
     */
    @Transactional
    public void upgradePasswordIfNeeded(UserCredential credential, String rawPassword) {
        if (!SecurityUtil.isBCryptPassword(credential.getCredential())) {
            // 旧格式密码，升级为 BCrypt
            credential.setPassword(rawPassword);
            userCredentialRepository.save(credential);
        }
    }
}
