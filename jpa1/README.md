# 자바 ORM 표준 JPA 프로그래밍 - 기본편
https://www.inflearn.com/course/ORM-JPA-Basic#curriculum

## JPA 시작
### RDB 사용의 문제점
- DB를 조작할 때마다 매번 비슷한 CRUD SQL을 작성해야 한다.
- 객체와 RDB의 차이
  - 상속
    - RDB는 상속이 없어 테이블 별로 쿼리문을 작성해야한다.
  - 연관관계
    - 객체는 A.getB()로 참조하고, RDB는 외래키를 사용한다.
    - 객체는 단방향으로 참조하지만, RDB는 양방향 참조가 가능하다.
  - 데이터 타입
    - RDB의 데이터 타입으로 객체가 올 수 없다.
  - 데이터 식별 방법
  - 이러한 차이는 맵핑, DB 조작 등에서 불편함을 야기한다.
- 엔티티 신뢰
    ```JAVA
  class MemberSerivce{
        public void process(){
            Member member = memberDAO.find(id);
            member.getTeam();//null
            member.gerOrder().getDelivery();//null
        }
  }
  ```
  - find메소드에는 Team과 Order에 관한 join쿼리문을 구현하지 않았다.
  - 만약 다른 사람이 이를 모른 채로 사용하게 된다면 오류가 발생하게 되고 이는 엔티티의 신뢰 문제로 연결된다.
- 동일성
  - 자바의 컬렉션에서 같은 데이터를 여러번 조회해도 그 객체는 모두 같은 객체이다.
  - RDB에서 같은 데이터를 조회하면 조회할 때마다 새로운 객체가 생성되므로 서로 다른 객체가 된다.

- 이와 같은 문제를 해결하기 위해 나온 것이 JPA이다.
### JPA 소개
- JPA는 Java Persistence API의 약자로 자바 진영의 ORM 표준 기술이다.
  - ORM
    - Oriented-Relational Mapping의 약자로 객체와 관계형 데이터베이스를 이어주는 역할을 한다.
