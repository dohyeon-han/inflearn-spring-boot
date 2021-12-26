package com.inflearn.order;

import com.inflearn.order.config.AppConfig;
import com.inflearn.order.member.Grade;
import com.inflearn.order.member.Member;
import com.inflearn.order.member.MemberService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MemberApp {

    public static void main(String[] args){
//        AppConfig appConfig = new AppConfig();
//        MemberService memberService = appConfig.memberService();
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        MemberService memberService = applicationContext.getBean("memberService", MemberService.class);

        Member member = new Member(1L,"spring", Grade.VIP);
        memberService.join(member);

        Member findMember = memberService.findMember(1L);

        System.out.println("new member = " + member.getName()+", grade = "+member.getGrade());
        System.out.println("find member = " + findMember.getName()+", grade = "+findMember.getGrade());
    }
}
