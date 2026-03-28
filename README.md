<img width="2236" height="1258" alt="image" src="https://github.com/user-attachments/assets/d0709a16-a01d-4c33-9005-de4ae7c192dc" />

### **Languages**

![Static Badge](https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=openjdk&logoColor=white) ![Static Badge](https://img.shields.io/badge/SQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)

### **Technologies**
![Spring](https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white) ![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white) ![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white) ![QueryDSL](https://img.shields.io/badge/QueryDSL-007396?style=for-the-badge&logo=apache-couchdb&logoColor=white) ![JPA](https://img.shields.io/badge/JPA-59666C?style=for-the-badge&logo=hibernate&logoColor=white) ![Git](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white)
![GitHub](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white)
![Linux](https://img.shields.io/badge/Linux-FCC624?style=for-the-badge&logo=linux&logoColor=black) ![AWS](https://img.shields.io/badge/AWS-232F3E?style=for-the-badge&logo=amazon-aws&logoColor=white)
<img src="https://img.shields.io/badge/Amazon AWS-232F3E?style=flat-square&logo=Amazon%20AWS&logoColor=white"/>

###
---

# **Table of Contents**

* ✨ **프로젝트 소개**
* 📈 **DB ERD**
* ☁️ **인프라 구성 및 CI/CD 흐름**
* 🚨 **기술적 issue**

###
---
## ✨ **프로젝트 소개**

단순 모집을 넘어 목적과 숙련도가 일치하는 최적의 학습 파트너를 찾아주는 스터디 매칭 플랫폼 서비스입니다. 기존 스터디 플랫폼은 방 제목 하나에 의존해 모이다 보니, 실력 차이나 목적 불일치로 인해 중도 포기하는 경우가 많았습니다.
Switching은 이를 해결하기 위해 입문자와 숙련자의 니즈를 구분하고 학습 목적을 데이터화했습니다. 맞지 않는 옷을 입어서 생기는 중도 하차를 막고, 타겟팅된 매칭을 통해 스터디의 지속 가능성을 확보하고자 해 만든 RESTful API 기반의 백엔드 서버 프로젝트입니다.

---
## 📈 **DB ERD**
<img width="976" height="815" alt="스위칭 erd" src="https://github.com/user-attachments/assets/d6c2c251-1dd2-4bdd-b0cc-c0404997f578" />

---
## **☁️ 인프라 구성 및 CI/CD 흐름**
<img width="976" height="815" alt="시스템 다이어그램 최종본" src="https://github.com/user-attachments/assets/c0c008bc-f0fd-4ef7-8ea6-680ee9f13213" />

이번 프로젝트에서는 **GitHub Actions와 AWS를 활용한 자동화된 CI/CD 파이프라인**을 직접 구축했습니다. 단순히 배포만 자동화하는 것을 넘어, **서비스 중단 없이 안정적으로 배포할 수 있는 환경**을 만드는 데에 중점을 두었고,  혼자서 하나하나 설정하고 운영하면서 DevOps에 대한 실전 경험을 쌓을 수 있었습니다.

##### 구축 배경
- 매번 EC2에 접속해서 빌드하고 서버 재시작하는 작업을 자동화하고 싶었습니다.
- 배포 중 사용자에게 영향을 주지 않도록, 서비스가 **끊기지 않게 무중단 배포**를 구현해보고 싶었습니다.
- EC2, S3, RDS 같은 AWS 서비스를 직접 연동해보면서 **인프라 전반에 대한 이해도를 높이고 싶었습니다.**

##### 전체 구성 흐름

###### 1. GitHub Actions – CI/CD 자동화
- `main` 브랜치에 코드가 push되면, GitHub Actions가 자동으로 워크플로우를 실행합니다.
- 내부적으로는 Gradle로 프로젝트를 빌드하고, `.jar` 파일과 배포 스크립트를 묶어 `app.zip` 파일을 생성합니다.
- 만들어진 `app.zip`은 **S3 버킷**으로 업로드되며, 이후 배포에 사용됩니다.

###### 2. S3 – 배포 파일 저장소
- `app.zip` 파일은 S3에 업로드되고,
- CodeDeploy는 이 파일을 다운로드해서 EC2에 배포하는 데 활용합니다.

###### 3. CodeDeploy – 배포 자동화
- CodeDeploy는 `appspec.yml`을 기반으로 `clean-up.sh`, `start.sh` 같은 배포 스크립트를 실행합니다.
- 배포 과정은 다음과 같은 순서로 진행됩니다:
    1. 기존 파일 정리
    2. 새로운 서버 프로세스 실행 (기존 서버는 유지)
    3. **헬스 체크를 통과하면 nginx 설정을 바꿔 새 서버로 트래픽을 전환**
    4. 구버전 프로세스를 안전하게 종료

###### 4. 무중단 배포 – Nginx + 포트 스위칭 방식
- 한 대의 EC2 인스턴스에서 9090, 9091 포트를 번갈아 사용합니다.
    - 예: 현재 서버가 9090에서 돌고 있으면, 새 버전은 9091에서 실행됨
- 헬스 체크 성공 시 Nginx 설정을 바꿔 새 포트로 트래픽을 넘깁니다.
- 이렇게 하면 **사용자는 배포 도중에도 끊김 없이 서비스 이용이 가능**합니다.

###### 5. Amazon RDS – MySQL 데이터베이스
- 애플리케이션에서 사용하는 데이터는 전부 Amazon RDS (MySQL)에 저장됩니다.
- DB 연결 정보는 AWS Secrets Manager를 통해 안전하게 관리합니다.

## **🧠 배포 중 겪었던 문제 - 메모리 부족**
배포할 때 신버전과 구버전 서버가 동시에 실행되면서, **EC2 프리티어(메모리 1GB)의 메모리가 부족해지는 문제가 발생**했습니다.

###### ✅ 해결 방법 – Swap 메모리 추가
EC2 인스턴스에 Swap 공간을 추가해서, 부족한 메모리를 보완했습니다.

```bash
# 1GB 스왑 생성 및 활성화
sudo fallocate -l 1G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
```
덕분에 2개의 Java 프로세스를 동시에 띄워도 문제가 발생하지 않게 되었고, 무중단 배포도 안정적으로 동작하게 되었습니다.
&nbsp;
###
---
## 🚨 **기술적 issue**
- [#1] N+1 문제 원인 및 해결 과정  
  https://madeprogame.tistory.com/384  

- [#2] 동시성 문제 원인 및 해결 과정  
  https://madeprogame.tistory.com/400  

- [#3] 서비스 레이어 테스트 커버리지  
  https://madeprogame.tistory.com/402  

- [#4] 사용자 로그인 처리(JWT, Session, RefreshToken)  
  https://madeprogame.tistory.com/403  

- [#5] CI/CD 구축기  
  https://madeprogame.tistory.com/406  

- [#6] 무중단 배포 전략 (Blue-Green, Rolling, Canary)  
  https://madeprogame.tistory.com/405  

- [#7] 지오해시 기반 위치 검색 최적화  
  https://madeprogame.tistory.com/407  

- [#8] API 호출량 제한  
  https://madeprogame.tistory.com/408
  
###
---