- JPA는 애플리케이션과 JDBC API 사이에서 동작한다.
- JPA는 SQL을 작성하여 보내고, 받은 결과를 객체로 반환하는 역할을 한다.
  ![image](https://user-images.githubusercontent.com/63232876/151149783-44d087b7-280b-4b21-819c-852627e1f18b.png)
- JPA 성능 최적화 기능
  - 1차 캐시와 동일성 보장
  - 트랜젝션을 통한 쓰기 지연
  - 지연 로딩
    ```java
    //지연 로딩 
    Member member = memberDAO.find(id); //SELECT * FROM MEMBER
    Team team = member.getTema();
    String name = team.getName(); //SELECT * FROM TEAM
    //미리 team을 조회하지 않고 실제 값이 필요할 때 조회한다. → 엔티티 신뢰 문제 해소
    ```
### JPA 구동 방식
![image](https://user-images.githubusercontent.com/63232876/151149872-29771b4c-1ab7-4abd-89c7-2bd62edaf8cb.png)
- EntityManagerFactory는 DB당 하나만 생성
- EntityManager는 스레드 간에 공유 금지
### JPQL
- JPA로 엔티티 객체를 중심으로 직접 쿼리를 작성하지 않고 개발할 수 있다.
- 하지만 복잡한 검색은 쿼리를 작성해야 한다.
- JPA는 SQL을 추상화한 JPQL이라는 객체 지향 쿼리 언어를 제공한다.
- JPQL로 테이블 중심이 아닌 엔티티 객체 중심의 쿼리문을 작성할 수 있다.

## 영속성 컨텍스트
### 영속성 컨텍스트란?
- 엔티티를 영구 저장하는 환경, 가상 DB의 역할을 의미한다.
```java
EntityManger.persist(member);
```
- persist는 DB에 저장하는 것이 아니라 영속성 컨텍스트에 저장하는 것이다.
- 실제 저장은 commit으로 저장된다(flush).
### 엔티티 생명주기
- 비영속 : 영속성 컨텍스트와 전혀 관계가 없는 새로운 상태
- 영속 : 영속성 컨텍스트에 관리되는 상태(EntityManager.persist)
- 준영속 : 영속성 컨텍스트에 저장되었다가 분리된 상태(EntityManager.detach,close,clear)
- 삭제: 삭제된 상태(EntityManager.remove)
  #### 준영속 vs 비영속
  - 영속 상태의 엔티티는 @Id로 매핑된 키값이 존재하므로 준영속 상태 엔티티 역시 식별자가 존재한다.
### 영속성 컨텍스트 장점
- 1차 캐시
  - 캐시를 사용해 DB 접근 빈도를 낮추지만, 한 트랜젝션 안에서만 유효하다. 
- 동일성 보장
  - 같은 엔티티 조회시 두 객체는 같다
- 쓰기 지연
  - 커밋을 통해 쿼리를 한 번에 DB로 보낸 후 영속성 컨텍스트를 flush 한다.
- 변경 감지
  - 영속성 컨텍스트 최초의 상태(스냅샷)와 커밋할 때의 변경점을 찾아 DB를 수정한다.
### 플러시
- 영속성 컨텍스트의 내용을 DB와 동기화한다.
- commit()과의 차이는 commit()은 플러시 후 트랜젝션을 끝내는 것을 포함하기 때문에 롤백이 불가능하다.
## 엔티티 맵핑
### 테이블 맵핑
- @Entity
  - JPA가 관리할 클래스로 선언한다.
- @Table
  - 엔티티가 저장될 테이블의 이름을 지정한다.
### DB 스키마 자동 생성
  ```xml
  <property name="hibernate.hbm2ddl.auto" value="create"/>
  ```
- creat : 기존 테이블 삭제 후 생성
- create-drop : creat 후 어플리케이션 종료 시 삭제
- update : DB와 엔티티 맵핑 정보를 비교해 변경 사항 수정
- validate : DB와 엔티티 맵핑 정보를 비교해 변경 사항이 있으면 경고
- none : 아무것도 안함 
- 운영 중인 DB에는 create, create-drop, update를 사용하면 안된다!
### 기본 키 맵핑
#### IDENTITY
- 기본 키 생성을 데이터베이스에 위임한다.
- 해당 전략 사용 시 엔티티를 데이터베이스에 저장 후 기본 키가 생성되므로 쓰기 지연 없이 바로 저장된다.
#### SEQUENCE
- 데이터베이스 시퀀스 오브젝트를 사용한다.
- 데이터베이스 시퀀스 값이 하나씩 증가하도록 설정되어 있으면 allocationSize를 반드시 1로 설정해야 한다.
#### TABLE
- 키 전용 테이블을 만들어 사용한다.
- 모든 DB에서 사용가능하지만, 성능이 떨어진다.
#### AUTO
- DB에 맞게 자동으로 전략을 지정하며 기본 값이다.
## 연관관계 매핑
### 단방향 연관관계
```java
@Entity
public class Member {
	@Id
	@GeneratedValue
	private Long id;
	private String name;
	private int age;
	@ManyToOne
	@JoinColumn(name = "TEAM_ID")//fk 이름
	private Team team;
}
```
- 엔티티에 참조할 객체의 클래스를 선언 후 매핑할 수 있다.
```java
Member findMember = em.find(Member.class, member.getId());
Team findTeam = findMember.getTeam();
```
- 객체에서 외래키로 다시 찾을 필요 없이 바로 참조가 가능하다.
### 양방향 연관관계
```java
@Entity
public class Team {
  @Id
  @GeneratedValue
  private Long id;
  private String name;
  @OneToMany(mappedBy = "team")//반대편 엔티티의 필드값
  private List<Member> members = new ArrayList<>();
}
```
- @ManyToOne, @OneToMany 등을 이용해 엔티티를 이어주면 양방향 연관관계가 되어 양쪽에서 참조가 가능하다.
#### 연관관계 주인
- 양방향 연관관계를 통해 서로 참조가 가능하지만 외래 키를 관리하는 주체가 필요하다.
- 이때 외래 키를 갖는 쪽이 관계의 주인이 되어 외래 키를 관리할 수 있다.
### 다양한 연관관계
####
#### @ManyToOne
- 단방향 : many(주인) → one으로 참조 가능, 반대는 불가능
- 양방향 : 서로 참조 가능
#### @OneToMany
- 단방향 : one(주인) → many로 참조 가능
- 양방향 : 공식적으로 존재하는 매핑은 아니나 @JoinColumn(insertable=false, updatable=false)을 사용해 양방향처럼 사용 가능
- one이 주인이지만 many쪽 테이블에 외래 키가 존재하는 구조이다.
- update 쿼리가 한 번 더 사용되고, 사용하기에 헷갈리므로 다대일 양방향을 주로 사용한다.
#### @OneToOne
- 외래 키를 주 테이블에 둘 지, 대상 테이블에 둘 지 정해야 한다.
- 주 테이블
  - JPA로 매핑하기에 편리
  - 추후에 대상 테이블과의 관계가 일대다로 변한다면, 테이블 구조 변경이 어려움
- 대상 테이블
  - 테이블 구조 변경이 용이
  - 프록시 기능의 한계로 지연 로딩으로 설정해도 항상 즉시 로딩
#### @ManyToMany
- 2개의 테이블로는 다대다 관계를 표현 불가
- 실무에선 거의 사용하지 않는다.
- 연결 테이블을 이용해 @OneToMany, @ManyToOne을 사용한다.
## 고급 매핑
### 상속관계 매핑
#### @Inheritance(strategy=InheritanceType.XXX)
- JOINED
  - 테이블 정규화가 가능하고 저장 공간의 효율성 증가한다.
  - 조회 시 조인으로 인한 성능이 저하되고 insert 쿼리가 두 번 사용된다.
- SINGLE_TABLE
  - 조인을 하지 않아 빠르게 조회할 수 있다.
  - null을 허용해야하므로 공간이 낭비되고 이로 인해 성능에 저하가 올 수 있다.
- TABLE_PER_CLASS
  - 쓰지 말자, 상속의 장점을 버린다.
### Mapped Superclass
- 테이블 공통의 매핑 정보를 상속한다.
- 엔티티가 아니며 실제 테이블과도 매핑되지 않는다.
- 해당 클래스로 조회가 불가능한다.
## 프록시
### 프록시 기초
```java
Member member1 = em.find(Member.class, "memberId"); // 실제 DB에서 조회
        
Member member2 = em.getReference(Member.class , "memberId"); // 프록시 객체 반환
member2.getName(); //프록시 객체 초기화
```
- 프록시는 실제 클래스를 상속 받아서 만들어지므로 사용자 입장에서는 구분 없이 사용할 수 있다.
#### 프록시 객체 초기화
- member2.getName() 후, 실제 엔티티가 생성되어 있지 않으면 영속성 컨텍스트에 엔티티 생성 요청 -> 초기화
- 엔티티 객체 생성 후 프록시 객체는 엔티티 객체를 참조하여 결과 반환
#### 프록시 특징
- 프록시 객체는 한 번만 초기화 되며 실제 엔티티가 되는 것은 아니다.
- 프록시 객체는 엔티티 객체를 상속 받기 때문에 타입 체크 시 intance of 를 사용해야 한다.
#### 프록시와 식별자
- 프록시는 식별자(PK)를 보관하고, 식별자로 값을 조회한다.
- 연관관계 설정 시 프록시를 활용하면 SQL을 사용하지 않고 설정할 수 있다.
  ```java
  Member member em.find(Member.class, "member1");
  Team tema = em.getReference(Member.class , "team1"); // 쿼리문을 사용하지 않고 프록시 객체 생성
  member.setTeam(team);
  ```
### 즉시 로딩과 지연 로딩
#### 즉시 로딩(fetch = FetchType.EAGER)
- 엔티티 조회 시 연관된 엔티티까지 함께 조회한다.
- 즉사 로딩은 항상 outer join을 사용한다. inner join을 사용하면 일대다 관계에서 다 쪽의 엔티티가  null이면 일 자체 엔티티도 null이 된다. 
#### 지연 로딩(fetch = FetchType.LAZY)
- 엔티티 조회 시 해당 엔티티만 조회하고, 연관된 엔티티에는 키를 이용한 프록시 객체를 만든다.
- 연관된 엔티티 조회 시 SQL을 한 번 더 사용한다.
#### 컬랙션 래퍼
- 컬랙션에 지연 로딩을 사용하면 컬랙션 자체를 호출할 때는 초기화되지 않고 컬랙션 내의 실제 데이터를 조회할 때 초기화된다.
#### JPA fetch 전략
- JPA의 기본 설정은 연관된 엔티티가 하나면 즉시 로딩(ManyToOne, OneToOne), 컬랙션이면 지연 로딩(OneToMany, ManyToMany)을 사용한다.
- 추천하는 방법은 모든 연관 관계에 지연 로딩을 사용하고 필요할 때 즉시 로딩으로 변경하는 것이다.
### 영속성 전이
- 엔티티를 영속 상태로 만들 때 연관된 엔티티도 같이 영속 상태로 만들 수 있다.
- 부모와 자식 간에 영속성 전이가 되지 않으면 무결성 예외가 발생한다.
  ```java
  @OneToMany(cascade = CascadeType.ALL)
  ```
### 고아 객체
- 부모와 자식간의 연관관계가 끊어진 경우 고아 객체가 된다.
- JPA는 자식 엔티티를 자동으로 삭제해 주는 기능이 있다.
  ```java
  @OneToMany(orphanRemoval = true)
  ```
  
## 값 타입
### 임베디드 타입
- 새로운 타입을 엔티티에 지정할 수 있다.
  ```java
  @Entity
  public class Member{
    @Id @GeneratedValue
    privete Long id;
    
    @Embedded Address homeAddress;
  }
  // Address 타입
  @Embeddable
  public class Address{
    private String city;
    private String street;
  }
  ```
- 임베디드 타입이 null이면 컬럼 값은 모두 null이다.
### 불변 타입
- setter를 허용하면 같은 임베디드 객체를 공유하여 한 쪽의 변경이 다른 쪽에 영항을 줄 수 있다.
- setter는 허용하지 않는 것이 좋다.
### 값 비교
- 동일성 비교 : 객체의 메모리 주소가 같은 지 비교한다. == 사용
- 동등성 비교 : 인스턴스의 값을 비교한다. equals() 사용
### 값 타입 컬렉션
- @ElementCollection로 값 타입 컬렉션을 선언하고 @CollectionTable로 컬럼을 매핑한다.
- 값 타입 컬렉션을 가진 엔티티만 영속화 해도 해당 엔티티와 관계를 맺는 값 타입 엔티티도 같이 영속화 된다.
#### 제한사항
- 값 타입 컬렉션은 식별자가 없어 값의 변경이 일어나면 연관된 모든 데니터를 삭제하고 다시 하나하나 저장한다.
- 따라서 연관된 테이블에 데이터가 많다면 값 타입 컬렉션 대신 일대다 관계가 유리하다.
- 값 타입을 하나의 기본 키로 보기 때문에 중복된 데이터를 저장할 수 없다.
