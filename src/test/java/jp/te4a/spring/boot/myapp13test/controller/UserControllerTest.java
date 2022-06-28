package jp.te4a.spring.boot.myapp13test.controller;

//未完成


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
import jp.te4a.spring.boot.myapp13.form.UserForm;

@ContextConfiguration(classes = BookApplication.class)

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)

@ExtendWith(SpringExtension.class)

@AutoConfigureMockMvc

@Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

@TestInstance(TestInstance.Lifecycle.PER_CLASS)

@WithUserDetails(value="testuser", userDetailsServiceBeanName="loginUserDetailsService")
public class UserControllerTest {
	
    
    public static final Operation INSERT_USER_DATA1 = Operations.insertInto(
            "users").columns("username", "password").values("testuser1", "password1").build();
    public static final Operation INSERT_USER_DATA2 = Operations.insertInto(
            "users").columns("username", "password").values("testuser2", "password2").build();

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
        //Thymeleafのための対応(htmlファイルの場所を設定)
        viewResolver.setPrefix("/templates");
        viewResolver.setSuffix(".html");
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void ユーザ追加ページ() throws Exception {

        Destination dest = new DataSourceDestination(dataSource);
        Operation ops = Operations.sequenceOf(INSERT_USER_DATA1, INSERT_USER_DATA2);
        DbSetup dbSetup = new DbSetup(dest, ops);
        dbSetup.launch();
        
        UserForm user1 = new UserForm();
        user1.setUsername("testuser1");
        user1.setPassword("password1");

        UserForm user2 = new UserForm();
        user2.setUsername("testuser1");
        user2.setPassword("password1");
        
        //URL+HTTPメソッド
        MvcResult result = mockMvc.perform(get("/users"))
        		//HTTPステータス200番台成功
                .andExpect(status().is2xxSuccessful())
                //View(遷移先URL)を指定
                .andExpect(view().name("users/add"))
                .andReturn();

        try {
            List<UserForm> list = (List<UserForm>) result
                        .getModelAndView().getModel().get("users/add");
    
            assertThat(list).contains(user1, user2);
        } catch (NullPointerException e) {
            throw new Exception(e);
        }
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void ユーザ追加_入力テスト() throws Exception{
    	
    	
    	UserForm user = new UserForm();
    	user.setUsername("testuser");
    	user.setPassword("password");

        MvcResult result = mockMvc.perform(post("/users/create")
        		.param("username", "testuser")
        		.flashAttr("form", user)
        		.sessionAttr("form", user)
        		.with(SecurityMockMvcRequestPostProcessors.csrf()))//CSRFトークンをセット、これがないと動かない

               .andExpect(status().is2xxSuccessful())
               .andExpect(view().name("users/add"))
               .andReturn();
    }
    
}



