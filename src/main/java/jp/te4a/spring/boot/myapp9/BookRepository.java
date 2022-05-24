package jp.te4a.spring.boot.myapp9;



import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<BookBean, Integer>{
	
	
}

