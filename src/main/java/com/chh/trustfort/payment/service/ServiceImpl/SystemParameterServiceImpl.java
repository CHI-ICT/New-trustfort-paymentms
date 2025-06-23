package com.chh.trustfort.payment.service.ServiceImpl;

import com.chh.trustfort.payment.repository.SystemParameterRepository;
import com.chh.trustfort.payment.service.SystemParameterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SystemParameterServiceImpl implements SystemParameterService {

    private final SystemParameterRepository systemParameterRepository;

    @Override
    public String getValue(String key) {
        return systemParameterRepository.findByKey(key)
                .orElseThrow(() -> new RuntimeException("System parameter not found: " + key))
                .getValue();
    }
}

