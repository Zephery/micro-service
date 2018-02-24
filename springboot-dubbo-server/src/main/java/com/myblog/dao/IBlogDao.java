package com.myblog.dao;

import com.myblog.model.Blog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Zephery
 * @since 2018/2/23 19:36
 */
public interface IBlogDao extends JpaRepository<Blog, Integer> {
}