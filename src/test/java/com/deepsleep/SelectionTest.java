package com.deepsleep;

import com.deepsleep.data.enums.SelectionStatus;
import com.deepsleep.service.SelectionService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
class SelectionTest {

    @Resource
    private SelectionService selectionService;

    @Test
    void contextLoads() {
        System.out.println(selectionService.dropCourse(1111L,12L));
        System.out.println(selectionService.pickCourse(1111L,1111L));
        System.out.println(selectionService.dropCourse(1111L,1111L));
        System.out.println(selectionService.endCourse(1111L,1111L,0.0));
        System.out.println(selectionService.pickCourse(1111L,1112L));
        System.out.println(selectionService.pickCourse(1111L,1113L));
        System.out.println(selectionService.endCourse(1111L,1113L,100.0));
        System.out.println(selectionService.showList(1111L,1,10));
        System.out.println(selectionService.showList(1111L,1,10,
                Arrays.asList(SelectionStatus.PICKED, SelectionStatus.OVER)));
        System.out.println(selectionService.showList(0L,1,10));
    }

}
