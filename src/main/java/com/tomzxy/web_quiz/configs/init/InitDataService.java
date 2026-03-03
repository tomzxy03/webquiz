package com.tomzxy.web_quiz.configs.init;


import com.tomzxy.web_quiz.mapstructs.PermissionMapper;
import com.tomzxy.web_quiz.models.Permission;
import com.tomzxy.web_quiz.repositories.PermissionRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.AbstractMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class InitDataService implements CommandLineRunner {
    private final PermissionMapper permissionMapper;
    private final PermissionRepo permissionRepo;

    private final Map<String,String> ACTIVES = Map.ofEntries(
            new AbstractMap.SimpleEntry<String,String>("VIEW" , "xem dữ liệu"),
            new AbstractMap.SimpleEntry<String,String>("CREATE" , "thêm dữ liệu"),
            new AbstractMap.SimpleEntry<String,String>("UPDATE" , "sửa dữ liệu"),
            new AbstractMap.SimpleEntry<String,String>("DELETE" , "xóa dữ liệu")
    );

    @Override
    public void run(String... args) throws Exception {
        for(Map.Entry<String,String> item: ACTIVES.entrySet()){
            Permission permission = new Permission(item.getKey(),item.getValue());
            createPermission(permission);
        }
    }
    public void createPermission(Permission permission){
        if(!permissionRepo.existsById(permission.getPermissionName())){
            permissionRepo.save(permission);
        }
    }

}
