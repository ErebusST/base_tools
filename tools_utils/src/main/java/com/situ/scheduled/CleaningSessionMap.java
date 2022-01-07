/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.scheduled;

import com.situ.tools.SessionMap;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * The type Cleaning session map.
 *
 * @author 司徒彬
 * @date 2020 /7/24 16:16
 */
@Component
public class CleaningSessionMap {
    /**
     * Clean .
     *
     * @author ErebusST
     * @since 2022 -01-07 15:36:09
     */
    @Scheduled(cron = "* * * * * ?")
    public void clean() {
        SessionMap.clearOverTimeSession();
    }
}
