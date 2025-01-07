package com.switching.study_matching_site.dto.member;

import com.switching.study_matching_site.domain.EnterStatus;
import com.switching.study_matching_site.domain.FriendRequest;
import com.switching.study_matching_site.domain.Member;
import com.switching.study_matching_site.domain.Profile;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO는 불변객체로 만들면 전달 과정 중에서의 데이터 불변성 보장
 * 앤티티 클래스는 절대로 요청이나 응답값을 전달하는 클래스로 사용하면 안됨
 * 엔티티 클래스는 데이터베이스와 매핑되어 있는 핵심 클래스이기 때문이다.
 * 엔티티 클래스 기준으로 테이블이 생성되고 스키마가 변경된다.
 * VIEW는 비지니스 요구사항에서 자주 변경되는 부분이다.
 * 만약 엔티티 클래스를 요청값이나 응답값을 전달하는 클래스로 사용한다면 VIEW가 변경될 떄마다 엔티티 클래스를 매번 그에 맞춰 변경해야 한다.
 * 엔티티 클래스를 변경하면 관련되어 있는 얽혀있는 무수한 클래스들에게 영향을 끼치게 된다.
 * 따라서 요청이나 응답값을 전달하는 클래스로는 뷰의 변경의 따라 다른 클래스들에게 영향을 끼치지 않고 변경할 수 있는 DTO를 사욜해야 한다.
 * 또한, 응답값으로 여러 테이블들을 조인한 결과값을 조회할 경우가 빈번하기 떄문에 엔티티 클래스로만은 응답값을 표현하기 어려운 경우가 많다.
 * 그래서 엔티티 클래스와 DTO를 분리해주자!
 **/

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberCreateDto {

    private Long id;

    /**
     * @Pattern
     * 문자열 길이가 5자 이상 10자 이하
     * 최소한 하나의 영문자 (대소문자 모두 가능) 포함
     * 특수문자나 공백은 포함되지 않음
     * 숫자는 필수가 아님
     */
    //@NotNull
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?!.*[^a-zA-Z0-9]).{5,10}",
        message = "아이디는 5글자 이상 10글자 이하여야 합니다.")
    private String loginId;

    /**
     * @Pattern
     * 길이는 8자 이상 30자 이하.
     * 영문자, 숫자, 특수문자 각각 최소 1개 이상 포함.
     * 허용된 특수문자는 @$!%*#?&로 제한됩니다
     */
    //@NotNull
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,30}$",
            message = "* 비밀번호 길이는 8자 이상 30자 이하.\n" +
                "     * 영문자, 숫자, 특수문자 각각 최소 1개 이상 포함.\n" +
                "     * (허용된 특수문자: @$!%*#?&)")
    private String password;

    //@NotNull
    @Length(min = 3, max = 7,
    message = "3글자 이상 7글자 미만이여야 합니다.")
    private String username;

    //@NotNull
    @DateTimeFormat(pattern = "yyyy-mm-dd")
    private LocalDate birthDate;

    /**
     * @Pattern
     * 5자에서 20자 사이의 문자+특수문자 형식의 사용자명, 뒤에 도메인 형식
     */
    //@NotNull
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{5,20}@[A-Za-z]{2,}\\.[A-Za-z]{2,}$",
            message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    //@NotNull
    @Length(max = 13)
    @Pattern(regexp = "^01([0|1|6|7|8|9])-?([0-9]{3,4})-?([0-9]{4})$",
            message = "올바른 휴대폰 번호 형식이 아닙니다. ex) 010-1111-2222")
    private String phoneNumber;

    // @NotNull
    private EnterStatus enterStatus = EnterStatus.OUT;



    /**
     * DTO -> ENTITY 변환 메소드
     */
    public Member toEntity() {
        Member entity = new Member();
        entity.setUsername(this.username);
        entity.setLoginId(this.loginId);
        entity.setPassword((this.password));
        entity.setBirthDate(this.birthDate);
        entity.setEmail(this.email);
        entity.setPhoneNumber(this.phoneNumber);
        entity.setEnterStatus(this.enterStatus);
        return entity;
    }

    /**
     * ENTITY -> DTO 변환 메소드
     */
    public static MemberCreateDto fromEntity(Member entity) {

        return MemberCreateDto.builder()
                .id(entity.getId())
                .phoneNumber(entity.getPhoneNumber())
                .email(entity.getEmail())
                .birthDate(entity.getBirthDate())
                .loginId(entity.getLoginId())
                .password(entity.getPassword())
                .enterStatus(entity.getEnterStatus())
                .username(entity.getUsername())
                .build();

    }

    @Override
    public String toString() {
        return "MemberDto{" +
                "id=" + id +
                ", loginId='" + loginId + '\'' +
                ", password='" + password + '\'' +
                ", username='" + username + '\'' +
                ", birthDate=" + birthDate +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", enterStatus=" + enterStatus +
                '}';
    }
}
