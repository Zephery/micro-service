package com.myblog.dao;

import com.myblog.model.Weibo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author Zephery
 * @since 2018/2/25 13:22
 */
public interface IWeiboDao extends JpaRepository<Weibo, Integer> {

    @Query(value = "select * from weibo w WHERE to_days(now()) - TO_DAYS(created_at) = 0 " +
            "ORDER BY created_at ASC", nativeQuery = true)
    List<Weibo> getWeibosToday();
}