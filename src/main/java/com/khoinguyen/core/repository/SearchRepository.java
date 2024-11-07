package com.khoinguyen.core.repository;

import com.khoinguyen.core.dto.response.PageResponse;
import com.khoinguyen.core.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SearchRepository {
      @PersistenceContext
      private EntityManager entityManager;

    public PageResponse<?> getAllUsersWithSortByMultipleColumnAndSearch(int pageNo, int pageSize, String search, String sortBy) {

        StringBuilder sqlQuery = new StringBuilder("select new com.khoinguyen.core.dto.response.UserDetailResponse(u.id, u.firstName, u.lastName) from User u where 1=1 ");

        if(StringUtils.hasText(search)){
            sqlQuery.append(" or lower(u.firstName) like lower(:firstName) ");
            sqlQuery.append(" or lower(u.lastName) like lower(:lastName) ");
            sqlQuery.append(" or lower(u.email) like lower(:email) ");
        }

        if(StringUtils.hasText(sortBy)){
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sortBy);
            if(matcher.find()) {
                sqlQuery.append(String.format("order by u.%s %s", matcher.group(1), matcher.group(3)));
            }

        }

        Query selectQuery = entityManager.createQuery(sqlQuery.toString());
        selectQuery.setFirstResult(pageNo);
        selectQuery.setMaxResults(pageSize);

        if(StringUtils.hasText(search)){
            selectQuery.setParameter("firstName", String.format("%%%s%%", search));
            selectQuery.setParameter("lastName", String.format("%%%s%%", search));
            selectQuery.setParameter("email", String.format("%%%s%%", search));
        }

        List<User> users = selectQuery.getResultList();

        //query ra list user

        //query count record
        StringBuilder sqlCountQuery = new StringBuilder("select count(u) from User u where 1=1 ");
        if(StringUtils.hasText(search)){
            sqlCountQuery.append(" or lower(u.firstName) like lower(?1) ");
            sqlCountQuery.append(" or lower(u.lastName) like lower(?2) ");
            sqlCountQuery.append(" or lower(u.email) like lower(?3) ");
        }

        Query selectCountQuery = entityManager.createQuery(sqlCountQuery.toString());
        if(StringUtils.hasText(search)){
            selectCountQuery.setParameter(1, String.format("%%%s%%", search));
            selectCountQuery.setParameter(2, String.format("%%%s%%", search));
            selectCountQuery.setParameter(3, String.format("%%%s%%", search));
        }

        Long totalElements = (Long) selectCountQuery.getSingleResult();
        long totalPages = (totalElements + pageSize - 1) / pageSize;

        Page<?> page = new PageImpl<User>(users, PageRequest.of(pageNo, pageSize), totalElements);

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(page.getTotalPages())
                .items(page.stream().toList())
                .build();
    }
}
