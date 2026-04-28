package tech.wetech.admin3.sys.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.wetech.admin3.common.BusinessException;
import tech.wetech.admin3.common.CommonResultStatus;
import tech.wetech.admin3.sys.model.Coupon;
import tech.wetech.admin3.sys.repository.CouponRepository;

@Service
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public Page<Coupon> search(String keyword, Integer status, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        if (status != null) {
            return couponRepository.search(keyword, status, pageRequest);
        }
        return couponRepository.findAll(pageRequest);
    }

    public Coupon getById(Long id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST, "优惠券不存在"));
    }

    @Transactional
    public Coupon create(Coupon coupon) {
        coupon.setReceivedCount(0);
        coupon.setUsedCount(0);
        return couponRepository.save(coupon);
    }

    @Transactional
    public Coupon update(Long id, Coupon coupon) {
        Coupon existing = getById(id);
        existing.setName(coupon.getName());
        existing.setType(coupon.getType());
        existing.setValue(coupon.getValue());
        existing.setMinAmount(coupon.getMinAmount());
        existing.setMaxDiscount(coupon.getMaxDiscount());
        existing.setTotalCount(coupon.getTotalCount());
        existing.setPerLimit(coupon.getPerLimit());
        existing.setValidType(coupon.getValidType());
        existing.setValidDays(coupon.getValidDays());
        existing.setStartTime(coupon.getStartTime());
        existing.setEndTime(coupon.getEndTime());
        existing.setApplicableProducts(coupon.getApplicableProducts());
        existing.setDescription(coupon.getDescription());
        existing.setStatus(coupon.getStatus());
        return couponRepository.save(existing);
    }

    @Transactional
    public void updateStatus(Long id, Integer status) {
        Coupon coupon = getById(id);
        coupon.setStatus(status);
        couponRepository.save(coupon);
    }

    @Transactional
    public void delete(Long id) {
        couponRepository.deleteById(id);
    }
}
