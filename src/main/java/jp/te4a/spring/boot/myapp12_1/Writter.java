package jp.te4a.spring.boot.myapp12_1;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = TestValidator.class)
@Target({ElementType.METHOD,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Writter {
    String ok();
    String message() default  "{ok} を入力してください。";
    // Classオブジェクトを得る（戻り値とする）メソッドgroups()
    // デフォルト値は空のクラス
    Class<?>[] groups() default {};
    // Payloadクラスを継承したClassオブジェクトを得る
    // （戻り値とする）メソッドpayload()、デフォルト値は空のクラス
    Class<? extends Payload>[] payload() default{};
}

