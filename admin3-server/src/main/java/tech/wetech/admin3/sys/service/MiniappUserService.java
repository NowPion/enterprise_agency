package tech.wetech.admin3.sys.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.wetech.admin3.common.BusinessException;
import tech.wetech.admin3.common.CommonResultStatus;
import tech.wetech.admin3.sys.model.MiniappUser;
import tech.wetech.admin3.sys.repository.MiniappUserRepository;

import java.time.LocalDateTime;

@Service
public class MiniappUserService {

    private final MiniappUserRepository miniappUserRepository;

    public MiniappUserService(MiniappUserRepository miniappUserRepository) {
        this.miniappUserRepository = miniappUserRepository;
    }

    public Page<MiniappUser> search(String keyword, Integer status, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return miniappUserRepository.search(keyword, status, pageRequest);
    }

    public MiniappUser findById(Long id) {
        return miniappUserRepository.findById(id)
            .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST));
    }

    @Transactional
    public MiniappUser update(Long id, String nickname, String phone, Integer status) {
        MiniappUser user = findById(id);
        if (nickname != null) user.setNickname(nickname);
        if (phone != null) user.setPhone(phone);
        if (status != null) user.setStatus(status);
        return miniappUserRepository.save(user);
    }

    @Transactional
    public void updateStatus(Long id, Integer status) {
        MiniappUser user = findById(id);
        user.setStatus(status);
        miniappUserRepository.save(user);
    }

    @Transactional
    public void blockUser(Long id, String reason) {
        MiniappUser user = findById(id);
        user.setBlocked(1);
        user.setBlockReason(reason);
        user.setBlockTime(LocalDateTime.now());
        miniappUserRepository.save(user);
    }

    @Transactional
    public void unblockUser(Long id) {
        MiniappUser user = findById(id);
        user.setBlocked(0);
        user.setBlockReason(null);
        user.setBlockTime(null);
        miniappUserRepository.save(user);
    }

    public boolean isPhoneBlocked(String phone) {
        if (phone == null || phone.isBlank()) return false;
        return miniappUserRepository.existsByPhoneAndBlocked(phone, 1);
    }

    public MiniappUser findByOpenid(String openid) {
        return miniappUserRepository.findByOpenid(openid)
            .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST));
    }

    @Transactional
    public MiniappUser createOrUpdate(String openid, String unionid, String nickname, String avatar) {
        MiniappUser user = miniappUserRepository.findByOpenid(openid).orElse(null);
        if (user == null) {
            user = new MiniappUser();
            user.setOpenid(openid);
            user.setStatus(1);
            user.setCreateTime(LocalDateTime.now());
        }
        if (unionid != null) user.setUnionid(unionid);
        if (nickname != null) user.setNickname(nickname);
        if (avatar != null) user.setAvatar(avatar);
        user.setLastLoginTime(LocalDateTime.now());
        return miniappUserRepository.save(user);
    }
}
