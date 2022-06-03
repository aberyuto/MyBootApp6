package jp.te4a.spring.boot.myapp13.repository;



import org.springframework.stereotype.Repository;

import jp.te4a.spring.boot.myapp13.bean.BookBean;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<BookBean, Integer>{
	
	
}

