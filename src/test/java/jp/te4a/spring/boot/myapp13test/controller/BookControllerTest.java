package jp.te4a.spring.boot.myapp13test.controller;



import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import javax.activation.DataSource;
import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.destination.Destination;
import com.ninja_squad.dbsetup.operation.Operation;


import jp.te4a.spring.boot.myapp13.BookApplication;
import jp.te4a.spring.boot.myapp13.form.BookForm;

@ContextConfiguration(classes = BookApplication.class)

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)

@ExtendWith(SpringExtension.class)

@AutoConfigureMockMvc

@Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

@TestInstance(TestInstance.Lifecycle.PER_CLASS)

@WithUserDetails(value="testuser", userDetailsServiceBeanName="loginUserDetailsService")
public class BookControllerTest {
	
    
    public static final Operation INSERT_BOOK_DATA1 = Operations.insertInto(
            "books").columns("id", "title", "writter", "publisher", "price").values(1, "タイトル１", "著者１", "出版社１", 100).build();
    public static final Operation INSERT_BOOK_DATA2 = Operations.insertInto(
            "books").columns("id", "title", "writter", "publisher", "price").values(2, "タイトル２", "著者２", "出版社２", 200).build();

	@Autowired
    MockMvc mockMvc;

    @Autowired
    WebApplicationContext wac;
    
    @Autowired
    private javax.sql.DataSource dataSource;
    
    
    @BeforeAll
    public void テスト前処理() {

        // Thymeleafを使用していることがテスト時に認識されない様子
        // 循環ビューが発生しないことを明示するためにViewResolverを使用
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates");
        viewResolver.setSuffix(".html");
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }
   
    @SuppressWarnings("unchecked")
    @Test
    public void 書籍追加一覧ページ表示_書籍あり() throws Exception {

        Destination dest = new DataSourceDestination(dataSource);
        Operation ops = Operations.sequenceOf(INSERT_BOOK_DATA1, INSERT_BOOK_DATA2);
        DbSetup dbSetup = new DbSetup(dest, ops);
        dbSetup.launch();
        
        BookForm form1 = new BookForm();
        form1.setId(1);
        form1.setTitle("タイトル１");
        form1.setWritter("著者１");
        form1.setPublisher("出版社１");
        form1.setPrice(100);

        BookForm form2 = new BookForm();
        form2.setId(2);
        form2.setTitle("タイトル２");
        form2.setWritter("著者２");
        form2.setPublisher("出版社２");
        form2.setPrice(200);
        
        MvcResult result = mockMvc.perform(get("/books"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("books/list"))
                .andReturn();

        try {
            List<BookForm> list = (List<BookForm>) result
                        .getModelAndView().getModel().get("books");
    
            assertThat(list).contains(form1, form2);
        } catch (NullPointerException e) {
            throw new Exception(e);
        }
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void 画面パーツ_入力テスト() throws Exception{
    	
    	
    	BookForm bookForm = new BookForm();
    	bookForm.setId(1);
        bookForm.setTitle("タイトル１");
        bookForm.setWritter("著者１");
        bookForm.setPublisher("出版社１");
        bookForm.setPrice(100);
        

        MvcResult result = mockMvc.perform(post("/books/create")
        		.param("title", "タイトル1")
        		.flashAttr("form", bookForm)
        		.sessionAttr("form", bookForm)
        		.with(SecurityMockMvcRequestPostProcessors.csrf()))//CSRFトークンをセット、これがないと動かない

               .andExpect(status().is2xxSuccessful())
               .andExpect(view().name("books/list"))
               .andReturn();
    }
    
}



